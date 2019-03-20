package cz.incad.nkp.digital.bf;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardContextClient;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import com.amaio.plaant.desk.QueryException;
import com.amaio.plaant.sync.Record;
import com.amaio.plaant.sync.TableField;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.core.constants.CcoreExemplar;
import cz.incad.core.constants.CcorePrivazek;
import cz.incad.core.constants.ConstVypujcniModul;
import cz.incad.core.tools.ReliefLogger;
import cz.incad.core.tools.RecordWorkerContext;
import cz.incad.core.tools.Utilities;
import cz.incad.core.tools.ReliefWizardMessage;
import cz.incad.nkp.digital.constants.CnkpTitNkp;
import cz.incad.nkp.digital.constants.ConstBasic_NKP;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


/*******************************************************************************************************************************************
 * 
 * @author martin
 */
public class PredatZpracovateli extends BussinessFunctionMother implements CcorePrivazek, ConstBasic_NKP, ConstVypujcniModul, CcoreExemplar, CnkpTitNkp {
    private static final String LOCAL_SECURITY = "data103";
    private Record ctenarNKP = null;
    private String zpusobDigitalizace = null;
    private String financovano = null;
    private TableField typPraceTableField = null;
    private boolean commit = true;
    private boolean commited = false;
    //private Vector<Record> titulyNkpFinal = new Vector<Record>(0);
    private Vector<TitulNkpFinal> titulyNkpFinal = new Vector<TitulNkpFinal>(0);
    private Vector<Record> exemplareFinal = new Vector<Record>(0);
    private Vector<String> seznamCaroveKody = new Vector<String>(0);
    //private Vector<String> seznamTitulNkpId = new Vector<String>(0);


