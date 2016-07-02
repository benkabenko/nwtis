/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alebenkov.web.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.foi.nwtis.alebenkov.konfiguracije.Konfiguracija;
import org.foi.nwtis.alebenkov.konfiguracije.bp.BP_konfiguracija;
import org.foi.nwtis.alebenkov.rest.klijenti.GMKlijent;
import org.foi.nwtis.alebenkov.web.podaci.Adresa;
import org.foi.nwtis.alebenkov.web.podaci.Dnevnik;
import org.foi.nwtis.alebenkov.web.podaci.Lokacija;
import org.foi.nwtis.alebenkov.web.podaci.MeteoPodaci;
import org.foi.nwtis.alebenkov.web.podaci.User;
import org.foi.nwtis.alebenkov.web.slusaci.SlusacAplikacije;

/**
 *
 * @author abenkovic
 */
public class DBOps {

    private BP_konfiguracija bpConfig = null;
    private Konfiguracija konfig = null;
    private String url = null;
    private String korisnik = null;
    private String lozinka = null;
    private Connection connection = null;
    private Statement statemant = null;
    private ResultSet rs = null;
    private String sql = null;
    private boolean sqlExe;
    private int sqlUp;

    /**
     *
     */
    public DBOps() {
        this.bpConfig = SlusacAplikacije.getBpConfig();
        this.konfig = SlusacAplikacije.getServerConfig();
        this.url = bpConfig.getServerDatabase() + bpConfig.getUserDatabase() + "?useUnicode=true&characterEncoding=utf-8";
        this.korisnik = bpConfig.getUserUsername();
        this.lozinka = bpConfig.getUserPassword();

        try {
            Class.forName(bpConfig.getDriverDatabase()); //dovoljno pozvati jednom na razini projekta da bi se ucitao sam driver
        } catch (ClassNotFoundException ex) {
            System.out.println("ERROR | Greska kod ucitavanja drivera: " + ex.getMessage());
        }
    }

    /**
     *
     * @param user
     * @param pass
     * @return
     */
    public int[] provjeraKorisnika(String user, String pass) {
        int[] korisnik = {0, 0};

        try {
            connection = DriverManager.getConnection(url, this.korisnik, this.lozinka);
            statemant = connection.createStatement();

            sql = "SELECT * FROM alebenkov_korisnici where user = '" + user + "' AND pass = '" + pass + "'";
            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                korisnik[0] = rs.getInt("role");
                korisnik[1] = rs.getInt("rang");
            }

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }

