package dev.theskidster.mapeditor.world;

import java.util.Map;
import java.util.TreeMap;
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
        
        this.vertices.forEach((id, vertex) -> vertex.point = this);
    }
    
    Point(Point point) {
        position = new Vector3f(point.position);
        vertices = new TreeMap<>();
        
        point.vertices.forEach((id, vertex) -> {
            Vertex newVertex = new Vertex(vertex.texCoords.x, vertex.texCoords.y, vertex.normal.x, vertex.normal.y, vertex.normal.z);
            newVertex.point  = this;
            
            vertices.put(id, newVertex);
        });
    }
    
}