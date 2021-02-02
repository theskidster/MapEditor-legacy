package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.graphics.Graphics;
import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import static dev.theskidster.mapeditor.world.World.CELL_SIZE;
import java.nio.FloatBuffer;
import java.util.Map;
import org.joml.Vector3i;
import static org.lwjgl.opengl.GL33C.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Feb 1, 2021
 */

class CellRenderer {

    private final int vboPosOffset;
    private final int vboColOffset;
    
    private Graphics g;
    
    CellRenderer() {
        vboPosOffset = glGenBuffers();
        vboColOffset = glGenBuffers();
        
        g = new Graphics();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(12);
            g.indices  = stack.mallocInt(6);
            
            g.vertices.put(0)        .put(0).put(CELL_SIZE);
            g.vertices.put(CELL_SIZE).put(0).put(CELL_SIZE);
            g.vertices.put(CELL_SIZE).put(0).put(0);
            g.vertices.put(0).put(0).put(0);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        
        glEnableVertexAttribArray(0);
    }
    
    private void offsetPosition(Map<Vector3i, Cell> cells) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer positions = stack.mallocFloat(cells.size() * Float.BYTES);
            
            cells.forEach((location, cell) -> {
                positions.put(cell.position.x).put(cell.position.y).put(cell.position.z);
            });
            
            positions.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, vboPosOffset);
            glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);
        }
        
        glVertexAttribPointer(2, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(2);
        glVertexAttribDivisor(2, 1);
    }
    
    private void offsetColor(Map<Vector3i, Cell> cells) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer colors = stack.mallocFloat(cells.size() * Float.BYTES);
            
            cells.forEach((location, cell) -> {
                if(cell.hovered) colors.put(Color.PINK.r).put(Color.PINK.g).put(Color.PINK.b);
                else             colors.put(cell.color.x).put(cell.color.y).put(cell.color.z);
            });
            
            colors.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, vboColOffset);
            glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW);
        }
        
        glVertexAttribPointer(3, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(3);
        glVertexAttribDivisor(3, 1);
    }
    
    void draw(ShaderProgram program, Map<Vector3i, Cell> cells) {
        glBindVertexArray(g.vao);
        
        offsetPosition(cells);
        offsetColor(cells);
        
        program.setUniform("uType", 3);
        
        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, cells.size());
        App.checkGLError();
    }
    
}