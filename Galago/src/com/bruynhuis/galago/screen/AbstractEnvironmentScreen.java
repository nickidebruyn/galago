/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.screen;

import com.bruynhuis.galago.filters.CartoonEdgeProcessor;
import com.bruynhuis.galago.filters.FXAAFilter;
import com.bruynhuis.galago.filters.FogFilter;
import com.bruynhuis.galago.filters.VignetteFilter;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.ui.button.Checkbox;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.app.FlyCamAppState;
import com.jme3.bounding.BoundingBox;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;

/**
 *
 * @author NideBruyn
 */
public abstract class AbstractEnvironmentScreen extends AbstractScreen implements PickListener {

    protected ChaseCamera chaseCamera;
    protected FlyCamAppState flyCamAppState;
    protected FilterPostProcessor fpp;
    protected TouchPickListener touchPickListener;
    protected float cameraDistance = 16f;
    protected float snapSize = 1f;
    protected float gridSize = 12.5f;
    protected DirectionalLight directionalLight;
    protected AmbientLight ambientLight;

    protected BloomFilter simpleBloomFilter;
    protected FXAAFilter fXAAFilter;
    protected CartoonEdgeProcessor cartoonEdgeProcessor;
    protected SSAOFilter basicSSAOFilter;
    protected FogFilter fogFilter;
    protected VignetteFilter vignetteFilter;
    protected DirectionalLightShadowFilter dlsf;
    protected DirectionalLightShadowRenderer dlsr;

    private Node sceneNode;
    private Node ground;
    private Node paintGizmo;
    private Grid grid;
    private Geometry gridGeom;
//    private WireBox wireBox;

    private Spatial objectToAdd;
    private Spatial selectedObject;
    protected ColorRGBA selectionColor = ColorRGBA.Black;

    private Vector2f mouseDownPosition;
    private Vector3f contactNormal = new Vector3f(0, 0, 0);
    private boolean buttonClicked = false;

    private SettingsPanel settingsPanel;
    private EditPanel editPanel;
    private Checkbox noCameraCheckbox, orbitCameraCheckbox, flyCameraCheckbox;
    private Checkbox directionalLightCheckbox, ambientLightCheckbox;
    private Checkbox gridCheckbox, snapToGridCheckbox, wireframeCheckbox, snapToNormalCheckbox;
    private Checkbox bloomCheckbox, fxaaCheckbox, ssaoCheckbox, fogCheckbox, vignetteCheckbox, shadowCheckbox, shadowRendererCheckbox;

    protected ColorRGBA panelColor = ColorRGBA.Gray;
    protected ColorRGBA panelHeadingColor = ColorRGBA.DarkGray;
    protected ColorRGBA panelHeadingTextColor = ColorRGBA.White;

    protected ColorRGBA panelButtonColor = ColorRGBA.DarkGray;
    protected ColorRGBA panelButtonTextColor = ColorRGBA.White;

    private TouchButtonAdapter editPanelAction;

