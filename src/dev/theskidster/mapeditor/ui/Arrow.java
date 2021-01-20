package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.Background;
import dev.theskidster.mapeditor.graphics.Icon;
import dev.theskidster.mapeditor.util.Rectangle;
import dev.theskidster.mapeditor.util.Mouse;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 17, 2021
 */

class Arrow {
    
    private Icon icon;
    private Rectangle rectangle;
    private Color color;
    
    Arrow(boolean up) {
        icon      = new Icon("spr_icons.png", 20, 20);
        rectangle = new Rectangle(0, 0, 20, 10);
    }
    
    void update(Mouse mouse) {
        if(rectangle.intersects(mouse.cursorPos)) {
            color = Color.MEDIUM_GRAY;
        } else {
            color = Color.DARK_GRAY;
        }
    }
    
    void renderBackground(Background background) {
        background.drawRectangle(rectangle, color);
    }
    
    void renderIcon(ShaderProgram program) {
        icon.render(program);
    }
    
}