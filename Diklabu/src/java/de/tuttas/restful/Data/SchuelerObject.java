/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.restful.Data;

/**
 *
 * @author Jörg
 */
public class SchuelerObject {
    private String name;
    private String vorname;
    private String klasse;
    private String login;
    private int id;

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }
    
    

    public String getKlasse() {
        return klasse;
    }

    public String getName() {
        return name;
    }

    public String getVorname() {
        return vorname;
    }

    public void setKlasse(String Klasse) {
        this.klasse = Klasse;
    }

    public void setName(String Name) {
        this.name = Name;
    }

    public void setVorname(String Vorname) {
        this.vorname = Vorname;
    }

    @Override
    public String toString() {
        return "SchuelerObject Name="+this.getName()+" Vorname="+this.getVorname()+" Klasse="+this.getKlasse();
    }
    
    
    
}
