package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.graphics.Graphics;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Jan 26, 2021
 */

class Origin {
    
    private final Graphics g;
    private final Vector3f colorVec = new Vector3f();
    
    Origin(int width, int height, int depth) {        
        g = new Graphics();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(6 * 3);
            
            float halfWidth = (width / 2);
            float halfDepth = (depth / 2);
            
            //(vec3 position)
            g.vertices.put(-halfWidth).put(0)     .put(0);
            g.vertices.put( halfWidth).put(0)     .put(0);
            g.vertices.put(0)         .put(0)     .put(0);
            g.vertices.put(0)         .put(height).put(0);
            g.vertices.put(0)         .put(0)     .put(-halfDepth);
            g.vertices.put(0)         .put(0)     .put( halfDepth);
            
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
                case 0 -> colorVec.set(Color.RED.r, Color.RED.g, Color.RED.b);
                case 1 -> colorVec.set(Color.BLUE.r, Color.BLUE.g, Color.BLUE.b);
                case 2 -> colorVec.set(Color.GREEN.r, Color.GREEN.g, Color.GREEN.b);
            }
            
            program.setUniform("uType", 0);
            program.setUniform("uModel", false, g.modelMatrix);
            program.setUniform("uColor", colorVec);
            
            glDrawArrays(GL_LINES, 2 * i, (2 * i) + 2);
        }
    }
    
}