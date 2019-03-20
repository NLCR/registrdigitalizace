/*
 * cz.incad.nkp.digital.bf.PredatZpracovateli2.java
 * created on 5.8.2008
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
import com.amaio.plaant.dbdef.DbField;
import com.amaio.plaant.dbdef.DbFieldType;
import com.amaio.plaant.dbdef.WizardUI;
import com.amaio.plaant.dbdef.WizardUI.WizardPanel;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.core.tools.ReliefWizardMessage;


/*******************************************************************************
 * @author mno
 * <b>Class description:</b>
 * 
 */
public class PredatZpracovateli2 extends BussinessFunctionMother {
    private static int pocetPanelu = 0;
    private static Boolean finished = Boolean.FALSE;
    private static Boolean first = Boolean.TRUE;


    /***************************************************************************
     * 
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        ReliefWizardMessage wmf = new ReliefWizardMessage();
        return wmf;
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
        Object carKod;
        WizardUI wu;
        
        carKod = getWizardContextClient().getWizardRecord().getSimpleField("lendExemplar").getValue();
        wu = getWizardContextClient().getBusinessMethod().getWizardUI();
                        
        if (carKod == null) {
            if (first) throw new WizardException("V průběhu funkce jste nezadali žádné vstupní hodnoty.");
            wu.uiPanels.remove(pocetPanelu - 1);
            initializePanelEnding(wu);
            getWizardContextClient().getWizardController().rebuildWizard();
            try {
                runBusinessMethod();
            } catch (ApplicationErrorException ex) {
                throw new WizardException(ex);
            }
            first = Boolean.FALSE;
            return wm;
        } else {
            wm.addLine("HODNOTA: " + carKod);
        }

        wm.addLine("Pocet elementu: " + wu.uiPanels.size());
        wm.addLine("Aktuální Panel Name: " + panelName);
        pocetPanelu = wu.uiPanels.size();

        wm.addLine("PUVODNI-------------------------------");
        for (int i = 0; i < wu.uiPanels.size(); i++) {
            wm.addLine(i + ". " + ((WizardPanel)wu.uiPanels.get(i)).getPanelName());
        }

        wm.addLine("");
        wm.addLine("");
        wm.addLine("");
        
        
        initializePanel(wu, "Panel_New" + (pocetPanelu + 1));
        
        wm.addLine("Pocet elementu po zmene: " + wu.uiPanels.size());
        //vypis elementu
        wm.addLine("ZMENA-------------------------------");
        for (int i = 0; i < wu.uiPanels.size(); i++) {
            wm.addLine(i + ". " + ((WizardPanel)wu.uiPanels.get(i)).getPanelName());
        }
        getWizardContextClient().getWizardController().rebuildWizard();
        //BUG plaantu
        getWizardContextClient().getWizardRecord().getSimpleField("lendExemplar").setValue(null);
        first = Boolean.FALSE;
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
        WizardMessage wm;

        if (finished) return null;

        wm = new WizardMessage();
        wm.addLine("RUN BUSSINES METHOD - KONEC");
        finished = Boolean.TRUE;
        return wm;
    }


    /***************************************************************************
     *
     * @param wu
     * @param ident
     * @return
     */
    private static WizardPanel initializePanel(WizardUI wu, String ident) {
		// tady nastavuji dynamicky panel, ktery se bude porad opakovat, pouze field se bude menit (cisloX)
		WizardPanel wp = wu.createWizardPanel(ident,"Přidaný panel " + (pocetPanelu + 1),null,"Popis nového panelu.");
        DbField vek = new DbField();
		wp.removeAllFields();
		vek.setDbName("lendExemplar");
		vek.setName("lendExemplar");
		vek.setHumanHelp("lendExemplar");
		vek.setType(DbFieldType.STRING_TYPE);
		vek.setInitValue(null);
		wp.addPanelField(vek);
        return wp;
	}
    
    
    /***************************************************************************
     *
     * @param wu
     * @return
     */
	private static WizardPanel initializePanelEnding(WizardUI wu) {
		// vygeneruje posledni panel s otazkou jestli je vse ok
		WizardPanel wp = wu.createWizardPanel("Konec Funkce","Kliknutím na Disketku ukončíte Funkci",null,"");
		//DbField vek = new DbField();
		wp.removeAllFields();
		
//		vek.setDbName("ukonceni");
//		vek.setName("ukonceni");
//		vek.setHumanHelp("OK ?");
//		vek.setType(DbFieldType.BOOLEAN_TYPE);
//		vek.setRepresentation(DbFieldRepresentation.CHECK);
//		vek.setInitValue(Boolean.FALSE);
//		wp.addPanelField(vek);
		return wp;
	}
    
    
}//eof class PredatZpracovateli2
