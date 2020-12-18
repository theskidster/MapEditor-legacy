package dev.theskidster.mapeditor.main;

import java.util.ArrayList;
import java.util.List;
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
    
    public static final String VERSION = "0.3.0";
    
    private Monitor monitor;
    private Window window;
    private ShaderProgram program;
    
    /**
     * Initializes the graphics API and establishes the graphics pipeline using a custom {@linkplain ShaderProgram shader program}.
     */
    private void glInit() {
        glfwMakeContextCurrent(window.handle);
        GL.createCapabilities();
        
        List<ShaderSource> shaderSourceFiles = new ArrayList<>() {{
            add(new ShaderSource("vertex.glsl", GL_VERTEX_SHADER));
            add(new ShaderSource("fragment.glsl", GL_FRAGMENT_SHADER));
        }};
        
        program = new ShaderProgram(shaderSourceFiles);
        glUseProgram(program.handle);
        
        program.addUniform(ShaderBufferType.MAT4, "uModel");
        program.addUniform(ShaderBufferType.MAT4, "uView");
        program.addUniform(ShaderBufferType.MAT4, "uProjection");
        program.addUniform(ShaderBufferType.INT, "uType");
    }
    
    /**
     * Initializes application dependencies and enters a loop that will terminate once the user decides to exit.
     */
    void start() {
        glfwInit();
        
        monitor = new Monitor();
        window  = new Window("RGM Editor", monitor);
        
        glInit();
        
        glViewport(0, 0, window.width / 2, window.height / 2);
        
        window.show(monitor);
        Logger.printSystemInfo();
        
        while(!glfwWindowShouldClose(window.handle)) {
            glfwPollEvents();
            
            glClearColor(0, 0, 1, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            glfwSwapBuffers(window.handle);
        }
        
        glDeleteProgram(program.handle);
        GL.destroy();
        Logger.close();
        glfwTerminate();
    }
    
    public void setUniform(String name, int value) {
        
    }
    
    public void setUniform(String name, float value) {
        
    }
    
    public void setUniform(String name, Vector2f value) {
        
    }
    
    public void setUniform(String name, Vector3f value) {
        
    }
    
    public void setUniform(String name, Matrix3f value) {
        
    }
    
    public void setUniform(String name, Matrix4f value) {
        
    }
    
}
