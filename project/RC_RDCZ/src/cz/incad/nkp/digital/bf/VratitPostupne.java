package cz.incad.nkp.digital.bf;

import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import com.amaio.plaant.desk.QueryException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.core.constants.CcoreExemplar;
import cz.incad.core.tools.RecordWorker;
import cz.incad.core.tools.RecordWorkerContext;
import cz.incad.core.tools.ReliefWizardMessage;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/*******************************************************************************
 *
 * @author martin
 */
public class VratitPostupne extends BussinessFunctionMother implements CcoreExemplar {
    private static final String LOCAL_SECURITY = "data002";
    private Vector<String> seznamZadabychLendExemplar = new Vector<String>(0);
    private boolean commited = false;
    private boolean commit = true;
    private int countExemplare = 0;


    /***************************************************************************
     *
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        WizardMessage wm = new WizardMessage();
        RecordWorkerContext rwc = new RecordWorkerContext(getWizardContextClient());

        //TODO Nastaveni defaultni hodnoty pro Siglu podle zarazeni uzivatele.
        //Nastaveni defaultni hodnoty Sigla
        getWizardContextClient().getWizardRecord().getSimpleField("sigla").setValue("ABA001");

        rwc.addRootDomain(CLASSNAME_VYPUJCKA);
        return wm;
    }


    /***************************************************************************
     *
     * @param panelName
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     */
    public WizardMessage panelLeave(String panelName) throws ValidationException, WizardException {
        ReliefWizardMessage wmf = new ReliefWizardMessage();
        String lendExemplar = null;
        String sigla = null;
        String carKod = null;
        Record vypujcka = null;
        RecordsIterator rit = null;
        RecordsIterator ritVypujcky = null;
        RecordWorker rw = new RecordWorker();
        RecordWorkerContext rSeeker = new RecordWorkerContext(getWizardContextClient());
        int countVypujcek = 0;

        //wm.setHtml(true);
        if (commited) {
            wmf.addUpozorneni("Práci s Funkcí jste již ukončili.", "Není možné dále pokračovat v zadávání dalších údajů.", "Okno bude za 3 vteřiny samo zavřeno.", null, null, 0);
            wmf.addLine("<script> setTimeout(\"window.close()\", 3 * 1000); </script>");
            return wmf;
        }

        sigla = (String) getWizardContextClient().getWizardRecord().getFieldValue("sigla");
        carKod = (String) getWizardContextClient().getWizardRecord().getFieldValue(EXEMPLAR_lendExemplar);
        lendExemplar = sigla + carKod;
        //lendExemplar = (String) getWizardContextClient().getWizardRecord().getFieldValue(EXEMPLAR_lendExemplar);
        //if (lendExemplar == null) {
        if (carKod == null) {
            try {
                //PREDCASNY COMMIT ALL do databaze
                wmf = (ReliefWizardMessage) runBusinessMethod();
                commit = false;//pojistka pro pri pad ze se uzivatel rozhodne skoncit na poslednim panelu predcasne.
            } catch (ApplicationErrorException ex) {
                Logger.getLogger(VratitPostupne.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Vycisteni hodnoty, BUG plantu
            getWizardContextClient().getWizardRecord().getSimpleField(EXEMPLAR_lendExemplar).setValue(null);
            return wmf; //Konec BF
        }
        if (seznamZadabychLendExemplar.contains(lendExemplar)) {
            //TODO - wizard message formater
            wmf.addUpozorneni("Tuto kombinaci sigla + čárový kód jste již jednou zadali, bya ignorována.", "Zadaná sigla + čárový kód: " + "<b>" + lendExemplar + "</b>", null, null, null, 0);
            getWizardContextClient().getWizardRecord().getSimpleField(EXEMPLAR_lendExemplar).setValue(null);
            return wmf;
        }
        seznamZadabychLendExemplar.add(lendExemplar);
        try {
            rit = getSelectedRecords(CLASSNAME_EXEMPLAR, EXEMPLAR_lendExemplar + " = '" + lendExemplar + "'");
            switch (rit.getRecordsCount()) {
                case 0 : {
                    wmf.addUpozorneni("Zadaná kombinace sigla + ćárový kód se neshoduje s žádným exemplářem v databázi.", "Zadali jste sigla + čárový kód: <b>" + lendExemplar + "</b>", null, null, null, 0);
                } break;
                case 1 : {//jeden exemplar
                    //exemplar = rit.nextRecord();
                    //zjišťujem jestli je na exemplář aktivní výpůjčka, podle data vrácení, pokud je null, tak výpůjčku považujem za aktivní
                    ritVypujcky = rSeeker.getZaznamy(CLASSNAME_VYPUJCKA, VYPUJCKA_rExemplar + "." + EXEMPLAR_lendExemplar, lendExemplar, "datumVraceni", null, null, null, null, null, null, false);
                    countVypujcek = ritVypujcky.getRecordsCount();
                    if(countVypujcek == 0) {
                        wmf.addUpozorneni("Na zadaný exemplář nebyla nalezena žádná aktivní výpůjčka.", "Sigla + čárový kód exempláře :" + lendExemplar, "Vrácení tohoto exempláře nebude provedeno.", null, null, 0);
                        break;
                    }
                    if(countVypujcek > 1) {
                        wmf.addUpozorneni("Na zadaný exemplář existují " + countVypujcek + " aktivní výpůjčky.", "Sigla + čárový kód exempláře :" + lendExemplar, "Vrácení tohoto exempláře nebude provedeno.", null, null, 0);
                      }
                    vypujcka = ritVypujcky.nextRecord();
                    vypujcka.getSimpleField(VYPUJCKA_stavVypujcky).setValue(STAV_VYPUJCKY_VRACENO);
                    vypujcka.getSimpleField(VYPUJCKA_datumVraceni).setValue(new Date());
                    rw.setZWizarda(vypujcka, true);
                    countExemplare++;
                } break;
                default : {//více exemplářů se stejným LendExemplar
                    wmf.addUpozorneni("V databázi bylo nalezeno více aktivnívh výpůjček na zadaný exemplář.", "Sigla + čárový kód exempláře :" + lendExemplar, "Vrácení tohoto exempláře nebude provedeno.", "Nejprve musíte opravit duplicitu v databázi!", null, 0);
                } break;
            }
        } catch (QueryException ex) {
            ex.printStackTrace();
        }

        //Vycisteni hodnoty, BUG plantu
        getWizardContextClient().getWizardRecord().getSimpleField(EXEMPLAR_lendExemplar).setValue(null);
        return wmf;
    }


    /***************************************************************************
     *
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.WizardException
     * @throws com.amaio.plaant.businessFunctions.ApplicationErrorException
     */
    public WizardMessage runBusinessMethod() throws ValidationException, WizardException, ApplicationErrorException {
        //WizardMessage wm = new WizardMessage();
        ReliefWizardMessage wmf = new ReliefWizardMessage();

        if (commit && !commited) { // podmínka pro commit
            commited = true;
            getWizardContextClient().commit();
        }
        wmf.addInfo("Probíhá zpracování zadaných údajů.<br>Od tohoto okamžiku není možné zadávat další údaje.<br><br>Statistika funkce:<br>","Počet vrácených exemplářů: " + countExemplare, "Funkce byla ukončena.", null, null, 0);
        return wmf;
    }


}
