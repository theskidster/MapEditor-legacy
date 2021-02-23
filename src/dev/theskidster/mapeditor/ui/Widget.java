package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.TrueTypeFont;
import dev.theskidster.mapeditor.util.Mouse;
import dev.theskidster.mapeditor.main.ShaderProgram;
import java.util.LinkedHashSet;
import java.util.Set;
import static org.lwjgl.glfw.GLFW.GLFW_ARROW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_HAND_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_IBEAM_CURSOR;

/**
 * @author J Hoffman
 * Created: Jan 3, 2021
 */

public abstract class Widget {
    
    boolean hovered;
    boolean removeRequest;
    
    Set<Element> elements = new LinkedHashSet<>();
    
    abstract void update(int width, int height, Mouse mouse);
    abstract void render(ShaderProgram program, TrueTypeFont font);
    
    void updateCursorShape(Mouse mouse) {
        if(hovered) {
            if(elements.stream().anyMatch((element) -> element.hovered)) {
                Element e = elements.stream()
                            .filter((element) -> element.hovered)
                            .findFirst()
                            .get();

                if(e instanceof Focusable) {
                    mouse.setCursorShape(GLFW_IBEAM_CURSOR);
                } else if(e instanceof ElementArrow || e instanceof ElementFolderButton ||
                          e instanceof ElementLabelButton || e instanceof Frame.CloseButton) {
                    mouse.setCursorShape(GLFW_HAND_CURSOR);
                }
            } else {
                mouse.setCursorShape(GLFW_ARROW_CURSOR);
            }
        } else {
            if(!UI.getWidgetHovered()) mouse.setCursorShape(GLFW_ARROW_CURSOR);
        }
    }
    
}