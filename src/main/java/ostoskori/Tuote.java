package ostoskori;

public class Tuote {
    private String nimi;
    private double hinta;
 


    public Tuote(String nimi, double hinta) {
        this.nimi = nimi;
        this.hinta = hinta;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
    public void setHinta(double hinta) {
        this.hinta = hinta;
    }

    public double getHinta() {
        return hinta;
    }

}