package dev.theskidster.mapeditor.scene;

import org.joml.Vector3f;

/**
 * @author J Hoffman
 * Created: Dec 16, 2020
 */

public class Scene {
    
    private TestObject test;
    
    public Scene() {
        test = new TestObject(new Vector3f(0, 0, -50));
    }
    
    public void update() {
        test.update();
    }
    
    public void render() {
        test.render();
    }
    
}