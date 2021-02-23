package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.Icon;
import dev.theskidster.mapeditor.graphics.Background;
import dev.theskidster.mapeditor.graphics.TrueTypeFont;
import dev.theskidster.mapeditor.util.Rectangle;
import dev.theskidster.mapeditor.util.Mouse;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.GLFW_ARROW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_HAND_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_IBEAM_CURSOR;

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
    
    protected final class CloseButton extends Element {
        
        private final Rectangle rectangle;
        private final Icon icon;
        private Color color;
        
        CloseButton() {
            rectangle = new Rectangle(
                    xPos + (width - TITLE_BAR_HEIGHT), 
                    yPos, 
                    TITLE_BAR_HEIGHT, 
                    TITLE_BAR_HEIGHT);
            
            icon = new Icon(UI.iconTexture, 20, 20);
            icon.setSprite(0, 0);
        }
        
        @Override
        void update(Mouse mouse) {
            if(rectangle.intersects(mouse.cursorPos)) {
                hovered = true;
                color   = Color.RGM_RED;
                
                if(mouse.clicked) {
                    close();
                    removeRequest = true;
                }
            } else {
                hovered = false;
                color   = Color.RGM_BLACK;
            }
            
            rectangle.xPos = xPos + (width - TITLE_BAR_HEIGHT);
            rectangle.yPos = yPos - TITLE_BAR_HEIGHT;
            
            icon.position.set(rectangle.xPos + 9, yPos - 9);
        }
        
        @Override
        void renderBackground(Background background) {
            background.drawRectangle(rectangle, color);
        }
        
        @Override
        void renderIcon(ShaderProgram program) {
            icon.render(program);
        }

        @Override
        void renderText(ShaderProgram program, TrueTypeFont font) {}
    }
    
    public Frame(int xPos, int yPos, int width, int height, boolean closeable) {
        this.xPos   = xPos;
        this.yPos   = yPos;
        this.width  = width;
        this.height = height;
        
        titleBar    = new Rectangle(xPos, yPos - TITLE_BAR_HEIGHT, width, TITLE_BAR_HEIGHT);
        content     = new Rectangle(xPos, yPos, width, height);
        closeButton = (closeable) ? new CloseButton() : null;
        
        if(closeable) elements.add(closeButton);
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
        
        setIconPos();
        
        titleBar.xPos = xPos;
        titleBar.yPos = yPos - TITLE_BAR_HEIGHT;
        content.xPos  = xPos;
        content.yPos  = yPos;
    }
    
    protected void setIconPos() {
        if(icon != null) icon.position.set(xPos + 13, yPos - 9);
    }
    
    void findHovered(Vector2i cursorPos) {
        hovered = content.intersects(cursorPos) || titleBar.intersects(cursorPos);
    }
    
}