package gender;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author kicorangel
 */
public class GenerateBOWBaseline {
//    private static String PATH = "/data/pan13/pan13-author-profiling-training-corpus-2013-01-09/es/";
//    private static String TRUTH = "/data/pan13/pan13-author-profiling-training-corpus-2013-01-09/truth-es.txt";
//    private static String BOW = "/data/pan13/bow-es.txt";
//    private static String OUTPUT = "/data/pan13/pan-ap-13-training-es-{task}-extended.arff";
//    private static int NTERMS = 100;

    private static String PATH = "data/pan13/pan13-author-profiling-test-corpus2-2013-04-29/es/";
    private static String TRUTH = "data/pan13/pan13-author-profiling-test-corpus2-2013-04-29/truth-es.txt";
    private static String BOW = "data/pan13/bow-es-man.txt";
    private static String BOWFEM = "data/pan13/bow-es-female.txt";
    private static String OUTPUT = "data/pan13/gender_results/pan-ap-13-test-es-{task}-extended4.arff";
    private static int NTERMS = 500;
	    
    public static void main(String[] args) {
    
        try {
            Hashtable<String, TruthInfo> oTruth = ReadTruth(TRUTH);

            /*Palabras más frecuentes en hombres*/
            ArrayList<String>oBOWMale = ReadBOW(PATH, BOW, oTruth);
            
            /*Palabras más frecuentes en mujeres*/
            ArrayList<String>oBOWFemale = ReadBOWFemale(PATH, BOWFEM, oTruth);

            /*Frecuencias de las palabras más frecuentes en hombres*/
            ArrayList<String>freqBOWMale = FreqBOW(PATH, BOW, oTruth);
            
            /*Frecuencias de las palabras más frecuentes en mujeres*/
            ArrayList<String>freqBOWFemale = FreqBOW(PATH, BOWFEM, oTruth);
            
            GenerateBaseline(PATH, oBOWMale, oBOWFemale, freqBOWMale, freqBOWFemale, oTruth, OUTPUT.replace("{task}", "gender"), "MALE, FEMALE");
            GenerateBaseline(PATH, oBOWMale, oBOWFemale, freqBOWFemale, freqBOWFemale, oTruth, OUTPUT.replace("{task}", "age"), "10S, 20S, 30S");
            
        }catch (Exception ex) {
            
        }
    }
    
