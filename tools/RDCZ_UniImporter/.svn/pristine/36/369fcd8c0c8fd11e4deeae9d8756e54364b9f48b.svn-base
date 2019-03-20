/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter.model;

import com.amaio.plaant.businessFunctions.impl.ExternalContextServer;
import cz.incad.r3tools.R3Commons;
import cz.incad.relief3.rdcz.uniimporter.utils.Configuration;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.CreateException;
import javax.naming.NamingException;

/** ****************************************************************************
 *
 * @author martin
 */
public class SiglaCommander {
    private static final SiglaCommander _instance = new SiglaCommander();
    private List<OnePrispevatel> allSigla;
    private static final String ADMIN_LOGIN         = "admin";
    private static final String ADMIN_PASSWORD      = "vanilkovazmrzlina";

    /** ************************************************************************
     * Privátní konstruktor - Jedináček
     */
    private SiglaCommander() {
        //Načíst všechny sigly
        ExternalContextServer context = null;
        List<OnePrispevatel> lTemp = new LinkedList<OnePrispevatel>();

        try {
            //připojíme se do Relief a získáme SIGLy.
            context = R3Commons.getConnection(Configuration.getInstance().IMPORT_RELIEF_URL, ADMIN_LOGIN, ADMIN_PASSWORD);
            
            
            
            //new OnePrispevatel("NAZEV", "USERNAME", "USERPASS");
        } catch (RemoteException ex) {
            Logger.getLogger(SiglaCommander.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(SiglaCommander.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CreateException ex) {
            Logger.getLogger(SiglaCommander.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (context != null) context.close();
        }

        //Nakonec předáme link na seznam globální proměnné.
        allSigla = lTemp;
    }

    /** ************************************************************************
     * Vrátí instanci
     * @return 
     */
    public static SiglaCommander getInstance() {
        return _instance;
    }

}
