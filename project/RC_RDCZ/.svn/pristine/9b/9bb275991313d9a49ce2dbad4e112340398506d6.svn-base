/*******************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cz.incad.rd;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.businessFunctions.AnnotationKeys;
import com.amaio.plaant.businessFunctions.UpdateException;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.dbdef.DbBundles;
import com.amaio.plaant.dbdef.DbListI;
import com.amaio.plaant.dbdef.ListValue;
import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.RecordEntity;
import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;
import cz.incad.core.tools.DirectConnection;
import cz.incad.core.tools.ReliefLogger;
import cz.incad.core.tools.ReliefUser;
import cz.incad.nkp.digital.constants.CnkpSecurity;
import cz.incad.rd.list.SpecificDynamicLists;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

/*******************************************************************************
 *
 * @author martin
 */
public class ProfilEntity extends RecordEntity implements DbListI, CnkpSecurity {
    public static final String CLASSNAME        = "cz.incad.rd.Profil";

    //RELIEF FIELDS
    public static final String f_value = "value";
    public static final String f_humanName = "humanName";
    public static final String f_nazev = "nazev";
    public static final String f_popis = "popis";
    public static final String f_pripCheck = "pripCheck";
    public static final String f_pripStav = "pripStav";
    public static final String f_pripPrac = "pripPrac";
    public static final String f_mikroCheck = "mikroCheck";
    public static final String f_mikroPrac = "mikroPrac";
    public static final String f_skenCheck = "skenCheck";
    public static final String f_skenPrac = "skenPrac";
    public static final String f_metaCheck = "metaCheck";
    public static final String f_metaPrac = "metaPrac";
    public static final String f_publCheck = "publCheck";
    public static final String f_publPrac = "publPrac";
    public static final String f_parSkenovani = "parSkenovani";
    public static final String f_metaEditor = "metaEditor";
    public static final String f_digKnihovna = "digKnihovna";
    public static final String f_financovano = "financovano";
    public static final String f_zpusobDig = "zpusobDig";
    public static final String f_archNeg = "archNeg";
    public static final String f_matNegativ = "matNegativ";
    public static final String f_pozitiv = "pozitiv";
    public static final String f_rozliseni = "rozliseni";
    public static final String f_barevnaHloubka = "barevnaHloubka";
    public static final String f_barevnaSkala = "barevnaSkala";
    //public static final String f_druhDigitalizace = "druhDigitalizace";
    //public static final String f_typSkeneru = "typSkeneru";
    public static final String f_skenerProfil = "skenerProfil";
    public static final String f_archCheck = "archCheck";
    public static final String f_archPrac = "archPrac";
    public static final String f_skenOCRCheck = "skenOCRCheck";
    public static final String f_skenOCRPrac = "skenOCRPrac";
    //public static final String f_skenSkenZMikCheck = "skenSkenZMikCheck";
    public static final String f_archNegPrac = "archNegPrac";
    public static final String f_matNegativPrac = "matNegativPrac";
    public static final String f_predatCheck = "predatCheck";
    public static final String f_predatKomuOdKoho = "predatKomuOdKoho";
    public static final String f_predatVarka = "predatVarka";
    public static final String f_tDalsiPrace = "tDalsiPrace";
    public static final String f_mikroStav = "mikroStav";
    public static final String f_skenStav = "skenStav";
    public static final String f_metaStav = "metaStav";
    public static final String f_publStav = "publStav";
    public static final String f_archStav = "archStav";
    public static final String f_dalsiPraceClearFirst = "dalsiPraceClearFirst";
    public static final String f_headCheck = "headCheck";
    public static final String f_predTypAkce = "predTypAkce";
    public static final String f_ocrCheck = "ocrCheck";
    public static final String f_skenOCRSvabach = "skenOCRSvabach";
    public static final String f_skenDJVU = "skenDJVU";
    public static final String f_skenJPEG = "skenJPEG";
    public static final String f_skenGIF = "skenGIF";
    public static final String f_skenTIFF = "skenTIFF";
    public static final String f_skenPDF = "skenPDF";
    public static final String f_skenTXT = "skenTXT";
    public static final String f_zpracCheck = "zpracCheck";
    public static final String f_zpracStav = "zpracStav";
    public static final String f_zpracPrac = "zpracPrac";
    public static final String f_zpracEditor = "zpracEditor";
    public static final String f_zpracParm = "zpracParm";


