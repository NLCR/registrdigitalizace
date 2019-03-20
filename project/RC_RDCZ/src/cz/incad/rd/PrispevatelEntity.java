/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.rd;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.businessFunctions.UpdateException;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.sync.Record;
import cz.incad.commontools.utils.mail.EmailValidator;
import cz.incad.core.basic.EntityImplMother;
import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;
import cz.incad.core.tools.DirectConnection;
import cz.incad.core.tools.ReliefLogger;
import cz.incad.core.tools.ReliefUser;
import cz.incad.core.tools.Utilities;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/** ****************************************************************************
 *
 * @author martin
 */
public class PrispevatelEntity extends EntityImplMother {
    //FIX - kvuli importnimu programu
    public static final String DLISTS_value         = "value";
    public static final String DLISTS_cz            = "cz";
    public static final String DLISTS_en            = "en";
    public static final String DLISTS_de            = "de";
    public static final String DLISTS_fr            = "fr";

    public static final String F_poznamka_STR       = "poznamka";
    public static final String F_nazev_STR          = "nazev";
    public static final String F_sigla_STR          = "sigla";
    public static final String F_nazevEn_STR        = "nazevEn";
    public static final String F_zkratka_STR        = "zkratka";
    public static final String F_osoba_STR          = "osoba";
    public static final String F_email_STR          = "email";
    public static final String F_homepage_STR       = "homepage";
    public static final String F_tDigKnihovna_TAB   = "tDigKnihovna";
    public static final String F_tKatalog_TAB       = "tKatalog";
    public static final String F_adresa_STR         = "adresa";
    public static final String F_linkRejstrik_STR   = "linkRejstrik";
    public static final String F_tImporty_TAB       = "tImporty";
    public static final String F_stav_STR           = "stav";
    public static final String F_telefon_STR        = "telefon";
    
    public static final String F_zalDate_DAT        = "zalDate";
    public static final String F_zalUser_STR        = "zalUser";
    public static final String F_ediDate_DAT        = "ediDate";
    public static final String F_ediUser_STR        = "ediUser";
    public static final String F_zarazeniUser_STR   = "zarazeniUser";

    //Security
    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.SYSTEM_APPLICATION;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {};
    private static final String SECURITY_POLE_HLIDANA[] = {};

    /** ************************************************************************
     * 
     * @param rec
     * @return 
     */
    @Override
    public Record onGetRecord(Record rec) {
        securityOnGetRecord(rec, SECURITY_DESK_TYPE, SECURITY_POLE_HLIDANA);
        return rec;
    }

    /** ************************************************************************
     *
     * @param rec
     * @return
     * @throws com.amaio.plaant.businessFunctions.AddException
     */
    @Override
    public Record onCreateLocal(Record rec) throws AddException {
        securityOnCreateLocal(rec, SECURITY_DESK_TYPE, SECURITY_CAN_CREATE_COMPETITIONS);
        ReliefUser ui = new ReliefUser(getTriggerContext());

        //nastavení zakladajiciho uživatele.
        rec.getSimpleField(F_zalUser_STR).setValue(ui.getLogin());
        //nastaveni dnesniho datumu
        rec.getSimpleField(F_zalDate_DAT).setValue(new Date());
        rec.getSimpleField(F_stav_STR).setValue("novy");
        return rec;
    }

    /** ************************************************************************
     * 
     * @param rec
     * @return
     * @throws AddException
     * @throws ValidationException 
     */
    @Override
    public Record onCreate(Record rec) throws AddException, ValidationException {
        rec = super.onCreate(rec);
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        ValidationException vex;
        
        // Kontrola na duplicitu value
        try {
            con = DirectConnection.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT value FROM prispevatel where value = '" + rec.getSimpleField(DLISTS_value).getValue() + "'");
            if (rs.next()) {
                vex = Utilities.getValidationException(getTriggerContext());
                vex.addField("VALUE", "Tato hodnota VALUE již v databázi existuje: " + rec.getSimpleField(DLISTS_value).getValue(), false);
                throw vex;
            }
        } catch (SQLException ex) {
            ReliefLogger.throwing("", "Chyba při zpracování SQL", ex);
            throw new AddException("Chyba při kontorle jedinečnosti hodnoty VALUE.");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException ex) {
                ReliefLogger.throwing("", "Chyba při zavírání připojení do databáze.", ex);
            }
        }

        if (rec.getSimpleField(DLISTS_cz).getValue() == null) {
            rec.getSimpleField(DLISTS_cz).setValue(rec.getSimpleField(DLISTS_value).getValue());
        }
        if (rec.getSimpleField(DLISTS_en).getValue() == null) {
            rec.getSimpleField(DLISTS_en).setValue(rec.getSimpleField(DLISTS_value).getValue());
        }
        if (rec.getSimpleField(DLISTS_de).getValue() == null) {
            rec.getSimpleField(DLISTS_de).setValue(rec.getSimpleField(DLISTS_value).getValue());
        }
        if (rec.getSimpleField(DLISTS_fr).getValue() == null) {
            rec.getSimpleField(DLISTS_fr).setValue(rec.getSimpleField(DLISTS_value).getValue());
        }

        onCreateUpdate(rec);
        return rec;
    }

    /** ************************************************************************
     * 
     * @param rec
     * @return
     * @throws ValidationException
     * @throws UpdateException 
     */
    @Override
    public Record onUpdate(Record rec) throws ValidationException, UpdateException {
        ReliefUser ui;

        rec = super.onUpdate(rec);
        ui = new ReliefUser(getTriggerContext());
        rec.getSimpleField(F_ediUser_STR).setValue(ui.getLogin());
        rec.getSimpleField(F_ediDate_DAT).setValue(new Date());

        onCreateUpdate(rec);
        return rec;
    }

    /** ************************************************************************
     * 
     * @param rec
     * @throws ValidationException 
     */
    private void onCreateUpdate(Record rec) throws ValidationException {
        //Kontrola emailové adresy
        EmailValidator ev;
        String[] aEmail;
        String emailValue;
        ValidationException vex = Utilities.getValidationException(getTriggerContext());

        emailValue = (String)rec.getSimpleField(F_email_STR).getValue();
        if (emailValue != null) {
            ev = new EmailValidator();
            aEmail = emailValue.split(";");
            for (int i = 0; i < aEmail.length; i++) {
                if (!ev.validate(aEmail[i])) {
                    vex.addField(F_email_STR, "Neplatná emailová adresa: " + aEmail[i], false);
                }
            }
        }

        if (vex.isGravid()) {
            throw vex;
        }
    }

 }
