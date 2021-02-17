package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.Background;
import dev.theskidster.mapeditor.graphics.Icon;
import dev.theskidster.mapeditor.graphics.TrueTypeFont;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import dev.theskidster.mapeditor.util.Mouse;
import dev.theskidster.mapeditor.util.Rectangle;

/**
 * @author J Hoffman
 * Created: Feb 13, 2021
 */

class WidgetToolBar extends Widget {
    
    private final int xOffset = 346;
    int currTool = 0;
    
    private boolean[] buttonHovered = new boolean[5];
    
    Rectangle[] buttons   = new Rectangle[5];
    Icon[] icons          = new Icon[5];
    Background background = new Background(5);
    
    WidgetToolBar() {
        for(int b = 0; b < buttons.length; b++) {
            buttons[b] = new Rectangle((57 * b) + xOffset, 41, 44, 44);
            icons[b]   = new Icon(UI.iconTexture, 20, 20);
            
            icons[b].position.set((57 * b) + (xOffset + 12), 73);
            
            //TODO: create icon constructor that uses existing texture.
            switch(b) {
                case 0 -> icons[0].setSprite(0, 5);
                case 1 -> icons[1].setSprite(1, 5);
                case 2 -> icons[2].setSprite(2, 5);
                case 3 -> icons[3].setSprite(3, 5);
                case 4 -> icons[4].setSprite(4, 5);
            }
        }
    }
    
    @Override
    void update(int width, int height, Mouse mouse) {
        for(int b = 0; b < buttons.length; b++) {
            buttonHovered[b] = buttons[b].intersects(mouse.cursorPos);
            if(buttonHovered[b] && mouse.clicked) currTool = b;
        }
    }

    @Override
    void render(ShaderProgram program, TrueTypeFont font) {
        background.batchStart();
            for(int b = 0; b < buttons.length; b++) {
                Color color;
                
                if(b == currTool) {
                    color = Color.RGM_BLUE;
                } else {
                    color = (buttonHovered[b]) ? Color.RGM_MEDIUM_GRAY : Color.RGM_DARK_GRAY;
                }
                
                background.drawRectangle(buttons[b], color);
            }
        background.batchEnd(program);
        
        for(Icon icon : icons) icon.render(program);
    }

}