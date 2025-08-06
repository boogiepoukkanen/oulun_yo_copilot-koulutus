package ostoskori;

public class Tuote {
    private String _nimi;
    private double _hinta;
    private int _id;
    private double paino;
    private String koko;
    private String paivays;
    private String kuvaus;
    private String valmistaja;
    private String kategoria;
    private String merkki;
    private String malli;
    private String vari;
    private String materiaali;
    private String kokoYksikko;
    private String painoYksikko;
    private String alkuperamaa;
    private String valmistusmaa;
    private String ean;
    private int _saldo;



    // Oletuskonstruktori

    public Tuote(String nimi, double hinta) {
        this._nimi = nimi;
        this._hinta = hinta;
    }

    public Tuote(String nimi, double hinta, int saldo) {
        this._nimi = nimi;
        this._hinta = hinta;
        this._saldo = saldo;
    }

    public String getNimi() {
        return _nimi;
    }

    public void setNimi(String nimi) {
        this._nimi = nimi;
    }
    
    public void setHinta(double hinta) {
        this._hinta = hinta;
    }

    public double getHinta() {
        return _hinta;
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }


    public double getPaino() {
        return paino;
    }

    public void setPaino(double paino) {
        this.paino = paino;
    }

    public String getKoko() {
        return koko;
    }

    public void setKoko(String koko) {
        this.koko = koko;
    }

    public String getPaivays() {
        return paivays;
    }

    public void setPaivays(String paivays) {
        this.paivays = paivays;
    }

    public String getKuvaus() {
        return kuvaus;
    }
    
    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }
    public String getValmistaja() {
        return valmistaja;
    }
    
    public void setValmistaja(String valmistaja) {
        this.valmistaja = valmistaja;
    }
    public String getKategoria() {
        return kategoria;
    }
    
    public void setKategoria(String kategoria) {
        this.kategoria = kategoria;
    }
    public String getMerkki() {
        return merkki;
    }
    
    public void setMerkki(String merkki) {
        this.merkki = merkki;
    }
    public String getMalli() {
        return malli;
    }
    
    public void setMalli(String malli) {
        this.malli = malli;
    }
    public String getVari() {
        return vari;
    }
    
    public void setVari(String vari) {
        this.vari = vari;
    }
    public String getMateriaali() {
        return materiaali;
    }
    
    public void setMateriaali(String materiaali) {
        this.materiaali = materiaali;
    }

    public String getKokoYksikko() {
        return kokoYksikko;
    }
    
    public void setKokoYksikko(String kokoYksikko) {
        this.kokoYksikko = kokoYksikko;
    }

    public String getPainoYksikko() {
        return painoYksikko;
    }
    
    public void setPainoYksikko(String painoYksikko) {
        this.painoYksikko = painoYksikko;
    }

    public String getAlkuperamaa() {
        return alkuperamaa;
    }
    
    public void setAlkuperamaa(String alkuperamaa) {
        this.alkuperamaa = alkuperamaa;
    }

    public String getValmistusmaa() {
        return valmistusmaa;
    }
    
    public void setValmistusmaa(String valmistusmaa) {
        this.valmistusmaa = valmistusmaa;
    }

    public String getEan() {
        return ean;
    }
    
    public void setEan(String ean) {
        this.ean = ean;
    }

    public int getSaldo() {
        return _saldo;
    }

    public void setSaldo(int saldo) {
        this._saldo = saldo;
    }

    // tämä metodi listaa tuotteen ominaisuudet merkkijonona
    // jos ominaisuutta ei ole asetettu, se ei näy tulosteessa
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Tuote{");
        sb.append("nimi='").append(_nimi).append('\'');
        sb.append(", hinta=").append(_hinta);
        sb.append(", id=").append(_id);
        sb.append(", saldo=").append(_saldo);

        if (paino != 0) sb.append(", paino=").append(paino);
        if (koko != null && !koko.isEmpty()) sb.append(", koko='").append(koko).append('\'');
        if (paivays != null && !paivays.isEmpty()) sb.append(", paivays='").append(paivays).append('\'');
        if (kuvaus != null && !kuvaus.isEmpty()) sb.append(", kuvaus='").append(kuvaus).append('\'');
        if (valmistaja != null && !valmistaja.isEmpty()) sb.append(", valmistaja='").append(valmistaja).append('\'');
        if (kategoria != null && !kategoria.isEmpty()) sb.append(", kategoria='").append(kategoria).append('\'');
        if (merkki != null && !merkki.isEmpty()) sb.append(", merkki='").append(merkki).append('\'');
        if (malli != null && !malli.isEmpty()) sb.append(", malli='").append(malli).append('\'');
        if (vari != null && !vari.isEmpty()) sb.append(", vari='").append(vari).append('\'');
        if (materiaali != null && !materiaali.isEmpty()) sb.append(", materiaali='").append(materiaali).append('\'');
        if (kokoYksikko != null && !kokoYksikko.isEmpty()) sb.append(", kokoYksikko='").append(kokoYksikko).append('\'');
        if (painoYksikko != null && !painoYksikko.isEmpty()) sb.append(", painoYksikko='").append(painoYksikko).append('\'');
        if (alkuperamaa != null && !alkuperamaa.isEmpty()) sb.append(", alkuperamaa='").append(alkuperamaa).append('\'');
        if (valmistusmaa != null && !valmistusmaa.isEmpty()) sb.append(", valmistusmaa='").append(valmistusmaa).append('\'');
        if (ean != null && !ean.isEmpty()) sb.append(", ean='").append(ean).append('\'');
        sb.append('}');
        return sb.toString();
    }

}