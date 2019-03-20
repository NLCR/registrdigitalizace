package cz.incad.nkp.digital.bf;

import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import com.amaio.plaant.sync.Record;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.nkp.digital.constants.CnkpUkol;
import cz.incad.nkp.digital.constants.CnkpZakazka;
import java.util.Vector;

/*******************************************************************************
 *
 * @author martin
 */
public class FinishSpecificUkol extends BussinessFunctionMother implements CnkpZakazka, CnkpUkol {
    private static final String LOCAL_SECURITY = "data109";
    private Vector<String> allCeckingUkoly = new Vector<String>(0,1);

    /***************************************************************************
     *
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        WizardMessage wm = new WizardMessage();

        //Kontrola na počet aktivních záznamů při spouštění BF
        //momentální nastavení je alespoň JEDEN označený záznam při spuštění BF
        if (getWizardContextClient().getSelectedKeys().length == 0)
            throw new WizardException("Pro tuto funkci musí být označen alespoň jeden pracovní záznam.");
        wm.addLine("Počet záznamů vybraných ke zpracování: " + getWizardContextClient().getSelectedKeys().length);

        //nastaveni defaultnich hodnot
        getWizardContextClient().getWizardRecord().getSimpleField("specificUkol1").setValue("NULL");
        getWizardContextClient().getWizardRecord().getSimpleField("specificUkol2").setValue("NULL");
        getWizardContextClient().getWizardRecord().getSimpleField("specificUkol3").setValue("NULL");

        return wm;
    }


    /***************************************************************************
     *
     * @param panelName
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public WizardMessage panelLeave(String panelName) throws ValidationException, WizardException {
        WizardMessage wm = new WizardMessage();

        if (!getWizardContextClient().getWizardRecord().getSimpleField("specificUkol1").getValue().toString().equalsIgnoreCase("NULL")) allCeckingUkoly.add((String)getWizardContextClient().getWizardRecord().getSimpleField("specificUkol1").getValue());
        if (!getWizardContextClient().getWizardRecord().getSimpleField("specificUkol2").getValue().toString().equalsIgnoreCase("NULL")) allCeckingUkoly.add((String)getWizardContextClient().getWizardRecord().getSimpleField("specificUkol2").getValue());
        if (!getWizardContextClient().getWizardRecord().getSimpleField("specificUkol3").getValue().toString().equalsIgnoreCase("NULL")) allCeckingUkoly.add((String)getWizardContextClient().getWizardRecord().getSimpleField("specificUkol3").getValue());
        if (!getWizardContextClient().getWizardRecord().getSimpleField("specificUkol4").getValue().toString().equalsIgnoreCase("NULL")) allCeckingUkoly.add((String)getWizardContextClient().getWizardRecord().getSimpleField("specificUkol4").getValue());
        if (!getWizardContextClient().getWizardRecord().getSimpleField("specificUkol5").getValue().toString().equalsIgnoreCase("NULL")) allCeckingUkoly.add((String)getWizardContextClient().getWizardRecord().getSimpleField("specificUkol5").getValue());
        if (!getWizardContextClient().getWizardRecord().getSimpleField("specificUkol6").getValue().toString().equalsIgnoreCase("NULL")) allCeckingUkoly.add((String)getWizardContextClient().getWizardRecord().getSimpleField("specificUkol6").getValue());
        if (!getWizardContextClient().getWizardRecord().getSimpleField("specificUkol7").getValue().toString().equalsIgnoreCase("NULL")) allCeckingUkoly.add((String)getWizardContextClient().getWizardRecord().getSimpleField("specificUkol7").getValue());
        if (!getWizardContextClient().getWizardRecord().getSimpleField("specificUkol8").getValue().toString().equalsIgnoreCase("NULL")) allCeckingUkoly.add((String)getWizardContextClient().getWizardRecord().getSimpleField("specificUkol8").getValue());
        return wm;
    }


    /***************************************************************************
     *
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     * @throws com.amaio.plaant.businessFunctions.ApplicationErrorException
     */
    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        WizardMessage wm = new WizardMessage();
        RecordsIterator ritZakazka;
        RecordsIterator ritUkol;
        Record recZakazka;
        Record recUkol;
        int countUkol = 0;

        if (allCeckingUkoly.size() == 0) return getSumarizace(wm, countUkol);
        ritZakazka = getWizardContextClient().getSelectedRecords();
        ritZakazka = getWizardContextClient().getSelectedRecords();
        while (ritZakazka.hasMoreRecords()) {
            recZakazka = ritZakazka.nextRecord();
            ritUkol = recZakazka.getTableField(ZAKAZKA_tUkol).getTableRecords();
            while (ritUkol.hasMoreRecords()) {
                recUkol = ritUkol.nextRecord();
                if (recUkol.getSimpleField(UKOL_typPrace).getValue() != null) {
                    for (int i = 0; i < allCeckingUkoly.size(); i++) {
                        if (recUkol.getSimpleField(UKOL_typPrace).getValue().toString().equals(allCeckingUkoly.get(i))) {
                            recUkol.getSimpleField(RECORD_stavRec).setValue("finished");
                            countUkol++;
                        }
                    }
                }
            }
        }

        getWizardContextClient().commit();
        return getSumarizace(wm, countUkol);
    }


    /***************************************************************************
     *
     * @param wm
     * @param countUkoly
     * @return
     */
    private WizardMessage getSumarizace(WizardMessage wm , int countUkol) {
        wm.addLine("Sumarizace:");
        wm.addLine("Počet modifikovaných úkolů: " + countUkol);
        return wm;
    }


}
