package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.Icon;
import dev.theskidster.mapeditor.graphics.Background;
import dev.theskidster.mapeditor.graphics.TrueTypeFont;
import dev.theskidster.mapeditor.util.Rectangle;
import dev.theskidster.mapeditor.util.Mouse;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;

/**
 * @author J Hoffman
 * Created: Jan 10, 2021
 */

abstract class Frame extends Widget {
    
    protected final int TITLE_BAR_HEIGHT = 40;
    
    protected int xPos;
    protected int yPos;
    protected final int width;
    protected final int height;
    
    protected Icon icon;
    protected String title;
    protected final Rectangle titleBar;
    protected final Rectangle content;
    protected final CloseButton closeButton;
    
    protected final class CloseButton {
        
        private final Rectangle rectangle;
        private final Icon icon;
        private Color color;
        
        CloseButton() {
            rectangle = new Rectangle(
                    xPos + (width - TITLE_BAR_HEIGHT), 
                    yPos, 
                    TITLE_BAR_HEIGHT, 
                    TITLE_BAR_HEIGHT);
            
            icon = new Icon("spr_icons.png", 20, 20);
            icon.setSprite(0, 0);
        }
        
        void update(Mouse mouse) {
            if(rectangle.intersects(mouse.cursorPos)) {
                color = Color.RED;
                if(mouse.clicked) {
                    close();
                    removeRequest = true;
                }
            } else {
                color = Color.BLACK;
            }
            
            rectangle.xPos = xPos + (width - TITLE_BAR_HEIGHT);
            rectangle.yPos = yPos - TITLE_BAR_HEIGHT;
            
            icon.position.set(rectangle.xPos + 9, yPos - 9);
        }
        
        void renderBackground(Background background) {
            background.drawRectangle(rectangle, color);
        }
        
        void renderIcon(ShaderProgram program) {
            icon.render(program);
        }
    }
    
    public Frame(int xPos, int yPos, int width, int height, boolean closeable) {
        this.xPos   = xPos;
        this.yPos   = yPos;
        this.width  = width;
        this.height = height;
        
        titleBar    = new Rectangle(0, 0, width, TITLE_BAR_HEIGHT);
        content     = new Rectangle(0, 0, width, height);
        closeButton = (closeable) ? new CloseButton() : null;
    }
    
    public Frame(String title, int xPos, int yPos, int width, int height, boolean closable) {
        this(xPos, yPos, width, height, closable);
        this.title = title;
    }
    
    public Frame(Icon icon, String title, int xPos, int yPos, int width, int height, boolean closable) {
        this(title, xPos, yPos, width, height, closable);
        this.icon = icon;
    }
    
    @Override
    abstract void update(int width, int height, Mouse mouse);

    @Override
    abstract void render(ShaderProgram program, TrueTypeFont font);

    abstract void close();
    
    protected void center(int width, int height) {
        xPos = (width / 2) - ((int) content.width / 2);
        yPos = (height / 2) - ((int) content.height / 2);
        
        if(icon != null) icon.position.set(xPos + 13, yPos - 9);
        
        titleBar.xPos = xPos;
        titleBar.yPos = yPos - TITLE_BAR_HEIGHT;
        content.xPos  = xPos;
        content.yPos  = yPos;
    }
    
}