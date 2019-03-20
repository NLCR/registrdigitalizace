/*******************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.rd.bf;

import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.core.tools.DirectConnection;
import cz.incad.core.tools.ReliefLogger;
import cz.incad.core.tools.ReliefUser;
import cz.incad.core.tools.Utilities;
import cz.incad.rd.DalsiPraceEntity;
import cz.incad.rd.PeriodikumEntity;
import cz.incad.rd.PredlohaEntity;
import cz.incad.rd.XPredMikroEntity;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/*******************************************************************************
 *
 * @author martin
 */
public class Predloha_KonverzeDatZeStareStrukturySQL extends BussinessFunctionMother {


    /***************************************************************************
     * 
     * @return
     * @throws ValidationException
     * @throws WizardException
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        WizardMessage wm = new WizardMessage();
        ReliefUser ru = new ReliefUser(getWizardContextClient());

        if (!"admin".equals(ru.getLogin())) throw new WizardException("Only admin can run this one.");
        wm.addLine("Konvertní Funkce... z RD.CZ na RD.CZ v2 SQL");

        return wm;
    }


    /***************************************************************************
     *
     * @param string
     * @return
     * @throws ValidationException
     * @throws WizardException
     */
    public WizardMessage panelLeave(String string) throws ValidationException, WizardException {
        WizardMessage wm = new WizardMessage();
        return wm;
    }


    /***************************************************************************
     *
     * @return
     * @throws ValidationException
     * @throws WizardException
     * @throws ApplicationErrorException
     */
    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        WizardMessage wm = new WizardMessage();
        Connection conn = DirectConnection.getConnection();
        OnePredloha predlohaTemplate;
        OneXPredMikro xTitMfTemplate;
        OneDalsiPrace dalsiPraceTemplate;
        String forgottenExemplar = "";
        //Hlavička pro externí soubor s neimportovanými exempláři
        String headLine =
                "ExempalrID" + "|" +
                "carkod" + "|" +
                "signatura" + "|" +
                "obdobi" + "|" +
                "idNumberAleph" + "|" +
                "idNumber" + "|" +
                "issn" + "|" +
                "mistoVyd" + "|" +
                "rokVyd" + "|" +
                "TitulID" + "|" +
                "hlNazev";

        String SQL_Zakazky_ALL      = "SELECT * FROM record INNER JOIN zakazka ON record.id = zakazka.id ORDER BY record.id";
        String SQL_Titul_ONE        = "SELECT * FROM record INNER JOIN titul ON record.id = titul.id INNER JOIN titnkp ON record.id = titnkp.id WHERE record.id = ";
        String SQL_Ukoy             = "SELECT * FROM record INNER JOIN ukol ON record.id = ukol.id WHERE ukol.rzakazkauk = ";
        String SQL_XtitNkpMf        = "SELECT * FROM record INNER JOIN xtitnkpmf ON record.id = xtitnkpmf.id WHERE xtitnkpmf.rtitnkpxt = ";
        String SQL_Exemplar_count   = "SELECT count(id) FROM exemplar WHERE rtitulex = ";
        String SQL_Exemplar         = "SELECT * FROM exemplar WHERE rtitulex = ";
        String SQL_Privazky         = "SELECT * FROM privazek WHERE rtitul = ";
        String SQL_ExemplarZPrivazku= "SELECT * FROM exemplar WHERE id = ";


        Statement stmtZakaza;
        Statement stmtInsert;
        Statement stmtTitul;
        Statement stmtUkoly;
        Statement stmtXtitNKPmf;
        Statement stmtExemplar;
        Statement stmtPrivazky;
        Statement stmtExemplarZPrivazku;


        ResultSet rsZakazka;
        ResultSet rsTitul;
        ResultSet rsUkoly;
        ResultSet rsXPredMikro;
        ResultSet rsExemplarCount;
        ResultSet rsExemplar;
        ResultSet rsPrivazky;
        ResultSet rsExemplarZPrivazku;

        List usedTitul = new LinkedList();
        List usedXtitNKPmf = new LinkedList();
        List usedExemplar = new LinkedList();
        List usedPrivazek = new LinkedList();


        int countTitul = 0;
        int countExemplar = 0;
        int pocetPripojenychExemplaru;
        int idPredlohy   = 0;
        int idDalsiUkol  = 0;
        int idXpredMikro = 0;

