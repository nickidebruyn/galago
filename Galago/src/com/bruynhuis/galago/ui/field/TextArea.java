/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.field;

import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.resource.FontManager;
import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.ui.ImageWidget;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ttf.TrueTypeFont;
import com.bruynhuis.galago.ttf.shapes.TrueTypeContainer;
import com.bruynhuis.galago.ttf.util.StringContainer;
import com.bruynhuis.galago.ui.TextAlign;
import static com.bruynhuis.galago.ui.TextAlign.BOTTOM;
import static com.bruynhuis.galago.ui.TextAlign.CENTER;
import static com.bruynhuis.galago.ui.TextAlign.LEFT;
import static com.bruynhuis.galago.ui.TextAlign.RIGHT;
import static com.bruynhuis.galago.ui.TextAlign.TOP;
import com.bruynhuis.galago.ui.button.TouchKeyNames;
import com.bruynhuis.galago.ui.listener.FocusListener;
import com.bruynhuis.galago.ui.listener.KeyboardListener;
import com.bruynhuis.galago.util.Debug;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import com.jme3.input.InputManager;
import com.jme3.input.KeyNames;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.Properties;

/**
 * A text area where you can type some text.
 *
 * @author nidebruyn
 */
public class TextArea extends ImageWidget implements InputType {

    protected Panel panel;
    protected String id;
    protected InputManager inputManager;
    protected Camera cam;
    protected CollisionResults results;
    protected Ray ray;
    protected ActionListener actionListener;
    private boolean enabled = true;
    private boolean wasDown = false;
    private boolean focus = false;
    protected BitmapFont bitmapFont;
    protected BitmapText bitmapText;
    protected TrueTypeFont trueTypeFont;
    protected TrueTypeContainer trueTypeContainer;
    protected StringContainer stringContainer;
    protected FontStyle fontStyle;
    protected int maxLength = 255;
    protected int maxLines = 5;
    protected float padding = 0;
    
    protected boolean caps = false;
    protected boolean shift = false;
    
    protected KeyboardListener keyboardListener;
    protected FocusListener focusListener;
    protected KeyNames keyNames = new KeyNames();
    protected TouchKeyNames touchKeyNames = new TouchKeyNames();
    private String textToUpdate;

    /**
     *
     * @param panel
     * @param id
     * @param pictureFile
     */
    public TextArea(Panel panel, String id, String pictureFile) {
        this(panel, id, pictureFile, 500, 230, 5, new FontStyle(FontManager.DEFAULT_FONT, 18), false);
    }

    /**
     *
     * @param panel
     * @param width
     * @param height
     */
    public TextArea(Panel panel, float width, float height) {
        this(panel, "some_area", "Resources/textarea.png", width, height, 5, new FontStyle(FontManager.DEFAULT_FONT, 18), false);

    }
    
    /**
     *
     * @param panel
     * @param width
     * @param height
     */
    public TextArea(Panel panel, String uid, float width, float height) {
        this(panel, uid, "Resources/textarea.png", width, height, 5, new FontStyle(FontManager.DEFAULT_FONT, 18), false);

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
        this(panel, id, pictureFile, width, height, 5, new FontStyle(FontManager.DEFAULT_FONT, 18), false);
    }

