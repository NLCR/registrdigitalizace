/*******************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.rd.bf;

import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardContextClient;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import com.amaio.plaant.dbdef.WizardUI;
import com.amaio.plaant.dbdef.WizardUI.WizardPanel;
import com.amaio.plaant.desk.QueryException;
import com.amaio.plaant.desk.container.PlaantUniqueKey;
import com.amaio.plaant.sync.Record;
import com.amaio.plaant.sync.UniqueKey;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.core.tools.RecordWorkerContext;
import cz.incad.core.tools.ReliefLogger;
import cz.incad.core.tools.ReliefUser;
import cz.incad.core.tools.ReliefWizardMessage;
import cz.incad.rd.PredaniEntity;
import cz.incad.rd.PredlohaEntity;
import cz.incad.rd.ProfilEntity;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/*******************************************************************************
 *
 * @author martin
 */
public class Predloha_PredatCarovyKod extends BussinessFunctionMother {
     private static final String LOCAL_SECURITY = "data114";
     public static final String f_komu = "komu";
     public static final String f_varka = "varka";
     public static final String f_carKod = "carKod";

     private String komu = "";
     private String varka = "";
     private int countZadaneCarKody = 0;
     private int countIgnorovaneCarKody = 0;
     private int countCelkemPredloh = 0;
     List<UniqueKey> listPredlohyPUK = new LinkedList<UniqueKey>();
     List<String> listAllCarKody = new LinkedList<String>();


