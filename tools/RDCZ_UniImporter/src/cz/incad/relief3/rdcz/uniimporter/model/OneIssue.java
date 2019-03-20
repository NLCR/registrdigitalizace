/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter.model;

import cz.incad.commontools.utils.StringUtils;
import cz.incad.relief3.rdcz.uniimporter.model.enums.DataStateEnum;
import cz.incad.relief3.rdcz.uniimporter.model.enums.ImportTypeEnum;
import cz.incad.relief3.rdcz.uniimporter.model.enums.IssueStateEnum;
import cz.incad.relief3.rdcz.uniimporter.utils.OnePrispevatelIdentificator;
import cz.incad.relief3.rdcz.uniimporter.utils.OneSource;
import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 *******************************************************************************
 *
 * @author martin
 */
public class OneIssue {

    private final UUID uuid;
    private final File dataFile;
    private String logSummary = null;
    private String logInfo = null;
    private String logFailInfo = null;
    private String logPropertiesInfo = null;
    public IssueStateEnum issueState = IssueStateEnum._0_readyForParseFileName;
    public DataStateEnum dataState = null;
    public String fileSigla = null;
    //public String dataSigla = null;
    public List<OneRecord> lRecords = null;
    public OnePrispevatelIdentificator onePrispevatelIdentificator = null;
    public OneSource source = null;
    public boolean isTest = false;
    public boolean iskvo = false;
    public ImportTypeEnum imortType = ImportTypeEnum.STANDARD;

    /**
     ***************************************************************************
     *
     * @param file soubor s importními daty
     * @param source
     */
    public OneIssue(File file, OneSource source) {
        this.uuid = UUID.randomUUID();
        this.dataFile = file;
        this.source = source;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public UUID getUUID() {
        return this.uuid;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public File getFile() {
        return this.dataFile;
    }

    /**
     ***************************************************************************
     *
     * @param obj
     * @return
     */
    @Override
    public final boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (!(obj instanceof OneIssue)) {
            return false;
        }
        if (this.hashCode() == obj.hashCode()) {
            return true;
        }
        return false;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    @Override
    public final int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.uuid != null ? this.uuid.hashCode() : 0);
        return hash;
    }

    /**
     ***************************************************************************
     *
     * @param value
     */
    public final void appendLogSummary(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        if (this.logSummary == null) {
            this.logSummary = value;
        } else {
            this.logSummary += StringUtils.LINE_SEPARATOR_WINDOWS + value;
        }
        this.logSummary
                += StringUtils.LINE_SEPARATOR_WINDOWS
                + "---------------------------------------------------"
                + StringUtils.LINE_SEPARATOR_WINDOWS;
    }

    /**
     ***************************************************************************
     *
     * @param value
     */
    public final void appendLogInfo(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        if (this.logInfo == null) {
            this.logInfo = value;
        } else {
            this.logInfo += StringUtils.LINE_SEPARATOR_WINDOWS + value;
        }
        this.logInfo
                += StringUtils.LINE_SEPARATOR_WINDOWS
                + "---------------------------------------------------";
    }

    /**
     ***************************************************************************
     *
     * @param value
     */
    public final void appendLogFailInfo(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        if (this.logFailInfo == null) {
            this.logFailInfo = value;
        } else {
            this.logFailInfo += StringUtils.LINE_SEPARATOR_WINDOWS + value;
        }
        this.logFailInfo
                += StringUtils.LINE_SEPARATOR_WINDOWS
                + "---------------------------------------------------";
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public final String getLogSummary() {
        return this.logSummary;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public final String getLogInfo() {
        return this.logInfo;
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public final String getLogFailInfo() {
        return this.logFailInfo;
    }

    /**
     ***************************************************************************
     *
     * @param value
     */
    public final void appendLogPropertiesInfo(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        if (this.logPropertiesInfo == null) {
            this.logPropertiesInfo = value;
        } else {
            this.logPropertiesInfo += StringUtils.LINE_SEPARATOR_WINDOWS + value;
        }
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public final String getLogPropertiesInfo() {
        return this.logPropertiesInfo;
    }

}
