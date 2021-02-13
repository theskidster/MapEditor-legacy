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

/**
 * @author J Hoffman
 * Created: Dec 16, 2020
 */

/**
 * Represents a three-dimensional space that will contain all renderable objects.
 */
public class World {
    
    public static final float CELL_SIZE = 1f;
    
    final int width;
    final int height;
    final int depth;
    private int numLights = 1;
    
    private final Floor floor                = new Floor();
    private final RayAabIntersection rayTest = new RayAabIntersection();
    final Vector3i initialLocation           = new Vector3i();
    final Vector3i cursorLocation            = new Vector3i();
    
    private final Origin origin;
    private final Geometry geometry;
    
    final Map<Vector2i, Boolean> tiles;
    
    private final LightSource[] lights = new LightSource[App.MAX_LIGHTS];
    
    public World(int width, int height, int depth, String filename) {
        this.width  = width;
        this.height = height;
        this.depth  = depth;
        
        origin   = new Origin(width, height, depth);
        geometry = new Geometry(filename);
        
        tiles = new HashMap<>() {{
            for(int w = -(width / 2); w < width / 2; w++) {
                for(int d = -(depth / 2); d < depth / 2; d++) {
                    put(new Vector2i(w, d), false);
                }
            }
        }};
        
        lights[0] = new LightSource(Light.NOON);
    }
    
    public void update(Vector3f camRay) {
        for(LightSource light : lights) {
            if(light != null) light.update();
        }
    }
    
    public void render(ShaderProgram program, Vector3f camPos, Vector3f camUp) {
        floor.draw(program, tiles);
        geometry.draw(program, lights, numLights);
        
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
    
    public void addShape() {
        if(tiles.containsValue(true)) {
            Vector2i tileLocation = tiles.entrySet().stream().filter(entry -> entry.getValue()).findAny().get().getKey();
            cursorLocation.set(tileLocation.x, 0, tileLocation.y);
            initialLocation.set(cursorLocation);
            
            geometry.addShape(cursorLocation.x, 0, cursorLocation.z);
        }
    }
    
    public void stretchShape(float verticalChange, boolean ctrlHeld) {
        geometry.stretchShape(verticalChange, ctrlHeld, this);
    }
    
    public void finalizeShape() {
        geometry.shapeHeight = 0;
    }
    
}