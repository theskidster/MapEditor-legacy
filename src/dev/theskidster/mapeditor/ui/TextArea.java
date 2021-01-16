package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Color;
import dev.theskidster.mapeditor.util.Timer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author J Hoffman
 * Created: Jan 13, 2021
 */

final class TextArea implements PropertyChangeListener {
    
    private final int TEXT_AREA_HEIGHT = 30;
    
    private int xOffset;
    private int yOffset;
    private int width;
    private int xIndex;
    
    private boolean hasFocus;
    private boolean shiftHeld;
    private boolean caratIdle;
    private boolean caratBlink;
    private boolean initialTextPos;
    
    private final StringBuilder typed = new StringBuilder();
    private final Vector2i textPos    = new Vector2i();
    
    private Rectangle rectBack;
    private Rectangle rectFront;
    private Rectangle scissorBox;
    
    private Icon iconLeft;
    private Icon iconRight;
    private Icon carat;
    
    private Timer timer = new Timer(1, 18, this);
    
    private Map<Integer, Key> keyChars;
    
    private class Key {
        private final char c;
        private final char C;
        
        public Key(char c, char C) {
            this.c = c;
            this.C = C;
        }
        
        public char getChar(boolean shiftHeld) { return (!shiftHeld) ? c : C; }
    }
    
    TextArea(int xOffset, int yOffset, int width) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.width   = width;
        
        rectBack   = new Rectangle(xOffset, yOffset, width, TEXT_AREA_HEIGHT);
        rectFront  = new Rectangle(xOffset, yOffset + 1, width, TEXT_AREA_HEIGHT - 2);
        scissorBox = new Rectangle();
        
        iconLeft  = new Icon("spr_icons.png", 15, TEXT_AREA_HEIGHT);
        iconRight = new Icon("spr_icons.png", 15, TEXT_AREA_HEIGHT);
        carat     = new Icon("spr_icons.png", 15, TEXT_AREA_HEIGHT);
        
        iconLeft.setSprite(6, 0);
        iconRight.setSprite(7, 0);
        carat.setSprite(4, 2);
        
        iconLeft.setPosition(xOffset, yOffset + TEXT_AREA_HEIGHT);
        iconRight.setPosition(xOffset + (width - 15), yOffset + TEXT_AREA_HEIGHT);
        
