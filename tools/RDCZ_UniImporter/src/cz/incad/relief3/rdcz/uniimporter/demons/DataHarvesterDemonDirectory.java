/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter.demons;

import cz.incad.relief3.rdcz.uniimporter.model.DataHarvesterDemon;
import cz.incad.relief3.rdcz.uniimporter.model.IssueHeap;
import cz.incad.relief3.rdcz.uniimporter.model.OneIssue;
import cz.incad.relief3.rdcz.uniimporter.utils.OneSource;
import java.io.File;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *******************************************************************************
 *
 * @author martin
 */
public class DataHarvesterDemonDirectory extends DataHarvesterDemon implements Serializable {

    private static final Logger LOG = Logger.getLogger(DataHarvesterDemonDirectory.class.getName());
    private File SOURCE_DIRECTORY;

    /**
     * ************************************************************************
     *
     * @param source
     */
    public DataHarvesterDemonDirectory(OneSource source) {
        super(source);
    }

    /**
     * ************************************************************************
     * Metoda kontrolující parametry Source.
     *
     * @return
     */
    @Override
    protected final boolean validateSource() {
        String sourceDirectoryPath = this.source.source_argument1;

        if (sourceDirectoryPath == null || sourceDirectoryPath.length() == 0) {
            return false;
        }

        this.SOURCE_DIRECTORY = new File(this.source.source_argument1);

        //Kontrolujeme jestli zadaná cesta je adresářem
        if (!this.SOURCE_DIRECTORY.isDirectory()) {
            return false;
        }

        //kontrolujeme jestli máme v adresáři právo na čtení
        if (!this.SOURCE_DIRECTORY.canRead()) {
            return false;
        }

        return true;
    }

    /**
     ***************************************************************************
     *
     */
    @Override
    public void getData() {
        LOG.log(Level.INFO, "DataHarvesterDemonDirectory start harvesting...");
        File sourceDirectory = new File(this.source.source_argument1);
        File destinationFile;
        OneIssue oneIssue;

        for (File sourceFile : sourceDirectory.listFiles()) {
            // Odfiltrujeme pouze soubory
            if (sourceFile.isFile()) {
                // Soubory přesuneme do pracovní složky se změněným jménem a zaregistrujeme do IssueHeap.
                destinationFile = getDestinationFile(sourceFile.getName());
                LOG.log(Level.INFO, "Move file {0} -> {1}", new Object[]{sourceFile.getAbsolutePath(), destinationFile.getAbsolutePath()});
                sourceFile.renameTo(destinationFile);
                //Vytvoříme nové OneIssue
                oneIssue = new OneIssue(destinationFile, this.source);
                //Zapíšeme do issue informace o Properties
                oneIssue.appendLogPropertiesInfo(this.source.getInfo());
                //Zaregistrujeme nové OneIssue do IssueHeap
                IssueHeap.getInstance().addIssue(oneIssue);
            }
        }
    }

}
