/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.button;

import com.bruynhuis.galago.ui.ImageWidget;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.effect.Effect;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.PopupDialog;

/**
 * A TouchButton is the control which represents a button on the screen. It can
 * have any image and you add a TouchButtonListener to it to check for clicks.
 *
 * @author nidebruyn
 */
public class TouchButton extends ImageWidget implements Touchable {

    public static final String TYPE_TOUCH_BUTTON = "TYPE_TOUCH_BUTTON";
    protected Panel panel;
    protected int uid;
    protected String id;
    protected TouchButtonListener touchButtonListener;
    protected ActionListener actionListener;
    private boolean enabled = true;
    private boolean wasDown = false;
    protected BitmapText bitmapText;
    protected float fontSize = 36;
    protected String textStr;
    protected float lastTouchX = 0;
    protected float lastTouchY = 0;

    /**
     *
     * @param panel
     * @param id
     */
    public TouchButton(Panel panel, String id) {
        this(panel, id, null, panel.getWindow().getWidth(), panel.getWindow().getHeight());
        this.panel = panel;
    }

    /**
     * Default button constructor.
     *
     * @param panel
     * @param id
     * @param text
     */
    public TouchButton(Panel panel, String id, String text) {
        this(panel, id, "Resources/largebutton.png", 254, 44);
        this.panel = panel;
        this.setText(text);
        this.setTextColor(ColorRGBA.DarkGray);
        this.setFontSize(20);
        this.addEffect(new TouchEffect(this));
    }

    /**
     *
     * @param panel
     * @param id
     * @param pictureFile
     * @param width
     * @param height
     */
    public TouchButton(Panel panel, String id, String pictureFile, float width, float height) {
        super(panel.getWindow(), panel, pictureFile, width, height, false);
        this.panel = panel;
        this.id = id;
        setName(id);

        init();

    }

    /**
     *
     * @param panel
     * @param id
     * @param pictureFile
     * @param width
     * @param height
     * @param lockscale Lock the scale effect when the game runs at a diffener
     * resolution than designed by.
     */
    public TouchButton(Panel panel, String id, String pictureFile, float width, float height, boolean lockscale) {
        super(panel.getWindow(), panel, pictureFile, width, height, lockscale);
        this.panel = panel;
        this.id = id;
        setName(id);

        init();

    }
    
    protected boolean isClickable() {
        return ((getParent() instanceof PopupDialog) && this.isVisible() && window.isDialogOpen()) ||
                (!(getParent() instanceof PopupDialog) && this.isVisible() && !window.isDialogOpen());
    }

    @Override
    protected boolean isBatched() {
        return false;
    }