    //SQL FIELDS
    public static final String sql_value = "value";
    public static final String sql_humanName = "humanName";
    public static final String sql_nazev = "nazev";
    public static final String sql_popis = "popis";
    public static final String sql_pripCheck = "pripCheck";
    public static final String sql_pripStav = "pripStav";
    public static final String sql_pripPrac = "pripPrac";
    public static final String sql_mikroCheck = "mikroCheck";
    public static final String sql_mikroPrac = "mikroPrac";
    public static final String sql_skenCheck = "skenCheck";
    public static final String sql_skenPrac = "skenPrac";
    public static final String sql_metaCheck = "metaCheck";
    public static final String sql_metaPrac = "metaPrac";
    public static final String sql_publCheck = "publCheck";
    public static final String sql_publPrac = "publPrac";
    public static final String sql_parSkenovani = "parSkenovani";
    public static final String sql_metaEditor = "metaEditor";
    public static final String sql_digKnihovna = "digKnihovna";
    public static final String sql_financovano = "financovano";
    public static final String sql_zpusobDig = "zpusobDig";
    public static final String sql_archNeg = "archNeg";
    public static final String sql_matNegativ = "matNegativ";
    public static final String sql_pozitiv = "pozitiv";
    public static final String sql_rozliseni = "rozliseni";
    public static final String sql_barevnaHloubka = "barevnaHloubka";
    public static final String sql_barevnaSkala = "barevnaSkala";
    public static final String sql_druhDigitalizace = "druhDigitalizace";
    //public static final String sql_typSkeneru = "typSkeneru";
    public static final String sql_skenerProfil = "skenerProfil";
    public static final String sql_archCheck = "archCheck";
    public static final String sql_archPrac = "archPrac";
    public static final String sql_skenOCRCheck = "skenOCRCheck";
    public static final String sql_skenOCRPrac = "skenOCRPrac";
    //public static final String sql_skenSkenZMikCheck = "skenSkenZMikCheck";
    public static final String sql_archNegPrac = "archNegPrac";
    public static final String sql_matNegativPrac = "matNegativPrac";
    public static final String sql_predatCheck = "predatCheck";
    public static final String sql_predatKomuOdKoho = "predatKomuOdKoho";
    public static final String sql_predatVarka = "predatVarka";
    public static final String sql_mikroStav = "mikroStav";
    public static final String sql_skenStav = "skenStav";
    public static final String sql_metaStav = "metaStav";
    public static final String sql_publStav = "publStav";
    public static final String sql_archStav = "archStav";
    public static final String sql_dalsiPraceClearFirst = "dalsiPraceClearFirst";
    public static final String sql_headCheck = "headCheck";
    public static final String sql_predTypAkce = "predTypAkce";
    public static final String sql_ocrCheck = "ocrCheck";
    public static final String sql_skenOCRSvabach = "skenOCRSvabach";
    public static final String sql_skenDJVU = "skenDJVU";
    public static final String sql_skenJPEG = "skenJPEG";
    public static final String sql_skenGIF = "skenGIF";
    public static final String sql_skenTIFF = "skenTIFF";
    public static final String sql_skenPDF = "skenPDF";
    public static final String sql_skenTXT = "skenTXT";
    public static final String sql_zpracCheck = "zpracCheck";
    public static final String sql_zpracStav = "zpracStav";
    public static final String sql_zpracPrac = "zpracPrac";
    public static final String sql_zpracEditor = "zpracEditor";
    public static final String sql_zpracParm = "zpracParm";

    //SECURITY
    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.DATA;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {SECURITY_ROLE_knavkorektor, SECURITY_ROLE_knavpracovnik, SECURITY_ROLE_apppredloha};
    private static final String SECURITY_POLE_HLIDANA[] = {
        ProfilEntity.f_idCislo
        };

    //ENTITY
    private boolean alphabetize;
    private String name;
    protected String bundleName;
    protected DbBundles dbBundles;

