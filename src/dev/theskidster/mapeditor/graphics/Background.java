package dev.theskidster.mapeditor.graphics;

import dev.theskidster.mapeditor.util.Rectangle;
import dev.theskidster.mapeditor.util.Color;
import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.LogLevel;
import dev.theskidster.mapeditor.main.Logger;
import dev.theskidster.mapeditor.main.ShaderProgram;
import java.nio.BufferOverflowException;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author J Hoffman
 * Created: Jan 4, 2021
 */

public final class Background {

    private int numVertices;
    
    private final Graphics g = new Graphics();
    
    public Background(int numRectangles) {
        g.vertices = MemoryUtil.memAllocFloat(20 * numRectangles * Float.BYTES);
        g.indices  = MemoryUtil.memAllocInt(6 * numRectangles * Float.BYTES);
        
        glBindVertexArray(g.vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, g.vbo);
        glBufferData(GL_ARRAY_BUFFER, g.vertices.capacity() + 1, GL_DYNAMIC_DRAW);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, g.ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, g.indices.capacity(), GL_DYNAMIC_DRAW);
        
        glVertexAttribPointer(0, 2, GL_FLOAT, false, (5 * Float.BYTES), 0);
        glVertexAttribPointer(2, 3, GL_FLOAT, false, (5 * Float.BYTES), (2 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(2);
    }
    
    private void render(ShaderProgram program) {
        glBindVertexArray(g.vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, g.vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, g.vertices);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, g.ibo);
        glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, g.indices);
        
        program.setUniform("uType", 0);
        
        glDrawElements(GL_TRIANGLES, g.indices.limit() * (numVertices / 20), GL_UNSIGNED_INT, NULL);
        
        App.checkGLError();
    }
    
    public void batchStart() {
        numVertices = 0;
    }
    
    public void batchEnd(ShaderProgram program) {
        if(numVertices > 0) {
            g.vertices.flip();
            g.indices.flip();
            
            render(program);
            
            g.vertices.clear();
            g.indices.clear();
            
            numVertices = 0;
        }
    }
    
    public void drawRectangle(float x, float y, float width, float height, Color color) {
        try {
            int startIndex = (numVertices / 20) * Float.BYTES;
            
            g.vertices.put(x)        .put(y + height).put(color.r).put(color.g).put(color.b);
            g.vertices.put(x + width).put(y + height).put(color.r).put(color.g).put(color.b);
            g.vertices.put(x + width).put(y)         .put(color.r).put(color.g).put(color.b);
            g.vertices.put(x)        .put(y)         .put(color.r).put(color.g).put(color.b);
            
            g.indices.put(startIndex)    .put(startIndex + 1).put(startIndex + 2);
            g.indices.put(startIndex + 3).put(startIndex + 2).put(startIndex);

            numVertices += 20;
        } catch(BufferOverflowException e) {
            Logger.setStackTrace(e);
            Logger.log(LogLevel.SEVERE, 
                       "Background buffer experienced overflow, " + 
                       "check max number of allowed rectangles in constructor.");
        }
    }
    
    public void drawRectangle(Rectangle rectangle, Color color) {
        drawRectangle(
                rectangle.xPos,
                rectangle.yPos,
                rectangle.width,
                rectangle.height,
                color);
    }
    
    public void destroy() {
        MemoryUtil.memFree(g.vertices);
        MemoryUtil.memFree(g.indices);
        g.freeBuffers();
    }
    
}