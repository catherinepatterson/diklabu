/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.restful;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import de.tuttas.config.Config;
import de.tuttas.entities.Anwesenheit;
import de.tuttas.entities.Ausbilder;
import de.tuttas.entities.Betrieb;
import de.tuttas.entities.Klasse;
import de.tuttas.restful.Data.SchuelerObject;
import de.tuttas.entities.Schueler;
import de.tuttas.entities.Schueler_Klasse;
import de.tuttas.restful.Data.BildObject;
import de.tuttas.restful.Data.Credential;
import de.tuttas.restful.Data.PsResultObject;
import de.tuttas.restful.Data.ResultObject;
import de.tuttas.util.ImageUtil;
import de.tuttas.util.Log;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

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

    @POST
    @Path("/{idschueler}")
    public SchuelerObject getPupil(@PathParam("idschueler") int idschueler, SchuelerObject so) {
        em.getEntityManagerFactory().getCache().evictAll();
        Schueler s = em.find(Schueler.class, so.getId());
        if (s == null) {
            return null;
        }
        s.setINFO(so.getInfo());
        em.merge(s);
        return so;
    }

    @POST
    @Produces({"application/json; charset=iso-8859-1"})
    public Schueler addSchueler(Schueler s) {
        Log.d("add Schuler " + s.toString());
        em.getEntityManagerFactory().getCache().evictAll();
        em.persist(s);
        em.flush();
        return s;
    }

    @POST
    @Produces({"application/json; charset=iso-8859-1"})
    @Path("/{idschueler}")
    public Schueler setSchueler(@PathParam("idschueler") int sid,Schueler s) {
        Log.d("set Schuler " + s.toString());
        Schueler sl = em.find(Schueler.class, sid);
        if (sl!=null) {
            if (s.getABGANG()!=null) sl.setABGANG(s.getABGANG());
            if (s.getEMAIL()!=null) sl.setEMAIL(s.getEMAIL());
            if (s.getGEBDAT()!=null) sl.setGEBDAT(s.getGEBDAT());
            if (s.getID_AUSBILDER()!=null) sl.setID_AUSBILDER(s.getID_AUSBILDER());
            if (s.getINFO()!=null) sl.setINFO(s.getINFO());
            if (s.getNNAME()!=null) sl.setNNAME(s.getNNAME());
            if (s.getVNAME()!=null) sl.setVNAME(s.getVNAME());
            em.merge(sl);
        }
        return sl;
    }
    
           
    @POST
    @Path("/info/")
    @Produces({"application/json; charset=iso-8859-1"})
    public List<Schueler> getPupilbyCredential(Credential c) {
        em.getEntityManagerFactory().getCache().evictAll();
        Query query = em.createNamedQuery("findSchuelerbyCredentials");
        query.setParameter("paramName", c.getName());
        query.setParameter("paramVorname", c.getVorName());
        query.setParameter("paramGebDatum", c.getGebDatum());
        List<Schueler> pupils = query.getResultList();
        return pupils;
    }

    @DELETE
    @Path("/{idschueler}")
    @Produces({"application/json; charset=iso-8859-1"})
    public PsResultObject deleteSchueler(@PathParam("idschueler") int sid) {
        em.getEntityManagerFactory().getCache().evictAll();
        Log.d("Webservice delete Schueler:" + sid);
        PsResultObject ro = new PsResultObject();
        Schueler s = em.find(Schueler.class, sid);
        if (s != null) {
            Query query = em.createNamedQuery("findKlassenids");
            query.setParameter("paramidSchueler", sid);
            List<Schueler_Klasse> sk = query.getResultList();
            if (sk.size()!=0) {
                ro.setMsg("Schüler "+s.getVNAME()+" "+s.getNNAME()+" kann nicht gelöscht werden, da er sich noch in Klassenn befindet");
                ro.setSuccess(false);   
                int[] ids = new int[sk.size()];
                for (int i=0;i<sk.size();i++) {
                    ids[i]=sk.get(i).getID_KLASSE();                    
                }
                ro.setIds(ids);
            }
            else {
                query = em.createNamedQuery("findAnwesenheitByidSchueler");
                query.setParameter("paramidSchueler", sid);    
                List<Anwesenheit> anw = query.getResultList();
                for (Anwesenheit a:anw) {
                    Log.d("Lösche Anwesenheitseintrag "+a);
                    em.remove(a);
                }
                em.flush();
                em.remove(s);
                ro.setMsg("Schüler "+s.getVNAME()+" "+s.getNNAME()+" gelöscht");
                ro.setSuccess(true);
            }
        } else {
            ro.setMsg("Kann Schüler mit id=" + sid + " nicht finden");
            ro.setSuccess(false);
        }
        return ro;
    }

    @GET
    @Path("/{idschueler}")
    @Produces({"application/json; charset=iso-8859-1"})
    public SchuelerObject getPupil(@PathParam("idschueler") int idschueler) {
        Log.d("Abfrage Schueler mit der ID " + idschueler);
        Schueler s = em.find(Schueler.class, idschueler);

        if (s != null) {
            SchuelerObject so = new SchuelerObject();
            so.setId(idschueler);
            so.setGebDatum(s.getGEBDAT());
            so.setName(s.getNNAME());
            so.setVorname(s.getVNAME());
            so.setEmail(s.getEMAIL());
            so.setInfo(s.getINFO());
            Query query = em.createNamedQuery("findKlassenbySchuelerID");
            query.setParameter("paramIDSchueler", so.getId());
            List<Klasse> klassen = query.getResultList();
            Log.d("Result List:" + klassen);
            so.setKlassen(klassen);

            if (s.getID_AUSBILDER() != null) {
                Ausbilder a = em.find(Ausbilder.class, s.getID_AUSBILDER());
                so.setAusbilder(a);

                if (a != null) {
                    Betrieb b = em.find(Betrieb.class, a.getID_BETRIEB());
                    so.setBetrieb(b);
                }
            }

            return so;
        }
        return null;
    }

    @GET
    @Path("/bild/{idschueler}")
    @Produces("image/jpg")
    public Response getFile(@PathParam("idschueler") int idschueler) {

        String filename = Config.IMAGE_FILE_PATH + idschueler + ".jpg";
        Log.d("Lade  file " + filename);
        File file = new File(filename);
        if (!file.exists()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/bild64/{idschueler}")

    public BildObject getFile64(@PathParam("idschueler") int idschueler) {
        BildObject bo = new BildObject();
        bo.setId(idschueler);
        String filename = Config.IMAGE_FILE_PATH + idschueler + ".jpg";
        Log.d("Lade file " + filename);
        File file = new File(filename);

        if (!file.exists()) {
            return null;
        }
        BufferedImage img = null;
        try {
            img = ImageIO.read(file);
            bo.setBase64(ImageUtil.encodeToString(img, "jpeg"));
            return bo;

        } catch (IOException e) {
            return bo;
        }
    }

    @POST
    @Path("/bild/{idschueler}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ResultObject uploadFile(
            @PathParam("idschueler") int idschueler,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {

        ResultObject r = new ResultObject();
        String fileLocation = Config.IMAGE_FILE_PATH + idschueler + ".jpg";
        Log.d("upload  File for " + idschueler);
        try {

            byte[] imageBytes = IOUtils.toByteArray(uploadedInputStream);

            int i = uploadedInputStream.read(imageBytes);
            Log.d("habe " + i + " bytes gelesen!");
            InputStream myInputStream = new ByteArrayInputStream(imageBytes);
            Image image = ImageIO.read(myInputStream);
            Log.d("Image gelesen =" + image);
            InputStream myExifInputStream = new ByteArrayInputStream(imageBytes);
            int orientation = ImageUtil.getImageOrientation(myExifInputStream);
            BufferedImage bImage = ImageUtil.toBufferedImage(image);
            Log.d("Image hat w=" + bImage.getWidth() + " h=" + bImage.getHeight());
            bImage = ImageUtil.transformImage(bImage, ImageUtil.getExifTransformation(orientation, image.getWidth(null), image.getHeight(null)));
            Log.d("Image hat nach Transformation w=" + bImage.getWidth() + " h=" + bImage.getHeight());
            if (image != null) {
                int originalWidth = bImage.getWidth();
                int originalHeight = bImage.getHeight();
                int newWidth = 200;
                int newHeight = Math.round(newWidth * ((float) originalHeight / originalWidth));
                BufferedImage bi = this.createResizedCopy(bImage, newWidth, newHeight, true);
                ImageIO.write(bi, "jpg", new File(Config.IMAGE_FILE_PATH + idschueler + ".jpg"));
                r.setMsg("Bild erfolgreich hochgeladen!");
                r.setSuccess(true);
            } else {
                r.setMsg("Fehler beim Hochladen des Bildes!");
                r.setSuccess(false);

            }
        } catch (IOException e) {
            Log.d("Error");
            r.setMsg(e.getMessage());
            r.setSuccess(false);
        } catch (MetadataException ex) {
            Logger.getLogger(SchuelerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ImageProcessingException ex) {
            Logger.getLogger(SchuelerManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(SchuelerManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return r;
    }

    private BufferedImage createResizedCopy(Image originalImage, int scaledWidth, int scaledHeight, boolean preserveAlpha) {
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        return scaledBI;
    }
}