        //Inicializace hlavičky pro export neimportovanych souboru
        wm.addLine(headLine);
        try {
            stmtZakaza = conn.createStatement();
            stmtInsert = conn.createStatement();
            stmtTitul = conn.createStatement();
            stmtUkoly = conn.createStatement();
            stmtXtitNKPmf = conn.createStatement();
            stmtExemplar = conn.createStatement();
            stmtPrivazky = conn.createStatement();
            stmtExemplarZPrivazku = conn.createStatement();

            rsZakazka = stmtZakaza.executeQuery(SQL_Zakazky_ALL);
            while (rsZakazka.next()) {
                //Projizdime jednu zakazku za druhou
                idPredlohy++;
                predlohaTemplate = new OnePredloha();

                // Doplneni informaci ze zakazky
                predlohaTemplate.id(idPredlohy);
                predlohaTemplate.securityOwner(rsZakazka.getString("securityOwner"));
                predlohaTemplate.poznRec(rsZakazka.getString("poznRec"));
                predlohaTemplate.stavRec(rsZakazka.getString("stavRec"));
                predlohaTemplate.finUser(rsZakazka.getString("finUser"));
                predlohaTemplate.finDate("" + rsZakazka.getDate("finDate"));
                predlohaTemplate.ediUser(rsZakazka.getString("ediUser"));
                predlohaTemplate.ediDate("" + rsZakazka.getDate("ediDate"));
                predlohaTemplate.zalUser(rsZakazka.getString("zalUser"));
                predlohaTemplate.zalDate("" + rsZakazka.getDate("zalDate"));
                predlohaTemplate.skenDJVU(rsZakazka.getString("djvu"));
                predlohaTemplate.udirdcz(rsZakazka.getString("udirdcz"));
                predlohaTemplate.tempCarKod(rsZakazka.getString("carKod"));
                predlohaTemplate.skenJPEG(rsZakazka.getString("jpeg"));
                predlohaTemplate.skenTXT(rsZakazka.getString("txt"));
                predlohaTemplate.skenPDF(rsZakazka.getString("pdf"));
                predlohaTemplate.skenTIFF(rsZakazka.getString("tiff"));
                predlohaTemplate.stupneSedi(rsZakazka.getString("stupneSedi"));
                predlohaTemplate.rozliseni(rsZakazka.getString("dpi"));
                predlohaTemplate.skenScanStran(rsZakazka.getString("scanovanoStran"));
                predlohaTemplate.tempParSkenovani(rsZakazka.getString("parSkenovani"));
                predlohaTemplate.typSkeneru(rsZakazka.getString("skener"));
                predlohaTemplate.skenPocetSouboru(rsZakazka.getString("pocetSouboru"));
                predlohaTemplate.skenOCRSvabach(rsZakazka.getString("ocrSvabach"));
                predlohaTemplate.skenOCR(rsZakazka.getString("ocr"));
                predlohaTemplate.rozsah(rsZakazka.getString("rozsah"));
                predlohaTemplate.oldZakazkaId(rsZakazka.getString("id"));
                predlohaTemplate.cisloZakazky(rsZakazka.getString("cisloZakazky"));
                predlohaTemplate.url(rsZakazka.getString("url"));
                predlohaTemplate.metaXML(rsZakazka.getString("xml"));
                predlohaTemplate.metaObrazky(rsZakazka.getString("obrazky"));

                //Doplneni informaci z titulu.
                ReliefLogger.severe("SQL Titul_ONE: " + SQL_Titul_ONE + rsZakazka.getString("rtitnkpza"));
                rsTitul = stmtTitul.executeQuery(SQL_Titul_ONE + rsZakazka.getString("rtitnkpza"));
                if (rsTitul.next()) {
                    //Zakazka ma připojený titul a teď ho zpracujem
                    if (!usedTitul.contains(rsTitul.getString("id"))) {
                        usedTitul.add(rsTitul.getString("id"));
                    }
                    predlohaTemplate.linkKatalog(rsTitul.getString("linkKatalog"));
                    predlohaTemplate.cnb(rsTitul.getString("cnb"));
                    predlohaTemplate.seriePart(rsTitul.getString("seriePart"));
                    predlohaTemplate.serieName(rsTitul.getString("serieName"));
                    predlohaTemplate.isbn(rsTitul.getString("isbn"));
                    predlohaTemplate.vlastnik2(rsTitul.getString("vlastnik2"));
                    predlohaTemplate.lccKlasifikace(rsTitul.getString("lccKlasifikace"));
                    predlohaTemplate.digitalniKnihovna(rsTitul.getString("digitalniKnihovna"));
                    predlohaTemplate.bazeAleph(rsTitul.getString("bazeAleph"));
                    predlohaTemplate.vlastnik(rsTitul.getString("vlastnik"));
                    predlohaTemplate.urlTitul(rsTitul.getString("url"));
                    predlohaTemplate.formatAleph(rsTitul.getString("formatAleph"));
                    predlohaTemplate.idNumberAleph(rsTitul.getString("idNumberAleph"));
                    predlohaTemplate.idNumber(rsTitul.getString("idNumber"));
                    predlohaTemplate.financovano(rsTitul.getString("financovano"));
                    predlohaTemplate.zpusobDig(rsTitul.getString("zpusobDig"));
                    predlohaTemplate.cast(rsTitul.getString("cast"));
                    predlohaTemplate.pocetPoli(rsTitul.getString("pocetPoli"));
                    predlohaTemplate.inverze(rsTitul.getString("inverze"));
                    predlohaTemplate.rozsahTit(rsTitul.getString("rozsahTit"));
                    predlohaTemplate.autorPrij(rsTitul.getString("autorPrij"));
                    predlohaTemplate.issn(rsTitul.getString("issn"));
                    predlohaTemplate.vyskaVcm(rsTitul.getString("vyskaVcm"));
                    predlohaTemplate.pocetStran(rsTitul.getString("pocetStran"));
                    predlohaTemplate.mistoVyd(rsTitul.getString("mistoVyd"));
                    predlohaTemplate.rokVyd(rsTitul.getString("rokVyd"));
                    predlohaTemplate.hlNazev(rsTitul.getString("hlNazev"));
                    predlohaTemplate.oldClassname(rsTitul.getString("classname"));
                    predlohaTemplate.oldTitulId(rsTitul.getString("id"));
                    //predlohaTemplate.(rsTitul.getString(""));


                    //Zpracujeme hned i Pripojene Mikrofilmy respektive XtitMF
                    ReliefLogger.severe("SQL XtitNKPmf: " + SQL_XtitNkpMf + rsTitul.getString("id"));
                    rsXPredMikro = stmtXtitNKPmf.executeQuery(SQL_XtitNkpMf + rsTitul.getString("id"));
                    while(rsXPredMikro.next()) {
                        //Zpracujeme jeden zaznam za druhym
                        idXpredMikro++;
                        xTitMfTemplate = new OneXPredMikro();

                        xTitMfTemplate.id(idXpredMikro);
                        xTitMfTemplate.securityOwner(rsXPredMikro.getString("securityOwner"));
                        xTitMfTemplate.poznRec(rsXPredMikro.getString("poznRec"));
                        xTitMfTemplate.stavRec(rsXPredMikro.getString("stavRec"));
                        xTitMfTemplate.finUser(rsXPredMikro.getString("finUser"));
                        xTitMfTemplate.finDate("" + rsXPredMikro.getDate("finDate"));
                        xTitMfTemplate.ediUser(rsXPredMikro.getString("ediUser"));
                        xTitMfTemplate.ediDate("" + rsXPredMikro.getDate("ediDate"));
                        xTitMfTemplate.zalUser(rsXPredMikro.getString("zalUser"));
                        xTitMfTemplate.zalDate("" + rsXPredMikro.getDate("zalDate"));

                        xTitMfTemplate.rMikrofilm(rsXPredMikro.getString("rmikrofilmxt"));
                        xTitMfTemplate.rPredloha("" + idPredlohy);
                        xTitMfTemplate.poradiVeSvitku(rsXPredMikro.getString("poradivesvitku"));
                        xTitMfTemplate.poleCista(rsXPredMikro.getString("polecista"));
                        xTitMfTemplate.poleHruba(rsXPredMikro.getString("polehruba"));
                        xTitMfTemplate.pocetScanu(rsXPredMikro.getString("pocetscanu"));
                        xTitMfTemplate.mfeCislo(rsXPredMikro.getString("mfecislo"));
                        xTitMfTemplate.oldTitulId(rsXPredMikro.getString("rtitnkpxt"));
                        xTitMfTemplate.oldXTitNkpMfId(rsXPredMikro.getString("id"));
                        xTitMfTemplate.oznaceniCasti(rsXPredMikro.getString("oznacenicasti"));
                        xTitMfTemplate.carKod(rsXPredMikro.getString("carkod"));

                        stmtInsert.executeUpdate(xTitMfTemplate.getSQL_INSERT());
                    }
                    rsXPredMikro.close();//Uzavreni kurzoru

                    //Zpracování exempláře a přívazku
                    ReliefLogger.severe("SQL_Exemplar_count: " + SQL_Exemplar_count + rsTitul.getString("id"));
                    rsExemplarCount = stmtExemplar.executeQuery(SQL_Exemplar_count + rsTitul.getString("id"));
                    rsExemplarCount.next();
                    pocetPripojenychExemplaru = rsExemplarCount.getInt(1);
                    rsExemplarCount.close();

                    if (pocetPripojenychExemplaru == 1) {
                        //Titul má právě jeden Exemplář
                        ReliefLogger.severe(SQL_Exemplar + rsTitul.getString("id"));
                        rsExemplar = stmtExemplar.executeQuery(SQL_Exemplar + rsTitul.getString("id"));
                        rsExemplar.next();
                        //máme jeden exemplář a teď ho zpracujem
                        predlohaTemplate.signatura(rsExemplar.getString("signatura"));
                        predlohaTemplate.carKod(rsExemplar.getString("carkod"));
                        //predlohaTemplate.obdobi(rsExemplar.getString("obdobi"));
                        predlohaTemplate.oldExemplarId(rsExemplar.getString("id"));

                        rsExemplar.close();
                    } else if (pocetPripojenychExemplaru > 1) {
                        //Titul má více Exemplářů
                        countTitul++;
                        ReliefLogger.severe(SQL_Exemplar + rsTitul.getString("id"));
                        rsExemplar = stmtExemplar.executeQuery(SQL_Exemplar + rsTitul.getString("id"));
                        while (rsExemplar.next()) {
                            countExemplar++;
                            //Tyto tituly nebudou převáděny - musejí se převést ručně.
                            forgottenExemplar =
                                    rsExemplar.getString("id") + "|" +
                                    rsExemplar.getString("carkod") + "|" +
                                    rsExemplar.getString("signatura") + "|" +
                                    rsExemplar.getString("obdobi") + "|" +
                                    rsTitul.getString("idNumberAleph") + "|" +
                                    rsTitul.getString("idNumber") + "|" +
                                    rsTitul.getString("issn") + "|" +
                                    rsTitul.getString("mistoVyd") + "|" +
                                    rsTitul.getString("rokVyd") + "|" +
                                    rsTitul.getString("id") + "|" +
                                    rsTitul.getString("hlNazev");
                            wm.addLine(forgottenExemplar);
                        }
                        rsExemplar.close();
                    } else {
                        //Titul nemá žádný exemplář, zpracujeme přívazky
                        ReliefLogger.severe(SQL_Privazky + rsTitul.getString("id"));
                        rsPrivazky = stmtPrivazky.executeQuery(SQL_Privazky + rsTitul.getString("id"));
                        if (rsPrivazky.next()) {
                            //existuje privazek, zpracujeme ho
                            predlohaTemplate.poradi(rsPrivazky.getString("poradi"));
                            predlohaTemplate.oldPrivazekId(rsPrivazky.getString("id"));
                            predlohaTemplate.poznamkaPrivazek(rsPrivazky.getString("poznamka"));

                            //hledáme jestli přívazek má exemplář
                            ReliefLogger.severe(SQL_ExemplarZPrivazku + rsPrivazky.getString("rexemplar"));
                            rsExemplarZPrivazku = stmtExemplarZPrivazku.executeQuery(SQL_ExemplarZPrivazku + rsPrivazky.getString("rexemplar"));
                            if (rsExemplarZPrivazku.next()) {
                                //Mame exemplar pres privazek, zpracujeme ho
                                predlohaTemplate.signatura(rsExemplarZPrivazku.getString("signatura"));
                                predlohaTemplate.carKod(rsExemplarZPrivazku.getString("carkod"));
                                //predlohaTemplate.obdobi(rsExemplarZPrivazku.getString("obdobi"));
                                predlohaTemplate.oldExemplarId(rsExemplarZPrivazku.getString("id"));
                            }
                            rsExemplarZPrivazku.close();
                        }

                        rsPrivazky.close();
                    }
                }//Konec zpracovani Titulu, exemplare, privazku a xtitmikro

                //INSERT novy zaznam Predloha
                ReliefLogger.severe("SQL Insert predlohy se zakazkou: " + predlohaTemplate.getSQL_INSERT());
                stmtInsert.executeUpdate(predlohaTemplate.getSQL_INSERT());

                //Zpracovani Ukolu
                ReliefLogger.severe("SQL Ukoly: " + SQL_Ukoy + rsZakazka.getString("id"));
                rsUkoly = stmtUkoly.executeQuery(SQL_Ukoy + rsZakazka.getString("id"));
                while(rsUkoly.next()) {
                    idDalsiUkol++;
                    dalsiPraceTemplate = new OneDalsiPrace();

                    dalsiPraceTemplate.id(idDalsiUkol);
                    dalsiPraceTemplate.securityOwner(rsUkoly.getString("securityOwner"));
                    dalsiPraceTemplate.poznRec(rsUkoly.getString("poznRec"));
                    dalsiPraceTemplate.stavRec(rsUkoly.getString("stavRec"));
                    dalsiPraceTemplate.finUser(rsUkoly.getString("finUser"));
                    dalsiPraceTemplate.finDate("" + rsUkoly.getDate("finDate"));
                    dalsiPraceTemplate.ediUser(rsUkoly.getString("ediUser"));
                    dalsiPraceTemplate.ediDate("" + rsUkoly.getDate("ediDate"));
                    dalsiPraceTemplate.zalUser(rsUkoly.getString("zalUser"));
                    dalsiPraceTemplate.zalDate("" + rsUkoly.getDate("zalDate"));

                    dalsiPraceTemplate.typPrace(rsUkoly.getString("typPrace"));
                    dalsiPraceTemplate.praceStav(rsUkoly.getString("stavRec"));
                    dalsiPraceTemplate.pracePrac(rsUkoly.getString("zpracovatel"),rsUkoly.getString("pracovnik"));
                    dalsiPraceTemplate.rPredloha("" + idPredlohy);

                    stmtInsert.executeUpdate(dalsiPraceTemplate.getSQL_INSERT());
                }

                rsUkoly.close();//Uzavreni kurzoru
                rsTitul.close();//Uzavreni kurzoru
                conn.commit();//Commit jednoho kompletniho zaznamu
            }
            //Commit všeho najednou.
            //conn.commit();

            rsZakazka.close();
            stmtUkoly.close();
            stmtTitul.close();
            stmtZakaza.close();


////////////////////////////////////////////////////////////////////////////////
//KONEC PRVNI CASTI PREVODU
//ZACATEK DRUHE CASTI PREVODU - TITULY KTERE NEMAJI ZAKAZKU
////////////////////////////////////////////////////////////////////////////////

            String SQL_TitulyBezZakazky = "SELECT * FROM record INNER JOIN titul ON record.id = titul.id INNER JOIN titnkp ON record.id = titnkp.id WHERE record.id not in (select rtitnkpza from zakazka where rtitnkpza is not null group by rtitnkpza)";
            Statement stmtTitulyBezZakazky;


            stmtTitulyBezZakazky = conn.createStatement();
            rsTitul = stmtTitulyBezZakazky.executeQuery(SQL_TitulyBezZakazky);
            while (rsTitul.next()) {
                //Máme titul bez zakázky a teď ho zpracujeme
                predlohaTemplate = new OnePredloha();
                idPredlohy++;

                //Zakazka ma připojený titul a teď ho zpracujem
                if (!usedTitul.contains(rsTitul.getString("id"))) {
                    usedTitul.add(rsTitul.getString("id"));
                }
                predlohaTemplate.id(idPredlohy);
                predlohaTemplate.securityOwner(rsTitul.getString("securityOwner"));
                predlohaTemplate.poznRec(rsTitul.getString("poznRec"));
                predlohaTemplate.stavRec(rsTitul.getString("stavRec"));
                predlohaTemplate.finUser(rsTitul.getString("finUser"));
                predlohaTemplate.finDate("" + rsTitul.getDate("finDate"));
                predlohaTemplate.ediUser(rsTitul.getString("ediUser"));
                predlohaTemplate.ediDate("" + rsTitul.getDate("ediDate"));
                predlohaTemplate.zalUser(rsTitul.getString("zalUser"));
                predlohaTemplate.zalDate("" + rsTitul.getDate("zalDate"));
                predlohaTemplate.linkKatalog(rsTitul.getString("linkKatalog"));
                predlohaTemplate.cnb(rsTitul.getString("cnb"));
                predlohaTemplate.seriePart(rsTitul.getString("seriePart"));
                predlohaTemplate.serieName(rsTitul.getString("serieName"));
                predlohaTemplate.isbn(rsTitul.getString("isbn"));
                predlohaTemplate.vlastnik2(rsTitul.getString("vlastnik2"));
                predlohaTemplate.lccKlasifikace(rsTitul.getString("lccKlasifikace"));
                predlohaTemplate.digitalniKnihovna(rsTitul.getString("digitalniKnihovna"));
                predlohaTemplate.bazeAleph(rsTitul.getString("bazeAleph"));
                predlohaTemplate.vlastnik(rsTitul.getString("vlastnik"));
                predlohaTemplate.urlTitul(rsTitul.getString("url"));
                predlohaTemplate.formatAleph(rsTitul.getString("formatAleph"));
                predlohaTemplate.idNumberAleph(rsTitul.getString("idNumberAleph"));
                predlohaTemplate.idNumber(rsTitul.getString("idNumber"));
                predlohaTemplate.financovano(rsTitul.getString("financovano"));
                predlohaTemplate.zpusobDig(rsTitul.getString("zpusobDig"));
                predlohaTemplate.cast(rsTitul.getString("cast"));
                predlohaTemplate.pocetPoli(rsTitul.getString("pocetPoli"));
                predlohaTemplate.inverze(rsTitul.getString("inverze"));
                predlohaTemplate.rozsahTit(rsTitul.getString("rozsahTit"));
                predlohaTemplate.autorPrij(rsTitul.getString("autorPrij"));
                predlohaTemplate.issn(rsTitul.getString("issn"));
                predlohaTemplate.vyskaVcm(rsTitul.getString("vyskaVcm"));
                predlohaTemplate.pocetStran(rsTitul.getString("pocetStran"));
                predlohaTemplate.mistoVyd(rsTitul.getString("mistoVyd"));
                predlohaTemplate.rokVyd(rsTitul.getString("rokVyd"));
                predlohaTemplate.hlNazev(rsTitul.getString("hlNazev"));
                predlohaTemplate.oldClassname(rsTitul.getString("classname"));
                predlohaTemplate.oldTitulId(rsTitul.getString("id"));
                //predlohaTemplate.(rsTitul.getString(""));


                //Zpracujeme hned i Pripojene Mikrofilmy respektive XtitMF
                ReliefLogger.severe("SQL XtitNKPmf: " + SQL_XtitNkpMf + rsTitul.getString("id"));
                rsXPredMikro = stmtXtitNKPmf.executeQuery(SQL_XtitNkpMf + rsTitul.getString("id"));
                while(rsXPredMikro.next()) {
                    //Zpracujeme jeden zaznam za druhym
                    idXpredMikro++;
                    xTitMfTemplate = new OneXPredMikro();

                    xTitMfTemplate.id(idXpredMikro);
                    xTitMfTemplate.securityOwner(rsXPredMikro.getString("securityOwner"));
                    xTitMfTemplate.poznRec(rsXPredMikro.getString("poznRec"));
                    xTitMfTemplate.stavRec(rsXPredMikro.getString("stavRec"));
                    xTitMfTemplate.finUser(rsXPredMikro.getString("finUser"));
                    xTitMfTemplate.finDate("" + rsXPredMikro.getDate("finDate"));
                    xTitMfTemplate.ediUser(rsXPredMikro.getString("ediUser"));
                    xTitMfTemplate.ediDate("" + rsXPredMikro.getDate("ediDate"));
                    xTitMfTemplate.zalUser(rsXPredMikro.getString("zalUser"));
                    xTitMfTemplate.zalDate("" + rsXPredMikro.getDate("zalDate"));

                    xTitMfTemplate.rMikrofilm(rsXPredMikro.getString("rmikrofilmxt"));
                    xTitMfTemplate.rPredloha("" + idPredlohy);
                    xTitMfTemplate.poradiVeSvitku(rsXPredMikro.getString("poradivesvitku"));
                    xTitMfTemplate.poleCista(rsXPredMikro.getString("polecista"));
                    xTitMfTemplate.poleHruba(rsXPredMikro.getString("polehruba"));
                    xTitMfTemplate.pocetScanu(rsXPredMikro.getString("pocetscanu"));
                    xTitMfTemplate.mfeCislo(rsXPredMikro.getString("mfecislo"));
                    xTitMfTemplate.oldTitulId(rsXPredMikro.getString("rtitnkpxt"));
                    xTitMfTemplate.oldXTitNkpMfId(rsXPredMikro.getString("id"));
                    xTitMfTemplate.oznaceniCasti(rsXPredMikro.getString("oznacenicasti"));
                    xTitMfTemplate.carKod(rsXPredMikro.getString("carkod"));

                    stmtInsert.executeUpdate(xTitMfTemplate.getSQL_INSERT());
                }
                rsXPredMikro.close();//Uzavreni kurzoru

                //Zpracování exempláře a přívazku
                ReliefLogger.severe("SQL_Exemplar_count: " + SQL_Exemplar_count + rsTitul.getString("id"));
                rsExemplarCount = stmtExemplar.executeQuery(SQL_Exemplar_count + rsTitul.getString("id"));
                rsExemplarCount.next();
                pocetPripojenychExemplaru = rsExemplarCount.getInt(1);
                rsExemplarCount.close();

                if (pocetPripojenychExemplaru == 1) {
                    //Titul má právě jeden Exemplář
                    ReliefLogger.severe(SQL_Exemplar + rsTitul.getString("id"));
                    rsExemplar = stmtExemplar.executeQuery(SQL_Exemplar + rsTitul.getString("id"));
                    rsExemplar.next();
                    //máme jeden exemplář a teď ho zpracujem
                    predlohaTemplate.signatura(rsExemplar.getString("signatura"));
                    predlohaTemplate.carKod(rsExemplar.getString("carkod"));
                    //predlohaTemplate.obdobi(rsExemplar.getString("obdobi"));
                    predlohaTemplate.oldExemplarId(rsExemplar.getString("id"));

                    rsExemplar.close();
                } else if (pocetPripojenychExemplaru > 1) {
                    //Titul má více Exemplářů
                    countTitul++;
                    ReliefLogger.severe(SQL_Exemplar + rsTitul.getString("id"));
                    rsExemplar = stmtExemplar.executeQuery(SQL_Exemplar + rsTitul.getString("id"));
                    while (rsExemplar.next()) {
                        countExemplar++;
                        //Tyto tituly nebudou převáděny - musejí se převést ručně.
                        forgottenExemplar =
                                rsExemplar.getString("id") + "|" +
                                rsExemplar.getString("carkod") + "|" +
                                rsExemplar.getString("signatura") + "|" +
                                rsExemplar.getString("obdobi") + "|" +
                                rsTitul.getString("idNumberAleph") + "|" +
                                rsTitul.getString("idNumber") + "|" +
                                rsTitul.getString("issn") + "|" +
                                rsTitul.getString("mistoVyd") + "|" +
                                rsTitul.getString("rokVyd") + "|" +
                                rsTitul.getString("id") + "|" +
                                rsTitul.getString("hlNazev");
                        wm.addLine(forgottenExemplar);
                    }
                    rsExemplar.close();
                } else {
                    //Titul nemá žádný exemplář, zpracujeme přívazky
                    ReliefLogger.severe(SQL_Privazky + rsTitul.getString("id"));
                    rsPrivazky = stmtPrivazky.executeQuery(SQL_Privazky + rsTitul.getString("id"));
                    if (rsPrivazky.next()) {
                        //existuje privazek, zpracujeme ho
                        predlohaTemplate.poradi(rsPrivazky.getString("poradi"));
                        predlohaTemplate.oldPrivazekId(rsPrivazky.getString("id"));
                        predlohaTemplate.poznamkaPrivazek(rsPrivazky.getString("poznamka"));

                        //hledáme jestli přívazek má exemplář
                        ReliefLogger.severe(SQL_ExemplarZPrivazku + rsPrivazky.getString("rexemplar"));
                        rsExemplarZPrivazku = stmtExemplarZPrivazku.executeQuery(SQL_ExemplarZPrivazku + rsPrivazky.getString("rexemplar"));
                        if (rsExemplarZPrivazku.next()) {
                            //Mame exemplar pres privazek, zpracujeme ho
                            predlohaTemplate.signatura(rsExemplarZPrivazku.getString("signatura"));
                            predlohaTemplate.carKod(rsExemplarZPrivazku.getString("carkod"));
                            //predlohaTemplate.obdobi(rsExemplarZPrivazku.getString("obdobi"));
                            predlohaTemplate.oldExemplarId(rsExemplarZPrivazku.getString("id"));
                        }
                        rsExemplarZPrivazku.close();
                    }

                    rsPrivazky.close();
                }

                ReliefLogger.severe("SQL Predloha bez zakazky: " + predlohaTemplate.getSQL_INSERT());
                stmtInsert.executeUpdate(predlohaTemplate.getSQL_INSERT());
                conn.commit(); //Commit jednoho kompletniho Titulu bez zakazky
            }

            rsTitul.close();
            stmtTitulyBezZakazky.close();
            stmtXtitNKPmf.close();
            stmtExemplar.close();
            stmtPrivazky.close();
            stmtExemplarZPrivazku.close();
            //stmtInsert.close();

////////////////////////////////////////////////////////////////////////////////
//KONEC DRUHE CASTI PREVODU
//ZACATEK TRETI CASTI PREVODU - ZALOZENI PERIODIK A PROPOJENI S PREDLOHOU
////////////////////////////////////////////////////////////////////////////////
            Statement stmtTitulyMultiExemplarZakazka = conn.createStatement();
            Statement stmtPeriodikum = conn.createStatement();
            ResultSet rsTitulyMultiExemplarZakazka;
            ResultSet rsPeriodikum;
            int idPeriodikum = 0;
            OnePeriodikum periodikumTemplate;

            String SQL_SELECT_TITULYMULTI_EXEMPLAR = "select * from titul INNER JOIN titnkp ON titul.id = titnkp.id where titul.id in (select rtitulex from exemplar where rtitulex is not null group by rtitulex having count(rtitulex) > 1)";
            String SQL_SELECT_TITULYMULTI_ZAKAZKA = "select * from titul INNER JOIN titnkp ON titul.id = titnkp.id where titul.id in (select rtitnkpza from zakazka where rtitnkpza is not null group by rtitnkpza having count(rtitnkpza) > 1)";


            //Vytahnem si tituly, ktere jsou z Exemplare
            ReliefLogger.severe("SQL_SELECT_TITULYMULTI_EXEMPLAR: " + SQL_SELECT_TITULYMULTI_EXEMPLAR);
            rsTitulyMultiExemplarZakazka = stmtTitulyMultiExemplarZakazka.executeQuery(SQL_SELECT_TITULYMULTI_EXEMPLAR);
            while (rsTitulyMultiExemplarZakazka.next()) {
                ReliefLogger.severe("SQL SELECT JESTLI EXISTUJE PERIODIKUM: " + "SELECT * FROM PERIODIKUM WHERE oldTitulId = " + rsTitulyMultiExemplarZakazka.getString("id"));
                rsPeriodikum = stmtPeriodikum.executeQuery("SELECT * FROM PERIODIKUM WHERE oldTitulId = " + rsTitulyMultiExemplarZakazka.getString("id"));
                if (rsPeriodikum.next()) {
                    // Periodikum již existuje a jen k němu připojíme předlohu
                    ReliefLogger.severe("SQL PRIPOJENI PREDLOHY K EXISTUJICIMU PERIODIKU: " + "UPDATE predloha SET rperiodikum = " + rsPeriodikum.getString("id") + " WHERE oldtitulid = " + rsTitulyMultiExemplarZakazka.getString("id"));
                    stmtInsert.executeUpdate("UPDATE predloha SET rperiodikum = " + rsPeriodikum.getString("id") + " WHERE oldtitulid = " + rsTitulyMultiExemplarZakazka.getString("id"));
                } else {
                    // Periodikum ještě neexistuje, Založíme nové, doplníme hodnoty a připojíme předlohu
                    idPeriodikum++;
                    periodikumTemplate = new OnePeriodikum();

                    periodikumTemplate.id(idPeriodikum);
                    periodikumTemplate.oldTitulId(rsTitulyMultiExemplarZakazka.getString("id"));
                    periodikumTemplate.stavRec("active");
                    periodikumTemplate.zalUser("admin");
                    periodikumTemplate.zalDate("2010-11-03");
                    periodikumTemplate.securityOwner("relief");

                    periodikumTemplate.druhDokumentu(rsTitulyMultiExemplarZakazka.getString("formatAleph"));
                    periodikumTemplate.issn(rsTitulyMultiExemplarZakazka.getString("issn"));
                    periodikumTemplate.nazev(rsTitulyMultiExemplarZakazka.getString("hlNazev"));
                    periodikumTemplate.pole_001(rsTitulyMultiExemplarZakazka.getString("idNumber"));
                    periodikumTemplate.sysno(rsTitulyMultiExemplarZakazka.getString("idNumberAleph"));

                    ReliefLogger.severe("SQL INSERT NEW PERIODIKUM: " + periodikumTemplate.getSQL_INSERT());
                    stmtInsert.executeUpdate(periodikumTemplate.getSQL_INSERT());

                    ReliefLogger.severe("SQL PRIPOJENI PREDLOHY K NOVEMU PERIODIKU: " + "UPDATE predloha SET rperiodikum = " + idPeriodikum + " WHERE oldtitulid = " + rsTitulyMultiExemplarZakazka.getString("id"));
                    stmtInsert.executeUpdate("UPDATE predloha SET rperiodikum = " + idPeriodikum + " WHERE oldtitulid = " + rsTitulyMultiExemplarZakazka.getString("id"));
                }
                rsPeriodikum.close();
                conn.commit();
            }
            rsTitulyMultiExemplarZakazka.close();

            //Vytahnem si tituly, ktere jsou ze Zakazky
            ReliefLogger.severe("SQL_SELECT_TITULYMULTI_ZAKAZKA: " + SQL_SELECT_TITULYMULTI_ZAKAZKA);
            rsTitulyMultiExemplarZakazka = stmtTitulyMultiExemplarZakazka.executeQuery(SQL_SELECT_TITULYMULTI_ZAKAZKA);
            while (rsTitulyMultiExemplarZakazka.next()) {
                ReliefLogger.severe("SQL SELECT JESTLI EXISTUJE PERIODIKUM: " + "SELECT * FROM PERIODIKUM WHERE oldTitulId = " + rsTitulyMultiExemplarZakazka.getString("id"));
                rsPeriodikum = stmtPeriodikum.executeQuery("SELECT * FROM PERIODIKUM WHERE oldTitulId = " + rsTitulyMultiExemplarZakazka.getString("id"));
                if (rsPeriodikum.next()) {
                    // Periodikum již existuje a jen k němu připojíme předlohu
                    ReliefLogger.severe("SQL PRIPOJENI PREDLOHY K EXISTUJICIMU PERIODIKU: " + "UPDATE predloha SET rperiodikum = " + rsPeriodikum.getString("id") + " WHERE oldtitulid = " + rsTitulyMultiExemplarZakazka.getString("id"));
                    stmtInsert.executeUpdate("UPDATE predloha SET rperiodikum = " + rsPeriodikum.getString("id") + " WHERE oldtitulid = " + rsTitulyMultiExemplarZakazka.getString("id"));
                } else {
                    // Periodikum ještě neexistuje, Založíme nové, doplníme hodnoty a připojíme předlohu
                    idPeriodikum++;
                    periodikumTemplate = new OnePeriodikum();

                    periodikumTemplate.id(idPeriodikum);
                    periodikumTemplate.oldTitulId(rsTitulyMultiExemplarZakazka.getString("id"));
                    periodikumTemplate.stavRec("active");
                    periodikumTemplate.zalUser("admin");
                    periodikumTemplate.zalDate("2010-11-03");
                    periodikumTemplate.securityOwner("relief");

                    periodikumTemplate.druhDokumentu(rsTitulyMultiExemplarZakazka.getString("formatAleph"));
                    periodikumTemplate.issn(rsTitulyMultiExemplarZakazka.getString("issn"));
                    periodikumTemplate.nazev(rsTitulyMultiExemplarZakazka.getString("hlNazev"));
                    periodikumTemplate.pole_001(rsTitulyMultiExemplarZakazka.getString("idNumber"));
                    periodikumTemplate.sysno(rsTitulyMultiExemplarZakazka.getString("idNumberAleph"));

                    ReliefLogger.severe("SQL INSERT NEW PERIODIKUM: " + periodikumTemplate.getSQL_INSERT());
                    stmtInsert.executeUpdate(periodikumTemplate.getSQL_INSERT());

                    ReliefLogger.severe("SQL PRIPOJENI PREDLOHY K NOVEMU PERIODIKU: " + "UPDATE predloha SET rperiodikum = " + idPeriodikum + " WHERE oldtitulid = " + rsTitulyMultiExemplarZakazka.getString("id"));
                    stmtInsert.executeUpdate("UPDATE predloha SET rperiodikum = " + idPeriodikum + " WHERE oldtitulid = " + rsTitulyMultiExemplarZakazka.getString("id"));
                }
                rsPeriodikum.close();
                conn.commit();
            }
            rsTitulyMultiExemplarZakazka.close();

            stmtTitulyMultiExemplarZakazka.close();
            stmtInsert.close();
            conn.close();
        } catch(SQLException ex) {
            ex.printStackTrace();
            ReliefLogger.severe("CHYBA: " + ex.getSQLState() + " - " + ex.getMessage());
            wm.addLine("::: KONVERZE SKONČILA CHYBOU !!! :::");
            wm.addLine("CHYBA: " + ex.getMessage());
            return wm;
        }
        ReliefLogger.severe("Pocet titulu ktere maji vice exemplaru: " + countTitul);
        ReliefLogger.severe("Pocet vicenasobnych exemplaru: " + countExemplar);
        ReliefLogger.severe("::: IMPORT FINISHED :::");
        wm.addLine("::: IMPORT FINISHED :::");
        wm.addLine("Pocet titulu ktere maji vice exemplaru: " + countTitul);
        wm.addLine("Pocet vicenasobnych exemplaru: " + countExemplar);
        return wm;
    }

    /***************************************************************************
     *
     * @param value
     * @return
     */
    public static String checkValue(String value) {
        if (value != null) {
            if("null".equalsIgnoreCase(value)) return null;
            while(value.startsWith(" ")) {
                value = value.substring(1, value.length());
            }
            while (value.endsWith(" ")) {
                value = value.substring(0, value.length() - 1);
            }

            if (value.contains("'")) {
                value = value.replace("'", "''");
            }
            return "'" + value + "'";
        }
        return null;
    }
    
    /***************************************************************************
     *
     * @param value
     * @return
     */
    public static String checkValue2(String value) {
        if (value != null) {
            if("null".equalsIgnoreCase(value)) return null;
            while(value.startsWith(" ")) {
                value = value.substring(1, value.length());
            }
            while (value.endsWith(" ")) {
                value = value.substring(0, value.length() - 1);
            }

            if (value.contains("'")) {
                value = value.replace("'", "''");
            }
            return value;
        }
        return null;
    }
}


