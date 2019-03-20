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
import cz.incad.rd.DalsiPraceEntity;
import cz.incad.rd.PredlohaEntity;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*******************************************************************************
 *
 * @author martin
 */
public class Predloha_TransformDalsiPrace extends BussinessFunctionMother {

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
        wm.addLine("Konvertní Funkce... Konverze satrých úkolů do nové struktury.");

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

        Statement stmtPredlohy;
        Statement stmtInsert;
        Statement stmtDalsiUkoly;
        ResultSet rsPredlohy;
        ResultSet rsDalsiUkoly;

        String typPrace;
        String value = null;

        try {
            stmtPredlohy = conn.createStatement();
            stmtInsert = conn.createStatement();
            stmtDalsiUkoly = conn.createStatement();
            rsPredlohy = stmtPredlohy.executeQuery("SELECT id from Predloha");
            while (rsPredlohy.next()) {
                rsDalsiUkoly = stmtDalsiUkoly.executeQuery("SELECT * FROM dalsiprace where typprace is not null and rpredloha = " + rsPredlohy.getString(1));
                while (rsDalsiUkoly.next()) {
                    typPrace = rsDalsiUkoly.getString(DalsiPraceEntity.sql_typPrace);
                    if ("skenovani".equalsIgnoreCase(typPrace)) {
                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(rsDalsiUkoly.getString(DalsiPraceEntity.sql_stavRec));
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_skenStav + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(rsDalsiUkoly.getString(DalsiPraceEntity.sql_pracePrac));
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_skenPrac + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue("" + rsDalsiUkoly.getDate(DalsiPraceEntity.sql_finDate));
                        if (value != null) {
                            value = "to_date(" + value + ",'yyyy-mm-dd')";
                        }
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_skenDate + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                    } else if ("pripravaDokumentu".equalsIgnoreCase(typPrace)) {
                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(rsDalsiUkoly.getString(DalsiPraceEntity.sql_stavRec));
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_pripStav + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(rsDalsiUkoly.getString(DalsiPraceEntity.sql_pracePrac));
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_pripPrac + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue("" + rsDalsiUkoly.getDate(DalsiPraceEntity.sql_finDate));
                        if (value != null) {
                            value = "to_date(" + value + ",'yyyy-mm-dd')";
                        }
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_pripDate + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(rsDalsiUkoly.getString(DalsiPraceEntity.sql_poznRec));
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_pripPozn + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                    } else if ("tvorbaMetadat".equalsIgnoreCase(typPrace)) {
                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(rsDalsiUkoly.getString(DalsiPraceEntity.sql_stavRec));
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_metaStav + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(rsDalsiUkoly.getString(DalsiPraceEntity.sql_pracePrac));
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_metaPrac + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue("" + rsDalsiUkoly.getDate(DalsiPraceEntity.sql_finDate));
                        if (value != null) {
                            value = "to_date(" + value + ",'yyyy-mm-dd')";
                        }
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_metaDate + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                    } else if ("zverejneni".equalsIgnoreCase(typPrace)) {
                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(rsDalsiUkoly.getString(DalsiPraceEntity.sql_stavRec));
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_publStav + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(rsDalsiUkoly.getString(DalsiPraceEntity.sql_pracePrac));
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_publPrac + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue("" + rsDalsiUkoly.getDate(DalsiPraceEntity.sql_finDate));
                        if (value != null) {
                            value = "to_date(" + value + ",'yyyy-mm-dd')";
                        }
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_publDate + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                    } else if ("archivace".equalsIgnoreCase(typPrace)) {
                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(rsDalsiUkoly.getString(DalsiPraceEntity.sql_stavRec));
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_archStav + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(rsDalsiUkoly.getString(DalsiPraceEntity.sql_pracePrac));
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_archPrac + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue("" + rsDalsiUkoly.getDate(DalsiPraceEntity.sql_finDate));
                        if (value != null) {
                            value = "to_date(" + value + ",'yyyy-mm-dd')";
                        }
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_archDate + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                    } else if ("ocr".equalsIgnoreCase(typPrace)) {
                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(rsDalsiUkoly.getString(DalsiPraceEntity.sql_stavRec));
                        if ("finished".equalsIgnoreCase(value)) {
                            stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_skenOCR + " = 1 WHERE id = " + rsPredlohy.getString(1));
                        }

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(rsDalsiUkoly.getString(DalsiPraceEntity.sql_pracePrac));
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_skenOCRPrac + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue("" + rsDalsiUkoly.getDate(DalsiPraceEntity.sql_finDate));
                        if (value != null) {
                            value = "to_date(" + value + ",'yyyy-mm-dd')";
                        }
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_skenOCRPrac + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                    } else if ("skenovaniZMikrofilmu".equalsIgnoreCase(typPrace)) {
////////////////////////////////////////////////////////////////////////////////
                        //TODO OPRAVIT
                        //stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_skenSkenZMik + " = 1 WHERE id = " + rsPredlohy.getString(1));

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(rsDalsiUkoly.getString(DalsiPraceEntity.sql_stavRec));
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_skenStav + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(rsDalsiUkoly.getString(DalsiPraceEntity.sql_pracePrac));
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_skenPrac + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue("" + rsDalsiUkoly.getDate(DalsiPraceEntity.sql_finDate));
                        if (value != null) {
                            value = "to_date(" + value + ",'yyyy-mm-dd')";
                        }
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_skenDate + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                    } else if ("vyrobaArchivniNegativ".equalsIgnoreCase(typPrace)) {
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_archNeg + " = 1 WHERE id = " + rsPredlohy.getString(1));

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(rsDalsiUkoly.getString(DalsiPraceEntity.sql_pracePrac));
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_archNegPrac + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue("" + rsDalsiUkoly.getDate(DalsiPraceEntity.sql_finDate));
                        if (value != null) {
                            value = "to_date(" + value + ",'yyyy-mm-dd')";
                        }
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_archNegDate + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                    } else if ("vyrobaMatricniNegativ".equalsIgnoreCase(typPrace)) {
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_matNegativ + " = 1 WHERE id = " + rsPredlohy.getString(1));

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(rsDalsiUkoly.getString(DalsiPraceEntity.sql_pracePrac));
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_matNegativPrac + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                        value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue("" + rsDalsiUkoly.getDate(DalsiPraceEntity.sql_finDate));
                        if (value != null) {
                            value = "to_date(" + value + ",'yyyy-mm-dd')";
                        }
                        stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_matNegativDate + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                    } else if ("".equalsIgnoreCase(typPrace)) {
                        //value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(rsDalsiUkoly.getString(DalsiPraceEntity.sql_));
                        //stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_ + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                    } else if ("".equalsIgnoreCase(typPrace)) {
                        //value = Predloha_KonverzeDatZeStareStrukturySQL.checkValue(rsDalsiUkoly.getString(DalsiPraceEntity.sql_));
                        //stmtInsert.executeUpdate("UPDATE predloha SET " + PredlohaEntity.sql_ + " = " + value + " WHERE id = " + rsPredlohy.getString(1));

                    }
                }
                rsDalsiUkoly.close();
                conn.commit();
            }

            rsPredlohy.close();
            stmtPredlohy.close();
            stmtDalsiUkoly.close();
            stmtInsert.close();
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
