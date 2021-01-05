package dev.theskidster.mapeditor.ui;

import org.joml.Vector2i;

/**
 * @author J Hoffman
 * Created: Jan 4, 2021
 */

public class MenuBar extends Widget {

    private final Rectangle rectangle;
    private final Background background;
    
    public MenuBar() {
        rectangle  = new Rectangle(new Vector2i(0, 0), 0, 24);
        background = new Background(1);
    }
    
    @Override
    void update(int width, int height) {
        rectangle.width = width;
    }

    @Override
    void render() {        
        background.batchStart();
            background.drawRectangle(rectangle, Color.GRAY);
        background.batchEnd();
    }

}