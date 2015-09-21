package age;


/**
 *
 * @author kico
 */
public class Features {
	
	//Total documents
    public int NComas = 0;
    public int NPuntos = 0;
    public int N2Puntos = 0;
    public int Nopenadm = 0;
    public int Ncloseadm = 0;
    public int Nopenquestion = 0;
    public int Nclosequestion = 0;
    public int Nk = 0;
    
        
	public float meanDocumentLength;
	
	private int numberOfDocuments; 
	
	public float meanWordLengthPerAuthor;
	
	private float meanWordsPerDocument;
	
	private int floodings;
	
	private int risa;
			
    public void GetTotalDocumentsFeatures(String text) {
        for (int i=0;i<text.length();i++) {
            if (text.charAt(i) == ',') {
                NComas++;
            }
            if (text.charAt(i) == '.') {
                NPuntos++;
            }
            if (text.charAt(i) == ':') {
                N2Puntos++;
            }
            if (text.charAt(i) == '¡') {
            	Nopenadm++;
            }
            if (text.charAt(i) == '!') {
            	Ncloseadm++;
            }
            if (text.charAt(i) == '¿') {
            	Nopenquestion++;
            }
            if (text.charAt(i) == '?') {
            	Nclosequestion++;
            }
            if (text.charAt(i) == 'k' || text.charAt(i) == 'K') {
            	Nk++;
            }
        }
        
        meanDocumentLength = text.length()/numberOfDocuments;
    }
        
    public void setNumberOfDocuments(int numberOfDocuments) {
		this.numberOfDocuments = numberOfDocuments;
	}
    
    
	public float getMeanWordLengthPerAuthor() {
		return meanWordLengthPerAuthor;
	}

	public void setMeanWordLengthPerAuthor(float meanWordLengthPerAuthor) {
		this.meanWordLengthPerAuthor = meanWordLengthPerAuthor;
	}

	public float getMeanWordsPerDocument() {
		return meanWordsPerDocument;
	}

	public void setMeanWordsPerDocument(float meanWordsPerDocument) {
		this.meanWordsPerDocument = meanWordsPerDocument;
	}

	public int getFloodings() {
		return floodings;
	}

	public void setFloodings(int floodings) {
		this.floodings = floodings;
	}

	public int getRisa() {
		return risa;
	}

	public void setRisa(int risa) {
		this.risa = risa;
	}

}
