/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.rd;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.RecordEntity;
import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;
import cz.incad.nkp.digital.constants.CnkpMikrofilm;
import cz.incad.nkp.digital.constants.CnkpSecurity;
import cz.incad.nkp.digital.constants.ConstBasic_NKP;
import java.util.Vector;

/** ****************************************************************************
 *
 * @author martin
 */
public class XPredMikroEntity extends RecordEntity implements ConstBasic_NKP, CnkpMikrofilm, CnkpSecurity {
    public static final String CLASSNAME            = "cz.incad.rd.XPredMikro";
    public static final String r_rMikrofilm         = "rMikrofilm";
    public static final String r_rPredloha          = "rPredloha";
    public static final String f_poradiVeSvitku     = "poradiVeSvitku";
    public static final String f_poleCista          = "poleCista";
    public static final String f_poleHruba          = "poleHruba";
    public static final String f_pocetScanu         = "pocetScanu";
    public static final String f_mfeCislo           = "mfeCislo";
    public static final String f_oldTitulId         = "oldTitulId";
    public static final String f_oldXTitNkpMfId     = "oldXTitNkpMfId";
    public static final String f_oznaceniCasti      = "oznaceniCasti";
    public static final String f_carKod             = "carKod";

    public static final String sql_id               = "id";
    public static final String sql_rMikrofilm       = "rMikrofilm";
    public static final String sql_rPredloha        = "rPredloha";
    public static final String sql_poradiVeSvitku   = "poradiVeSvitku";
    public static final String sql_poleCista        = "poleCista";
    public static final String sql_poleHruba        = "poleHruba";
    public static final String sql_pocetScanu       = "pocetScanu";
    public static final String sql_mfeCislo         = "mfeCislo";
    public static final String sql_oldTitulId       = "oldTitulId";
    public static final String sql_oldXTitNkpMfId   = "oldXTitNkpMfId";
    public static final String sql_oznaceniCasti    = "oznaceniCasti";
    public static final String sql_carKod           = "carKod";

    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.DATA;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {SECURITY_ROLE_APP_CORRECTOR, SECURITY_ROLE_appxpredmikro};
    private static final String SECURITY_POLE_HLIDANA[] = {f_mfeCislo, f_poznRec};

    /** ************************************************************************
     * 
     * @param rec
     * @return
     */
    @Override
    public Record onGetRecord(Record rec) {
        //Nastavíme security tak aby pole MFE cislo bylo editovatelné jen pro Administratora registru
        securityOnGetRecord(rec, SECURITY_DESK_TYPE, SECURITY_POLE_HLIDANA);
        return rec;
    }

    /** ************************************************************************
     * 
     * @param rec
     * @return
     * @throws AddException 
     */
    @Override
    public Record onCreateLocal(Record rec) throws AddException {
        super.onCreateLocal(rec);
        securityOnCreateLocal(rec, SECURITY_DESK_TYPE, SECURITY_CAN_CREATE_COMPETITIONS);
        return rec;
    }

    /** ************************************************************************
     * 
     * @param rec
     * @param vRecordFieldsSecurity
     * @param canErase
     */
    @Override
    protected boolean setCustomSecurityOnGetRecord(Record rec, String[] poleHlidana, Vector<Integer> vRecordFieldsSecurity, boolean canErase, String kompetence) {
        // Guru agnedy
        if (kompetence.contains(SECURITY_ROLE_appxpredmikro)) {
            mergeSecurity(vRecordFieldsSecurity, 2,2);
            canErase = Boolean.TRUE;
        } else {
            mergeSecurity(vRecordFieldsSecurity, 1,2);
            canErase = Boolean.TRUE;
        }

        return canErase;
    }

}