    @Override
    protected void init() {

        //THIS IS THE SETTINGS PANEL
        settingsPanel = new SettingsPanel(hudPanel);
        settingsPanel.setBackgroundColor(panelColor);
        settingsPanel.addButtonListerner(new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    buttonClicked = true;

                }
            }

        });

        settingsPanel.addHeading(" Camera", panelHeadingTextColor, panelHeadingColor);
        noCameraCheckbox = settingsPanel.addCheckbox(" No camera", panelButtonTextColor, new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

                if (noCameraCheckbox.isChecked()) {
                    flyCameraCheckbox.setChecked(false);
                    orbitCameraCheckbox.setChecked(false);
                    chaseCamera.setEnabled(false);
                    flyCamAppState.setEnabled(false);

                }

            }

        });

        orbitCameraCheckbox = settingsPanel.addCheckbox(" Orbit camera", panelButtonTextColor, new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

                if (orbitCameraCheckbox.isChecked()) {
                    flyCameraCheckbox.setChecked(false);
                    noCameraCheckbox.setChecked(false);
                    chaseCamera.setEnabled(true);
                    flyCamAppState.setEnabled(false);

                }

            }

        });
        flyCameraCheckbox = settingsPanel.addCheckbox(" Fly camera", panelButtonTextColor, new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

                if (flyCameraCheckbox.isChecked()) {
                    noCameraCheckbox.setChecked(false);
                    orbitCameraCheckbox.setChecked(false);
                    chaseCamera.setEnabled(false);
                    flyCamAppState.setEnabled(true);

                }

                orbitCameraCheckbox.setChecked(!flyCameraCheckbox.isChecked());
                flyCamAppState.setEnabled(flyCameraCheckbox.isChecked());
                chaseCamera.setEnabled(orbitCameraCheckbox.isChecked());

            }

        });

        settingsPanel.addHeading(" Lighting", panelHeadingTextColor, panelHeadingColor);
        directionalLightCheckbox = settingsPanel.addCheckbox(" Directional light", panelButtonTextColor, new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

                directionalLight.setEnabled(directionalLightCheckbox.isChecked());

            }

        });
        ambientLightCheckbox = settingsPanel.addCheckbox(" Ambient light", panelButtonTextColor, new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

                ambientLight.setEnabled(ambientLightCheckbox.isChecked());

            }

        });

        settingsPanel.addHeading(" Tools", panelHeadingTextColor, panelHeadingColor);
        gridCheckbox = settingsPanel.addCheckbox(" Show grid", panelButtonTextColor, new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

                if (gridCheckbox.isChecked()) {
                    gridGeom.setCullHint(Spatial.CullHint.Never);
                } else {
                    gridGeom.setCullHint(Spatial.CullHint.Always);
                }

            }

        });
        snapToGridCheckbox = settingsPanel.addCheckbox(" Snap to grid", panelButtonTextColor, new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

            }

        });
        snapToNormalCheckbox = settingsPanel.addCheckbox(" Snap to normal", panelButtonTextColor, new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

            }

        });
        wireframeCheckbox = settingsPanel.addCheckbox(" Wireframe", panelButtonTextColor, new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                SpatialUtils.enableWireframe(sceneNode, wireframeCheckbox.isChecked());

            }

        });

        settingsPanel.addHeading(" Environment", panelHeadingTextColor, panelHeadingColor);
        bloomCheckbox = settingsPanel.addCheckbox(" Bloom", panelButtonTextColor, new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

                simpleBloomFilter.setEnabled(bloomCheckbox.isChecked());

            }

        });

        fxaaCheckbox = settingsPanel.addCheckbox(" FXAA", panelButtonTextColor, new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

                fXAAFilter.setEnabled(fxaaCheckbox.isChecked());

            }

        });

        ssaoCheckbox = settingsPanel.addCheckbox(" Basic SSOA", panelButtonTextColor, new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                basicSSAOFilter.setEnabled(ssaoCheckbox.isChecked());

            }

        });

//        fogCheckbox = settingsPanel.addCheckbox(" Fog", new TouchButtonAdapter() {
//
//            @Override
//            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
//                buttonClicked = true;
//            }
//
//            @Override
//            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
//                fogFilter.setEnabled(fogCheckbox.isChecked());
//
//            }
//
//        });
        vignetteCheckbox = settingsPanel.addCheckbox(" Vignette", panelButtonTextColor, new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                vignetteFilter.setEnabled(vignetteCheckbox.isChecked());

            }

        });

        shadowCheckbox = settingsPanel.addCheckbox(" Shadows", panelButtonTextColor, new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                dlsf.setEnabled(shadowCheckbox.isChecked());

            }

        });

        shadowRendererCheckbox = settingsPanel.addCheckbox(" Shadow Renderer", panelButtonTextColor, new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (shadowRendererCheckbox.isChecked()) {
                    baseApplication.getViewPort().addProcessor(dlsr);

                } else {
                    baseApplication.getViewPort().removeProcessor(dlsr);
                }

            }

        });

