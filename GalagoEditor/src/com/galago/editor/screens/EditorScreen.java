package com.galago.editor.screens;

import com.bruynhuis.galago.filters.FXAAFilter;
import com.bruynhuis.galago.input.Input;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.messages.MessageListener;
import com.galago.editor.utils.EditorUtils;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.util.ColorUtils;
import com.bruynhuis.galago.util.SpatialUtils;
import com.galago.editor.spatial.Gizmo;
import com.galago.editor.spatial.GizmoListener;
import com.galago.editor.ui.HierarchyPanel;
import com.galago.editor.ui.ToolbarPanel;
import com.galago.editor.utils.Action;
import com.galago.editor.utils.MaterialUtils;
import com.jme3.app.FlyCamAppState;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Line;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.water.WaterFilter;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author ndebruyn
 */
public class EditorScreen extends AbstractScreen implements MessageListener, PickListener, GizmoListener {

    public static final String NAME = "ParticleEditor";

    private ToolbarPanel toolbarPanel;
    private HierarchyPanel hierarchyPanel;

    protected AmbientLight ambientLight;
    protected DirectionalLight sunLight;
    protected Spatial sky;
    protected LightProbe lightProbe;
    protected DirectionalLightShadowRenderer shadowRenderer;
    protected DirectionalLightShadowFilter shadowFilter;
    protected FilterPostProcessor fpp;
    protected FilterPostProcessor oceanProcessor;
    protected WaterFilter oceanFilter;
    protected FXAAFilter fXAAFilter;
    protected SSAOFilter basicSSAOFilter;

    protected ChaseCamera chaseCamera;
    protected FlyCamAppState flyCamAppState;
    protected float cameraDistance = 15f;
    protected float distanceScale = 0f;
    
    protected TouchPickListener touchPickListener;

    private Node editNode;
    private Gizmo transformGizmo;
    private Spatial selectedSpatial;
    private Node chaseCameraTarget;
    
    @Override
    protected void init() {

        toolbarPanel = new ToolbarPanel(hudPanel);
        toolbarPanel.leftCenter(0, 0);

        hierarchyPanel = new HierarchyPanel(hudPanel);
        hierarchyPanel.leftCenter(EditorUtils.TOOLBAR_WIDTH, 0);
        
        touchPickListener = new TouchPickListener("scene-control", camera, rootNode);
        touchPickListener.setPickListener(this);
        

    }

    @Override
    protected void load() {
        baseApplication.getViewPort().setBackgroundColor(EditorUtils.theme.getBackgroundColor());

        newScene();

        initEnvironment();

        initCameras();
        
        initGizmos();
    }

    @Override
    protected void show() {
        baseApplication.getMessageManager().addMessageListener(this);
        toolbarPanel.reset();
        hierarchyPanel.hide();
        touchPickListener.registerWithInput(inputManager);
    }

    @Override
    protected void exit() {
        baseApplication.getMessageManager().removeMessageListener(this);
        touchPickListener.unregisterInput();
    }

    @Override
    protected void pause() {
    }

    protected void newScene() {
        rootNode.detachAllChildren();

        initGrid();

        editNode = new Node("root");
        rootNode.attachChild(editNode);

    }

    private void initGrid() {
        int gridLines = 1000;
        float lineSpacing = 1;
        Grid grid = new Grid(gridLines, gridLines, lineSpacing);
        Geometry gridGeom = new Geometry("gridsmall", grid);
        SpatialUtils.addColor(gridGeom, EditorUtils.theme.getGridColor(), true);
        gridGeom.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        gridGeom.move(-gridLines * lineSpacing * 0.5f, 0, -gridLines * lineSpacing * 0.5f);
        gridGeom.setShadowMode(RenderQueue.ShadowMode.Off);
        rootNode.attachChild(gridGeom);

        Line line = new Line(new Vector3f(-gridLines * lineSpacing * 0.5f, 0, 0), new Vector3f(gridLines * lineSpacing * 0.5f, 0, 0));
        Geometry g = new Geometry("gridlinex", line);
        SpatialUtils.addColor(g, EditorUtils.theme.getXAxisColor(), true);
        g.getMaterial().getAdditionalRenderState().setLineWidth(2);
        g.setShadowMode(RenderQueue.ShadowMode.Off);
        rootNode.attachChild(g);

        line = new Line(new Vector3f(0, 0, -gridLines * lineSpacing * 0.5f), new Vector3f(0, 0, gridLines * lineSpacing * 0.5f));
        g = new Geometry("gridlinez", line);
        SpatialUtils.addColor(g, EditorUtils.theme.getZAxisColor(), true);
        g.getMaterial().getAdditionalRenderState().setLineWidth(2);
        g.setShadowMode(RenderQueue.ShadowMode.Off);
        rootNode.attachChild(g);

        gridLines = 100;
        lineSpacing = 10;
        grid = new Grid(gridLines, gridLines, lineSpacing);
        gridGeom = new Geometry("gridlarge", grid);
        SpatialUtils.addColor(gridGeom, EditorUtils.theme.getPanelColor(), true);
        gridGeom.move(-gridLines * lineSpacing * 0.5f, 0.01f, -gridLines * lineSpacing * 0.5f);
        gridGeom.setShadowMode(RenderQueue.ShadowMode.Off);
        rootNode.attachChild(gridGeom);

    }

