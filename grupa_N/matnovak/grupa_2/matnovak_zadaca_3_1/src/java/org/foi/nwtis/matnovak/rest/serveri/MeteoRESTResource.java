/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.matnovak.rest.serveri;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.MediaType;
import org.foi.nwtis.matnovak.rest.klijenti.GMKlijent;
import org.foi.nwtis.matnovak.rest.klijenti.OWMKlijent;
import org.foi.nwtis.matnovak.web.podaci.Lokacija;
import org.foi.nwtis.matnovak.web.podaci.MeteoPodaci;

/**
 * REST Web Service
 *
 * @author grupa_2
 */
public class MeteoRESTResource {

    private String id;

    /**
     * Creates a new instance of MeteoRESTResource
     */
    private MeteoRESTResource(String id) {
        this.id = id;
    }

    /**
     * Get instance of the MeteoRESTResource
     */
    public static MeteoRESTResource getInstance(String id) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of MeteoRESTResource class.
        return new MeteoRESTResource(id);
    }

    /**
     * Retrieves representation of an instance of org.foi.nwtis.matnovak.rest.serveri.MeteoRESTResource
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJson() {
        //TODO preuzmi iz baze podataka
        String adrese[] = {"Hrvatska, Varaždin, Pavlinska 2"
                          ,"Hrvatska, Varaždin, Kralja Petra Krešimira 4"
                          ,"Hrvatska, Zagreb, Ilica 1"
                          ,"Hrvatska, Čakovec, Ul. kralja tomislava 5"
                          ,"Hrvatska, Split, Trg Braće Radića 15"
                          ,"Hrvatska, Osijek, Ul. Ivana Gundulića 66"
                          ,"Hrvatska, Rijeka, Trg Ivana Koblera 1"};
        
        int rbr = Integer.parseInt(id);
        if(rbr < 0 || rbr>adrese.length){
            return "ERROR nepoznati id";
        }
        else {
            JsonObjectBuilder jobRoot = Json.createObjectBuilder();
            JsonObjectBuilder jobMeteo = Json.createObjectBuilder();
            JsonArrayBuilder jab = Json.createArrayBuilder();
            GMKlijent gmk = new GMKlijent();
            Lokacija l = gmk.getGeoLocation(adrese[rbr]);
            String APPID = "xxx"; //TODO pruzmi iz konfiguracijske datoteke
            OWMKlijent owmk = new OWMKlijent(APPID);
            MeteoPodaci mp = owmk.getRealTimeWeather(l.getLatitude(), l.getLongitude());
            
            jobRoot.add("adresa", adrese[rbr]);
            jobMeteo.add("datumVrijeme", (new Date()).getTime());
            jobMeteo.add("temperatura", mp.getTemperatureValue());
            jobMeteo.add("temperaturaMjernaJedinica", mp.getTemperatureUnit());
            jobMeteo.add("tlak", mp.getPressureValue());
            jobMeteo.add("tlakMjernaJedinica", mp.getPressureUnit());
            jab.add(jobMeteo);
            jobRoot.add("meteopodaci", jab);
            return jobRoot.build().toString();
        }

    }

    /**
     * PUT method for updating or creating an instance of MeteoRESTResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public void putJson(String content) {
    }

    /**
     * DELETE method for resource MeteoRESTResource
     */
    @DELETE
    public void delete() {
    }
}
