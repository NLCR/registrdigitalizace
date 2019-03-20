/*
 * cz.incad.nkp.digital.bf.PridejMonografii.java
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
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import com.amaio.plaant.desk.QueryException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.core.constants.CcoreExemplar;
import cz.incad.core.constants.CcorePrivazek;
import cz.incad.core.constants.CnkpConfig;
import cz.incad.core.caches.CacheCoreSetting;
import cz.incad.core.tools.DirectConnection;
import cz.incad.core.tools.RecordWorker;
import cz.incad.core.tools.RecordWorkerContext;
import cz.incad.core.tools.ReliefLogger;
import cz.incad.core.tools.Utilities;
import cz.incad.core.tools.ReliefWizardMessage;
import cz.incad.nkp.digital.constants.CnkpMikrofilm;
import cz.incad.nkp.digital.constants.CnkpTitNkp;
import cz.incad.nkp.digital.constants.CnkpXTitNkpMf;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

/*******************************************************************************
 * @author mno
 * <b>Class description:</b>
 * 
 */
public class PridejMonografii extends BussinessFunctionMother implements CnkpMikrofilm, CcoreExemplar, CcorePrivazek, CnkpXTitNkpMf, CnkpTitNkp, CnkpConfig {
    private static final String LOCAL_SECURITY = "data102";
    private Vector<Record> vMonografie = new Vector<Record>(0);
    private Vector<Integer> vPocetPoli = new Vector<Integer>(0);
    private Record mikrofilm = null;
    private String pocetPoliMask = "";
    private int pocetPoliPriSpusteni = 0;
    
