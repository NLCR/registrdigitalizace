/* *****************************************************************************
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter.demons;

import com.amaio.plaant.DbBrowser;
import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.RecordNotFoundException;
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.impl.ExternalContextServer;
import com.amaio.plaant.desk.QueryException;
import com.amaio.plaant.desk.container.PlaantUniqueKey;
import com.amaio.plaant.metadata.Filter;
import com.amaio.plaant.sync.Domain;
import com.amaio.plaant.sync.Record;
import com.amaio.plaant.sync.UniqueKey;
import cz.incad.commontools.utils.StringUtils;
import cz.incad.r3tools.R3Commons;
import cz.incad.r3tools.R3FilterTools;
import cz.incad.rd.NepCCNBEntity;
import cz.incad.rd.NepISBNEntity;
import cz.incad.rd.NepISSNEntity;
import cz.incad.rd.PredlohaEntity;
import cz.incad.rd.TabVarNazevEntity;
import cz.incad.relief3.rdcz.uniimporter.model.ImportListener;
import cz.incad.relief3.rdcz.uniimporter.model.IssueHeap;
import cz.incad.relief3.rdcz.uniimporter.model.OneIssue;
import cz.incad.relief3.rdcz.uniimporter.model.OneRecord;
import cz.incad.relief3.rdcz.uniimporter.model.OneXmlPart;
import cz.incad.relief3.rdcz.uniimporter.model.enums.IssueStateEnum;
import cz.incad.relief3.rdcz.uniimporter.utils.Configuration;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *******************************************************************************
 *
 * @author Martin
 */
public class ReliefImportRepair1Demon implements ImportListener {

    private static final Logger LOG = Logger.getLogger(ReliefImportRepair1Demon.class.getName());
    private boolean runAgain = true;

