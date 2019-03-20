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
import cz.incad.rd.PredlohaEntity;
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
public class Predloha_SetVizitka extends BussinessFunctionMother {

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
        wm.addLine("Funkce doplňující vizitku u předlohy - SQL");

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
        ReliefUser ru = new ReliefUser(getWizardContextClient());
        Connection conn = DirectConnection.getConnection();
        
        Statement stmtPredlohy;
        Statement stmtUpdate;
        ResultSet rsPredlohy;
        Object jmeno;
        Object autor;
        Object rok;
        Object cast;
        Object rozsah;
        Object cisloPer;
        Object ccnb;
        Object carkod;
        Object signatura;
        Object druhDokumentu;
        Object ISSN;
        Object ISBN;

        String SQL_UPDATE;
        String SQL_SELECT;
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date now = new Date();

        try {
            stmtUpdate = conn.createStatement();
            stmtPredlohy = conn.createStatement();

            SQL_SELECT =
                    "select id," +
                    PredlohaEntity.sql_nazev + "," +
                    PredlohaEntity.sql_autor + "," +
                    PredlohaEntity.sql_rokVyd + "," +
                    PredlohaEntity.sql_cast + "," +
                    PredlohaEntity.sql_rozsah + "," +
                    PredlohaEntity.sql_cisloPer + "," +
                    PredlohaEntity.sql_cCNB + "," +
                    PredlohaEntity.sql_carKod + "," +
                    PredlohaEntity.sql_signatura + "," +
                    PredlohaEntity.sql_issn + "," +
                    PredlohaEntity.sql_ISBN + "," +
                    PredlohaEntity.sql_druhDokumentu +
                    " from predloha";
            ReliefLogger.severe("SQL SELECT: " + SQL_SELECT);
            rsPredlohy = stmtPredlohy.executeQuery(SQL_SELECT);
            while (rsPredlohy.next()) {
                jmeno = Predloha_KonverzeDatZeStareStrukturySQL.checkValue2(rsPredlohy.getString(PredlohaEntity.sql_nazev));
                autor = Predloha_KonverzeDatZeStareStrukturySQL.checkValue2(rsPredlohy.getString(PredlohaEntity.sql_autor));
                rok = Predloha_KonverzeDatZeStareStrukturySQL.checkValue2(rsPredlohy.getString(PredlohaEntity.sql_rokVyd));
                cast = Predloha_KonverzeDatZeStareStrukturySQL.checkValue2(rsPredlohy.getString(PredlohaEntity.sql_cast));
                rozsah = Predloha_KonverzeDatZeStareStrukturySQL.checkValue2(rsPredlohy.getString(PredlohaEntity.sql_rozsah));
                cisloPer = Predloha_KonverzeDatZeStareStrukturySQL.checkValue2(rsPredlohy.getString(PredlohaEntity.sql_cisloPer));
                ccnb = Predloha_KonverzeDatZeStareStrukturySQL.checkValue2(rsPredlohy.getString(PredlohaEntity.sql_cCNB));
                carkod = Predloha_KonverzeDatZeStareStrukturySQL.checkValue2(rsPredlohy.getString(PredlohaEntity.sql_carKod));
                signatura = Predloha_KonverzeDatZeStareStrukturySQL.checkValue2(rsPredlohy.getString(PredlohaEntity.sql_signatura));
                ISSN = Predloha_KonverzeDatZeStareStrukturySQL.checkValue2(rsPredlohy.getString(PredlohaEntity.sql_issn));
                ISBN = Predloha_KonverzeDatZeStareStrukturySQL.checkValue2(rsPredlohy.getString(PredlohaEntity.sql_ISBN));
                druhDokumentu = rsPredlohy.getString(PredlohaEntity.sql_druhDokumentu);

                SQL_UPDATE = "update predloha" +
                        " set " + PredlohaEntity.sql_vizitka + " = " +
                        PredlohaEntity.makePredloha(druhDokumentu ,jmeno, autor, rok, cast, rozsah, cisloPer, ccnb, carkod, signatura, ISSN, ISBN, true) + ", " +
                        "ediUser = '" + ru.getLogin() +"', " +
                        "ediDate = to_date('" + sdf.format(now) + "', 'dd.mm.yyyy')" +
                        "where id = " + rsPredlohy.getString("id");

                ReliefLogger.severe("SQL UPDATE: " + SQL_UPDATE);
                stmtUpdate.executeUpdate(SQL_UPDATE);
            }
            conn.commit();

            stmtPredlohy.close();
            stmtUpdate.close();
            conn.close();
        } catch(SQLException ex) {
            ex.printStackTrace();
            ReliefLogger.severe("EXCEPTION: " + ex.getMessage());
            wm.addLine("::: FUNCTION FAILED :::");
            wm.addLine("EXCEPTION: " + ex.getMessage());
        }

        wm.addLine("::: FUNCTION SUCCESSFUL :::");
        return wm;
    }

}
