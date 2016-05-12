/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.dkermek.web;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author grupa_1
 */
public class DretvaPromjena extends Thread {

    SlusacPromjena slusacPromjena;
    int brojSekundi;
    String nazivKlase;
    Brojaci objekt;

    public DretvaPromjena(SlusacPromjena slusacPromjena, int brojSekundi, String nazivKlase) {
        this.slusacPromjena = slusacPromjena;
        this.brojSekundi = brojSekundi;
        this.nazivKlase = nazivKlase;
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        try {
            while (true) {
                this.objekt.run();
                sleep(brojSekundi * 1000);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(DretvaPromjena.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized void start() {
        try {
            Class klasa = Class.forName(this.nazivKlase);
            objekt = (Brojaci) klasa.newInstance();
            objekt.dodajSlusaca(slusacPromjena);
            super.start();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(DretvaPromjena.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
