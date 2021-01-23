package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.Background;
import dev.theskidster.mapeditor.graphics.Icon;
import dev.theskidster.mapeditor.graphics.TrueTypeFont;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Mouse;

/**
 * @author J Hoffman
 * Created: Jan 23, 2021
 */

final class ElementLabelButton extends Element {

    private int xOffset;
    private int yOffset;
    private int width;
    
    private Icon iconLeft;
    private Icon iconRight;
    
    ElementLabelButton(int xOffset, int yOffset, int width) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.width   = width;
        
        iconLeft  = new Icon("spr_icons.png", 15, 30);
        iconRight = new Icon("spr_icons.png", 15, 30);
        
        
    }
    
    @Override
    void update(Mouse mouse) {
    }

    @Override
    void renderBackground(Background background) {
    }

    @Override
    void renderIcon(ShaderProgram program) {
    }

    @Override
    void renderText(ShaderProgram program, TrueTypeFont font) {
    }

}