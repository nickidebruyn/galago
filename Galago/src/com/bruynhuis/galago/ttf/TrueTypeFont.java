/*
 * Feel free to use, modify, and/or distribute this source code for personal,
 * educational, commercial or any other reason you may conceive with or without
 * credit. There are absolutely no restrictions on the use, modification or
 * distribution of this code.
 */
package com.bruynhuis.galago.ttf;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Geometry;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import com.jme3.util.NativeObjectManager;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.bruynhuis.galago.ttf.shapes.TrueTypeContainer;
import com.bruynhuis.galago.ttf.shapes.TrueTypeText;
import com.bruynhuis.galago.ttf.util.StringContainer;

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
public abstract class TrueTypeFont {
    public final int outline;
    public final int padding;
    public final int dpi;
    
    protected final AssetManager assetManager;
    
    protected final int spaceCodePoint;
    protected int defaultCodePoint;
    
    protected float scale = 1;
    
    //The height of the visual bounds of the largest character + padding in this
    //font. This is the height of one line of text in the font's texture atlas. All
    //characters in the atlas use this value as the height of their respective
    //bounding area in the texture atlas.
    protected int charHeight;
    
    protected final int pointSize;
    protected int lineHeight;
    protected int ascender;
    protected int descender;
    protected int lineGap = 0;
    protected int bold = 0;
    
    protected final List<AtlasLine> atlasLines = new ArrayList<AtlasLine>();
    protected Texture2D atlas;
    protected boolean atlasResized = false;
    protected int atlasWidth = 0;
    protected int atlasHeight = 0;
    protected int resizeWidth;
    protected boolean texLock = false;
    protected int maxTexRes = 2048;
    
    protected final Map<Integer, TrueTypeBitmapGlyph> cache = 
            new HashMap<Integer, TrueTypeBitmapGlyph>();
    protected final List<Integer> invalidCharacters = new ArrayList<Integer>();
    
    protected final List<TTF_AtlasListener> onAtlas = new LinkedList<TTF_AtlasListener>();
    
    public TrueTypeFont(AssetManager assetManager, int pointSize,
            int outline, int screenDensity) {
        spaceCodePoint = ' ';
        
        this.assetManager = assetManager;
        
        this.outline = outline;
        padding = 6 + (outline * 2);
        dpi = screenDensity;
        
        this.pointSize = pointSize;
    }
    
    /**
     * Locking the atlas will prevent any new characters from being added
     * to the atlas. Any requested characters not currently in the atlas
     * will be replaced by the default character.
     * 
     * @param lock True will lock the atlas, false will unlock it.
     * 
     * @see #setDefaultCharacter(java.lang.String) 
     */
    public void lockAtlas(boolean lock) {
        texLock = lock;
    }
    
    /**
     * Whether or not the atlas is currently locked.
     * 
     * @return True if the atlas is locked, false otherwise.
     * 
     * @see #lockAtlas(boolean) 
     */
    public boolean isLocked() {
        return texLock;
    }
    
    /**
     * Sets the maximum resolution of the texture atlas. Default
     * 2048px.
     * 
     * @param resolution The maximum resolution of the texture atlas.
     */
    public void setMaxAtlasResolution(int resolution) {
        maxTexRes = Math.max(resolution, 0);
    }
    
    /**
     * Retrieve the maximum resolution of the texture atlas.
     * 
     * @return The maximum resolution of the texture atlas.
     */
    public int getMaxAtlasResolution() {
        return maxTexRes;
    }
    
    public Texture2D getAtlas() {
        return atlas;
    }
    
    /**
     * Add a {@link TTF_AtlasListener} which will be called after the texture atlas
     * has been modified.
     * 
     * @param listener The {@link TTF_AtlasListener} you wish to add to the list
     * of listeners that will be called after the texture atlas has been resized.
     * 
     * @see TTF_AtlasListener
     */
    public void addAtlasListener(final TTF_AtlasListener listener) {
        onAtlas.add(listener);
    }
    
    /**
     * Removes a {@link TTF_AtlasListener} from this <code>TrueTypeFont</code>.
     * 
     * @param listener The <code>TTMF_AtlasListener</code> you wish to remove.
     * @return True if the listener was removed, false if the listener was not found.
     */
    public boolean removeAtlasListener(final TTF_AtlasListener listener) {
        return onAtlas.remove(listener);
    }
    
