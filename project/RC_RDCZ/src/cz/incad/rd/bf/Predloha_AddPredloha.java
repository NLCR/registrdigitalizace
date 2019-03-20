/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.rd.bf;

import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import com.amaio.plaant.sync.Domain;
import com.amaio.plaant.sync.Record;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.core.tools.DirectConnection;
import cz.incad.core.tools.ReliefLogger;
import cz.incad.core.tools.ReliefWizardMessage;
import cz.incad.rd.PredlohaEntity;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

/** ****************************************************************************
 *
 * @author martin
 */
public class Predloha_AddPredloha extends BussinessFunctionMother {
    private static final String LOCAL_SECURITY = "data119";
    //Pole z wizarda
    //Panel01
    public static final String f_p1_ccnb            = "ccnb";
    public static final String f_p1_issn            = "issn";
    public static final String f_p1_isbn            = "isbn";
    //Panel02
    public static final String f_p2_nazev           = "nazev";
    public static final String f_p2_autor           = "autor";
    public static final String f_p2_rok             = "rok";
    public static final String f_p2_castRocnik      = "castRocnik";
    public static final String f_p2_cisloPeriodika  = "cisloPeriodika";
    //Panel03
    public static final String f_p3_carKod          = "carKod";
    public static final String f_p3_signatura       = "signatura";
    public static final String f_p3_pocetStranek    = "pocetStranek";
    
    //Hodnoty zadane uzivatelem
    private String value_ccnb               = null;
    private String value_issn               = null;
    private String value_isbn               = null;
    private String value_nazev              = null;
    private String value_autor              = null;
    private String value_rok                = null;
    private String value_castRocnik         = null;
    private String value_cisloPeriodika     = null;
    private String value_carKod             = null;
    private String value_signatura          = null;
    private String value_pocetStranek       = null;
    
    private boolean isFoundPanel01 = false;
    private boolean isFoundPanel02 = false;
    String SQL_PART_SELECT_WHERE = "select id,idcislo,nazev,autor,rokvyd,cast,cisloPer,ccnb,isbn,issn from predloha where ";