    /**
     ***************************************************************************
     *
     * @param issue
     */
    @Override
    public void ImportIt(OneIssue issue) {
        LOG.log(Level.INFO, "Start Import Repair 1 ISSUE: {0}", issue.getUUID());
        ExternalContextServer ctx = null;
        DbBrowser dbb = null;
        int countOdmitnuteZaznamy = 0;
        int countNotFoundZaznamy = 0;
        int countUpdateZaznamy = 0;
        //int countRevizeZaznamy      = 0;
        int countAddRAWDataFail = 0;
        int actualRecord = 0;
        ReliefImportRepair1Demon.OneRecordImportState state;
        /* List záznamů, které mají být smazány */
        List<PlaantUniqueKey> lPKeysToDelete = new LinkedList<PlaantUniqueKey>();
        List<OneXmlPart> lXmlPart = new LinkedList<OneXmlPart>();
        Domain dPredloha;
        Domain dNepISSN;
        Domain dNepISBN;
        Domain dNepCCNB;
        Domain dTabVarNazev;

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
            issue.issueState = IssueStateEnum._4_readyForImport_repair_1;
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
                        "STARTING IMPORT R1: {0} - {1} - {2}/{3} - {4}",
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
                //počítadlo
                switch (state) {
                    case NOTFOUND:
                        countNotFoundZaznamy++;
                        break;
                    case UPDATE:
                        countUpdateZaznamy++;
                        break;
                    case CANCELED:
                        countOdmitnuteZaznamy++;
                        break;
                }
                //Smažeme záznamy, které jsme se v průběhu procesu rozhodli smazat
                if (!issue.isTest) {
                    for (int j = 0; j < lPKeysToDelete.size(); j++) {
                        try {
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
                            ctx.addRootDomain(dPredloha);
                            filter = R3FilterTools.getEmptyFilter();
                            if (lXmlPart.get(j).idCislo != null) {
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
                                LOG.log(Level.SEVERE, "Chyba pri doplnovani XML: {0}", lXmlPart.get(j));
                                issue.appendLogFailInfo("Chyba při doplňování XML Záznam nenalezen: " + lXmlPart.get(j));
                            }

                            if (issue.isTest) {
                                ctx.rollback();
                            } else {
                                ctx.commit();
                            }
                        } catch (Exception ex) {
                            countAddRAWDataFail++;
                            LOG.log(Level.SEVERE, "Chyba pri doplnovani XML: " + lXmlPart.get(j), ex);
                            issue.appendLogFailInfo("Chyba při doplňování XML: " + lXmlPart.get(j));
                            //TODO 
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
            LOG.log(Level.INFO, "Finish Repair 0 Import ISSUE: {0}", issue.getUUID());
            //Zapíšeme summary log o Importu
            issue.appendLogSummary(
                    "Celkem záznamů               : " + issue.lRecords.size() + StringUtils.LINE_SEPARATOR_WINDOWS
                    + "Počet nenalezených záznamů   : " + countNotFoundZaznamy + StringUtils.LINE_SEPARATOR_WINDOWS
                    + "Aktualizovaných záznamů      : " + countUpdateZaznamy + StringUtils.LINE_SEPARATOR_WINDOWS
                    + "Odmítnutých záznamů          : " + countOdmitnuteZaznamy + StringUtils.LINE_SEPARATOR_WINDOWS
                    + "Záznamů bez RAW dat (XML,CSV): " + countAddRAWDataFail);
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
    private ReliefImportRepair1Demon.OneRecordImportState doOneRecord(
            OneRecord oneRec,
            ExternalContextServer ctx,
            DbBrowser dbb,
            OneIssue issue,
            int actualRecord,
            List<PlaantUniqueKey> lPKeysToDelete,
            List<OneXmlPart> lXmlPart
    ) {
        RecordsIterator rit;
        RecordsIterator ritTEMP;
        Record recPredloha;
        List<UniqueKey> lFoundRecords = new LinkedList<UniqueKey>();
        Domain dPredloda = R3Commons.getDomain(ctx, R3Commons.getClassnameWithoutEntity(PredlohaEntity.class.getName()));
        Object urlPredloha = null;
        Object urlSource = null;
        int textLenght = 0;
        String tempPar1 = null;
        String tempPar2 = null;
        String tempVal1 = null;
        String tempVal2 = null;
        List<String> lTemp = new LinkedList<String>();;

        //1. kontrola jestli záznam má vyplněná pole
        //název,rokvydání,sigla
        if ((oneRec.getCisloCNB() == null && (oneRec.getIssn() == null || oneRec.getIssn().isEmpty())) || oneRec.getSiglaVlastnika() == null) {
            issue.appendLogInfo("Záznam neimportován z důvodu, že neměl vyplněn alespoň jedno z následujících polí Číslo ČNB, ISSN, Sigla:" + oneRec.toString());
            return ReliefImportRepair1Demon.OneRecordImportState.CANCELED;
        }

        if (oneRec.getCisloCNB() != null) {
            tempPar1 = PredlohaEntity.f_cCNB;
            tempVal1 = oneRec.getCisloCNB();
        }
        if (oneRec.getIssn() != null && !oneRec.getIssn().isEmpty()) {
            tempPar2 = PredlohaEntity.f_issn;
            tempVal2 = oneRec.getIssn().get(0);
        }

        rit = findRecordBy4Fields(
                ctx,
                dbb,
                PredlohaEntity.f_sigla1,
                oneRec.getSiglaVlastnika(),
                tempPar1,
                tempVal1,
                tempPar2,
                tempVal2,
                null,
                null,
                Filter.EQUAL_CRIT
        );
        if (rit.hasMoreRecords()) {
            //Máme iterátor všech záznamů odpovídající podmínce Sigla + CCNB nebo ISSN nebo OBOJI

            //Načteme si seznam PUK které budeme potom zpracovávat jeden za druhým.
            while (rit.hasMoreRecords()) {
                lFoundRecords.add(rit.nextRecord().getKey());
            }

            LOG.log(Level.INFO, "Predloha found records: {0}", lFoundRecords.size());
            for (int x = 0; x < lFoundRecords.size(); x++) {
                try {
                    recPredloha = ctx.getRecord((PlaantUniqueKey) lFoundRecords.get(x));
                } catch (RecordNotFoundException ex) {
                    LOG.log(Level.WARNING, lFoundRecords.get(x).toString(), ex);
                    continue;
                }
                LOG.log(Level.FINE, "PREDLOHA to update: {0}", recPredloha.getSimpleField(PredlohaEntity.f_idCislo).getValue());

                //přidáme předlohu do aktuálního OC4J kontejneru
                ctx.addRootDomain(dPredloda);

                recPredloha.getSimpleField(PredlohaEntity.f_importSource).setValue(oneRec.getImportSource());
                //máme záznam předlohy a provedeme update záznamu.
                //updatesimpleFieldIfSourceNotNull(recPredloha, PredlohaEntity.f_druhDokumentu, oneRec.getDruhDokumentu());
                //updatesimpleFieldIfSourceNotNull(recPredloha, PredlohaEntity.f_pole001, oneRec.getIdentifikatorZaznamu());
                //updatesimpleFieldIfSourceNotNull(recPredloha, PredlohaEntity.f_autor, oneRec.getAutor());
                //updatesimpleFieldIfSourceNotNull(recPredloha, PredlohaEntity.f_nazev, oneRec.getNazev());
                //updatesimpleFieldIfSourceNotNull(recPredloha, PredlohaEntity.f_podnazev, oneRec.getPodnazev());
                updatesimpleFieldIfSourceNotNullAndTargetIsNull(recPredloha, PredlohaEntity.f_mistoVyd, oneRec.getMistoVydani());
                updatesimpleFieldIfSourceNotNullAndTargetIsNull(recPredloha, PredlohaEntity.f_vydavatel, oneRec.getVydavatel());
                //updatesimpleFieldIfSourceNotNull(recPredloha, PredlohaEntity.f_rokVyd, oneRec.getRokyVydani());
                //if (!"SE".equalsIgnoreCase(oneRec.getDruhDokumentu())) {
                //    updatesimpleFieldIfSourceNotNull(recPredloha, PredlohaEntity.f_pocetStran, oneRec.getPocetStran());
                //    updatesimpleFieldIfSourceNotNull(recPredloha, PredlohaEntity.f_vyskaKnihy, oneRec.getVyskaKnihy());
                //}
                updatesimpleFieldIfSourceNotNullAndTargetIsNull(recPredloha, PredlohaEntity.f_serieName, oneRec.getSerieName());
                updatesimpleFieldIfSourceNotNullAndTargetIsNull(recPredloha, PredlohaEntity.f_seriePart, oneRec.getSeriePart());
                //updatesimpleFieldIfSourceNotNull(recPredloha, PredlohaEntity.f_sysno, oneRec.getSysno());
                //updatesimpleFieldIfSourceNotNull(recPredloha, PredlohaEntity.f_, oneRec.get);

                //Doplnění platných neplatných ISSN/ISBN/čČNB
                //ISSN
                //if (oneRec.getIssn() != null && !oneRec.getIssn().isEmpty()) {
                //    lTemp.clear();
                //    //recPredloha.getSimpleField(PredlohaEntity.f_issn).setValue(null);
                //    ritTEMP = recPredloha.getTableField(PredlohaEntity.f_tNepISSN).getTableRecords();
                //    while (ritTEMP.hasMoreRecords()) {
                //        lTemp.add((String) ritTEMP.nextRecord().getFieldValue(NepISSNEntity.f_value));
                //    }
                //    //Nastavíme nové hodnoty
                //    if (oneRec.getIssn() != null) {
                //        for (int i = 0; i < oneRec.getIssn().size(); i++) {
                //            if (i == 0) {
                //                recPredloha.getSimpleField(PredlohaEntity.f_issn).setValue(oneRec.getIssn().get(i));
                //            } else {
                //                if (!lTemp.contains(oneRec.getIssn().get(i))) {
                //                    recPredloha.getTableField(PredlohaEntity.f_tNepISSN).createTableRecord().getSimpleField(NepISSNEntity.f_value).setValue(oneRec.getIssn().get(i));
                //                }
                //            }
                //        }
                //    }
                //}
                
                //ISSN
                if (oneRec.getIssn() != null && !oneRec.getIssn().isEmpty()) {
                    lTemp.clear();
                    //recPredloha.getSimpleField(PredlohaEntity.f_ISBN).setValue(null);
                    ritTEMP = recPredloha.getTableField(PredlohaEntity.f_tNepISSN).getTableRecords();
                    while (ritTEMP.hasMoreRecords()) {
                        lTemp.add((String) ritTEMP.nextRecord().getFieldValue(NepISSNEntity.f_value));
                    }
                    //Nastavíme nové hodnoty ISSN
                    if (oneRec.getIssn() != null) {
                        for (int i = 0; i < oneRec.getIssn().size(); i++) {
                            if (i == 0) {
                                updatesimpleFieldIfSourceNotNullAndTargetIsNull(recPredloha, PredlohaEntity.f_issn, oneRec.getIssn().get(i));
                                //recPredloha.getSimpleField(PredlohaEntity.f_ISBN).setValue(oneRec.getIsbn().get(i));
                            }
                        }
                    }
                }
                
                
                //ISSN neplatné
                if (oneRec.getIssn_Nep() != null && !oneRec.getIssn_Nep().isEmpty()) {
                    lTemp.clear();
                    ritTEMP = recPredloha.getTableField(PredlohaEntity.f_tNepISSN).getTableRecords();
                    while (ritTEMP.hasMoreRecords()) {
                        lTemp.add((String)ritTEMP.nextRecord().getFieldValue(NepISSNEntity.f_value)); 
                    }
                    for (int i = 0; i < oneRec.getIssn_Nep().size(); i++) {
                        if (!lTemp.contains(oneRec.getIssn_Nep(i))) {
                            recPredloha.getTableField(PredlohaEntity.f_tNepISSN).createTableRecord().getSimpleField(NepISSNEntity.f_value).setValue(oneRec.getIssn_Nep(i));
                        }
                    }
                }
                
                String isbnOldValue = "";
                Record recWithOldValue = null;
                //ISBN
                if (oneRec.getIsbn() != null && !oneRec.getIsbn().isEmpty()) {
                    lTemp.clear();
                    isbnOldValue = (String)recPredloha.getSimpleField(PredlohaEntity.f_ISBN).getValue();
                    //recPredloha.getSimpleField(PredlohaEntity.f_ISBN).setValue(null);
                    ritTEMP = recPredloha.getTableField(PredlohaEntity.f_tNepISBN).getTableRecords();
                    while (ritTEMP.hasMoreRecords()) {
                        lTemp.add((String) ritTEMP.nextRecord().getFieldValue(NepISBNEntity.f_value));
                    }
                    //Nastavíme nové hodnoty ISBN
                    if (oneRec.getIsbn() != null) {
                        for (int i = 0; i < oneRec.getIsbn().size(); i++) {
                            if (i == 0) {
                                updatesimpleFieldIfSourceNotNullAndTargetIsNull(recPredloha, PredlohaEntity.f_ISBN, oneRec.getIsbn().get(i));
                                //recPredloha.getSimpleField(PredlohaEntity.f_ISBN).setValue(oneRec.getIsbn().get(i));
                            } else {
                                //if (!lTemp.contains(oneRec.getIsbn().get(i))) {
                                //    recPredloha.getTableField(PredlohaEntity.f_tNepISBN).createTableRecord().getSimpleField(NepISBNEntity.f_value).setValue(oneRec.getIsbn().get(i));
                                //}
                            }
                        }
                    } else if (oneRec.getIsmn() != null) {
                        updatesimpleFieldIfSourceNotNullAndTargetIsNull(recPredloha, PredlohaEntity.f_ISBN, oneRec.getIsmn());
                        //recPredloha.getSimpleField(PredlohaEntity.f_ISBN).setValue(oneRec.getIsmn());
                    }
                }
                
                
                //ISBN neplatné
                if (oneRec.getIsbn_Nep() != null && !oneRec.getIsbn_Nep().isEmpty()) {
                    lTemp.clear();
                    ritTEMP = recPredloha.getTableField(PredlohaEntity.f_tNepISBN).getTableRecords();
                    while (ritTEMP.hasMoreRecords()) {
                        recWithOldValue = ritTEMP.nextRecord();
                        if (isbnOldValue.equals(recWithOldValue.getSimpleField(NepISBNEntity.f_value))) {
                            isbnOldValue = "";
                        }
                        lTemp.add((String)recWithOldValue.getFieldValue(NepISBNEntity.f_value)); 
                    }
                    for (int i = 0; i < oneRec.getIsbn_Nep().size(); i++) {
                        if (isbnOldValue.equals(oneRec.getIsbn_Nep(i))) {
                            isbnOldValue = "";
                        }
                        if (!lTemp.contains(oneRec.getIsbn_Nep(i))) {
                            recPredloha.getTableField(PredlohaEntity.f_tNepISBN).createTableRecord().getSimpleField(NepISBNEntity.f_value).setValue(oneRec.getIsbn_Nep(i));
                        }
                    }
                    if (isbnOldValue != "") {
                        recPredloha.getTableField(PredlohaEntity.f_tNepISBN).createTableRecord().getSimpleField(NepISBNEntity.f_value).setValue(isbnOldValue);
                    }
                }
                
                //čČNB
                recWithOldValue = null;
                String cCnbOldValue = "";
                if (oneRec.getCisloCNB() != null && !oneRec.getCisloCNB().isEmpty()) {
                    cCnbOldValue = (String)recPredloha.getSimpleField(PredlohaEntity.f_cCNB).getValue();
                    //recPredloha.getSimpleField(PredlohaEntity.f_cCNB).setValue(oneRec.getCisloCNB());
                }
                //čČNB neplatné
                if (oneRec.getCisloCNB_nep() != null && !oneRec.getCisloCNB_nep().isEmpty()) {
                    lTemp.clear();
                    ritTEMP = recPredloha.getTableField(PredlohaEntity.f_tNepCCNB).getTableRecords();
                    while (ritTEMP.hasMoreRecords()) {
                        recWithOldValue = ritTEMP.nextRecord();
                        if (cCnbOldValue.equals(recWithOldValue.getSimpleField(NepCCNBEntity.f_value))) {
                            cCnbOldValue = "";
                        }
                        //TODO třesunout do tempu a nezapisovat dokud nebude záznam odcommitován!
                        //lPKeysToDelete.add((PlaantUniqueKey) ritTEMP.nextRecord().getKey());
                        lTemp.add((String) recWithOldValue.getFieldValue(NepCCNBEntity.f_value));
                    }

                    //Nastavíme nové hodnoty
                    for (int i = 0; i < oneRec.getCisloCNB_nep().size(); i++) {
                        if (!lTemp.contains(oneRec.getCisloCNB_nep().get(i))) {
                            recPredloha.getTableField(PredlohaEntity.f_tNepCCNB).createTableRecord().getSimpleField(NepCCNBEntity.f_value).setValue(oneRec.getCisloCNB_nep().get(i));
                        }
                        if (cCnbOldValue.equals(oneRec.getCisloCNB_nep().get(i))) {
                            cCnbOldValue = "";
                        }
                    }
                    if (cCnbOldValue != "") {
                        recPredloha.getTableField(PredlohaEntity.f_tNepCCNB).createTableRecord().getSimpleField(NepCCNBEntity.f_value).setValue(cCnbOldValue);
                    }
                }

                //Variantní název
                if (oneRec.getVariantniNazev() != null && !oneRec.getVariantniNazev().isEmpty()) {
                    lTemp.clear();
                    ritTEMP = recPredloha.getTableField(PredlohaEntity.f_tVarNazev).getTableRecords();
                    while (ritTEMP.hasMoreRecords()) {
                        lTemp.add((String) ritTEMP.nextRecord().getFieldValue(TabVarNazevEntity.f_varNazev));
                    }
                    //Nastavíme nové hodnoty
                    if (oneRec.getVariantniNazev() != null) {
                        for (int i = 0; i < oneRec.getVariantniNazev().size(); i++) {
                            if (!lTemp.contains(oneRec.getVariantniNazev().get(i))) {
                                recPredloha.getTableField(PredlohaEntity.f_tVarNazev).createTableRecord().getSimpleField(TabVarNazevEntity.f_varNazev).setValue(oneRec.getVariantniNazev().get(i));
                            }
                        }
                    }
                }

                //Doplnění URL pro záznamy ve stavu hotovo
                //if (DataStateEnum.HOTOVO.equals(issue.dataState)) {
//                if (oneRec.getUrlCastRokRocnik856() != null && oneRec.getValue856z() == null) {
//                    urlPredloha = recPredloha.getFieldValue(PredlohaEntity.f_url);
//                    urlSource = oneRec.getUrlCastRokRocnik856();
//                    if ((urlSource != null && urlPredloha != null && !((String) urlPredloha).equals((String) urlSource)) || (urlSource != null && urlPredloha == null) || (urlSource == null && urlPredloha != null)) {
//                        recPredloha.getSimpleField(PredlohaEntity.f_ediDateURL).setValue(new Date());
//                    }
//                    recPredloha.getSimpleField(PredlohaEntity.f_url).setValue(oneRec.getUrlCastRokRocnik856());
//                } else if (oneRec.getUrlCastRokRocnik911() != null && oneRec.getValue911z() == null) {
//                    urlPredloha = recPredloha.getFieldValue(PredlohaEntity.f_url);
//                    urlSource = oneRec.getUrlCastRokRocnik911();
//                    if ((urlSource != null && urlPredloha != null && !((String) urlPredloha).equals((String) urlSource)) || (urlSource != null && urlPredloha == null) || (urlSource == null && urlPredloha != null)) {
//                        recPredloha.getSimpleField(PredlohaEntity.f_ediDateURL).setValue(new Date());
//                    }
//                    recPredloha.getSimpleField(PredlohaEntity.f_url).setValue(oneRec.getUrlCastRokRocnik911());
//                }
//                if (oneRec.getUrlNaTitul856() != null && oneRec.getValue856z() == null) {
//                    urlPredloha = recPredloha.getFieldValue(PredlohaEntity.f_urlTitul);
//                    urlSource = oneRec.getUrlNaTitul856();
//                    if ((urlSource != null && urlPredloha != null && !((String) urlPredloha).equals((String) urlSource)) || (urlSource != null && urlPredloha == null) || (urlSource == null && urlPredloha != null)) {
//                        recPredloha.getSimpleField(PredlohaEntity.f_ediDateURL).setValue(new Date());
//                    }
//                    recPredloha.getSimpleField(PredlohaEntity.f_urlTitul).setValue(oneRec.getUrlNaTitul856());
//                } else if (oneRec.getUrlNaTitul911() != null && oneRec.getValue911z() == null) {
//                    urlPredloha = recPredloha.getFieldValue(PredlohaEntity.f_urlTitul);
//                    urlSource = oneRec.getUrlNaTitul911();
//                    if ((urlSource != null && urlPredloha != null && !((String) urlPredloha).equals((String) urlSource)) || (urlSource != null && urlPredloha == null) || (urlSource == null && urlPredloha != null)) {
//                        recPredloha.getSimpleField(PredlohaEntity.f_ediDateURL).setValue(new Date());
//                    }
//                    recPredloha.getSimpleField(PredlohaEntity.f_urlTitul).setValue(oneRec.getUrlNaTitul911());
//                }
                //}
                //Přesunuto na test až sem, aby jsme zkusil jestli to vyřeší problém s dlouhými stringy
                //recPredloha.getSimpleField(PredlohaEntity.f_xml).setValue(oneRec.getRawXML());
//                if (oneRec.getRawXML() != null) {
//                    textLenght = StringUtils.getTextLength(oneRec.getRawXML(), null);
//                    LOG.log(Level.FINEST, "DEBUG - Text lenght: {0}", textLenght);
//                    if (textLenght > Commons.MAX_STRING_LENGHT) {
//                        LOG.log(Level.FINEST, "XML set externaly");
//                        lXmlPart.add(new OneXmlPart((String) recPredloha.getFieldValue(PredlohaEntity.f_idCislo), oneRec.getRawXML()));
//                    } else {
//                        LOG.log(Level.FINEST, "XML set internaly");
//                        recPredloha.getSimpleField(PredlohaEntity.f_xml).setValue(oneRec.getRawXML());
//                    }
//                    recPredloha.getSimpleField(PredlohaEntity.f_ediDateXML).setValue(new Date());
//                }
                //odcommitujeme jednu updatovanou předlohu - musíme commitovat po jedné - out of memory - Plaant
                {
                    if (issue.isTest) {
                        LOG.log(Level.INFO, "!!! ROLLBACK !!!");
                        ctx.rollback();
                    } else {
                        LOG.log(Level.INFO, "!!! COMMIT !!!");
                        ctx.commit();
                    }
                }
            }
            return ReliefImportRepair1Demon.OneRecordImportState.UPDATE;

        } else {
            return ReliefImportRepair1Demon.OneRecordImportState.NOTFOUND;
        }
    }

    /**
     ***************************************************************************
     *
     */
    @Override
    public synchronized void run() {
        LOG.log(Level.INFO, "ReliefImportRepair1Demon start working...");
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
                issue = issueHeap.getOneIssue(IssueStateEnum._4_readyForImport_repair_1);
                if (issue != null) {
                    LOG.log(Level.FINE, "Modul REPAIR1");
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
                LOG.log(Level.FINER, "ReliefImportRepair1Demon goes sleep for {0}", Configuration.getInstance().SLEEP_TIME_DATA_IMPORTER);
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
     * @param rec
     * @param field
     * @param value
     */
    private void updatesimpleFieldIfSourceNotNullAndTargetIsNull(Record rec, String field, Object value) {
        if (value != null && rec.getFieldValue(field) == null) {
            rec.getSimpleField(field).setValue(value);
        }
    }

    /**
     ***************************************************************************
     *
     */
    private enum OneRecordImportState {

        UPDATE, NOTFOUND, CANCELED
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
    private static RecordsIterator findRecordBy4Fields(ExternalContextServer ctx, DbBrowser dbb, String column1, String value1, String column2, String value2, String column3, String value3, String column4, String value4, byte porovnavaciPravidlo) {
        Filter filter = R3FilterTools.getEmptyFilter();
        String cnPredloha = R3Commons.getClassnameWithoutEntity(PredlohaEntity.class.getName());

        try {
            if (column1 != null) {
                R3FilterTools.addFilterRule(filter, dbb, cnPredloha, Filter.AND_OP, 1, column1, porovnavaciPravidlo, value1, 1, true);
            }
            if (column2 != null) {
                R3FilterTools.addFilterRule(filter, dbb, cnPredloha, Filter.AND_OP, 1, column2, porovnavaciPravidlo, value2, 1, true);
            }
            if (column3 != null) {
                R3FilterTools.addFilterRule(filter, dbb, cnPredloha, Filter.AND_OP, 1, column3, porovnavaciPravidlo, value3, 1, true);
            }
            if (column4 != null) {
                R3FilterTools.addFilterRule(filter, dbb, cnPredloha, Filter.AND_OP, 1, column4, porovnavaciPravidlo, value4, 1, true);
            }
            return R3FilterTools.getRecords(ctx, cnPredloha, filter, null, null);
        } catch (RemoteException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (QueryException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }

        return null;
    }

}
