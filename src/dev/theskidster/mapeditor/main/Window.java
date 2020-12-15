package dev.theskidster.mapeditor.main;

import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.*;
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
    
    Window(String title) {
        handle = glfwCreateWindow(width, height, title, NULL, NULL);
    }
    
}
