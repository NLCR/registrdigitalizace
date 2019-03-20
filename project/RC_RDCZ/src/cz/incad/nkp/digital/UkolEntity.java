/*
 * UkolEntity.java
 *
 * Created on 27. duben 2006, 10:53
 * The class and its source code is a property of Ing. Pavel Vedral (C) 2006.
 */

package cz.incad.nkp.digital;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.RecordEntity;
import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;
import cz.incad.nkp.digital.constants.CnkpSecurity;
import cz.incad.nkp.digital.constants.CnkpUkol;
import java.util.Vector;


/**
 *
 * @author <a href="mailto:pve@casd.cz">Ing. Pavel Vedral</a>
 * @version $Revision: 1.4 $
 */
public class UkolEntity extends RecordEntity implements CnkpUkol, CnkpSecurity {
    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.DATA;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {SECURITY_ROLE_knavkorektor, SECURITY_ROLE_knavpracovnik, SECURITY_ROLE_appukol};
    private static final String SECURITY_POLE_HLIDANA[] = {
        UKOL_rZakazka,
        UKOL_typPrace,
        UKOL_zpracovatel,
        RECORD_stavRec,
        RECORD_poznRec,
        RECORD_stavPozn};
    
    
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
        if (kompetence.contains(SECURITY_ROLE_appzakazka)) {
            mergeSecurity(vRecordFieldsSecurity, 2,2,2,2,2,2);
            canErase = Boolean.TRUE;
        }

        //Kompetence - knavkorektor
        if (kompetence.contains(SECURITY_ROLE_knavkorektor)) {
            mergeSecurity(vRecordFieldsSecurity, 2,2,2,2,2,2);
            canErase = Boolean.TRUE;
        }

        //Kompetence - knavpracovnik
        if (kompetence.contains(SECURITY_ROLE_knavpracovnik)) {
            mergeSecurity(vRecordFieldsSecurity, 2,2,2,2,2,2);
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
