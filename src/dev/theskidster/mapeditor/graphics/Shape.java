package dev.theskidster.mapeditor.graphics;

import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.ShaderProgram;
import static dev.theskidster.mapeditor.world.World.HCS;
import java.nio.FloatBuffer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Jan 25, 2021
 */

public final class Shape {

    private final int vao = glGenVertexArrays();
    private final int vbo = glGenBuffers();
    
    public boolean removeRequest;
    public boolean selected;
    
    public Vector3f position;
    private Texture texture;
    private final FloatBuffer vertices;
    private final Matrix4f modelMatrix = new Matrix4f();
    
    public Vector3f[] verts = new Vector3f[3];
    
    public Shape(Vector3f position) {
        this.position = position;
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            vertices = stack.mallocFloat(24);
            
            verts[0] = new Vector3f(position.x - HCS, position.y + HCS, position.z - HCS);
            verts[1] = new Vector3f(position.x + HCS, position.y + HCS, position.z - HCS);
            verts[2] = new Vector3f(position.x + HCS, position.y - HCS, position.z - HCS);
            
            //(vec3 position)
            vertices.put(position.x - HCS).put(position.y + HCS).put(position.z - HCS); //0
            vertices.put(position.x + HCS).put(position.y + HCS).put(position.z - HCS); //1
            vertices.put(position.x + HCS).put(position.y - HCS).put(position.z - HCS); //2
            vertices.put(position.x - HCS).put(position.y - HCS).put(position.z - HCS); //3
            vertices.put(position.x - HCS).put(position.y + HCS).put(position.z + HCS); //4
            vertices.put(position.x + HCS).put(position.y + HCS).put(position.z + HCS); //5
            vertices.put(position.x + HCS).put(position.y - HCS).put(position.z + HCS); //6
            vertices.put(position.x - HCS).put(position.y - HCS).put(position.z + HCS); //7
            
            vertices.flip();
        }
        
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        glEnableVertexAttribArray(0);
        
        setTexture("img_terrain.png");
    }
    
    public void update() {
        modelMatrix.translation(position);
        
        verts[0].x = position.x - HCS;
        verts[0].y = position.y + HCS;
        verts[0].z = position.z - HCS;
        
        verts[1].x = position.x + HCS;
        verts[1].y = position.y + HCS;
        verts[1].z = position.z - HCS;
        
        verts[2].x = position.x + HCS;
        verts[2].y = position.y - HCS;
        verts[2].z = position.z - HCS;
    }
    
    public void render(ShaderProgram program) {
        glPointSize(5);
        glBindVertexArray(vao);
        
        program.setUniform("uType", 1);
        program.setUniform("uModel", false, modelMatrix);
        program.setUniform("uSelected", (selected) ? 1f : 0f);
        
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