package generatebowbaseline_original;

/**
 *
 * @author kico
 */
public class Features {
    public int NComas = 0;
    public int NPuntos = 0;
    public int N2Puntos = 0;
    
    public void GetNumFeatures(String text) {
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
        }
    }
}
