package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.graphics.LightSource;
import dev.theskidster.mapeditor.graphics.Texture;
import dev.theskidster.mapeditor.main.App;
import static dev.theskidster.mapeditor.main.App.SELECT_TOOL;
import dev.theskidster.mapeditor.main.LogLevel;
import dev.theskidster.mapeditor.main.Logger;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import static dev.theskidster.mapeditor.world.World.CELL_SIZE;
import java.nio.BufferOverflowException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.joml.Intersectionf;
import org.joml.Matrix3f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector3i;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryUtil;

/**
 * @author J Hoffman
 * Created: Feb 12, 2021
 */

final class Geometry {

    private final int FLOATS_PER_VERTEX = 8;
    private int numVertices;
    private int bufferSizeInBytes;
    private int vpOffset;
    private int fOffset;
    float shapeHeight;
    
    private final int vao = glGenVertexArrays();
    private final int vbo = glGenBuffers();
    
    private final int[] offsets = new int[8];
    
    private boolean updateData;
    
    private final Matrix3f normal       = new Matrix3f();
    private final Vector3i locationDiff = new Vector3i();
    private Texture texture;
    
    private Map<Integer, Vector2f> texCoords;
    private Map<Integer, Vector3f> normals;
    private LinkedHashMap<Integer, Vector3f> initialVPs      = new LinkedHashMap<>();
    private LinkedHashMap<Integer, Vector3f> vertexPositions = new LinkedHashMap<>();
    private LinkedHashMap<Integer, Face> faces               = new LinkedHashMap<>();
    
    private Vector3f color        = Color.convert(Color.WHITE);
    private Vector2f sphereResult = new Vector2f();
    
    Geometry(String filename) {
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
        
        findBufferSize();
        
        glVertexAttribPointer(0, 3, GL_FLOAT, false, (FLOATS_PER_VERTEX * Float.BYTES), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, (FLOATS_PER_VERTEX * Float.BYTES), (3 * Float.BYTES));
        glVertexAttribPointer(2, 3, GL_FLOAT, false, (FLOATS_PER_VERTEX * Float.BYTES), (5 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
        
        texture = new Texture(filename);
        
        glBindTexture(GL_TEXTURE_2D, texture.handle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glBindTexture(GL_TEXTURE_2D, 0);
    }
    
    void findBufferSize() {
        numVertices       = faces.size() * 3;
        bufferSizeInBytes = numVertices * FLOATS_PER_VERTEX * Float.BYTES;
        
        glBindVertexArray(vao);
        
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, bufferSizeInBytes, GL_DYNAMIC_DRAW);
    }
    
    void draw(ShaderProgram program, LightSource[] lights, int numLights) {
        if(updateData) {
            try {
                findBufferSize();
                
                FloatBuffer vertices = MemoryUtil.memAllocFloat(bufferSizeInBytes);
                
                faces.forEach((index, face) -> {
                    for(int i = 0; i < 3; i++) {
                        Vector3f pos    = vertexPositions.get(face.vp[i]);
                        Vector2f coords = texCoords.get(face.tc[i]);
                        Vector3f norm   = normals.get(face.n);
                        
                        vertices.put(pos.x).put(pos.y).put(pos.z)
                                .put(coords.x).put(coords.y)
                                .put(norm.x).put(norm.y).put(norm.z);
                    }
                });
                
                vertices.flip();
                
                glBindBuffer(GL_ARRAY_BUFFER, vbo);
                glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
                
            } catch(BufferOverflowException e) {
                Logger.setStackTrace(e);
                Logger.log(LogLevel.SEVERE, "Geometry buffer experienced overflow.");
            }
            
            updateData = false;
        }
        
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        
        glBindVertexArray(vao);
        glBindTexture(GL_TEXTURE_2D, texture.handle);
        
        program.setUniform("uType", 2);
        program.setUniform("uNormal", true, normal);
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
                    program.setUniform("uLights[" + i + "].position",   App.noValue());
                    program.setUniform("uLights[" + i + "].ambient",    App.noValue());
                    program.setUniform("uLights[" + i + "].diffuse",    App.noValue());
                }
            }
        }
        
