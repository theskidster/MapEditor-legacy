package dev.theskidster.mapeditor.main;

import dev.theskidster.mapeditor.scene.Scene;
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
    
    public static final String VERSION = "0.5.0";
    
    private Monitor monitor;
    private Window window;
    private static ShaderProgram program;
    private Camera camera;
    private Scene scene;
    
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
        
        camera = new Camera(window.width, window.height);
        scene  = new Scene();
    }
    
    /**
     * Initializes application dependencies and enters a loop that will terminate once the user decides to exit.
     */
    void start() {
        glfwInit();
        
        monitor = new Monitor();
        window  = new Window("RGM Editor", monitor);
        
        glInit();
        
        glViewport(0, 0, window.width, window.height);
        
        window.show(monitor);
        Logger.printSystemInfo();
        
        while(!glfwWindowShouldClose(window.handle)) {
            glfwPollEvents();
            
            glClearColor(1, 1, 1, 0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            camera.update();
            scene.update();
            
            camera.render();
            scene.render();
            
            glfwSwapBuffers(window.handle);
        }
        
        glDeleteProgram(program.handle);
        GL.destroy();
        Logger.close();
        glfwTerminate();
    }
    
    public static void setUniform(String name, int value) {
        glUniform1i(
                program.getUniform(name).location, 
                value);
    }
    
    public static void setUniform(String name, float value) {
        glUniform1f(
                program.getUniform(name).location, 
                value);
    }
    
    public static void setUniform(String name, Vector2f value) {
        glUniform2fv(
                program.getUniform(name).location,
                value.get(program.getUniform(name).asFloatBuffer()));
    }
    
    public static void setUniform(String name, Vector3f value) {
        glUniform3fv(
                program.getUniform(name).location,
                value.get(program.getUniform(name).asFloatBuffer()));
    }
    
    public static void setUniform(String name, boolean transpose, Matrix3f value) {
        glUniformMatrix3fv(
                program.getUniform(name).location,
                transpose,
                value.get(program.getUniform(name).asFloatBuffer()));
    }
    
    public static void setUniform(String name, boolean transpose, Matrix4f value) {
        glUniformMatrix4fv(
                program.getUniform(name).location,
                transpose,
                value.get(program.getUniform(name).asFloatBuffer()));
    }
    
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
    
}
