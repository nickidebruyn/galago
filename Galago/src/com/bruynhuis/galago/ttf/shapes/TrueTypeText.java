/*
 * Feel free to use, modify, and/or distribute this source code for personal,
 * educational, commercial or any other reason you may conceive with or without
 * credit. There are absolutely no restrictions on the use, modification or
 * distribution of this code.
 */
package com.bruynhuis.galago.ttf.shapes;

import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import com.bruynhuis.galago.ttf.TrueTypeBitmapGlyph;

/**
 * This <code>Mesh</code> creates quads for each glyph in an array of
 * {@link TrueTypeBitmapGlyph}s, assigning UV coordinates and normals as
 * necessary.
 * 
 * @see truetypefont.TrueTypeFont
 * @see TrueTypeBitmapGlyph
 * @see truetypefont.TrueTypeFont#getBitmapText(truetypefont.TrueTypeBitmapGlyph[], int kerning) 
 * @see truetypefont.TrueTypeFont#getBitmapGeom(truetypefont.TrueTypeBitmapGlyph[], int kerning, com.jme3.math.ColorRGBA) 
 * 
 * @author Adam T. Ryder
 * <a href="http://1337atr.weebly.com">http://1337atr.weebly.com</a>
 */
public class TrueTypeText extends Mesh {
    public final int kerning;
    
    public TrueTypeText(TrueTypeBitmapGlyph[] glyphs, int kerning) {
        this.kerning = kerning;
        
        int numChars = 0;
        
        //Spaces don't have any geometry
        for (TrueTypeBitmapGlyph glyph : glyphs) {
            if (glyph.codePoint != ' ') {
                numChars++;
            }
        }
        
        FloatBuffer verts = BufferUtils.createFloatBuffer(numChars * 12);
        FloatBuffer norms = BufferUtils.createFloatBuffer(verts.capacity());
        FloatBuffer tex1 = BufferUtils.createFloatBuffer(numChars * 8);
        ShortBuffer indices = BufferUtils.createShortBuffer(numChars * 6);
        
        int currentX = 0;
        int padding = glyphs[0].ttf.padding / 2;
        short currentIndex = 0;
        for (TrueTypeBitmapGlyph glyph : glyphs) {
            if (glyph.text.equals(" ")) {
                currentX += glyph.xAdvance + kerning;
                continue;
            }
            
            //Lower left
            verts.put(currentX - padding);
            verts.put(-glyph.hMod - padding);
            verts.put(0);

            tex1.put(glyph.getLeftU());
            tex1.put(glyph.getBottomV());

            //Lower right
            verts.put(currentX + glyph.w);
            verts.put(-glyph.hMod - padding);
            verts.put(0);

            tex1.put(glyph.getRightU());
            tex1.put(glyph.getBottomV());

            //Upper left
            verts.put(currentX - padding);
            verts.put(glyph.h - glyph.hMod);
            verts.put(0);

            tex1.put(glyph.getLeftU());
            tex1.put(glyph.getTopV());

            //Upper right
            verts.put(currentX + glyph.w);
            verts.put(glyph.h - glyph.hMod);
            verts.put(0);

            tex1.put(glyph.getRightU());
            tex1.put(glyph.getTopV());
            
            norms.put(0);
            norms.put(0);
            norms.put(1);
            norms.put(0);
            norms.put(0);
            norms.put(1);
            norms.put(0);
            norms.put(0);
            norms.put(1);
            norms.put(0);
            norms.put(0);
            norms.put(1);

            indices.put(currentIndex);
            indices.put((short)(currentIndex + 1));
            indices.put((short)(currentIndex + 2));
            indices.put((short)(currentIndex + 2));
            indices.put((short)(currentIndex + 1));
            indices.put((short)(currentIndex + 3));
            
            currentX += glyph.xAdvance + kerning;
            currentIndex += 4;
        }
        
        setBuffer(Type.Position, 3, verts);
        setBuffer(Type.Normal, 3, norms);
        setBuffer(Type.TexCoord, 2, tex1);
        setBuffer(Type.Index, 3, indices);
        
        updateBound();
        updateCounts();
        
    }

//    @Override
//    public void createCollisionData() {
//        
//    }
    
    
}
