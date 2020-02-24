/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import com.bruynhuis.galago.control.RotationControl;
import com.bruynhuis.galago.filters.ArtFilter;
import com.bruynhuis.galago.filters.BarrelBlurFilter;
import com.bruynhuis.galago.filters.BleachFilter;
import com.bruynhuis.galago.filters.BoxBlurFilter;
import com.bruynhuis.galago.filters.ChromaticAberrationFilter;
import com.bruynhuis.galago.filters.CircularBlurFilter;
import com.bruynhuis.galago.filters.CircularFadingFilter;
import com.bruynhuis.galago.filters.FXAAFilter;
import com.bruynhuis.galago.filters.FogFilter;
import com.bruynhuis.galago.filters.LightningFilter;
import com.bruynhuis.galago.filters.NoiseFilter;
import com.bruynhuis.galago.filters.OldVideoFilter;
import com.bruynhuis.galago.filters.ShockwaveFilter;
import com.bruynhuis.galago.filters.SimpleBloomFilter;
import com.bruynhuis.galago.filters.VignetteFilter;
import com.bruynhuis.galago.filters.ZoomBlurFilter;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.button.Checkbox;
import com.bruynhuis.galago.ui.field.HSlider;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.ValueChangeListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.panel.VPanel;
import com.bruynhuis.galago.util.SpatialUtils;
import com.bruynhuis.galago.util.Timer;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.CartoonEdgeFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.scene.Spatial;
import com.jme3.water.SimpleWaterProcessor;

/**
 *
 * @author NideBruyn
 */
public class PostShaderScreen extends AbstractScreen implements PickListener {

    private FilterPostProcessor fpp;
    private Label instructions;
    private Checkbox bloomCheckbox;
    private Checkbox shockwaveCheckbox;
    private Checkbox chromaticCheckbox;
    private Checkbox fxaaCheckbox;
    private Checkbox cartoonCheckbox;
    private Checkbox bleachCheckbox;
    private Checkbox noiseCheckbox;
    private Checkbox artCheckbox;
    private Checkbox ssaoCheckbox;
    private Checkbox lightningCheckbox;
    private Checkbox waterCheckbox;
    private Checkbox circularBlurCheckbox;
    private Checkbox boxBlurCheckbox;
    private Checkbox oldVideoCheckbox;
    private Checkbox circularFadingCheckbox;
    private Checkbox zoomBlurCheckbox;
    private Checkbox fogCheckbox;
    private Checkbox barrelBlurCheckbox;
    private Checkbox vignetteCheckbox;
    
    private HSlider fogDensitySlider;
    private HSlider fogStartSlider;
    private HSlider vignetteReductionSlider;
    private HSlider vignetteBoostSlider;
    
    private ShockwaveFilter shockwaveFilter;
    private ChromaticAberrationFilter chromaticAberrationFilter;
    private SimpleBloomFilter simpleBloomFilter;
    private FXAAFilter fXAAFilter;
    private CartoonEdgeFilter cartoonEdgeFilter;
    private BleachFilter bleachFilter;
    private NoiseFilter noiseFilter;
    private ArtFilter artFilter;
    private SSAOFilter basicSSAOFilter;
    private LightningFilter lightningFilter;
    private CircularBlurFilter circularBlurFilter;
    private BoxBlurFilter boxBlurFilter;
    private BarrelBlurFilter barrelBlurFilter;
    private OldVideoFilter oldVideoFilter;
    private CircularFadingFilter circularFadingFilter;
    private ZoomBlurFilter zoomBlurFilter;
    private FogFilter fogFilter;
    private VignetteFilter vignetteFilter;
    
    private SimpleWaterProcessor simpleWaterProcessor;
    private TouchPickListener touchPickListener;
    private Timer shockwaveTimer = new Timer(100);