/*******************************************************************************
 *
 * @author martin
 */
class OnePredloha {
    private String idCislo              = null;
    //Zakazka - promene
    private int id;
    private String securityOwner        = null;
    private String poznRec              = null;
    private String stavRec              = null;
    private String finUser              = null;
    private String finDate              = null;
    private String ediUser              = null;
    private String ediDate              = null;
    private String zalUser              = null;
    private String zalDate              = null;
    private String skenDJVU             = null;
    private String udirdcz              = null;
    private String tempCarKod           = null;
    private String skenJPEG             = null;
    private String skenTXT              = null;
    private String skenPDF              = null;
    private String skenTIFF             = null;
    private String stupneSedi           = null;
    private String rozliseni            = null;
    private String skenScanStran        = null;
    private String tempParSkenovani     = null;
    private String typSkeneru           = null;
    private String skenPocetSouboru     = null;
    private String skenOCRSvabach       = null;
    private String skenOCR              = null;
    private String rozsah               = null;
    private String oldZakazkaId         = null;
    private String cisloZakazky         = null;
    private String url                  = null;
    private String metaXML              = null;
    private String metaObrazky          = null;
    //Titul - promene
    private String linkKatalog          = null;
    private String cnb                  = null;
    private String seriePart            = null;
    private String serieName            = null;
    private String isbn                 = null;
    private String vlastnik2            = null;
    private String lccKlasifikace       = null;
    private String digitalniKnihovna    = null;
    private String bazeAleph            = null;
    private String vlastnik             = null;
    private String urlTitul             = null;
    private String formatAleph          = null;
    private String idNumberAleph        = null;
    private String idNumber             = null;
    private String financovano          = null;
    private String zpusobDig            = null;
    private String cast                 = null;
    private String pocetPoli            = null;
    private String inverze              = null;
    private String rozsahTit            = null;
    private String autorPrij            = null;
    private String issn                 = null;
    private String vyskaVcm             = null;
    private String pocetStran           = null;
    private String mistoVyd             = null;
    private String rokVyd               = null;
    private String hlNazev              = null;
    private String oldClassname         = null;
    private String oldTitulId           = null;
    //Exemplar Promene
    private String signatura            = null;
    private String carKod               = null;
//    private String obdobi               = null;
    private String oldExemplarId        = null;
    //Privazek Promene
    private String poradi               = null;
    private String oldPrivazekId        = null;
    private String poznamkaPrivazek     = null;
    //private String  = null;


