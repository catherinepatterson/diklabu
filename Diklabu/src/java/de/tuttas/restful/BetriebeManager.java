/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.restful;

import de.tuttas.entities.Ausbilder;
import de.tuttas.entities.Betrieb;
import de.tuttas.entities.Schueler;
import de.tuttas.entities.Schueler_Klasse;
import de.tuttas.restful.Data.PsResultObject;
import de.tuttas.restful.Data.ResultObject;
import de.tuttas.util.Log;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 * @author Jörg
 */
@Path("admin/betriebe")
@Stateless
public class BetriebeManager {

    /**
     * Injection des EntityManagers
     */
    @PersistenceContext(unitName = "DiklabuPU")
    EntityManager em;

    @GET
    @Produces({"application/json; charset=iso-8859-1"})
    public List<Betrieb> getCompanies() {
        Log.d("Webservice klasse Betriebe");

        Query query = em.createNamedQuery("findBetriebe");
        List<Betrieb> betriebe = query.getResultList();
        return betriebe;
    }

    @GET
    @Path("/{betrieb}")
    @Produces({"application/json; charset=iso-8859-1"})
    public List<Betrieb> getCompanies(@PathParam("betrieb") String b) {
        Log.d("Webservice Betrieb:" + b);

        Query query = em.createNamedQuery("findBetriebByName");
        query.setParameter("paramNameBetrieb", b);
        List<Betrieb> betriebe = query.getResultList();
        Log.d("Results=" + betriebe);
        return betriebe;
    }
    
    @GET
    @Path("id/{id}")
    @Produces({"application/json; charset=iso-8859-1"})
    public Betrieb getCompany(@PathParam("id") int bid) {
        Betrieb b = em.find(Betrieb.class, bid);
        return b;
    }

    @DELETE
    @Path("/{idbetrieb}")
    @Produces({"application/json; charset=iso-8859-1"})
    public PsResultObject deleteCompanies(@PathParam("idbetrieb") int bid) {
        Betrieb b = em.find(Betrieb.class, bid);

        PsResultObject ro = new PsResultObject();
        if (b != null) {
            Query query = em.createNamedQuery("findAusbilderByBetriebId");
            query.setParameter("paramBetriebId", bid);
            List<Ausbilder> ausbilder = query.getResultList();
            if (ausbilder.size() != 0) {
                ro.setSuccess(false);
                ro.setMsg("Dem Betrieb " + b.getNAME() + " sind noch Asubilder zugewiesen!");
                int[] ids = new int[ausbilder.size()];
                for (int i=0;i<ausbilder.size();i++) {
                    ids[i]=ausbilder.get(i).getID();                    
                }
                ro.setIds(ids);
            }
            else {
                em.remove(b);
                ro.setSuccess(true);
                ro.setMsg("Betrieb " + b.getNAME() + " gelöscht!");
            }
        }
        else {
            ro.setSuccess(false);
            ro.setMsg("Kann Betrieb mit ID="+bid+" nicht finden!");
        }
        return ro;
    }

    
    @POST
    @Produces({"application/json; charset=iso-8859-1"})
    public Betrieb createCompany(Betrieb b) {
        em.persist(b);
        em.flush();
        return b;        
    }
    
    @POST
    @Produces({"application/json; charset=iso-8859-1"})
    @Path("id/{idbetrieb}")
    public Betrieb setCompany(@PathParam("idbetrieb") int bid, Betrieb b) {
        em.getEntityManagerFactory().getCache().evictAll();
        Betrieb be = em.find(Betrieb.class, bid);
        if (be != null) {
            if (b.getNAME() != null) {
                be.setNAME(b.getNAME());
            }
            if (b.getNR() != null) {
                be.setNR(b.getNR());
            }
            if (b.getORT() != null) {
                be.setORT(b.getORT());
            }
            if (b.getPLZ() != null) {
                be.setPLZ(b.getPLZ());
            }
            if (b.getSTRASSE() != null) {
                be.setSTRASSE(b.getSTRASSE());
            }
            em.merge(be);
        }
        return be;
    }
}
