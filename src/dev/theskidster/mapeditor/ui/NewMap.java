package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.util.Color;
import dev.theskidster.mapeditor.main.ShaderProgram;

/**
 * @author J Hoffman
 * Created: Jan 8, 2021
 */

public class NewMap extends Frame {
    
    private float xOffset;
    private float yOffset;
    
    private Rectangle[] rectangles;
    
    private Icon icon;
    
    Background background;
    
    public NewMap() {
        background = new Background(3);
        
        rectangles = new Rectangle[] {
            new Rectangle(0, 0, 244, 215)
        };
        
        icon = new Icon("spr_icons.png", 20, 20);
        icon.setSprite(2, 2);
    }
    
    @Override
    void update(int width, int height, Mouse mouse) {
        xOffset = (width / 2) - rectangles[0].width;
        yOffset = (height / 2) - rectangles[0].height;
        
        icon.setPosition((int) xOffset + 13, (int) yOffset - 9);
    }

    @Override
    void render(ShaderProgram program, TrueTypeFont font) {
        background.batchStart();
            background.drawRectangle(xOffset, yOffset - TITLE_HEIGHT, 488, TITLE_HEIGHT, Color.BLACK);
            background.drawRectangle(xOffset, yOffset, 488, 430, Color.DARK_GRAY);
        background.batchEnd(program);
        
        font.drawString(program, "New Map", xOffset + 45, yOffset - (TITLE_HEIGHT / 3), 1, Color.WHITE);
        
        icon.render(program);
    }
    
}