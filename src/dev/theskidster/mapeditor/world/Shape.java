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
    
    private final int FLOATS_PER_VERTEX = 8;
    private int numVertices;
    private int bufferSizeInBytes;
    int height = 1;
    
    private final int vao = glGenVertexArrays();
    private final int vbo = glGenBuffers();
    
    private boolean updateData = true;
    
    Vector3f position;
    private Matrix3f normal      = new Matrix3f();
    private Matrix4f modelMatrix = new Matrix4f();
    private Texture texture;
    
    private Map<Integer, Vector3f> vertexPositions;
    private Map<Integer, Vector2f> texCoords;
    private Map<Integer, Vector3f> normals;
    private Map<Integer, Face> faces;
    
    private final Vector3f[] initialVPs;
    
    Shape(float x, float y, float z) {
        position = new Vector3f(x, y, z);
        
        vertexPositions = new TreeMap<>() {{
            put(0, new Vector3f(0,         0,         CELL_SIZE));
            put(1, new Vector3f(CELL_SIZE, 0,         CELL_SIZE));
            put(2, new Vector3f(CELL_SIZE, CELL_SIZE, CELL_SIZE));
            put(3, new Vector3f(0,         CELL_SIZE, CELL_SIZE));
            put(4, new Vector3f(0,         0,         0));
            put(5, new Vector3f(CELL_SIZE, 0,         0));
            put(6, new Vector3f(CELL_SIZE, CELL_SIZE, 0));
            put(7, new Vector3f(0,         CELL_SIZE, 0));
        }};
        
        initialVPs = new Vector3f[vertexPositions.size()];
        
        for(int i = 0; i < initialVPs.length; i++) {
            initialVPs[i] = new Vector3f(vertexPositions.get(i));
        }
        
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
            put(0,  new Face(new int[]{0, 1, 2}, new int[]{2, 3, 0}, new int[]{2, 2, 2}));
            put(1,  new Face(new int[]{2, 3, 0}, new int[]{0, 1, 2}, new int[]{2, 2, 2}));
            //RIGHT:
            put(2,  new Face(new int[]{1, 5, 6}, new int[]{2, 3, 0}, new int[]{0, 0, 0}));
            put(3,  new Face(new int[]{6, 2, 1}, new int[]{0, 1, 2}, new int[]{0, 0, 0}));
            //BACK:
            put(4,  new Face(new int[]{7, 6, 5}, new int[]{0, 1, 2}, new int[]{5, 5, 5}));
            put(5,  new Face(new int[]{5, 4, 7}, new int[]{2, 3, 0}, new int[]{5, 5, 5}));
            //LEFT:
            put(6,  new Face(new int[]{4, 0, 3}, new int[]{2, 3, 0}, new int[]{3, 3, 3}));
            put(7,  new Face(new int[]{3, 7, 4}, new int[]{0, 1, 2}, new int[]{3, 3, 3}));
            //BOTTOM:
            put(8,  new Face(new int[]{4, 5, 1}, new int[]{2, 3, 0}, new int[]{4, 4, 4}));
            put(9,  new Face(new int[]{1, 0, 4}, new int[]{0, 1, 2}, new int[]{4, 4, 4}));
            //TOP:
            put(10, new Face(new int[]{3, 2, 6}, new int[]{2, 3, 0}, new int[]{1, 1, 1}));
            put(11, new Face(new int[]{6, 7, 3}, new int[]{0, 1, 2}, new int[]{1, 1, 1}));
        }};
        
        findBufferSize();
        
        /*
        TODO:
        
        Might remove the model matrix from rendering and instead use it for 
        making calculations on the data that will be used to generate the shapes
        vertices.
        
        This could be useful for when the time comes to implement 
        translating/rotating/scaling of shapes.
        
        - Add face & vertex (position) selection
        - Add face & vertex (position) creation/deletion
        - Add texture coordinate manipulation
        - Add face & vertex (position) manipulation
        - Find whether the cube is convex (this will be used to determine collision elegability)
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
    
    void resetVertexPos(int index, String axis) {
        float value;
        
        switch(axis) {
            case "x", "X" -> value = initialVPs[index].x;
            case "y", "Y" -> value = initialVPs[index].y;
            case "z", "Z" -> value = initialVPs[index].z;
            default -> { return; }
        }
        
        setVertexPos(index, axis, value);
    }
    
    void resetVertexPos() {
        boolean alreadyReset = true;
        
        for(int i = 0; i < initialVPs.length; i++) {
            alreadyReset = initialVPs[i].equals(vertexPositions.get(i));
            if(!alreadyReset) break;
        }
        
        if(!alreadyReset) {
            for(int i = 0; i < initialVPs.length; i++) {
                float yPos = (initialVPs[i].y == CELL_SIZE) ? height : initialVPs[i].y;
                vertexPositions.put(i, new Vector3f(initialVPs[i].x, yPos, initialVPs[i].z));
            }
            
            updateData = true;
        }
    }
    
    Vector3f getVertexPos(int index) { return vertexPositions.get(index); }
    
    void setVertexPos(int index, String axis, float value) {
        switch(axis) {
            case "x", "X" -> vertexPositions.get(index).x = value;
            case "y", "Y" -> vertexPositions.get(index).y = value;
            case "z", "Z" -> vertexPositions.get(index).z = value;
        }
        
        updateData = true;
    }
    
    void setVertexPos(int index, float x, float y, float z) {
        vertexPositions.get(index).set(x - position.x, y, z - position.z);
        updateData = true;
    }
    
}