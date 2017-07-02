/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui;

import com.bruynhuis.galago.resource.FontManager;

/**
 * This class defines a certain type of font.
 * You can use this style on Labels, TouchButtons and TextAreas
 * Preload the font when the BaseApplication starts up in the initFont() method
 * @author NideBruyn
 */
public class FontStyle {
    
    private String fontFile;
    private int fontSize;
    private int outlineSize;
    private boolean italic;

    public FontStyle(String fontFile, int fontSize) {
        this(fontFile, fontSize, 0, false);
    }

    public FontStyle(int fontSize) {
        this(FontManager.DEFAULT_FONT, fontSize, 0, false);
    } 

    public FontStyle(int fontSize, int outlineSize) {
        this(FontManager.DEFAULT_FONT, fontSize, outlineSize, false);
    }

    public FontStyle(int fontSize, int outlineSize, boolean italic) {
        this(FontManager.DEFAULT_FONT, fontSize, outlineSize, italic);
    }
    
    public FontStyle(String fontFile, int fontSize, int outlineSize, boolean italic) {
        this.fontFile = fontFile;
        this.fontSize = fontSize;
        this.outlineSize = outlineSize;
        this.italic = italic;
    }

    public String getFontFile() {
        return fontFile;
    }

    public int getFontSize() {
        return fontSize;
    }

    public int getOutlineSize() {
        return outlineSize;
    }

    public boolean isItalic() {
        return italic;
    }

    public String getUniqueKey() {
        return getFontFile()+"-"+getFontSize()+"-"+getOutlineSize() + "-" + italic;
    }
    
}
