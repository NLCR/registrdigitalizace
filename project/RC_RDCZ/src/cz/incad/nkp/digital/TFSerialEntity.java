/*
 * TFSerialEntity.java
 *
 * Created on 26. duben 2006, 16:31
 * The class and its source code is a property of Ing. Pavel Vedral (C) 2006.
 */

package cz.incad.nkp.digital;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;
import cz.incad.nkp.digital.constants.CnkpSecurity;
import cz.incad.nkp.digital.constants.CnkpTFSerial;
import java.util.Vector;


/**
 *
 * @author <a href="mailto:pve@casd.cz">Ing. Pavel Vedral</a>
 * @version $Revision: 1.3 $
 */
public class TFSerialEntity extends TitNkpEntity implements CnkpTFSerial, CnkpSecurity{
    public static final String NS_PREFIX = "PE";
    public static final String NS_NAME = "TFSerial.cisloZaznamu";
    
    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.DATA;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {SECURITY_ROLE_knavkorektor, SECURITY_ROLE_knavpracovnik, SECURITY_ROLE_appperiodika};
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


    /***************************************************************************************************************************************
     * 
     * @param rec
     * @return
     */
    @Override
    public Record onGetRecord(Record rec) {
        securityOnGetRecord(rec, SECURITY_DESK_TYPE, SECURITY_POLE_HLIDANA);
        return rec;
    }


    /***************************************************************************************************************************************
     * 
     * @param rec
     * @param poleHlidana
     * @param vRecordFieldsSecurity
     * @param canErase
     */
    @Override
    protected boolean setCustomSecurityOnGetRecord(Record rec, String[] poleHlidana, Vector<Integer> vRecordFieldsSecurity, boolean canErase, String kompetence) {
        // Guru agnedy
        if (kompetence.contains(SECURITY_ROLE_appperiodika)) {
            mergeSecurity(vRecordFieldsSecurity, 2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2);
            canErase = Boolean.TRUE;
        }

        //Kompetence - knavkorektor
        if (kompetence.contains(SECURITY_ROLE_knavkorektor)) {
            mergeSecurity(vRecordFieldsSecurity, 2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2);
            canErase = Boolean.TRUE;
        }

        //Kompetence - knavpracovnik
        if (kompetence.contains(SECURITY_ROLE_knavpracovnik)) {
            mergeSecurity(vRecordFieldsSecurity, 2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2);
        }

        return canErase;
    }


    /***************************************************************************************************************************************
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
