package dev.theskidster.mapeditor.main;

import dev.theskidster.mapeditor.ui.UI;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkVec2;
import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.stb.STBImage.*;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * @author J Hoffman
 * Created: Dec 15, 2020
 */

public final class Window {

    public int width;
    public int height;
    
    public final long handle;
    
    String title;
    Vector2i position;
    
    Window(String title, Monitor monitor) {
        this.title = title;
        
        //TODO: pull these values in from prefrences file
        this.width  = 1480;
        this.height = 960;
        
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
        try(MemoryStack stack = MemoryStack.stackPush()) {
            InputStream file = App.class.getResourceAsStream("/dev/theskidster/mapeditor/assets/" + filename);
            byte[] data      = file.readAllBytes();
            
            IntBuffer widthBuf   = stack.mallocInt(1);
            IntBuffer heightBuf  = stack.mallocInt(1);
            IntBuffer channelBuf = stack.mallocInt(1);
            
            ByteBuffer icon = stbi_load_from_memory(
                    stack.malloc(data.length).put(data).flip(),
                    widthBuf,
                    heightBuf,
                    channelBuf,
                    STBI_rgb_alpha);
            
            glfwSetWindowIcon(handle, GLFWImage.mallocStack(1, stack)
                    .width(widthBuf.get())
                    .height(heightBuf.get())
                    .pixels(icon));
            
            stbi_image_free(icon);
            
        } catch(IOException e) {
            Logger.setStackTrace(e);
            Logger.log(LogLevel.WARNING, "Failed to set window icon: \"" + filename + "\"");
        }
    }
    
    /**
     * Displays the window and establishes its input callback events.
     * 
     * @param monitor the monitor to display this window on
     */
    void show(Monitor monitor) {
        setWindowIcon("img_logo.png");
        
        glfwSetWindowMonitor(handle, NULL, position.x, position.y, width, height, monitor.refreshRate);
        glfwSetWindowPos(handle, position.x, position.y);
        glfwSwapInterval(1);
        glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        glfwShowWindow(handle);
    }
    
    void pollInput(UI ui) {        
        ui.beginInput();
        glfwPollEvents();
        ui.endInput(handle);
    }
    
    public void setCallbacks(NkContext nkContext) {
        glfwSetWindowSizeCallback(handle, (window, w, h) -> {
            width  = w;
            height = h;
            
            if(App.glReady) glViewport(0, 0, width, height);
        });
        
        glfwSetScrollCallback(handle, (window, xOffset, yOffset) -> {
            try(MemoryStack stack = MemoryStack.stackPush()) {
                NkVec2 scroll = NkVec2.mallocStack(stack)
                        .x((float) xOffset)
                        .y((float) yOffset);
                
                nk_input_scroll(nkContext, scroll);
            }
        });
        
        glfwSetCharCallback(handle, (window, codepoint) -> nk_input_unicode(nkContext, codepoint));
        
        glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
            boolean press = action == GLFW_PRESS;
            
            switch(key) {                    
                case GLFW_KEY_DELETE:
                    nk_input_key(nkContext, NK_KEY_DEL, press);
                    break;
                    
                case GLFW_KEY_ENTER:
                    nk_input_key(nkContext, NK_KEY_ENTER, press);
                    break;
                    
                case GLFW_KEY_TAB:
                    nk_input_key(nkContext, NK_KEY_TAB, press);
                    break;
                    
                case GLFW_KEY_BACKSPACE:
                    nk_input_key(nkContext, NK_KEY_BACKSPACE, press);
                    break;
                    
                case GLFW_KEY_UP:
                    nk_input_key(nkContext, NK_KEY_UP, press);
                    break;
                    
                case GLFW_KEY_DOWN:
                    nk_input_key(nkContext, NK_KEY_DOWN, press);
                    break;
                    
                case GLFW_KEY_LEFT_SHIFT:
                case GLFW_KEY_RIGHT_SHIFT:
                    nk_input_key(nkContext, NK_KEY_SHIFT, press);
                    break;
                    
                case GLFW_KEY_LEFT_CONTROL:
                case GLFW_KEY_RIGHT_CONTROL:
                    if(press) {
                        nk_input_key(nkContext, NK_KEY_COPY, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_PASTE, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_CUT, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_TEXT_UNDO, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_TEXT_REDO, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_TEXT_WORD_LEFT, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_TEXT_WORD_RIGHT, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_TEXT_LINE_START, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_TEXT_LINE_END, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                    } else {
                        nk_input_key(nkContext, NK_KEY_LEFT, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_RIGHT, glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS);
                        nk_input_key(nkContext, NK_KEY_COPY, false);
                        nk_input_key(nkContext, NK_KEY_PASTE, false);
                        nk_input_key(nkContext, NK_KEY_CUT, false);
                        nk_input_key(nkContext, NK_KEY_SHIFT, false);
                    }
                    break;
            }
        });
        
        glfwSetCursorPosCallback(handle, (window, xPos, yPos) -> nk_input_motion(nkContext, (int) xPos, (int) yPos));
        
        glfwSetMouseButtonCallback(handle, (window, button, action, mods) -> {
            try(MemoryStack stack = MemoryStack.stackPush()) {
                DoubleBuffer xPosBuf = stack.mallocDouble(1);
                DoubleBuffer yPosBuf = stack.mallocDouble(1);
                
                glfwGetCursorPos(window, xPosBuf, yPosBuf);
                
                int nkButton;
                
                switch(button) {
                    case GLFW_MOUSE_BUTTON_RIGHT:  nkButton = NK_BUTTON_RIGHT;  break;
                    case GLFW_MOUSE_BUTTON_MIDDLE: nkButton = NK_BUTTON_MIDDLE; break;
                    default:                       nkButton = NK_BUTTON_LEFT;
                }
                
                nk_input_button(nkContext, nkButton, (int) xPosBuf.get(0), (int) yPosBuf.get(0), action == GLFW_PRESS);
                
                if(nkButton != -1 && action == GLFW_PRESS && UI.getMenuBarClicked()) {
                    UI.resetMenuBarState();
                }
            }
        });
    }
    
}
