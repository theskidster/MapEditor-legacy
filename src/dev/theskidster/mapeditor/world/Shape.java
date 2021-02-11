package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.graphics.LightSource;
import dev.theskidster.mapeditor.graphics.Texture;
import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.LogLevel;
import dev.theskidster.mapeditor.main.Logger;
import dev.theskidster.mapeditor.main.ShaderProgram;
import static dev.theskidster.mapeditor.world.World.CELL_SIZE;
import java.nio.BufferOverflowException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Feb 9, 2021
 */

final class Shape {
    
    private final class Face2 {
        int[] vp = new int[3];
        int[] tc = new int[3];
        int[] n  = new int[3];
        
        Face2(int[] vp, int[] tc, int[] n) {
            this.vp = vp;
            this.tc = tc;
            this.n  = n;
        }
    }
    
    private final int FLOATS_PER_VERTEX = 8;
    private int numVertices;
    private int bufferSizeInBytes;
    
    private final int vao = glGenVertexArrays();
    private final int vbo = glGenBuffers();
    
    private boolean updateData = true;
    
    Vector3f position;
    private Matrix3f normal      = new Matrix3f();
    private Matrix4f modelMatrix = new Matrix4f();
    
    private Map<Integer, Vector3f> vertexPositions;
    private Map<Integer, Vector2f> texCoords;
    private Map<Integer, Vector3f> normals;
    private Map<Integer, Face2> faces;
    
    private Texture texture;
    
