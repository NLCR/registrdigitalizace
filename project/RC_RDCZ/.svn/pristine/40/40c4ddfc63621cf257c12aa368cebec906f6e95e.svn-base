/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.rd;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.businessFunctions.UpdateException;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.RecordEntity;
import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;
import cz.incad.nkp.digital.constants.CnkpSecurity;
import java.util.Vector;

/** ****************************************************************************
 *
 * @author martin
 */
public class DalsiPraceEntity extends RecordEntity implements CnkpSecurity {
    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.DATA;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {SECURITY_ROLE_appdalsiprace};
    private static final String SECURITY_POLE_HLIDANA[] = {
        DalsiPraceEntity.f_typPrace
        };

    public static final String CLASSNAME        = "cz.incad.rd.DalsiPrace";
    public static final String f_typPrace       = "typPrace";
    public static final String f_praceStav      = "praceStav";
    public static final String f_pracePrac      = "pracePrac";

    public static final String sql_id           = "id";
    public static final String sql_typPrace     = "typPrace";
    public static final String sql_praceStav    = "praceStav";
    public static final String sql_pracePrac    = "pracePrac";
    public static final String sql_rPredloha    = "rPredlohaDP";


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
     * @param poleHlidana
     * @param vRecordFieldsSecurity
     * @param canErase
     */
    @Override
    protected boolean setCustomSecurityOnGetRecord(Record rec, String[] poleHlidana, Vector<Integer> vRecordFieldsSecurity, boolean canErase, String kompetence) {
        // Guru agnedy
        if (kompetence.contains(SECURITY_ROLE_appdalsiprace)) {
            mergeSecurity(vRecordFieldsSecurity, 2);
            canErase = Boolean.TRUE;
        }

        return canErase;
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
        rec = this.onCreateUpdate(rec);
        rec = super.onCreate(rec);
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
        rec = this.onCreateUpdate(rec);
        rec = super.onUpdate(rec);
        return rec;
    }

    /** ************************************************************************
     * 
     * @param rec
     * @return
     * @throws ValidationException 
     */
    private Record onCreateUpdate(Record rec) throws ValidationException {
        if("dokonceno".equals(rec.getSimpleField(f_praceStav).getValue())) {
            rec.getSimpleField(f_stavRec).setValue("finished");
        }
        return rec;
    }

}
