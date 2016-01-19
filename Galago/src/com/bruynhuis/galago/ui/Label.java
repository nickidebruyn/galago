/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui;

import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;

/**
 *
 * A normal text label on the screen.
 * 
 * @author nidebruyn
 */
public class Label extends Widget {
    
    protected Panel panel;
    protected BitmapText bitmapText;
    protected float fontSize = 26;
    
    /**
     * 
     * @param panel
     * @param text 
     */
    public Label(Panel panel, String text) {
        this(panel, text, 26, 300, 40, panel.getWindow().getBitmapFont());
    }
    
    /**
     * 
     * @param panel
     * @param text
     * @param fontSize 
     */
    public Label(Panel panel, String text, float fontSize) {
        this(panel, text, fontSize, 300, 40, panel.getWindow().getBitmapFont());
        
    }
    
    public Label(Panel panel, String text, float fontSize, float width, float height) {
        this(panel, text, fontSize, width, height, panel.getWindow().getBitmapFont());
        
    }
    
    /**
     * 
     * @param panel
     * @param text
     * @param fontSize
     * @param width
     * @param height 
     */
    public Label(Panel panel, String text, float fontSize, float width, float height, BitmapFont font) {
        super(panel.getWindow(), panel, width, height, false);
        this.panel = panel;
        this.fontSize = fontSize;        
        
        //This fixes the out of memory opengl error we get in android.
        if (text == null || text.length() == 0) text = " ";
                
        //Init the text
        bitmapText = font.createLabel(text);
//        bitmapText.setAlpha(1f);
        bitmapText.setText(text);             // the text
        bitmapText.setBox(new Rectangle(-getWidth()*0.5f, getHeight()*0.5f, getWidth(), getHeight()*0.5f));
        bitmapText.setSize(fontSize*window.getScaleFactorHeight());      // font size
        bitmapText.setColor(ColorRGBA.White);// font color
        bitmapText.setAlignment(BitmapFont.Align.Center);
        bitmapText.setVerticalAlignment(BitmapFont.VAlign.Center);
        widgetNode.attachChild(bitmapText);
        
        panel.add(this);
        
//        bitmapText.setLocalTranslation(bitmapText.getLocalTranslation().x, bitmapText.getLocalTranslation().y, 0.001f);
    }
    
    public void setWrapMode(LineWrapMode lineWrapMode) {
        this.bitmapText.setLineWrapMode(lineWrapMode);
    }
    
    /**
     * 
     * @param align 
     */
    public void setAlignment(BitmapFont.Align align) {
        bitmapText.setAlignment(align);
    }
    
    /**
     * 
     * @param align 
     */
    public void setVerticalAlignment(BitmapFont.VAlign align) {
        bitmapText.setVerticalAlignment(align);
    }
    
    /**
     * 
     * @param text 
     */
    //This fixes the out of memory opengl error we get in android.
    public void setText(String text) {
        if (text == null || text.length() == 0) text = " ";
        this.bitmapText.setText(text);
    }
        
    public String getText() {
        return this.bitmapText.getText();
    }
    
    /**
     * 
     * @param colorRGBA 
     */
    public void setTextColor(ColorRGBA colorRGBA) {
        this.bitmapText.setColor(colorRGBA);
    }
    
    /**
     * 
     * @param size 
     */
    public void setFontSize(float size) {
        bitmapText.setSize(size*window.getScaleFactorHeight());      // font size
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (visible && widgetNode.getCullHint().equals(Spatial.CullHint.Always)) {
            bitmapText.setCullHint(Spatial.CullHint.Never);
        } else if (!visible && widgetNode.getCullHint().equals(Spatial.CullHint.Never)) {
            bitmapText.setCullHint(Spatial.CullHint.Always);
        }
    }

    @Override
    public void setTransparency(float alpha) {
        bitmapText.setAlpha(alpha);
    }

    @Override
    protected boolean isBatched() {
        return false;
    }
}
