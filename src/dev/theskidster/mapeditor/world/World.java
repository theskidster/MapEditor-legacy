package dev.theskidster.mapeditor.world;

import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.RayAabIntersection;

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
    
    private final Origin origin;
    private final CellRenderer cellRenderer;
    static Cell selectedCell;
    
    private static RayAabIntersection rayTest;
    
    private static Map<Vector3i, Cell> cells;
    
    public World(int width, int height, int depth) {
        this.width  = width;
        this.height = height;
        this.depth  = depth;
        
        origin = new Origin(width, height, depth);
        
        cells = new HashMap<>() {{
            for(int w = -(width / 2); w < width / 2; w++) {
                for(int d = -(depth / 2); d < depth / 2; d++) {
                    Vector3i location = new Vector3i(w, 0, d);
                    put(location, new Cell(new Vector3f(location), Color.PURPLE));
                }
            }
        }};
        
        cellRenderer = new CellRenderer();
        
        rayTest = new RayAabIntersection();
    }
    
    public void update() {}
    
    public void render(ShaderProgram program) {
        cellRenderer.draw(program, cells);
        origin.render(program);
    }
    
    public static void selectCell(Vector3f camPos, Vector3f camRay) {
        cells.forEach((location, cell) -> {
            rayTest.set(camPos.x, camPos.y, camPos.z, camRay.x, camRay.y, camRay.z);
            
            if(rayTest.test(cell.position.x, cell.position.y, cell.position.z, cell.max.x, cell.max.y, cell.max.z)) {
                cell.hovered = true;
                selectedCell = cell;
            } else {
                cell.hovered = false;
            }
        });
    }

}