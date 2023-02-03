package com.galago.editor.themes;

import com.jme3.math.ColorRGBA;

/**
 *
 * @author ndebruyn
 */
public abstract class EditorTheme {
    
    public abstract ColorRGBA getIconColor();
    
    public abstract ColorRGBA getBackgroundColor();
    
    public abstract ColorRGBA getGridColor();

    public abstract ColorRGBA getPanelColor();

    public abstract ColorRGBA getSelectionColor();
    
    public abstract ColorRGBA getSelectionInvertColor();
    
    public abstract ColorRGBA getTooltipColor();
    
    public abstract ColorRGBA getTooltipTextColor();
    
    public abstract ColorRGBA getXAxisColor();
    
    public abstract ColorRGBA getYAxisColor();
    
    public abstract ColorRGBA getZAxisColor();

    public abstract ColorRGBA getHeaderTextColor();

    public abstract ColorRGBA getHeaderColor();
    
    public abstract ColorRGBA getButtonColor();
    
    public abstract ColorRGBA getButtonTextColor();
    
    public abstract ColorRGBA getFieldColor();
    
    public abstract ColorRGBA getFieldTextColor();    

    public abstract ColorRGBA getOutlinerColor();
    
}
