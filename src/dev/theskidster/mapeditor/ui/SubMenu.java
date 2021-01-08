package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.ShaderProgram;
import java.util.List;

/**
 * @author J Hoffman
 * Created: Jan 8, 2021
 */

class SubMenu {
    
    boolean hovered;

    private final Background background;

    private final List<LabelButton> buttons;
    private final List<Rectangle> rectangles;

    SubMenu(List<LabelButton> buttons, List<Rectangle> rectangles) {
        this.buttons    = buttons;
        this.rectangles = rectangles;
        
        background = new Background(buttons.size() + rectangles.size());
    }

    void update(Mouse mouse) {
        //hovered = rectangle.intersects(mouse.cursorPos);
        
        buttons.forEach(button -> button.update(mouse));
    }

    void render(ShaderProgram program, TrueTypeFont text) {
        background.batchStart();
            rectangles.forEach(rect -> background.drawRectangle(rect, Color.LIGHT_GRAY));
            buttons.forEach(button -> button.renderBackground(background));
        background.batchEnd(program);

        buttons.forEach(button -> button.renderText(program, text));
    }

}