    @Override
    protected void init() {
        instructions = new Label(hudPanel, "Post Process Filter", 26, 600, 60);
        instructions.centerTop(0, 0);
        instructions.setTextColor(ColorRGBA.LightGray);

        VPanel filtersPanel = new VPanel(hudPanel, "Resources/panel.png", 320, 700);
        filtersPanel.setTransparency(0.2f);
        hudPanel.add(filtersPanel);

        //Do the checkboxes

        chromaticCheckbox = addCheckbox(filtersPanel, "Chromatic Filter", new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    chromaticAberrationFilter.setEnabled(chromaticCheckbox.isChecked());

                }
            }
        });

        shockwaveCheckbox = addCheckbox(filtersPanel, "Shockwave Filter", new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    shockwaveFilter.setEnabled(shockwaveCheckbox.isChecked());

                }
            }
        });

        fxaaCheckbox = addCheckbox(filtersPanel, "FXAA Filter", new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    fXAAFilter.setEnabled(fxaaCheckbox.isChecked());

                }
            }
        });

        bloomCheckbox = addCheckbox(filtersPanel, "Bloom Filter", new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    simpleBloomFilter.setEnabled(bloomCheckbox.isChecked());

                }
            }
        });

        cartoonCheckbox = addCheckbox(filtersPanel, "Cartoon Edge Filter", new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    cartoonEdgeFilter.setEnabled(cartoonCheckbox.isChecked());

                }
            }
        });

        bleachCheckbox = addCheckbox(filtersPanel, "Bleach Filter", new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    bleachFilter.setEnabled(bleachCheckbox.isChecked());

                }
            }
        });

        noiseCheckbox = addCheckbox(filtersPanel, "Noise Filter", new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    noiseFilter.setEnabled(noiseCheckbox.isChecked());

                }
            }
        });

        artCheckbox = addCheckbox(filtersPanel, "Art Filter", new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    artFilter.setEnabled(artCheckbox.isChecked());

                }
            }
        });

        ssaoCheckbox = addCheckbox(filtersPanel, "SSAO Filter", new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    basicSSAOFilter.setEnabled(ssaoCheckbox.isChecked());

                }
            }
        });

        lightningCheckbox = addCheckbox(filtersPanel, "Lightning Filter", new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    lightningFilter.setEnabled(lightningCheckbox.isChecked());

                }
            }
        });

        waterCheckbox = addCheckbox(filtersPanel, "Simple Water", new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    if (waterCheckbox.isChecked()) {
                        simpleWaterProcessor = SpatialUtils.addSimpleWater(rootNode, new Vector3f(50f, 50, 50), 400, 0, 0.1f, true);

                    } else {
                        baseApplication.getViewPort().removeProcessor(simpleWaterProcessor);
                    }

                }
            }
        });
        
        circularBlurCheckbox = addCheckbox(filtersPanel, "Circular Blur", new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    circularBlurFilter.setEnabled(circularBlurCheckbox.isChecked());

                }
            }
        });
        
        boxBlurCheckbox = addCheckbox(filtersPanel, "Box Blur", new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    boxBlurFilter.setEnabled(boxBlurCheckbox.isChecked());

                }
            }
        });
        
        barrelBlurCheckbox = addCheckbox(filtersPanel, "Barrel Blur", new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    barrelBlurFilter.setEnabled(barrelBlurCheckbox.isChecked());

                }
            }
        });
        
        oldVideoCheckbox = addCheckbox(filtersPanel, "Old Video", new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    oldVideoFilter.setEnabled(oldVideoCheckbox.isChecked());

                }
            }
        });
        
        circularFadingCheckbox = addCheckbox(filtersPanel, "Circular Fading", new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    circularFadingFilter.setEnabled(circularFadingCheckbox.isChecked());

                }
            }
        });
        
        zoomBlurCheckbox = addCheckbox(filtersPanel, "Zoom Blur", new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    zoomBlurFilter.setEnabled(zoomBlurCheckbox.isChecked());

                }
            }
        });
        
        fogCheckbox = addCheckbox(filtersPanel, "Fog Filter", new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    fogFilter.setEnabled(fogCheckbox.isChecked());

                }
            }
        });
        
        vignetteCheckbox = addCheckbox(filtersPanel, "Vignette Filter", new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    vignetteFilter.setEnabled(vignetteCheckbox.isChecked());

                }
            }
        });

        touchPickListener = new TouchPickListener("my", camera, rootNode);
        touchPickListener.setPickListener(this);

        filtersPanel.rightTop(3, 3);
        filtersPanel.layout();
        
        
        
        VPanel slidersPanel = new VPanel(hudPanel, "Resources/panel.png", 320, 700);
        slidersPanel.setTransparency(0.2f);
        hudPanel.add(slidersPanel);
        
        //Add sliders
        fogDensitySlider = new HSlider(slidersPanel, 250);
        fogDensitySlider.setLabelText("Fog Density: ");
        fogDensitySlider.setMinValue(0);
        fogDensitySlider.setMaxValue(1);
        fogDensitySlider.addValueChangeListener(new ValueChangeListener() {

            public void doValueChange(float value) {
                fogFilter.setFogDensity(value);
            }
        });
        
        fogStartSlider = new HSlider(slidersPanel, 250);
        fogStartSlider.setLabelText("Fog Start Dist: ");
        fogStartSlider.setMinValue(10);
        fogStartSlider.setMaxValue(500);
        fogStartSlider.addValueChangeListener(new ValueChangeListener() {

            public void doValueChange(float value) {
                fogFilter.setFogStartDistance(value);
            }
        });
        
        vignetteReductionSlider = new HSlider(slidersPanel, 250);
        vignetteReductionSlider.setLabelText("Vignette Reduction: ");
        vignetteReductionSlider.setMinValue(0);
        vignetteReductionSlider.setMaxValue(3);
        vignetteReductionSlider.addValueChangeListener(new ValueChangeListener() {

            public void doValueChange(float value) {
                vignetteFilter.setReduction(value);
            }
        });
        
        vignetteBoostSlider = new HSlider(slidersPanel, 250);
        vignetteBoostSlider.setLabelText("Vignette Boost: ");
        vignetteBoostSlider.setMinValue(0);
        vignetteBoostSlider.setMaxValue(3);
        vignetteBoostSlider.addValueChangeListener(new ValueChangeListener() {

            public void doValueChange(float value) {
                vignetteFilter.setBoost(value);
            }
        });
        
        slidersPanel.leftCenter(0, 0);
        slidersPanel.layout();

    }

    private Checkbox addCheckbox(VPanel vPanel, String title, TouchButtonAdapter touchButtonAdapter) {
        Panel panel = new Panel(vPanel, null, 300, 36);
        vPanel.add(panel);

        Label label = new Label(panel, title, 18, 250, 36);
        label.setAlignment(TextAlign.LEFT);
        label.leftCenter(0, 0);

        Checkbox checkbox = new Checkbox(panel, title, 36, 36, false);
        checkbox.rightCenter(0, 0);
        checkbox.addTouchButtonListener(touchButtonAdapter);

        return checkbox;

    }

    @Override
    protected void load() {
        baseApplication.showStats();
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.White);


        SpatialUtils.addSkySphere(rootNode, 1, camera);