//        THIS IS THE EDIT PANEL
        editPanelAction = new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {

                    loadObjectToAdd(uid.trim().toLowerCase());

                }
            }

        };

        editPanel = new EditPanel(hudPanel);
        editPanel.setBackgroundColor(panelColor);
        loadToolbarItem();

    }

    protected void addSettingsbarHeading(String title) {
        settingsPanel.addHeading(title, panelHeadingTextColor, panelHeadingColor);
    }

    protected void addSettingsbarButton(String title, TouchButtonAdapter listener) {
        settingsPanel.addButton(title, panelButtonTextColor, panelButtonColor, listener);
    }

    protected void addToolbarHeading(String title) {
        editPanel.addHeading(title, panelHeadingTextColor, panelHeadingColor);
    }

    protected void addToolbarButton(String title) {
        editPanel.addButton(title, panelButtonTextColor, panelButtonColor, editPanelAction);
    }

    protected void addToolbarCheckbox(String title) {
        editPanel.addCheckbox(title, panelButtonTextColor, editPanelAction);
    }

    protected abstract void loadToolbarItem();

    protected void loadObjectToAdd(String type) {
        log("Adding: " + type);
        if (type != null) {

            //Unload previous object if exist
            if (objectToAdd != null) {
                objectToAdd.removeFromParent();
                objectToAdd = null;

            }

            //Load the object by type
            objectToAdd = loadObject(rootNode, type, false);
            SpatialUtils.updateSpatialTransparency(objectToAdd, true, 0.2f);

        }
    }

    protected abstract Spatial loadObject(Node parent, String type, boolean paint);

    protected void updateObjectToAddPosition() {

        if (objectToAdd != null && paintGizmo != null) {

            //Get the bounds
            if (objectToAdd.getWorldBound() != null) {

//                objectToAdd.setLocalRotation(Quaternion.ZERO);
                BoundingBox bb = (BoundingBox) objectToAdd.getWorldBound();
                float yExt = bb.getYExtent();
//                
//                log("Bounds: " + bb);
//                log("Dis: " + (bb.getCenter().y + yExt));

                objectToAdd.setLocalTranslation(
                        paintGizmo.getLocalTranslation().x,
                        paintGizmo.getLocalTranslation().y,
                        paintGizmo.getLocalTranslation().z);

                objectToAdd.getLocalRotation().lookAt(contactNormal, Vector3f.UNIT_Y);
                SpatialUtils.rotate(objectToAdd, 90, 0, 0);

                if (snapToNormalCheckbox.isChecked()) {
                    objectToAdd.move(contactNormal.mult(yExt));
                }

            }

        }

    }

    protected void paintObjectToAdd() {

        if (objectToAdd != null) {
            Spatial obj = loadObject(sceneNode, objectToAdd.getName(), true);
            SpatialUtils.translate(obj,
                    objectToAdd.getWorldTranslation().x,
                    objectToAdd.getWorldTranslation().y,
                    objectToAdd.getWorldTranslation().z);

            obj.setLocalRotation(objectToAdd.getWorldRotation().clone());

        }

    }

    @Override
    protected void load() {

//        SpatialUtils.addSkySphere(sceneNode, 2, camera);
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.Gray);

        //Load the scene node
        sceneNode = new Node("scene-node");
        rootNode.attachChild(sceneNode);

        //START GROUND #########################################################
        //Load the ground grid
        ground = new Node("ground");
        sceneNode.attachChild(ground);

//        grid = new Grid((int) cameraDistance * 2, (int) cameraDistance * 2, snapSize);
//        gridGeom = new Geometry("ground-geom", grid);
//        ground.attachChild(gridGeom);
//        SpatialUtils.addColor(gridGeom, ColorRGBA.LightGray, true);
//        gridGeom.center();

        Spatial groundPlane = SpatialUtils.addBox(ground, cameraDistance * 2, 0.001f, cameraDistance * 2);
//        SpatialUtils.addColor(groundPlane, ColorRGBA.Red, true);
        SpatialUtils.addTexture(groundPlane, "Resources/worksheet.png", true);
        ((Geometry)groundPlane).getMesh().scaleTextureCoordinates(new Vector2f(cameraDistance * 2 * 2f, cameraDistance * 2 * 2f));
        groundPlane.move(0.5f, -0.01f, 0.5f);
        SpatialUtils.addMass(groundPlane, 0);
        //END GROUND #################################################################

        //Load the paint gizmo
        paintGizmo = new Node("paint-gizmo");
        paintGizmo.setShadowMode(RenderQueue.ShadowMode.Off);
        rootNode.attachChild(paintGizmo);

        Geometry paintGizmoGeom = (Geometry) SpatialUtils.addBox(paintGizmo, 1f, 0.0001f, 1f); //SpatialUtils.addPlane(paintGizmo, 0.5f, 0.5f);
        paintGizmoGeom.setQueueBucket(RenderQueue.Bucket.Transparent);
        paintGizmoGeom.setShadowMode(RenderQueue.ShadowMode.Off);
        SpatialUtils.addTexture(paintGizmoGeom, "Resources/textures/paint-marker.png", true);
        paintGizmoGeom.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.AlphaAdditive);
        paintGizmoGeom.getMaterial().setFloat("AlphaDiscardThreshold", 0.1f);
        paintGizmoGeom.getMaterial().getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);

        //Load the fly cam
        flyCamAppState = new FlyCamAppState();
        baseApplication.getStateManager().attach(flyCamAppState);

        //Load the camera
        chaseCamera = new ChaseCamera(camera, sceneNode, inputManager);
        chaseCamera.setDefaultDistance(cameraDistance);
        chaseCamera.setChasingSensitivity(60);
        chaseCamera.setSmoothMotion(true);
        chaseCamera.setTrailingEnabled(false);

        chaseCamera.setDefaultHorizontalRotation(135 * FastMath.DEG_TO_RAD);
        chaseCamera.setDefaultVerticalRotation(40 * FastMath.DEG_TO_RAD);

        chaseCamera.setMinVerticalRotation(2 * FastMath.DEG_TO_RAD);
        chaseCamera.setMaxVerticalRotation(60 * FastMath.DEG_TO_RAD);

