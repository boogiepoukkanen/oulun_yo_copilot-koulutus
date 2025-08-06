package ostoskori;

import java.sql.*;
import java.util.*;

public class Tietokanta {
    private static final String DB_URL = "jdbc:sqlite:ostoskori.db";
    private Connection _yhteys;

    public Tietokanta() {
        try {
            Class.forName("org.sqlite.JDBC");
            _yhteys = DriverManager.getConnection(DB_URL);
            luoTaulut();
        } catch (Exception e) {
            System.err.println("Virhe tietokantayhteyden alustuksessa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void luoTaulut() throws SQLException {
        Statement stmt = _yhteys.createStatement();
        // Yritetään lisätä saldo-kenttä olemassa olevaan tauluun, jos sitä ei ole
        try {
            stmt.executeUpdate("ALTER TABLE tuote ADD COLUMN saldo INTEGER DEFAULT 0");
        } catch (SQLException e) {
            // Kenttä on jo olemassa, ei tehdä mitään
        }
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS tuote (id INTEGER PRIMARY KEY AUTOINCREMENT, nimi TEXT, hinta REAL, kategoria TEXT, saldo INTEGER DEFAULT 0)");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ostoskori (tuote_id INTEGER)");
        stmt.close();
    }

    public List<Tuote> haeKaikkiTuotteet() throws SQLException {
        List<Tuote> tuotteet = new ArrayList<>();
        Statement stmt = _yhteys.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT id, nimi, hinta, kategoria, saldo FROM tuote");
        while (rs.next()) {
            Tuote t = new Tuote(rs.getString("nimi"), rs.getDouble("hinta"), rs.getInt("saldo"));
            t.setId(rs.getInt("id"));
            t.setKategoria(rs.getString("kategoria"));
            tuotteet.add(t);
        }
        rs.close();
        stmt.close();
        return tuotteet;
    }

    public List<Tuote> haeOstoskori() throws SQLException {
        List<Tuote> ostoskori = new ArrayList<>();
        Statement stmt = _yhteys.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT t.id, t.nimi, t.hinta FROM ostoskori o JOIN tuote t ON o.tuote_id = t.id");
        while (rs.next()) {
            Tuote t = new Tuote(rs.getString("nimi"), rs.getDouble("hinta"));
            t.setId(rs.getInt("id"));
            ostoskori.add(t);
        }
        rs.close();
        stmt.close();
        return ostoskori;
    }

    public void tallennaTuote(Tuote tuote) throws SQLException {
        if (tuote.getId() > 0) {
            PreparedStatement ps = _yhteys.prepareStatement("UPDATE tuote SET nimi=?, hinta=?, kategoria=?, saldo=? WHERE id=?");
            ps.setString(1, tuote.getNimi());
            ps.setDouble(2, tuote.getHinta());
            ps.setString(3, tuote.getKategoria());
            ps.setInt(4, tuote.getSaldo());
            ps.setInt(5, tuote.getId());
            ps.executeUpdate();
            ps.close();
        } else {
            PreparedStatement ps = _yhteys.prepareStatement("INSERT INTO tuote (nimi, hinta, kategoria, saldo) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, tuote.getNimi());
            ps.setDouble(2, tuote.getHinta());
            ps.setString(3, tuote.getKategoria());
            ps.setInt(4, tuote.getSaldo());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                tuote.setId(keys.getInt(1));
            }
            keys.close();
            ps.close();
        }
    }

    public void tallennaOstoskori(List<Tuote> tuotteet) {
        try {
            Statement stmt = _yhteys.createStatement();
            stmt.executeUpdate("DELETE FROM ostoskori");
            stmt.close();
            PreparedStatement ps = _yhteys.prepareStatement("INSERT INTO ostoskori (tuote_id) VALUES (?)");
            for (Tuote t : tuotteet) {
                ps.setInt(1, t.getId());
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Virhe ostoskorin tallennuksessa: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Tuote> haeTuotteetNimella(String nimi) throws SQLException {
        List<Tuote> tuotteet = new ArrayList<>();
        PreparedStatement ps = _yhteys.prepareStatement("SELECT id, nimi, hinta, kategoria FROM tuote WHERE nimi LIKE ?");
        ps.setString(1, "%" + nimi + "%");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Tuote t = new Tuote(rs.getString("nimi"), rs.getDouble("hinta"));
            t.setId(rs.getInt("id"));
            t.setKategoria(rs.getString("kategoria"));
            tuotteet.add(t);
        }
        rs.close();
        ps.close();
        return tuotteet;
    }

    public List<Tuote> haeTuotteetHintaValilla(double min, double max) throws SQLException {
        List<Tuote> tuotteet = new ArrayList<>();
        PreparedStatement ps = _yhteys.prepareStatement("SELECT id, nimi, hinta, kategoria FROM tuote WHERE hinta BETWEEN ? AND ?");
        ps.setDouble(1, min);
        ps.setDouble(2, max);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Tuote t = new Tuote(rs.getString("nimi"), rs.getDouble("hinta"));
            t.setId(rs.getInt("id"));
            t.setKategoria(rs.getString("kategoria"));
            tuotteet.add(t);
        }
        rs.close();
        ps.close();
        return tuotteet;
    }

    public List<Tuote> haeTuotteetKategoriassa(String kategoria) throws SQLException {
        List<Tuote> tuotteet = new ArrayList<>();
        PreparedStatement ps = _yhteys.prepareStatement("SELECT id, nimi, hinta, kategoria FROM tuote WHERE kategoria LIKE ?");
        ps.setString(1, "%" + kategoria + "%");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Tuote t = new Tuote(rs.getString("nimi"), rs.getDouble("hinta"));
            t.setId(rs.getInt("id"));
            t.setKategoria(rs.getString("kategoria"));
            tuotteet.add(t);
        }
        rs.close();
        ps.close();
        return tuotteet;
    }
}
