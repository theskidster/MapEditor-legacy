package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.TrueTypeFont;
import dev.theskidster.mapeditor.graphics.Background;
import dev.theskidster.mapeditor.util.Mouse;
import dev.theskidster.mapeditor.main.ShaderProgram;

/**
 * @author J Hoffman
 * Created: Jan 17, 2021
 */

class FocusableSpinBox extends Focusable {
    
    public FocusableSpinBox(int xOffset, int yOffset, int width) {
        super(xOffset, yOffset, width);
    }

    @Override
    void focus() {
        hasFocus = true;
        UI.setFocusable(this);
    }

    @Override
    void unfocus() {
        hasFocus = false;
        
        if(UI.getFocusable() != null && UI.getFocusable().equals(this)) {
            UI.setFocusable(null);
        }
    }

    @Override
    void processInput(int key, int action) {
    }

    @Override
    void update(Mouse mouse) {
    }

    @Override
    void renderBackground(Background backgound) {
    }

    @Override
    void renderIcon(ShaderProgram program) {
    }

    @Override
    void renderText(ShaderProgram program, TrueTypeFont font) {
    }
    
}