    private void initCameras() {
        //Load the fly cam
//        flyCamAppState = new FlyCamAppState();
//        baseApplication.getStateManager().attach(flyCamAppState);

        chaseCameraTarget = new Node("chasecam-target");
        rootNode.attachChild(chaseCameraTarget);

        //Load the camera
        chaseCamera = new ChaseCamera(camera, chaseCameraTarget, inputManager);
        chaseCamera.setDefaultDistance(cameraDistance);
        chaseCamera.setChasingSensitivity(60);
        chaseCamera.setSmoothMotion(true);
        chaseCamera.setTrailingEnabled(false);

        chaseCamera.setDefaultHorizontalRotation(135 * FastMath.DEG_TO_RAD);
        chaseCamera.setDefaultVerticalRotation(40 * FastMath.DEG_TO_RAD);

        chaseCamera.setMinVerticalRotation(-60 * FastMath.DEG_TO_RAD);
        chaseCamera.setMaxVerticalRotation(60 * FastMath.DEG_TO_RAD);
        chaseCamera.setInvertVerticalAxis(true);

//            chaseCamera.setMinVerticalRotation(0 * FastMath.DEG_TO_RAD);
//            chaseCamera.setMaxVerticalRotation(0 * FastMath.DEG_TO_RAD);
        chaseCamera.setLookAtOffset(new Vector3f(0, 0f, 0));

        chaseCamera.setHideCursorOnRotate(false);
//            chaseCamera.setRotationSpeed(5);
        chaseCamera.setRotationSpeed(0);
        chaseCamera.setZoomSensitivity(4);
        chaseCamera.setMinDistance(cameraDistance / 5f);
        chaseCamera.setMaxDistance(cameraDistance * 5f);

        chaseCamera.setDragToRotate(true);
        chaseCamera.setRotationSensitivity(4);

    }

//    private void initFX() {
//
//        fpp = new FilterPostProcessor(assetManager);
//
//        fXAAFilter = new FXAAFilter();
//        fXAAFilter.setEnabled(true);
//        fpp.addFilter(fXAAFilter);
//
//        baseApplication.getViewPort().addProcessor(fpp);
//    }
    private void initEnvironment() {
        lightProbe = SpatialUtils.loadLightProbe(rootNode, "Models/Probes/Sky_Cloudy.j3o");

        ambientLight = new AmbientLight();
        ambientLight.setColor(ColorRGBA.LightGray);
        rootNode.addLight(ambientLight);

        sunLight = new DirectionalLight();
        sunLight.setColor(ColorRGBA.White);
        sunLight.setDirection(new Vector3f(0.6f, -0.8f, -0.6f).normalizeLocal());
        rootNode.addLight(sunLight);

//        sky = SpatialUtils.addSkySphere(rootNode, ColorUtils.rgb(52, 172, 224), ColorUtils.rgb(223, 249, 251), baseApplication.getCamera());

        shadowFilter = new DirectionalLightShadowFilter(baseApplication.getAssetManager(), 1024, 2);
        shadowFilter.setEdgeFilteringMode(EdgeFilteringMode.Dither);
        shadowFilter.setEdgesThickness(10);
        shadowFilter.setEnabledStabilization(true);
        shadowFilter.setShadowZExtend(50);
        shadowFilter.setShadowIntensity(0.4f);
        shadowFilter.setLight(sunLight);

        fpp = new FilterPostProcessor(baseApplication.getAssetManager());
        baseApplication.getViewPort().addProcessor(fpp);

//        fpp.addFilter(shadowFilter);
        //Smooth edging
        fXAAFilter = new FXAAFilter();
        fXAAFilter.setEnabled(true);
        fpp.addFilter(fXAAFilter);

        //SSAO Ambient oclussion at runtime
        basicSSAOFilter = new SSAOFilter();
        basicSSAOFilter.setBias(0.2f);
        basicSSAOFilter.setIntensity(1.0f);
        basicSSAOFilter.setSampleRadius(0.5f);
        basicSSAOFilter.setScale(0.1f);
        basicSSAOFilter.setEnabled(true);
//        fpp.addFilter(basicSSAOFilter);

        //Water effect
        oceanFilter = new WaterFilter(rootNode, sunLight.getDirection());
        oceanFilter.setWaterHeight(0);
        oceanFilter.setLightColor(sunLight.getColor().clone());
        oceanFilter.setWaterTransparency(0.9f);
        oceanFilter.setWaterColor(ColorUtils.rgb(52, 152, 219));
        oceanFilter.setWaveScale(0.002f);
        oceanFilter.setShininess(0.8f);
        oceanFilter.setSpeed(0.5f);
        oceanFilter.setMaxAmplitude(0.8f);
        oceanFilter.setFoamHardness(1f);
        oceanFilter.setFoamIntensity(0.5f);
        oceanFilter.setFoamExistence(new Vector3f(0.25f, 0.75f, 0.25f));
//        oceanFilter.setShoreHardness(1.0f);
//        fpp.addFilter(oceanFilter);
    }
    
