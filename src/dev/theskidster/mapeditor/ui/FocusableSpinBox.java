package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.Background;
import dev.theskidster.mapeditor.graphics.Icon;
import dev.theskidster.mapeditor.graphics.TrueTypeFont;
import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import dev.theskidster.mapeditor.util.Mouse;
import dev.theskidster.mapeditor.util.Timer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author J Hoffman
 * Created: Jan 17, 2021
 */

class FocusableSpinBox extends Focusable implements PropertyChangeListener {
    
    int value;
    
    private final String unit;
    
    private final Icon iconLeft;
    private final Icon iconMiddle;
    private final Icon iconRight;
    
    public FocusableSpinBox(int xOffset, int yOffset, int width, String unit, int value) {
        super(xOffset, yOffset, width);
        
        typed.append(value);
        
        this.unit  = unit;
        this.value = value;
        
        timer = new Timer(1, 18, this);
        
        iconLeft   = new Icon("spr_icons.png", 15, FOCUSABLE_HEIGHT);
        iconMiddle = new Icon("spr_icons.png", 15, FOCUSABLE_HEIGHT);
        iconRight  = new Icon("spr_icons.png", 15, FOCUSABLE_HEIGHT);
        
        iconLeft.setSprite(6, 0);
        iconMiddle.setSprite(6, 1);
        iconRight.setSprite(7, 0);
    }

    private void parseValue() {
        try {
            value = Integer.parseInt(typed.toString());
        } catch(NumberFormatException e) {}
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
        
        //Included some minor validation
        if(value > MAX_VALUE) {
            value = MAX_VALUE;
            setText(value + "");
        } else if(value < 1 || typed.toString().equals("")) {
            value = 1;
            setText(value + "");
        }
    }
    
    @Override
    void processInput(int key, int action) {
        if(action == GLFW_PRESS || action == GLFW_REPEAT) {
            caratIdle  = false;
            caratBlink = true;
            timer.restart();
            
            keyChars.forEach((k, c) -> {
                if(key == k && (k >= 48 && k <= 57)) {
                    insertChar(c.getChar(shiftHeld));
                    parseValue();
                }
            });
            
            switch(key) {
                case GLFW_KEY_BACKSPACE:
                    if(getIndex() > 0) {
                        setIndex(getIndex() - 1);
                        typed.deleteCharAt(getIndex());
                        parseValue();
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
    }
    
    @Override
    void update(Mouse mouse) {
        timer.update();
        if(App.tick(18) && caratIdle) caratBlink = !caratBlink;
        
        if(rectFront.intersects(mouse.cursorPos)) {
            if(mouse.clicked) {
                if(hasFocus) {
                    if(typed.length() > 0) {
                        int newIndex = findClosestIndex(mouse.cursorPos.x - (parentX + xOffset));
                        setIndex(newIndex);
                        scroll();
                    }
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
        iconMiddle.render(program);
        iconRight.render(program);
        
        if(hasFocus && caratBlink) carat.render(program);
    }
    
    @Override
    void renderText(ShaderProgram program, TrueTypeFont font) {
        font.drawString(scissorBox, program, typed + " " + unit, textPos.x + getTextOffset(), textPos.y, 1, Color.WHITE);
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
                
                iconLeft.position.x   = parentX + xOffset;
                iconMiddle.position.x = parentX + (xOffset + (width - 6));
                iconRight.position.x  = parentX + (xOffset + (width + 9));
                break;
                
            case "parentY":
                parentY = (Integer) evt.getNewValue();
                
                updatePosY();
                
                int iconPosY = parentY + yOffset + FOCUSABLE_HEIGHT;
                
                iconLeft.position.y   = iconPosY;
                iconMiddle.position.y = iconPosY;
                iconRight.position.y  = iconPosY;
                break;
        }
    }
    
}