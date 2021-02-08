package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.graphics.Graphics;
import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.LogLevel;
import dev.theskidster.mapeditor.main.Logger;
import dev.theskidster.mapeditor.main.ShaderProgram;
import java.nio.BufferOverflowException;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author J Hoffman
 * Created: Feb 2, 2021
 */

class GeometryBatch {
    
    private final int numFloats = 40;
    private final int numFloatsPerVertex = 5;
    private int numVertices;
    private int prevNumShapes;
    
    private final Graphics g = new Graphics();
    
    private void render(ShaderProgram program) {        
        glBindVertexArray(g.vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, g.vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, g.vertices);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, g.ibo);        
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, g.indices);
        
        program.setUniform("uType", 2);
        
        glDrawElements(GL_TRIANGLES, g.indices.limit(), GL_UNSIGNED_INT, NULL);
        
        App.checkGLError();
    }
    
    void batchStart(int numShapes) {
        if(numShapes != prevNumShapes) {
            g.vertices = MemoryUtil.memAllocFloat(numFloats * numShapes * Float.BYTES);
            g.indices  = MemoryUtil.memAllocInt((12 * 3) * numShapes * Float.BYTES);
            
            glBindVertexArray(g.vao);
            
            glBindBuffer(GL_ARRAY_BUFFER, g.vbo);
            glBufferData(GL_ARRAY_BUFFER, g.vertices.capacity(), GL_DYNAMIC_DRAW);
            
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, g.ibo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, g.indices.capacity(), GL_DYNAMIC_DRAW);
            
            glVertexAttribPointer(0, 3, GL_FLOAT, false, (numFloatsPerVertex * Float.BYTES), 0);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, (numFloatsPerVertex * Float.BYTES), (3 * Float.BYTES));
        
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);
            
            prevNumShapes = numShapes;
        }
        
        numVertices = 0;
    }
    
    void batchEnd(ShaderProgram program) {
        if(numVertices > 0) {
            g.vertices.flip();
            g.indices.flip();
            
            render(program);
            
            g.vertices.clear();
            g.indices.clear();
            
            numVertices = 0;
        }
    }
    
    void drawGeometry(Geometry shape) {
        try {
            int indexOffset = (numVertices / numFloats) * Float.BYTES;
            
            for(int v = 0; v < 8; v++) {
                Vector3f vertexPos = shape.vertices.get(v).position;
                Vector2f texCoords = shape.vertices.get(v).texCoords;

                g.vertices.put(vertexPos.x).put(vertexPos.y).put(vertexPos.z).put(texCoords.x).put(texCoords.y);
            }
            
            shape.faces.forEach((index, face) -> {
                int vert1 = indexOffset + face.indices[0];
                int vert2 = indexOffset + face.indices[1];
                int vert3 = indexOffset + face.indices[2];
                
                g.indices.put(vert1).put(vert2).put(vert3);
            });

            numVertices += numFloats;
        } catch(BufferOverflowException e) {
            Logger.setStackTrace(e);
            Logger.log(LogLevel.SEVERE, "Geometry buffer experienced overflow");
        }
    }
    
}