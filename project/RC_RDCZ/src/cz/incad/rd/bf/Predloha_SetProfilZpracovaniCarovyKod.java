/*******************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.rd.bf;

import com.amaio.plaant.businessFunctions.AddException;
//import com.amaio.plaant.businessFunctions.AnnotationKeys;
import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.RecordNotFoundException;
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.ValidationException;
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
import cz.incad.core.tools.Utilities;
import cz.incad.rd.PredlohaEntity;
import cz.incad.rd.ProfilEntity;
import cz.incad.rd.SkenerEntity;
//import cz.incad.rd.list.SpecificDynamicLists;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/*******************************************************************************
 *
 * @author martin
 */
public class Predloha_SetProfilZpracovaniCarovyKod extends BussinessFunctionMother {
    private static final String LOCAL_SECURITY = "data116";
    private Record recProfil;
    private Record recSkener;
    boolean useVarka;
    String varka = null;
    int countZadaneCarKody = 0;
    int countIgnorovaneCarKody = 0;
    int countCelkemPredloh = 0;
    List<UniqueKey> listPredlohyPUK = new LinkedList<UniqueKey>();
    List<String> listAllCarKody = new LinkedList<String>();
    public static final String f_profil         = "profil";
    public static final String f_useVarka       = "useVarka";
    public static final String f_varka          = "varka";
    public static final String f_carKod         = "carKod";

