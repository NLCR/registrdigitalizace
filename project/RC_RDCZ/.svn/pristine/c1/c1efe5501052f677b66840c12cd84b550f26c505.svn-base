/*
 * cz.incad.nkp.digital.bf.GenerujECislo.java
 * created on 30.4.2008
 *
 * author: Martin Nováček (mno)
 * e-mail: <a href="mailto:martin.novacek@incad.cz">
 *
 * INCAD, spol. s r.o.
 * U krčského nádraží 36
 * 140 00  Praha 4, Česká republika
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
import cz.incad.core.caches.CacheCoreSetting;
import cz.incad.core.tools.DirectConnection;
import cz.incad.core.tools.ReliefLogger;
import cz.incad.core.tools.ReliefNumericSeries;
import cz.incad.core.tools.Utilities;
import cz.incad.core.tools.ReliefWizardMessage;
import cz.incad.nkp.digital.constants.ConstBasic_NKP;
import cz.incad.nkp.digital.constants.CnkpMikrofilm;
import cz.incad.nkp.digital.constants.CnkpTitNkp;
import cz.incad.nkp.digital.constants.CnkpXTitNkpMf;
//import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/*******************************************************************************************************************************************
 * @author mno
 * <b>Class description:</b>
 * 
 */
public class GenerujECislo extends BussinessFunctionMother implements CnkpMikrofilm, CnkpXTitNkpMf, CnkpTitNkp, ConstBasic_NKP, CnkpConfig {
    private static final String LOCAL_SECURITY = "data105";
    private Record mikrofilm = null;
    private int eCislo = -1;
    private Vector<Record> vXTitNkp;
    private Vector<String> vECisla;


    /***************************************************************************************************************************************
     * 
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        ReliefWizardMessage rwm = new ReliefWizardMessage();
        boolean notSorted = Boolean.TRUE;
        boolean isVelikostOK = Boolean.TRUE;
        boolean isPoradiOk = Boolean.TRUE;
        int pocetPoliTemp = 0;
        int pocetPoliCelkem = 0;
        int poradiNaSvitkuPrvni = 0;
        int poradiNaSvitkuDruha = 0;
        float velikostPrvnihoZaznamu = 0;
        float velikostDruhehoZaznamu = 0;
        String ukazkaSouctuPoctuPoli = "";
        String poradiMiss = "";
        RecordsArray raMezitabulka;
        Record prvniZaznam = null;
        Record druhejZaznam = null;
        //Record recordTemp = null;
        Vector<Integer> vPoradiNaMikrofilmu = new Vector<Integer>(0);
        //Vector<Record> vXTitNkp = new Vector<Record>(0);

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
        //rwm.addLine("Počet polí na mikrofilmu: " + ukazkaSouctuPoctuPoli + " = " + pocetPoliCelkem);
        // kontrola na to jestli Monografie nepresahne limitni pocet poli.
        if (pocetPoliCelkem > Integer.parseInt(CacheCoreSetting.getCoreSetting(NKP_PROP_BF_ZALOZ_MIKROFILM_MAX_POLE_CHECK))) { //"NKP.BF.zalozMikrofilm.maxPole.check"
            rwm.addUpozorneni("Součet polí na mikrofilmu (" + pocetPoliCelkem +") přesahuje přednastavenou konstantu " + Integer.parseInt(CacheCoreSetting.getCoreSetting(NKP_PROP_BF_ZALOZ_MIKROFILM_MAX_POLE_CHECK)) + " polí.", null, null, null, null, 0); //"NKP.BF.zalozMikrofilm.maxPole.check"
            //throw new ValidationException("Součet polí na mikrofilmu (" + pocetPoliCelkem +") přesahuje přednastavenou konstantu " + Integer.parseInt(CacheCoreSetting.getCoreSetting("NKP.BF.zalozMikrofilm.maxPole.check")) + " polí.");
        }
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


    /***************************************************************************************************************************************
     * 
     * @param panelName
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public WizardMessage panelLeave(String panelName) throws ValidationException, WizardException {
        ReliefWizardMessage rwm = new ReliefWizardMessage();
        Object eCisloObject = getWizardContextClient().getWizardRecord().getSimpleField("eCislo").getValue();
        String hledaneECislo;
        //List lNepouzitelnychEcisel = new ArrayList(0);
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement stmt = null;
        /** Šablona pro zjištění jestli zadane Ecislo jiz existuje. */
        String SELECT_MFECISLO = "SELECT MFECISLO FROM XTITNKPMF WHERE MFECISLO = ?";
        boolean isEcisloFree = true;
        int eCisloFirst;

