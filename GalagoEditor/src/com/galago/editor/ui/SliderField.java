package com.galago.editor.ui;

import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.button.ControlButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
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
 * @author ndebruyn
 */
public class SliderField extends Panel {

    protected float value = 0f;
    protected float minValue = 0f;
    protected float maxValue = 100f;
    protected float incrementValue = 1f;
    protected boolean decimals = true;
    protected Image buttonImage;
    protected Label label;
    protected ControlButton touchButton;
    private String labelText = "";
    private boolean calculateValue = false;
    private List<ValueChangeListener> valueChangeListeners = new ArrayList<ValueChangeListener>();
    private List<TouchButtonListener> touchButtonListener = new ArrayList<TouchButtonListener>();

    private float percentage = 0f;
    private float NATIVE_WIDTH = 264;
    protected float PADDING = 2f;
    private float HALFSIZE = (NATIVE_WIDTH * 0.5f) - PADDING; //This is half the amount

    public SliderField(Panel parent, float min, float max, float increment) {
        this(parent, "Interface/slider.png", "Interface/slider-button.png", 128, 38);

        setMinValue(min);
        setMaxValue(max);
        setIncrementValue(increment);

        label.centerTop(0, -4);
        label.setFontSize(13);

    }

    public SliderField(Panel parent, String sliderImage, String buttonImagefile, float width, float height) {
        super(parent, sliderImage, width, height, true);
        float scaling = width / NATIVE_WIDTH;
        NATIVE_WIDTH = width;
        HALFSIZE = (NATIVE_WIDTH * 0.5f) - PADDING;

        label = new Label(this, "0", 14, NATIVE_WIDTH, 16);
        label.setTextColor(ColorRGBA.LightGray);
        label.centerTop(0, -16);

        buttonImage = new Image(this, buttonImagefile, height * scaling, height * scaling, true);
        buttonImage.center();

        touchButton = new ControlButton(this, "slider-button", NATIVE_WIDTH, height, true);
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

        percentage = val / (HALFSIZE * 2f);

//        value =        
        value = ((maxValue - minValue) * percentage) + (minValue);
//        label.setText(labelText + String.format("%.2f", value));
//        setLabelText(labelText);
        setValue(value);

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
        if (decimals) {
            this.label.setText(labelText + String.format("%.4f", value));
        } else {
            this.label.setText((int) value + "");
        }
        
        float per = ((value-minValue) / (maxValue-minValue)) * NATIVE_WIDTH;
        
        System.out.println("Val = " + per);
        
        buttonImage.leftCenter(per-10, 0);

    }

    public void setLabelText(String labelText) {
        this.labelText = labelText;
        if (decimals) {
            this.label.setText(labelText + String.format("%.4f", value));
        } else {
            this.label.setText((int) value + "");
        }
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

    public void addTouchButtonListener(TouchButtonListener buttonListener) {
        this.touchButton.addTouchButtonListener(buttonListener);
    }

    public void removeTouchButtonListener(TouchButtonListener buttonListener) {
        this.touchButton.removeTouchButtonListener(buttonListener);
    }

    public Label getLabel() {
        return label;
    }

    public void setLabelColor(ColorRGBA colorRGBA) {
        label.setTextColor(colorRGBA);
    }

    public boolean isDecimals() {
        return decimals;
    }

    public void setDecimals(boolean decimals) {
        this.decimals = decimals;
    }

}
