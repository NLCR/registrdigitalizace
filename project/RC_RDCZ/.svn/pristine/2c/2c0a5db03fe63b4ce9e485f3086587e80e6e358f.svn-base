/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.nkp.digital.bf;

import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.core.caches.CacheCoreSetting;
import cz.incad.core.tools.ReliefWizardMessage;
import cz.incad.nkp.digital.constants.CnkpMikrofilm;

/*******************************************************************************************************************************************
 *
 * @author martin
 */
public class TiskPruvodkaMikrofilmu extends BussinessFunctionMother implements CnkpMikrofilm {
    private static final String LOCAL_SECURITY = "tisk101";
    private Object can;

    /***************************************************************************************************************************************
     *
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        //Kontrola na počet aktivních záznamů při spouštění BF
        //momentální nastavení je právě JEDEN označený záznam při spuštění BF
        if (getWizardContextClient().getSelectedKeys().length != 1) {
            throw new WizardException("Pro tuto funkci musí být označen jeden pracovní záznam.");
        }
        can = getWizardContextClient().getSelectedRecords().nextRecord().getSimpleField(MIKROFILM_archCisloNeg).getValue();
        return null;
    }


    /***************************************************************************************************************************************
     *
     * @param panelName
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public WizardMessage panelLeave(String panelName) throws ValidationException, WizardException {
        return null;
    }


    /***************************************************************************************************************************************
     *
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     * @throws com.amaio.plaant.businessFunctions.ApplicationErrorException
     */
    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        ReliefWizardMessage rwm = new ReliefWizardMessage();

        rwm.addInfo("Tiskový výstup Průvodka mikrofilmu pro čan " + can, null, null, null, null, 0);
        rwm.addLine("<script> setTimeout(\"window.close()\", 10 * 1000); </script>");
        rwm.addLine("<SCRIPT language=\"JavaScript\">window.location=\"" + CacheCoreSetting.getCoreSetting(RC_JASPERREPORTS_ROOT_HTTP) + "?reportdir=NKP_PRUVODKA&reportFileName=PruvodkaMikrofilm&CAN=" + can + "&action=Print+report\"</SCRIPT>");
        return rwm;
    }


} //eof