    //Zakazka - setovaci fce
    public void id(int value) {
        this.id = value;
        this.idCislo = "'Pr" + Utilities.doplnNulyPredCislo(value, 9, "0") + "'";
    }
    public void securityOwner(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.securityOwner = value;
    }
    public void poznRec(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.poznRec = value;
    }
    public void stavRec(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.stavRec = value;
    }
    public void finUser(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.finUser = value;
    }
    public void finDate(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        if (value != null) {
            value = "to_date(" + value + ",'yyyy-mm-dd')";
        }
        this.finDate = value;
    }
    public void ediUser(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.ediUser = value;
    }
    public void ediDate(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        if (value != null) {
            value = "to_date(" + value + ",'yyyy-mm-dd')";
        }
        this.ediDate = value;
    }
    public void zalUser(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.zalUser = value;
    }
    public void zalDate(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        if (value != null) {
            value = "to_date(" + value + ",'yyyy-mm-dd')";
        }
        this.zalDate = value;
    }
    public void skenDJVU(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.skenDJVU = value;
    }
    public void udirdcz(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.udirdcz = value;
    }
    public void tempCarKod(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.tempCarKod = value;
    }
    public void skenJPEG(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.skenJPEG = value;
    }
    public void skenTXT(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.skenTXT = value;
    }
    public void skenPDF(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.skenPDF = value;
    }
    public void skenTIFF(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.skenTIFF = value;
    }
    public void stupneSedi(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.stupneSedi = value;
    }
    public void rozliseni(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.rozliseni = value;
    }
    public void skenScanStran(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.skenScanStran = value;
    }
    public void tempParSkenovani(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.tempParSkenovani = value;
    }
    public void typSkeneru(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.typSkeneru = value;
    }
    public void skenPocetSouboru(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.skenPocetSouboru = value;
    }
    public void skenOCRSvabach(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.skenOCRSvabach = value;
    }
    public void skenOCR(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.skenOCR = value;
    }
    public void rozsah(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.rozsah = value;
    }
    public void oldZakazkaId(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.oldZakazkaId = value;
    }
    public void cisloZakazky(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.cisloZakazky = value;
    }
    public void url(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.url = value;
    }
    public void metaXML(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.metaXML = value;
    }
    public void metaObrazky(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.metaObrazky = value;
    }
    //Titul - setovaci fce
    public void linkKatalog(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.linkKatalog = value;
    }
    public void cnb(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.cnb = value;
    }
    public void seriePart(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.seriePart = value;
    }
    public void serieName(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.serieName = value;
    }
    public void isbn(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.isbn = value;
    }
    public void vlastnik2(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.vlastnik2 = value;
    }
    public void lccKlasifikace(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.lccKlasifikace = value;
    }
    public void digitalniKnihovna(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.digitalniKnihovna = value;
    }
    public void bazeAleph(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.bazeAleph = value;
    }
    public void vlastnik(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.vlastnik = value;
    }
    public void urlTitul(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.urlTitul = value;
    }
    public void formatAleph(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.formatAleph = value;
    }
    public void idNumberAleph(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.idNumberAleph = value;
    }
    public void idNumber(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.idNumber = value;
    }
    public void financovano(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.financovano = value;
    }
    public void zpusobDig(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.zpusobDig = value;
    }
    public void cast(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.cast = value;
    }
    public void pocetPoli(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.pocetPoli = value;
    }
    public void inverze(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.inverze = value;
    }
    public void rozsahTit(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.rozsahTit = value;
    }
    public void autorPrij(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.autorPrij = value;
    }
    public void issn(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.issn = value;
    }
    public void vyskaVcm(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.vyskaVcm = value;
    }
    public void pocetStran(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.pocetStran = value;
    }
    public void mistoVyd(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.mistoVyd = value;
    }
    public void rokVyd(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.rokVyd = value;
    }
    public void hlNazev(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.hlNazev = value;
    }
    public void oldClassname(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.oldClassname = value;
    }
    public void oldTitulId(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.oldTitulId = value;
    }
    //Exemplar - setovaci fce
    public void signatura(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.signatura = value;
    }
    public void carKod(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.carKod = value;
    }
//    public void obdobi(String value) {
//        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
//        this.obdobi = value;
//    }
    public void oldExemplarId(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.oldExemplarId = value;
    }
    //Privazek - setovaci fce
    public void poradi(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.poradi = value;
    }
    public void oldPrivazekId(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.oldPrivazekId = value;
    }
    public void poznamkaPrivazek(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.poznamkaPrivazek = value;
    }



