package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.graphics.Shape;
import dev.theskidster.mapeditor.main.Camera;
import dev.theskidster.mapeditor.main.ShaderProgram;
import java.util.HashMap;
import java.util.Map;
import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Dec 16, 2020
 */

/**
 * Represents a three-dimensional space that will contain all renderable objects.
 */
public class World {
    
    public static final int CELL_SIZE = 16;
    public static final int HCS       = CELL_SIZE / 2;
    
    public final int width;
    public final int height;
    public final int depth;
    
    private int[][][] cells;
    
    private static Map<Integer, Shape> shapes;
    private Origin origin;
    
    /**
     * Constructs a new scene instance and initializes any objects that will be present in it.
     */
    public World(int width, int height, int depth) {
        this.width  = width;
        this.height = height;
        this.depth  = depth;
        
        cells = new int[width][height][depth];
        //TODO: use data from newmap widget to construct world
        //TODO: implement map resizing
        
        shapes = new HashMap<>() {{
            put(0, new Shape(new Vector3f(0, 0, -50)));
        }};
        
        origin = new Origin(width, height, depth);
    }
    
    public void update() {
        shapes.forEach((id, shape) -> shape.update());
    }
    
    public void render(ShaderProgram program) {
        shapes.forEach((id, shape) -> shape.render(program));
        
        origin.render(program);
    }
    
    private static Vector3f dir = new Vector3f();
    private static Vector3f min = new Vector3f();
    private static Vector3f max = new Vector3f();
    private static Vector2f nearFar = new Vector2f();
    
    //TODO: move this to somewhere more appropriate
    public static void selectShape(Camera camera) {
        int id              = 0;
        Shape selectedShape = null;
        float closestDist   = Float.POSITIVE_INFINITY;
        
        camera.view.positiveZ(dir).negate();
        
        for(int i = 0; i < shapes.size(); i++) {
            shapes.get(i).selected = false;
            min.set(shapes.get(i).position);
            max.set(shapes.get(i).position);
            min.add(-CELL_SIZE, -CELL_SIZE, -CELL_SIZE);
            max.add(CELL_SIZE, CELL_SIZE, CELL_SIZE);
            
            if(Intersectionf.intersectRayAab(camera.position, dir, min, max, nearFar) && nearFar.x < closestDist) {
                closestDist   = nearFar.x;
                selectedShape = shapes.get(i);
                id            = i;
            }
        }
        
        if(selectedShape != null) {
            shapes.get(id).selected = true;
        }
    }
    
}