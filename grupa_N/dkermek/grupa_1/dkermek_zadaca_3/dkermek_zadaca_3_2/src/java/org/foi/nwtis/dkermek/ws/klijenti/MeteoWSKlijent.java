/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.dkermek.ws.klijenti;

/**
 *
 * @author grupa_1
 */
public class MeteoWSKlijent {

    public static java.util.List<org.foi.nwtis.dkermek.ws.serveri.Adresa> dajSveAdrese() {
        org.foi.nwtis.dkermek.ws.serveri.GeoMeteoWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.GeoMeteoWS_Service();
        org.foi.nwtis.dkermek.ws.serveri.GeoMeteoWS port = service.getGeoMeteoWSPort();
        return port.dajSveAdrese();
    }
    
}
