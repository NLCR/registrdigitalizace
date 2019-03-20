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
import cz.incad.nkp.digital.constants.CnkpKopieMf;
import cz.incad.nkp.digital.constants.CnkpSecurity;
import java.util.Date;
import java.util.Vector;

/**
 *******************************************************************************
 * @author mno
 * <b>Class description:</b>
 *
 */
public class KopieMfEntity extends RecordEntity implements CnkpKopieMf, CnkpSecurity {

    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.DATA;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {SECURITY_ROLE_fotografRevizor};
    private static final String SECURITY_POLE_HLIDANA[] = {
        KOPIEMF_datumPredani,
        KOPIEMF_datumVytvoreni,
        //KOPIEMF_datumZpracovani,
        KOPIEMF_objednavatel,
        KOPIEMF_typMf,
        KOPIEMF_zpracovatel,
        RECORD_stavRec,
        RECORD_poznRec,
        RECORD_stavPozn};

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
        /* Kompetence - Fotograf Korektor */
        if (kompetence.contains(SECURITY_ROLE_fotografRevizor)) {
            canErase = Boolean.TRUE;
            mergeSecurity(vRecordFieldsSecurity, 2, 2, 2, 2, 2, 2, 2, 2);
        }
        /* Kompetence - Fotograf */
        if (kompetence.contains(SECURITY_ROLE_fotograf)) {
            mergeSecurity(vRecordFieldsSecurity, 1, 1, 1, 1, 1, 1, 1, 1);
        }
        return canErase;
    }

    /**
     ***************************************************************************
     * Nastavuje defaultni hodnoty
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
        rec.getSimpleField(KOPIEMF_datumVytvoreni).setValue(new Date());

        return rec;
    }

}