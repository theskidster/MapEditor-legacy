package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.ShaderProgram;

/**
 * @author J Hoffman
 * Created: Jan 10, 2021
 */

abstract class Frame extends Widget {

    private class CloseButton {
        
        CloseButton() {
            
        }
        
        void update(Mouse mouse) {
            
        }
        
        void render() {
            
        }
    }
    
    protected final int TITLE_HEIGHT = 40;
    
    @Override
    abstract void update(int width, int height, Mouse mouse);

    @Override
    abstract void render(ShaderProgram program, TrueTypeFont font);

}