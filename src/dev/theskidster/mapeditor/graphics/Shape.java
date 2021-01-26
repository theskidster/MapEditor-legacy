package dev.theskidster.mapeditor.graphics;

import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.ShaderProgram;
import static dev.theskidster.mapeditor.world.World.HCS;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Jan 25, 2021
 */

public final class Shape {

    public Vector3f position;
    
    private final Graphics g;
    private Texture texture;
    
    public Shape(Vector3f position) {
        this.position = position;
        
        g = new Graphics();
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(24);
            
            //(vec3 position)
            g.vertices.put(position.x - HCS).put(position.y + HCS).put(position.z - HCS); //0
            g.vertices.put(position.x + HCS).put(position.y + HCS).put(position.z - HCS); //1
            g.vertices.put(position.x + HCS).put(position.y - HCS).put(position.z - HCS); //2
            g.vertices.put(position.x - HCS).put(position.y - HCS).put(position.z - HCS); //3
            g.vertices.put(position.x - HCS).put(position.y + HCS).put(position.z + HCS); //4
            g.vertices.put(position.x + HCS).put(position.y + HCS).put(position.z + HCS); //5
            g.vertices.put(position.x + HCS).put(position.y - HCS).put(position.z + HCS); //6
            g.vertices.put(position.x - HCS).put(position.y - HCS).put(position.z + HCS); //7
            
            g.vertices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        
        glEnableVertexAttribArray(0);
        
        setTexture("img_terrain.png");
    }
    
    public void update() {
        g.modelMatrix.translation(position);
    }
    
    public void render(ShaderProgram program) {
        glPointSize(5);
        glBindVertexArray(g.vao);
        
        program.setUniform("uType", 1);
        program.setUniform("uModel", false, g.modelMatrix);
        
        glDrawArrays(GL_POINTS, 0, 8);
        
        App.checkGLError();
        glPointSize(1);
    }
    
    public void setTexture(String filename) {
        if(texture != null) texture.freeTexture();
        
        texture = new Texture(filename);
        
        glBindTexture(GL_TEXTURE_2D, texture.handle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
}