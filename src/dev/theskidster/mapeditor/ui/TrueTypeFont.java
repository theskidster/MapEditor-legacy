package dev.theskidster.mapeditor.ui;

import dev.theskidster.mapeditor.main.LogLevel;
import dev.theskidster.mapeditor.main.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.opengl.GL12.*;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;
import static org.lwjgl.stb.STBTruetype.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * @author J Hoffman
 * Created: Dec 22, 2020
 */

public class TrueTypeFont {
    
    private final int FONT_HEIGHT   = 18;
    private final int BITMAP_WIDTH  = 1024;
    private final int BITMAP_HEIGHT = 1024;
    private final int fontTexHandle = glGenTextures();
    
    private float scale;
    private float descent;
    
    /*
    private static final NkAllocator ALLOCATOR;
    private static final NkDrawVertexLayoutElement.Buffer VERTEX_LAYOUT;
    
    static {
        ALLOCATOR = NkAllocator.create()
                .alloc((handle, old, size) -> nmemAllocChecked(size))
                .mfree((handle, ptr) -> nmemFree(ptr));
        
        VERTEX_LAYOUT = NkDrawVertexLayoutElement.create(4)
                .position(0).attribute(NK_VERTEX_POSITION).format(NK_FORMAT_FLOAT).offset(0)
                .position(1).attribute(NK_VERTEX_TEXCOORD).format(NK_FORMAT_FLOAT).offset(8)
                .position(2).attribute(NK_VERTEX_COLOR).format(NK_FORMAT_R8G8B8A8).offset(16)
                .position(3).attribute(NK_VERTEX_ATTRIBUTE_COUNT).format(NK_FORMAT_COUNT).offset(0)
                .flip();
    }
    */
    
    public TrueTypeFont(String filename) {
        try(InputStream file = TrueTypeFont.class.getResourceAsStream("/dev/theskidster/mapeditor/assets/" + filename)) {
            loadFont(file);
        } catch(Exception e) {
            Logger.setStackTrace(e);
            Logger.log(LogLevel.WARNING, "Failed to load true type font: \"" + filename + "\"");
        }
    }
    
    private void loadFont(InputStream file) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            byte[] data = file.readAllBytes();
            
            ByteBuffer fontBuf     = MemoryUtil.memAlloc(data.length).put(data).flip();
            IntBuffer descentBuf   = stack.mallocInt(1);
            STBTTFontinfo fontInfo = STBTTFontinfo.create();
            
            stbtt_InitFont(fontInfo, fontBuf);
            stbtt_GetFontVMetrics(fontInfo, null, descentBuf, null);
            
            scale   = stbtt_ScaleForPixelHeight(fontInfo, FONT_HEIGHT);
            descent = descentBuf.get(0) * scale;
            
            ByteBuffer bitmapBuf = MemoryUtil.memAlloc(BITMAP_WIDTH * BITMAP_HEIGHT);
            
            STBTTPackContext pc            = STBTTPackContext.mallocStack(stack);
            STBTTPackedchar.Buffer charBuf = STBTTPackedchar.create(95);
            
            stbtt_PackBegin(pc, bitmapBuf, BITMAP_WIDTH, BITMAP_HEIGHT, 0, 1, NULL);
            stbtt_PackSetOversampling(pc, 4, 4);
            stbtt_PackFontRange(pc, fontBuf, 0, FONT_HEIGHT, 32, charBuf);
            stbtt_PackEnd(pc);
            
            ByteBuffer textureBuf = MemoryUtil.memAlloc(BITMAP_WIDTH * BITMAP_HEIGHT * Float.BYTES);
            
            for(int i = 0; i < bitmapBuf.capacity(); i++) {
                textureBuf.putInt((bitmapBuf.get(i) << 24) | 0x00FFFFFF);
            }
            textureBuf.flip();
            
            glBindTexture(GL_TEXTURE_2D, fontTexHandle);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, BITMAP_WIDTH, BITMAP_HEIGHT, 0, GL_RGBA, GL_UNSIGNED_INT_8_8_8_8_REV, textureBuf);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            
            MemoryUtil.memFree(textureBuf);
            MemoryUtil.memFree(bitmapBuf);
        } catch(IOException e) {
            Logger.setStackTrace(e);
            Logger.log(LogLevel.SEVERE, "Failed to parse data from ttf file.");
        }
    }
    
    public float getScale()   { return scale; }
    public float getDescent() { return descent; }
    
}