/*
 * ZalozZakazku.java
 *
 * Založeno: 10. září 2007, 15:03
 *
 *
 * Autor: Martin Nováček
 * e-mail: <a href="mailto:martin.novacek@incad.cz">
 *
 * INCAD, spol. s r.o.
 * U krčského nádraží 36
 * 140 00  Praha 4, Česká republika
 *
 * @Revize: $Revision$
 * Základní popis třídy:
 *
 */

package cz.incad.nkp.digital.bf;

import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import com.amaio.plaant.sync.Record;
//import com.amaio.plaant.sync.UniqueKey;
import cz.incad.core.bf.BussinessFunctionMother;
//import cz.incad.core.tools.ReliefLogger;
//import cz.incad.core.tools.RecordWorkerContext;
import cz.incad.nkp.digital.constants.ConstBasic_NKP;
import java.util.Vector;


public class ZalozZakazku extends BussinessFunctionMother implements ConstBasic_NKP {
    private static final String LOCAL_SECURITY = "data100";
    //private ReliefLogger logger;
    private Vector <String>listVlastniku = new Vector<String>(0);
    private Vector <FormRecord>listUkolu = new Vector<FormRecord>(0);
    private Vector <Record> listMonografii = new Vector<Record>(0);
    private final String FORM_VLASTNIK = "vlastnik";
    private final String FORM_TYP_PRACE = "typPrace";
    private final String FORM_ZPRACOVATEL = "zpracovatel";
    private final String NULL_STRING = "NULL";
    
    
    /** Konstruktor třídy ZalozZakazku */
    public ZalozZakazku() {
        //logger = new ReliefLogger("cz.incad.nkp.digital.bf.zalozZakazku");
    }
    
    
    /** startWizard */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        int pocet = getWizardContextClient().getSelectedKeys().length;
        Record wRec = getWizardContextClient().getWizardRecord();
        WizardMessage wm = new WizardMessage();
        RecordsIterator pracovniZaznamy = null;
        Record zpracovavanaMonografie = null;
        
