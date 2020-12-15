package dev.theskidster.mapeditor.main;

import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import org.lwjgl.glfw.GLFWVidMode;

/**
 * @author J Hoffman
 * Created: Dec 15, 2020
 */

final class Monitor {
    
    final long handle;
    
    GLFWVidMode videoMode;
    
    Monitor(long handle) {
        this.handle = handle;
        videoMode   = glfwGetVideoMode(handle);
    }
    
}