/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.nkp.digital.bf;

import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.RecordsArray;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import com.amaio.plaant.sync.Record;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.core.constants.CnkpConfig;
//import cz.incad.core.caches.CacheCoreSetting;
//import cz.incad.core.tools.DirectConnection;
import cz.incad.core.tools.DirectConnection;
import cz.incad.core.tools.ReliefLogger;
import cz.incad.core.tools.ReliefNumericSeries;
//import cz.incad.core.tools.Utilities;
import cz.incad.core.tools.ReliefWizardMessage;
import cz.incad.nkp.digital.constants.ConstBasic_NKP;
import cz.incad.nkp.digital.constants.CnkpMikrofilm;
import cz.incad.nkp.digital.constants.CnkpTitNkp;
import cz.incad.nkp.digital.constants.CnkpXTitNkpMf;
//import java.math.BigDecimal;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
import cz.incad.rd.PredlohaEntity;
import java.sql.Connection;
//import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


/*******************************************************************************
 *
 * @author martin
 */
public class GenerujECisloAutomat extends BussinessFunctionMother implements CnkpMikrofilm, CnkpXTitNkpMf, CnkpTitNkp, ConstBasic_NKP, CnkpConfig {
    private static final String LOCAL_SECURITY = "data106";
    private Record mikrofilm = null;
    private Vector<Record> vXTitNkp;


