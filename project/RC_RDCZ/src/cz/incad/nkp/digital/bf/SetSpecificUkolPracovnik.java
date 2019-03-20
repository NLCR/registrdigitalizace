package cz.incad.nkp.digital.bf;

import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import com.amaio.plaant.sync.Record;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.core.tools.ReliefWizardMessage;
import cz.incad.nkp.digital.constants.CnkpUkol;
import cz.incad.nkp.digital.constants.CnkpZakazka;
import java.util.LinkedList;
import java.util.List;

/*******************************************************************************
 *
 * @author martin
 */
public class SetSpecificUkolPracovnik extends BussinessFunctionMother implements CnkpUkol, CnkpZakazka {
    private static final String LOCAL_SECURITY = "data110";
    private String pracovnik = null;
    private String zpracovatel = null;
    private List<String> ukol = new LinkedList<String>();
    //RANGE = 1-9 !!! - musi korespondovat s Definici
    private final int COUNT_UKOL = 6;


    /***************************************************************************
     * 
     * @return
     * @throws ValidationException
     * @throws WizardException
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        ReliefWizardMessage wmf = new ReliefWizardMessage();

        //Kontrola na počet aktivních záznamů při spouštění BF
        //momentální nastavení je právě JEDEN označený záznam při spuštění BF
        if (getWizardContextClient().getSelectedKeys().length < 1) {
            throw new WizardException("Pro tuto funkci musí být označen alespoň jeden pracovní záznam.");
        }

        //Nastaveni defaultnich hodnot
        getWizardContextClient().getWizardRecord().getSimpleField("pracovnik01").setValue("NULL");
        getWizardContextClient().getWizardRecord().getSimpleField("zpracovatel01").setValue("zpknav");
        for (int i = 1; i <= COUNT_UKOL; i++) {
            getWizardContextClient().getWizardRecord().getSimpleField("ukol0" + i).setValue("NULL");
        }
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
        pracovnik = (String)getWizardContextClient().getWizardRecord().getSimpleField("pracovnik01").getValue();
        zpracovatel = (String)getWizardContextClient().getWizardRecord().getSimpleField("zpracovatel01").getValue();

        for (int i = 1; i <= COUNT_UKOL; i++) {
            if (!("NULL").equals((String)getWizardContextClient().getWizardRecord().getSimpleField("ukol0" + i).getValue())) {
                ukol.add((String)getWizardContextClient().getWizardRecord().getSimpleField("ukol0" + i).getValue());
            }
        }

        if (ukol.isEmpty()|| pracovnik.equals("NULL")) throw new WizardException("Nezadali jste všechny hodnoty.");
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
        RecordsIterator ritZakazky;
        RecordsIterator ritUkoly;
        Record recZakazka;
        Record recUkol;

        ritZakazky = getWizardContextClient().getSelectedRecords();
        getWizardContextClient().addRootDomain(getDomenu("cz.incad.nkp.digital.Ukol"));
        while (ritZakazky.hasMoreRecords()) {
            recZakazka = ritZakazky.nextRecord();
            ritUkoly = recZakazka.getTableField(ZAKAZKA_tUkol).getTableRecords();
            while (ritUkoly.hasMoreRecords()) {
                recUkol = ritUkoly.nextRecord();
                if (recUkol.getSimpleField(UKOL_typPrace).getValue() != null) {
                    if (ukol.contains((String)recUkol.getSimpleField(UKOL_typPrace).getValue())) {
                        recUkol.getSimpleField(UKOL_pracovnik).setValue(pracovnik);
                        recUkol.getSimpleField(UKOL_zpracovatel).setValue(zpracovatel);
                    }
                }
            }
        }

        getWizardContextClient().commit();
        return wmf;
    }

}
