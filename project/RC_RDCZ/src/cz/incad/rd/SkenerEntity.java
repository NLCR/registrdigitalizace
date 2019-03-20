/*******************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.rd;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.DListsEntity;
import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;
import cz.incad.nkp.digital.constants.CnkpSecurity;
import java.util.Vector;

/*******************************************************************************
 *
 * @author martin
 */
public class SkenerEntity extends DListsEntity implements CnkpSecurity {
    public static final String CLASSNAME        = "cz.incad.rd.Skener";

    public static final String f_typSkeneru     = "typSkeneru";
    public static final String f_modelSkeneru   = "modelSkeneru";
    public static final String f_vyrobceSkeneru = "vyrobceSkeneru";
    public static final String f_pouzitySW      = "pouzitySW";
    public static final String f_verzeSW        = "verzeSW";
    public static final String f_vyrobceSW      = "vyrobceSW";
    public static final String f_vlastnik       = "vlastnik";

    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.DATA;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {SECURITY_ROLE_knavkorektor, SECURITY_ROLE_appskener};
    private static final String SECURITY_POLE_HLIDANA[] = {
        SkenerEntity.f_cz
        };

    /***************************************************************************
     *
     * @param rec
     * @return
     */
    @Override
    public Record onGetRecord(Record rec) {
        securityOnGetRecord(rec, SECURITY_DESK_TYPE, SECURITY_POLE_HLIDANA);
        return rec;
    }

    /***************************************************************************
     *
     * @param rec
     * @param poleHlidana
     * @param vRecordFieldsSecurity
     * @param canErase
     */
    @Override
    protected boolean setCustomSecurityOnGetRecord(Record rec, String[] poleHlidana, Vector<Integer> vRecordFieldsSecurity, boolean canErase, String kompetence) {
        // Guru agnedy
        if (kompetence.contains(SECURITY_ROLE_appskener)) {
            mergeSecurity(vRecordFieldsSecurity, 2);
            canErase = Boolean.TRUE;
        }

        return canErase;
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
        securityOnCreateLocal(rec, SECURITY_DESK_TYPE, SECURITY_CAN_CREATE_COMPETITIONS);
        return rec;
    }

}
