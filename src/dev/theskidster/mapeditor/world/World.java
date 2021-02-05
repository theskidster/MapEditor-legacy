package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.main.ShaderProgram;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector3f;
import org.joml.RayAabIntersection;
import org.joml.Vector2i;
import org.joml.Vector3i;

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
    private final GeometryBatch geomBatch    = new GeometryBatch();
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
    
    public void update() {}
    
    public void render(ShaderProgram program) {
        floor.draw(program, tiles);
        
        geomBatch.batchStart(shapes.size());
            shapes.forEach((id, shape) -> geomBatch.drawGeometry(shape));
        geomBatch.batchEnd(program);
        
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
        shapes.put(currIndex, new Geometry(cursorLocation));
    }
    
    public void stretchGeometry(float verticalChange, boolean ctrlHeld) {
        Geometry shape = shapes.get(currIndex);
        
        if(ctrlHeld) {
            shapeHeight += verticalChange;
            shapeHeight = (shapeHeight > height) ? height : shapeHeight;
            
            if((int) shapeHeight > 0) {
                shape.height = (int) shapeHeight;

                shape.vertices.get(1).y = shape.height;
                shape.vertices.get(2).y = shape.height;
                shape.vertices.get(6).y = shape.height;
                shape.vertices.get(7).y = shape.height;
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
                            shape.resetVertexAxis(0, "z");
                            shape.resetVertexAxis(1, "z");
                            shape.vertices.get(2).set(cursorLocation.x + CELL_SIZE, shape.vertices.get(2).y, initialLocation.z);
                            shape.vertices.get(3).set(cursorLocation.x + CELL_SIZE, shape.vertices.get(3).y, initialLocation.z);
                            shape.vertices.get(4).set(cursorLocation.x + CELL_SIZE, shape.vertices.get(4).y, cursorLocation.z + CELL_SIZE);
                            shape.vertices.get(5).set(initialLocation.x,            shape.vertices.get(5).y, cursorLocation.z + CELL_SIZE);
                            shape.vertices.get(6).set(initialLocation.x,            shape.vertices.get(6).y, cursorLocation.z + CELL_SIZE);
                            shape.vertices.get(7).set(cursorLocation.x + CELL_SIZE, shape.vertices.get(7).y, cursorLocation.z + CELL_SIZE);
                        } else if(locationDiff.z < 0) {
                            shape.vertices.get(0).set(initialLocation.x,            shape.vertices.get(0).y, cursorLocation.z);
                            shape.vertices.get(1).set(initialLocation.x,            shape.vertices.get(1).y, cursorLocation.z);
                            shape.vertices.get(2).set(cursorLocation.x + CELL_SIZE, shape.vertices.get(2).y, cursorLocation.z);
                            shape.vertices.get(3).set(cursorLocation.x + CELL_SIZE, shape.vertices.get(3).y, cursorLocation.z);
                            shape.vertices.get(4).set(cursorLocation.x + CELL_SIZE, shape.vertices.get(4).y, initialLocation.z + CELL_SIZE);
                            shape.resetVertexAxis(5, "z");
                            shape.resetVertexAxis(6, "z");
                            shape.vertices.get(7).set(cursorLocation.x + CELL_SIZE, shape.vertices.get(7).y, initialLocation.z + CELL_SIZE);
                        } else {
                            shape.resetVertexAxis(0, "z");
                            shape.resetVertexAxis(1, "z");
                            shape.vertices.get(2).set(cursorLocation.x + CELL_SIZE, shape.vertices.get(2).y, initialLocation.z);
                            shape.vertices.get(3).set(cursorLocation.x + CELL_SIZE, shape.vertices.get(3).y, initialLocation.z);
                            shape.vertices.get(4).set(cursorLocation.x + CELL_SIZE, shape.vertices.get(4).y, initialLocation.z + CELL_SIZE);
                            shape.resetVertexAxis(5, "z");
                            shape.resetVertexAxis(6, "z");
                            shape.vertices.get(7).set(cursorLocation.x + CELL_SIZE, shape.vertices.get(7).y, initialLocation.z + CELL_SIZE);
                        }
                    } else if(locationDiff.x < 0) {
                        if(locationDiff.z > 0) {
                            shape.vertices.get(0).set(cursorLocation.x, shape.vertices.get(0).y, initialLocation.z);
                            shape.vertices.get(1).set(cursorLocation.x, shape.vertices.get(1).y, initialLocation.z);
                            shape.resetVertexAxis(2, "z");
                            shape.resetVertexAxis(3, "z");
                            shape.vertices.get(4).set(initialLocation.x + CELL_SIZE, shape.vertices.get(4).y, cursorLocation.z + CELL_SIZE);
                            shape.vertices.get(5).set(cursorLocation.x,              shape.vertices.get(5).y, cursorLocation.z + CELL_SIZE);
                            shape.vertices.get(6).set(cursorLocation.x,              shape.vertices.get(6).y, cursorLocation.z + CELL_SIZE);
                            shape.vertices.get(7).set(initialLocation.x + CELL_SIZE, shape.vertices.get(7).y, cursorLocation.z + CELL_SIZE);
                        } else if(locationDiff.z < 0) {
                            shape.vertices.get(0).set(cursorLocation.x,              shape.vertices.get(0).y, cursorLocation.z);
                            shape.vertices.get(1).set(cursorLocation.x,              shape.vertices.get(1).y, cursorLocation.z);
                            shape.vertices.get(2).set(initialLocation.x + CELL_SIZE, shape.vertices.get(2).y, cursorLocation.z);
                            shape.vertices.get(3).set(initialLocation.x + CELL_SIZE, shape.vertices.get(3).y, cursorLocation.z);
                            shape.resetVertexAxis(4, "z");
                            shape.vertices.get(5).set(cursorLocation.x,              shape.vertices.get(5).y, initialLocation.z + CELL_SIZE);
                            shape.vertices.get(6).set(cursorLocation.x,              shape.vertices.get(6).y, initialLocation.z + CELL_SIZE);
                            shape.resetVertexAxis(7, "z");
                        } else {
                            shape.vertices.get(0).set(cursorLocation.x, shape.vertices.get(0).y, initialLocation.z);
                            shape.vertices.get(1).set(cursorLocation.x, shape.vertices.get(1).y, initialLocation.z);
                            shape.resetVertexAxis(2, "z");
                            shape.resetVertexAxis(3, "z");
                            shape.resetVertexAxis(4, "z");
                            shape.vertices.get(5).set(cursorLocation.x, shape.vertices.get(5).y, initialLocation.z + CELL_SIZE);
                            shape.vertices.get(6).set(cursorLocation.x, shape.vertices.get(6).y, initialLocation.z + CELL_SIZE);
                            shape.resetVertexAxis(7, "z");
                        }
                    } else {
                        if(locationDiff.z > 0) {
                            shape.resetVertexAxis(0, "x");
                            shape.resetVertexAxis(1, "x");
                            shape.resetVertexAxis(2, "x");
                            shape.resetVertexAxis(3, "x");
                            shape.vertices.get(4).set(initialLocation.x + CELL_SIZE, shape.vertices.get(4).y, cursorLocation.z + CELL_SIZE);
                            shape.vertices.get(5).set(initialLocation.x,             shape.vertices.get(5).y, cursorLocation.z + CELL_SIZE);
                            shape.vertices.get(6).set(initialLocation.x,             shape.vertices.get(6).y, cursorLocation.z + CELL_SIZE);
                            shape.vertices.get(7).set(initialLocation.x + CELL_SIZE, shape.vertices.get(7).y, cursorLocation.z + CELL_SIZE);
                        } else if(locationDiff.z < 0) {
                            shape.vertices.get(0).set(initialLocation.x,             shape.vertices.get(0).y, cursorLocation.z);
                            shape.vertices.get(1).set(initialLocation.x,             shape.vertices.get(1).y, cursorLocation.z);
                            shape.vertices.get(2).set(initialLocation.x + CELL_SIZE, shape.vertices.get(2).y, cursorLocation.z);
                            shape.vertices.get(3).set(initialLocation.x + CELL_SIZE, shape.vertices.get(3).y, cursorLocation.z);
                            shape.resetVertexAxis(4, "x");
                            shape.resetVertexAxis(5, "x");
                            shape.resetVertexAxis(6, "x");
                            shape.resetVertexAxis(7, "x");
                        }
                    }
                } else {
                    shape.resetVertices();
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