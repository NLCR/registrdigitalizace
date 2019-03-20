/*
 * ConstClassNames.java
 *
 * Založeno: 2. září 2007, 22:08
 *
 *
 * Autor: Martin Nováček
 * e-mail: <a href="mailto:martin.novacek@incad.cz">
 *
 * INCAD, spol. s r.o.
 * U krčského nádraží 36
 * 140 00  Praha 4, Česká republika
 *
 * @Revize: $Revision$
 * Základní popis třídy:
 *
 */

package cz.incad.nkp.digital.constants;


public interface ConstBasic_NKP {
    
//Config
    /** CONFIG - NKP.BF.zalozMikrofilm.maxPole.check */
    //public static final String NKP_CFG_BF_ZALOZMIKROFILM_MAXPOLE_CHECK = "NKP.BF.zalozMikrofilm.maxPole.check";

//ClassNames
    /** cz.incad.nkp.digital.Mikrofilm */
    public static final String NKP_MIKROFILM_CLASSNAME = "cz.incad.nkp.digital.Mikrofilm";
    /** cz.incad.nkp.digital.TFMonografie */
    public static final String NKP_MONOGRAFIE_CLASSNAME = "cz.incad.nkp.digital.TFMonografie";
    /** cz.incad.nkp.digital.Pracovnik */
    public static final String NKP_PRACOVNIK_CLASSNAME = "cz.incad.nkp.digital.Pracovnik";
    /** cz.incad.nkp.digital.Zakazka */
    public static final String NKP_ZAKAZKA_CLASSNAME = "cz.incad.nkp.digital.Zakazka";
    /** cz.incad.nkp.digital.Ukol */
    public static final String NKP_UKOL_CLASSNAME = "cz.incad.nkp.digital.Ukol";
    /** cz.incad.nkp.digital.Vlastnik */
    public static final String NKP_VLASTNIK_CLASSNAME = "cz.incad.nkp.digital.Vlastnik";
    /** cz.incad.nkp.digital.Instituce */
    public static final String NKP_INSTITUCE_CLASSNAME = "cz.incad.nkp.digital.Instituce";
    /** cz.incad.nkp.digital.XTitNkpMf
     * MeziTabulka ** Mikrofilm - TFMonografie */
    public static final String NKP_XTitNkpMf_CLASSNAME = "cz.incad.nkp.digital.XTitNkpMf";
    /** cz.incad.nkp.digital.TitNkp */
    public static final String NKP_TITULNKP_CLASSNAME = "cz.incad.nkp.digital.TitNkp";
    
//Common
    /** Pole ukazující na TableRecord Převodních tabulek - cz.incad.nkp.digital.XTitNkpMf */
    public static final String NKP_TABLE_XTITNKPMF_FIELD = "tXTitNkpMf";

    /** Pole ukazující na TableRecord Převodních tabulek - cz.incad.rd.tXPredMikro */
    public static final String NKP_TABLE_XPREDMIKRO_FIELD = "tXPredMikro";


    /** TableField "tZakazka" obsahující ukazatele na záznamy z - cz.incad.nkp.digital.ZakazkaEntity */
    public static final String NKP_TABLE_ZAKAZKA_FLD = "tZakazka";
    /** Referenční pole ukazující na cz.incad.nkp.digital.TitNkp */
    public static final String NKP_REFERENCE_NA_TITNKP = "rTitNkp";

    /** Referenční pole ukazující na cz.incad.nkp.digital.TitNkp */
    public static final String NKP_REFERENCE_NA_PREDLOHA = "rPredloha";

