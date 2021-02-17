package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.ShaderProgram;
import java.nio.FloatBuffer;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;

/**
 * @author J Hoffman
 * Created: Feb 16, 2021
 */

class VertexSelector {
    
    private int bufferSizeInBytes;
    
    public final int vao = glGenVertexArrays();
    public final int vbo = glGenBuffers();
    
    private List<Integer> selectedVertices = new LinkedList<>();
    
    VertexSelector() {
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (6 * Float.BYTES), 0);
        glVertexAttribPointer(3, 3, GL_FLOAT, false, (6 * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(3);
    }
    
    private void findBufferSize(int numVertices) {
        bufferSizeInBytes = numVertices * 6 * Float.BYTES;
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, bufferSizeInBytes, GL_DYNAMIC_DRAW);
    }
    
    void draw(ShaderProgram program, LinkedHashMap<Integer, Vector3f> vertexPositions) {
        glPointSize(6);
        glBindVertexArray(vao);
        
        findBufferSize(vertexPositions.size());
        FloatBuffer vertexBuf = MemoryUtil.memAllocFloat(bufferSizeInBytes);
        
        for(int i = 0; i < vertexPositions.size(); i++) {
            Vector3f position = vertexPositions.get(i);
            
            if(selectedVertices.contains(i)) {
                vertexBuf.put(position.x).put(position.y).put(position.z)
                         .put(1).put(0).put(0);
            } else {
                vertexBuf.put(position.x).put(position.y).put(position.z)
                         .put(1).put(1).put(1);
            }
        }
        
        vertexBuf.flip();
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertexBuf);
        
        MemoryUtil.memFree(vertexBuf);

        program.setUniform("uType", 4);
        
        glDrawArrays(GL_POINTS, 0, vertexPositions.size());
        glPointSize(1);
        
        App.checkGLError();
    }
    
    void addVertex(int index) {
        selectedVertices.add(index);
    }
    
    void removeVertex(int index) {
        selectedVertices.remove(index);
    }
    
    void clear() {
        selectedVertices.clear();
    }
    
}