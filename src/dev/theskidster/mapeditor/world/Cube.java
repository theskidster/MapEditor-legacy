package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.ShaderProgram;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11C.GL_FLOAT;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Feb 5, 2021
 */

class Cube {

    private final int vao = glGenVertexArrays();
    private final int vbo = glGenBuffers();
    private final int ibo = glGenBuffers();
    
    Vector3f position;
    
    private final FloatBuffer vertices;
    private final IntBuffer indices;
    
    private final Matrix4f modelMatrix = new Matrix4f();
    
    Cube(Vector3f position) {
        this.position = position;
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            vertices = stack.mallocFloat(24 * Float.BYTES);
            indices  = stack.mallocInt(36 * Integer.BYTES);
            
            //FRONT: (vec2 position), (vec2 tex coords), (vec3 color)
            vertices.put(-1).put(-1).put(1)  .put(0).put(0)  .put(1).put(0).put(0);
            vertices .put(1).put(-1).put(1)  .put(0).put(0)  .put(0).put(1).put(0);
            vertices .put(1) .put(1).put(1)  .put(0).put(0)  .put(0).put(0).put(1);
            vertices.put(-1) .put(1).put(1)  .put(0).put(0)  .put(1).put(1).put(1);
            //BACK:
            vertices.put(-1).put(-1).put(-1) .put(0).put(0)  .put(1).put(1).put(0);
            vertices .put(1).put(-1).put(-1) .put(0).put(0)  .put(0).put(1).put(1);
            vertices .put(1) .put(1).put(-1) .put(0).put(0)  .put(1).put(0).put(1);
            vertices.put(-1) .put(1).put(-1) .put(0).put(0)  .put(0).put(0).put(0);
            
            //FRONT:
            indices.put(0).put(1).put(2);
            indices.put(2).put(3).put(0);
            //RIGHT:
            indices.put(1).put(5).put(6);
            indices.put(6).put(2).put(1);
            //BACK:
            indices.put(7).put(6).put(5);
            indices.put(5).put(4).put(7);
            //LEFT:
            indices.put(4).put(0).put(3);
            indices.put(3).put(7).put(4);
            //BOTTOM:
            indices.put(4).put(5).put(1);
            indices.put(1).put(0).put(4);
            //TOP:
            indices.put(3).put(2).put(6);
            indices.put(6).put(7).put(3);
            
            vertices.flip();
            indices.flip();
        }
        
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (8 * Float.BYTES), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, (8 * Float.BYTES), (3 * Float.BYTES));
        glVertexAttribPointer(2, 3, GL_FLOAT, false, (8 * Float.BYTES), (5 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
    }
    
    void update() {
        modelMatrix.translation(position);
    }
    
    void render(ShaderProgram program) {
        glEnable(GL_DEPTH_TEST);
        glBindVertexArray(vao);
        
        program.setUniform("uType", 3);
        program.setUniform("uModel", false, modelMatrix);
        
        glDrawElements(GL_TRIANGLES, indices.limit(), GL_UNSIGNED_INT, 0);
        glDisable(GL_DEPTH_TEST);
        
        App.checkGLError();
    }
    
}