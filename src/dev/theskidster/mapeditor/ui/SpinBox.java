package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.util.Mouse;
import dev.theskidster.mapeditor.main.ShaderProgram;

/**
 * @author J Hoffman
 * Created: Jan 17, 2021
 */

class SpinBox {
    
    private boolean hasFocus;
    
    public SpinBox() {
        
    }
    
    void focus() {
        hasFocus = true;
    }
    
    void unfocus() {
        hasFocus = false;
    }
    
    public void update(Mouse mouse) {
        
    }
    
    public void renderBackground(Background background) {
        //TODO: seeing a lot of redundant code here- create component class
        System.out.println("HEY!");
    }
    
    public void renderIcon(ShaderProgram program) {
        
    }
    
    public void renderText(ShaderProgram program, TrueTypeFont font) {
        
    }
    
}