    public abstract boolean canDisplay(int codePoint);
    
    /**
     * Sets the default character to be displayed when a glyph is not available
     * in the font or the character is in the list of invalid characters set
     * with {@link TrueTypeFont#setInvalidCharacters(java.lang.String)}. 
     * Only the first character in the String will be used. If no default character
     * is set the true type font file's default character will be used.
     * 
     * @param text The character to use as a default character.
     * @return True if the character was set as default, false if the supplied
     * string was empty or the font did not contain the character.
     * 
     * @see TrueTypeFont#setInvalidCharacters(java.lang.String) 
     */
    public boolean setDefaultCharacter(String text) {
        if (text.isEmpty() || texLock)
            return false;
        
        StringBuilder sb = new StringBuilder(text);
        int codePoint = sb.codePointAt(0);
        if (canDisplay(codePoint)) {
            defaultCodePoint = codePoint;
            return true;
        }
        
        return false;
    }
    
    /**
     * Sets a list of characters to be invalidated. When a character from
     * the list of invalids is encountered the default character will be
     * used instead.
     * 
     * @param text A <code>String</code> containing all the characters to be
     * invalidated.
     * 
     * @see TrueTypeFont#setDefaultCharacter(java.lang.String) 
     */
    public void setInvalidCharacters(String text) {
        StringBuilder sb = new StringBuilder(text);
        for (int i = 0; i < sb.length(); i++) {
            int codePoint = sb.codePointAt(i);
            if (!invalidCharacters.contains(codePoint))
                invalidCharacters.add(codePoint);
        }
    }
    
    /**
     * Use this method to set a scale for text returned by the
     * <code>createText</code> and <code>getBitmapGeom</code> methods.
     * Bitmap texts tend not to look wonderful in smaller point sizes. To
     * remedy this use a larger point size, 26pt for example, and then use
     * this method to scale that back down to a smaller point size, such as
     * 14pt. For example <code>setScale(14f / 26f)</code>.
     * 
     * This will effect not only the scale of geometries returned by methods
     * of this <code>TrueTypeFont</code>, but also results returned by methods
     * such as <code>getLineWidth(String text)</code>.
     * 
     * @param scale The scale to modify geometries and calculations by.
     */
    public void setScale(float scale) {
        this.scale = scale;
    }
    
    /**
     * 
     * @return The scale used by this <code>TrueTypeFont</code> to modify
     * Geometry sizes and calculations.
     */
    public float getScale() {
        return scale;
    }
    
    /**
     * 
     * @return The size of the outline around bitmap text.
     */
    public int getOutline() {
        return outline;
    }
    
    /**
     * Retrieves a mesh ready to display the requested text. NO LINE BREAKS!!
     * 
     * @param text The text to display on the mesh.
     * @param kerning Additional space between characters in pixels.
     * @return <code>TrueTypeText</code> mesh.
     * 
     * @see TrueTypeText
     */
    public TrueTypeText getBitmapText(String text, int kerning) {
        return getBitmapText(getBitmapGlyphs(text), kerning);
    }
    
    /**
     * Retrieves a mesh ready to display the requested text. NO LINE BREAKS!!
     * 
     * @param text The text to display on the mesh.
     * @param kerning Additional space between characters in pixels.
     * @return <code>TrueTypeText</code> mesh.
     * 
     * @see TrueTypeText
     */
    public TrueTypeText getBitmapText(StringBuilder text, int kerning) {
        return getBitmapText(getBitmapGlyphs(text), kerning);
    }
    
    /**
     * Retrieves a mesh ready to display the requested text.
     * 
     * @param glyphs The text to display on the mesh.
     * @param kerning Additional space between characters in pixels.
     * @return <code>TrueTypeText</code> mesh.
     * 
     * @see TrueTypeText
     */
    public TrueTypeText getBitmapText(TrueTypeBitmapGlyph[] glyphs, int kerning) {
        return new TrueTypeText(glyphs, kerning);
    }
    
