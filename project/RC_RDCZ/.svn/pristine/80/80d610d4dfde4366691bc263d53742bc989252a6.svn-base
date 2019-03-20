/*******************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.rd.list;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.DListsEntity;
import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;

/*******************************************************************************
 *
 * @author martin
 */
public class DdigPripStavEntity extends DListsEntity {
    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.SYSTEM_APPLICATION;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {};
    private static final String SECURITY_POLE_HLIDANA[] = {};

    private static final String listValue_dokonceno = "dokonceno";
    private static final String listValue_nedokonceno = "nedokonceno";

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