    public TextArea(Panel panel, String id, String pictureFile, float width, float height, float padding) {
        this(panel, id, pictureFile, width, height, 5, new FontStyle(FontManager.DEFAULT_FONT, 18), false);
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
    public TextArea(Panel panel, String id, String pictureFile, float width, float height, float padding, FontStyle fontStyle, boolean lockScale) {
        super(panel.getWindow(), panel, pictureFile, width, height, lockScale);
        this.id = id;
        this.fontStyle = fontStyle;
        this.padding = window.getScaleFactorWidth() * padding;

        bitmapFont = panel.getWindow().getApplication().getFontManager().getBitmapFonts(fontStyle);

        if (bitmapFont != null) {
            //Init the text
            bitmapText = bitmapFont.createLabel("");
            bitmapText.setText("");             // the text
            //The Rectangle box height value for bitmap text is not a physical height but half the height
            bitmapText.setBox(new Rectangle(-getWidth() * 0.5f, getHeight() * 0.5f, getWidth(), getHeight() * 0.5f));
            bitmapText.setSize(fontStyle.getFontSize() * panel.getWindow().getScaleFactorHeight());      // font size
            bitmapText.setColor(ColorRGBA.White);// font color
            bitmapText.setAlignment(BitmapFont.Align.Center);
            bitmapText.setVerticalAlignment(BitmapFont.VAlign.Center);
            
        } else {
            //Init the text
            trueTypeFont = panel.getWindow().getApplication().getFontManager().getTtfFonts(fontStyle);
            stringContainer = new StringContainer(trueTypeFont, "TextArea", 0, new Rectangle((-getWidth() * 0.5f) + padding, (getHeight() * 0.5f) - padding, getWidth() - padding, (getHeight()) - padding));
//        stringContainer.setWrapMode(StringContainer.WrapMode.Word);
            stringContainer.setAlignment(StringContainer.Align.Left);
            stringContainer.setVerticalAlignment(StringContainer.VAlign.Top);
            trueTypeContainer = trueTypeFont.getFormattedText(stringContainer, ColorRGBA.DarkGray, ColorRGBA.DarkGray);

            widgetNode.attachChild(trueTypeContainer);
        }
        
        widgetNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                if (textToUpdate != null) {
                    setText(textToUpdate);
                    textToUpdate = null;
                }
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {

            }
        });

        
        this.inputManager = window.getInputManager();
        this.cam = window.getApplication().getCamera();
        this.results = new CollisionResults();
        this.ray = new Ray(cam.getLocation(), cam.getDirection());

        actionListener = new ActionListener() {
            public void onAction(String name, boolean isPressed, float tpf) {
                results.clear();

                if (isVisible() && isEnabled()) {

                    if ((TextArea.this.id + "MOUSE").equals(name)) {

                        // 1. calc direction
                        Vector3f origin = new Vector3f(inputManager.getCursorPosition().x, inputManager.getCursorPosition().y, 1f);
                        Vector3f direction = new Vector3f(0, 0, -1);

                        // 2. Aim the ray from cam loc to cam direction.        
                        ray.setOrigin(origin);
                        ray.setDirection(direction);

                        // 3. Collect intersections between Ray and Shootables in results list.
                        window.getWindowNode().collideWith(ray, results);

                        // 5. Use the results (we mark the hit object)
                        if (results.size() > 0) {

                            for (int i = 0; i < results.size(); i++) {
                                CollisionResult cr = results.getCollision(i);
//                                System.out.println("\t-> Hit: " + cr.getGeometry().getParent().getName());

                                if (widgetNode.hasChild(cr.getGeometry())) {
                                    System.out.println("\t\t\tCollision -> " + TextArea.this.id);
                                    if (isPressed) {
                                        wasDown = true;
                                        getWindow().removeFocusFromFields();
                                        focus = true;
                                        fireFocusListener(TextArea.this.id);
                                    }
                                }
                            }
                        }

                    }
                }
                
            }
        };

