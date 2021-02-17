package dev.theskidster.mapeditor.main;

import static dev.theskidster.mapeditor.main.App.*;
import dev.theskidster.mapeditor.ui.UI;
import dev.theskidster.mapeditor.world.World;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWImage;
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
    
    private boolean leftHeld;
    private boolean middleHeld;
    private boolean rightHeld;
    private boolean shiftHeld;
    private boolean ctrlHeld;
    
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
    void show(Monitor monitor, UI ui, Camera camera, World world) {
        setWindowIcon("img_logo.png");
        
        glfwSetWindowMonitor(handle, NULL, position.x, position.y, width, height, monitor.refreshRate);
        glfwSetWindowPos(handle, position.x, position.y);
        glfwSwapInterval(1);
        glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        glfwShowWindow(handle);
        
        //Set initial UI viewport resolution.
        ui.setViewport(width, height);
        
        glfwSetWindowSizeCallback(handle, (window, w, h) -> {
            width  = w;
            height = h;
            
            if(App.glReady) {
                glViewport(0, 0, width, height);
                ui.setViewport(width, height);
            }
        });
        
        glfwSetScrollCallback(handle, (window, xOffset, yOffset) -> {
            camera.dolly((float) yOffset);
        });
        
        glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
            ui.enterText(key, action);
            shiftHeld = (key == GLFW_KEY_LEFT_SHIFT && action == GLFW_PRESS);
            ctrlHeld  = (key == GLFW_KEY_LEFT_CONTROL && action == GLFW_PRESS);
        });
        
        glfwSetCursorPosCallback(handle, (window, xPos, yPos) -> {
            ui.setMousePosition(xPos, yPos);
            
            camera.castRay((float) ((2f * xPos) / width - 1f), (float) (1f - (2f * yPos) / height));
            
            if(ui.getToolSelected(GEOMETRY_TOOL)) world.selectTile(camera.position, camera.ray);
            
            if(leftHeld ^ middleHeld ^ rightHeld) {
                if(leftHeld && ui.getToolSelected(GEOMETRY_TOOL))   {
                    world.stretchShape(camera.rayVerticalChange, ctrlHeld);
                }
                
                if(middleHeld) camera.setPosition(xPos, yPos);
                if(rightHeld)  camera.setDirection(xPos, yPos);
            } else {
                camera.prevX = xPos;
                camera.prevY = yPos;
            }
        });
        
        glfwSetMouseButtonCallback(handle, (window, button, action, mods) -> {
            ui.setMouseAction(button, action);
            if(action == GLFW_PRESS && !ui.getMenuBarActive()) ui.resetMenuBar();
            
            switch(button) {
                case GLFW_MOUSE_BUTTON_LEFT -> {
                    leftHeld = action == GLFW_PRESS;
                    
                    switch(ui.getToolID()) {
                        case SELECT_TOOL -> {
                            world.selectVertices(camera.position, camera.ray, ctrlHeld);
                        }
                        
                        case GEOMETRY_TOOL -> {
                            if(leftHeld) world.addShape();
                            else         world.finalizeShape();
                        }
                    }
                }
                
                case GLFW_MOUSE_BUTTON_MIDDLE -> middleHeld = action == GLFW_PRESS;
                case GLFW_MOUSE_BUTTON_RIGHT  -> rightHeld = action == GLFW_PRESS;
            }
        });
    }
    
}
