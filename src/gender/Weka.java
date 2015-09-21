package gender;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeMap;

/**
 *
 * @author kico
 */
public class Weka {
    
    public static String HeaderToWeka(ArrayList<String> BOW, ArrayList<String> BOWFemale, int iNTerms, String classValue)
    {
        String sHeader =  "@relation 'BOW'\n" ;

        for (int i=0;i<BOW.size();i++) {
            String sTerm = BOW.get(i);
            sHeader += "@attribute 'term-m-" + sTerm.replaceAll("'", "quote") + "' real\n";
            
            if (i>=iNTerms) {
                break;
            }
        }

        for (int i=0;i<BOWFemale.size();i++) {
            String sTerm = BOWFemale.get(i);
            sHeader += "@attribute 'term-f-" + sTerm.replaceAll("'", "quote") + "' real\n";
            
            if (i>=iNTerms) {
                break;
            }
        }
        
        
//        sHeader += "@attribute 'ncomas' real\n" +
//                "@attribute 'npuntos' real\n" +
//                "@attribute 'n2puntos' real\n";
        sHeader += "@attribute 'class' {" + classValue + "}\n" +
        "@data\n";
        return sHeader;
    }
    
    /*
     * BOW      : Bolsa de palabras de hombres.
     * BOWFemale: Bolsa de palabras de mujeres.
     * oDoc     : Palabras del autor.
     */
    
    
    public static String FeaturesToWeka(ArrayList<String> BOW, ArrayList<String> BOWFemale, ArrayList<String> freqBOWMale, ArrayList<String> freqBOWFemale, Hashtable<String, Integer>oDoc, Features oFeatures, int iN, String classValue)    {
        String weka = "";
        
        // Número total de palabras del autor.
        int iTotal = oDoc.size();
        
        // Recorremos la bolsa de palabras de hombres.
        for (int i=0;i<BOW.size();i++) {
            String sTerm = BOW.get(i);
            double freq = 0;
            double freqMale = Double.parseDouble(freqBOWMale.get(i).replaceAll(" ","."));
            
            // Si el autor ha mencionado la palabra de la bolsa de hombres
            if (oDoc.containsKey(sTerm)) {
            	
            	//Nº de veces que el autor menciona la palabra entre el total de palabras.
                //freq = (double)((double)oDoc.get(sTerm) / (double)iTotal);
            	
            	freq = (double)((double)oDoc.get(sTerm) / (double)iTotal) * freqMale;
            }
            
            weka += freq + ",";
            
            if (i>=iN) {
                break;
            }
        }

        // Recorremos la bolsa de palabras de mujeres.
        for (int i=0;i<BOWFemale.size();i++) {
            String sTerm = BOWFemale.get(i);
            double freq = 0;
            double freqFemale = Double.parseDouble(freqBOWFemale.get(i).replaceAll(" ","."));
            
            if (oDoc.containsKey(sTerm)) {
                freq = (double)((double)oDoc.get(sTerm) / (double)iTotal) * freqFemale;
            }
            
            weka += freq + ",";
            
            if (i>=iN) {
                break;
            }
        }
//        
//        weka += (double)((double)oFeatures.NComas / (double)iTotal) + "," + 
//                (double)((double)oFeatures.NPuntos / (double)iTotal) + "," + 
//                (double)((double)oFeatures.N2Puntos / (double)iTotal) + ",";
        
        weka +=  classValue + "\n";
        
        return weka;
    }
}
