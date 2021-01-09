package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.ShaderProgram;
import org.joml.Vector2i;

/**
 * @author J Hoffman
 * Created: Jan 8, 2021
 */

public class NewMap extends Widget {
    
    private float xOffset;
    private float yOffset;
    
    private Rectangle[] rectangles;
    
    Background background;
    
    public NewMap() {
        background = new Background(3);
        
        rectangles = new Rectangle[] {
            new Rectangle(0, 0, 150, 75)
        };
    }
    
    @Override
    void update(int width, int height, Mouse mouse) {
        xOffset = (width / 2) - rectangles[0].width;
        yOffset = (height / 2) - rectangles[0].height;
    }

    @Override
    void render(ShaderProgram program, TrueTypeFont font) {
        background.batchStart();
            background.drawRectangle(xOffset, yOffset - UI.TITLE_HEIGHT, 300, UI.TITLE_HEIGHT, Color.BLACK);
            background.drawRectangle(xOffset, yOffset, 300, 150 + UI.TITLE_HEIGHT, Color.DARK_GRAY);
        background.batchEnd(program);
    }
    
}