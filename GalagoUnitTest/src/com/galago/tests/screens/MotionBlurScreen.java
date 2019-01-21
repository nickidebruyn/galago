/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.bruynhuis.galago.control.RotationControl;
import com.bruynhuis.galago.control.tween.SpatialAccessor;
import com.bruynhuis.galago.filters.BarrelBlurFilter;
import com.bruynhuis.galago.filters.FXAAFilter;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.field.HSlider;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.ValueChangeListener;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.GammaCorrectionFilter;
import com.jme3.scene.Spatial;

/**
 *
 * @author nidebruyn
 */
public class MotionBlurScreen extends AbstractScreen {

    private Spatial box;
    private FilterPostProcessor fpp;
    private HSlider gammaSlider;
    private HSlider blurSlider;
    private GammaCorrectionFilter gammaFilter;
    private BarrelBlurFilter barrelBlur;
    
    @Override
    protected void init() {
        TouchButton button = new TouchButton(hudPanel, "button_motion", "Back");
        button.centerBottom(0, 0);
        button.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                showPreviousScreen();
            }
        });
        
        gammaSlider = new HSlider(hudPanel, 250);
        gammaSlider.leftBottom(0, 0);
        gammaSlider.setLabelText("Gamma: ");
        gammaSlider.setMinValue(0);
        gammaSlider.setMaxValue(1);
        gammaSlider.addValueChangeListener(new ValueChangeListener() {

            public void doValueChange(float value) {
                gammaFilter.setGamma(value);
            }
        });
        
        blurSlider = new HSlider(hudPanel, 250);
        blurSlider.leftBottom(0, 80);
        blurSlider.setLabelText("Blur: ");
        blurSlider.setMinValue(0);
        blurSlider.setMaxValue(50);
        blurSlider.addValueChangeListener(new ValueChangeListener() {

            public void doValueChange(float value) {
                barrelBlur.setAmount(value);
            }
        });
    }

    @Override
    protected void load() {
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.Black);
        
        SpatialUtils.addSkySphere(rootNode, 1, camera);
        SpatialUtils.addSunLight(rootNode, ColorRGBA.White);

        box = SpatialUtils.addBox(rootNode, 1, 1, 1);
        SpatialUtils.addColor(box, ColorRGBA.randomColor(), false);
        box.addControl(new RotationControl(new Vector3f(100, 100, 100)));
        
        fpp = new FilterPostProcessor(assetManager);
        baseApplication.getViewPort().addProcessor(fpp);
        
        barrelBlur = new BarrelBlurFilter();        
        fpp.addFilter(barrelBlur);
        
        FXAAFilter fXAAFilter = new FXAAFilter();        
        fpp.addFilter(fXAAFilter);
        
        gammaFilter = new GammaCorrectionFilter(0);
        fpp.addFilter(gammaFilter);

    }

    @Override
    protected void show() {
        camera.setLocation(new Vector3f(0, 0, 20));
        camera.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);

        doMovement(FastMath.nextRandomInt(-4, 4), FastMath.nextRandomInt(-3, 3));
    }
    
    private void doMovement(float targetX, float targetY) {
        Tween.to(box, SpatialAccessor.POS_XYZ, 1)
                .target(targetX, targetY, 0)
                .delay(0.2f)
                .setCallback(new TweenCallback() {

            public void onEvent(int i, BaseTween<?> bt) {
                doMovement(FastMath.nextRandomInt(-5, 5), FastMath.nextRandomInt(-4, 4));
            }
        }).start(baseApplication.getTweenManager());
        
    }

    @Override
    protected void exit() {
        baseApplication.getViewPort().removeProcessor(fpp);
        rootNode.detachAllChildren();

    }

    @Override
    protected void pause() {
    }
}
