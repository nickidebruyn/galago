/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.field;

import com.bruynhuis.galago.ui.ImageWidget;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;

/**
 * A text area where you can type some text.
 * 
 * @author nidebruyn
 */
public class TextArea extends ImageWidget {

    protected Panel panel;
    protected String id;
    protected ActionListener actionListener;
    private boolean enabled = true;
    private boolean focus = false;
    protected BitmapText bitmapText;
    protected float fontSize = 24;
    protected int maxLength = 255;
    protected int maxLines = 5;

    /**
     * 
     * @param panel
     * @param id
     * @param pictureFile 
     */
    public TextArea(Panel panel, String id, String pictureFile) {
        this(panel, id, pictureFile, 500, 230, 5);
    }
    
    /**
     * 
     * @param panel
     * @param width
     * @param height 
     */
    public TextArea(Panel panel, float width, float height) {
        this(panel, "some_area", null, width, height, 5);
        
    }

    /**
     * 
     * @param panel
     * @param id
     * @param pictureFile
     * @param width
     * @param height 
     */
    public TextArea(Panel panel, String id, String pictureFile, float width, float height) {
        this(panel, id, pictureFile, width, height, 5);
    }
    
    /**
     * 
     * @param panel
     * @param id
     * @param pictureFile
     * @param width
     * @param height
     * @param padding 
     */
    public TextArea(Panel panel, String id, String pictureFile, float width, float height, float padding) {
        super(panel.getWindow(), panel, pictureFile, width, height, false);
        this.id = id;

        padding = window.getScaleFactorWidth()*padding;
        //Init the text
        bitmapText = window.getBitmapFont().createLabel(id);
//        bitmapText.setAlpha(1f);
        bitmapText.setBox(new Rectangle((-getWidth()*0.5f)+padding, getHeight()*0.5f, getWidth()-padding, getHeight()*0.5f));
        bitmapText.setSize(fontSize * window.getScaleFactorHeight());      // font size
        bitmapText.setColor(ColorRGBA.White);// font color
        bitmapText.setText("TextArea");             // the text
        bitmapText.setAlignment(BitmapFont.Align.Left);
        bitmapText.setVerticalAlignment(BitmapFont.VAlign.Top);
//        bitmapText.setLineWrapMode(LineWrapMode.NoWrap);
        widgetNode.attachChild(bitmapText);


        panel.add(this);
    }
    
    @Override
    protected boolean isBatched() {
        return false;
    }
    
    public void clear() {
        setText("");
        updateScrolling();
    }

    /**
     * 
     * @param maxLength 
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getMaxLines() {
        return maxLines;
    }

    /**
     * 
     * @param maxLines 
     */
    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    /**
     * 
     * @param text 
     */
    public void setText(String text) {
        if (text == null || text.length() == 0) text = " ";
        bitmapText.setText(text);
    }
    
    public String getText() {
        return bitmapText.getText();
    }
    
    /**
     * 
     * @param text 
     */
    public void addText(String text) {
        bitmapText.setText(bitmapText.getText() + "" + text);
    }

    /**
     * Add a new line of text every time.
     * @param text 
     */
    public void append(String text) {
        if (bitmapText.getText() == null || bitmapText.getText().length() == 0) {
            bitmapText.setText(text);
        } else {
            bitmapText.setText(bitmapText.getText() + "\n" + text);
        }
        updateScrolling();
    }

    protected void updateScrolling() {
        String[] rows = bitmapText.getText().split("\n");
        if (rows.length >= maxLines) {
            bitmapText.setText("");
            for (int i = 1; i < rows.length; i++) {
                String text = rows[i];
                if (bitmapText.getText() == null || bitmapText.getText().length() == 0) {
                    bitmapText.setText(text);
                } else {
                    bitmapText.setText(bitmapText.getText() + "\n" + text);
                }
            }
            System.out.println("===========================Max lines "+maxLines+"=========Rows "+rows.length+"============================");
            System.out.println("" + bitmapText.getText());
        }
        
    }

    /**
     * 
     * @param colorRGBA 
     */
    public void setTextColor(ColorRGBA colorRGBA) {
        bitmapText.setColor(colorRGBA);
    }

    /**
     * 
     * @param size 
     */
    public void setFontSize(float size) {
        bitmapText.setSize(size * window.getScaleFactorHeight());// font size
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 
     * @param enabled 
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void blur() {
        focus = false;
    }
}
