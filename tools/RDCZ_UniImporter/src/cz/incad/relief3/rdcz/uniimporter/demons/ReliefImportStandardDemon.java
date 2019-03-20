/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter.demons;

import com.amaio.plaant.DbBrowser;
import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.impl.ExternalContextServer;
import com.amaio.plaant.desk.QueryException;
import com.amaio.plaant.desk.container.PlaantUniqueKey;
import com.amaio.plaant.metadata.Filter;
import com.amaio.plaant.sync.Domain;
import com.amaio.plaant.sync.Record;
import cz.incad.commontools.utils.StringUtils;
import cz.incad.r3tools.R3Commons;
import cz.incad.r3tools.R3FilterTools;
import cz.incad.rd.*;
import cz.incad.relief3.rdcz.uniimporter.model.ImportListener;
import cz.incad.relief3.rdcz.uniimporter.model.IssueHeap;
import cz.incad.relief3.rdcz.uniimporter.model.OneIssue;
import cz.incad.relief3.rdcz.uniimporter.model.OneRecord;
import cz.incad.relief3.rdcz.uniimporter.model.OneXmlPart;
import cz.incad.relief3.rdcz.uniimporter.model.enums.DataStateEnum;
import cz.incad.relief3.rdcz.uniimporter.model.enums.IssueStateEnum;
import cz.incad.relief3.rdcz.uniimporter.model.enums.ParserMethodEnum;
import cz.incad.relief3.rdcz.uniimporter.utils.Commons;
import cz.incad.relief3.rdcz.uniimporter.utils.Configuration;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *******************************************************************************
 *
 * @author martin
 */
public class ReliefImportStandardDemon implements ImportListener {

    private static final Logger LOG = Logger.getLogger(ReliefImportStandardDemon.class.getName());
    private boolean runAgain = true;
    private final String dedicatedSigla;

    /**
     ***************************************************************************
     *
     * @param dedicatedSigla
     */
    public ReliefImportStandardDemon(String dedicatedSigla) {
        this.dedicatedSigla = dedicatedSigla;
    }

