package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.graphics.Graphics;
import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.LogLevel;
import dev.theskidster.mapeditor.main.Logger;
import dev.theskidster.mapeditor.main.ShaderProgram;
import static dev.theskidster.mapeditor.world.World.CELL_SIZE;
import java.nio.BufferOverflowException;
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

final class Cube {

    private final int vao = glGenVertexArrays();
    private final int vbo = glGenBuffers();
    private final int ibo = glGenBuffers();
    
    private boolean dataChanged;
    
    private Graphics g = new Graphics();
    
    Map<Integer, Vertex> vertices;
    Map<Integer, Face> faces;
    
    Cube(float xLoc, float zLoc) {
        //TODO: refactor this class, rename to geometry, delete old geometry and batch classes
        
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
    }
    
    void update() {}
    
    void render(ShaderProgram program) {
        g.vertices = MemoryUtil.memAllocFloat(40 * Float.BYTES);
        g.indices  = MemoryUtil.memAllocInt(36 * Float.BYTES);

        glBindVertexArray(g.vao);

        glBindBuffer(GL_ARRAY_BUFFER, g.vbo);
        glBufferData(GL_ARRAY_BUFFER, g.vertices.capacity(), GL_DYNAMIC_DRAW);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, g.ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, g.indices.capacity(), GL_DYNAMIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, (5 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
            
        try {
            for(int v = 0; v < 8; v++) {
                Vector3f vertexPos = vertices.get(v).position;
                Vector2f texCoords = vertices.get(v).texCoords;

                g.vertices.put(vertexPos.x).put(vertexPos.y).put(vertexPos.z).put(texCoords.x).put(texCoords.y);
            }
            
            faces.forEach((index, face) -> {
                g.indices.put(face.indices[0]).put(face.indices[1]).put(face.indices[2]);
            });
        } catch(BufferOverflowException e) {
            Logger.setStackTrace(e);
            Logger.log(LogLevel.SEVERE, "Geometry buffer experienced overflow");
        }
        
        g.vertices.flip();
        g.indices.flip();
        
        glBindVertexArray(g.vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, g.vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, g.vertices);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, g.ibo);        
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, g.indices);
        
        program.setUniform("uType", 2);
        
        glDrawElements(GL_TRIANGLES, g.indices.limit(), GL_UNSIGNED_INT, NULL);
        
        App.checkGLError();
        
        g.vertices.clear();
        g.indices.clear();
    }
    
    public void test() {
        vertices.put(0, new Vertex(0, 3, 0, 0, 0));
        dataChanged = true;
    }
    
}