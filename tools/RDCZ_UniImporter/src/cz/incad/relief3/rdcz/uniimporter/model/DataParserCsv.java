/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter.model;

import cz.incad.commontools.utils.StringUtils;
import cz.incad.relief3.rdcz.uniimporter.model.enums.IssueStateEnum;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *******************************************************************************
 *
 * @author martin
 */
public class DataParserCsv implements DataParser {

    private static final Logger LOG = Logger.getLogger(DataParserCsv.class.getName());
    private static final char MULTI_VALUES_SEPARATOR = ',';
    private static final int COUNT_FIELDS = 29;

    /**
     ***************************************************************************
     *
     * @param oneItem
     * @return
     */
    @Override
    public OneIssue parseIt(OneIssue oneItem) {
        List<OneRecord> lRecords = new LinkedList<OneRecord>();
        List<String> oneLine_splited;
        String encoding = oneItem.source.source_parseMethod_argument1;
        String oneLine_RawData;
        BufferedReader dataSource = null;
        OneRecord oneRecord;
        List<String> wrongCountFields = new LinkedList<String>();
        List<String> wrongData = new LinkedList<String>();
        int countLine = 0;
        //String nazev;
        //String podnazev;

        try {
            dataSource = new BufferedReader(new InputStreamReader(new FileInputStream(oneItem.getFile()), Charset.forName(encoding)));
            //Cyklus načítá z csv souboru řádek za řádkem
            while ((oneLine_RawData = dataSource.readLine()) != null) {
                //Počítadlo řádků
                countLine++;

                //Kontrola na počet sloupců v řádku
                oneLine_splited = StringUtils.split(oneLine_RawData, oneItem.source.source_parseMethod_argument2.charAt(0), true, true);
                if (COUNT_FIELDS == oneLine_splited.size()) {
                    oneRecord = new OneRecord();

                    //Blok parsování
                    try {
                        //Načtené hodnoty z CSV převedeme do Objektu představující jeden záznam pro Relief.
                        oneRecord.setDruhDokumentu(oneLine_splited.get(0));
                        //Číslo ČNB
                        if (oneLine_splited.get(1) != null) {
                            if ("GL".equalsIgnoreCase(oneLine_splited.get(1)) || "ZL".equalsIgnoreCase(oneLine_splited.get(1)) || "ED".equalsIgnoreCase(oneLine_splited.get(1))) {
                                oneRecord.setCisloCNB(null);
                                oneRecord.setSpecDruh(oneLine_splited.get(1).toUpperCase());
                            } else {
                                List<String> cCNB_all;
                                cCNB_all = StringUtils.removeNullFromList(StringUtils.split(oneLine_splited.get(1), DataParserCsv.MULTI_VALUES_SEPARATOR, true, true));
                                List<String> cCNB_nep = new LinkedList<String>();
                                for (int i = 0; i < cCNB_all.size(); i++) {
                                    if (i == 0) {
                                        oneRecord.setCisloCNB(cCNB_all.get(i));
                                    } else {
                                        cCNB_nep.add(cCNB_all.get(i));
                                    }
                                }
                                oneRecord.setCisloCNB_nep(cCNB_nep);
                            }
                        }
                        oneRecord.setIdentifikatorZaznamu(oneLine_splited.get(2));
                        oneRecord.setBaze(oneLine_splited.get(3));
                        oneRecord.setSysno(oneLine_splited.get(4));
                        oneRecord.setAutor(oneLine_splited.get(5));
                        if (oneLine_splited.get(6) != null) {
                            if (oneLine_splited.get(6).indexOf(":") > 0 && oneLine_splited.get(6).indexOf(":") < (oneLine_splited.get(6).length() - 1)) {
                                oneRecord.setNazev(oneLine_splited.get(6).substring(0, (oneLine_splited.get(6).indexOf(":"))));
                                oneRecord.setPodnazev(oneLine_splited.get(6).substring((oneLine_splited.get(6).indexOf(":") + 1), oneLine_splited.get(6).length()));
                            } else {
                                oneRecord.setNazev(oneLine_splited.get(6));
                            }
                        }
                        oneRecord.setRokyVydani(oneLine_splited.get(7));
                        if (oneLine_splited.get(8) != null) {
                            oneRecord.setIsbn(StringUtils.removeNullFromList(StringUtils.split(oneLine_splited.get(8), DataParserCsv.MULTI_VALUES_SEPARATOR, true, true)));
                        }
                        if (oneLine_splited.get(9) != null) {
                            oneRecord.setIssn(StringUtils.removeNullFromList(StringUtils.split(oneLine_splited.get(9), DataParserCsv.MULTI_VALUES_SEPARATOR, true, true)));
                        }
                        oneRecord.setSiglaVlastnika(oneLine_splited.get(10));
                        if (oneLine_splited.get(11) != null) {
                            oneRecord.setVariantniNazev(StringUtils.removeNullFromList(StringUtils.split(oneLine_splited.get(11), DataParserCsv.MULTI_VALUES_SEPARATOR, true, true)));
                        }
                        oneRecord.setZdrojFinancovani(oneLine_splited.get(12));
                        oneRecord.setPoznamky(oneLine_splited.get(13));
                        oneRecord.setUrlNaTitul856(oneLine_splited.get(14));
                        oneRecord.setCarovyKod(oneLine_splited.get(15));
                        oneRecord.setSignatura(oneLine_splited.get(16));
                        oneRecord.setRokPeriodika(oneLine_splited.get(17));
                        oneRecord.setCastDilRocnik(oneLine_splited.get(18));
                        oneRecord.setCisloZakazky(oneLine_splited.get(19));
                        oneRecord.setUrlCastRokRocnik856(oneLine_splited.get(20));
                        oneRecord.setPocetSouboru(oneLine_splited.get(21));
                        if (oneLine_splited.get(22) != null) {
                            if (oneLine_splited.get(22).toLowerCase().contains("1")) {
                                oneRecord.setOcr(true);
                            }
                        }
                        if (oneLine_splited.get(23) != null) {
                            if (oneLine_splited.get(23).toLowerCase().contains("1")) {
                                oneRecord.setOcrSvabach(true);
                            }
                        }
                        oneRecord.setTypSkeneru(oneLine_splited.get(24));
                        oneRecord.setParametrySkenovani(oneLine_splited.get(25));
                        oneRecord.setDpi(oneLine_splited.get(26));
                        oneRecord.setBarevnaHloubka(oneLine_splited.get(27));
                        if (oneLine_splited.get(28) != null) {
                            if (oneLine_splited.get(28).toLowerCase().contains("djvu")) {
                                oneRecord.setDjvu(true);
                            } else {
                                oneRecord.setDjvu(false);
                            }
                            if (oneLine_splited.get(28).toLowerCase().contains("gif")) {
                                oneRecord.setGif(true);
                            } else {
                                oneRecord.setGif(false);
                            }
                            if (oneLine_splited.get(28).toLowerCase().contains("jpeg") || oneLine_splited.get(28).toLowerCase().contains("jpg")) {
                                oneRecord.setJpeg(true);
                            } else {
                                oneRecord.setJpeg(false);
                            }
                            if (oneLine_splited.get(28).toLowerCase().contains("pdf")) {
                                oneRecord.setPdf(true);
                            } else {
                                oneRecord.setPdf(false);
                            }
                            if (oneLine_splited.get(28).toLowerCase().contains("tiff")) {
                                oneRecord.setTiff(true);
                            } else {
                                oneRecord.setTiff(false);
                            }
                            if (oneLine_splited.get(28).toLowerCase().contains("txt")) {
                                oneRecord.setTxt(true);
                            } else {
                                oneRecord.setTxt(false);
                            }
                        }
                        oneRecord.setImportSource(oneItem.getFile().getName());

                        lRecords.add(oneRecord);
                    } catch (Exception ex) {
                        LOG.log(Level.WARNING, "Parse Error.", ex);
                        //Zapíšeme do logu informaci o chybě při parsování
                        wrongData.add("Chyba při parsování dat. Chyba nastala na řádku: " + countLine);
                    }
                } else {
                    //Zapíšeme do logu informaci o špatném počtu sloupců
                    wrongCountFields.add("ŘÁDEK: " + countLine + " - " + oneLine_RawData);
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Chyba při čtení souboru.", ex);
            oneItem.appendLogFailInfo("Chyba při čtení souboru. Chyba nastala na řádku: " + countLine);
            oneItem.issueState = IssueStateEnum._9_readyForLog;
            return oneItem;
        } finally {
            if (dataSource != null) {
                try {
                    dataSource.close();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }

        if (wrongCountFields.isEmpty() && wrongData.isEmpty()) {
            oneItem.lRecords = lRecords;
            oneItem.issueState = IssueStateEnum._2_readyForImport_standard;
        } else {
            //Zapíšeme do issue chyby vzniklé při parzování dat a změníme stav na ready_for_log
            for (int i = 0; i < wrongCountFields.size(); i++) {
                oneItem.appendLogFailInfo(wrongCountFields.get(i));
            }

            for (int i = 0; i < wrongData.size(); i++) {
                oneItem.appendLogFailInfo(wrongData.get(i));
            }
            //Změna stavu issue
            oneItem.issueState = IssueStateEnum._9_readyForLog;
        }
        return oneItem;
    }

}
