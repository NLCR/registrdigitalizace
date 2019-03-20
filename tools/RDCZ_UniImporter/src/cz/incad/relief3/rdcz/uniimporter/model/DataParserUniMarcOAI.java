/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter.model;

import cz.incad.commontools.utils.StringUtils;
import cz.incad.commontools.utils.XmlUtils;
import cz.incad.relief3.rdcz.uniimporter.model.enums.IssueStateEnum;
import cz.incad.relief3.rdcz.uniimporter.utils.Commons;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 *******************************************************************************
 *
 * @author martin
 */
public class DataParserUniMarcOAI implements DataParser {

    private static final Logger LOG = Logger.getLogger(DataParserUniMarcOAI.class.getName());

    /**
     ***************************************************************************
     *
     * @param oneIssue
     * @return
     */
    @Override
    public OneIssue parseIt(OneIssue oneIssue) {
        LOG.log(Level.INFO, "Start Parsing...");
        List<OneRecord> lRecords = new LinkedList<OneRecord>();
        OneRecord oneRecord;
        Document doc;
        NodeList nlRecord;
        NodeList nlRecordChilds;
        NodeList nlRecordMetadata;
        NodeList nl200;
        //NodeList nl246;
        NodeList nl700;
        NodeList nl210;
        NodeList nl225;
        NodeList nl910;
        NodeList nl215;
        NodeList nl010;
        NodeList nl011;
        NodeList nl020;
        //NodeList nl011;
        NodeList nl899;
        NodeList nl911;
        //NodeList nlITM;
        NodeList nlRecordHeader;
        Node nRecord;

        //Bibliograficke udaje
        String leader;
        String druhDokumentu;
        String cisloCNB;
        List<String> cisloCNB_nep;
        String identifikatorZaznamu;
        String sysno;
        String autor;
        String autor_prijmeni;
        String autor_jmeno;
        //String nazev;
        String podnazev;
        String rokyVydani;
        List<String> isbn;
        List<String> isbn_nep;
        List<String> issn;
        List<String> issn_nep;
        List<String> variantniNazvy;
        String siglaVlastnika;
        //String urlNaTitul856;
        String urlNaTitul911;
        String mistoVydani;
        String vydavatel;
        String pocetStran;
        String vyskaKnihy;
        String serieName;
        String seriePart;
        //Exemplar
        //String signatura;
        //String carKod;
        OneITM oneItm;
        //Udaje o digitalizaci
        //String urlCastRokRocnik856;
        String urlCastRokRocnik911;
        List<OneITM> items = new LinkedList<OneITM>();
        String nazev_200a;
        String nazev_200b;
        String nazev_200e;
        String nazev_200h;
        //String nazev_200d;
        String nazev_200i;
        String rawXML = null;
        String url911u;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            doc = factory.newDocumentBuilder().parse(new BufferedInputStream(new FileInputStream(oneIssue.getFile())));
        } catch (ParserConfigurationException ex) {
            LOG.log(Level.SEVERE, null, ex);
            oneIssue.appendLogFailInfo("Soubor nebyl importován. Nastala chyba při parsování souboru. Chyba: " + ex.getMessage());
            oneIssue.issueState = IssueStateEnum._9_readyForLog;
            return oneIssue;
        } catch (SAXException ex) {
            LOG.log(Level.SEVERE, null, ex);
            oneIssue.appendLogFailInfo("Soubor nebyl importován. Nastala chyba při parsování souboru. Chyba: " + ex.getMessage());
            oneIssue.issueState = IssueStateEnum._9_readyForLog;
            return oneIssue;
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            oneIssue.appendLogFailInfo("");
            oneIssue.appendLogFailInfo("Soubor nebyl importován. Nastala chyba při načtení souboru. Chyba: " + ex.getMessage());
            oneIssue.issueState = IssueStateEnum._9_readyForLog;
            return oneIssue;
        }

