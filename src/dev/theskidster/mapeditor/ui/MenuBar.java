package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.ShaderProgram;
import org.joml.Vector2i;

/**
 * @author J Hoffman
 * Created: Jan 4, 2021
 */

public class MenuBar extends Widget {

    private final Rectangle rectangle;
    private final Background background;
    
    public MenuBar() {
        rectangle  = new Rectangle(new Vector2i(0, 0), 0, 28);
        background = new Background(1);
    }
    
    @Override
    void update(int width, int height, Mouse mouse) {
        hovered = rectangle.intersects(mouse.cursorPos);
        rectangle.width = width;
    }

    @Override
    void render(ShaderProgram program, TrueTypeFont font) {        
        background.batchStart();
            background.drawRectangle(rectangle, Color.GRAY);
        background.batchEnd(program);
        
        font.drawString(program, "test", 20, 60, 1, Color.WHITE);
    }

}