package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.Background;
import dev.theskidster.mapeditor.graphics.TrueTypeFont;
import dev.theskidster.mapeditor.graphics.Icon;
import dev.theskidster.mapeditor.util.Rectangle;
import dev.theskidster.mapeditor.util.Mouse;
import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import dev.theskidster.mapeditor.util.Timer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author J Hoffman
 * Created: Jan 13, 2021
 */

final class FocusableTextArea extends Focusable implements PropertyChangeListener {
    
    private final int FOCUSABLE_HEIGHT = 30;
    private final int PADDING = 4;
    
    private int parentX;
    private int parentY;
    private int xIndex;
    private int lengthToIndex;
    private int textOffset;
    
    private boolean caratIdle;
    private boolean caratBlink;
    
    private final StringBuilder typed = new StringBuilder();
    private final Vector2i textPos    = new Vector2i();
    
    private Rectangle rectBack;
    private Rectangle rectFront;
    private Rectangle scissorBox;
    
    private Icon iconLeft;
    private Icon iconRight;
    private Icon carat;
    
    private final Timer timer = new Timer(1, 18, this);
    
    FocusableTextArea(int xOffset, int yOffset, int width) {
        super(xOffset, yOffset, width);
        
        rectBack   = new Rectangle(xOffset, yOffset, width, FOCUSABLE_HEIGHT);
        rectFront  = new Rectangle(xOffset, yOffset + 1, width, FOCUSABLE_HEIGHT - 2);
        scissorBox = new Rectangle();
        
        iconLeft  = new Icon("spr_icons.png", 15, FOCUSABLE_HEIGHT);
        iconRight = new Icon("spr_icons.png", 15, FOCUSABLE_HEIGHT);
        carat     = new Icon("spr_icons.png", 15, FOCUSABLE_HEIGHT);
        
        iconLeft.setSprite(6, 0);
        iconRight.setSprite(7, 0);
        carat.setSprite(4, 2);
        
        iconLeft.position.set(xOffset, yOffset + FOCUSABLE_HEIGHT);
        iconRight.position.set(xOffset + (width - 15), yOffset + FOCUSABLE_HEIGHT);
    }
    
    FocusableTextArea(String text, int xOffset, int yOffset, int width) {
        this(xOffset, yOffset, width);
        typed.append(text);
    }
    
    private void insertChar(char c) {
        typed.insert(xIndex, c);
        xIndex++;
        scroll();
    }
    
    private void scroll() {
        lengthToIndex = TrueTypeFont.getLengthInPixels(typed.substring(0, xIndex), 1);
        
        textOffset = (width - PADDING) - (lengthToIndex + textPos.x - (parentX + xOffset + PADDING));
        if(textOffset > 0) textOffset = 0;
        
        carat.position.set(
                (parentX + xOffset) + (lengthToIndex + textOffset) + PADDING, 
                (parentY + yOffset) + FOCUSABLE_HEIGHT - 5);
    }
    
    void setText(String text) {
        typed.setLength(0);
        xIndex = 0;
        
        for(char c : text.toCharArray()) insertChar(c);
    }
    
    @Override
    void focus() {
        hasFocus = true;
        UI.setFocusable(this);
        timer.start();
    }
    
    @Override
    void unfocus() {
        hasFocus = false;
        
        if(UI.getFocusable() != null && UI.getFocusable().equals(this)) {
            UI.setFocusable(null);
        }
    }
    
    @Override
    void processInput(int key, int action) {
        if(action == GLFW_PRESS || action == GLFW_REPEAT) {
            caratIdle  = false;
            caratBlink = true;
            timer.restart();
            
            keyChars.forEach((k, c) -> {
                if(key == k) insertChar(c.getChar(shiftHeld));
            });
            
            switch(key) {
                case GLFW_KEY_BACKSPACE:
                    if(xIndex > 0) {
                        xIndex--;
                        typed.deleteCharAt(xIndex);
                        scroll();
                    }
                    break;
                    
                case GLFW_KEY_RIGHT:
                    xIndex = (xIndex > typed.length() - 1) ? xIndex = typed.length() : xIndex + 1;
                    scroll();
                    break;
                    
                case GLFW_KEY_LEFT:
                    xIndex = (xIndex <= 0) ? xIndex = 0 : xIndex - 1;
                    scroll();
                    break;
                    
                case GLFW_KEY_TAB:
                    //TODO: add observable to skip to next component in widget?
                    break;
            }
        } else {
            timer.start();
        }
        
        switch(key) {
            case GLFW_KEY_LEFT_SHIFT: case GLFW_KEY_RIGHT_SHIFT:
                shiftHeld = action == GLFW_PRESS;
                break;
        }
    }
    
    @Override
    void update(Mouse mouse) {
        timer.update();
        if(App.tick(18) && caratIdle) caratBlink = !caratBlink;
        
        if(rectFront.intersects(mouse.cursorPos)) {
            if(mouse.clicked) {
                if(hasFocus) {
                    //TODO: set caret position
                } else {
                    focus();
                }
            }
        } else {
            if(mouse.clicked) unfocus();
        }
        
        scissorBox.width  = width;
        scissorBox.height = FOCUSABLE_HEIGHT;
    }
    
    @Override
    void renderBackground(Background background) {
        background.drawRectangle(rectBack, Color.LIGHT_GRAY);
        background.drawRectangle(rectFront, Color.MEDIUM_GRAY);
    }
    
    @Override
    void renderIcon(ShaderProgram program) {
        iconLeft.render(program);
        iconRight.render(program);
        
        if(hasFocus && caratBlink) carat.render(program);
    }
    
    @Override
    void renderText(ShaderProgram program, TrueTypeFont font) {
        font.drawString(scissorBox, program, typed.toString(), textPos.x + textOffset, textPos.y, 1, Color.WHITE);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()) {
            case "finished": //Used for cursor timer
                caratIdle = (Boolean) evt.getNewValue();
                break;
                
            case "parentX":
                parentX = (Integer) evt.getNewValue();
                
                rectBack.xPos    = parentX + xOffset;
                rectFront.xPos   = parentX + xOffset;
                scissorBox.xPos  = parentX + xOffset + 1;
                textPos.x        = parentX + xOffset + PADDING;
                carat.position.x = (parentX + xOffset) + lengthToIndex + PADDING;
                
                iconLeft.position.x  = parentX + xOffset;
                iconRight.position.x = parentX + (xOffset + (width - 15));
                break;
                
            case "parentY":
                parentY = (Integer) evt.getNewValue();
                
                rectBack.yPos    = parentY + yOffset;
                rectFront.yPos   = parentY + yOffset + 1;
                scissorBox.yPos  = parentY + yOffset + FOCUSABLE_HEIGHT;
                textPos.y        = parentY + yOffset + 21;
                carat.position.y = (parentY + yOffset) + FOCUSABLE_HEIGHT - 5;
                
                iconLeft.position.y  = parentY + yOffset + FOCUSABLE_HEIGHT;
                iconRight.position.y = parentY + yOffset + FOCUSABLE_HEIGHT;
                break;
        }
    }
    
}