    /** TableField "tZakazka" obsahující ukazatele na záznamy z - cz.incad.nkp.digital.Vlastnik */
    public static final String NKP_TABLE_VLASTNIK_FLD = "tVlastnik";
    /** Referenční pole "rZakazka" ukazující na cz.incad.nkp.digital.Zakazka */
    public static final String NKP_REFERENCE_NA_ZAKAZKA = "rZakazka";
    /** TableField "tUkol" obsahující ukazatele na záznamy z - cz.incad.nkp.digital.Ukol */
    public static final String NKP_TABLE_UKOL_FLD = "tUkol";
    
//TitNkp
    /** Pole ukazující na TableRecord SoubXML - cz.incad.nkp.digital.SoubXML */
    public static final String NKP_TABLE_SOUBXML_FIELD = "tSoubXML";
    /** Pole ukazující na TableRecord SoubXML - cz.incad.nkp.digital.SoubXML */
    public static final String NKP_POCET_POLI_FLD = "pocetPoli";
    
//Mikrofilm
    /** Pole pocetScanu(integer) v agendě cz.incad.nkp.digital.Mikrofilm */
    public static final String MIKROFILM_POCET_SCANU_FIELD = "pocetScanu";
    /** Pole celkemCistaP(integer) v agendě cz.incad.nkp.digital.Mikrofilm */
    public static final String MIKROFILM_POLE_CISTA_CELKEM_FIELD = "celkemCistaP";
    /** Pole celkemHrubaP(integer) v agendě cz.incad.nkp.digital.Mikrofilm */
    public static final String MIKROFILM_POLE_HRUBA_CELKEM_FIELD = "celkemHrubaP";
    /** Pole celkemDokumentu(integer) v agendě cz.incad.nkp.digital.Mikrofilm */
    public static final String MIKROFILM_POCET_DOKUMENTU_CELKEM_FIELD = "celkemDokumentu";
    /** Pole archCisloNeg(integer) v agendě cz.incad.nkp.digital.Mikrofilm - ČAN */
    public static final String MIKROFILM_ARCH_CIS_NEG_FLD = "archCisloNeg";
    
    
//XTitNkpMf - propojovací tabulka ** TFMonografie -- Mikrofilm
    /** Referenční pole ukazující z rMikrofilm na Mikrofilm */
    public static final String REFERENCE_Z_XTITNKPMF_NA_MIKROFILM = "rMikrofilm";
    /** Referenční pole ukazující z rMikrofilm na TitNkp */
    //public static final String REFERENCE_Z_XTITNKPMF_NA_TITNKP = "rTitNkp";
    /** Pole poradiVeSvitku(Integer) v agendě cz.incad.nkp.digital.XTitNkpMf */
    public static final String XTITNKPMF_PORADI_VE_SVITKU_FIELD = "poradiVeSvitku";
    /** Pole poleCista(Integer) v agendě cz.incad.nkp.digital.XTitNkpMf */
    public static final String XTITNKPMF_POLE_CISTA_FIELD = "poleCista";
    /** Pole poleHruba(Integer) v agendě cz.incad.nkp.digital.XTitNkpMf */
    public static final String XTITNKPMF_POLE_HRUBA_FIELD = "poleHruba";
    /** Pole pocetScanu(Integer) v agendě cz.incad.nkp.digital.XTitNkpMf */
    public static final String XTITNKPMF_POCET_SCANU_FIELD = "pocetScanu";
    /** Pole mfeCislo(String) v agendě cz.incad.nkp.digital.XTitNkpMf */
    public static final String XTITNKPMF_MFECISLO_FIELD = "mfeCislo";
    
//Zakazka
    /** Pole cisloZakazky(String) v agendě cz.incad.nkp.digital.ZakazkaEntity - číselná řada */
    public static final String ZAKAZKA_CISLO_ZAKAZKY_FLD = "cisloZakazky";
    
//Monografie
    /** Pole inverze(String) v agendě cz.incad.nkp.digital.MonografieEntity */
    public static final String MONOGRAFIE_E_CISLO_FLD = "mfeCislo";
    /** Pole hlNazev(String) v agendě cz.incad.nkp.digital.MonografieEntity */
    public static final String MONOGRAFIE_HL_NAZEV_FLD = "hlNazev";
    
//Ukol
    /** Pole typPrace(String) v agendě cz.incad.nkp.digital.Ukol */
    public static final String NKP_UKOL_TYP_PRACE_FLD = "typPrace";
    /** Pole zpracovatel(String) v agendě cz.incad.nkp.digital.Ukol */
    public static final String NKP_UKOL_ZPRACOVATEL_FLD = "zpracovatel";
    
//Vlastnik
    /** Pole vlastnik(String) v agendě cz.incad.nkp.digital.Vlastnik */
    public static final String NKP_VLASTNIK_VLASTNIK_FLD = "vlastnik";
    
    
}

/*
 * $Log$
 *
 */
