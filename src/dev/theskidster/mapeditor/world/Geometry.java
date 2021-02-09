package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.LogLevel;
import dev.theskidster.mapeditor.main.Logger;
import dev.theskidster.mapeditor.main.ShaderProgram;
import static dev.theskidster.mapeditor.world.World.CELL_SIZE;
import java.nio.BufferOverflowException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author J Hoffman
 * Created: Feb 5, 2021
 */

final class Geometry {

    int height = 1;
    
    private final int FLOATS_PER_VERTEX = 5;
    private final int FLOATS_PER_FACE   = 3;
    
    private final int vao = glGenVertexArrays();
    private final int vbo = glGenBuffers();
    private final int ibo = glGenBuffers();
    
    private boolean updateData = true;
    
    private FloatBuffer vertexBuf;
    private IntBuffer indexBuf;
    
    Map<Integer, Vertex> vertices;
    Map<Integer, Face> faces;
    
    private final Vector3f[] initialVertexPositions;
    
    Geometry(float xLoc, float zLoc) {        
        vertices = new HashMap<>() {{
            //FRONT:
            put(0, new Vertex(xLoc,             0,         zLoc + CELL_SIZE, 0, 0));
            put(1, new Vertex(xLoc + CELL_SIZE, 0,         zLoc + CELL_SIZE, 0, 0));
            put(2, new Vertex(xLoc + CELL_SIZE, CELL_SIZE, zLoc + CELL_SIZE, 0, 0));
            put(3, new Vertex(xLoc,             CELL_SIZE, zLoc + CELL_SIZE, 0, 0));
            //BACK:
            put(4, new Vertex(xLoc,             0,         zLoc, 0, 0));
            put(5, new Vertex(xLoc + CELL_SIZE, 0,         zLoc, 0, 0));
            put(6, new Vertex(xLoc + CELL_SIZE, CELL_SIZE, zLoc, 0, 0));
            put(7, new Vertex(xLoc,             CELL_SIZE, zLoc, 0, 0));
        }};
        
        faces = new HashMap<>() {{
            //FRONT:
            put(0, new Face(0, 1, 2));
            put(1, new Face(2, 3, 0));
            //RIGHT:
            put(2, new Face(1, 5, 6));
            put(3, new Face(6, 2, 1));
            //BACK:
            put(4, new Face(7, 6, 5));
            put(5, new Face(5, 4, 7));
            //LEFT:
            put(6, new Face(4, 0, 3));
            put(7, new Face(3, 7, 4));
            //BOTTOM:
            put(8, new Face(4, 5, 1));
            put(9, new Face(1, 0, 4));
            //TOP:
            put(10, new Face(3, 2, 6));
            put(11, new Face(6, 7, 3));
        }};
        
        initialVertexPositions = new Vector3f[vertices.size()];
        
        for(int v = 0; v < vertices.size(); v++) {
            initialVertexPositions[v] = new Vector3f(vertices.get(v).position);
        }
    }
    
    void update() {
        if(updateData) {
            vertexBuf = MemoryUtil.memAllocFloat((vertices.size() * FLOATS_PER_VERTEX) * Float.BYTES);
            indexBuf  = MemoryUtil.memAllocInt((faces.size() * FLOATS_PER_FACE) * Float.BYTES);

            glBindVertexArray(vao);

            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, vertexBuf.capacity(), GL_DYNAMIC_DRAW);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuf.capacity(), GL_DYNAMIC_DRAW);

            glVertexAttribPointer(0, 3, GL_FLOAT, false, (FLOATS_PER_VERTEX * Float.BYTES), 0);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, (FLOATS_PER_VERTEX * Float.BYTES), (3 * Float.BYTES));

            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);

            try {
                for(int v = 0; v < 8; v++) {
                    Vector3f vertexPos = vertices.get(v).position;
                    Vector2f texCoords = vertices.get(v).texCoords;

                    vertexBuf.put(vertexPos.x).put(vertexPos.y).put(vertexPos.z).put(texCoords.x).put(texCoords.y);
                }

                faces.forEach((index, face) -> {
                    indexBuf.put(face.indices[0]).put(face.indices[1]).put(face.indices[2]);
                });
            } catch(BufferOverflowException e) {
                Logger.setStackTrace(e);
                Logger.log(LogLevel.SEVERE, "Geometry buffer experienced overflow");
            }
            
            vertexBuf.flip();
            indexBuf.flip();
            
            updateData = false;
        }
    }
    
    void render(ShaderProgram program) {
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertexBuf);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);        
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, indexBuf);
        
        program.setUniform("uType", 2);
        
        glDrawElements(GL_TRIANGLES, indexBuf.capacity(), GL_UNSIGNED_INT, NULL);
        
        App.checkGLError();
    }
    
    float getVertexPos(int index, String axis) {
        if(!vertices.containsKey(index)) {
            Logger.log(LogLevel.WARNING, "No vertex with an ID of: (" + index + ") exists in this shape.");
            return 0;
        }
        
        switch(axis) {
            case "x", "X" -> { return vertices.get(index).position.x; }
            case "y", "Y" -> { return vertices.get(index).position.y; }
            case "z", "Z" -> { return vertices.get(index).position.z; }
            
            default -> { 
                Logger.log(LogLevel.WARNING, "Invalid axis: \"" + axis + "\" value specified, must be one of X, Y, or Z.");
                return 0; 
            }
        }
    }
    
    void setVertexPos(int index, String axis, float value) {
        if(!vertices.containsKey(index)) {
            Logger.log(LogLevel.WARNING, "No vertex with an ID of: (" + index + ") exists in this shape.");
        }
        
        switch(axis) {
            case "x", "X" -> {
                vertices.get(index).position.x = value; 
                updateData = true;
            }
            
            case "y", "Y" -> {
                vertices.get(index).position.y = value;
                updateData = true;
            }
            
            case "z", "Z" -> {
                vertices.get(index).position.z = value;
                updateData = true;
            }
            
            default -> {
                Logger.log(LogLevel.WARNING, "Invalid axis: \"" + axis + "\" value specified, must be one of X, Y, or Z.");
            }
        }
    }
    
    void setVertexPos(int index, float x, float y, float z) {
        if(vertices.containsKey(index)) {
            vertices.get(index).position.set(x, y, z);
            updateData = true;
        } else {
            Logger.log(LogLevel.WARNING, "No vertex with an ID of: (" + index + ") exists in this shape.");
        }
    }
    
    void resetVertexPos(int index, String axis) {
        float value;
        
        switch(axis) {
            case "x", "X" -> { value = initialVertexPositions[index].x; }
            case "y", "Y" -> { value = initialVertexPositions[index].y; }
            case "z", "Z" -> { value = initialVertexPositions[index].z; }
            
            default -> {
                Logger.log(LogLevel.WARNING, "Invalid axis: \"" + axis + "\" value specified, must be one of X, Y, or Z.");
                return;
            }
        }
        
        setVertexPos(index, axis, value);
    }
    
    void resetVertexPos() {
        boolean alreadyReset = true;
        
        for(int v = 0; v < initialVertexPositions.length; v++) {
            alreadyReset = initialVertexPositions[v].equals(vertices.get(v).position);
            if(!alreadyReset) break;
        }
        
        if(!alreadyReset) {
            for(int v = 0; v < initialVertexPositions.length; v++) {
                float yPos = (initialVertexPositions[v].y == CELL_SIZE) ? height : initialVertexPositions[v].y;
                vertices.get(v).position = new Vector3f(initialVertexPositions[v].x, yPos, initialVertexPositions[v].z);
            }
            
            updateData = true;
        }
    }
    
}