    /***************************************************************************
     * 
     * @return
     * @throws ValidationException
     * @throws WizardException
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        ReliefWizardMessage rwm = new ReliefWizardMessage();
        ReliefUser ru = new ReliefUser(getWizardContextClient());

        //Nastavíme defaultní hodnoty formuláře
        if ("zunkphostivar".equals(ru.getZarazeni())) {
            getWizardContextClient().getWizardRecord().getSimpleField(Predloha_SetProfilZpracovaniCarovyKod.f_useVarka).setValue(true);
        }
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
    public WizardMessage panelLeave(String panelName) throws ValidationException, WizardException {
        ReliefWizardMessage rwm = new ReliefWizardMessage();
        RecordWorkerContext rwc = new RecordWorkerContext(getWizardContextClient());
        ReliefUser ru = new ReliefUser(getWizardContextClient());
        RecordsIterator ritProfil;
        RecordsIterator ritPredlohy;
        RecordsIterator ritSkener;
        Record recPredloha;
        String allFoundPredlohy;
        String allExitedTituly;
        String carKod;
        int countActualCarkodPredloha = 0;
        ValidationException vex;

        WizardUI wui = getWizardContextClient().getBusinessMethod().getWizardUI();
        ArrayList<WizardPanel> wPanels = wui.getWizardPanels();

        if ("panel01".equals(panelName)) {
            try {
                //Získáme záznam Profilu
                //ritProfil = rwc.getZaznamy(ProfilEntity.CLASSNAME, ProfilEntity.f_value, getWizardContextClient().getWizardRecord().getSimpleField(Predloha_SetProfilZpracovaniCarovyKod.f_profil).getValue().toString(), ProfilEntity.f_securityOwner, ru.getZarazeni(), null, false);
                ritProfil = rwc.getZaznamy(ProfilEntity.CLASSNAME, ProfilEntity.f_value, getWizardContextClient().getWizardRecord().getSimpleField(Predloha_SetProfilZpracovaniCarovyKod.f_profil).getValue().toString(), null, false);
                if (!ritProfil.hasMoreRecords()) throw new WizardException("Vámi vybraný profil zpracování je neplatný. Prosím opravte tuto chybu v agendě Profil zpracování.");
                recProfil = ritProfil.nextRecord();

                //Získáme profil Skeneru
                if (recProfil.getSimpleField(ProfilEntity.f_skenerProfil).getValue() != null) {
                    ritSkener = rwc.getZaznamy(SkenerEntity.CLASSNAME, SkenerEntity.f_value, recProfil.getSimpleField(ProfilEntity.f_skenerProfil).getValue().toString(), null, false);
                    if (!ritSkener.hasMoreRecords()) throw new WizardException("Vámi vybraný profil zpracování má zadaný již neplatný Profil Skeneru. Prosím opravte tuto chybu v agendě Profil zpracování.");
                    recSkener = ritSkener.nextRecord();
                }
            } catch (QueryException ex) {
                ReliefLogger.severe("Chyba při vyzvedávání profilu zpracování.", ex);
                throw new WizardException("Kontaktujte dodavatele software. SQL vyjímka: " + ex.getMessage());
            }

            //Načteme další hodnoty z formuláře
            this.useVarka = (Boolean)getWizardContextClient().getWizardRecord().getSimpleField(Predloha_SetProfilZpracovaniCarovyKod.f_useVarka).getValue();
            if (this.useVarka) {
                this.varka = (String)getWizardContextClient().getWizardRecord().getSimpleField(Predloha_SetProfilZpracovaniCarovyKod.f_varka).getValue();
                if (this.varka == null) {
                    vex = Utilities.getValidationException(getWizardContextClient());
                    vex.addField("Várka", "Máte zaškrtnuto, že chcete nahradit hodnotu 'Várka' v profilu zpracování, ale nezadali jste novou hodnotu.", false);
                    throw vex;
                }
            }

        } else if ("panel02".equals(panelName)) {

            //otestujeme zdali je CK null a když ne tak přidáme další panel.
            if (getWizardContextClient().getWizardRecord().getSimpleField(Predloha_SetProfilZpracovaniCarovyKod.f_carKod).getValue() != null) {
                wPanels.add(wPanels.size() -1, wPanels.get(1));
                getWizardContextClient().getWizardController().rebuildWizard();

                //Získáme čárkód
                carKod = (String)getWizardContextClient().getWizardRecord().getSimpleField(Predloha_SetProfilZpracovaniCarovyKod.f_carKod).getValue();
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
                            allExitedTituly = "";
                            countActualCarkodPredloha = 0;
                            while (ritPredlohy.hasMoreRecords()) {
                                recPredloha = ritPredlohy.nextRecord();
                                
                                //Pravilo pro národní knihovnu
                                if ("zunkphostivar".equals(ru.getZarazeni())) {
                                    ReliefLogger.severe("ZPRACOVAVAME JAKO NK");
                                    if (recPredloha.getSimpleField(PredlohaEntity.f_cisloZakazky).getValue() == null) {
                                        countActualCarkodPredloha++;
                                        if (!listPredlohyPUK.contains(recPredloha.getKey())) {
                                            listPredlohyPUK.add(recPredloha.getKey());
                                            countCelkemPredloh++;
                                        }
                                        allFoundPredlohy += "[" + countActualCarkodPredloha + "] " + recPredloha.getSimpleField(PredlohaEntity.f_nazev).getValue() + "<br><br>";
                                    } else {
                                        allExitedTituly += recPredloha.getSimpleField(PredlohaEntity.f_nazev).getValue() + "<br><br>";
                                        //Předlo má číslo zakázky a tudíž byla pravděpodobně již zpracována
                                        
                                    }
                                } else {
                                    countActualCarkodPredloha++;
                                    if (!listPredlohyPUK.contains(recPredloha.getKey())) {
                                        listPredlohyPUK.add(recPredloha.getKey());
                                        countCelkemPredloh++;
                                    }
                                    allFoundPredlohy += "[" + countActualCarkodPredloha + "] " + recPredloha.getSimpleField(PredlohaEntity.f_nazev).getValue() + "<br><br>";
                                }
                            }
                            //Vypsání nalezených/zamítnutých titulů
                            if (!allFoundPredlohy.isEmpty()) {
                                rwm.addInfo("::: Předlohy nalezené pod čárovým kódem: " + carKod + " :::", allFoundPredlohy, null, null, null, 0);
                            }
                            if (!allExitedTituly.isEmpty()) {
                                rwm.addUpozorneni("::: Předlohy vyřazené ze zpracování, jelikož již mají přidělené číslo zakázky :::", allExitedTituly, null, null, null, 0);
                            }
                        }
                    } catch (QueryException ex) {
                        ReliefLogger.severe("Chyba pri vyzvedavani predlohy na zaklade caroveho kodu.", ex);
                        throw new WizardException("SQL vyjímka: " + ex.getMessage());
                    }

                } else {
                    rwm.addUpozorneni("Tento čárový kód jste již jednou zadali. Čárový kód bude ignorován.", "ČK: " + carKod, null, null, null, 0);
                    countIgnorovaneCarKody++;
                }
                //Vynulování čárového kódu ve formuláři wizarda
                getWizardContextClient().getWizardRecord().getSimpleField(Predloha_SetProfilZpracovaniCarovyKod.f_carKod).setValue(null);

            } else {
                //Zadali jste CarKod = NULL -- Konec Panelů
                rwm.addInfo("::: Sumarizace :::", "Počet zadaných čárových kódů: " + countZadaneCarKody, "Počet ignorovaných čárových kódů: " + countIgnorovaneCarKody, "Počet zpracovaných předloh: " + countCelkemPredloh, null, 0);
            }

        } else if ("panel03".equals(panelName)) {
            //Zaverecna stranka na ktere je vypsana sumarizace předchozího zpracování.
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

        for (int i = 0; i < listPredlohyPUK.size(); i++) {
            getWizardContextClient().addRootDomain(rwc.getDomenu(PredlohaEntity.CLASSNAME));
            try {
                recPredloha = getWizardContextClient().getRecord((PlaantUniqueKey) listPredlohyPUK.get(i));
                Predloha_SetProfilZpracovaniSelectedRecords.doOnePredloha(recPredloha, this.recProfil, this.recSkener, this.varka,getWizardContextClient());
            } catch (RecordNotFoundException ex) {
                ReliefLogger.severe("Chyba pri ukladani zaznamu.", ex);
                getWizardContextClient().rollback();
                rwm.addChyba("Při závěrečném zpracování došlo k chybě u následujícího záznamu, který proto nebyl zpracován: ", "Čárový kód: " + recPredloha.getSimpleField(PredlohaEntity.f_carKod), "Název: " + recPredloha.getSimpleField(PredlohaEntity.f_nazev), null, null, 0);
            } catch (AddException ex) {
                ReliefLogger.severe("Chyba pri ukladani zaznamu.", ex);
                getWizardContextClient().rollback();
                rwm.addChyba("Při závěrečném zpracování došlo k chybě u následujícího záznamu, který proto nebyl zpracován: ", "Čárový kód: " + recPredloha.getSimpleField(PredlohaEntity.f_carKod), "Název: " + recPredloha.getSimpleField(PredlohaEntity.f_nazev), null, null, 0);
            }
            getWizardContextClient().commit();
        }
        return rwm;
    }

}