    /**
     * Retrieves a <code>Geometry</code> with material to display the requested
     * text. NO LINE BREAKS!!
     * 
     * @param text The text to display on the mesh.
     * @param kerning Additional space between characters in pixels.
     * @param color The desired color of the text as a <code>ColorRGBA</code> object.
     * @return A <code>Geometry</code> object with material.
     * 
     * @see TrueTypeText
     */
    public Geometry getBitmapGeom(String text, int kerning, ColorRGBA color) {
        return getBitmapGeom(getBitmapGlyphs(text), kerning, color, color);
    }
    
    /**
     * Retrieves a <code>Geometry</code> with material to display the requested
     * text. NO LINE BREAKS!!
     * 
     * @param text The text to display on the mesh.
     * @param kerning Additional space between characters in pixels.
     * @param color The desired color of the text as a <code>ColorRGBA</code> object.
     * @return A <code>Geometry</code> object with material.
     * 
     * @see TrueTypeText
     */
    public Geometry getBitmapGeom(StringBuilder text, int kerning, ColorRGBA color) {
        return getBitmapGeom(getBitmapGlyphs(text), kerning, color, color);
    }
    
    /**
     * Retrieves a <code>Geometry</code> with material to display the requested
     * text.
     * 
     * @param glyphs The text to display on the mesh.
     * @param kerning Additional space between characters in pixels.
     * @param color The desired color of the text as a <code>ColorRGBA</code> object.
     * @return A <code>Geometry</code> object with material.
     * 
     * @see TrueTypeBitmapGlyph
     */
    public Geometry getBitmapGeom(TrueTypeBitmapGlyph[] glyphs, int kerning, ColorRGBA color) {
        return getBitmapGeom(glyphs, kerning, color, color);
    }
    
    /**
     * Retrieves a <code>Geometry</code> with material to display the requested
     * text. NO LINE BREAKS!!
     * 
     * @param text The text to display on the mesh.
     * @param kerning Additional space between characters in pixels.
     * @param color The desired color of the text as a <code>ColorRGBA</code> object.
     * @param outlineColor The desired outline color of the text as a <code>ColorRGBA</code>
     * object. This only has an effect if an outline greater than zero was specified
     * when creating the <code>TrueTypeFont</code> object.
     * @return A <code>Geometry</code> object with material.
     * 
     * @see TrueTypeText
     */
    public Geometry getBitmapGeom(String text, int kerning, ColorRGBA color,
            ColorRGBA outlineColor) {
        return getBitmapGeom(getBitmapGlyphs(text), kerning, color, outlineColor);
    }
    
    /**
     * Retrieves a <code>Geometry</code> with material to display the requested
     * text. NO LINE BREAKS!!
     * 
     * @param text The text to display on the mesh.
     * @param kerning Additional space between characters in pixels.
     * @param color The desired color of the text as a <code>ColorRGBA</code> object.
     * @param outlineColor The desired outline color of the text as a <code>ColorRGBA</code>
     * object. This only has an effect if an outline greater than zero was specified
     * when creating the <code>TrueTypeFont</code> object.
     * @return A <code>Geometry</code> object with material.
     * 
     * @see TrueTypeText
     */
    public Geometry getBitmapGeom(StringBuilder text, int kerning, ColorRGBA color,
            ColorRGBA outlineColor) {
        return getBitmapGeom(getBitmapGlyphs(text), kerning, color, outlineColor);
    }
    
    /**
     * Retrieves a <code>Geometry</code> with material to display the requested
     * text.
     * 
     * @param glyphs The text to display on the mesh.
     * @param kerning Additional space between characters in pixels.
     * @param color The desired color of the text as a <code>ColorRGBA</code> object.
     * @param outlineColor The desired outline color of the text as a <code>ColorRGBA</code>
     * object. This only has an effect if an outline greater than zero was specified
     * when creating the <code>TrueTypeFont</code> object.
     * @return A <code>Geometry</code> object with material.
     * 
     * @see TrueTypeBitmapGlyph
     */
    public Geometry getBitmapGeom(TrueTypeBitmapGlyph[] glyphs, int kerning, ColorRGBA color,
            ColorRGBA outlineColor) {
        Geometry g = new Geometry("TrueTypeText", getBitmapText(glyphs, kerning));
        
        if (outline > 0) {
            Material mat = new Material(assetManager, "Common/MatDefs/TTF/TTF_BitmapOutlined.j3md");
            mat.setTexture("Texture", atlas);
            mat.setColor("Color", color);
            mat.setColor("Outline", outlineColor);
            g.setMaterial(mat);
            g.setLocalScale(scale, scale, 1);
        } else {
            Material mat = new Material(assetManager, "Common/MatDefs/TTF/TTF_Bitmap.j3md");
            mat.setTexture("Texture", atlas);
            mat.setColor("Color", color);
            g.setMaterial(mat);
            g.setLocalScale(scale, scale, 1);
        }
        
        return g;
    }
    
