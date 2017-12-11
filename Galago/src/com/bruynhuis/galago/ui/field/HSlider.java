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
public class HSlider extends Panel {

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
    private float NATIVE_WIDTH = 264;
    private float PADDING = 10f;
    private float HALFSIZE = (NATIVE_WIDTH*0.5f)-PADDING; //This is half the amount

    public HSlider(Panel parent, float width) {
        super(parent, "Resources/hslider.png", width, 54, true);
        float scaling = width/NATIVE_WIDTH;
        NATIVE_WIDTH = width;
        HALFSIZE = (NATIVE_WIDTH*0.5f)-PADDING;
        
        label = new Label(this, "0", 14, NATIVE_WIDTH, 16);
        label.setTextColor(ColorRGBA.LightGray);
        label.centerTop(0, -16);
        
        buttonImage = new Image(this, "Resources/button-slider.png", 24*scaling, 54*scaling, true);
        buttonImage.center();

        touchButton = new ControlButton(this, "slider-button", NATIVE_WIDTH, 54, true);
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
                    calculateNewPosition(window.getInputManager().getCursorPosition().x);
                }
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });

        parent.add(this);
    }

    private void calculateNewPosition(float touchX) {
        float touchDownX = getWidgetNode().getWorldTranslation().x;
        float dx = (touchDownX - touchX) / window.getScaleFactorWidth();
        float dis = FastMath.sqrt((float) (dx * dx + 0));
        float val = 0f;
        
        if (dis > HALFSIZE) {
            dis = HALFSIZE;
        }
        
        //Calculate value
        if (touchX < touchDownX) {
            val = HALFSIZE - dis;
            buttonImage.centerAt(-dis, 0);

        } else if (touchX > touchDownX) {
            val = HALFSIZE + dis;
            buttonImage.centerAt(dis, 0);
                
        } else {            
            buttonImage.center();
        }
        
        percentage = val/(HALFSIZE*2f);
        
//        value =        
        
        
        value = ((maxValue - minValue)*percentage) + (minValue);
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
