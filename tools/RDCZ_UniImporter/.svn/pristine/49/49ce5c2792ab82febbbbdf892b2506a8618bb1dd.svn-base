/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *******************************************************************************
 *
 * @author martin
 */
public class Configuration {

    private static final Logger LOG = Logger.getLogger(Configuration.class.getName());
    private static final String CONFIGURATION_FILE = "conf.properties";

    private Properties properties;
    private final String WORKING_DIRECTORY = "xHEAP";
    private final String ARCHIVE_DIRECTORY = "xARCHIVE";
    private final String FAIL_DIRECTORY = "xFAIL";
    private final String LOG_DIRECTORY = "xLOG";

    /**
     * List zdrojů
     */
    public List<OneSource> lSource = new LinkedList<OneSource>();
    public final File F_WORKING_DIRECTORY;
    public final File F_ARCHIVE_DIRECTORY;
    public final File F_FAIL_DIRECTORY;
    public final File F_LOG_DIRECTORY;
    public final int SLEEP_TIME_FILE_NAMEPARSER;
    //public final int SLEEP_TIME_DATA_HARVESTER;
    public final int SLEEP_TIME_DATA_PARSER;
    public final int SLEEP_TIME_DATA_IMPORTER;
    public final int SLEEP_TIME_DATA_LOGGER;

    public final String IMPORT_RELIEF_URL;
    public final String IMPORT_RELIEF_USER;
    public final String IMPORT_RELIEF_PASS;

    public final String IMPORT_DATABASE_URL;
    public final String IMPORT_DATABASE_USER;
    public final String IMPORT_DATABASE_PASS;
    public final boolean IS_LOG_MAIL;
    public final String IMPORT_LOG_MAIL_SMTPSERVER;
    public final String IMPORT_LOG_MAIL_FROM;
    public final String IMPORT_LOG_MAIL_SUBJECT;
    public final boolean IS_LOG_MAIL_ADMINISTRATOR;
    public final String IMPORT_LOG_MAIL_ADMINISTRATOR_TO;
    public final boolean IS_LOG_MAIL_USER;

    private static final Configuration _instance = new Configuration();

    //PROPERTIES KEYS
    /**
     * počet zdrojů
     */
    private static final String KEY_DATASOURCE_COUNT = "DATASOURCE.COUNT";
    private static final String KEY_RELIEF_URL = "IMPORT.RELIEF.URL";
    private static final String KEY_RELIEF_USER = "IMPORT.RELIEF.USER";
    private static final String KEY_RELIEF_PASS = "IMPORT.RELIEF.PASS";
    private static final String KEY_IMPORT_SLEEPTIME_FILE_NAMEPARSER = "IMPORT.SLEEPTIME.FILE.NAMEPARSER";
    //private static final String KEY_IMPORT_SLEEPTIME_DATA_HARVESTER = "IMPORT.SLEEPTIME.DATA.HARVESTER";
    private static final String KEY_IMPORT_SLEEPTIME_DATA_PARSER = "IMPORT.SLEEPTIME.DATA.PARSER";
    private static final String KEY_IMPORT_SLEEPTIME_DATA_IMPORTER = "IMPORT.SLEEPTIME.DATA.IMPORTER";
    private static final String KEY_IMPORT_SLEEPTIME_DATA_LOGGER = "IMPORT.SLEEPTIME.DATA.LOGGER";
    private static final String KEY_IMPORT_DATABASE_URL = "IMPORT.DATABASE.URL";
    private static final String KEY_IMPORT_DATABASE_USER = "IMPORT.DATABASE.USER";
    private static final String KEY_IMPORT_DATABASE_PASS = "IMPORT.DATABASE.PASS";
    private static final String KEY_IMPORT_LOG_MAIL_SMTPSERVER = "IMPORT.LOG.MAIL.SMTPSERVER";
    private static final String KEY_IMPORT_LOG_MAIL_FROM = "IMPORT.LOG.MAIL.FROM";
    private static final String KEY_IMPORT_LOG_MAIL_SUBJECT = "IMPORT.LOG.MAIL.SUBJECT";
    private static final String KEY_IMPORT_LOG_MAIL_ADMINISTRATOR = "IMPORT.LOG.MAIL.ADMINISTRATOR";
    private static final String KEY_IMPORT_LOG_MAIL_ADMINISTRATOR_TO = "IMPORT.LOG.MAIL.ADMINISTRATOR.TO";
    private static final String KEY_IMPORT_LOG_MAIL_USER = "IMPORT.LOG.MAIL.USER";

