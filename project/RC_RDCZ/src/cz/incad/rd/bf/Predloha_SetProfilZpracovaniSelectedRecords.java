/*******************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.rd.bf;

import com.amaio.plaant.businessFunctions.AddException;
//import com.amaio.plaant.businessFunctions.AnnotationKeys;
import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.RecordsArray;
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardContextClient;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import com.amaio.plaant.desk.QueryException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.core.tools.RecordWorkerContext;
import cz.incad.core.tools.ReliefUser;
import cz.incad.core.tools.ReliefWizardMessage;
import cz.incad.rd.DalsiPraceEntity;
import cz.incad.rd.PredaniEntity;
import cz.incad.rd.PredlohaEntity;
import cz.incad.rd.ProfilEntity;
import cz.incad.rd.SkenerEntity;
//import cz.incad.rd.list.SpecificDynamicLists;

/*******************************************************************************
 *
 * @author martin
 */
public class Predloha_SetProfilZpracovaniSelectedRecords extends BussinessFunctionMother {
    private static final String LOCAL_SECURITY = "data117";
    public static final String f_profil = "profil";

    /***************************************************************************
     * 
     * @return
     * @throws ValidationException
     * @throws WizardException
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        ReliefWizardMessage rwm = new ReliefWizardMessage();

        RecordsIterator ritPredlohy = getWizardContextClient().getSelectedRecords();
        if (!ritPredlohy.hasMoreRecords()) throw new WizardException("Pro tuto funkci musí být vybrán alespoň jeden záznam.");

        rwm.addInfo("Počet vybraných záznamů ke zpracování: " + ritPredlohy.getRecordsCount(), null, null, null, null, 0);
        //Nastaveni hodnot dynamickeho listu
        //getWizardContextClient().getWizardRecord().getSimpleField(f_profil).setAnnotation(AnnotationKeys.LIST_SOURCE_CUSTOM_PROPERTY, new SpecificDynamicLists(new ReliefUser(getWizardContextClient())));
        return rwm;
    }

    /***************************************************************************
     *
     * @param string
     * @return
     * @throws ValidationException
     * @throws WizardException
     */
    public WizardMessage panelLeave(String string) throws ValidationException, WizardException {
        if (getWizardContextClient().getWizardRecord().getSimpleField("profil").getValue() == null) {
            throw new WizardException("Pole nesmí být prázdné, musíte vybrat profil zpracování.");
        }
        return null;
    }

    /***************************************************************************
     *
     * @return
     * @throws ValidationException
     * @throws WizardException
     * @throws ApplicationErrorException
     */
    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        ReliefWizardMessage rwm = new ReliefWizardMessage();
        RecordWorkerContext rwc = new RecordWorkerContext(getWizardContextClient());
        Record recProfil;
        Record recSkener = null;
        Record recPredloha;
        RecordsIterator ritProfil;
        RecordsIterator ritSkener;
        RecordsIterator ritPredlohy;

        try {
            //Získáme profil zpracování
            ritProfil = rwc.getZaznamy(ProfilEntity.CLASSNAME, ProfilEntity.f_value, getWizardContextClient().getWizardRecord().getSimpleField("profil").getValue().toString(), null, false);
            if (!ritProfil.hasMoreRecords()) throw new WizardException("V databázi nebyl nalezen vámi vybraný profil.");
            recProfil = ritProfil.nextRecord();

            //Získáme profil Skeneru
            if (recProfil.getSimpleField(ProfilEntity.f_skenerProfil).getValue() != null) {
                ritSkener = rwc.getZaznamy(SkenerEntity.CLASSNAME, SkenerEntity.f_value, recProfil.getSimpleField(ProfilEntity.f_skenerProfil).getValue().toString(), null, false);
                if (!ritSkener.hasMoreRecords()) throw new WizardException("Vámi vybraný profil zpracování má zadaný již neplatný Profil Skeneru. Prosím opravte tuto chybu v agendě Profil zpracování.");
                recSkener = ritSkener.nextRecord();
            }

            ritPredlohy = getWizardContextClient().getSelectedRecords();
            while (ritPredlohy.hasMoreRecords()) {
                recPredloha = ritPredlohy.nextRecord();

                try {
                    doOnePredloha(recPredloha, recProfil, recSkener, null,getWizardContextClient());
                } catch(Exception ex) {
                    ex.printStackTrace();
                    //rollback + vypis u kereho zaznamu se to nepovedlo
                    rwm.addChyba("CHYBA", null, null, null, null, 0);
                }
            }

        } catch (QueryException ex) {
            ex.printStackTrace();
            getWizardContextClient().rollback();
            throw new WizardException("SQL vyjímka: " + ex.getMessage());
        }

