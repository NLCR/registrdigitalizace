/*******************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.rd.list;

import com.amaio.plaant.dbdef.ListSource;
import com.amaio.plaant.dbdef.ListSourceItem;
import com.amaio.plaant.dbdef.ListValue;
import cz.incad.core.tools.DirectConnection;
import cz.incad.core.tools.ReliefLogger;
import cz.incad.core.tools.ReliefUser;
import cz.incad.core.tools.Utilities;
import java.io.Serializable;
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
public class SpecificDynamicLists implements ListSource, Serializable {
    //public static final String PARAMETR_ADD_TO_LIST_VALUE_NEMENIT = "nemenit";
    private Object[] parameters;
    
    /***************************************************************************
     * 
     * @param parms
     */
    public SpecificDynamicLists(Object parm0, Object parm1, Object parm2, Object parm3, Object parm4, Object parm5, Object parm6, Object parm7, Object parm8, Object parm9) {
        this.parameters  = new Object[10];
        this.parameters[0] = parm0;
        this.parameters[1] = parm1;
        this.parameters[2] = parm2;
        this.parameters[3] = parm3;
        this.parameters[4] = parm4;
        this.parameters[5] = parm5;
        this.parameters[6] = parm6;
        this.parameters[7] = parm7;
        this.parameters[8] = parm8;
        this.parameters[9] = parm9;
    }

    /***************************************************************************
     * 
     * @param listName
     * @return
     */
    public ListSourceItem[] getList(String listName) {
        ListSourceItem[] customListFinal = new ListSourceItem[0];
        Connection conn = DirectConnection.getConnection();
        Statement stmtSQL = null;
        ResultSet rsSQL = null;
        List<ListSourceItem> newList = new LinkedList<ListSourceItem>();
        ReliefUser ru;
        String valueZarazeni;
        String valueRole;

        try {
            if ("DSkener".equals(listName)) {
                //ReliefLogger.severe("Setting Specific DL: " + listName);
                ru = (ReliefUser)parameters[0];
                //pridani prazdne hodnoty
                newList.add(new ListValue("", "[null]"));
                stmtSQL = conn.createStatement();
                rsSQL = stmtSQL.executeQuery("select value,cz,listZarazeniUser,listRoleUser from dlists where classname = 'cz.incad.rd.Skener' order by value");
                while (rsSQL.next()) {
                    valueZarazeni = rsSQL.getString(3);
                    valueRole = rsSQL.getString(4);
                    if (isListValueAllovedForUser(valueZarazeni, valueRole, ru)) {
                        // 2 = i18n, 1 = value (DB) !!!
                        newList.add(new ListValue(rsSQL.getString(2), rsSQL.getString(1)));
                    }
                }
                return newList.toArray(customListFinal);

            } else if ("DpracovnikUkol".equals(listName)) {
                //ReliefLogger.severe("Setting Specific DL: " + listName);
                //pridani prazdne hodnoty
                newList.add(new ListValue("", "[null]"));
                stmtSQL = conn.createStatement();
                rsSQL = stmtSQL.executeQuery("select value,cz from dlists where classname = 'cz.incad.nkp.digital.DpracovnikUkol' order by cz");
                while (rsSQL.next()) {
                    // 2 = i18n, 1 = value (DB) !!!
                    newList.add(new ListValue(rsSQL.getString(2), rsSQL.getString(1)));
                }
                return newList.toArray(customListFinal);

            } else if ("DdigZpracEditor".equals(listName)) {
                //ReliefLogger.severe("Setting Specific DL: " + listName);
                //pridani prazdne hodnoty
                newList.add(new ListValue("", "[null]"));
                stmtSQL = conn.createStatement();
                rsSQL = stmtSQL.executeQuery("select value,cz from dlists where classname = 'cz.incad.rd.list.DdigZpracEditor' order by cz");
                while (rsSQL.next()) {
                    // 2 = i18n, 1 = value (DB) !!!
                    newList.add(new ListValue(rsSQL.getString(2), rsSQL.getString(1)));
                }
                return newList.toArray(customListFinal);

            } else if ("DzpusobDig".equals(listName)) {
                //ReliefLogger.severe("Setting Specific DL: " + listName);
                //pridani prazdne hodnoty
                newList.add(new ListValue("", "[null]"));
                stmtSQL = conn.createStatement();
                rsSQL = stmtSQL.executeQuery("select value,cz from dlists where classname = 'cz.incad.nkp.digital.DzpusobDig' order by cz");
                while (rsSQL.next()) {
                    // 2 = i18n, 1 = value (DB) !!!
                    newList.add(new ListValue(rsSQL.getString(2), rsSQL.getString(1)));
                }
                return newList.toArray(customListFinal);

            } else if ("Dprofil".equals(listName)) {
                //ReliefLogger.severe("Setting Specific DL: " + listName);
                customListFinal = new ListSourceItem[0];
                return customListFinal;

            //} else if ("".equals(listName)) {
            //    ReliefLogger.severe("Setting Specific DL: " + listName);
            //    customListFinal = new ListSourceItem[0];
            //    return customListFinal;

            } else {
                // Nebyl nalezen zadny list
                ReliefLogger.severe("WARNING: nebyl nalezen spacificky dynamicky list: " + listName);
                customListFinal = new ListSourceItem[0];
                return customListFinal;
            }
            //ListSourceItem customListFinal[] = {new ListValue("i18n value1", "value1"), new ListValue("i18n value2", "value2")};

        } catch(Exception ex) {
            ReliefLogger.severe("Vyjímka při generování custom Dynamic Listu: " + listName, ex);
            return customListFinal;
        } finally {
            try {
                if (rsSQL != null) rsSQL.close();
                if (stmtSQL != null) stmtSQL.close();
                if (conn != null) conn.close();
                //ReliefLogger.severe("SPECIFICKE DLISTY - FINALY BLOK");
            } catch(SQLException ex) {
                ReliefLogger.severe("Vyjímka při uzavírání připojení do databáze.", ex);
            }
        }
    }

