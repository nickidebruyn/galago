/*
 * Feel free to use, modify, and/or distribute this source code for personal,
 * educational, commercial or any other reason you may conceive with or
 * without credit. There are absolutely no restrictions on the use,
 * modification or distribution of this code.
 */
package com.bruynhuis.galago.ttf.util;

import android.graphics.Path;

/**
 * An immutable object that represents a Glyph to be rendered on Android devices.
 * 
 * @author Adam T. Ryder
 * <a href="http://1337atr.weebly.com">http://1337atr.weebly.com</a>
 */
public class Glyf {
    public final Path contours;
    public final float minX;
    public final float maxX;
    public final float minY;
    public final float maxY;

    public Glyf(Path contours, float maxX, float minX, float maxY, float minY) {
        this.contours = contours;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    public float getWidth() {
        return maxX - minX;
    }

    public float getHeight() {
        return maxY - minY;
    }
}
