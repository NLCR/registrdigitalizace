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
public class Config_RepairAllMainFlags extends BussinessFunctionMother {

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
        Connection conn;
        boolean isMainFlagFound;
        Statement stmtMainFlagGroup;
        Statement stmtMainFlag;
        Statement stmtInsert;
        Statement stmtMainFlagTest;

        ResultSet rsMainFlagGroup;
        ResultSet rsMainFlag;
        ResultSet rsMainFlagTest;

        List<TableAndField> lNullExecution = new LinkedList<TableAndField>();

        lNullExecution.add(new TableAndField("PREDLOHA",   "RPERIODIKUM"));
        lNullExecution.add(new TableAndField("XPREDMIKRO", "RMIKROFILM"));
        lNullExecution.add(new TableAndField("XPREDMIKRO", "RPREDLOHA"));
        lNullExecution.add(new TableAndField("DALSIPRACE", "RPREDLOHADP"));
        lNullExecution.add(new TableAndField("DALSIPRACE", "RPROFIL"));
        //lNullExecution.add(new TableAndField("", ""));


        try {
            conn = DirectConnection.getConnection();
            stmtMainFlagGroup   = conn.createStatement();
            stmtMainFlag        = conn.createStatement();
            stmtInsert          = conn.createStatement();
            stmtMainFlagTest    = conn.createStatement();

            //Nejprve se zbavíme null hodnot
            for (int i = 0; i < lNullExecution.size(); i++) {
                ReliefLogger.severe("SQL UPDATE NULL EXECUTION: " + "UPDATE " + lNullExecution.get(i).tableName + " SET " + lNullExecution.get(i).fieldName + "MF" + " = 0 WHERE " + lNullExecution.get(i).fieldName + "MF" + " IS null AND " + lNullExecution.get(i).fieldName + " IS NOT null");
                stmtInsert.executeUpdate("UPDATE " + lNullExecution.get(i).tableName + " SET " + lNullExecution.get(i).fieldName + "MF" + " = 0 WHERE " + lNullExecution.get(i).fieldName + "MF" + " IS null AND " + lNullExecution.get(i).fieldName + " IS NOT null");
            }
            conn.commit();//Commit odebrani null hodnot

            for (int i = 0; i < lNullExecution.size(); i++) {
                ReliefLogger.severe("SQL SELECT MAINFLAG GROUP: " + "");
                rsMainFlagGroup = stmtMainFlagGroup.executeQuery("SELECT " + lNullExecution.get(i).fieldName +" FROM " + lNullExecution.get(i).tableName + " where " + lNullExecution.get(i).fieldName +" is not null group by " + lNullExecution.get(i).fieldName);
                while (rsMainFlagGroup.next()) {
                    //mame seznam vsech predloh ktere maji referenci a tudiz by meli mit mainflag
                    //povedeme kontrolu jestli je potreba mainflag nastavovat
                    ReliefLogger.severe("SQL MAINFLAG TEST: " + "select count(" + lNullExecution.get(i).fieldName + "MF" + ") from " + lNullExecution.get(i).tableName + " where " + lNullExecution.get(i).fieldName + " = " + rsMainFlagGroup.getString(1)+ " and " + lNullExecution.get(i).fieldName + "MF" + " = 1");
                    rsMainFlagTest = stmtMainFlagTest.executeQuery("select count(" + lNullExecution.get(i).fieldName + "MF" + ") from " + lNullExecution.get(i).tableName + " where " + lNullExecution.get(i).fieldName + " = " + rsMainFlagGroup.getString(1)+ " and " + lNullExecution.get(i).fieldName + "MF" + " = 1");
                    rsMainFlagTest.next();
                    switch(rsMainFlagTest.getInt(1)) {
                        case 0: {
                            //Chybi mainflag musime ho doplnit, doplnime k prvnimu zaznamu na kterej narazime.
                            ReliefLogger.severe("SQL SELECT RECORDS FROM ONE REFERENCE: " + "SELECT id FROM " + lNullExecution.get(i).tableName + " WHERE " + lNullExecution.get(i).fieldName + " = " + rsMainFlagGroup.getString(1));
                            rsMainFlag = stmtMainFlag.executeQuery("SELECT id FROM " + lNullExecution.get(i).tableName + " WHERE " + lNullExecution.get(i).fieldName + " = " + rsMainFlagGroup.getString(1));
                            if (rsMainFlag.next()) {
                                ReliefLogger.severe("UPDATE SET MAINFLAG 1: " + "UPDATE " + lNullExecution.get(i).tableName + " SET " + lNullExecution.get(i).fieldName + "MF" + " = 1 WHERE id = " + rsMainFlag.getString("id"));
                                stmtInsert.executeUpdate("UPDATE " + lNullExecution.get(i).tableName + " SET " + lNullExecution.get(i).fieldName + "MF" + " = 1 WHERE id = " + rsMainFlag.getString("id"));
                            }
                            rsMainFlag.close();
                            break;
                        }
                        case 1: {
                            //Vse je v poradku a nemusime nic opravovat
                            break;
                        }
                        default: {
                            //Mainflagu je vic ostatni musime odmazat, nechame jen prvni na ktery narazime.
                            isMainFlagFound = false;
                            ReliefLogger.severe("SQL SELECT RECORDS FROM ONE REFERENCE: " + "select id," + lNullExecution.get(i).fieldName + "MF" +" from " + lNullExecution.get(i).tableName + " where " + lNullExecution.get(i).fieldName + " = " + rsMainFlagGroup.getString(1));
                            rsMainFlag = stmtMainFlag.executeQuery("select id," + lNullExecution.get(i).fieldName + "MF" +" from " + lNullExecution.get(i).tableName + " where " + lNullExecution.get(i).fieldName + " = " + rsMainFlagGroup.getString(1));
                            while (rsMainFlag.next()) {
                                if (isMainFlagFound) {
                                    if (rsMainFlag.getInt(2) == 1) {
                                        ReliefLogger.severe("UPDATE SET MAINFLAG 0: " + "UPDATE " + lNullExecution.get(i).tableName + " SET " + lNullExecution.get(i).fieldName + "MF" + " = 0 WHERE id = " + rsMainFlag.getString("id"));
                                        stmtInsert.executeUpdate("UPDATE " + lNullExecution.get(i).tableName + " SET " + lNullExecution.get(i).fieldName + "MF" + " = 0 WHERE id = " + rsMainFlag.getString("id"));
                                    }
                                } else {
                                    if (rsMainFlag.getInt(2) == 1) {
                                        isMainFlagFound = true;
                                    }
                                }
                            }

                            rsMainFlag.close();
                            break;
                        }
                    }
                    rsMainFlagTest.close();
                    conn.commit();
                }
                rsMainFlagGroup.close();
            }

            stmtMainFlagTest.close();
            stmtMainFlagGroup.close();
            stmtMainFlag.close();
            stmtInsert.close();
            conn.close();
        } catch(SQLException ex) {
            ex.printStackTrace();
            ReliefLogger.severe(ex.getMessage());
            wm.addLine("::: FAILED :::");
            wm.addLine("EXCEPTION: " + ex.getMessage());
            return wm;
        }

        wm.addLine("::: FUNCTION FINISHED SUCCESSFUL :::");
        return wm;
    }

}

/*******************************************************************************
 * Instance představuje kombinaci Tabulka pole, které bude dále zpracováváno
 * @author martin
 */
class TableAndField {
    public String tableName;
    public String fieldName;

    /***************************************************************************
     * Konstruktor
     * @param tName
     * @param fName
     */
    public TableAndField(String tName, String fName) {
        this.tableName = tName;
        this.fieldName = fName;
    }
}
