package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.graphics.Graphics;
import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.ShaderProgram;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Dec 17, 2020
 */

class TestObject {
    
    Vector3f position;
    private Graphics g;
    
    TestObject(Vector3f position, Vector3f[] verts) {
        this.position = position;
        
        g = new Graphics();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(18);
            
            //(vec3 position), (vec3 color)
            g.vertices.put(verts[0].x).put(verts[0].y).put(verts[0].z)   .put(0).put(0).put(1);
            g.vertices.put(verts[1].x).put(verts[1].y).put(verts[1].z)   .put(0).put(0).put(1);
            g.vertices.put(verts[2].x).put(verts[2].y).put(verts[2].z)   .put(0).put(0).put(1);
            
            g.vertices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (6 * Float.BYTES), 0);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, (6 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }
    
    void update() {
        g.modelMatrix.translation(position);
    }
    
    void render(ShaderProgram program) {
        glBindVertexArray(g.vao);
        
        program.setUniform("uType", 2);
        program.setUniform("uModel", false, g.modelMatrix);
        
        glDrawArrays(GL_TRIANGLES, 0, 3);
        
        App.checkGLError();
    }
    
}