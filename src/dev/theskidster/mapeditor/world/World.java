package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.main.ShaderProgram;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector3f;
import org.joml.RayAabIntersection;
import org.joml.Vector2i;
import org.joml.Vector3i;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author J Hoffman
 * Created: Dec 16, 2020
 */

/**
 * Represents a three-dimensional space that will contain all renderable objects.
 */
public class World {
    
    public static final float CELL_SIZE = 1f;
    
    private final int width;
    private final int height;
    private final int depth;
    private int currIndex;
    
    private float shapeHeight;
    
    private final Floor floor                = new Floor();
    private final RayAabIntersection rayTest = new RayAabIntersection();
    private final Vector3i initialLocation   = new Vector3i();
    private final Vector3i cursorLocation    = new Vector3i();
    private final Vector3i locationDiff      = new Vector3i();
    
    private final Origin origin;
    
    private final Map<Vector2i, Boolean> tiles;
    private final Map<Integer, Geometry> shapes = new HashMap<>();
    
    public World(int width, int height, int depth) {
        this.width  = width;
        this.height = height;
        this.depth  = depth;
        
        origin = new Origin(width, height, depth);
        
        tiles = new HashMap<>() {{
            for(int w = -(width / 2); w < width / 2; w++) {
                for(int d = -(depth / 2); d < depth / 2; d++) {
                    put(new Vector2i(w, d), false);
                }
            }
        }};
    }
    
    public void update() {
        shapes.forEach((id, shape) -> shape.update());
    }
    
    public void render(ShaderProgram program) {
        floor.draw(program, tiles);
        
        glEnable(GL_DEPTH_TEST);
        shapes.forEach((id, shape) -> shape.render(program));
        glDisable(GL_DEPTH_TEST);
        
        origin.render(program);
    }
    
    public void selectTile(Vector3f camPos, Vector3f camRay) {
        rayTest.set(camPos.x, camPos.y, camPos.z, camRay.x, camRay.y, camRay.z);
        
        tiles.entrySet().forEach((entry) -> {
            Vector2i location = entry.getKey();
            entry.setValue(rayTest.test(location.x, 0, location.y, location.x + CELL_SIZE, 0, location.y + CELL_SIZE));
        });
    }
    
    public void addGeometry() {
        if(tiles.containsValue(true)) {
            Vector2i tileLocation = tiles.entrySet().stream().filter(entry -> entry.getValue()).findAny().get().getKey();
            cursorLocation.set(tileLocation.x, 0, tileLocation.y);
            initialLocation.set(cursorLocation);
        } else {
            //TODO: find cell with a y position above the floor
        }
        
        currIndex = shapes.size() + 1;
        shapes.put(currIndex, new Geometry(cursorLocation.x, cursorLocation.z));
    }
    
