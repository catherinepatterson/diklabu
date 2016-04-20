
package de.tuttas.entities;

import java.io.Serializable;
import java.sql.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Entity eines Schuelers
 * @author Jörg
 */

@Entity
@NamedQueries({
   @NamedQuery(name = "findSchuelerbyCredentials", query= "select s from Schueler s where s.NNAME like :paramName and s.VNAME like :paramVorname and S.GEBDAT = :paramGebDatum"),
   @NamedQuery(name = "findSchuelerbyNameKlasse", query= "select s from Schueler s JOIN Schueler_Klasse sk ON s.ID=sk.ID_SCHUELER JOIN Klasse k on k.ID=sk.ID_KLASSE where s.NNAME like :paramName and s.VNAME like :paramVorname and k.KNAME like :paramKlasse"),
   @NamedQuery(name = "findBetriebeEinerBenanntenKlasse", query= "select NEW de.tuttas.restful.Data.AusbilderObject(s.ID,a.ANREDE,a.NNAME,a.EMAIL,a.TELEFON,a.FAX,b.NAME,b.PLZ,b.ORT,b.STRASSE,b.NR) from Schueler s JOIN Schueler_Klasse sk ON s.ID=sk.ID_SCHUELER JOIN Klasse k ON k.ID=sk.ID_KLASSE JOIN Ausbilder a on a.ID=s.ID_AUSBILDER JOIN Betrieb b on b.ID=a.ID_BETRIEB where k.KNAME like :paramNameKlasse AND s.ABGANG not like 'J' order BY s.NNAME"),
    @NamedQuery(name = "findSchuelerByAusbilderId", query= "select s from Schueler s where s.ID_AUSBILDER = :paramAusbilderId")   
})
public class Schueler implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    /**
     * Primärschlüssel
     */
    private Integer ID;
    /**
     * Vorname
     */
    private String NNAME;
    /**
     * Nachname
     */
    private String VNAME;
    /**
     * Geburtsdatum in der Form yyyy-mm-dd
     */
    private Date GEBDAT;
    
    private String EMAIL;
    

    private String ACC;
    
    // Kommentare zu Schülern
    private String INFO;
    
    private Integer ID_AUSBILDER;
    
    private String ABGANG;

    public void setINFO(String INFO) {
        this.INFO = INFO;
    }

    public String getINFO() {
        return INFO;
    }

    public void setABGANG(String ABGANG) {
        this.ABGANG = ABGANG;
    }

    public String getABGANG() {
        return ABGANG;
    }

        
    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    
    
    public void setID_AUSBILDER(Integer ID_AUSBILDER) {
        this.ID_AUSBILDER = ID_AUSBILDER;
    }

    public Integer getID_AUSBILDER() {
        return ID_AUSBILDER;
    }
    
    

    public String getACC() {
        return ACC;
    }

    public void setACC(String ACC) {
        this.ACC = ACC;
    }
    
    
    
    public Date getGEBDAT() {
        return GEBDAT;
    }

    public void setGEBDAT(Date GEBDAT) {
        this.GEBDAT = GEBDAT;
    }

    public String getNNAME() {
        return NNAME;
    }

    public void setNNAME(String NNAME) {
        this.NNAME = NNAME;
    }

    public String getVNAME() {
        return VNAME;
    }

    public void setVNAME(String VNAME) {
        this.VNAME = VNAME;
    }
    
   

    public Integer getId() {
        return ID;
    }

    public void setId(Integer id) {
        this.ID = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ID != null ? ID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Schueler)) {
            return false;
        }
        Schueler other = (Schueler) object;
        if ((this.ID == null && other.ID != null) || (this.ID != null && !this.ID.equals(other.ID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Schueler[ id=" + ID + "GEBDAT="+GEBDAT+" NAME="+VNAME+" "+NNAME+"]";
    }
    
}