    /** ************************************************************************
     * Není implementováno
     * @return
     * @throws ValidationException
     * @throws WizardException 
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
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
        ReliefWizardMessage rwm = new ReliefWizardMessage();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        // PANEL 01 ------------------------------------------------------------
        if ("panel01".equals(panelName)) {
            List<String> lPanel01_SQL = new LinkedList();
            isFoundPanel01 = false;

            //Načteme hodnoty
            this.value_ccnb = checkValue(getWizardContextClient().getWizardRecord().getSimpleField(f_p1_ccnb).getValue());
            this.value_issn = checkValue(getWizardContextClient().getWizardRecord().getSimpleField(f_p1_issn).getValue());
            this.value_isbn = checkValue(getWizardContextClient().getWizardRecord().getSimpleField(f_p1_isbn).getValue());

            if ((this.value_ccnb != null) && ((this.value_isbn != null) || (this.value_issn != null))) {
                //Je vyplněna kombinace CCNB + (ISSN nebo ISBN nebo obojí)
                if (this.value_isbn != null) {
                    lPanel01_SQL.add(SQL_PART_SELECT_WHERE + "ccnb = '" + this.value_ccnb + "' and isbn = '" + this.value_isbn + "'");
                } else {
                    lPanel01_SQL.add(SQL_PART_SELECT_WHERE + "ccnb = '" + this.value_ccnb + "' and issn = '" + this.value_issn + "'");
                }
            }

            if (this.value_ccnb != null) {
                //Je vyplněno čČNB
                lPanel01_SQL.add(SQL_PART_SELECT_WHERE + "ccnb = '" + this.value_ccnb + "'");
            }
            if (this.value_isbn != null) {
                //Je vyplněno ISBN
                lPanel01_SQL.add(SQL_PART_SELECT_WHERE + "isbn = '" + this.value_isbn + "'");
            }
            if (this.value_issn != null) {
                //Je vyplněno ISSN
                lPanel01_SQL.add(SQL_PART_SELECT_WHERE + "issn = '" + this.value_issn + "'");
            }

            if (!lPanel01_SQL.isEmpty()) {
                isFoundPanel01 = false;

                try {
                    conn = DirectConnection.getConnection();
                    stmt = conn.createStatement();

                    for (int i = 0; i < lPanel01_SQL.size(); i++) {
                        if (isFoundPanel01) break;
                        ReliefLogger.info("SQL: " + lPanel01_SQL.get(i));
                        rs = stmt.executeQuery(lPanel01_SQL.get(i));
                        while (rs.next()) {
                            isFoundPanel01 = true;
                            rwm.addInfoMini(nullRemover(rs.getString("nazev")),
                                    "Autor: " + nullRemover(rs.getString("autor")),
                                    "Rok(y) vydání: " + nullRemover(rs.getString("rokvyd")),
                                    "Část / Ročník: " + nullRemover(rs.getString("cast")),
                                    "Číslo periodika: " + nullRemover(rs.getString("cisloPer")),
                                    "čČNB: " + nullRemover(rs.getString("ccnb")),
                                    "ISBN: " + nullRemover(rs.getString("isbn")),
                                    "ISSN: " + nullRemover(rs.getString("issn")),
                                    "Číslo záznamu: " + nullRemover(rs.getString("idcislo")));
                        }
                        if (rs != null) rs.close();
                    }
                    if (stmt != null) stmt.close();

                } catch(Exception ex) {
                    ReliefLogger.severeTrace("Neocekavana chyba.", null, ex);
                } finally {
                    try {
                        conn.close();
                    } catch (SQLException ex01) {
                    }
                }
            }
            
            //Vypsání zadaných hodnot
            rwm.addInfoMini("--- Zadané hodnoty ---","čČNB: " + nullRemover(this.value_ccnb) + " | ISSN: " + nullRemover(this.value_issn) + " | ISBN: " + nullRemover(this.value_isbn));

        // PANEL 02 ------------------------------------------------------------
        } else if ("panel02".equals(panelName)) {
            List<String> lPanel02_SQL = new LinkedList();
            //Načteme hodnoty
            //1
            this.value_nazev = checkValue(getWizardContextClient().getWizardRecord().getSimpleField(f_p2_nazev).getValue());
            //2
            this.value_autor = checkValue(getWizardContextClient().getWizardRecord().getSimpleField(f_p2_autor).getValue());
            //3
            this.value_rok = checkValue(getWizardContextClient().getWizardRecord().getSimpleField(f_p2_rok).getValue());
            //4
            this.value_castRocnik = checkValue(getWizardContextClient().getWizardRecord().getSimpleField(f_p2_castRocnik).getValue());
            //5
            this.value_cisloPeriodika = checkValue(getWizardContextClient().getWizardRecord().getSimpleField(f_p2_cisloPeriodika).getValue());
            isFoundPanel02 = false;

            //12345
            if (this.value_nazev != null && this.value_autor != null && this.value_rok != null && this.value_castRocnik != null && this.value_cisloPeriodika != null) {
                //Je vyplněno vše
                lPanel02_SQL.add(SQL_PART_SELECT_WHERE + "nazev = '" + this.value_nazev + "'" + " and autor = '" + this.value_autor + "'" + " and rokvyd = '" + this.value_rok + "'" + " and cast = '" + this.value_castRocnik + "'" + " and cisloPer = '" + this.value_cisloPeriodika + "'");
            }
            if (this.value_nazev != null && this.value_autor != null && this.value_rok != null && this.value_castRocnik != null && this.value_cisloPeriodika != null) {
                //Je vyplněno vše (LIKE)
                lPanel02_SQL.add(SQL_PART_SELECT_WHERE + "nazev like '%" + this.value_nazev + "%'" + " and autor like '%" + this.value_autor + "%'" + " and rokvyd = '" + this.value_rok + "'" + " and cast = '" + this.value_castRocnik + "'" + " and cisloPer = '" + this.value_cisloPeriodika + "'");
            }

            //1235
            if (this.value_nazev != null && this.value_autor != null && this.value_rok != null && this.value_cisloPeriodika != null) {
                //Je vyplněno 
                lPanel02_SQL.add(SQL_PART_SELECT_WHERE + "nazev = '" + this.value_nazev + "'" + " and autor = '" + this.value_autor + "'" + " and rokvyd = '" + this.value_rok + "'" + " and cisloPer = '" + this.value_cisloPeriodika + "'");
            }
            if (this.value_nazev != null && this.value_autor != null && this.value_rok != null && this.value_cisloPeriodika != null) {
                //Je vyplněno  (LIKE)
                lPanel02_SQL.add(SQL_PART_SELECT_WHERE + "nazev like '%" + this.value_nazev + "%'" + " and autor like '%" + this.value_autor + "%'" + " and rokvyd = '" + this.value_rok + "'" + " and cisloPer = '" + this.value_cisloPeriodika + "'");
            }

            //1234
            if (this.value_nazev != null && this.value_autor != null && this.value_rok != null && this.value_castRocnik != null) {
                //Je vyplněno
                lPanel02_SQL.add(SQL_PART_SELECT_WHERE + "nazev = '" + this.value_nazev + "'" + " and autor = '" + this.value_autor + "'" + " and rokvyd = '" + this.value_rok + "'" + " and cast = '" + this.value_castRocnik + "'");
            }
            if (this.value_nazev != null && this.value_autor != null && this.value_rok != null && this.value_castRocnik != null) {
                //Je vyplněno (LIKE)
                lPanel02_SQL.add(SQL_PART_SELECT_WHERE + "nazev like '%" + this.value_nazev + "%'" + " and autor like '%" + this.value_autor + "%'" + " and rokvyd = '" + this.value_rok + "'" + " and cast = '" + this.value_castRocnik + "'");
            }

            //135
            if (this.value_nazev != null && this.value_rok != null && this.value_cisloPeriodika != null) {
                //Je vyplněno 
                lPanel02_SQL.add(SQL_PART_SELECT_WHERE + "nazev = '" + this.value_nazev + "'" + " and rokvyd = '" + this.value_rok + "'" + " and cisloPer = '" + this.value_cisloPeriodika + "'");
            }
            if (this.value_nazev != null && this.value_rok != null && this.value_cisloPeriodika != null) {
                //Je vyplněno  (LIKE)
                lPanel02_SQL.add(SQL_PART_SELECT_WHERE + "nazev like '%" + this.value_nazev + "%'" + " and rokvyd = '" + this.value_rok + "'" + " and cisloPer = '" + this.value_cisloPeriodika + "'");
            }

            //134
            if (this.value_nazev != null && this.value_rok != null && this.value_castRocnik != null) {
                //Je vyplněno
                lPanel02_SQL.add(SQL_PART_SELECT_WHERE + "nazev = '" + this.value_nazev + "'" + " and rokvyd = '" + this.value_rok + "'" + " and cast = '" + this.value_castRocnik + "'");
            }
            if (this.value_nazev != null && this.value_rok != null && this.value_castRocnik != null) {
                //Je vyplněno (LIKE)
                lPanel02_SQL.add(SQL_PART_SELECT_WHERE + "nazev like '%" + this.value_nazev + "%'" + " and rokvyd = '" + this.value_rok + "'" + " and cast = '" + this.value_castRocnik + "'");
            }

            //123
            if (this.value_nazev != null && this.value_autor != null && this.value_rok != null) {
                //Je vyplněno
                lPanel02_SQL.add(SQL_PART_SELECT_WHERE + "nazev = '" + this.value_nazev + "'" + " and autor = '" + this.value_autor + "'" + " and rokvyd = '" + this.value_rok + "'");
            }
            if (this.value_nazev != null && this.value_autor != null && this.value_rok != null) {
                //Je vyplněno (LIKE)
                lPanel02_SQL.add(SQL_PART_SELECT_WHERE + "nazev like '%" + this.value_nazev + "%'" + " and autor like '%" + this.value_autor + "%'" + " and rokvyd = '" + this.value_rok + "'");
            }
            
            //13
            if (this.value_nazev != null && this.value_rok != null) {
                //Je vyplněno
                lPanel02_SQL.add(SQL_PART_SELECT_WHERE + "nazev = '" + this.value_nazev + "'" + " and rokvyd = '" + this.value_rok + "'");
            }
            if (this.value_nazev != null && this.value_rok != null) {
                //Je vyplněno (LIKE)
                lPanel02_SQL.add(SQL_PART_SELECT_WHERE + "nazev like '%" + this.value_nazev + "%'" + " and rokvyd = '" + this.value_rok + "'");
            }
            
            //12
            if (this.value_nazev != null && this.value_autor != null) {
                //Je vyplněno
                lPanel02_SQL.add(SQL_PART_SELECT_WHERE + "nazev = '" + this.value_nazev + "'" + " and autor = '" + this.value_autor + "'");
            }
            if (this.value_nazev != null && this.value_autor != null) {
                //Je vyplněno (LIKE)
                lPanel02_SQL.add(SQL_PART_SELECT_WHERE + "nazev like '%" + this.value_nazev + "%'" + " and autor like '%" + this.value_autor + "%'");
            }
            
            //1
            if (this.value_nazev != null) {
                //Je vyplněno
                lPanel02_SQL.add(SQL_PART_SELECT_WHERE + "nazev = '" + this.value_nazev + "'");
            }
            if (this.value_nazev != null) {
                //Je vyplněno (LIKE)
                lPanel02_SQL.add(SQL_PART_SELECT_WHERE + "nazev like '%" + this.value_nazev + "%'");
            }

            if (!lPanel02_SQL.isEmpty()) {
                isFoundPanel02 = false;

                try {
                    conn = DirectConnection.getConnection();
                    stmt = conn.createStatement();
                    for (int i = 0; i < lPanel02_SQL.size(); i++) {
                        if (isFoundPanel02) break;
                        //stmt = conn.createStatement();
                        ReliefLogger.info("SQL: " + lPanel02_SQL.get(i));
                        rs = stmt.executeQuery(lPanel02_SQL.get(i));
                        while (rs.next()) {
                            isFoundPanel02 = true;
                            rwm.addInfoMini(nullRemover(rs.getString("nazev")),
                                    "Autor: " + nullRemover(rs.getString("autor")),
                                    "Rok(y) vydání: " + nullRemover(rs.getString("rokvyd")),
                                    "Část / Ročník: " + nullRemover(rs.getString("cast")),
                                    "Číslo periodika: " + nullRemover(rs.getString("cisloPer")),
                                    "čČNB: " + nullRemover(rs.getString("ccnb")),
                                    "ISBN: " + nullRemover(rs.getString("isbn")),
                                    "ISSN: " + nullRemover(rs.getString("issn")),
                                    "Číslo záznamu: " + nullRemover(rs.getString("idcislo")));
                        }
                        if (rs != null) rs.close();
                    }
                    if (stmt != null) stmt.close();

                } catch(Exception ex) {
                    ReliefLogger.severeTrace("Neocekavana chyba.", null, ex);
                } finally {
                    try {
                        conn.close();
                    } catch (SQLException ex01) {
                    }
                }
            }
            //Vypsání zadaných hodnot
            rwm.addInfoMini("--- Zadané hodnoty ---","Název: " + nullRemover(this.value_nazev), "Autor: " + nullRemover(this.value_autor) + " | Rok(y) vydání: " + nullRemover(this.value_rok) + " | Část/Ročník: " + nullRemover(this.value_castRocnik) + "  | Číslo periodika: " + nullRemover(this.value_cisloPeriodika));

        // PANEL 03 ------------------------------------------------------------
        } else if ("panel03".equals(panelName)) {
            //Načteme hodnoty
            this.value_carKod = checkValue(getWizardContextClient().getWizardRecord().getSimpleField(f_p3_carKod).getValue());
            this.value_signatura = checkValue(getWizardContextClient().getWizardRecord().getSimpleField(f_p3_signatura).getValue());
            this.value_pocetStranek = checkValue(getWizardContextClient().getWizardRecord().getSimpleField(f_p3_pocetStranek).getValue());
            rwm.addInfoMini("Název: " + nullRemover(this.value_nazev),
                            "Autor: " + nullRemover(this.value_autor),
                            "Rok(y) vydání: " + nullRemover(this.value_rok),
                            "Část / Ročník: " + nullRemover(this.value_castRocnik),
                            "Číslo periodika: " + nullRemover(this.value_cisloPeriodika),
                            "čČNB: " + nullRemover(this.value_ccnb),
                            "ISBN: " + nullRemover(this.value_isbn),
                            "ISSN: " + nullRemover(this.value_issn),
                            "Čárový kód: " + nullRemover(this.value_carKod),
                            "Signatura: " + nullRemover(this.value_signatura),
                            "Počet stran: " + nullRemover(this.value_pocetStranek));
        // PANEL 04 ------------------------------------------------------------
        } else if ("panel04".equals(panelName)) {
            rwm.addLine("panel04");
        }

        return rwm;
    }

    /** ************************************************************************
     * 
     * @return
     * @throws ValidationException
     * @throws WizardException
     * @throws ApplicationErrorException 
     */
    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        ReliefWizardMessage rwm = new ReliefWizardMessage();
        Domain dPredloha;
        Record recPredloha;

