/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.field;

import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.button.ControlButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.ValueChangeListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author NideBruyn
 */
public class VSlider extends Panel {

    private float value = 0f;
    private float minValue = 0f;
    private float maxValue = 100f;
    private float incrementValue = 1f;
    private Image buttonImage;
    private Label label;
    private ControlButton touchButton;
    private String labelText = "";
    private boolean calculateValue = false;
    private List<ValueChangeListener> valueChangeListeners = new ArrayList<ValueChangeListener>();
    
    private float percentage = 0f;
    private float NATIVE_HEIGHT = 264;
    private float PADDING = 10f;
    private float HALFSIZE = (NATIVE_HEIGHT*0.5f)-PADDING; //This is half the amount
    
    public VSlider(Panel parent, float height) {
        this(parent, "Resources/vslider.png", "Resources/button-sliderv.png", 54, height);
    }

    public VSlider(Panel parent, String sliderImage, String buttonImagefile, float width, float height) {
        super(parent, sliderImage, width, height, true);
        float scaling = height/NATIVE_HEIGHT;
        NATIVE_HEIGHT = height;
        HALFSIZE = (NATIVE_HEIGHT*0.5f)-PADDING;
        
        label = new Label(this, "0", 14, 16, 16);
        label.setTextColor(ColorRGBA.LightGray);
        label.leftCenter(0, 0);
        
        buttonImage = new Image(this, buttonImagefile, width*scaling, width*scaling, true);
        buttonImage.center();

        touchButton = new ControlButton(this, "slider-button", width, NATIVE_HEIGHT, true);
        touchButton.center();
        touchButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                calculateValue = true;
                
            }

            @Override
            public void doTouchMove(float touchX, float touchY, float tpf, String uid) {
                calculateValue = true;
                
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                calculateValue = false;
            }

            @Override
            public void doTouchCancel(float touchX, float touchY, float tpf, String uid) {
                calculateValue = false;
            }
            
            
        });
        
        widgetNode.addControl(new AbstractControl() {

            @Override
            protected void controlUpdate(float tpf) {
                if (calculateValue) {
                    calculateNewPosition(window.getInputManager().getCursorPosition().y);
                }
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });

        parent.add(this);
    }

    private void calculateNewPosition(float touchY) {
        float touchDownY = getWidgetNode().getWorldTranslation().y;
        float dy = (touchDownY - touchY) / window.getScaleFactorHeight();
        float dis = FastMath.sqrt((float) (dy * dy + 0));
        float val = 0f;
                
        if (dis > HALFSIZE) {
            dis = HALFSIZE;
        }
        
        //Calculate value
        if (touchY < touchDownY) {
            val = HALFSIZE - dis;
            buttonImage.centerAt(0, -dis);

        } else if (touchY > touchDownY) {
            val = HALFSIZE + dis;
            buttonImage.centerAt(0, dis);
                
        } else {            
            buttonImage.center();
        }
        
        
        percentage = val/(HALFSIZE*2f);
        
        
        value = ((maxValue - minValue)*percentage) + (minValue);
//        Debug.log("Value = " + value);
        
        label.setText(labelText + String.format("%.2f", value));        
        
        if (valueChangeListeners.size() > 0) {
            for (int i = 0; i < valueChangeListeners.size(); i++) {
                ValueChangeListener valueChangeListener = valueChangeListeners.get(i);
                valueChangeListener.doValueChange(value);
            }
            
        }

    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
        this.label.setText(labelText + String.format("%.2f", value));
    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
        this.label.setText(labelText + String.format("%.2f", value));
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public float getIncrementValue() {
        return incrementValue;
    }

    public void setIncrementValue(float incrementValue) {
        this.incrementValue = incrementValue;
    }
    
    /**
     * Use this method to set the ValueChangeListener
     *
     * @param ValueChangeListener
     */
    public void addValueChangeListener(ValueChangeListener valueChangeListener) {
        this.valueChangeListeners.add(valueChangeListener);
    }

    public void removeValueChangeListener(ValueChangeListener valueChangeListener) {
        this.valueChangeListeners.remove(valueChangeListener);
    }
    
    public void clearValueChangeListeners() {
        this.valueChangeListeners.clear();
    }

    public Label getLabel() {
        return label;
    }
    
    public void setLabelColor(ColorRGBA colorRGBA) {
        label.setTextColor(colorRGBA);
    }
}
