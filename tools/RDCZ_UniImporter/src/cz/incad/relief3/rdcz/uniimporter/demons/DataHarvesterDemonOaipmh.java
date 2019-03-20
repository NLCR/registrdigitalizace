/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter.demons;

import cz.incad.commontools.utils.StringUtils;
import cz.incad.commontools.utils.oaipmh.Harvester;
import cz.incad.commontools.utils.xml.XMLReader;
import cz.incad.commontools.utils.xml.XMLStatic;
import cz.incad.relief3.rdcz.uniimporter.model.DataHarvesterDemon;
import cz.incad.relief3.rdcz.uniimporter.model.IssueHeap;
import cz.incad.relief3.rdcz.uniimporter.model.OneIssue;
import cz.incad.relief3.rdcz.uniimporter.model.enums.IssueStateEnum;
import cz.incad.relief3.rdcz.uniimporter.utils.Configuration;
import cz.incad.relief3.rdcz.uniimporter.utils.OneSource;
import cz.incad.relief3.rdcz.uniimporter.utils.TimeStamp;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *******************************************************************************
 *
 * @author martin.novacek@incad.cz
 */
public class DataHarvesterDemonOaipmh extends DataHarvesterDemon implements Serializable {

    private static final Logger LOG = Logger.getLogger(DataHarvesterDemonOaipmh.class.getName());
    private String OAIPMH_SERVER_URL;
    private String OAIPMH_COMMAND;
    private String FILE_NAME_STATIC;
    private String ENCODING_INPUT_XML;
    private String ENCODING_OUTPUT_XML;
    private String FROM_FILE;
    private String oaipmhFrom;

    /**
     ***************************************************************************
     *
     * @param source
     */
    public DataHarvesterDemonOaipmh(OneSource source) {
        super(source);
    }

