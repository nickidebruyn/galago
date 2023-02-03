package com.galago.editor.themes;

import com.bruynhuis.galago.util.ColorUtils;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author ndebruyn
 */
public class DefaultTheme extends EditorTheme {
    
    @Override
    public ColorRGBA getIconColor() {
        return ColorUtils.rgb(255, 255, 255);
    }

    @Override
    public ColorRGBA getBackgroundColor() {
        return ColorUtils.rgb(56,56,56);
    }

    @Override
    public ColorRGBA getGridColor() {
        return ColorUtils.rgb(80,80,80);
    }    

    @Override
    public ColorRGBA getPanelColor() {
        return ColorUtils.rgb(51,49,68);
    }

    @Override
    public ColorRGBA getSelectionColor() {
        return ColorUtils.rgb(244,234,87);
    }

    @Override
    public ColorRGBA getSelectionInvertColor() {
        return ColorUtils.rgb(51,49,68);
    }

    @Override
    public ColorRGBA getTooltipColor() {
        return ColorUtils.rgb(254,76,76);
    }
    
    @Override
    public ColorRGBA getTooltipTextColor() {
        return ColorUtils.rgb(254,254,254);
    }

    @Override
    public ColorRGBA getXAxisColor() {
        return ColorRGBA.Red;
    }

    @Override
    public ColorRGBA getYAxisColor() {
        return ColorRGBA.Green;
    }

    @Override
    public ColorRGBA getZAxisColor() {
        return ColorRGBA.Blue;
    }

    @Override
    public ColorRGBA getHeaderTextColor() {
        return ColorUtils.rgb(255, 255, 255);
    }

    @Override
    public ColorRGBA getHeaderColor() {
        return ColorUtils.rgb(61,59,78);
    }

    @Override
    public ColorRGBA getButtonColor() {
        return ColorUtils.rgb(81,78,109);
    }

    @Override
    public ColorRGBA getButtonTextColor() {
        return ColorUtils.rgb(255,255,255);
    }

    @Override
    public ColorRGBA getFieldColor() {
        return ColorUtils.rgb(95,98,100);
    }

    @Override
    public ColorRGBA getFieldTextColor() {
        return ColorUtils.rgb(255,255,255);
    }

    @Override
    public ColorRGBA getOutlinerColor() {
        return ColorUtils.rgb(255,221,70);
    }
    
    
}
