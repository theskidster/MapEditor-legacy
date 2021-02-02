package dev.theskidster.mapeditor.world;

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
            put(0, new Vector3f());
            put(1, new Vector3f());
            put(2, new Vector3f());
            put(3, new Vector3f());
            put(4, new Vector3f());
            put(5, new Vector3f());
        }};
    }
    
}