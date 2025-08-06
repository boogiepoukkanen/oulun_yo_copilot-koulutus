package ostoskori;
/**
 * Tämä sovellus sisältää ostoskorin, johon voi lisätä tuotteita.
 * Tuotteilla on erilaisia ominaisuuksia, kuten päivämäärä, paino, koko yms.
 * 
 */



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
