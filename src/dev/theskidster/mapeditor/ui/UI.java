package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.ShaderProgram;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector2f;

/**
 * @author J Hoffman
 * Created: Jan 3, 2021
 */

public class UI {
    
    int width;
    int height;
    
    private final Vector2f mousePos   = new Vector2f();
    private final Matrix4f projMatrix = new Matrix4f();
    
    Map<String, Widget> widgets;
    
    public UI() {
        widgets = new HashMap<String, Widget>() {{
            put("Menu Bar", new MenuBar());
        }};
    }
    
    public void update() {        
        float hw = (2f / width);
        float hh = (-2f / height);
        
        projMatrix.set(hw,  0,   0, 0, 
                        0, hh,   0, 0, 
                        0,  0, -1f, 0, 
                      -1f, 1f,   0, 1f);
        
        widgets.forEach((name, widget) -> widget.update(width, height));
    }
    
    public void render(ShaderProgram program) {
        program.setUniform("uProjection", false, projMatrix);
        
        widgets.forEach((name, widget) -> widget.render());
    }
    
    public void setMousePosition(double x, double y) {
        mousePos.set(x, y);
    }
    
    public void setViewport(int width, int height) {
        this.width  = width;
        this.height = height;
        
        projMatrix.setPerspective((float) Math.toRadians(45), (float) width / height, 0.1f, Float.POSITIVE_INFINITY);
    }
    
}