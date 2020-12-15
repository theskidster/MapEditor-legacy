package dev.theskidster.mapeditor.main;

import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author J Hoffman
 * Created: Dec 15, 2020
 */

/**
 * Contains the applications entry point.
 */
public class Main {

    /**
     * Application entry point.
     * 
     * @param args the arguments to be supplied to the application when run from a command line
     */
    public static void main(String args[]) throws Exception {
        new App().start();
    }
    
}
