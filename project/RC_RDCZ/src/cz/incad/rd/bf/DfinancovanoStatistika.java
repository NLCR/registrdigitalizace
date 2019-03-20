/* *****************************************************************************
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
import cz.incad.core.tools.Utilities;
import java.sql.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** ****************************************************************************
 *
 * @author martin
 */
public class DfinancovanoStatistika extends BussinessFunctionMother {

    /** ************************************************************************
     * 
     * @return
     * @throws ValidationException
     * @throws WizardException 
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        return null;
    }

    /** ************************************************************************
     * 
     * @param panelName
     * @return
     * @throws ValidationException
     * @throws WizardException 
     */
    public WizardMessage panelLeave(String panelName) throws ValidationException, WizardException {
        return null;
    }

    /** ************************************************************************
     * 
     * @return
     * @throws ValidationException
     * @throws WizardException
     * @throws ApplicationErrorException 
     */
    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        WizardMessage wm = new WizardMessage();
        Connection con = null;
        try {
            con = DirectConnection.getConnection();
            doStatistic(con);
            wm.addLine("Funkce proběhla v pořádku, údaje byly aktualizovány.");
        } catch(SQLException ex) {
            ReliefLogger.severe("Chyba při vyzvedavani primeho pripojeni do databaze, nebo pro zpracovani SQL pozadavku.", ex);
            wm.addLine("Funkce skončila neočekávanou chybou, statistiky mohou být neaktuální. Kontaktujte dodavatele software. že se v tento čas nastala vyjímečná událost: " + new Date());
        } finally {
            try {
                if (con != null) con.close();
            } catch(SQLException ex) {
                ReliefLogger.severe("Chyba pri zavirani primeho spojeni do databaze.", ex);
            }
        }

        return wm;
    }

    /** ************************************************************************
     * 
     * @param con
     * @throws SQLException 
     */
    private static void doStatistic(Connection con) throws SQLException {
        List<String> lUpDate = new LinkedList<String>();
        int countPredlohyCelkem;
        int countStrankyCelkem;
        int countPredlohyDokoncene;
        int countStrankyDokoncene;
        int countPredlohyZapocitane;
        int pocetStarn;
        String pocetStranValue;
        String stavRecValue;

        Statement stmtUpdate = con.createStatement();

        PreparedStatement pstmtCount = con.prepareStatement("select pocetstran,stavrec from predloha where financovano = ?");
        ResultSet rsCount;

        Statement stmtSigly = con.createStatement();
        ResultSet rsSigly = stmtSigly.executeQuery("select id,value from dlists where classname = 'cz.incad.nkp.digital.Dfinancovano'");

        while (rsSigly.next()) {
            //vynulování čítačů;
            countPredlohyCelkem         = 0;
            countStrankyCelkem          = 0;
            countPredlohyDokoncene      = 0;
            countStrankyDokoncene       = 0;
            countPredlohyZapocitane     = 0;
            
            //zpracováváme siglu za siglou
            int programId = rsSigly.getInt("id");
            String programValue = rsSigly.getString("value");
            if (programValue == null || programValue.isEmpty()) continue; 

            pstmtCount.setString(1, programValue);
            rsCount = pstmtCount.executeQuery();
            while (rsCount.next()) {
                countPredlohyCelkem++;
                //sečteme všechny strany a počet titulů
                pocetStranValue = rsCount.getString("pocetstran");
                stavRecValue = rsCount.getString("stavRec");
                
//                //Původní způsob, nahrazen try,catch
//                if (Utilities.canBeStringInteger(pocetStranValue)) {
//                    //lze započítat
//                    countPredlohyZapocitane++;
//                    countStrankyCelkem += Integer.parseInt(pocetStranValue);
//                    if ("finished".equals(stavRecValue) || "archived".equals(stavRecValue)) {
//                        //je dokončen
//                        countStrankyDokoncene += Integer.parseInt(pocetStranValue);
//                        countPredlohyDokoncene++;
//                    }
//                }

                try {
                    pocetStarn = Utilities.getFirstNumberFromString(pocetStranValue);
                    //lze započítat - nevyletěla vyjímka, takže nějaké číslo máme:)
                    countPredlohyZapocitane++;
                    countStrankyCelkem += pocetStarn;
                    if ("finished".equals(stavRecValue) || "archived".equals(stavRecValue)) {
                        //je dokončen
                        countStrankyDokoncene += pocetStarn;
                        countPredlohyDokoncene++;
                    }
                } catch(Exception ex) {
                }
            }

            //Vytvorime update pro danou siglu
            lUpDate.add("UPDATE Dfinancovano set pocetPredloh = " + countPredlohyCelkem + ", pocetStran = " + countStrankyCelkem + " , pocetpredlohDok = " + countPredlohyDokoncene + " , pocetStranDok = " + countStrankyDokoncene + ", pocetVyplStr = " + countPredlohyZapocitane + " where id = " + programId);
        }
        //Uzavřeme statement
        if (pstmtCount != null) pstmtCount.close();
        if (rsSigly != null) rsSigly.close();
        if (stmtSigly != null) stmtSigly.close();

        //Projdeme list s Updaty a odcommitujeme do databáze
        for (int i = 0; i < lUpDate.size(); i++) {
            ReliefLogger.info("SQL: " + lUpDate.get(i));
            stmtUpdate.execute(lUpDate.get(i));
        }

        con.commit();
        if (stmtUpdate != null) stmtUpdate.close();
    }

//    /** ************************************************************************
//     * 
//     * @param value
//     * @return 
//     */
//    private int findIntAtString(String value) {
//        Pattern intsOnly = Pattern.compile("\\d+"); 
//        Matcher makeMatch = intsOnly.matcher(value); 
//        makeMatch.find();
//        return Integer.parseInt(makeMatch.group());
//    }

}
