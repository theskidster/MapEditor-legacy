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
    
    private boolean prevPressed;
    private boolean currPressed;
    boolean hovered;
    boolean clicked;

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
            currPressed = mouse.clicked;

            if(prevPressed != currPressed && !prevPressed) {
                menubar.openSubMenus = !menubar.openSubMenus;
                if(!menubar.openSubMenus) menubar.resetState();
            }

            color = (currPressed || menubar.openSubMenus) ? Color.BLUE : Color.MEDIUM_GRAY;
        } else {
            hovered = false;
            color   = (active) ? Color.BLUE : Color.DARK_GRAY;
        }
    }

    @Override
    void update(Mouse mouse) {
        hovered = rectangle.intersects(mouse.cursorPos);
        color   = (hovered) ? Color.BLUE : Color.DARK_GRAY;
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

        font.drawString(program, text, xOffset, yOffset, 1, Color.WHITE);
    }

}