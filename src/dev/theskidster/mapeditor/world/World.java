package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.graphics.Shape;
import dev.theskidster.mapeditor.main.ShaderProgram;
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
    
    private Shape shape;
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
        
        origin = new Origin(width, height, depth);
        
        shape = new Shape(new Vector3f(0, 0, -50));
    }
    
    public void update() {
        shape.update();
    }
    
    public void render(ShaderProgram program) {
        shape.render(program);
        
        origin.render(program);
    }
    
}