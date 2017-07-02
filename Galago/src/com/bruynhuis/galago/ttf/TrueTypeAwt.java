/*
 * Feel free to use, modify, and/or distribute this source code for personal,
 * educational, commercial or any other reason you may conceive with or without
 * credit. There are absolutely no restrictions on the use, modification or
 * distribution of this code.
 */
package com.bruynhuis.galago.ttf;

import com.jme3.asset.AssetManager;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.util.BufferUtils;
import com.jme3.util.NativeObjectManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>The <code>TrueTypeFont</code> class encapsulates variables and methods used to
 * create and display text created from a True Type Font(.ttf) file.</p>
 * 
 * <p>###It is imperative that you take care to provide {@link truetypefont.TTF_AtlasListener}s.
 * When the atlas is recreated to store a new character the previous atlas' underlying
 * ByteBuffers are removed from memory immediately. If you do not wish to work with
 * {@link truetypefont.TTF_AtlasListener}s then after loading the font pre-load all the
 * characters you desire to use by supplying a String with said characters to
 * {@link #getBitmapText(java.lang.String, int)} then supplying true to
 * {@link #lockAtlas(boolean)} which will prevent the atlas from loading any new characters.###</p>
 * 
 * @author Adam T. Ryder
 * <a href="http://1337atr.weebly.com">http://1337atr.weebly.com</a>
 */
public class TrueTypeAwt extends TrueTypeFont {
    private final Font font;
    private final FontRenderContext frc;
    private final AffineTransform transform;
    
    public TrueTypeAwt(AssetManager assetManager, Font font, int pointSize,
            int outline, int screenDensity) {
        super(assetManager, pointSize, outline, screenDensity);
        if (font != null) {
            this.font = font; 
        } else {
            this.font = new Font(Font.SANS_SERIF, Font.PLAIN, pointSize);
        }
        
        double dpiScale =  dpi / 72f;
        transform = new AffineTransform();
        transform.setToScale(dpiScale, dpiScale);
        
        Graphics2D g = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB).createGraphics();
        g.setFont(this.font);
        frc = g.getFontRenderContext();
        
        g.dispose();
        
        FontMetrics fm = g.getFontMetrics(this.font);
        ascender = (int)Math.ceil(fm.getMaxAscent() * dpiScale) + outline;
        descender = (int)Math.ceil(fm.getMaxDescent() * dpiScale) + Math.round(outline / 2f);
        lineGap = (int)Math.ceil(fm.getLeading() * dpiScale);
        lineHeight = ascender + descender + lineGap;
        
        defaultCodePoint = '\u0000';
        
        charHeight = (int)Math.ceil(this.font.getMaxCharBounds(frc).getHeight() * dpiScale) + padding;
        resizeWidth = (int)Math.ceil(this.font.getMaxCharBounds(frc).getWidth() * dpiScale) + padding;
        
        getBitmapGlyphs(new StringBuilder().appendCodePoint(defaultCodePoint).append(" "));
    }
    
    @Override
    public boolean canDisplay(int codePoint) {
        return font.canDisplay(codePoint);
    }
    
    /**
     * 
     * @return The <code>java.awt.Font</code> used by this <code>TrueTypeFont</code>.
     */
    public Font getFont() {
        return font;
    }
    
    /**
     * 
     * @return The <code>java.awt.font.FontRenderContext</code> used to render glyphs.
     */
    public FontRenderContext getFontRenderContext() {
        return frc;
    }
    
    /**
     * For internal use only. Creates {@link TrueTypeBitmapGlyph}s that are not already
     * in the atlas and adds them to the texture atlas. If the atlas is modified all
     * attached {@link TTF_AtlasListener}s are called.
     * 
     * @param characters A <code>List</code> of {@link TrueTypeFont.CharToCreate}
     * containing the characters to be created and added to the texture atlas. This
     * list may contain doubles.
     * 
     * @see TrueTypeFont#getBitmapGlyphs(java.lang.StringBuilder) 
     * @see TTF_AtlasListener
     */
    @Override
    protected void createBitmapGlyphs(List<CharToCreate> characters) {
        if (atlas == null) {
            resizeAtlas();
        }
        
        boolean added = false;
        StringBuilder sb = new StringBuilder();
        Map<Integer, GlyphVector> backLog = new HashMap<Integer, GlyphVector>();
        do {
            int line = 0;
            for (AtlasLine al : atlasLines) {
                for (Iterator<CharToCreate> it = characters.iterator(); it.hasNext();) {
                    CharToCreate ctc = it.next();
                    if (cache.containsKey(ctc.codePoint)) {
                        it.remove();
                        continue;
                    }
                    
                    sb.delete(0, sb.length());
                    sb.appendCodePoint(ctc.codePoint);
                    GlyphVector gv = backLog.get(ctc.codePoint);
                    if (gv == null)
                        gv = font.createGlyphVector(frc, sb.toString());
                    gv.setGlyphTransform(0, transform);
                    Rectangle2D bounds = gv.getVisualBounds();
                    int w = (int)(Math.abs(bounds.getX()) + bounds.getWidth()) + padding;
                    if (al.canFit(w)) {
                        TrueTypeBitmapGlyph ttbg = new TrueTypeBitmapGlyph(al.getX(),
                                line * charHeight, w, gv, this, ctc.codePoint, sb);
                        cache.put(ctc.codePoint, ttbg);
                        
                        added = true;
                        al.addChar(w);
                        it.remove();
                    } else
                        backLog.put(ctc.codePoint, gv);
                }
                line++;
            }
            
            if (!characters.isEmpty()) {
                if (atlasWidth + resizeWidth > maxTexRes
                        && atlasHeight + charHeight > maxTexRes) {
                    for (Iterator<CharToCreate> it = characters.iterator(); it.hasNext();) {
                        it.next().codePoint = defaultCodePoint;
                        it.remove();
                    }
                    break;
                } else
                    resizeAtlas();
            }
        } while (!characters.isEmpty());
        
        if (atlasResized || added) {
            int oldWidth = (atlas != null) ? atlas.getImage().getWidth() : 0;
            int oldHeight = (atlas != null) ? atlas.getImage().getHeight() : 0;
            
            if (outline > 0) {
                createAtlasOutlined();
            } else
                createAtlas();
            
            for (TTF_AtlasListener listener : onAtlas) {
                listener.mod(assetManager, oldWidth, oldHeight, atlasWidth,
                        atlasHeight, this);
            }
        }
    }
    
    /**
     * For internal use only. This method is used to either create or re-create/re-size
     * the texture atlas.
     * 
     * @see TrueTypeFont#createBitmapGlyphs(java.util.List)
     */
    @Override
    protected void createAtlas() {
        BufferedImage tmpImg = new BufferedImage(atlasWidth, atlasHeight,
                BufferedImage.TYPE_INT_BGR);
        Graphics2D g = tmpImg.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, atlasWidth, atlasHeight);
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        g.setColor(Color.white);
        g.setFont(font);
        
        AffineTransform af = new AffineTransform();
        af.setToScale(transform.getScaleX(), -transform.getScaleY());
        for (TrueTypeBitmapGlyph glyph : cache.values()) {
            int x = (glyph.x + (padding / 2)) - glyph.xMod;
            int y = (glyph.y + (padding /2 )) + glyph.hMod;
            g.translate(x, y);
            
            GlyphVector gv = font.createGlyphVector(frc, glyph.getCharacter());
            gv.setGlyphTransform(0, af);
            Shape s = gv.getOutline();
            g.fill(s);
            
            g.translate(-x, -y);
        }
        if (atlas != null) {
            atlas.getImage().dispose();
            if (!NativeObjectManager.UNSAFE) {
                for (ByteBuffer buf : atlas.getImage().getData()) {
                    BufferUtils.destroyDirectBuffer(buf);
                }
            }
        }
        
        atlas = new Texture2D(new AWTLoader().load(tmpImg, false));
        g.dispose();
        
        atlasResized = false;
    }
    
    @Override
    protected void createAtlasOutlined() {
        BufferedImage tmpImg = new BufferedImage(atlasWidth, atlasHeight,
                BufferedImage.TYPE_INT_BGR);
        Graphics2D g = tmpImg.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, atlasWidth, atlasHeight);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        
        AffineTransform af = new AffineTransform();
        af.setToScale(transform.getScaleX(), -transform.getScaleY());
        Color fill = new Color(255, 0, 255);
        BasicStroke stroke = new BasicStroke(outline, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
        BasicStroke noStroke = new BasicStroke(0);
        for (TrueTypeBitmapGlyph glyph : cache.values()) {
            g.setStroke(stroke);
            int x = (glyph.x + (padding / 2)) - glyph.xMod;
            int y = (glyph.y + (padding / 2)) + glyph.hMod;
            g.translate(x, y);
            
            GlyphVector gv = font.createGlyphVector(frc, glyph.getCharacter());
            gv.setGlyphTransform(0, af);
            Shape s = gv.getOutline();
            g.setPaint(new Color(255, 0, 0));
            g.draw(s);
            g.setStroke(noStroke);
            g.setPaint(fill);
            g.fill(s);
            g.translate(-x, -y);
            
        }
        if (atlas != null) {
            atlas.getImage().dispose();
            if (!NativeObjectManager.UNSAFE) {
                for (ByteBuffer buf : atlas.getImage().getData()) {
                    BufferUtils.destroyDirectBuffer(buf);
                }
            }
        }
        
        atlas = new Texture2D(new AWTLoader().load(tmpImg, false));
        g.dispose();
        
        atlasResized = false;
    }
}
