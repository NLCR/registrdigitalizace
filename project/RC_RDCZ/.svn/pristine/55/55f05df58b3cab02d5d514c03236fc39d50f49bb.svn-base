/*******************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.rd;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.EntityImplMother;
import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;

/*******************************************************************************
 *
 * @author martin
 */
public class LokaceEntity extends EntityImplMother {
    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.SYSTEM_CORE;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {};
    private static final String SECURITY_POLE_HLIDANA[] = {};
    public static final String datumZal = "datumZal";
    public static final String digKnihovna = "digKnihovna";
    public static final String datumKontroly = "datumKontroly";
    public static final String url = "url";
    public static final String xml = "xml";

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
