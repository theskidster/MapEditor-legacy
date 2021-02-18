package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.graphics.Graphics;
import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import java.util.Map;
import org.joml.Intersectionf;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Feb 17, 2021
 */

final class MovementCursor {
    
    private final float START  = 0.1f;
    private final float LENGTH = 0.5f;
    
    private boolean selected;
    
    private Vector3f position;
    
    private final Graphics g1       = new Graphics();
    private final Vector3f colorVec = new Vector3f();
    private final Vector3f avg      = new Vector3f();
    private final Vector3f min      = new Vector3f();
    private final Vector3f max      = new Vector3f();
    
    MovementCursor(Vector3f position) {
        this.position = position;
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g1.vertices = stack.mallocFloat(36);
            
            //(vec3 position)
            g1.vertices.put(-START) .put(0)      .put(0);
            g1.vertices.put(-LENGTH).put(0)      .put(0);
            g1.vertices.put( START) .put(0)      .put(0);
            g1.vertices.put( LENGTH).put(0)      .put(0);
            g1.vertices.put(0)      .put(-START) .put(0);
            g1.vertices.put(0)      .put(-LENGTH).put(0);
            g1.vertices.put(0)      .put( START) .put(0);
            g1.vertices.put(0)      .put( LENGTH).put(0);
            g1.vertices.put(0)      .put(0)      .put( START);
            g1.vertices.put(0)      .put(0)      .put( LENGTH);
            g1.vertices.put(0)      .put(0)      .put(-START);
            g1.vertices.put(0)      .put(0)      .put(-LENGTH);
            
            g1.vertices.flip();
        }
        
        g1.bindBuffers();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (3 * Float.BYTES), 0);
        
        glEnableVertexAttribArray(0);
    }
    
    void update(Map<Integer, Vector3f> vertexPositions) {
        if(vertexPositions.size() == 1) {
            vertexPositions.keySet().forEach(key -> position.set(vertexPositions.get(key)));
        } else {
            vertexPositions.forEach((index, pos) -> {
                avg.x += pos.x;
                avg.y += pos.y;
                avg.z += pos.z;
            });
            
            avg.div(vertexPositions.size());
            position.set(avg);
            avg.set(0);
        }
        
        g1.modelMatrix.translation(position);
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
    
    void selectArrow(Vector3f camPos, Vector3f camRay) {
        boolean result = false;
        
        float distance = (float) Math.sqrt(
                    Math.pow((position.x - camPos.x), 2) + 
                    Math.pow((position.y - camPos.y), 2) +
                    Math.pow((position.z - camPos.z), 2)) *
                    0.003f;
        
        for(int i = 0; i < 6; i++) {
            switch(i) {
                case 0 -> {
                    min.set(position.x - LENGTH, position.y - distance, position.z - distance);
                    max.set(position.x - START, position.y + distance, position.z + distance);
                }
                /*
                case 1 -> {
                    min.set(position.x + START, position.y, position.z);
                    max.set(position.x + LENGTH, position.y, position.z);
                }
                case 2 -> {
                    min.set(position.x, position.y - START, position.z);
                    max.set(position.x, position.y - LENGTH, position.z);
                }
                case 3 -> {
                    min.set(position.x, position.y + START, position.z);
                    max.set(position.x, position.y + LENGTH, position.z);
                }
                case 4 -> {
                    min.set(position.x, position.y, position.z - START);
                    max.set(position.x, position.y, position.z - LENGTH);
                }
                case 5 -> {
                    min.set(position.x, position.y, position.z + START);
                    max.set(position.x, position.y, position.z + LENGTH);
                }
                */
            }
            
            result = Intersectionf.testRayAab(camPos, camRay, min, max);
            if(result) break;
        }
        
        selected = result;
    }
    
    void moveArrow(float rayChangeX, float rayChangeY) {
        System.out.println("moving");
        //TODO: move vertices along the axis of the selected arrow
    }
    
    boolean getSelected() { return selected; }
    
}