    /***************************************************************************
     *
     * @return
     * @throws ValidationException
     * @throws WizardException
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        ReliefWizardMessage rwm = new ReliefWizardMessage();

        return rwm;
    }

    /***************************************************************************
     *
     * @param panelName
     * @return
     * @throws ValidationException
     * @throws WizardException
     */
    public WizardMessage panelLeave(String panelName) throws ValidationException, WizardException {
        ReliefWizardMessage rwm = new ReliefWizardMessage();
        WizardUI wui = getWizardContextClient().getBusinessMethod().getWizardUI();
        ArrayList<WizardPanel> wPanels = wui.getWizardPanels();
        String carKod;
        String allFoundPredlohy;
        RecordsIterator ritPredlohy;
        RecordWorkerContext rwc = new RecordWorkerContext(getWizardContextClient());
        ReliefUser ru = new ReliefUser(getWizardContextClient());
        int countActualCarkodPredloha;
        Record recPredloha;

        if ("panel01".equals(panelName)) {
            this.komu = (String) getWizardContextClient().getWizardRecord().getSimpleField(Predloha_PredatCarovyKod.f_komu).getValue();
            this.varka = (String) getWizardContextClient().getWizardRecord().getSimpleField(Predloha_PredatCarovyKod.f_varka).getValue();

        } else if ("panel02".equals(panelName)) {
            //otestujeme zdali je CK null a když ne tak přidáme další panel.
            ReliefLogger.severe("VALUE: " + getWizardContextClient().getWizardRecord().getSimpleField(Predloha_PredatCarovyKod.f_carKod).getValue());
            if (getWizardContextClient().getWizardRecord().getSimpleField(Predloha_PredatCarovyKod.f_carKod).getValue() != null) {
                wPanels.add(wPanels.size() -1, wPanels.get(1));
                getWizardContextClient().getWizardController().rebuildWizard();

                //Získáme čárkód
                carKod = (String) getWizardContextClient().getWizardRecord().getSimpleField(Predloha_PredatCarovyKod.f_carKod).getValue();
                countZadaneCarKody++;

                if (!listAllCarKody.contains(carKod)) {
                    listAllCarKody.add(carKod);

                    try {
                        ritPredlohy = rwc.getZaznamy(PredlohaEntity.CLASSNAME, PredlohaEntity.f_carKod, carKod, ProfilEntity.f_securityOwner, ru.getZarazeni(), null, false);
                        //Kontrola na to jestli Byla nalezena předloha s tímto čárovým kódem
                        if (!ritPredlohy.hasMoreRecords()) {
                            countIgnorovaneCarKody++;
                            rwm.addUpozorneni("V databázi nebyla nalezena kniha se zadaným čárovým kódem !", "Čárový kód: " + carKod, null, null, null, 0);
                        } else {
                            allFoundPredlohy = "";
                            countActualCarkodPredloha = 0;
                            while (ritPredlohy.hasMoreRecords()) {
                                recPredloha = ritPredlohy.nextRecord();

                                countActualCarkodPredloha++;
                                if (!listPredlohyPUK.contains(recPredloha.getKey())) {
                                    listPredlohyPUK.add(recPredloha.getKey());
                                    countCelkemPredloh++;
                                }
                                allFoundPredlohy += "[" + countActualCarkodPredloha + "] " + recPredloha.getSimpleField(PredlohaEntity.f_nazev).getValue() + "<br><br>";
                            }

                            //Vypsání nalezených titulů
                            if (!allFoundPredlohy.isEmpty()) {
                                rwm.addInfo("::: Předlohy nalezené pod čárovým kódem: " + carKod + " :::", allFoundPredlohy, null, null, null, 0);
                            }
                        }
                    } catch (QueryException ex) {
                        ReliefLogger.severe("Chyba pri vyzvedavani predlohy na zaklade caroveho kodu.", ex);
                        throw new WizardException("Nastala neočekávaná chyba při zpracování, kontaktujte dodavatele aplikace.");
                    }

                } else {
                    rwm.addUpozorneni("Tento čárový kód jste již jednou zadali. Čárový kód bude ignorován.", "ČK: " + carKod, null, null, null, 0);
                    countIgnorovaneCarKody++;
                }
                //Vynulování čárového kódu ve formuláři wizarda
                getWizardContextClient().getWizardRecord().getSimpleField(Predloha_PredatCarovyKod.f_carKod).setValue(null);

            } else {
                //Zadali jste CarKod = NULL -- Konec Panelů
                rwm.addInfo("::: Sumarizace :::", "Počet zadaných čárových kódů: " + countZadaneCarKody, "Počet ignorovaných čárových kódů: " + countIgnorovaneCarKody, "Počet zpracovaných předloh: " + countCelkemPredloh, null, 0);
            }

        } else if ("panel03".equals(panelName)) {
            
        }

        return rwm;
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
        Record recPredloha = null;

        //Zpracování všech nalezených záznamů
        for (int i = 0; i < listPredlohyPUK.size(); i++) {
            getWizardContextClient().addRootDomain(rwc.getDomenu(PredlohaEntity.CLASSNAME));
            try {
                recPredloha = getWizardContextClient().getRecord((PlaantUniqueKey) listPredlohyPUK.get(i));
                Predloha_PredatCarovyKod.doOnePredaniPrevzeti(recPredloha, "predani", this.komu, this.varka, getWizardContextClient());
            } catch (Exception ex) {
                ReliefLogger.severe("Chyba pri ukladani zaznamu.", ex);
                getWizardContextClient().rollback();
                rwm.addChyba("Při závěrečném zpracování došlo k chybě u následujícího záznamu, který proto nebyl zpracován: ", "Čárový kód: " + recPredloha.getSimpleField(PredlohaEntity.f_carKod), "Název: " + recPredloha.getSimpleField(PredlohaEntity.f_nazev), null, null, 0);
            }
            getWizardContextClient().commit();
        }

        return rwm;
    }

    /***************************************************************************
     *
     * @param recPredloha
     * @param typAkce
     * @param predatKomuOdKoho
     * @param varka
     * @param wcc
     * @return
     */
    public static boolean doOnePredaniPrevzeti(Record recPredloha, String typAkce, String predatKomuOdKoho, String varka, WizardContextClient wcc) {
        Record recPredaniNew;
        RecordWorkerContext rwc = new RecordWorkerContext(wcc);
        ReliefUser ru = new ReliefUser(wcc);

        try {
            recPredaniNew = wcc.create(rwc.getDomenu(PredaniEntity.CLASSNAME));

            //připojíme záznam
            recPredloha.getTableField(PredlohaEntity.t_tPredani).addKey(recPredaniNew.getKey());
            recPredaniNew.getSimpleField(PredaniEntity.f_kdo).setValue(ru.getZarazeni());
            recPredaniNew.getSimpleField(PredaniEntity.f_komuOdKoho).setValue(predatKomuOdKoho);
            recPredaniNew.getSimpleField(PredaniEntity.f_typAkce).setValue(typAkce);
            recPredaniNew.getSimpleField(PredaniEntity.f_varka).setValue(varka);
        } catch(Exception ex) {
            ReliefLogger.severe("Chyba při předání převzetí.", ex);
            return false;
        }

        return true;
    }

}
