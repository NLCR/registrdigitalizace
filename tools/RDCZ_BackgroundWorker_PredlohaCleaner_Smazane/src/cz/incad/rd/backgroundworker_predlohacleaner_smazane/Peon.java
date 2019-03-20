/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.rd.backgroundworker_predlohacleaner_smazane;

import com.amaio.plaant.DbBrowser;
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.impl.ExternalContextServer;
import com.amaio.plaant.desk.QueryException;
import com.amaio.plaant.desk.container.PlaantUniqueKey;
import com.amaio.plaant.metadata.Filter;
import com.amaio.plaant.sync.Domain;
import com.amaio.plaant.sync.Record;
import com.amaio.plaant.sync.UniqueKey;
import cz.incad.r3tools.R3Commons;
import cz.incad.r3tools.R3FilterTools;
import cz.incad.rd.PredlohaEntity;
import cz.incad.rd.DigObjektEntity;
import cz.incad.rd.URNNBNEntity;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.CreateException;
import javax.naming.NamingException;

/** ****************************************************************************
 *
 * @author martin
 */
public class Peon {
    private static final Logger LOG = Logger.getLogger(Peon.class.getName());
    private ExternalContextServer context;
    private DbBrowser dbb;
    Properties props;


    /** ************************************************************************
     * Konstruktor, načte properties hodnoty do proměných,
     * provede jejich validaci a nakonec založí připojení do Relief.
     * @param props
     * @throws IOException
     * @throws RemoteException
     * @throws NamingException
     * @throws CreateException 
     */
    public Peon(Properties props) throws RemoteException, NamingException, CreateException{
        String PROP_PASSWORD                = "PASSWORD";
        String PROP_LOGIN                   = "LOGIN";
        String PROP_URL                     = "URL";

        this.props = props;
        //Získáme připojení do Databáze
        LOG.log(Level.INFO, "Pokus o p\u0159ipojen\u00ed do Releif.\nURL:{0}\nLOGIN:{1}\nPASSWORD:{2}", new Object[]{props.getProperty(PROP_URL), props.getProperty(PROP_LOGIN), props.getProperty(PROP_PASSWORD)});
        this.context = R3Commons.getConnection(props.getProperty(PROP_URL), props.getProperty(PROP_LOGIN), props.getProperty(PROP_PASSWORD));
        this.dbb = R3Commons.getDbBrowser();
        LOG.log(Level.INFO, "Relief connection sucessfull.");
    }

    /** ************************************************************************
     * 
     * @throws RemoteException
     * @throws QueryException 
     */
    void run() throws RemoteException, QueryException {
        RecordsIterator ritPredloha;
        Record recPredloha;
        RecordsIterator ritDigObjekt;
        Record recDigObjekt;
        RecordsIterator ritUrnnbn;
        Record recUrnnbn;
        String predlohaClassname;
        String digObjektClassname;
        Filter filter;
        Filter filterDigObjekt;
        Domain dPredloha;
        Domain dDigObjekt;
        List<UniqueKey> lRecordsToDelete;
        List<UniqueKey> lRecordsToDeleteDigObjekt;
        List<String> lRecordsToDeleteCisloID = new LinkedList<String>();
        String pomocnyText;
        String idPredloha;

        lRecordsToDelete = new LinkedList<UniqueKey>();
        predlohaClassname = R3Commons.getClassnameWithoutEntity(PredlohaEntity.class.getName());
        digObjektClassname = R3Commons.getClassnameWithoutEntity(DigObjektEntity.class.getName());
        dPredloha = R3Commons.getDomain(context, predlohaClassname);
        dDigObjekt = R3Commons.getDomain(context, digObjektClassname);
        filter = R3FilterTools.getEmptyFilter();
        R3FilterTools.addFilterRule(filter, dbb, predlohaClassname, Filter.AND_OP, 1, PredlohaEntity.f_stavRec, Filter.EQUAL_CRIT, "vyrazeno", 1, false);
        ritPredloha = R3FilterTools.getRecords(context, predlohaClassname, filter, null, null);

        while (ritPredloha.hasMoreRecords()) {
            recPredloha = ritPredloha.nextRecord();
            lRecordsToDelete.add(recPredloha.getKey());
            lRecordsToDeleteCisloID.add(recPredloha.getFieldValue(PredlohaEntity.f_idCislo).toString());
        }

        for (int i = 0; i < lRecordsToDelete.size(); i++) {
            try {
                //odmazani zaznamu DigObjekt
                filterDigObjekt = R3FilterTools.getEmptyFilter();
                idPredloha = lRecordsToDelete.get(i).toString().replace(predlohaClassname + ":", "");
                LOG.log(Level.INFO, " hledam: " + idPredloha);
                R3FilterTools.addFilterRule(filterDigObjekt, dbb, digObjektClassname, Filter.AND_OP, 1, DigObjektEntity.f_rPredloha, Filter.EQUAL_CRIT, idPredloha, 1, false);
                ritDigObjekt = R3FilterTools.getRecords(context, digObjektClassname, filterDigObjekt, null, null);
                pomocnyText = "";
                if (ritDigObjekt.hasMoreRecords()) {
                    LOG.log(Level.SEVERE, " zaznam obsahuje Digitalni objekt. mazu");
                    lRecordsToDeleteDigObjekt = new LinkedList<UniqueKey>();
                    context.addRootDomain(dDigObjekt);
                    while (ritDigObjekt.hasMoreRecords()) {
                        recDigObjekt = ritDigObjekt.nextRecord();
                        lRecordsToDeleteDigObjekt.add(recDigObjekt.getKey());
                    }
                    if (lRecordsToDeleteDigObjekt.size()>=5) {
                        pomocnyText = " (smazano " + lRecordsToDeleteDigObjekt.size() + " zaznamu digObjektu)";
                    } else if (lRecordsToDeleteDigObjekt.size()>=2) {
                        pomocnyText = " (smazany " + lRecordsToDeleteDigObjekt.size() + " zaznamy digObjektu)";
                    } else {
                        pomocnyText = " (smazan " + lRecordsToDeleteDigObjekt.size() + " zaznam digObjektu)";
                    }
                    for (int j = 0; j < lRecordsToDeleteDigObjekt.size(); j++) {
                        LOG.log(Level.SEVERE, " mazu digObjekt: " + lRecordsToDeleteDigObjekt.get(i).toString());
                        context.remove((PlaantUniqueKey)lRecordsToDeleteDigObjekt.get(i));
                    }
                    context.commit();
                }
                //konec odmazani zaznamu

                LOG.log(Level.INFO, "DELETING: " + lRecordsToDelete.get(i).toString() + " - " + lRecordsToDeleteCisloID.get(i) + pomocnyText);
                context.addRootDomain(dPredloha);
                context.remove((PlaantUniqueKey)lRecordsToDelete.get(i));
                context.commit();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "RECORD: " + lRecordsToDelete.get(i), ex);
                Main.sendMail(props.getProperty(Main.PROP_MAIL_SMTPSERVER), props.getProperty(Main.PROP_MAIL_FROM), props.getProperty(Main.PROP_MAIL_ADMINISTRATOR_TO), "EXCEPTION: " + ex.getMessage() + "\n" + "RECORD: " + lRecordsToDelete.get(i), " - ApplicationErrorException");
                context.rollback();
            }
   
        }
        context.close();
        
    }

}
