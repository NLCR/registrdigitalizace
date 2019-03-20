/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter.model;

import cz.incad.commontools.utils.StringUtils;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *******************************************************************************
 *
 * @author martin
 */
public class OneRecord {

    private static final Logger LOG = Logger.getLogger(OneRecord.class.getName());
    //souhr vsech polí

    //Bibliograficke udaje
    private String druhDokumentu = null;
    private String leader = null;
    private String tag008 = null;
    private String cisloCNB = null;
    private List<String> cisloCNB_nep = null;
    private String identifikatorZaznamu = null;//např Pole001
    private String baze = null;
    private String sysno = null;
    private String autor = null;
    private String nazev = null;
    private String podnazev = null;
    private String rokyVydani = null;
    private List<String> isbn = null;
    private List<String> isbn_nep = null;
    private String ismn = null;
    private List<String> issn = null;
    private List<String> issn_nep = null;
    private String siglaVlastnika = null;
    private List<String> variantniNazev = null;
    private String zdrojFinancovani = null;
    private String poznamky = null;
    private String urlNaTitul856 = null;
    private String urlNaTitul911 = null;
    private String mistoVydani = null;
    private String vydavatel = null;
    private String pocetStran = null;
    private String vyskaKnihy = null;
    private String specDruh = null;
    private String serieName = null;
    private String seriePart = null;
    private String privazek790w = null;

    //Exemplar
    private String carovyKod = null;
    private String signatura = null;
    private String rokPeriodika = null;
    private String castDilRocnik = null;
    private String cisloZakazky = null;
    private String poznamkaExemplar = null;
    private String cisloPeriodika = null;

    //Udaje o digitalizaci
    private String urlCastRokRocnik856 = null;
    private String urlCastRokRocnik911 = null;
    private String value856z = null;
    private String value911z = null;
    private String pocetSouboru = null;
    private boolean ocr = false;
    private boolean ocrSvabach = false;
    private String typSkeneru = null;
    private String parametrySkenovani = null;
    private String dpi = null;
    private String barevnaHloubka = null;

    private boolean djvu = false;
    private boolean gif = false;
    private boolean jpeg = false;
    private boolean pdf = false;
    private boolean tiff = false;
    private boolean txt = false;
    private String rawXML = null;

