package dev.theskidster.mapeditor.main;

import static dev.theskidster.mapeditor.main.ShaderBufferType.*;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.lwjgl.opengl.GL20.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Dec 16, 2020
 */

/**
 * Represents a completed shader program that defines how the graphics pipeline should process rendering information sent to the GPU.
 */
final class ShaderProgram {
    
    final int handle;
    
    private final Map<String, ShaderUniform> uniforms;
    private final Map<ShaderBufferType, Integer> bufferSizes;
    
    /**
     * Creates a new shader program with the code supplied from a collection of compiled .glsl source files.
     * 
     * @param shaders the objects representing .glsl source code describing various stages of rendering
     */
    ShaderProgram(List<ShaderSource> shaders) {
        handle   = glCreateProgram();
        uniforms = new HashMap<>();
        
        shaders.forEach(shader -> glAttachShader(handle, shader.handle));
        
        glLinkProgram(handle);
        
        bufferSizes = new HashMap<>() {{
            put(VEC2, 2);
            put(VEC3, 3);
            put(MAT3, 3);
            put(MAT4, 4);
        }};
    }
    
    ShaderUniform getUniform(String name) { return uniforms.get(name); }
    
    /**
     * Generates a new shader uniform object using the supplied parameters. Added to increase code readability in the 
     * {@linkplain addUniform(ShaderBufferType, String) addUniform()} method.
     * 
     * @param name   the name of the uniform variable exactly as it is typed in the .glsl source file
     * @param buffer the data buffer that this uniform variable will use when sending data to the GPU
     * @return       a new shader uniform object
     */
    private ShaderUniform createUniform(String name, Buffer buffer) {
        return new ShaderUniform(glGetUniformLocation(handle, name), buffer); 
    }
    
    /**
     * Specifies a new uniform variable that this shader program can utilize later to supply the GPU with rendering information.
     * 
     * @param type the data type of the buffer that the uniform variable will use
     * @param name the name of the uniform variable exactly as it is typed in the .glsl source file
     */
    void addUniform(ShaderBufferType type, String name) {
        if(glGetUniformLocation(handle, name) == -1) {
            Logger.log(LogLevel.SEVERE, "Failed to find uniform: \"" + "\". Check variable name or .glsl file where it's declared.");
            return;
        }
        
        /**
         * If the uniform variable returns -1 the application will terminate
         * before ever reaching this statement.
         */
        try(MemoryStack stack = MemoryStack.stackPush()) {
            switch(type) {
                case INT:
                    uniforms.put(name, createUniform(name, stack.mallocInt(1)));
                    break;

                case FLOAT:
                    uniforms.put(name, createUniform(name, stack.mallocFloat(1)));
                    break;

                case VEC2: case VEC3:
                    uniforms.put(name, createUniform(name, stack.mallocFloat(bufferSizes.get(type))));
                    break;

                case MAT3: case MAT4:
                    uniforms.put(name, createUniform(name, stack.mallocFloat(bufferSizes.get(type) * Float.BYTES)));
                    break;
            }
        }
    }
    
}