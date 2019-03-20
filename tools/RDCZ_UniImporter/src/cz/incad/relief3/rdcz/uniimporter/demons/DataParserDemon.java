/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter.demons;

import cz.incad.relief3.rdcz.uniimporter.model.*;
import cz.incad.relief3.rdcz.uniimporter.model.enums.DataStateEnum;
import cz.incad.relief3.rdcz.uniimporter.model.enums.IssueStateEnum;
import cz.incad.relief3.rdcz.uniimporter.model.enums.ParserMethodEnum;
import cz.incad.relief3.rdcz.uniimporter.utils.Configuration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *******************************************************************************
 *
 * @author martin.novacek@incad.cz
 */
public class DataParserDemon implements Demon {

    private static final Logger LOG = Logger.getLogger(DataParserDemon.class.getName());
    private boolean runAgain = true;

    /**
     ***************************************************************************
     *
     */
    @Override
    public synchronized void run() {
        //Získáme odkaz na uložiště ISSUEs
        IssueHeap issueHeap = IssueHeap.getInstance();
        OneIssue issue = null;

        LOG.log(Level.INFO, "DataParserDemon start working...");
        while (this.runAgain) {
            try {
                //začneme zpracovávat issues
                issue = issueHeap.getOneIssue(IssueStateEnum._1_readyForParseData);
                if (issue != null) {
                    chooseDataParser(issue);
                    //KONTTORLA JESTLI NEJDE NAHODOU O OPRAVNY IMPORT
                    //POKUD ANO TAK MUSIME ZMENIT STAV ISSUE ABY HO ZPRACOVAL SPRAVNY DEMON
                    if (IssueStateEnum._2_readyForImport_standard.compareTo(issue.issueState) == 0 && DataStateEnum.REPAIR0.compareTo(issue.dataState) == 0) {
                        issue.issueState = IssueStateEnum._3_readyForImport_repair_0;
                    }
                    if (IssueStateEnum._2_readyForImport_standard.compareTo(issue.issueState) == 0 && DataStateEnum.REPAIR1.compareTo(issue.dataState) == 0) {
                        issue.issueState = IssueStateEnum._4_readyForImport_repair_1;
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                //ex.printStackTrace();
                issue.appendLogFailInfo("Neočekávaná chyba při parsování dat: " + ex.getMessage());
                issue.issueState = IssueStateEnum._9_readyForLog;
            }

            try {
                LOG.log(Level.FINER, "DataParserDemon goes sleep for {0}", Configuration.getInstance().SLEEP_TIME_DATA_PARSER);
                this.wait(Configuration.getInstance().SLEEP_TIME_DATA_PARSER);
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     ***************************************************************************
     *
     */
    @Override
    public void stop() {
        this.runAgain = false;
    }

    /**
     ***************************************************************************
     *
     * @param issue
     */
    private void chooseDataParser(OneIssue issue) throws UnsupportedOperationException {
        DataParser dataParser;

        if (ParserMethodEnum.CSV.equals(issue.source.source_parseMethod)) {
            dataParser = new DataParserCsv();
        } else if (ParserMethodEnum.MARCXML.equals(issue.source.source_parseMethod)) {
            dataParser = new DataParserMarcXml();
        } else if (ParserMethodEnum.UNIMARC.equals(issue.source.source_parseMethod)) {
            dataParser = new DataParserUniMarcOAI();
        } else if (ParserMethodEnum.MARCXMLOAI.equals(issue.source.source_parseMethod)) {
            dataParser = new DataParserMarcXmlOAI();
        } else {
            throw new UnsupportedOperationException("Unsupported enum value: " + issue.source.source_parseMethod);
        }

        dataParser.parseIt(issue);
    }

}