    /***************************************************************************
     *
     * @return
     */
    public String getSQL_INSERT() {
        String exit =
        "INSERT INTO predloha (" +
            //Zakazka
            PredlohaEntity.sql_id + "," +
            PredlohaEntity.sql_idCislo + "," +
            PredlohaEntity.sql_securityOwner + "," +
            PredlohaEntity.sql_poznRec + "," +
            PredlohaEntity.sql_stavRec + "," +
            PredlohaEntity.sql_finUser + "," +
            PredlohaEntity.sql_finDate + "," +
            PredlohaEntity.sql_ediUser + "," +
            PredlohaEntity.sql_ediDate + "," +
            PredlohaEntity.sql_zalUser + "," +
            PredlohaEntity.sql_zalDate + "," +
            PredlohaEntity.sql_skenDJVU + "," +
            PredlohaEntity.sql_udirdcz + "," +
            PredlohaEntity.sql_tempCarKod + "," +
            PredlohaEntity.sql_skenJPEG + "," +
            PredlohaEntity.sql_skenTXT + "," +
            PredlohaEntity.sql_skenPDF + "," +
            PredlohaEntity.sql_skenTIFF + "," +
            PredlohaEntity.sql_skenStupneSedi + "," +
            PredlohaEntity.sql_rozliseni + "," +
            PredlohaEntity.sql_skenScanStran + "," +
            PredlohaEntity.sql_tempParSkenovani + "," +
            PredlohaEntity.sql_typSkeneru + "," +
            PredlohaEntity.sql_skenPocetSouboru + "," +
            PredlohaEntity.sql_skenOCRSvabach + "," +
            PredlohaEntity.sql_skenOCR + "," +
            PredlohaEntity.sql_rozsah + "," +
            PredlohaEntity.sql_oldZakazkaId + "," +
            PredlohaEntity.sql_cisloZakazky + "," +
            PredlohaEntity.sql_url + "," +
            PredlohaEntity.sql_metaXML + "," +
            PredlohaEntity.sql_metaObrazky + "," +
            //Titul
            PredlohaEntity.sql_linkKatalog + "," +
            PredlohaEntity.sql_cCNB + "," +
            PredlohaEntity.sql_seriePart + "," +
            PredlohaEntity.sql_serieName + "," +
            PredlohaEntity.sql_ISBN + "," +
            PredlohaEntity.sql_sigla2 + "," +
            PredlohaEntity.sql_lccKlasifikace + "," +
            PredlohaEntity.sql_digKnihovna + "," +
            PredlohaEntity.sql_katalog + "," +
            PredlohaEntity.sql_sigla1 + "," +
            PredlohaEntity.sql_urlTitul + "," +
            PredlohaEntity.sql_druhDokumentu + "," +
            PredlohaEntity.sql_sysno + "," +
            PredlohaEntity.sql_pole001 + "," +
            PredlohaEntity.sql_financovano + "," +
            PredlohaEntity.sql_zpusobDig + "," +
            PredlohaEntity.sql_cast + "," +
            PredlohaEntity.sql_pocetPoli + "," +
            PredlohaEntity.sql_mfCislo + "," +
            PredlohaEntity.sql_obdobi + "," +
            PredlohaEntity.sql_autor + "," +
            PredlohaEntity.sql_issn + "," +
            PredlohaEntity.sql_vyskaKnihy + "," +
            PredlohaEntity.sql_pocetStran + "," +
            PredlohaEntity.sql_mistoVyd + "," +
            PredlohaEntity.sql_rokVyd + "," +
            PredlohaEntity.sql_nazev + "," +
            PredlohaEntity.sql_oldClassTitul + "," +
            PredlohaEntity.sql_oldTitulId + "," +
            //Exemplar
            PredlohaEntity.sql_signatura + "," +
            PredlohaEntity.sql_carKod + "," +
//            PredlohaEntity.sql_obdobiExemplar + "," +
            PredlohaEntity.sql_oldExemplarId + "," +
            //Privazek
            PredlohaEntity.sql_poradiPriv + "," +
            PredlohaEntity.sql_oldPrivazekId + "," +
            PredlohaEntity.sql_privazekPoznamka +
            ") VALUES ('" + 
            //Zakazka
            this.id + "'," +
            this.idCislo + "," +
            this.securityOwner + "," +
            this.poznRec + "," +
            this.stavRec + "," +
            this.finUser + "," +
            this.finDate + "," +
            this.ediUser + "," +
            this.ediDate + "," +
            this.zalUser + "," +
            this.zalDate + "," +
            this.skenDJVU + "," +
            this.udirdcz + "," +
            this.tempCarKod + "," +
            this.skenJPEG + "," +
            this.skenTXT + "," +
            this.skenPDF + "," +
            this.skenTIFF + "," +
            this.stupneSedi + "," +
            this.rozliseni + "," +
            this.skenScanStran + "," +
            this.tempParSkenovani + "," +
            this.typSkeneru + "," +
            this.skenPocetSouboru + "," +
            this.skenOCRSvabach + "," +
            this.skenOCR + "," +
            this.rozsah + "," +
            this.oldZakazkaId + "," +
            this.cisloZakazky + "," +
            this.url + "," +
            this.metaXML + "," +
            this.metaObrazky + "," +
            //Titul
            this.linkKatalog + "," +
            this.cnb + "," +
            this.seriePart + "," +
            this.serieName + "," +
            this.isbn + "," +
            this.vlastnik2 + "," +
            this.lccKlasifikace + "," +
            this.digitalniKnihovna + "," +
            this.bazeAleph + "," +
            this.vlastnik + "," +
            this.urlTitul + "," +
            this.formatAleph + "," +
            this.idNumberAleph + "," +
            this.idNumber + "," +
            this.financovano + "," +
            this.zpusobDig + "," +
            this.cast + "," +
            this.pocetPoli + "," +
            this.inverze + "," +
            this.rozsahTit + "," +
            this.autorPrij + "," +
            this.issn + "," +
            this.vyskaVcm + "," +
            this.pocetStran + "," +
            this.mistoVyd + "," +
            this.rokVyd + "," +
            this.hlNazev + "," +
            this.oldClassname + "," +
            this.oldTitulId + "," +
            //Exemplar
            this.signatura + "," +
            this.carKod + "," +
//            this.obdobi + "," +
            this.oldExemplarId + "," +
            //Privazek
            this.poradi + "," +
            this.oldPrivazekId + "," +
            this.poznamkaPrivazek +
            ")";

        ReliefLogger.severe("SQL INSERT_PREDLOHA: " + exit);
        return exit;
    }


    

}


