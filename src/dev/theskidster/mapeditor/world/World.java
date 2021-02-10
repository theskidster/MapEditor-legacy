package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.graphics.Light;
import dev.theskidster.mapeditor.graphics.LightSource;
import dev.theskidster.mapeditor.main.App;
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
    private int prevShapeIndex;
    private int currShapeIndex;
    private int numLights = 1;
    
    private float shapeHeight;
    
    private final Floor floor                = new Floor();
    private final RayAabIntersection rayTest = new RayAabIntersection();
    private final Vector3i initialLocation   = new Vector3i();
    private final Vector3i cursorLocation    = new Vector3i();
    private final Vector3i locationDiff      = new Vector3i();
    
    private final Cube cube;
    
    private final Origin origin;
    
    private final Map<Vector2i, Boolean> tiles;
    private final Map<Integer, Geometry> shapes = new HashMap<>();
    
    private final LightSource[] lights = new LightSource[App.MAX_LIGHTS];
    
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
        
        lights[0] = new LightSource(Light.NOON);
        
        cube = new Cube(new Vector3f(3, 3, -3));
    }
    
    public void update(Vector3f camRay) {
        shapes.forEach((id, shape) -> shape.update());
        
        for(LightSource light : lights) {
            if(light != null) light.update();
        }
        
        cube.update();
    }
    
    public void render(ShaderProgram program, Vector3f camPos, Vector3f camUp) {
        floor.draw(program, tiles);
        
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        shapes.forEach((id, shape) -> shape.render(program, lights, numLights));
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        
        cube.render(program);
        
        for(LightSource light : lights) {
            if(light != null) light.render(program, camPos, camUp);
        }
        
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
            
            prevShapeIndex = currShapeIndex;
            currShapeIndex = shapes.size() + 1;
            shapes.put(currShapeIndex, new Geometry(cursorLocation.x, cursorLocation.z));
        }
    }
    
    public void stretchGeometry(float verticalChange, boolean ctrlHeld) {
        if(prevShapeIndex != currShapeIndex) {
            Geometry shape = shapes.get(currShapeIndex);

            if(ctrlHeld) {
                shapeHeight += verticalChange;
                shapeHeight = (shapeHeight > height) ? height : shapeHeight;

                if((int) shapeHeight > 0) {
                    shape.height = (int) shapeHeight;

                    shape.setPointPos(2, "y", shape.height);
                    shape.setPointPos(3, "y", shape.height);
                    shape.setPointPos(6, "y", shape.height);
                    shape.setPointPos(7, "y", shape.height);
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
                                shape.setPointPos(0, initialLocation.x,            shape.getPointPos(0, "y"), cursorLocation.z + CELL_SIZE);
                                shape.setPointPos(1, cursorLocation.x + CELL_SIZE, shape.getPointPos(1, "y"), cursorLocation.z + CELL_SIZE);
                                shape.setPointPos(2, cursorLocation.x + CELL_SIZE, shape.getPointPos(2, "y"), cursorLocation.z + CELL_SIZE);
                                shape.setPointPos(3, initialLocation.x,            shape.getPointPos(3, "y"), cursorLocation.z + CELL_SIZE);
                                shape.resetPointPos(4, "z");
                                shape.setPointPos(5, cursorLocation.x + CELL_SIZE, shape.getPointPos(5, "y"), initialLocation.z);
                                shape.setPointPos(6, cursorLocation.x + CELL_SIZE, shape.getPointPos(6, "y"), initialLocation.z);
                                shape.resetPointPos(7, "z");
                            } else if(locationDiff.z < 0) {
                                shape.resetPointPos(0, "z");
                                shape.setPointPos(1, cursorLocation.x + CELL_SIZE, shape.getPointPos(1, "y"), initialLocation.z + CELL_SIZE);
                                shape.setPointPos(2, cursorLocation.x + CELL_SIZE, shape.getPointPos(2, "y"), initialLocation.z + CELL_SIZE);
                                shape.resetPointPos(3, "z");
                                shape.setPointPos(4, initialLocation.x,            shape.getPointPos(4, "y"), cursorLocation.z);
                                shape.setPointPos(5, cursorLocation.x + CELL_SIZE, shape.getPointPos(5, "y"), cursorLocation.z);
                                shape.setPointPos(6, cursorLocation.x + CELL_SIZE, shape.getPointPos(6, "y"), cursorLocation.z);
                                shape.setPointPos(7, initialLocation.x,            shape.getPointPos(7, "y"), cursorLocation.z);
                            } else {
                                shape.resetPointPos(0, "z");
                                shape.setPointPos(1, cursorLocation.x + CELL_SIZE, shape.getPointPos(1, "y"), initialLocation.z + CELL_SIZE);
                                shape.setPointPos(2, cursorLocation.x + CELL_SIZE, shape.getPointPos(2, "y"), initialLocation.z + CELL_SIZE);
                                shape.resetPointPos(3, "z");
                                shape.resetPointPos(4, "z");
                                shape.setPointPos(5, cursorLocation.x + CELL_SIZE, shape.getPointPos(5, "y"), initialLocation.z);
                                shape.setPointPos(6, cursorLocation.x + CELL_SIZE, shape.getPointPos(6, "y"), initialLocation.z);
                                shape.resetPointPos(7, "z");
                            }
                        } else if(locationDiff.x < 0) {
                            if(locationDiff.z > 0) {
                                shape.setPointPos(0, cursorLocation.x,              shape.getPointPos(0, "y"), cursorLocation.z + CELL_SIZE);
                                shape.setPointPos(1, initialLocation.x + CELL_SIZE, shape.getPointPos(1, "y"), cursorLocation.z + CELL_SIZE);
                                shape.setPointPos(2, initialLocation.x + CELL_SIZE, shape.getPointPos(2, "y"), cursorLocation.z + CELL_SIZE);
                                shape.setPointPos(3, cursorLocation.x,              shape.getPointPos(3, "y"), cursorLocation.z + CELL_SIZE);
                                shape.setPointPos(4, cursorLocation.x,              shape.getPointPos(4, "y"), initialLocation.z);
                                shape.resetPointPos(5, "z");
                                shape.resetPointPos(6, "z");
                                shape.setPointPos(7, cursorLocation.x, shape.getPointPos(7, "y"), initialLocation.z);
                            } else if(locationDiff.z < 0) {
                                shape.setPointPos(0, cursorLocation.x, shape.getPointPos(0, "y"), initialLocation.z + CELL_SIZE);
                                shape.resetPointPos(1, "z");
                                shape.resetPointPos(2, "z");
                                shape.setPointPos(3, cursorLocation.x,              shape.getPointPos(3, "y"), initialLocation.z + CELL_SIZE);
                                shape.setPointPos(4, cursorLocation.x,              shape.getPointPos(4, "y"), cursorLocation.z);
                                shape.setPointPos(5, initialLocation.x + CELL_SIZE, shape.getPointPos(5, "y"), cursorLocation.z);
                                shape.setPointPos(6, initialLocation.x + CELL_SIZE, shape.getPointPos(6, "y"), cursorLocation.z);
                                shape.setPointPos(7, cursorLocation.x,              shape.getPointPos(7, "y"), cursorLocation.z);
                            } else {
                                shape.setPointPos(0, cursorLocation.x, shape.getPointPos(0, "y"), initialLocation.z + CELL_SIZE);
                                shape.resetPointPos(1, "z");
                                shape.resetPointPos(2, "z");
                                shape.setPointPos(3, cursorLocation.x, shape.getPointPos(3, "y"), initialLocation.z + CELL_SIZE);
                                shape.setPointPos(4, cursorLocation.x, shape.getPointPos(4, "y"), initialLocation.z);
                                shape.resetPointPos(5, "z");
                                shape.resetPointPos(6, "z");
                                shape.setPointPos(7, cursorLocation.x, shape.getPointPos(7, "y"), initialLocation.z);
                            }
                        } else {
                            if(locationDiff.z > 0) {
                                shape.setPointPos(0, initialLocation.x,             shape.getPointPos(0, "y"), cursorLocation.z + CELL_SIZE);
                                shape.setPointPos(1, initialLocation.x + CELL_SIZE, shape.getPointPos(1, "y"), cursorLocation.z + CELL_SIZE);
                                shape.setPointPos(2, initialLocation.x + CELL_SIZE, shape.getPointPos(2, "y"), cursorLocation.z + CELL_SIZE);
                                shape.setPointPos(3, initialLocation.x,             shape.getPointPos(3, "y"), cursorLocation.z + CELL_SIZE);
                                shape.resetPointPos(4, "x");
                                shape.resetPointPos(5, "x");
                                shape.resetPointPos(6, "x");
                                shape.resetPointPos(7, "x");
                            } else if(locationDiff.z < 0) {
                                shape.resetPointPos(0, "x");
                                shape.resetPointPos(1, "x");
                                shape.resetPointPos(2, "x");
                                shape.resetPointPos(3, "x");
                                shape.setPointPos(4, initialLocation.x,             shape.getPointPos(4, "y"), cursorLocation.z);
                                shape.setPointPos(5, initialLocation.x + CELL_SIZE, shape.getPointPos(5, "y"), cursorLocation.z);
                                shape.setPointPos(6, initialLocation.x + CELL_SIZE, shape.getPointPos(6, "y"), cursorLocation.z);
                                shape.setPointPos(7, initialLocation.x,             shape.getPointPos(7, "y"), cursorLocation.z);
                            }
                        }
                    } else {
                        shape.resetPointPos();
                    }
                }
            }
        }
    }
    
    public void finalizeGeometry() {
        shapeHeight    = 0;
        prevShapeIndex = currShapeIndex;
    }

    public void changeCube() {
        cube.change();
    }
    
}