package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.graphics.LightSource;
import dev.theskidster.mapeditor.graphics.Texture;
import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.LogLevel;
import dev.theskidster.mapeditor.main.Logger;
import dev.theskidster.mapeditor.main.ShaderProgram;
import static dev.theskidster.mapeditor.world.World.CELL_SIZE;
import java.nio.BufferOverflowException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.joml.Vector2f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author J Hoffman
 * Created: Feb 5, 2021
 */

final class Geometry {

    static final Vector3f noValue = new Vector3f();
    
    int height = 1;
    
    private final int FLOATS_PER_VERTEX = 5;
    private final int FLOATS_PER_FACE   = 3;
    
    private final int vao = glGenVertexArrays();
    private final int vbo = glGenBuffers();
    private final int ibo = glGenBuffers();
    
    private boolean updateData = true;
    
    private FloatBuffer vertexBuf;
    private IntBuffer indexBuf;
    
    Map<Integer, Point> points;
    Map<Integer, Face> faces;
    
    private final Vector3f[] initialPointPositions;
    
    private Texture texture;
    
    Geometry(float xLoc, float zLoc) {
        points = new HashMap<>();
        
        /*
        Point != Vertex
        
        A point on a shape defines a 3D location that multiple vertices may be 
        attached to. That is, two vertices with different texture coordinates 
        may be attached to a single point.
        */
        
        for(int p = 0; p < 8; p++) {
            Vector3f pointPos             = new Vector3f();
            Map<Integer, Vertex> vertices = new TreeMap<>();
            
            switch(p) {
                case 0 -> {
                    pointPos.set(xLoc, 0, zLoc + CELL_SIZE);
                    vertices.put(p,      new Vertex(0, 0, 0, 0, 0)); //0
                    vertices.put(p + 8,  new Vertex(0, 0, 0, 0, 0)); //8
                    vertices.put(p + 16, new Vertex(0, 0, 0, 0, 0)); //16
                }
                case 1 -> {
                    pointPos.set(xLoc + CELL_SIZE, 0, zLoc + CELL_SIZE);
                    vertices.put(p,      new Vertex(0, 0, 0, 0, 0)); //1
                    vertices.put(p + 8,  new Vertex(0, 0, 0, 0, 0)); //9
                    vertices.put(p + 16, new Vertex(0, 0, 0, 0, 0)); //17
                }
                case 2 -> {
                    pointPos.set(xLoc + CELL_SIZE, CELL_SIZE, zLoc + CELL_SIZE);
                    vertices.put(p,      new Vertex(0, 0, 0, 0, 0)); //2
                    vertices.put(p + 8,  new Vertex(0, 0, 0, 0, 0)); //10
                    vertices.put(p + 16, new Vertex(0, 0, 0, 0, 0)); //18
                }
                case 3 -> {
                    pointPos.set(xLoc, CELL_SIZE, zLoc + CELL_SIZE);
                    vertices.put(p,      new Vertex(0, 0, 0, 0, 0)); //3
                    vertices.put(p + 8,  new Vertex(0, 0, 0, 0, 0)); //11
                    vertices.put(p + 16, new Vertex(0, 0, 0, 0, 0)); //19
                }
                case 4 -> {
                    pointPos.set(xLoc, 0, zLoc);
                    vertices.put(p,      new Vertex(0, 0, 0, 0, 0)); //4
                    vertices.put(p + 8,  new Vertex(0, 0, 0, 0, 0)); //12
                    vertices.put(p + 16, new Vertex(0, 0, 0, 0, 0)); //20
                }
                case 5 -> {
                    pointPos.set(xLoc + CELL_SIZE, 0, zLoc);
                    vertices.put(p,      new Vertex(0, 0, 0, 0, 0)); //5
                    vertices.put(p + 8,  new Vertex(0, 0, 0, 0, 0)); //13
                    vertices.put(p + 16, new Vertex(0, 0, 0, 0, 0)); //21
                }
                case 6 -> {
                    pointPos.set(xLoc + CELL_SIZE, CELL_SIZE, zLoc);
                    vertices.put(p,      new Vertex(0, 0, 0, 0, 0)); //6
                    vertices.put(p + 8,  new Vertex(0, 0, 0, 0, 0)); //14
                    vertices.put(p + 16, new Vertex(0, 0, 0, 0, 0)); //22
                }
                case 7 -> {
                    pointPos.set(xLoc, CELL_SIZE, zLoc);
                    vertices.put(p,      new Vertex(0, 0, 0, 0, 0)); //7
                    vertices.put(p + 8,  new Vertex(0, 0, 0, 0, 0)); //15
                    vertices.put(p + 16, new Vertex(0, 0, 0, 0, 0)); //23
                }
            }
            
            points.put(p, new Point(pointPos, vertices));
        }
        
        faces = new HashMap<>() {{
            /*
            put(0, new Face(0, 1, 2));
            put(1, new Face(2, 1, 3));
            
            put(2, new Face(4, 5, 6));
            put(3, new Face(6, 5, 7));
            
            put(4, new Face(8, 9, 10));
            put(5, new Face(10, 9, 11));
            
            put(6, new Face(12, 13, 14));
            put(7, new Face(14, 13, 15));
            
            put(8, new Face(16, 17, 18));
            put(9, new Face(18, 17, 19));
            
            put(10, new Face(20, 21, 22));
            put(11, new Face(22, 21, 23));
            */
            
            //FRONT:
            put(0, new Face(0, 1, 2));
            put(1, new Face(2, 3, 0));
            //RIGHT:
            put(2, new Face(1, 5, 6));
            put(3, new Face(6, 2, 1));
            //BACK:
            put(4, new Face(7, 6, 5));
            put(5, new Face(5, 4, 7));
            //LEFT:
            put(6, new Face(4, 0, 3));
            put(7, new Face(3, 7, 4));
            //BOTTOM:
            put(8, new Face(4, 5, 1));
            put(9, new Face(1, 0, 4));
            //TOP:
            put(10, new Face(3, 2, 6));
            put(11, new Face(6, 7, 3));
        }};
        
        initialPointPositions = new Vector3f[points.size()];
        
        for(int v = 0; v < points.size(); v++) {
            initialPointPositions[v] = new Vector3f(points.get(v).position);
        }
        
        vertexBuf = MemoryUtil.memAllocFloat((points.size() * 3) * FLOATS_PER_VERTEX);
        indexBuf  = MemoryUtil.memAllocInt(faces.size() * FLOATS_PER_FACE);
        
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertexBuf.capacity() * Float.BYTES, GL_DYNAMIC_DRAW);
        
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuf.capacity() * Float.BYTES, GL_DYNAMIC_DRAW);
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (FLOATS_PER_VERTEX * Float.BYTES), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, (FLOATS_PER_VERTEX * Float.BYTES), (3 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        
        texture = new Texture("img_terrain.png");
        
        glBindTexture(GL_TEXTURE_2D, texture.handle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
    void update() {
        if(updateData) {
            try {
                Map<Integer, Vertex> vertices = new TreeMap<>();
                
                points.forEach((pointID, point) -> {
                    point.vertices.forEach((vertID, vertex) -> {
                        vertices.put(vertID, vertex);
                    });
                });
                
                vertices.forEach((vertID, vertex) -> {
                    Vector3f vertexPos = vertex.point.position;
                    Vector2f texCoords = vertex.texCoords;
                    
                    vertexBuf.put(vertexPos.x).put(vertexPos.y).put(vertexPos.z).put(texCoords.x).put(texCoords.y);
                });

                faces.forEach((id, face) -> {
                    indexBuf.put(face.indices[0]).put(face.indices[1]).put(face.indices[2]);
                });
            } catch(BufferOverflowException e) {
                Logger.setStackTrace(e);
                Logger.log(LogLevel.SEVERE, "Geometry buffer experienced overflow");
            }
            
            vertexBuf.flip();
            indexBuf.flip();
            
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertexBuf);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);        
            glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, indexBuf);
            
            updateData = false;
        }
    }
    
    void render(ShaderProgram program, LightSource[] lights, int numLights) {
        glBindVertexArray(vao);
        glBindTexture(GL_TEXTURE_2D, texture.handle);
        
        program.setUniform("uType", 2);
        program.setUniform("uNumLights", numLights);
        
        for(int i = 0; i < App.MAX_LIGHTS; i++) {
            if(lights[i] != null) {
                if(lights[i].enabled) {
                    program.setUniform("uLights[" + i + "].brightness", lights[i].getBrightness());
                    program.setUniform("uLights[" + i + "].contrast",   lights[i].getContrast());
                    program.setUniform("uLights[" + i + "].position",   lights[i].getPosition());
                    program.setUniform("uLights[" + i + "].ambient",    lights[i].getAmbientColor());
                    program.setUniform("uLights[" + i + "].diffuse",    lights[i].getDiffuseColor());
                } else {
                    program.setUniform("uLights[" + i + "].brightness", 0);
                    program.setUniform("uLights[" + i + "].contrast",   0);
                    program.setUniform("uLights[" + i + "].position",   noValue);
                    program.setUniform("uLights[" + i + "].ambient",    noValue);
                    program.setUniform("uLights[" + i + "].diffuse",    noValue);
                }
            }
        }
        
        glDrawElements(GL_TRIANGLES, indexBuf.capacity(), GL_UNSIGNED_INT, NULL);
        
        App.checkGLError();
    }
    
    float getPointPos(int index, String axis) {
        if(!points.containsKey(index)) {
            Logger.log(LogLevel.WARNING, "No vertex with an ID of: (" + index + ") exists in this shape.");
            return 0;
        }
        
        switch(axis) {
            case "x", "X" -> { return points.get(index).position.x; }
            case "y", "Y" -> { return points.get(index).position.y; }
            case "z", "Z" -> { return points.get(index).position.z; }
            
            default -> { 
                Logger.log(LogLevel.WARNING, "Invalid axis: \"" + axis + "\" value specified, must be one of X, Y, or Z.");
                return 0; 
            }
        }
    }
    
    void setPointPos(int index, String axis, float value) {
        if(!points.containsKey(index)) {
            Logger.log(LogLevel.WARNING, "No vertex with an ID of: (" + index + ") exists in this shape.");
        }
        
        switch(axis) {
            case "x", "X" -> {
                points.get(index).position.x = value; 
                updateData = true;
            }
            
            case "y", "Y" -> {
                points.get(index).position.y = value;
                updateData = true;
            }
            
            case "z", "Z" -> {
                points.get(index).position.z = value;
                updateData = true;
            }
            
            default -> {
                Logger.log(LogLevel.WARNING, "Invalid axis: \"" + axis + "\" value specified, must be one of X, Y, or Z.");
            }
        }
    }
    
    void setPointPos(int index, float x, float y, float z) {
        if(points.containsKey(index)) {
            points.get(index).position.set(x, y, z);
            updateData = true;
        } else {
            Logger.log(LogLevel.WARNING, "No vertex with an ID of: (" + index + ") exists in this shape.");
        }
    }
    
    void resetPointPos(int index, String axis) {
        float value;
        
        switch(axis) {
            case "x", "X" -> { value = initialPointPositions[index].x; }
            case "y", "Y" -> { value = initialPointPositions[index].y; }
            case "z", "Z" -> { value = initialPointPositions[index].z; }
            
            default -> {
                Logger.log(LogLevel.WARNING, "Invalid axis: \"" + axis + "\" value specified, must be one of X, Y, or Z.");
                return;
            }
        }
        
        setPointPos(index, axis, value);
    }
    
    void resetPointPos() {
        boolean alreadyReset = true;
        
        for(int v = 0; v < initialPointPositions.length; v++) {
            alreadyReset = initialPointPositions[v].equals(points.get(v).position);
            if(!alreadyReset) break;
        }
        
        if(!alreadyReset) {
            for(int v = 0; v < initialPointPositions.length; v++) {
                float yPos = (initialPointPositions[v].y == CELL_SIZE) ? height : initialPointPositions[v].y;
                points.get(v).position = new Vector3f(initialPointPositions[v].x, yPos, initialPointPositions[v].z);
            }
            
            updateData = true;
        }
    }
    
}