package dev.theskidster.mapeditor.world;

import java.util.Map;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Feb 9, 2021
 */

class Point {
    
    Vector3f position;
    Map<Integer, Vertex> vertices;
    
    Point(Vector3f position, Map<Integer, Vertex> vertices) {
        this.position = position;
        this.vertices = vertices;
    }
    
}