/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.alebenkov.zadaca_1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.alebenkov.konfiguracije.Konfiguracija;
import static org.foi.nwtis.alebenkov.zadaca_1.ObradaZahtjeva.StanjeDretve.Slobodna;

/**
 *
 * @author alen
 */
public class ObradaZahtjeva extends Thread {

    public enum StanjeDretve {

        Slobodna, Zauzeta
    };

    private Konfiguracija konfig;
    private Socket socket;
    private StanjeDretve stanje;

    public ObradaZahtjeva(ThreadGroup group, String name) {
        super(group, name);
        this.stanje = StanjeDretve.Slobodna;
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        InputStream is = null;
        OutputStream os = null;
        System.out.println("Pokrecem serversku dretvu " + this.getName() + " stanje " + this.getState() + this.getStanje());
        
        try {
            is = socket.getInputStream();
            os = socket.getOutputStream();

            StringBuilder sb = new StringBuilder();
            while (is.available() > 0) {
                int znak = is.read();
                if (znak == -1) {
                    break;
                }
                sb.append((char) znak);
            }

            System.out.println("Dobivena naredba: "+sb.toString());
            os.write("OK".getBytes());
            os.flush();
            socket.shutdownOutput();
            this.wait();
            this.setStanje(Slobodna);
        } catch (IOException ex) {
            System.out.println("Problem neki " + ex.getMessage());
            Logger.getLogger(ObradaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);

        } catch (InterruptedException ex) {
            Logger.getLogger(ObradaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (is != null) {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(ObradaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (os != null) {
            try {
                os.close();
            } catch (IOException ex) {
                Logger.getLogger(ObradaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);

            }
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ObradaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates. 

    }

    public void setKonfig(Konfiguracija konfig) {
        this.konfig = konfig;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setStanje(StanjeDretve stanje) {
        this.stanje = stanje;
    }

    public StanjeDretve getStanje() {
        return stanje;
    }

}
