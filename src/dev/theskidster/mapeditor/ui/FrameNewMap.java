package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.Background;
import dev.theskidster.mapeditor.graphics.TrueTypeFont;
import dev.theskidster.mapeditor.graphics.Icon;
import dev.theskidster.mapeditor.util.Mouse;
import dev.theskidster.mapeditor.util.Color;
import dev.theskidster.mapeditor.main.ShaderProgram;
import dev.theskidster.mapeditor.util.Observable;
import java.io.File;

/**
 * @author J Hoffman
 * Created: Jan 8, 2021
 */

public final class FrameNewMap extends Frame {
    
    private final FocusableTextArea textArea1;
    private final FocusableTextArea textArea2;
    private final ElementFolderButton fButton1;
    private final ElementFolderButton fButton2;
    private final FocusableSpinBox spinBox1;
    private final FocusableSpinBox spinBox2;
    private final FocusableSpinBox spinBox3;
    
    private final Background background;
    
    private final Observable observable = new Observable(this);
    
    //TODO: parse files using location provided from text areas
    private File blockset;
    private File skybox;
    
    public FrameNewMap(int xPos, int yPos) {
        super(new Icon("spr_icons.png", 20, 20), "New Map", xPos, yPos, 488, 430, true);
        
        background = new Background(19);
        
        textArea1 = new FocusableTextArea(126, 52, 300);
        textArea2 = new FocusableTextArea(126, 162, 300);
        fButton1  = new ElementFolderButton(440, 77, textArea1);
        fButton2  = new ElementFolderButton(440, 187, textArea2);
        spinBox1  = new FocusableSpinBox(126, 272, 186, "blocks", 64);
        spinBox2  = new FocusableSpinBox(126, 315, 186, "blocks", 32);
        spinBox3  = new FocusableSpinBox(126, 358, 186, "blocks", 64);
        
        observable.properties.put("parentX", xPos);
        observable.properties.put("parentY", yPos);
        observable.addObserver(textArea1);
        observable.addObserver(textArea2);
        observable.addObserver(fButton1);
        observable.addObserver(fButton2);
        observable.addObserver(spinBox1);
        observable.addObserver(spinBox2);
        observable.addObserver(spinBox3);
        
        icon.setSprite(2, 2);
    }
    
    @Override
    void update(int width, int height, Mouse mouse) {
        center(width, height);
        observable.notifyObservers("parentX", xPos);
        observable.notifyObservers("parentY", yPos);
        
        closeButton.update(mouse);
        textArea1.update(mouse);
        textArea2.update(mouse);
        fButton1.update(mouse);
        fButton2.update(mouse);
        spinBox1.update(mouse);
        spinBox2.update(mouse);
        spinBox3.update(mouse);
    }

    @Override
    void render(ShaderProgram program, TrueTypeFont font) {
        background.batchStart();
            background.drawRectangle(titleBar, Color.BLACK);
            background.drawRectangle(content, Color.DARK_GRAY);
            background.drawRectangle(xPos + 13, yPos + 38, 462, 58, Color.LIGHT_GRAY);
            background.drawRectangle(xPos + 14, yPos + 39, 460, 56, Color.DARK_GRAY);
            background.drawRectangle(xPos + 13, yPos + 148, 462, 58, Color.LIGHT_GRAY);
            background.drawRectangle(xPos + 14, yPos + 149, 460, 56, Color.DARK_GRAY);
            background.drawRectangle(xPos + 13, yPos + 258, 337, 144, Color.LIGHT_GRAY);
            background.drawRectangle(xPos + 14, yPos + 259, 335, 142, Color.DARK_GRAY);
            closeButton.renderBackground(background);
            textArea1.renderBackground(background);
            textArea2.renderBackground(background);
            spinBox1.renderBackground(background);
            spinBox2.renderBackground(background);
            spinBox3.renderBackground(background);
        background.batchEnd(program);
        
        icon.render(program);
        closeButton.renderIcon(program);
        textArea1.renderIcon(program);
        textArea2.renderIcon(program);
        fButton1.renderIcon(program);
        fButton2.renderIcon(program);
        spinBox1.renderIcon(program);
        spinBox2.renderIcon(program);
        spinBox3.renderIcon(program);
        
        font.drawString(program, "New Map", xPos + 45, yPos - (TITLE_BAR_HEIGHT / 3), 1, Color.WHITE);
        font.drawString(program, "Blockset:", xPos + 12, yPos + 24, 1, Color.WHITE);
        font.drawString(program, "Source:", xPos + 26, yPos + 73, 1, Color.WHITE);
        font.drawString(program, "Skybox:", xPos + 12, yPos + 134, 1, Color.WHITE);
        font.drawString(program, "Source:", xPos + 26, yPos + 183, 1, Color.WHITE);
        font.drawString(program, "Dimensions:", xPos + 12, yPos + 244, 1, Color.WHITE);
        font.drawString(program, "Width:", xPos + 26, yPos + 293, 1, Color.WHITE);
        font.drawString(program, "Height:", xPos + 26, yPos + 336, 1, Color.WHITE);
        font.drawString(program, "Depth:", xPos + 26, yPos + 379, 1, Color.WHITE);
        textArea1.renderText(program, font);
        textArea2.renderText(program, font);
        spinBox1.renderText(program, font);
        spinBox2.renderText(program, font);
        spinBox3.renderText(program, font);
    }

    @Override
    void close() {
        textArea1.unfocus();
        textArea2.unfocus();
        spinBox1.unfocus();
        spinBox2.unfocus();
        spinBox3.unfocus();
    }
    
}