        getWizardContextClient().commit();
        return rwm;
    }

//    /***************************************************************************
//     * 
//     * @param rwm
//     * @param recProfilZpracovani
//     * @return
//     */
//    public static ReliefWizardMessage createProfilZpracovaniInfo(ReliefWizardMessage rwm, Record recProfilZpracovani) {
//
//        return rwm;
//    }

    /***************************************************************************
     *
     * @param recPredloha
     * @param recProfil
     * @param recSkener
     * @param wcc
     * @throws AddException
     * @throws ApplicationErrorException
     */
    public static void doOnePredloha(Record recPredloha, Record recProfil, Record recSkener, String varkaCover, WizardContextClient wcc) throws AddException, ApplicationErrorException {
        ReliefUser ru = new ReliefUser(wcc);
        RecordWorkerContext rwc = new RecordWorkerContext(wcc);
        Record recDalsiPraceNew;
        Record recPredaniNew;
        RecordsArray ritDalsiPraceProfil = recProfil.getTableField("tDalsiPrace").getTableRecordsArray();
        RecordsArray ritDalsiPracePredloha;

        //nastavíme jméno poižitého profilu
        recPredloha.getSimpleField(PredlohaEntity.f_lProfil).setValue(recProfil.getSimpleField(ProfilEntity.f_value).getValue()); 

        //Hlavička
        if ((Boolean)recProfil.getSimpleField(ProfilEntity.f_headCheck).getValue()) {
            recPredloha.getSimpleField(PredlohaEntity.f_financovano).setValue(recProfil.getSimpleField(ProfilEntity.f_financovano).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_zpusobDig).setValue(recProfil.getSimpleField(ProfilEntity.f_zpusobDig).getValue());
        }

        //Příprava
        if ((Boolean)recProfil.getSimpleField(ProfilEntity.f_pripCheck).getValue()) {
            recPredloha.getSimpleField(PredlohaEntity.f_pripStav).setValue(recProfil.getSimpleField(ProfilEntity.f_pripStav).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_pripPrac).setValue(recProfil.getSimpleField(ProfilEntity.f_pripPrac).getValue());
        }

        //Mikrofilmování
        if ((Boolean)recProfil.getSimpleField(ProfilEntity.f_mikroCheck).getValue()) {
            recPredloha.getSimpleField(PredlohaEntity.f_mikroStav).setValue(recProfil.getSimpleField(ProfilEntity.f_mikroStav).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_mikroPrac).setValue(recProfil.getSimpleField(ProfilEntity.f_mikroPrac).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_archNeg).setValue(recProfil.getSimpleField(ProfilEntity.f_archNeg).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_archNegPrac).setValue(recProfil.getSimpleField(ProfilEntity.f_archNegPrac).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_matNegativ).setValue(recProfil.getSimpleField(ProfilEntity.f_matNegativ).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_matNegativPrac).setValue(recProfil.getSimpleField(ProfilEntity.f_matNegativPrac).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_pozitiv).setValue(recProfil.getSimpleField(ProfilEntity.f_pozitiv).getValue());
        }

        //Skenování
        if ((Boolean)recProfil.getSimpleField(ProfilEntity.f_skenCheck).getValue()) {
            recPredloha.getSimpleField(PredlohaEntity.f_skenStav).setValue(recProfil.getSimpleField(ProfilEntity.f_skenStav).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_skenPrac).setValue(recProfil.getSimpleField(ProfilEntity.f_skenPrac).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_rozliseni).setValue(recProfil.getSimpleField(ProfilEntity.f_rozliseni).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_barevnaHloubka).setValue(recProfil.getSimpleField(ProfilEntity.f_barevnaHloubka).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_barevnaSkala).setValue(recProfil.getSimpleField(ProfilEntity.f_barevnaSkala).getValue());
            //recPredloha.getSimpleField(PredlohaEntity.f_druhDigitalizace).setValue(recProfil.getSimpleField(ProfilEntity.f_druhDigitalizace).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_parSkenovani).setValue(recProfil.getSimpleField(ProfilEntity.f_parSkenovani).getValue());
            //recPredloha.getSimpleField(PredlohaEntity.f_skenSkenZMik).setValue(recProfil.getSimpleField(ProfilEntity.f_skenSkenZMikCheck).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_skenDJVU).setValue(recProfil.getSimpleField(ProfilEntity.f_skenDJVU).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_skenJPEG).setValue(recProfil.getSimpleField(ProfilEntity.f_skenJPEG).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_skenGIF).setValue(recProfil.getSimpleField(ProfilEntity.f_skenGIF).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_skenTIFF).setValue(recProfil.getSimpleField(ProfilEntity.f_skenTIFF).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_skenPDF).setValue(recProfil.getSimpleField(ProfilEntity.f_skenPDF).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_skenTXT).setValue(recProfil.getSimpleField(ProfilEntity.f_skenTXT).getValue());

//Presunuto do nasledujici sekce
//            recPredloha.getSimpleField(PredlohaEntity.f_skenOCR).setValue(recProfil.getSimpleField(ProfilEntity.f_skenOCRCheck).getValue());
//            recPredloha.getSimpleField(PredlohaEntity.f_skenOCRPrac).setValue(recProfil.getSimpleField(ProfilEntity.f_skenOCRPrac).getValue());
            //Zpracování profilu skeneru
            if (recSkener != null) {
                //TODO, upravit tak aby to respektovalo lokalizaci
                recPredloha.getSimpleField(PredlohaEntity.f_skenerProfil).setValue(recSkener.getSimpleField(SkenerEntity.f_value).getValue());
                recPredloha.getSimpleField(PredlohaEntity.f_modelSkeneru).setValue(recSkener.getSimpleField(SkenerEntity.f_modelSkeneru).getValue());
                recPredloha.getSimpleField(PredlohaEntity.f_pouzitySW).setValue(recSkener.getSimpleField(SkenerEntity.f_pouzitySW).getValue());
                recPredloha.getSimpleField(PredlohaEntity.f_typSkeneru).setValue(recSkener.getSimpleField(SkenerEntity.f_typSkeneru).getValue());
                recPredloha.getSimpleField(PredlohaEntity.f_verzeSW).setValue(recSkener.getSimpleField(SkenerEntity.f_verzeSW).getValue());
                recPredloha.getSimpleField(PredlohaEntity.f_vlastnikSkeneru).setValue(recSkener.getSimpleField(SkenerEntity.f_vlastnik).getValue());
                recPredloha.getSimpleField(PredlohaEntity.f_vyrobceSW).setValue(recSkener.getSimpleField(SkenerEntity.f_vyrobceSW).getValue());
                recPredloha.getSimpleField(PredlohaEntity.f_vyrobceSkeneru).setValue(recSkener.getSimpleField(SkenerEntity.f_vyrobceSkeneru).getValue());
            }

            //Doplnění čísla zakázky
            if (recPredloha.getSimpleField(PredlohaEntity.f_cisloZakazky).getValue() == null) {
                //Národní knihovna
                if ("zunkphostivar".equals(recPredloha.getSimpleField(PredlohaEntity.f_securityOwner).getValue())) {
                    //mame záznam NK který nemá zakázkové číslo.
                    if (recPredloha.getSimpleField(PredlohaEntity.f_zpusobDig).getValue() != null && !"primDestrukt".equals(recProfil.getSimpleField(ProfilEntity.f_zpusobDig).getValue())) {
                        recPredloha.getSimpleField(PredlohaEntity.f_cisloZakazky).setValue(getFormattedNextNumber("Zakazka.cisloZakazky", "", wcc));
                    }
                }
            }
        }

        //OCR
        if ((Boolean)recProfil.getSimpleField(ProfilEntity.f_ocrCheck).getValue()) {
            recPredloha.getSimpleField(PredlohaEntity.f_skenOCR).setValue(recProfil.getSimpleField(ProfilEntity.f_skenOCRCheck).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_skenOCRPrac).setValue(recProfil.getSimpleField(ProfilEntity.f_skenOCRPrac).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_skenOCRSvabach).setValue(recProfil.getSimpleField(ProfilEntity.f_skenOCRSvabach).getValue());
        }

        //Metadata
        if ((Boolean)recProfil.getSimpleField(ProfilEntity.f_metaCheck).getValue()) {
            recPredloha.getSimpleField(PredlohaEntity.f_metaStav).setValue(recProfil.getSimpleField(ProfilEntity.f_metaStav).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_metaPrac).setValue(recProfil.getSimpleField(ProfilEntity.f_metaPrac).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_metaEditor).setValue(recProfil.getSimpleField(ProfilEntity.f_metaEditor).getValue());
        }

        //Zpracování
        if ((Boolean)recProfil.getSimpleField(ProfilEntity.f_zpracCheck).getValue()) {
            recPredloha.getSimpleField(PredlohaEntity.f_zpracStav).setValue(recProfil.getSimpleField(ProfilEntity.f_zpracStav).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_zpracPrac).setValue(recProfil.getSimpleField(ProfilEntity.f_zpracPrac).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_zpracEditor).setValue(recProfil.getSimpleField(ProfilEntity.f_zpracEditor).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_zpracParm).setValue(recProfil.getSimpleField(ProfilEntity.f_zpracParm).getValue());
        }

        //Zveřejnění
        if ((Boolean)recProfil.getSimpleField(ProfilEntity.f_publCheck).getValue()) {
            recPredloha.getSimpleField(PredlohaEntity.f_publStav).setValue(recProfil.getSimpleField(ProfilEntity.f_publStav).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_publPrac).setValue(recProfil.getSimpleField(ProfilEntity.f_publPrac).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_digKnihovna).setValue(recProfil.getSimpleField(ProfilEntity.f_digKnihovna).getValue());
        }

        //Archivace
        if ((Boolean)recProfil.getSimpleField(ProfilEntity.f_archCheck).getValue()) {
            recPredloha.getSimpleField(PredlohaEntity.f_archStav).setValue(recProfil.getSimpleField(ProfilEntity.f_archStav).getValue());
            recPredloha.getSimpleField(PredlohaEntity.f_archPrac).setValue(recProfil.getSimpleField(ProfilEntity.f_archPrac).getValue());
        }

        //Předání
        if ((Boolean)recProfil.getSimpleField(ProfilEntity.f_predatCheck).getValue()) {
            recPredaniNew = wcc.create(rwc.getDomenu(PredaniEntity.CLASSNAME));
            //připojíme záznam
            recPredloha.getTableField(PredlohaEntity.t_tPredani).addKey(recPredaniNew.getKey());
            recPredaniNew.getSimpleField(PredaniEntity.f_kdo).setValue(ru.getZarazeni());
            recPredaniNew.getSimpleField(PredaniEntity.f_komuOdKoho).setValue(recProfil.getSimpleField(ProfilEntity.f_predatKomuOdKoho).getValue());
            recPredaniNew.getSimpleField(PredaniEntity.f_typAkce).setValue(recProfil.getSimpleField(ProfilEntity.f_predTypAkce).getValue());
            if (varkaCover != null) {
                recPredaniNew.getSimpleField(PredaniEntity.f_varka).setValue(varkaCover);
            } else {
                recPredaniNew.getSimpleField(PredaniEntity.f_varka).setValue(recProfil.getSimpleField(ProfilEntity.f_predatVarka).getValue());
            }
        }

        //Další práce
//        //Smazání starých záznamů
//        if ((Boolean)recProfil.getSimpleField(ProfilEntity.f_dalsiPraceClearFirst).getValue()) {
//
//        } else {
//
//        }

        ritDalsiPracePredloha = recPredloha.getTableField(PredlohaEntity.t_tDalsiPrace).getTableRecordsArray();
        labelouterloop:
        for (int i = 0; i < ritDalsiPraceProfil.getSize(); i++) {
            //Zjištujeme zdali daný úkol již není u předlohy zadán.
            for (int j = 0; j < ritDalsiPracePredloha.getSize(); j++) {
                if (ritDalsiPraceProfil.getRecord(i).getSimpleField(DalsiPraceEntity.f_typPrace).getValue() == null) {
                    continue labelouterloop;
                }
                if (ritDalsiPraceProfil.getRecord(i).getSimpleField(DalsiPraceEntity.f_typPrace).getValue().toString().equals(ritDalsiPracePredloha.getRecord(j).getSimpleField(DalsiPraceEntity.f_typPrace).getValue())) {
                    continue labelouterloop;
                }
            }
            recDalsiPraceNew = wcc.create(rwc.getDomenu(DalsiPraceEntity.CLASSNAME));
            //Propojení na předlohu
            recPredloha.getTableField(PredlohaEntity.t_tDalsiPrace).addKey(recDalsiPraceNew.getKey());
            recDalsiPraceNew.getSimpleField(DalsiPraceEntity.f_typPrace).setValue(ritDalsiPraceProfil.getRecord(i).getSimpleField(DalsiPraceEntity.f_typPrace).getValue());
            recDalsiPraceNew.getSimpleField(DalsiPraceEntity.f_pracePrac).setValue(ritDalsiPraceProfil.getRecord(i).getSimpleField(DalsiPraceEntity.f_pracePrac).getValue());
            recDalsiPraceNew.getSimpleField(DalsiPraceEntity.f_praceStav).setValue(ritDalsiPraceProfil.getRecord(i).getSimpleField(DalsiPraceEntity.f_praceStav).getValue());
        }
    }

}