    Shape(Vector3f position) {
        this.position = position;
        
        vertexPositions = new HashMap<>() {{
            float halfSize = CELL_SIZE / 2;
            
            put(0, new Vector3f(-halfSize, -halfSize,  halfSize));
            put(1, new Vector3f( halfSize, -halfSize,  halfSize));
            put(2, new Vector3f( halfSize,  halfSize,  halfSize));
            put(3, new Vector3f(-halfSize,  halfSize,  halfSize));
            put(4, new Vector3f(-halfSize, -halfSize, -halfSize));
            put(5, new Vector3f( halfSize, -halfSize, -halfSize));
            put(6, new Vector3f( halfSize,  halfSize, -halfSize));
            put(7, new Vector3f(-halfSize,  halfSize, -halfSize));
        }};
        
        texCoords = new HashMap<>() {{
            put(0, new Vector2f(0, 0));
            put(1, new Vector2f(1, 0));
            put(2, new Vector2f(1, 1));
            put(3, new Vector2f(0, 1));
        }};
        
        normals = new HashMap<>() {{
            put(0, new Vector3f( 1,  0,  0));
            put(1, new Vector3f( 0,  1,  0));
            put(2, new Vector3f( 0,  0,  1));
            put(3, new Vector3f(-1,  0,  0));
            put(4, new Vector3f( 0, -1,  0));
            put(5, new Vector3f( 0,  0, -1));
        }};
        
        faces = new TreeMap<>() {{
            //FRONT:
            put(0,  new Face2(new int[]{0, 1, 2}, new int[]{2, 3, 0}, new int[]{2, 2, 2}));
            put(1,  new Face2(new int[]{2, 3, 0}, new int[]{0, 1, 2}, new int[]{2, 2, 2}));
            //RIGHT:
            put(2,  new Face2(new int[]{1, 5, 6}, new int[]{2, 3, 0}, new int[]{0, 0, 0}));
            put(3,  new Face2(new int[]{6, 2, 1}, new int[]{0, 1, 2}, new int[]{0, 0, 0}));
            //BACK:
            put(4,  new Face2(new int[]{7, 6, 5}, new int[]{0, 1, 2}, new int[]{5, 5, 5}));
            put(5,  new Face2(new int[]{5, 4, 7}, new int[]{2, 3, 0}, new int[]{5, 5, 5}));
            //LEFT:
            put(6,  new Face2(new int[]{4, 0, 3}, new int[]{2, 3, 0}, new int[]{3, 3, 3}));
            put(7,  new Face2(new int[]{3, 7, 4}, new int[]{0, 1, 2}, new int[]{3, 3, 3}));
            //BOTTOM:
            put(8,  new Face2(new int[]{4, 5, 1}, new int[]{2, 3, 0}, new int[]{4, 4, 4}));
            put(9,  new Face2(new int[]{1, 0, 4}, new int[]{0, 1, 2}, new int[]{4, 4, 4}));
            //TOP:
            put(10, new Face2(new int[]{3, 2, 6}, new int[]{2, 3, 0}, new int[]{1, 1, 1}));
            put(11, new Face2(new int[]{6, 7, 3}, new int[]{0, 1, 2}, new int[]{1, 1, 1}));
        }};
        
        findBufferSize();
        
        /*
        TODO:
        
        - Add setter methods for vertex attributes
        - Find whether the cube is convex (this will be used to determine collision elegability)
        - Add face & vertex (position) selection
        - Add face & vertex (position) creation/deletion
        - Add texture coordinate manipulation
        - Add face & vertex (position) manipulation
        */
        
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, bufferSizeInBytes, GL_DYNAMIC_DRAW);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (FLOATS_PER_VERTEX * Float.BYTES), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, (FLOATS_PER_VERTEX * Float.BYTES), (3 * Float.BYTES));
        glVertexAttribPointer(2, 3, GL_FLOAT, false, (FLOATS_PER_VERTEX * Float.BYTES), (5 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        
        texture = new Texture("img_null.png");
        
        glBindTexture(GL_TEXTURE_2D, texture.handle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
    private void findBufferSize() {
        numVertices       = faces.size() * 3;
        bufferSizeInBytes = numVertices * FLOATS_PER_VERTEX * Float.BYTES;
    }
    
    void update() {
        normal.set(modelMatrix.invert());
        modelMatrix.translation(position);
        
        if(updateData) {
            try(MemoryStack stack = MemoryStack.stackPush()) {
                findBufferSize();
                
                FloatBuffer vertices = stack.mallocFloat(bufferSizeInBytes);
                
                faces.forEach((index, face) -> {
                    for(int i = 0; i < 3; i++) {
                        Vector3f pos    = vertexPositions.get(face.vp[i]);
                        Vector2f coords = texCoords.get(face.tc[i]);
                        Vector3f norm   = normals.get(face.n[i]);
                        
                        vertices.put(pos.x).put(pos.y).put(pos.z)
                                .put(coords.x).put(coords.y)
                                .put(norm.x).put(norm.y).put(norm.z);
                    }
                });
                
                vertices.flip();
                
                glBindBuffer(GL_ARRAY_BUFFER, vbo);
                glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
                
            } catch(BufferOverflowException e) {
                Logger.setStackTrace(e);
                Logger.log(LogLevel.SEVERE, "Cube buffer experienced overflow");
            }
            
            updateData = false;
        }
    }
    
    void render(ShaderProgram program, LightSource[] lights, int numLights) {
        glBindVertexArray(vao);
        glBindTexture(GL_TEXTURE_2D, texture.handle);
        
        program.setUniform("uType", 4);
        program.setUniform("uModel", false, modelMatrix);
        program.setUniform("uNormal", true, normal);
        program.setUniform("uNumLights", numLights);
        
        for(int i = 0; i < App.MAX_LIGHTS; i++) {
            if(lights[i] != null) {
                if(lights[i].enabled) {
                    program.setUniform("uLights[" + i + "].brightness", lights[i].getBrightness());
                    program.setUniform("uLights[" + i + "].contrast",   lights[i].getContrast());
                    program.setUniform("uLights[" + i + "].position",   lights[i].getPosition());
                    program.setUniform("uLights[" + i + "].ambient",    lights[i].getAmbientColor());
                    program.setUniform("uLights[" + i + "].diffuse",    lights[i].getDiffuseColor());
                } else {
                    program.setUniform("uLights[" + i + "].brightness", 0);
                    program.setUniform("uLights[" + i + "].contrast",   0);
                    program.setUniform("uLights[" + i + "].position",   App.noValue());
                    program.setUniform("uLights[" + i + "].ambient",    App.noValue());
                    program.setUniform("uLights[" + i + "].diffuse",    App.noValue());
                }
            }
        }
        
        glDrawArrays(GL_TRIANGLES, 0, numVertices);
        
        App.checkGLError();
    }
    
}