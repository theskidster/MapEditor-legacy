package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.Icon;
import dev.theskidster.mapeditor.graphics.TrueTypeFont;
import dev.theskidster.mapeditor.util.Rectangle;
import dev.theskidster.mapeditor.util.Timer;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2i;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author J Hoffman
 * Created: Jan 19, 2021
 */

public abstract class Focusable extends Element {
    
    protected final int FOCUSABLE_HEIGHT = 30;
    protected final int PADDING = 4;
    
    protected int parentX;
    protected int parentY;
    protected int xIndex;
    protected int lengthToIndex;
    protected int textOffset;
    
    protected final int xOffset;
    protected final int yOffset;
    protected final int width;
    
    protected boolean hasFocus;
    protected boolean shiftHeld;
    protected boolean caratIdle;
    protected boolean caratBlink;
    
    protected final StringBuilder typed = new StringBuilder();
    protected final Vector2i textPos    = new Vector2i();
    
    protected Rectangle rectBack;
    protected Rectangle rectFront;
    protected Rectangle scissorBox;
    
    protected final Icon carat;
    
    protected Timer timer;
    
    protected Map<Integer, Key> keyChars;
    
    protected class Key {
        private final char c;
        private final char C;
        
        public Key(char c, char C) {
            this.c = c;
            this.C = C;
        }
        
        public char getChar(boolean shiftHeld) { return (!shiftHeld) ? c : C; }
    }
    
    Focusable(int xOffset, int yOffset, int width) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.width   = width;
        
        rectBack   = new Rectangle(xOffset, yOffset, width, FOCUSABLE_HEIGHT);
        rectFront  = new Rectangle(xOffset, yOffset + 1, width, FOCUSABLE_HEIGHT - 2);
        scissorBox = new Rectangle();
        carat      = new Icon("spr_icons.png", 15, FOCUSABLE_HEIGHT);
        
        carat.setSprite(4, 2);
        
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
    
    abstract void focus();
    abstract void unfocus();
    
    abstract void processInput(int key, int action);

    protected void insertChar(char c) {
        typed.insert(xIndex, c);
        xIndex++;
        scroll();
    }
    
    protected void scroll() {
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
    
}