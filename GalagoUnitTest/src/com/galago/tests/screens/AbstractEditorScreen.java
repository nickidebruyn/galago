/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import com.bruynhuis.galago.filters.CartoonEdgeProcessor;
import com.bruynhuis.galago.filters.FXAAFilter;

import com.bruynhuis.galago.filters.VignetteFilter;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.button.Checkbox;
import com.bruynhuis.galago.ui.field.HSlider;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.ValueChangeListener;
import com.bruynhuis.galago.util.SpatialUtils;
import com.galago.tests.ui.EditPanel;
import com.galago.tests.ui.SettingsPanel;
import com.jme3.app.FlyCamAppState;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.debug.WireBox;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;

/**
 *
 * @author NideBruyn
 */
public abstract class AbstractEditorScreen extends AbstractScreen implements PickListener {

    protected ChaseCamera chaseCamera;
    protected FlyCamAppState flyCamAppState;
    protected FilterPostProcessor fpp;
    protected TouchPickListener touchPickListener;
    protected float cameraDistance = 15f;
    protected float snapSize = 1f;
    protected DirectionalLight directionalLight;
    protected AmbientLight ambientLight;

    protected BloomFilter simpleBloomFilter;
    protected FXAAFilter fXAAFilter;
    protected CartoonEdgeProcessor cartoonEdgeProcessor;
    protected SSAOFilter basicSSAOFilter;
    protected FogFilter fogFilter;
    protected VignetteFilter vignetteFilter;

    protected Node sceneNode;
    protected Node ground;
    protected Node paintGizmo;
    protected Grid grid;
    protected WireBox wireBox;
    protected TerrainQuad terrainQuad;
    protected Material terrainMaterial;
    protected AbstractHeightMap heightmap;

    protected Spatial objectToAdd;

    protected Vector2f mouseDownPosition;
    protected Vector3f contactNormal = new Vector3f(0, 0, 0);
    protected boolean buttonClicked = false;

    protected SettingsPanel settingsPanel;
    protected EditPanel editPanel;
    protected Checkbox orbitCameraCheckbox, flyCameraCheckbox;
    protected Checkbox directionalLightCheckbox, ambientLightCheckbox;
    protected Checkbox gridCheckbox, snapToGridCheckbox, wireframeCheckbox;
    protected Checkbox bloomCheckbox, fxaaCheckbox, ssaoCheckbox, fogCheckbox, vignetteCheckbox;
    protected HSlider paintGizmoSlider;    
    
    private boolean modifyTerrainHeight = true;