    private String importSource = null;

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getDruhDokumentu() {
        return this.druhDokumentu;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getLeader() {
        return this.leader;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getTag008() {
        return this.tag008;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getCisloCNB() {
        return this.cisloCNB;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public List<String> getCisloCNB_nep() {
        return this.cisloCNB_nep;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public List<String> getCisloCNB_all() {
        List<String> list = new LinkedList<String>();
        if (getCisloCNB() != null) {
            list.add(getCisloCNB());
        }

        if (getCisloCNB_nep() != null) {
            for (int i = 0; i < getCisloCNB_nep().size(); i++) {
                list.add(getCisloCNB_nep().get(i));
            }
        }

        if (list.isEmpty()) {
            return null;
        }
        return list;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getIdentifikatorZaznamu() {
        return this.identifikatorZaznamu;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getBaze() {
        return this.baze;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getSysno() {
        return this.sysno;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getAutor() {
        return this.autor;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getNazev() {
        return this.nazev;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getPodnazev() {
        return this.podnazev;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getRokyVydani() {
        return this.rokyVydani;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public List<String> getIsbn() {
        return this.isbn;
    }
    
    /**
     ***************************************************************************
     *
     * @return
     */
    public List<String> getIsbn_Nep() {
        return this.isbn_nep;
    }
    
    /**
     ***************************************************************************
     *
     * @return
     */
    public String getIsbn_Nep(int id) {
        return this.isbn_nep.get(id);
    }
    
    /**
     ***************************************************************************
     *
     * @return
     */
    public List<String> getIsbn_All() {
        List<String> list = new LinkedList<String>();
        if (getIsbn() != null) {
            list.addAll(getIsbn());
        }
        if (getIsbn_Nep() != null) {
            list.addAll(getIsbn_Nep());
        }
        if (list.isEmpty()) {
            return null;
        }
        return list;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getIsmn() {
        return this.ismn;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public List<String> getIssn() {
        return issn;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public List<String> getIssn_Nep() {
        return this.issn_nep;
    }
    
    /**
     ***************************************************************************
     *
     * @return
     */
    public String getIssn_Nep(int id) {
        return this.issn_nep.get(id);
    }
    
    /**
     ***************************************************************************
     *
     * @return
     */
    public List<String> getIssn_All() {
        List<String> list = new LinkedList<String>();
        if (getIssn() != null) {
            list.addAll(getIssn());
        }
        if (getIssn_Nep() != null) {
            list.addAll(getIssn_Nep());
        }
        if (list.isEmpty()) {
            return null;
        }
        return list;
    }
    
    /**
     ***************************************************************************
     *
     * @return
     */
    public String getSiglaVlastnika() {
        return this.siglaVlastnika;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public List<String> getVariantniNazev() {
        return this.variantniNazev;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getZdrojFinancovani() {
        return this.zdrojFinancovani;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getPoznamky() {
        return this.poznamky;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getUrlNaTitul856() {
        return this.urlNaTitul856;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getUrlNaTitul911() {
        return this.urlNaTitul911;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getMistoVydani() {
        return this.mistoVydani;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getVydavatel() {
        return this.vydavatel;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getPocetStran() {
        return this.pocetStran;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getVyskaKnihy() {
        return this.vyskaKnihy;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getSpecDruh() {
        return this.specDruh;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getSerieName() {
        return this.serieName;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getSeriePart() {
        return this.seriePart;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getPrivazekPole001() {
        return this.privazek790w;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getCarovyKod() {
        return this.carovyKod;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getSignatura() {
        return this.signatura;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getRokPeriodika() {
        return this.rokPeriodika;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getCastDilRocnik() {
        return this.castDilRocnik;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getCisloZakazky() {
        return this.cisloZakazky;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getPoznamkaExemplar() {
        return this.poznamkaExemplar;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getCisloPeriodika() {
        return this.cisloPeriodika;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getUrlCastRokRocnik856() {
        return this.urlCastRokRocnik856;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getUrlCastRokRocnik911() {
        return this.urlCastRokRocnik911;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getValue856z() {
        return this.value856z;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getValue911z() {
        return this.value911z;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getPocetSouboru() {
        return this.pocetSouboru;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public boolean getOcr() {
        return this.ocr;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public boolean getOcrSvabach() {
        return this.ocrSvabach;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getTypSkeneru() {
        return this.typSkeneru;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getParametrySkenovani() {
        return this.parametrySkenovani;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getDpi() {
        return this.dpi;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getBarevnaHloubka() {
        return this.barevnaHloubka;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public boolean getDjvu() {
        return this.djvu;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public boolean getGif() {
        return this.gif;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public boolean getJpeg() {
        return this.jpeg;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public boolean getPdf() {
        return this.pdf;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public boolean getTiff() {
        return this.tiff;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public boolean getTxt() {
        return this.txt;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getRawXML() {
        return this.rawXML;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getImportSource() {
        return this.importSource;
    }

//-----------------------------------------------------------------------------------------    
//-----------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------
    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setDruhDokumentu(String value) {
        this.druhDokumentu = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setLeader(String value) {
        this.leader = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setTag008(String value) {
        this.tag008 = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setCisloCNB(String value) {
        this.cisloCNB = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setCisloCNB_nep(List<String> value) {
        if (value != null) {
            if (!value.isEmpty()) {
                this.cisloCNB_nep = new LinkedList<String>();
                for (String one : value) {
                    this.cisloCNB_nep.add(one);
                }
            }
        }
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setIdentifikatorZaznamu(String value) {
        this.identifikatorZaznamu = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setBaze(String value) {
        this.baze = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setSysno(String value) {
        this.sysno = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setAutor(String value) {
        if (value != null) {
            value = value.trim();
            if (value.endsWith(":") || value.endsWith("/")) {
                if (value.length() > 2) {
                    value = value.substring(0, value.length() - 2);
                }
            }
            value = value.trim();
        }
        this.autor = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setNazev(String value) {
        if (value != null) {
            value = value.trim();
            if (value.endsWith(":") || value.endsWith("/")) {
                if (value.length() > 2) {
                    value = value.substring(0, value.length() - 1);
                }
            }
            value = value.trim();
        }

        this.nazev = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setVariantniNazev(List<String> value) {
        if (value != null && !value.isEmpty()) {
            this.variantniNazev = new LinkedList<String>();
            for (String one : value) {
                this.variantniNazev.add(one);
            }
        }
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void addVariantniNazev(List<String> value) {
        if (value != null && !value.isEmpty()) {
            if (this.variantniNazev == null) {
                this.variantniNazev = new LinkedList<String>();
            }
            for (String one : value) {
                this.variantniNazev.add(one);
            }
        }
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void addVariantniNazev(String value) {
        if (value != null && !value.isEmpty()) {
            if (this.variantniNazev == null) {
                this.variantniNazev = new LinkedList<String>();
            }
            this.variantniNazev.add(value);
        }
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setRokyVydani(String value) {
        if (value != null) {
            value = value.trim();
            if (value.endsWith(":") || value.endsWith("/")) {
                if (value.length() > 2) {
                    value = value.substring(0, value.length() - 2);
                }
            }
            value = value.trim();
            this.rokyVydani = value;
        }
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setIsbn(List<String> value) {
        if (value != null) {
            if (!value.isEmpty()) {
                this.isbn = new LinkedList<String>();
                for (String one : value) {
                    this.isbn.add(one);
                }
            }
        }
    }
    
    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setIsbn_nep(List<String> value) {
        if (value != null) {
            if (!value.isEmpty()) {
                this.isbn_nep = new LinkedList<String>();
                for (String one : value) {
                    this.isbn_nep.add(one);
                }
            }
        }
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setIsmn(String value) {
        this.ismn = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setIssn(List<String> value) {
        if (value != null) {
            if (!value.isEmpty()) {
                this.issn = new LinkedList<String>();
                for (String one : value) {
                    this.issn.add(one);
                }
            }
        }
    }
    
    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setIssn_nep(List<String> value) {
        if (value != null) {
            if (!value.isEmpty()) {
                this.issn_nep = new LinkedList<String>();
                for (String one : value) {
                    this.issn_nep.add(one);
                }
            }
        }
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setSiglaVlastnika(String value) {
        this.siglaVlastnika = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setPodnazev(String value) {
        if (value != null) {
            value = value.trim();
        }
        this.podnazev = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setZdrojFinancovani(String value) {
        this.zdrojFinancovani = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setPoznamky(String value) {
        this.poznamky = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setUrlNaTitul856(String value) {
        this.urlNaTitul856 = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setUrlNaTitul911(String value) {
        this.urlNaTitul911 = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setMistoVydani(String value) {
        if (value != null) {
            value = value.trim();
            if (value.endsWith(":") || value.endsWith("/")) {
                if (value.length() > 2) {
                    value = value.substring(0, value.length() - 2);
                }
            }
            value = value.trim();
        }
        this.mistoVydani = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setVydavatel(String value) {
        if (value != null) {
            value = value.trim();
            if (value.endsWith(":") || value.endsWith("/")) {
                if (value.length() > 2) {
                    value = value.substring(0, value.length() - 2);
                }
            }
            value = value.trim();
        }
        this.vydavatel = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setPocetStran(String value) {
        this.pocetStran = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setVyskaKnihy(String value) {
        this.vyskaKnihy = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setSpecDruh(String value) {
        this.specDruh = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setSerieName(String value) {
        this.serieName = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setSeriePart(String value) {
        this.seriePart = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setPrivazekPole001(String value) {
        this.privazek790w = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setCarovyKod(String value) {
        this.carovyKod = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setSignatura(String value) {
        this.signatura = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setRokPeriodika(String value) {
        this.rokPeriodika = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setCastDilRocnik(String value) {
        this.castDilRocnik = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setCisloZakazky(String value) {
        this.cisloZakazky = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setPoznamkaExemplar(String value) {
        this.poznamkaExemplar = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setCisloPeriodika(String value) {
        this.cisloPeriodika = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setUrlCastRokRocnik856(String value) {
        this.urlCastRokRocnik856 = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setUrlCastRokRocnik911(String value) {
        this.urlCastRokRocnik911 = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setValue856z(String value) {
        this.value856z = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setValue911z(String value) {
        this.value911z = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setPocetSouboru(String value) {
        this.pocetSouboru = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setOcr(boolean value) {
        this.ocr = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setOcrSvabach(boolean value) {
        this.ocrSvabach = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setTypSkeneru(String value) {
        this.typSkeneru = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setParametrySkenovani(String value) {
        this.parametrySkenovani = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setDpi(String value) {
        this.dpi = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setBarevnaHloubka(String value) {
        this.barevnaHloubka = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setDjvu(boolean value) {
        this.djvu = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setGif(boolean value) {
        this.gif = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setJpeg(boolean value) {
        this.jpeg = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setPdf(boolean value) {
        this.pdf = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setTiff(boolean value) {
        this.tiff = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setTxt(boolean value) {
        this.txt = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setRawXML(String value) {
        this.rawXML = value;
    }

    /**
     * ************************************************************************
     *
     * @param value
     */
    public void setImportSource(String value) {
        this.importSource = value;
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getPrijmeniAutora() {
        String value = getAutor();
        StringBuilder exit = new StringBuilder(0);

        if (value == null) {
            return null;
        }

        for (int i = 0; i < value.length(); i++) {
            if (Character.isLetterOrDigit(value.charAt(i))) {
                exit.append(value.charAt(i));
            } else if (exit.length() != 0) {
                return exit.toString();
            }
        }

        return exit.toString();
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String getNazevDoDvojteckyNeboLomitka() {
        String value = getNazev();
        if (value == null) {
            return null;
        }
        StringBuilder exit = new StringBuilder(0);

        value = value.trim();
        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == 47 || value.charAt(i) == 58) {
                return exit.toString();
            } else {
                exit.append(value.charAt(i));
            }
        }

        return exit.toString();
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(0);
        sb.append(StringUtils.LINE_SEPARATOR_WINDOWS);

        sb.append("SIGLA:           ").append(getSiglaVlastnika()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("Pole001:         ").append(getIdentifikatorZaznamu()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("čČNB:            ").append(getCisloCNB()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("ISBN:            ").append(getIsbn()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("ISSN:            ").append(getIssn()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("Čár. kód:        ").append(getCarovyKod()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("Signatura:       ").append(getSignatura()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("Název:           ").append(getNazev()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("Autor:           ").append(getAutor()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("Rok(y) vydání:   ").append(getRokyVydani()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("Rok periodika:   ").append(getRokPeriodika()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("Část/Díl/Ročník: ").append(getCastDilRocnik()).append(StringUtils.LINE_SEPARATOR_WINDOWS);

        return sb.toString();
    }

    /**
     * ************************************************************************
     *
     * @return
     */
    public String allAboutMe() {
        StringBuilder sb = new StringBuilder(0);

        sb.append("druhDokumentu: ").append(this.druhDokumentu).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("leader: ").append(this.leader).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("cisloCNB: ").append(this.cisloCNB).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        if (this.cisloCNB_nep != null) {
            sb.append("List cisloCNB Neplatné: ").append(this.cisloCNB_nep.size()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        } else {
            sb.append("List cisloCNB Neplatné: null").append(StringUtils.LINE_SEPARATOR_WINDOWS);
        }

        sb.append("identifikatorZaznamu: ").append(this.identifikatorZaznamu).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("baze: ").append(this.baze).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("sysno: ").append(this.sysno).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("autor: ").append(this.autor).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("nazev: ").append(this.nazev).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("podnazev: ").append(this.podnazev).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("rokyVydani: ").append(this.rokyVydani).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        if (this.isbn != null) {
            sb.append("List isbn: ").append(this.isbn.size()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        } else {
            sb.append("List isbn: null").append(StringUtils.LINE_SEPARATOR_WINDOWS);
        }
        if (this.isbn_nep != null) {
            sb.append("List isbn neplatné: ").append(this.isbn_nep.size()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        } else {
            sb.append("List isbn neplatne: null").append(StringUtils.LINE_SEPARATOR_WINDOWS);
        }
        if (this.issn != null) {
            sb.append("List issn: ").append(this.issn.size()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        } else {
            sb.append("List issn: null").append(StringUtils.LINE_SEPARATOR_WINDOWS);
        }
        if (this.issn_nep != null) {
            sb.append("List issn neplatné: ").append(this.issn_nep.size()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        } else {
            sb.append("List issn neplatne: null").append(StringUtils.LINE_SEPARATOR_WINDOWS);
        }
        sb.append("siglaVlastnika: ").append(this.siglaVlastnika).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        if (this.variantniNazev != null) {
            sb.append("List variantniNazev: ").append(this.variantniNazev.size()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        } else {
            sb.append("List variantniNazev: null").append(StringUtils.LINE_SEPARATOR_WINDOWS);
        }
        sb.append("zdrojFinancovani: ").append(this.zdrojFinancovani).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("poznamky: ").append(this.poznamky).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("urlNaTitul856: ").append(this.urlNaTitul856).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("urlNaTitul911: ").append(this.urlNaTitul911).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("mistoVydani: ").append(this.mistoVydani).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("vydavatel: ").append(this.vydavatel).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("pocetStran: ").append(this.pocetStran).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("vyskaKnihy: ").append(this.vyskaKnihy).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("carovyKod: ").append(this.carovyKod).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("signatura: ").append(this.signatura).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("rokPeriodika: ").append(this.rokPeriodika).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("castDilRocnik: ").append(this.castDilRocnik).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("cisloZakazky: ").append(this.cisloZakazky).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("poznamkaExemplar: ").append(this.poznamkaExemplar).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("cisloPeriodika: ").append(this.cisloPeriodika).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("urlCastRokRocnik856: ").append(this.urlCastRokRocnik856).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("urlCastRokRocnik911: ").append(this.urlCastRokRocnik911).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("pocetSouboru: ").append(this.pocetSouboru).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("ocr: ").append(this.ocr).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("ocrSvabach: ").append(this.ocrSvabach).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("typSkeneru: ").append(this.typSkeneru).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("parametrySkenovani: ").append(this.parametrySkenovani).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("dpi: ").append(this.dpi).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("barevnaHloubka: ").append(this.barevnaHloubka).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("djvu: ").append(this.djvu).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("gif: ").append(this.gif).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("jpeg: ").append(this.jpeg).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("pdf: ").append(this.pdf).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("tiff: ").append(this.tiff).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("txt: ").append(this.txt).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("importSource: ").append(this.importSource).append(StringUtils.LINE_SEPARATOR_WINDOWS);

        return sb.toString();
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public String getInfo() {
        String exit = "\n";

        exit += "SYSNO: " + this.getSysno() + "\n";
        exit += "POLE001: " + this.getIdentifikatorZaznamu() + "\n";
        exit += "CARKOD: " + this.getCarovyKod() + "\n";
        exit += "SGNATURA: " + this.getSignatura() + "\n";
        exit += "CCNB: " + this.getCisloCNB() + "\n";
        //exit += ": " + this.get() + "\n";

        return exit;
    }

}