//            chaseCamera.setMinVerticalRotation(0 * FastMath.DEG_TO_RAD);
//            chaseCamera.setMaxVerticalRotation(0 * FastMath.DEG_TO_RAD);
        chaseCamera.setLookAtOffset(new Vector3f(0, 0f, 0));

        chaseCamera.setHideCursorOnRotate(false);
//            chaseCamera.setRotationSpeed(5);
        chaseCamera.setRotationSpeed(8);
        chaseCamera.setMinDistance(cameraDistance / 2f);
        chaseCamera.setMaxDistance(cameraDistance * 2f);

        chaseCamera.setDragToRotate(true);
        chaseCamera.setRotationSensitivity(5);

        //Load the Light
        directionalLight = new DirectionalLight();
        directionalLight.setDirection(new Vector3f(0.3f, -0.95f, -0.5f));
        directionalLight.setColor(ColorRGBA.White);
//        directionalLight.setFrustumCheckNeeded(true);
        rootNode.addLight(directionalLight);

        /**
         * A white ambient light source.
         */
        ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.White);
        ambientLight.setFrustumCheckNeeded(true);
        ambientLight.setEnabled(false);
        rootNode.addLight(ambientLight);

        //LOAD THE FilterPostProcessor
        fpp = new FilterPostProcessor(assetManager);

        simpleBloomFilter = new BloomFilter(BloomFilter.GlowMode.SceneAndObjects);
//        simpleBloomFilter.setScale(0.2f);
//        simpleBloomFilter.setThreshold(1f);
        simpleBloomFilter.setEnabled(false);
        fpp.addFilter(simpleBloomFilter);

        fXAAFilter = new FXAAFilter();
        fXAAFilter.setEnabled(false);
        fpp.addFilter(fXAAFilter);

        basicSSAOFilter = new SSAOFilter();
        basicSSAOFilter.setBias(0.2f);
        basicSSAOFilter.setIntensity(2.5f);
        basicSSAOFilter.setSampleRadius(0.15f);
        basicSSAOFilter.setScale(0.12f);
        basicSSAOFilter.setEnabled(false);
        fpp.addFilter(basicSSAOFilter);

        fogFilter = new FogFilter();
        fogFilter.setFogColor(baseApplication.BACKGROUND_COLOR);
        fogFilter.setFogDensity(2f);