//        SpatialUtils.addSkyDome(rootNode, "Textures/skydome.jpg", camera);
        SpatialUtils.addSunLight(rootNode, ColorRGBA.White);

        Spatial floor = SpatialUtils.addBox(rootNode, 15, 1f, 15);
        SpatialUtils.addColor(floor, ColorRGBA.Gray, false);
        RigidBodyControl rbc = SpatialUtils.addMass(floor, 0);
        rbc.setRestitution(0);

        Spatial s = SpatialUtils.addCameraNode(rootNode, camera, 40, 8, 10);
        SpatialUtils.move(s, 0, 2, 0);
        s.addControl(new RotationControl(2));


        fpp = new FilterPostProcessor(assetManager);
        shockwaveFilter = new ShockwaveFilter();
        shockwaveFilter.setShockParams(new Vector3f(5, 0.2f, 0.2f));
        shockwaveFilter.setEnabled(false);

        fpp.addFilter(shockwaveFilter);
        chromaticAberrationFilter = new ChromaticAberrationFilter();
        chromaticAberrationFilter.setEnabled(false);
        fpp.addFilter(chromaticAberrationFilter);

        simpleBloomFilter = new SimpleBloomFilter();
//        simpleBloomFilter.setScale(0.2f);
//        simpleBloomFilter.setThreshold(1f);
        simpleBloomFilter.setEnabled(false);
        fpp.addFilter(simpleBloomFilter);

        fXAAFilter = new FXAAFilter();
        fXAAFilter.setEnabled(false);
        fpp.addFilter(fXAAFilter);

        cartoonEdgeFilter = new CartoonEdgeFilter();
        cartoonEdgeFilter.setEdgeIntensity(1F);
        cartoonEdgeFilter.setEdgeWidth(2);
        cartoonEdgeFilter.setDepthThreshold(0.2f);
        cartoonEdgeFilter.setEnabled(false);
        fpp.addFilter(cartoonEdgeFilter);

        bleachFilter = new BleachFilter();
        bleachFilter.setEnabled(false);
        fpp.addFilter(bleachFilter);

        noiseFilter = new NoiseFilter();
        noiseFilter.setEnabled(false);
        fpp.addFilter(noiseFilter);

        artFilter = new ArtFilter();
        artFilter.setEnabled(false);
        fpp.addFilter(artFilter);

        basicSSAOFilter = new SSAOFilter();
        basicSSAOFilter.setBias(0.2f);
        basicSSAOFilter.setIntensity(4);
        basicSSAOFilter.setSampleRadius(1);
        basicSSAOFilter.setScale(0.2f);
        basicSSAOFilter.setEnabled(false);
        fpp.addFilter(basicSSAOFilter);

        lightningFilter = new LightningFilter();
        lightningFilter.setEnabled(false);
        fpp.addFilter(lightningFilter);
        
        circularBlurFilter = new CircularBlurFilter();
        circularBlurFilter.setEnabled(false);
        fpp.addFilter(circularBlurFilter);
        
        boxBlurFilter = new BoxBlurFilter();
        boxBlurFilter.setEnabled(false);
        fpp.addFilter(boxBlurFilter);
        
        barrelBlurFilter = new BarrelBlurFilter();
        barrelBlurFilter.setAmount(1);
        barrelBlurFilter.setEnabled(false);
        fpp.addFilter(barrelBlurFilter);
        
        oldVideoFilter = new OldVideoFilter();
        oldVideoFilter.setEnabled(false);
        fpp.addFilter(oldVideoFilter);
        
        circularFadingFilter = new CircularFadingFilter();
        circularFadingFilter.setCircleRadius(0.4f);
        circularFadingFilter.setCircleCenter(new Vector3f(window.getWidthScaled()*0.5f, window.getHeightScaled()*0.5f, 0));
        circularFadingFilter.setEnabled(false);
        fpp.addFilter(circularFadingFilter);
        
        zoomBlurFilter = new ZoomBlurFilter();
        zoomBlurFilter.setStrength(0.001f);
        zoomBlurFilter.setCenter(new Vector2f(window.getWidthScaled()*0.5f, window.getHeightScaled()*0.5f));
        zoomBlurFilter.setEnabled(false);
        fpp.addFilter(zoomBlurFilter);
        
        fogFilter = new FogFilter();
        fogFilter.setFogColor(ColorRGBA.Red);
        fogFilter.setFogStartDistance(10);
        fogFilter.setFogMaxDistance(800);
        fogFilter.setEnabled(false);
        fpp.addFilter(fogFilter);
        
        vignetteFilter = new VignetteFilter();
        vignetteFilter.setEnabled(false);
        fpp.addFilter(vignetteFilter);

        baseApplication.getViewPort().addProcessor(fpp);

        touchPickListener.registerWithInput(inputManager);

    }

    @Override
    protected void show() {
        shockwaveTimer.start();
    }

    @Override
    protected void exit() {

        touchPickListener.unregisterInput();
        baseApplication.getViewPort().removeProcessor(fpp);
        rootNode.detachAllChildren();

    }

    @Override
    protected void pause() {
    }

    @Override
    public void update(float tpf) {
        if (isActive()) {
            shockwaveTimer.update(tpf);
            if (shockwaveTimer.finished()) {
                shockwaveFilter.doEffect(2.2f, new Vector2f(FastMath.nextRandomInt(0, (int) window.getWidth()), FastMath.nextRandomInt(0, (int) window.getHeight())));
                chromaticAberrationFilter.doEffect(0.3f, new Vector3f(0.03f, 0.0f, -0.03f));

                shockwaveTimer.setMaxTime(FastMath.nextRandomInt(50, 200));
                shockwaveTimer.reset();
            }
        }
    }

    public void picked(PickEvent pickEvent, float tpf) {
        if (isActive()) {
            if (!pickEvent.isKeyDown() && pickEvent.getContactObject() != null) {
                //Add a object
                float size = (FastMath.nextRandomInt(1, 10) * 0.1f) + 1f;
                Spatial s = SpatialUtils.addBox(rootNode, size, size, size);
                SpatialUtils.addTexture(s, "Textures/crate.jpg", false);
                float xPos = pickEvent.getContactPoint().x;
                float yPos = pickEvent.getContactPoint().y + 4f;
                float zPos = pickEvent.getContactPoint().z;
                SpatialUtils.translate(s, xPos, yPos, zPos);
                RigidBodyControl rbc = SpatialUtils.addMass(s, 1);
                rbc.setRestitution(0);

            }

        }
    }

    public void drag(PickEvent pickEvent, float tpf) {
        
        if (isActive() && circularFadingFilter.isEnabled()) {
            circularFadingFilter.setCircleCenter(new Vector3f(pickEvent.getCursorPosition().x, pickEvent.getCursorPosition().y, 0));
        }
    }
}