        //Načteme všechny záznamy Record
        nlRecord = doc.getElementsByTagName("record");
        for (int i = 0; i < nlRecord.getLength(); i++) {
            //Zpracováváme jeden záznam za druhým.
            nl200 = null;
            //nl246       = null;
            nl700 = null;
            nl210 = null;
            nl225 = null;
            nl910 = null;
            nl215 = null;
            nl010 = null;
            nl011 = null;
            nl020 = null;
            //nl011       = null;
            nl899 = null;
            nl911 = null;
            //nlITM       = null;
            nlRecordHeader = null;
            nlRecordMetadata = null;
            //Vynulovani hodnot
            //Bibliograficke udaje
            leader = null;
            druhDokumentu = null;
            cisloCNB = null;
            cisloCNB_nep = null;
            identifikatorZaznamu = null;
            sysno = null;
            autor = null;
            autor_prijmeni = null;
            autor_jmeno = null;
            //nazev                   = null;
            podnazev = null;
            rokyVydani = null;
            isbn = null;
            isbn_nep = new LinkedList<String>();
            issn = null;
            issn_nep = new LinkedList<String>();
            variantniNazvy = null;
            siglaVlastnika = null;
            //urlNaTitul856           = null;
            urlNaTitul911 = null;
            mistoVydani = null;
            vydavatel = null;
            pocetStran = null;
            vyskaKnihy = null;
            serieName = null;
            seriePart = null;
            //Exemplar
            //signatura               = null;
            //carKod                  = null;
            oneItm = null;
            //Udaje o digitalizaci
            items.clear();
            //urlCastRokRocnik856     = null;
            urlCastRokRocnik911 = null;
            nazev_200a = null;
            nazev_200b = null;
            nazev_200e = null;
            nazev_200h = null;
            //nazev_200d              = null;
            nazev_200i = null;
            rawXML = null;
            url911u = null;

            nRecord = nlRecord.item(i);
            oneItm = new OneITM();
            //uložíme XML
            rawXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + getOneRecordXml(nRecord);
            nlRecordChilds = nRecord.getChildNodes();
            for (int j = 0; j < nlRecordChilds.getLength(); j++) {
                //Procházíme jeden prvek Recordu za druhým.
                //Odfiltrujeme jen zajímavé elementy
                if (nlRecordChilds.item(j).getNodeType() == 1) {
                    //SYSNO header - identifier
                    if ("header".equalsIgnoreCase(nlRecordChilds.item(j).getNodeName())) {
                        nlRecordHeader = nlRecordChilds.item(j).getChildNodes();
                        for (int k = 0; k < nlRecordHeader.getLength(); k++) {
                            //Odfiltrujeme jen zajímavé elementy
                            if (nlRecordHeader.item(k).getNodeType() == 1) {
                                if ("identifier".equalsIgnoreCase(nlRecordHeader.item(k).getNodeName())) {
                                    sysno = nlRecordHeader.item(k).getTextContent();
                                }
                            }
                        }
                    }

                    if ("metadata".equalsIgnoreCase(nlRecordChilds.item(j).getNodeName())) {
                        nlRecordMetadata = null;
                        for (int j1 = 0; j1 < nlRecordChilds.item(j).getChildNodes().getLength(); j1++) {
                            if (nlRecordChilds.item(j).getChildNodes().item(j1).getNodeType() == 1) {
                                nlRecordMetadata = nlRecordChilds.item(j).getChildNodes().item(j1).getChildNodes();
                            }
                        }
                        if (nlRecordMetadata == null) {
                            continue;
                        }
                        //nlRecordMetadata = nlRecordChilds.item(j).getChildNodes().item(0).getChildNodes();
                        for (int k = 0; k < nlRecordMetadata.getLength(); k++) {
                            //Odfiltrujeme jen zajímavé elementy
                            if (nlRecordMetadata.item(k).getNodeType() == 1) {
                                //DRUH DOKUMENTU
                                //Leader
                                if ("unimarc:leader".equals(nlRecordMetadata.item(k).getNodeName())) {
                                    leader = nlRecordMetadata.item(k).getTextContent();
                                    druhDokumentu = Commons.getDruhDokumentuFromLeader(leader);
//                                    if (leader.length() > 7) {
//                                        char dkchar = leader.toCharArray()[7];
//                                        if (dkchar == 'm') {
//                                            druhDokumentu = "BK";
//                                        } else if (dkchar == 's') {
//                                            druhDokumentu = "SE";
//                                        } else if (dkchar == 'i') {
//                                            druhDokumentu = "IZ";
//                                        }
//                                    }
                                }

                                //POLE 001
                                if ("001".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag"))) {
                                    identifikatorZaznamu = nlRecordMetadata.item(k).getTextContent();
                                }

                                //200aehd název podnázev díl alternativní název
                                if ("200".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag")) && nl200 == null) {
                                    nl200 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl200.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl200.item(l).getNodeType() == 1) {
                                            //Hlavní název
                                            if ("a".equals(XmlUtils.getAttributValue(nl200.item(l), "code"))) {
                                                nazev_200a = nl200.item(l).getTextContent();
                                            }
                                            // dalsi cleneni (hudebnina/kartografickyDokumnet
                                            if ("b".equals(XmlUtils.getAttributValue(nl200.item(l), "code"))) {
                                                nazev_200b = nl200.item(l).getTextContent();
//                                                LOG.log(Level.INFO, nazev_200b);
                                            }
                                            //Other title information
                                            if ("e".equals(XmlUtils.getAttributValue(nl200.item(l), "code"))) {
                                                nazev_200e = nl200.item(l).getTextContent();
                                            }
                                            //Number of part
                                            if ("h".equals(XmlUtils.getAttributValue(nl200.item(l), "code"))) {
                                                nazev_200h = nl200.item(l).getTextContent();
                                            }
                                            //alternativní název
                                            if ("d".equals(XmlUtils.getAttributValue(nl200.item(l), "code"))) {
                                                if (variantniNazvy == null) {
                                                    variantniNazvy = new LinkedList<String>();
                                                }
                                                variantniNazvy.add(nl200.item(l).getTextContent());
                                            }
                                            //Name of part
                                            if ("i".equals(XmlUtils.getAttributValue(nl200.item(l), "code"))) {
                                                nazev_200i = nl200.item(l).getTextContent();
                                            }
                                        }
                                    }
                                }

                                //Autoři 700ab
                                if ("700".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag")) && nl700 == null) {
                                    nl700 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl700.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl700.item(l).getNodeType() == 1) {
                                            if ("a".equals(XmlUtils.getAttributValue(nl700.item(l), "code"))) {
                                                autor_prijmeni = nl700.item(l).getTextContent();
                                            }
                                            if ("b".equals(XmlUtils.getAttributValue(nl700.item(l), "code"))) {
                                                autor_jmeno = nl700.item(l).getTextContent();
                                            }
                                        }
                                    }
                                }

                                //Vydavatelské údaje acd
                                if ("210".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag")) && nl210 == null) {
                                    nl210 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl210.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl210.item(l).getNodeType() == 1) {
                                            //místo vydání
                                            if ("a".equals(XmlUtils.getAttributValue(nl210.item(l), "code"))) {
                                                mistoVydani = nl210.item(l).getTextContent();
                                            }
                                            //Vydavatel
                                            if ("c".equals(XmlUtils.getAttributValue(nl210.item(l), "code"))) {
                                                vydavatel = nl210.item(l).getTextContent();
                                            }
                                            //Rok vydání
                                            if ("d".equals(XmlUtils.getAttributValue(nl210.item(l), "code"))) {
                                                rokyVydani = nl210.item(l).getTextContent();
                                            }
                                        }
                                    }
                                }

