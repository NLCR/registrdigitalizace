/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.rd;

import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.EntityImplMother;

/** ****************************************************************************
 *
 * @author martin
 */
public class DigVazbyEntity extends EntityImplMother {

    /** ************************************************************************
     * 
     * @param rec
     * @return 
     */
    @Override
    public Record onGetRecord(Record rec) {
        return rec;
    }

}
