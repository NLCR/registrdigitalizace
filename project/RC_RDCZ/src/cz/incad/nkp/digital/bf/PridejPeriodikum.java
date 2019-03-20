package cz.incad.nkp.digital.bf;

import com.amaio.plaant.businessFunctions.ApplicationErrorException;
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.businessFunctions.WizardException;
import com.amaio.plaant.businessFunctions.WizardMessage;
import com.amaio.plaant.desk.QueryException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.bf.BussinessFunctionMother;
import cz.incad.core.tools.DirectConnection;
import cz.incad.core.tools.RecordWorker;
import cz.incad.core.tools.RecordWorkerContext;
import cz.incad.core.tools.ReliefLogger;
import cz.incad.core.tools.ReliefWizardMessage;
import cz.incad.core.tools.Utilities;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*******************************************************************************
 *
 * @author martin
 */
public class PridejPeriodikum extends BussinessFunctionMother {
    private static final String LOCAL_SECURITY = "data112";
    private Record mikrofilm = null;
    private Record exemplar;
    private Record periodikum;
    private String carKod;

    /***************************************************************************
     *
     * @return
     * @throws ValidationException
     * @throws WizardException
     */
    public WizardMessage startWizard() throws ValidationException, WizardException {
        securityStartWizard(LOCAL_SECURITY);
        ReliefWizardMessage rwm = new ReliefWizardMessage();

        if (getWizardContextClient().getSelectedKeys().length != 1) {
            throw new WizardException("Pro tuto funkci musí být označen jeden pracovní záznam.");
        }

        // získáme vybraný záznam mikrofilmu se kterým se bude nadále pracovat.
        mikrofilm = getWizardContextClient().getSelectedRecords().nextRecord();

        // kontrola na stav záznamu, jestli se nesnaží pracovat s uzavřeným záznamem.
        if (!((String)(mikrofilm.getSimpleField("stavRec").getValue())).equals("active")) throw new WizardException("Vybraný mikrofilm není ve stavu ACTIVE.");

        rwm.addInfo("::: Informace o mikrofilmu :::", "Archivní Číslo negativu: " + mikrofilm.getSimpleField("archCisloNeg").getValue(), "", "", null, 0); //"NKP.BF.zalozMikrofilm.maxPole.check"

        //TODO Nastaveni defaultni hodnoty pro Siglu podle zarazeni uzivatele.
        //Nastaveni defaultni hodnoty Sigla
        getWizardContextClient().getWizardRecord().getSimpleField("sigla").setValue("ABA001");

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
        String sigla;
        //String carKod;
        String lendExemplar;
        ReliefWizardMessage rwm = new ReliefWizardMessage();
        RecordsIterator ritExemplare = null;
        RecordWorkerContext rwc = new RecordWorkerContext(getWizardContextClient());
        

        sigla = (String) getWizardContextClient().getWizardRecord().getFieldValue("sigla");
        carKod = (String) getWizardContextClient().getWizardRecord().getFieldValue(EXEMPLAR_lendExemplar);
        lendExemplar = sigla + carKod;

        try {
            ritExemplare = rwc.getZaznamy(CLASSNAME_EXEMPLAR, EXEMPLAR_lendExemplar, lendExemplar, null, Boolean.FALSE);
            if (!ritExemplare.hasMoreRecords()) {
                throw new ValidationException("Zadaná kombinace SIGLA + čárový kód nebyla nalezena v databázi.");
            }
            if (ritExemplare.getRecordsCount() > 1) {
                throw new ValidationException("Pod zadaným čárovým kódem bylo nalezeno více exemplárů. Opravte tuto databázovou nekonzistenci. -- " + lendExemplar);
            }
            // Máme jeden exemplář se kterým budeme nadále pracovat.
            exemplar = ritExemplare.nextRecord();
            // hledám periodikum, které jsou k danému exempláři připojené
            if (exemplar.getReferencedField(EXEMPLAR_rTitul).getValue() != null) {
                periodikum = exemplar.getReferencedField(EXEMPLAR_rTitul).getReferencedRecord();
            } else {
                throw new WizardException("Exemplář nemá referenci na hlavní Titul. Opravte tuto databázovou nekonzistenci. -- " + lendExemplar);
            }

            rwm.addLine("Periodikum nalezené pod zadaným čárovým kódem:");
            rwm.addLine(Utilities.trimText((String)periodikum.getSimpleField(TITUL_hlNazev).getValue(), 100));
        } catch (QueryException ex) {
            ReliefLogger.throwing("Vyjimka při požadavku o záznam z agendy.", "", ex);
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
        Record tXTitNkpMf = null;
        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;
        String sql = "select max(poradiVeSvitku) from XTitNkpMf where rMikrofilmXT = '" + RecordWorker.getRecordID(mikrofilm.getKey()) + "'";
        int poradiVeSvitku = 0;

        //hledáme nejvyšší pořadí ve svitku k danému mikrofilmu
        try {
            con = DirectConnection.getConnection();
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                poradiVeSvitku = rs.getInt(1);
            }
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (con != null) con.close();
        }
        catch (SQLException exc) {
            ReliefLogger.throwing("", "", exc);
        }
        //tXTitNkpMf - bude se nastavovat poradi na svitku...nejvyssi z tech ktere jsou uz pripojene.
            //nastavíme u záznamu periodikum zadané odhadované počty polí.
            //monografieTemp.getSimpleField("pocetPoli").setValue(new BigDecimal(vPocetPoli.get(i)));
            //vytvoříme nový záznam v agendě tXTitNkpMf což je vazevbí tabulka mezi mikrofilmem a titNkp čímž se sváže mikrofilm s vazební tabulkou
            tXTitNkpMf = mikrofilm.getTableField("tXTitNkpMf").createTableRecord();
            //svážeme tXTitNkpMf s mikrofilm
            tXTitNkpMf.getReferencedField("rMikrofilm").setKey(mikrofilm.getKey());
            // svážeme tXTitNkpMf s titNkp
            tXTitNkpMf.getReferencedField("rTitNkp").setKey(periodikum.getKey());
            // svážeme titNkp s tXTitNkpMf
            periodikum.getTableField("tXTitNkpMf").addKey(tXTitNkpMf.getKey());
            // nastavíme pořadí ve svitku
            tXTitNkpMf.getSimpleField("poradiVeSvitku").setValue(++poradiVeSvitku);
            // nastavíme carKod
            tXTitNkpMf.getSimpleField("carKod").setValue(carKod);

        // závěrečný commit do databáze
        getWizardContextClient().commit();
        return rwm;
    }

}