    /***************************************************************************
     * 
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        ReliefWizardMessage rwm = new ReliefWizardMessage();
        //RecordsIterator ritMonografie = null;
        
        //Kontrola na počet aktivních záznamů při spouštění BF
        //momentální nastavení je právě JEDEN označený záznam při spuštění BF
        if (getWizardContextClient().getSelectedKeys().length != 1) {
            throw new WizardException("Pro tuto funkci musí být označen jeden pracovní záznam.");
        }
        // získáme vybraný záznam mikrofilmu se kterým se bude nadále pracovat.
        mikrofilm = getWizardContextClient().getSelectedRecords().nextRecord();
        // kontrola na stav záznamu, jestli se nesnaží pracovat s uzavřeným záznamem.

        if (!((String)(mikrofilm.getSimpleField("stavRec").getValue())).equals("active")) throw new WizardException("Vybraný mikrofilm není ve stavu ACTIVE.");
        
        //rwm.addLine("::: Informace o mikrofilmu :::");
        //rwm.addLine("Archivní Číslo negativu: " + mikrofilm.getSimpleField("archCisloNeg").getValue());
        //rwm.addLine("Dokumentů na mikrofilmu, celkem: " + mikrofilm.getSimpleField("celkemDokumentu").getValue());
        //získáme záznamy mezitabulek ze kterých teď musíme získat informace o počtu polí monografií na mikrofilmu.
//        ritMonografie = mikrofilm.getTableField("tXTitNkpMf").getTableRecords();
//        while (ritMonografie.hasMoreRecords()) {
//            //xTitNkpTemp = ritMonografie.nextRecord();
//            pocetPoliPriSpusteni = pocetPoliPriSpusteni + ((BigDecimal)ritMonografie.nextRecord().getReferencedField("rTitNkp").getReferencedRecord().getSimpleField("pocetPoli").getValue()).intValue();
//
//        }
        //rwm.addLine("Odhad polí na mikrofilmu, celkem: " + pocetPoliPriSpusteni);
        //rwm.addLine("Zbývá " + (Integer.parseInt(CacheCoreSetting.getCoreSetting("NKP.BF.zalozMikrofilm.maxPole.check")) - pocetPoliPriSpusteni) + " ze " + Integer.parseInt(CacheCoreSetting.getCoreSetting("NKP.BF.zalozMikrofilm.maxPole.check")) + " polí.");
        // vložíme počáteční informaci o mikrofilmu
        //rwm.addInfo("::: Informace o mikrofilmu :::", "Archivní Číslo negativu: " + mikrofilm.getSimpleField("archCisloNeg").getValue(), "Zbývá " + (Integer.parseInt(CacheCoreSetting.getCoreSetting(NKP_PROP_BF_ZALOZ_MIKROFILM_MAX_POLE_CHECK)) - pocetPoliPriSpusteni) + " ze " + Integer.parseInt(CacheCoreSetting.getCoreSetting(NKP_PROP_BF_ZALOZ_MIKROFILM_MAX_POLE_CHECK)) + " polí.", "", null, 0); //"NKP.BF.zalozMikrofilm.maxPole.check"

        rwm.addInfo("::: Informace o mikrofilmu :::", "Archivní Číslo negativu: " + mikrofilm.getSimpleField("archCisloNeg").getValue(), "", "", null, 0); //"NKP.BF.zalozMikrofilm.maxPole.check"

        //TODO Nastaveni defaultni hodnoty pro Siglu podle zarazeni uzivatele.
        //Nastaveni defaultni hodnoty Sigla
        getWizardContextClient().getWizardRecord().getSimpleField("sigla").setValue("ABA001");

        return rwm;
    }
    
    /***************************************************************************
     * 
     * @param panelName 
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    @SuppressWarnings({"deprecation", "deprecation"})
    public WizardMessage panelLeave(String panelName) throws ValidationException, WizardException {
        ReliefWizardMessage rwm = new ReliefWizardMessage();
        RecordWorkerContext rwc = new RecordWorkerContext(getWizardContextClient());
        RecordsIterator ritExemplare = null;
        RecordsIterator ritPrivazky = null;
        RecordsIterator ritTemp = null;
        Record exemplar = null;
        Record privazek = null;
        Record recordTemp = null;
        Vector<Record> vPrivazkyTemp = new Vector<Record>(0);
        Boolean notSorted = Boolean.TRUE;
        String lendExemplar = null;
        String carKod = null;
        String sigla = null;
        //String pocetPoliParsedOne = null;
        //String pocetPoliMaskWizard = null;
        //int pocetPoliCelkem = 0;
        //int pocetPoliTemp = 0;
        int druhej = 0;
        int prvni = 0;
        Object oPoradi = null;
        
        // procesy prováděné na prvním panelu
        if (panelName.equals("Panel1")) {
            vMonografie.clear();//cisteni poli v pripade vyjimky.
            pocetPoliMask = "";
            // načteme hodnotu z formuláře
            sigla = (String) getWizardContextClient().getWizardRecord().getFieldValue("sigla");
            carKod = (String) getWizardContextClient().getWizardRecord().getFieldValue(EXEMPLAR_lendExemplar);
            lendExemplar = sigla + carKod;
            //lendExemplar = (String) getWizardContextClient().getWizardRecord().getSimpleField("lendExemplar").getValue();
            // získáme zázmnam požadovaného exempláře podle lend exemplar. nebo zjistime ze takovy neexistuje - proste hledame exemplar podle zadaneho lendExemplar
            try {
                ritExemplare = rwc.getZaznamy(CLASSNAME_EXEMPLAR, EXEMPLAR_lendExemplar, lendExemplar, null, Boolean.FALSE);
                if (!ritExemplare.hasMoreRecords()) {
                    throw new ValidationException("Zadaný čárový kód nebyl nalezen v databázi.");
                }
                if (ritExemplare.getRecordsCount() > 1) {
                    throw new ValidationException("Pod zadaným čárovým kódem bylo nalezeno více exemplárů. Opravte tuto databázovou nekonzistenci. -- " + lendExemplar);
                }
                // Máme jeden exemplář se kterým budeme nadále pracovat.
                exemplar = ritExemplare.nextRecord();
                // hledám monografie, které jsou k danému exempláři připojené
                // nejdrive najdeme hlavni monografii
                if (exemplar.getReferencedField(EXEMPLAR_rTitul).getValue() != null) {
                    vMonografie.add(exemplar.getReferencedField(EXEMPLAR_rTitul).getReferencedRecord());
                } else {
                    rwm.addUpozorneni("Exemplář nemá referenci na hlavní(první) Titul.", null, null, null, null, 0);
                }
                // teď hledám jestli má exemplář přívazky a v případě, že ano tak je přidám do vektoru monografií
                if (exemplar.getTableField(EXEMPLAR_tPrivazek).getValue() != null) {
                    ritPrivazky = exemplar.getTableField(EXEMPLAR_tPrivazek).getTableRecords();
                    // všechny nalezené přívazky nasypeme do vektoru kde je budeme kontrolovat a třídit podle pořadí
                    while (ritPrivazky.hasMoreRecords()) {
                        privazek = ritPrivazky.nextRecord();
                        // kontrola jestli přívazek má připojenou monografii
                        if (privazek.getReferencedField(CcorePrivazek.PRIVAZEK_rTitul).getValue() != null) {
                            vPrivazkyTemp.add(privazek);
                        } else {
                            throw new ValidationException("Přívazek číslo: " + privazek.getSimpleField(CcorePrivazek.PRIVAZEK_poradi) + "nemá připojenou Monografii. Nelze tedy přidat na mikrofilm celý exemplář.");
                        }
                    }
                    // kontrola na počet přívazků - když je jeden, není potřeba třídění.
                    if (vPrivazkyTemp.size() > 1) {
                        // setřídění přívazků podle pořadí
                        while (notSorted) {
                            notSorted = Boolean.FALSE;
                            for (int i = 0; i < (vPrivazkyTemp.size() - 1); i++) {
                                oPoradi = vPrivazkyTemp.get(i).getSimpleField("poradi").getValue();
                                
                                if (oPoradi == null) {
                                    prvni = 999999;
                                } else {
                                    prvni = (Integer)vPrivazkyTemp.get(i).getSimpleField("poradi").getValue();
                                }
                                oPoradi = vPrivazkyTemp.get(i + 1).getSimpleField("poradi").getValue();
                                if (oPoradi == null) {
                                    druhej = 999999;
                                } else {
                                    druhej = (Integer)vPrivazkyTemp.get(i + 1).getSimpleField("poradi").getValue();
                                }
                                if (prvni > druhej) {
                                    notSorted = Boolean.TRUE; // cyklus pobezi jeste jednou
                                    // prohození těch dvou záznamů.
                                    recordTemp = vPrivazkyTemp.set(i, vPrivazkyTemp.get(i + 1));
                                    vPrivazkyTemp.set(i + 1, recordTemp);
                                }
                            }
                        }
                    }
                    // srovnané monografie z přívazků nasypeme za hlavní monografii exempláře do pole vMonografie
                    privazek = null;
                    for (int i = 0; i < vPrivazkyTemp.size(); i++) {
                        privazek = vPrivazkyTemp.get(i);
                        vMonografie.add(privazek.getReferencedField(PRIVAZEK_rTitul).getReferencedRecord());
                    }
                } else {
                    ReliefLogger.severe("Exemplar nema privazky.");
                }
                // máme vektor všch monografií, které patří k danému čárovému kódu a privazky jsou setridene podle poradi.
                // provedeme kontrolu na to jestli nějaká už neleží na jiném mikrofilmu. a zaroven si zjistime odhad kolik poki ma ktera monografie.
                for (int i = 0; i < vMonografie.size(); i++) {
                    //zjištění počtu polí a složení do stringu který se bude vkládat do pole ve wizárdovi.
                    //pocetPoliMask = pocetPoliMask + vMonografie.get(i).getSimpleField("pocetPoli").getValue() + "-";
                    //kontrola jestli již je monografie muístěná na jiném mikrofilmu
                    ritTemp = rwc.getZaznamy("cz.incad.nkp.digital.XTitNkpMf", "rTitNkp.lendTitul", vMonografie.get(i).getSimpleField(TITUL_lendTitul).getValue().toString(), null, Boolean.FALSE);
                    while (ritTemp.hasMoreRecords()) {
                        rwm.addUpozorneni("Tato monografie je již na jiném mikrofilmu.", "ČAN mikrofilmu: " + (Integer)ritTemp.nextRecord().getReferencedField(XTITNKPMF_rMikrofilm).getReferencedRecord().getSimpleField(MIKROFILM_archCisloNeg).getValue(), "Monografie: " + Utilities.trimText((String)vMonografie.get(i).getSimpleField(TITUL_hlNazev).getValue(), 100), null, null, 0);
                        //rwm.addLine("Monografie je již na jiném mikrofilmu. ČAN: " + (Integer)ritTemp.nextRecord().getReferencedField("rMikrofilm").getReferencedRecord().getSimpleField("archCisloNeg").getValue());
                        //rwm.addLine(Utilities.trimText((String)vMonografie.get(i).getSimpleField("hlNazev").getValue(), 100));
                    }
                }
                //pocetPoliMask = pocetPoliMask.substring(0, pocetPoliMask.length() - 1);
                //getWizardContextClient().getWizardRecord().getSimpleField("pocetPoli").setValue(pocetPoliMask);
                // ReliefLogger.severe(pocetPoliMask);
            } catch (QueryException ex) {
                ReliefLogger.throwing("Vyjimka pri pozadavku o zaznam z agendy.", "", ex);
            }
            rwm.addLine("Seznam monografií nalezených pod zadanýcm čárovým kódem:");
            for (int i = 0; i < vMonografie.size(); i++) {
                rwm.addLine((i + 1) + ". " + Utilities.trimText((String)vMonografie.get(i).getSimpleField(TITUL_hlNazev).getValue(), 100));
            }
            return rwm;
        } // KONEC prvního panelu.
        
//        // procesy prováděné na druhém panelu
//        if (panelName.equals("Panel2")) {
//            vPocetPoli.clear();// cisteni promenych v pripade ze nastane vyjimka
//            // pocetPoliTemp = 0;
//            // načteme hodnotu z formuláře
//            pocetPoliMaskWizard = (String) getWizardContextClient().getWizardRecord().getSimpleField("pocetPoli").getValue();
//            // kontrola jestli je ta maska z wizarda spravne
//            if (vMonografie.size() == 1) {//případ kdy je monografie bez přívazků
//                if (!Utilities.canBeStringInteger(pocetPoliMaskWizard)) {//kontrola jestli zadaná hodnota je Integer číslo.
//                    throw new ValidationException("Zadaná hodnota není celé číslo.");
//                }
//                vPocetPoli.add(Integer.parseInt(pocetPoliMaskWizard));
//                pocetPoliTemp = Integer.parseInt(pocetPoliMaskWizard);
//            } else { // případ kdy je monografie s přívazky
//                try {
//                    for (int i = 1; i < vMonografie.size(); i++) {
//                        pocetPoliParsedOne = pocetPoliMaskWizard.substring(0,pocetPoliMaskWizard.indexOf("-"));
//                        if (!Utilities.canBeStringInteger(pocetPoliParsedOne)) {//kontrola jestli zadaná hodnota je číslo.
//                            throw new ValidationException("Zadaná hodnota není celé číslo. Dodržte následující formát: " + pocetPoliMask);
//                        }
//                        vPocetPoli.add(Integer.parseInt(pocetPoliParsedOne));
//                        pocetPoliTemp = pocetPoliTemp + Integer.parseInt(pocetPoliParsedOne);
//                        pocetPoliMaskWizard = pocetPoliMaskWizard.substring(pocetPoliMaskWizard.indexOf("-") + 1);
//                    }
//                    if (!Utilities.canBeStringInteger(pocetPoliMaskWizard)) {//kontrola jestli zadaná hodnota je číslo.
//                        throw new ValidationException("Zadaná hodnota není správná. Dodržte následující formát: " + pocetPoliMask);
//                    }
//                    vPocetPoli.add(Integer.parseInt(pocetPoliMaskWizard));
//                    pocetPoliTemp = pocetPoliTemp + Integer.parseInt(pocetPoliMaskWizard);
//                } catch (StringIndexOutOfBoundsException exc) {
//                    throw new ValidationException("Zadaná hodnota není správná. Dodržte následující formát: " + pocetPoliMask);
//                }
//            }
//            // kontrola na to jestli Monografie nepresahne limitni pocet poli.
//            pocetPoliCelkem = pocetPoliPriSpusteni + pocetPoliTemp;
//            if (pocetPoliCelkem > Integer.parseInt(CacheCoreSetting.getCoreSetting(NKP_PROP_BF_ZALOZ_MIKROFILM_MAX_POLE_CHECK))) { //"NKP.BF.zalozMikrofilm.maxPole.check"
//                throw new ValidationException("Součet počtů zadaných polí (" + pocetPoliCelkem +") přesahuje přednastavenou konstantu " + Integer.parseInt(CacheCoreSetting.getCoreSetting(NKP_PROP_BF_ZALOZ_MIKROFILM_MAX_POLE_CHECK)) + " polí."); //"NKP.BF.zalozMikrofilm.maxPole.check"
//            }
//            return rwm;
//        } // KONEC druhého panelu.
        throw new WizardException("Došlo ke vnitřní chybě bussines funkce. Nebyl nalzezen odpovídající wizard panel");
    }
    
    /***************************************************************************
     * 
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     * @throws com.amaio.plaant.businessFunctions.ApplicationErrorException
     */
    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        ReliefWizardMessage rwm = new ReliefWizardMessage();
        Record tXTitNkpMf = null;
        Record monografieTemp = null;
        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;
        String sql = "select max(poradiVeSvitku) from XTitNkpMf where rMikrofilmXT = '" + RecordWorker.getRecordID(mikrofilm.getKey()) + "'";
        int poradiVeSvitku = 0;
        
