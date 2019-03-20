/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.rd;

import com.amaio.plaant.businessFunctions.AnnotationKeys;
import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.EntityImplMother;
import cz.incad.core.tools.ReliefUser;

/** ****************************************************************************
 *
 * @author martin.novacek@incad.cz
 */
public class URNNBNEntity extends EntityImplMother {

    /** ************************************************************************
     * 
     * @param rec
     * @return 
     */
    @Override
    public Record onGetRecord(Record rec) {
        ReliefUser ru = new ReliefUser(getTriggerContext());
        String userName = ru.getLogin();
        if (!ru.isSystemAdmin()) {
            rec.setAnnotation(AnnotationKeys.READ_ONLY_SECURITY_PROPERTY, AnnotationKeys.TRUE_VALUE);
            rec.setAnnotation(AnnotationKeys.REMOVE_FORBIDDEN_SECURITY_PROPERTY, AnnotationKeys.TRUE_VALUE);
        }
        
        return rec;
    }

}
