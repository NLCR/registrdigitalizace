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
import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;
import cz.incad.core.lend.CtenarEntity;
import cz.incad.nkp.digital.constants.CnkpCtenarNKP;
import cz.incad.nkp.digital.constants.CnkpSecurity;
import java.util.Vector;

/**
 *******************************************************************************
 * @author mno
 * <b>Class description:</b>
 *
 */
public class CtenarNKPEntity extends CtenarEntity implements CnkpCtenarNKP, CnkpSecurity {

    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.DATA;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {SECURITY_ROLE_pripravamikrofilmu, SECURITY_ROLE_knavpracovnik};
    private static final String SECURITY_POLE_HLIDANA[] = {
        CTENARNKP_financovano,
        CTENARNKP_tAtypPrace,
        CTENARNKP_zpracovatel,
        CTENARNKP_zpusobDig,
        CTENAR_akTitulPred,
        CTENAR_akTitulZa,
        CTENAR_cKatCtenar,
        CTENAR_hrisnik,
        CTENAR_jmeno,
        CTENAR_lendCtenar,
        CTENAR_prijmeni,
        CTENAR_rKatCtenar,
        CTENAR_tAdresa,
        CTENAR_tKontakt,
        CTENAR_tRezervace,
        CTENAR_tVypujcka,
        CTENAR_zakazVypujcek,};

    /**
     ***************************************************************************
     *
     * @param rec vsupní záznam
     * @return zpracovaný vstupní záznam
     */
    @Override
    public Record onGetRecord(Record rec) {
        securityOnGetRecord(rec, SECURITY_DESK_TYPE, SECURITY_POLE_HLIDANA);
        return rec;
    }

    /**
     ***************************************************************************
     * Implementace Lokálních Security pro trigger onGetRecord Metoda není
     * mplementována
     *
     * @param rec vsupní záznam
     * @param poleHlidana seznam hlídaných polí
     * @param vRecordFieldsSecurity vektor securit odpovídající seznamu
     * hlídaných polí
     * @param canErase Boolean - Default FALSE - Nastavuje, zdali uživatel bude
     * moci záznam smazat
     */
    @Override
    protected boolean setCustomSecurityOnGetRecord(Record rec, String[] poleHlidana, Vector<Integer> vRecordFieldsSecurity, boolean canErase, String kompetence) {
        return canErase;
    }

    /**
     ***************************************************************************
     * @param rec vsupní záznam
     * @return zpracovaný vstupní záznam
     * @throws com.amaio.plaant.businessFunctions.AddException
     */
    @Override
    public Record onCreateLocal(Record rec) throws AddException {
        super.onCreateLocal(rec);
        securityOnCreateLocal(rec, SECURITY_DESK_TYPE, SECURITY_CAN_CREATE_COMPETITIONS);
        return rec;
    }

}