        glDrawArrays(GL_TRIANGLES, 0, numVertices);
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        
        if(World.currTool == SELECT_TOOL) {
            glPointSize(6);
            glBindVertexArray(vao);
            
            program.setUniform("uType", 0);
            program.setUniform("uColor", color);
            
            glDrawArrays(GL_POINTS, 0, numVertices);
            glPointSize(1);
        }
        
        App.checkGLError();
    }
    
    void addShape(float x, float y, float z) {
        shapeHeight = 1;
        
        vpOffset = vertexPositions.size();
        fOffset  = faces.size();
        
        vertexPositions.put(vpOffset,     new Vector3f(x,             y,             z + CELL_SIZE));
        vertexPositions.put(vpOffset + 1, new Vector3f(x + CELL_SIZE, y,             z + CELL_SIZE));
        vertexPositions.put(vpOffset + 2, new Vector3f(x + CELL_SIZE, y + CELL_SIZE, z + CELL_SIZE));
        vertexPositions.put(vpOffset + 3, new Vector3f(x,             y + CELL_SIZE, z + CELL_SIZE));
        vertexPositions.put(vpOffset + 4, new Vector3f(x,             y,             z));
        vertexPositions.put(vpOffset + 5, new Vector3f(x + CELL_SIZE, y,             z));
        vertexPositions.put(vpOffset + 6, new Vector3f(x + CELL_SIZE, y + CELL_SIZE, z));
        vertexPositions.put(vpOffset + 7, new Vector3f(x,             y + CELL_SIZE, z));
        
        initialVPs.clear();
        
        for(int i = vpOffset; i < vpOffset + 8; i++) {
            initialVPs.put(i, new Vector3f(vertexPositions.get(i)));
        }
        
        //FRONT:
        faces.put(fOffset,     new Face(new int[]{vpOffset,     vpOffset + 1, vpOffset + 2}, new int[]{2, 3, 0}, 2));
        faces.put(fOffset + 1, new Face(new int[]{vpOffset + 2, vpOffset + 3, vpOffset},     new int[]{0, 1, 2}, 2));
        //RIGHT:
        faces.put(fOffset + 2, new Face(new int[]{vpOffset + 1, vpOffset + 5, vpOffset + 6}, new int[]{2, 3, 0}, 0));
        faces.put(fOffset + 3, new Face(new int[]{vpOffset + 6, vpOffset + 2, vpOffset + 1}, new int[]{0, 1, 2}, 0));
        //BACK:
        faces.put(fOffset + 4, new Face(new int[]{vpOffset + 7, vpOffset + 6, vpOffset + 5}, new int[]{0, 1, 2}, 5));
        faces.put(fOffset + 5, new Face(new int[]{vpOffset + 5, vpOffset + 4, vpOffset + 7}, new int[]{2, 3, 0}, 5));
        //LEFT:
        faces.put(fOffset + 6, new Face(new int[]{vpOffset + 4, vpOffset,     vpOffset + 3}, new int[]{2, 3, 0}, 3));
        faces.put(fOffset + 7, new Face(new int[]{vpOffset + 3, vpOffset + 7, vpOffset + 4}, new int[]{0, 1, 2}, 3));
        //BOTTOM:
        faces.put(fOffset + 8, new Face(new int[]{vpOffset + 4, vpOffset + 5, vpOffset + 1}, new int[]{2, 3, 0}, 4));
        faces.put(fOffset + 9, new Face(new int[]{vpOffset + 1, vpOffset,     vpOffset + 4}, new int[]{0, 1, 2}, 4));
        //TOP:
        faces.put(fOffset + 10, new Face(new int[]{vpOffset + 3, vpOffset + 2, vpOffset + 6}, new int[]{2, 3, 0}, 1));
        faces.put(fOffset + 11, new Face(new int[]{vpOffset + 6, vpOffset + 7, vpOffset + 3}, new int[]{0, 1, 2}, 1));
        
        updateData = true;
    }
    
    void stretchShape(float verticalChange, boolean ctrlHeld, World world) {
        if(ctrlHeld) {
            shapeHeight += verticalChange;
            shapeHeight = (shapeHeight > world.height) ? world.height : shapeHeight;
            shapeHeight = (shapeHeight < 1) ? 1 : shapeHeight;
            
            if((int) shapeHeight > 0) {
                setVertexPos(vpOffset + 2, "y", (int) shapeHeight);
                setVertexPos(vpOffset + 3, "y", (int) shapeHeight);
                setVertexPos(vpOffset + 6, "y", (int) shapeHeight);
                setVertexPos(vpOffset + 7, "y", (int) shapeHeight);
            }
        } else {
            if(world.tiles.containsValue(true)) {
                Vector2i tileLocation = world.tiles.entrySet().stream().filter(entry -> entry.getValue()).findAny().get().getKey();
                world.cursorLocation.set(tileLocation.x, 0, tileLocation.y);

                if(!world.cursorLocation.equals(world.initialLocation)) {
                    world.cursorLocation.sub(world.initialLocation, locationDiff);
                    
                    for(int i = 0; i < offsets.length; i++) offsets[i] = vpOffset + i;
                    
                    /*
                    There's probably a more elegant mathematical solution to 
                    this- but I'm too dumb to implement it.
                    */
                    if(locationDiff.x > 0) {
                        if(locationDiff.z > 0) {
                            setVertexPos(offsets[0], world.initialLocation.x,            getVertexPos(offsets[0]).y, world.cursorLocation.z + CELL_SIZE);
                            setVertexPos(offsets[1], world.cursorLocation.x + CELL_SIZE, getVertexPos(offsets[1]).y, world.cursorLocation.z + CELL_SIZE);
                            setVertexPos(offsets[2], world.cursorLocation.x + CELL_SIZE, getVertexPos(offsets[2]).y, world.cursorLocation.z + CELL_SIZE);
                            setVertexPos(offsets[3], world.initialLocation.x,            getVertexPos(offsets[3]).y, world.cursorLocation.z + CELL_SIZE);
                            resetVertexPos(offsets[4], "z");
                            setVertexPos(offsets[5], world.cursorLocation.x + CELL_SIZE, getVertexPos(offsets[5]).y, world.initialLocation.z);
                            setVertexPos(offsets[6], world.cursorLocation.x + CELL_SIZE, getVertexPos(offsets[6]).y, world.initialLocation.z);
                            resetVertexPos(offsets[7], "z");
                        } else if(locationDiff.z < 0) {
                            resetVertexPos(offsets[0], "z");
                            setVertexPos(offsets[1], world.cursorLocation.x + CELL_SIZE, getVertexPos(offsets[1]).y, world.initialLocation.z + CELL_SIZE);
                            setVertexPos(offsets[2], world.cursorLocation.x + CELL_SIZE, getVertexPos(offsets[2]).y, world.initialLocation.z + CELL_SIZE);
                            resetVertexPos(offsets[3], "z");
                            setVertexPos(offsets[4], world.initialLocation.x,            getVertexPos(offsets[4]).y, world.cursorLocation.z);
                            setVertexPos(offsets[5], world.cursorLocation.x + CELL_SIZE, getVertexPos(offsets[5]).y, world.cursorLocation.z);
                            setVertexPos(offsets[6], world.cursorLocation.x + CELL_SIZE, getVertexPos(offsets[6]).y, world.cursorLocation.z);
                            setVertexPos(offsets[7], world.initialLocation.x,            getVertexPos(offsets[7]).y, world.cursorLocation.z);
                        } else {
                            resetVertexPos(offsets[0], "z");
                            setVertexPos(offsets[1], world.cursorLocation.x + CELL_SIZE, getVertexPos(offsets[1]).y, world.cursorLocation.z + CELL_SIZE);
                            setVertexPos(offsets[2], world.cursorLocation.x + CELL_SIZE, getVertexPos(offsets[2]).y, world.cursorLocation.z + CELL_SIZE);
                            resetVertexPos(offsets[3], "z");
                            resetVertexPos(offsets[4], "z");
                            setVertexPos(offsets[5], world.cursorLocation.x + CELL_SIZE, getVertexPos(offsets[5]).y, world.initialLocation.z);
                            setVertexPos(offsets[6], world.cursorLocation.x + CELL_SIZE, getVertexPos(offsets[6]).y, world.initialLocation.z);
                            resetVertexPos(offsets[7], "z");
                        }
                    } else if(locationDiff.x < 0) {
                        if(locationDiff.z > 0) {
                            setVertexPos(offsets[0], world.cursorLocation.x,              getVertexPos(offsets[0]).y, world.cursorLocation.z + CELL_SIZE);
                            setVertexPos(offsets[1], world.initialLocation.x + CELL_SIZE, getVertexPos(offsets[1]).y, world.cursorLocation.z + CELL_SIZE);
                            setVertexPos(offsets[2], world.initialLocation.x + CELL_SIZE, getVertexPos(offsets[2]).y, world.cursorLocation.z + CELL_SIZE);
                            setVertexPos(offsets[3], world.cursorLocation.x,              getVertexPos(offsets[3]).y, world.cursorLocation.z + CELL_SIZE);
                            setVertexPos(offsets[4], world.cursorLocation.x,              getVertexPos(offsets[4]).y, world.initialLocation.z);
                            resetVertexPos(offsets[5], "z");
                            resetVertexPos(offsets[6], "z");
                            setVertexPos(offsets[7], world.cursorLocation.x, getVertexPos(offsets[7]).y, world.initialLocation.z);
                        } else if(locationDiff.z < 0) {
                            setVertexPos(offsets[0], world.cursorLocation.x, getVertexPos(offsets[0]).y, world.initialLocation.z + CELL_SIZE);
                            resetVertexPos(offsets[1], "z");
                            resetVertexPos(offsets[2], "z");
                            setVertexPos(offsets[3], world.cursorLocation.x,              getVertexPos(offsets[3]).y, world.initialLocation.z + CELL_SIZE);
                            setVertexPos(offsets[4], world.cursorLocation.x,              getVertexPos(offsets[4]).y, world.cursorLocation.z);
                            setVertexPos(offsets[5], world.initialLocation.x + CELL_SIZE, getVertexPos(offsets[5]).y, world.cursorLocation.z);
                            setVertexPos(offsets[6], world.initialLocation.x + CELL_SIZE, getVertexPos(offsets[6]).y, world.cursorLocation.z);
                            setVertexPos(offsets[7], world.cursorLocation.x,              getVertexPos(offsets[7]).y, world.cursorLocation.z);
                        } else {
                            setVertexPos(offsets[0], world.cursorLocation.x, getVertexPos(offsets[0]).y, world.initialLocation.z + CELL_SIZE);
                            resetVertexPos(offsets[1], "z");
                            resetVertexPos(offsets[2], "z");
                            setVertexPos(offsets[3], world.cursorLocation.x, getVertexPos(offsets[3]).y, world.initialLocation.z + CELL_SIZE);
                            setVertexPos(offsets[4], world.cursorLocation.x, getVertexPos(offsets[4]).y, world.initialLocation.z);
                            resetVertexPos(offsets[5], "z");
                            resetVertexPos(offsets[6], "z");
                            setVertexPos(offsets[7], world.cursorLocation.x, getVertexPos(offsets[7]).y, world.initialLocation.z);
                        }
                    } else {
                        if(locationDiff.z > 0) {
                            setVertexPos(offsets[0], world.initialLocation.x,             getVertexPos(offsets[0]).y, world.cursorLocation.z + CELL_SIZE);
                            setVertexPos(offsets[1], world.initialLocation.x + CELL_SIZE, getVertexPos(offsets[1]).y, world.cursorLocation.z + CELL_SIZE);
                            setVertexPos(offsets[2], world.initialLocation.x + CELL_SIZE, getVertexPos(offsets[2]).y, world.cursorLocation.z + CELL_SIZE);
                            setVertexPos(offsets[3], world.initialLocation.x,             getVertexPos(offsets[3]).y, world.cursorLocation.z + CELL_SIZE);
                            resetVertexPos(offsets[4], "x");
                            resetVertexPos(offsets[5], "x");
                            resetVertexPos(offsets[6], "x");
                            resetVertexPos(offsets[7], "x");
                        } else if(locationDiff.z < 0) {
                            resetVertexPos(offsets[0], "x");
                            resetVertexPos(offsets[1], "x");
                            resetVertexPos(offsets[2], "x");
                            resetVertexPos(offsets[3], "x");
                            setVertexPos(offsets[4], world.initialLocation.x,             getVertexPos(offsets[4]).y, world.cursorLocation.z);
                            setVertexPos(offsets[5], world.initialLocation.x + CELL_SIZE, getVertexPos(offsets[5]).y, world.cursorLocation.z);
                            setVertexPos(offsets[6], world.initialLocation.x + CELL_SIZE, getVertexPos(offsets[6]).y, world.cursorLocation.z);
                            setVertexPos(offsets[7], world.initialLocation.x,             getVertexPos(offsets[7]).y, world.cursorLocation.z);
                        }
                    }
                } else {
                    resetVertexPos();
                }
            }
        }
    }
    
    private void resetVertexPos(int index, String axis) {
        float value;
        
        switch(axis) {
            case "x", "X" -> value = initialVPs.get(index).x;
            case "y", "Y" -> value = initialVPs.get(index).y;
            case "z", "Z" -> value = initialVPs.get(index).z;
            default -> { return; }
        }
        
        setVertexPos(index, axis, value);
    }
    
    private void resetVertexPos() {
        boolean alreadyReset = true;
        
        for(int i = vpOffset; i < vpOffset + 7; i++) {
            alreadyReset = initialVPs.get(i).equals(vertexPositions.get(i));
            if(!alreadyReset) break;
        }
        
        if(!alreadyReset) {
            for(int i = vpOffset; i < vpOffset + 8; i++) {
                float yPos = (initialVPs.get(i).y == CELL_SIZE) ? (int) shapeHeight : initialVPs.get(i).y;
                vertexPositions.put(i, new Vector3f(initialVPs.get(i).x, yPos, initialVPs.get(i).z));
            }
            
            updateData = true;
        }
    }
    
    List<Integer> hoverVertex(Vector3f camPos, Vector3f camRay, Vector3f camDir) {
        List<Integer> vertices = new LinkedList<>();
        
        vertexPositions.forEach((index, position) -> {
            float distance = (float) Math.sqrt(
                    Math.pow((position.x - camPos.x), 2) + 
                    Math.pow((position.y - camPos.y), 2) +
                    Math.pow((position.z - camPos.z), 2)) *
                    0.0003f;

            if(Intersectionf.testRaySphere(camPos, camRay, position, distance)) {
                vertices.add(index);
            }
        });
        
        if(vertices.size() > 0) {
            color.set(Color.RED.r, Color.RED.g, Color.RED.b);
        } else {
            color.set(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b);
        }
        
        return vertices;
    }
    
    public static boolean testRaySphere(Vector3fc origin, Vector3fc dir, Vector3fc center, float radiusSquared, String d) {
        System.out.println(d);
        return testRaySphere(origin.x(), origin.y(), origin.z(), dir.x(), dir.y(), dir.z(), center.x(), center.y(), center.z(), radiusSquared);
    }
    
    public static boolean testRaySphere(float originX, float originY, float originZ, float dirX, float dirY, float dirZ,
            float centerX, float centerY, float centerZ, float radiusSquared) {
        
        float Lx = centerX - originX;
        float Ly = centerY - originY;
        float Lz = centerZ - originZ;
        
        //System.out.println("L: " + Lx + ", " + Ly + ", " + Lz);
        
        float tca = Lx * dirX + Ly * dirY + Lz * dirZ;
        
        System.out.println("TCA: " + tca);
        
        float d2 = Lx * Lx + Ly * Ly + Lz * Lz - tca * tca;
        
        System.out.println("D2: " + d2);
        
        if (d2 > radiusSquared) {
            System.out.println("false");
            return false;
        }
        float thc = (float) org.joml.Math.sqrt(radiusSquared - d2);
        float t0 = tca - thc;
        float t1 = tca + thc;
        
        //System.out.println("thc: " + thc);
        //System.out.println("t0: " + t0);
        //System.out.println("t1: " + t1);
        
        boolean result = t0 < t1 && t1 >= 0.0f;
        
        if(result) {
            System.out.println("true");
        }
        
        return result;
    }
    
    Vector3f getVertexPos(int index) { return vertexPositions.get(index); }
    
    void setVertexPos(int index, String axis, float value) {
        switch(axis) {
            case "x", "X" -> vertexPositions.get(index).x = value;
            case "y", "Y" -> vertexPositions.get(index).y = value;
            case "z", "Z" -> vertexPositions.get(index).z = value;
        }
        
        updateData = true;
    }
    
    void setVertexPos(int index, float x, float y, float z) {
        vertexPositions.get(index).set(x, y, z);
        updateData = true;
    }
    
}