    @Override
    protected void init() {

        //THIS IS THE SETTINGS PANEL
        settingsPanel = new SettingsPanel(hudPanel);
        settingsPanel.addButtonListerner(new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    buttonClicked = true;

                }
            }

        });

        settingsPanel.addHeading(" Camera");
        orbitCameraCheckbox = settingsPanel.addCheckbox(" Orbit camera", new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                flyCameraCheckbox.setChecked(!orbitCameraCheckbox.isChecked());
                chaseCamera.setEnabled(orbitCameraCheckbox.isChecked());
                flyCamAppState.setEnabled(flyCameraCheckbox.isChecked());
            }

        });
        flyCameraCheckbox = settingsPanel.addCheckbox(" Fly camera", new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                orbitCameraCheckbox.setChecked(!flyCameraCheckbox.isChecked());
                flyCamAppState.setEnabled(flyCameraCheckbox.isChecked());
                chaseCamera.setEnabled(orbitCameraCheckbox.isChecked());

            }

        });

        settingsPanel.addHeading(" Lighting");
        directionalLightCheckbox = settingsPanel.addCheckbox(" Directional light", new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

                directionalLight.setEnabled(directionalLightCheckbox.isChecked());

            }

        });
        ambientLightCheckbox = settingsPanel.addCheckbox(" Ambient light", new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

                ambientLight.setEnabled(ambientLightCheckbox.isChecked());

            }

        });

        settingsPanel.addHeading(" Tools");
        gridCheckbox = settingsPanel.addCheckbox(" Show grid", new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

                if (gridCheckbox.isChecked()) {
                    ground.setCullHint(Spatial.CullHint.Never);
                } else {
                    ground.setCullHint(Spatial.CullHint.Always);
                }

            }

        });
        snapToGridCheckbox = settingsPanel.addCheckbox(" Snap to grid", new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

            }

        });
        wireframeCheckbox = settingsPanel.addCheckbox(" Wireframe", new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                SpatialUtils.enableWireframe(sceneNode, wireframeCheckbox.isChecked());                

            }

        });

        settingsPanel.addHeading(" Environment");
        bloomCheckbox = settingsPanel.addCheckbox(" Bloom", new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

                simpleBloomFilter.setEnabled(bloomCheckbox.isChecked());

            }

        });

        fxaaCheckbox = settingsPanel.addCheckbox(" FXAA", new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

                fXAAFilter.setEnabled(fxaaCheckbox.isChecked());

            }

        });

        ssaoCheckbox = settingsPanel.addCheckbox(" Basic SSOA", new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                basicSSAOFilter.setEnabled(ssaoCheckbox.isChecked());

            }

        });

        fogCheckbox = settingsPanel.addCheckbox(" Fog", new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                fogFilter.setEnabled(fogCheckbox.isChecked());

            }

        });

        vignetteCheckbox = settingsPanel.addCheckbox(" Vignette", new TouchButtonAdapter() {

            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                vignetteFilter.setEnabled(vignetteCheckbox.isChecked());

            }

        });

        TouchButtonAdapter objectAction = new TouchButtonAdapter() {

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

        settingsPanel.addHeading(" Object");
        settingsPanel.addButton(" None", objectAction);
        settingsPanel.addButton(" Cube", objectAction);
        settingsPanel.addButton(" Sphere", objectAction);
//        settingsPanel.addButton(" Cone", objectAction);
//        settingsPanel.addButton(" Tree", objectAction);
//        settingsPanel.addButton(" Lava", objectAction);
//        settingsPanel.addButton(" Rock", objectAction);
        settingsPanel.addButton(" Terrain", objectAction);
        settingsPanel.addButton(" Ocean", objectAction);

//        THIS IS THE EDIT PANEL
        editPanel = new EditPanel(hudPanel);
        
        paintGizmoSlider = new HSlider(hudPanel, 250);
        paintGizmoSlider.setMaxValue(5);
        paintGizmoSlider.setMinValue(1);
        paintGizmoSlider.setIncrementValue(0.1f);
        paintGizmoSlider.centerTop(0, 0);
        paintGizmoSlider.setLabelText("Radius");
        paintGizmoSlider.addValueChangeListener(new ValueChangeListener() {
            
            @Override
            public void doValueChange(float value) {
                paintGizmo.setLocalScale(value, 0.0001f, value);
            }
        });
        paintGizmoSlider.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                buttonClicked = true;
            }
        });
        

    }

    protected void loadObjectToAdd(String type) {
        log("Adding: " + type);
        if (type != null) {

            //Unload previous object if exist
            if (objectToAdd != null) {
                objectToAdd.removeFromParent();
                objectToAdd = null;

            }
            
            if (type.equalsIgnoreCase("terrain")) {
                if (terrainQuad != null && terrainQuad.getParent() != null) {
                    terrainQuad.removeFromParent();
                    terrainMaterial = null;                    
                }
                loadTerrain();
                
            } else if (type.equalsIgnoreCase("ocean")) {
                SpatialUtils.addOceanWater(sceneNode, directionalLight.getDirection(), 2);
                
            } else {
                //Load the object by type
                objectToAdd = loadObject(rootNode, type);
                updateSpatialTransparency(objectToAdd, true);                
                
            }

        }
    }

    private Spatial loadObject(Node parent, String type) {
        Spatial s = null;
        if (type.equals("cube")) {
            s = SpatialUtils.addBox(parent, snapSize / 2, snapSize / 2, snapSize / 2);
//            SpatialUtils.addColor(s, ColorRGBA.Orange, true);
            s.setMaterial(baseApplication.getAssetManager().loadMaterial("Materials/rock.j3m"));

        } else if (type.equals("sphere")) {
            s = SpatialUtils.addSphere(parent, 50, 50, snapSize / 2);
            SpatialUtils.addColor(s, ColorRGBA.Orange, true);

        } else if (type.equals("tree")) {
            s = baseApplication.getAssetManager().loadModel("Models/tree.j3o");
            parent.attachChild(s);

        } else if (type.equals("lava")) {
            s = baseApplication.getAssetManager().loadModel("Models/lava.j3o");
            parent.attachChild(s);

        } else if (type.equals("rock")) {
            s = baseApplication.getAssetManager().loadModel("Models/rock.j3o");
            parent.attachChild(s);

        } else if (type.equals("cone")) {
            Node cone = new Node("cone");
            parent.attachChild(cone);
            Spatial spatial = SpatialUtils.addCone(cone, 24, snapSize / 2f, snapSize);
            SpatialUtils.addColor(spatial, ColorRGBA.Pink, true);
            SpatialUtils.rotate(spatial, -90, 0, 0);
            s = cone;

        }

        if (s != null) {
            s.setName(type);
        }

        return s;
    }

    protected void updateSpatialTransparency(Spatial spatial, final boolean transparent) {
        if (spatial != null) {
            SceneGraphVisitor sgv = new SceneGraphVisitor() {
                @Override
                public void visit(Spatial sp) {
                    if (sp instanceof Geometry) {
                        Geometry geom = (Geometry) sp;

                        if (transparent) {
                            geom.setQueueBucket(RenderQueue.Bucket.Transparent);
                            geom.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
                            MatParam diffuseParam = geom.getMaterial().getParam("Diffuse");
                            if (diffuseParam != null) {
                                ColorRGBA col = (ColorRGBA) diffuseParam.getValue();
                                diffuseParam.setValue(new ColorRGBA(col.r, col.g, col.b, 0.3f));
                            }

                        }

                        log("Update spatial material: " + geom.getMaterial());

                    }

                }
            };

            spatial.depthFirstTraversal(sgv);
        }

    }

    protected void updateObjectToAddPosition() {

        if (objectToAdd != null && paintGizmo != null) {

            //Get the bounds
            if (objectToAdd.getWorldBound() != null) {

//                objectToAdd.setLocalRotation(Quaternion.ZERO);
//                
//                log("Bounds: " + objectToAdd.getWorldBound());
//               
//                BoundingBox bb = (BoundingBox) objectToAdd.getWorldBound();
//                float xExt = bb.getXExtent();
//                float yExt = bb.getYExtent();
//                float zExt = bb.getZExtent();
                objectToAdd.setLocalTranslation(
                        paintGizmo.getLocalTranslation().x,
                        paintGizmo.getLocalTranslation().y,
                        paintGizmo.getLocalTranslation().z);

                objectToAdd.getLocalRotation().lookAt(contactNormal, Vector3f.UNIT_Y);
                SpatialUtils.rotate(objectToAdd, 90, 0, 0);

//                objectToAdd.move(contactNormal.mult(0.5f));
            }

        }

    }

    protected void paintObjectToAdd() {

        if (objectToAdd != null) {
            Spatial obj = loadObject(sceneNode, objectToAdd.getName());
            SpatialUtils.translate(obj,
                    objectToAdd.getWorldTranslation().x,
                    objectToAdd.getWorldTranslation().y,
                    objectToAdd.getWorldTranslation().z);

            obj.setLocalRotation(objectToAdd.getLocalRotation().clone());

        }

    }

    protected void loadTerrain() {
        /**
         * 1. Create terrain material and load four textures into it.
         */
        terrainMaterial = new Material(assetManager, "Common/MatDefs/Terrain/HeightBasedTerrain.j3md");
        
        Texture texture1 = assetManager.loadTexture("Textures/terrain/Ocean Floor.jpg");
        texture1.setWrap(Texture.WrapMode.Repeat);
        terrainMaterial.setTexture("region1ColorMap", texture1);
        terrainMaterial.setVector3("region1", new Vector3f(-1, 1, 46)); //(startHeight, endHeight, texScale)
        
        Texture texture2 = assetManager.loadTexture("Textures/terrain/sand.jpg");
        texture2.setWrap(Texture.WrapMode.Repeat);
        terrainMaterial.setTexture("region2ColorMap", texture2);
        terrainMaterial.setVector3("region2", new Vector3f(0.5f, 8, 46f)); //(startHeight, endHeight, texScale)
        
        Texture texture3 = assetManager.loadTexture("Textures/terrain/grass.jpg");
        texture3.setWrap(Texture.WrapMode.Repeat);
        terrainMaterial.setTexture("region3ColorMap", texture3);
        terrainMaterial.setVector3("region3", new Vector3f(7f, 30, 80)); //(startHeight, endHeight, texScale)
        
        Texture texture4 = assetManager.loadTexture("Textures/terrain/Mountain Faults.jpg");
        texture4.setWrap(Texture.WrapMode.Repeat);
        terrainMaterial.setTexture("region4ColorMap", texture4);
        terrainMaterial.setVector3("region4", new Vector3f(28, 100, 42)); //(startHeight, endHeight, texScale)
        
        
        Texture textureSlope = assetManager.loadTexture("Textures/terrain/Age of the Canyon.jpg");
        textureSlope.setWrap(Texture.WrapMode.Repeat);
        terrainMaterial.setTexture("slopeColorMap", textureSlope);
        terrainMaterial.setFloat("slopeTileFactor", 12f);
        
        terrainMaterial.setFloat("terrainSize", 512f);

        /**
         * 2. Create the height map
         */
        Texture heightMapImage = assetManager.loadTexture("Textures/terrain/heightmap2.png");
        heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
        heightmap.load();
        
        /**
         * 3. We have prepared material and heightmap. Now we create the actual
         * terrain: 3.1) Create a TerrainQuad and name it "my terrain". 3.2) A
         * good value for terrain tiles is 64x64 -- so we supply 64+1=65. 3.3)
         * We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
         * 3.4) As LOD step scale we supply Vector3f(1,1,1). 3.5) We supply the
         * prepared heightmap itself.
         */
        int patchSize = 65;
        terrainQuad = new TerrainQuad("my terrain", patchSize, 513, heightmap.getHeightMap());

        /**
         * 4. We give the terrain its material, position & scale it, and attach
         * it.
         */
        terrainQuad.setMaterial(terrainMaterial);        
        terrainQuad.setLocalTranslation(0, 0, 0);
        terrainQuad.setLocalScale(1f, 0.5f, 1f);
        sceneNode.attachChild(terrainQuad);
        
        /**
         * 5. The LOD (level of detail) depends on were the camera is:
         */
        TerrainLodControl control = new TerrainLodControl(terrainQuad, camera);
        terrainQuad.addControl(control);
    }

    @Override
    protected void load() {

        //Load the scene node
        sceneNode = new Node("scene-node");
        rootNode.attachChild(sceneNode);

        //Load the ground grid
        ground = new Node("ground-name");
        sceneNode.attachChild(ground);

        grid = new Grid((int) cameraDistance * 10, (int) cameraDistance * 10, snapSize);
        Geometry gridGeom = new Geometry("ground", grid);
        ground.attachChild(gridGeom);
        SpatialUtils.addColor(gridGeom, ColorRGBA.Gray, true);
        gridGeom.center();

        Spatial groundPlane = SpatialUtils.addBox(ground, cameraDistance * 10, 0f, cameraDistance * 10);
        SpatialUtils.addColor(groundPlane, ColorRGBA.Gray, true);
        groundPlane.setCullHint(Spatial.CullHint.Always);

        //Load the paint gizmo
        paintGizmo = new Node("paint-gizmo");
        rootNode.attachChild(paintGizmo);

        Geometry paintGizmoGeom = (Geometry) SpatialUtils.addBox(paintGizmo, 1f, 0.0001f, 1f); //SpatialUtils.addPlane(paintGizmo, 0.5f, 0.5f);
        paintGizmoGeom.setQueueBucket(RenderQueue.Bucket.Transparent);
        SpatialUtils.addTexture(paintGizmoGeom, "Textures/paint-marker.png", true);
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
        directionalLight.setDirection((new Vector3f(-0.5f, -0.85f, -0.5f)).normalizeLocal());
        directionalLight.setColor(ColorRGBA.White);
        directionalLight.setFrustumCheckNeeded(true);
        rootNode.addLight(directionalLight);

        /**
         * A white ambient light source.
         */
        ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.LightGray);
        ambientLight.setFrustumCheckNeeded(true);
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
        basicSSAOFilter.setIntensity(1.5f);
        basicSSAOFilter.setSampleRadius(0.15f);
        basicSSAOFilter.setScale(0.12f);
        basicSSAOFilter.setEnabled(false);
        fpp.addFilter(basicSSAOFilter);

        fogFilter = new FogFilter();
        fogFilter.setFogColor(baseApplication.BACKGROUND_COLOR);
        fogFilter.setFogDensity(2f);
        fogFilter.setFogDistance(80);
        fogFilter.setEnabled(false);
        fpp.addFilter(fogFilter);

        vignetteFilter = new VignetteFilter();
        vignetteFilter.setEnabled(false);
        fpp.addFilter(vignetteFilter);

        baseApplication.getViewPort().addProcessor(fpp);

        //Load the cartoon edge processor
        cartoonEdgeProcessor = new CartoonEdgeProcessor();
        baseApplication.getViewPort().addProcessor(cartoonEdgeProcessor);

        //SET THE CHECKBOX DEFAULTS        
        orbitCameraCheckbox.setChecked(true);
        flyCameraCheckbox.setChecked(false);
        directionalLightCheckbox.setChecked(true);
        ambientLightCheckbox.setChecked(true);

        gridCheckbox.setChecked(true);

    }

    @Override
    protected void show() {
        touchPickListener = new TouchPickListener(camera, sceneNode);
        touchPickListener.setPickListener(this);
        touchPickListener.registerWithInput(inputManager);

//        settingsPanel.closePanel();
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

            if (pickEvent.getContactObject() != null && !pickEvent.isKeyDown()) {

                if (pickEvent.getCursorPosition().distance(mouseDownPosition) < 50 && !buttonClicked) {
                    paintObjectToAdd();
                    mouseDownPosition = null;
                }

                if (buttonClicked) {
                    buttonClicked = false;
                }

            }

        }

    }

    @Override
    public void drag(PickEvent pickEvent, float tpf) {
        if (pickEvent.getContactObject() != null) {

            if (pickEvent.getContactNormal() != null) {
//                log("Contact Normal: " + pickEvent.getContactNormal());
                paintGizmo.getLocalRotation().lookAt(pickEvent.getContactNormal(), Vector3f.UNIT_Y);
                contactNormal.set(pickEvent.getContactNormal());
                SpatialUtils.rotate(paintGizmo, -90, 0, 0);

            } else {
                contactNormal.set(0, 0, 0);
            }

            Vector3f pointerWorldLocation = pickEvent.getContactPoint();
            if (snapToGridCheckbox.isChecked()) {
                paintGizmo.setLocalTranslation((int) pointerWorldLocation.x, (int) pointerWorldLocation.y, (int) pointerWorldLocation.z);
            } else {
                paintGizmo.setLocalTranslation(pointerWorldLocation.x, pointerWorldLocation.y, pointerWorldLocation.z);
            }

            if (objectToAdd != null) {
                updateObjectToAddPosition();
                
            } else if (modifyTerrainHeight) {
                if (terrainQuad != null) {
                    
                    if (pickEvent.isKeyDown() && pickEvent.isRightButton()) {
                        log("You are trying to modify terrain at: " + pickEvent.getContactPoint());
                        SpatialUtils.doModifyTerrainHeight(terrainQuad, pickEvent.getContactPoint(), paintGizmo.getLocalScale().x, 0.1f);
                        
                    }
                    
                }
                
            }
            
        }
    }

    @Override
    public void update(float tpf) {
        if (isActive() && camera != null) {

            if (camera.getLocation().y < 0) {
                camera.setLocation(new Vector3f(camera.getLocation().x, 0, camera.getLocation().z));

            }

//            if (objectToAdd != null) {
//                SpatialUtils.rotate(objectToAdd, 0, 1, 0);
//                
//            }
        }
    }

    
}