        //ZACATEK PRVNIHO PANELU        
        if (panelName.equals("Panel1")) {
            //čištění listu v případě vyjímky
            //lNepouzitelnychEcisel.clear(); //mozna zbytecne ale pro jistotu
            if (eCisloObject == null) {
                //Případ kdy není zadané Ečíslo a budou se generovat z číselné řady
                rwm.addInfo("E-čísla byla automaticky generována z číselné řady.", null, null, null, null, 0);
                //rwm.addLine("E-čísla budou automaticky generována z číselné řady.");
            } else {
                //Když zadají Ecislo a NEBUDE se generovat z číselné řady.
                //převedeme zadané e číslo na int
                eCislo = (Integer)eCisloObject;
                eCisloFirst = eCislo;
                vECisla = new Vector<String>(0);
                //zjistit kolik je monografií a jestli je tam mezera v Ecislech na to aby se tam vsechny vesly
                conn = DirectConnection.getConnection();
                try {
                    stmt = conn.prepareStatement(SELECT_MFECISLO);
                    for (int i = 0 ; i < vXTitNkp.size(); i++) {
                        //Utilities.doplnNulyPredCislo(eCislo++, 5, "E ");
                        hledaneECislo = "E " + Utilities.doplnNulyPredCislo(eCislo++, 5, "0");
                        stmt.setString(1,hledaneECislo);
                        //ReliefLogger.severe("Hledane E cislo: " + hledaneECislo);
                        rs = stmt.executeQuery();
                        //vektor E čísel, předpoklad je takový že když nějaké nevyhovuje tak BF dál nepokračuje, proto ho nijak nehlídám
                        vECisla.add(hledaneECislo);
                        while (rs.next()) {
                            ReliefLogger.severe("ResultSet: " + rs.getString("MFECISLO"));
                            isEcisloFree = Boolean.FALSE;
                            //lNepouzitelnychEcisel.add(rs.getString("MFECISLO"));
                            //rwm.addLine("E číslo: " + rs.getString("MFECISLO") + " je již použito.");
                        }
                    }
                    // uzavření databázových zdrojů
                    rs.close();
                    stmt.close();
                    conn.close();
                }catch (SQLException ex) {
                    ReliefLogger.throwing("", "", ex);
                }
                if (!isEcisloFree) throw new ValidationException("Potřebná řada E čísel není dostupná. Alespoň jedno E číslo z řady " + eCisloFirst + "-" + (eCislo - 1) + " je již použito.");
            }
            //rwm.addInfo("Pro potvrzení opeace odsouhlaste napsáním slova: \"ano\"", null, null, null, null, 0);
        } // KONEC PRVNIHO PANELU
        //ZACATEK DRUHEHO PANELU
//        if (panelName.equals("Panel2")) {
//            String potvrzeni = (String)getWizardContextClient().getWizardRecord().getSimpleField("potvrzeni").getValue();
//            if (!potvrzeni.equalsIgnoreCase("ano")) throw new ValidationException("Jestliže nesouhlasíte s uvedenými informacemi ukončete funkci.");
//        } // KONEC DRUHEHO PANELU
        return rwm;
    }


    /***************************************************************************************************************************************
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

        // zjistíme jestli budeme Ečíslo generovat z číselné řady
        if (eCislo == -1) {
            rns = new ReliefNumericSeries(getWizardContextClient());
            //projedeme jeden zaznam monografie a druhym a nastavime mu Ecislo z ciselne rady
            for (int i = 0; i < vXTitNkp.size(); i++) {
                nsNext = "E " + rns.getNextNumber("XTitNkpMf.mfeCislo");
                vXTitNkp.get(i).getSimpleField(XTITNKPMF_mfeCislo).setValue(nsNext);
                ReliefLogger.severe("Prirazeno E-cislo: " + (String)nsNext);
            }
        } else {
            // E číslo zadává uživatel
            for (int i = 0; i < vXTitNkp.size(); i++) {
                vXTitNkp.get(i).getSimpleField(XTITNKPMF_mfeCislo).setValue(vECisla.get(i));
                ReliefLogger.severe("Prirazeno E-cislo: " + (String)vECisla.get(i));
            }
        }
        // změníme stav záznamu a security by se měly postarat o to aby se nemohly provádět další změny.
        mikrofilm.getSimpleField(RECORD_stavRec).setValue("progress");
        // závěrečný commit
        getWizardContextClient().commit();
        return wmf;
    }


    /***************************************************************************************************************************************
     * 
     * @param rec
     * @return
     * @deprecated - je to zdvojenej kód měl by se sjednotit.
     */
    private float getVyskaVcm(Record rec) {
        Object testNaNull = null;

        testNaNull = rec.getReferencedField(NKP_REFERENCE_NA_TITNKP).getReferencedRecord().getSimpleField("vyskaVcm").getValue();
        if (testNaNull == null) return 0;
        return ((Number) testNaNull).floatValue();
    }


}//eof class GenerujECislo