    /**
     * Creates a {@link TrueTypeContainer} to display formatted text. The
     * resulting text will be displayed with the supplied color and no
     * outline.
     * 
     * @param stringContainer An {@link StringContainer} containing the text
     * to be rendered along with associated formatting.
     * @param color The desired color of the text.
     * @return A {@link TrueTypeContainer} with the formatted text ready for
     * rendering. The <code>TrueTypeContainer</code> will be located at the
     * <code>StringContainer</code>s <code>textBox</code> x and y values.
     * 
     * @see TrueTypeContainer
     * @see StringContainer
     * @see #getFormattedText(truetypefont.util.StringContainer, com.jme3.math.ColorRGBA, com.jme3.math.ColorRGBA) 
     * @see #getFormattedText(truetypefont.util.StringContainer, com.jme3.material.Material)  
     */
    public TrueTypeContainer getFormattedText(StringContainer stringContainer,
            ColorRGBA color) {
        return getFormattedText(stringContainer, color, ColorRGBA.BlackNoAlpha);
    }
    
    /**
     * Creates a {@link TrueTypeContainer} to display formatted text. The
     * resulting text will be displayed with the supplied color and,
     * if this <code>TrueTypeFont</code> was created with an outline value
     * greater than 0, an outline of the supplied outlineColor.
     * 
     * @param stringContainer An {@link StringContainer} containing the text
     * to be rendered along with associated formatting.
     * @param color The desired color of the text.
     * @param outlineColor The desired color of the text's outline. This will
     * only be applied if this <code>TrueTypeFont</code> was created with
     * an outline width property.
     * @return A {@link TrueTypeContainer} with the formatted text ready for
     * rendering. The <code>TrueTypeContainer</code> will be located at the
     * <code>StringContainer</code>s <code>textBox</code> x and y values.
     * 
     * @see TrueTypeContainer
     * @see StringContainer
     * @see #getFormattedText(truetypefont.util.StringContainer, com.jme3.math.ColorRGBA) 
     * @see #getFormattedText(truetypefont.util.StringContainer, com.jme3.material.Material)  
     */
    public TrueTypeContainer getFormattedText(StringContainer stringContainer,
            ColorRGBA color, ColorRGBA outlineColor) {
        TrueTypeContainer ttc = new TrueTypeContainer(stringContainer);
        
        Material mat;
        if (outline > 0) {
            mat = new Material(assetManager, "Common/MatDefs/TTF/TTF_BitmapOutlined.j3md");
            mat.setTexture("Texture", atlas);
            mat.setColor("Color", color);
            mat.setColor("Outline", outlineColor);
        } else {
            mat = new Material(assetManager, "Common/MatDefs/TTF/TTF_Bitmap.j3md");
            mat.setTexture("Texture", atlas);
            mat.setColor("Color", color);
        }
        
        ttc.setMaterial(mat);
        ttc.setLocalTranslation(stringContainer.getTextBox().x,
                stringContainer.getTextBox().y, 0);
        
        return ttc;
    }
    
