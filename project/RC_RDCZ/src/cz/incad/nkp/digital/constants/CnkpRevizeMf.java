/*
 * cz.incad.nkp.digital.konstanty.CnkpRevizeMf.java
 * created on 10.6.2008
 *
 * author: Martin Nováček (mno)
 * e-mail: <a href="mailto:martin.novacek@incad.cz">
 *
 * INCAD, spol. s r.o.
 * U krčského nádraží 36
 * 140 00  Praha 4, Česká republika
 */

package cz.incad.nkp.digital.constants;

import cz.incad.core.constants.CcoreRecord;

/**
 * @author mno
 * <b>Class description:</b>
 * 
 */
public interface CnkpRevizeMf extends CcoreRecord {
    
    /** active - Nový */
    public static final String REVIZEMF_STAV_active = "active";
    /** progress - Zpracováván */
    public static final String REVIZEMF_STAV_progress = "progress";
    /** finished - Dokončen */
    public static final String REVIZEMF_STAV_finished = "finished";
    
    /** DATE */
    public static final String REVIZEMF_datumRevize = "datumRevize";
    /** STRING */
    public static final String REVIZEMF_techPredloha = "techPredloha";
    /** NUMERIC */
    public static final String REVIZEMF_denzitaZac = "denzitaZac";
    /** NUMERIC */
    public static final String REVIZEMF_denzitaKonec = "denzitaKonec";
    /** STRING */
    public static final String REVIZEMF_faktorRoz = "faktorRoz";
    /** STRING */
    public static final String REVIZEMF_defekty = "defekty";
    /** STRING */
    public static final String REVIZEMF_endMf = "endMf";
    /** STRING */
    public static final String REVIZEMF_startMf = "startMf";
    /** NUMERIC */
    public static final String REVIZEMF_dMin = "dMin";
    /** STRING */
    public static final String REVIZEMF_revizor = "revizor";
    /** STRING */
    public static final String REVIZEMF_phB = "phB";
    /** STRING */
    public static final String REVIZEMF_lhB = "lhB";
    /** STRING */
    public static final String REVIZEMF_sB = "sB";
    /** STRING */
    public static final String REVIZEMF_pdB = "pdB";
    /** STRING */
    public static final String REVIZEMF_ldB = "ldB";
    /** STRING */
    public static final String REVIZEMF_phC = "phC";
    /** STRING */
    public static final String REVIZEMF_lhC = "lhC";
    /** STRING */
    public static final String REVIZEMF_sC = "sC";
    /** STRING */
    public static final String REVIZEMF_pdC = "pdC";
    /** STRING */
    public static final String REVIZEMF_ldC = "ldC";
    /** STRING */
    public static final String REVIZEMF_phP = "phP";
    /** STRING */
    public static final String REVIZEMF_lhP = "lhP";
    /** STRING */
    public static final String REVIZEMF_sP = "sP";
    /** STRING */
    public static final String REVIZEMF_pdP = "pdP";
    /** STRING */
    public static final String REVIZEMF_ldP = "ldP";
    /** REFERENCE */
    public static final String REVIZEMF_rMikrofilm = "rMikrofilm";
    
    
}//eof interface CnkpRevizeMf
