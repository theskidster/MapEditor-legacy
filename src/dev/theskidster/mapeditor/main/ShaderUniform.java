package dev.theskidster.mapeditor.main;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * @author J Hoffman
 * Created: Dec 16, 2020
 */

/**
 * Provides a type-neutral structure that can be used to couple a pointer assigned to represent a shader uniform variable by the graphics API to its 
 * corresponding data buffer. Once stored, this buffer can be retrieved as the type needed by the implementation.
 */
final class ShaderUniform {

    final int location;
    private Buffer buffer;
    
    /**
     * Creates an association between a pointer and data buffer and provides it as an object.
     * 
     * @param location the unique number provided by OpenGL which is used to identify a shader uniform variable
     * @param buffer   a type-neutral data buffer that will be used to supply the GPU with rendering information during runtime
     */
    ShaderUniform(int location, Buffer buffer) {
        this.location = location;
        this.buffer   = buffer;
    }
    
    IntBuffer asIntBuffer()     { return (IntBuffer) buffer; }
    FloatBuffer asFloatBuffer() { return (FloatBuffer) buffer; }
    
}