    /***************************************************************************
     *
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    @SuppressWarnings("deprecation")
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        ReliefWizardMessage rwm = new ReliefWizardMessage();
        boolean notSorted = Boolean.TRUE;
        boolean isVelikostOK = Boolean.TRUE;
        boolean isPoradiOk = Boolean.TRUE;
        //int pocetPoliTemp = 0;
        //int pocetPoliCelkem = 0;
        int poradiNaSvitkuPrvni = 0;
        int poradiNaSvitkuDruha = 0;
        float velikostPrvnihoZaznamu = 0;
        float velikostDruhehoZaznamu = 0;
        //String ukazkaSouctuPoctuPoli = "";
        String poradiMiss = "";
        RecordsArray raMezitabulka;
        Record prvniZaznam = null;
        Record druhejZaznam = null;
        Vector<Integer> vPoradiNaMikrofilmu = new Vector<Integer>(0);

        //inicializace proměné třídy
        vXTitNkp = new Vector<Record>(0);
        //Kontrola na počet aktivních záznamů při spouštění BF
        //momentální nastavení je právě JEDEN označený záznam při spuštění BF
        if (getWizardContextClient().getSelectedKeys().length != 1) {
            throw new WizardException("Pro tuto funkci musí být označen jeden pracovní záznam.");
        }
        // získáme vybraný záznam mikrofilmu se kterým se bude nadále pracovat.
        mikrofilm = getWizardContextClient().getSelectedRecords().nextRecord();
        // získáme pole záznamů mezitabulek
        raMezitabulka = mikrofilm.getTableField(MIKROFILM_tXPredMikro).getTableRecordsArray();
        // kontrola na stav záznamu, jestli se nesnaží pracovat s uzavřeným záznamem.
        if (!((String)(mikrofilm.getSimpleField(RECORD_stavRec).getValue())).equals("active")) throw new ValidationException("Vybraný mikrofilm není ve stavu ACTIVE.");
        // kontrola jestli mikrofilm má nějaké monografie přiřazené
        if (raMezitabulka.getSize() == 0) throw new ValidationException("Mikrofilm nemá přiřazené žádné monogfrafie. Funkce byla zastavena.");
        // sečteme všechny počty polí
//        for (int i = 0; i < raMezitabulka.getSize(); i++) {
//            //titNkpTemp = raMezitabulka.getRecord(i).getReferencedField("rTitNkp").getReferencedRecord();
//            //v průběhu sčítání si vytvoříme vektor všech monografií, resp. titNKP, který využijeme později.
//            //vXTitNkp.add(titNkpTemp);
//            pocetPoliTemp = ((BigDecimal)raMezitabulka.getRecord(i).getReferencedField(XTITNKPMF_rTitNkp).getReferencedRecord().getSimpleField(TITNKP_pocetPoli).getValue()).intValue();
//            pocetPoliCelkem = pocetPoliCelkem + pocetPoliTemp;
//            ukazkaSouctuPoctuPoli = ukazkaSouctuPoctuPoli + " + " + pocetPoliTemp;
//        }
//        rwm.addInfo("::: Informase o Mikrofilmu :::", "Počet polí na mikrofilmu: " + ukazkaSouctuPoctuPoli + " = " + pocetPoliCelkem, null, null, null, 0);
//        // kontrola na to jestli Monografie nepresahne limitni pocet poli.
//        if (pocetPoliCelkem > Integer.parseInt(CacheCoreSetting.getCoreSetting(NKP_PROP_BF_ZALOZ_MIKROFILM_MAX_POLE_CHECK))) { //"NKP.BF.zalozMikrofilm.maxPole.check"
//            rwm.addUpozorneni("Součet polí na mikrofilmu (" + pocetPoliCelkem +") přesahuje přednastavenou konstantu " + Integer.parseInt(CacheCoreSetting.getCoreSetting(NKP_PROP_BF_ZALOZ_MIKROFILM_MAX_POLE_CHECK)) + " polí.", null, null, null, null, 0); //"NKP.BF.zalozMikrofilm.maxPole.check"
//        }
        /* kontrola na to jestli jsou pořadí na svitku správně Budou se provádět 2 kontroly
         * 1. je na to jestli jsou správně přiřazená čísla u jednotlivých záznamů, jestli tam nejsou mezery nebo nejake cislo 2x
         * 2 kontrola je na to jestli jsou zázamy za sebou seřazené podle výšky
         */
        // 1. kontrola
        //načteme všechny pořadí na mikrofilmu do vektoru
        for (int i = 0; i < raMezitabulka.getSize(); i++) {
            vPoradiNaMikrofilmu.add((Integer)raMezitabulka.getRecord(i).getSimpleField(XTITNKPMF_poradiVeSvitku).getValue());
        }
        //zjistíme jestli nejsou v pořadí mezery
        for (int i = 0; i < raMezitabulka.getSize(); i++) {
            if (!vPoradiNaMikrofilmu.contains(i + 1)) {
                ReliefLogger.severe("Neobsahuje: " + (i + 1));
                poradiMiss = poradiMiss + i + " ";
                isPoradiOk = Boolean.FALSE;
            }
        }
        if (!isPoradiOk) { //případ kdy nějaké číslo chybí
            throw new ValidationException("Pořadí dokumentů na Mikrofilmu je špatně zadáno. Seznam čísel která chybí: " + poradiMiss);
        }
        // 2. kontrola
        // v první řadě musíme knížky setřídit podle pořadí na svitku a pak můžeme kontrolovat jestli jsou správně seřazené.
        //na to aby jsme mohli tridit potrebujeme records array prevest na Vector.
        for (int i = 0; i < raMezitabulka.getSize(); i++) {
            vXTitNkp.add(raMezitabulka.getRecord(i));
        }
        //mame vektor a muzeme zacit tridit podle poradi na svitku
        while (notSorted) {
            notSorted = false;
            for (int i =0; i < (vXTitNkp.size() - 1); i++) {
                prvniZaznam = vXTitNkp.get(i);
                druhejZaznam = vXTitNkp.get(i + 1);
                poradiNaSvitkuPrvni = (Integer)prvniZaznam.getSimpleField(XTITNKPMF_poradiVeSvitku).getValue();
                poradiNaSvitkuDruha = (Integer)druhejZaznam.getSimpleField(XTITNKPMF_poradiVeSvitku).getValue();
                if (poradiNaSvitkuPrvni > poradiNaSvitkuDruha) {
                    //prohození záznamů
                    vXTitNkp.set(i, druhejZaznam);
                    vXTitNkp.set(i+1, prvniZaznam);
                    notSorted = true;
                }
            }
        }
        //máme setříděné záznamy podle pořadí na svitku, zkontrolujeme jestli jsou správně podle výšky knihy.
        for (int i =0; i < (vXTitNkp.size() - 1); i++) {
            prvniZaznam = vXTitNkp.get(i);
            druhejZaznam = vXTitNkp.get(i + 1);
            velikostPrvnihoZaznamu = getVyskaVcm(prvniZaznam);
            velikostDruhehoZaznamu = getVyskaVcm(druhejZaznam);
            if (velikostDruhehoZaznamu > velikostPrvnihoZaznamu) {
                isVelikostOK = false;
            }
        }
        //jestliže velikosti nesedí, vyhodí se vyjímka
        if (!isVelikostOK) rwm.addUpozorneni("Pořadí knížek na mikrofilmu neodpovídá jejich výšce.", null, null, null, null, 0); //throw new ValidationException("Pořadí knížek na mikrofilmu neodpovídá jejich výšce.");
        //ukazka jak by se mela pouzivat Validation exception.
        //ValidationException vax = (ValidationException)getWizardContextClient().getFactory(ValidationException.class).create();
        return rwm;
    }

    /***************************************************************************
     *
     * @param panelName
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public WizardMessage panelLeave(String panelName) throws ValidationException, WizardException {
        return null;
    }

    /***************************************************************************
     *
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     * @throws com.amaio.plaant.businessFunctions.ApplicationErrorException
     */
    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        ReliefWizardMessage wmf = new ReliefWizardMessage();
        ReliefNumericSeries rns;
        Object nsNext;
        rns = new ReliefNumericSeries(getWizardContextClient());

        Connection conn = DirectConnection.getConnection();
        Statement stmt = null;

        try {
            stmt = conn.createStatement();
            for (int i = 0; i < vXTitNkp.size(); i++) {
                nsNext = "E " + rns.getNextNumber("XTitNkpMf.mfeCislo");
                while(!isECisloOk(stmt, nsNext)) {
                    ReliefLogger.severe("E-CISLO: " + nsNext);
                    nsNext = "E " + rns.getNextNumber("XTitNkpMf.mfeCislo");
                }
                vXTitNkp.get(i).getSimpleField(XTITNKPMF_mfeCislo).setValue(nsNext);
                ReliefLogger.severe("Prirazeno E-cislo: " + (String)nsNext);
            }
        } catch (SQLException ex) {
        } finally {
            try {
                if (stmt != null) stmt.close();
                conn.close();
            } catch (SQLException ex) {
            }
        }

        // změníme stav záznamu a security by se měly postarat o to aby se nemohly provádět další změny.
        mikrofilm.getSimpleField(RECORD_stavRec).setValue("progress");
        // závěrečný commit
        getWizardContextClient().commit();
        return wmf;
    }

    /***************************************************************************
     *
     * @param rec
     * @return
     * @deprecated - je to zdvojenej kód měl by se sjednotit.
     */
    private float getVyskaVcm(Record rec) {
        Object testNaNull = null;

        testNaNull = rec.getReferencedField(NKP_REFERENCE_NA_PREDLOHA).getReferencedRecord().getSimpleField(PredlohaEntity.f_vyskaKnihy).getValue();
        if (testNaNull == null) return 0;
        //TODO hack vnové struktuře, chtělo by to později oravit.
        return 0;
        //return ((Number) testNaNull).floatValue();
    }

    /** ************************************************************************
     * 
     * @param stmt
     * @param nsNext
     * @return 
     */
    private boolean isECisloOk(Statement stmt, Object nsNext) {
        ResultSet rs;
        String SQL = "select id from xpredmikro where mfecislo = '";
        try {
            rs = stmt.executeQuery(SQL + nsNext + "'");
            if (rs.next()) {
                rs.close();
                return false;
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(GenerujECisloAutomat.class.getName()).log(Level.SEVERE, null, ex);
            //Tohle je dovne ale je to tu schvalne abych si pri chybe nevybral celou ciselnou radu v nekonecnem cyklu.
            //Radsi jednu duplicitu nez prijit o celou ciselnou radu.
            return true;
        }
        return true;
    }

}//eof class GenerujECisloAutomat
