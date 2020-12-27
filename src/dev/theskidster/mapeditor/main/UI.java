package dev.theskidster.mapeditor.main;

import static dev.theskidster.mapeditor.main.TrueTypeFont.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.nuklear.NkAllocator;
import org.lwjgl.nuklear.NkBuffer;
import org.lwjgl.nuklear.NkContext;
import org.lwjgl.nuklear.NkConvertConfig;
import org.lwjgl.nuklear.NkDrawCommand;
import org.lwjgl.nuklear.NkDrawNullTexture;
import org.lwjgl.nuklear.NkDrawVertexLayoutElement;
import org.lwjgl.nuklear.NkMouse;
import org.lwjgl.nuklear.NkRect;
import org.lwjgl.nuklear.NkUserFont;
import org.lwjgl.nuklear.NkUserFontGlyph;
import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.stb.STBTTAlignedQuad;
import static org.lwjgl.stb.STBTruetype.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * @author J Hoffman
 * Created: Dec 26, 2020
 */

final class UI {
    
    private int vao;
    private int vbo;
    private int ibo;
    
    private final int MAX_VBUFFER_SIZE = 512 * 1024;
    private final int MAX_IBUFFER_SIZE = 128 * 1024;
    
    private Vector2f framebufferScale = new Vector2f();
    private Matrix4f projMatrix       = new Matrix4f();
    
    private NkContext nkContext         = NkContext.create();
    private NkAllocator nkAlloc         = NkAllocator.create();
    private NkBuffer nkCommandBuf       = NkBuffer.create();
    private NkDrawNullTexture nkNullTex = NkDrawNullTexture.create();
    private NkUserFont nkFont           = NkUserFont.create();
    
    private NkDrawVertexLayoutElement.Buffer nkVertexLayout = NkDrawVertexLayoutElement.create(4);
    
    UI(Window window) {
        nkAlloc.alloc((handle, old, size) -> MemoryUtil.nmemAllocChecked(size));
        nkAlloc.mfree((handle, pointer) -> MemoryUtil.nmemFree(pointer));
        
        nkVertexLayout.position(0).attribute(NK_VERTEX_POSITION).format(NK_FORMAT_FLOAT).offset(0);
        nkVertexLayout.position(1).attribute(NK_VERTEX_TEXCOORD).format(NK_FORMAT_FLOAT).offset(8);
        nkVertexLayout.position(2).attribute(NK_VERTEX_COLOR).format(NK_FORMAT_R8G8B8A8).offset(16);
        nkVertexLayout.position(3).attribute(NK_VERTEX_ATTRIBUTE_COUNT).format(NK_FORMAT_COUNT).offset(0);
        nkVertexLayout.flip();
        
        nk_buffer_init(nkCommandBuf, nkAlloc, Float.BYTES * 1024);
        
        window.setCallbacks(nkContext);        
        nkContext = nkInit(window.handle);
        
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        ibo = glGenBuffers();
        
        //Initialize vertex attributes
        {
            glBindVertexArray(vao);
            glBindBuffer(GL_ARRAY_BUFFER, vbo);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);

            //Values passed here correspond to those assinged in the UI vertex shader.
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);
            glEnableVertexAttribArray(2);

