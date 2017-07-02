/*
 * Feel free to use, modify, and/or distribute this source code for personal,
 * educational, commercial or any other reason you may conceive with or
 * without credit. There are absolutely no restrictions on the use,
 * modification or distribution of this code.
 */
package com.bruynhuis.galago.ttf.util;

/**
 *
 * @author Adam T. Ryder
 * <a href="http://1337atr.weebly.com">http://1337atr.weebly.com</a>
 */
public enum Style {
    Plain,
    Bold,
    Italic,
    BoldItalic;
    
    @Override
    public String toString() {
        switch(this) {
            case Plain:
                return "Plain";
            case Bold:
                return "Bold";
            case Italic:
                return "Italic";
            default:
                return "BoldItalic";
        }
    }
}