/*******************************************************************************
 * X Předloha Mikrofilm
 * @author martin
 */
class OneXPredMikro {
    private String idCislo = null;
    //XpredlohaMikrofilm - promene
    private int id;
    private String securityOwner        = null;
    private String poznRec              = null;
    private String stavRec              = null;
    private String finUser              = null;
    private String finDate              = null;
    private String ediUser              = null;
    private String ediDate              = null;
    private String zalUser              = null;
    private String zalDate              = null;

    private String rMikrofilm           = null;
    private String rPredloha            = null;
    private String poradiVeSvitku       = null;
    private String poleCista            = null;
    private String poleHruba            = null;
    private String pocetScanu           = null;
    private String mfeCislo             = null;
    private String oldTitulId           = null;
    private String oldXTitNkpMfId       = null;
    private String oznaceniCasti        = null;
    private String carKod               = null;


    //Zakazka - setovaci fce
    public void id(int value) {
        this.id = value;
        this.idCislo = "'Pm" + Utilities.doplnNulyPredCislo(value, 9, "0") + "'";
    }
    public void securityOwner(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.securityOwner = value;
    }
    public void poznRec(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.poznRec = value;
    }
    public void stavRec(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.stavRec = value;
    }
    public void finUser(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.finUser = value;
    }
    public void finDate(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        if (value != null) {
            value = "to_date(" + value + ",'yyyy-mm-dd')";
        }
        this.finDate = value;
    }
    public void ediUser(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.ediUser = value;
    }
    public void ediDate(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        if (value != null) {
            value = "to_date(" + value + ",'yyyy-mm-dd')";
        }
        this.ediDate = value;
    }
    public void zalUser(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.zalUser = value;
    }
    public void zalDate(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        if (value != null) {
            value = "to_date(" + value + ",'yyyy-mm-dd')";
        }
        this.zalDate = value;
    }
    public void rMikrofilm(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.rMikrofilm = value;
    }
    public void rPredloha(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.rPredloha = value;
    }
    public void poradiVeSvitku(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.poradiVeSvitku = value;
    }
    public void poleCista(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.poleCista = value;
    }
    public void poleHruba(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.poleHruba = value;
    }
    public void pocetScanu(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.pocetScanu = value;
    }
    public void mfeCislo(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.mfeCislo = value;
    }
    public void oldTitulId(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.oldTitulId = value;
    }
    public void oldXTitNkpMfId(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.oldXTitNkpMfId = value;
    }
    public void oznaceniCasti(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.oznaceniCasti = value;
    }
    public void carKod(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.carKod = value;
    }


