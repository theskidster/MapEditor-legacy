package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.ShaderProgram;
import org.joml.Vector2i;

/**
 * @author J Hoffman
 * Created: Jan 5, 2021
 */

public class LabelButton extends Widget {

    boolean pressed;
    
    private String text;
    private Rectangle rectangle;
    private Vector2i padding;
    private Color color;
    
    public LabelButton(String text, Rectangle rectangle, Vector2i padding) {
        this.text      = text;
        this.rectangle = rectangle;
        this.padding   = padding;
    }
    
    @Override
    void update(int width, int height, Mouse mouse) {
        if(rectangle.intersects(mouse.cursorPos)) {
            hovered = true;
            pressed = mouse.clicked;
            color   = (pressed) ? Color.BLUE : Color.SILVER;
        } else {
            hovered = false;
            color   = Color.GRAY;
        }
    }

    @Override
    void render(ShaderProgram program, TrueTypeFont font) {
        int xOffset = rectangle.position.x + padding.x;
        int yOffset = rectangle.position.y + padding.y + font.FONT_HEIGHT;
        
        font.drawString(program, text, xOffset, yOffset, 1, Color.WHITE);
    }
    
    void update(Mouse mouse, boolean openSubMenus, boolean isActive) {
        if(rectangle.intersects(mouse.cursorPos)) {
            hovered = true;
            pressed = mouse.clicked;
            color   = (pressed | openSubMenus) ? Color.BLUE : Color.SILVER;
        } else {
            hovered = false;
            color   = (isActive) ? Color.BLUE : Color.GRAY;
        }
    }
    
    void drawRectangle(Background background) {
        background.drawRectangle(rectangle, color);
    }
    
}