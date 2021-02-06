package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.graphics.Graphics;
import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import static dev.theskidster.mapeditor.world.World.CELL_SIZE;
import java.nio.FloatBuffer;
import java.util.Map;
import org.joml.Vector2i;
import static org.lwjgl.opengl.GL33C.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Feb 1, 2021
 */

class Floor {

    private final int vboPosOffset;
    private final int vboColOffset;
    
    private Graphics g;
    
    Floor() {
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
    
    private void offsetPosition(Map<Vector2i, Boolean> tiles) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer positions = stack.mallocFloat(tiles.size() * Float.BYTES);
            
            tiles.forEach((location, hovered) -> {
                positions.put(location.x).put(0).put(location.y);
            });
            
            positions.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, vboPosOffset);
            glBufferData(GL_ARRAY_BUFFER, positions, GL_STATIC_DRAW);
        }
        
        glVertexAttribPointer(3, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(3);
        glVertexAttribDivisor(3, 1);
    }
    
    private void offsetColor(Map<Vector2i, Boolean> tiles) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer colors = stack.mallocFloat(tiles.size() * Float.BYTES);
            
            tiles.forEach((location, hovered) -> {
                if(hovered) colors.put(Color.PINK.r).put(Color.PINK.g).put(Color.PINK.b);
                else        colors.put(Color.PURPLE.r).put(Color.PURPLE.g).put(Color.PURPLE.b);
            });
            
            colors.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, vboColOffset);
            glBufferData(GL_ARRAY_BUFFER, colors, GL_STATIC_DRAW);
        }
        
        glVertexAttribPointer(4, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(4);
        glVertexAttribDivisor(4, 1);
    }
    
    public void draw(ShaderProgram program, Map<Vector2i, Boolean> tiles) {
        glBindVertexArray(g.vao);
        
        offsetPosition(tiles);
        offsetColor(tiles);
        
        program.setUniform("uType", 1);
        
        glDrawElementsInstanced(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, tiles.size());
        App.checkGLError();
    }
    
}