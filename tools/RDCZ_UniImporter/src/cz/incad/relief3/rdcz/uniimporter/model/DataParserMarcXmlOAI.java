/* *****************************************************************************
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 *******************************************************************************
 *
 * @author Martin
 */
public class DataParserMarcXmlOAI implements DataParser {

    private static final Logger LOG = Logger.getLogger(DataParserMarcXmlOAI.class.getName());

    /**
     * ************************************************************************
     *
     * @param oneIssue
     * @return
     */
    @Override
    public OneIssue parseIt(OneIssue oneIssue) {
        LOG.log(Level.FINE, "Start Parsing...");
        List<OneRecord> lRecords = new LinkedList<OneRecord>();
        OneRecord oneRecord;
        Document doc;
        NodeList nlRecord;
        NodeList nlRecordChilds;
        NodeList nlRecordMetadata;
        NodeList nl245;
        NodeList nl246;
        NodeList nl100;
        NodeList nl260;
        NodeList nl264;
        NodeList nl490;
        NodeList nl910;
        NodeList nl300;
        NodeList nl015;
        NodeList nl020;
        NodeList nl022;
        NodeList nl024;
        NodeList nl790;
        NodeList nl856;
        NodeList nl911;
        NodeList nlITM;
        Node nRecord;

        //Bibliograficke udaje
        String druhDokumentu;
        String leader;
        String cisloCNB;
        List<String> cisloCNB_nepl;
        String identifikatorZaznamu;
        String sysno;
        String autor;
        String nazev;
        String rokyVydani;
        List<String> isbn;
        List<String> isbn_nep;
        List<String> issn;
        List<String> issn_nep;
        String ismn;
        List<String> variantniNazvy;
        String siglaVlastnika;
        String signaturaVlastnika;
        String urlNaTitul856;
        String urlNaTitul911;
        String tmp856z;
        String tmp911z;
        String mistoVydani;
        String vydavatel;
        String pocetStran;
        String vyskaKnihy;
        String serieName;
        String seriePart;
        String tag008;
        //Exemplar
        //Udaje o digitalizaci
        String urlCastRokRocnik856;
        String urlCastRokRocnik911;
        List<OneITM2> items = new LinkedList<OneITM2>();
        String nazev_245a;
        String nazev_245b;
        //String nazev_245n;
        //String nazev_245p;
        String rawXML = null;
        List<String> l245n = new ArrayList<String>();
        List<String> l245p = new ArrayList<String>();
        int max_np = 0;
        String privazekPole001_790w;
        Boolean dotIsSet = Boolean.FALSE;

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
            nl245 = null;
            nl246 = null;
            nl100 = null;
            nl260 = null;
            nl264 = null;
            nl490 = null;
            nl910 = null;
            nl300 = null;
            nl015 = null;
            nl020 = null;
            nl022 = null;
            nl024 = null;
            nl790 = null;
            nl856 = null;
            nl911 = null;
            nlITM = null;
            //Vynulovani hodnot
            //Bibliograficke udaje
            druhDokumentu = null;
            leader = null;
            cisloCNB = null;
            cisloCNB_nepl = null;
            identifikatorZaznamu = null;
            sysno = null;
            autor = null;
            nazev = null;
            rokyVydani = null;
            isbn = null;
            isbn_nep = new LinkedList<String>();
            issn = null;
            issn_nep = new LinkedList<String>();
            ismn = null;
            variantniNazvy = null;
            siglaVlastnika = null;
            signaturaVlastnika = null;
            urlNaTitul856 = null;
            urlNaTitul911 = null;
            tmp856z = null;
            tmp911z = null;
            mistoVydani = null;
            vydavatel = null;
            pocetStran = null;
            vyskaKnihy = null;
            serieName = null;
            seriePart = null;
            tag008 = null;
            //Exemplar
            //Udaje o digitalizaci
            urlCastRokRocnik856 = null;
            urlCastRokRocnik911 = null;
            items.clear();
            nazev_245a = null;
            nazev_245b = null;
            //nazev_245n              = null;
            //nazev_245p              = null;
            rawXML = null;
            l245n.clear();
            l245p.clear();
            privazekPole001_790w = null;

            nRecord = nlRecord.item(i);
            //uložíme XML
            rawXML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + getOneRecordXml(nRecord);
            nlRecordChilds = nRecord.getChildNodes();
            for (int j = 0; j < nlRecordChilds.getLength(); j++) {
                //Procházíme jeden prvek Recordu za druhým.
                //Odfiltrujeme jen zajímavé elementy
                if (nlRecordChilds.item(j).getNodeType() == 1) {
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
                                //LEADER
                                if ("marc:leader".equalsIgnoreCase(nlRecordMetadata.item(k).getNodeName()) || "leader".equalsIgnoreCase(nlRecordMetadata.item(k).getNodeName())) {
                                    leader = nlRecordMetadata.item(k).getTextContent();
                                }
                                //TAG008
                                if ("008".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag"))) {
                                    tag008 = nlRecordMetadata.item(k).getTextContent();
                                    if (tag008 != null && tag008.length() == 0) {
                                        tag008 = null;
                                    }
                                }
                                //DRUH DOKUMENTU
                                if ("FMT".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag"))) {
                                    druhDokumentu = nlRecordMetadata.item(k).getTextContent();
                                    if (druhDokumentu != null && druhDokumentu.length() == 0) {
                                        druhDokumentu = null;
                                    }
                                }
                                //Workaround pro doplnění druhu dokumentu
                                if (druhDokumentu == null && leader != null) {
                                    druhDokumentu = Commons.getDruhDokumentuFromLeader(leader);
                                }
                                //POLE 001
                                if ("001".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag"))) {
                                    identifikatorZaznamu = nlRecordMetadata.item(k).getTextContent();
                                }
                                //SYSNO
                                if ("SYS".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag"))) {
                                    sysno = nlRecordMetadata.item(k).getTextContent();
                                }
                                //245abnp název podnázev
                                if ("245".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag")) && nl245 == null) {
                                    nl245 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl245.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl245.item(l).getNodeType() == 1) {
                                            if ("a".equals(XmlUtils.getAttributValue(nl245.item(l), "code"))) {
                                                nazev_245a = clear_245a(nl245.item(l).getTextContent());
                                            }
                                            if ("b".equals(XmlUtils.getAttributValue(nl245.item(l), "code"))) {
                                                nazev_245b = clear_245b(nl245.item(l).getTextContent());
                                            }
                                            if ("n".equals(XmlUtils.getAttributValue(nl245.item(l), "code"))) {
                                                l245n.add(clear_245n(nl245.item(l).getTextContent()));
                                            }
                                            if ("p".equals(XmlUtils.getAttributValue(nl245.item(l), "code"))) {
                                                l245p.add(clear_245p(nl245.item(l).getTextContent()));
                                            }
                                        }
                                    }
                                }
                                //variantní názvy 246a
                                if ("246".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag"))) {
                                    nl246 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl246.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl246.item(l).getNodeType() == 1) {
                                            if ("a".equals(XmlUtils.getAttributValue(nl246.item(l), "code"))) {
                                                if (variantniNazvy == null) {
                                                    variantniNazvy = new LinkedList<String>();
                                                }
                                                variantniNazvy.add(nl246.item(l).getTextContent());
                                            }
                                        }
                                    }
                                }
                                //Autoři 100a
                                if ("100".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag")) && nl100 == null) {
                                    nl100 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl100.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl100.item(l).getNodeType() == 1) {
                                            if ("a".equals(XmlUtils.getAttributValue(nl100.item(l), "code"))) {
                                                autor = clear_100a(nl100.item(l).getTextContent());
                                            }
                                        }
                                    }
                                }
                                //Vydavatelské údaje abc
                                if ("260".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag")) && nl260 == null) {
                                    nl260 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl260.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl260.item(l).getNodeType() == 1) {
                                            //místo vydání
                                            if ("a".equals(XmlUtils.getAttributValue(nl260.item(l), "code")) && mistoVydani == null) {
                                                mistoVydani = clear_260a(nl260.item(l).getTextContent());
                                            }
                                            //Vydavatel
                                            if ("b".equals(XmlUtils.getAttributValue(nl260.item(l), "code")) && vydavatel == null) {
                                                vydavatel = clear_260b(nl260.item(l).getTextContent());
                                            }
                                            //Rok vydání
                                            if ("c".equals(XmlUtils.getAttributValue(nl260.item(l), "code")) && rokyVydani == null) {
                                                rokyVydani = clear_260c(nl260.item(l).getTextContent());
                                                if (rokyVydani.length()>2) {
                                                    Boolean prevedNaRp = false;
                                                    LOG.log(Level.SEVERE, "kontrola na rokVydani");
                                                    String rokZacatek = rokyVydani.substring(0,2);
                                                    LOG.log(Level.SEVERE, "kontrola na rokVydani2: " + rokZacatek);
                                                    if (("15".equals(rokZacatek)) || ("16".equals(rokZacatek)) || ("17".equals(rokZacatek))) {
                                                        prevedNaRp = true;
                                                    }
                                                    if ((prevedNaRp) && ((druhDokumentu == null) || ("BK".equalsIgnoreCase(druhDokumentu)))) {
                                                        druhDokumentu = "RP";
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                //Vydavatelské údaje abc
                                if ("264".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag")) && nl264 == null) {
                                    nl264 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl264.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl264.item(l).getNodeType() == 1) {
                                            //místo vydání
                                            if ("a".equals(XmlUtils.getAttributValue(nl264.item(l), "code")) && mistoVydani == null) {
                                                mistoVydani = clear_260a(nl264.item(l).getTextContent());
                                            }
                                            //Vydavatel
                                            if ("b".equals(XmlUtils.getAttributValue(nl264.item(l), "code")) && vydavatel == null) {
                                                vydavatel = clear_260b(nl264.item(l).getTextContent());
                                            }
                                            //Rok vydání
                                            if ("c".equals(XmlUtils.getAttributValue(nl264.item(l), "code")) && rokyVydani == null) {
                                                rokyVydani = clear_260c(nl264.item(l).getTextContent());
                                                if (rokyVydani.length()>2) {
                                                    Boolean prevedNaRp = false;
                                                    LOG.log(Level.SEVERE, "kontrola na rokVydani");
                                                    String rokZacatek = rokyVydani.substring(0,2);
                                                    LOG.log(Level.SEVERE, "kontrola na rokVydani2: " + rokZacatek);
                                                    if (("15".equals(rokZacatek)) || ("16".equals(rokZacatek)) || ("17".equals(rokZacatek))) {
                                                        prevedNaRp = true;
                                                    }
                                                    if ((prevedNaRp) && ((druhDokumentu == null) || ("BK".equalsIgnoreCase(druhDokumentu)))) {
                                                        druhDokumentu = "RP";
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                //Edice 490 a,v
                                if ("490".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag")) && nl490 == null) {
                                    nl490 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl490.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl490.item(l).getNodeType() == 1) {
                                            //seriename
                                            if ("a".equals(XmlUtils.getAttributValue(nl490.item(l), "code"))) {
                                                serieName = nl490.item(l).getTextContent();
                                            }
                                            //seriepart
                                            if ("v".equals(XmlUtils.getAttributValue(nl490.item(l), "code"))) {
                                                seriePart = nl490.item(l).getTextContent();
                                            }
                                        }
                                    }
                                }
                                //SIGLA ind='1' a
                                if ("910".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag")) && nl910 == null) {
                                    nl910 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl910.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl910.item(l).getNodeType() == 1) {
                                            if ("a".equals(XmlUtils.getAttributValue(nl910.item(l), "code"))) {
                                                siglaVlastnika = nl910.item(l).getTextContent();
                                            }
                                            if ("b".equals(XmlUtils.getAttributValue(nl910.item(l), "code"))) {
                                                signaturaVlastnika = nl910.item(l).getTextContent();
                                            }
                                        }
                                    }
                                }
                                //počet stran výška knihy 300ac
                                if ("300".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag")) && nl300 == null) {
                                    nl300 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl300.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl300.item(l).getNodeType() == 1) {
                                            //Počet stran
                                            if ("a".equals(XmlUtils.getAttributValue(nl300.item(l), "code"))) {
                                                pocetStran = clear_300a(nl300.item(l).getTextContent());
                                            }
                                            //Výška knihy
                                            if ("c".equals(XmlUtils.getAttributValue(nl300.item(l), "code"))) {
                                                vyskaKnihy = nl300.item(l).getTextContent();
                                            }
                                        }
                                    }
                                }
                                //čČNB 015a
                                if ("015".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag")) && nl015 == null) {
                                    nl015 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl015.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl015.item(l).getNodeType() == 1) {
                                            //Platné čČNB
                                            if ("a".equals(XmlUtils.getAttributValue(nl015.item(l), "code"))) {
                                                //if (cisloCNB == null) {
                                                //    cisloCNB = new LinkedList<String>();
                                                //}
                                                //cisloCNB.add(nl015.item(k).getTextContent());
                                                cisloCNB = nl015.item(l).getTextContent();
                                            }

                                            //Neplatné čČNB
                                            if ("z".equals(XmlUtils.getAttributValue(nl015.item(l), "code"))) {
                                                if (cisloCNB_nepl == null) {
                                                    cisloCNB_nepl = new LinkedList<String>();
                                                }
                                                cisloCNB_nepl.add(nl015.item(l).getTextContent());
                                            }
                                        }
                                    }
                                }
                                //ISBN 020az
                                if ("020".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag"))) {
                                    nl020 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl020.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl020.item(l).getNodeType() == 1) {
                                            //Platné ISBN
                                            if ("a".equals(XmlUtils.getAttributValue(nl020.item(l), "code"))) {
                                                if (nl020.item(l).getTextContent() != null) {
                                                    if (nl020.item(l).getTextContent().length() > 0) {
                                                        if (StringUtils.canBeStringInteger(nl020.item(l).getTextContent().substring(0, 1))) {
                                                            if (isbn == null) {
                                                                isbn = new LinkedList<String>();
                                                                isbn.add(clear_020(nl020.item(l).getTextContent()));
                                                            } else if (!obsahujeZaznam(isbn_nep, clear_020(nl020.item(l).getTextContent()))) {
                                                                isbn_nep.add(clear_020(nl020.item(l).getTextContent()));
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            //Neplatné ISBN
                                            if ("z".equals(XmlUtils.getAttributValue(nl020.item(l), "code"))) {
                                                if (nl020.item(l).getTextContent() != null) {
                                                    if (nl020.item(l).getTextContent().length() > 0) {
                                                        if (StringUtils.canBeStringInteger(nl020.item(l).getTextContent().substring(0, 1))) {
                                                            if (!obsahujeZaznam(isbn_nep, nl020.item(l).getTextContent())) {
                                                                isbn_nep.add(clear_020(nl020.item(l).getTextContent()));
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            //Neplatné ISBN
                                            if ("y".equals(XmlUtils.getAttributValue(nl020.item(l), "code"))) {
                                                if (nl020.item(l).getTextContent() != null) {
                                                    if (nl020.item(l).getTextContent().length() > 0) {
                                                        if (StringUtils.canBeStringInteger(nl020.item(l).getTextContent().substring(0, 1))) {
                                                            if (!obsahujeZaznam(isbn_nep, nl020.item(l).getTextContent())) {
                                                                isbn_nep.add(clear_020(nl020.item(l).getTextContent()));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                //ISSN 022az
                                if ("022".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag"))) {
                                    nl022 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl022.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl022.item(l).getNodeType() == 1) {
                                            //Platné ISSN
                                            if ("a".equals(XmlUtils.getAttributValue(nl022.item(l), "code"))) {
                                                if (issn == null) {
                                                    issn = new LinkedList<String>();
                                                    issn.add(nl022.item(l).getTextContent());
                                                } else if (!obsahujeZaznam(issn_nep, nl022.item(l).getTextContent())) {
                                                    issn_nep.add(nl022.item(l).getTextContent());
                                                }
                                            }

                                            //Neplatné ISSN
                                            if ("z".equals(XmlUtils.getAttributValue(nl022.item(l), "code"))) {
                                                if (!obsahujeZaznam(issn_nep, nl022.item(l).getTextContent())) {
                                                    issn_nep.add(nl022.item(l).getTextContent());
                                                }
                                            }

                                            //Neplatné ISSN
                                            if ("y".equals(XmlUtils.getAttributValue(nl022.item(l), "code"))) {
                                                if (!obsahujeZaznam(issn_nep, nl022.item(l).getTextContent())) {
                                                    issn_nep.add(nl022.item(l).getTextContent());
                                                }
                                            }
                                        }
                                    }
                                }
                                //ISMN 024a
                                if ("024".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag"))) {
                                    nl024 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl024.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl024.item(l).getNodeType() == 1) {
                                            //Platné ISMN
                                            if ("a".equals(XmlUtils.getAttributValue(nl024.item(l), "code"))) {
                                                ismn = clear_024(nl024.item(l).getTextContent());
                                            }
                                        }
                                    }
                                }
                                //URL 856u
                                if ("856".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag")) && nl856 == null) {
                                    nl856 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl856.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl856.item(l).getNodeType() == 1) {
                                            //URL TITUL
                                            if ("u".equals(XmlUtils.getAttributValue(nl856.item(l), "code"))) {
                                                if ("SE".equalsIgnoreCase(druhDokumentu)) {
                                                    urlNaTitul856 = nl856.item(l).getTextContent();
                                                } else {
                                                    urlCastRokRocnik856 = nl856.item(l).getTextContent();
                                                }
                                            }
                                            if ("z".equals(XmlUtils.getAttributValue(nl856.item(l), "code"))) {
                                                tmp856z = nl856.item(l).getTextContent();
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
                                                if ("SE".equalsIgnoreCase(druhDokumentu)) {
                                                    urlNaTitul911 = nl911.item(l).getTextContent();
                                                } else {
                                                    urlCastRokRocnik911 = nl911.item(l).getTextContent();
                                                }
                                            }
                                            if ("z".equals(XmlUtils.getAttributValue(nl911.item(l), "code"))) {
                                                tmp911z = nl911.item(l).getTextContent();
                                            }
                                        }
                                    }
                                }

                                //Autoři 790w
                                if ("790".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag")) && nl790 == null) {
                                    nl790 = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nl790.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nl790.item(l).getNodeType() == 1) {
                                            if ("w".equals(XmlUtils.getAttributValue(nl790.item(l), "code"))) {
                                                privazekPole001_790w = clear_790w(nl790.item(l).getTextContent());
                                            }
                                        }
                                    }
                                }

                                if ("ITM".equals(XmlUtils.getAttributValue(nlRecordMetadata.item(k), "tag"))) {
                                    OneITM2 oneItm = new OneITM2();

                                    nlITM = nlRecordMetadata.item(k).getChildNodes();
                                    for (int l = 0; l < nlITM.getLength(); l++) {
                                        //Odfiltrujeme jen zajímavé elementy
                                        if (nlITM.item(l).getNodeType() == 1) {
                                            //Čárový kód
                                            if ("b".equals(XmlUtils.getAttributValue(nlITM.item(l), "code"))) {
                                                oneItm.b = nlITM.item(l).getTextContent();
                                            }
                                            //signatura
                                            if ("c".equals(XmlUtils.getAttributValue(nlITM.item(l), "code"))) {
                                                oneItm.c = nlITM.item(l).getTextContent();
                                            }
                                            //poznamka
                                            if ("d".equals(XmlUtils.getAttributValue(nlITM.item(l), "code"))) {
                                                oneItm.d = nlITM.item(l).getTextContent();
                                            }
                                            //svazek rocnik
                                            if ("v".equals(XmlUtils.getAttributValue(nlITM.item(l), "code"))) {
                                                oneItm.v = nlITM.item(l).getTextContent();
                                            }
                                            //cislo
                                            if ("i".equals(XmlUtils.getAttributValue(nlITM.item(l), "code"))) {
                                                oneItm.i = nlITM.item(l).getTextContent();
                                            }
                                            //Rok
                                            if ("y".equals(XmlUtils.getAttributValue(nlITM.item(l), "code"))) {
                                                oneItm.y = nlITM.item(l).getTextContent();
                                            }
                                        }
                                    }
                                    //Připojíme oneItem do pole všech itemů
                                    items.add(oneItm);
                                }
                            }
                        }
                    }
                }
            }

            //Malej podvůdek, když přijde záznam bez exempláře,
            //tak musime vytvořit nafakeovaný exemplář bez hodnot aby se předloha vůbec založila
            if (items.isEmpty()) {
                LOG.log(Level.SEVERE, " záznam nemá vyplněn exemplář.");
                if (signaturaVlastnika == null) {
                    items.add(new OneITM2());
                } else {
                    LOG.log(Level.SEVERE, " záznam nemá vyplněn exemplář - vyplňuji signaturu z pole 910: " + siglaVlastnika);
                    OneITM2 oneItm = new OneITM2();
                    oneItm.c = signaturaVlastnika;
                    items.add(oneItm);
                }
            }

            //Rozparzovaný záznam přidáme do listu
            for (int itemCount = 0; itemCount < items.size(); itemCount++) {
                //Vytvožíme si nový záznam
                oneRecord = new OneRecord();
                //Nastavíme záznamu rozparzované hodnoty
                oneRecord.setDruhDokumentu(druhDokumentu);
                oneRecord.setIdentifikatorZaznamu(identifikatorZaznamu);
                oneRecord.setSysno(sysno);

                nazev = null;
                max_np = l245n.size() >= l245p.size() ? l245n.size() : l245p.size();
                if (nazev_245a != null) {
                    nazev = nazev_245a;
                }

                dotIsSet = Boolean.FALSE;
                for (int j = 0; j < max_np; j++) {
                    try {
                        if (nazev == null) {
                            nazev = l245n.get(j);
                        } else {
                            if (!dotIsSet && nazev_245b != null) {
                                nazev += ".";
                                dotIsSet = Boolean.TRUE;
                            }
                            nazev += " " + l245n.get(j);
                        }
                    } catch (IndexOutOfBoundsException ex) {
                    }

                    try {
                        if (nazev == null) {
                            nazev = l245p.get(j);
                        } else {
                            if (!dotIsSet && nazev_245b != null) {
                                nazev += ".";
                                dotIsSet = Boolean.TRUE;
                            }
                            nazev += " " + l245p.get(j);
                        }
                    } catch (IndexOutOfBoundsException ex) {
                    }
                }

                if (tag008 != null && tag008.length() >= 18 && cisloCNB == null) {
                    if (!"xr".equalsIgnoreCase(tag008.substring(15, 17))) {
                        oneRecord.setSpecDruh("ZL");
                    }
                }

                oneRecord.setRawXML(rawXML);
                oneRecord.setNazev(nazev);
                oneRecord.setPodnazev(nazev_245b);
                oneRecord.setAutor(autor);
                oneRecord.setMistoVydani(mistoVydani);
                oneRecord.setVydavatel(vydavatel);
                oneRecord.setRokyVydani(rokyVydani);
                oneRecord.setSiglaVlastnika(siglaVlastnika);
                oneRecord.setPocetStran(pocetStran);
                oneRecord.setVyskaKnihy(vyskaKnihy);
                oneRecord.setCisloCNB(cisloCNB);
                oneRecord.setCisloCNB_nep(cisloCNB_nepl);
                oneRecord.setIsbn(isbn);
                oneRecord.setIsbn_nep(isbn_nep);
                oneRecord.setIsmn(ismn);
                oneRecord.setIssn_nep(issn_nep);
                oneRecord.setIssn(issn);
                oneRecord.setVariantniNazev(variantniNazvy);
                oneRecord.setUrlNaTitul856(urlNaTitul856);
                oneRecord.setUrlNaTitul911(urlNaTitul911);
                oneRecord.setUrlCastRokRocnik856(urlCastRokRocnik856);
                oneRecord.setValue856z(tmp856z);
                oneRecord.setValue911z(tmp911z);
                oneRecord.setUrlCastRokRocnik911(urlCastRokRocnik911);
                oneRecord.setCarovyKod(items.get(itemCount).b);
                oneRecord.setSignatura(items.get(itemCount).c);
                oneRecord.setPoznamkaExemplar(items.get(itemCount).d);
                oneRecord.setCastDilRocnik(items.get(itemCount).v);
                oneRecord.setCisloPeriodika(items.get(itemCount).i);
                oneRecord.setRokPeriodika(items.get(itemCount).y);
                oneRecord.setSerieName(serieName);
                oneRecord.setSeriePart(seriePart);
                oneRecord.setPrivazekPole001(privazekPole001_790w);
                oneRecord.setImportSource(oneIssue.getFile().getName());

                lRecords.add(oneRecord);
            }
        }

        oneIssue.lRecords = lRecords;
        LOG.log(Level.FINE, "Finish Parsing...");
        //Změna stavu issue
        oneIssue.issueState = IssueStateEnum._2_readyForImport_standard;
        return oneIssue;
    }

    /**
     ***************************************************************************
     *
     * @param value
     * @return
     */
    private static String clear_245a(String value) {
        if (value == null) {
            return null;
        }
        boolean isOk = false;
        while (!isOk) {
            isOk = true;
            value = value.trim();
            if (value.length() > 1 && (value.endsWith(":") || value.endsWith("/") || value.endsWith("="))) {
                value = value.substring(0, value.length() - 1);
                isOk = false;
            }
            value = value.trim();
        }
        return value;
    }

    /**
     ***************************************************************************
     *
     * @param value
     * @return
     */
    private static String clear_245b(String value) {
        if (value == null) {
            return null;
        }
        boolean isOk = false;
        while (!isOk) {
            isOk = true;
            value = value.trim();
            if (value.length() > 1 && (value.endsWith("/"))) {
                value = value.substring(0, value.length() - 1);
                isOk = false;
            }
            value = value.trim();
        }
        return value;
    }

    /**
     ***************************************************************************
     *
     * @param value
     * @return
     */
    private static String clear_245n(String value) {
        if (value == null) {
            return null;
        }
        boolean isOk = false;
        while (!isOk) {
            isOk = true;
            value = value.trim();
            if (value.length() > 1 && (value.endsWith(":") || value.endsWith("/") || value.endsWith("="))) {
                value = value.substring(0, value.length() - 1);
                isOk = false;
            }
            value = value.trim();
        }
        return value;
    }

    /**
     ***************************************************************************
     *
     * @param value
     * @return
     */
    private static String clear_245p(String value) {
        if (value == null) {
            return null;
        }
        boolean isOk = false;
        while (!isOk) {
            isOk = true;
            value = value.trim();
            if (value.length() > 1 && (value.endsWith(":") || value.endsWith("/") || value.endsWith("="))) {
                value = value.substring(0, value.length() - 1);
                isOk = false;
            }
            value = value.trim();
        }
        return value;
    }

    /**
     ***************************************************************************
     *
     * @param value
     * @return
     */
    private static String clear_300a(String value) {
        if (value == null) {
            return null;
        }
        boolean isOk = false;
        while (!isOk) {
            isOk = true;
            value = value.trim();
            if (value.length() > 1 && (value.endsWith(";") || value.endsWith(":"))) {
                value = value.substring(0, value.length() - 1);
                isOk = false;
            }
            value = value.trim();
        }
        return value;
    }

    /**
     ***************************************************************************
     *
     * @param value
     * @return
     */
    private static String clear_260a(String value) {
        if (value == null) {
            return null;
        }
        boolean isOk = false;
        while (!isOk) {
            isOk = true;
            value = value.trim();
            if (value.length() > 1 && (value.endsWith(";") || value.endsWith(":") || value.endsWith(","))) {
                value = value.substring(0, value.length() - 1);
                isOk = false;
            }
            value = value.replace("[", "");
            value = value.replace("]", "");
            value = value.trim();
        }
        return value;
    }

    /**
     ***************************************************************************
     *
     * @param value
     * @return
     */
    private static String clear_260b(String value) {
        if (value == null) {
            return null;
        }
        boolean isOk = false;
        while (!isOk) {
            isOk = true;
            value = value.trim();
            if (value.length() > 1 && (value.endsWith(",") || value.endsWith(";"))) {
                value = value.substring(0, value.length() - 1);
                isOk = false;
            }
            value = value.replace("[", "");
            value = value.replace("]", "");
            value = value.trim();
        }
        return value;
    }

    /**
     ***************************************************************************
     *
     * @param value
     * @return
     */
    private static String clear_100a(String value) {
        if (value == null) {
            return null;
        }
        boolean isOk = false;
        while (!isOk) {
            isOk = true;
            value = value.trim();
            if (value.length() > 1 && (value.endsWith(","))) {
                value = value.substring(0, value.length() - 1);
                isOk = false;
            }
            value = value.trim();
        }
        return value;
    }

    /**
     ***************************************************************************
     *
     * @param value
     * @return
     */
    private static String clear_260c(String value) {
        if (value == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(value);
        boolean isOk = false;
        while (!isOk) {
            isOk = true;
            if (sb.length() > 1 && (sb.indexOf("[") != -1)) {
                sb = sb.deleteCharAt(sb.indexOf("["));
                isOk = false;
            }
            if (sb.length() > 1 && (sb.indexOf("]") != -1)) {
                sb = sb.deleteCharAt(sb.indexOf("]"));
                isOk = false;
            }
            value = value.trim();
        }

        return sb.toString().trim();
    }

    /**
     ***************************************************************************
     * Ořízne příchozí řetězec o " ("
     *
     * @param value
     * @return
     */
    private static String clear_020(String value) {
        String trimmer = "(";
        if (value == null || value.length() == 0) {
            return null;
        }
        value = value.trim();
        if (value.contains(trimmer) && StringUtils.canBeStringInteger(value.substring(0, 1))) {
            value = value.substring(0, value.indexOf(trimmer));
        }
        value = value.trim();
        return value;
    }

    /**
     ***************************************************************************
     * Ořízne příchozí řetězec o " ("
     *
     * @param value
     * @return
     */
    private static String clear_024(String value) {
        String trimmer = "(";
        if (value == null || value.length() == 0) {
            return null;
        }
        value = value.trim();
        if (value.contains(trimmer) && "M".equals(value.substring(0, 1))) {
            value = value.substring(0, value.indexOf(trimmer));
        }
        value = value.trim();
        return value;
    }

    /**
     ***************************************************************************
     *
     * @param value
     * @return
     */
    private static String clear_790w(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        return value;
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
        if (list.size() == 0) {
            return false;
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (value != null && value.equals(list.get(i))) {
                    return true;
                }
            }
            return false;
        }
    }
}

/**
 *******************************************************************************
 *
 * @author martin.novacek@incad.cz
 */
class OneITM2 {

    /**
     * Čárový kód
     */
    public String b = null;
    /**
     * Signatura
     */
    public String c = null;
    /**
     * Poznámka
     */
    public String d = null;
    /**
     * Svazek / ročník
     */
    public String v = null;
    /**
     * Číslo
     */
    public String i = null;
    /**
     * Rok
     */
    public String y = null;

}
