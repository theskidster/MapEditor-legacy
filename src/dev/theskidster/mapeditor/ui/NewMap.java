package dev.theskidster.mapeditor.ui;

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
            new Rectangle(0, 0, 150, 75)
        };
        
        icon = new Icon("spr_icons.png", 20, 20);
        icon.setSprite(5, 0);
    }
    
    @Override
    void update(int width, int height, Mouse mouse) {
        xOffset = (width / 2) - rectangles[0].width;
        yOffset = (height / 2) - rectangles[0].height;
        
        icon.setPosition((int) xOffset + 12, (int) yOffset - 9);
    }

    @Override
    void render(ShaderProgram program, TrueTypeFont font) {
        background.batchStart();
            background.drawRectangle(xOffset, yOffset - TITLE_HEIGHT, 300, TITLE_HEIGHT, Color.BLACK);
            background.drawRectangle(xOffset, yOffset, 300, 150 + TITLE_HEIGHT, Color.DARK_GRAY);
        background.batchEnd(program);
        
        font.drawString(program, "New Map", xOffset + 42, yOffset - (TITLE_HEIGHT / 3), 1, Color.WHITE);
        
        icon.render(program);
    }
    
}