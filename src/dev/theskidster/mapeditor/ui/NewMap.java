package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.util.Color;
import dev.theskidster.mapeditor.main.ShaderProgram;
import java.util.ArrayList;
import java.util.List;

/**
 * @author J Hoffman
 * Created: Jan 8, 2021
 */

public class NewMap extends Frame {
    
    private final Background background;
    private List<Rectangle> rectangles;
    
    public NewMap(int xPos, int yPos) {
        super(new Icon("spr_icons.png", 20, 20), "New Map", xPos, yPos, 488, 430, true);
        
        background = new Background(5);
        
        rectangles = new ArrayList<>() {{
            add(titleBar);
            add(content);
        }};
        
        icon.setSprite(2, 2);
    }
    
    @Override
    void update(int width, int height, Mouse mouse) {
        center(width, height);
        closeButton.update(mouse);
    }

    @Override
    void render(ShaderProgram program, TrueTypeFont font) {
        background.batchStart();
            //background.drawRectangle(titleBar, Color.BLACK);
            background.drawRectangle(xPos, yPos - TITLE_BAR_HEIGHT, 488, TITLE_BAR_HEIGHT, Color.BLACK);
            background.drawRectangle(xPos, yPos, 488, 430, Color.DARK_GRAY);
            background.drawRectangle(xPos + 13, yPos + 38, 462, 58, Color.LIGHT_GRAY);
            background.drawRectangle(xPos + 14, yPos + 39, 460, 56, Color.DARK_GRAY);
            closeButton.renderBackground(background);
        background.batchEnd(program);
        
        font.drawString(program, "New Map", xPos + 45, yPos - (TITLE_BAR_HEIGHT / 3), 1, Color.WHITE);
        font.drawString(program, "Blockset:", xPos + 12, yPos + 24, 1, Color.WHITE);
        font.drawString(program, "Source:", xPos + 26, yPos + 73, 1, Color.WHITE);
        
        icon.render(program);
        closeButton.renderIcon(program);
    }
    
}