        inputManager.addRawInputListener(new RawInputListener() {

            public void beginInput() {

            }

            public void endInput() {

            }

            public void onJoyAxisEvent(JoyAxisEvent evt) {

            }

            public void onJoyButtonEvent(JoyButtonEvent evt) {

            }

            public void onMouseMotionEvent(MouseMotionEvent evt) {

            }

            public void onMouseButtonEvent(MouseButtonEvent evt) {

            }

            public void onKeyEvent(KeyInputEvent evt) {
//                System.out.println("Keyinput ***************** Key = " + evt.getKeyCode());

                if (enabled && focus && evt.isReleased()) {
                    String keyChar = keyNames.getName(evt.getKeyCode());
//                    System.out.println("Keyinput ***************** code = " + evt.getKeyCode());
//                    System.out.println("Keyinput ***************** char = " + evt.getKeyChar());

                    if (evt.getKeyCode() == 14) {
                        if (getText().length() > 0) {
                            setText(getText().substring(0, getText().length() - 1));
                        }
                    } else if (evt.getKeyCode() == 15) {
                        focus = false;

                    } else if (keyChar != null && evt.getKeyCode() == 57) {
                        setText(getText() + " ");

                    } else if (keyChar != null && evt.getKeyCode() == 58) {
                        caps = !caps;
                        
                    } else if (keyChar != null && (evt.getKeyCode() == 42 || evt.getKeyCode() == 54)) {
                        caps = false;

                    } else if (keyChar != null && keyChar.length() == 1) {
                        if (!caps) {
                            keyChar = keyChar.toLowerCase();
                        }
                        setText(getText() + keyChar);
                    }

                    if (getText().length() > maxLength) {
                        setText(getText().substring(0, maxLength));
                    }

                    fireKeyboardListener(evt);

                } else if (enabled && focus && !evt.isReleased()) {
                    String keyChar = keyNames.getName(evt.getKeyCode());
//                    System.out.println("Keyinput ***************** code = " + evt.getKeyCode());
//                    System.out.println("Keyinput ***************** char = " + evt.getKeyChar());

                    if (keyChar != null && (evt.getKeyCode() == 42 || evt.getKeyCode() == 54)) {
                        caps = true;

                    }

                    fireKeyboardListener(evt);

                }

            }

            public void onTouchEvent(TouchEvent evt) {
//                System.out.println("Touchinput ***************** Keycode = " + evt.getKeyCode());
                
//                if (enabled && focus && evt.getType().equals(TouchEvent.Type.KEY_DOWN)) {
//                    String keyChar = touchKeyNames.getName(evt.getKeyCode());
//                    System.out.println("\n\n\nTouchinput ***************** KeyCode = " + evt.getKeyCode());
//                    
//                    if (evt.getKeyCode() == 67) { //backspace
//                        if (getText().length() > 0) {
//                            setText(getText().substring(0, getText().length()-1));
//                        }                        
//                        
//                    } else if (keyChar != null && evt.getKeyCode() == 62) { //space
//                        setText(getText() + " ");
//                        
//                    } else if (keyChar != null && evt.getKeyCode() == 59) { //shift
//                        caps = !caps;
//                        
//                    } else if (keyChar != null && keyChar.length() == 1) {
//                        //TODO:
////                        if (!caps) {
//                            keyChar = keyChar.toLowerCase();                            
////                        }
//                        setText(getText() + keyChar);
//                    }
//                    
//                    if (getText().length() > maxLength) {
//                        setText(getText().substring(0, maxLength));
//                    }
//                    
////                    if (evt.getKeyCode() == 67) {
////                        if (getText().length() > 0) {
////                            setText(getText().substring(0, getText().length()-1));
////                        }                        
////                        
////                    } else if (evt.getKeyCode() == 59) {
////                        if (getText().length() > 0) {
////                            setText(getText().substring(0, getText().length()-1));
////                        }                        
////                        
////                    } else if (keyChar != null && keyChar.length() == 1) {
////                        setText(getText() + keyChar);
////                    }
////                    
////                    if (getText().length() > maxLength) {
////                        setText(getText().substring(0, maxLength));
////                    }
//                    
//                }
//

            }
        });
        
        panel.add(this);
        
