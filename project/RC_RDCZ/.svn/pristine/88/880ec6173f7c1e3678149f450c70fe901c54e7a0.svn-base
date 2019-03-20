/*
 * cz.incad.nkp.digital.bf.MikrofilmKorekceStavuVazebniTabulky.java
 * created on 16.7.2008
 *
 * author: Martin Nováček (mno)
 * e-mail: <a href="mailto:martin.novacek@incad.cz">
 *
 * INCAD, spol. s r.o.
 * U krčského nádraží 36
 * 140 00  Praha 4, Česká republika
 */

package cz.incad.nkp.digital.bf;

import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import com.amaio.plaant.desk.QueryException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.core.tools.RecordWorkerContext;
import cz.incad.nkp.digital.constants.CnkpMikrofilm;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author mno
 * <b>Class description:</b>
 * 
 */
public class MikrofilmKorekceStavuVazebniTabulky extends BussinessFunctionMother implements CnkpMikrofilm {
    private static final String LOCAL_SECURITY = "sys101";
    
    /***************************************************************************
     * 
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        return null;
    }
    
    /***************************************************************************
     * 
     * @param arg0
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public WizardMessage panelLeave(String arg0) throws ValidationException, WizardException {
        return null;
    }
    
    /***************************************************************************
     * 
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     * @throws com.amaio.plaant.businessFunctions.ApplicationErrorException
     */
    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        RecordsIterator rMikrofilmy;
        RecordsIterator rMezitabulky;
        RecordWorkerContext rwc = new RecordWorkerContext(getWizardContextClient());
        Record rec;
        
        try {
            rMikrofilmy = rwc.getZaznamy(NKP_MIKROFILM_CLASSNAME, null, Boolean.FALSE);
            while (rMikrofilmy.hasMoreRecords()) {
                rec = rMikrofilmy.nextRecord();
                rMezitabulky = rec.getTableField(MIKROFILM_tXPredMikro).getTableRecords();
                if (rMezitabulky != null) {
                    while (rMezitabulky.hasMoreRecords()) {
                        rMezitabulky.nextRecord().getSimpleField(RECORD_stavRec).setValue(rec.getSimpleField(RECORD_stavRec).getValue());
                    }
                }
            }
        } catch (QueryException ex) {
            Logger.getLogger(MikrofilmKorekceStavuVazebniTabulky.class.getName()).log(Level.SEVERE, null, ex);
        }
        getWizardContextClient().commit();
        return null;
    }
    
    
}//eof class MikrofilmKorekceStavuVazebniTabulky