    /**
     ***************************************************************************
     * Metoda kontrolující parametry Source.
     *
     * @return
     */
    @Override
    protected final boolean validateSource() {
        this.OAIPMH_SERVER_URL = this.source.source_argument1;
        this.OAIPMH_COMMAND = this.source.source_argument2;
        this.FILE_NAME_STATIC = this.source.source_argument3;
        this.ENCODING_INPUT_XML = this.source.source_argument4;
        this.ENCODING_OUTPUT_XML = this.source.source_argument5;
        this.FROM_FILE = this.source.source_argument6;

        if (this.OAIPMH_SERVER_URL == null || this.OAIPMH_SERVER_URL.length() == 0) {
            return false;
        }

        if (this.OAIPMH_COMMAND == null || this.OAIPMH_COMMAND.length() == 0) {
            return false;
        }

        if (this.FILE_NAME_STATIC == null || this.FILE_NAME_STATIC.length() == 0) {
            return false;
        }

        if (this.ENCODING_INPUT_XML == null || this.ENCODING_INPUT_XML.length() == 0) {
            return false;
        }

        if (this.ENCODING_OUTPUT_XML == null || this.ENCODING_OUTPUT_XML.length() == 0) {
            return false;
        }

        if (this.FROM_FILE != null) {
            try {
                this.oaipmhFrom = getFromValue();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

    /**
     ***************************************************************************
     * Metoda sklízející data z OAIPMH
     */
    @Override
    public void getData() {
        OneIssue oneIssue;
        Harvester harvester;
        List<File> lFiles = null;
        String fromDateNext;
        BufferedWriter fromOutStream;
        Calendar cal;

        try {
            //Načteme čas ze souboru from
            this.oaipmhFrom = getFromValue();
            //Zapíšeme čas hartvestu pro příští pokus
            cal = new GregorianCalendar();
            fromDateNext = cal.get(Calendar.YEAR) + "-" + StringUtils.getFormatedNumber("", 2, cal.get(Calendar.MONTH) + 1, "") + "-" + StringUtils.getFormatedNumber("", 2, cal.get(Calendar.DAY_OF_MONTH), "") + "T" + StringUtils.getFormatedNumber("", 2, cal.get(Calendar.HOUR_OF_DAY), "") + ":" + StringUtils.getFormatedNumber("", 2, cal.get(Calendar.MINUTE), "") + ":" + StringUtils.getFormatedNumber("", 2, cal.get(Calendar.SECOND), "") + "Z";

            harvester = new Harvester(this.OAIPMH_SERVER_URL, this.OAIPMH_COMMAND + this.oaipmhFrom, Configuration.getInstance().F_WORKING_DIRECTORY.getAbsolutePath(), this.ENCODING_INPUT_XML, this.ENCODING_OUTPUT_XML);
            lFiles = harvester.harvestIt(true, TimeStamp.getTimeStamp() + "_" + this.FILE_NAME_STATIC);
            if (lFiles != null && lFiles.size() > 0) {
                //datum sklizně zapíšeme pouze když se podaří něco úspěšně sklidit
                if (this.FROM_FILE != null && this.FROM_FILE.length() > 0) {
                    fromOutStream = new BufferedWriter(new FileWriter(this.FROM_FILE));
                    fromOutStream.write(fromDateNext);
                    fromOutStream.flush();
                    fromOutStream.close();
                }
            }

        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Harvest data failed.", ex);
        }

        if (lFiles == null || lFiles.size() == 0) {
            LOG.log(Level.INFO, "No harvested data.");
            return;
        }

        //Pozakládáme pro stažené soubory issues
        XMLReader xmlReader = new XMLReader();
        NodeList nlByNameTemp;
        Node nByNameTemp;
        int lengthTemp = 0;
        int lenghtLimit = 0;
        String attributValueTemp;
        Boolean isLimitExceeded = Boolean.FALSE;
        for (int i = 0; i < lFiles.size(); i++) {
            //Pojistka - když se jedná o parciální import, tak zjistit jestli náhodou není přírustek více jak hodnota v parametru.
            try {
                lenghtLimit = Integer.parseInt(source.source_argument7);
            } catch(Throwable ex) {
                lenghtLimit = 0;
            }
            if (i == 0) {
                //Pouze tehdy když se jedná o parciální přírůstek
                try {
                    if (source.source_argument6 != null && source.source_argument6.length() > 0 && lenghtLimit > 0) {
                        xmlReader.loadXml(lFiles.get(i));
                        nlByNameTemp = xmlReader.getByName("resumptionToken");
                        lengthTemp = nlByNameTemp.getLength();
                        if (lengthTemp > 0) {
                            nByNameTemp = nlByNameTemp.item(0);
                            attributValueTemp = XMLStatic.getAttributValue(nByNameTemp, "completeListSize");
                            lengthTemp = Integer.parseInt(attributValueTemp);
                        } else {
                            nlByNameTemp = xmlReader.getByName("record");
                            lengthTemp = nlByNameTemp.getLength();
                        }
                    }

                    
                    if (lengthTemp > lenghtLimit) {
                        isLimitExceeded = Boolean.TRUE;
                    }
                } catch (Throwable ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }

            //Vytvoříme nové OneIssue
            oneIssue = new OneIssue(lFiles.get(i), this.source);
            //Zapíšeme do issue informace o Properties
            oneIssue.appendLogPropertiesInfo(this.source.getInfo());
            if (isLimitExceeded) {
                oneIssue.appendLogFailInfo("Vyjímka při stahování dat. Při praciálním stahování došlo ke stažení většího množství záznamů(" + lengthTemp + "), než je povolený limit (" + Integer.parseInt(source.source_argument7) + "). Záznamy nebyly importovány" );
                oneIssue.issueState = IssueStateEnum._9_readyForLog;
            }
            //Zaregistrujeme nové OneIssue do IssueHeap
            IssueHeap.getInstance().addIssue(oneIssue);
            LOG.log(Level.INFO, "ISSUE ADDED: {0}", lFiles.get(i).getName());
        }
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    private String getFromValue() throws IOException {
        BufferedReader fromInStream;
        String temp;

        if (this.FROM_FILE.length() > 0) {
            fromInStream = new BufferedReader(new FileReader(this.FROM_FILE));
            temp = fromInStream.readLine();
            if (temp == null) {
                return "";
            } else {
                return "&from=" + temp;
            }
        }
        return "";
    }

}
