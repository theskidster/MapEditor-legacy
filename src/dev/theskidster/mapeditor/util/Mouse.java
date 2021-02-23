package dev.theskidster.mapeditor.util;

import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author J Hoffman
 * Created: Jan 5, 2021
 */

public final class Mouse {
    
    private int prevShape;
    
    private final long winHandle;
    private long cursorHandle;
    
    public boolean clicked;
    
    public String button = "";
    
    public Vector2i cursorPos = new Vector2i();
    
    public Mouse(long winHandle) {
        this.winHandle = winHandle;
        
        cursorHandle = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        glfwSetCursor(winHandle, cursorHandle);
    }
    
    public void setCursorShape(int shape) {
        if(shape != prevShape) {
            glfwDestroyCursor(cursorHandle);
            cursorHandle = glfwCreateStandardCursor(shape);
            glfwSetCursor(winHandle, cursorHandle);
        }
        
        prevShape = shape;
    }
    
}