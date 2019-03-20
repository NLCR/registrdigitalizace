/* *****************************************************************************
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.incad.relief3.rdcz.uniimporter;

import cz.incad.relief3.rdcz.uniimporter.model.DataParserMarcXmlOAI;
import cz.incad.relief3.rdcz.uniimporter.model.OneIssue;
import cz.incad.relief3.rdcz.uniimporter.utils.OneSource;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *******************************************************************************
 *
 * @author Martin
 */
public class Test_ParserXMLOAI {

    private static final Logger LOG = Logger.getLogger(Test_ParserXMLOAI.class.getName());

    /**
     ***************************************************************************
     *
     * @param args
     */
    public static void main(String[] args) {
        String arg0; //filename
        OneIssue oneIssue = null;

        System.out.println(Test_ParserXMLOAI.class.getName() + " starting...");

        if (args.length != 1) {
            System.out.println("ERROR: špatný počet parametrů");
            return;
        }

        arg0 = args[0];

        try {
            System.out.println("");
            //Press any key
            System.out.println(":: Properties ::");
            System.out.println("Filename : " + arg0);
            System.out.println("");
            System.out.println("");

            DataParserMarcXmlOAI dp = new DataParserMarcXmlOAI();
            File file = new File(arg0);
            OneSource oneSource = new OneSource();
            oneIssue = new OneIssue(file, oneSource);
            dp.parseIt(oneIssue);

        } catch (Exception ex) {
            //ex.printStackTrace();
            LOG.log(Level.SEVERE, null, ex);
            System.out.println("ERROR: " + ex.getMessage());
        } finally {
            System.out.println("");
            System.out.println("");
            System.out.println(":: FAIL ::");
            if (oneIssue != null) {
                System.out.println(oneIssue.getLogFailInfo());
            }
        }
    }

}
