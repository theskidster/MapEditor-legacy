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
    private final Icon[] icons = new Icon[13];
    
    public NewMap(int xPos, int yPos) {
        super(xPos, yPos, 488, 430);
        
        background = new Background(5);
        
        rectangles = new ArrayList<>() {{
            add(title);
            add(body);
        }};
        
        for(int i = 0; i < icons.length; i++) {
            icons[i] = new Icon("spr_icons.png", 20, 20);
        }
        
        icons[0].setSprite(2, 2);
    }
    
    @Override
    void update(int width, int height, Mouse mouse) {
        center(width, height);
        
        icons[0].setPosition((int) xPos + 13, (int) yPos - 9);
        
        closeButton.update(mouse);
    }

    @Override
    void render(ShaderProgram program, TrueTypeFont font) {
        background.batchStart();
            background.drawRectangle(xPos, yPos - TITLE_HEIGHT, 488, TITLE_HEIGHT, Color.BLACK);
            background.drawRectangle(xPos, yPos, 488, 430, Color.DARK_GRAY);
            background.drawRectangle(xPos + 13, yPos + 38, 462, 58, Color.LIGHT_GRAY);
            background.drawRectangle(xPos + 14, yPos + 39, 460, 56, Color.DARK_GRAY);
            closeButton.renderBackground(background);
        background.batchEnd(program);
        
        font.drawString(program, "New Map", xPos + 45, yPos - (TITLE_HEIGHT / 3), 1, Color.WHITE);
        font.drawString(program, "Blockset:", xPos + 12, yPos + 24, 1, Color.WHITE);
        font.drawString(program, "Source:", xPos + 26, yPos + 73, 1, Color.WHITE);
        
        for(Icon icon : icons) icon.render(program);
        closeButton.renderIcon(program);
    }
    
}