        //hledáme nejvyšší pořadí ve svitku k danému mikrofilmu
        try {
            con = DirectConnection.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                poradiVeSvitku = rs.getInt(1);
            }
            rs.close();
            stmt.close();
            con.close();
        }
        catch (SQLException exc) {
            ReliefLogger.throwing("", "", exc);
        }
        //tXTitNkpMf - bude se nastavovat poradi na svitku...nejvyssi z tech ktere jsou uz pripojene.
        for (int i = 0; i < vMonografie.size(); i++) {
            monografieTemp = vMonografie.get(i);
            //nastavíme u záznamu monografie zadané odhadované počty polí.
            //monografieTemp.getSimpleField("pocetPoli").setValue(new BigDecimal(vPocetPoli.get(i)));
            //vytvoříme nový záznam v agendě tXTitNkpMf což je vazevbí tabulka mezi mikrofilmem a titNkp čímž se sváže mikrofilm s vazební tabulkou
            tXTitNkpMf = mikrofilm.getTableField("tXTitNkpMf").createTableRecord();
            //svážeme tXTitNkpMf s mikrofilm
            tXTitNkpMf.getReferencedField("rMikrofilm").setKey(mikrofilm.getKey());
            // svážeme tXTitNkpMf s titNkp
            tXTitNkpMf.getReferencedField("rTitNkp").setKey(monografieTemp.getKey());
            // svážeme titNkp s tXTitNkpMf
            monografieTemp.getTableField("tXTitNkpMf").addKey(tXTitNkpMf.getKey());
            // nastavíme pořadí ve svitku
            tXTitNkpMf.getSimpleField("poradiVeSvitku").setValue(++poradiVeSvitku);
            //ReliefLogger.severe("PoradiVeSvitku: " + poradiVeSvitku);
        }
        // závěrečný commit do databáze
        getWizardContextClient().commit();
        return rwm;
    }
    
    
}//eof class PridejMonografii
