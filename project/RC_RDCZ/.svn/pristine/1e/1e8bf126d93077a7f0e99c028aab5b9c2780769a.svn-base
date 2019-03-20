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
import com.amaio.plaant.businessFunctions.RecordsIterator;
import com.amaio.plaant.businessFunctions.UpdateException;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.RecordEntity;
import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;
import cz.incad.core.tools.ReliefLogger;
import cz.incad.nkp.digital.constants.ConstBasic_NKP;
import cz.incad.nkp.digital.constants.CnkpMikrofilm;
import cz.incad.nkp.digital.constants.CnkpSecurity;
//import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

/**
 *******************************************************************************
 *
 * @author martin
 */
public class MikrofilmEntity extends RecordEntity implements ConstBasic_NKP, CnkpMikrofilm, CnkpSecurity {

    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.DATA;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {SECURITY_ROLE_pripravamikrofilmu, SECURITY_ROLE_knavkorektor, SECURITY_ROLE_knavpracovnik, SECURITY_ROLE_appmikrofilm};
    private static final String SECURITY_POLE_HLIDANA[] = {
        MIKROFILM_archCisloNeg,
        MIKROFILM_snimanoDate,
        MIKROFILM_kamera,
        MIKROFILM_tXPredMikro,
        MIKROFILM_operator,
        MIKROFILM_faktorZvet,
        MIKROFILM_tRevizeMf,
        MIKROFILM_tKopieMf,
        MIKROFILM_zpracovatel,
        MIKROFILM_vlastnik,
        RECORD_stavRec,
        RECORD_poznRec,
        RECORD_stavPozn};