    private static void GenerateBaseline(String path, ArrayList<String> aBOW, ArrayList<String> aBOWFemale, ArrayList<String> freqBOWMale,  ArrayList<String> freqBOWFemale, Hashtable<String, TruthInfo> oTruth, String outputFile, String classValues) {
        FileWriter fw = null;
        
        try {
            fw = new FileWriter(outputFile);
            fw.write(Weka.HeaderToWeka(aBOW, aBOWFemale, NTERMS, classValues));
            fw.flush();
            
            File directory = new File(path);
            String [] files = directory.list();
            for (int iFile = 0; iFile < files.length; iFile++) 
            {
                System.out.println("--> Generating " + (iFile+1) + "/" + files.length);
                try {
                    Hashtable<String, Integer> oDocBOW = new Hashtable<String, Integer>();

                    String sFileName = files[iFile];

                    File fXmlFile = new File(path + "/" + sFileName);
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(fXmlFile);
                    NodeList documents = doc.getDocumentElement().getElementsByTagName("conversation");
                    String []fileInfo = sFileName.split("_");
                    String sAuthor = fileInfo[0];
                    String sAuthorContent = "";
                    
                    // Recorro todos los documentos de un autor y obtengo sus palabras (oDocBOW)
                    double longAllDocuments = 0;
                    
                    for (int i=0;i<documents.getLength();i++) {
                        try {
                            Element element = (Element)documents.item(i);
                            String sHtml = element.getTextContent();
                            String sContent = GetText(sHtml);
                            ArrayList<String> aTerms = getTokens(sContent);
                            sAuthorContent += sContent + " " ;

                            for (int t=0; t<aTerms.size(); t++) {
                                String sTerm = aTerms.get(t);
                                int iFreq = 0;
                                if (oDocBOW.containsKey(sTerm)) {
                                    iFreq = oDocBOW.get(sTerm);
                                }
                                oDocBOW.put(sTerm, ++iFreq);
                            }
                            longAllDocuments = longAllDocuments + aTerms.size();
                            
                        } catch (Exception ex) {
                                    System.out.println("ERROR: " + ex.toString());
                        }
                    }
                    
                    
                    Features oFeatures = new Features();
                    oFeatures.GetNumFeatures(sAuthorContent);
                    
                    if (oTruth.containsKey(sAuthor)) {
                        TruthInfo truth = oTruth.get(sAuthor);
                        String sGender = truth.Gender.toUpperCase();
                        String sAge = truth.Age.toUpperCase();

                        if (classValues.contains("MALE")) {
                            fw.write(Weka.FeaturesToWeka(aBOW, aBOWFemale, freqBOWMale, freqBOWFemale, oDocBOW, oFeatures, NTERMS, sGender));        
                        } else {
                            fw.write(Weka.FeaturesToWeka(aBOW, aBOWFemale, freqBOWMale, freqBOWFemale, oDocBOW, oFeatures, NTERMS, sAge));
                        }
                        fw.flush();
                    }

                 } catch (Exception ex) {
                    System.out.println("ERROR: " + ex.toString());
                 }
            }
        } catch (Exception ex) {
            
        } finally {
            if (fw!=null) { try { fw.close(); } catch (Exception k) {} }
        }
    }
    