    /**
     ***************************************************************************
     * Privátní konstruktor
     */
    private Configuration() {
        File fConfiguration;
        FileReader fReader;

        F_WORKING_DIRECTORY = new File(this.WORKING_DIRECTORY);
        F_ARCHIVE_DIRECTORY = new File(this.ARCHIVE_DIRECTORY);
        F_FAIL_DIRECTORY = new File(this.FAIL_DIRECTORY);
        F_LOG_DIRECTORY = new File(this.LOG_DIRECTORY);

        try {
            //Načteme properties ze souboru
            this.properties = new Properties();
            fConfiguration = new File(CONFIGURATION_FILE);
            LOG.log(Level.INFO, "Loading configuration ''{0}''", fConfiguration.getAbsolutePath());
            fReader = new FileReader(fConfiguration);
            this.properties.load(fReader);
        } catch (FileNotFoundException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        } catch (UnsupportedOperationException e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }

        //Sleep time
        this.SLEEP_TIME_FILE_NAMEPARSER = Integer.parseInt(this.properties.getProperty(KEY_IMPORT_SLEEPTIME_FILE_NAMEPARSER, "300000"));
        //this.SLEEP_TIME_DATA_HARVESTER = Integer.parseInt(this.properties.getProperty(KEY_IMPORT_SLEEPTIME_DATA_HARVESTER, "600000"));
        this.SLEEP_TIME_DATA_PARSER = Integer.parseInt(this.properties.getProperty(KEY_IMPORT_SLEEPTIME_DATA_PARSER, "600000"));
        this.SLEEP_TIME_DATA_IMPORTER = Integer.parseInt(this.properties.getProperty(KEY_IMPORT_SLEEPTIME_DATA_IMPORTER, "600000"));
        this.SLEEP_TIME_DATA_LOGGER = Integer.parseInt(this.properties.getProperty(KEY_IMPORT_SLEEPTIME_DATA_LOGGER, "600000"));

        //Načtení hodnot pro připojení do Relief
        this.IMPORT_RELIEF_URL = this.properties.getProperty(KEY_RELIEF_URL);
        this.IMPORT_RELIEF_USER = this.properties.getProperty(KEY_RELIEF_USER);
        this.IMPORT_RELIEF_PASS = this.properties.getProperty(KEY_RELIEF_PASS);

        //Načtení hodnot pro připojení do databáze
        this.IMPORT_DATABASE_URL = this.properties.getProperty(KEY_IMPORT_DATABASE_URL);
        this.IMPORT_DATABASE_USER = this.properties.getProperty(KEY_IMPORT_DATABASE_USER);
        this.IMPORT_DATABASE_PASS = this.properties.getProperty(KEY_IMPORT_DATABASE_PASS);

        if ("1".equals(this.properties.getProperty(KEY_IMPORT_LOG_MAIL_ADMINISTRATOR, "0"))) {
            this.IS_LOG_MAIL_ADMINISTRATOR = true;
        } else {
            this.IS_LOG_MAIL_ADMINISTRATOR = false;
        }

        if ("1".equals(this.properties.getProperty(KEY_IMPORT_LOG_MAIL_USER, "0"))) {
            this.IS_LOG_MAIL_USER = true;
        } else {
            this.IS_LOG_MAIL_USER = false;
        }

        this.IMPORT_LOG_MAIL_FROM = this.properties.getProperty(KEY_IMPORT_LOG_MAIL_FROM);
        this.IMPORT_LOG_MAIL_SUBJECT = this.properties.getProperty(KEY_IMPORT_LOG_MAIL_SUBJECT);
        this.IMPORT_LOG_MAIL_SMTPSERVER = this.properties.getProperty(KEY_IMPORT_LOG_MAIL_SMTPSERVER);
        this.IMPORT_LOG_MAIL_ADMINISTRATOR_TO = this.properties.getProperty(KEY_IMPORT_LOG_MAIL_ADMINISTRATOR_TO);

        //Dopočítávaná proměnná, nenačítá se z properties.
        if (this.IS_LOG_MAIL_ADMINISTRATOR || this.IS_LOG_MAIL_USER) {
            this.IS_LOG_MAIL = true;
        } else {
            this.IS_LOG_MAIL = false;
        }

        //Proběhne validace načtených properties
        propertiesValidation();
    }

