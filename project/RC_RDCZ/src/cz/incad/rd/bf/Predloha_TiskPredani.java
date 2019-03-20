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
import cz.incad.core.caches.CacheCoreSetting;
import cz.incad.core.tools.DirectConnection;
import cz.incad.core.tools.ReliefLogger;
import cz.incad.core.tools.ReliefUser;
import cz.incad.core.tools.ReliefWizardMessage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

/*******************************************************************************
 *
 * @author martin
 */
public class Predloha_TiskPredani extends BussinessFunctionMother {
    private static final String LOCAL_SECURITY = "tisk103";
    private Object VARKA = null;
    private Object SECURITYOWNER;
    private Object DATUM = "";
    private Object PREDAL = "";
    private Object PREVZAL = "";

    /***************************************************************************
     *
     * @return
     * @throws ValidationException
     * @throws WizardException
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        ReliefWizardMessage rwm = new ReliefWizardMessage();
        int countVarka = 0;
        String infoVarka = "";
        ReliefUser ru = new ReliefUser(getWizardContextClient());
//        //Kontrola na počet aktivních záznamů při spouštění BF
//        //momentální nastavení je právě JEDEN označený záznam při spuštění BF
//        if (getWizardContextClient().getSelectedKeys().length != 1) {
//            throw new WizardException("Pro tuto funkci musí být označen jeden pracovní záznam.");
//        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        this.SECURITYOWNER = ru.getZarazeni();
        this.DATUM = sdf.format(new Date());
        this.PREDAL = "NK - centrum digitalizace";

        Connection conn = DirectConnection.getConnection();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("select predVarka from predloha where securityowner = '" + this.SECURITYOWNER + "' and predvarka is not null group by predVarka");
            while (rs.next()) {
                countVarka++;
                if(countVarka == 11) {
                    infoVarka += "<br>";
                }
                infoVarka += "'" + rs.getString("predVarka") + "';";
            }
            rwm.addInfo("Existující várky", infoVarka, null, null, null, 0);

            //Uzavření zdrojů
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (SQLException ex) {
            ReliefLogger.severe("Chyba pri zpracovani SQL požadavku.", ex);
            throw new WizardException("Při běhu funkce vyvstala neočekávaná chyba, kontaktujte dodavatele software.");
        }


        return rwm;
    }

    /***************************************************************************
     *
     * @param panelName
     * @return
     * @throws ValidationException
     * @throws WizardException
     */
    public WizardMessage panelLeave(String panelName) throws ValidationException, WizardException {
        ReliefWizardMessage rwm = new ReliefWizardMessage();
        if ("panel01".equals(panelName)) {
            this.VARKA = getWizardContextClient().getWizardRecord().getSimpleField("varka").getValue();
            Connection conn = DirectConnection.getConnection();

            try {
                ResultSet rs;
                Statement stmt = conn.createStatement();

                rs = stmt.executeQuery("Select cz from dlists where classname = 'cz.incad.core.list.DzarazeniUser' and value in (select predKomuOdKoho from predloha where securityowner = '" + this.SECURITYOWNER + "' and predkdo = '" + this.SECURITYOWNER + "' and predtypakce = 'predani' and predvarka = '" + this.VARKA + "' group by predKomuOdKoho)");
                if (rs.next()) {
                    this.PREVZAL = rs.getString("cz");
                    if (rs != null) rs.close();
                } else {
                    if (rs != null) rs.close();
                    throw new WizardException("Nebyly nalezeny žádné odpovídající záznamy.");
                }

                rwm.addInfo("Várka: " + this.VARKA, "Datum: " + this.DATUM, "Předal: " + this.PREDAL, "Převzal: " + this.PREVZAL, null, 0);
                
                rs = stmt.executeQuery("select signatura,carkod,rokvyd,nazev,predKomuOdKoho from predloha where securityowner = '" + this.SECURITYOWNER + "' and predkdo = '" + this.SECURITYOWNER + "' and predtypakce = 'predani' and predvarka = '" + this.VARKA + "'");
                rwm.addLine("<table border = 1px><tr><th>Signatura</th><th>Čár. kód</th><th>Název</th><th>Rok vydání</th></tr>");
                while (rs.next()) {
                    rwm.addLine("<tr><td>" + rs.getString("signatura") + "</td><td>" + rs.getString("carkod") + "</td><td>" + rs.getString("nazev") + "</td><td>" + rs.getString("rokvyd") + "</td></tr>");
                }
                rwm.addLine("</table>");

                //Uzavření zdrojů
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ReliefLogger.severe("Chyba pri zpracovani SQL požadavku.", ex);
                throw new WizardException("Při běhu funkce vyvstala neočekávaná chyba, kontaktujte dodavatele software.");
            }
            
        } else if ("panel02".equals(panelName)) {
            //zatim nic, koukaci panel
        }
        return rwm;
    }

    /***************************************************************************
     *
     * @return
     * @throws ValidationException
     * @throws WizardException
     * @throws ApplicationErrorException
     */
    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        ReliefWizardMessage rwm = new ReliefWizardMessage();
        String reportRequest = CacheCoreSetting.getCoreSetting(RC_JASPERREPORTS_ROOT_HTTP) + "?reportdir=NKP_PREDAVACI_PROTOKOL&reportFileName=PredavaciProtokol&VARKA=" + this.VARKA + "&SECURITYOWNER=" + this.SECURITYOWNER + "&DATUM=" + this.DATUM + "&PREDAL=" + this.PREDAL + "&PREVZAL=" + this.PREVZAL + "&action=Print+report";

        rwm.addInfo("Tiskový výstup Průvodka mikrofilmu pro čan " + this.VARKA, null, null, null, null, 0);
        ReliefLogger.severe("REPORT REQUEST: " + reportRequest);
        rwm.addLine("<SCRIPT language=\"JavaScript\">window.location=\"" + reportRequest + "\"</SCRIPT>");
        return rwm;
    }

}