        if (pocet == 0) {
            throw new WizardException("Pro tuto funkci musí být označen alespoň jeden pracovní záznam.");
        }
        wm.addLine("Vybrali jste " + pocet + " záznamů ke zpracování.");
        //Získáme seznam označených záznamů
        pracovniZaznamy = getWizardContextClient().getSelectedRecords();
        while (pracovniZaznamy.hasMoreRecords()) {
            //Získáme záznam jedné monografie.
            zpracovavanaMonografie = pracovniZaznamy.nextRecord();
            if (zpracovavanaMonografie.getTableField(NKP_TABLE_ZAKAZKA_FLD).getTableRecords().hasMoreRecords()) { // Monografie již má aspoň jednu zakázku.
                wm.addLine("Monografie " + (String)zpracovavanaMonografie.getSimpleField(MONOGRAFIE_HL_NAZEV_FLD).getValue() + " již má zakázku, bude tedy vyřazena z vámi vybraného seznamu záznamů ke zpracování.");
            } else { // Monografie nemá žádnou zakázku.
                listMonografii.add(zpracovavanaMonografie);
            }
        }
        if (listMonografii.isEmpty()) { 
            throw new WizardException("V seznamu nezbyl žádný záznam ke zpracování. Všechny byly ze seznamu odstraněny při kontrole.");
        }
        //nastavení defaultních hodnot formuláře
        //FIX - nejsou nastavené žádné hodnoty - dodá Lucka
        wRec.getSimpleField("vlastnik1").setValue("ABA001");
        //wRec.getSimpleField("vlastnik2").setValue("");
        wRec.getSimpleField("typPrace1").setValue("mikrofilmovani");
        wRec.getSimpleField("zpracovatel1").setValue("Ampaco");
        wRec.getSimpleField("typPrace2").setValue("skenovani");
        wRec.getSimpleField("zpracovatel2").setValue("Ampaco");
        wRec.getSimpleField("typPrace3").setValue("upravaDat");
        wRec.getSimpleField("zpracovatel3").setValue("Elsyst");
        wRec.getSimpleField("typPrace4").setValue("tvorbaMetadat");
        wRec.getSimpleField("zpracovatel4").setValue("Elsyst");
        wRec.getSimpleField("typPrace5").setValue("zpristupneni");
        wRec.getSimpleField("zpracovatel5").setValue("NKP-zpracovatel");
        wRec.getSimpleField("typPrace6").setValue("archivace");
        wRec.getSimpleField("zpracovatel6").setValue("NKP-zpracovatel");
        return wm;
    }
    
    /** panelLeave */
    @SuppressWarnings("empty-statement")
    public WizardMessage panelLeave(String string) throws ValidationException, WizardException {
        WizardMessage wm = new WizardMessage();
        int pocetPoliVlastnik = 2; //Zde lze měni tpočet 
        int pocetPoliZpracATyp = 6;        
        String vlastnik;
        String zpracovatel;
        String typPrace;
        Record formular = getWizardContextClient().getWizardRecord();
        Vector <String>uniqueTypPrace = new Vector<String>(0);
        
        for (int i = 1; i <= pocetPoliVlastnik; i++) {
            vlastnik = (String) formular.getSimpleField((FORM_VLASTNIK + i)).getValue();
            if (!vlastnik.equals(NULL_STRING)) {
                if (!listVlastniku.contains(vlastnik)) {
                    listVlastniku.add(vlastnik);
                } else {
                    wm.addLine("Opakovaně zadán stejný vlastník. Zdvojený záznam byl odebrán a byl ponechán pouze jeden. >>> " + vlastnik);
                }
            }
            vlastnik = null;
        }
        for (int j = 1; j <= pocetPoliZpracATyp; j++) {
            zpracovatel = (String) formular.getSimpleField((FORM_ZPRACOVATEL + j)).getValue();;
            typPrace = (String) formular.getSimpleField((FORM_TYP_PRACE + j)).getValue();;
            if ((!zpracovatel.equals(NULL_STRING)) && (!typPrace.equals(NULL_STRING))) {
                if (!uniqueTypPrace.contains(typPrace)) {
                    uniqueTypPrace.add(typPrace);
                    listUkolu.add(new FormRecord(zpracovatel, typPrace));
                } else {
                    wm.addLine("Opakovaně zadán stejný typ práce. Zdvojený záznam byl odebrán a byl ponechán pouze jeden. >>> " + typPrace);
                }
                
            }
            zpracovatel = null;
            typPrace = null;
        }
        if (listVlastniku.isEmpty()) {
            throw new WizardException("Nezadali jste žádného vlastníka.");
        }
        if (listUkolu.isEmpty()) {
            throw new WizardException("Nezadali jste žádné úkoly.");
        }
        return wm;
    }
    
    /** runBussinesMethod */
    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        WizardMessage wm = new WizardMessage();
        Record zpracovavanaMonografie = null;
        Record newZakazka = null;
        Record newUkol = null;
        //RecordsIterator pracovniZaznamy = null;
        //boolean isZakazka = false;
        //UniqueKey vlastnikKey = null; 
        FormRecord ukol = null;
        //RecordWorkerContext rwc = new RecordWorkerContext(getWizardContextClient());
        Record newVlastnik = null;
        
        for (int i = 0; i <= (listMonografii.size() - 1); i++) {
            zpracovavanaMonografie = listMonografii.get(i);
            newZakazka = zpracovavanaMonografie.getTableField(NKP_TABLE_ZAKAZKA_FLD).createTableRecord();
            newZakazka.getReferencedField(NKP_REFERENCE_NA_TITNKP).setKey(zpracovavanaMonografie.getKey());
            for (int j = 0; j <= (listVlastniku.size() - 1); j++) {
                newVlastnik = newZakazka.getTableField(NKP_TABLE_VLASTNIK_FLD).createTableRecord();
                newVlastnik.getSimpleField(NKP_VLASTNIK_VLASTNIK_FLD).setValue(listVlastniku.get(j));
            }
            for (int k = 0; k <= (listUkolu.size() - 1); k++) {
                newUkol = newZakazka.getTableField(NKP_TABLE_UKOL_FLD).createTableRecord();
                newUkol.getReferencedField(NKP_REFERENCE_NA_ZAKAZKA).setKey(newZakazka.getKey());
                ukol = listUkolu.get(k);
                newUkol.getSimpleField(NKP_UKOL_ZPRACOVATEL_FLD).setValue(ukol.zpracovatel);
                newUkol.getSimpleField(NKP_UKOL_TYP_PRACE_FLD).setValue(ukol.typPrace);
            }
            getWizardContextClient().commit();
        }
        //Commit
        //getWizardContextClient().commit();
        return wm;
    }
    
    /** vnořená třída, nahrazuje Record*/ 
    private class FormRecord {
        private String zpracovatel = null;
        private String typPrace = null;
                        
        /** Konstruktor vnořené třídy. */
        public FormRecord(String zpracovatel, String typPrace) {
            this.zpracovatel = zpracovatel;
            this.typPrace = typPrace;
        }
    }
    
    
}