        keyChars = new HashMap<>() {{
            put(GLFW_KEY_SPACE,      new Key(' ', ' '));
            put(GLFW_KEY_APOSTROPHE, new Key('\'', '\"'));
            put(GLFW_KEY_COMMA,      new Key(',', '<'));
            put(GLFW_KEY_MINUS,      new Key('-', '_'));
            put(GLFW_KEY_PERIOD,     new Key('.', '>'));
            put(GLFW_KEY_SLASH,      new Key('/', '?'));
            put(GLFW_KEY_0, new Key('0', ')'));
            put(GLFW_KEY_1, new Key('1', '!'));
            put(GLFW_KEY_2, new Key('2', '@'));
            put(GLFW_KEY_3, new Key('3', '#'));
            put(GLFW_KEY_4, new Key('4', '$'));
            put(GLFW_KEY_5, new Key('5', '%'));
            put(GLFW_KEY_6, new Key('6', '^'));
            put(GLFW_KEY_7, new Key('7', '&'));
            put(GLFW_KEY_8, new Key('8', '*'));
            put(GLFW_KEY_9, new Key('9', '('));
            put(GLFW_KEY_SEMICOLON, new Key(';', ':'));
            put(GLFW_KEY_EQUAL,     new Key('=', '+'));
            put(GLFW_KEY_A, new Key('a', 'A'));
            put(GLFW_KEY_B, new Key('b', 'B'));
            put(GLFW_KEY_C, new Key('c', 'C'));
            put(GLFW_KEY_D, new Key('d', 'D'));
            put(GLFW_KEY_E, new Key('e', 'E'));
            put(GLFW_KEY_F, new Key('f', 'F'));
            put(GLFW_KEY_G, new Key('g', 'G'));
            put(GLFW_KEY_H, new Key('h', 'H'));
            put(GLFW_KEY_I, new Key('i', 'I'));
            put(GLFW_KEY_J, new Key('j', 'J'));
            put(GLFW_KEY_K, new Key('k', 'K'));
            put(GLFW_KEY_L, new Key('l', 'L'));
            put(GLFW_KEY_M, new Key('m', 'M'));
            put(GLFW_KEY_N, new Key('n', 'N'));
            put(GLFW_KEY_O, new Key('o', 'O'));
            put(GLFW_KEY_P, new Key('p', 'P'));
            put(GLFW_KEY_Q, new Key('q', 'Q'));
            put(GLFW_KEY_R, new Key('r', 'R'));
            put(GLFW_KEY_S, new Key('s', 'S'));
            put(GLFW_KEY_T, new Key('t', 'T'));
            put(GLFW_KEY_U, new Key('u', 'U'));
            put(GLFW_KEY_V, new Key('v', 'V'));
            put(GLFW_KEY_W, new Key('w', 'W'));
            put(GLFW_KEY_X, new Key('x', 'X'));
            put(GLFW_KEY_Y, new Key('y', 'Y'));
            put(GLFW_KEY_Z, new Key('z', 'Z'));
            put(GLFW_KEY_LEFT_BRACKET,  new Key('[', '{'));
            put(GLFW_KEY_BACKSLASH,     new Key('\\', '|'));
            put(GLFW_KEY_RIGHT_BRACKET, new Key(']', '}'));
            put(GLFW_KEY_GRAVE_ACCENT,  new Key('`', '~'));
        }};
    }
    
    TextArea(String text, int xOffset, int yOffset, int width) {
        this(xOffset, yOffset, width);
        typed.append(text);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch(evt.getPropertyName()) {
            case "finished": //Used for cursor timer
                caratIdle = (Boolean) evt.getNewValue();
                break;
        }
    }
    
    private void insertChar(char c) {
        typed.insert(xIndex, c);
        xIndex++;
        scroll();
    }
    
    private void scroll() {
        
        int caratX = rectFront.xPos + TrueTypeFont.getLengthInPixels(typed.substring(0, xIndex), 1) + 4;
        int length = TrueTypeFont.getLengthInPixels(typed.substring(0, xIndex), 1);
        
        if(caratX > rectFront.xPos + width) {
            if(length > width) {
                System.out.println(length + " " + textPos.x + " " + rectFront.xPos);
                //System.out.println(length - (textPos.x - rectFront.xPos));
                //System.out.println(Math.abs(length - textPos.x) > width);
                
                textPos.x -= TrueTypeFont.getCharAdvance(typed.charAt(xIndex - 1), 1);
            }
        } else {
            if(caratX < rectFront.xPos) {
            } else {
                carat.setPosition(caratX, rectFront.yPos + TEXT_AREA_HEIGHT - 6);
            }
        }
    }
    
    public void processInput(int key, int action) {
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
    
    void focus() {
        hasFocus = true;
        UI.setTextArea(this);
    }
    
    void unfocus() {
        hasFocus = false;
        UI.setTextArea(null);
    }
    
    void update(Mouse mouse, int parentX, int parentY) {
        timer.update();
        if(App.tick(18) && caratIdle) caratBlink = !caratBlink;
        
        if(rectFront.intersects(mouse.cursorPos)) {
            //TODO: set cursor image
            
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
        
        rectBack.xPos = parentX + xOffset;
        rectBack.yPos = parentY + yOffset;
        
        rectFront.xPos = parentX + xOffset;
        rectFront.yPos = parentY + yOffset + 1;
        
        scissorBox.xPos   = parentX + xOffset + 1;
        scissorBox.yPos   = parentY + yOffset + TEXT_AREA_HEIGHT;
        scissorBox.width  = width;
        scissorBox.height = TEXT_AREA_HEIGHT;
        
        iconLeft.setPosition(
                parentX + xOffset, 
                parentY + yOffset + TEXT_AREA_HEIGHT);
        
        iconRight.setPosition(
                parentX + (xOffset + (width - 15)), 
                parentY + yOffset + TEXT_AREA_HEIGHT);
        
        //TODO: this is hacky
        if(!initialTextPos) {
            textPos.set(parentX + xOffset + 4, parentY + yOffset + 21);
            initialTextPos = true;
        } else {
            textPos.y = parentY + yOffset + 21;
        }
    }
    
    void renderBackground(Background background) {
        background.drawRectangle(rectBack, Color.LIGHT_GRAY);
        background.drawRectangle(rectFront, Color.MEDIUM_GRAY);
    }
    
    void renderIcon(ShaderProgram program) {
        iconLeft.render(program);
        iconRight.render(program);
        
        if(hasFocus && caratBlink) carat.render(program);
    }
    
    void renderText(ShaderProgram program, TrueTypeFont font) {
        font.drawString(
                scissorBox,
                program, 
                typed.toString(), 
                textPos.x, 
                textPos.y, 
                1, 
                Color.WHITE);
    }
    
}