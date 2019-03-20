/** ****************************************************************************
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package cz.incad.rd;

import com.amaio.plaant.businessFunctions.AddException;
import com.amaio.plaant.businessFunctions.AnnotationKeys;
import com.amaio.plaant.businessFunctions.UpdateException;
import com.amaio.plaant.businessFunctions.ValidationException;
import com.amaio.plaant.sync.Record;
import cz.incad.core.basic.EntityImplMother;
import cz.incad.core.enums.ReliefSecurityDeskTypeEnum;
import cz.incad.core.tools.ReliefUser;
import cz.incad.nkp.digital.constants.CnkpSecurity;

/**
 *******************************************************************************
 *
 * @author martin
 */
public class DigObjektEntity extends EntityImplMother implements CnkpSecurity {

    public static final String f_urnnbn = "urnnbn";
    public static final String t_tLokace = "tLokace";
    public static final String f_xml = "xml";
    public static final String f_nazev = "nazev";
    public static final String f_rokVyd = "rokVyd";
    public static final String f_dostupnost = "dostupnost";
    public static final String f_formatXML = "formatXML";
    public static final String f_zdrojXML = "zdrojXML";
    public static final String f_dateXML = "dateXML";
    public static final String t_tXPredDigObj = "tXPredDigObj";
    public static final String f_autor = "autor";
    public static final String f_vydavatel = "vydavatel";
    public static final String f_mistoVyd = "mistoVyd";
    public static final String f_isbn = "isbn";
    public static final String f_issn = "issn";
    public static final String f_uuid = "uuid";
    public static final String f_handler = "handler";
    public static final String f_signatura = "signatura";
    public static final String f_idZakazky = "idZakazky";
    public static final String f_formatDokumentu = "formatDokumentu";
    public static final String f_druhDokumentu = "druhDokumentu";
    public static final String f_pocetKopii = "pocetKopii";
    public static final String f_vizitka = "vizitka";
    public static final String f_indexovano = "indexovano";
    public static final String f_ccnb = "ccnb";
    public static final String f_carKod = "carKod";
    public static final String f_sigla = "sigla";
    public static final String f_urlSentToAleph = "urlSentToAleph";
    public static final String f_rPredloha = "rPredloha";

    private static final ReliefSecurityDeskTypeEnum SECURITY_DESK_TYPE = ReliefSecurityDeskTypeEnum.SYSTEM_CORE;
    private static final String SECURITY_CAN_CREATE_COMPETITIONS[] = {};
    private static final String SECURITY_POLE_HLIDANA[] = {};

    /**
     ***************************************************************************
     *
     * @param rec
     * @return
     */
    @Override
    public Record onGetRecord(Record rec) {
        securityOnGetRecord(rec, SECURITY_DESK_TYPE, SECURITY_POLE_HLIDANA);

        ReliefUser ru = new ReliefUser(getTriggerContext());
        String userName = ru.getLogin();
        if (!ru.isSystemAdmin()) {
            rec.setAnnotation(AnnotationKeys.READ_ONLY_SECURITY_PROPERTY, AnnotationKeys.TRUE_VALUE);
            rec.setAnnotation(AnnotationKeys.REMOVE_FORBIDDEN_SECURITY_PROPERTY, AnnotationKeys.TRUE_VALUE);
        }
        
        return rec;
    }

    /**
     ***************************************************************************
     *
     * @param rec
     * @return
     * @throws AddException
     */
    @Override
    public Record onCreateLocal(Record rec) throws AddException {
        securityOnCreateLocal(rec, SECURITY_DESK_TYPE, SECURITY_CAN_CREATE_COMPETITIONS);
        super.onCreateLocal(rec);

        return rec;
    }

    /**
     * *************************************************************************
     *
     * @param rec
     * @return
     * @throws AddException
     * @throws ValidationException
     */
    @Override
    public Record onCreate(Record rec) throws AddException, ValidationException {
        super.onCreate(rec);
        this.onCreateOnUpdate(rec);

        return rec;
    }

    /**
     ***************************************************************************
     *
     * @param rec
     * @return
     * @throws ValidationException
     * @throws UpdateException
     */
    @Override
    public Record onUpdate(Record rec) throws ValidationException, UpdateException {
        super.onUpdate(rec);
        this.onCreateOnUpdate(rec);

        return rec;
    }

    /**
     ***************************************************************************
     *
     * @param rec
     */
    private void onCreateOnUpdate(Record rec) {
//        Object jmeno = rec.getSimpleField(f_nazev).getValue();
//        Object autor = rec.getSimpleField(f_autor).getValue();
//        Object rok = rec.getSimpleField(f_rokVyd).getValue();
//
//        if (jmeno == null) {
//            jmeno = "";
//        }
//        if (autor == null) {
//            autor = "";
//        }
//        if (rok == null) {
//            rok = "";
//        }
//
//        rec.getSimpleField(f_vizitka).setValue(jmeno + "\n" + autor + ", " + rok);
    }

}
