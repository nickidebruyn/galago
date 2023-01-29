package com.galago.editor.ui;

import com.bruynhuis.galago.input.Input;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.listener.FocusListener;
import com.bruynhuis.galago.ui.listener.KeyboardListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.galago.editor.utils.EditorUtils;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author ndebruyn
 */
public class FloatField extends Panel {

    private static float scale = 0.6f;

    private InputField inputField;
    private Image focusImage;
    private float originalValue;
    private boolean focus;
    private float incrementAmount = 0.005f;
    private float minAmount = 0;
    private float maxAmount = 10000000;

    public FloatField(Panel parent, String id) {
        super(parent, null, 260 * scale, 48 * scale);

        inputField = new InputField(this, id);
        inputField.center();

        focusImage = new Image(this, "Interface/field-focus.png", 260 * scale, 48 * scale);
        focusImage.setBackgroundColor(EditorUtils.theme.getSelectionColor());
        focusImage.center();
        focusImage.setTransparency(0);

        inputField.addFocusListener(new FocusListener() {
            @Override
            public void doFocus(String id) {
                focusImage.setTransparency(1);
                if (inputField.getText().matches("0.0")) {
                    inputField.setText("");
                }
                focus = true;
            }

            @Override
            public void doBlur(String id) {
                focusImage.setTransparency(0);
                if (inputField.getText().trim().matches("")) {
                    setValue(0.0f);
                }
                
                focus = false;
            }

        });

        parent.add(this);
        
        getWidgetNode().addControl(new AbstractControl() {
            
            @Override
            protected void controlUpdate(float f) {
                
                if (focus) {
                    if (Input.get("up_arrow") == 1) {
                        setValue(getValue() + incrementAmount);
                    }
                    
                    if (Input.get("down_arrow") == 1) {
                        setValue(getValue() - incrementAmount);
                    }
                }
                
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
            
        });

    }

    public void addKeyboardListener(KeyboardListener keyboardListener) {
        inputField.addKeyboardListener(keyboardListener);

    }

    public void setValue(float val) {
        originalValue = val;
        
        if (val > maxAmount) {
            val = maxAmount;            
        } else if (val < minAmount) {
            val = minAmount;            
        }        
        
        inputField.setText(String.valueOf(val));

    }

    public float getValue() {

        if (inputField.getText().isBlank()) {
            return 0;
        }

        try {
            return Float.parseFloat(inputField.getText());
            
        } catch (Exception e) {
        }

        return originalValue;
    }

    public float getIncrementAmount() {
        return incrementAmount;
    }

    public void setIncrementAmount(float incrementAmount) {
        this.incrementAmount = incrementAmount;
    }

    public float getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(float minAmount) {
        this.minAmount = minAmount;
    }

    public float getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(float maxAmount) {
        this.maxAmount = maxAmount;
    }

}
