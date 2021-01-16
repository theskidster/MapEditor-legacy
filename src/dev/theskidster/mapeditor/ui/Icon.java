package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.graphics.Atlas;
import dev.theskidster.mapeditor.graphics.Graphics;
import dev.theskidster.mapeditor.graphics.Texture;
import dev.theskidster.mapeditor.main.App;
import dev.theskidster.mapeditor.main.LogLevel;
import dev.theskidster.mapeditor.main.Logger;
import dev.theskidster.mapeditor.main.ShaderProgram;
import java.util.HashMap;
import java.util.Map;
import org.joml.Vector2f;
import org.joml.Vector2i;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.system.MemoryStack;

/**
 * @author J Hoffman
 * Created: Jan 10, 2021
 */

class Icon {
    
    private final Graphics g = new Graphics();
    private final Texture texture;
    private final Atlas atlas;
    
    private Vector2f currCell = new Vector2f();
    final Vector2f position   = new Vector2f();
    
    private final Map<Vector2i, Vector2f> texOffsets = new HashMap<>();
    
    Icon(String filename, int cellWidth, int cellHeight) {
        texture = new Texture(filename);
        
        glBindTexture(GL_TEXTURE_2D, texture.handle);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);
        
        atlas = new Atlas(texture, cellWidth, cellHeight);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            g.vertices = stack.mallocFloat(20);
            g.indices  = stack.mallocInt(6);
            
            //(vec2 position), (vec2 tex coords)
            g.vertices.put(0)        .put(-cellHeight)  .put(0)             .put(0);
            g.vertices.put(cellWidth).put(-cellHeight)  .put(atlas.imgWidth).put(0);
            g.vertices.put(cellWidth).put(0)            .put(atlas.imgWidth).put(atlas.imgHeight);
            g.vertices.put(0)        .put(0)            .put(0)             .put(atlas.imgHeight);
            
            g.indices.put(0).put(1).put(2);
            g.indices.put(2).put(3).put(0);
            
            g.vertices.flip();
            g.indices.flip();
        }
        
        g.bindBuffers();
        
        glVertexAttribPointer(0, 2, GL_FLOAT, false, (4 * Float.BYTES), 0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, (4 * Float.BYTES), (2 * Float.BYTES));
        
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        
        float texPosX = 0;
        float texPosY = 0;
        int cellPosX  = 0;
        int cellPosY  = 0;
        
        for(int i = 0; i < atlas.imgCount; i++) {
            if(i % atlas.rows != 0 && i != 0) {
                texPosX += atlas.imgWidth;
                cellPosX++;
                
                texOffsets.put(new Vector2i(cellPosX, cellPosY), new Vector2f(texPosX, texPosY));
            } else if(i == 0) {
                texOffsets.put(new Vector2i(), new Vector2f());
            } else {
                texPosX  = 0;
                cellPosX = 0;
                texPosY += atlas.imgHeight;
                cellPosY++; 
                
                texOffsets.put(new Vector2i(cellPosX, cellPosY), new Vector2f(texPosX, texPosY));
            }
        }
    }
    
    void setSprite(int cellX, int cellY) {
        Vector2i cell = new Vector2i(cellX, cellY);
        
        if(texOffsets.containsKey(cell)) {
            currCell = texOffsets.get(new Vector2i(cellX, cellY));
        } else {
            Logger.log(LogLevel.WARNING, 
                    "Failed to set icon sprite. The cell: (" + cellX + ", " + cellY + 
                    ") is out of bounds.");
        }
    }
    
    void render(ShaderProgram program) {
        glEnable(GL_BLEND);
        
        glBindTexture(GL_TEXTURE_2D, texture.handle);
        glBindVertexArray(g.vao);
        
        program.setUniform("uType", 2);
        program.setUniform("uTexCoords", currCell);
        program.setUniform("uPosition", position);
        
        glDrawElements(GL_TRIANGLES, g.indices.limit(), GL_UNSIGNED_INT, 0);
        
        glDisable(GL_BLEND);
        
        App.checkGLError();
    }
    
}