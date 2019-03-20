/*******************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.rd;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.businessFunctions.UpdateException;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.RecordEntity;

/*******************************************************************************
 *
 * @author martin
 */
public class IsoEntity extends RecordEntity {

    /***************************************************************************
     * 
     * @param rec
     * @return
     */
    @Override
    public Record onGetRecord(Record rec) {
        return rec;
    }


    /***************************************************************************
     *
     * @param rec
     * @return
     * @throws AddException
     */
    @Override
    public Record onCreateLocal(Record rec) throws AddException {
        return super.onCreateLocal(rec);
    }


    /***************************************************************************
     *
     * @param rec
     * @return
     * @throws AddException
     * @throws ValidationException
     */
    @Override
    public Record onCreate(Record rec) throws AddException, ValidationException {
        rec = super.onCreate(rec);
        rec = onCreateOnUpdate(rec);
        return rec;
    }


    /***************************************************************************
     *
     * @param rec
     * @return
     * @throws ValidationException
     * @throws UpdateException
     */
    @Override
    public Record onUpdate(Record rec) throws ValidationException, UpdateException {
        rec = super.onUpdate(rec);
        rec = onCreateOnUpdate(rec);
        return rec;
    }


    /***************************************************************************
     *
     * @param rec
     */
    private Record onCreateOnUpdate(Record rec) {
        return rec;
    }

}