    /***************************************************************************
     *
     * @param rec
     * @return
     */
    @Override
    public Record onGetRecord(Record rec) {
        securityOnGetRecord(rec, SECURITY_DESK_TYPE, SECURITY_POLE_HLIDANA);
//        ReliefUser ru = new ReliefUser(getTriggerContext()); 
//        String userName = ru.getLogin();
        
//        if (!userName.contains("import")) {
//            ReliefLogger.severe("TOTO NENI IMPORTNI UZIVATEL");
//            //Nastavení Specifických dynamických listů.
////            ReliefLogger.severe("Nastavime specificke dynamicke listy...");
////            rec.getField(ProfilEntity.f_skenerProfil).setAnnotation(AnnotationKeys.LIST_SOURCE_CUSTOM_PROPERTY, new SpecificDynamicLists(new ReliefUser(getTriggerContext()), null, null, null, null, null, null, null, null, null));
////            rec.getField(ProfilEntity.f_archNegPrac).setAnnotation(AnnotationKeys.LIST_SOURCE_CUSTOM_PROPERTY, new SpecificDynamicLists(null, null, null, null, null, null, null, null, null, null));
////            rec.getField(ProfilEntity.f_archPrac).setAnnotation(AnnotationKeys.LIST_SOURCE_CUSTOM_PROPERTY, new SpecificDynamicLists(null, null, null, null, null, null, null, null, null, null));
////            rec.getField(ProfilEntity.f_matNegativPrac).setAnnotation(AnnotationKeys.LIST_SOURCE_CUSTOM_PROPERTY, new SpecificDynamicLists(null, null, null, null, null, null, null, null, null, null));
////            rec.getField(ProfilEntity.f_metaPrac).setAnnotation(AnnotationKeys.LIST_SOURCE_CUSTOM_PROPERTY, new SpecificDynamicLists(null, null, null, null, null, null, null, null, null, null));
////            rec.getField(ProfilEntity.f_mikroPrac).setAnnotation(AnnotationKeys.LIST_SOURCE_CUSTOM_PROPERTY, new SpecificDynamicLists(null, null, null, null, null, null, null, null, null, null));
////            rec.getField(ProfilEntity.f_pripPrac).setAnnotation(AnnotationKeys.LIST_SOURCE_CUSTOM_PROPERTY, new SpecificDynamicLists(null, null, null, null, null, null, null, null, null, null));
////            rec.getField(ProfilEntity.f_publPrac).setAnnotation(AnnotationKeys.LIST_SOURCE_CUSTOM_PROPERTY, new SpecificDynamicLists(null, null, null, null, null, null, null, null, null, null));
////            rec.getField(ProfilEntity.f_skenOCRPrac).setAnnotation(AnnotationKeys.LIST_SOURCE_CUSTOM_PROPERTY, new SpecificDynamicLists(null, null, null, null, null, null, null, null, null, null));
////            rec.getField(ProfilEntity.f_skenPrac).setAnnotation(AnnotationKeys.LIST_SOURCE_CUSTOM_PROPERTY, new SpecificDynamicLists(null, null, null, null, null, null, null, null, null, null));
////            rec.getField(ProfilEntity.f_zpracPrac).setAnnotation(AnnotationKeys.LIST_SOURCE_CUSTOM_PROPERTY, new SpecificDynamicLists(null, null, null, null, null, null, null, null, null, null));
////            rec.getField(ProfilEntity.f_zpracEditor).setAnnotation(AnnotationKeys.LIST_SOURCE_CUSTOM_PROPERTY, new SpecificDynamicLists(null, null, null, null, null, null, null, null, null, null));
////            rec.getField(ProfilEntity.f_zpusobDig).setAnnotation(AnnotationKeys.LIST_SOURCE_CUSTOM_PROPERTY, new SpecificDynamicLists(null, null, null, null, null, null, null, null, null, null));
//        } else {
//            ReliefLogger.severe("TOTO JE IMPORTNI UZIVATEL");
//        }
        
        return rec;
    }

    /***************************************************************************
     * 
     * @param rec
     * @param poleHlidana
     * @param vRecordFieldsSecurity
     * @param canErase
     */
    @Override
    protected boolean setCustomSecurityOnGetRecord(Record rec, String[] poleHlidana, Vector<Integer> vRecordFieldsSecurity, boolean canErase, String kompetence) {
        // Guru agnedy
        if (kompetence.contains(SECURITY_ROLE_appprofil)) {
            mergeSecurity(vRecordFieldsSecurity, 2);
            canErase = Boolean.TRUE;
        }
        return canErase;
    }

    /***************************************************************************
     *
     * @param rec
     * @return
     * @throws com.amaio.plaant.businessFunctions.AddException
     */
    @Override
    public Record onCreateLocal(Record rec) throws AddException {
        securityOnCreateLocal(rec, SECURITY_DESK_TYPE, SECURITY_CAN_CREATE_COMPETITIONS);
        rec = super.onCreateLocal(rec);

        return rec;
    }