    protected void initGizmos() {
        transformGizmo = new Gizmo("GIZMO", camera, inputManager);
        transformGizmo.setGizmoListener(this);
        
    }

    protected void save() {
        JFileChooser fileChooser = new JFileChooser();

        String destFolder = System.getProperty("user.home");

        fileChooser.setCurrentDirectory(new File(destFolder));
        fileChooser.setDialogTitle("Save Game As");
        fileChooser.setApproveButtonText("Save");
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file != null && (file.getName().endsWith(EditorUtils.SPATIAL_EXTENSION) || file.isDirectory());
            }

            @Override
            public String getDescription() {
                return EditorUtils.SPATIAL_EXTENSION;
            }
        };
        fileChooser.setFileFilter(fileFilter);
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Game to save: " + selectedFile);
            try {
                EditorUtils.saveSpatial(editNode, selectedFile);

            } catch (Exception ex) {
                ex.printStackTrace();

            }

        }

    }

    protected void open() {
        JFileChooser fileChooser = new JFileChooser();
        String destFolder = System.getProperty("user.home");
        if (baseApplication.getGameSaves().getGameData().getProperties().get(EditorUtils.LAST_LOCATION) != null) {
            destFolder = (String) baseApplication.getGameSaves().getGameData().getProperties().get(EditorUtils.LAST_LOCATION);
        }

        fileChooser.setCurrentDirectory(new File(destFolder));
        fileChooser.setDialogTitle("Open Spatial");
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file != null && (file.getName().endsWith(EditorUtils.SPATIAL_EXTENSION) || file.isDirectory());
            }

            @Override
            public String getDescription() {
                return EditorUtils.SPATIAL_EXTENSION;
            }
        };
        fileChooser.setFileFilter(fileFilter);
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            Spatial spatial = EditorUtils.openSpatial(fileChooser.getSelectedFile(), assetManager);
            baseApplication.getGameSaves().getGameData().getProperties().setProperty(EditorUtils.LAST_LOCATION, fileChooser.getSelectedFile().getPath());
            baseApplication.getGameSaves().save();

            if (spatial != null) {
                rootNode.detachAllChildren();
                editNode = (Node) spatial;
                rootNode.attachChild(editNode);

            }

        }
    }

    protected void importObject() {
        JFileChooser fileChooser = new JFileChooser();
        String destFolder = System.getProperty("user.home");
        if (baseApplication.getGameSaves().getGameData().getProperties().get(EditorUtils.LAST_LOCATION) != null) {
            destFolder = (String) baseApplication.getGameSaves().getGameData().getProperties().get(EditorUtils.LAST_LOCATION);
        }

        fileChooser.setCurrentDirectory(new File(destFolder));
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return EditorUtils.isCompatableModel(file) || file.isDirectory();
            }

            @Override
            public String getDescription() {
                return EditorUtils.getCompatableModelExtensions();
            }
        });

        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();

            if (selectedFiles != null && selectedFiles.length > 0) {
                baseApplication.getGameSaves().getGameData().getProperties().setProperty(EditorUtils.LAST_LOCATION, selectedFiles[0].getPath());
                baseApplication.getGameSaves().save();

                for (int i = 0; i < selectedFiles.length; i++) {
                    importModelFromFile(selectedFiles[i]);
                }
            }
        }
    }

    @Override
    public void messageReceived(String message, Object object) {

        if (Action.NEW.equals(message)) {
            newScene();

        } else if (Action.OPEN.equals(message)) {
            open();

        } else if (Action.SAVE.equals(message)) {
            save();

        } else if (Action.ADD.equals(message)) {
            addObject();

        } else if (Action.IMPORT.equals(message)) {
            importObject();

        } else if (Action.HIERARCHY.equals(message)) {
            hierarchyPanel.reload(editNode);
            hierarchyPanel.show();
            
        } else if (Action.SELECT.equals(message)) {
            if (object == null) {
                transformGizmo.setTarget(null);
                transformGizmo.removeFromParent();
                
            } else {
                rootNode.attachChild(transformGizmo);                
                Spatial spatial = (Spatial) object;
                transformGizmo.setLocalTranslation(spatial.getWorldTranslation());
//                transformGizmo.setLocalRotation(spatial.getWorldRotation());
                chaseCameraTarget.setLocalTranslation(spatial.getWorldTranslation().clone());
                selectedSpatial = spatial;
                transformGizmo.setTarget(spatial);

            }


        } else {
            hierarchyPanel.hide();
        }

    }

    private void addObject() {
        Spatial box = SpatialUtils.addBox(editNode, 1, 1, 1);
        box.setName("Box");
        SpatialUtils.addColor(box, ColorRGBA.randomColor(), false);
//        box.move(FastMath.nextRandomInt(-10, 10), 0, FastMath.nextRandomInt(-10, 10));

    }

    private Spatial importModelFromFile(File selectedFile) {
        if (selectedFile != null && EditorUtils.isCompatableModel(selectedFile)) {
            System.out.println("Importing model: " + selectedFile);
            System.out.println("Parent: " + selectedFile.getParent());
            System.out.println("FileName: " + selectedFile.getName());
            baseApplication.getAssetManager().registerLocator(selectedFile.getParent(), FileLocator.class);
            Spatial spatial = baseApplication.getAssetManager().loadModel(selectedFile.getName());
            System.out.println("Model (" + spatial.getName() + ") successfully imported.");
            if (selectedFile.getName().endsWith(".obj")) {
                spatial.setName(selectedFile.getName().replace(".obj", ""));

            } else if (selectedFile.getName().endsWith(".fbx")) {
                spatial.setName(selectedFile.getName().replace(".fbx", ""));

            } else if (selectedFile.getName().endsWith(".gltf")) {
                spatial.setName(selectedFile.getName().replace(".gltf", ""));

            } else {
                spatial.setName(selectedFile.getName());

            }

            //NB
            MaterialUtils.convertTexturesToEmbedded(spatial);
            editNode.attachChild(spatial);
            return spatial;
        }

        return null;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        
        if (isActive()) {
            
            //Calculate distance scale
            distanceScale = transformGizmo.getWorldTranslation().distance(camera.getLocation())/cameraDistance;
//            log("Distance scale: " + distanceScale);
            transformGizmo.setLocalScale(distanceScale);
            
            //Check for input
            if (Input.get("camera-action") > 0) {
//                log("Right mouse down");
                chaseCamera.setRotationSpeed(8);
                
            } else {
                chaseCamera.setRotationSpeed(0);
                
            }
                      
        }
    }
    
    

    @Override
    public void picked(PickEvent pickEvent, float tpf) {
        
        if (pickEvent.isRightButton()) {
            if (pickEvent.isKeyDown()) {
                log("Camera rotate on");
                
            } else {
                log("Camera rotate off");
                
            }
            
        }

        
    }

    @Override
    public void drag(PickEvent pickEvent, float tpf) {
    }

    @Override
    public void gizmoUpdate(Vector3f position, Quaternion rotations, Vector3f scale) {
        if (selectedSpatial != null) {
//            selectedSpatial.setLocalTranslation(position.x, position.y, position.z);
//            selectedSpatial.setLocalRotation(rotations.clone());
            
        }
    }
}
