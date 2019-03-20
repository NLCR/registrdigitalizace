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
import cz.incad.nkp.digital.constants.CnkpSecurity;
import cz.incad.nkp.digital.constants.CnkpTFMonografie;
import java.util.Vector;

/**
 *******************************************************************************
 *
 * @author martin
 */
public class TFHudebninyEntity extends TitNkpEntity implements CnkpTFMonografie, CnkpSecurity {

    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.DATA;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {SECURITY_ROLE_knavkorektor, SECURITY_ROLE_knavpracovnik, SECURITY_ROLE_apphudebniny};
    private static final String SECURITY_POLE_HLIDANA[] = {
        TITNKP_autorJmeno,
        TITNKP_autorPrij,
        TITNKP_cast,
        TITNKP_financovano,
        TITNKP_inverze,
        TITNKP_issn,
        TITNKP_opravaBibZaz,
        TITNKP_pocetPoli,
        TITNKP_rozsahTit,
        TITNKP_tSoubXML,
        TITNKP_tXTitNkpMf,
        TITNKP_tZakazka,
        TITNKP_zpusobDig,
        TITNKP_idNumber,
        TITNKP_idNumberAleph,
        TITNKP_formatAleph,
        TITNKP_url,
        TITNKP_marcxml,
        TITNKP_vlastnik,
        TITUL_hlNazev,
        TITUL_importCSV,
        TITUL_lendTitul,
        TITUL_listTematika,
        TITUL_mistoVyd,
        TITUL_pocetStran,
        //TITUL_prirCislo,
        TITUL_rInsVydavatel,
        TITUL_rokVyd,
        TITUL_tCFyz,
        TITUL_tCISBN,
        TITUL_tCJazyk,
        TITUL_tCKlicSlovo,
        TITUL_tCNazev,
        TITUL_tCZeme,
        TITUL_tExemplar,
        TITUL_tPrivazek,
        TITUL_tXTiAutor,
        TITUL_tXTiInst,
        TITUL_vyskaVcm,
        TITNKP_bazeAleph,
        RECORD_idCislo,
        RECORD_poznRec,
        RECORD_stavPozn,
        RECORD_stavRec,
        TITNKP_digitalniKnihovna,
        TITNKP_lccKlasifikace,
        TITNKP_issn2,
        TITNKP_issn3,
        TITNKP_isbn};

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
        // Guru agnedy
        if (kompetence.contains(SECURITY_ROLE_apphudebniny)) {
            mergeSecurity(vRecordFieldsSecurity, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2);
            canErase = Boolean.TRUE;
        }

        //Kompetence - Fotograf
        if (kompetence.contains(SECURITY_ROLE_fotograf)) {
            mergeSecurity(vRecordFieldsSecurity, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
        }

        //Kompetence - knavkorektor
        if (kompetence.contains(SECURITY_ROLE_knavkorektor)) {
            mergeSecurity(vRecordFieldsSecurity, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2);
            canErase = Boolean.TRUE;
        }

        //Kompetence - knavpracovnik
        if (kompetence.contains(SECURITY_ROLE_knavpracovnik)) {
            mergeSecurity(vRecordFieldsSecurity, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2);
        }

        return canErase;
    }

    /**
     ***************************************************************************
     *
     * @param rec
     * @return
     * @throws com.amaio.plaant.businessFunctions.AddException
     */
    @Override
    public Record onCreateLocal(Record rec) throws AddException {
        super.onCreateLocal(rec);
        securityOnCreateLocal(rec, SECURITY_DESK_TYPE, SECURITY_CAN_CREATE_COMPETITIONS);
        return rec;
    }

}