    /***************************************************************************
     *
     * @return
     */
    public String getSQL_INSERT() {
        String exit =
        "INSERT INTO xpredmikro (" +
            XPredMikroEntity.sql_id + "," +
            XPredMikroEntity.sql_idCislo + "," +
            XPredMikroEntity.sql_securityOwner + "," +
            XPredMikroEntity.sql_poznRec + "," +
            XPredMikroEntity.sql_stavRec + "," +
            XPredMikroEntity.sql_finUser + "," +
            XPredMikroEntity.sql_finDate + "," +
            XPredMikroEntity.sql_ediUser + "," +
            XPredMikroEntity.sql_ediDate + "," +
            XPredMikroEntity.sql_zalUser + "," +
            XPredMikroEntity.sql_zalDate + "," +
            XPredMikroEntity.sql_rMikrofilm + "," +
            XPredMikroEntity.sql_rPredloha + "," +
            XPredMikroEntity.sql_poradiVeSvitku + "," +
            XPredMikroEntity.sql_poleCista + "," +
            XPredMikroEntity.sql_poleHruba + "," +
            XPredMikroEntity.sql_pocetScanu + "," +
            XPredMikroEntity.sql_mfeCislo + "," +
            XPredMikroEntity.sql_oldTitulId + "," +
            XPredMikroEntity.sql_oldXTitNkpMfId + "," +
            XPredMikroEntity.sql_oznaceniCasti + "," +
            XPredMikroEntity.sql_carKod +
            ") VALUES ('" +
            this.id + "'," +
            this.idCislo + "," +
            this.securityOwner + "," +
            this.poznRec + "," +
            this.stavRec + "," +
            this.finUser + "," +
            this.finDate + "," +
            this.ediUser + "," +
            this.ediDate + "," +
            this.zalUser + "," +
            this.zalDate + "," +
            this.rMikrofilm + "," +
            this.rPredloha + "," +
            this.poradiVeSvitku + "," +
            this.poleCista + "," +
            this.poleHruba + "," +
            this.pocetScanu + "," +
            this.mfeCislo + "," +
            this.oldTitulId + "," +
            this.oldXTitNkpMfId + "," +
            this.oznaceniCasti + "," +
            this.carKod +
            ")";

        ReliefLogger.severe("SQL INSERT_XpredMikro: " + exit);
        return exit;
    }
}


