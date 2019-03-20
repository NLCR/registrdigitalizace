/** ****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.rd;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.EntityImplMother;
import java.util.Date;

/** ****************************************************************************
 *
 * @author martin
 */
public class XPredDigObjEntity extends EntityImplMother {
    public static final String f_rDigObjekt = "rDigObjekt";
    public static final String f_rPredloha = "rPredloha";
    public static final String f_revizeDate = "revizeDate";
    public static final String f_revizePrac = "revizePrac";
    public static final String f_revizeStav = "revizeStav";


    /** ************************************************************************
     *
     * @param rec
     * @return
     */
    @Override
    public Record onGetRecord(Record rec) {
        return rec;
    }

    /** ************************************************************************
     * Nastaví defaultní hodnoty.
     * @param rec
     * @return
     * @throws AddException 
     */
    @Override
    public Record onCreateLocal(Record rec) throws AddException {

        rec.getSimpleField(f_revizeDate).setValue(new Date());
        rec.getSimpleField(f_revizeStav).setValue("automatickaRevizeOK");
        return rec;
    }

}