            glVertexAttribPointer(0, 2, GL_FLOAT, false, 20, 0);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 20, 8);
            glVertexAttribPointer(2, 4, GL_UNSIGNED_BYTE, true, 20, 16);
        }
        
        //Initialize null texture
        {
            int texHandle = glGenTextures();
            
            nkNullTex.texture().id(texHandle);
            nkNullTex.uv().set(0.5f, 0.5f);
            
            glBindTexture(GL_TEXTURE_2D, texHandle);
            
            try(MemoryStack stack = MemoryStack.stackPush()) {
                glTexImage2D(
                        GL_TEXTURE_2D, 
                        0, 
                        GL_RGBA8, 
                        1, 1, 
                        0, 
                        GL_RGBA, 
                        GL_UNSIGNED_INT_8_8_8_8_REV, 
                        stack.ints(0xFFFFFFFF));
            }
            
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            
            //Reset OpenGL state
            glBindTexture(GL_TEXTURE_2D, 0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
        
        TrueTypeFont font = new TrueTypeFont("fnt_karlaregular.ttf");
        
        nkFont.width((handle, h, text, len) -> {
            float textWidth = 0;
            
            try(MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer unicodeBuf = stack.mallocInt(1);
                IntBuffer advanceBuf = stack.mallocInt(1);
                
                int glyphLength = nnk_utf_decode(text, MemoryUtil.memAddress(unicodeBuf), len);
                int textLength  = glyphLength;
                
                if(glyphLength == 0) return 0;
                
                while(textLength <= len && glyphLength != 0) {
                    if(unicodeBuf.get(0) == NK_UTF_INVALID) break;
                    
                    stbtt_GetCodepointHMetrics(font.info, unicodeBuf.get(0), advanceBuf, null);
                    textWidth += advanceBuf.get(0) * font.scale;
                    
                    glyphLength = nnk_utf_decode(text + textLength, MemoryUtil.memAddress(unicodeBuf), len - textLength);
                    textLength += glyphLength;
                }
            }
            
            return textWidth;
        });
        
        nkFont.height(FONT_HEIGHT);
        
        nkFont.query((handle, fontHeight, glyph, codepoint, nextCodepoint) -> {
            try(MemoryStack stack = MemoryStack.stackPush()) {
                FloatBuffer xBuf = stack.floats(0.0f);
                FloatBuffer yBuf = stack.floats(0.0f);
                
                STBTTAlignedQuad quad = STBTTAlignedQuad.mallocStack(stack);
                IntBuffer advanceBuf  = stack.mallocInt(1);
                
                stbtt_GetPackedQuad(font.charBuf, BITMAP_WIDTH, BITMAP_HEIGHT, codepoint - 32, xBuf, yBuf, quad, false);
                stbtt_GetCodepointHMetrics(font.info, codepoint, advanceBuf, null);
                
                NkUserFontGlyph nkGlyph = NkUserFontGlyph.create(glyph);
                
                nkGlyph.width(quad.x1() - quad.x0());
                nkGlyph.height(quad.y1() - quad.y0());
                nkGlyph.offset().set(quad.x0(), quad.y0() + (FONT_HEIGHT + font.descent));
                nkGlyph.xadvance(advanceBuf.get(0) * font.scale);
                nkGlyph.uv(0).set(quad.s0(), quad.t0());
                nkGlyph.uv(1).set(quad.s1(), quad.t1());
            }
        });
        
        nkFont.texture(it -> it.id(font.texHandle));
        
        nk_style_set_font(nkContext, nkFont);
    }
    
    private NkContext nkInit(long winHandle) {
        nk_init(nkContext, nkAlloc, null);
        
        //Specifies clipboard copy action
        nkContext.clip().copy((handle, text, length) -> {
            if(length == 0) return;
            
            try(MemoryStack stack = MemoryStack.stackPush()) {
                ByteBuffer stringBuf = stack.malloc(length + 1);
                
                MemoryUtil.memCopy(text, MemoryUtil.memAddress(stringBuf), length);
                stringBuf.put(length, (byte) 0);
                
                glfwSetClipboardString(winHandle, stringBuf);
            }
        });
        
        //Specifies clipboard paste action
        nkContext.clip().paste((handle, edit) -> {
            long text = nglfwGetClipboardString(winHandle);
            if(text != NULL) nnk_textedit_paste(edit, text, nnk_strlen(text));
        });
        
        return nkContext;
    }
    
    void beginInput() {
        nk_input_begin(nkContext);
    }
    
    void endInput(long handle) {
        NkMouse mouse = nkContext.input().mouse();
        
        if(mouse.grab()) {
            glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        } else if(mouse.grabbed()) {
            float prevX = mouse.prev().x();
            float prevY = mouse.prev().y();
            
            glfwSetCursorPos(handle, prevX, prevY);
            
            mouse.pos().x(prevX);
            mouse.pos().y(prevY);
        } else if(mouse.ungrab()) {
            glfwSetInputMode(handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
        
        nk_input_end(nkContext);
    }
    
    void update(Window window) {
        projMatrix.set(
                2f / window.width, 0, 0, 0, 
                0, -2f / window.height, 0, 0, 
                0, 0, -1f, 0, 
                -1f, 1f, 0, 1f);
        
        try(MemoryStack stack = MemoryStack.stackPush()) {
            NkRect rect = NkRect.mallocStack(stack);
            
            if(nk_begin(nkContext, window.title, nk_rect(400, 400, 300, 200, rect), NK_WINDOW_BORDER)) {
                nk_layout_row_dynamic(nkContext, 20, 1);
                nk_label(nkContext, "test text", NK_TEXT_ALIGN_LEFT | NK_TEXT_ALIGN_BOTTOM);
            }
            
            nk_end(nkContext);
        }
    }
    
    void render(Window window, ShaderProgram program) {
        glEnable(GL_BLEND);
        glBlendEquation(GL_FUNC_ADD);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_SCISSOR_TEST);
        glActiveTexture(GL_TEXTURE0);
        
        program.setUniform("uTexture", 0);
        program.setUniform("uProjection", false, projMatrix);

        glViewport(0, 0, window.viewWidth, window.viewHeight);
        
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        
        glBufferData(GL_ARRAY_BUFFER, MAX_VBUFFER_SIZE, GL_STREAM_DRAW);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, MAX_IBUFFER_SIZE, GL_STREAM_DRAW);
        
        ByteBuffer vertexBuf = Objects.requireNonNull(glMapBuffer(GL_ARRAY_BUFFER, GL_WRITE_ONLY, MAX_VBUFFER_SIZE, null));
        ByteBuffer indexBuf  = Objects.requireNonNull(glMapBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_WRITE_ONLY, MAX_IBUFFER_SIZE, null));
        
        {
            try(MemoryStack stack = MemoryStack.stackPush()) {
                NkConvertConfig nkConfig = NkConvertConfig.callocStack(stack);
                
                nkConfig.vertex_layout(nkVertexLayout);
                nkConfig.vertex_size(20);
                nkConfig.vertex_alignment(4);
                nkConfig.null_texture(nkNullTex);
                nkConfig.circle_segment_count(22);
                nkConfig.curve_segment_count(22);
                nkConfig.arc_segment_count(22);
                nkConfig.global_alpha(1f);
                nkConfig.shape_AA(NK_ANTI_ALIASING_ON);
                nkConfig.line_AA(NK_ANTI_ALIASING_ON);
                
                NkBuffer nkVertexBuf = NkBuffer.mallocStack(stack);
                NkBuffer nkIndexBuf  = NkBuffer.mallocStack(stack);
                
                nk_buffer_init_fixed(nkVertexBuf, vertexBuf);
                nk_buffer_init_fixed(nkIndexBuf, indexBuf);
                
                nk_convert(nkContext, nkCommandBuf, nkVertexBuf, nkIndexBuf, nkConfig);
            }
            
            glUnmapBuffer(GL_ARRAY_BUFFER);
            glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER);
            
            framebufferScale.x = (float) window.viewWidth / (float) window.width;
            framebufferScale.y = (float) window.viewHeight / (float) window.height;
            
            long offset = NULL;
            
            for(NkDrawCommand nkCommand = nk__draw_begin(nkContext, nkCommandBuf); 
                nkCommand != null; 
                nkCommand = nk__draw_next(nkCommand, nkCommandBuf, nkContext)) {
                
                if(nkCommand.elem_count() == 0) continue;
                
                glBindTexture(GL_TEXTURE_2D, nkCommand.texture().id());
                
                glScissor(
                        (int) (nkCommand.clip_rect().x() * framebufferScale.x),
                        (int) ((window.height - (int) (nkCommand.clip_rect().y() + nkCommand.clip_rect().h())) * framebufferScale.y),
                        (int) (nkCommand.clip_rect().w() * framebufferScale.x),
                        (int) (nkCommand.clip_rect().h() * framebufferScale.y));
                
                glDrawElements(GL_TRIANGLES, nkCommand.elem_count(), GL_UNSIGNED_SHORT, offset);
                
                offset += nkCommand.elem_count() * 2;
            }
            
            nk_clear(nkContext);
        }
        
        App.checkGLError();
        
        //Reset OpenGL state
        glUseProgram(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        glDisable(GL_BLEND);
        glDisable(GL_SCISSOR_TEST);
    }
    
}