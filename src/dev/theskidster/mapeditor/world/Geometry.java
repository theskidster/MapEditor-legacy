package dev.theskidster.mapeditor.world;

import static dev.theskidster.mapeditor.world.World.CELL_SIZE;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 * @author J Hoffman
 * Created: Feb 2, 2021
 */

class Geometry {

    Map<Integer, Vector3f> vertices;
    
    Geometry(Vector3i position) {
        vertices = new HashMap<>() {{
            put(0, new Vector3f(position.x,             position.y,             position.z));
            put(1, new Vector3f(position.x,             position.y + CELL_SIZE, position.z));
            put(2, new Vector3f(position.x + CELL_SIZE, position.y + CELL_SIZE, position.z));
            put(3, new Vector3f(position.x + CELL_SIZE, position.y,             position.z));
            put(4, new Vector3f(position.x + CELL_SIZE, position.y,             position.z + CELL_SIZE));
            put(5, new Vector3f(position.x,             position.y,             position.z + CELL_SIZE));
            put(6, new Vector3f(position.x,             position.y + CELL_SIZE, position.z + CELL_SIZE));
            put(7, new Vector3f(position.x + CELL_SIZE, position.y + CELL_SIZE, position.z + CELL_SIZE));
        }};
    }
    
}