package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.graphics.Graphics;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import static dev.theskidster.mapeditor.world.World.CELL_SIZE;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Jan 26, 2021
 */

class Origin {
    
    private Graphics g;
    private Color color;
    
    private Vector3f colorVec = new Vector3f();
    
    Origin(int width, int height, int depth) {
        int halfWidth  = (width / 2) * CELL_SIZE;
        int halfHeight = (height / 2) * CELL_SIZE;
        int halfDepth  = (depth / 2) * CELL_SIZE;
        
        g = new Graphics();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(6 * 3);
            
            //(vec3 position)
            g.vertices.put(-halfWidth).put(0)          .put(0);
            g.vertices.put(halfWidth) .put(0)          .put(0);
            g.vertices.put(0)         .put(0)          .put(-halfDepth);
            g.vertices.put(0)         .put(0)          .put(halfDepth);
            g.vertices.put(0)         .put(-halfHeight).put(0);
            g.vertices.put(0)         .put(halfHeight) .put(0);
            
            g.vertices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        
        glEnableVertexAttribArray(0);
    }
    
    public void render(ShaderProgram program) {
        glBindVertexArray(g.vao);
        
        for(int i = 0; i < 3; i++) {
            switch(i) {
                case 0: color = Color.RED;   break;
                case 1: color = Color.GREEN; break;
                case 2: color = Color.BLUE;  break;
            }
            
            colorVec.set(color.r, color.g, color.b);
            
            program.setUniform("uType", 0);
            program.setUniform("uModel", false, g.modelMatrix);
            program.setUniform("uColor", colorVec);
            
            glDrawArrays(GL_LINES, 2 * i, (2 * i) + 2);
        }
    }
    
}