                                //Edice 225 a,v
                                if ("225".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag")) && nl225 == null) {
                                    nl225 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl225.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl225.item(l).getNodeType() == 1) {
                                            //seriename
                                            if ("a".equals(XmlUtils.getAttributValue(nl225.item(l), "code"))) {
                                                serieName = nl225.item(l).getTextContent();
                                            }
                                            //seriepart
                                            if ("v".equals(XmlUtils.getAttributValue(nl225.item(l), "code"))) {
                                                seriePart = nl225.item(l).getTextContent();
                                            }
                                        }
                                    }
                                }

                                //počet stran výška knihy 215ad
                                if ("215".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag")) && nl215 == null) {
                                    nl215 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl215.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl215.item(l).getNodeType() == 1) {
                                            //Počet stran
                                            if ("a".equals(XmlUtils.getAttributValue(nl215.item(l), "code"))) {
                                                pocetStran = nl215.item(l).getTextContent();
                                            }
                                            //Výška knihy
                                            if ("d".equals(XmlUtils.getAttributValue(nl215.item(l), "code"))) {
                                                vyskaKnihy = nl215.item(l).getTextContent();
                                            }
                                        }
                                    }
                                }

                                //910ab - SIGLA, signatura
                                if ("910".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag")) && nl910 == null) {
                                    nl910 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl910.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl910.item(l).getNodeType() == 1) {
                                            if ("a".equals(XmlUtils.getAttributValue(nl910.item(l), "code"))) {
                                                siglaVlastnika = nl910.item(l).getTextContent();
                                            }
                                            if ("b".equals(XmlUtils.getAttributValue(nl910.item(l), "code"))) {
                                                oneItm.c = nl910.item(l).getTextContent();
                                            }
                                        }
                                    }
                                }

                                //čČNB 020a
                                if ("020".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag")) && nl020 == null) {
                                    nl020 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl020.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl020.item(l).getNodeType() == 1) {
                                            //Platné čČNB
                                            if ("b".equals(XmlUtils.getAttributValue(nl020.item(l), "code"))) {
                                                cisloCNB = (nl020.item(l).getTextContent());
                                            }

                                            //Neplatné čČNB
                                            if ("z".equals(XmlUtils.getAttributValue(nl020.item(l), "code"))) {
                                                if (cisloCNB_nep == null) {
                                                    cisloCNB_nep = new LinkedList<String>();
                                                }
                                                cisloCNB_nep.add(nl020.item(l).getTextContent());
                                            }
                                        }
                                    }
                                }

                                //URL 911u
                                if ("911".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag")) && nl911 == null) {
                                    nl911 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl911.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl911.item(l).getNodeType() == 1) {
                                            //URL TITUL
                                            if ("u".equals(XmlUtils.getAttributValue(nl911.item(l), "code"))) {
                                                url911u = nl911.item(l).getTextContent();
                                                //urlNaTitul911 = nl911.item(l).getTextContent();
//                                                if ("SE".equalsIgnoreCase(druhDokumentu)) {
//                                                    urlNaTitul911 = nl911.item(k).getTextContent();
//                                                } else {
//                                                    urlCastRokRocnik911 = nl911.item(k).getTextContent();
//                                                }
                                            }
                                        }
                                    }
                                }

                                //ISBN 010azy
                                if ("010".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag"))) {
                                    nl010 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl010.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl010.item(l).getNodeType() == 1) {
                                            //Platné ISBN
                                            if ("a".equals(XmlUtils.getAttributValue(nl010.item(l), "code"))) {
                                                if (nl010.item(l).getTextContent() != null) {
                                                    if (nl010.item(l).getTextContent().length() > 0) {
                                                        if (StringUtils.canBeStringInteger(nl010.item(l).getTextContent().substring(0, 1))) {
                                                            if (isbn == null) {
                                                                isbn = new LinkedList<String>();
                                                                isbn.add(nl010.item(l).getTextContent());
                                                            } else if (!obsahujeZaznam(isbn_nep, nl010.item(l).getTextContent())) {
                                                                isbn_nep.add(nl010.item(l).getTextContent());
                                                            } 
                                                        }
                                                    }
                                                }
                                            }

                                            //Neplatné ISBN
                                            if ("z".equals(XmlUtils.getAttributValue(nl010.item(l), "code"))) {
                                                if (nl010.item(l).getTextContent() != null) {
                                                    if (nl010.item(l).getTextContent().length() > 0) {
                                                        if (StringUtils.canBeStringInteger(nl010.item(l).getTextContent().substring(0, 1))) {
                                                            if (!obsahujeZaznam(isbn_nep, nl010.item(l).getTextContent())) {
                                                                isbn_nep.add(nl010.item(l).getTextContent());
                                                            } 
                                                        }
                                                    }
                                                }
                                            }

                                            //Neplatné ISBN
                                            if ("y".equals(XmlUtils.getAttributValue(nl010.item(l), "code"))) {
                                                if (nl010.item(l).getTextContent() != null) {
                                                    if (nl010.item(l).getTextContent().length() > 0) {
                                                        if (StringUtils.canBeStringInteger(nl010.item(l).getTextContent().substring(0, 1))) {
                                                            if (!obsahujeZaznam(isbn_nep, nl010.item(l).getTextContent())) {
                                                                isbn_nep.add(nl010.item(l).getTextContent());
                                                            } 
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                //ISSN 011azy
                                if ("011".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag"))) {
                                    nl011 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl011.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl011.item(l).getNodeType() == 1) {
                                            //Platné ISSN
                                            if ("a".equals(XmlUtils.getAttributValue(nl011.item(l), "code"))) {
                                                if (issn == null) {
                                                    issn = new LinkedList<String>();
                                                    issn.add(nl011.item(l).getTextContent());
                                                } else if (!obsahujeZaznam(issn_nep, nl011.item(l).getTextContent())) {
                                                    issn_nep.add(nl011.item(l).getTextContent());
                                                }
                                            }

                                            //Neplatné ISSN
                                            if ("z".equals(XmlUtils.getAttributValue(nl011.item(l), "code"))) {
                                                if (!obsahujeZaznam(issn_nep, nl011.item(l).getTextContent())) {
                                                    issn_nep.add(nl011.item(l).getTextContent());
                                                }
                                            }

                                            //Neplatné ISSN
                                            if ("y".equals(XmlUtils.getAttributValue(nl011.item(l), "code"))) {
                                                if (!obsahujeZaznam(issn_nep, nl011.item(l).getTextContent())) {
                                                    issn_nep.add(nl011.item(l).getTextContent());
                                                }
                                            }
                                        }
                                    }
                                }

                                //CarKod
                                if ("899".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag")) && nl899 == null) {
                                    nl899 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl899.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl899.item(l).getNodeType() == 1) {
                                            if ("p".equals(XmlUtils.getAttributValue(nl899.item(l), "code"))) {
                                                oneItm.b = nl899.item(l).getTextContent();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            items.add(oneItm);

            //Rozparzovaný záznam přidáme do listu
            for (int itemCount = 0; itemCount < items.size(); itemCount++) {
                //Vytvoříme si nový záznam
                oneRecord = new OneRecord();
                //Nastavíme záznamu rozparzované hodnoty
                oneRecord.setDruhDokumentu(druhDokumentu);
                oneRecord.setIdentifikatorZaznamu(identifikatorZaznamu);
                oneRecord.setSysno(sysno);

                //přenastavení druhu dokumentu na Hubebninu či KartografickýDokument, když vyplněno pole 200b
                if (nazev_200b != null) {
//                    LOG.log(Level.INFO, " zmena druhuDokumentu");
                    if ("hudebnina".equals(nazev_200b.substring(0,9).toLowerCase())) {
//                        LOG.log(Level.INFO, " zmena druhuDokumentu - hudebnina");
                        oneRecord.setDruhDokumentu("MU");
                    } else if ("kartografick".equals(nazev_200b.substring(0,12).toLowerCase())) {
//                        LOG.log(Level.INFO, " zmena druhuDokumentu - kartograficky");
                        oneRecord.setDruhDokumentu("MP");
                    }
                }

                //Sestavení podnázvu
                if (nazev_200e != null) {
                    podnazev = nazev_200e;
                } else {
                    podnazev = nazev_200i;
                    if (podnazev != null) {
                        podnazev += " " + nazev_200h;
                    } else {
                        podnazev = nazev_200h;
                    }
                }
                
                //Sestavení autora
                if (autor_prijmeni != null) {
                    autor = autor_prijmeni;
                }
                if (autor_jmeno != null) {
                    if (autor != null) {
                        autor += " " + autor_jmeno;
                    } else {
                        autor = autor_jmeno;
                    }
                }

                //zpracování URL podle druhu dokumentu
                if ("SE".equalsIgnoreCase(druhDokumentu)) {
                    urlNaTitul911 = url911u;
                } else {
                    urlCastRokRocnik911 = url911u;
                }

                oneRecord.setRawXML(rawXML);
                oneRecord.setNazev(nazev_200a);
                oneRecord.setPodnazev(podnazev);
                oneRecord.setAutor(autor);
                oneRecord.setMistoVydani(mistoVydani);
                oneRecord.setVydavatel(vydavatel);
                oneRecord.setRokyVydani(rokyVydani);
                oneRecord.setSiglaVlastnika(siglaVlastnika);
                oneRecord.setPocetStran(pocetStran);
                oneRecord.setVyskaKnihy(vyskaKnihy);
                oneRecord.setCisloCNB(cisloCNB);
                oneRecord.setCisloCNB_nep(cisloCNB_nep);
                oneRecord.setIsbn(isbn);
                oneRecord.setIsbn_nep(isbn_nep);
                oneRecord.setIssn(issn);
                oneRecord.setIssn_nep(issn_nep);
                oneRecord.setVariantniNazev(variantniNazvy);
                //oneRecord.setUrlNaTitul856(urlNaTitul856);
                oneRecord.setUrlNaTitul911(urlNaTitul911);
                //oneRecord.setUrlCastRokRocnik856(urlCastRokRocnik856);
                oneRecord.setUrlCastRokRocnik911(urlCastRokRocnik911);
                oneRecord.setCarovyKod(items.get(itemCount).b);
                oneRecord.setSignatura(items.get(itemCount).c);
                oneRecord.setPoznamkaExemplar(items.get(itemCount).d);
                //oneRecord.setCastDilRocnik(items.get(itemCount).v);
                oneRecord.setCastDilRocnik(nazev_200h);
                oneRecord.setCisloPeriodika(items.get(itemCount).i);
                oneRecord.setRokPeriodika(items.get(itemCount).y);
                oneRecord.setSerieName(serieName);
                oneRecord.setSeriePart(seriePart);
                oneRecord.setImportSource(oneIssue.getFile().getName());
                
                // DEBUG
                LOG.info(oneRecord.allAboutMe());
//                LOG.info(" one entry successfully parsed");
                lRecords.add(oneRecord);
            }
        }

        oneIssue.lRecords = lRecords;
        LOG.log(Level.INFO, "Finish Parsing...");
        //Změna stavu issue
        oneIssue.issueState = IssueStateEnum._2_readyForImport_standard;
        return oneIssue;
        //return null;
    }

    /**
     ***************************************************************************
     *
     * @param sourceNode
     * @return
     */
    private static String getOneRecordXml(Node sourceNode) {
        DocumentBuilder dBuilder = null;
        DocumentBuilderFactory dBuilderF;
        Document oneItemXML;
        Element root;
        Node targetNode;
        StringWriter sw;
        StreamResult result;
        DOMSource source;

        dBuilderF = DocumentBuilderFactory.newInstance();
        try {
            dBuilder = dBuilderF.newDocumentBuilder();
            oneItemXML = dBuilder.newDocument();
            root = oneItemXML.createElement("collection");
            oneItemXML.appendChild(root);
            targetNode = oneItemXML.getFirstChild();
            importNode(targetNode, sourceNode); //Switcher

            //set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            //create string from xml tree
            sw = new StringWriter();
            result = new StreamResult(sw);
            source = new DOMSource(oneItemXML);

            trans.transform(source, result);
            String xmlString = sw.toString();
            return xmlString;
        } catch (ParserConfigurationException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return "";
        } catch (TransformerConfigurationException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return "";
        } catch (TransformerException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return "";
        }
    }

    /**
     ***************************************************************************
     * Simple tree walker that will clone recursively a node. This is to avoid
     * using parser-specific API such as Sun's <tt>changeNodeOwner</tt>
     * when we are dealing with DOM L1 implementations since
     * <tt>cloneNode(boolean)</tt>
     * will not change the owner document.
     * <tt>changeNodeOwner</tt> is much faster and avoid the costly cloning
     * process.
     * <tt>importNode</tt> is in the DOM L2 interface.
     *
     * @param parent the node parent to which we should do the import to.
     * @param child the node to clone recursively. Its clone will be appended to
     * <tt>parent</tt>.
     * @return the cloned node that is appended to <tt>parent</tt>
     */
    public static Node importNode(Node parent, Node child) {
        Node copy = null;
        final Document doc = parent.getOwnerDocument();

        switch (child.getNodeType()) {
            case Node.CDATA_SECTION_NODE:
                copy = doc.createCDATASection(((CDATASection) child).getData());
                break;
            case Node.COMMENT_NODE:
                copy = doc.createComment(((Comment) child).getData());
                break;
            case Node.DOCUMENT_FRAGMENT_NODE:
                copy = doc.createDocumentFragment();
                break;
            case Node.ELEMENT_NODE:
                final Element elem = doc.createElement(((Element) child).getTagName());
                copy = elem;
                final NamedNodeMap attributes = child.getAttributes();
                if (attributes != null) {
                    final int size = attributes.getLength();
                    for (int i = 0; i < size; i++) {
                        final Attr attr = (Attr) attributes.item(i);
                        elem.setAttribute(attr.getName(), attr.getValue());
                    }
                }
                break;
            case Node.ENTITY_REFERENCE_NODE:
                copy = doc.createEntityReference(child.getNodeName());
                break;
            case Node.PROCESSING_INSTRUCTION_NODE:
                final ProcessingInstruction pi = (ProcessingInstruction) child;
                copy = doc.createProcessingInstruction(pi.getTarget(), pi.getData());
                break;
            case Node.TEXT_NODE:
                copy = doc.createTextNode(((Text) child).getData());
                break;
            default:
                // this should never happen
                throw new IllegalStateException("Invalid node type: " + child.getNodeType());
        }

        // okay we have a copy of the child, now the child becomes the parent
        // and we are iterating recursively over its children.
        try {
            final NodeList children = child.getChildNodes();
            if (children != null) {
                final int size = children.getLength();
                for (int i = 0; i < size; i++) {
                    final Node newChild = children.item(i);
                    if (newChild != null) {
                        importNode(copy, newChild);
                    }
                }
            }
        } catch (DOMException ignored) {
        }

        // bingo append it. (this should normally not be done here)
        parent.appendChild(copy);
        return copy;
    }

    private boolean obsahujeZaznam(List<String> list, String value) {
        for (int i = 0; i < list.size(); i++) {
            if (value != null && value.equals(list.get(i))) {
                return true;
            }
        }
        return  false;
    }

}
