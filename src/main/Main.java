import tuote.Tuote;
import ostoskori.Ostoskori;

public class Main {
    public static void main(String[] args) {
        Ostoskori kori = new Ostoskori();
        kori.lisaaTuote(new Tuote("Maito", 1.5));
        kori.lisaaTuote(new Tuote("Leipä", 2.0));

        for (Tuote t : kori.getTuotteet()) {
            System.out.println(t);
        }
    }
}
