package ostoskori;

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

    
}
