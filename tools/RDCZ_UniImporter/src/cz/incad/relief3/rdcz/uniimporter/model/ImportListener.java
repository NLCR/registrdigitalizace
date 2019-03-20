/* *****************************************************************************
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter.model;

/** ****************************************************************************
 *
 * @author martin.novacek@incad.cz
 */
public interface ImportListener extends Demon {

    /** ************************************************************************
     * 
     * 
     * @param oneIssue 
     */
    public void ImportIt(OneIssue oneIssue);
}