    /**
     ***************************************************************************
     * Konstruktor třídy MikrofilmEntity
     */
    public MikrofilmEntity() {
        super();
    }

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
     * @param vRecordFieldsSecurity
     * @param canErase
     */
    @Override
    protected boolean setCustomSecurityOnGetRecord(Record rec, String[] poleHlidana, Vector<Integer> vRecordFieldsSecurity, boolean canErase, String kompetence) {
        Object oStavZaznamu;

        // Guru agnedy
        if (kompetence.contains(SECURITY_ROLE_appmikrofilm)) {
            mergeSecurity(vRecordFieldsSecurity, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2);
            canErase = Boolean.TRUE;
        }

        //Kontrola na to jestli je validacni pole stavRec vyplnene nejakou hodnotou
        //null znamenalo crash aplikace
        oStavZaznamu = rec.getSimpleField(RECORD_stavRec).getValue();
        if (oStavZaznamu == null) {
            mergeSecurity(vRecordFieldsSecurity, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            return canErase;
        }
        //získáme stav záznamu
        String stavZaznamu = (String) oStavZaznamu;

        //Stav active - Novy
        if (stavZaznamu.equals(MIKROFILM_STAV_active)) {
            //Kompetence - Priprava mikrofilmu
            if (kompetence.contains(SECURITY_ROLE_pripravamikrofilmu)) {
                canErase = Boolean.TRUE;
                //ReliefLogger.severe("Nastavuji active - priprava mikrofilmu");
                mergeSecurity(vRecordFieldsSecurity, 2, 1, 2, 2, 2, 1, 1, 2, 2, 2, 1, 2, 2);
            }
            //Kompetence - Fotograf-revizor
            if (kompetence.contains(SECURITY_ROLE_fotografRevizor)) {
                mergeSecurity(vRecordFieldsSecurity, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
            }
            //Kompetence - Fotograf
            if (kompetence.contains(SECURITY_ROLE_fotograf)) {
                mergeSecurity(vRecordFieldsSecurity, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
            }
            //Kompetence - knavkorektor
            if (kompetence.contains(SECURITY_ROLE_knavkorektor)) {
                mergeSecurity(vRecordFieldsSecurity, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2);
                canErase = Boolean.TRUE;
            }

            //Kompetence - knavpracovnik
            if (kompetence.contains(SECURITY_ROLE_knavpracovnik)) {
                mergeSecurity(vRecordFieldsSecurity, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2);
            }
        }

        //Stav progress - Zpracovavan
        if (stavZaznamu.equals(MIKROFILM_STAV_progress)) {
            //Kompetence - Priprava mikrofilmu
            if (kompetence.contains(SECURITY_ROLE_pripravamikrofilmu)) {
                mergeSecurity(vRecordFieldsSecurity, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
            }
            //Kompetence - Fotograf-revizor
            if (kompetence.contains(SECURITY_ROLE_fotografRevizor)) {
                mergeSecurity(vRecordFieldsSecurity, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2);
            }
            //Kompetence - Fotograf
            if (kompetence.contains(SECURITY_ROLE_fotograf)) {
                mergeSecurity(vRecordFieldsSecurity, 1, 2, 2, 1, 2, 2, 2, 1, 2, 2, 1, 2, 2);
            }
            //Kompetence - knavkorektor
            if (kompetence.contains(SECURITY_ROLE_knavkorektor)) {
                mergeSecurity(vRecordFieldsSecurity, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
                canErase = Boolean.TRUE;
            }

            //Kompetence - knavpracovnik
            if (kompetence.contains(SECURITY_ROLE_knavpracovnik)) {
                mergeSecurity(vRecordFieldsSecurity, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);
            }
        }

        //Stav finiched - Dokonceno
        if (stavZaznamu.equals(MIKROFILM_STAV_finished)) {
            setRecordReadOnly(rec);
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
        rec.getSimpleField(MIKROFILM_vlastnik).setValue("ABA001");
        return rec;
    }

    /**
     ***************************************************************************
     *
     * @param rec
     * @return
     * @throws com.amaio.plaant.businessFunctions.AddException
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     */
    @Override
    public Record onCreate(Record rec) throws AddException, ValidationException {
        super.onCreate(rec);
        onCreateOnUpdate(rec, Boolean.TRUE);
        Object cisloArchivnihoNegativu = null;
        cisloArchivnihoNegativu = rec.getSimpleField(MIKROFILM_ARCH_CIS_NEG_FLD).getValue();
        if (cisloArchivnihoNegativu == null) {
            Object next = getFormattedNextNumber("Mikrofilm.cisloArchivnihoNegativu", "");
            try {
                //rec.getSimpleField(ZAKAZKA_CISLO_ZAKAZKY_FLD).setValue(getFormattedNextNumber("Zakazka.cisloArchivnihoNegativu", ""));
                rec.getSimpleField(MIKROFILM_archCisloNeg).setValue(Integer.parseInt((String) next));
                //logger.putLogSevere("ZAKAZKA --- " + next.toString());
            } catch (com.amaio.plaant.sync.NoSuchFieldException exc) {
                ReliefLogger.throwing("", "", exc);
            }
            //logger.putLogSevere("ZakazkaEntity-onCreate " + (rec.getSimpleField(ZAKAZKA_CISLO_ZAKAZKY_FLD).getValue()));
            return rec;
        }
        return rec;
    }

    /**
     ***************************************************************************
     *
     * @param rec
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.UpdateException
     */
    @Override
    public Record onUpdate(Record rec) throws ValidationException, UpdateException {
        super.onUpdate(rec);
        onCreateOnUpdate(rec, Boolean.FALSE);
        return rec;
    }

    /**
     ***************************************************************************
     *
     * @param rec
     * @param create
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     */
    private void onCreateOnUpdate(Record rec, boolean create) throws ValidationException {
        boolean notSorted = true;
        int poleCistaCelkem = 0;
        int poleHrubaCelkem = 0;
        int pocetScanuCelkem = 0;
        int pocetDokumentuNaMikrofilmu = 0;
        Object cislo = null;
        Record meziTabulka = null;
        RecordsIterator seznamMezitabulek = null;
        Vector<Record> sorter = new Vector<Record>(0);
        Record prvniZaznam = null;
        Record druhejZaznam = null;
        float velikostPrvnihoZaznamu;
        float velikostDruhehoZaznamu;
        Iterator<Record> rit = null;
        String stavZaznamu = (String) rec.getSimpleField(RECORD_stavRec).getValue();
        RecordsIterator riDokumentyNaMikrofilmu = null;

        // Obsluha checkboxů
        //checkboxController(rec, MIKROFILM_duplikat, MIKROFILM_duplikatDate);
        //checkboxController(rec, MIKROFILM_pozitiv, MIKROFILM_pozitivDate);
        //checkboxController(rec,MIKROFILM_digitalizace, MIKROFILM_digitalizaceDate);
        //získáme iterátor mezitabulek, ze kterých budem získávat informace.
        seznamMezitabulek = rec.getTableField(NKP_TABLE_XPREDMIKRO_FIELD).getTableRecords();
        //Naplníme Vektor jednotlivými záznamy.
        while (seznamMezitabulek.hasMoreRecords()) {
            sorter.add(seznamMezitabulek.nextRecord());
        }
        if (create) {
            //Třídění podle velikosti pole vyskaVcm, prvni je nejvetsi.
            while (notSorted) {
                notSorted = false;
                for (int i = 0; i < (sorter.size() - 1); i++) {
                    prvniZaznam = sorter.get(i);
                    druhejZaznam = sorter.get(i + 1);
                    velikostPrvnihoZaznamu = getVyskaVcm(prvniZaznam);
                    velikostDruhehoZaznamu = getVyskaVcm(druhejZaznam);
                    if (velikostDruhehoZaznamu > velikostPrvnihoZaznamu) {
                        //prohození záznamů
                        sorter.set(i, druhejZaznam);
                        sorter.set(i + 1, prvniZaznam);
                        notSorted = true;
                    }
                }
            }
            notSorted = true; //asi zbytečné, ale pro jistotu, nevim jak přesně Plaant s Triggerem pracuje
        }
        rit = sorter.iterator();
        while (rit.hasNext()) {
            //počítadlo dokumentů na mikrofilmu
            pocetDokumentuNaMikrofilmu++;
            meziTabulka = rit.next();
            cislo = meziTabulka.getSimpleField(XTITNKPMF_POLE_CISTA_FIELD).getValue();
            if (cislo != null) {
                poleCistaCelkem = poleCistaCelkem + (Integer) cislo;
            }
            cislo = meziTabulka.getSimpleField(XTITNKPMF_POLE_HRUBA_FIELD).getValue();
            if (cislo != null) {
                poleHrubaCelkem = poleHrubaCelkem + (Integer) cislo;
            }
            cislo = meziTabulka.getSimpleField(XTITNKPMF_POCET_SCANU_FIELD).getValue();
            if (cislo != null) {
                pocetScanuCelkem = pocetScanuCelkem + (Integer) cislo;
            }
            //při onCreate nastavuje pořadí dokumentů na mikrofilmu
            if (create) {
                meziTabulka.getSimpleField(XTITNKPMF_PORADI_VE_SVITKU_FIELD).setValue(pocetDokumentuNaMikrofilmu);
            }
        }
        //Nastaví hodnotu pole: pole čistá celkem
        rec.getSimpleField(MIKROFILM_POLE_CISTA_CELKEM_FIELD).setValue(poleCistaCelkem);
        //Nastaví hodnotu pole: pole hrubá celkem
        rec.getSimpleField(MIKROFILM_POLE_HRUBA_CELKEM_FIELD).setValue(poleHrubaCelkem);
        //Nastaví hodnotu pole: počet scanů celkem
        rec.getSimpleField(MIKROFILM_POCET_SCANU_FIELD).setValue(pocetScanuCelkem);
        //Nastaví hodnotu pole: počet dokumentů na mikrofilmu
        rec.getSimpleField(MIKROFILM_POCET_DOKUMENTU_CELKEM_FIELD).setValue(pocetDokumentuNaMikrofilmu);
        //nastavime stejny stav zaznamu ktery ma mikrofilm i pro vsechny podrizene mezitabulky Dokument na mikrofilmu.
        riDokumentyNaMikrofilmu = rec.getTableField(MIKROFILM_tXPredMikro).getTableRecords();
        while (riDokumentyNaMikrofilmu.hasMoreRecords()) {
            meziTabulka = riDokumentyNaMikrofilmu.nextRecord();
            meziTabulka.getSimpleField(RECORD_stavRec).setValue(stavZaznamu);
        }
        //nastavime stejny stav zaznamu ktery ma mikrofilm i pro vsechny podrizene RevizeMikrofilmu.
        riDokumentyNaMikrofilmu = rec.getTableField(MIKROFILM_tRevizeMf).getTableRecords();
        while (riDokumentyNaMikrofilmu.hasMoreRecords()) {
            meziTabulka = riDokumentyNaMikrofilmu.nextRecord();
            meziTabulka.getSimpleField(RECORD_stavRec).setValue(stavZaznamu);
        }
    }

    /**
     ***************************************************************************
     *
     * @param rec
     * @return
     */
    private float getVyskaVcm(Record rec) {
        Object testNaNull = null;

        testNaNull = rec.getReferencedField(NKP_REFERENCE_NA_TITNKP).getReferencedRecord().getSimpleField("vyskaVcm").getValue();
        if (testNaNull == null) {
            return 0;
        }
        return ((Number) testNaNull).floatValue();
    }

}
