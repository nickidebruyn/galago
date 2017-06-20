/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.util;

import com.jme3.math.ColorRGBA;

/**
 *
 * @author NideBruyn
 */
public class ColorUtils {

    public static ColorRGBA rgb(int r, int g, int b) {
        return new ColorRGBA((float) r / 255f, (float) g / 255f, (float) b / 255f, 1);
    }

    public static ColorRGBA rgb(int r, int g, int b, int a) {
        return new ColorRGBA((float) r / 255f, (float) g / 255f, (float) b / 255f, (float) a / 255f);
    }

    public static ColorRGBA hsv(float hue, float saturation, float value) {
        float r, g, b;

        int h = (int) (hue * 6);
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        if (h == 0) {
            r = value;
            g = t;
            b = p;
        } else if (h == 1) {
            r = q;
            g = value;
            b = p;
        } else if (h == 2) {
            r = p;
            g = value;
            b = t;
        } else if (h == 3) {
            r = p;
            g = q;
            b = value;
        } else if (h == 4) {
            r = t;
            g = p;
            b = value;
        } else if (h <= 6) {
            r = value;
            g = p;
            b = q;
        } else {
            throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }
        
//        Debug.log("r = " + r);
//        Debug.log("g = " + g);
//        Debug.log("b = " + b);

        return new ColorRGBA(r, g, b, 1);
    }
}
