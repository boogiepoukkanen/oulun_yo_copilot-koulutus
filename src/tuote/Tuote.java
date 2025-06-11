package tuote;

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

    public double getHinta() {
        return hinta;
    }

    @Override
    public String toString() {
        return nimi + " (" + hinta + " €)";
    }
}
