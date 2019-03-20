/*
 * PrepoctiMikrofilmy.java
 *
 * Created on 19. listopad 2007, 12:53
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
import cz.incad.nkp.digital.constants.ConstBasic_NKP;

/**
 * @author Martin Nováček (mno)
 *
 * Bussines Funkce pro Administrátora.
 * Po spuštrění BF se přepočítají všechny počítané hodnoty v Agendě Mikrofilm
 * Tyto hodnoty jsou za normálních okolností počítané triggerem.
 * Funkce se používá bezprostředně po Importu dat, aby doplnila informace místo triggeru.
 */
public class PrepoctiMikrofilmy extends BussinessFunctionMother implements ConstBasic_NKP { 
    private static final String LOCAL_SECURITY = "sys100";
    
    /**
     * Metoda není implementována. Veškerá logika bude v metodě runBussinesMethod().
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        WizardMessage wm = new WizardMessage();
        wm.addLine("Čekejte prosím, probíhá zpracování dat...");
        return wm;
    }
    
    /**
     * Metoda v této BF není implementována. - BF nemá ovládaci uživatelský panel.
     */
    public WizardMessage panelLeave(String string) throws ValidationException, WizardException {
        return null;
    }
    
    /**
     * Výkonná metoda. Vybere z agendy Mikrofilm všechny záznamy které založil uživatel admin (import 5key)
     * "zalUser = admin" a nad těmi provede trigger onUpdate.
     */
    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        WizardMessage wm = new WizardMessage();
        RecordsIterator rit = null;
        RecordWorkerContext rw = new RecordWorkerContext(getWizardContextClient());
        Record rec  = null;
        int counter = 0;
        
        try {
            //získáme iterátor všech záznamů z dané agendy, Podmínka je ošemetná, dostanem takto jen záznamy kde "zalUser = admin"
            rit = rw.getZaznamy(NKP_MIKROFILM_CLASSNAME, "zalUser", "admin", null, false);
        } catch (QueryException ex) {
            ex.printStackTrace();
        }
        wm.addLine("Celkový počet záznamů, které měly být zpracovány: " + rit.getRecordsCount());
        while (rit.hasMoreRecords()) {
            rec = rit.nextRecord();
            rec.getSimpleField("zalUser").setValue("admin");
            getWizardContextClient().commit();
            counter++;
        }
        wm.addLine("Počet záznamů které byly zpracovány: " + counter);
        return wm;
    }
    
    
}
