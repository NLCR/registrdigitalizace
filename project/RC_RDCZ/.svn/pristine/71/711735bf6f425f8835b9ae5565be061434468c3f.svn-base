package cz.incad.nkp.digital.bf;

import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.core.tools.DirectConnection;
import cz.incad.core.tools.ReliefLogger;
import cz.incad.core.tools.ReliefWizardMessage;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


/*******************************************************************************
 *
 * @author martin
 */
public class SwitchMonografiePeriodikum extends BussinessFunctionMother {
    private static final String LOCAL_SECURITY = "data111";


    /***************************************************************************
     * 
     * @return
     * @throws ValidationException
     * @throws WizardException
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        ReliefWizardMessage wmf = new ReliefWizardMessage();

        //Kontrola na počet aktivních záznamů při spouštění BF
        //momentální nastavení je právě JEDEN označený záznam při spuštění BF
        if (getWizardContextClient().getSelectedKeys().length == 0) {
            throw new WizardException("Pro tuto funkci musí být označen alespoň jeden pracovní záznam.");
        }
        wmf.addInfo("Počet záznamů označených ke zpracování: " + getWizardContextClient().getSelectedKeys().length, null, null, null, null, 0);
        return wmf;
    }


    /***************************************************************************
     *
     * @param panelName
     * @return
     * @throws ValidationException
     * @throws WizardException
     */
    public WizardMessage panelLeave(String panelName) throws ValidationException, WizardException {
        return null;
    }


    /***************************************************************************
     *
     * @return
     * @throws ValidationException
     * @throws WizardException
     * @throws ApplicationErrorException
     */
    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        ReliefWizardMessage wmf = new ReliefWizardMessage();
        RecordsIterator rit = getWizardContextClient().getSelectedRecords();
        Connection conn = null;
        Statement stmt;

        try {
            conn = DirectConnection.getConnection();
            stmt = conn.createStatement();

            while (rit.hasMoreRecords()) {
                switchOne(stmt, rit.nextRecord().getKey().toString());
            }

            if (stmt != null) stmt.close();
            if (conn != null) conn.close();

        } catch (SQLException ex) {
            ReliefLogger.severe(ex.toString());
            ex.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex1) {
                ReliefLogger.severe(ex1.toString());
                ex1.printStackTrace();
            }
        }
        wmf.addUpozorneni("Záznamy byly přesunuty. Je nutné aby jste po zavření tohoto okna klikni na ikonu \"Obnova podle stavu databáze\"", "Ikonu nalezente na horní liště a vypadá takto: " + "<img src=\"./reliefKlient/images/controls/refresh.gif\" onmouseover=\"switchImage('brefresh')\" onmouseout=\"switchImage('brefresh')\" border = 0  id=\"brefresh\" title=\"Obnova podle stavu databáze\"  alt=\"Obnova podle stavu databáze\">", "Teprve po tomto úkonu budou zobrazena korektní data.", null, null, 0);
        //wmf.addLine("<img src=\"./reliefKlient/images/controls/refresh.gif\" onmouseover=\"switchImage('brefresh')\" onmouseout=\"switchImage('brefresh')\" border = 0  id=\"brefresh\" title=\"Obnova podle stavu databáze\"  alt=\"Obnova podle stavu databáze\">");
        return wmf;
    }


    /***************************************************************************
     *
     * @param recordID
     */
    private void switchOne(Statement stmt, String recordKey) throws SQLException, WizardException {
        String newClassName = null;
        String recordID;
        String nextSQL;

        if (recordKey.contains("cz.incad.nkp.digital.TFMonografie")) newClassName = "cz.incad.nkp.digital.TFSerial";
        if (recordKey.contains("cz.incad.nkp.digital.TFSerial")) newClassName = "cz.incad.nkp.digital.TFMonografie";

        if (newClassName == null) throw new WizardException("Vstupní záznam je nepodporovaného typu: " + recordKey);

        recordID = recordKey.substring(recordKey.indexOf(":") + 1);

        nextSQL = "update record set classname = '" + newClassName + "' where id = " + recordID;
        ReliefLogger.severe("SQL:" + nextSQL);
        stmt.executeUpdate(nextSQL);
        nextSQL = "update titul set classname = '" + newClassName + "' where id = " + recordID;
        ReliefLogger.severe("SQL:" + nextSQL);
        stmt.executeUpdate(nextSQL);
        nextSQL = "update titnkp set classname = '" + newClassName + "' where id = " + recordID;
        ReliefLogger.severe("SQL:" + nextSQL);
        stmt.executeUpdate(nextSQL);

        stmt.getConnection().commit();
    }

}
