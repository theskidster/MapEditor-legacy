package dev.theskidster.mapeditor.main;

import dev.theskidster.mapeditor.scene.Scene;
import dev.theskidster.mapeditor.ui.FrameNewMap;
import dev.theskidster.mapeditor.ui.UI;
import dev.theskidster.mapeditor.util.Color;
import dev.theskidster.mapeditor.util.Event;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
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
    
    public static final int SELECT_TOOL   = 0;
    public static final int GEOMETRY_TOOL = 1;
    public static final int VOLUME_TOOL   = 2;
    public static final int TRIGGER_TOOL  = 3;
    public static final int LIGHT_TOOL    = 4;
    public static final int MAX_LIGHTS = 32;
    private static int tickCount = 0;
    
    private static boolean vSync = true; //TODO: pull these values in from prefrences file
    static boolean glReady;
    
    public static final String VERSION = "0.7.0";
    
    private Monitor monitor;
    private static Window window;
    private static ShaderProgram sceneProgram;
    private ShaderProgram uiProgram;
    private Camera camera;
    private Scene scene;
    private UI ui;
    private static final Vector3f noValue = new Vector3f();
    
    private static final Queue<Event> events = new LinkedList<>();
    
    /**
     * Initializes application dependencies and enters a loop that will terminate once the user decides to exit.
     */
    void start() {
        glfwInit();
        
        monitor = new Monitor();
        window  = new Window("RGM Editor v" + VERSION, monitor);
        
        glReady = glInit();
        
        setClearColor(Color.RGM_NAVY);
        
        window.show(monitor, ui, camera, scene);
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
                delta    -= TARGET_DELTA;
                ticked    = true;
                tickCount = (tickCount == Integer.MAX_VALUE) ? 0 : tickCount + 1;
                
                glfwPollEvents();
                pollEvents();
                
                camera.update(window.width, window.height);
                scene.update(camera.ray, ui.getToolID());
                ui.update();
            }
            
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            sceneProgram.use();
            camera.render(sceneProgram);
            scene.render(sceneProgram, camera.position, camera.up);
            
            uiProgram.use();
            ui.render(uiProgram);
            
            glfwSwapBuffers(window.handle);
            
            if(!ticked) {
                try {
                    Thread.sleep(1);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        
        glDeleteProgram(sceneProgram.handle);
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
            
            uiProgram.addUniform(ShaderBufferType.INT,  "uType");
            uiProgram.addUniform(ShaderBufferType.VEC2, "uTexCoords");
            uiProgram.addUniform(ShaderBufferType.VEC2, "uPosition");
            uiProgram.addUniform(ShaderBufferType.VEC3, "uFontColor");
            uiProgram.addUniform(ShaderBufferType.MAT4, "uProjection");
        }
        
        //Initialize scene shader
        {
            var shaderSourceFiles = new ArrayList<ShaderSource>() {{
                add(new ShaderSource("sceneVertex.glsl", GL_VERTEX_SHADER));
                add(new ShaderSource("sceneFragment.glsl", GL_FRAGMENT_SHADER));
            }};
        
            sceneProgram = new ShaderProgram(shaderSourceFiles);
            sceneProgram.use();
            
            sceneProgram.addUniform(ShaderBufferType.INT, "uType");
            sceneProgram.addUniform(ShaderBufferType.INT, "uNumLights");
            sceneProgram.addUniform(ShaderBufferType.MAT4,"uModel");
            sceneProgram.addUniform(ShaderBufferType.MAT4,"uView");
            sceneProgram.addUniform(ShaderBufferType.MAT4,"uProjection");
            sceneProgram.addUniform(ShaderBufferType.VEC3,"uColor");
            sceneProgram.addUniform(ShaderBufferType.MAT3,"uNormal");
            
            for(int i = 0; i < MAX_LIGHTS; i++) {
                sceneProgram.addUniform(ShaderBufferType.FLOAT, "uLights[" + i + "].brightness");
                sceneProgram.addUniform(ShaderBufferType.FLOAT, "uLights[" + i + "].contrast");
                sceneProgram.addUniform(ShaderBufferType.VEC3,  "uLights[" + i + "].position");
                sceneProgram.addUniform(ShaderBufferType.VEC3,  "uLights[" + i + "].ambient");
                sceneProgram.addUniform(ShaderBufferType.VEC3,  "uLights[" + i + "].diffuse");
            }
        }
        
        camera = new Camera();
        scene  = new Scene(16, 32, 16, "img_null.png");
        ui     = new UI();
        
        camera.update(window.width, window.height);
        camera.render(sceneProgram);
        
        return true;
    }
    
    private void pollEvents() {
        if(events.size() > 0) {
            Event event = events.peek();
            
            if(!event.resolved) {
                switch(event.type) {
                    case Event.WIDGET_NEW_MAP -> {
                        ui.addWidget("New Map", new FrameNewMap(window.width, window.height));
                        event.resolved = true;
                    }
                }
            } else {
                events.poll();
            }
        }
    }
    
    /**
     * 
     */
    public static void checkGLError() {
        int glError = glGetError();
        
        if(glError != GL_NO_ERROR) {
            String desc = "";
            
            switch(glError) {
                case GL_INVALID_ENUM      -> desc = "invalid enum";
                case GL_INVALID_VALUE     -> desc = "invalid value";
                case GL_INVALID_OPERATION -> desc = "invalid operation";
                case GL_STACK_OVERFLOW    -> desc = "stack overflow";
                case GL_STACK_UNDERFLOW   -> desc = "stack underflow";
                case GL_OUT_OF_MEMORY     -> desc = "out of memory";
            }
            
            Logger.log(LogLevel.SEVERE, "OpenGL Error: (" + glError + ") " + desc);
        }
    }
    
    public static void end() {
        glfwSetWindowShouldClose(window.handle, true);
    }
    
    public static void addEvent(Event event) {
        events.add(event);
    }
    
    public static void setClearColor(Color color) {
        glClearColor(color.r, color.g, color.b, 0);
    }
    
    public static boolean tick(int cycles) {
        return tickCount % cycles == 0;
    }
    
    public static Vector3f noValue() {
        return noValue;
    }
    
}
