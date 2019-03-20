/* *****************************************************************************
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter.utils;

/**
 *******************************************************************************
 *
 * @author martin
 */
public class Commons {

    /**
     ***************************************************************************
     *
     */
    private Commons() {
    }

    public static final int MAX_STRING_LENGHT = 2500;

    /**
     ***************************************************************************
     *
     * @param leader
     * @return
     */
    public static final String getDruhDokumentuFromLeader(String leader) {
        if (leader != null && leader.length() > 7) {
            switch (leader.toCharArray()[7]) {
                case 's':
                    return "SE";
                case 'i':
                    return "IZ";
                default:
                    switch (leader.toCharArray()[6]) {
                        case 'i':
                            return "AV";
                        case 'j':
                            return "AV";
                        case 'c':
                            return "MU";
                        case 'e':
                            return "MP";
                        default:
                            return "BK";
                    }
            }
        }
        //změna z 2017.11.21 - po domluvě s p. Dvořákovou
        //return null;
        return "BK";
    }

}
