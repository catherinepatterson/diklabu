/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.entities;

/**
 *
 * @author Jörg
 */
public class Noten_all_Id {
    private Integer ID_SCHUELER;
    private String ID_LERNFELD;
    private Integer ID_SCHULJAHR;

    public Noten_all_Id() {
    }

    public Noten_all_Id(Integer ID_SCHUELER, String ID_LERNFELD, Integer ID_SCHULJAHR) {
        this.ID_SCHUELER = ID_SCHUELER;
        this.ID_LERNFELD = ID_LERNFELD;
        this.ID_SCHULJAHR = ID_SCHULJAHR;
    }

    public Integer getID_SCHUELER() {
        return ID_SCHUELER;
    }

    public void setID_SCHUELER(Integer ID_SCHUELER) {
        this.ID_SCHUELER = ID_SCHUELER;
    }

    public String getID_LERNFELD() {
        return ID_LERNFELD;
    }

    public void setID_LERNFELD(String ID_LERNFELD) {
        this.ID_LERNFELD = ID_LERNFELD;
    }

    public Integer getID_SCHULJAHR() {
        return ID_SCHULJAHR;
    }

    public void setID_SCHULJAHR(Integer ID_SCHULJAHR) {
        this.ID_SCHULJAHR = ID_SCHULJAHR;
    }
    
    
}
