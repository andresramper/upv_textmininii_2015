package age;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author kico
 */
public class Weka {
    
    public static String HeaderToWeka(ArrayList<String> BOW, int iNTerms, String classValue)
    {
        String sHeader =  "@relation 'BOW'\n" ;

        /*for (int i=0;i<BOW.size();i++) {
            String sTerm = BOW.get(i);
            sHeader += "@attribute 'term-" + sTerm.replaceAll("'", "quote") + "' real\n";
            
            if (i>=iNTerms) {
                break;
            }
        }*/
        sHeader += "@attribute 'ncomas' real\n" +
                "@attribute 'npuntos' real\n" +
                "@attribute 'n2puntos' real\n";
        
        sHeader += "@attribute 'nopadm' real\n" +
                "@attribute 'ncladm' real\n" +
                "@attribute 'nopqt' real\n" +
                "@attribute 'nclqt' real\n";
        
        sHeader += "@attribute 'nk' real\n";
        
        sHeader += "@attribute 'documentlength' real\n" +
        		"@attribute 'wordsperdocument' real\n" +
                "@attribute 'wordlength' real\n";
        
        sHeader += "@attribute 'floodings' real\n";
        
        sHeader += "@attribute 'risa' real\n";
        
        sHeader += "@attribute 'class' {" + classValue + "}\n" +
        "@data\n";
        return sHeader;
    }
    
    public static String FeaturesToWeka(ArrayList<String> BOW, Hashtable<String, Integer>oDoc, Features oFeatures, int iN, String classValue)    {
        String weka = "";
        int iTotal = oDoc.size();
        /*for (int i=0;i<BOW.size();i++) {
            String sTerm = BOW.get(i);
            double freq = 0;
            if (oDoc.containsKey(sTerm)) {
                freq = (double)((double)oDoc.get(sTerm) / (double)iTotal);
            }
            
            weka += freq + ",";
            	
            if (i>=iN) {
                break;
            }
        }*/
        
        weka += (double)((double)oFeatures.NComas / (double)iTotal) + "," + 
                (double)((double)oFeatures.NPuntos / (double)iTotal) + "," + 
                (double)((double)oFeatures.N2Puntos / (double)iTotal) + ",";
        
        weka += (double)((double)oFeatures.Nopenadm / (double)iTotal) + "," + 
                (double)((double)oFeatures.Ncloseadm / (double)iTotal) + "," + 
                (double)((double)oFeatures.Nopenquestion / (double)iTotal) + "," +
        		(double)((double)oFeatures.Nclosequestion / (double)iTotal) + ",";
        
        weka += (double)((double)oFeatures.Nk / (double)iTotal) + ",";
        
        weka += (double)((double)oFeatures.meanDocumentLength) + "," + 
        		(double)((double)oFeatures.getMeanWordsPerDocument()) + "," + 
                (double)((double)oFeatures.getMeanWordLengthPerAuthor()) + ",";
        
        weka += (double)((double)oFeatures.getFloodings()) + ",";
        
        weka += (double)((double)oFeatures.getRisa()) + ",";
        
        weka +=  classValue + "\n";
        
        return weka;
    }
}
