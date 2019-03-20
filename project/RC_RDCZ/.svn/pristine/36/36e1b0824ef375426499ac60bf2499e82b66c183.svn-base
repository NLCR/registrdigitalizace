/* *****************************************************************************
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.rd.bf;

import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.core.tools.DirectConnection;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *******************************************************************************
 *
 * @author Martin
 */
public class Config_SystemInfo extends BussinessFunctionMother {

    private static Logger LOG = Logger.getLogger(Config_SystemInfo.class.getName());

    /**
     ***************************************************************************
     *
     * @return @throws ValidationException
     * @throws WizardException
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        return null;
    }

    /**
     ***************************************************************************
     *
     * @param panelName
     * @return
     * @throws ValidationException
     * @throws WizardException
     */
    public WizardMessage panelLeave(String panelName) throws ValidationException, WizardException {
        return null;
    }

    /**
     ***************************************************************************
     *
     * @return @throws ValidationException
     * @throws WizardException
     * @throws ApplicationErrorException
     */
    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        WizardMessage wm = new WizardMessage();

        try {
            Connection conn = DirectConnection.getConnection();
            wm.addLine("DriverName: " + conn.getMetaData().getDriverName());
            wm.addLine("DriverVersion: " + conn.getMetaData().getDriverVersion());

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
            wm.addLine(ex.getMessage());
        }

        return wm;
    }
}
