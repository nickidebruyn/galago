package com.galago.editor.ui.panels;

import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.ValueChangeListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.galago.editor.ui.ColorButton;
import com.galago.editor.ui.SliderField;
import com.galago.editor.ui.SpinnerButton;
import com.galago.editor.utils.Action;
import com.galago.editor.utils.MaterialUtils;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import java.awt.Color;
import javax.swing.JColorChooser;

/**
 *
 * @author ndebruyn
 */
public class SkyPanel extends AbstractPropertiesPanel {

    private static final String TOP_COLOR = "StartColor";
    private static final String BOTTOM_COLOR = "EndColor";
    private static final String MIN_STEP = "MinStep";
    private static final String MAX_STEP = "MaxStep";

    private Geometry sky;
    private Material skyMaterial;
    private AmbientLight ambientLight;
    private DirectionalLight sunLight;
    private LightProbe lightProbe;
    
    private SpinnerButton ambientLightOnOffButton;
    private SpinnerButton sunLightOnOffButton;
    private SpinnerButton environmentLightOnOffButton;

    private SpinnerButton skyOnOffButton;
    private ColorButton topColorButton;
    private ColorButton bottomColorButton;
    private SliderField minField;
    private SliderField maxField;

    public SkyPanel(Panel parent) {
        super(parent, "sky");
    }

    @Override
    protected void init() {
        
        createHeader("ambient-light", "Global Light");

        ambientLightOnOffButton = createLabeledSpinner("Enabled", "ambient-enabled", new String[]{"Off", "On"});
        ambientLightOnOffButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                System.out.println("Selected ambient enabled: " + ambientLightOnOffButton.getIndex());
                //window.getApplication().getMessageManager().sendMessage(Action.ADD_AMBIENT, null);
                ambientLight.setEnabled(!ambientLight.isEnabled());
                reload();
            }
        });
        
        createHeader("sun-light", "Sun Light");

        sunLightOnOffButton = createLabeledSpinner("Enabled", "sun-enabled", new String[]{"Off", "On"});
        sunLightOnOffButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                System.out.println("Selected sun enabled: " + sunLightOnOffButton.getIndex());
//                window.getApplication().getMessageManager().sendMessage(Action.ADD_SUN, null);
                sunLight.setEnabled(!sunLight.isEnabled());
                reload();
            }
        });
        
        createHeader("environment-light", "Environment Light");

        environmentLightOnOffButton = createLabeledSpinner("Enabled", "environment-enabled", new String[]{"Off", "On"});
        environmentLightOnOffButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                System.out.println("Selected environment enabled: " + environmentLightOnOffButton.getIndex());
//                window.getApplication().getMessageManager().sendMessage(Action.ADD_SUN, null);
                lightProbe.setEnabled(!lightProbe.isEnabled());
                reload();
            }
        });        

        createHeader("sky", "Sky Settings");

        skyOnOffButton = createLabeledSpinner("Enabled", "sky-enabled", new String[]{"Off", "On"});
        skyOnOffButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                System.out.println("Selected sky enabled: " + skyOnOffButton.getIndex());
                window.getApplication().getMessageManager().sendMessage(Action.ADD_SKY, null);
                reload();
            }
        });

        topColorButton = createLabeledColorButton("Top Color", "top-color-button");
        topColorButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                Color currentColor = MaterialUtils.convertColor((ColorRGBA) skyMaterial.getParamValue(TOP_COLOR));
                Color newColor = JColorChooser.showDialog(null, "Choose a color", currentColor);
                System.out.println("Color: " + newColor);
                if (newColor != null) {
                    ColorRGBA colorRGBA = MaterialUtils.convertColor(newColor);
                    skyMaterial.setColor(TOP_COLOR, colorRGBA);
                    topColorButton.setColor(colorRGBA);
                }

            }

        });

        bottomColorButton = createLabeledColorButton("Bottom Color", "bottom-color-button");
        bottomColorButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                Color currentColor = MaterialUtils.convertColor((ColorRGBA) skyMaterial.getParamValue(BOTTOM_COLOR));
                Color newColor = JColorChooser.showDialog(null, "Choose a color", currentColor);
                System.out.println("Color: " + newColor);

                if (newColor != null) {
                    ColorRGBA colorRGBA = MaterialUtils.convertColor(newColor);
                    skyMaterial.setColor(BOTTOM_COLOR, colorRGBA);
                    bottomColorButton.setColor(colorRGBA);
                }

            }

        });

        minField = createLabeledSliderDecimal("Min", 0, 1, 0.1f);
        minField.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void doValueChange(float value) {
                skyMaterial.setFloat(MIN_STEP, value);

            }

        });

        maxField = createLabeledSliderDecimal("Max", 0, 1, 0.1f);
        maxField.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void doValueChange(float value) {
                skyMaterial.setFloat(MAX_STEP, value);

            }

        });

    }

    @Override
    protected void reload() {
        
        //Set the light values
        sunLightOnOffButton.setSelection(sunLight != null && sunLight.isEnabled() ? 1 : 0);
        ambientLightOnOffButton.setSelection(ambientLight != null && ambientLight.isEnabled() ? 1 : 0);
        environmentLightOnOffButton.setSelection(lightProbe != null && lightProbe.isEnabled() ? 1 : 0);

        //Set the sky settings
        boolean enabled = sky != null && sky.getParent() != null;

        topColorButton.getParent().setVisible(enabled);
        bottomColorButton.getParent().setVisible(enabled);
        minField.getParent().setVisible(enabled);
        maxField.getParent().setVisible(enabled);

        skyOnOffButton.setSelection(enabled ? 1 : 0);

        if (enabled) {
            topColorButton.setColor((ColorRGBA) skyMaterial.getParamValue(TOP_COLOR));
            bottomColorButton.setColor((ColorRGBA) skyMaterial.getParamValue(BOTTOM_COLOR));
            minField.setValue(skyMaterial.getParamValue(MIN_STEP));
            maxField.setValue(skyMaterial.getParamValue(MAX_STEP));

        }

    }

    public Geometry getSky() {
        return sky;
    }

    public void setSky(Geometry sky) {
        this.sky = sky;

        if (this.sky != null) {
            this.skyMaterial = this.sky.getMaterial();
        } else {
            this.skyMaterial = null;
        }

        this.reload();
    }

    public AmbientLight getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(AmbientLight ambientLight) {
        this.ambientLight = ambientLight;
        this.reload();
    }

    public DirectionalLight getSunLight() {
        return sunLight;
    }

    public void setSunLight(DirectionalLight sunLight) {
        this.sunLight = sunLight;
        this.reload();
    }

    public LightProbe getLightProbe() {
        return lightProbe;
    }

    public void setLightProbe(LightProbe lightProbe) {
        this.lightProbe = lightProbe;
        this.reload();
    }

}
