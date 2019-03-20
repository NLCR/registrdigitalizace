/* *****************************************************************************
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter.model;

/**
 *******************************************************************************
 *
 * @author Martin
 */
public class OneXmlPart {

    public final String idCislo;
    public final String xml;

    /**
     ***************************************************************************
     * Konstruktor
     *
     * @param idCislo
     * @param xml
     */
    public OneXmlPart(String idCislo, String xml) {
        this.idCislo = idCislo;
        this.xml = xml;
    }

}
