/*
 * cz.incad.nkp.digital.konstanty.CnkpSouborXML.java
 * created on 11.6.2008
 *
 * author: Martin Nováček (mno)
 * e-mail: <a href="mailto:martin.novacek@incad.cz">
 *
 * INCAD, spol. s r.o.
 * U krčského nádraží 36
 * 140 00  Praha 4, Česká republika
 */

package cz.incad.nkp.digital.constants;

import cz.incad.core.constants.CcoreSoubor;

/*******************************************************************************
 * @author mno
 * <b>Class description:</b>
 * 
 */
public interface CnkpSouborXML extends CcoreSoubor {
    /** REFERENCE */
    public static final String SOUBORXML_rTitNkp = "rTitNkp";
    /** INTEGER - COMPUTED */
    public static final String SOUBORXML_pocetImg = "pocetImg";
    /** rozsah - COMPUTED */
    public static final String SOUBORXML_rozsah = "rozsah";
    /** TABLE */
    public static final String SOUBORXML_tSouborZakazky = "tSouborZakazky";
    
    
}//eof interface CnkpSouborXML
