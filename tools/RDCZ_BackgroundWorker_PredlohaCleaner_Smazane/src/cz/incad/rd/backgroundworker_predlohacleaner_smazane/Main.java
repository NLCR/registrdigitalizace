/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.rd.backgroundworker_predlohacleaner_smazane;

import com.amaio.plaant.desk.QueryException;
import cz.manocz80.utils.SendMail;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.CreateException;
import javax.naming.NamingException;

/** ****************************************************************************
 *
 * @author martin
 */
public class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    private static final String CONFIGURATION_FILE_NAME     = "config.properties";
    public static final String PROP_MAIL_SMTPSERVER         = "MAIL.SMTPSERVER";
    public static final String PROP_MAIL_FROM               = "MAIL.FROM";
    public static final String PROP_MAIL_ADMINISTRATOR_TO   = "MAIL.ADMINISTRATOR.TO";

    /** ************************************************************************
     * 
     * @param args
     * @throws FileNotFoundException
     * @throws IOException
     * @throws RemoteException
     * @throws QueryException
     * @throws MimeTypeParseException 
     */
    public static void main(String[] args) {
        Peon peon;

        LOG.log(Level.INFO, "Neplecha započata...");
        Properties props = new Properties();
        try {
            //Načteme properties
            props.load(new FileReader(new File(CONFIGURATION_FILE_NAME)));
            //spustíme výkonnou metodou
            peon = new Peon(props);
            peon.run();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            sendMail(props.getProperty(PROP_MAIL_SMTPSERVER), props.getProperty(PROP_MAIL_FROM), props.getProperty(PROP_MAIL_ADMINISTRATOR_TO), "EXCEPTION: " + ex.getMessage(), " - IOException");
        } catch (NamingException ex) {
            LOG.log(Level.SEVERE, null, ex);
            sendMail(props.getProperty(PROP_MAIL_SMTPSERVER), props.getProperty(PROP_MAIL_FROM), props.getProperty(PROP_MAIL_ADMINISTRATOR_TO), "EXCEPTION: " + ex.getMessage(), " - NamingException");
        } catch (QueryException ex) {
            LOG.log(Level.SEVERE, null, ex);
            sendMail(props.getProperty(PROP_MAIL_SMTPSERVER), props.getProperty(PROP_MAIL_FROM), props.getProperty(PROP_MAIL_ADMINISTRATOR_TO), "EXCEPTION: " + ex.getMessage(), " - QueryException");
        } catch (CreateException ex) {
            LOG.log(Level.SEVERE, null, ex);
            sendMail(props.getProperty(PROP_MAIL_SMTPSERVER), props.getProperty(PROP_MAIL_FROM), props.getProperty(PROP_MAIL_ADMINISTRATOR_TO), "EXCEPTION: " + ex.getMessage(), " - CreateException");
        }

        LOG.log(Level.INFO, "Neplecha ukončena...");
    }

    /** ************************************************************************
     * 
     * @param smtpServer
     * @param from
     * @param administratorTo 
     */
    public static void sendMail(String smtpServer, String from, String administratorTo, String message, String subject) {
        SendMail sm;
        try {
            sm = new SendMail(smtpServer);
            sm.send(from, "RDCZ-BWPCS" + subject, message, administratorTo);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "", ex);
        }
    }

}