/*
 * $Log$
 *
 */








//
//
//ZALOHA - Tiskove Vystupy ukazka, pozdeji smazat
//        
//        
//        public WizardMessage startWizard() throws ValidationException, WizardException {
//            Record wRec = getWizardContextClient().getWizardRecord();
//            wRec.getSimpleField("vlastnik1").setValue("BOE303");
//            wRec.getSimpleField("vlastnik2").setValue("NULL");
//        return null;
//    }
//    
//    public WizardMessage panelLeave(String string) throws ValidationException, WizardException {
//        return null;
//    }
//    
//    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
//        WizardMessage wm = new WizardMessage();
//        MessageFormater m = new MessageFormater(wm);
//        String[] maily = new String[] {"reklamy@centrum.cz", "martin@incad.cz"};
//        wm.setHtml(true);
//        wm.addLine(maily[1]);
//        sendMail(maily, "Plaant Test Mail", "Hello world");
//        wm.addLine("<br>");
//        wm.addLine("<script> document.write(\"A toto napsal JavaScript\"); </script>");
//        wm.addLine("<BR>MojeTabulka<BR>");
//        wm.addLine("<TABLE><TR><TD>X11</TD><TD>X12</TD><TD>X13</TD></TR><TR><TD>X21</TD><TD>X22</TD><TD>X23</TD></TR></TABLE><BR>");
//        wm.addLine("<script> okno = window.open(\"\", \"Výpůjční lístek\", \"width=400, height=400\"); </script>");
//        wm.addLine("<script> okno.document.write(\"A toto napsal JavaScript1<BR>\"); </script>");
//        wm.addLine("<script> okno.document.write(\"A toto napsal JavaScript2<BR>\"); </script>");
//        wm.addLine("<script> okno.document.write(\"A toto napsal JavaScript3\"); </script>");
//        wm.addLine("<script> document.write(\"A toto napsal JavaScript ..post morte\"); </script>");
//        wm.addLine("<script> okno.document.write(\"A toto napsal JavaScript4, post post morte:)\"); </script>");
//        wm.addLine("<script> okno.document.write(\"<TABLE><TR><TD>X11</TD><TD>X12</TD><TD>X13</TD></TR><TR><TD>X21</TD><TD>X22</TD><TD>X23</TD></TR></TABLE><BR>\"); </script>");
//        wm.addLine("<script>  </script>");
//        wm.addLine("<script>  </script>");
//        wm.addLine("<script>  </script>");
//        wm.addLine("<script>  </script>");
//        wm.addLine("<script>  </script>");
//        wm.addLine("<script>  </script>");
//        wm.addLine("<script> okno.window.print(); </script>");
//        //m.printPage();
//        //m.CloseWindow();
//        return wm;
//    }
//    
//    /**
//     * Metoda umožňující posílání Emailu z Plaant.
//     * jestliže se metoda bude v budoucnu odebírat, tak nezapomenout odebrat
//     * i import přidaný jen kvůli této metodě.
//     * import com.amaio.plaant.businessFunctions.services.mail.MailService;
//     */
//    private void sendMail(String[] to,String subject,String body) {
//        ReliefLogger logger = new ReliefLogger("Mailer");
//        
//        try {
//            MailService service = (MailService) getWizardContextClient().getService(MailService.class.getName());
//            service.sendMail(to,subject,body);
//        }
//        catch (Exception exc) {
//            logger.putLogSevere("sendMail: " + "\nRELIEF: Nepodařilo se odeslat e-mail!" + "\nRELIEF: To  : " + to + "\nRELIEF: Subject: " + subject + "\nRELIEF: Body: " + body);
//            logger.putLogSevere(exc.toString()); 
//        }
//    }
//    
