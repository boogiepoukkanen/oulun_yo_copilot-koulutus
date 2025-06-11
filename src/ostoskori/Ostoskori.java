package ostoskori;

import tuote.Tuote;
import java.util.ArrayList;
import java.util.List;

public class Ostoskori {
    private List<Tuote> tuotteet = new ArrayList<>();

    public void lisaaTuote(Tuote tuote) {
        tuotteet.add(tuote);
    }

    public List<Tuote> getTuotteet() {
        return tuotteet;
    }

    // Copilot voi ehdottaa esimerkiksi yhteishinnan laskua, tuotteiden poistamista jne.
}
