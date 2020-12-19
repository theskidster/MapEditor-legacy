package dev.theskidster.mapeditor.main;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Dec 17, 2020
 */

/**
 * Represents a camera that can be used to move throughout the {@link dev.theskidster.mapeditor.scene.Scene Scene}.
 */
final class Camera {
    
    private final Vector3f position;
    private final Vector3f direction;
    private final Vector3f up;
    private final Vector3f tempFront;
    
    private final Matrix4f view;
    private final Matrix4f proj;
    
    Camera(float width, float height) {
        position  = new Vector3f();
        direction = new Vector3f(0, 0, -1);
        up        = new Vector3f(0, 1, 0);
        tempFront = new Vector3f();
        
        view = new Matrix4f();
        proj = new Matrix4f();
        
        proj.setPerspective((float) Math.toRadians(45), (float) width / height, 0.1f, Float.POSITIVE_INFINITY);
        App.setUniform("uProjection", false, proj);
    }
    
    void update() {
        //TODO: add camera control via keyboard
    }
    
    void render() {
        view.setLookAt(position, position.add(direction, tempFront), up);
        App.setUniform("uView", false, view);
    }
    
}