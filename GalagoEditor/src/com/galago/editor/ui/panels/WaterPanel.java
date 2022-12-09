package com.galago.editor.ui.panels;

import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.ValueChangeListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.galago.editor.ui.SliderField;
import com.galago.editor.ui.SpinnerButton;
import com.galago.editor.ui.actions.TerrainAction;
import com.jme3.water.WaterFilter;

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

        heightField = createLabeledSliderDecimal("Height", -10, 10, 0.1f);
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