//        fogFilter.setFogDistance(80);
        fogFilter.setEnabled(false);
        fpp.addFilter(fogFilter);

        vignetteFilter = new VignetteFilter();
        vignetteFilter.setEnabled(false);
        fpp.addFilter(vignetteFilter);

        dlsf = new DirectionalLightShadowFilter(assetManager, 512, 1);
        dlsf.setLight(directionalLight);
        dlsf.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
        dlsf.setEnabled(false);
        fpp.addFilter(dlsf);

        baseApplication.getViewPort().addProcessor(fpp);

        //Load the cartoon edge processor
        cartoonEdgeProcessor = new CartoonEdgeProcessor();
        baseApplication.getViewPort().addProcessor(cartoonEdgeProcessor);

        dlsr = new DirectionalLightShadowRenderer(assetManager, 512, 1);
        dlsr.setLight(directionalLight);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCF4);

        //SET THE CHECKBOX DEFAULTS        
        orbitCameraCheckbox.setChecked(true);
        flyCameraCheckbox.setChecked(false);
        directionalLightCheckbox.setChecked(true);
        ambientLightCheckbox.setChecked(false);

        gridCheckbox.setChecked(true);
        snapToNormalCheckbox.setChecked(true);

    }

    @Override
    protected void show() {
//        baseApplication.showDebuging();
        touchPickListener = new TouchPickListener("main", camera, sceneNode);
        touchPickListener.setPickListener(this);
        touchPickListener.registerWithInput(inputManager);

        settingsPanel.closePanel();
        editPanel.closePanel();

        flyCamAppState.setEnabled(false);
        flyCamAppState.getCamera().setDragToRotate(true);
        flyCamAppState.getCamera().setMoveSpeed(10f);
        flyCamAppState.getCamera().setRotationSpeed(3f);

//        Vector3f dir = new Vector3f(0, 1, 0);
//        Spatial c = loadObject(sceneNode, "cone");
//        c.getLocalRotation().lookAt(dir, Vector3f.UNIT_Y);
//        SpatialUtils.rotate(c, 90, 0, 0);        
//        c.move(dir.mult(0.5f));
    }

    @Override
    protected void exit() {

        baseApplication.getViewPort().removeProcessor(fpp);
        baseApplication.getViewPort().removeProcessor(cartoonEdgeProcessor);
        baseApplication.getViewPort().removeProcessor(dlsr);
        touchPickListener.unregisterInput();

        rootNode.removeLight(ambientLight);
        rootNode.removeLight(directionalLight);
        rootNode.detachAllChildren();

    }

    @Override
    protected void pause() {
    }

    @Override
    public void picked(PickEvent pickEvent, float tpf) {

        if (isActive()) {

            if (pickEvent.isKeyDown()) {
                mouseDownPosition = pickEvent.getCursorPosition().clone();
            }

            if (mouseDownPosition != null && pickEvent.getContactObject() != null && !pickEvent.isKeyDown()) {

                if (pickEvent.getCursorPosition().distance(mouseDownPosition) < 100 && !buttonClicked) {
                    paintObjectToAdd();
                    mouseDownPosition = null;
                }

                if (buttonClicked) {
                    buttonClicked = false;
                }

            } else if (mouseDownPosition != null && pickEvent.getContactObject() != null
                    && pickEvent.isKeyDown() && objectToAdd == null && pickEvent.isRightButton()) {
                log("You clicked on " + pickEvent.getContactObject());

                if (pickEvent.getContactObject().getParent().equals(sceneNode)) {
                    log("Parent 1");
                    selectNewObject(pickEvent.getContactObject());

                } else if (pickEvent.getContactObject().getParent().getParent().equals(sceneNode)) {
                    log("Parent 2");
                    selectNewObject(pickEvent.getContactObject().getParent());

                } else if (pickEvent.getContactObject().getParent().getParent().getParent().equals(sceneNode)) {
                    log("Parent 3");
                    selectNewObject(pickEvent.getContactObject().getParent().getParent());

                } else if (pickEvent.getContactObject().getParent().getParent().getParent().getParent().equals(sceneNode)) {
                    log("Parent 4");
                    selectNewObject(pickEvent.getContactObject().getParent().getParent().getParent());

                } else if (pickEvent.getContactObject().getParent().getParent().getParent().getParent().getParent().equals(sceneNode)) {
                    log("Parent 5");
                    selectNewObject(pickEvent.getContactObject().getParent().getParent().getParent().getParent());

                }

            }

        }

    }

    /**
     * Helper method for selecting an object in the scene.
     *
     * @param item
     */
    private void selectNewObject(Spatial item) {

        if (selectedObject != null) {
            SpatialUtils.updateSpatialTransparency(selectedObject, true, 1f);
            selectedObject = null;
        }

        if (item != null && item != ground) {
            log("You selected " + item.getName());
            selectedObject = item;
            SpatialUtils.updateSpatialTransparency(selectedObject, true, 0.5f);

        }

    }

    @Override
    public void drag(PickEvent pickEvent, float tpf) {
        if (pickEvent.getContactObject() != null) {

            if (snapToNormalCheckbox.isChecked()) {

                if (pickEvent.getContactNormal() != null) {
                    paintGizmo.getLocalRotation().lookAt(pickEvent.getContactNormal(), Vector3f.UNIT_Y);
                    contactNormal.set(pickEvent.getContactNormal());
                    SpatialUtils.rotate(paintGizmo, -90, 0, 0);

                } else {
                    contactNormal.set(0, 0, 0);
                }
            } else {
                Vector3f upNormal = new Vector3f(0, 1, 0);
                paintGizmo.getLocalRotation().lookAt(upNormal, Vector3f.UNIT_Y);
                contactNormal.set(upNormal);
                SpatialUtils.rotate(paintGizmo, -90, 0, 0);

            }

            Vector3f pointerWorldLocation = pickEvent.getContactPoint();
            if (snapToGridCheckbox.isChecked()) {
                paintGizmo.setLocalTranslation((int) pointerWorldLocation.x, (int) pointerWorldLocation.y, (int) pointerWorldLocation.z);
            } else {
                paintGizmo.setLocalTranslation(pointerWorldLocation.x, pointerWorldLocation.y, pointerWorldLocation.z);
            }

            if (objectToAdd != null) {
                updateObjectToAddPosition();

            }

        }
    }

    @Override
    public void update(float tpf) {
        if (isActive() && camera != null) {

            if (camera.getLocation().y < 0) {
                camera.setLocation(new Vector3f(camera.getLocation().x, 0, camera.getLocation().z));

            }

        }
    }

}
