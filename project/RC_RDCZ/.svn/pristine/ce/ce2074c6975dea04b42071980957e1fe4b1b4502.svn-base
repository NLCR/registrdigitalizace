/*
 * Titul.java
 *
 * Created on 26. duben 2006, 15:41
 * <a href="mailto:pve@casd.cz">Ing. Pavel Vedral</a>
 * @version $Revision: 1.2 $
 */

package cz.incad.nkp.digital;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.doc.TitulEntity;
import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;
import java.math.BigDecimal;
import java.util.Vector;

/**
 *
 * @author <a href="mailto:pve@casd.cz">Ing. Pavel Vedral</a>
 * @version $Revision: 1.2 $
 */
public class TitNkpEntity extends TitulEntity {
    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.DATA;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {};
    private static final String SECURITY_POLE_HLIDANA[] = {};
    
    
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
     * @param poleHlidana
     * @param vRecordFieldsSecurity
     * @param canErase
     */
    @Override
    protected boolean setCustomSecurityOnGetRecord(Record rec, String[] poleHlidana, Vector<Integer> vRecordFieldsSecurity, boolean canErase, String kompetence) {
        return canErase;
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
        //ToDo dodelat podminku ktera zajisti ze se to sputi jen kdyz je to spusteno v teto agende
        //securityOnCreateLocal(rec, SECURITY_DESK_TYPE, SECURITY_CAN_CREATE_COMPETITIONS);
        if (rec.hasField("vyskaVcm")) {
            rec.getSimpleField("vyskaVcm").setValue(new BigDecimal(0));
        } else {
            //logger.severe("Zaznam nema pole vyskaVcm");
        }
        //nastaveni defaulktni hodnoty pro pole pocetStran
        if (rec.hasField("pocetStran")) {
            rec.getSimpleField("pocetStran").setValue(new BigDecimal(0));
        } else {
            //logger.severe("Zaznam nema pole vyskaVcm");
        }
        return rec;
    }


}