    /**
     * Creates a {@link TrueTypeContainer} to display formatted text.
     * 
     * @param stringContainer An {@link StringContainer} containing the text
     * to be rendered along with associated formatting.
     * @param material A <code>Material</code> to render the text with.
     * @return A {@link TrueTypeContainer} with the formatted text ready for
     * rendering. The <code>TrueTypeContainer</code> will be located at the
     * <code>StringContainer</code>s <code>textBox</code> x and y values.
     * 
     * @see TrueTypeContainer
     * @see StringContainer
     * @see #getFormattedText(truetypefont.util.StringContainer, com.jme3.math.ColorRGBA) 
     * @see #getFormattedText(truetypefont.util.StringContainer, com.jme3.math.ColorRGBA, com.jme3.math.ColorRGBA) 
     */
    public TrueTypeContainer getFormattedText(StringContainer stringContainer,
            Material material) {
        TrueTypeContainer ttc = new TrueTypeContainer(stringContainer);
        ttc.setMaterial(material);
        ttc.setLocalTranslation(stringContainer.getTextBox().x,
                stringContainer.getTextBox().y, 0);
        
        return ttc;
    }
    
    /**
     * Gets an array of {@link TrueTypeBitmapGlyph}s representing the characters in
     * the supplied <code>String</code>. Characters not already in the texture atlas
     * will be added. The <code>TrueTypeBitmapGlyph</code>s. NO LINE BREAKS!!
     * 
     * @param text A <code>String</code> containing the characters you wish to retrieve.
     * @return An array of <code>TrueTypeBitmapGlyph</code>s representing the characters
     * in the supplied <code>String</code>
     * 
     * @see TrueTypeBitmapGlyph
     * @see TrueTypeFont#getBitmapGlyphs(java.lang.StringBuilder) 
     */
    public TrueTypeBitmapGlyph[] getBitmapGlyphs(String text) {
        StringBuilder sb = new StringBuilder(text);
        return getBitmapGlyphs(sb);
    }
    