    /***************************************************************************************************************************************
     * 
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public final WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        ReliefWizardMessage wmf = new ReliefWizardMessage();
        RecordWorkerContext rwc = new RecordWorkerContext(getWizardContextClient());
        RecordsIterator ritPrace = null;
        String prace = "";


        //Kontrola na počet aktivních záznamů při spouštění BF
        //momentální nastavení je právě JEDEN označený záznam při spuštění BF
        if (getWizardContextClient().getSelectedKeys().length != 1) {
            throw new WizardException("Pro tuto funkci musí být označen jeden pracovní záznam.");
        }
        //načtení záznamu ctenareNKP.
        ctenarNKP = getWizardContextClient().getSelectedRecords().nextRecord();
        zpusobDigitalizace = (String)ctenarNKP.getSimpleField("zpusobDig").getValue();
        financovano = (String)ctenarNKP.getSimpleField("financovano").getValue();
        typPraceTableField = ctenarNKP.getTableField("tAtypPrace");
        rwc.addRootDomain(NKP_ZAKAZKA_CLASSNAME);
        ritPrace = typPraceTableField.getTableRecords();
        while (ritPrace.hasMoreRecords()) {
            prace = prace + (String) ritPrace.nextRecord().getSimpleField("typPrace").getValue() + ", ";
        }
        wmf.addInfo("Informace o odběrateli knih:<br>Způsob digitalizace: " + zpusobDigitalizace + "<br>Financováno: " + financovano, "Typ práce: " + prace, null, null, null, 0);

        //TODO Nastaveni defaultni hodnoty pro Siglu podle zarazeni uzivatele.
        //Nastaveni defaultni hodnoty Sigla
        getWizardContextClient().getWizardRecord().getSimpleField("sigla").setValue("ABA001");

        return wmf;
    }


    /***************************************************************************************************************************************
     * 
     * @param panelName
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public final WizardMessage panelLeave(String panelName) throws ValidationException, WizardException {
        ReliefWizardMessage wmf = new ReliefWizardMessage();
        String lendExemplar = null;
        String carKod = null;
        String sigla = null;
        Record titulNKP = null;
        Record titul = null;
        Record exemplar = null;
        Record privazek = null;
        RecordsIterator ritExemplar = null;
        RecordsIterator ritPrivazky = null;
        RecordsIterator ritVypujcka = null;
        RecordsIterator ritZakazky = null;
        //Iterator<Record> itTitulyNKP = null;
        Iterator<TitulNkpFinal> itTitulyNKP = null;
        Vector<TitulNkpFinal> titulyNKP = new Vector<TitulNkpFinal>(0);
        RecordWorkerContext rwc = new RecordWorkerContext(getWizardContextClient());
        boolean vypujcit = false;
        //Record newTitul = null;
        TitulNkpFinal newTitul = null;


        wmf.setHtml(true);
        if (commited) {
            wmf.addUpozorneni("Práci s Funkcí jste již ukončili.", "Není možné dále pokračovat v zadávání dalších údajů.", "Okno bude za 3 vteřiny samo zavřeno.", null, null, 0);
            wmf.addLine("<script> setTimeout(\"window.close()\", 3 * 1000); </script>");
            return wmf;
        }
        sigla = (String) getWizardContextClient().getWizardRecord().getFieldValue("sigla");
        carKod = (String) getWizardContextClient().getWizardRecord().getFieldValue(EXEMPLAR_lendExemplar);
        lendExemplar = sigla + carKod;
        //if (lendExemplar == null) { //Puvodni verze pred predelanim na Sigla+carkod
        if (carKod == null) {
//            wm.addLine("Funkce ukončena.<BR>Data se ukládají do databáze.<BR>Okno funkce se zavře za 3 sekundy.");
//            wm.addLine("<script> setTimeout(\"window.close()\", 3 * 1000); </script>");
            //TODO - volani zaverecne fce, commit zmen.
            try {
                //PREDCASNY COMMIT ALL do databaze
                wmf = (ReliefWizardMessage) runBusinessMethod();
                commit = false;//pojistka pro pri pad ze se uzivatel rozhodne skoncit na poslednim panelu predcasne.
            } catch (ApplicationErrorException ex) {
                Logger.getLogger(PredatZpracovateli.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Vycisteni hodnoty, BUG plantu
            getWizardContextClient().getWizardRecord().getSimpleField(EXEMPLAR_lendExemplar).setValue(null);
            return wmf; //Konec BF
        }
        //kontrola na to jestli zadany carovy kod nebyl uz jednou zadan.
        if (seznamCaroveKody.contains(lendExemplar)) {//byl zadan stejny carovy kod podruhe
            wmf.addUpozorneni("Tuto kombinaci sigla + čárový kód jste již jednou zadali, byla ignorována.", "Zadaná kombinace sigla + čárový kód: " + "<b>" + lendExemplar + "</b>", null, null, null, 0);
            //Vycisteni hodnoty, BUG plantu
            getWizardContextClient().getWizardRecord().getSimpleField(EXEMPLAR_lendExemplar).setValue(null);
            return wmf;//Konec panelu
        }
        seznamCaroveKody.add(lendExemplar);
        //hledáme exemplář se zadaným lendExemplar
        try {
            ritExemplar = rwc.getZaznamy(CLASSNAME_EXEMPLAR, EXEMPLAR_lendExemplar, lendExemplar, null, false);
        } catch (QueryException ex) {
            ReliefLogger.severe("Nepredpokladana vyjimlka pri hledani zaznamu: " + ex);
        }
        switch (ritExemplar.getRecordsCount()) {
            case 0 : {//zaznam nebyl nalezen
                wmf.addUpozorneni("Zadaná kombinace sigla + čárový kód se neshoduje s žádným exemplářem v databázi.", "Zadali jste kombinaci sigla + čárový kód: <b>" + lendExemplar + "</b>", null, null, null, 0);
            } break;
            case 1 : {//Existuje prave jeden zaznam...lze pokracovat v pujcovani
                exemplar = ritExemplar.nextRecord();
                //Hledame jestli na exemplar neni aktivni vypujcka - hleda se to podle toho jestli je vyplneny datum vraceni
                try {
                ritVypujcka = rwc.getZaznamy(CLASSNAME_VYPUJCKA, VYPUJCKA_rExemplar + "." + EXEMPLAR_lendExemplar, lendExemplar, "datumVraceni", null, null, false);
                } catch (QueryException ex) {
                    ReliefLogger.severe("Nepredpokladana vyjimlka pri hledani zaznamu: " + ex);
                }
                if (ritVypujcka.hasMoreRecords()) {
                    wmf.addUpozorneni("Systém nedovoluje založit výpůjčku na již vypůjčený exemplář.", "Sigla + Čárový kód exempláře: " + lendExemplar, "Výpůjčka na tento exemplář nebude založena.", null, null, 0);
                    //Vycisteni hodnoty, BUG plantu
                    getWizardContextClient().getWizardRecord().getSimpleField(EXEMPLAR_lendExemplar).setValue(null);
                    return wmf;
                }
                //vezmu titul udelam z nej titulNKP a ulozim ho do TempuTitulu
                titul = exemplar.getReferencedField(EXEMPLAR_rTitul).getReferencedRecord();
                if (titul == null) {
                    wmf.addUpozorneni("Tento exemplář nemá referenci na Titul.", "Sigla + čárový kód exempláře: " + lendExemplar, "Výpůjčka na tento exemplář nebude založena.", null, null, 0);
                } else {
                    //prevedeme zaznam agendy titul na zaznam agendy titulNKP
                    titulNKP = titul2titulNKP(getWizardContextClient(), titul);
                    if (titulNKP == null) {
                        //TODO CHYBA
                        wmf.addLine("Problem pri prevodu titul na titulNKP. Vypujcka se neprovedla. Zavazna chyba.");
                        //Vycisteni hodnoty, BUG plantu
                        getWizardContextClient().getWizardRecord().getSimpleField(EXEMPLAR_lendExemplar).setValue(null);
                        return wmf;
                    }
                    titulyNKP.add(new TitulNkpFinal(titulNKP, carKod));
                }
                //hledame privazky k tomuto exemplari.
                try {
                    ritPrivazky = rwc.getZaznamy(CLASSNAME_PRIVAZEK, PRIVAZEK_rExemplar, getRecordId(exemplar), null, false);
                } catch (QueryException ex) {
                    ReliefLogger.severe("Nepredpokladana vyjimlka pri hledani zaznamu: " + ex);
                }
                //countPrivazku = ritPrivazky.getRecordsCount();
                while (ritPrivazky.hasMoreRecords()) {
                    privazek = ritPrivazky.nextRecord();
                    titul = privazek.getReferencedField(PRIVAZEK_rTitul).getReferencedRecord();
                    if (titul == null) {
                        wmf.addUpozorneni("Tento přívazek nemá referenci na Titul.", "Pro tento přívazek nebude založena zakázka.", null, null, null, 0);
                        continue; //skočí na další přívazek
                    }  //privazek ma referenci na titul - je normalni pokracujeme dal.
                    titulNKP = titul2titulNKP(getWizardContextClient(), titul);
                    if (titulNKP == null) {
                        //TODO chyba
                        wmf.addLine("Problem pri prevodu titul na titulNKP. Na titul tohoto privazku nebyla zalozena zakazka.");
                        continue;//skočí na další přívazek
                    }
                    //titulyNKP.add(titulNKP);
                    titulyNKP.add(new TitulNkpFinal(titulNKP, carKod));

                } //máme Vektor s přívazky pro zadaný exemplář - privazky a mame seznam uniqueKey vsech titulu + seznam vsech titulu svazanych k exemplari a jeho privazkum.
                //jestli se nepovedlo nasbirat zadne nove tituly, tak se skoci rovnou na dalsi panel wizarda
                if (titulyNKP.size() == 0 ) {
                    //Vycisteni hodnoty, BUG plantu
                    getWizardContextClient().getWizardRecord().getSimpleField(EXEMPLAR_lendExemplar).setValue(null);
                    return wmf;
                }
                //převede vektor vsech nasbiranych titulu
                itTitulyNKP = titulyNKP.iterator();
                while (itTitulyNKP.hasNext()) {
                    //newTitul = itTitulyNKP.next();
                    newTitul = itTitulyNKP.next();

                    //hledame jestli titul uz nahodou nema nejakou zakazku
                    try {
                        ritZakazky = rwc.getZaznamy(NKP_ZAKAZKA_CLASSNAME, "rTitNkp" , getRecordId(newTitul.recTitul), null, false);
                    } catch (QueryException ex) {
                        ReliefLogger.severe("Nepredpokladana vyjimlka pri hledani zaznamu: " + ex);
                    }
                    if (ritZakazky.hasMoreRecords() || titulyNkpFinal.contains(newTitul)) {
                        wmf.addUpozorneni("Na tento titul již existuje zakázka, nebo na tento titul ukazoval jeden z předešlých exemplářů.", "Na tento titul nebude založena zakázka.", "Hlavní název titulu: " + (String)newTitul.recTitul.getSimpleField("hlNazev").getValue(), null, null, 0);
                        continue;
                    }
                    ritZakazky = null;//vynulovani promene
                    //Ulozime hlavni titul exemplare do vysledneho Vektoru
                    titulyNkpFinal.add(newTitul);
                    //seznamTitulNkpId.add(getRecordId(newTitul));
                    //výpis informací v případě úspěšného zařazení exempláře do fronty čekající na commit.
                    wmf.addLine("Signatura: " + (String) exemplar.getSimpleField("signatura").getValue());
                    //wmf.addLine("Čárový kód: " + lendExemplar);
                    wmf.addLine("Sigla: " + sigla);
                    wmf.addLine("Čárový kód: " + carKod);
                    wmf.addLine("Hlavní název: " + Utilities.trimText((String) newTitul.recTitul.getSimpleField("hlNazev").getValue(),80, Boolean.TRUE));
                    vypujcit = true;
                }
                //pridame stavajici exempalr mezi ostatni na keter se bude vytvaret vypujcka
                if (vypujcit) exemplareFinal.add(exemplar);
                wmf.addLine("");
            } break;
            default: {//nalezeno vice zaznamu nic se pujcovat nebude
                wmf.addUpozorneni("V databázi bylo nalezeno více exemplářů se zadaným čárovým kódem.", "Čárový kód exempláře :" + lendExemplar, "Výpůvčka ne tento exemplář nebude založena.", "Nejprve musíte opravit duplicitu ćárového kódu v databázi!", null, 0);
                //addWmLog(wm, "Upozornění", "V databázi bylo nalezeno více exemplářů se zadaným čárovým kódem: " + lendExemplar, "Výpůjčka nebyla provedena, nejprve musíte opravit duplicitu v databázi!", null, null, null, null);
            } break;
        }
        //Vycisteni hodnoty, BUG plantu
        getWizardContextClient().getWizardRecord().getSimpleField(EXEMPLAR_lendExemplar).setValue(null);
        return wmf;
    }


    /***************************************************************************************************************************************
     * 
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     * @throws com.amaio.plaant.businessFunctions.ApplicationErrorException
     */
    public final WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        ReliefWizardMessage wmf = new ReliefWizardMessage();
        Iterator<Record> itExemplare = null;
        Iterator<TitulNkpFinal> itTitulyNkp = null;
        //Iterator<Record> itTitulyNkp = null;
        //Record titulNkp = null;
        TitulNkpFinal titulNkp = null;
        Record zakazka = null;
        RecordWorkerContext rwc = new RecordWorkerContext(getWizardContextClient());
        RecordsIterator ritTypPraceAndZpracovatel = null;
        Record typPraceAndZpracovatel = null;
        Record ukol = null;
        int countExemplaru = 0;
        int countZakazek = 0;
        int countUkolu = 0;
        int countTitulu = 0;
        String zpracovatel = "";


