/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui;

import com.bruynhuis.galago.resource.FontManager;
import com.bruynhuis.galago.ttf.TrueTypeBMP;
import com.bruynhuis.galago.ttf.shape.TrueTypeContainer;
import com.bruynhuis.galago.ttf.util.StringContainer;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;

/**
 *
 * A normal text label on the screen.
 * 
 * @author nidebruyn
 */
public class TTFLabel extends Widget {

    protected Panel panel;
    protected BitmapFont bitmapFont;
    protected BitmapText bitmapText;
    protected TrueTypeBMP trueTypeBMP;
    protected TrueTypeContainer trueTypeContainer;
    protected StringContainer stringContainer;
    protected FontStyle fontStyle;

    /**
     *
     * @param panel
     * @param text
     */
    public TTFLabel(Panel panel, String text) {
        this(panel, text, 300, 40, new FontStyle(FontManager.DEFAULT_FONT, 26));
    }

    /**
     *
     * @param panel
     * @param text
     * @param fontSize
     */
    public TTFLabel(Panel panel, String text, int fontSize) {
        this(panel, text, 300, 40, new FontStyle(FontManager.DEFAULT_FONT, fontSize));

    }

    public TTFLabel(Panel panel, String text, int fontSize, float width, float height) {
        this(panel, text, width, height, new FontStyle(FontManager.DEFAULT_FONT, fontSize));

    }

    /**
     *
     * @param panel
     * @param text
     * @param fontSize
     * @param width
     * @param height
     */
    public TTFLabel(Panel panel, String text, float width, float height, FontStyle fontStyle) {
        super(panel.getWindow(), panel, width, height, false);
        this.panel = panel;

        //This fixes the out of memory opengl error we get in android.
        if (text == null || text.length() == 0) {
            text = " ";
        }

        bitmapFont = panel.getWindow().getApplication().getFontManager().getBitmapFonts(fontStyle);
//        Rectangle textBox = new Rectangle(-getWidth() * 0.5f, getHeight() * 0.5f, getWidth(), getHeight() *0.5f);

        if (bitmapFont != null) {
            //Init the text
            bitmapText = bitmapFont.createLabel(text);
            bitmapText.setText(text);             // the text
            //The Rectangle box height value for bitmap text is not a physical height but half the height
            bitmapText.setBox(new Rectangle(-getWidth() * 0.5f, getHeight() * 0.5f, getWidth(), getHeight() *0.5f));
            bitmapText.setSize(fontStyle.getFontSize() * panel.getWindow().getScaleFactorHeight());      // font size
            bitmapText.setColor(ColorRGBA.White);// font color
            bitmapText.setAlignment(BitmapFont.Align.Center);
            bitmapText.setVerticalAlignment(BitmapFont.VAlign.Center);
            widgetNode.attachChild(bitmapText);
        } else {
            //Init the text
            trueTypeBMP = panel.getWindow().getApplication().getFontManager().getTtfFonts(fontStyle);
            stringContainer = new StringContainer(trueTypeBMP, text, 0, new Rectangle(-getWidth() * 0.5f, getHeight() * 0.5f, getWidth(), getHeight()));
//        stringContainer.setWrapMode(StringContainer.WrapMode.Word);
            stringContainer.setAlignment(StringContainer.Align.Center);
            stringContainer.setVerticalAlignment(StringContainer.VAlign.Center);
            trueTypeContainer = trueTypeBMP.getFormattedText(stringContainer, ColorRGBA.White.mult(0.99f), new ColorRGBA(0.01f, 0.01f, 0.01f, 1));

            widgetNode.attachChild(trueTypeContainer);
        }

        panel.add(this);

//        bitmapText.setLocalTranslation(bitmapText.getLocalTranslation().x, bitmapText.getLocalTranslation().y, 0.001f);
    }

    /**
     *
     * @param align
     */
    public void setAlignment(TextAlign align) {
        if (bitmapText != null) {
            switch (align) {
                case LEFT:
                    bitmapText.setAlignment(BitmapFont.Align.Left);
                    break;
                case RIGHT:
                    bitmapText.setAlignment(BitmapFont.Align.Right);
                    break;
                case CENTER:
                    bitmapText.setAlignment(BitmapFont.Align.Center);
                    break;
            }

        } else if (stringContainer != null) {
            switch (align) {
                case LEFT:
                    stringContainer.setAlignment(StringContainer.Align.Left);
                    break;
                case RIGHT:
                    stringContainer.setAlignment(StringContainer.Align.Right);
                    break;
                case CENTER:
                    stringContainer.setAlignment(StringContainer.Align.Center);
                    break;
            }
            
            this.trueTypeContainer.updateGeometry();
        }

    }

