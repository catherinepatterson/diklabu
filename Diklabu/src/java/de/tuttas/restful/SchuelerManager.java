/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.restful;

import de.tuttas.config.Config;
import de.tuttas.entities.Ausbilder;
import de.tuttas.entities.Betrieb;
import de.tuttas.entities.Klasse;
import de.tuttas.entities.LoginSchueler;
import de.tuttas.restful.Data.SchuelerObject;
import de.tuttas.entities.Schueler;
import java.io.File;
import java.io.FileNotFoundException;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

/**
 *
 * @author Jörg
 */
@Path("schueler")
@Stateless
public class SchuelerManager {
  

    /**
     * Injection des EntityManagers
     */
    @PersistenceContext(unitName = "DiklabuPU")
    EntityManager em;

    @GET
    @Path("/{idschueler}")
    public SchuelerObject getPupil(@PathParam("idschueler") int idschueler) {
        System.out.println("Abfrage Schueler mit der ID " + idschueler);
        Schueler s = em.find(Schueler.class, idschueler);

        if (s != null) {
            SchuelerObject so = new SchuelerObject();
            so.setId(idschueler);
            so.setGebDatum(s.getGEBDAT());
            so.setName(s.getNNAME());
            so.setVorname(s.getVNAME());
            Query query = em.createNamedQuery("findKlassenbySchuelerID");
            query.setParameter("paramIDSchueler", so.getId());
            List<Klasse> klassen = query.getResultList();
            System.out.println("Result List:" + klassen);
            so.setKlassen(klassen);

            Ausbilder a = em.find(Ausbilder.class, s.getID_AUSBILDER());
            so.setAusbilder(a);

            if (a != null) {
                Betrieb b = em.find(Betrieb.class, a.getID_BETRIEB());
                so.setBetrieb(b);
            }
            return so;
        }
        return null;
    }

    @GET
    @Path("/bild/{idschueler}")    
    @Produces("image/jpg")
    public Response getFile(@PathParam("idschueler") int idschueler) {

        String filename = Config.IMAGE_FILE_PATH+idschueler+".jpg";
        System.out.println("Lade file "+filename);
        File file = new File(filename);
        if (!file.exists()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition",
                "attachment; filename=image_from_server.png");
        return response.build();
        
        

    }

}
