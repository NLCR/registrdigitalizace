/*******************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.rd;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.EntityImplMother;
import cz.incad.nkp.digital.constants.CnkpSecurity;
import java.util.GregorianCalendar;

/*******************************************************************************
 *
 * @author martin
 */
public class PredaniEntity extends EntityImplMother implements CnkpSecurity {
    public static final String CLASSNAME        = "cz.incad.rd.Predani";

    public static final String f_typAkce        = "typAkce";
    public static final String f_kdo            = "kdo";
    public static final String f_kdyDate        = "kdyDate";
    public static final String f_kdyTime        = "kdyTime";
    public static final String f_komuOdKoho     = "komuOdKoho";
    public static final String f_varka          = "varka";
    public static final String f_idBf           = "idBf";
    public static final String r_rPredloha      = "rPredloha";

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
        GregorianCalendar gCal = new GregorianCalendar();
        rec = super.onCreateLocal(rec);
        
        rec.getSimpleField(PredaniEntity.f_kdyDate).setValue(gCal.getTime());
        rec.getSimpleField(PredaniEntity.f_kdyTime).setValue(gCal.getTime());
        return rec;
    }

}
