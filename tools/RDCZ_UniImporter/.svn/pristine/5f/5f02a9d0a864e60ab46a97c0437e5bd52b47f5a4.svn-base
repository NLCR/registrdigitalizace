/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/** ****************************************************************************
 *
 * @author martin
 */
public class TimeStamp {
    /* Délka stringu který vrací metoda getTimeStamp(). */
    public static final int PREFIX_SIZE = 14;

    /** ************************************************************************
     * Privátní konstruktor
     */
    private TimeStamp() {
    }

    /** ************************************************************************
     * Metoda vrátí aktuální datum ja String ve formátu yyyyMMddHHmmss.
     * @return 
     */
    public static String getTimeStamp() {
        return getTimeStamp("yyyyMMddHHmmss", new Date());
    }
    
    /** ************************************************************************
     * Metoda vrátí aktuální datum jako String ve formátu, který je zadán jako parametr.
     * @param mask návratový formát
     * @return 
     */
    public static String getTimeStamp(String mask) {
        return getTimeStamp(mask, new Date());
    }

    /** ************************************************************************
     * Metoda vrátí zadaný datum jako String ve formátu, který je zadán jako parametr.
     * @param mask
     * @param date
     * @return 
     */
    public static String getTimeStamp(String mask, Date date) {
        return new SimpleDateFormat(mask).format(date);
    }

}