    /**
     ***************************************************************************
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public Set<Object> keySet() {
        return properties.keySet();
    }

    /**
     ***************************************************************************
     *
     * @param key
     * @return
     */
    public boolean containsKey(Object key) {
        return properties.containsKey(key);
    }

    /**
     ***************************************************************************
     *
     * @return
     */
    public static Configuration getInstance() {
        return _instance;
    }

    /**
     ***************************************************************************
     * Kontrolije zdali jsou v konfiguračním souboru všechna potřebná nastavení.
     */
    private void propertiesValidation() throws UnsupportedOperationException {
        OneSource source;

        //Kontrola globálních proměných
        if (!F_WORKING_DIRECTORY.canWrite()) {
            LOG.log(Level.INFO, "Creating directory: {0}", F_WORKING_DIRECTORY.getAbsolutePath());
            if (!F_WORKING_DIRECTORY.mkdir()) {
                throw new UnsupportedOperationException("Configuration: Creating directory fail: " + F_WORKING_DIRECTORY.getAbsolutePath());
            }
        }

        if (!F_ARCHIVE_DIRECTORY.canWrite()) {
            LOG.log(Level.INFO, "Creating directory: {0}", F_ARCHIVE_DIRECTORY.getAbsolutePath());
            if (!F_ARCHIVE_DIRECTORY.mkdir()) {
                throw new UnsupportedOperationException("Configuration: Creating directory fail: " + F_ARCHIVE_DIRECTORY.getAbsolutePath());
            }
        }

        if (!F_FAIL_DIRECTORY.canWrite()) {
            LOG.log(Level.INFO, "Creating directory: {0}", F_FAIL_DIRECTORY.getAbsolutePath());
            if (!F_FAIL_DIRECTORY.mkdir()) {
                throw new UnsupportedOperationException("Configuration: Creating directory fail: " + F_FAIL_DIRECTORY.getAbsolutePath());
            }
        }

        if (!F_LOG_DIRECTORY.canWrite()) {
            LOG.log(Level.INFO, "Creating directory: {0}", F_LOG_DIRECTORY.getAbsolutePath());
            if (!F_LOG_DIRECTORY.mkdir()) {
                throw new UnsupportedOperationException("Configuration: Creating directory fail: " + F_LOG_DIRECTORY.getAbsolutePath());
            }
        }

        //RELIEF CONNECTION CHECK
        if (this.IMPORT_RELIEF_URL == null || this.IMPORT_RELIEF_USER == null || this.IMPORT_RELIEF_PASS == null || this.IMPORT_RELIEF_URL.isEmpty() || this.IMPORT_RELIEF_USER.isEmpty() || this.IMPORT_RELIEF_PASS.isEmpty()) {
            throw new UnsupportedOperationException("Configuration: Relief connection check failed.");
        }

        //DATABASE CONNECTION CHECK
        if (this.IMPORT_DATABASE_URL == null || this.IMPORT_DATABASE_USER == null || this.IMPORT_DATABASE_PASS == null || this.IMPORT_DATABASE_URL.isEmpty() || this.IMPORT_DATABASE_USER.isEmpty() || this.IMPORT_DATABASE_PASS.isEmpty()) {
            throw new UnsupportedOperationException("Configuration: Database connection check failed.");
        }

        //LOG MAIL CHECK
        if (this.IS_LOG_MAIL) {
            //Kontorluje se pouze když je tento modul zapnutý
            if (this.IMPORT_LOG_MAIL_SMTPSERVER == null || this.IMPORT_LOG_MAIL_SMTPSERVER.isEmpty()) {
                throw new UnsupportedOperationException("Configuration: SMTP SERVER address failed.");
            }
            if (this.IMPORT_LOG_MAIL_FROM == null || this.IMPORT_LOG_MAIL_FROM.isEmpty() || !this.IMPORT_LOG_MAIL_FROM.contains("@")) {
                throw new UnsupportedOperationException("Configuration: Email from failed. - " + this.IMPORT_LOG_MAIL_FROM);
            }
            if (this.IMPORT_LOG_MAIL_SUBJECT == null || this.IMPORT_LOG_MAIL_SUBJECT.isEmpty()) {
                throw new UnsupportedOperationException("Configuration: Email Subject failed.");
            }
            if (IS_LOG_MAIL_ADMINISTRATOR) {
                if (this.IMPORT_LOG_MAIL_ADMINISTRATOR_TO == null || this.IMPORT_LOG_MAIL_ADMINISTRATOR_TO.isEmpty() || !this.IMPORT_LOG_MAIL_ADMINISTRATOR_TO.contains("@")) {
                    throw new UnsupportedOperationException("Configuration: Administrator's email failed. - " + this.IMPORT_LOG_MAIL_ADMINISTRATOR_TO);
                }
            }
        }

        //Načteme počet zdrojů
        int dataSourceCount;
        try {
            dataSourceCount = Integer.parseInt(this.properties.getProperty(KEY_DATASOURCE_COUNT, "x"));
        } catch (NumberFormatException ex) {
            throw new UnsupportedOperationException("Configuration: Unsupported value for " + Configuration.KEY_DATASOURCE_COUNT + ": " + F_WORKING_DIRECTORY.getAbsolutePath());
        }

        //cyklus načítající zdroje - jeden za druhým
        for (int i = 0; i < dataSourceCount; i++) {
            source = new OneSource();
            source.setState(this.properties.getProperty("DATASOURCE." + (i + 1) + ".SOURCE.STATE"));
            source.setSource(this.properties.getProperty("DATASOURCE." + (i + 1) + ".SOURCE"));
            source.setSleepTime(this.properties.getProperty("DATASOURCE." + (i + 1) + ".SOURCE.SLEEPTIME"));
            source.source_argument1 = this.properties.getProperty("DATASOURCE." + (i + 1) + ".SOURCE.ARGUMENT1");
            source.source_argument2 = this.properties.getProperty("DATASOURCE." + (i + 1) + ".SOURCE.ARGUMENT2");
            source.source_argument3 = this.properties.getProperty("DATASOURCE." + (i + 1) + ".SOURCE.ARGUMENT3");
            source.source_argument4 = this.properties.getProperty("DATASOURCE." + (i + 1) + ".SOURCE.ARGUMENT4");
            source.source_argument5 = this.properties.getProperty("DATASOURCE." + (i + 1) + ".SOURCE.ARGUMENT5");
            source.source_argument6 = this.properties.getProperty("DATASOURCE." + (i + 1) + ".SOURCE.ARGUMENT6");
            source.source_argument7 = this.properties.getProperty("DATASOURCE." + (i + 1) + ".SOURCE.ARGUMENT7");
            source.setSource_parseMethod(this.properties.getProperty("DATASOURCE." + (i + 1) + ".PARSEMETHOD"));
            source.source_parseMethod_argument1 = this.properties.getProperty("DATASOURCE." + (i + 1) + ".PARSEMETHOD.ARGUMENT1");
            source.source_parseMethod_argument2 = this.properties.getProperty("DATASOURCE." + (i + 1) + ".PARSEMETHOD.ARGUMENT2");
            source.source_parseMethod_argument3 = this.properties.getProperty("DATASOURCE." + (i + 1) + ".PARSEMETHOD.ARGUMENT3");
            source.source_parseMethod_argument4 = this.properties.getProperty("DATASOURCE." + (i + 1) + ".PARSEMETHOD.ARGUMENT4");
            source.source_parseMethod_argument5 = this.properties.getProperty("DATASOURCE." + (i + 1) + ".PARSEMETHOD.ARGUMENT5");
            source.source_parseMethod_argument6 = this.properties.getProperty("DATASOURCE." + (i + 1) + ".PARSEMETHOD.ARGUMENT6");
            lSource.add(source);
        }
    }

}
