package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.LogLevel;
import dev.theskidster.mapeditor.main.Logger;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import static dev.theskidster.mapeditor.world.World.CELL_SIZE;
import java.nio.BufferOverflowException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Feb 9, 2021
 */

final class Cube {
    
    private final class Face2 {
        int[] vp = new int[3];
        int[] tc = new int[3];
        int[] n  = new int[3];
        
        Face2(int[] vp, int[] tc, int[] n) {
            this.vp = vp;
            this.tc = tc;
            this.n  = n;
        }
    }
    
    private final int FLOATS_PER_VERTEX = 8;
    private int numVertices;
    private int bufferSizeInBytes;
    
    private final int vao = glGenVertexArrays();
    private final int vbo = glGenBuffers();
    
    private boolean updateData = true;
    
    Vector3f position;
    private Matrix4f modelMatrix = new Matrix4f();
    
    private Map<Integer, Vector3f> vertexPositions;
    private Map<Integer, Vector2f> texCoords;
    private Map<Integer, Vector3f> normals;
    private Map<Integer, Face2> faces;
    
    Cube(Vector3f position) {
        this.position = position;
        
        vertexPositions = new HashMap<>() {{
            put(0, new Vector3f(0,         0,         CELL_SIZE));
            put(1, new Vector3f(CELL_SIZE, 0,         CELL_SIZE));
            put(2, new Vector3f(CELL_SIZE, CELL_SIZE, CELL_SIZE));
            put(3, new Vector3f(0,         CELL_SIZE, CELL_SIZE));
            put(4, new Vector3f(0,         0,         0));
            put(5, new Vector3f(CELL_SIZE, 0,         0));
            put(6, new Vector3f(CELL_SIZE, CELL_SIZE, 0));
            put(7, new Vector3f(0,         CELL_SIZE, 0));
        }};
        
        texCoords = new HashMap<>() {{
            put(0, new Vector2f(0, 0));
            put(1, new Vector2f(1, 0));
            put(2, new Vector2f(1, 1));
            put(3, new Vector2f(0, 1));
        }};
        
        normals = new HashMap<>() {{
            put(0, new Vector3f( 1,  0,  0));
            put(1, new Vector3f( 0,  1,  0));
            put(2, new Vector3f( 0,  0,  1));
            put(3, new Vector3f(-1,  0,  0));
            put(4, new Vector3f( 0, -1,  0));
            put(5, new Vector3f( 0,  0, -1));
        }};
        
        faces = new TreeMap<>() {{
            //FRONT:
            put(0,  new Face2(new int[]{0, 1, 2}, new int[]{0, 0, 0}, new int[]{0, 0, 0}));
            put(1,  new Face2(new int[]{2, 3, 0}, new int[]{0, 0, 0}, new int[]{0, 0, 0}));
            //RIGHT:
            put(2,  new Face2(new int[]{1, 5, 6}, new int[]{0, 0, 0}, new int[]{0, 0, 0}));
            put(3,  new Face2(new int[]{6, 2, 1}, new int[]{0, 0, 0}, new int[]{0, 0, 0}));
            //BACK:
            put(4,  new Face2(new int[]{7, 6, 5}, new int[]{0, 0, 0}, new int[]{0, 0, 0}));
            put(5,  new Face2(new int[]{5, 4, 7}, new int[]{0, 0, 0}, new int[]{0, 0, 0}));
            //LEFT:
            put(6,  new Face2(new int[]{4, 0, 3}, new int[]{0, 0, 0}, new int[]{0, 0, 0}));
            put(7,  new Face2(new int[]{3, 7, 4}, new int[]{0, 0, 0}, new int[]{0, 0, 0}));
            //BOTTOM:
            put(8,  new Face2(new int[]{4, 5, 1}, new int[]{0, 0, 0}, new int[]{0, 0, 0}));
            put(9,  new Face2(new int[]{1, 0, 4}, new int[]{0, 0, 0}, new int[]{0, 0, 0}));
            //TOP:
            put(10, new Face2(new int[]{3, 2, 6}, new int[]{0, 0, 0}, new int[]{0, 0, 0}));
            put(11, new Face2(new int[]{6, 7, 3}, new int[]{0, 0, 0}, new int[]{0, 0, 0}));
        }};
        
        findBufferSize();
        
        /*
        TODO:
        
        - Add texture
        - Add setter methods for vertex attributes
        - Find whether the cube is convex (this will be used to determine collision elegability)
        - Add face & vertex (position) selection
        - Add face & vertex (position) creation/deletion
        - Add texture coordinate manipulation
        - Add face & vertex (position) manipulation
        */
        
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, bufferSizeInBytes, GL_DYNAMIC_DRAW);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (FLOATS_PER_VERTEX * Float.BYTES), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, (FLOATS_PER_VERTEX * Float.BYTES), (3 * Float.BYTES));
        glVertexAttribPointer(2, 2, GL_FLOAT, false, (FLOATS_PER_VERTEX * Float.BYTES), (5 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        
        /*
        VERTEX POSITIONS:
        -----------------
        v0: 0, 0, 1
        v1: 1, 0, 1
        v2: 1, 1, 1
        v3: 0, 1, 1
        v4: 0, 0, 0
        v5: 1, 0, 0
        v6: 1, 1, 0
        v7: 0, 1, 0
        
        TEX COORDS:
        -----------------
        vt0: 0, 0
        vt1: 1, 0
        vt2: 1, 1
        vt3: 0, 1
        
        FACES:
        -----------------
        front:
            (vp0/tc0), (vp1/tc1), (vp2/tc2)
            (vp2/tc2), (vp3/tc3), (vp0/tc0)
        right:
            (vp1/), (vp5/), (vp6/)
            (vp6/), (vp2/), (vp1/)
        back:
            (vp7/), (vp6/), (vp5/)
            (vp5/), (vp4/), (vp7/)
        left:
            (vp4/), (vp0/), (vp3/)
            (vp3/), (vp7/), (vp4/)
        bottom:
            (vp4/), (vp5/), (vp1/)
            (vp1/), (vp0/), (vp4/)
        top:
            (vp3/), (vp2/), (vp6/)
            (vp6/), (vp7/), (vp3/)
        
        This actually gives us a total of 36 vertices that will be generated 
        using data from the faces.
        
        We'll mimic an OBJ by using faces to dynamically constuct unique vertices 
        by pairing vertex attributes, since GJK generates simplex structures using
        only vertex positions this will work fine.
        */
    }
    
    private void findBufferSize() {
        numVertices       = faces.size() * 3;
        bufferSizeInBytes = numVertices * FLOATS_PER_VERTEX * Float.BYTES;
    }
    
    void update() {
        modelMatrix.translation(position);
        
        if(updateData) {
            try(MemoryStack stack = MemoryStack.stackPush()) {
                findBufferSize();
                
                FloatBuffer vertices = stack.mallocFloat(bufferSizeInBytes);
                
                faces.forEach((index, face) -> {
                    for(int i = 0; i < 3; i++) {
                        Vector3f pos    = vertexPositions.get(face.vp[i]);
                        Vector2f coords = texCoords.get(face.tc[i]);
                        Vector3f normal = normals.get(face.n[i]);
                        
                        vertices.put(pos.x).put(pos.y).put(pos.z)
                                .put(coords.x).put(coords.y)
                                .put(normal.x).put(normal.y).put(normal.z);
                    }
                });
                
                vertices.flip();
                
                glBindBuffer(GL_ARRAY_BUFFER, vbo);
                glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
                
            } catch(BufferOverflowException e) {
                Logger.setStackTrace(e);
                Logger.log(LogLevel.SEVERE, "Cube buffer experienced overflow");
            }
            
            updateData = false;
        }
    }
    
    void render(ShaderProgram program) {
        glBindVertexArray(vao);
        
        program.setUniform("uType", 4);
        program.setUniform("uColor", Color.convert(Color.RED));
        program.setUniform("uModel", false, modelMatrix);
        
        glDrawArrays(GL_TRIANGLES, 0, numVertices);
        
        App.checkGLError();
    }
    
}