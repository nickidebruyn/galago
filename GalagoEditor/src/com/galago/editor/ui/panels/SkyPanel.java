package com.galago.editor.ui.panels;

import com.bruynhuis.galago.ui.listener.KeyboardListener;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.ValueChangeListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.galago.editor.ui.ColorButton;
import com.galago.editor.ui.FloatField;
import com.galago.editor.ui.LongField;
import com.galago.editor.ui.SliderField;
import com.galago.editor.ui.SpinnerButton;
import com.galago.editor.utils.Action;
import com.galago.editor.utils.MaterialUtils;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;
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
    private DirectionalLightShadowFilter shadowFilter;

    private SpinnerButton ambientLightOnOffButton;
    private SpinnerButton sunLightOnOffButton;
    private SpinnerButton environmentLightOnOffButton;

    private SpinnerButton skyOnOffButton;
    private ColorButton topColorButton;
    private ColorButton bottomColorButton;
    private SliderField minField;
    private SliderField maxField;

    private ColorButton ambientColorButton;
    private ColorButton sunColorButton;

    private SpinnerButton shadowsOnOffButton;
    private SpinnerButton shadowEdgeFilterSpinner;
    private LongField shadowEdgeThickness;
    private FloatField shadowIntensity;
    private FloatField shadowZExtend;
    private FloatField shadowZFadeLength;

    public SkyPanel(Panel parent) {
        super(parent, "sky");
    }

    @Override
    protected void init() {

        verticalSpacing = 30;

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
        ambientColorButton = createLabeledColorButton("Color", "ambient-color-button");
        ambientColorButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                Color currentColor = MaterialUtils.convertColor(ambientLight.getColor());
                Color newColor = JColorChooser.showDialog(null, "Choose a color", currentColor);
                System.out.println("Color: " + newColor);
                if (newColor != null) {
                    ColorRGBA colorRGBA = MaterialUtils.convertColor(newColor);
                    ambientLight.setColor(colorRGBA);
                    ambientColorButton.setColor(colorRGBA);
                }

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
        sunColorButton = createLabeledColorButton("Color", "sun-color-button");
        sunColorButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                Color currentColor = MaterialUtils.convertColor(sunLight.getColor());
                Color newColor = JColorChooser.showDialog(null, "Choose a color", currentColor);
                System.out.println("Color: " + newColor);
                if (newColor != null) {
                    ColorRGBA colorRGBA = MaterialUtils.convertColor(newColor);
                    sunLight.setColor(colorRGBA);
                    sunColorButton.setColor(colorRGBA);
                }

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

        createHeader("env-shadows", "Shadows");

        shadowsOnOffButton = createLabeledSpinner("Enabled", "shadows-enabled", new String[]{"Off", "On"});
        shadowsOnOffButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                System.out.println("Selected shadows enabled: " + shadowsOnOffButton.getIndex());
                shadowFilter.setEnabled(!shadowFilter.isEnabled());
                reload();
            }
        });

        shadowEdgeFilterSpinner = createLabeledSpinner("Edge mode", "edge-mode", new String[]{"Nearest", "Bilinear", "Dither", "PCF4", "PCFPOISSON", "PCF8"});
        shadowEdgeFilterSpinner.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (shadowFilter != null) {
                    if (shadowEdgeFilterSpinner.getIndex() == 0) {
                        shadowFilter.setEdgeFilteringMode(EdgeFilteringMode.Nearest);

                    } else if (shadowEdgeFilterSpinner.getIndex() == 1) {
                        shadowFilter.setEdgeFilteringMode(EdgeFilteringMode.Bilinear);

                    } else if (shadowEdgeFilterSpinner.getIndex() == 2) {
                        shadowFilter.setEdgeFilteringMode(EdgeFilteringMode.Dither);

                    } else if (shadowEdgeFilterSpinner.getIndex() == 3) {
                        shadowFilter.setEdgeFilteringMode(EdgeFilteringMode.PCF4);

                    } else if (shadowEdgeFilterSpinner.getIndex() == 4) {
                        shadowFilter.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);

                    } else if (shadowEdgeFilterSpinner.getIndex() == 5) {
                        shadowFilter.setEdgeFilteringMode(EdgeFilteringMode.PCF8);

                    }
                }

                //reload();
            }
        });
        
        shadowEdgeThickness = createLabeledLongInput("Thickness", "shadow-thickness");
        shadowEdgeThickness.addKeyboardListener(new KeyboardListener() {
            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                shadowFilter.setEdgesThickness((int)shadowEdgeThickness.getValue());
            }
            
        });
        
        shadowIntensity = createLabeledFloatInput("Intensity", "shadow-intensity");
        shadowIntensity.addKeyboardListener(new KeyboardListener() {
            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                shadowFilter.setShadowIntensity(shadowIntensity.getValue());
            }
            
        });
        
        shadowZExtend = createLabeledFloatInput("Depth", "shadow-depth");
        shadowZExtend.addKeyboardListener(new KeyboardListener() {
            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                shadowFilter.setShadowZExtend(shadowZExtend.getValue());
            }
            
        });
        
        shadowZFadeLength = createLabeledFloatInput("Fade length", "shadow-fade");
        shadowZFadeLength.addKeyboardListener(new KeyboardListener() {
            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                shadowFilter.setShadowZFadeLength(shadowZFadeLength.getValue());
            }
            
        });

    }

    @Override
    protected void reload() {

        //Set the light values
        sunLightOnOffButton.setSelection(sunLight != null && sunLight.isEnabled() ? 1 : 0);

        if (sunLightOnOffButton.getIndex() == 1) {
            sunColorButton.setColor(sunLight.getColor());
        }

        ambientLightOnOffButton.setSelection(ambientLight != null && ambientLight.isEnabled() ? 1 : 0);

        if (ambientLightOnOffButton.getIndex() == 1) {
            ambientColorButton.setColor(ambientLight.getColor());
        }

        environmentLightOnOffButton.setSelection(lightProbe != null && lightProbe.isEnabled() ? 1 : 0);

        //Set the sky settings
        boolean enabled = sky != null && sky.getParent() != null;

        topColorButton.getParent().setVisible(enabled);
        bottomColorButton.getParent().setVisible(enabled);
        minField.getParent().setVisible(enabled);
        maxField.getParent().setVisible(enabled);

        skyOnOffButton.setSelection(enabled ? 1 : 0);

        //Only if sky is enabled
        if (enabled) {
            topColorButton.setColor((ColorRGBA) skyMaterial.getParamValue(TOP_COLOR));
            bottomColorButton.setColor((ColorRGBA) skyMaterial.getParamValue(BOTTOM_COLOR));
            minField.setValue(skyMaterial.getParamValue(MIN_STEP));
            maxField.setValue(skyMaterial.getParamValue(MAX_STEP));

        }

        shadowsOnOffButton.setSelection(shadowFilter != null && shadowFilter.isEnabled() ? 1 : 0);

        //Do this only of shadows are enabled
        if (shadowFilter != null && shadowFilter.isEnabled()) {
            //TODO: Show the shadow options

            //"Nearest", "Bilinear", "Dither", "PCF4", "PCFPOISSON", "PCF8"
            if (shadowFilter.getEdgeFilteringMode().equals(EdgeFilteringMode.Nearest)) {
                shadowEdgeFilterSpinner.setSelection(0);

            } else if (shadowFilter.getEdgeFilteringMode().equals(EdgeFilteringMode.Bilinear)) {
                shadowEdgeFilterSpinner.setSelection(1);

            } else if (shadowFilter.getEdgeFilteringMode().equals(EdgeFilteringMode.Dither)) {
                shadowEdgeFilterSpinner.setSelection(2);

            } else if (shadowFilter.getEdgeFilteringMode().equals(EdgeFilteringMode.PCF4)) {
                shadowEdgeFilterSpinner.setSelection(3);

            } else if (shadowFilter.getEdgeFilteringMode().equals(EdgeFilteringMode.PCFPOISSON)) {
                shadowEdgeFilterSpinner.setSelection(4);

            } else if (shadowFilter.getEdgeFilteringMode().equals(EdgeFilteringMode.PCF8)) {
                shadowEdgeFilterSpinner.setSelection(5);

            }

            shadowEdgeThickness.setValue(shadowFilter.getEdgesThickness());
            shadowIntensity.setValue(shadowFilter.getShadowIntensity());
            shadowZExtend.setValue(shadowFilter.getShadowZExtend());
            shadowZFadeLength.setValue(shadowFilter.getShadowZFadeLength());
        }

        //set some field visibility
        shadowEdgeFilterSpinner.getParent().setVisible(shadowFilter != null && shadowFilter.isEnabled());
        shadowEdgeThickness.getParent().setVisible(shadowFilter != null && shadowFilter.isEnabled());
        shadowIntensity.getParent().setVisible(shadowFilter != null && shadowFilter.isEnabled());        
        shadowZExtend.getParent().setVisible(shadowFilter != null && shadowFilter.isEnabled());
        shadowZFadeLength.getParent().setVisible(shadowFilter != null && shadowFilter.isEnabled());

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

    public void setShadowFilter(DirectionalLightShadowFilter dlsf) {
        this.shadowFilter = dlsf;
        this.reload();
    }

}