    /**
     *
     * @param align
     */
    public void setVerticalAlignment(TextAlign align) {
        if (bitmapText != null) {
            switch (align) {
                case TOP:
                    bitmapText.setVerticalAlignment(BitmapFont.VAlign.Top);
                    break;
                case BOTTOM:
                    bitmapText.setVerticalAlignment(BitmapFont.VAlign.Bottom);
                    break;
                case CENTER:
                    bitmapText.setVerticalAlignment(BitmapFont.VAlign.Center);
                    break;
            }

        } else if (stringContainer != null) {
            switch (align) {
                case TOP:
                    stringContainer.setVerticalAlignment(StringContainer.VAlign.Top);
                    break;
                case BOTTOM:
                    stringContainer.setVerticalAlignment(StringContainer.VAlign.Bottom);
                    break;
                case CENTER:
                    stringContainer.setVerticalAlignment(StringContainer.VAlign.Center);
                    break;
            }
            
            this.trueTypeContainer.updateGeometry();
        }
    }

    /**
     *
     * @param text
     */
    //This fixes the out of memory opengl error we get in android.
    public void setText(String text) {
        if (text == null || text.length() == 0) {
            text = " ";
        }

        if (bitmapText != null) {
            this.bitmapText.setText(text);

        } else if (stringContainer != null) {
            this.stringContainer.setText(text);
            this.trueTypeContainer.updateGeometry();

        }

    }

    public String getText() {
        if (bitmapText != null) {
            return this.bitmapText.getText();

        } else if (stringContainer != null) {
            return this.stringContainer.getText();

        } else {
            return null;
        }
    }

    /**
     *
     * @param colorRGBA
     */
    public void setTextColor(ColorRGBA colorRGBA) {
        if (bitmapText != null) {
            this.bitmapText.setColor(colorRGBA);

        } else if (stringContainer != null) {
            this.trueTypeContainer.getMaterial().setColor("Color", colorRGBA);
            this.trueTypeContainer.updateGeometry();

        }
    }

    /**
     * This will set the outline color if there is an outline
     *
     * @param colorRGBA
     */
    public void setOutlineColor(ColorRGBA colorRGBA) {
        if (stringContainer != null) {
            this.trueTypeContainer.getMaterial().setColor("Outline", colorRGBA);
            this.trueTypeContainer.updateGeometry();

        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (bitmapText != null) {
            if (visible && widgetNode.getCullHint().equals(Spatial.CullHint.Always)) {
                bitmapText.setCullHint(Spatial.CullHint.Never);
            } else if (!visible && widgetNode.getCullHint().equals(Spatial.CullHint.Never)) {
                bitmapText.setCullHint(Spatial.CullHint.Always);
            }

        } else if (stringContainer != null) {
            if (visible && widgetNode.getCullHint().equals(Spatial.CullHint.Always)) {
                trueTypeContainer.setCullHint(Spatial.CullHint.Never);
            } else if (!visible && widgetNode.getCullHint().equals(Spatial.CullHint.Never)) {
                trueTypeContainer.setCullHint(Spatial.CullHint.Always);
            }

        }

    }

    @Override
    public void setTransparency(float alpha) {
        if (bitmapText != null) {
            bitmapText.setAlpha(alpha);

        } else if (stringContainer != null) {
            //TODO: NEED TO find a way to handle this
            
//            this.trueTypeContainer.getMaterial().setColor("Color", colorRGBA);
//            this.trueTypeContainer.updateGeometry();

        }
        
    }

    @Override
    public float getTransparency() {
        if (bitmapText != null) {
            return bitmapText.getAlpha();

        } else if (stringContainer != null) {
            //TODO: NEED TO find a way to handle this
            return 1f;
            
//            this.trueTypeContainer.getMaterial().setColor("Color", colorRGBA);
//            this.trueTypeContainer.updateGeometry();

        } else {
            return 1f;
        }
        
    }

    @Override
    protected boolean isBatched() {
        return false;
    }
}
