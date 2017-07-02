/*
 * Feel free to use, modify, and/or distribute this source code for personal,
 * educational, commercial or any other reason you may conceive with or without
 * credit. There are absolutely no restrictions on the use, modification or
 * distribution of this code.
 */
package com.bruynhuis.galago.ttf;

import com.bruynhuis.galago.ttf.util.Style;
import com.jme3.asset.AssetKey;
import com.jme3.asset.cache.AssetCache;
import com.jme3.asset.cache.SimpleAssetCache;
import com.jme3.asset.cache.WeakRefAssetCache;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import java.io.IOException;

/**
 * A key used to lookup a {@link TrueTypeFont} resource from cache.
 * 
 * @author Adam T. Ryder
 * <a href="http://1337atr.weebly.com">http://1337atr.weebly.com</a>
 */
public class TrueTypeKey extends AssetKey {
    private Style style;
    private int pointSize;
    
    private boolean weakCache = false;
    private int outline;
    
    private int dpi;
    
    /**
     * Instantiates a new <code>TrueTypeKey</code> using the default style of
     * <code>Style.Plain</code>, no outline and strong cache.
     * 
     * @param name Path to the desired font file.
     */
    public TrueTypeKey(String name) {
        this(name, Style.Plain);
    }
    
    /**
     * Instantiates a new <code>TrueTypeKey</code> using the default point size,
     * no outline and strong cache.
     * 
     * @param name Path to the desired font file.
     * @param style The style of the font. Should be one of {@link Style}
     */
    public TrueTypeKey(String name, Style style) {
        this(name, style, 12);
    }
    
    /**
     * Instantiates a new <code>TrueTypeKey</code> using no outline and strong cache.
     * 
     * @param name Path to the desired font file.
     * @param style The style of the font. Should be one of {@link Style}
     * @param pointSize The point size of the associated font.
     */
    public TrueTypeKey(String name, Style style,
            int pointSize) {
        this(name, style, pointSize, 0);
    }
    
    /**
     * Instantiates a new <code>TrueTypeKey</code> using strong cache.
     * 
     * @param name Path to the desired font file.
     * @param style The style of the font. Should be one of {@link Style}
     * @param pointSize The point size of the associated font.
     * @param outline The size of the outline around bitmap text.
     */
    public TrueTypeKey(String name, Style style,
            int pointSize, int outline) {
        this(name, style, pointSize, outline, 72);
    }
    
    /**
     * Instantiates a new <code>TrueTypeKey</code> using strong cache.
     * 
     * @param name Path to the desired font file.
     * @param style The style of the font. Should be one of {@link Style}
     * @param pointSize The point size of the associated font.
     * @param outline The size of the outline around bitmap text.
     * @param screenDensity The density of the screen in dots per inch.
     */
    public TrueTypeKey(String name, Style style,
            int pointSize, int outline, int screenDensity) {
        this(name, style, pointSize, outline, screenDensity, false);
    }
    
    /**
     * Instantiates a new <code>TrueTypeKey</code>.
     * 
     * @param name Path to the desired font file.
     * @param style The style of the font. Should be one of {@link Style}
     * @param pointSize The point size of the associated font.
     * @param outline The size of the outline around bitmap text.
     * @param screenDensity The density of the screen in dots per inch.
     * @param weakCache If true the resulting <code>TrueTypeFont</code> will be stored
     * in a cache using weak references, the font may be reclaimed by the garbage
     * collector if there are no other references to it and memory is low.
     */
    public TrueTypeKey(String name, Style style, int pointSize, int outline, int screenDensity,
            boolean weakCache) {
        super(name);
        this.style = style;
        this.weakCache = weakCache;
        this.pointSize = pointSize;
        this.outline = outline;
        this.dpi = screenDensity;
    }
    
    @Override
    public Class<? extends AssetCache> getCacheType(){
        return (!weakCache) ? SimpleAssetCache.class : WeakRefAssetCache.class;
    }
    
    /**
     * 
     * @return The size of the outline around bitmap text.
     */
    public int getOutline() {
        return outline;
    }
    
    /**
     * 
     * @return The style used by the font associated with this key.
     * 
     * @see Style
     */
    public Style getStyle() {
        return style;
    }
    
    /**
     * 
     * @return The point size used by the font associated with this key.
     */
    public int getPointSize() {
        return pointSize;
    }
    
    /**
     * The jMonkeyEngine cache system can be set to use weak or strong references for
     * caching. If this is set to true this font will be stored as a weak reference
     * meaning it may be cleaned up by the garbage collector if there are no other
     * strong references to the font and memory is running low.
     * 
     * @return True if using weak references, false for strong references.
     */
    public boolean isWeakCache() {
        return weakCache;
    }
    
    /**
     * Gets the screen density setting for the font loaded by this key.
     * 
     * @return 
     */
    public int getScreenDensity() {
        return dpi;
    }
    
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TrueTypeKey))
            return false;
        
        TrueTypeKey key = (TrueTypeKey)other;
        return name.equals(key.getName())
                && style == key.getStyle()
                && pointSize == key.getPointSize()
                && outline == key.getOutline()
                && weakCache == key.isWeakCache()
                && dpi == key.getScreenDensity();
    }
    
    @Override
    public int hashCode() {
        return (this.toString()).hashCode();
    }
    
    @Override
    public String toString() {
        return name + "_Style:" + style.toString() +
                "_PointSize:" + Integer.toString(pointSize) +
                "_Outline:" + Integer.toString(outline) +
                "_ScreenDensity:" + Integer.toString(dpi) +
                "_Cache:" + (weakCache ? "Weak" : "Strong");
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(name, "name", null);
        oc.write(style.toString(), "style", "Plain");
        oc.write(pointSize, "pointSize", 5);
        oc.write(outline, "outline", 0);
        oc.write(dpi, "density", 72);
        oc.write(weakCache, "weakCache", false);
    }
    
    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        name = reducePath(ic.readString("name", null));
        extension = getExtension(name);
        String styl = ic.readString("style", "Plain");
        style = Style.Plain;
//        switch (styl) {
//            case "Plain":
//                style = Style.Plain;
//                break;
//            case "Bold":
//                style = Style.Bold;
//                break;
//            case "Italic":
//                style = Style.Italic;
//                break;
//            case "BoldItalic":
//                style = Style.BoldItalic;
//                break;
//            default:
//                style = Style.Plain;
//        }
        pointSize = ic.readInt("pointSize", 5);
        outline = ic.readInt("outline", 0);
        dpi = ic. readInt("density", 72);
        weakCache = ic.readBoolean("weakCache", false);
    }
}
