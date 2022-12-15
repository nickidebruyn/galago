package com.galago.editor.ui.panels;

import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.ValueChangeListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.galago.editor.ui.ColorButton;
import com.galago.editor.ui.SliderField;
import com.galago.editor.ui.SpinnerButton;
import com.galago.editor.utils.MaterialUtils;
import com.jme3.math.ColorRGBA;
import com.jme3.water.WaterFilter;
import java.awt.Color;
import javax.swing.JColorChooser;

/**
 *
 * @author ndebruyn
 */
public class WaterPanel extends AbstractPropertiesPanel {

    private WaterFilter waterFilter;

    private SpinnerButton onOffButton;
    private SliderField heightField;
    private SliderField transparencyField;
    private SliderField waveScaleField;
    private SliderField shininessField;
    private SliderField speedField;
    private SliderField maxAmplitudeField;
    private SliderField foamHardnessField;
    private SliderField foamIntensityField;
    private SliderField shoreHardnessField;
    private SliderField causticsIntensityField;
    private SliderField reflectionDisplacementField;
    private ColorButton waterColorButton;
    private ColorButton deepWaterColorButton;
    private SliderField refractionStrengthField;

    public WaterPanel(Panel parent) {
        super(parent, "water");
    }

    @Override
    protected void init() {

        createHeader("water", "Water Settings");

        onOffButton = createLabeledSpinner("Enabled", "water-enabled", new String[]{"Off", "On"});
        onOffButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                System.out.println("Selected water enabled: " + onOffButton.getIndex());

                waterFilter.setEnabled(onOffButton.getIndex() == 1);

                reload();
            }
        });

        heightField = createLabeledSliderDecimal("Height", -10, 50, 0.1f);
        heightField.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void doValueChange(float value) {
                waterFilter.setWaterHeight(value);

            }

        });

        transparencyField = createLabeledSliderDecimal("Opacity", 0, 1, 0.1f);
        transparencyField.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void doValueChange(float value) {
                waterFilter.setWaterTransparency(value);

            }

        });

        waveScaleField = createLabeledSliderDecimal("Wave scale", 0, 0.01f, 0.001f);
        waveScaleField.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void doValueChange(float value) {
                waterFilter.setWaveScale(value);
            }
        });

        shininessField = createLabeledSliderDecimal("Shininess", 0, 2, 0.1f);
        shininessField.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void doValueChange(float value) {
                waterFilter.setShininess(value);
            }
        });

        speedField = createLabeledSliderDecimal("Speed", 0, 2, 0.01f);
        speedField.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void doValueChange(float value) {
                waterFilter.setSpeed(value);
            }
        });

        maxAmplitudeField = createLabeledSliderDecimal("Amplitude", 0, 2, 0.01f);
        maxAmplitudeField.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void doValueChange(float value) {
                waterFilter.setMaxAmplitude(value);
            }
        });

        foamHardnessField = createLabeledSliderDecimal("Foam hardness", 0, 2, 0.01f);
        foamHardnessField.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void doValueChange(float value) {
                waterFilter.setFoamHardness(value);
            }
        });

        foamIntensityField = createLabeledSliderDecimal("Foam intensity", 0, 2, 0.01f);
        foamIntensityField.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void doValueChange(float value) {
                waterFilter.setFoamIntensity(value);
            }
        });

        shoreHardnessField = createLabeledSliderDecimal("Shore hardness", 0, 2, 0.01f);
        shoreHardnessField.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void doValueChange(float value) {
                waterFilter.setShoreHardness(value);
            }
        });
        
        causticsIntensityField = createLabeledSliderDecimal("Caustics", 0, 1, 0.1f);
        causticsIntensityField.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void doValueChange(float value) {
                waterFilter.setCausticsIntensity(value);
            }
        });
        
        reflectionDisplacementField = createLabeledSliderDecimal("Reflection blur", 0, 50, 1f);
        reflectionDisplacementField.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void doValueChange(float value) {
                waterFilter.setReflectionDisplace(value);
            }
        });
        
        refractionStrengthField = createLabeledSliderDecimal("Refraction Strength", 0, 1, 0.1f);
        refractionStrengthField.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void doValueChange(float value) {
                waterFilter.setRefractionStrength(value);
            }
        });

        waterColorButton = createLabeledColorButton("Color", "color-button");
        waterColorButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                Color currentColor = MaterialUtils.convertColor(waterFilter.getWaterColor());
                Color newColor = JColorChooser.showDialog(null, "Choose a color", currentColor);
                System.out.println("Color: " + newColor);
                if (newColor != null) {
                    ColorRGBA colorRGBA = MaterialUtils.convertColor(newColor);
                    waterFilter.setWaterColor(colorRGBA);
                    waterColorButton.setColor(colorRGBA);
                }

            }

        });
        
        deepWaterColorButton = createLabeledColorButton("Deep Color", "deep-color-button");
        deepWaterColorButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                Color currentColor = MaterialUtils.convertColor(waterFilter.getDeepWaterColor());
                Color newColor = JColorChooser.showDialog(null, "Choose a color", currentColor);
                System.out.println("Color: " + newColor);
                if (newColor != null) {
                    ColorRGBA colorRGBA = MaterialUtils.convertColor(newColor);
                    waterFilter.setDeepWaterColor(colorRGBA);
                    deepWaterColorButton.setColor(colorRGBA);
                }

            }

        });

    }

    @Override
    protected void reload() {

        boolean enabled = waterFilter != null && waterFilter.isEnabled();

        heightField.getParent().setVisible(enabled);
        transparencyField.getParent().setVisible(enabled);
        waveScaleField.getParent().setVisible(enabled);
        shininessField.getParent().setVisible(enabled);
        speedField.getParent().setVisible(enabled);
        maxAmplitudeField.getParent().setVisible(enabled);
        foamHardnessField.getParent().setVisible(enabled);
        foamIntensityField.getParent().setVisible(enabled);
        shoreHardnessField.getParent().setVisible(enabled);
        causticsIntensityField.getParent().setVisible(enabled);
        reflectionDisplacementField.getParent().setVisible(enabled);
        refractionStrengthField.getParent().setVisible(enabled);
        waterColorButton.getParent().setVisible(enabled);
        deepWaterColorButton.getParent().setVisible(enabled);

        onOffButton.setSelection(enabled ? 1 : 0);

        if (enabled) {
            heightField.setValue(waterFilter.getWaterHeight());
            transparencyField.setValue(waterFilter.getWaterTransparency());
            waveScaleField.setValue(waterFilter.getWaveScale());
            shininessField.setValue(waterFilter.getShininess());
            speedField.setValue(waterFilter.getSpeed());
            maxAmplitudeField.setValue(waterFilter.getMaxAmplitude());
            foamHardnessField.setValue(waterFilter.getFoamHardness());
            foamIntensityField.setValue(waterFilter.getFoamIntensity());
            shoreHardnessField.setValue(waterFilter.getShoreHardness());
            causticsIntensityField.setValue(waterFilter.getCausticsIntensity());
            reflectionDisplacementField.setValue(waterFilter.getReflectionDisplace());
            refractionStrengthField.setValue(waterFilter.getRefractionStrength());
            waterColorButton.setColor(waterFilter.getWaterColor());
            deepWaterColorButton.setColor(waterFilter.getDeepWaterColor());

        }

    }

    public WaterFilter getWaterFilter() {
        return waterFilter;
    }

    public void setWaterFilter(WaterFilter waterFilter) {
        this.waterFilter = waterFilter;

        this.reload();
    }

}