//    /***************************************************************************
//     * 
//     * @param ru
//     * @param list_ZarazeniUser_raw
//     * @param list_RoleUser_raw
//     * @return 
//     */
//    private static boolean isAcceptable(ReliefUser ru, String list_ZarazeniUser_raw, String list_RoleUser_raw) {
//        String user_ZarazeniUser_raw = ru.getZarazeni();
//        String user_roleUser_raw = ru.getKompetence();
//        return isAcceptable(list_ZarazeniUser_raw, list_RoleUser_raw, user_ZarazeniUser_raw, user_roleUser_raw);
//    }

    /***************************************************************************
     * 
     * @param list_ZarazeniUser_raw
     * @param list_RoleUser_raw
     * @param user_ZarazeniUser_raw
     * @param user_RoleUser_raw
     * @return 
     */
    private static boolean isListValueAllovedForUser(String list_ZarazeniUser_raw, String list_RoleUser_raw, ReliefUser ru) {
//        ReliefLogger.severe("list_ZarazeniUser_raw: " + list_ZarazeniUser_raw);
//        ReliefLogger.severe("list_RoleUser_raw: " + list_RoleUser_raw);
//        ReliefLogger.severe("XXXXXX: 0");
        List<String> list_ZarazeniUser;// = Utilities.getListFromFormatedString(list_ZarazeniUser_raw, ";", false);
        List<String> list_RoleUser;    // = Utilities.getListFromFormatedString(list_RoleUser_raw, ";", false);
        List<String> user_ZarazeniUser;// = Utilities.getListFromFormatedString(user_ZarazeniUser_raw, ";", false);
        List<String> user_RoleUser;    // = Utilities.getListFromFormatedString(user_RoleUser_raw, ";", false);
        
        //Administrátoři vidí vždy všechny hodnoty
        //ReliefLogger.severe("isSystemAdmin: " + ru.isSystemAdmin());
        //ReliefLogger.severe("isAppAdmin: " + ru.isApplicationAdmin());
        if (ru.isSystemAdmin() || ru.isApplicationAdmin()){
            //ReliefLogger.severe("XXXXXX: 0a");
            return true;
        }
        //ReliefLogger.severe("XXXXXX: 0b");

        //security uživatele
        String user_ZarazeniUser_raw = ru.getZarazeni();
        String user_RoleUser_raw = ru.getKompetence();

        //List bez omezení vždy je akceptovaný
        if (list_ZarazeniUser_raw == null && list_RoleUser_raw == null) {
            //ReliefLogger.severe("XXXXXX: 1");
            return true;
        }
        
        if (list_RoleUser_raw == null) { //Případ kdy list nemá role ale má zařazení
            //ReliefLogger.severe("XXXXXX: 2a");
            if (user_ZarazeniUser_raw == null) return false;
            //ReliefLogger.severe("XXXXXX: 2b");
            list_ZarazeniUser = Utilities.getListFromFormatedString(list_ZarazeniUser_raw, ";", false);
            user_ZarazeniUser = Utilities.getListFromFormatedString(user_ZarazeniUser_raw, ";", false);
            return Utilities.compareLists(user_ZarazeniUser, list_ZarazeniUser);
        }
        
        if (list_ZarazeniUser_raw == null) { //Případ kdy list nemá zařazení ale má role
            //ReliefLogger.severe("XXXXXX: 3a");
            if (user_RoleUser_raw == null) return false;
            //ReliefLogger.severe("XXXXXX: 3b");
            list_RoleUser = Utilities.getListFromFormatedString(list_RoleUser_raw, ";", false);
            user_RoleUser = Utilities.getListFromFormatedString(user_RoleUser_raw, ";", false);
            return Utilities.compareLists(user_RoleUser, list_RoleUser);
        }
   
        //Případ kdy list má zařazení i role
        //ReliefLogger.severe("XXXXXX: 4a");
        if (user_ZarazeniUser_raw == null || user_RoleUser_raw == null) return false;
        //ReliefLogger.severe("XXXXXX: 4b");
        list_ZarazeniUser = Utilities.getListFromFormatedString(list_ZarazeniUser_raw, ";", false);
        list_RoleUser = Utilities.getListFromFormatedString(list_RoleUser_raw, ";", false);
        user_ZarazeniUser = Utilities.getListFromFormatedString(user_ZarazeniUser_raw, ";", false);
        user_RoleUser = Utilities.getListFromFormatedString(user_RoleUser_raw, ";", false);
        if (Utilities.compareLists(user_ZarazeniUser, list_ZarazeniUser) && Utilities.compareLists(user_RoleUser, list_RoleUser)) {
            //ReliefLogger.severe("XXXXXX: 4c");
            return true;
        }

        return false;
    }

//    /***************************************************************************
//     * 
//     * @param parameter
//     * @return
//     */
//    private boolean isParameter(int parameter) {
//        boolean exit = false;
//
//        for (int i = 0; i < this.parameters.length; i++) {
//            if (parameter == this.parameters[i])
//        }
//        return exit;
//    }

}