    protected void init() {
        //Init the text
        bitmapText = window.getBitmapFont().createLabel(id);
        bitmapText.setText(" ");

//        float xP = -getWidth() * 0.5f;
//        float yP = getHeight() * 0.5f;
//        float recWidth = getWidth();
//        float factor = 1f;
//        float recHeight = (getHeight() * 0.5f) * factor;
//        float spacing = 10f * window.getScaleFactorWidth();
//
//        bitmapText.setBox(new Rectangle(xP, yP, recWidth - spacing, recHeight));
        
        float xP = -getWidth() * 0.5f;
        float yP = getHeight() * 0.5f;
        float recWidth = getWidth();
        float recHeight = (getHeight() * 0.5f);
        bitmapText.setBox(new Rectangle(xP, yP, recWidth, recHeight));
        
        bitmapText.setSize(fontSize * window.getScaleFactorHeight());      // font size
        bitmapText.setColor(ColorRGBA.White);// font color
        bitmapText.setAlignment(BitmapFont.Align.Center);
        bitmapText.setVerticalAlignment(BitmapFont.VAlign.Center);
        bitmapText.setLineWrapMode(LineWrapMode.Word);
        widgetNode.attachChild(bitmapText);

        widgetNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                if (isVisible() && isEnabled() && isTouched() && isClickable()) {
                    if (touchButtonListener != null) {
                        touchButtonListener.doTouchMove(lastTouchX, lastTouchY, tpf, id);
                    }
                }

//                if (isVisible() && isEnabled()) {
//                    //Check if the touch movement stopped.
//                    touchMoveTimer.update(tpf);
//                    if (touchMoveTimer.finished()) {
//                        fireTouchCancel(panel.getWindow().getApplication().getInputManager().getCursorPosition().x,
//                                panel.getWindow().getApplication().getInputManager().getCursorPosition().y, tpf);
//                        touchMoveTimer.stop();
//                    }
//                }

            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });

        bitmapText.center();

        panel.add(this);

        widgetNode.setUserData(TYPE_TOUCH_BUTTON, this);

    }

    /**
     *
     * @param align
     */
    public void setTextAlignment(BitmapFont.Align align) {
        bitmapText.setAlignment(align);
    }

    /**
     *
     * @param vAlign
     */
    public void setTextVerticalAlignment(BitmapFont.VAlign vAlign) {
        bitmapText.setVerticalAlignment(vAlign);
    }

    public int getUid() {
        return uid;
    }

    /**
     *
     * @param uid
     */
    public void setUid(int uid) {
        this.uid = uid;
    }

    /**
     *
     * @param text
     */
    public void setText(String text) {
        textStr = text;
        if (text == null || text.length() == 0) {
            text = " ";
        }
        bitmapText.setText(text);
    }

    public String getText() {
        return textStr;
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

    @Override
    public void fireTouchDown(float x, float y, float tpf) {
        if (enabled && !isTouched() && isClickable()) {
            wasDown = true;
            lastTouchX = x;
            lastTouchY = y;

            for (Effect effect : effects) {
                effect.fireTouchDown();
            }

            if (touchButtonListener != null) {
                touchButtonListener.doTouchDown(x, y, tpf, id);
            }

        }

    }

    @Override
    public void fireTouchUp(float x, float y, float tpf) {
        if (enabled && isTouched() && isClickable()) {
            lastTouchX = x;
            lastTouchY = y;

            for (Effect effect : effects) {
                effect.fireTouchUp();
            }
            if (touchButtonListener != null) {
                touchButtonListener.doTouchUp(x, y, tpf, id);
            }

            wasDown = false;

        }

    }

    @Override
    public void fireTouchCancel(float x, float y, float tpf) {
        if (enabled && isTouched() && isClickable()) {
            lastTouchX = x;
            lastTouchY = y;

            for (Effect effect : effects) {
                effect.fireTouchUp();
            }
            if (touchButtonListener != null) {
                touchButtonListener.doTouchCancel(x, y, tpf, id);
            }

            wasDown = false;

        }

    }

    @Override
    public void fireTouchMove(float x, float y, float tpf) {
        if (enabled && isTouched() && isClickable()) {
            lastTouchX = x;
            lastTouchY = y;
        }

    }

    @Override
    public boolean isTouched() {
        return wasDown;
    }

    /**
     * Use this method to set the TouchButtonListener
     *
     * @param touchButtonListener1
     */
    public void addTouchButtonListener(TouchButtonListener touchButtonListener1) {
        this.touchButtonListener = touchButtonListener1;
    }
    
    public void removeTouchButtonListener(TouchButtonListener touchButtonListener1) {
        this.touchButtonListener = null;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (visible) {
            bitmapText.setCullHint(Spatial.CullHint.Never);
        } else {
            bitmapText.setCullHint(Spatial.CullHint.Always);
        }
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
        for (Effect effect : effects) {
            effect.fireEnabled(enabled);
        }
    }

    @Override
    public void setTransparency(float alpha) {
        super.setTransparency(alpha); //To change body of generated methods, choose Tools | Templates.

        if (bitmapText != null) {
            bitmapText.setAlpha(alpha);
        }
    }
    
    public void setMargins(float left, float top, float right, float bottom) {
        float xP = -getWidth() * 0.5f;
        float yP = getHeight() * 0.5f;
        float recWidth = getWidth();
        float recHeight = (getHeight() * 0.5f);

        bitmapText.setBox(new Rectangle(xP+left, yP-top, recWidth-right, recHeight+bottom));
        
    }

    @Override
    public void updatePicture(String pictureFile) {
        ColorRGBA colorRGBA = bitmapText.getColor().clone();
        super.updatePicture(pictureFile); //To change body of generated methods, choose Tools | Templates.
        setTextColor(colorRGBA);
    }

    @Override
    public void updateToOriginalPicture() {
        ColorRGBA colorRGBA = bitmapText.getColor().clone();
        super.updateToOriginalPicture(); //To change body of generated methods, choose Tools | Templates.
        setTextColor(colorRGBA);
    }

    public void select(float tpf) {
        
        if (enabled && isClickable()) {
            for (Effect effect : effects) {
                effect.fireSelected();
            }

        }
    }

    public void unselect(float tpf) {
        if (enabled && isClickable()) {            
            for (Effect effect : effects) {
                effect.fireUnselected();
            }

        }
    }
}