    /***************************************************************************
     *
     * @param rec
     * @return
     * @throws AddException
     * @throws ValidationException
     */
    @Override
    public Record onCreate(Record rec) throws AddException, ValidationException {
        rec = super.onCreate(rec);
        rec = onCreateUpdate(rec);
        rec.getSimpleField(ProfilEntity.f_value).setValue(rec.getSimpleField(ProfilEntity.f_idCislo).getValue());
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
        rec = super.onUpdate(rec);
        rec = onCreateUpdate(rec);
        return rec;
    }

    /***************************************************************************
     * 
     * @param rec
     * @return
     */
    private Record onCreateUpdate(Record rec) {
        Object nazev = rec.getSimpleField(ProfilEntity.f_nazev).getValue();
        Object popis = rec.getSimpleField(ProfilEntity.f_popis).getValue();
        
        rec.getSimpleField(ProfilEntity.f_humanName).setValue(nazev + " - " + popis);
        rec = correctBooleanFields(rec);
        return rec;
    }


    /***************************************************************************
     * Provádíme kontorlu Boolean polí na to aby měli nastavenou defaultní hodnotu 0 místo null.
     * @param rec
     */
    private Record correctBooleanFields(Record rec) {
        //Seznam polí typu boolean která se mají testovat
        String[] fieldsToCheck = {
            ProfilEntity.f_archCheck,
            ProfilEntity.f_dalsiPraceClearFirst,
            ProfilEntity.f_headCheck,
            ProfilEntity.f_metaCheck,
            ProfilEntity.f_mikroCheck,
            ProfilEntity.f_pozitiv,
            ProfilEntity.f_predatCheck,
            ProfilEntity.f_pripCheck,
            ProfilEntity.f_publCheck,
            ProfilEntity.f_skenCheck,
            ProfilEntity.f_skenOCRCheck,
            //ProfilEntity.f_skenSkenZMikCheck,
            ProfilEntity.f_archNeg,
            ProfilEntity.f_matNegativ,
            ProfilEntity.f_ocrCheck,
            ProfilEntity.f_skenOCRSvabach,
            ProfilEntity.f_skenDJVU,
            ProfilEntity.f_skenJPEG,
            ProfilEntity.f_skenGIF,
            ProfilEntity.f_skenTIFF,
            ProfilEntity.f_skenPDF,
            ProfilEntity.f_skenTXT,
            ProfilEntity.f_zpracCheck
            };

        for (int i = 0; i < fieldsToCheck.length; i++) {
            if (rec.getSimpleField(fieldsToCheck[i]).getValue() == null) rec.getSimpleField(fieldsToCheck[i]).setValue(false);
        }

        return rec;
    }


//------------------------------------------------------------------------------
//SEKCE DYNAMICK0HO LSITU - SIMPLE ---------------------------------------------
//Bude přesunuta jimam až dořešim dědičnost tohodle druhu listu
//------------------------------------------------------------------------------

    /***************************************************************************
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    /***************************************************************************
     *
     * @param nm
     */
    public void setName(String nm) {
        this.name = nm;
    }

    /***************************************************************************
     *
     * @return
     */
    public boolean isAlphabetize() {
        return this.alphabetize;
    }

    /***************************************************************************
     *
     * @param alphabetize
     */
    public void setAlphabetize(boolean alphabetize) {
        this.alphabetize = alphabetize;
    }

    /***************************************************************************
     *
     * @param bundleName
     */
    public void setBundle(String bundleName) {
        this.bundleName = bundleName;
    }

    /***************************************************************************
     *
     * @param dbBundles
     */
    public void setDbBundles(DbBundles dbBundles) {
        this.dbBundles = dbBundles;
    }

    /***************************************************************************
     *
     * @param locale
     * @return
     */
    public List getLocalizedList(Locale locale) {
        List<ListValue> items = new LinkedList<ListValue>();
        Connection conn = DirectConnection.getConnection();
        Statement stmt;
        ResultSet rs;
        
        String sql;
        
        if (alphabetize) {
            sql = "select value,humanname from profil order by humanname";
        } else {
            sql = "select value,humanname from profil";
        }
        
        try {
            stmt = conn.createStatement();
            
            
            
            //rs = stmt.executeQuery("select value,humanname from profil");
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                items.add(new ListValue(rs.getString("humanname"), rs.getString("value")));
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
            return items;
        }

        return items;
    }

}
