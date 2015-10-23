
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
})
public class Schueler implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
        // TODO: Warning - this method won't work in the case the id fields are not set
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
        return "de.tuttas.entities.Schueler[ id=" + ID + " ]";
    }
    
}