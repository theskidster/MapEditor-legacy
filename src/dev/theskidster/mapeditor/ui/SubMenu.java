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
    private final Rectangle rectangle;

    private final List<LabelButton> buttons;

    SubMenu(List<LabelButton> buttons, Rectangle rectangle) {
        this.buttons   = buttons;
        this.rectangle = rectangle;
        
        background = new Background(buttons.size() + 1);
    }

    void update(Mouse mouse) {
        hovered = rectangle.intersects(mouse.cursorPos);
        buttons.forEach(button -> button.update(mouse));
    }

    void render(ShaderProgram program, TrueTypeFont text) {
        background.batchStart();
            background.drawRectangle(rectangle, Color.LIGHT_GRAY);
            buttons.forEach(button -> button.renderBackground(background));
        background.batchEnd(program);

        buttons.forEach(button -> button.renderText(program, text));
    }

}