        if (window.getApplication().isMobileApp()) {
            addFocusListener(new FocusListener() {
                public void doFocus(String id) {
                    Properties p = new Properties();
                    p.setProperty(BaseApplication.NAME, getText());
                    window.getApplication().fireKeyboardInputListener(p, TextArea.this);
                }

                @Override
                public void doBlur(String id) {
                }
                
            });
        }
    }

    @Override
    protected boolean isBatched() {
        return false;
    }
    
    @Override
    public void add(Node parent) {
        super.add(parent);
        String mappingName = TextArea.this.id + "MOUSE";
        if (!inputManager.hasMapping(mappingName)) {
            inputManager.addMapping(mappingName, new MouseButtonTrigger(0));
        }

        inputManager.addListener(actionListener, TextArea.this.id + "MOUSE");
    }

    @Override
    public void remove() {
        super.remove();
        inputManager.removeListener(actionListener);
    }
    
    public void addKeyboardListener(KeyboardListener keyboardListener) {
        this.keyboardListener = keyboardListener;
    }
    
    protected void fireKeyboardListener(KeyInputEvent event) {
        if (keyboardListener != null) {
            keyboardListener.doKeyPressed(event);
        }
    }
    
    public void addFocusListener(FocusListener focusListener1) {
        this.focusListener = focusListener1;
    }
    
    protected void fireFocusListener(String id) {
        if (focusListener != null) {
            focusListener.doFocus(id);
        }
    }
    
    protected void fireBlurListener(String id) {
        if (focusListener != null) {
            focusListener.doBlur(id);
        }
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
        } else if (bitmapText != null) {
            this.bitmapText.setText(text);
            if (this.bitmapText.getParent() == null) {
                widgetNode.attachChild(this.bitmapText);
            }

        } else if (stringContainer != null) {
            this.stringContainer.setText(text);
            if (this.trueTypeContainer.getParent() == null) {
                widgetNode.attachChild(this.trueTypeContainer);
            }
            this.trueTypeContainer.updateGeometry();

        }

    }

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

    /**
     *
     * @param text
     */
    public void addText(String text) {

        if (bitmapText != null) {
            bitmapText.setText(bitmapText.getText() + "" + text);

        } else if (stringContainer != null) {
            stringContainer.setText(stringContainer.getText() + "" + text);

        }

    }

    /**
     * Add a new line of text every time.
     *
     * @param text
     */
    public void append(String text) {
        
//        System.out.println("######### SET TEXT: " + bitmapText.getText());

        if (bitmapText != null) {
            if (bitmapText.getText() == null || bitmapText.getText().length() == 0) {
                bitmapText.setText(text);
            } else {
                bitmapText.setText(bitmapText.getText() + "\n" + text);
            }

        } else if (stringContainer != null) {
            if (stringContainer.getText() == null || stringContainer.getText().length() == 0) {
                stringContainer.setText(text);
            } else {
                stringContainer.setText(stringContainer.getText() + "\n" + text);
            }

        }


        updateScrolling();
    }

    /**
     * This will update the scrolling of text on the Text Area
     */
    protected void updateScrolling() {

        if (bitmapText != null) {
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
//                System.out.println("===========================Max lines " + maxLines + "=========Rows " + rows.length + "============================");
//                System.out.println("" + bitmapText.getText());
            }

        } else if (stringContainer != null) {
            String[] rows = stringContainer.getText().split("\n");
            if (rows.length >= maxLines) {
                stringContainer.setText("");
                for (int i = 1; i < rows.length; i++) {
                    String text = rows[i];
                    if (stringContainer.getText() == null || stringContainer.getText().length() == 0) {
                        stringContainer.setText(text);
                    } else {
                        stringContainer.setText(stringContainer.getText() + "\n" + text);
                    }
                }
//                System.out.println("===========================Max lines " + maxLines + "=========Rows " + rows.length + "============================");
//                System.out.println("" + stringContainer.getText());
            }

        }

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
            Debug.log("WARNING: FontSize not supported on TouchButton: " + id);
        }
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        
        if (bitmapText != null) {
            if (visible && widgetNode.getCullHint().equals(Spatial.CullHint.Always)) {
                inputManager.addListener(actionListener, TextArea.this.id + "MOUSE");
                bitmapText.setCullHint(Spatial.CullHint.Never);
                
            } else if (!visible && widgetNode.getCullHint().equals(Spatial.CullHint.Never)) {
                inputManager.removeListener(actionListener);
                bitmapText.setCullHint(Spatial.CullHint.Always);
            }

        } else if (stringContainer != null) {
            if (visible && widgetNode.getCullHint().equals(Spatial.CullHint.Always)) {
                inputManager.addListener(actionListener, TextArea.this.id + "MOUSE");
                trueTypeContainer.setCullHint(Spatial.CullHint.Never);
            } else if (!visible && widgetNode.getCullHint().equals(Spatial.CullHint.Never)) {
                inputManager.removeListener(actionListener);
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
    }

    public void blur() {
        focus = false;
        fireBlurListener(id);
    }

    @Override
    public void updateText(String text) {
        this.textToUpdate = text;
    }
}
