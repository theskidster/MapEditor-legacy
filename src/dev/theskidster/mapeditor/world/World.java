package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.graphics.Shape;
import dev.theskidster.mapeditor.main.ShaderProgram;
import java.util.HashMap;
import java.util.Map;
import org.joml.Intersectionf;
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
    
    static Vector3f origin2 = new Vector3f();
    static int count = 0;
    
    public static void selectShape(Vector3f position, Vector3f ray) {
        for(int s = 0; s < shapes.size(); s++) {
            if(Intersectionf.testRayTriangle(position, ray, shapes.get(s).verts[0], shapes.get(s).verts[1], shapes.get(s).verts[2], 1)) {
                System.out.println(count);
                count++;
            }
        }
    }

}