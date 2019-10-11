/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.button;

import com.bruynhuis.galago.resource.FontManager;
import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.ui.ImageWidget;
import com.bruynhuis.galago.ui.TextAlign;
import static com.bruynhuis.galago.ui.TextAlign.BOTTOM;
import static com.bruynhuis.galago.ui.TextAlign.CENTER;
import static com.bruynhuis.galago.ui.TextAlign.LEFT;
import static com.bruynhuis.galago.ui.TextAlign.RIGHT;
import static com.bruynhuis.galago.ui.TextAlign.TOP;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.effect.Effect;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.PopupDialog;
import com.bruynhuis.galago.ttf.TrueTypeFont;
import com.bruynhuis.galago.ttf.shapes.TrueTypeContainer;
import com.bruynhuis.galago.ttf.util.StringContainer;
import com.bruynhuis.galago.util.Debug;
import com.jme3.font.LineWrapMode;
import com.jme3.material.MatParam;
import java.util.ArrayList;

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
    protected ArrayList<TouchButtonListener> touchButtonListeners = new ArrayList<>();
    protected ActionListener actionListener;
    private boolean enabled = true;
    private boolean wasDown = false;
    protected BitmapFont bitmapFont;
    protected BitmapText bitmapText;
    protected TrueTypeFont trueTypeFont;
    protected TrueTypeContainer trueTypeContainer;
    protected StringContainer stringContainer;
    protected FontStyle fontStyle;
    protected String textStr = "Button";
    protected float lastTouchX = 0;
    protected float lastTouchY = 0;
    protected float padding = 0;

    /**
     *
     * @param panel
     * @param id
     */
    public TouchButton(Panel panel, String id) {
        this(panel, id, null, panel.getWindow().getWidth(), panel.getWindow().getHeight(), new FontStyle(FontManager.DEFAULT_FONT, 18));
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
        this(panel, id, "Resources/largebutton.png", 254, 54, new FontStyle(FontManager.DEFAULT_FONT, 18));
        this.panel = panel;
        this.addEffect(new TouchEffect(this));
        this.setText(text);
        this.setTextColor(ColorRGBA.DarkGray);
    }

    public TouchButton(Panel panel, String id, String pictureFile, float width, float height) {
        this(panel, id, pictureFile, width, height, new FontStyle(FontManager.DEFAULT_FONT, 18));
    }

    public TouchButton(Panel panel, String id, String pictureFile, float width, float height, boolean lockscale) {
        this(panel, id, pictureFile, width, height, new FontStyle(FontManager.DEFAULT_FONT, 18), lockscale);
    }

    /**
     *
     * @param panel
     * @param id
     * @param pictureFile
     * @param width
     * @param height
     */
    public TouchButton(Panel panel, String id, String pictureFile, float width, float height, FontStyle fontStyle) {
        super(panel.getWindow(), panel, pictureFile, width, height, false);
        this.panel = panel;
        this.id = id;
        this.fontStyle = fontStyle;
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
    public TouchButton(Panel panel, String id, String pictureFile, float width, float height, FontStyle fontStyle, boolean lockscale) {
        super(panel.getWindow(), panel, pictureFile, width, height, lockscale);
        this.panel = panel;
        this.id = id;
        this.fontStyle = fontStyle;
        setName(id);

        init();

    }

    protected boolean isClickable() {
        return ((getParent() instanceof PopupDialog) && this.isVisible() && window.isDialogOpen())
                || (getParent().getParent() != null && (getParent().getParent() instanceof PopupDialog) && this.isVisible() && window.isDialogOpen())
                || (getParent().getParent() != null && getParent().getParent().getParent() != null && (getParent().getParent().getParent() instanceof PopupDialog) && this.isVisible() && window.isDialogOpen())
                || (!(getParent() instanceof PopupDialog) && this.isVisible() && !window.isDialogOpen());
    }

    @Override
    protected boolean isBatched() {
        return false;
    }

    protected void init() {
        //Init the text

//        //This fixes the out of memory opengl error we get in android.
//        if (textStr == null || textStr.length() == 0 || textStr.equals(" ")) {
//            textStr = ".";
//        }

        this.padding = window.getScaleFactorWidth() * 0;

        bitmapFont = panel.getWindow().getApplication().getFontManager().getBitmapFonts(fontStyle);

        if (bitmapFont != null) {
            //Init the text
            bitmapText = bitmapFont.createLabel(textStr);
            bitmapText.setText(textStr);             // the text
            //The Rectangle box height value for bitmap text is not a physical height but half the height
            Rectangle rectangle = new Rectangle((-getWidth() * 0.5f) + padding, ((getHeight() * 0.6f) - padding), getWidth() - padding, (getHeight()) - padding);
//            System.out.println("TouchButton Rectange = " + rectangle);
            bitmapText.setBox(rectangle);
            bitmapText.setSize(fontStyle.getFontSize() * window.getScaleFactorHeight());      // font size
            bitmapText.setColor(ColorRGBA.White);// font color
            bitmapText.setLineWrapMode(LineWrapMode.Word);
            bitmapText.setAlignment(BitmapFont.Align.Center);
            bitmapText.setVerticalAlignment(BitmapFont.VAlign.Center);
//            widgetNode.attachChild(bitmapText);
//            bitmapText.center();

        } else {
            //Init the text
            trueTypeFont = panel.getWindow().getApplication().getFontManager().getTtfFonts(fontStyle);
            stringContainer = new StringContainer(trueTypeFont, textStr, 0, new Rectangle(-getWidth() * 0.5f, getHeight() * 0.5f, getWidth(), getHeight()));
            stringContainer.setWrapMode(StringContainer.WrapMode.Word);
            stringContainer.setAlignment(StringContainer.Align.Center);
            stringContainer.setVerticalAlignment(StringContainer.VAlign.Center);
            trueTypeContainer = trueTypeFont.getFormattedText(stringContainer, ColorRGBA.White.mult(0.99f), ColorRGBA.DarkGray);

//            widgetNode.attachChild(trueTypeContainer);
        }

        widgetNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                if (isVisible() && isEnabled() && isTouched() && isClickable()) {

                    for (int i = 0; i < touchButtonListeners.size(); i++) {
                        TouchButtonListener touchButtonListener = touchButtonListeners.get(i);
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


        panel.add(this);

        widgetNode.setUserData(TYPE_TOUCH_BUTTON, this);

    }

    /**
     *
     * @param align
     */
    public void setTextAlignment(TextAlign align) {
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
    public void setTextVerticalAlignment(TextAlign align) {
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

    private boolean isTextEmpty(String text) {
        return text == null || text.length() == 0 || text.equals(" ");
    }

    /**
     *
     * @param text
     */
    //This fixes the out of memory opengl error we get in android.
    public void setText(String text) {

        if (isTextEmpty(text)) {
            if (bitmapText != null) {
                this.bitmapText.removeFromParent();

            } else if (stringContainer != null) {
                this.trueTypeContainer.removeFromParent();

            }
        } else {
            if (bitmapText != null) {
                this.bitmapText.setText(text);
                if (this.bitmapText.getParent() == null) widgetNode.attachChild(this.bitmapText);

            } else if (stringContainer != null) {
                this.stringContainer.setText(text);
                if (this.trueTypeContainer.getParent() == null) widgetNode.attachChild(this.trueTypeContainer);
                this.trueTypeContainer.updateGeometry();

            }
        }

    }

    /**
     * Return the text value of this button
     * @return 
     */
    public String getText() {
        if (bitmapText != null) {
            if (this.bitmapText.getParent() == null) {
                return "";
            } else {
                return this.bitmapText.getText();
            }            

        } else if (stringContainer != null) {
            if (this.trueTypeContainer.getParent() == null) {
                return "";
            } else {
                return this.stringContainer.getText();
            }            

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
     * @param size
     */
    public void setFontSize(float size) {

        if (bitmapText != null) {
            bitmapText.setSize(size * window.getScaleFactorHeight());// font size

        } else if (stringContainer != null) {
            //TODO
            Debug.log("WARNING: FontSize not supported on TouchButton: " + uid);
        }

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


            for (int i = 0; i < touchButtonListeners.size(); i++) {
                TouchButtonListener touchButtonListener = touchButtonListeners.get(i);
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
            for (int i = 0; i < touchButtonListeners.size(); i++) {
                TouchButtonListener touchButtonListener = touchButtonListeners.get(i);
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
            for (int i = 0; i < touchButtonListeners.size(); i++) {
                TouchButtonListener touchButtonListener = touchButtonListeners.get(i);
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
        this.touchButtonListeners.add(touchButtonListener1);
    }

    public void removeTouchButtonListener(TouchButtonListener touchButtonListener1) {
        this.touchButtonListeners.remove(touchButtonListener1);
    }

    public void clearTouchButtonListeners() {
        this.touchButtonListeners.clear();
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

        } else if (stringContainer != null) {
            //TODO: NEED TO find a way to handle this
            MatParam matParam = this.trueTypeContainer.getMaterial().getParam("Color");
            ColorRGBA col = (ColorRGBA) matParam.getValue();
            col.a = alpha;
            this.trueTypeContainer.getMaterial().setColor("Color", col);
            this.trueTypeContainer.updateGeometry();
        }
    }

    public void setMargins(float left, float top, float right, float bottom) {
        float xP = -getWidth() * 0.5f;
        float yP = getHeight() * 0.5f;
        float recWidth = getWidth();
        float recHeight = (getHeight() * 0.5f);

        if (bitmapText != null) {
            bitmapText.setBox(new Rectangle(xP + left, yP - top, recWidth - right, recHeight + bottom));

        } else if (stringContainer != null) {
            recHeight = (getHeight());
            stringContainer.setTextBox(new Rectangle(xP + left, yP - top, recWidth - right, recHeight + bottom));
            this.trueTypeContainer.updateGeometry();

        }


    }

    @Override
    public void updatePicture(String pictureFile) {
        ColorRGBA colorRGBA = null;
        if (bitmapText != null) {
            colorRGBA = bitmapText.getColor().clone();

        } else if (stringContainer != null) {
            colorRGBA = ((ColorRGBA) trueTypeContainer.getMaterial().getParam("Color").getValue()).clone();

        }

        super.updatePicture(pictureFile);
        setTextColor(colorRGBA);
    }

    @Override
    public void updateToOriginalPicture() {
        ColorRGBA colorRGBA = null;
        if (bitmapText != null) {
            colorRGBA = bitmapText.getColor().clone();

        } else if (stringContainer != null) {
            colorRGBA = ((ColorRGBA) trueTypeContainer.getMaterial().getParam("Color").getValue()).clone();

        }

        super.updateToOriginalPicture();
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
    
    public void setId(String id) {
        this.id = id;
        this.setName(id);
    }
}
