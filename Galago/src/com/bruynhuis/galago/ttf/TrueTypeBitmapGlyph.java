/*
 * Feel free to use, modify, and/or distribute this source code for personal,
 * educational, commercial or any other reason you may conceive with or without
 * credit. There are absolutely no restrictions on the use, modification or
 * distribution of this code.
 */
package com.bruynhuis.galago.ttf;

import android.graphics.Path;
import java.awt.font.GlyphVector;
import com.bruynhuis.galago.ttf.util.Glyf;

/**
 * A <code>TrueTypeBitmapGlyph</code> represents a glyph currently stored in a
 * texture atlas from a {@link TrueTypeFont}. This class holds variables necessary
 * in retrieving the UV coordinates from the texture atlas, the appropriate width
 * and height as-well as the advance along the x-axis to the next character.
 * 
 * @see TrueTypeFont
 * 
 * @author Adam T. Ryder
 * <a href="http://1337atr.weebly.com">http://1337atr.weebly.com</a>
 */
public class TrueTypeBitmapGlyph {
    public Path contour;
    
    public final int x;
    public final int y;
    public final int w;
    public final int h;
    
    public final int hMod;
    public final int xMod;
    
    public final int xAdvance;
    
    public final TrueTypeFont ttf;
    public final int codePoint;
    public final String text;
    
    public final int visualHeight;
    
    /**
     * For internal use only. Creates a <code>TrueTypeBitmapGlyph</code>
     * from the given character contained in the supplied <code>StringBuilder</code>
     * and defined by the given unicode code point.
     * 
     * @param atlasX The x position of the character in the texture atlas.
     * @param atlasY The y position of the character in the texture atlas.
     * @param width The width of the character in the texture atlas.
     * @param gv The characters <code>java.awt.GlyphVector</code>
     * @param ttf The {@link TrueTypeFont} used to create this character.
     * @param codePoint This characters Unicode code point.
     * @param sb A <code>StringBuilder</code> containing only this character.
     */
    public TrueTypeBitmapGlyph(int atlasX, int atlasY, int width, GlyphVector gv,
            TrueTypeFont ttf, int codePoint, StringBuilder sb) {
        x = atlasX;
        y = atlasY;
        
        this.codePoint = codePoint;
        text = sb.toString();
        this.ttf = ttf;
        
        w = width;
        h = (int)Math.ceil(gv.getVisualBounds().getHeight()) + ttf.padding;
        hMod = (int)gv.getVisualBounds().getMaxY();
        xMod = (int)gv.getVisualBounds().getMinX();
        
        xAdvance = (int)gv.getGlyphMetrics(0).getAdvanceX() + ttf.outline + Math.round(ttf.outline / 2f);
        
        visualHeight = -(int)gv.getVisualBounds().getMinY() + ttf.outline + Math.round(ttf.outline / 2f);
        
        contour = null;
    }
    
    /**
     * Used to create a <code>TrueTypeBitmapGlyph</code> for the space character.
     * 
     * @param gv
     * @param ttf
     * @param codePoint 
     */
    public TrueTypeBitmapGlyph(GlyphVector gv, TrueTypeFont ttf, int codePoint) {
        x = 0;
        y = 0;
        
        this.codePoint = codePoint;
        text = " ";
        this.ttf = ttf;
        
        h = ttf.padding;
        hMod = 0;
        xMod = 0;
        
        xAdvance = (int)gv.getGlyphMetrics(0).getAdvanceX() + ttf.outline + Math.round(ttf.outline / 2f);
        w = (int)Math.ceil(xAdvance) + ttf.padding;
        
        visualHeight = -(int)gv.getVisualBounds().getMinY();
        
        contour = null;
    }
    
    /**
     * Used when loading on Android.
     * 
     * @param atlasX
     * @param atlasY
     * @param width
     * @param glyf
     * @param xAdvance
     * @param ttf
     * @param codePoint
     * @param sb 
     */
    public TrueTypeBitmapGlyph(int atlasX, int atlasY, int width, Glyf glyf, float xAdvance,
            TrueTypeFont ttf, int codePoint, StringBuilder sb) {
        x = atlasX;
        y = atlasY;
        
        this.codePoint = codePoint;
        text = sb.toString();
        this.ttf = ttf;
        
        w = width;
        h = (int)Math.ceil(glyf.getHeight()) + ttf.padding + ttf.bold;
        hMod = (int)-Math.ceil(glyf.minY - (ttf.bold / 2f));
        xMod = (int)Math.ceil(glyf.minX - (ttf.bold / 2f));
        visualHeight = (int)Math.ceil(glyf.maxY) + ttf.outline  + ttf.bold;
        this.xAdvance = Math.round(xAdvance);
        
        contour = glyf.contours;
    }
    
    /**
     * 
     * @return The height, in pixels, of one line of text.
     */
    public int getLineHeight() {
        return ttf.lineHeight;
    }
    
    /**
     * 
     * @return The distance, in pixels, from this characters origin to the
     * beginning of the next character.
     */
    public int getXAdvance() {
        return xAdvance;
    }
    
    /**
     * 
     * @return The width of the glyph from origin to the end of the shape
     * along the x-axis.
     */
    public int getVisualWidth() {
        if (text.equals(" "))
            return xAdvance;
        
        return w - ttf.padding;
    }
    
    public int getVisualHeight() {
        return visualHeight;
    }
    
    /**
     * 
     * @return The Y offset of the character in the atlas from its intended
     * Y location relative to the characters origin. When displaying the character
     * you'll want to subtract this value from the intended locations y-axis value.
     */
    public int getHeightOffset() {
        return hMod;
    }
    
    /**
     * 
     * @return The X offset of the character in the atlas from its intended
     * X location relative to the characters origin. When displaying the character
     * you'll want to add this value from the intended locations x-axis value.
     */
    public int getXOffset() {
        return xMod;
    }
    
    /**
     * 
     * @return A <code>String</code> containing the character this glyph represents.
     */
    public String getCharacter() {
        return new StringBuilder(text).toString();
    }
    
    /**
     * 
     * @return The Unicode code point for this character.
     */
    public int getCodePoint() {
        return codePoint;
    }
    
    /**
     * 
     * @return The left x UV coordinate of this character in the texture atlas.
     */
    public float getLeftU() {
        return (float)x / ttf.getAtlas().getImage().getWidth();
    }
    
    /**
     * 
     * @return The right x UV coordinate of this character in the texture atlas.
     */
    public float getRightU() {
        return (float)(x + w) / ttf.getAtlas().getImage().getWidth();
    }
    
    /**
     * 
     * @return The bottom y UV coordinate of this character in the texture atlas.
     */
    public float getBottomV() {
        return (float)y / ttf.getAtlas().getImage().getHeight();
    }
    
    /**
     * 
     * @return The top y UV coordinate of this character in the texture atlas.
     */
    public float getTopV() {
        return (float)(y + h) / ttf.getAtlas().getImage().getHeight();
    }
}
