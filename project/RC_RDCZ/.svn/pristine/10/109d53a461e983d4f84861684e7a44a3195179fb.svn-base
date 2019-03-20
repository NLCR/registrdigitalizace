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
import cz.incad.core.tools.ReliefLogger;
import java.io.File;
import java.io.IOException;

/*******************************************************************************
 *
 * @author martin
 */
public class DataInspector extends BussinessFunctionMother {
    private static final String LOCAL_SECURITY = "sys102";
    private final String classNameZakazka = "cz.incad.nkp.digital.Zakazka";
    private int action;


    /***************************************************************************
     * 
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public final WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        return null;
    }

    /***************************************************************************
     *
     * @param panelName
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public final WizardMessage panelLeave(String panelName) throws ValidationException, WizardException {
        WizardMessage wm = new WizardMessage();

        if (panelName.equalsIgnoreCase("DataInspectorDialog01")) {
            action = (Integer)getWizardContextClient().getWizardRecord().getSimpleField("action").getValue();
        } else {
            throw new WizardException("Nedefininovaný Panel...");
        }
        return wm;
    }


    /***************************************************************************
     *
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     * @throws com.amaio.plaant.businessFunctions.ApplicationErrorException
     */
    public final WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        WizardMessage wm = new WizardMessage();

        try {
            if (action == 0) {
                return wm.addLine("Nic se nestalo...");
            } else if (action == 1) {
                return runAction1(wm);
            } else if (action == 999) {
                return runActionTest(wm);
            }
        } catch(Exception ex) {
            throw new WizardException("Exception: " + ex.getMessage());
        }

        return wm;
    }


    /***************************************************************************
     * Nastavit dokumenty jako dokoncene na zaklade vyplneneho pole Zakazka.URLkramerius
     * @param wm
     */
    private WizardMessage runAction1(WizardMessage wm) throws QueryException, ValidationException, WizardException, ApplicationErrorException {
        RecordWorkerContext rw = new RecordWorkerContext(getWizardContextClient());
        RecordsIterator ritZakazka;
        Record recTemp;
        Record recZakazka;
        Record recTitul;
        boolean commit = false;
        int countAll;
        int countNow = 0;

        wm.addLine("Action 1");
        wm.addLine("Nastavit dokumenty jako dokoncene na zaklade vyplneneho pole Zakazka.URLkramerius.");

//        ritZakazka = rw.getZaznamy(classNameZakazka, "url", "", false);
//        //wm.addLine("Pocet zakazek URL = \"\": " + ritZakazka.getRecordsCount());
//        while (ritZakazka.hasMoreRecords()) {
//            recTemp = ritZakazka.nextRecord();
//            recTemp.getSimpleField("url").setValue(null);
//            commit = true;
//            wm.addLine("ID: " + recTemp.getKey().toString() + "  idCislo: " + recTemp.getSimpleField("idCislo").getValue());
//        }
//        if (commit) {
//            getWizardContextClient().commit();
//            commit = false;
//        }

        ritZakazka = rw.getZaznamy4xFieldIsNotNull(classNameZakazka, "url", null, null, null, null, false);
        wm.addLine("Pocet zakazek URL is NOT NULL: " + ritZakazka.getRecordsCount());
        countAll = ritZakazka.getRecordsCount();
        wm.addLine("Čísla zakázek které nemají připojený Titul:");
        while (ritZakazka.hasMoreRecords()) {
            countNow++;
            getWizardContextClient().addRootDomain(getDomenu(classNameZakazka));
            recZakazka = ritZakazka.nextRecord();
            recZakazka.getSimpleField("stavRec").setValue("finished");
            recTitul = recZakazka.getReferencedField("rTitNkp").getReferencedRecord();
            ReliefLogger.severe(countNow + "/" + countAll + " - " + "idCislo: " + recZakazka.getSimpleField("idCislo").getValue());
            if (recTitul != null) {
                if (!"archived".equals(recTitul.getSimpleField("stavRec").getValue())) {
                    recTitul.getSimpleField("stavRec").setValue("finished");
                }
            } else {
                wm.addLine("" + recZakazka.getSimpleField("idCislo") + " - " + recZakazka.getSimpleField("cisloZakazky").getValue());
            }
            getWizardContextClient().commit();
        }

        return wm;
    }


    /***************************************************************************
     *
     * @param wm
     * @return
     */
    private WizardMessage runActionTest(WizardMessage wm) throws IOException {
        File soubor = new File("testFile");
        soubor.createNewFile();
        ReliefLogger.severe("AbsolutePath: " + soubor.getAbsolutePath() + "\n+CanonicalPath: " + soubor.getCanonicalPath());
        return wm;
    }


}
