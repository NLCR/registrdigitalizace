/** ****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.rd;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.businessFunctions.AnnotationKeys;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.DListsEntity;
import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;
import cz.incad.core.tools.DirectConnection;
import cz.incad.core.tools.ReliefLogger;
import cz.incad.core.tools.Utilities;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** ****************************************************************************
 *
 * @author martin
 */
public class DigKnihovnaEntity extends DListsEntity {
    public static final String F_stav_STR = "stav";
    public static final String F_zkratka_STR = "zkratka";
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
        rec.getSimpleField(f_value).setAnnotation(AnnotationKeys.READ_ONLY_SECURITY_PROPERTY, AnnotationKeys.TRUE_VALUE);
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
        super.onCreateLocal(rec);
        rec.getSimpleField(F_stav_STR).setValue("novy");
        securityOnCreateLocal(rec, SECURITY_DESK_TYPE, SECURITY_CAN_CREATE_COMPETITIONS);
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
            rs = stmt.executeQuery("SELECT value FROM dlists where classname = 'cz.incad.rd.DigKnihovna' and value = '" + rec.getSimpleField(f_value).getValue() + "'");
            if (rs.next()) {
                vex = Utilities.getValidationException(getTriggerContext());
                vex.addField("VALUE", "Tato hodnota VALUE již v databázi existuje: " + rec.getSimpleField(f_value).getValue(), false);
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

        if (rec.getSimpleField(f_cz).getValue() == null) {
            rec.getSimpleField(f_cz).setValue(rec.getSimpleField(f_value).getValue());
        }
        if (rec.getSimpleField(f_en).getValue() == null) {
            rec.getSimpleField(f_en).setValue(rec.getSimpleField(f_value).getValue());
        }
        if (rec.getSimpleField(f_de).getValue() == null) {
            rec.getSimpleField(f_de).setValue(rec.getSimpleField(f_value).getValue());
        }
        if (rec.getSimpleField(f_fr).getValue() == null) {
            rec.getSimpleField(f_fr).setValue(rec.getSimpleField(f_value).getValue());
        }
        if (rec.getSimpleField(F_zkratka_STR).getValue() == null) {
            rec.getSimpleField(F_zkratka_STR).setValue(rec.getSimpleField(f_value).getValue());
        }
        return rec;
    }

}
