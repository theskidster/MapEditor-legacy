package dev.theskidster.mapeditor.main;

import dev.theskidster.mapeditor.ui.UI;
import dev.theskidster.mapeditor.scene.Scene;
import dev.theskidster.mapeditor.ui.WidgetMBEdit;
import dev.theskidster.mapeditor.ui.WidgetMBFile;
import dev.theskidster.mapeditor.ui.WidgetMBLayer;
import dev.theskidster.mapeditor.ui.WidgetMBMap;
import dev.theskidster.mapeditor.ui.WidgetMBView;
import dev.theskidster.mapeditor.util.Event;
import static dev.theskidster.mapeditor.util.Event.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.opengl.GL;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author J Hoffman
 * Created: Dec 15, 2020
 */

/**
 * Encapsulates all program state into a single convenient static class.
 */
public final class App {
    
    private static boolean vSync = true; //TODO: pull these values in from prefrences file
    static boolean glReady;
    
    public static final String VERSION = "0.7.0";
    
    private Monitor monitor;
    private Window window;
    private static ShaderProgram worldProgram;
    private ShaderProgram uiProgram;
    private Camera camera;
    private Scene scene;
    private static UI ui;
    
    private Event currEvent;
    private static Queue<Event> events = new LinkedList<>();
    
    /**
     * Initializes application dependencies and enters a loop that will terminate once the user decides to exit.
     */
    void start() {
        glfwInit();
        
        monitor = new Monitor();
        window  = new Window("RGM Editor v" + VERSION, monitor);
        
        glReady = glInit();
        
        glClearColor(0.5f, 0.5f, 0.5f, 0);
        
        ui = new UI(window);
        window.show(monitor);
        Logger.printSystemInfo();
        
        final double TARGET_DELTA = 1 / 60.0;
        double currTime;
        double prevTime = glfwGetTime();
        double delta = 0;
        boolean ticked;
        
        while(!glfwWindowShouldClose(window.handle)) {
            currTime = glfwGetTime();
            
            delta += currTime - prevTime;
            if(delta < TARGET_DELTA && vSync) delta = TARGET_DELTA;
            
            prevTime = currTime;
            ticked   = false;
            
            while(delta >= TARGET_DELTA) {
                delta   -= TARGET_DELTA;
                ticked  = true;
                
                window.pollInput(ui);
                
                camera.update(window.width, window.height);
                scene.update();
                ui.update(window);
                
                pollEvents();
            }
            
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            worldProgram.use();
            camera.render();
            scene.render();
            
            uiProgram.use();
            ui.render(window, uiProgram);
            
            glfwSwapBuffers(window.handle);
            
            if(!ticked) {
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        glDeleteProgram(worldProgram.handle);
        GL.destroy();
        Logger.close();
        glfwTerminate();
    }
    
    /**
     * Initializes the graphics API and establishes the graphics pipeline using a custom {@linkplain ShaderProgram shader program}.
     */
    private boolean glInit() {
        glfwMakeContextCurrent(window.handle);
        GL.createCapabilities();
        
        //Initialize UI shader
        {
            var shaderSourceFiles = new ArrayList<ShaderSource>() {{
                add(new ShaderSource("uiVertex.glsl", GL_VERTEX_SHADER));
                add(new ShaderSource("uiFragment.glsl", GL_FRAGMENT_SHADER));
            }};
            
            uiProgram = new ShaderProgram(shaderSourceFiles);
            uiProgram.use();
            
            uiProgram.addUniform(ShaderBufferType.INT, "uTexture");
            uiProgram.addUniform(ShaderBufferType.MAT4, "uProjection");
        }
        
        //Initialize world shader
        {
            var shaderSourceFiles = new ArrayList<ShaderSource>() {{
                add(new ShaderSource("worldVertex.glsl", GL_VERTEX_SHADER));
                add(new ShaderSource("worldFragment.glsl", GL_FRAGMENT_SHADER));
            }};
        
            worldProgram = new ShaderProgram(shaderSourceFiles);
            worldProgram.use();
            
            worldProgram.addUniform(ShaderBufferType.MAT4, "uModel");
            worldProgram.addUniform(ShaderBufferType.MAT4, "uView");
            worldProgram.addUniform(ShaderBufferType.MAT4, "uProjection");
            worldProgram.addUniform(ShaderBufferType.INT, "uType");
        }
        
        camera = new Camera(window.width, window.height);
        scene  = new Scene();
        
        return true;
    }
    
    private void pollEvents() {
        if(events.size() > 0) {
            currEvent = events.peek();
            
            if(!currEvent.resolved) {
                switch(currEvent.type) {
                    case WIDGET_FILE:
                        ui.addWidget("File", new WidgetMBFile());
                        currEvent.resolved = true;
                        break;
                        
                    case WIDGET_EDIT:
                        ui.addWidget("Edit", new WidgetMBEdit());
                        currEvent.resolved = true;
                        break;
                        
                    case WIDGET_VIEW:
                        ui.addWidget("View", new WidgetMBView());
                        currEvent.resolved = true;
                        break;
                        
                    case WIDGET_MAP:
                        ui.addWidget("Map", new WidgetMBMap());
                        currEvent.resolved = true;
                        break;
                        
                    case WIDGET_LAYER:
                        ui.addWidget("Layer", new WidgetMBLayer());
                        currEvent.resolved = true;
                        break;
                }
            } else {
                events.poll();
            }
        }
    }
    
    static void resetMenuBar() {
        ui.resetMenuBar();
    }
    
    static boolean getMenuBarActive() {
        return ui.getMenuBarActive();
    }
    
    /**
     * 
     * 
     * @param name
     * @param value 
     */
    public static void setUniform(String name, int value) {
        worldProgram.setUniform(name, value);
    }
    
    /**
     * 
     * @param name
     * @param value 
     */
    public static void setUniform(String name, float value) {
        worldProgram.setUniform(name, value);
    }
    
    /**
     * 
     * @param name
     * @param value 
     */
    public static void setUniform(String name, Vector2f value) {
        worldProgram.setUniform(name, value);
    }
    
    /**
     * 
     * @param name
     * @param value 
     */
    public static void setUniform(String name, Vector3f value) {
        worldProgram.setUniform(name, value);
    }
    
    /**
     * 
     * @param name
     * @param transpose
     * @param value 
     */
    public static void setUniform(String name, boolean transpose, Matrix3f value) {
        worldProgram.setUniform(name, transpose, value);
    }
    
    /**
     * 
     * @param name
     * @param transpose
     * @param value 
     */
    public static void setUniform(String name, boolean transpose, Matrix4f value) {
        worldProgram.setUniform(name, transpose, value);
    }
    
    public static void addEvent(Event event) {
        events.add(event);
    }
    
    /**
     * 
     */
    public static void checkGLError() {
        int glError = glGetError();
        
        if(glError != GL_NO_ERROR) {
            String desc = "";
            
            switch(glError) {
                case GL_INVALID_ENUM:      desc = "invalid enum";      break;
                case GL_INVALID_VALUE:     desc = "invalid value";     break;
                case GL_INVALID_OPERATION: desc = "invalid operation"; break;
                case GL_STACK_OVERFLOW:    desc = "stack overflow";    break;
                case GL_STACK_UNDERFLOW:   desc = "stack underflow";   break;
                case GL_OUT_OF_MEMORY:     desc = "out of memory";     break;
            }
            
            Logger.log(LogLevel.SEVERE, "OpenGL Error: (" + glError + ") " + desc);
        }
    }
    
    //TODO: delet this
    public static void checkShaderError(int handle, String filename) {
        if(glGetShaderi(handle, GL_COMPILE_STATUS) != GL_TRUE) {
            Logger.log(LogLevel.SEVERE, "Failed to compile GLSL file: \"" + filename + "\" " + glGetShaderInfoLog(handle));
        }
    }
    
}