        try {
            dPredloha = getDomenu(PredlohaEntity.CLASSNAME);
            getWizardContextClient().addRootDomain(dPredloha);
            recPredloha = getWizardContextClient().create(dPredloha);
            recPredloha.getSimpleField(PredlohaEntity.f_cCNB).setValue(value_ccnb);
            recPredloha.getSimpleField(PredlohaEntity.f_issn).setValue(value_issn);
            recPredloha.getSimpleField(PredlohaEntity.f_ISBN).setValue(value_isbn);
            recPredloha.getSimpleField(PredlohaEntity.f_nazev).setValue(value_nazev);
            recPredloha.getSimpleField(PredlohaEntity.f_autor).setValue(value_autor);
            recPredloha.getSimpleField(PredlohaEntity.f_rokVyd).setValue(value_rok);
            recPredloha.getSimpleField(PredlohaEntity.f_cast).setValue(value_castRocnik);
            recPredloha.getSimpleField(PredlohaEntity.f_cisloPer).setValue(value_cisloPeriodika);
            recPredloha.getSimpleField(PredlohaEntity.f_carKod).setValue(value_carKod);
            recPredloha.getSimpleField(PredlohaEntity.f_signatura).setValue(value_signatura);
            recPredloha.getSimpleField(PredlohaEntity.f_pocetStran).setValue(value_pocetStranek);

            getWizardContextClient().commit();
        } catch(Exception ex) {
            ReliefLogger.severeTrace(null, null, ex);
            getWizardContextClient().rollback();
        }
 
        return rwm;
    }

    /** ************************************************************************
     * 
     * @param value
     * @return 
     */
    private static String checkValue(Object value) {
        String exit = null;

        if (value != null) {
            exit = (String)value;
            if (exit.isEmpty()) exit = null;
        } 
        return exit;
    }

    private static String nullRemover(String value) {
        if (value == null) return "---";
        return value;
    }
}