    /* Male: bolsa de palabras más frecuentes*/
    private static ArrayList<String> ReadBOW(String corpusPath, String bowPath, Hashtable<String, TruthInfo> oTruth) {
        Hashtable<String, Integer> oBOW = new Hashtable<String, Integer>();
        ArrayList<String> aBOW = new ArrayList<String>();
        
        if (new File(bowPath).exists()) {
        	
        	/*Male*/
            FileReader fr = null;
            BufferedReader bf = null;

            try {
                fr = new FileReader(bowPath);
                bf = new BufferedReader(fr);
                String sCadena = "";

                while ((sCadena = bf.readLine())!=null)
                {
                    String []data = sCadena.split(":::");
                    if (data.length==2) {
                        String sTerm = data[0];
                        aBOW.add(sTerm);
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.toString());
            } finally {
                if (bf!=null) { try { bf.close(); } catch (Exception k) {} }
                if (fr!=null) { try { fr.close(); } catch (Exception k) {} }
            }
            
        } else {
            File directory = new File(corpusPath);
            File []files = directory.listFiles();

            for (int iFile = 0; iFile < files.length; iFile++)  {
                System.out.println("--> Preprocessing " + (iFile+1) + "/" + files.length);

                try {
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(files[iFile]);
                    NodeList documents = doc.getDocumentElement().getElementsByTagName("conversation");
                    
                    /* Obtener el sexo y genero del autor*/
                    String sFileName = files[iFile].toString();
                    String []fileInfo = sFileName.split("_");
                    
                    String sAuthor = fileInfo[0];
                    sAuthor = (sAuthor.split("/"))[4];
                    String sGender = null;
                    String sAge = null;
                    
                    if (oTruth.containsKey(sAuthor)) {
                        TruthInfo truth = oTruth.get(sAuthor);
                        sGender = truth.Gender.toUpperCase();
                        sAge = truth.Age.toUpperCase();
                    }
                    /**/
                    
                    double iWords = 0;
                    double iDocs = documents.getLength();
                    for (int i=0;i<iDocs;i++) {
                        Element element = (Element)documents.item(i);
                        String sHtml = element.getTextContent();
                        String sContent = GetText(sHtml);
                        ArrayList<String> aTerms = getTokens(sContent);
                        for (int t=0; t<aTerms.size(); t++) {
                            String sTerm = aTerms.get(t);
                            
                            //if (sTerm.length() > 1) {
	                            if (sGender.equalsIgnoreCase("MALE")) {
	                                int iFreq = 0;
	
	                                if (oBOW.containsKey(sTerm)) {
	                                    iFreq = oBOW.get(sTerm);
	                                }
	                                oBOW.put(sTerm, ++iFreq);
	                            }
                            //}
                        }
                    }
                } catch (Exception ex) {

                }
            }
            
            ValueComparator bvc =  new ValueComparator(oBOW);
            TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
            sorted_map.putAll(oBOW);
            
            FileWriter fw = null;
            try {
                fw = new FileWriter(bowPath);
                for( Iterator<String> it = sorted_map.keySet().iterator(); it.hasNext();) {
                    String sTerm = it.next();
                    int iFreq = oBOW.get(sTerm);

                    aBOW.add(sTerm);
                    fw.write(sTerm + ":::" + iFreq + "\n");
                    fw.flush();
                }
            } catch (Exception ex) {
                
            } finally {
                if (fw!=null) { try {fw.close();} catch(Exception k) {} }
            }
        }
        
        return aBOW;
    }
    
    
    /* Female: bolsa de palabras más frecuentes*/
    private static ArrayList<String> ReadBOWFemale(String corpusPath, String bowPath, Hashtable<String, TruthInfo> oTruth) {
        Hashtable<String, Integer> oBOW = new Hashtable<String, Integer>();
        ArrayList<String> aBOW = new ArrayList<String>();
        
        if (new File(bowPath).exists()) {
        	
            FileReader fr = null;
            BufferedReader bf = null;

            try {
                fr = new FileReader(bowPath);
                bf = new BufferedReader(fr);
                String sCadena = "";

                while ((sCadena = bf.readLine())!=null)
                {
                    String []data = sCadena.split(":::");
                    if (data.length==2) {
                        String sTerm = data[0];
                        aBOW.add(sTerm);
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.toString());
            } finally {
                if (bf!=null) { try { bf.close(); } catch (Exception k) {} }
                if (fr!=null) { try { fr.close(); } catch (Exception k) {} }
            }
            
        } else {
            File directory = new File(corpusPath);
            File []files = directory.listFiles();

            for (int iFile = 0; iFile < files.length; iFile++)  {
                System.out.println("--> Preprocessing " + (iFile+1) + "/" + files.length);

                try {
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(files[iFile]);
                    NodeList documents = doc.getDocumentElement().getElementsByTagName("conversation");
                    
                    /* Obtener el sexo y genero del autor*/
                    String sFileName = files[iFile].toString();
                    String []fileInfo = sFileName.split("_");
                    
                    String sAuthor = fileInfo[0];
                    sAuthor = (sAuthor.split("/"))[4];
                    String sGender = null;
                    String sAge = null;
                    
                    if (oTruth.containsKey(sAuthor)) {
                        TruthInfo truth = oTruth.get(sAuthor);
                        sGender = truth.Gender.toUpperCase();
                        sAge = truth.Age.toUpperCase();
                    }
                    /**/
                    
                    double iDocs = documents.getLength();
                    for (int i=0;i<iDocs;i++) {
                        Element element = (Element)documents.item(i);
                        String sHtml = element.getTextContent();
                        String sContent = GetText(sHtml);
                        ArrayList<String> aTerms = getTokens(sContent);
                        for (int t=0; t<aTerms.size(); t++) {
                            String sTerm = aTerms.get(t);
                            
                            //if (sTerm.length() > 1) {
	                            if (sGender.equalsIgnoreCase("FEMALE")) {
	                                int iFreq = 0;
	
	                                if (oBOW.containsKey(sTerm)) {
	                                    iFreq = oBOW.get(sTerm);
	                                }
	                                oBOW.put(sTerm, ++iFreq);
	                            }
                            //}
                        }
                    }
                } catch (Exception ex) {

                }
            }
            
            ValueComparator bvc =  new ValueComparator(oBOW);
            TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
            sorted_map.putAll(oBOW);
            
            FileWriter fw = null;
            try {
                fw = new FileWriter(bowPath);
                for( Iterator<String> it = sorted_map.keySet().iterator(); it.hasNext();) {
                    String sTerm = it.next();
                    int iFreq = oBOW.get(sTerm);

                    aBOW.add(sTerm);
                    fw.write(sTerm + ":::" + iFreq + "\n");
                    fw.flush();
                }
            } catch (Exception ex) {
                
            } finally {
                if (fw!=null) { try {fw.close();} catch(Exception k) {} }
            }
        }
        
        return aBOW;
    }   
    
    /************************/
    
    /* Frecuencias de aparición de las palabras más frecuentes*/
    private static ArrayList<String> FreqBOW(String corpusPath, String bowPath, Hashtable<String, TruthInfo> oTruth) {
        Hashtable<String, Integer> oBOW = new Hashtable<String, Integer>();
        ArrayList<String> aBOW = new ArrayList<String>();
        
        if (new File(bowPath).exists()) {
        	
        	/*Male*/
            FileReader fr = null;
            BufferedReader bf = null;

            try {
                fr = new FileReader(bowPath);
                bf = new BufferedReader(fr);
                String sCadena = "";

                while ((sCadena = bf.readLine())!=null)
                {
                    String []data = sCadena.split(":::");
                    if (data.length==2) {
                        String sTerm = data[1];
                        aBOW.add(sTerm);
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex.toString());
            } finally {
                if (bf!=null) { try { bf.close(); } catch (Exception k) {} }
                if (fr!=null) { try { fr.close(); } catch (Exception k) {} }
            }
            
        }
        
        return aBOW;
    }
    
    
    /************************/
    
    private static Hashtable<String, TruthInfo> ReadTruth(String path) {
        Hashtable<String, TruthInfo> oTruth = new Hashtable<String, TruthInfo>();
        
        FileReader fr = null;
        BufferedReader bf = null;
        
        try {
            fr = new FileReader(path);
            bf = new BufferedReader(fr);
            String sCadena = "";

            while ((sCadena = bf.readLine())!=null)
            {
                String []data = sCadena.split(":::");
                if (data.length==3) {
                    String sAuthorId = data[0];
                    if (!oTruth.containsKey(sAuthorId)) {
                        TruthInfo info = new TruthInfo();
                        info.Gender = data[1];
                        info.Age= data[2];
                        oTruth.put(sAuthorId, info);
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        } finally {
            if (bf!=null) { try { bf.close(); } catch (Exception k) {} }
            if (fr!=null) { try { fr.close(); } catch (Exception k) {} }
        }
        
        return oTruth;
    }
    
     public static ArrayList<String> getTokens(String text) throws IOException {
        return getTokens(new SpanishAnalyzer(new String[0]), "myfield", text);
    }
    
    public static ArrayList<String> getTokens(Analyzer analyzer, String field, String text) throws IOException {
        return getTokens(analyzer.tokenStream(field,  new StringReader(text)));
    }

    public static ArrayList<String> getTokens(TokenStream stream) throws IOException {
        ArrayList<String> oTokens = new ArrayList<String>();
        TermAttribute term = stream.addAttribute(TermAttribute.class);
        while(stream.incrementToken()) {
            oTokens.add(term.term());
        }
        return oTokens;
    }
    
    public static ArrayList<String> getTokens(Analyzer analyzer, String text) throws IOException {
        return getTokens(analyzer.tokenStream("myfield", new StringReader(text)));
    }
    
    public static String GetText(String html)
    {
        try {
            Html2Text html2text = new Html2Text();
            Reader in = new StringReader(html);
            html2text.parse(in);
            return html2text.getText();
        } catch (IOException ex) {
            return html;
        }
    }
}
