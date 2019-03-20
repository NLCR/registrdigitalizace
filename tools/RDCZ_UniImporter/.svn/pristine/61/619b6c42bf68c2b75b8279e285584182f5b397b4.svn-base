/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter.utils;

import cz.incad.commontools.utils.StringUtils;
import cz.incad.relief3.rdcz.uniimporter.model.enums.DataSourceEnum;
import cz.incad.relief3.rdcz.uniimporter.model.enums.ParserMethodEnum;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *******************************************************************************
 *
 * @author martin.novacek@incad.cz
 */
public class OneSource {

    private static final Logger LOG = Logger.getLogger(OneSource.class.getName());
    public final String id;
    public Boolean source_state = Boolean.FALSE;
    public int source_sleep_time = 3600000;
    public DataSourceEnum source = null;
    public String source_argument1 = null;
    public String source_argument2 = null;
    public String source_argument3 = null;
    public String source_argument4 = null;
    public String source_argument5 = null;
    public String source_argument6 = null;
    public String source_argument7 = null;
    public ParserMethodEnum source_parseMethod = null;
    public String source_parseMethod_argument1 = null;
    public String source_parseMethod_argument2 = null;
    public String source_parseMethod_argument3 = null;
    public String source_parseMethod_argument4 = null;
    public String source_parseMethod_argument5 = null;
    public String source_parseMethod_argument6 = null;
    //public Boolean isOaiParcial = Boolean.FALSE;
    //TODO implementovat možnost nastavit maximální hodnotu oai setu - velikosti setu
    //použít sourceargument //parseMethod_argument1

    /**
     ***************************************************************************
     *
     */
    public OneSource() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     ***************************************************************************
     *
     * @param value
     */
    public void setSource(String value) {
        if (value == null) {
            this.source = null;
        } else {
            if (DataSourceEnum.DIRECTORY.toString().equalsIgnoreCase(value)) {
                this.source = DataSourceEnum.DIRECTORY;
            } else if (DataSourceEnum.FTP.toString().equalsIgnoreCase(value)) {
                this.source = DataSourceEnum.FTP;
            } else if (DataSourceEnum.OAIPMH.toString().equalsIgnoreCase(value)) {
                this.source = DataSourceEnum.OAIPMH;
            } else if (DataSourceEnum.ZAHORIKWAY.toString().equalsIgnoreCase(value)) {
                this.source = DataSourceEnum.ZAHORIKWAY;
            }
        }
    }

    /**
     ***************************************************************************
     *
     * @param value
     */
    public void setSource_parseMethod(String value) {
        if (value == null) {
            this.source_parseMethod = null;
        } else {
            if (ParserMethodEnum.CSV.toString().equalsIgnoreCase(value)) {
                this.source_parseMethod = ParserMethodEnum.CSV;
            } else if (ParserMethodEnum.MARCXML.toString().equalsIgnoreCase(value)) {
                this.source_parseMethod = ParserMethodEnum.MARCXML;
            } else if (ParserMethodEnum.UNIMARC.toString().equalsIgnoreCase(value)) {
                this.source_parseMethod = ParserMethodEnum.UNIMARC;
            } else if (ParserMethodEnum.MARCXMLOAI.toString().equalsIgnoreCase(value)) {
                this.source_parseMethod = ParserMethodEnum.MARCXMLOAI;
            }
        }
    }

    /**
     ***************************************************************************
     *
     * @param value
     */
    public void setSleepTime(String value) {
        int defaultValue = 3600000;
        int minimalValue = 60000;
        int finalValue = defaultValue;
        int intValue;

        if (value != null) {
            try {
                intValue = Integer.parseInt(value);
                if (intValue > minimalValue) {
                    finalValue = intValue;
                } else {
                    LOG.log(Level.WARNING, "VALUE TO LOW, SET MINIMAL VLAUE: {0}", minimalValue);
                    finalValue = minimalValue;
                }
            } catch (Throwable ex) {
                LOG.log(Level.WARNING, "UNSUPPORTED VALUE SLEEPTIME PROPERTY: {0}. SET DEFAULT VALUE.", value);
            }
        }
        LOG.log(Level.FINE, "SleepTime set: {0}", finalValue);
        this.source_sleep_time = finalValue;
    }

    /**
     ***************************************************************************
     *
     * @param value
     */
    public void setState(String value) {
        if ("1".equals(value)) {
            this.source_state = Boolean.TRUE;
        } else {
            this.source_state = Boolean.FALSE;
            LOG.log(Level.INFO, "SOURCE DISABLED: {0}", this.getInfo());
        }
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public final String getInfo() {
        StringBuilder sb = new StringBuilder(0);
        sb.append("ID: ").append(this.id).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("STATE: ").append(this.source_state).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("DataSource: ").append(this.source).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        //sb.append("SleepTime: ").append(this.source_sleep_time).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        if ((this.source_argument1 != null) && (!"".equals(this.source_argument1))) sb.append("Argument 1: ").append(this.source_argument1).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        //sb.append("Argument 2: ").append(this.source_argument2).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        //sb.append("Argument 3: ").append(this.source_argument3).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        //sb.append("Argument 4: ").append(this.source_argument4).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        if ((this.source_argument5 != null) && (!"".equals(this.source_argument5))) sb.append("Argument 5: ").append(this.source_argument5).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        if ((this.source_argument6 != null) && (!"".equals(this.source_argument6))) sb.append("Argument 6: ").append(this.source_argument6).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        if ((this.source_argument7 != null) && (!"".equals(this.source_argument7))) sb.append("Argument 7: ").append(this.source_argument7).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        sb.append("ParseMethod : ").append(this.source_parseMethod).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        if ((this.source_parseMethod_argument1 != null) && (!"".equals(this.source_parseMethod_argument1))) sb.append("Argument 1: ").append(this.source_parseMethod_argument1).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        if ((this.source_parseMethod_argument2 != null) && (!"".equals(this.source_parseMethod_argument2))) sb.append("Argument 2: ").append(this.source_parseMethod_argument2).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        //sb.append("Argument 3: ").append(this.source_parseMethod_argument3).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        //sb.append("Argument 4: ").append(this.source_parseMethod_argument4).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        //sb.append("Argument 5: ").append(this.source_parseMethod_argument5).append(StringUtils.LINE_SEPARATOR_WINDOWS);
        //sb.append("Argument 6: ").append(this.source_parseMethod_argument6).append(StringUtils.LINE_SEPARATOR_WINDOWS);

        return sb.toString();
    }

}
