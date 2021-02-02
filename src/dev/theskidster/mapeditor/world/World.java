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
    
    public static final float CELL_SIZE = 1;
    
    public final int width;
    public final int height;
    public final int depth;
    
    private final Floor floor                = new Floor();
    private final RayAabIntersection rayTest = new RayAabIntersection();
    private final Vector3i cursorLocation    = new Vector3i();
    
    private final Origin origin;
    
    private final Map<Vector2i, Boolean> tiles;
    private Map<Integer, Geometry> geometry = new HashMap<>();
    
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
        
        //TODO: geometry batch rendering
        
        origin.render(program);
    }
    
    public void selectTile(Vector3f camPos, Vector3f camRay) {
        rayTest.set(camPos.x, camPos.y, camPos.z, camRay.x, camRay.y, camRay.z);
        
        tiles.entrySet().forEach((entry) -> {
            Vector2i location = entry.getKey();
            entry.setValue(rayTest.test(location.x, 0, location.y, location.x + CELL_SIZE, 0, location.y + CELL_SIZE));
        });
    }
    
    public void addGeometry(Vector3f camPos, Vector3f camRay, boolean shiftHeld) {
        if(tiles.containsValue(true)) {
            Vector2i tileLocation = tiles.entrySet().stream().filter(entry -> entry.getValue()).findAny().get().getKey();
            cursorLocation.set(tileLocation.x, 0, tileLocation.y);
            
        } else {
            //TODO: find cell with a y position above the floor
        }
        
        System.out.println("add");
    }
    
    public void stretchGeometry(boolean shiftHeld) {
        System.out.println("stretch");
    }
    
    public void finalizeGeometry() {
        System.out.println("finalize");
    }

}