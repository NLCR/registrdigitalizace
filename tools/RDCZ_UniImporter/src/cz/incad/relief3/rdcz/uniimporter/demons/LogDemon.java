/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter.demons;

import com.amaio.plaant.DbBrowser;
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.impl.ExternalContextServer;
import com.amaio.plaant.desk.BinaryData;
import com.amaio.plaant.desk.container.impl.PlaantBinaryValue;
import com.amaio.plaant.metadata.Filter;
import com.amaio.plaant.sync.Domain;
import com.amaio.plaant.sync.Record;
import cz.incad.commontools.utils.FileUtils;
import cz.incad.commontools.utils.mail.SendMail;
import cz.incad.commontools.utils.StringUtils;
import cz.incad.r3tools.R3Commons;
import cz.incad.r3tools.R3FilterTools;
import cz.incad.rd.ImportyEntity;
import cz.incad.rd.PrispevatelEntity;
import cz.incad.relief3.rdcz.uniimporter.model.Demon;
import cz.incad.relief3.rdcz.uniimporter.model.IssueHeap;
import cz.incad.relief3.rdcz.uniimporter.model.OneIssue;
import cz.incad.relief3.rdcz.uniimporter.model.enums.IssueStateEnum;
import cz.incad.relief3.rdcz.uniimporter.utils.Configuration;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.MimeType;

/** ****************************************************************************
 *
 * @author martin.novacek@incad.cz
 */
public class LogDemon implements Demon {
    private static final Logger LOG = Logger.getLogger(LogDemon.class.getName());
    private boolean runAgain = true;

