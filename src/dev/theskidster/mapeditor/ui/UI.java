package dev.theskidster.mapeditor.ui;

import com.mlomb.freetypejni.FreeType;
import dev.theskidster.mapeditor.main.ShaderProgram;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author J Hoffman
 * Created: Jan 3, 2021
 */

public class UI {
    
    private int width;
    private int height;
    
    TrueTypeFont font;
    
    private final Mouse mouse         = new Mouse();
    private final Matrix4f projMatrix = new Matrix4f();
    
    Map<String, Widget> widgets;
    
    public UI() {
        font = new TrueTypeFont(FreeType.newLibrary(), "fnt_karla_regular.ttf");
        
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
        
        widgets.forEach((name, widget) -> widget.update(width, height, mouse));
    }
    
    public void render(ShaderProgram program) {
        program.setUniform("uProjection", false, projMatrix);
        
        widgets.forEach((name, widget) -> widget.render(program, font));
    }
    
    public void setMousePosition(double x, double y) {
        mouse.cursorPos.set((int) x, (int) y);
    }
    
    public void setMouseAction(int button, int action) {
        switch(button) {
            case GLFW_MOUSE_BUTTON_RIGHT:  mouse.button = "right";  break;
            case GLFW_MOUSE_BUTTON_MIDDLE: mouse.button = "middle"; break;
            default:                       mouse.button = "left";   break;
        }
        
        mouse.clicked = (action == GLFW_PRESS);
    }
    
    public void setViewport(int width, int height) {
        this.width  = width;
        this.height = height;
        
        projMatrix.setPerspective((float) Math.toRadians(45), (float) width / height, 0.1f, Float.POSITIVE_INFINITY);
    }
    
}