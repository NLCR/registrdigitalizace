/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter.model;

import cz.incad.relief3.rdcz.uniimporter.demons.FileNameParserDemon;
import cz.incad.relief3.rdcz.uniimporter.utils.Configuration;
import cz.incad.relief3.rdcz.uniimporter.utils.OneSource;
import cz.incad.relief3.rdcz.uniimporter.utils.TimeStamp;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *******************************************************************************
 *
 * @author martin
 */
public abstract class DataHarvesterDemon implements Demon {

    private static final Logger LOG = Logger.getLogger(DataHarvesterDemon.class.getName());
    protected OneSource source;
    private boolean runAgain;
    public static final String TEMPORARY_DIRECTORY = "XTEMP";

    /**
     ***************************************************************************
     * Konstruktor
     *
     * @param source
     */
    public DataHarvesterDemon(OneSource source) {
        this.source = source;
        this.runAgain = validateSource();
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    protected abstract boolean validateSource();

    /**
     ***************************************************************************
     *
     */
    @Override
    public synchronized void run() {
        while (this.runAgain) {
            try {
                getData();
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Exception: {0}", ex.getMessage());
                this.runAgain = false;
            }

            try {
                LOG.log(Level.FINER, "DataHarvesterDemon goes sleep for {0}", this.source.source_sleep_time);
                this.wait(this.source.source_sleep_time);
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
     */
    public abstract void getData();

    /**
     ***************************************************************************
     *
     * @param sourceFileName
     * @return
     */
    protected static File getDestinationFile(String sourceFileName) {
        return new File(Configuration.getInstance().F_WORKING_DIRECTORY.getAbsolutePath() + File.separator + TimeStamp.getTimeStamp() + FileNameParserDemon.SPLITTER + sourceFileName);
    }

}
