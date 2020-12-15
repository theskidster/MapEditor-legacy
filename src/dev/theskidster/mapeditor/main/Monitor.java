package dev.theskidster.mapeditor.main;

import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWVidMode;

/**
 * @author J Hoffman
 * Created: Dec 15, 2020
 */

final class Monitor {
    
    int width;
    int height;
    int refreshRate;
    
    final long handle;
    
    GLFWVidMode videoMode;
    
    Monitor() {        
        handle    = glfwGetPrimaryMonitor();
        videoMode = glfwGetVideoMode(handle);
        
        width       = videoMode.width();
        height      = videoMode.height();
        refreshRate = videoMode.refreshRate();
    }
    
}