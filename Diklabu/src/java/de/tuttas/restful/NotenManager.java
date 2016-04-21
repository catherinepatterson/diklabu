/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.restful;

import de.tuttas.entities.Lehrer;
import de.tuttas.entities.Lernfeld;
import de.tuttas.entities.Noten;
import de.tuttas.entities.Schueler;
import de.tuttas.entities.Verlauf;
import de.tuttas.restful.Data.NotenObjekt;
import de.tuttas.util.Log;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 * @author Jörg
 */
@Path("noten")
@Stateless
public class NotenManager {
    /**
     * Injection des EntityManagers
     */
    @PersistenceContext(unitName = "DiklabuPU")
    EntityManager em;

    /**
     * Noten einer Klasse abfragen, sortiert nach Schüler ID und LF
     * @param kl Bezeichnung der Klasse
     * @return Liste der Noten
     */

    @GET
    @Path("/{klasse}")
    public List<NotenObjekt> getNoten(@PathParam("klasse") String kl) {
        Log.d("Webservice noten GET: klasse=" + kl);
        Query query = em.createNamedQuery("findNoteneinerKlasse");
        query.setParameter("paramNameKlasse", kl);
        List<Noten> noten = query.getResultList();
        Log.d("Result List:" + noten);
        List<NotenObjekt> lno = new ArrayList<>();
        int sid=0;
        NotenObjekt no=null;
        for (Noten n : noten) {
            if (sid!=n.getID_SCHUELER()) {
                no=new NotenObjekt();
                no.setSchuelerID(n.getID_SCHUELER());
                no.setSuccess(true);
                lno.add(no);
                sid=n.getID_SCHUELER();
            }
            no.getNoten().add(n);
        }        
        System.out.println("lno="+lno);
        return lno;
    }

    
    /**
     * Noten eines Schülers abfragen
     * @param sid ID des Schülers
     * @return Notenobjekt
     */
    @GET
    @Path("/schueler/{schuelerID}")
    public NotenObjekt getNoten(@PathParam("schuelerID") int sid) {
        Log.d("Webservice noten GET: schuelerID=" + sid);
        Query query = em.createNamedQuery("findNoteneinesSchuelers");
        query.setParameter("paramNameSchuelerID", sid);
        List<Noten> noten = query.getResultList();
        Log.d("Result List:" + noten);
        NotenObjekt no = new NotenObjekt();
        no.setSchuelerID(sid);
        no.setNoten(noten);
        no.setSuccess(true);
        return no;
    }
    
    /**
     * Noten eintragen
     * @param n Noten-Entitie Objekt
     * @return Noten Entitie Objekt
     */
    @POST
    public Noten setNote(Noten n) {
        em.getEntityManagerFactory().getCache().evictAll();
        Log.d("POST Note = " + n.toString());
        Query q = em.createNamedQuery("findNote");
        q.setParameter("paramSchuelerID", n.getID_SCHUELER());
        q.setParameter("paramLernfeldID", n.getID_LERNFELD());
        List<Noten> noten = q.getResultList();
        Log.d("Result List:" + noten);
        if (noten.size() != 0) {
            for (Noten no : noten) {                
                if (no.getID_LERNFELD().compareTo(n.getID_LERNFELD())==0 &&
                    no.getID_SCHUELER().intValue()==n.getID_SCHUELER().intValue() &&
                    no.getID_LK().compareTo(n.getID_LK())==0    ) {
                    Log.d("Es existier bereits ein Eintrag also Update");
                    no.setWERT(n.getWERT());
                    em.merge(no);
                    no.setSuccess(false);
                    no.setMsg("Eintrag aktualisiert!");
                    return no;
                }
            }
            n.setSuccess(false);
            n.setMsg("Es können nur eigene Noteneinträge geändert werden!");
            return n;
        } else {            
            Schueler s = em.find(Schueler.class, n.getID_SCHUELER().intValue());
            Lehrer l = em.find(Lehrer.class, n.getID_LK());
            Lernfeld lf= em.find(Lernfeld.class, n.getID_LERNFELD());
            if (s!=null && l!=null && lf!=null) {
                Log.d("Neuen Noteneintrag erzeugen");
                em.persist(n);
                n.setSuccess(true);
            }
            else {
                Log.d("Neuen Noteneintrag konnte nicht erzeugt werden");
                n.setSuccess(false);
                n.setMsg("Neuer Noteneintrag konnte nicht erzeugt werden!");                
            }            
        }
        return n;
    }    
}