        return korisnik;
    }

    /**
     *
     * @param newUser
     * @param newPass
     * @param newRole
     * @return
     */
    public boolean dodajKorisnika(String newUser, String newPass, String newRole) {
        int role = 2;
        if (newRole.contains("ADMIN")) {
            role = 1;
        }
        int brojRedaka = 0;

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();

            sql = "SELECT * FROM alebenkov_korisnici where user = '" + newUser + "'";
            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                brojRedaka++; //ovo se moze rjesiti na elegantniji nacin ali ovo mi je trenutno najbrzi (getRow, beforeFirst,..) ali je potrebno podici odredjene zastavice na resultsetu
            }
            if (brojRedaka == 0) {

                sql = "INSERT INTO alebenkov_korisnici(user, pass, role, rang) VALUES ('" + newUser + "','" + newPass + "','" + role + "', 1 )";
                sqlExe = statemant.execute(sql);
                System.out.println("SERVER | Korisnik dodan u bazu.");
                return true;

            }

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }

        return false;
    }

    /**
     *
     * @param user
     * @return
     */
    public String povecajRang(String user) {
        String status = "";
        int brojRedaka = 0;
        int trenutniRang = 0;

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();

            sql = "SELECT * FROM alebenkov_korisnici where user = '" + user + "'";
            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                brojRedaka++; //ovo se moze rjesiti na elegantniji nacin ali ovo mi je trenutno najbrzi (getRow, beforeFirst,..) ali je potrebno podici odredjene zastavice na resultsetu
                trenutniRang = rs.getInt("rang");
            }
            if (trenutniRang == 0) {
                status = "ERR 35";
            } else if (trenutniRang > 4) {
                status = "ERR 34";
            } else {
                int noviRang = trenutniRang + 1;
                sql = "UPDATE alebenkov_korisnici SET rang='" + noviRang + "' WHERE user = '" + user + "'";
                sqlUp = statemant.executeUpdate(sql);
                System.out.println("SERVER | Rang korisnika povecan.");
                status = "OK 10";
            }

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return status;
    }

    /**
     *
     * @param user
     * @return
     */
    public String smanjiRang(String user) {
        String status = "";
        int brojRedaka = 0;
        int trenutniRang = 0;

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();

            sql = "SELECT * FROM alebenkov_korisnici where user = '" + user + "'";
            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                brojRedaka++; //ovo se moze rjesiti na elegantniji nacin ali ovo mi je trenutno najbrzi (getRow, beforeFirst,..) ali je potrebno podici odredjene zastavice na resultsetu
                trenutniRang = rs.getInt("rang");
            }
            if (trenutniRang == 0) {
                status = "ERR 35";
            } else if (trenutniRang < 2) {
                status = "ERR 34";
            } else {
                int noviRang = trenutniRang - 1;
                sql = "UPDATE alebenkov_korisnici SET rang='" + noviRang + "' WHERE user = '" + user + "'";
                sqlUp = statemant.executeUpdate(sql);
                System.out.println("SERVER | Rang korisnika smanjen.");
                status = "OK 10";
            }

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }

        return status;
    }

    /**
     *
     * @return
     */
    public int[] statistikaKorisnika() {

        int[] statistika = {0, 0, 0};

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();

            sql = "SELECT * FROM alebenkov_korisnici";
            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                statistika[0]++;
                if (rs.getInt("role") == 1) {
                    statistika[1]++;
                } else {
                    statistika[2]++;
                }
            }

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }

        return statistika;

    }

    /**
     *
     * @param user
     * @param naredba
     * @param odgovor
     * @return
     */
    public boolean dnevnik(String user, String naredba, String odgovor) {

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();

            sql = "INSERT INTO alebenkov_dnevnik(user, naredba, odgovor) VALUES ('" + user + "','" + naredba + "','" + odgovor + "')";
            sqlExe = statemant.execute(sql);
            System.out.println("SERVER | Zapis dodan u dnevnik.");
            return true;

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }

        return false;
    }

    /**
     *
     * @param user
     * @return
     */
    public boolean provjeraKvote(String user) {
        int rang = 0;
        int kvota = 0;
        int brojUpita = 0;

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();

            //uzimam rang zadanog korisnika
            sql = "SELECT * FROM alebenkov_korisnici where user = '" + user + "'";
            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                rang = rs.getInt("rang");
                break;
            }

            //uzimam kvotu za dobiveni rang
            kvota = Integer.parseInt(konfig.dajPostavku("kvota" + rang));

            //uzimam trenutno vrijeme i smanjujem ga za zadani interval korisnika
            int intervalKorisnika = Integer.parseInt(konfig.dajPostavku("intervalKorisnika")) * 1000;
            long trenutnoVrijeme = System.currentTimeMillis();
            long granicaVremena = trenutnoVrijeme - intervalKorisnika;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:mm:ss.S");
            String granica = sdf.format(granicaVremena);

            //nakon sto imam krajnju vremensku granicu radim upit nad dnevnikom da vidim koliko je korisnik do sada napravio upita u tom razdoblju
            sql = "SELECT * FROM alebenkov_dnevnik where user = '" + user + "' AND time > '" + granica + "'";
            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                brojUpita++;
            }

            System.out.println("BROJ UPITA: " + brojUpita + " KVOTA: " + kvota + " GRANICA: " + granica);

            //ukoliko nije nadmasio kvotu saljem true
            if (brojUpita < kvota) {
                return true;
            }

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }

        return false;
    }

    /**
     *
     * @param adresa
     * @param kreirao
     * @return
     */
    public boolean dodajAdresu(String adresa, String kreirao) {

        int brojRedaka = 0;

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();

            sql = "SELECT * FROM alebenkov_adrese where adresa = '" + adresa + "'";
            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                brojRedaka++; //ovo se moze rjesiti na elegantniji nacin ali ovo mi je trenutno najbrzi (getRow, beforeFirst,..) ali je potrebno podici odredjene zastavice na resultsetu
            }

            if (brojRedaka == 0) {

                GMKlijent gmk = new GMKlijent();
                Lokacija lokacija;
                lokacija = gmk.getGeoLocation(adresa);

                sql = "INSERT INTO alebenkov_adrese(adresa, latitude, longitude, kreirao) VALUES ('" + adresa + "','" + lokacija.getLatitude() + "','" + lokacija.getLongitude() + "','" + kreirao + "')";
                sqlExe = statemant.execute(sql);
                System.out.println("|| Adresa spremljena u bazu.");
                return true;

            }

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return false;
    }

    /**
     *
     * @param adresa
     * @return
     */
    public boolean testirajAdresu(String adresa) {

        int brojRedaka = 0;

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();

            sql = "SELECT * FROM alebenkov_adrese where adresa = '" + adresa + "'";
            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                brojRedaka++; //ovo se moze rjesiti na elegantniji nacin ali ovo mi je trenutno najbrzi (getRow, beforeFirst,..) ali je potrebno podici odredjene zastavice na resultsetu
            }

            if (brojRedaka > 0) {
                return true;
            }

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public List<Adresa> ucitajAdrese() {
        List<Adresa> adrese = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();

            sql = "SELECT * FROM alebenkov_adrese";
            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                Lokacija l = new Lokacija(rs.getString("latitude"), rs.getString("longitude"));
                Adresa a = new Adresa(rs.getInt("id"), rs.getString("adresa"), l);
                adrese.add(a);
            }

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return adrese;
    }

    /**
     *
     * @param user
     * @return
     */
    public List<Adresa> adreseKorisnika(String user) {
        List<Adresa> adrese = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();

            sql = "SELECT * FROM alebenkov_adrese where kreirao = '" + user + "'";
            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                Lokacija l = new Lokacija(rs.getString("latitude"), rs.getString("longitude"));
                Adresa a = new Adresa(rs.getInt("id"), rs.getString("adresa"), l);
                adrese.add(a);
            }

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return adrese;
    }

    /**
     *
     * @param a
     * @param mp
     * @return
     */
    public boolean spremiMeteo(Adresa a, MeteoPodaci mp) {
        String adresaStanice = mp.getCountry() + ", " + mp.getName();
        int idAdresa = a.getIdadresa();
        java.util.Date utilDate = new java.util.Date();
        java.sql.Timestamp ts = new java.sql.Timestamp(utilDate.getTime());
        sql = "INSERT INTO alebenkov_meteo(idadresa, adresastanice, latitude, longitude, vrijeme, vrijemeopis, temp, tempmin, tempmax, vlaga, tlak, vjetar, vjetarsmjer, preuzeto) "
                + "VALUES ("
                + idAdresa + ",'"
                + adresaStanice + "','"
                + a.getGeoloc().getLatitude() + "','"
                + a.getGeoloc().getLongitude() + "','"
                + mp.getWeatherMain() + "','"
                + mp.getWeatherValue() + "',"
                + mp.getTemperatureValue() + ","
                + mp.getTemperatureMin() + ","
                + mp.getTemperatureMax() + ","
                + mp.getHumidityValue() + ","
                + mp.getPressureValue() + ","
                + mp.getWindSpeedValue() + ","
                + mp.getWindDirectionValue() + ",'"
                + ts + "')";
        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();
            sqlExe = statemant.execute(sql);
            return true;
        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return false;
    }

    /**
     *
     * @param a
     * @param mp
     * @param adr
     * @return
     */
    public boolean spremiMeteoPrognozu(Adresa a, MeteoPodaci mp, String adr) {
        String adresaStanice = adr;
        int idAdresa = a.getIdadresa();
        java.util.Date utilDate = new java.util.Date();
        java.sql.Timestamp ts = new java.sql.Timestamp(utilDate.getTime());
        sql = "INSERT INTO alebenkov_meteoPrognoza(idadresa, adresastanice, latitude, longitude, vrijeme, vrijemeopis, temp, tempmin, tempmax, vlaga, tlak, vjetar, vjetarsmjer,prognozaZa, preuzeto) "
                + "VALUES ("
                + idAdresa + ",'"
                + adresaStanice + "','"
                + a.getGeoloc().getLatitude() + "','"
                + a.getGeoloc().getLongitude() + "','"
                + mp.getWeatherMain() + "','"
                + mp.getWeatherValue() + "',"
                + mp.getTemperatureValue() + ","
                + mp.getTemperatureMin() + ","
                + mp.getTemperatureMax() + ","
                + mp.getHumidityValue() + ","
                + mp.getPressureValue() + ","
                + mp.getWindSpeedValue() + ","
                + mp.getWindDirectionValue() + ",'"
                + mp.getName() + "','"
                + ts + "')";
        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();
            sqlExe = statemant.execute(sql);
            return true;
        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return false;
    }

    /**
     *
     * @param adresa
     * @return
     */
    public MeteoPodaci zadnjiMeteoPodaci(String adresa) {
        MeteoPodaci mp = new MeteoPodaci();
        sql = "select * from alebenkov_meteo as m, alebenkov_adrese as a where m.IDADRESA = a.id and a.ADRESA = '" + adresa + "' ORDER BY idMeteo DESC LIMIT 1";

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();

            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                mp.setWeatherMain(rs.getString("vrijeme"));
                mp.setWeatherValue(rs.getString("vrijemeOpis"));
                mp.setTemperatureValue(rs.getFloat("temp"));
                mp.setTemperatureMin(rs.getFloat("tempMin"));
                mp.setTemperatureMax(rs.getFloat("tempMax"));
                mp.setHumidityValue(rs.getFloat("vlaga"));
                mp.setPressureValue(rs.getFloat("tlak"));
                mp.setWindSpeedValue(rs.getFloat("vjetar"));
                mp.setWindDirectionValue(rs.getFloat("vjetarSmjer"));
                mp.setLastUpdate(rs.getTimestamp("preuzeto"));
            }

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return mp;
    }

    /**
     *
     * @param n
     * @return
     */
    public List<String> rangLista(int n) {
        List<String> adrese = new ArrayList<>();
        sql = "select *, COUNT(*) as ukupno from alebenkov_meteo as m, alebenkov_adrese as a where m.IDADRESA = a.id GROUP by a.adresa ORDER BY ukupno DESC LIMIT " + n + "";

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();

            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                adrese.add(rs.getInt("ukupno") + " - " + rs.getString("adresa"));
            }

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return adrese;
    }

    /**
     *
     * @param n
     * @param adresa
     * @return
     */
    public List<MeteoPodaci> zadnjihN(int n, String adresa) {
        List<MeteoPodaci> mpl = new ArrayList<>();
        sql = "select * from alebenkov_meteo as m, alebenkov_adrese as a where m.IDADRESA = a.id and a.ADRESA = '" + adresa + "' ORDER BY m.preuzeto DESC LIMIT " + n + "";

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();

            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                MeteoPodaci mp = new MeteoPodaci();
                mp.setWeatherMain(rs.getString("vrijeme"));
                mp.setWeatherValue(rs.getString("vrijemeOpis"));
                mp.setTemperatureValue(rs.getFloat("temp"));
                mp.setTemperatureMin(rs.getFloat("tempMin"));
                mp.setTemperatureMax(rs.getFloat("tempMax"));
                mp.setHumidityValue(rs.getFloat("vlaga"));
                mp.setPressureValue(rs.getFloat("tlak"));
                mp.setWindSpeedValue(rs.getFloat("vjetar"));
                mp.setWindDirectionValue(rs.getFloat("vjetarSmjer"));
                mp.setLastUpdate(rs.getTimestamp("preuzeto"));
                mpl.add(mp);
            }

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return mpl;
    }

    /**
     *
     * @param adresa
     * @param odD
     * @param doD
     * @return
     */
    public List<MeteoPodaci> razdobljeMeteo(String adresa, String odD, String doD) {
        List<MeteoPodaci> mpl = new ArrayList<>();
        System.out.println("OD: " + odD);
        sql = "select * from alebenkov_meteo as m, alebenkov_adrese as a where m.IDADRESA = a.id and a.ADRESA = '" + adresa + "' AND m.preuzeto>'" + odD + "' AND m.preuzeto<'" + doD + "'";

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();

            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                MeteoPodaci mp = new MeteoPodaci();
                mp.setWeatherMain(rs.getString("vrijeme"));
                mp.setWeatherValue(rs.getString("vrijemeOpis"));
                mp.setTemperatureValue(rs.getFloat("temp"));
                mp.setTemperatureMin(rs.getFloat("tempMin"));
                mp.setTemperatureMax(rs.getFloat("tempMax"));
                mp.setHumidityValue(rs.getFloat("vlaga"));
                mp.setPressureValue(rs.getFloat("tlak"));
                mp.setWindSpeedValue(rs.getFloat("vjetar"));
                mp.setWindDirectionValue(rs.getFloat("vjetarSmjer"));
                mp.setLastUpdate(rs.getTimestamp("preuzeto"));
                mpl.add(mp);
            }

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return mpl;
    }

    /**
     *
     * @param id
     * @return
     */
    public List<MeteoPodaci> zadnjaMeteoPrognoza(String id) {
        List<MeteoPodaci> mpl = new ArrayList<>();
        sql = "SELECT * FROM alebenkov_meteoPrognoza WHERE idAdresa=" + id + " ORDER BY idPrognoza DESC LIMIT 37";

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();

            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                MeteoPodaci mp = new MeteoPodaci();
                mp.setWeatherMain(rs.getString("vrijeme"));
                mp.setWeatherValue(rs.getString("vrijemeOpis"));
                mp.setTemperatureValue(rs.getFloat("temp"));
                mp.setTemperatureMin(rs.getFloat("tempMin"));
                mp.setTemperatureMax(rs.getFloat("tempMax"));
                mp.setHumidityValue(rs.getFloat("vlaga"));
                mp.setPressureValue(rs.getFloat("tlak"));
                mp.setWindSpeedValue(rs.getFloat("vjetar"));
                mp.setWindDirectionValue(rs.getFloat("vjetarSmjer"));
                mp.setLastUpdate(rs.getTimestamp("preuzeto"));
                mp.setName(rs.getString("prognozaZa"));
                mp.setCountry(rs.getString("adresaStanice"));
                mpl.add(mp);
            }

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return mpl;
    }

    /**
     *
     * @param id
     * @param datum
     * @return
     */
    public List<MeteoPodaci> prognozaZaDan(String id, String datum) {
        List<MeteoPodaci> mpl = new ArrayList<>();
        sql = "SELECT * FROM alebenkov_meteoPrognoza WHERE idAdresa=" + id + " AND prognozaZa = '" + datum + "'";

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();

            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                MeteoPodaci mp = new MeteoPodaci();
                mp.setWeatherMain(rs.getString("vrijeme"));
                mp.setWeatherValue(rs.getString("vrijemeOpis"));
                mp.setTemperatureValue(rs.getFloat("temp"));
                mp.setTemperatureMin(rs.getFloat("tempMin"));
                mp.setTemperatureMax(rs.getFloat("tempMax"));
                mp.setHumidityValue(rs.getFloat("vlaga"));
                mp.setPressureValue(rs.getFloat("tlak"));
                mp.setWindSpeedValue(rs.getFloat("vjetar"));
                mp.setWindDirectionValue(rs.getFloat("vjetarSmjer"));
                mp.setLastUpdate(rs.getTimestamp("preuzeto"));
                mp.setName(rs.getString("prognozaZa"));
                mp.setCountry(rs.getString("adresaStanice"));
                mpl.add(mp);
            }

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return mpl;
    }

    /**
     *
     * @return
     */
    public List<User> ucitajKorisnike() {
        List<User> korisnici = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();

            sql = "SELECT * FROM alebenkov_korisnici";
            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                User u = new User(rs.getString("user"), rs.getString("pass"), rs.getInt("role"), rs.getInt("rang"));
                korisnici.add(u);
            }

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return korisnici;
    }

    /**
     *
     * @param i 1-za dnevnik rada servisa, 2-za pregled zahjtjeva socker servera
     * @return
     */
    public List<Dnevnik> ucitajDnevnik(int i) {
        List<Dnevnik> dnevnik = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();

            sql = "SELECT * FROM alebenkov_dnevnik";
            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                Date date = rs.getTimestamp("time");
                if (i == 1 && !rs.getString("naredba").contains("PASSWD")) {
                    Dnevnik d = new Dnevnik(rs.getInt("id"), rs.getString("user"), rs.getString("naredba"), rs.getString("odgovor"), date);
                    dnevnik.add(d);

                } else if (i == 2 && rs.getString("naredba").contains("PASSWD")) {
                    Dnevnik d = new Dnevnik(rs.getInt("id"), rs.getString("user"), rs.getString("naredba"), rs.getString("odgovor"), date);
                    dnevnik.add(d);
                }

            }

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return dnevnik;
    }

    public List<Dnevnik> ucitajZahtjeveKorisnika(String user) {
        List<Dnevnik> dnevnik = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(url, korisnik, lozinka);
            statemant = connection.createStatement();

            sql = "SELECT * FROM alebenkov_dnevnik where user='" + user + "'";
            rs = statemant.executeQuery(sql);
            while (rs.next()) {
                Date date = rs.getTimestamp("time");
                if (rs.getString("naredba").contains("PASSWD")) {
                    Dnevnik d = new Dnevnik(rs.getInt("id"), rs.getString("user"), rs.getString("naredba"), rs.getString("odgovor"), date);
                    dnevnik.add(d);
                }

            }

        } catch (SQLException ex) {
            System.out.println("ERROR | Greska u radu s bazom: " + ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (statemant != null) {
                try {
                    statemant.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
        return dnevnik;
    }

}