    /**
     * Gets an array of {@link TrueTypeBitmapGlyph}s representing the characters in
     * the supplied <code>String</code>. Characters not already in the texture atlas
     * will be added. The <code>TrueTypeBitmapGlyph</code>s. NO LINE BREAKS!!
     * 
     * @param sb A <code>StringBuilder</code> containing the characters you wish to
     * retrieve.
     * @return An array of <code>TrueTypeBitmapGlyph</code>s representing the characters
     * in the supplied <code>String</code>
     * 
     * @see TrueTypeBitmapGlyph
     * @see TrueTypeFont#getBitmapGlyphs(java.lang.String) 
     */
    public TrueTypeBitmapGlyph[] getBitmapGlyphs(StringBuilder sb) {
        TrueTypeBitmapGlyph[] glyphs = new TrueTypeBitmapGlyph[sb.length()];
        LinkedList<CharToCreate> unCached = new LinkedList<CharToCreate>();
        
        for (int i = 0; i < sb.length(); i++) {
            int codePoint = sb.codePointAt(i);
            if (!canDisplay(codePoint) || invalidCharacters.contains(codePoint)) {
                codePoint = defaultCodePoint;
            }
            glyphs[i] = cache.get(codePoint);
            if (glyphs[i] == null) {
                if (texLock) {
                    glyphs[i] = cache.get(defaultCodePoint);
                } else
                    unCached.add(new CharToCreate(i, codePoint));
            }
        }
        
        if (!unCached.isEmpty()) {
            createBitmapGlyphs((LinkedList<CharToCreate>)unCached.clone());
            for (CharToCreate ctc : unCached) {
                glyphs[ctc.index] = cache.get(ctc.codePoint);
            }
        }
        
        return glyphs;
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
    protected abstract void createBitmapGlyphs(List<CharToCreate> characters);
    
    /**
     * For internal use only. Doesn't actually resize the texture atlas itself.
     * This method is used to recalculate what the texture atlas' size should be
     * when it is eventually resized.
     * 
     * @see TrueTypeFont#createBitmapGlyphs(java.util.List) 
     */
    protected void resizeAtlas() {
        atlasWidth += (atlasWidth + resizeWidth > maxTexRes) ? 0 : resizeWidth;
        atlasHeight += (atlasHeight + charHeight > maxTexRes) ? 0 : charHeight;
        
        int numNewLines = (int)FastMath.floor((float)atlasHeight / charHeight) - atlasLines.size();
        for (int i = 0; i < numNewLines; i++) {
            atlasLines.add(new AtlasLine());
        }
        
        atlasResized = true;
    }
    
    /**
     * For internal use only. This method is used to either create or re-create/re-size
     * the texture atlas.
     * 
     * @see TrueTypeFont#createBitmapGlyphs(java.util.List)
     */
    protected abstract void createAtlas();
    
    protected abstract void createAtlasOutlined();
    
    /**
     * Get the width of one line of text in pixels. Characters supplied to
     * this method will be added to the texture atlas if not already present.
     * Use this method to obtain the line width of text created with one of the
     * bitmap text methods. NO LINE BREAKS!!
     * 
     * @param text A <code>String</code> containing the text to calculate.
     * @param kerning Additional spacing between characters in pixels.
     * @return The width of the supplied <code>String</code> in pixels.
     * 
     * @see TrueTypeFont#getBitmapGeom(java.lang.String, int kerning, com.jme3.math.ColorRGBA)
     * @see TrueTypeFont#getLineWidth(java.lang.StringBuilder, int kerning) 
     * @see TrueTypeFont#getLineWidth(TrueTypeBitmapGlyph[], int kerning) 
     */
    public float getLineWidth(String text, int kerning) {
        return getLineWidth(getBitmapGlyphs(text), kerning);
    }
    
    /**
     * Get the width of one line of text in pixels. Characters supplied to
     * this method will be added to the texture atlas if not already present.
     * Use this method to obtain the line width of text created with one of the
     * bitmap text methods. NO LINE BREAKS!!
     * 
     * @param text A <code>StringBuilder</code> containing the text to calculate.
     * @param kerning Additional spacing between characters in pixels.
     * @return The width of the supplied <code>StringBuilder</code>s in pixels.
     * 
     * @see TrueTypeFont#getBitmapGeom(java.lang.String, int kerning, com.jme3.math.ColorRGBA) 
     * @see TrueTypeFont#getLineWidth(java.lang.String, int kerning) 
     * @see TrueTypeFont#getLineWidth(TrueTypeBitmapGlyph[], int kerning) 
     */
    public float getLineWidth(StringBuilder text, int kerning) {
        return getLineWidth(getBitmapGlyphs(text), kerning);
    }
    
    /**
     * Get the width of one line of text in pixels.Use this method to obtain
     * the line width of text created with one of the bitmap text methods.
     * 
     * @param glyphs An array of <code>TrueTypeBitmapGlyph</code>s.
     * @param kerning Additional spacing between characters in pixels.
     * @return The width of the supplied <code>TrueTypeBitmapGlyph</code>s in pixels.
     * 
     * @see TrueTypeFont#getBitmapGeom(java.lang.String, int kerning, com.jme3.math.ColorRGBA) 
     * @see TrueTypeFont#getLineWidth(java.lang.String, int kerning) 
     * @see TrueTypeFont#getLineWidth(java.lang.StringBuilder, int kerning) 
     */
    public float getLineWidth(TrueTypeBitmapGlyph[] glyphs, int kerning) {
        int lineWidth = 0;
        for (int i = 0; i < glyphs.length; i++) {
            lineWidth += glyphs[i].getXAdvance() + (i < glyphs.length ? kerning : 0);
        }
        
        return lineWidth * scale;
    }
    
    /**
     * This is the actual line height from baseline to baseline not modified by
     * {@link TrueTypeFont#setScale(float)}.
     * 
     * @return The line height, in pixels, from baseline to baseline.
     */
    public int getActualLineHeight() {
        return lineHeight;
    }
    
    /**
     * The line height from baseline to baseline scaled by
     * {@link TrueTypeFont#setScale(float)}.
     * 
     * @return The line height,from baseline to baseline.
     */
    public float getScaledLineHeight() {
        return scale * lineHeight;
    }
    
    /**
     * The line height from baseline to baseline scaled by
     * {@link TrueTypeFont#setScale(float)}.
     * 
     * @return The line height, in pixels, from baseline to baseline.
     */
    public int getScaledLineHeightInt() {
        return Math.round(scale * lineHeight);
    }
    
    public int getActualLineGap() {
        return lineGap;
    }
    
    /**
     * Gets the additional spacing between lines.
     * 
     * @return 
     */
    public float getScaledLineGap() {
        return lineGap * scale;
    }
    
    /**
     * Gets the additional spacing between lines rounded to the
     * nearest integer.
     * 
     * @return 
     */
    public int getScaledLineGapInt() {
        return Math.round(scale * lineGap);
    }
    
    public float getScaledPointSize() {
        return pointSize * scale;
    }

    public int getPointSize() {
        return pointSize;
    }
    
    public int getScaledPointSizeInt() {
        return Math.round(scale * pointSize);
    }
    
    /**
     * 
     * @return The number of pixels above the baseline that a character
     * can extend. Note some characters may extend greater than this amount.
     */
    public int getActualAscender() {
        return ascender;
    }
    
    /**
     * 
     * @return The number of pixels above the baseline that a character
     * can extend scaled by the <code>TrueTypeFont</code>s scale value.
     * Note some characters may extend greater than this amount.
     */
    public float getScaledAscender() {
        return scale * ascender;
    }
    
    /**
     * 
     * @return The number of pixels below the baseline that a character
     * can extend. Note some characters may extend greater than this amount.
     */
    public int getActualDescender() {
        return descender;
    }
    
    /**
     * 
     * @return The number of pixels below the baseline that a character
     * can extend scaled by the <code>TrueTypeFont</code>s scale value.
     * Note some characters may extend greater than this amount.
     */
    public float getScaledDescender() {
        return scale * descender;
    }
    
    /**
     * Get the height of the text from the bottom of the character that extends the
     * deepest below the baseline to the top of the character that extends the highest
     * above the baseline. NO LINE BREAKS!!
     * 
     * @param text The text to calculate the hight of.
     * @return The height.
     */
    public float getVisualLineHeight(String text) {
        return getVisualLineHeight(getBitmapGlyphs(text));
    }
    
    /**
     * Get the height of the text from the bottom of the character that extends the
     * deepest below the baseline to the top of the character that extends the highest
     * above the baseline. NO LINE BREAKS!!
     * 
     * @param text The text to calculate the hight of.
     * @return The height.
     */
    public float getVisualLineHeight(StringBuilder text) {
        return getVisualLineHeight(getBitmapGlyphs(text));
    }
    
    /**
     * Get the height of the text from the bottom of the character that extends the
     * deepest below the baseline to the top of the character that extends the highest
     * above the baseline.
     * 
     * @param glyphs The text to calculate the hight of.
     * @return The height.
     */
    public float getVisualLineHeight(TrueTypeBitmapGlyph[] glyphs) {
        int maxY = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        for (TrueTypeBitmapGlyph glyph : glyphs) {
            maxY = (glyph.visualHeight > maxY) ?
                    (int)glyph.visualHeight : maxY;
            minY = (glyph.visualHeight < minY) ?
                    (int)glyph.visualHeight : minY;
        }
        
        return (maxY - minY) * scale;
    }
    
    /**
     * A helper class used when adding new characters to the texture atlas and cache.
     * 
     * @see TrueTypeFont#createBitmapGlyphs(java.util.List) 
     * 
     * @author Adam T. Ryder http://1337atr.weebly.com
     */
    protected class CharToCreate {
        public final int index;
        public int codePoint;
        
        protected CharToCreate(int index, int codePoint) {
            this.index = index;
            this.codePoint = codePoint;
        }
    }
    
    /**
     * A helper class used in determining a new characters position in the texture
     * atlas and if said character can fit on a particular line in that atlas.
     * 
     * @see TrueTypeFont#createBitmapGlyphs(java.util.List) 
     * @see TrueTypeFont#resizeAtlas() 
     * 
     * @author Adam T. Ryder http://1337atr.weebly.com
     */
    protected class AtlasLine {
        private int currentX = 0;
        
        public boolean canFit(int cWidth) {
            return (atlasWidth - currentX) - cWidth >= 0;
        }
        
        public void addChar(int cWidth) {
            currentX += cWidth;
        }
        
        public int getX() {
            return currentX;
        }
    }
    
    /**
     * Recreates the texture atlas.
     */
    public void reloadTexture() {
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
    
    @Override
    public void finalize() throws Throwable {
        if (atlas != null) {
            atlas.getImage().dispose();
            if (!NativeObjectManager.UNSAFE) {
                for (ByteBuffer buf : atlas.getImage().getData()) {
                    BufferUtils.destroyDirectBuffer(buf);
                }
            }
        }
        
        super.finalize();
    }
}