        if (commit && !commited) { // podmínka pro commit
            commited = true;
            itExemplare = exemplareFinal.iterator();
            //zalozeni vypujcek
            while (itExemplare.hasNext()) {
                try {
                    makeVypujckaNew(ctenarNKP, itExemplare.next(), STAV_VYPUJCKY_VYPUJCENO, false);
                    countExemplaru++;
                } catch (ApplicationErrorException ex) {
                    ReliefLogger.severe("Nepredpokladana vyjimlka pri hledani zaznamu: " + ex);
                }
            }
            //vytvorit zakazky a k tomu i ukoly.
            itTitulyNkp = titulyNkpFinal.iterator();
            while (itTitulyNkp.hasNext()) {
                titulNkp = itTitulyNkp.next();
                titulNkp.recTitul.getSimpleField("zpusobDig").setValue(zpusobDigitalizace);
                titulNkp.recTitul.getSimpleField("financovano").setValue(financovano);
                titulNkp.recTitul.getSimpleField("stavRec").setValue("predanoZpracovateli");
                countTitulu++;
                //vytvoreni nove zakazky pro dany titul
                try {
                    zakazka = rwc.getNewRecord(NKP_ZAKAZKA_CLASSNAME);
                    countZakazek++;
                } catch (AddException ex) {
                    ReliefLogger.severe("Nepredpokladana vyjimlka pri zakladani noveho zaznamu: " + ex);
                } catch (ApplicationErrorException ex) {
                    ReliefLogger.severe("Nepredpokladana vyjimlka pri zakladani noveho zaznamu: " + ex);
                }

                //K zaznamu zakazky doplnime Carovy kod exemplare ze ktereho se zakazka tvorila.
                zakazka.getSimpleField("carKod").setValue(titulNkp.carkod);

                //spojime zakazku s titulemNKP
                zakazka.getReferencedField("rTitNkp").setKey(titulNkp.recTitul.getKey());
                titulNkp.recTitul.getTableField("tZakazka").addKey(zakazka.getKey());
                //pro kazdy vyskyt typ prace zalozime ukol a vyplnime tp prace + zpracovatel
                ritTypPraceAndZpracovatel = typPraceTableField.getTableRecords();
                while (ritTypPraceAndZpracovatel.hasMoreRecords()) {
                    typPraceAndZpracovatel = ritTypPraceAndZpracovatel.nextRecord();
                    try {
                        ukol = rwc.getNewRecord(NKP_UKOL_CLASSNAME);
                        countUkolu++;
                    } catch (AddException ex) {
                        ReliefLogger.severe("Nepredpokladana vyjimlka pri zakladani noveho zaznamu: " + ex);
                    } catch (ApplicationErrorException ex) {
                        ReliefLogger.severe("Nepredpokladana vyjimlka pri zakladani noveho zaznamu: " + ex);
                    }
                    ukol.getReferencedField("rZakazka").setKey(zakazka.getKey());
                    zakazka.getTableField("tUkol").addKey(ukol.getKey());
                    ukol.getSimpleField("zpracovatel").setValue(typPraceAndZpracovatel.getSimpleField("zpracovatel").getValue());
                    ukol.getSimpleField("typPrace").setValue(typPraceAndZpracovatel.getSimpleField("typPrace").getValue());
                    //Security podle zpracovatele
                    zpracovatel = (String) typPraceAndZpracovatel.getSimpleField("zpracovatel").getValue();
                    if (zpracovatel.equals("Ampaco")) ukol.getSimpleField(RECORD_securityOwner).setValue("zuampaco");
                    if (zpracovatel.equals("Elsyst")) ukol.getSimpleField(RECORD_securityOwner).setValue("zuelsyst");
                    if (zpracovatel.equals("microna")) ukol.getSimpleField(RECORD_securityOwner).setValue("zumicrona");
                    if (zpracovatel.equals("zpknav")) ukol.getSimpleField(RECORD_securityOwner).setValue("zuknav");
                    if (zpracovatel.equals("nkCdHostivar")) ukol.getSimpleField(RECORD_securityOwner).setValue("zunkphostivar");
                    if (zpracovatel.equals("nkKlementinum")) ukol.getSimpleField(RECORD_securityOwner).setValue("zunkpklementinum");
                    if (zpracovatel.equals("snkMartin")) ukol.getSimpleField(RECORD_securityOwner).setValue("zusnkmartin");
                }
            }
            //COMMIT ALL
            try {
                getWizardContextClient().commit();
                //wm.addLine("COMMITED");
            } catch (ApplicationErrorException ex) {
                ReliefLogger.severe("Nepredpokladana vyjimlka pri COMMITu: " + ex);
            }
            wmf.addInfo("Probíhá zpracování zadaných údajů.<br>Od tohoto okamžiku není možné zadávat další údaje.<br><br>Statistika funkce:<br>","Počet titulů: " + countTitulu + "<br>Počet zakázek: " + countZakazek + "<br>Počet výpůjček: " + countExemplaru +"<br>Počet úkolů: " + countUkolu + "<br>", "Funkce byla ukončena.", null, null, 0);
        }
        return wmf;
    }


    /***************************************************************************************************************************************
     * Metoda vrátí ID číslo daného záznamu v databázi.
     * Provádí to tak, že ho vezme z uniqueKey daného záznamu.
     * @param zaznam
     * @return
     */
    private final String getRecordId(Record zaznam) {
        String id = null;

        id = zaznam.getKey().toString();
        id = id.substring(id.indexOf(":") + 1, id.length());
        return id;
    }


    /***************************************************************************************************************************************
     * Metoda vraci zaznam potomka(titulNKP) patrici k rodici z titul.
     * Hleda ho pers identicke pole lendtitul.
     * @param wcc
     * @param titulCore
     * @return
     */
    private final Record titul2titulNKP (WizardContextClient wcc, Record titulCore) {
        String lendTitul = null;
        RecordsIterator rit = null;
        RecordWorkerContext rwc = new RecordWorkerContext(wcc);

        lendTitul = (String) titulCore.getSimpleField(TITUL_lendTitul).getValue();
        try {
            rit = rwc.getZaznamy(NKP_TITULNKP_CLASSNAME, TITUL_lendTitul, lendTitul, null, false);
        } catch (QueryException ex) {
            ReliefLogger.severe("Nepredpokladana vyjimlka pri hledani zaznamu: " + ex);
            return null;
        }
        if(rit.hasMoreRecords()) return rit.nextRecord();
        return null;
    }


    /***************************************************************************
     * Interní třída, která ma sloužit jako přepravka.
     */
    class TitulNkpFinal {
        public Record recTitul;
        public String carkod;

        public TitulNkpFinal(Record recTitul, String carKod) {
            this.recTitul = recTitul;
            this.carkod = carKod;
        }

    }

}
