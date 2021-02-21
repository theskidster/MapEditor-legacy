package dev.theskidster.mapeditor.scene;

import dev.theskidster.mapeditor.graphics.Light;
import dev.theskidster.mapeditor.graphics.LightSource;
import dev.theskidster.mapeditor.main.App;
import static dev.theskidster.mapeditor.main.App.SELECT_TOOL;
import dev.theskidster.mapeditor.main.ShaderProgram;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
public class Scene {
    
    public static final float CELL_SIZE = 1f;
    
    final int width;
    final int height;
    final int depth;
    private int numLights = 1;
    static int currTool;
    
    private boolean vertexSelected;
    private boolean snapToGrid;
    
    private Movement cursorMovement = new Movement();
    final Vector3i initialLocation  = new Vector3i();
    final Vector3i cursorLocation   = new Vector3i();
    final Vector3f tempVec          = new Vector3f();
    
    private final RayAabIntersection rayTest = new RayAabIntersection();
    
    private final Floor floor = new Floor();
    private final Origin origin;
    private final Geometry geometry;
    
    final Map<Vector2i, Boolean> tiles;
    private final Map<Integer, Vector3f> selectedVertices = new LinkedHashMap<>();
    private final Map<Integer, Vector3f> newVertPos       = new LinkedHashMap<>();
    
    private final LightSource[] lights = new LightSource[App.MAX_LIGHTS];
    
    MovementCursor cursor = new MovementCursor(new Vector3f(0, 1, 1));
    
    public Scene(int width, int height, int depth, String filename) {
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
    
    public void update(Vector3f camRay, int toolID) {
        currTool = toolID;
        
        selectedVertices.putAll(geometry.getSelectedVertices());
        
        selectedVertices.forEach((index, position) -> {
            if(!newVertPos.containsKey(index)) {
                newVertPos.put(index, new Vector3f(position));
            } else {
                if(!snapToGrid && !newVertPos.get(index).equals(position.x, position.y, position.z)) {
                    newVertPos.put(index, new Vector3f(position));
                }
            }
        });
        
        vertexSelected = selectedVertices.size() > 0;
        
        if(Scene.currTool == SELECT_TOOL && vertexSelected) {
            selectedVertices.forEach((index, position) -> {
                Vector3f newPos = newVertPos.get(index);
                
                switch(cursorMovement.axis) {
                    case "x", "X" -> newVertPos.get(index).set(newPos.x += cursorMovement.value, newPos.y, newPos.z);
                    case "y", "Y" -> newVertPos.get(index).set(newPos.x, newPos.y += cursorMovement.value, newPos.z);
                    case "z", "Z" -> newVertPos.get(index).set(newPos.x, newPos.y, newPos.z += cursorMovement.value);
                }
                
                if(!snapToGrid) {
                    geometry.setVertexPos(index, newPos.x, newPos.y, newPos.z);
                } else {
                    geometry.setVertexPos(index, newPos.x, newPos.y, newPos.z);
                    geometry.snapVertexPos(index);
                }
            });
            
            cursor.update(selectedVertices);
            
            cursorMovement.axis  = "";
            cursorMovement.value = 0;
        }
        
        selectedVertices.clear();
        
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
        
        if(Scene.currTool == SELECT_TOOL && vertexSelected) {
            cursor.render(program);
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
    
    public void selectVertices(Vector3f camPos, Vector3f camRay, boolean retainPrev) {
        camRay.mul(0.5f, tempVec);
        tempVec.normalize();
        
        if(!retainPrev) geometry.clearSelectedVertices();
        
        geometry.selectVertices(camPos, tempVec);
    }
    
    public void selectCursor(Vector3f camPos, Vector3f camRay) {
        cursor.selectArrow(camPos, camRay);
    }
    
    public void moveCursor(Vector3f camDir, Vector3f rayChange, boolean ctrlHeld) {
        snapToGrid     = ctrlHeld;
        cursorMovement = cursor.moveArrow(camDir, rayChange);
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
    
    public boolean geometryEmpty() {
        return geometry.getEmpty();
    }
    
    public boolean getVertexSelected() { return vertexSelected; }
    public boolean getCursorSelected() { return cursor.getSelected(); }
    
}