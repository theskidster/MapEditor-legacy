package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.graphics.Graphics;
import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.ShaderProgram;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;

/**
 * @author J Hoffman
 * Created: Feb 2, 2021
 */

class GeometryBatch {
    
    private final int numFloats = 24;
    private final int numFloatsPerVertex = 3;
    private int numVertices;
    private int prevNumShapes;
    
    private final Graphics g = new Graphics();
    
    private void render(ShaderProgram program) {
        glPointSize(4);
        
        glBindVertexArray(g.vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, g.vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, g.vertices);
        
        program.setUniform("uType", 2);
        
        glDrawArrays(GL_POINTS, 0, numVertices / numFloatsPerVertex);
        
        glPointSize(1);
        App.checkGLError();
    }
    
    void batchStart(int numShapes) {
        if(numShapes != prevNumShapes) {
            g.vertices = MemoryUtil.memAllocFloat(numFloats * numShapes * Float.BYTES);
        
            glBindVertexArray(g.vao);
            
            glBindBuffer(GL_ARRAY_BUFFER, g.vbo);
            glBufferData(GL_ARRAY_BUFFER, g.vertices.capacity(), GL_DYNAMIC_DRAW);
            
            glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        
            glEnableVertexAttribArray(0);
            
            prevNumShapes = numShapes;
        }
        
        numVertices = 0;
    }
    
    void batchEnd(ShaderProgram program) {
        if(numVertices > 0) {
            g.vertices.flip();
            
            render(program);
            
            g.vertices.clear();
            
            numVertices = 0;
        }
    }
    
    void drawGeometry(Geometry shape) {
        for(int i = 0; i < 8; i++) {
            g.vertices.put(shape.vertices.get(i).x).put(shape.vertices.get(i).y).put(shape.vertices.get(i).z);
        }
        
        numVertices += numFloats;
    }
    
}