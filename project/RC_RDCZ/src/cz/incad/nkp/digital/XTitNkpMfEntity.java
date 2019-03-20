/*******************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.nkp.digital;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.businessFunctions.UpdateException;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.sync.Record;
import com.amaio.plaant.sync.SimpleField;
import cz.incad.core.basic.RecordEntity;
import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;
import cz.incad.nkp.digital.constants.CnkpSecurity;
import cz.incad.nkp.digital.constants.CnkpXTitNkpMf;
import cz.incad.nkp.digital.constants.ConstBasic_NKP;
import cz.incad.rd.tools.UtilitiesNKP;
import java.util.Vector;


/*******************************************************************************************************************************************
 *
 * @author martin
 */
public class XTitNkpMfEntity extends RecordEntity implements ConstBasic_NKP, CnkpXTitNkpMf, CnkpSecurity {
    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.DATA;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {SECURITY_ROLE_pripravamikrofilmu};
    private static final String SECURITY_POLE_HLIDANA[] = {
        XTITNKPMF_poradiVeSvitku,
        XTITNKPMF_poleCista,
        XTITNKPMF_poleHruba,
        XTITNKPMF_rTitNkp,
        XTITNKPMF_rMikrofilm,
        XTITNKPMF_oznaceniCasti,
        XTITNKPMF_pocetScanu,
        XTITNKPMF_mfeCislo,
        RECORD_stavRec,
        RECORD_poznRec,
        RECORD_stavPozn};


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
        Object oStavZaznamu;

        //Kontrola na to jestli je validacni pole stavRec vyplnene nejakou hodnotou
        //null znamenalo crash aplikace
        oStavZaznamu = rec.getSimpleField(RECORD_stavRec).getValue();
        if (oStavZaznamu == null) {
            mergeSecurity(vRecordFieldsSecurity, 0,0,0,0,0,0,0,0,0,0,0);
            return canErase;
        }

        //získáme stav záznamu
        String stavZaznamu = (String)oStavZaznamu;

        //Stav active - Novy
        if (stavZaznamu.equals(XTITNKPMF_STAV_active)) {
            //Kompetence - Priprava mikrofilmu
            if (kompetence.contains(SECURITY_ROLE_pripravamikrofilmu)) {
                canErase = Boolean.TRUE;
                //ReliefLogger.severe("Nastavuji active - priprava mikrofilmu");
                mergeSecurity(vRecordFieldsSecurity, 2,1,1,1,2,2,1,1,1,1,1);
            }
            //Kompetence - Fotograf-revizor
            if(kompetence.contains(SECURITY_ROLE_fotografRevizor)) {
                //ReliefLogger.severe("Nastavuji active - fotografrevizor");
                mergeSecurity(vRecordFieldsSecurity, 1,1,1,1,1,1,1,1,1,1,1);
            }
            //Kompetence - Fotograf
            if(kompetence.contains(SECURITY_ROLE_fotograf)) {
                //ReliefLogger.severe("Nastavuji active - fotograf");
                mergeSecurity(vRecordFieldsSecurity, 1,1,1,1,1,1,1,1,1,1,1);
            }
        }

        //Stav progress - Zpracovavan
        if (stavZaznamu.equals(XTITNKPMF_STAV_progress)) {
            //Kompetence - Priprava mikrofilmu
            if (kompetence.contains(SECURITY_ROLE_pripravamikrofilmu)) {
                //ReliefLogger.severe("Nastavuji progress - priprava mikrofilmu");
                mergeSecurity(vRecordFieldsSecurity, 1,1,1,1,1,1,1,1,1,1,1);
            }
            //Kompetence - Fotograf-revizor
            if(kompetence.contains(SECURITY_ROLE_fotografRevizor)) {
                //ReliefLogger.severe("Nastavuji progress - fotografrevizor");
                mergeSecurity(vRecordFieldsSecurity, 1,2,2,1,1,1,2,1,1,2,1);
            }
            //Kompetence - Fotograf
            if(kompetence.contains(SECURITY_ROLE_fotograf)) {
                //ReliefLogger.severe("Nastavuji progress - fotograf");
                mergeSecurity(vRecordFieldsSecurity, 1,2,2,1,1,1,2,1,1,2,1);
            }
        }

        //Stav finiched - Dokonceno
        if (stavZaznamu.equals(XTITNKPMF_STAV_finished)) {
            setRecordReadOnly(rec);
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


    /***************************************************************************************************************************************
     * 
     * @param rec
     * @return
     * @throws com.amaio.plaant.businessFunctions.AddException
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     */
    @Override
    public Record onCreate(Record rec) throws AddException, ValidationException {
        super.onCreate(rec);
        onCreaeteOnUpdate(rec);
        return rec;
    }


    /***************************************************************************************************************************************
     * 
     * @param rec
     * @return
     * @throws com.amaio.plaant.businessFunctions.ValidationException
     * @throws com.amaio.plaant.businessFunctions.UpdateException
     */
    @Override
    public Record onUpdate(Record rec) throws ValidationException, UpdateException {
        super.onUpdate(rec);
        onCreaeteOnUpdate(rec);
        return rec;
    }


    /***************************************************************************************************************************************
     * 
     * @param rec
     */
    private void onCreaeteOnUpdate(Record rec) {
        UtilitiesNKP util = new UtilitiesNKP();
        SimpleField sf = rec.getSimpleField(XTITNKPMF_MFECISLO_FIELD);
        Object sfValue = sf.getValue();
        String value = null;
        
        if (sfValue == null) return;
        value = (String)sfValue;
        if (value.contains("E") || value.contains("e")) {
            value = trimSpacesStartEnd(value);
            value = value.substring(1, value.length());
            value = trimSpacesStartEnd(value);
            value = util.makeECislo(value);
            value = "E " + value;
            sf.setValue(value);
        } else {//případ kdy obsahuje jen číslo ve Stringu
            value = "E " + util.makeECislo(trimSpacesStartEnd(value));
            sf.setValue(value);
        }
    }


    /***************************************************************************************************************************************
     * Vrací vstupní řetězec bez mezer před a za řetězcem, mezery uvnitř nejsou kontrolovány a ponechávají se.
     * @param retezec
     * @return
     */
    private String trimSpacesStartEnd(String retezec) {
        boolean startKO = true;
        boolean endKO = true;
        while (startKO) {
            if (retezec.startsWith(" ")) {
                retezec = retezec.substring(1, retezec.length());
            } else startKO = false;
        }
        while (endKO) {
            if (retezec.endsWith(" ")) {
                retezec = retezec.substring(0, retezec.length() - 1);
            } else endKO = false;
        }
        return retezec;
    }


}
