/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter.demons;

import cz.incad.relief3.rdcz.uniimporter.model.DataHarvesterDemon;
import cz.incad.relief3.rdcz.uniimporter.model.IssueHeap;
import cz.incad.relief3.rdcz.uniimporter.model.OneIssue;
import cz.incad.relief3.rdcz.uniimporter.utils.OneSource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 *******************************************************************************
 *
 * @author martin.novacek@incad.cz
 */
public class DataHarvesterDemonFtp extends DataHarvesterDemon implements Serializable {

    private static final Logger LOG = Logger.getLogger(DataHarvesterDemonFtp.class.getName());
    private String URL;
    private int PORT;
    private String LOGIN;
    private String PASS;
    private String DIRECTORY;

    /**
     *************************************************************************
     *
     * @param source
     */
    public DataHarvesterDemonFtp(OneSource source) {
        super(source);
    }

    /**
     *************************************************************************
     * Metoda kontrolující parametry Source.
     *
     * @return
     */
    @Override
    protected final boolean validateSource() {
        this.URL = this.source.source_argument1;
        this.LOGIN = this.source.source_argument3;
        this.PASS = this.source.source_argument4;
        this.DIRECTORY = this.source.source_argument5;

        if (this.URL == null || this.URL.length() == 0) {
            return false;
        }
        if (this.LOGIN == null || this.LOGIN.length() == 0) {
            return false;
        }
        if (this.PASS == null || this.PASS.length() == 0) {
            return false;
        }

        try {
            this.PORT = Integer.parseInt(this.source.source_argument2);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Validation failed.", ex);
            return false;
        }
        return true;
    }

    /**
     *************************************************************************
     * Metoda sklízející data z FTP
     */
    @Override
    public void getData() {
        FTPClient ftp = new FTPClient();
        FTPFile[] listFiles;

        try {
            LOG.log(Level.INFO, "Connect FTP.");
            ftp.connect(URL, PORT);
            ftp.login(LOGIN, PASS);
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.enterLocalPassiveMode();
            LOG.log(Level.INFO, "Connect FTP OK.");
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Connect to FTP failed.", ex);
            return;
        }

        try {
            if (DIRECTORY != null) {
                if (!ftp.changeWorkingDirectory(DIRECTORY)) {
                    throw new IOException();
                }
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Directory not found: {0}", DIRECTORY);
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ex1) {
                }
            }
            return;
        }

        //Harvestování dat
        try {
            LOG.log(Level.INFO, "Harvest data.");
            listFiles = ftp.listFiles();

            for (int i = 0; i < listFiles.length; i++) {
                FTPFile ftpFile = listFiles[i];
                harvestOneFile(ftp, ftpFile);
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Harvest data failed.", ex);
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ex) {
                }
            }
        }
    }

    /**
     ***************************************************************************
     *
     * @param ftp
     * @param ftpFile
     */
    private void harvestOneFile(FTPClient ftp, FTPFile ftpFile) throws IOException {
        File destinationFile = getDestinationFile(ftpFile.getName());
        OneIssue oneIssue;

        //TODO FIX kontrola na to jestli je soubor už kompletní.
        //Dočasné řešení
        //Vložíme sem čekací lhůtu pro případ že by soubor nebyl ještě kompletní tak aby se stihl nahrát
        try {
            this.wait(10000);
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        //překopírujeme soubor do uložiště
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destinationFile));
        ftp.retrieveFile(ftpFile.getName(), bos);
        bos.flush();
        bos.close();

        LOG.log(Level.INFO, "DELETE FILE: {0}", ftpFile.getName());
        ftp.deleteFile(ftpFile.getName());

        //Vytvoříme nové OneIssue
        oneIssue = new OneIssue(destinationFile, this.source);
        //Zapíšeme do issue informace o Properties
        oneIssue.appendLogPropertiesInfo(this.source.getInfo());
        //Zaregistrujeme nové OneIssue do IssueHeap
        IssueHeap.getInstance().addIssue(oneIssue);
    }

}
