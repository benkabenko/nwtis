/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matnovak.web.kontrole;

/**
 *
 * @author grupa_3
 */
public class Korisnik {
    private String korisnik;
    private String ime;
    private String prezime;
    private String ip_adresa;
    private String ses_ID;
    private int vrsta;

    public Korisnik(String korisnik, String ime, String prezime, String ip_adresa, String ses_ID, int vrsta) {
        this.korisnik = korisnik;
        this.ime = ime;
        this.prezime = prezime;
        this.ip_adresa = ip_adresa;
        this.ses_ID = ses_ID;
        this.vrsta = vrsta;
    }
    
    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getIp_adresa() {
        return ip_adresa;
    }

    public void setIp_adresa(String ip_adresa) {
        this.ip_adresa = ip_adresa;
    }

    public String getSes_ID() {
        return ses_ID;
    }

    public void setSes_ID(String ses_ID) {
        this.ses_ID = ses_ID;
    }

    public int getVrsta() {
        return vrsta;
    }

    public void setVrsta(int vrsta) {
        this.vrsta = vrsta;
    }
    
    
}
