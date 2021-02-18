package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.graphics.Graphics;
import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import java.util.Map;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Feb 17, 2021
 */

class MovementCursor {
    
    Vector3f position;
    
    private Graphics g1         = new Graphics();
    private Vector3f colorVec   = new Vector3f();
    private static Vector3f avg = new Vector3f();
    
    MovementCursor(Vector3f position) {
        this.position = position;
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g1.vertices = stack.mallocFloat(36);
            
            float start  = 0.1f;
            float length = 0.5f;
            
            //(vec3 position)
            g1.vertices.put(-start) .put(0)      .put(0);
            g1.vertices.put(-length).put(0)      .put(0);
            g1.vertices.put( start) .put(0)      .put(0);
            g1.vertices.put( length).put(0)      .put(0);
            g1.vertices.put(0)      .put(-start) .put(0);
            g1.vertices.put(0)      .put(-length).put(0);
            g1.vertices.put(0)      .put( start) .put(0);
            g1.vertices.put(0)      .put( length).put(0);
            g1.vertices.put(0)      .put(0)      .put( start);
            g1.vertices.put(0)      .put(0)      .put( length);
            g1.vertices.put(0)      .put(0)      .put(-start);
            g1.vertices.put(0)      .put(0)      .put(-length);
            
            g1.vertices.flip();
        }
        
        g1.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        
        glEnableVertexAttribArray(0);
    }
    
    void update(Map<Integer, Vector3f> vertexPositions) {
        if(vertexPositions.size() == 1) {
            vertexPositions.keySet().forEach(key -> g1.modelMatrix.translation(vertexPositions.get(key)));
        } else {
            vertexPositions.forEach((index, pos) -> {
                avg.x += pos.x;
                avg.y += pos.y;
                avg.z += pos.z;
            });
            
            avg.div(vertexPositions.size());
            
            position.set(avg);
            avg.set(0);
            
            g1.modelMatrix.translation(position);
        }
    }
    
    void render(ShaderProgram program) {
        glLineWidth(3);
        glBindVertexArray(g1.vao);
        
        for(int i = 0; i < 6; i++) {
            switch(i) {
                case 0, 1 -> colorVec.set(Color.RGM_RED.r,   Color.RGM_RED.g,   Color.RGM_RED.b);
                case 2, 3 -> colorVec.set(Color.RGM_BLUE.r,  Color.RGM_BLUE.g,  Color.RGM_BLUE.b);
                case 4, 5 -> colorVec.set(Color.RGM_GREEN.r, Color.RGM_GREEN.g, Color.RGM_GREEN.b);
            }
            
            program.setUniform("uType", 0);
            program.setUniform("uModel", false, g1.modelMatrix);
            program.setUniform("uColor", colorVec);
            
            glDrawArrays(GL_LINES, 2 * i, (2 * i) + 2);
        }
        
        glLineWidth(1);
        App.checkGLError();
    }
    
}