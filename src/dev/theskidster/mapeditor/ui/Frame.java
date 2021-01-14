package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 10, 2021
 */

abstract class Frame extends Widget {

    protected final class CloseButton {
        
        private final Rectangle rectangle;
        private final Icon icon;
        private Color color;
        
        CloseButton() {
            rectangle = new Rectangle(
                    xPos + (width - TITLE_HEIGHT), 
                    yPos, 
                    TITLE_HEIGHT, 
                    TITLE_HEIGHT);
            
            icon = new Icon("spr_icons.png", 20, 20);
            icon.setSprite(0, 0);
        }
        
        void update(Mouse mouse) {
            if(rectangle.intersects(mouse.cursorPos)) {
                color = Color.RED;
                if(mouse.clicked) removeRequest = true;
            } else {
                color = Color.BLACK;
            }
            
            rectangle.xPos = xPos + (width - TITLE_HEIGHT);
            rectangle.yPos = yPos - TITLE_HEIGHT;
            
            icon.setPosition(rectangle.xPos + 9, yPos - 9);
        }
        
        void renderBackground(Background background) {
            background.drawRectangle(rectangle, color);
        }
        
        void renderIcon(ShaderProgram program) {
            icon.render(program);
        }
    }
    
    protected final int TITLE_HEIGHT = 40;
    
    protected int xPos;
    protected int yPos;
    protected final int width;
    protected final int height;
    
    protected final Rectangle title;
    protected final Rectangle body;
    protected final CloseButton closeButton;
    
    public Frame(int xPos, int yPos, int width, int height) {
        this.xPos   = xPos;
        this.yPos   = yPos;
        this.width  = width;
        this.height = height;
        
        title       = new Rectangle(0, 0, width, TITLE_HEIGHT);
        body        = new Rectangle(0, 0, width, height);
        closeButton = new CloseButton();
    }
    
    @Override
    abstract void update(int width, int height, Mouse mouse);

    @Override
    abstract void render(ShaderProgram program, TrueTypeFont font);

    protected void center(int width, int height) {
        xPos = (width / 2) - ((int) body.width / 2);
        yPos = (height / 2) - ((int) body.height / 2);
    }
    
}