    /**
     ***************************************************************************
     *
     */
    @Override
    public synchronized void run() {
        LOG.log(Level.INFO, "ReliefImportDemon Standard start working...");
        //Získáme odkaz na uložiště ISSUEs
        IssueHeap issueHeap = IssueHeap.getInstance();
        OneIssue issue = null;

        //Přidáme startovací timeout, aby sšichni démoni nestartovali najednou.
        try {
            Long firstSleep;
            firstSleep = new Long((int) (Math.random() * 10000));
            LOG.log(Level.FINE, "FIRST SLEEP: {0}", firstSleep);
            this.wait(firstSleep);
        } catch (InterruptedException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        while (this.runAgain) {
            try {
                //začneme zpracovávat issues

                if (this.dedicatedSigla != null) {
                    issue = issueHeap.getOneIssue(IssueStateEnum._2_readyForImport_standard, this.dedicatedSigla);
                } else {
                    issue = issueHeap.getOneIssue(IssueStateEnum._2_readyForImport_standard);
                }
                if (issue != null) {
                    issue.issueState = IssueStateEnum._5_importing;
                    ImportIt(issue);
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, null, ex);
                if (issue != null) {
                    issue.appendLogFailInfo("Vyjímka při importování dat: " + ex.getMessage());
                    issue.issueState = IssueStateEnum._9_readyForLog;
                }
            }

            try {
                LOG.log(Level.FINER, "ReliefImportDemon goes sleep for {0}", Configuration.getInstance().SLEEP_TIME_DATA_IMPORTER);
                this.wait(Configuration.getInstance().SLEEP_TIME_DATA_IMPORTER);
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
     * @param issue
     */
    @Override
    public void ImportIt(OneIssue issue) {
        LOG.log(Level.INFO, "Start Import ISSUE: {0}", issue.getUUID());
        ExternalContextServer ctx = null;
        DbBrowser dbb = null;
        int countOdmitnuteZaznamy = 0;
        int countCreateZaznamy = 0;
        int countUpdateZaznamy = 0;
        int countRevizeZaznamy = 0;
        int countRevize2Zaznamy = 0;
        int countAddRAWDataFail = 0;
        int actualRecord = 0;
        OneRecordImportState state;
        /* List záznamů, které mají být smazány */
        List<PlaantUniqueKey> lPKeysToDelete = new LinkedList<PlaantUniqueKey>();
        List<OneXmlPart> lXmlPart = new LinkedList<OneXmlPart>();
        Domain dPredloha;
        Domain dNepISSN;
        Domain dNepISBN;
        Domain dNepCCNB;
        Domain dTabVarNazev;

        //List<OneRecord> lRecordsOdmitnute = null; //Marek
        //List<String> lRecordsOdmitnuteString = null;//Marek
        
        String URL = Configuration.getInstance().IMPORT_RELIEF_URL;
        String LOGIN = Configuration.getInstance().IMPORT_RELIEF_USER;
        String PASS = Configuration.getInstance().IMPORT_RELIEF_PASS;

        //Kontorla příchozích dat, jestli je vůbec co importovat, než se připojíme do Relief.
        if (issue.lRecords == null || issue.lRecords.isEmpty()) {
            LOG.log(Level.WARNING, "SOUBOR NEMA ZADNA IMPORTNI DATA: {0}", issue.getFile().getAbsolutePath());
            issue.appendLogFailInfo("Nebyly nalezeny žádné záznamy ke zpracování.");
            issue.issueState = IssueStateEnum._9_readyForLog;
            return;
        }

        try {
            LOG.info("Open Relief connection...");
            ctx = R3Commons.getConnection(URL, LOGIN, PASS);

            LOG.info("Get DB Browser...");
            dbb = R3Commons.getDbBrowser();
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Open Relief connection failed.", ex);
            dbb = null;
            if (ctx != null) {
                ctx.close();
            }
            ctx = null;
            LOG.log(Level.INFO, "Finish Import ISSUE: {0}", issue.getUUID());
            issue.issueState = IssueStateEnum._2_readyForImport_standard;
            return;
        }
        LOG.info("Relief connection opened...");

        //připravíme si proměné s agendami
        dPredloha = R3Commons.getDomain(ctx, R3Commons.getClassnameWithoutEntity(PredlohaEntity.class.getName()));
        dNepISSN = R3Commons.getDomain(ctx, R3Commons.getClassnameWithoutEntity(NepISSNEntity.class.getName()));
        dNepISBN = R3Commons.getDomain(ctx, R3Commons.getClassnameWithoutEntity(NepISBNEntity.class.getName()));
        dNepCCNB = R3Commons.getDomain(ctx, R3Commons.getClassnameWithoutEntity(NepCCNBEntity.class.getName()));
        dTabVarNazev = R3Commons.getDomain(ctx, R3Commons.getClassnameWithoutEntity(TabVarNazevEntity.class.getName()));

        try {
            for (int i = 0; i < issue.lRecords.size(); i++) {
                //vyčištění seznamu záznamů ke smazání
                lPKeysToDelete.clear();
                //vyčištění seznamu na doplneni RAW XML
                lXmlPart.clear();
                actualRecord = i + 1;
                LOG.log(
                        Level.INFO,
                        "STARTING IMPORT STANDARD: {0} - {1} - {2}/{3} - {4}",
                        new Object[]{
                            issue.getUUID(),
                            issue.getFile().getName(),
                            i,
                            issue.lRecords.size(),
                            issue.lRecords.get(i).getInfo()
                        }
                );
                state = doOneRecord(
                        issue.lRecords.get(i),
                        ctx,
                        dbb,
                        issue,
                        actualRecord,
                        lPKeysToDelete,
                        lXmlPart
                );
                if (issue.isTest) {
                    ctx.rollback();
                    //System.gc();
                } else {
                    ctx.commit();
                    //System.gc();
                }
                //počítadlo
                switch (state) {
                    case CREATE:
                        countCreateZaznamy++;
                        break;
                    case UPDATE:
                        countUpdateZaznamy++;
                        break;
                    case REVIZE:
                        countRevizeZaznamy++;
                        countCreateZaznamy++;
                        break;
                    case REVIZE_VLASTNI:
                        countRevize2Zaznamy++;
                        countCreateZaznamy++;
                        break;
                    case CANCELED:
                        countOdmitnuteZaznamy++;
                        LOG.log(Level.INFO, "Odmitnuty zaznam.");
                        //lRecordsOdmitnuteString.add(issue.lRecords.get(i).getCisloCNB()); //Marek
                        //lRecordsOdmitnute.add(issue.lRecords.get(i)); //Marek
                        break;
                }
                //Smažeme záznamy, které jsme se v průběhu procesu rozhodli smazat
                if (!issue.isTest) {
                    for (int j = 0; j < lPKeysToDelete.size(); j++) {
                        try {
                            LOG.log(Level.SEVERE, "Mažu nepotřebný záznam: " + lPKeysToDelete.get(j));
                            ctx.addRootDomain(dNepISSN);
                            ctx.addRootDomain(dNepISBN);
                            ctx.addRootDomain(dNepCCNB);
                            ctx.addRootDomain(dTabVarNazev);
                            ctx.remove(lPKeysToDelete.get(j));
                            if (issue.isTest) {
                                ctx.rollback();
                            } else {
                                ctx.commit();
                            }
                        } catch (ApplicationErrorException ex) {
                            LOG.log(Level.SEVERE, "Chyba při mazání nepotřebného záznamu: " + lPKeysToDelete.get(j), ex);
                            issue.appendLogFailInfo("Chyba ři mazání nepotřebného záznam: " + lPKeysToDelete.get(j));
                            ctx.rollback();
                        }
                    }

                    //Doplnění XML části k záznamům
                    Record recPredloha;
                    Filter filter;
                    RecordsIterator rit;

                    for (int j = 0; j < lXmlPart.size(); j++) {
                        try {
                            LOG.log(Level.SEVERE, "Doplňuji XML části k záznamům: " + lXmlPart.get(j));
                            ctx.addRootDomain(dPredloha);
                            filter = R3FilterTools.getEmptyFilter();
                            if (lXmlPart.get(j).idCislo != null) {
                                LOG.log(Level.FINE, "Try Attach RAW XML data Externaly: {0}", lXmlPart.get(j).idCislo);
                                R3FilterTools.addFilterRule(
                                        filter,
                                        dbb,
                                        dPredloha.getQName(),
                                        Filter.AND_OP,
                                        1,
                                        PredlohaEntity.f_idCislo,
                                        Filter.EQUAL_CRIT,
                                        lXmlPart.get(j).idCislo,
                                        1,
                                        Boolean.FALSE
                                );
                            }
                            rit = R3FilterTools.getRecords(ctx, dPredloha.getQName(), filter, null, null);
                            if (rit != null && rit.getRecordsCount() == 1) {
                                recPredloha = rit.nextRecord();
                                recPredloha.getSimpleField(PredlohaEntity.f_xml).setValue(lXmlPart.get(j).xml);
                            } else {
                                countAddRAWDataFail++;
                                LOG.log(Level.SEVERE, "Chyba pri doplnovani XML: \n{0}\n{1}", new Object[]{lXmlPart.get(j).idCislo, lXmlPart.get(j).xml});
                                issue.appendLogFailInfo("Chyba při doplňování XML Záznam nenalezen: " + lXmlPart.get(j).idCislo);
                            }

                            if (issue.isTest) {
                                ctx.rollback();
                            } else {
                                ctx.commit();
                            }
                        } catch (Exception ex) {
                            countAddRAWDataFail++;
                            LOG.log(Level.SEVERE, "Chyba pri doplnovani XML: \n" + lXmlPart.get(j).idCislo + "\n" + lXmlPart.get(j).xml, ex);
                            issue.appendLogFailInfo("Chyba při doplňování XML: " + lXmlPart.get(j).idCislo);
                            ctx.rollback();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Chyba při zpracování záznamů.", ex);
            issue.appendLogFailInfo("Chyba ři zpracování dat, chyba nastala u záznamu číslo: " + actualRecord);
            ctx.rollback();
        } finally {
            LOG.log(Level.INFO, "Finish Import ISSUE: {0}", issue.getUUID());
            //Zapíšeme summary log o Importu
            issue.appendLogSummary(
                    "Celkem záznamů:                        " + issue.lRecords.size() + StringUtils.LINE_SEPARATOR_WINDOWS
                    + "Nově založených záznamů:               " + countCreateZaznamy + StringUtils.LINE_SEPARATOR_WINDOWS
                    + "Aktualizovaných záznamů:               " + countUpdateZaznamy + StringUtils.LINE_SEPARATOR_WINDOWS
                    + "Odmítnutých záznamů:                   " + countOdmitnuteZaznamy + StringUtils.LINE_SEPARATOR_WINDOWS
                    + "Z nových k revizi - vlastní duplicity: " + countRevize2Zaznamy + StringUtils.LINE_SEPARATOR_WINDOWS
                    + "Z nových k revizi - cizí duplicity:    " + countRevizeZaznamy + StringUtils.LINE_SEPARATOR_WINDOWS
                    + "Záznamů bez RAW dat (XML,CSV):         " + countAddRAWDataFail
            );

            issue.issueState = IssueStateEnum._9_readyForLog;
            LOG.info("Close Relief connection...");
            dbb = null;
            if (ctx != null) {
                ctx.close();
            }
            ctx = null;
        }
    }

    /**
     ***************************************************************************
     *
     * @param oneRec
     * @param state
     * @param ctx
     * @param dbb
     * @return
     */
    private OneRecordImportState doOneRecord(
            OneRecord oneRec,
            ExternalContextServer ctx,
            DbBrowser dbb,
            OneIssue issue,
            int actualRecord,
            List<PlaantUniqueKey> lPKeysToDelete,
            List<OneXmlPart> lXmlPart) {

        OneRecordImportState oneRecordImportState;
        RecordsIterator rit;
        Domain dPredloha = R3Commons.getDomain(
                ctx,
                R3Commons.getClassnameWithoutEntity(PredlohaEntity.class.getName())
        );

        //1. kontrola jestli záznam má vyplněná pole
        //název,rokvydání,sigla
        if (oneRec.getNazev() == null || oneRec.getRokyVydani() == null || oneRec.getSiglaVlastnika() == null) {
            issue.appendLogInfo("Záznam neimportován z důvodu, že neměl vyplněn alespoň jedno z následujících polí Název, Rok(y) vydání, Sigla:" + oneRec.toString());
            return OneRecordImportState.CANCELED;
        }

        ctx.addRootDomain(dPredloha);
        //Voláme sekci 1 - Hledáme duplicitu proti záznamům vlastní knihovny
        oneRecordImportState = doOneRecord_sekce1(oneRec, ctx, dbb, issue, actualRecord, lPKeysToDelete, lXmlPart);
        LOG.log(Level.INFO, " stav záznamu '" + oneRec.getCisloCNB() + "|" + oneRec.getCarovyKod() + "|" + oneRec.getCastDilRocnik() + "|" + oneRec.getCisloPeriodika() + "' je: " + oneRecordImportState);
        if (oneRecordImportState != null) {
            return oneRecordImportState;
        } else if (DataStateEnum.HOTOVO.equals(issue.dataState)) {
            //zakládá se záznam ve stavu hotovo
            return doOneRecord_sekce3(
                    oneRec,
                    ctx,
                    dbb,
                    issue,
                    actualRecord,
                    dPredloha,
                    lXmlPart
            );
        } else {
            //Hledáme duplicitu proti záznamům mimo vlastní knihovnu
            return doOneRecord_sekce4(
                    oneRec,
                    ctx,
                    dbb,
                    issue,
                    actualRecord,
                    dPredloha,
                    lXmlPart
            );
        }
    }

    /**
     ***************************************************************************
     *
     * @param oneRec
     * @param ctx
     * @param dbb
     * @param issue
     * @param actualRecord
     * @return
     */
    private /*RecordsIterator*/ OneRecordImportState doOneRecord_sekce1(
            OneRecord oneRec,
            ExternalContextServer ctx,
            DbBrowser dbb,
            OneIssue issue,
            int actualRecord,
            List<PlaantUniqueKey> lPKeysToDelete,
            List<OneXmlPart> lXmlPart
    ) {
        RecordsIterator rit;
        OneRecordImportState oneRecordImportState;

        //kontroly proti duplicitám ve vlastní knihovně
        //Má příchozí záznam vyplněná pole Pole001?
        if (oneRec.getIdentifikatorZaznamu() != null) {
            //Má záznam vyplněná pole CARKOD?
            if (oneRec.getCarovyKod() != null) {
                rit = findRecordBy4Fields(
                        ctx, dbb,
                        PredlohaEntity.f_sigla1, oneRec.getSiglaVlastnika(),
                        PredlohaEntity.f_pole001, oneRec.getIdentifikatorZaznamu(),
                        PredlohaEntity.f_carKod, oneRec.getCarovyKod(),
                        null, null,
                        Filter.EQUAL_CRIT
                );
                if (rit.hasMoreRecords()) {
                    return doOneRecord_sekce2a(oneRec, ctx, dbb, issue, actualRecord, rit, lPKeysToDelete, lXmlPart );
                }
                rit = null;                            

                // je Sigla z vybraných
                if (kontrolaNaVybraneSigla(oneRec.getSiglaVlastnika())) {
                    rit = findRecordBy4Fields(
                            ctx, dbb,
                            PredlohaEntity.f_sigla1, oneRec.getSiglaVlastnika(),
                            PredlohaEntity.f_pole001, oneRec.getIdentifikatorZaznamu(),
                            null, null,
                            null, null,
                            Filter.EQUAL_CRIT
                    );
                    if (rit.hasMoreRecords()) {
                        return doOneRecord_sekce2b("pole001", OneRecordImportState.REVIZE_VLASTNI, oneRec, ctx, dbb, issue, actualRecord, rit, lPKeysToDelete, lXmlPart );
                        //return doOneRecord_sekce2b("pole001", OneRecordImportState.REVIZE, oneRec, ctx, dbb, issue, actualRecord, rit, lPKeysToDelete, lXmlPart );
                    }
                    rit = null;                            

                    //Má příchozí záznam vyplněná pole ČČNB
                    if (oneRec.getCisloCNB() != null) {
                        rit = findRecordBy4FieldsAndMultiFieldIfValueISNotNull(
                                ctx, dbb,
                                PredlohaEntity.f_sigla1, oneRec.getSiglaVlastnika(),
                                null, null,
                                null, null,
                                null, null,
                                MultiFieldEnum.CCNB,
                                oneRec.getCisloCNB_all(),
                                Filter.EQUAL_CRIT
                        );
                        if (rit.hasMoreRecords()) {
                            return doOneRecord_sekce2b("čČNB", OneRecordImportState.REVIZE_VLASTNI, oneRec, ctx, dbb, issue, actualRecord, rit, lPKeysToDelete, lXmlPart );
                            //return doOneRecord_sekce2b("čČNB", OneRecordImportState.REVIZE, oneRec, ctx, dbb, issue, actualRecord, rit, lPKeysToDelete, lXmlPart );
                        }
                    }
                    rit = null;                            

                    //Má příchozí záznam vyplněná pole ISSN
                    if (oneRec.getIssn() != null) {
                        rit = findRecordBy4FieldsAndMultiFieldIfValueISNotNull(
                                ctx, dbb,
                                PredlohaEntity.f_sigla1, oneRec.getSiglaVlastnika(),
                                null, null,
                                null, null,
                                null, null,
                                MultiFieldEnum.ISSN, oneRec.getIssn(),
                                Filter.EQUAL_CRIT
                        );
                        if (rit.hasMoreRecords()) {
                            return doOneRecord_sekce2b("ISSN", OneRecordImportState.REVIZE_VLASTNI, oneRec, ctx, dbb, issue, actualRecord, rit, lPKeysToDelete, lXmlPart );
                            //return doOneRecord_sekce2b("ISSN", OneRecordImportState.REVIZE, oneRec, ctx, dbb, issue, actualRecord, rit, lPKeysToDelete, lXmlPart );
                        }
                    }
                    rit = null;                            

                    //Má příchozí záznam vyplněná pole ISBN
                    if (oneRec.getIsbn()!= null) {
                        rit = findRecordBy4FieldsAndMultiFieldIfValueISNotNull(
                                ctx, dbb,
                                PredlohaEntity.f_sigla1, oneRec.getSiglaVlastnika(),
                                null, null,
                                null, null,
                                null, null,
                                MultiFieldEnum.ISBN, oneRec.getIsbn(),
                                Filter.EQUAL_CRIT
                        );
                        if (rit.hasMoreRecords()) {
                            return doOneRecord_sekce2b("ISBN", OneRecordImportState.REVIZE_VLASTNI, oneRec, ctx, dbb, issue, actualRecord, rit, lPKeysToDelete, lXmlPart );
                            //return doOneRecord_sekce2b("ISBN", OneRecordImportState.REVIZE, oneRec, ctx, dbb, issue, actualRecord, rit, lPKeysToDelete, lXmlPart );
                        }
                    }
                    rit = null;                            
                } else { // není vybrané sigla, má vyplněný čarový kód, proto hledání podle dalších polí není
                    return null;
                }

                return null;
            }
        }

        oneRecordImportState = doOneRecord_sekce1b(oneRec, ctx, dbb, issue, actualRecord, lPKeysToDelete, lXmlPart);
        if (oneRecordImportState!= null) {
            return oneRecordImportState;
        }
        
        return null;
    }

    /**
     ***************************************************************************
     * hledání záznamů ve vlastní knihovně dle čCNB, ISSN, ISBN a případně i podle nepovinných polí pokud jsou vyplněny.
     * Hledá se podle všech variant
     * @param oneRec
     * @param ctx
     * @param dbb
     * @param issue
     * @param actualRecord
     * @return
     */
    private /*RecordsIterator*/ OneRecordImportState doOneRecord_sekce1b(
            OneRecord oneRec,
            ExternalContextServer ctx,
            DbBrowser dbb,
            OneIssue issue,
            int actualRecord,
            List<PlaantUniqueKey> lPKeysToDelete,
            List<OneXmlPart> lXmlPart
    ) {
        RecordsIterator rit;

        //Má příchozí záznam vyplněná pole POLE001
       if (oneRec.getIdentifikatorZaznamu() != null) {
            rit = findRecordBy5FieldsIfValueISNotNull(
                    ctx, dbb,
                    PredlohaEntity.f_sigla1, oneRec.getSiglaVlastnika(),
                    PredlohaEntity.f_pole001, oneRec.getIdentifikatorZaznamu(),
                    PredlohaEntity.f_cast, oneRec.getCastDilRocnik(),
                    PredlohaEntity.f_rozsah, oneRec.getRokPeriodika(),
                    PredlohaEntity.f_cisloPer, oneRec.getCisloPeriodika(),
                    Filter.EQUAL_CRIT
            );
            if (rit.hasMoreRecords()) {
                return doOneRecord_sekce2a(oneRec, ctx, dbb, issue, actualRecord, rit, lPKeysToDelete, lXmlPart );
            }
            return null;
        }
        
        //Má příchozí záznam vyplněná pole ČČNB
        if (oneRec.getCisloCNB() != null) {
            //nepovinné pole: CAST, ROK_PERIODIKA(rozsah), CISLO_PERIODIKA(cisloper)
            rit = findRecordBy4FieldsAndMultiFieldIfValueISNotNull(
                    ctx, dbb,
                    PredlohaEntity.f_sigla1, oneRec.getSiglaVlastnika(),
                    PredlohaEntity.f_cast, oneRec.getCastDilRocnik(),
                    PredlohaEntity.f_rozsah, oneRec.getRokPeriodika(),
                    PredlohaEntity.f_cisloPer, oneRec.getCisloPeriodika(),
                    MultiFieldEnum.CCNB, oneRec.getCisloCNB_all(),
                    Filter.EQUAL_CRIT
            );
            if (rit.hasMoreRecords()) {
                return doOneRecord_sekce2a(oneRec, ctx, dbb, issue, actualRecord, rit, lPKeysToDelete, lXmlPart );
            }
            return null;
        }

        //Má příchozí záznam vyplněná pole ISSN
        if (oneRec.getIssn() != null) {
            //nepovinné pole: CAST, ROK_PERIODIKA(rozsah), CISLO_PERIODIKA(cisloper)
            rit = findRecordBy4FieldsAndMultiFieldIfValueISNotNull(
                    ctx, dbb,
                    PredlohaEntity.f_sigla1, oneRec.getSiglaVlastnika(),
                    PredlohaEntity.f_cast, oneRec.getCastDilRocnik(),
                    PredlohaEntity.f_rozsah, oneRec.getRokPeriodika(),
                    PredlohaEntity.f_cisloPer, oneRec.getCisloPeriodika(),
                    MultiFieldEnum.ISSN, oneRec.getIssn(),
                    Filter.EQUAL_CRIT
            );
            if (rit.hasMoreRecords()) {
                return doOneRecord_sekce2a(oneRec, ctx, dbb, issue, actualRecord, rit, lPKeysToDelete, lXmlPart );
            }
            return null;
        }

        //Má příchozí záznam vyplněná pole ISBN
        if (oneRec.getIsbn() != null) {
            //nepovinné pole: CAST, ROK_PERIODIKA(rozsah), CISLO_PERIODIKA(cisloper)
            rit = findRecordBy4FieldsAndMultiFieldIfValueISNotNull(
                    ctx, dbb,
                    PredlohaEntity.f_sigla1, oneRec.getSiglaVlastnika(),
                    PredlohaEntity.f_cast, oneRec.getCastDilRocnik(),
                    PredlohaEntity.f_rozsah, oneRec.getRokPeriodika(),
                    PredlohaEntity.f_cisloPer, oneRec.getCisloPeriodika(),
                    MultiFieldEnum.ISBN, oneRec.getIsbn(),
                    Filter.EQUAL_CRIT
            );
            if (rit.hasMoreRecords()) {
                return doOneRecord_sekce2a(oneRec, ctx, dbb, issue, actualRecord, rit, lPKeysToDelete, lXmlPart );
            }
            return null;
        }

        return null;
    }
    
    /**
     ***************************************************************************
     * pokud nalezeno více než jeden záznam, odmítnuto
     * pokud nalezen právě jeden záznam, kontrola stavu a záznam buď odmítnut nebo aktualizován
     * @param oneRec
     * @param ctx
     * @param dbb
     * @param issue
     * @param actualRecord
     * @param rit
     */
    private OneRecordImportState doOneRecord_sekce2a(
            OneRecord oneRec,
            ExternalContextServer ctx,
            DbBrowser dbb,
            OneIssue issue,
            int actualRecord,
            RecordsIterator rit,
            List<PlaantUniqueKey> lPKeysToDelete,
            List<OneXmlPart> lXmlPart
    ) {
        Record recPredloha;
        StringBuilder sb = new StringBuilder(0);
        DataStateEnum stavRDCZ;
        //RecordsIterator ritTEMP;
        LOG.log(Level.INFO, " je volána sekce 2a");
        if (rit.getRecordsCount() != 1) {
            //Nalezli jsme víc záznamů předlohy
            sb.append("Záznam neimportován z důvodu duplicit, podle zadaných identifikátorů bylo nalezeno více záznamů předlohy: ").append(oneRec.toString()).append(StringUtils.LINE_SEPARATOR_WINDOWS).append("Při kontrolách nalezena duplicita s těmito záznamy:").append(StringUtils.LINE_SEPARATOR_WINDOWS);
            while (rit.hasMoreRecords()) {
                recPredloha = rit.nextRecord();
                sb.append(recPredloha.getSimpleField(PredlohaEntity.f_idCislo).getValue()).append(" - ").append(recPredloha.getSimpleField(PredlohaEntity.f_sigla1).getValue()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
            }
            issue.appendLogInfo(sb.toString());
            LOG.log(Level.SEVERE, "CANCELED - z důvodu duplicit");
            return OneRecordImportState.CANCELED;
        } else {
            //Nalezli jsme právě jeden záznam předlohy
            recPredloha = rit.nextRecord();
            stavRDCZ = convertPredlohaStateToDataState((String) recPredloha.getSimpleField(PredlohaEntity.f_stavRec).getValue());
            if (stavRDCZ == null) {
                issue.appendLogInfo("Záznam nebyl importován, odpovídající záznam v RD.CZ " + recPredloha.getSimpleField(PredlohaEntity.f_idCislo).getValue() + " - " + recPredloha.getSimpleField(PredlohaEntity.f_sigla1).getValue() + " měl neplatný stav záznamu: " + oneRec.toString());
                LOG.log(Level.SEVERE, "CANCELED - odpovídající záznam měl chybný stav");
                return OneRecordImportState.CANCELED;
            } else if (stavRDCZ.compareTo(issue.dataState) >= 0) {
                //Záznam z RDCZ má vyšší nebo stejný stav jako importovaný záznam
                issue.appendLogInfo("Záznam nebyl importován, odpovídající záznam v RD.CZ " + recPredloha.getSimpleField(PredlohaEntity.f_idCislo).getValue() + " - " + recPredloha.getSimpleField(PredlohaEntity.f_sigla1).getValue() + " měl vyšší nebo stejný stav záznamu: " + oneRec.toString());
                LOG.log(Level.SEVERE, "CANCELED - odpovídající záznam měl vyšší či stejný stav");
                return OneRecordImportState.CANCELED;
            } else {
                //Záznam z RDCZ má nižší stav než importovaný záznam
                //UpDate záznamu
                setUpdateRecordPredloha(recPredloha, oneRec, issue, lPKeysToDelete, lXmlPart);
                LOG.log(Level.SEVERE, "UPDATE - update záznamu");
                return OneRecordImportState.UPDATE;
            }
        }
    }

    /**
     ***************************************************************************
     * založí záznam ve stavu interní revize - pravděpodobná duplicita k záznamu ve vlastní knihovně - bez ohledu na počet stávajících záznamů
     * @param oneRec
     * @param ctx
     * @param dbb
     * @param issue
     * @param actualRecord
     * @param rit
     */
    private OneRecordImportState doOneRecord_sekce2b(
            String poleDuplicity,
            OneRecordImportState recordState,
            OneRecord oneRec,
            ExternalContextServer ctx,
            DbBrowser dbb,
            OneIssue issue,
            int actualRecord,
            RecordsIterator rit,
            List<PlaantUniqueKey> lPKeysToDelete,
            List<OneXmlPart> lXmlPart
    ) {
        Record recPredloha;
        String className = R3Commons.getClassnameWithoutEntity(PredlohaEntity.class.getName());
        Domain dPredloha = R3Commons.getDomain(ctx, className);
        Record recTemp, recTemp2;
        StringBuilder sb = new StringBuilder(0);

        LOG.log(Level.INFO, " je volána sekce 2b");
        try {
            recTemp = ctx.create(dPredloha);
            setNewRecordPredloha(recTemp, oneRec, issue, true, lXmlPart);
//            recTemp.getSimpleField(PredlohaEntity.f_stavRec).setValue(getIssueState(recordState));
            //2018.01.22 - nastavení vlastní revize na revizi standardní
            recTemp.getSimpleField(PredlohaEntity.f_stavRec).setValue(getIssueState(OneRecordImportState.REVIZE));
            recTemp.getSimpleField(PredlohaEntity.f_jeVlastniDuplicita).setValue(Boolean.TRUE);
            
            sb = new StringBuilder(0);
            sb.append("REVIZE vlastní: duplicitní " + poleDuplicity).append(oneRec.toString()).append(StringUtils.LINE_SEPARATOR_WINDOWS);

            //Připojíme informaci o tom proti kterým záznamům v RD.CZ byla nalezena duplicita
            /*
            //na žádost z 22.01.2018 - p.Dvořáková, dle emailu schváleno p.Ljubkou -  odtraněno zobrazení informací o duplicitních záznamech
            sb.append("Seznam záznamů v RD.CZ proti kterým byla nalezena duplicita:").append(StringUtils.LINE_SEPARATOR_WINDOWS);
            while (rit.hasMoreRecords()) {
                recTemp2 = rit.nextRecord();
                sb.append(" " + recTemp2.getSimpleField(PredlohaEntity.f_idCislo).getValue()).append(" - ").append(recTemp2.getSimpleField(PredlohaEntity.f_sigla1).getValue()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
            }
            */
            //Zapíšeme celej LOG
            issue.appendLogInfo(sb.toString());
            return recordState /*OneRecordImportState.REVIZE_VLASTNI*/;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Chyba v sekci 2b: " + e + e.getStackTrace()[0].getLineNumber());
        }
        return null;
    }
    
    /**
     ***************************************************************************
     * založení záznamu, pokud je stav Hotovo. zakládá se vždy
     * @param oneRec
     * @param ctx
     * @param dbb
     * @param issue
     * @param actualRecord
     * @param dPredloha
     * @return
     */
    private OneRecordImportState doOneRecord_sekce3(
            OneRecord oneRec,
            ExternalContextServer ctx,
            DbBrowser dbb,
            OneIssue issue,
            int actualRecord,
            Domain dPredloha,
            List<OneXmlPart> lXmlPart
    ) {
        try {
            Record recPredloha = ctx.create(dPredloha);
            //Nastavíme odpovídající stav záznamu
            setNewRecordPredloha(recPredloha, oneRec, issue, false, lXmlPart);
            return OneRecordImportState.CREATE;
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Create Record.", ex);
            ctx.rollback();
            return OneRecordImportState.CANCELED;
        }
    }

    /**
     ***************************************************************************
     * hledání v cizích knihovnách čCNB, ISSN, ISBN, autor + název
     * @param oneRec
     * @param ctx
     * @param dbb
     * @param issue
     * @param actualRecord
     * @param dPredloha
     * @return
     */
    private OneRecordImportState doOneRecord_sekce4(
            OneRecord oneRec,
            ExternalContextServer ctx,
            DbBrowser dbb,
            OneIssue issue,
            int actualRecord,
            Domain dPredloha,
            List<OneXmlPart> lXmlPart
    ) {
        RecordsIterator rit = null;
        Record recTemp;
        Record recTemp2;
        StringBuilder sb;

        try {
            //Kontroly proti ostatním knihovnám
            //kontrola duplicit na čísloČNB
            if (oneRec.getCisloCNB() != null) {
                rit = findRecordByMultiFieldsExceptSpecificSigla(
                        ctx, dbb,
                        MultiFieldEnum.CCNB, oneRec.getCisloCNB_all(),
                        oneRec.getSiglaVlastnika()
                );
                if (rit.hasMoreRecords()) {
                    recTemp = ctx.create(dPredloha);
                    setNewRecordPredloha(recTemp, oneRec, issue, true, lXmlPart);
                    sb = new StringBuilder(0);
                    sb.append("REVIZE cizí: duplicitní čČNB").append(oneRec.toString()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
                    //Připojíme informaci o tom proti kterým záznamům v RD.CZ byla nalezena duplicita
                    sb.append("Seznam záznamů v RD.CZ proti kterým byla nalezena duplicita:").append(StringUtils.LINE_SEPARATOR_WINDOWS);
                    while (rit.hasMoreRecords()) {
                        recTemp2 = rit.nextRecord();
                        sb.append(recTemp2.getSimpleField(PredlohaEntity.f_idCislo).getValue()).append(" - ").append(recTemp2.getSimpleField(PredlohaEntity.f_sigla1).getValue()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
                    }
                    //Zapíšeme celej LOG
                    issue.appendLogInfo(sb.toString());
                    return OneRecordImportState.REVIZE;
                }
                rit = null;
            }

            //kontrola duplicit na ISSN
            if (rit == null && oneRec.getIssn() != null) {
                rit = findRecordByMultiFieldsExceptSpecificSigla(
                        ctx, dbb,
                        MultiFieldEnum.ISSN, oneRec.getIssn(),
                        oneRec.getSiglaVlastnika()
                );
                if (rit.hasMoreRecords()) {
                    recTemp = ctx.create(dPredloha);
                    setNewRecordPredloha(recTemp, oneRec, issue, true, lXmlPart);
                    sb = new StringBuilder(0);
                    sb.append("REVIZE cizí: duplicitní ISSN").append(oneRec.toString()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
                    //issue.appendLogInfo("REVIZE: duplicitní ISSN" + oneRec.toString());
                    //Připojíme informaci o tom proti kterým záznamům v RD.CZ byla nalezena duplicita
                    sb.append("Seznam záznamů v RD.CZ proti kterým byla nalezena duplicita:").append(StringUtils.LINE_SEPARATOR_WINDOWS);
                    while (rit.hasMoreRecords()) {
                        recTemp2 = rit.nextRecord();
                        sb.append(recTemp2.getSimpleField(PredlohaEntity.f_idCislo).getValue()).append(" - ").append(recTemp2.getSimpleField(PredlohaEntity.f_sigla1).getValue()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
                    }
                    issue.appendLogInfo(sb.toString());
                    return OneRecordImportState.REVIZE;
                }
                rit = null;
            }

            //kontrola duplicit na ISBN
            if (rit == null && oneRec.getIsbn() != null) {
                rit = findRecordByMultiFieldsExceptSpecificSigla(
                        ctx, dbb,
                        MultiFieldEnum.ISBN,
                        oneRec.getIsbn(),
                        oneRec.getSiglaVlastnika()
                );
                if (rit.hasMoreRecords()) {
                    recTemp = ctx.create(dPredloha);
                    setNewRecordPredloha(recTemp, oneRec, issue, true, lXmlPart);
                    sb = new StringBuilder(0);
                    sb.append("REVIZE cizí: duplicitní ISBN").append(oneRec.toString()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
                    //issue.appendLogInfo("REVIZE: duplicitní ISBN" + oneRec.toString());
                    sb.append("Seznam záznamů v RD.CZ proti kterým byla nalezena duplicita:").append(StringUtils.LINE_SEPARATOR_WINDOWS);
                    while (rit.hasMoreRecords()) {
                        recTemp2 = rit.nextRecord();
                        sb.append(recTemp2.getSimpleField(PredlohaEntity.f_idCislo).getValue()).append(" - ").append(recTemp2.getSimpleField(PredlohaEntity.f_sigla1).getValue()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
                    }
                    issue.appendLogInfo(sb.toString());
                    return OneRecordImportState.REVIZE;
                }
                rit = null;
            }

            //kontrola duplicit na Autor(příjmení) + název(do dvojtečky nebo lomítka) + roky vydání
            if (rit == null && oneRec.getRokyVydani() != null && oneRec.getPrijmeniAutora() != null && oneRec.getNazevDoDvojteckyNeboLomitka() != null) {
                rit = findRecordBy4FieldsExceptSpecificSigla(
                        ctx, dbb,
                        PredlohaEntity.f_autor, oneRec.getPrijmeniAutora(),
                        PredlohaEntity.f_nazev, oneRec.getNazevDoDvojteckyNeboLomitka(),
                        PredlohaEntity.f_rokVyd, oneRec.getRokyVydani(),
                        null, null,
                        Filter.CONTAIN_CRIT,
                        oneRec.getSiglaVlastnika()
                );
                if (rit.hasMoreRecords()) {
                    recTemp = ctx.create(dPredloha);
                    setNewRecordPredloha(recTemp, oneRec, issue, true, lXmlPart);
                    sb = new StringBuilder(0);
                    sb.append("REVIZE cizí: duplicitní Autor(příjmení) + název(do dvojtečky nebo lomítka) + Rok(y) vydání").append(oneRec.toString()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
                    //issue.appendLogInfo("REVIZE: duplicitní Autor(příjmení) + název(do dvojtečky nebo lomítka) + Rok(y) vydání" + oneRec.toString());
                    sb.append("Seznam záznamů v RD.CZ proti kterým byla nalezena duplicita:").append(StringUtils.LINE_SEPARATOR_WINDOWS);
                    while (rit.hasMoreRecords()) {
                        recTemp2 = rit.nextRecord();
                        sb.append(recTemp2.getSimpleField(PredlohaEntity.f_idCislo).getValue()).append(" - ").append(recTemp2.getSimpleField(PredlohaEntity.f_sigla1).getValue()).append(StringUtils.LINE_SEPARATOR_WINDOWS);
                    }
                    issue.appendLogInfo(sb.toString());
                    return OneRecordImportState.REVIZE;
                }
                rit = null;
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Create Record.", ex);
            return OneRecordImportState.CANCELED;
        }

        //Rozdělení podle stavu importních dat
        try {
            if (DataStateEnum.PROBIHA.equals(issue.dataState)) {
                //V4a - PROBIHA
                recTemp = ctx.create(dPredloha);
                setNewRecordPredloha(recTemp, oneRec, issue, false, lXmlPart);
                return OneRecordImportState.CREATE;
            } else {
                //V4b - ZAMER
                recTemp = ctx.create(dPredloha);
                setNewRecordPredloha(recTemp, oneRec, issue, false, lXmlPart);
                return OneRecordImportState.CREATE;
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Create Record.", ex);
            return OneRecordImportState.CANCELED;
        }
    }

    /**
     ***************************************************************************
     *
     * @param value
     * @return
     */
    private DataStateEnum convertPredlohaStateToDataState(String value) {
        if ("planovane".equals(value) || "vyrazeno".equals(value)) {
            return DataStateEnum.ZAMER;
//        } else if ("active".equals(value) || "progress".equals(value) || "predanoZpracovateli".equals(value) || "pripravenoProMf".equals(value) || "revize".equals(value) || "revize_vlastni".equals(value)) {
        } else if ("active".equals(value) || "progress".equals(value) || "predanoZpracovateli".equals(value) || "pripravenoProMf".equals(value) || "revize".equals(value)) {
            return DataStateEnum.PROBIHA;
        } else if ("finished".equals(value) || "archived".equals(value)) {
            return DataStateEnum.HOTOVO;
        }
        return null;
    }

    /**
     ***************************************************************************
     *
     * @param dataState
     * @return
     */
    private static String getRecordState(DataStateEnum dataState) {
        if (DataStateEnum.ZAMER.equals(dataState)) {
            return "planovane";
        }
        if (DataStateEnum.PROBIHA.equals(dataState)) {
            return "progress";
        }
        if (DataStateEnum.HOTOVO.equals(dataState)) {
            return "finished";
        }
        return null;
    }

        /**
     ***************************************************************************
     *
     * @param dataState
     * @return
     */
    private static String getIssueState(OneRecordImportState dataState) {
        if (OneRecordImportState.CANCELED.equals(dataState)) {
            return "canceled";
        }
        if (OneRecordImportState.CREATE.equals(dataState)) {
            return "create";
        }
        if (OneRecordImportState.REVIZE.equals(dataState)) {
            return "revize";
        }
        //if (OneRecordImportState.REVIZE_VLASTNI.equals(dataState)) {
        //    return "revize_vlastni";
        //}
        if (OneRecordImportState.UPDATE.equals(dataState)) {
            return "update";
        }
        return null;
    }

    /**
     * kontrola na vybrané sigly, které mají hledání jinak
     * @param siglaVlastnika
     * @return 
     */
    private boolean kontrolaNaVybraneSigla(String siglaVlastnika) {
        if (("ABA001".equalsIgnoreCase(siglaVlastnika)) || ("ABA004".equalsIgnoreCase(siglaVlastnika)) || ("BOA001".equalsIgnoreCase(siglaVlastnika))) {
            return true;
        }
        return false;
    }

    /**
     ***************************************************************************
     *
     */
    private enum MultiFieldEnum {

        CCNB, ISSN, ISBN
    }

    /**
     ***************************************************************************
     *
     */
    private enum OneRecordImportState {

        REVIZE, CREATE, UPDATE, REVIZE_VLASTNI, CANCELED
//        REVIZE, CREATE, UPDATE, CANCELED
    }

    /**
     ***************************************************************************
     *
     * @param ctx
     * @param dbb
     * @param mf
     * @param value
     * @return
     */
    private static RecordsIterator findRecordByMultiFieldsExceptSpecificSigla(
            ExternalContextServer ctx,
            DbBrowser dbb,
            MultiFieldEnum mf,
            List values,
            String sigla
    ) {
        Filter filter = R3FilterTools.getEmptyFilter();
        String cnPredloha = R3Commons.getClassnameWithoutEntity(PredlohaEntity.class.getName());
        String simpleField = null;
        String multiField = null;
        switch (mf) {
            case CCNB:
                simpleField = PredlohaEntity.f_cCNB;
                multiField = PredlohaEntity.f_tNepCCNB;
                break;
            case ISSN:
                simpleField = PredlohaEntity.f_issn;
                multiField = PredlohaEntity.f_tNepISSN;
                //simpleField = PredlohaEntity.f_cCNB;
                //multiField = PredlohaEntity.f_tNepCCNB;
                break;
            case ISBN:
                simpleField = PredlohaEntity.f_ISBN;
                multiField = PredlohaEntity.f_tNepISBN;
                //simpleField = PredlohaEntity.f_cCNB;
                //multiField = PredlohaEntity.f_tNepCCNB;
                break;
        }

        try {
            //LOG.log(Level.SEVERE, "MULTIFIELD_TYPE: " + mf);
            //LOG.log(Level.SEVERE, "VALUE-SIZE: " + values.size());
            String tempLog = "";
            for (int i = 0; i < values.size(); i++) {
                tempLog += values.get(i) + ",";
            }
            //LOG.log(Level.SEVERE, "VALUE: " + tempLog);
            //LOG.log(Level.SEVERE, "SIMPLEFIELD: " + simpleField);
            //LOG.log(Level.SEVERE, "MULTIFIELD: " + multiField);
            R3FilterTools.addFilterRule(
                    filter,
                    dbb,
                    cnPredloha,
                    Filter.AND_OP,
                    1,
                    simpleField,
                    Filter.IN_LIST_CRIT,
                    values.toArray(),
                    0,
                    true
            );
            R3FilterTools.addFilterRule(
                    filter,
                    dbb,
                    cnPredloha,
                    Filter.OR_OP,
                    0,
                    multiField + ".value",
                    Filter.IN_LIST_CRIT,
                    values.toArray(),
                    1,
                    true
            );
            R3FilterTools.addFilterRule(
                    filter,
                    dbb,
                    cnPredloha,
                    Filter.AND_OP,
                    1,
                    PredlohaEntity.f_sigla1,
                    Filter.NOT_EQUAL_CRIT,
                    sigla,
                    1,
                    true
            );
            return R3FilterTools.getRecords(ctx, cnPredloha, filter, null, null);
        } catch (RemoteException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (QueryException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     ***************************************************************************
     *
     * @param ctx
     * @param dbb
     * @param column1
     * @param value1
     * @param column2
     * @param value2
     * @param column3
     * @param value3
     * @param column4
     * @param value4
     * @param porovnavaciPravidlo Filter.*_CRIT
     * @return
     */
    private static RecordsIterator findRecordBy4Fields(
            ExternalContextServer ctx,
            DbBrowser dbb,
            String column1,
            String value1,
            String column2,
            String value2,
            String column3,
            String value3,
            String column4,
            String value4,
            byte porovnavaciPravidlo
    ) {
        Filter filter = R3FilterTools.getEmptyFilter();
        String cnPredloha = R3Commons.getClassnameWithoutEntity(PredlohaEntity.class.getName());

        try {
            if (column1 != null) {
                if (value1 != null) {
                    R3FilterTools.addFilterRule(
                            filter,
                            dbb,
                            cnPredloha,
                            Filter.AND_OP,
                            1,
                            column1,
                            porovnavaciPravidlo,
                            value1,
                            1,
                            true
                    );
                } else {
                    R3FilterTools.addFilterRule(
                            filter,
                            dbb,
                            cnPredloha,
                            Filter.AND_OP,
                            1,
                            column1,
                            Filter.EMPTY_CRIT,
                            value1,
                            1,
                            true
                    );
                }
            }
            if (column2 != null) {
                if (value2 != null) {
                    R3FilterTools.addFilterRule(
                            filter,
                            dbb,
                            cnPredloha,
                            Filter.AND_OP,
                            1,
                            column2,
                            porovnavaciPravidlo,
                            value2,
                            1,
                            true
                    );
                } else {
                    R3FilterTools.addFilterRule(
                            filter,
                            dbb,
                            cnPredloha,
                            Filter.AND_OP,
                            1,
                            column2,
                            Filter.EMPTY_CRIT,
                            value2,
                            1,
                            true
                    );
                }
            }
            if (column3 != null) {
                if (value3 != null) {
                    R3FilterTools.addFilterRule(
                            filter,
                            dbb,
                            cnPredloha,
                            Filter.AND_OP,
                            1,
                            column3,
                            porovnavaciPravidlo,
                            value3,
                            1,
                            true
                    );
                } else {
                    R3FilterTools.addFilterRule(
                            filter,
                            dbb,
                            cnPredloha,
                            Filter.AND_OP,
                            1,
                            column3,
                            Filter.EMPTY_CRIT,
                            value3,
                            1,
                            true
                    );
                }
            }
            if (column4 != null) {
                if (value3 != null) {
                    R3FilterTools.addFilterRule(
                            filter,
                            dbb,
                            cnPredloha,
                            Filter.AND_OP,
                            1,
                            column4,
                            porovnavaciPravidlo,
                            value4,
                            1,
                            true
                    );
                } else {
                    R3FilterTools.addFilterRule(
                            filter,
                            dbb,
                            cnPredloha,
                            Filter.AND_OP,
                            1,
                            column4,
                            Filter.EMPTY_CRIT,
                            value4,
                            1,
                            true
                    );
                }
            }
            return R3FilterTools.getRecords(ctx, cnPredloha, filter, null, null);
        } catch (RemoteException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (QueryException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     ***************************************************************************
     *
     * @param ctx
     * @param dbb
     * @param column1
     * @param value1
     * @param column2
     * @param value2
     * @param column3
     * @param value3
     * @param column4
     * @param value4
     * @param column5
     * @param value5
     * @param porovnavaciPravidlo Filter.*_CRIT
     * @return
     */
    private static RecordsIterator findRecordBy5FieldsIfValueISNotNull(
            ExternalContextServer ctx,
            DbBrowser dbb,
            String column1,
            String value1,
            String column2,
            String value2,
            String column3,
            String value3,
            String column4,
            String value4,
            String column5,
            String value5,
            byte porovnavaciPravidlo
    ) {
        Filter filter = R3FilterTools.getEmptyFilter();
        String cnPredloha = R3Commons.getClassnameWithoutEntity(PredlohaEntity.class.getName());

        try {
            if (column1 != null && value1 != null && !value1.isEmpty()) {
                R3FilterTools.addFilterRule(
                        filter,
                        dbb,
                        cnPredloha,
                        Filter.AND_OP,
                        1,
                        column1,
                        porovnavaciPravidlo,
                        value1,
                        1,
                        true
                );
            }
            if (column2 != null && value2 != null && !value2.isEmpty()) {
                R3FilterTools.addFilterRule(
                        filter,
                        dbb,
                        cnPredloha,
                        Filter.AND_OP,
                        1,
                        column2,
                        porovnavaciPravidlo,
                        value2,
                        1,
                        true
                );
            }
            if (column3 != null && value3 != null && !value3.isEmpty()) {
                R3FilterTools.addFilterRule(
                        filter,
                        dbb,
                        cnPredloha,
                        Filter.AND_OP,
                        1,
                        column3,
                        porovnavaciPravidlo,
                        value3,
                        1,
                        true
                );
            }
            if (column4 != null && value4 != null && !value4.isEmpty()) {
                R3FilterTools.addFilterRule(
                        filter,
                        dbb,
                        cnPredloha,
                        Filter.AND_OP,
                        1,
                        column4,
                        porovnavaciPravidlo,
                        value4,
                        1,
                        true
                );
            }
            if (column5 != null && value5 != null && !value5.isEmpty()) {
                R3FilterTools.addFilterRule(
                        filter,
                        dbb,
                        cnPredloha,
                        Filter.AND_OP,
                        1,
                        column5,
                        porovnavaciPravidlo,
                        value5,
                        1,
                        true
                );
            }
            return R3FilterTools.getRecords(ctx, cnPredloha, filter, null, null);
        } catch (RemoteException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (QueryException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     ***************************************************************************
     *
     * @param ctx
     * @param dbb
     * @param column1
     * @param value1
     * @param column2
     * @param value2
     * @param column3
     * @param value3
     * @param porovnavaciPravidlo Filter.*_CRIT
     * @return
     */
    private static RecordsIterator findRecordBy4FieldsExceptSpecificSigla(
            ExternalContextServer ctx,
            DbBrowser dbb,
            String column1,
            String value1,
            String column2,
            String value2,
            String column3,
            String value3,
            String column4,
            String value4,
            byte porovnavaciPravidlo,
            String sigla
    ) {
        Filter filter = R3FilterTools.getEmptyFilter();
        String cnPredloha = R3Commons.getClassnameWithoutEntity(PredlohaEntity.class.getName());

        try {
            if (column1 != null) {
                R3FilterTools.addFilterRule(
                        filter,
                        dbb,
                        cnPredloha,
                        Filter.AND_OP,
                        1,
                        column1,
                        porovnavaciPravidlo,
                        value1,
                        1,
                        true
                );
            }
            if (column2 != null) {
                R3FilterTools.addFilterRule(
                        filter,
                        dbb,
                        cnPredloha,
                        Filter.AND_OP,
                        1,
                        column2,
                        porovnavaciPravidlo,
                        value2,
                        1,
                        true
                );
            }
            if (column3 != null) {
                R3FilterTools.addFilterRule(
                        filter,
                        dbb,
                        cnPredloha,
                        Filter.AND_OP,
                        1,
                        column3,
                        porovnavaciPravidlo,
                        value3,
                        1,
                        true
                );
            }
            if (column4 != null) {
                R3FilterTools.addFilterRule(
                        filter,
                        dbb,
                        cnPredloha,
                        Filter.AND_OP,
                        1,
                        column4,
                        porovnavaciPravidlo,
                        value4,
                        1,
                        true
                );
            }
            R3FilterTools.addFilterRule(
                    filter,
                    dbb,
                    cnPredloha,
                    Filter.AND_OP,
                    1,
                    PredlohaEntity.f_sigla1,
                    Filter.NOT_EQUAL_CRIT,
                    sigla,
                    1,
                    true
            );
            return R3FilterTools.getRecords(ctx, cnPredloha, filter, null, null);
        } catch (RemoteException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (QueryException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     ***************************************************************************
     *
     * @param ctx
     * @param dbb
     * @param column1
     * @param value1
     * @param column2
     * @param value2
     * @param column3
     * @param value3
     * @param porovnavaciPravidlo Filter.*_CRIT
     * @return
     */
    private static RecordsIterator findRecordBy4FieldsAndMultiFieldIfValueISNotNull(
            ExternalContextServer ctx,
            DbBrowser dbb,
            String column1,
            String value1,
            String column2,
            String value2,
            String column3,
            String value3,
            String column4,
            String value4,
            MultiFieldEnum mf,
            List values,
            byte porovnavaciPravidlo
    ) {
        Filter filter = R3FilterTools.getEmptyFilter();
        String cnPredloha = R3Commons.getClassnameWithoutEntity(PredlohaEntity.class.getName());
        String simpleField = null;
        String multiField = null;

        try {
            if (column1 != null && value1 != null && !value1.isEmpty()) {
                R3FilterTools.addFilterRule(
                        filter,
                        dbb,
                        cnPredloha,
                        Filter.AND_OP,
                        1,
                        column1,
                        porovnavaciPravidlo,
                        value1,
                        1,
                        true
                );
            }
            if (column2 != null && value2 != null && !value2.isEmpty()) {
                R3FilterTools.addFilterRule(
                        filter,
                        dbb,
                        cnPredloha,
                        Filter.AND_OP,
                        1,
                        column2,
                        porovnavaciPravidlo,
                        value2,
                        1,
                        true
                );
            }
            if (column3 != null && value3 != null && !value3.isEmpty()) {
                R3FilterTools.addFilterRule(
                        filter,
                        dbb,
                        cnPredloha,
                        Filter.AND_OP,
                        1,
                        column3,
                        porovnavaciPravidlo,
                        value3,
                        1,
                        true
                );
            }
            if (column4 != null && value4 != null && !value4.isEmpty()) {
                R3FilterTools.addFilterRule(
                        filter,
                        dbb,
                        cnPredloha,
                        Filter.AND_OP,
                        1,
                        column4,
                        porovnavaciPravidlo,
                        value4,
                        1,
                        true
                );
            }
            //Multifield
            switch (mf) {
                case CCNB:
                    simpleField = PredlohaEntity.f_cCNB;
                    multiField = PredlohaEntity.f_tNepCCNB;
                    break;
                case ISSN:
                    simpleField = PredlohaEntity.f_issn;
                    multiField = PredlohaEntity.f_tNepISSN;

                    break;
                case ISBN:
                    simpleField = PredlohaEntity.f_ISBN;
                    multiField = PredlohaEntity.f_tNepISBN;
                    break;
            }

            R3FilterTools.addFilterRule(
                    filter,
                    dbb,
                    cnPredloha,
                    Filter.AND_OP,
                    1,
                    simpleField,
                    Filter.IN_LIST_CRIT,
                    values.toArray(),
                    0,
                    true
            );
            R3FilterTools.addFilterRule(
                    filter,
                    dbb,
                    cnPredloha,
                    Filter.OR_OP,
                    0,
                    multiField + ".value",
                    Filter.IN_LIST_CRIT,
                    values.toArray(),
                    1,
                    true
            );

            return R3FilterTools.getRecords(ctx, cnPredloha, filter, null, null);
        } catch (RemoteException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (QueryException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        return null;
    }

    /**
     ***************************************************************************
     *
     * @param recPredloha
     * @param oneRec
     * @return
     */
    private static boolean setNewRecordPredloha(
            Record recPredloha,
            OneRecord oneRec,
            OneIssue issue,
            boolean isRevize,
            List<OneXmlPart> lXmlPart
    ) {
        Record recNepCCNB;
        Record recNepISSN;
        Record recNepISBN;
        Record recVarMazev;
        List<String> listVarNazev;
        int textLenght = 0;

        if (recPredloha.getSimpleField(PredlohaEntity.f_securityOwner).getValue() == null) {
            recPredloha.getSimpleField(PredlohaEntity.f_securityOwner).setValue(issue.onePrispevatelIdentificator.zarazeniUser);
        } else if (!issue.onePrispevatelIdentificator.zarazeniUser.equals(recPredloha.getSimpleField(PredlohaEntity.f_securityOwner).getValue())) {
            LOG.log(Level.WARNING, "ZAZNAM NEBYL ZMENEN JELIKOZ PATRI JINEMU UZIVATELI: {0}", recPredloha.getKey());
            return false;
        }

        recPredloha.getSimpleField(PredlohaEntity.f_importSource).setValue(oneRec.getImportSource());
        recPredloha.getSimpleField(PredlohaEntity.f_druhDokumentu).setValue(oneRec.getDruhDokumentu());
        recPredloha.getSimpleField(PredlohaEntity.f_pole001).setValue(oneRec.getIdentifikatorZaznamu());
        recPredloha.getSimpleField(PredlohaEntity.f_digKnihovna).setValue(issue.onePrispevatelIdentificator.digKnihovna);
        recPredloha.getSimpleField(PredlohaEntity.f_sysno).setValue(oneRec.getSysno());
        recPredloha.getSimpleField(PredlohaEntity.f_autor).setValue(oneRec.getAutor());
        recPredloha.getSimpleField(PredlohaEntity.f_nazev).setValue(oneRec.getNazev());
        recPredloha.getSimpleField(PredlohaEntity.f_podnazev).setValue(oneRec.getPodnazev());
        recPredloha.getSimpleField(PredlohaEntity.f_rokVyd).setValue(oneRec.getRokyVydani());
        recPredloha.getSimpleField(PredlohaEntity.f_mistoVyd).setValue(oneRec.getMistoVydani());
        recPredloha.getSimpleField(PredlohaEntity.f_vydavatel).setValue(oneRec.getVydavatel());
        recPredloha.getSimpleField(PredlohaEntity.f_specDruh).setValue(oneRec.getSpecDruh());
        //recPredloha.getSimpleField(PredlohaEntity.f_financovano).setValue(oneRec.getZdrojFinancovani());
        setPredlohaFinancovano(issue, oneRec, recPredloha);
        recPredloha.getSimpleField(PredlohaEntity.f_bibPoznamka).setValue(oneRec.getPoznamky());
        //Doplnění URL pro záznamy ve stavu hotovo
        if (DataStateEnum.HOTOVO.equals(issue.dataState)) {
            if ((oneRec.getUrlCastRokRocnik856() != null || oneRec.getUrlNaTitul856() != null) && oneRec.getValue856z() == null) {
                recPredloha.getSimpleField(PredlohaEntity.f_url).setValue(oneRec.getUrlCastRokRocnik856());
                recPredloha.getSimpleField(PredlohaEntity.f_urlTitul).setValue(oneRec.getUrlNaTitul856());
            } else if (oneRec.getValue911z() == null) {
                recPredloha.getSimpleField(PredlohaEntity.f_url).setValue(oneRec.getUrlCastRokRocnik911());
                recPredloha.getSimpleField(PredlohaEntity.f_urlTitul).setValue(oneRec.getUrlNaTitul911());
            }
        }
        recPredloha.getSimpleField(PredlohaEntity.f_carKod).setValue(oneRec.getCarovyKod());
        recPredloha.getSimpleField(PredlohaEntity.f_signatura).setValue(oneRec.getSignatura());
        recPredloha.getSimpleField(PredlohaEntity.f_rozsah).setValue(oneRec.getRokPeriodika());
        recPredloha.getSimpleField(PredlohaEntity.f_cast).setValue(oneRec.getCastDilRocnik());
        recPredloha.getSimpleField(PredlohaEntity.f_cisloZakazky).setValue(oneRec.getCisloZakazky());
        recPredloha.getSimpleField(PredlohaEntity.f_skenPocetSouboru).setValue(oneRec.getPocetSouboru());
        if (!"SE".equalsIgnoreCase(oneRec.getDruhDokumentu())) {
            recPredloha.getSimpleField(PredlohaEntity.f_pocetStran).setValue(oneRec.getPocetStran());
            recPredloha.getSimpleField(PredlohaEntity.f_vyskaKnihy).setValue(oneRec.getVyskaKnihy());
        }
        recPredloha.getSimpleField(PredlohaEntity.f_serieName).setValue(oneRec.getSerieName());
        recPredloha.getSimpleField(PredlohaEntity.f_seriePart).setValue(oneRec.getSeriePart());
        recPredloha.getSimpleField(PredlohaEntity.f_skenOCR).setValue(oneRec.getOcr());
        recPredloha.getSimpleField(PredlohaEntity.f_skenOCRSvabach).setValue(oneRec.getOcrSvabach());
        recPredloha.getSimpleField(PredlohaEntity.f_typSkeneru).setValue(oneRec.getTypSkeneru());
        recPredloha.getSimpleField(PredlohaEntity.f_parSkenovani).setValue(oneRec.getParametrySkenovani());
        recPredloha.getSimpleField(PredlohaEntity.f_rozliseni).setValue(oneRec.getDpi());
        recPredloha.getSimpleField(PredlohaEntity.f_barevnaHloubka).setValue(oneRec.getBarevnaHloubka());
        recPredloha.getSimpleField(PredlohaEntity.f_skenDJVU).setValue(oneRec.getDjvu());
        recPredloha.getSimpleField(PredlohaEntity.f_skenGIF).setValue(oneRec.getGif());
        recPredloha.getSimpleField(PredlohaEntity.f_skenJPEG).setValue(oneRec.getJpeg());
        recPredloha.getSimpleField(PredlohaEntity.f_skenTIFF).setValue(oneRec.getTiff());
        recPredloha.getSimpleField(PredlohaEntity.f_skenTXT).setValue(oneRec.getTxt());
        recPredloha.getSimpleField(PredlohaEntity.f_poznKExemplari).setValue(oneRec.getPoznamkaExemplar());
        recPredloha.getSimpleField(PredlohaEntity.f_cisloPer).setValue(oneRec.getCisloPeriodika());
        //rec.getSimpleField(PredlohaEntity.f_xml).setValue(oneRec.getRawXML());

        //Nastavíme stav záznamu
        if (isRevize) {
            recPredloha.getSimpleField(PredlohaEntity.f_poznRec).setValue("Stav při importu: " + issue.dataState);
            recPredloha.getSimpleField(PredlohaEntity.f_stavRec).setValue("revize");
        } else {
            recPredloha.getSimpleField(PredlohaEntity.f_stavRec).setValue(getRecordState(issue.dataState));
        }

        //Sigla
        recPredloha.getSimpleField(PredlohaEntity.f_sigla1).setValue(oneRec.getSiglaVlastnika());

        //Katalog
        setPredlohaKatalog(issue, oneRec, recPredloha);
//        if (oneRec.getBaze() != null) {
//            recPredloha.getSimpleField(PredlohaEntity.f_katalog).setValue(oneRec.getBaze());
//        } else {
//            recPredloha.getSimpleField(PredlohaEntity.f_katalog).setValue(issue.onePrispevatelIdentificator.katalog);
//        }

        //LIST Variantní název
        listVarNazev = oneRec.getVariantniNazev();
        if (listVarNazev != null) {
            for (int i = 0; i < listVarNazev.size(); i++) {
                recVarMazev = recPredloha.getTableField(PredlohaEntity.f_tVarNazev).createTableRecord();
                recVarMazev.getSimpleField(TabVarNazevEntity.f_varNazev).setValue(listVarNazev.get(i));
            }
        }

        //CCNB platné
        if (oneRec.getCisloCNB() == null) {
            recPredloha.getSimpleField(PredlohaEntity.f_cCNB).setValue(null);
        } else {
            recPredloha.getSimpleField(PredlohaEntity.f_cCNB).setValue(oneRec.getCisloCNB());
        }

        //LIST CCBN neplatné
        if (oneRec.getCisloCNB_nep() == null) {
            //rec.getSimpleField(PredlohaEntity.f_cCNB).setValue(null);
            //TODO smazat staré hodnoty
        } else {
            List<String> list = oneRec.getCisloCNB_nep();
            for (int i = 0; i < list.size(); i++) {
                recNepCCNB = recPredloha.getTableField(PredlohaEntity.f_tNepCCNB).createTableRecord();
                recNepCCNB.getSimpleField(NepCCNBEntity.f_value).setValue(list.get(i));
                recPredloha.getTableField(PredlohaEntity.f_tNepCCNB).addKey(recNepCCNB.getKey());
            }
        }

        //List ISBN
        if (oneRec.getIsbn() == null) {
            if (oneRec.getIsmn() == null) {
                recPredloha.getSimpleField(PredlohaEntity.f_ISBN).setValue(null);
            } else {
                recPredloha.getSimpleField(PredlohaEntity.f_ISBN).setValue(oneRec.getIsmn());
            }
        } else {
            List<String> list = oneRec.getIsbn();
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    recPredloha.getSimpleField(PredlohaEntity.f_ISBN).setValue(list.get(i));
                } else {
                    recNepISBN = recPredloha.getTableField(PredlohaEntity.f_tNepISBN).createTableRecord();
                    recNepISBN.getSimpleField(NepISBNEntity.f_value).setValue(list.get(i));
                    recPredloha.getTableField(PredlohaEntity.f_tNepISBN).addKey(recNepISBN.getKey());
                }
            }
        }
        
        //LIST ISBN NEPLATNE
        if (oneRec.getIsbn_Nep() != null && !oneRec.getIsbn_Nep().isEmpty()) {
            List<String> list = oneRec.getIsbn_Nep();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) != "") {
                    recNepISBN = recPredloha.getTableField(PredlohaEntity.f_tNepISBN).createTableRecord();
                    recNepISBN.getSimpleField(NepISBNEntity.f_value).setValue(list.get(i));
                    recPredloha.getTableField(PredlohaEntity.f_tNepISBN).addKey(recNepISBN.getKey());
                }
            }
        }

        //LIST ISSN
        if (oneRec.getIssn() == null) {
            recPredloha.getSimpleField(PredlohaEntity.f_issn).setValue(null);
        } else {
            List<String> list = oneRec.getIssn();
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    //System.out.println("připojuji platne ISSN");
                    recPredloha.getSimpleField(PredlohaEntity.f_issn).setValue(list.get(i));
                } else {
                    //System.out.println("připojuji neplatne ISSN");
                    recNepISSN = recPredloha.getTableField(PredlohaEntity.f_tNepISSN).createTableRecord();
                    recNepISSN.getSimpleField(NepISSNEntity.f_value).setValue(list.get(i));
                    recPredloha.getTableField(PredlohaEntity.f_tNepISSN).addKey(recNepISSN.getKey());
                }
            }
        }
        
        //LIST ISSN NEPLATNE
        if (oneRec.getIssn_Nep() != null) {
            List<String> list = oneRec.getIssn_Nep();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) != "") {
                    recNepISSN = recPredloha.getTableField(PredlohaEntity.f_tNepISSN).createTableRecord();
                    recNepISSN.getSimpleField(NepISSNEntity.f_value).setValue(list.get(i));
                    recPredloha.getTableField(PredlohaEntity.f_tNepISSN).addKey(recNepISSN.getKey());
                }
            }
        }

        //KVO - KNAV
        if (issue.iskvo) {
            recPredloha.getSimpleField(PredlohaEntity.f_isKVO).setValue(true);
        }

        //Přesunuto na test až sem, aby jsme zkusil jestli to vyřeší problém s dlouhými stringy
        //recPredloha.getSimpleField(PredlohaEntity.f_xml).setValue(oneRec.getRawXML());
        //lXmlPart.add(new OneXmlPart((String) recPredloha.getFieldValue(PredlohaEntity.f_idCislo), oneRec.getRawXML()));
        if (oneRec.getRawXML() != null) {
            textLenght = StringUtils.getTextLength(oneRec.getRawXML(), null);
            LOG.log(Level.FINEST, "DEBUG - Text lenght: {0}", textLenght);
            if (textLenght > Commons.MAX_STRING_LENGHT) {
                LOG.log(Level.FINEST, "XML set externaly");
                lXmlPart.add(new OneXmlPart((String) recPredloha.getFieldValue(PredlohaEntity.f_idCislo), oneRec.getRawXML()));
            } else {
                LOG.log(Level.FINEST, "XML set internaly");
                recPredloha.getSimpleField(PredlohaEntity.f_xml).setValue(oneRec.getRawXML());
            }
            recPredloha.getSimpleField(PredlohaEntity.f_ediDateXML).setValue(new Date());
        }
        return true;
    }

    /**
     ***************************************************************************
     *
     * @param recPredloha
     * @param oneRec
     * @param issue
     * @param lPKeysToDelete
     * @return
     */
    private static void setUpdateRecordPredloha(
            Record recPredloha,
            OneRecord oneRec,
            OneIssue issue,
            List<PlaantUniqueKey> lPKeysToDelete,
            List<OneXmlPart> lXmlPart
    ) {
        int textLenght = 0;
        RecordsIterator ritTEMP;

        //Záznam z RDCZ má nižší stav než importovaný záznam
        //UpDate záznamu
        //Nastavíme nový stav záznamu
        recPredloha.getSimpleField(PredlohaEntity.f_stavRec).setValue(getRecordState(issue.dataState));
        recPredloha.getSimpleField(PredlohaEntity.f_importSource).setValue(oneRec.getImportSource());
        if (oneRec.getDruhDokumentu() != null) {
            recPredloha.getSimpleField(PredlohaEntity.f_druhDokumentu).setValue(oneRec.getDruhDokumentu());
        }
        recPredloha.getSimpleField(PredlohaEntity.f_pole001).setValue(oneRec.getIdentifikatorZaznamu());
        recPredloha.getSimpleField(PredlohaEntity.f_sysno).setValue(oneRec.getSysno());
        recPredloha.getSimpleField(PredlohaEntity.f_nazev).setValue(oneRec.getNazev());
        recPredloha.getSimpleField(PredlohaEntity.f_podnazev).setValue(oneRec.getPodnazev());
        recPredloha.getSimpleField(PredlohaEntity.f_autor).setValue(oneRec.getAutor());
        recPredloha.getSimpleField(PredlohaEntity.f_rokVyd).setValue(oneRec.getRokyVydani());
        recPredloha.getSimpleField(PredlohaEntity.f_mistoVyd).setValue(oneRec.getMistoVydani());
        recPredloha.getSimpleField(PredlohaEntity.f_vydavatel).setValue(oneRec.getVydavatel());
        if (!"SE".equalsIgnoreCase(oneRec.getDruhDokumentu())) {
            recPredloha.getSimpleField(PredlohaEntity.f_pocetStran).setValue(oneRec.getPocetStran());
            recPredloha.getSimpleField(PredlohaEntity.f_vyskaKnihy).setValue(oneRec.getVyskaKnihy());
        }
        recPredloha.getSimpleField(PredlohaEntity.f_serieName).setValue(oneRec.getSerieName());
        recPredloha.getSimpleField(PredlohaEntity.f_seriePart).setValue(oneRec.getSeriePart());
        //recPredloha.getSimpleField(PredlohaEntity.f_xml).setValue(oneRec.getRawXML());
        setPredlohaKatalog(issue, oneRec, recPredloha);
        setPredlohaFinancovano(issue, oneRec, recPredloha);

        //Doplnění platných neplatných ISSN/ISBN/čČNB -- přepíšeme stávající záznamy, nic z minula nenecháváme
        //ISSN
        //Pole vyčistíme
        recPredloha.getSimpleField(PredlohaEntity.f_issn).setValue(null);
        ritTEMP = recPredloha.getTableField(PredlohaEntity.f_tNepISSN).getTableRecords();
        while (ritTEMP.hasMoreRecords()) {
            lPKeysToDelete.add((PlaantUniqueKey) ritTEMP.nextRecord().getKey());
        }
        //Nastavíme nové hodnoty
        if (oneRec.getIssn() != null) {
            for (int i = 0; i < oneRec.getIssn().size(); i++) {
                if (i == 0) {
                    recPredloha.getSimpleField(PredlohaEntity.f_issn).setValue(oneRec.getIssn().get(i));
                } else {
                    recPredloha.getTableField(PredlohaEntity.f_tNepISSN).createTableRecord().getSimpleField(NepISSNEntity.f_value).setValue(oneRec.getIssn().get(i));
                }
            }
        }
        
        //ISSN neplatné
        if (oneRec.getIssn_Nep() != null && !oneRec.getIssn_Nep().isEmpty()) {
            ritTEMP = recPredloha.getTableField(PredlohaEntity.f_tNepISSN).getTableRecords();
            while (ritTEMP.hasMoreRecords()) {
                lPKeysToDelete.add((PlaantUniqueKey) ritTEMP.nextRecord().getKey());
            }

            //Nastavíme nové hodnoty
            for (int i = 0; i < oneRec.getIssn_Nep().size(); i++) {
                recPredloha.getTableField(PredlohaEntity.f_tNepISSN).createTableRecord().getSimpleField(NepISSNEntity.f_value).setValue(oneRec.getIssn_Nep(i));
            }
        }

        //ISBN - ISMN
        if (oneRec.getIsbn() != null || oneRec.getIsmn() != null) {
            recPredloha.getSimpleField(PredlohaEntity.f_ISBN).setValue(null);
            ritTEMP = recPredloha.getTableField(PredlohaEntity.f_tNepISBN).getTableRecords();
            while (ritTEMP.hasMoreRecords()) {
                lPKeysToDelete.add((PlaantUniqueKey) ritTEMP.nextRecord().getKey());
            }
        }

        //Nastavíme nové hodnoty ISBN
        if (oneRec.getIsbn() != null) {
            for (int i = 0; i < oneRec.getIsbn().size(); i++) {
                if (i == 0) {
                    recPredloha.getSimpleField(PredlohaEntity.f_ISBN).setValue(oneRec.getIsbn().get(i));
                } else {
                    recPredloha.getTableField(PredlohaEntity.f_tNepISBN).createTableRecord().getSimpleField(NepISBNEntity.f_value).setValue(oneRec.getIsbn().get(i));
                }
            }
        } else if (oneRec.getIsmn() != null) {
            recPredloha.getSimpleField(PredlohaEntity.f_ISBN).setValue(oneRec.getIsmn());
        }
        
        //ISBN neplatné
        if (oneRec.getIsbn_Nep() != null && !oneRec.getIsbn_Nep().isEmpty()) {
            ritTEMP = recPredloha.getTableField(PredlohaEntity.f_tNepISBN).getTableRecords();
            while (ritTEMP.hasMoreRecords()) {
                lPKeysToDelete.add((PlaantUniqueKey) ritTEMP.nextRecord().getKey());
            }

            //Nastavíme nové hodnoty
            for (int i = 0; i < oneRec.getIsbn_Nep().size(); i++) {
                recPredloha.getTableField(PredlohaEntity.f_tNepISBN).createTableRecord().getSimpleField(NepISBNEntity.f_value).setValue(oneRec.getIsbn_Nep(i));
            }
        }

        //čČNB
        if (oneRec.getCisloCNB() != null) {
            recPredloha.getSimpleField(PredlohaEntity.f_cCNB).setValue(oneRec.getCisloCNB());
        }
        //čČNB neplatné
        if (oneRec.getCisloCNB_nep() != null && !oneRec.getCisloCNB_nep().isEmpty()) {
            ritTEMP = recPredloha.getTableField(PredlohaEntity.f_tNepCCNB).getTableRecords();
            while (ritTEMP.hasMoreRecords()) {
                lPKeysToDelete.add((PlaantUniqueKey) ritTEMP.nextRecord().getKey());
            }

            //Nastavíme nové hodnoty
            for (int i = 0; i < oneRec.getCisloCNB_nep().size(); i++) {
                recPredloha.getTableField(PredlohaEntity.f_tNepCCNB).createTableRecord().getSimpleField(NepCCNBEntity.f_value).setValue(oneRec.getCisloCNB_nep().get(i));
            }
        }

        //Variantní název
        if (oneRec.getVariantniNazev() != null && !oneRec.getVariantniNazev().isEmpty()) {
            ritTEMP = recPredloha.getTableField(PredlohaEntity.f_tVarNazev).getTableRecords();
            while (ritTEMP.hasMoreRecords()) {
                lPKeysToDelete.add((PlaantUniqueKey) ritTEMP.nextRecord().getKey());
            }
            //Nastavíme nové hodnoty
            if (oneRec.getVariantniNazev() != null) {
                for (int i = 0; i < oneRec.getVariantniNazev().size(); i++) {
                    recPredloha.getTableField(PredlohaEntity.f_tVarNazev).createTableRecord().getSimpleField(TabVarNazevEntity.f_varNazev).setValue(oneRec.getVariantniNazev().get(i));
                }
            }
        }

        //Doplnění URL pro záznamy ve stavu hotovo
        LOG.log(Level.FINEST, "DEBUG: URL 856: " + oneRec.getUrlCastRokRocnik856());
        LOG.log(Level.FINEST, "DEBUG: URL 911: " + oneRec.getUrlCastRokRocnik911());
        LOG.log(Level.FINEST, "DEBUG: URL TITUL 856: " + oneRec.getUrlNaTitul856());
        LOG.log(Level.FINEST, "DEBUG: URL TITUL 911: " + oneRec.getUrlNaTitul911());
        LOG.log(Level.FINEST, "DEBUG: ISSUE DATA STATE: " + issue.dataState);
        if (DataStateEnum.HOTOVO.equals(issue.dataState)) {

            LOG.log(Level.FINEST, "DEBUG: 01: " + oneRec.getUrlCastRokRocnik911() + " !=NULL AND " + oneRec.getValue911z() + " == NULL");
            LOG.log(Level.FINEST, "DEBUG: 02: " + oneRec.getUrlCastRokRocnik856() + " !=NULL AND " + oneRec.getValue856z() + " == NULL");
            if (oneRec.getUrlCastRokRocnik911() != null && oneRec.getValue911z() == null) {
                recPredloha.getSimpleField(PredlohaEntity.f_url).setValue(oneRec.getUrlCastRokRocnik911());
                LOG.log(Level.FINEST, "DEBUG: SET 01");
            } else if (oneRec.getUrlCastRokRocnik856() != null && oneRec.getValue856z() == null) {
                recPredloha.getSimpleField(PredlohaEntity.f_url).setValue(oneRec.getUrlCastRokRocnik856());
                LOG.log(Level.FINEST, "DEBUG: SET 02");
            }

            LOG.log(Level.FINEST, "DEBUG: 03: " + oneRec.getUrlNaTitul911() + " !=NULL AND " + oneRec.getValue911z() + " == NULL");
            LOG.log(Level.FINEST, "DEBUG: 04: " + oneRec.getUrlNaTitul911() + " !=NULL AND " + oneRec.getValue856z() + " == NULL");
            if (oneRec.getUrlNaTitul911() != null && oneRec.getValue911z() == null) {
                recPredloha.getSimpleField(PredlohaEntity.f_urlTitul).setValue(oneRec.getUrlNaTitul911());
                LOG.log(Level.FINEST, "DEBUG: SET 03");
            } else if (oneRec.getUrlNaTitul856() != null && oneRec.getValue856z() == null) {
                recPredloha.getSimpleField(PredlohaEntity.f_urlTitul).setValue(oneRec.getUrlNaTitul856());
                LOG.log(Level.FINEST, "DEBUG: SET 04");
            }
        }

        //Přesunuto na test až sem, aby jsme zkusil jestli to vyřeší problém s dlouhými stringy
        //recPredloha.getSimpleField(PredlohaEntity.f_xml).setValue(oneRec.getRawXML());
        //lXmlPart.add(new OneXmlPart((String) recPredloha.getFieldValue(PredlohaEntity.f_idCislo), oneRec.getRawXML()));
        if (oneRec.getRawXML() != null) {
            textLenght = StringUtils.getTextLength(oneRec.getRawXML(), null);
            LOG.log(Level.FINEST, "DEBUG - Text lenght: {0}", textLenght);
            if (textLenght > Commons.MAX_STRING_LENGHT) {
                LOG.log(Level.FINEST, "XML set externaly");
                lXmlPart.add(new OneXmlPart((String) recPredloha.getFieldValue(PredlohaEntity.f_idCislo), oneRec.getRawXML()));
            } else {
                LOG.log(Level.FINEST, "XML set internaly");
                recPredloha.getSimpleField(PredlohaEntity.f_xml).setValue(oneRec.getRawXML());
            }
            recPredloha.getSimpleField(PredlohaEntity.f_ediDateXML).setValue(new Date());
        }
    }

    /**
     ***************************************************************************
     *
     * @param issue
     * @param oneRec
     * @param recPredloha
     */
    private static void setPredlohaKatalog(OneIssue issue, OneRecord oneRec, Record recPredloha) {
        if (issue.fileSigla != null && ("ABA001".equalsIgnoreCase(issue.fileSigla))) {
            if (oneRec.getIdentifikatorZaznamu() != null && (oneRec.getIdentifikatorZaznamu().toUpperCase()).startsWith("STT")) {
                recPredloha.getSimpleField(PredlohaEntity.f_katalog).setValue("STT01");
                return;
            }
        } else if (issue.fileSigla != null && ("BOA001".equalsIgnoreCase(issue.fileSigla))) {
            if (oneRec.getDruhDokumentu() != null && "RP".equalsIgnoreCase(oneRec.getDruhDokumentu())) {
                recPredloha.getSimpleField(PredlohaEntity.f_katalog).setValue("MZK03");
                return;
            }
        }

        if (oneRec.getBaze() != null) {
            recPredloha.getSimpleField(PredlohaEntity.f_katalog).setValue(oneRec.getBaze());
        } else {
            recPredloha.getSimpleField(PredlohaEntity.f_katalog).setValue(issue.onePrispevatelIdentificator.katalog);
        }
    }

    /**
     ***************************************************************************
     *
     * @param oneRec
     * @param recPredloha
     */
    private static void setPredlohaFinancovano(OneIssue issue, OneRecord oneRec, Record recPredloha) {

        // změna z 2017.10.03 - nastavení financování dle názvu souboru
        if (issue.getFile() != null && (issue.getFile().getName().toUpperCase()).contains("_OWN_")) {
            recPredloha.getSimpleField(PredlohaEntity.f_financovano).setValue("vlastni");
        } else if(issue.getFile() != null && (issue.getFile().getName().toUpperCase()).contains("_MSVK_")) {
            recPredloha.getSimpleField(PredlohaEntity.f_financovano).setValue("MSVK");
        } else if (issue.getFile() != null && (issue.getFile().getName().toUpperCase()).contains("_VISK7_")) {
            recPredloha.getSimpleField(PredlohaEntity.f_financovano).setValue("VISK7");
        } else if (issue.getFile() != null && (issue.getFile().getName().toUpperCase()).contains("_IOP_")) {
            recPredloha.getSimpleField(PredlohaEntity.f_financovano).setValue("iop-kraj");
        } else {
            // původní financování
            if (issue.fileSigla != null && ("ABA001".equalsIgnoreCase(issue.fileSigla))) {
                if (issue.getFile() != null && (issue.getFile().getName().toUpperCase()).contains("EOD")) {
                    recPredloha.getSimpleField(PredlohaEntity.f_financovano).setValue("EOD");
                } else if (issue.getFile() != null && (issue.getFile().getName().toUpperCase()).contains("NKP")) {
                    recPredloha.getSimpleField(PredlohaEntity.f_financovano).setValue("google");
                } else if (issue.getFile() != null && (issue.getFile().getName().toUpperCase()).contains("_DG")) {
                    recPredloha.getSimpleField(PredlohaEntity.f_financovano).setValue("iop-ndku");
                } else {
                    recPredloha.getSimpleField(PredlohaEntity.f_financovano).setValue(oneRec.getZdrojFinancovani());
                }
            } else if (issue.fileSigla != null && ("ABA004".equalsIgnoreCase(issue.fileSigla))) {
                if (issue.getFile() != null && (issue.getFile().getName().toUpperCase()).contains("EOD")) {
                    recPredloha.getSimpleField(PredlohaEntity.f_financovano).setValue("EOD");
                } else if (issue.getFile() != null && (issue.getFile().getName().toUpperCase()).contains("NKP")) {
                    recPredloha.getSimpleField(PredlohaEntity.f_financovano).setValue("google");
                } else if (issue.getFile() != null && (issue.getFile().getName().toUpperCase()).contains("_DG")) {
                    recPredloha.getSimpleField(PredlohaEntity.f_financovano).setValue("iop-ndku");
                } else {
                    recPredloha.getSimpleField(PredlohaEntity.f_financovano).setValue(oneRec.getZdrojFinancovani());
                }
            } else if (issue.fileSigla != null && ("BOA001".equalsIgnoreCase(issue.fileSigla))) {
                if (oneRec.getDruhDokumentu() != null && !oneRec.getDruhDokumentu().equalsIgnoreCase("RP")) {
                    recPredloha.getSimpleField(PredlohaEntity.f_financovano).setValue("iop-ndku");
                }
            } else {
                recPredloha.getSimpleField(PredlohaEntity.f_financovano).setValue(oneRec.getZdrojFinancovani());
            }
        }
    }

}

/**
 *******************************************************************************
 * Přepravka
 *
 * @author martin.novacek@incad.cz
 */
class ImportCounter {

    public int countOdmitnuteZaznamy = 0;
    public int countCreateZaznamy = 0;
    public int countUpdateZaznamy = 0;
    public int countRevizeZaznamy = 0;
}
