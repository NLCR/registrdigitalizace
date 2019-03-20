/*
 * ZakazkaEntity.java
 *
 * Created on 27. duben 2006, 9:31
 * The class and its source code is a property of Ing. Pavel Vedral (C) 2006.
 */

package cz.incad.nkp.digital;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.UpdateException;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.RecordEntity;
//import cz.incad.core.constants.CcoreDstavRec;
//import cz.incad.core.constants.CcoreRecord;
import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;
import cz.incad.core.tools.ReliefLogger;
import cz.incad.core.tools.ReliefUser;
import cz.incad.nkp.digital.constants.CnkpSecurity;
import cz.incad.nkp.digital.constants.CnkpUkol;
import cz.incad.nkp.digital.constants.CnkpZakazka;
import cz.incad.nkp.digital.constants.ConstBasic_NKP;
import java.util.Vector;


/*******************************************************************************
 * 
 * @author <a href="mailto:pve@casd.cz">Ing. Pavel Vedral</a>
 * @version $Revision: 1.1 $
 */
public class ZakazkaEntity extends RecordEntity implements ConstBasic_NKP, CnkpZakazka, CnkpSecurity, CnkpUkol {
    public static final String CLASSNAME = "cz.incad.nkp.digital.Zakazka";
    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.DATA;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {SECURITY_ROLE_knavkorektor, SECURITY_ROLE_knavpracovnik, SECURITY_ROLE_appzakazka};
    private static final String SECURITY_POLE_HLIDANA[] = {
        ZAKAZKA_tUkol,
        ZAKAZKA_rTitNkp,
        ZAKAZKA_cisloZakazky,
        ZAKAZKA_rozsah,
        ZAKAZKA_rozsahOd,
        ZAKAZKA_rozsahDo,
        ZAKAZKA_xml,
        ZAKAZKA_obrazky,
        ZAKAZKA_tVlastnik,
        RECORD_stavRec,
        RECORD_poznRec,
        RECORD_stavPozn,
        ZAKAZKA_scanovanoStran,
        ZAKAZKA_dpi,
        ZAKAZKA_stupneSedi};


    /***************************************************************************
     * 
     * @param rec
     * @return
     */
    @Override
    public Record onGetRecord(Record rec) {
        securityOnGetRecord(rec, SECURITY_DESK_TYPE, SECURITY_POLE_HLIDANA);
        return rec;
    }


    /***************************************************************************
     * 
     * @param rec vsupní záznam
     * @param poleHlidana
     * @param vRecordFieldsSecurity
     * @param canErase
     */
    @Override
    protected boolean setCustomSecurityOnGetRecord(Record rec, String[] poleHlidana, Vector<Integer> vRecordFieldsSecurity, boolean canErase, String kompetence) {
        // Guru agnedy
        if (kompetence.contains(SECURITY_ROLE_appzakazka)) {
            mergeSecurity(vRecordFieldsSecurity, 2,2,2,2,2,2,2,2,2,2,2,2,2,2,2);
            canErase = Boolean.TRUE;
        }

        //Kompetence - knavkorektor
        if (kompetence.contains(SECURITY_ROLE_knavkorektor)) {
            mergeSecurity(vRecordFieldsSecurity, 2,2,2,2,2,2,2,2,2,2,2,2,2,2,2);
            canErase = Boolean.TRUE;
        }

        //Kompetence - knavpracovnik
        if (kompetence.contains(SECURITY_ROLE_knavpracovnik)) {
            mergeSecurity(vRecordFieldsSecurity, 2,2,2,2,2,2,2,2,2,2,2,2,2,2,2);
        }

        return canErase;
    }


    /***************************************************************************
     * Volá metodu předka a nastavuje lokální Security
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


    /***************************************************************************
     * 
     * @param rec vsupní záznam
     * @return zpracovaný vstupní záznam
     * @throws com.amaio.plaant.businessFunctions.AddException
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     */
    @Override
    public Record onCreate(Record rec) throws AddException, ValidationException {
        super.onCreate(rec);
        Object cisloZakazky = null;
        ReliefUser ru = new ReliefUser(getTriggerContext());
        RecordsIterator rit;
        Object value;

        cisloZakazky = rec.getSimpleField(ZAKAZKA_CISLO_ZAKAZKY_FLD).getValue();
        if (cisloZakazky == null) {
            try {
                if (ru.getZarazeni().equalsIgnoreCase("zustk")) {
                    return setCisloZakazky(rec, getFormattedNextNumber("Zakazka.cisloZakazkySTK", ""));
                }
                if (ru.getZarazeni().equalsIgnoreCase("zuknav")) {
                    return setCisloZakazky(rec, getFormattedNextNumber("Zakazka.cisloZakazkyKNAV", ""));
                }
                if (ru.getZarazeni().equalsIgnoreCase("zumzk")) {
                    return setCisloZakazky(rec, getFormattedNextNumber("Zakazka.cisloZakazkyMZK", ""));
                }
                if (ru.getZarazeni().equalsIgnoreCase("zumkp")) {
                    return setCisloZakazky(rec, getFormattedNextNumber("Zakazka.cisloZakazkyMKP", ""));
                }
                if (ru.getZarazeni().equalsIgnoreCase("zuPNA001")) {
                    return setCisloZakazky(rec, getFormattedNextNumber("Zakazka.cisloZakazkyPNA001", ""));
                }
                if (ru.getZarazeni().equalsIgnoreCase("zunkphostivar")) {
                    if (rec.getTableField(ZAKAZKA_tUkol) != null) {
                        rit = rec.getTableField(ZAKAZKA_tUkol).getTableRecords();
                        while (rit.hasMoreRecords()) {
                            value = rit.nextRecord().getSimpleField(UKOL_typPrace).getValue();
                            if (value != null) {
                                if ("skenovaniZMikrofilmu".equals(value) || "primeSkenovani".equals(value)) {
                                    return setCisloZakazky(rec, getFormattedNextNumber("Zakazka.cisloZakazky", ""));
                                }
                            }
                        }
                    }
                }

                return rec;
            }
            catch (com.amaio.plaant.sync.NoSuchFieldException exc) {
                ReliefLogger.throwing("Chyba pri vybirani dlasiho cisla z Ciselne rady: ", "", exc);
            }
        }

        onCreateUpdate(rec);
        return rec;
    }


    /***************************************************************************
     *
     * @param rec
     * @return
     * @throws ValidationException
     * @throws UpdateException
     */
    @Override
    public Record onUpdate(Record rec) throws ValidationException, UpdateException {
        super.onUpdate(rec);
        onCreateUpdate(rec);
        return rec;
    }


    /***************************************************************************
     *
     * @param rec
     * @throws ValidationException
     */
    private void onCreateUpdate(Record rec) throws ValidationException {
        if (("finished".equals(rec.getSimpleField(RECORD_stavRec).getValue())) && (rec.getSimpleField(ZAKAZKA_udirdcz).getValue() == null)) {
            rec.getSimpleField(ZAKAZKA_udirdcz).setValue(getNextNumber("UDIRDCZ"));
        }
    }


    /***************************************************************************
     *
     * @param zakazka
     * @param cisloZakazky
     * @return
     */
    private Record setCisloZakazky(Record zakazka, Object cisloZakazky) {
        zakazka.getSimpleField(ZAKAZKA_CISLO_ZAKAZKY_FLD).setValue(cisloZakazky);
        return zakazka;
    }


}
