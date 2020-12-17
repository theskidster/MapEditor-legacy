package dev.theskidster.mapeditor.main;

import java.nio.IntBuffer;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author J Hoffman
 * Created: Dec 15, 2020
 */

final class Window {

    int width;
    int height;
    
    final long handle;
    
    String title;
    Vector2i position;
    
    Window(String title, Monitor monitor) {
        //TODO: pull these values in from prefrences file
        width  = 1480;
        height = 960;
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer xStartBuf = stack.mallocInt(1);
            IntBuffer yStartBuf = stack.mallocInt(1);
            
            glfwGetMonitorPos(monitor.handle, xStartBuf, yStartBuf);
            
            position = new Vector2i(
                    Math.round((monitor.width - width) / 2) + xStartBuf.get(), 
                    Math.round((monitor.height - height) / 2) + yStartBuf.get());
        }
        
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        
        handle = glfwCreateWindow(width, height, title, NULL, NULL);
    }
    
    /**
     * Sets the icon image of the window. Images should be at least 32x32 pixels large, but no larger than 64x64.
     * 
     * @param filename the name of the file to load. Expects the file extension to be included.
     */
    private void setWindowIcon(String filename) {
        
    }
    
    /**
     * Displays the window and establishes its input callback events.
     * 
     * @param monitor the monitor to display this window on
     */
    void show(Monitor monitor) {
        glfwSetWindowMonitor(handle, NULL, position.x, position.y, width, height, monitor.refreshRate);
        glfwSetWindowPos(handle, position.x, position.y);
        glfwSwapInterval(1);
        glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        glfwShowWindow(handle);
        
        glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
            
        });
        
        glfwSetMouseButtonCallback(handle, (window, button, action, mods) -> {
            
        });
        
        glfwSetCursorPosCallback(handle, (window, xPos, yPos) -> {
            
        });
        
        //TODO: specify viewport bounds.
    }
    
}
