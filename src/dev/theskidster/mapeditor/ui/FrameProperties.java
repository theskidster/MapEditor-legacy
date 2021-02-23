package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.Background;
import dev.theskidster.mapeditor.graphics.Icon;
import dev.theskidster.mapeditor.graphics.TrueTypeFont;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import dev.theskidster.mapeditor.util.Mouse;
import dev.theskidster.mapeditor.util.Observable;
import dev.theskidster.mapeditor.util.Rectangle;

/**
 * @author J Hoffman
 * Created: Feb 22, 2021
 */

public final class FrameProperties extends Frame {

    private final ElementScrollBar scrollBar;
    private final Background background;
    
    private final Rectangle[] rectangles = new Rectangle[2];
    private final Observable observable  = new Observable(this);
    
    public FrameProperties(int xPos, int yPos) {
        super(new Icon(UI.iconTexture, 20, 20), "Properties", xPos, yPos, 320, 443, true);
        
        scrollBar  = new ElementScrollBar(283, 38, true, 346);
        background = new Background(8);
        
        rectangles[0] = new Rectangle(xPos + 13, yPos + 39, 271, 392);
        rectangles[1] = new Rectangle(xPos + 14, yPos + 40, 269, 390);
        
        //Set negative so elements positions will be updated according to their offsets.
        observable.properties.put("parentX", -xPos);
        observable.properties.put("parentY", -yPos);
        observable.addObserver(scrollBar);
        
        icon.setSprite(1, 2);
        setIconPos();
    }

    @Override
    void update(int width, int height, Mouse mouse) {
        xPos = (width - 333);
        
        titleBar.xPos      = xPos;
        content.xPos       = xPos;
        rectangles[0].xPos = xPos + 13;
        rectangles[0].yPos = yPos + 39;
        rectangles[1].xPos = xPos + 14;
        rectangles[1].yPos = yPos + 40;
        
        observable.notifyObservers("parentX", xPos);
        observable.notifyObservers("parentY", yPos);
        
        setIconPos();
        
        closeButton.update(mouse);
        scrollBar.update(mouse);
        
        findHovered(mouse.cursorPos);
        updateCursorShape(mouse);
    }

    @Override
    void render(ShaderProgram program, TrueTypeFont font) {
        background.batchStart();
            background.drawRectangle(titleBar, Color.RGM_BLACK);
            background.drawRectangle(content, Color.RGM_DARK_GRAY);
            background.drawRectangle(rectangles[0], Color.RGM_LIGHT_GRAY);
            background.drawRectangle(rectangles[1], Color.RGM_BLACK);
            closeButton.renderBackground(background);
            scrollBar.renderBackground(background);
        background.batchEnd(program);
        
        icon.render(program);
        scrollBar.renderIcon(program);
        closeButton.renderIcon(program);
        
        font.drawString(program, title, xPos + 45, yPos - (TITLE_BAR_HEIGHT / 3), 1, Color.RGM_WHITE);
        font.drawString(program, "Property:", xPos + 12,  yPos + 25, 1, Color.RGM_WHITE);
        font.drawString(program, "Value:",    xPos + 149, yPos + 25, 1, Color.RGM_WHITE);
    }

    @Override
    void close() {
        
    }

}