package cz.incad.nkp.digital.bf;

import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import com.amaio.plaant.desk.QueryException;
import com.amaio.plaant.metadata.Metadata;
import com.amaio.plaant.sync.Domain;
import com.amaio.plaant.sync.Record;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.core.tools.RecordWorkerContext;
import cz.incad.core.tools.ReliefFilters;
import cz.incad.core.tools.ReliefWizardMessage;
import cz.incad.nkp.digital.ZakazkaEntity;
import cz.incad.nkp.digital.constants.CnkpZakazka;
import java.util.logging.Level;
import java.util.logging.Logger;

/*******************************************************************************
 *
 * @author martin
 */
public class CompleteUDIRDCZ extends BussinessFunctionMother implements CnkpZakazka {
    private static final String LOCAL_SECURITY = "sys103";

    /***************************************************************************
     * 
     * @return
     * @throws ValidationException
     * @throws WizardException
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        ReliefWizardMessage wmf = new ReliefWizardMessage();
        return wmf;
    }


    /***************************************************************************
     *
     * @param string
     * @return
     * @throws ValidationException
     * @throws WizardException
     */
    public WizardMessage panelLeave(String string) throws ValidationException, WizardException {
        ReliefWizardMessage wmf = new ReliefWizardMessage();
        return wmf;
    }


    /***************************************************************************
     *
     * @return
     * @throws ValidationException
     * @throws WizardException
     * @throws ApplicationErrorException
     */
    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        ReliefWizardMessage wmf = new ReliefWizardMessage();
        RecordWorkerContext rwc = new RecordWorkerContext(getWizardContextClient());
        RecordsIterator ritZakazky;
        Record recZakazka;

        try {
            ritZakazky = rwc.getZaznamy("cz.incad.nkp.digital.Zakazka", RECORD_stavRec, "finished",ZAKAZKA_udirdcz , null, null, false);
            wmf.addLine("Počet zpracovaných záznamů: " + ritZakazky.getRecordsCount());
            while (ritZakazky.hasMoreRecords()) {
                recZakazka = ritZakazky.nextRecord();
                recZakazka.getSimpleField(ZAKAZKA_udirdcz).setValue(null);
            }
            getWizardContextClient().commit();
        } catch (QueryException ex) {
            Logger.getLogger(CompleteUDIRDCZ.class.getName()).log(Level.SEVERE, null, ex);
        }

        return wmf;
    }

}
