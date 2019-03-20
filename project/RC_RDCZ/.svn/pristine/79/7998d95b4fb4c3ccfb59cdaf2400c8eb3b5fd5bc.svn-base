/**
 * ZalozMikrofilm.java
 *
 * Založeno: 31. srpen 2007, 10:37
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

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import com.amaio.plaant.sync.Record;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.core.constants.CnkpConfig;
import cz.incad.core.caches.CacheCoreSetting;
import cz.incad.core.tools.DirectConnection;
import cz.incad.core.tools.ReliefLogger;
import cz.incad.nkp.digital.constants.ConstBasic_NKP;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;


public class ZalozMikrofilm extends BussinessFunctionMother implements ConstBasic_NKP, CnkpConfig {
    private static final String LOCAL_SECURITY = "data101";
    private Vector<Record> vstupniZaznamy = new Vector<Record>(0);
    private Long can = new Long(0);
    private Long mfECislo = new Long(0);
    private final String FORM_ARCH_CIS_NEG = "archCisloNeg";
    private final String FORM_E_CISLO = "mfECislo";
    //private HashMap<String,JedenListTisku> dataProTisk = new HashMap<String,JedenListTisku>();
    
    
    /** Konstruktor třídy ZalozMikrofilm */
    public ZalozMikrofilm() {
        //logger = new ReliefLogger("cz.incad.nkp.digital.bf.zalozMikrofilm");
    }
    
    
    /**
     * Povinná metoda panelového wizarda.
     */
    public WizardMessage startWizard() throws WizardException {
        securityStartWizard(LOCAL_SECURITY);
        Connection con = null;
        int maxPocetPoli = 0;
        int predbeznyPocetPoli = 0;
        int pocetZaznamu = getWizardContextClient().getSelectedKeys().length;
        Object pocetPoliObject;
        Record zaznam;
        Record wRec = getWizardContextClient().getWizardRecord();
        RecordsIterator records;
        RecordsIterator rit = null;
        ResultSet rs = null;
        Statement stmt = null;
        final String SELECT_MAX_ARCHCISLONEG_SQL = "SELECT MAX(ARCHCISLONEG) FROM MIKROFILM";
        WizardMessage wm = new WizardMessage();
        String seznamNevyhovujicich = "";
        
        //Kontrola na počet aktivních záznamů při spouštění BF
        if (pocetZaznamu == 0) {
            throw new WizardException("Pro tuto funkci musí být označen alespoň jeden pracovní záznam.");
        }
        records = getWizardContextClient().getSelectedRecords();
        while (records.hasMoreRecords()) {
            zaznam = records.nextRecord();
            //test jestli záznam není již součástí jiného mikrofilmu.
            rit = zaznam.getTableField(NKP_TABLE_XTITNKPMF_FIELD).getTableRecords();
            if (rit.hasMoreRecords()) {
                
                //tohle přesunout dolu a tady to sázet do stringu
                //throw new WizardException("Vybraný záznam je již součástí jiného Mikrofilmu. - " + (String) zaznam.getSimpleField("hlNazev").getValue());
                seznamNevyhovujicich = seznamNevyhovujicich + (String) zaznam.getSimpleField("hlNazev").getValue() + " *** ";
            }
            vstupniZaznamy.add(zaznam);
            pocetPoliObject = zaznam.getSimpleField(NKP_POCET_POLI_FLD).getValue();
            //Kontrola jestli hodnota co se bude převádět na integer neni náhodou null
            if (pocetPoliObject != null) {
                predbeznyPocetPoli = predbeznyPocetPoli + ((BigDecimal) pocetPoliObject).intValue();
            }
        }
        //kontrola na to jestli nemá být vyjímka
        if (seznamNevyhovujicich.length() > 1) {
            throw new WizardException("Vybrali jste i záznamy, které již jsou součástí jiných Mikrofilmů. Seznam nevyhovujících záznamů: " + seznamNevyhovujicich.substring(0, (seznamNevyhovujicich.length() - 5)) + ".");
        }
        maxPocetPoli = Integer.parseInt((String) CacheCoreSetting.getCoreSetting(NKP_PROP_BF_ZALOZ_MIKROFILM_MAX_POLE_CHECK));
        if (predbeznyPocetPoli > maxPocetPoli) {
            throw new WizardException("Počet polí je příliš vysoký! Celkový počet polí = " + predbeznyPocetPoli);
        }
        wm.addLine("Celkový počet vkládaných polí je: " + predbeznyPocetPoli);
        //Nastavíme ČAN
        try {
            con = DirectConnection.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(SELECT_MAX_ARCHCISLONEG_SQL);
            //logger.putLogConfig("Aktualni hodnota CAN: " + rs.getObject(1));
            if (rs.next()) {
                wRec.getSimpleField(FORM_ARCH_CIS_NEG).setValue((rs.getInt(1) + 1));
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException ex) {
            ReliefLogger.throwing("Chyba při přímém vstupu do dstabíze. SQL: ", SELECT_MAX_ARCHCISLONEG_SQL, ex);
        }
        return wm;
    }
    
    /** Povinná metoda panelového wizarda. */
    public WizardMessage panelLeave(String panelName) throws WizardException {
        Record wRec = getWizardContextClient().getWizardRecord();
        Object tester = null;
        
        tester = wRec.getFieldValue(FORM_ARCH_CIS_NEG);
        if (tester == null) {
            throw new WizardException("Nezadali jste Archivní číslo negativu ve správném tvaru.");
        }
        can = (Long) tester;
        tester = null;
        tester = wRec.getFieldValue(FORM_E_CISLO);
        if (tester == null) {
            throw new WizardException("Nezadali jste mfEčíslo ve správném tvaru.");
        }
        mfECislo = (Long) tester;
        return null;
    }
            
    /**
     * Povinná metoda panelového wizarda.
     */
    public WizardMessage runBusinessMethod() throws ApplicationErrorException, WizardException, ValidationException {
        Record newMikrofilm = null;
        Record monografie = null;
        Record newMeziTabulka = null;
        WizardMessage wm = new WizardMessage();
        //UtilitiesNKP util = new UtilitiesNKP();
        
        try {
            //založíme nový Mikrofilm
            newMikrofilm = getWizardContextClient().create(getDomenu(NKP_MIKROFILM_CLASSNAME));
            //nastavíme ČAN Mikrofilmu
            newMikrofilm.getSimpleField(MIKROFILM_ARCH_CIS_NEG_FLD).setValue(can.intValue());
            //Provážeme všechny záznamy potřebnými referencemi
            for (int i = 0; i < vstupniZaznamy.size(); i++) {
                //načteme z pole záznam Monografie
                monografie = vstupniZaznamy.get(i);
                //založíme novou mezitabulku
                newMeziTabulka = getWizardContextClient().create(getDomenu(NKP_XTitNkpMf_CLASSNAME));
                //přidáme klíč mezitabulky do TABLE Mikrofilmu.
                newMikrofilm.getTableField(NKP_TABLE_XTITNKPMF_FIELD).addKey(newMeziTabulka.getKey());
                //přidáme klíč Mikrofilmu do REFERENCE mezitabulky
                newMeziTabulka.getReferencedField(REFERENCE_Z_XTITNKPMF_NA_MIKROFILM).setKey(newMikrofilm.getKey());
                //přidáme klíč TFMonografie do REFERENCE mezitabulky
                newMeziTabulka.getReferencedField(NKP_REFERENCE_NA_TITNKP).setKey(monografie.getKey());
                //nastavíme mfEčíslo pro danou monografii
                //newMeziTabulka.getSimpleField(MONOGRAFIE_E_CISLO_FLD).setValue("E " + util.makeECislo(mfECislo++));
                newMeziTabulka.getSimpleField(MONOGRAFIE_E_CISLO_FLD).setValue("E " + mfECislo++);
                //přidáme klíč mezitabulky do TABLE TFMonografie
                monografie.getTableField(NKP_TABLE_XTITNKPMF_FIELD).addKey(newMeziTabulka.getKey());
            }
        } catch (AddException ex) {
            ex.printStackTrace();
        }
        //commit
        getWizardContextClient().commit();
        //tiskovyVystup(wm);
        return wm;
    }
    
//    private void tiskovyVystup(WizardMessage wzm) {
//        ParserHTML h = new ParserHTML();
//        ParserJavaScript j = new ParserJavaScript(wzm);
//        wzm.setHtml(true);
//        int sirkaOkna = 500;
//        int vyskaOkna = 500;
//                
//        j.WindowOpen(sirkaOkna,vyskaOkna);
//        
//        j.WindowAddText("<HTML><HEAD><STYLE>");
//        j.WindowAddText("body {font-size: 300%;}");
//        j.WindowAddText(".main {position: relative; width: 190mm; height: 277mm; page-break-after: always;}");
//        j.WindowAddText(".main2 {position: relative; width: 190mm; height: 277mm;}");
//        j.WindowAddText(".signatura {position: absolute; top: 0mm; right: 0mm; margin:0; padding:0; height: 140mm; writing-mode: tb-rl;}");
//        j.WindowAddText(".autori {position: absolute; top: 0mm; right: 30mm; height: 260mm; writing-mode: tb-rl;}");
//        j.WindowAddText(".right {position: absolute; bottom: 0mm; right: 0mm; margin:0; padding:0; height: 120mm; writing-mode: tb-rl;}");
//        j.WindowAddText(".cleaner {clear:both; height:1px; font-size:1px; border:none; margin:0; padding:0; background:transparent;}");
//        j.WindowAddText(".center {text-align: center; writing-mode: tb-rl; position: absolute; left: 0mm; top: 130mm;}");
//        j.WindowAddText("</STYLE></HEAD><BODY>");
//        j.WindowAddText("<div class=main>");
//        j.WindowAddText("<div class=signatura>signatura1<br></div>");
//        j.WindowAddText("<div class=autori>autoři <br>název<br></div>");
//        j.WindowAddText("<div class=right>pořadí ve svitku</div>");
//        j.WindowAddText("<div class=center><p>rok vydání</p></div></div>");
//
//        j.WindowAddText("<div class=main2>");
//        j.WindowAddText("<div class=signatura>signatura2<br></div>");
//        j.WindowAddText("<div class=autori>autoři <br>název<br></div>");
//        j.WindowAddText("<div class=right>pořadí ve svitku</div>");
//        j.WindowAddText("<div class=center><p>rok vydání</p></div></div>");
//        j.WindowAddText("</BODY></HTML>");
//        
//        j.WindowPrint();
// 
//    }
}


//class JedenListTisku {
//    public String signatura = "";
//    public String poradiVesvitku = "";
//    public String autori = "";
//    public String nazev = "";
//    public String rokVydani = "";
//}

/*
 * $Log$
 *
 */
