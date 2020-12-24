package dev.theskidster.mapeditor.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author J Hoffman
 * Created: Dec 16, 2020
 */

/**
 * These objects define how data will be processed by a single stage of the graphics pipeline by parsing a .glsl source file and providing its compiled 
 * contents to a {@link ShaderProgram}.
 */
final class ShaderSource {

    final int handle;
    
    /**
     * Parses code from .glsl file and provides it as an object that can be used to represent a single stage of a larger 
     * {@linkplain ShaderProgram shader program}.
     * 
     * @param filename the name of the file to load. Expects the file extension to be included.
     * @param type     the rendering stage that the code parsed from the file will be used in. One of; 
     *                 {@link org.lwjgl.opengl.GL30#GL_VERTEX_SHADER GL_VERTEX_SHADER}, 
     *                 {@link org.lwjgl.opengl.GL30#GL_FRAGMENT_SHADER GL_FRAGMENT_SHADER}, 
     *                 {@link org.lwjgl.opengl.GL32#GL_GEOMETRY_SHADER GL_GEOMETRY_SHADER}.
     */
    ShaderSource(String filename, int type) {
        StringBuilder builder = new StringBuilder();
        InputStream file      = ShaderSource.class.getResourceAsStream("/dev/theskidster/mapeditor/shaders/" + filename);
        
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(file, "UTF-8"));) {
            String line;
            
            while(((line) = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
        } catch(IOException e) {
            Logger.setStackTrace(e);
            Logger.log(LogLevel.SEVERE, "Failed to parse GLSL file: \"" + filename + "\"");
        }
        
        CharSequence src = builder.toString();
        
        handle = glCreateShader(type);
        glShaderSource(handle, src);
        glCompileShader(handle);
        
        App.checkShaderError(handle, filename);
    }
    
}