    public void stretchGeometry(float verticalChange, boolean ctrlHeld) {
        Geometry shape = shapes.get(currIndex);
        
        if(ctrlHeld) {
            shapeHeight += verticalChange;
            shapeHeight = (shapeHeight > height) ? height : shapeHeight;
            
            if((int) shapeHeight > 0) {
                shape.height = (int) shapeHeight;

                shape.setVertexPos(2, "y", shape.height);
                shape.setVertexPos(3, "y", shape.height);
                shape.setVertexPos(6, "y", shape.height);
                shape.setVertexPos(7, "y", shape.height);
            }
        } else {
            if(tiles.containsValue(true)) {
                Vector2i tileLocation = tiles.entrySet().stream().filter(entry -> entry.getValue()).findAny().get().getKey();
                cursorLocation.set(tileLocation.x, 0, tileLocation.y);
                
                if(!cursorLocation.equals(initialLocation)) {
                    cursorLocation.sub(initialLocation, locationDiff);
                    
                    /*
                    There's probably a more elegant mathematical solution to 
                    this- but I'm too dumb to implement it.
                    */
                    if(locationDiff.x > 0) {
                        if(locationDiff.z > 0) {
                            shape.setVertexPos(0, initialLocation.x,            shape.getVertexPos(0, "y"), cursorLocation.z + CELL_SIZE);
                            shape.setVertexPos(1, cursorLocation.x + CELL_SIZE, shape.getVertexPos(1, "y"), cursorLocation.z + CELL_SIZE);
                            shape.setVertexPos(2, cursorLocation.x + CELL_SIZE, shape.getVertexPos(2, "y"), cursorLocation.z + CELL_SIZE);
                            shape.setVertexPos(3, initialLocation.x,            shape.getVertexPos(3, "y"), cursorLocation.z + CELL_SIZE);
                            shape.resetVertexPos(4, "z");
                            shape.setVertexPos(5, cursorLocation.x + CELL_SIZE, shape.getVertexPos(5, "y"), initialLocation.z);
                            shape.setVertexPos(6, cursorLocation.x + CELL_SIZE, shape.getVertexPos(6, "y"), initialLocation.z);
                            shape.resetVertexPos(7, "z");
                        } else if(locationDiff.z < 0) {
                            shape.resetVertexPos(0, "z");
                            shape.setVertexPos(1, cursorLocation.x + CELL_SIZE, shape.getVertexPos(1, "y"), initialLocation.z + CELL_SIZE);
                            shape.setVertexPos(2, cursorLocation.x + CELL_SIZE, shape.getVertexPos(2, "y"), initialLocation.z + CELL_SIZE);
                            shape.resetVertexPos(3, "z");
                            shape.setVertexPos(4, initialLocation.x,            shape.getVertexPos(4, "y"), cursorLocation.z);
                            shape.setVertexPos(5, cursorLocation.x + CELL_SIZE, shape.getVertexPos(5, "y"), cursorLocation.z);
                            shape.setVertexPos(6, cursorLocation.x + CELL_SIZE, shape.getVertexPos(6, "y"), cursorLocation.z);
                            shape.setVertexPos(7, initialLocation.x,            shape.getVertexPos(7, "y"), cursorLocation.z);
                        } else {
                            shape.resetVertexPos(0, "z");
                            shape.setVertexPos(1, cursorLocation.x + CELL_SIZE, shape.getVertexPos(1, "y"), initialLocation.z + CELL_SIZE);
                            shape.setVertexPos(2, cursorLocation.x + CELL_SIZE, shape.getVertexPos(2, "y"), initialLocation.z + CELL_SIZE);
                            shape.resetVertexPos(3, "z");
                            shape.resetVertexPos(4, "z");
                            shape.setVertexPos(5, cursorLocation.x + CELL_SIZE, shape.getVertexPos(5, "y"), initialLocation.z);
                            shape.setVertexPos(6, cursorLocation.x + CELL_SIZE, shape.getVertexPos(6, "y"), initialLocation.z);
                            shape.resetVertexPos(7, "z");
                        }
                    } else if(locationDiff.x < 0) {
                        if(locationDiff.z > 0) {
                            shape.setVertexPos(0, cursorLocation.x,              shape.getVertexPos(0, "y"), cursorLocation.z + CELL_SIZE);
                            shape.setVertexPos(1, initialLocation.x + CELL_SIZE, shape.getVertexPos(1, "y"), cursorLocation.z + CELL_SIZE);
                            shape.setVertexPos(2, initialLocation.x + CELL_SIZE, shape.getVertexPos(2, "y"), cursorLocation.z + CELL_SIZE);
                            shape.setVertexPos(3, cursorLocation.x,              shape.getVertexPos(3, "y"), cursorLocation.z + CELL_SIZE);
                            shape.setVertexPos(4, cursorLocation.x,              shape.getVertexPos(4, "y"), initialLocation.z);
                            shape.resetVertexPos(5, "z");
                            shape.resetVertexPos(6, "z");
                            shape.setVertexPos(7, cursorLocation.x, shape.getVertexPos(7, "y"), initialLocation.z);
                        } else if(locationDiff.z < 0) {
                            shape.setVertexPos(0, cursorLocation.x, shape.getVertexPos(0, "y"), initialLocation.z + CELL_SIZE);
                            shape.resetVertexPos(1, "z");
                            shape.resetVertexPos(2, "z");
                            shape.setVertexPos(3, cursorLocation.x,              shape.getVertexPos(3, "y"), initialLocation.z + CELL_SIZE);
                            shape.setVertexPos(4, cursorLocation.x,              shape.getVertexPos(4, "y"), cursorLocation.z);
                            shape.setVertexPos(5, initialLocation.x + CELL_SIZE, shape.getVertexPos(5, "y"), cursorLocation.z);
                            shape.setVertexPos(6, initialLocation.x + CELL_SIZE, shape.getVertexPos(6, "y"), cursorLocation.z);
                            shape.setVertexPos(7, cursorLocation.x,              shape.getVertexPos(7, "y"), cursorLocation.z);
                        } else {
                            shape.setVertexPos(0, cursorLocation.x, shape.getVertexPos(0, "y"), initialLocation.z + CELL_SIZE);
                            shape.resetVertexPos(1, "z");
                            shape.resetVertexPos(2, "z");
                            shape.setVertexPos(3, cursorLocation.x, shape.getVertexPos(3, "y"), initialLocation.z + CELL_SIZE);
                            shape.setVertexPos(4, cursorLocation.x, shape.getVertexPos(4, "y"), initialLocation.z);
                            shape.resetVertexPos(5, "z");
                            shape.resetVertexPos(6, "z");
                            shape.setVertexPos(7, cursorLocation.x, shape.getVertexPos(7, "y"), initialLocation.z);
                        }
                    } else {
                        if(locationDiff.z > 0) {
                            shape.setVertexPos(0, initialLocation.x,             shape.getVertexPos(0, "y"), cursorLocation.z + CELL_SIZE);
                            shape.setVertexPos(1, initialLocation.x + CELL_SIZE, shape.getVertexPos(1, "y"), cursorLocation.z + CELL_SIZE);
                            shape.setVertexPos(2, initialLocation.x + CELL_SIZE, shape.getVertexPos(2, "y"), cursorLocation.z + CELL_SIZE);
                            shape.setVertexPos(3, initialLocation.x,             shape.getVertexPos(3, "y"), cursorLocation.z + CELL_SIZE);
                            shape.resetVertexPos(4, "x");
                            shape.resetVertexPos(5, "x");
                            shape.resetVertexPos(6, "x");
                            shape.resetVertexPos(7, "x");
                        } else if(locationDiff.z < 0) {
                            shape.resetVertexPos(0, "x");
                            shape.resetVertexPos(1, "x");
                            shape.resetVertexPos(2, "x");
                            shape.resetVertexPos(3, "x");
                            shape.setVertexPos(4, initialLocation.x,             shape.getVertexPos(4, "y"), cursorLocation.z);
                            shape.setVertexPos(5, initialLocation.x + CELL_SIZE, shape.getVertexPos(5, "y"), cursorLocation.z);
                            shape.setVertexPos(6, initialLocation.x + CELL_SIZE, shape.getVertexPos(6, "y"), cursorLocation.z);
                            shape.setVertexPos(7, initialLocation.x,             shape.getVertexPos(7, "y"), cursorLocation.z);
                        }
                    }
                } else {
                    shape.resetVertexPos();
                }
            } else {
                //TODO: find cell with a y position above the floor
            }
        }
    }
    
    public void finalizeGeometry() {
        shapeHeight = 0;
    }

}