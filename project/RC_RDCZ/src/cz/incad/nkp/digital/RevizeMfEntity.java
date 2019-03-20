/* *****************************************************************************
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package cz.incad.nkp.digital;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.RecordEntity;
import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;
import cz.incad.nkp.digital.constants.CnkpRevizeMf;
import cz.incad.nkp.digital.constants.CnkpSecurity;
import java.util.Date;
import java.util.Vector;

/**
 *******************************************************************************
 * @author mno
 * <b>Class description:</b>
 *
 */
public class RevizeMfEntity extends RecordEntity implements CnkpRevizeMf, CnkpSecurity {

    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.DATA;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {SECURITY_ROLE_fotograf};
    private static final String SECURITY_POLE_HLIDANA[] = {
        REVIZEMF_dMin,
        REVIZEMF_datumRevize,
        REVIZEMF_defekty,
        REVIZEMF_denzitaKonec,
        REVIZEMF_denzitaZac,
        REVIZEMF_endMf,
        REVIZEMF_faktorRoz,
        REVIZEMF_ldB,
        REVIZEMF_ldC,
        REVIZEMF_ldP,
        REVIZEMF_lhB,
        REVIZEMF_lhC,
        REVIZEMF_lhP,
        REVIZEMF_pdB,
        REVIZEMF_pdC,
        REVIZEMF_pdP,
        REVIZEMF_phB,
        REVIZEMF_phC,
        REVIZEMF_phP,
        REVIZEMF_rMikrofilm,
        REVIZEMF_revizor,
        REVIZEMF_sB,
        REVIZEMF_sC,
        REVIZEMF_sP,
        REVIZEMF_startMf,
        REVIZEMF_techPredloha,
        RECORD_stavPozn,
        RECORD_stavRec,
        RECORD_zalDate,
        RECORD_zalUser};

    /**
     ***************************************************************************
     *
     * @param rec
     * @return
     */
    @Override
    public Record onGetRecord(Record rec) {
        securityOnGetRecord(rec, SECURITY_DESK_TYPE, SECURITY_POLE_HLIDANA);
        return rec;
    }

    /**
     ***************************************************************************
     *
     * @param rec
     * @param poleHlidana
     * @param vRecordFieldsSecurity
     * @param canErase
     */
    @Override
    protected boolean setCustomSecurityOnGetRecord(Record rec, String[] poleHlidana, Vector<Integer> vRecordFieldsSecurity, boolean canErase, String kompetence) {
        Object oStavZaznamu;

        //Kontrola na to jestli je validacni pole stavRec vyplnene nejakou hodnotou
        //null znamenalo crash aplikace
        oStavZaznamu = rec.getSimpleField(RECORD_stavRec).getValue();
        if (oStavZaznamu == null) {
            mergeSecurity(vRecordFieldsSecurity, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            return canErase;
        }

        //získáme stav záznamu
        String stavZaznamu = (String) rec.getSimpleField(RECORD_stavRec).getValue();

        //Stav active - Novy
        if (stavZaznamu.equals(REVIZEMF_STAV_active)) {
            //Kompetence - Priprava mikrofilmu
            if (kompetence.contains(SECURITY_ROLE_pripravamikrofilmu)) {
                //ReliefLogger.severe("Nastavuji active - priprava mikrofilmu");
                mergeSecurity(vRecordFieldsSecurity, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
            }
            //Kompetence - Fotograf-revizor
            if (kompetence.contains(SECURITY_ROLE_fotografRevizor)) {
                //ReliefLogger.severe("Nastavuji active - fotografrevizor");
                mergeSecurity(vRecordFieldsSecurity, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
            }
            //Kompetence - Fotograf
            if (kompetence.contains(SECURITY_ROLE_fotograf)) {
                //ReliefLogger.severe("Nastavuji active - fotograf");
                mergeSecurity(vRecordFieldsSecurity, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
            }
        }

        //Stav progress - Zpracovavan
        if (stavZaznamu.equals(REVIZEMF_STAV_progress)) {
            //Kompetence - Priprava mikrofilmu
            if (kompetence.contains(SECURITY_ROLE_pripravamikrofilmu)) {
                //ReliefLogger.severe("Nastavuji progress - priprava mikrofilmu");
                mergeSecurity(vRecordFieldsSecurity, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
            }
            //Kompetence - Fotograf-revizor
            if (kompetence.contains(SECURITY_ROLE_fotografRevizor)) {
                //ReliefLogger.severe("Nastavuji progress - fotograf");
                canErase = Boolean.TRUE;
                mergeSecurity(vRecordFieldsSecurity, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2);
            }
            //Kompetence - Fotograf
            if (kompetence.contains(SECURITY_ROLE_fotograf)) {
                //ReliefLogger.severe("Nastavuji progress - fotograf");
                canErase = Boolean.TRUE;
                mergeSecurity(vRecordFieldsSecurity, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2);
            }
        }

        //Stav finished - Dokonceno
        if (stavZaznamu.equals(REVIZEMF_STAV_finished)) {
            mergeSecurity(vRecordFieldsSecurity, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
        }
        return canErase;
    }

    /**
     ***************************************************************************
     * Nastavuje defaultní hodnoty
     *
     * @param rec
     * @return
     * @throws com.amaio.plaant.businessFunctions.AddException
     */
    @Override
    public Record onCreateLocal(Record rec) throws AddException {
        super.onCreateLocal(rec);
        securityOnCreateLocal(rec, SECURITY_DESK_TYPE, SECURITY_CAN_CREATE_COMPETITIONS);
        //nastaveni dnesniho datumu
        rec.getSimpleField("datumRevize").setValue(new Date());
        return rec;
    }

}
