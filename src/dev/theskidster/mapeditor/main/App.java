package dev.theskidster.mapeditor.main;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author J Hoffman
 * Created: Dec 15, 2020
 */

/**
 * Encapsulates all program state into a single convenient static class.
 */
public final class App {
    
    private static boolean vSync = true; //TODO: pull these values in from prefrences file
    
    public static final String VERSION = "0.2.0";
    
    private Monitor monitor;
    private Window window;
    
    /**
     * Initializes various dependencies then enters a loop that will only terminate once the user decides to exit the application.
     */
    void start() {
        glfwInit();
        
        monitor = new Monitor();
        window  = new Window("RG Map Editor", monitor);
        
        window.show(monitor);
        
        Logger.printSystemInfo();
        
        while(!glfwWindowShouldClose(window.handle)) {
            glfwPollEvents();
        }
        
        Logger.close();
    }
    
}
