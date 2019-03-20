/*******************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.rd;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.businessFunctions.AnnotationKeys;
import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.RecordEntity;
//import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;
import cz.incad.core.tools.ReliefUser;
import cz.incad.nkp.digital.constants.CnkpSecurity;

/*******************************************************************************
 *
 * @author martin
 */
public class PeriodikumEntity extends RecordEntity implements CnkpSecurity {
//    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.DATA;
//    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {SECURITY_ROLE_appperiodikum};
//    private static final String SECURITY_POLE_HLIDANA[] = {};

    public static final String CLASSNAME            = "cz.incad.rd.Periodikum";
    public static final String f_nazev              = "nazev";
    public static final String f_issn               = "issn";
    public static final String t_tPredloha          = "tPredloha";
    public static final String f_pole001            = "pole001";
    public static final String f_sysno              = "sysno";
    public static final String f_oldTitulId         = "oldTitulId";
    public static final String f_urnnbn             = "urnnbn";
    public static final String f_mfD                = "mfD";
    public static final String f_druhDokumentu      = "druhDokumentu";
    public static final String f_cCNB               = "cCNB";
    public static final String f_isbn               = "isbn";


    public static final String sql_id               = "id";
    public static final String sql_nazev            = "nazev";
    public static final String sql_issn             = "issn";
    public static final String sql_pole001          = "pole001";
    public static final String sql_sysno            = "sysno";
    public static final String sql_oldTitulId       = "oldTitulId";
    public static final String sql_urnnbn           = "urnnbn";
    public static final String sql_mfD              = "mfD";
    public static final String sql_druhDokumentu    = "druhDokumentu";
    public static final String sql_cCNB             = "cCNB";
    public static final String sql_isbn             = "isbn";


    /***************************************************************************
     *
     * @param rec
     * @return
     */
    @Override
    public Record onGetRecord(Record rec) {
        ReliefUser ru = new ReliefUser(getTriggerContext());
        if (!(!ru.hasUserRole(SECURITY_ROLE_appperiodikum) || ru.isCorrector() || ru.isApplicationAdmin() || ru.isSystemAdmin())) {
            rec.setAnnotation(AnnotationKeys.READ_ONLY_SECURITY_PROPERTY, AnnotationKeys.TRUE_VALUE);
            rec.setAnnotation(AnnotationKeys.REMOVE_FORBIDDEN_SECURITY_PROPERTY, AnnotationKeys.TRUE_VALUE);
        }
        //securityOnGetRecord(rec, SECURITY_DESK_TYPE, SECURITY_POLE_HLIDANA);
        return rec;
    }

    /***************************************************************************
     *
     * @param rec
     * @return
     * @throws com.amaio.plaant.businessFunctions.AddException
     */
    @Override
    public Record onCreateLocal(Record rec) throws AddException {
        super.onCreateLocal(rec);

        rec.getSimpleField(RECORD_securityOwner).setValue("NULL");
        ReliefUser ru = new ReliefUser(getTriggerContext());
        if (!(!ru.hasUserRole(SECURITY_ROLE_appperiodikum) || ru.isCorrector() || ru.isApplicationAdmin() || ru.isSystemAdmin())) {
            rec.setAnnotation(AnnotationKeys.READ_ONLY_SECURITY_PROPERTY, AnnotationKeys.TRUE_VALUE);
            rec.setAnnotation(AnnotationKeys.REMOVE_FORBIDDEN_SECURITY_PROPERTY, AnnotationKeys.TRUE_VALUE);
        }
        //securityOnCreateLocal(rec, SECURITY_DESK_TYPE, SECURITY_CAN_CREATE_COMPETITIONS);
        return rec;
    }

}
