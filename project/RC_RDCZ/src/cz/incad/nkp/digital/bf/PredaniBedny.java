/** ****************************************************************************
 * PredaniBedny.java
 *
 * Založeno: 8. leden 2008, 15:20
 *
 *
 * Autor: Martin Nováček
 * e-mail: <a href="mailto:martin.novacek@incad.cz">
 *
 * INCAD, spol. s r.o.
 * U krčského nádraží 36
 * 140 00  Praha 4, Česká republika
 *
 * @Revize: $Revision$
 * Základní popis třídy:
 *
 */

package cz.incad.nkp.digital.bf;

import com.amaio.plaant.businessFunctions.*;
import com.amaio.plaant.desk.QueryException;
import com.amaio.plaant.desk.container.PlaantUniqueKey;
import com.amaio.plaant.sync.Record;
import com.amaio.plaant.sync.SimpleField;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.core.tools.Epocha;
import cz.incad.nkp.digital.constants.ConstBasic_NKP;
import cz.incad.rd.PredlohaEntity;
//import java.util.logging.Level;
import java.util.Date;
//import java.util.logging.Logger;

/** ****************************************************************************
 * BF která je asi jen dočasná, nebo se bude časem modifikovat.
 * U vybraných záznamů nastaví datum předání -- datumPredani
 */
public class PredaniBedny extends BussinessFunctionMother implements ConstBasic_NKP {
    private static final String LOCAL_SECURITY = "data104";
    private RecordsIterator pracovniZaznamy = null;
    
    /** ************************************************************************
     * Metoda není implementována. Veškerá logika bude v metodě runBussinesMethod().
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        int pocet = getWizardContextClient().getSelectedKeys().length;
        
        if (pocet == 0) {
            try {
                pracovniZaznamy = getWizardContextClient().getRecordsIterator(getWizardContextClient().getMetadata());
            } catch (QueryException ex) {
                //Logger.getLogger(PredaniBedny.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        } else {
            pracovniZaznamy = getWizardContextClient().getSelectedRecords();
            return null;
        }
    }
    
    /** ************************************************************************
     * Metoda v této BF není implementována. - BF nemá ovládaci uživatelský panel.
     */
    public WizardMessage panelLeave(String string) throws ValidationException, WizardException {
        return null;
    }

    /** ************************************************************************
     * Výkonná metoda.
     * 
     */
    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        WizardMessage wm = new WizardMessage();
        Record rec = null;
        Record recTemp;
        Date datum = new Date();
        SimpleField pole = null;
        boolean commitCheck = true;
        Epocha epocha = new Epocha();

        wm.addLine("Dnešní Datum: " + epocha.getDateToday());
        wm.addLine("Počet vstupních záznamů: " + pracovniZaznamy.getRecordsCount());
        wm.addLine("");
        while (pracovniZaznamy.hasMoreRecords()) {
            recTemp = pracovniZaznamy.nextRecord();
            //rec = pracovniZaznamy.nextRecord();
            //Tohle je tu kvuli BUGu v plaantu kdy nevrati kompletni zaznam ale jen cast a na zbytek dostadi NULL
            try {
                rec = getWizardContextClient().getRecord((PlaantUniqueKey)recTemp.getKey());
            } catch(RecordNotFoundException ex) {
                wm.addLine("Záznam nebyl nalezen: " + recTemp.getSimpleField(PredlohaEntity.f_idCislo));
                continue;
            }
            pole = rec.getSimpleField("datumPredani");
            if (pole.getValue() != null) {
                wm.addLine("Mezi vybranými záznamy byl nalezen jeden nebo více záznamů s již vyplněnou hodnotou pole Datum Předání. Opravte tuto nesrovnalost, do té doby nemůže být tato akce dokončena.");
                commitCheck = false;
                break;
            }
            rec.getSimpleField("datumPredani").setValue(datum);
        }
        if (commitCheck) {
            getWizardContextClient().commit();
            wm.addLine("Funkce broběhla v pořádku.");
        }
        return wm;
    }


}