    /** ************************************************************************
     * 
     */
    @Override
    public synchronized void run() {
        //Získáme odkaz na uložiště ISSUEs
        IssueHeap issueHeap = IssueHeap.getInstance();
        OneIssue issue;

        LOG.log(Level.INFO, "LogDemon start working...");
        while(this.runAgain) {
            try {
                //začneme zpracovávat issues
                issue = issueHeap.getOneIssue(IssueStateEnum._9_readyForLog);
                if (issue != null) {
                    logIt(issue, issueHeap);
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Exception: {0}", ex.getMessage());
                //TODO - dodelat nějaké počítadlo vyjímek a popřípadě řešit issue jinak
                this.runAgain = false;
            }

            try {
                LOG.log(Level.FINER, "LogDemon goes sleep for {0}", Configuration.getInstance().SLEEP_TIME_DATA_LOGGER);
                this.wait(Configuration.getInstance().SLEEP_TIME_DATA_LOGGER);
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }

    /** ************************************************************************
     * 
     */
    @Override
    public void stop() {
        this.runAgain = false;
    }

    /** ************************************************************************
     * 
     */
    private void logIt(OneIssue issue, IssueHeap issueHeap) {
        ExternalContextServer context;
        DbBrowser dbb;
        Filter fPrispevatel;
        Domain dImporty;
        Record recImporty;
        Record recPrispevatel;
        RecordsIterator ritPrispevatel;
        String cImporty = R3Commons.getClassnameWithoutEntity(ImportyEntity.class.getName());
        String cPrispevatel = R3Commons.getClassnameWithoutEntity(PrispevatelEntity.class.getName());
        File logFile = new File(Configuration.getInstance().F_LOG_DIRECTORY + File.separator + issue.getFile().getName() + ".log");
        BufferedWriter logFileWriter;
        BinaryData binData;
        StringBuffer mailBody = new StringBuffer(0);
        List<String> lEmails = new LinkedList<String>();

        //Zapíšeme Log do souboru
        try {
            LOG.log(Level.INFO, "Write log to file: {0}", logFile.getName());
            logFileWriter = new BufferedWriter(new FileWriter(logFile));
            if (issue.isTest) {
                logFileWriter.write("=== JEDNÁ SE POUZE O TESTOVACÍ IMPORT - NEBYLY PROVEDENY ZMĚNY V DATECH - LOG Z IMPORTU JE POUZE INFORMATIVNÍ ===" + StringUtils.LINE_SEPARATOR_WINDOWS + StringUtils.LINE_SEPARATOR_WINDOWS + StringUtils.LINE_SEPARATOR_WINDOWS);
            }
            if (issue.getLogSummary() != null) {
                logFileWriter.write(issue.getLogSummary());
            }
            if (issue.getLogFailInfo() != null) {
                logFileWriter.write(issue.getLogFailInfo());
            }
            if (issue.getLogInfo() != null) {
                logFileWriter.write(issue.getLogInfo());
            }
            logFileWriter.flush();
            logFileWriter.close();

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        //Pošleme LOG Mail
        try {
            if (Configuration.getInstance().IS_LOG_MAIL) {
                SendMail sm = new SendMail(Configuration.getInstance().IMPORT_LOG_MAIL_SMTPSERVER, "text/html; charset=\"CP1250\"");
                mailBody.append("V příloze je zpráva (log)  z importu souboru.").append(StringUtils.LINE_SEPARATOR_WINDOWS)
                        .append("Tento mail je generovaný importním programem.").append(StringUtils.LINE_SEPARATOR_WINDOWS).append(StringUtils.LINE_SEPARATOR_WINDOWS).append(StringUtils.LINE_SEPARATOR_WINDOWS)
                        .append("--- Následujících technických dat si netřeba všímat ---").append(StringUtils.LINE_SEPARATOR_WINDOWS)
                        .append(StringUtils.LINE_SEPARATOR_WINDOWS).append(issue.getLogPropertiesInfo());

                if (Configuration.getInstance().IS_LOG_MAIL_ADMINISTRATOR) {
                    lEmails.add(Configuration.getInstance().IMPORT_LOG_MAIL_ADMINISTRATOR_TO);
                }
                try {
                    if (Configuration.getInstance().IS_LOG_MAIL_USER) {
                        if (issue.onePrispevatelIdentificator.email != null) {
                            lEmails.addAll(Arrays.asList(issue.onePrispevatelIdentificator.email.split(";")));
                        }
                    }
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, " chyba při zpracování/přidání emailu přispěvatele.");
                    mailBody.append(StringUtils.LINE_SEPARATOR_WINDOWS).append("Chyba při zpracování/přidání emailu přispěvatele (pravděpodobně neexistence přispěvatele).").append(StringUtils.LINE_SEPARATOR_WINDOWS);
                }

                if (lEmails.size() > 0) {
                    sm.send(Configuration.getInstance().IMPORT_LOG_MAIL_FROM, Configuration.getInstance().IMPORT_LOG_MAIL_SUBJECT, mailBody.toString(), false, lEmails.toArray(new String[lEmails.size()]), null, null, new File[] {logFile});
                } 
            }
        } catch(Exception ex) {
            LOG.log(Level.SEVERE, "Send LOG Mail failed.", ex);
        }

        try {
            //získáme připojení do Relief
            LOG.log(Level.INFO, "Open Relief connection...");
            context = R3Commons.getConnection(Configuration.getInstance().IMPORT_RELIEF_URL, Configuration.getInstance().IMPORT_RELIEF_USER, Configuration.getInstance().IMPORT_RELIEF_PASS);
            dbb = R3Commons.getDbBrowser();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Open Relief connection failed.", ex);
            return;
        }
        LOG.log(Level.INFO, "Open Relief connection OK.");

        dImporty = R3Commons.getDomain(context, cImporty);

        try {
            context.addRootDomain(dImporty);
            //Vytvoříme nový záznam v agendě Importy
            recImporty = context.create(dImporty);

            //Zapíšeme informace o Properties
            recImporty.getSimpleField(ImportyEntity.F_parametry_STR).setValue(issue.getLogPropertiesInfo());

            if (issue.onePrispevatelIdentificator != null) {
                //Najdeme záznam řispěvatele;
                fPrispevatel = R3FilterTools.getEmptyFilter();
                R3FilterTools.addFilterRule(fPrispevatel, dbb, cPrispevatel, Filter.AND_OP, 1, PrispevatelEntity.DLISTS_value, Filter.EQUAL_CRIT, issue.onePrispevatelIdentificator.value, 1, false);
                ritPrispevatel = R3FilterTools.getRecords(context, cPrispevatel, fPrispevatel, null, null);
                if (ritPrispevatel.hasMoreRecords()) {
                    recPrispevatel = ritPrispevatel.nextRecord();
                    recImporty.getReferencedField(ImportyEntity.F_rPrispevatel_REF).setKey(recPrispevatel.getKey());
                } else {
                    //nenašli jsme přispěvatele.
                    LOG.log(Level.WARNING, "Prispevatel not found. Value: {0}, Sigla: {1}", new Object[]{issue.onePrispevatelIdentificator.value, issue.onePrispevatelIdentificator.sigla});
                    recImporty.getSimpleField(ImportyEntity.RECORD_poznRec).setValue("Prispevatel nenalezen. Value: " + issue.onePrispevatelIdentificator.value + ", Sigla: " + issue.onePrispevatelIdentificator.sigla);
                }
            } else {
            }

            if (issue.getFile() != null) {
                //Připojíme k záznamu Importu soubor s příchozími daty
                binData = new BinaryData(new MimeType("application/octet-stream"), issue.getFile().getName(), FileUtils.readFiletoByteArray(issue.getFile()));
                recImporty.getBinaryField(ImportyEntity.F_prichoziData_BIN).setBinaryValue(new PlaantBinaryValue(binData));
            }

            //Připojíme k záznamu Importu soubor s LOGy.
            binData = new BinaryData(new MimeType("application/octet-stream"), logFile.getName(), FileUtils.readFiletoByteArray(logFile));
            recImporty.getBinaryField(ImportyEntity.F_logData_BIN).setBinaryValue(new PlaantBinaryValue(binData));

            context.commit();
            
            //Commit proběhl, odstraníme OneIssue z IssueHeap
            LOG.log(Level.INFO, "Velikost IssueHeap: {0}", issueHeap.size());
            issueHeap.removeIssue(issue);
            LOG.log(Level.INFO, "Velikost IssueHeap: {0}", issueHeap.size());
            if (issue.getLogFailInfo() != null) {
                //Issue bylo s chybami
                issue.getFile().renameTo(new File(Configuration.getInstance().F_FAIL_DIRECTORY + File.separator + issue.getFile().getName()));
            } else {
                //Issue bylo bez chyb
                issue.getFile().renameTo(new File(Configuration.getInstance().F_ARCHIVE_DIRECTORY + File.separator + issue.getFile().getName()));
            }

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Nepodarilo se vytvorit zaznam v agende Importy.", ex);
            context.rollback();
        } finally {
            //Uzavřeme připojení do relief
            LOG.log(Level.INFO, "Close Relief connection...");
            dbb = null;
            if (context != null) {
                context.close();
            }
        }
    }

}
