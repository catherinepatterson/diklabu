/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.tuttas.util;

import de.tuttas.config.Config;

/**
 * Logger Klasse
 * @author Jörg
 */
public class Log {
    
    /**
     * Ausgabe einer LOG Meldung
     * @param msg  Die Meldung
     */
    public static void d(String msg) {
        //if (Config.debug) {
            System.out.println(msg);
        //}
    }
}
