package dev.theskidster.mapeditor.ui;

/**
 * @author J Hoffman
 * Created: Jan 19, 2021
 */

public abstract class Focusable extends Element {

    protected boolean hasFocus;
    
    abstract void focus();
    abstract void unfocus();
    
    abstract void processInput(int key, int action);

}