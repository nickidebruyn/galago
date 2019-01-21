/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import com.bruynhuis.galago.control.effects.LineControl;
import com.bruynhuis.galago.control.effects.TrailControl;
import com.bruynhuis.galago.control.tween.SpatialAccessor;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.field.HSlider;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.ValueChangeListener;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author nidebruyn
 */
public class TrailRenderScreen extends AbstractScreen implements PickListener {

    private Node target;
    private PointLight pointLight;
    private float width = 0.5f;
    private float endWidth = 0.03f;
    private float lifetime = 0.5f;
    private float widthFactor = 1f;
    private FilterPostProcessor fpp;
    private TouchPickListener touchPickListener;
    private float depth = -7f;
    private Vector3f targetPosition = new Vector3f(0, 0, 0);
    private HSlider speedSlider;
    private float speed = 0.5f;

    @Override
    protected void init() {
        TouchButton button = new TouchButton(hudPanel, "button_trail", "Back");
        button.centerBottom(0, 0);
        button.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                showPreviousScreen();
            }
        });

        speedSlider = new HSlider(hudPanel, 250);
        speedSlider.leftBottom(0, 0);
        speedSlider.setLabelText("Speed: ");
        speedSlider.setMinValue(0);
        speedSlider.setMaxValue(1);
        speedSlider.addValueChangeListener(new ValueChangeListener() {
            public void doValueChange(float value) {
                speed = value;
            }
        });

        touchPickListener = new TouchPickListener(camera, rootNode);
        touchPickListener.setPickListener(this);
    }

    @Override
    protected void load() {
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.Black);

        SpatialUtils.addSunLight(rootNode, ColorRGBA.White);        

        pointLight = new PointLight();
        pointLight.setColor(ColorRGBA.Green);
        pointLight.setRadius(4f);
        rootNode.addLight(pointLight);  

        Spatial wall = SpatialUtils.addBox(rootNode, 10, 6, 10);
        wall.move(0, 0, 0);
        Material mat = SpatialUtils.addTexture(wall, "Textures/floor.jpg", false);
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        ((Geometry) wall).getMesh().scaleTextureCoordinates(new Vector2f(5, 5));

        target = new Node("target");
        target.move(0, 0, depth);
        rootNode.attachChild(target);

        Spatial ball = SpatialUtils.addSphere(target, 30, 30, 0.3f);
        SpatialUtils.addColor(ball, new ColorRGBA(1.0f, 0.0f, 0.8f, 1.0f), false);

//        Material trailMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        trailMat.setColor("Color", ColorRGBA.White);
//        trailMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        //mat.getAdditionalRenderState().setWireframe(true);

        Material trailMat = assetManager.loadMaterial("Materials/trail.j3m");

        Geometry trailGeometry = new Geometry();
        trailMat.getAdditionalRenderState().setAlphaTest(true);
        trailMat.getAdditionalRenderState().setAlphaFallOff(0.5f);
        trailGeometry.setMaterial(trailMat);
        //rootNode.attachChild(trail);  // either attach the trail geometry node to the root…
        trailGeometry.setIgnoreTransform(true); // or set ignore transform to true. this should be most useful when attaching nodes in the editor
        //trailGeometry.setQueueBucket(RenderQueue.Bucket.Translucent);

        LineControl line = new LineControl(new LineControl.Algo2CamPosBBNormalized(), true);
        trailGeometry.addControl(line);
        TrailControl trailControl = new TrailControl(line);
        target.addControl(trailControl);
        trailControl.setStartWidth(this.endWidth * this.widthFactor);
        trailControl.setEndWidth(this.width * this.widthFactor);
        trailControl.setLifeSpan(this.lifetime);

        target.attachChild(trailGeometry);
        trailGeometry.setQueueBucket(RenderQueue.Bucket.Transparent);



        fpp = new FilterPostProcessor(assetManager);
        baseApplication.getViewPort().addProcessor(fpp);

        BloomFilter bf = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bf);

//        FXAAFilter fXAAFilter = new FXAAFilter();        
//        fpp.addFilter(fXAAFilter);


    }

    @Override
    protected void show() {
        camera.setLocation(new Vector3f(0, 0, 10));
        camera.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);

        touchPickListener.registerWithInput(inputManager);

        doMovement(FastMath.nextRandomInt(-5, 5), FastMath.nextRandomInt(-4, 4));
    }

    private void doMovement(float targetX, float targetY) {
        Tween.to(target, SpatialAccessor.POS_XYZ, speed)
                .target(targetX, targetY, depth)
                .delay(0.02f)
                .setCallback(new TweenCallback() {
            public void onEvent(int i, BaseTween<?> bt) {
                doMovement(FastMath.nextRandomInt(-5, 5), FastMath.nextRandomInt(-4, 4));
            }
        }).start(baseApplication.getTweenManager());

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

    public void picked(PickEvent pickEvent, float tpf) {
    }

    public void drag(PickEvent pickEvent, float tpf) {

        if (pickEvent.isKeyDown()) {

            if (pickEvent.getContactPoint() != null) {
                log("Contact at: " + pickEvent.getContactPoint());
                targetPosition = new Vector3f(pickEvent.getContactPoint().x, pickEvent.getContactPoint().y, depth);
            }

        }

    }

    @Override
    public void update(float tpf) {
//        if (target != null && targetPosition != null) {
//            target.setLocalTranslation(target.getLocalTranslation().interpolate(targetPosition, 0.2f));
//        }

        if (target != null && pointLight != null) {
            pointLight.setPosition(target.getLocalTranslation().clone());
        }

    }
}
