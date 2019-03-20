/*******************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.rd.bf;

import com.amaio.plaant.businessFunctions.AddException;
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
import cz.incad.core.tools.ReliefUser;
import cz.incad.core.tools.ReliefWizardMessage;
import cz.incad.core.tools.Utilities;
import cz.incad.rd.PredlohaEntity;
import cz.incad.rd.XPredMikroEntity;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*******************************************************************************
 *
 * @author martin
 */
public class Mikrofilm_PridejTitul extends BussinessFunctionMother {
    private static final String LOCAL_SECURITY = "data113";
    private Record recMikrofilm;

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
            throw new WizardException("Pro tuto funkci musí být označen právě jeden záznam Mikrofilmu.");
        }

        // získáme vybraný záznam mikrofilmu se kterým se bude nadále pracovat.
        recMikrofilm = getWizardContextClient().getSelectedRecords().nextRecord();

        // kontrola na stav záznamu, jestli se nesnaží pracovat s uzavřeným záznamem.
        if (!((String)(recMikrofilm.getSimpleField("stavRec").getValue())).equals("active")) throw new WizardException("Vybraný mikrofilm není ve stavu ACTIVE.");

        rwm.addInfo("::: Informace o mikrofilmu :::", "Archivní Číslo negativu: " + recMikrofilm.getSimpleField("archCisloNeg").getValue(), "", "", null, 0); //"NKP.BF.zalozMikrofilm.maxPole.check"

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
        ValidationException vex;
        ReliefWizardMessage rwm = new ReliefWizardMessage();

        if (getWizardContextClient().getWizardRecord().getSimpleField("carKod").getValue() == null) {
            vex = Utilities.getValidationException(getWizardContextClient());
            vex.addField("Čárováý kód", "Pole nesmí být prázdné.", false);
            throw vex;
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
        ReliefUser ru = new ReliefUser(getWizardContextClient());
        Connection conn = DirectConnection.getConnection();
        RecordsIterator ritTituly;
        Record recPredloha;
        Record recXPredMikro;
        Statement stmtSQL;
        ResultSet rsPoradiVevitku;
        int poradiVeSvitku = 0;

        getWizardContextClient().addRootDomain(rwc.getDomenu("cz.incad.rd.XPredMikro"));
        try {
            ritTituly = rwc.getZaznamy(PredlohaEntity.CLASSNAME, "carKod", (String)getWizardContextClient().getWizardRecord().getSimpleField("carKod").getValue(), "securityOwner", ru.getZarazeni(), null, false);
            
            //nstavíme pořadí ve vitku
            try {
                stmtSQL = conn.createStatement();
                rsPoradiVevitku = stmtSQL.executeQuery("select max(poradivesvitku) from xpredmikro where rmikrofilm = " + RecordWorker.getRecordID(recMikrofilm.getKey()));
                rsPoradiVevitku.next();
                poradiVeSvitku = rsPoradiVevitku.getInt(1);

                rsPoradiVevitku.close();
                stmtSQL.close();
                conn.close();
            } catch(SQLException ex) {
                ex.printStackTrace();
                poradiVeSvitku = 0;
            }

            //máme všechny tituly s odpovídajícím čárovým kódem
            while (ritTituly.hasMoreRecords()) {
                //připojíme všechny předlohy namikrofilm
                recPredloha = ritTituly.nextRecord();
                
                //založíme novou vazební tabulku X...
                recXPredMikro = getWizardContextClient().create(rwc.getDomenu(XPredMikroEntity.CLASSNAME));
                //propojkime s predlohou
                recXPredMikro.getReferencedField(XPredMikroEntity.r_rPredloha).setKey(recPredloha.getKey());
                //propojíme s mikrofilmem
                recXPredMikro.getReferencedField(XPredMikroEntity.r_rMikrofilm).setKey(recMikrofilm.getKey());
                //nastavíme pořadí ve svitku
                recXPredMikro.getSimpleField(XPredMikroEntity.f_poradiVeSvitku).setValue(++poradiVeSvitku);
                //Nastavíme čárový kód
                recXPredMikro.getSimpleField(XPredMikroEntity.f_carKod).setValue(getWizardContextClient().getWizardRecord().getSimpleField("carKod").getValue());
            }
        } catch(QueryException ex) {
            ex.printStackTrace();
            getWizardContextClient().rollback();
            throw new WizardException("SQL vyjímka: " + ex.getMessage());
        } catch(AddException ex) {
            ex.printStackTrace();
            getWizardContextClient().rollback();
            throw new WizardException("SQL vyjímka: " + ex.getMessage());
        }

        getWizardContextClient().commit();
        return rwm;
    }

}
