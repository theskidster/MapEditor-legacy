package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.util.Rectangle;
import dev.theskidster.mapeditor.util.Color;
import com.mlomb.freetypejni.Face;
import static com.mlomb.freetypejni.FreeTypeConstants.FT_LOAD_RENDER;
import com.mlomb.freetypejni.Library;
import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.LogLevel;
import dev.theskidster.mapeditor.main.Logger;
import dev.theskidster.mapeditor.main.ShaderProgram;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2i;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author J Hoffman
 * Created: Jan 5, 2021
 */

class TrueTypeFont {
    
    public static final int FONT_HEIGHT = 17;
    
    private Vector3f color = new Vector3f();
    
    private final int vao = glGenVertexArrays();
    private final int vbo = glGenBuffers();
    
    private static Map<Character, Glyph> glyphs = new HashMap<>();
    
    TrueTypeFont(Library freeType, String filename) {
        try(InputStream file = TrueTypeFont.class.getResourceAsStream("/dev/theskidster/mapeditor/assets/" + filename)) {
            loadFont(freeType, file);
            
            glBindVertexArray(vao);
            
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferData(GL_ARRAY_BUFFER, Float.BYTES * 6 * 4, GL_DYNAMIC_DRAW);
            
            glVertexAttribPointer(0, 2, GL_FLOAT, false, (4 * Float.BYTES), 0);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, (4 * Float.BYTES), (2 * Float.BYTES));
            
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);
            
        } catch(Exception e) {
            Logger.setStackTrace(e);
            Logger.log(LogLevel.WARNING, "Failed to load true type font: \"" + filename + "\"");
        }
    }
    
    private void loadFont(Library freeType, InputStream file) {
        try {
            Face face = freeType.newFace(file.readAllBytes(), 0);
            
            face.setPixelSizes(0, FONT_HEIGHT);
            
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            
            for(char c = 0; c < 128; c++) {
                face.loadChar(c, FT_LOAD_RENDER);
                
                Glyph g     = new Glyph();
                g.texHandle = glGenTextures();
                
                glBindTexture(GL_TEXTURE_2D, g.texHandle);
                
                glTexImage2D(GL_TEXTURE_2D, 
                             0, 
                             GL_RED, 
                             face.getGlyphSlot().getBitmap().getWidth(),
                             face.getGlyphSlot().getBitmap().getRows(),
                             0,
                             GL_RED,
                             GL_UNSIGNED_BYTE,
                             face.getGlyphSlot().getBitmap().getBuffer());
                
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                
                g.advance = face.getGlyphSlot().getAdvance().getX();
                g.size    = new Vector2i(face.getGlyphSlot().getBitmap().getWidth(), 
                                         face.getGlyphSlot().getBitmap().getRows());
                g.bearing = new Vector2i(face.getGlyphSlot().getBitmapLeft(), 
                                         face.getGlyphSlot().getBitmapTop());
                
                glyphs.put(c, g);
            }
            
            face.delete();
            freeType.delete();
            
        } catch(IOException e) {
            Logger.setStackTrace(e);
            Logger.log(LogLevel.SEVERE, "Failed to parse font data from ttf file.");
        }
    }
    
    void drawString(ShaderProgram program, String text, float x, float y, float scale, Color color) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        glBindVertexArray(vao);
        glActiveTexture(GL_TEXTURE0);
        
        program.setUniform("uType", 1);
        program.setUniform("uFontColor", this.color.set(color.r, color.g, color.b));
        
        for(char c : text.toCharArray()) {
            Glyph g = glyphs.get(c);
            
            float xPos = x + g.bearing.x * scale;
            float yPos = y + (g.size.y - g.bearing.y) * scale;
            
            float w = g.size.x * scale;
            float h = g.size.y * scale;

            float vertices[] = {
                xPos,     yPos - h, 0, 0,
                xPos,     yPos,     0, 1,
                xPos + w, yPos,     1, 1,

                xPos,     yPos - h, 0, 0,
                xPos + w, yPos,     1, 1,
                xPos + w, yPos - h, 1, 0,
            };

            glBindTexture(GL_TEXTURE_2D, g.texHandle);

            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

            glDrawArrays(GL_TRIANGLES, 0, 6);
            
            x += (g.advance >> 6) * scale;
        }
        
        glDisable(GL_BLEND);
        
        App.checkGLError();
    }
    
    void drawString(Rectangle rectangle, ShaderProgram program, String text, float x, float y, float scale, Color color) {
        glEnable(GL_SCISSOR_TEST);
        
        glScissor((int) rectangle.xPos, (int) rectangle.yPos, (int) rectangle.width, (int) rectangle.width);
        drawString(program, text, x, y, scale, color);
        
        glDisable(GL_SCISSOR_TEST);
    }
    
    static int getLengthInPixels(String text, float scale) {
        int length = 0;
        for(char c : text.toCharArray()) length += (glyphs.get(c).advance >> 6) * scale;
        
        return length;
    }
    
    static int getCharAdvance(char c, float scale) {
        return (int) ((glyphs.get(c).advance >> 6) * scale);
    }
    
}