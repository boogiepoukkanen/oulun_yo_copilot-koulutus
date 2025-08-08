package ostoskori;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class OstoskoriGUI extends JFrame {
    private Ostoskori _ostoskori = new Ostoskori();
    private DefaultListModel<Tuote> _tuoteListaMalli = new DefaultListModel<>();
    private DefaultListModel<Tuote> _koriListaMalli = new DefaultListModel<>();
    private JList<Tuote> _tuoteLista = new JList<>(_tuoteListaMalli);
    private JList<Tuote> _koriLista = new JList<>(_koriListaMalli);
    private JLabel _hintaLabel = new JLabel("Kokonaishinta: 0.00 €");
    private Tietokanta _tietokanta = new Tietokanta();
    private ResourceBundle _kieliBundle = lataaKieliBundle("suomi");
    private String _valittuKieli = "suomi";

    // UI components that need translation
    private JLabel otsikko;
    private JLabel ostoskoriOtsikko;
    private JButton lisaaButton;
    private JButton poistaButton;
    private JButton lisaaValikoimaanButton;
    private JButton hakuButton;
    private JButton tyhjennaButton;
    private JLabel hakuLabel;
    private JComboBox<String> kriteeriBox;
    private JComboBox<String> lajitteluBox;
    private JComboBox<String> alennusBox;
    private JLabel alennusLabel;
    private JComboBox<String> kieliBox;
    private JLabel kieliLabel;
    private JComboBox<String> valuuttaBox;
    private JLabel valuuttaLabel;
    private JButton paivitaKurssitButton;

    // Valuutta
    private Map<String, Double> _valuuttaKurssit = new HashMap<>(); // EUR-pohjaiset kurssit
    private String _valittuValuutta = "EUR";

    public OstoskoriGUI() {
        setTitle("Yhteenveto");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(20, 20, 20));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // Lataa tuotteet ja ostoskori tietokannasta
        try {
            for (Tuote t : _tietokanta.haeKaikkiTuotteet()) {
                _tuoteListaMalli.addElement(t);
            }
            for (Tuote t : _tietokanta.haeOstoskori()) {
                _koriListaMalli.addElement(t);
                _ostoskori.lisaaTuote(t);
            }
        } catch (Exception e) {
            System.err.println("Virhe ladattaessa tuotteita/ostoskoria tietokannasta: " + e.getMessage());
            e.printStackTrace();
        }
        paivitaHinta();

        otsikko = new JLabel(kaanna("otsikko"));
        otsikko.setFont(new Font("SansSerif", Font.BOLD, 28));
        otsikko.setForeground(Color.WHITE);
        otsikko.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(otsikko, gbc);

        // Ostoskori (tuotelista ja kori)
        JPanel ostoskoriKortti = luoKortti();
        ostoskoriKortti.setLayout(new BorderLayout());
        ostoskoriOtsikko = new JLabel(kaanna("ostoskori"));
        ostoskoriOtsikko.setFont(new Font("SansSerif", Font.BOLD, 18));
        ostoskoriOtsikko.setForeground(Color.WHITE);
        ostoskoriKortti.add(ostoskoriOtsikko, BorderLayout.NORTH);
        JPanel ostoskoriSisalto = new JPanel(new GridLayout(1, 2, 10, 0));
        ostoskoriSisalto.setOpaque(false);
        JScrollPane tuoteScroll = new JScrollPane(_tuoteLista);
        JScrollPane koriScroll = new JScrollPane(_koriLista);
        Dimension listaKoko = new Dimension(220, 200);
        tuoteScroll.setPreferredSize(listaKoko);
        tuoteScroll.setMinimumSize(listaKoko);
        koriScroll.setPreferredSize(listaKoko);
        koriScroll.setMinimumSize(listaKoko);
        tuoteScroll.setBorder(BorderFactory.createTitledBorder(kaanna("valikoima")));
        koriScroll.setBorder(BorderFactory.createTitledBorder(kaanna("ostoskori")));
        ostoskoriSisalto.add(tuoteScroll);
        ostoskoriSisalto.add(koriScroll);
        ostoskoriKortti.add(ostoskoriSisalto, BorderLayout.CENTER);
        JPanel hintaPanel = new JPanel(new BorderLayout());
        hintaPanel.setOpaque(false);
        _hintaLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        _hintaLabel.setForeground(new Color(128, 255, 0));
        hintaPanel.add(_hintaLabel, BorderLayout.EAST);
        ostoskoriKortti.add(hintaPanel, BorderLayout.SOUTH);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        add(ostoskoriKortti, gbc);

        // Napit valikoiman alle
        JPanel napitPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        napitPanel.setOpaque(false);
        lisaaButton = new JButton(kaanna("lisaa_koriin"));
        poistaButton = new JButton(kaanna("poista_korista"));
        lisaaValikoimaanButton = new JButton(kaanna("lisaa_uusi"));
        lisaaButton.addActionListener(e -> {
            Tuote valittu = _tuoteLista.getSelectedValue();
            if (valittu != null) {
                if (valittu.getSaldo() <= 0) {
                    JOptionPane.showMessageDialog(this, kaanna("ei_saldoa"), kaanna("ei_saldoa_otsikko"), JOptionPane.WARNING_MESSAGE);
                    return;
                }
                _ostoskori.lisaaTuote(valittu);
                _koriListaMalli.addElement(valittu);
                valittu.setSaldo(valittu.getSaldo() - 1);
                _tuoteLista.repaint();
                try {
                    _tietokanta.tallennaTuote(valittu);
                } catch (Exception ex) {
                    System.err.println("Virhe saldon tallennuksessa: " + ex.getMessage());
                    ex.printStackTrace();
                }
                _tietokanta.tallennaOstoskori(_ostoskori.getTuotteet());
                paivitaHinta();
            }
        });
        poistaButton.addActionListener(e -> {
            Tuote valittu = _koriLista.getSelectedValue();
            if (valittu != null) {
                _ostoskori.poistaTuote(valittu);
                _koriListaMalli.removeElement(valittu);
                // Kasvata varastosaldoa ja tallenna
                valittu.setSaldo(valittu.getSaldo() + 1);
                _tuoteLista.repaint();
                try {
                    _tietokanta.tallennaTuote(valittu);
                } catch (Exception ex) {
                    System.err.println("Virhe saldon palautuksessa: " + ex.getMessage());
                    ex.printStackTrace();
                }
                _tietokanta.tallennaOstoskori(_ostoskori.getTuotteet());
                paivitaHinta();
            }
        });
        lisaaValikoimaanButton.addActionListener(e -> avaaUusiTuoteDialogi());
        napitPanel.add(lisaaButton);
        napitPanel.add(poistaButton);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        add(napitPanel, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        add(lisaaValikoimaanButton, gbc);

        // Hakupaneeli
        JPanel hakuPanel = new JPanel(new GridBagLayout());
        hakuPanel.setOpaque(false);
        GridBagConstraints hakuGbc = new GridBagConstraints();
        hakuGbc.insets = new Insets(0, 0, 0, 8);
        hakuGbc.gridy = 0;
        hakuLabel = new JLabel(kaanna("haku"));
        String[] kriteerit = {kaanna("haku_nimi"), kaanna("haku_hinta"), kaanna("haku_kategoria")};
        kriteeriBox = new JComboBox<>(kriteerit);
        JTextField hakuKentta = new JTextField(16);
        hakuButton = new JButton(kaanna("hae"));
        tyhjennaButton = new JButton(kaanna("tyhjenna"));
        String[] lajitteluKriteerit = {kaanna("lajittele_nimi"), kaanna("lajittele_hinta"), kaanna("lajittele_kategoria")};
        lajitteluBox = new JComboBox<>(lajitteluKriteerit);
        hakuPanel.add(hakuLabel, hakuGbc);
        hakuGbc.gridx = 1; hakuPanel.add(hakuKentta, hakuGbc);
        hakuGbc.gridx = 2; hakuPanel.add(kriteeriBox, hakuGbc);
        hakuGbc.gridx = 3; hakuPanel.add(hakuButton, hakuGbc);
        hakuGbc.gridx = 4; hakuPanel.add(tyhjennaButton, hakuGbc);
        hakuGbc.gridx = 5; hakuPanel.add(lajitteluBox, hakuGbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(hakuPanel, gbc);

        // Alennusvalinta
        String[] alennusVaihtoehdot = {kaanna("alennus_ei"), kaanna("alennus_osta3"), kaanna("alennus_10")};
        alennusBox = new JComboBox<>(alennusVaihtoehdot);
        alennusBox.setSelectedIndex(0);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JPanel alennusPanel = new JPanel();
        alennusPanel.setOpaque(false);
        alennusLabel = new JLabel(kaanna("alennus"));
        alennusPanel.add(alennusLabel);
        alennusPanel.add(alennusBox);
        add(alennusPanel, gbc);

        // Kielivalinta
        String[] kielet = {"suomi", "svenska", "english"};
        kieliBox = new JComboBox<>(kielet);
        kieliBox.setSelectedIndex(0);
        kieliLabel = new JLabel(kaanna("kieli"));
        kieliBox.addActionListener(e -> {
            _valittuKieli = (String)kieliBox.getSelectedItem();
            _kieliBundle = lataaKieliBundle(_valittuKieli);
            paivitaKaikkiTekstit();
        });
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel kieliPanel = new JPanel();
        kieliPanel.setOpaque(false);
        kieliPanel.add(kieliLabel);
        kieliPanel.add(kieliBox);
        add(kieliPanel, gbc);

        // Valuutta-valinta ja kurssien päivitys
        String[] valuutat = {"EUR", "USD", "SEK"};
        valuuttaBox = new JComboBox<>(valuutat);
        valuuttaBox.setSelectedIndex(0);
        valuuttaLabel = new JLabel(kaanna("valuutta"));
        paivitaKurssitButton = new JButton(kaanna("paivita_kurssit"));
        valuuttaBox.addActionListener(e -> {
            _valittuValuutta = (String) valuuttaBox.getSelectedItem();
            paivitaHinta();
            _tuoteLista.repaint();
            _koriLista.repaint();
        });
        paivitaKurssitButton.addActionListener(e -> {
            haeValuuttakurssit();
            paivitaHinta();
            _tuoteLista.repaint();
            _koriLista.repaint();
        });
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JPanel valuuttaPanel = new JPanel();
        valuuttaPanel.setOpaque(false);
        valuuttaPanel.add(valuuttaLabel);
        valuuttaPanel.add(valuuttaBox);
        valuuttaPanel.add(paivitaKurssitButton);
        add(valuuttaPanel, gbc);

        // Lajitteluvalinta päivittää tuotelistan järjestyksen
        lajitteluBox.addActionListener(e -> {
            java.util.List<Tuote> tuotteet = new java.util.ArrayList<>();
            for (int i = 0; i < _tuoteListaMalli.size(); i++) {
                tuotteet.add(_tuoteListaMalli.get(i));
            }
            String valinta = (String) lajitteluBox.getSelectedItem();
            if (valinta.equals(kaanna("lajittele_nimi"))) {
                tuotteet.sort(java.util.Comparator.comparing(Tuote::getNimi, String.CASE_INSENSITIVE_ORDER));
            } else if (valinta.equals(kaanna("lajittele_hinta"))) {
                tuotteet.sort(java.util.Comparator.comparingDouble(Tuote::getHinta));
            } else if (valinta.equals(kaanna("lajittele_kategoria"))) {
                tuotteet.sort(java.util.Comparator.comparing(t -> t.getKategoria() == null ? "" : t.getKategoria(), String.CASE_INSENSITIVE_ORDER));
            }
            _tuoteListaMalli.clear();
            for (Tuote t : tuotteet) {
                _tuoteListaMalli.addElement(t);
            }
        });

        hakuButton.addActionListener(e -> {
            String kriteeri = (String) kriteeriBox.getSelectedItem();
            String arvo = hakuKentta.getText().trim();
            _tuoteListaMalli.clear();
            try {
                if (kriteeri.equals(kaanna("haku_nimi"))) {
                    for (Tuote t : _tietokanta.haeTuotteetNimella(arvo)) {
                        _tuoteListaMalli.addElement(t);
                    }
                } else if (kriteeri.equals(kaanna("haku_hinta"))) {
                    String[] osat = arvo.split("-");
                    if (osat.length == 2) {
                        double min = Double.parseDouble(osat[0].replace(",", "."));
                        double max = Double.parseDouble(osat[1].replace(",", "."));
                        for (Tuote t : _tietokanta.haeTuotteetHintaValilla(min, max)) {
                            _tuoteListaMalli.addElement(t);
                        }
                    }
                } else if (kriteeri.equals(kaanna("haku_kategoria"))) {
                    for (Tuote t : _tietokanta.haeTuotteetKategoriassa(arvo)) {
                        _tuoteListaMalli.addElement(t);
                    }
                }
            } catch (Exception ex) {
                System.err.println("Virhe haussa: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        tyhjennaButton.addActionListener(e -> {
            hakuKentta.setText("");
            kriteeriBox.setSelectedIndex(0);
            _tuoteListaMalli.clear();
            try {
                for (Tuote t : _tietokanta.haeKaikkiTuotteet()) {
                    _tuoteListaMalli.addElement(t);
                }
            } catch (Exception ex) {
                System.err.println("Virhe tuotteiden latauksessa: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        alennusBox.addActionListener(e -> paivitaHinta());

        // Tuotelistan renderer, joka näyttää myös saldon
        _tuoteLista.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Tuote) {
                    Tuote t = (Tuote) value;
                    double converted = muunnaHinta(t.getHinta(), _valittuValuutta);
                    label.setText(t.getNimi() + " (" + String.format(Locale.US, "%.2f", converted) + " " + getValuuttaSymboli(_valittuValuutta) + ") | Saldo: " + t.getSaldo());
                    label.setToolTipText("Kategoria: " + t.getKategoria() + ", Saldo: " + t.getSaldo());
                    if (t.getSaldo() == 0) {
                        label.setForeground(Color.GRAY);
                    }
                }
                return label;
            }
        });
        _koriLista.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Tuote) {
                    double converted = muunnaHinta(((Tuote) value).getHinta(), _valittuValuutta);
                    setText(((Tuote) value).getNimi() + " (" + String.format(Locale.US, "%.2f", converted) + " " + getValuuttaSymboli(_valittuValuutta) + ")");
                    setToolTipText(((Tuote) value).toString());
                } else {
                    setToolTipText(null);
                }
                setBackground(new Color(40, 40, 40));
                setForeground(Color.WHITE);
                return c;
            }
        });
        _tuoteLista.setSelectionBackground(new Color(60, 60, 60));
        _koriLista.setSelectionBackground(new Color(60, 60, 60));

        // Tuplaklikkaus avaa muokkausdialogin
        _tuoteLista.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    Tuote valittu = _tuoteLista.getSelectedValue();
                    if (valittu != null) {
                        avaaTuotteenMuokkausDialogi(valittu);
                        _tuoteLista.repaint();
                        _koriLista.repaint();
                    }
                }
            }
        });

        // Alusta oletuskurssit ennen käyttöä
        alustaOletusKurssit();

        // Hae kurssit käynnistyksessä
        SwingUtilities.invokeLater(this::haeValuuttakurssit);
    }

    // Lisää puuttuva kielibundle-latausmetodi
    private ResourceBundle lataaKieliBundle(String kieli) {
        try {
            return new PropertyResourceBundle(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("ostoskori/kielet.properties"), "UTF-8"));
        } catch (Exception e) {
            System.err.println("Käännöstiedoston lataus epäonnistui: " + e.getMessage());
            return null;
        }
    }

    // Käännösmetodi
    private String kaanna(String avain) {
        try {
            return _kieliBundle.getString(_valittuKieli + "." + avain);
        } catch (MissingResourceException e) {
            return avain;
        }
    }

    // Pyöristetty "kortti"-paneeli
    private JPanel luoKortti() {
        JPanel panel = new RoundedPanel(30, new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return panel;
    }

    // Pyöristetty paneeli
    private static class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color bgColor;
        public RoundedPanel(int radius, Color bgColor) {
            super();
            this.cornerRadius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            super.paintComponent(g);
            g2.dispose();
        }
    }

    private void paivitaHinta() {
        double summa = _ostoskori.getKokonaishinta();
        String alennus = (alennusBox != null && alennusBox.getSelectedItem() != null) ? alennusBox.getSelectedItem().toString() : kaanna("alennus_ei");
        if (alennus.equals(kaanna("alennus_osta3"))) {
            java.util.List<Tuote> tuotteet = _ostoskori.getTuotteet();
            tuotteet.sort(java.util.Comparator.comparingDouble(Tuote::getHinta).reversed());
            double total = 0;
            for (int i = 0; i < tuotteet.size(); i++) {
                if ((i+1) % 3 == 0) continue; // joka kolmas ilmaiseksi
                total += tuotteet.get(i).getHinta();
            }
            summa = total;
        } else if (alennus.equals(kaanna("alennus_10"))) {
            if (summa > 50) summa = summa * 0.9;
        }
        String hintaPrefix = kaanna("kokonaishinta");
        _hintaLabel.setText(hintaPrefix + formatoiValuutaksi(summa, _valittuValuutta));
    }

    // Valuuttakurssien haku
    private void haeValuuttakurssit() {
        // Yksinkertainen HTTP pyyntö exchangerate.host APIin
        HttpURLConnection yhteys = null;
        try {
            URL url = URI.create("https://api.exchangerate.host/latest?base=EUR&symbols=USD,SEK").toURL();
            yhteys = (HttpURLConnection) url.openConnection();
            yhteys.setConnectTimeout(4000);
            yhteys.setReadTimeout(4000);
            yhteys.setRequestMethod("GET");
            int status = yhteys.getResponseCode();
            if (status == 200) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(yhteys.getInputStream(), StandardCharsets.UTF_8))) {
                    String r; while ((r = br.readLine()) != null) sb.append(r);
                }
                String json = sb.toString();
                // Erittäin kevyt "parser" ilman riippuvuuksia
                Double usd = haeArvo(json, "USD");
                Double sek = haeArvo(json, "SEK");
                if (usd != null) _valuuttaKurssit.put("USD", usd);
                if (sek != null) _valuuttaKurssit.put("SEK", sek);
                JOptionPane.showMessageDialog(this, kaanna("kurssit_paivitetty"));
            } else {
                JOptionPane.showMessageDialog(this, kaanna("kurssit_virhe"));
            }
        } catch (Exception ex) {
            System.err.println("Virhe valuuttakurssien haussa: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, kaanna("kurssit_virhe"));
        } finally {
            if (yhteys != null) yhteys.disconnect();
        }
        paivitaHinta();
    }

    private Double haeArvo(String json, String tunnus) {
        try {
            int idx = json.indexOf("\"" + tunnus + "\"");
            if (idx == -1) return null;
            int kaksoispiste = json.indexOf(':', idx);
            if (kaksoispiste == -1) return null;
            int loppu = kaksoispiste + 1;
            while (loppu < json.length() && (Character.isWhitespace(json.charAt(loppu)) || json.charAt(loppu) == '"')) loppu++;
            int end = loppu;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end)=='.')) end++;
            String numero = json.substring(loppu, end);
            return Double.parseDouble(numero);
        } catch (Exception e) { return null; }
    }

    private void alustaOletusKurssit() {
        _valuuttaKurssit.put("EUR", 1.0);
        _valuuttaKurssit.put("USD", 1.08); // fallback-arvot
        _valuuttaKurssit.put("SEK", 11.5);
    }

    private double muunnaHinta(double euroMaara, String valuutta) {
        Double kurssi = _valuuttaKurssit.getOrDefault(valuutta, 1.0);
        return euroMaara * kurssi;
    }

    private String getValuuttaSymboli(String valuutta) {
        switch (valuutta) {
            case "USD": return "$";
            case "SEK": return "kr";
            default: return "€";
        }
    }

    private String formatoiValuutaksi(double euroMaara, String valuutta) {
        double converted = muunnaHinta(euroMaara, valuutta);
        return String.format(Locale.US, "%.2f %s", converted, getValuuttaSymboli(valuutta));
    }

    private void avaaUusiTuoteDialogi() {
        JTextField nimiKentta = new JTextField();
        JTextField hintaKentta = new JTextField();
        JTextField kategoriaKentta = new JTextField();
        JTextField saldoKentta = new JTextField("0");
        Object[] viestit = {
            kaanna("nimi"), nimiKentta,
            kaanna("hinta"), hintaKentta,
            kaanna("kategoria"), kategoriaKentta,
            kaanna("saldo"), saldoKentta
        };
        int valinta = JOptionPane.showConfirmDialog(this, viestit, kaanna("lisaa_otsikko"), JOptionPane.OK_CANCEL_OPTION);
        if (valinta == JOptionPane.OK_OPTION) {
            String nimi = nimiKentta.getText().trim();
            String hintaStr = hintaKentta.getText().replace(",", ".").trim();
            String kategoria = kategoriaKentta.getText().trim();
            String saldoStr = saldoKentta.getText().trim();
            if (!nimi.isEmpty() && !hintaStr.isEmpty() && !saldoStr.isEmpty()) {
                try {
                    double hinta = Double.parseDouble(hintaStr);
                    int saldo = Integer.parseInt(saldoStr);
                    Tuote uusi = new Tuote(nimi, hinta, saldo);
                    uusi.setKategoria(kategoria);
                    _tuoteListaMalli.addElement(uusi);
                    _tietokanta.tallennaTuote(uusi);
                } catch (NumberFormatException e) {
                    System.err.println("Virheellinen hinta tai saldo syötetty: " + hintaStr + ", " + saldoStr);
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, kaanna("virheellinen_hinta_tai_saldo"), kaanna("virhe_otsikko"), JOptionPane.ERROR_MESSAGE);
                } catch (Exception e) {
                    System.err.println("Virhe uuden tuotteen tallennuksessa: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void avaaTuotteenMuokkausDialogi(Tuote tuote) {
        JTextField nimiKentta = new JTextField(tuote.getNimi());
        JTextField hintaKentta = new JTextField(String.valueOf(tuote.getHinta()));
        JTextField idKentta = new JTextField(String.valueOf(tuote.getId()));
        JTextField kategoriaKentta = new JTextField(tuote.getKategoria() != null ? tuote.getKategoria() : "");
        JTextField painoKentta = new JTextField(String.valueOf(tuote.getPaino()));
        JTextField kokoKentta = new JTextField(tuote.getKoko() != null ? tuote.getKoko() : "");
        JTextField paivaysKentta = new JTextField(tuote.getPaivays() != null ? tuote.getPaivays() : "");
        JTextField kuvausKentta = new JTextField(tuote.getKuvaus() != null ? tuote.getKuvaus() : "");
        JTextField valmistajaKentta = new JTextField(tuote.getValmistaja() != null ? tuote.getValmistaja() : "");
        JTextField merkkiKentta = new JTextField(tuote.getMerkki() != null ? tuote.getMerkki() : "");
        JTextField malliKentta = new JTextField(tuote.getMalli() != null ? tuote.getMalli() : "");
        JTextField variKentta = new JTextField(tuote.getVari() != null ? tuote.getVari() : "");
        JTextField materiaaliKentta = new JTextField(tuote.getMateriaali() != null ? tuote.getMateriaali() : "");
        JTextField kokoYksikkoKentta = new JTextField(tuote.getKokoYksikko() != null ? tuote.getKokoYksikko() : "");
        JTextField painoYksikkoKentta = new JTextField(tuote.getPainoYksikko() != null ? tuote.getPainoYksikko() : "");
        JTextField alkuperamaaKentta = new JTextField(tuote.getAlkuperamaa() != null ? tuote.getAlkuperamaa() : "");
        JTextField valmistusmaaKentta = new JTextField(tuote.getValmistusmaa() != null ? tuote.getValmistusmaa() : "");
        JTextField eanKentta = new JTextField(tuote.getEan() != null ? tuote.getEan() : "");
        JTextField saldoKentta = new JTextField(String.valueOf(tuote.getSaldo()));

        JPanel panel = new JPanel(new GridLayout(0,2));
        panel.add(new JLabel(kaanna("nimi"))); panel.add(nimiKentta);
        panel.add(new JLabel(kaanna("hinta"))); panel.add(hintaKentta);
        panel.add(new JLabel("ID:")); panel.add(idKentta);
        panel.add(new JLabel(kaanna("kategoria"))); panel.add(kategoriaKentta);
        panel.add(new JLabel("Paino:")); panel.add(painoKentta);
        panel.add(new JLabel("Koko:")); panel.add(kokoKentta);
        panel.add(new JLabel("Päiväys:")); panel.add(paivaysKentta);
        panel.add(new JLabel("Kuvaus:")); panel.add(kuvausKentta);
        panel.add(new JLabel("Valmistaja:")); panel.add(valmistajaKentta);
        panel.add(new JLabel("Merkki:")); panel.add(merkkiKentta);
        panel.add(new JLabel("Malli:")); panel.add(malliKentta);
        panel.add(new JLabel("Väri:")); panel.add(variKentta);
        panel.add(new JLabel("Materiaali:")); panel.add(materiaaliKentta);
        panel.add(new JLabel("Kokoyksikkö:")); panel.add(kokoYksikkoKentta);
        panel.add(new JLabel("Painoyksikkö:")); panel.add(painoYksikkoKentta);
        panel.add(new JLabel("Alkuperämaa:")); panel.add(alkuperamaaKentta);
        panel.add(new JLabel("Valmistusmaa:")); panel.add(valmistusmaaKentta);
        panel.add(new JLabel("EAN:")); panel.add(eanKentta);
        panel.add(new JLabel(kaanna("saldo"))); panel.add(saldoKentta);

        int tulos = JOptionPane.showConfirmDialog(this, panel, kaanna("muokkaa_otsikko"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (tulos == JOptionPane.OK_OPTION) {
            tuote.setNimi(nimiKentta.getText());
            try { tuote.setHinta(Double.parseDouble(hintaKentta.getText().replace(",", "."))); } catch (Exception e) { System.err.println("Virheellinen hinta muokkauksessa: " + hintaKentta.getText()); e.printStackTrace(); }
            try { tuote.setId(Integer.parseInt(idKentta.getText())); } catch (Exception e) { System.err.println("Virheellinen ID muokkauksessa: " + idKentta.getText()); e.printStackTrace(); }
            tuote.setKategoria(kategoriaKentta.getText());
            try { tuote.setPaino(Double.parseDouble(painoKentta.getText().replace(",", "."))); } catch (Exception e) { System.err.println("Virheellinen paino muokkauksessa: " + painoKentta.getText()); e.printStackTrace(); }
            tuote.setKoko(kokoKentta.getText());
            tuote.setPaivays(paivaysKentta.getText());
            tuote.setKuvaus(kuvausKentta.getText());
            tuote.setValmistaja(valmistajaKentta.getText());
            tuote.setMerkki(merkkiKentta.getText());
            tuote.setMalli(malliKentta.getText());
            tuote.setVari(variKentta.getText());
            tuote.setMateriaali(materiaaliKentta.getText());
            tuote.setKokoYksikko(kokoYksikkoKentta.getText());
            tuote.setPainoYksikko(painoYksikkoKentta.getText());
            tuote.setAlkuperamaa(alkuperamaaKentta.getText());
            tuote.setValmistusmaa(valmistusmaaKentta.getText());
            tuote.setEan(eanKentta.getText());
            try { tuote.setSaldo(Integer.parseInt(saldoKentta.getText())); } catch (Exception e) { System.err.println("Virheellinen saldo muokkauksessa: " + saldoKentta.getText()); e.printStackTrace(); }
            try {
                _tietokanta.tallennaTuote(tuote);
            } catch (Exception e) {
                System.err.println("Virhe tuotteen tallennuksessa muokkauksen jälkeen: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Päivitä kaikki näkyvät tekstit kielen mukaan
    private void paivitaKaikkiTekstit() {
        setTitle(kaanna("otsikko"));
        if (lisaaButton != null) lisaaButton.setText(kaanna("lisaa_koriin"));
        if (poistaButton != null) poistaButton.setText(kaanna("poista_korista"));
        if (lisaaValikoimaanButton != null) {
            lisaaValikoimaanButton.setText(kaanna("lisaa_uusi"));
            lisaaValikoimaanButton.repaint();
        }
        if (hakuButton != null) hakuButton.setText(kaanna("hae"));
        if (tyhjennaButton != null) tyhjennaButton.setText(kaanna("tyhjenna"));
        if (hakuLabel != null) hakuLabel.setText(kaanna("haku"));
        if (kriteeriBox != null) {
            kriteeriBox.removeAllItems();
            kriteeriBox.addItem(kaanna("haku_nimi"));
            kriteeriBox.addItem(kaanna("haku_hinta"));
            kriteeriBox.addItem(kaanna("haku_kategoria"));
        }
        if (lajitteluBox != null) {
            lajitteluBox.removeAllItems();
            lajitteluBox.addItem(kaanna("lajittele_nimi"));
            lajitteluBox.addItem(kaanna("lajittele_hinta"));
            lajitteluBox.addItem(kaanna("lajittele_kategoria"));
        }
        if (alennusBox != null) {
            Object selected = alennusBox.getSelectedItem();
            alennusBox.removeAllItems();
            alennusBox.addItem(kaanna("alennus_ei"));
            alennusBox.addItem(kaanna("alennus_osta3"));
            alennusBox.addItem(kaanna("alennus_10"));
            // Palauta valinta jos mahdollista (vertaa avaimen perusteella, ei tekstillä)
            if (selected != null) {
                for (int i = 0; i < alennusBox.getItemCount(); i++) {
                    if (alennusBox.getItemAt(i).equals(selected) ||
                        alennusBox.getItemAt(i).toString().equals(kaanna("alennus_ei")) && selected.toString().equals(kaanna("alennus_ei")) ||
                        alennusBox.getItemAt(i).toString().equals(kaanna("alennus_osta3")) && selected.toString().equals(kaanna("alennus_osta3")) ||
                        alennusBox.getItemAt(i).toString().equals(kaanna("alennus_10")) && selected.toString().equals(kaanna("alennus_10"))) {
                        alennusBox.setSelectedIndex(i);
                        break;
                    }
                }
            } else {
                alennusBox.setSelectedIndex(0);
            }
        }
        if (alennusLabel != null) alennusLabel.setText(kaanna("alennus"));
        if (kieliLabel != null) kieliLabel.setText(kaanna("kieli"));
        if (otsikko != null) otsikko.setText(kaanna("otsikko"));
        if (ostoskoriOtsikko != null) ostoskoriOtsikko.setText(kaanna("ostoskori"));
        if (valuuttaLabel != null) valuuttaLabel.setText(kaanna("valuutta"));
        if (paivitaKurssitButton != null) paivitaKurssitButton.setText(kaanna("paivita_kurssit"));
        paivitaHinta();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OstoskoriGUI gui = new OstoskoriGUI();
            gui.setVisible(true);
        });
    }
}