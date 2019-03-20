package cz.incad.nkp.digital;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.RecordEntity;
import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;
import cz.incad.nkp.digital.constants.CnkpSecurity;
import cz.incad.nkp.digital.constants.CnkpUkol;

/**
 *
 * @author martin
 */
public class URLEntity extends RecordEntity implements CnkpUkol, CnkpSecurity  {
    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.DATA;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {SECURITY_ROLE_knavkorektor, SECURITY_ROLE_knavpracovnik, SECURITY_ROLE_appukol};
    private static final String SECURITY_POLE_HLIDANA[] = {};

    public static final String digKnihovna = "digKnihovna";
    public static final String url = "url";
    public static final String idCislo = "idCislo";
    public static final String zalDate = "zalDate";
    public static final String zalUser = "zalUser";
    public static final String ediDate = "ediDate";
    public static final String ediUser = "ediUser";
    public static final String finDate = "finDate";
    public static final String finUser = "finUser";
    public static final String stavRec = "stavRec";
    public static final String poznRec = "poznRec";
    public static final String stavPozn = "stavPozn";
    public static final String securityOwner = "securityOwner";


    /***************************************************************************************************************************************
     *
     * @param rec
     * @return
     */
    @Override
    public Record onGetRecord(Record rec) {
        securityOnGetRecord(rec, SECURITY_DESK_TYPE, SECURITY_POLE_HLIDANA);
        return rec;
    }


    /***************************************************************************************************************************************
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
