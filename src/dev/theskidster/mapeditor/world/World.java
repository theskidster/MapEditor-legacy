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
    
    //private TestObject test;
    private Shape shape;
    
    /**
     * Constructs a new scene instance and initializes any objects that will be present in it.
     */
    public World() {
        //test = new TestObject(new Vector3f(0, 0, -50));
        
        shape = new Shape(new Vector3f(0, 0, -50));
    }
    
    public void update() {
        //test.update();
        shape.update();
    }
    
    public void render(ShaderProgram program) {
        //test.render(program);
        shape.render(program);
    }
    
}