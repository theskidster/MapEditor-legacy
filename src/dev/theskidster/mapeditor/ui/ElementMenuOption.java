package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.Background;
import dev.theskidster.mapeditor.graphics.TrueTypeFont;
import dev.theskidster.mapeditor.util.Mouse;
import dev.theskidster.mapeditor.util.Rectangle;
import dev.theskidster.mapeditor.util.Color;
import dev.theskidster.mapeditor.main.ShaderProgram;
import org.joml.Vector2i;

/**
 * @author J Hoffman
 * Created: Jan 8, 2021
 */

final class ElementMenuOption extends Element {

    final String text;
    private final Rectangle rectangle;
    private final Vector2i padding;
    private Color color;

    ElementMenuOption(String text, Rectangle rectangle, Vector2i padding) {
        this.text      = text;
        this.rectangle = rectangle;
        this.padding   = padding;
        
        //TODO: add optional shortcut string
    }

    void update(Mouse mouse, WidgetMenuBar menubar, boolean active) {
        if(rectangle.intersects(mouse.cursorPos)) {
            hovered     = true;
            prevPressed = currPressed;
            currPressed = mouse.clicked && mouse.button.equals("left");

            if(prevPressed != currPressed && !prevPressed) {
                menubar.openSubMenus = !menubar.openSubMenus;
                if(!menubar.openSubMenus) menubar.resetState();
            }

            color = (currPressed || menubar.openSubMenus) ? Color.RGM_BLUE : Color.RGM_MEDIUM_GRAY;
        } else {
            hovered = false;
            color   = (active) ? Color.RGM_BLUE : Color.RGM_DARK_GRAY;
        }
    }

    @Override
    void update(Mouse mouse) {
        hovered = rectangle.intersects(mouse.cursorPos);
        color   = (hovered) ? Color.RGM_BLUE : Color.RGM_DARK_GRAY;
        clicked = (hovered && mouse.clicked);
    }

    @Override
    void renderBackground(Background background) {
        background.drawRectangle(rectangle, color);
    }

    @Override
    void renderIcon(ShaderProgram program) {}
    
    @Override
    void renderText(ShaderProgram program, TrueTypeFont font) {
        int xOffset = rectangle.xPos + padding.x;
        int yOffset = rectangle.yPos + padding.y + TrueTypeFont.FONT_HEIGHT;

        font.drawString(program, text, xOffset, yOffset, 1, Color.RGM_WHITE);
    }

}