/*******************************************************************************
 * Další práce
 * @author martin
 */
class OneDalsiPrace {
    private String idCislo = null;
    //Dalsi prace - promene
    private int id;
    private String securityOwner    = null;
    private String poznRec          = null;
    private String stavRec          = null;
    private String finUser          = null;
    private String finDate          = null;
    private String ediUser          = null;
    private String ediDate          = null;
    private String zalUser          = null;
    private String zalDate          = null;
    private String typPrace         = null;
    private String praceStav        = null;
    private String pracePrac        = null;
    private String rPredloha        = null;


    //Dalsi prace - setovaci fce
    public void id(int value) {
        this.id = value;
        this.idCislo = "'Dp" + Utilities.doplnNulyPredCislo(value, 9, "0") + "'";
    }
    public void securityOwner(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.securityOwner = value;
    }
    public void poznRec(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.poznRec = value;
    }
    public void stavRec(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.stavRec = value;
    }
    public void finUser(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.finUser = value;
    }
    public void finDate(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        if (value != null) {
            value = "to_date(" + value + ",'yyyy-mm-dd')";
        }
        this.finDate = value;
    }
    public void ediUser(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.ediUser = value;
    }
    public void ediDate(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        if (value != null) {
            value = "to_date(" + value + ",'yyyy-mm-dd')";
        }
        this.ediDate = value;
    }
    public void zalUser(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.zalUser = value;
    }
    public void zalDate(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        if (value != null) {
            value = "to_date(" + value + ",'yyyy-mm-dd')";
        }
        this.zalDate = value;
    }
    public void typPrace(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.typPrace = value;
    }
    public void praceStav(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.praceStav = value;
    }
    public void pracePrac(String zpracovatel, String pracovnik) {
        zpracovatel = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(zpracovatel);
        pracovnik = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(pracovnik);
        if(pracovnik != null) {
            this.pracePrac = pracovnik;
        } else {
            this.pracePrac = zpracovatel;
        }
    }
    public void rPredloha(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.rPredloha = value;
    }

    /***************************************************************************
     *
     * @return
     */
    public String getSQL_INSERT() {
        String exit =
        "INSERT INTO dalsiprace (" +
            DalsiPraceEntity.sql_id + "," +
            DalsiPraceEntity.sql_idCislo + "," +
            DalsiPraceEntity.sql_securityOwner + "," +
            DalsiPraceEntity.sql_poznRec + "," +
            DalsiPraceEntity.sql_stavRec + "," +
            DalsiPraceEntity.sql_finUser + "," +
            DalsiPraceEntity.sql_finDate + "," +
            DalsiPraceEntity.sql_ediUser + "," +
            DalsiPraceEntity.sql_ediDate + "," +
            DalsiPraceEntity.sql_zalUser + "," +
            DalsiPraceEntity.sql_zalDate + "," +
            DalsiPraceEntity.sql_typPrace + "," +
            DalsiPraceEntity.sql_praceStav + "," +
            DalsiPraceEntity.sql_pracePrac + "," +
            DalsiPraceEntity.sql_rPredloha +
            ") VALUES ('" +
            this.id + "'," +
            this.idCislo + "," +
            this.securityOwner + "," +
            this.poznRec + "," +
            this.stavRec + "," +
            this.finUser + "," +
            this.finDate + "," +
            this.ediUser + "," +
            this.ediDate + "," +
            this.zalUser + "," +
            this.zalDate + "," +
            this.typPrace + "," +
            this.praceStav + "," +
            this.pracePrac + "," +
            this.rPredloha +
            ")";

        ReliefLogger.severe("SQL INSERT_DALSI_PRACE: " + exit);
        return exit;
    }
}


/*******************************************************************************
 * Periodikum
 * @author martin
 */
class OnePeriodikum {
    private String idCislo = null;
    //Periodikum - promene
    private int id;
    private String securityOwner    = null;
    private String poznRec          = null;
    private String stavRec          = null;
    private String finUser          = null;
    private String finDate          = null;
    private String ediUser          = null;
    private String ediDate          = null;
    private String zalUser          = null;
    private String zalDate          = null;
    private String nazev            = null;
    private String issn             = null;
    private String pole_001         = null;
    private String sysno            = null;
    private String oldTitulId       = null;
    private String urnnbn           = null;
    private String mfD              = null;
    private String druhDokumentu    = null;


    //Periodikum - setovaci fce
    public void id(int value) {
        this.id = value;
        this.idCislo = "'Pe" + Utilities.doplnNulyPredCislo(value, 9, "0") + "'";
    }
    public void securityOwner(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.securityOwner = value;
    }
    public void poznRec(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.poznRec = value;
    }
    public void stavRec(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.stavRec = value;
    }
    public void finUser(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.finUser = value;
    }
    public void finDate(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        if (value != null) {
            value = "to_date(" + value + ",'yyyy-mm-dd')";
        }
        this.finDate = value;
    }
    public void ediUser(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.ediUser = value;
    }
    public void ediDate(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        if (value != null) {
            value = "to_date(" + value + ",'yyyy-mm-dd')";
        }
        this.ediDate = value;
    }
    public void zalUser(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.zalUser = value;
    }
    public void zalDate(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        if (value != null) {
            value = "to_date(" + value + ",'yyyy-mm-dd')";
        }
        this.zalDate = value;
    }
    public void nazev(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.nazev = value;
    }
    public void issn(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.issn = value;
    }
    public void pole_001(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.pole_001 = value;
    }
    public void sysno(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.sysno = value;
    }
    public void oldTitulId(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.oldTitulId = value;
    }
    public void urnnbn(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.urnnbn = value;
    }
    public void mfD(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.mfD = value;
    }
    public void druhDokumentu(String value) {
        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(value);
        this.druhDokumentu = value;
    }

    /***************************************************************************
     *
     * @return
     */
    public String getSQL_INSERT() {
        String exit =
        "INSERT INTO periodikum (" +
            PeriodikumEntity.sql_id + "," +
            PeriodikumEntity.sql_idCislo + "," +
            PeriodikumEntity.sql_securityOwner + "," +
            PeriodikumEntity.sql_poznRec + "," +
            PeriodikumEntity.sql_stavRec + "," +
            PeriodikumEntity.sql_finUser + "," +
            PeriodikumEntity.sql_finDate + "," +
            PeriodikumEntity.sql_ediUser + "," +
            PeriodikumEntity.sql_ediDate + "," +
            PeriodikumEntity.sql_zalUser + "," +
            PeriodikumEntity.sql_zalDate + "," +
            PeriodikumEntity.sql_nazev + "," +
            PeriodikumEntity.sql_issn + "," +
            PeriodikumEntity.sql_pole001 + "," +
            PeriodikumEntity.sql_sysno + "," +
            PeriodikumEntity.sql_oldTitulId + "," +
            PeriodikumEntity.sql_urnnbn + "," +
            PeriodikumEntity.sql_mfD + "," +
            PeriodikumEntity.sql_druhDokumentu +
            ") VALUES ('" +
            this.id + "'," +
            this.idCislo + "," +
            this.securityOwner + "," +
            this.poznRec + "," +
            this.stavRec + "," +
            this.finUser + "," +
            this.finDate + "," +
            this.ediUser + "," +
            this.ediDate + "," +
            this.zalUser + "," +
            this.zalDate + "," +
            this.nazev + "," +
            this.issn + "," +
            this.pole_001 + "," +
            this.sysno + "," +
            this.oldTitulId + "," +
            this.urnnbn + "," +
            this.mfD + "," +
            this.druhDokumentu +
            ")";

        ReliefLogger.severe("SQL INSERT_DALSI_PERIODIKUM: " + exit);
        return exit;
    }
}
