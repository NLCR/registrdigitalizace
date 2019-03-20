/*
 * cz.incad.nkp.digital.bf.ChangeCisloZakazky.java
 * created on 10.4.2008
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
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import com.amaio.plaant.sync.Record;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.nkp.digital.constants.CnkpZakazka;

/**
 * @author mno
 * <b>Class description:</b>
 * 
 */
public class ChangeCisloZakazky extends BussinessFunctionMother implements CnkpZakazka {
    private static final String LOCAL_SECURITY = "app100";
    private Record zaznam = null;
    private String cisloZakazkyNove = null;
    private String cisloZakazkyStare = null;
    
    /**
     * Inicializační metoda Bussines Funkce
     * Kontroluje zda-li je označen právě jeden pracovní záznam.
     * Kontroluje zdali vybraný záznam obsahuje pole cisloZakazky - číslo zakázky
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        WizardMessage wm = new WizardMessage();
        //Kontrola na počet aktivních záznamů při spouštění BF
        //momentální nastavení je právě JEDEN označený záznam při spuštění BF
        if (getWizardContextClient().getSelectedKeys().length != 1) 
            throw new WizardException("Pro tuto funkci musí být označen pouze jeden pracovní záznam.");
        //načtení záznamu titulu ke kterému budem tvořit exempláře.
        zaznam = getWizardContextClient().getSelectedRecords().nextRecord();
        if (!zaznam.hasField(ZAKAZKA_cisloZakazky)) 
            throw new WizardException("Tento záznam neobsahuje pole Číslo zakázky (cisloZakazky). Funkce není určena pro tento druh záznamů.");
        cisloZakazkyStare = (String) zaznam.getSimpleField(ZAKAZKA_cisloZakazky).getValue();
        getWizardContextClient().getWizardRecord().getSimpleField("cisloZakazkyNove").setValue(cisloZakazkyStare);
        wm.addLine("Nynější číslo zakázky: " + cisloZakazkyStare);
        return wm;
    }
    
    /**
     * Metoda načítá novou hodnotu cisloZakazky - číslo zakázky
     * Momentálně neprovádí žádné kontroly na validitu.
     * @param arg0
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public WizardMessage panelLeave(String arg0) throws ValidationException, WizardException {
        WizardMessage wm = new WizardMessage();
        Object hodnota = getWizardContextClient().getWizardRecord().getSimpleField("cisloZakazkyNove").getValue();
        if (hodnota == null) {
            cisloZakazkyNove = null;
            wm.addLine("Nezadali jste žádnou hodnotu.");
        } else {
            cisloZakazkyNove = (String) getWizardContextClient().getWizardRecord().getSimpleField("cisloZakazkyNove").getValue();
            wm.addLine("Nově zadané Číslo zakázky: " + cisloZakazkyNove);
        }
        return wm;
    }
    
    /**
     * Výkoná metoda Bussines Funkce
     * Změní hodnotu na nově zadanou uživatelem.
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     * @throws com.amaio.plaant.businessFunctions.ApplicationErrorException
     */
    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        WizardMessage wm = new WizardMessage();
        zaznam.getSimpleField(ZAKAZKA_cisloZakazky).setValue(cisloZakazkyNove);
        getWizardContextClient().commit();
        wm.addLine("Změna proběhla v pořádku");
        return wm;
    }

}//eof class ChangeCisloZakazky
