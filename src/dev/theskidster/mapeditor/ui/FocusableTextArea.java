package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.Background;
import dev.theskidster.mapeditor.graphics.Icon;
import dev.theskidster.mapeditor.graphics.TrueTypeFont;
import dev.theskidster.mapeditor.util.Mouse;
import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import dev.theskidster.mapeditor.util.Timer;
import java.beans.PropertyChangeEvent;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author J Hoffman
 * Created: Jan 13, 2021
 */

final class FocusableTextArea extends Focusable {
    
    private Icon iconLeft;
    private Icon iconRight;
    
    FocusableTextArea(int xOffset, int yOffset, int width) {
        super(xOffset, yOffset, width);
        
        timer = new Timer(1, 18, this);
        
        iconLeft  = new Icon("spr_icons.png", 15, FOCUSABLE_HEIGHT);
        iconRight = new Icon("spr_icons.png", 15, FOCUSABLE_HEIGHT);
        
        iconLeft.setSprite(6, 0);
        iconRight.setSprite(7, 0);
    }
    
    FocusableTextArea(String text, int xOffset, int yOffset, int width) {
        this(xOffset, yOffset, width);
        typed.append(text);
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
                    if(getIndex() > 0) {
                        setIndex(getIndex() - 1);
                        typed.deleteCharAt(getIndex());
                        scroll();
                    }
                    break;
                    
                case GLFW_KEY_RIGHT:
                    setIndex((getIndex() > typed.length() - 1) ? typed.length() : getIndex() + 1);
                    scroll();
                    break;
                    
                case GLFW_KEY_LEFT:
                    setIndex((getIndex() <= 0) ? 0 : getIndex() - 1);
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
        font.drawString(scissorBox, program, typed.toString(), textPos.x + getTextOffset(), textPos.y, 1, Color.WHITE);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()) {
            case "finished": //Used for cursor timer
                caratIdle = (Boolean) evt.getNewValue();
                break;
                
            case "parentX":
                parentX = (Integer) evt.getNewValue();
                
                updatePosX();
                
                iconLeft.position.x  = parentX + xOffset;
                iconRight.position.x = parentX + (xOffset + (width - 15));
                break;
                
            case "parentY":
                parentY = (Integer) evt.getNewValue();
                
                updatePosY();
                
                iconLeft.position.y  = parentY + yOffset + FOCUSABLE_HEIGHT;
                iconRight.position.y = parentY + yOffset + FOCUSABLE_HEIGHT;
                break;
        }
    }
    
}