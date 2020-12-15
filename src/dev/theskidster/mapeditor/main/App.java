package dev.theskidster.mapeditor.main;

import static org.lwjgl.glfw.GLFW.*;

/**
 * @author J Hoffman
 * Created: Dec 15, 2020
 */

/**
 * Encapsulates program state into a single file.
 */
public final class App {
    
    private static boolean vSync;
    
    public static final String VERSION = "0.1.0";
    
    private Monitor monitor;
    private Window window;
    
    void loadPrefrences() {
        
    }
    
    void start() {
        glfwInit();
        
        loadPrefrences();
        
        monitor = new Monitor();
        window  = new Window("RG Map Editor", monitor);
        
        window.show(monitor);
        
        while(!glfwWindowShouldClose(window.handle)) {
            glfwPollEvents();
            
            
        }
    }
    
}
