package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.TrueTypeFont;
import dev.theskidster.mapeditor.util.Mouse;
import com.mlomb.freetypejni.FreeType;
import dev.theskidster.mapeditor.main.ShaderProgram;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author J Hoffman
 * Created: Jan 3, 2021
 */

public class UI {
    
    private static int viewWidth;
    private static int viewHeight;
    
    private final TrueTypeFont font;
    
    private static Focusable focusable;
    
    private final Mouse mouse         = new Mouse();
    private final Matrix4f projMatrix = new Matrix4f();
    
    private final Map<String, Widget> widgets;
    
    public UI() {
        font = new TrueTypeFont(FreeType.newLibrary(), "fnt_karla_regular.ttf");
        
        widgets = new HashMap<String, Widget>() {{
            put("Menu Bar", new WidgetMenuBar());
        }};
    }
    
    public void update() {
        float hw = (2f / viewWidth);
        float hh = (-2f / viewHeight);
        
        projMatrix.set(hw,  0,   0, 0, 
                        0, hh,   0, 0, 
                        0,  0, -1f, 0, 
                      -1f, 1f,   0, 1f);
        
        widgets.forEach((name, widget) -> widget.update(viewWidth, viewHeight, mouse));
        widgets.entrySet().removeIf(widget -> widget.getValue().removeRequest);
    }
    
    public void render(ShaderProgram program) {
        program.setUniform("uProjection", false, projMatrix);
        
        widgets.forEach((name, widget) -> widget.render(program, font));
    }
    
    static void setFocusable(Focusable obj) {
        focusable = obj;
    }
    
    static Focusable getFocusable() {
        return focusable;
    }
    
    static int getViewWidth() {
        return viewWidth;
    }
    
    static int getViewHeight() {
        return viewHeight;
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
        viewWidth  = width;
        viewHeight = height;
        
        projMatrix.setPerspective((float) Math.toRadians(45), (float) width / height, 0.1f, Float.POSITIVE_INFINITY);
    }
    
    public boolean getMenuBarActive() {
        return ((WidgetMenuBar) widgets.get("Menu Bar")).getMenuBarActive();
    }
    
    public void resetMenuBar() {
        ((WidgetMenuBar) widgets.get("Menu Bar")).resetState();
    }
    
    public void addWidget(String name, Widget widget) {
        widgets.put(name, widget);
    }
    
    public void enterText(int key, int action) {
        if(focusable != null) focusable.processInput(key, action);
    }
    
}