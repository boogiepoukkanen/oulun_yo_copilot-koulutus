package ostoskori;

import java.util.ArrayList;
import java.util.List;

public class Ostoskori {
    private List<Tuote> tuotteet = new ArrayList<>();

    public void lisaaTuote(Tuote tuote) {
        tuotteet.add(tuote);
    }

    public void poistaTuote(Tuote tuote) {
        tuotteet.remove(tuote);
    }

    public List<Tuote> getTuotteet() {
        return tuotteet;
    }

    public double getKokonaishinta() {
        double summa = 0.0;
        for (Tuote tuote : tuotteet) {
            summa += tuote.getHinta();
        }
        return summa;
    }
}
