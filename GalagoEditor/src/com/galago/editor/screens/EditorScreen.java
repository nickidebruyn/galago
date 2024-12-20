package com.galago.editor.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.TweenCallback;
import com.bruynhuis.galago.filters.FXAAFilter;
import com.bruynhuis.galago.input.Input;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.messages.MessageListener;
import com.galago.editor.utils.EditorUtils;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.field.TextArea;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.util.ColorUtils;
import com.bruynhuis.galago.util.SpatialUtils;
import com.galago.editor.camera.EditorFlyCamAppState;
import com.galago.editor.spatial.Gizmo;
import com.galago.editor.spatial.GizmoListener;
import com.galago.editor.spatial.PaintGizmo;
import com.galago.editor.spatial.SelectObjectOutliner;
import com.galago.editor.ui.panels.HierarchyPanel;
import com.galago.editor.ui.panels.TerrainPanel;
import com.galago.editor.ui.panels.ToolbarPanel;
import com.galago.editor.ui.actions.TerrainAction;
import com.galago.editor.ui.dialogs.TerrainDialog;
import com.galago.editor.ui.panels.GeometryPropertiesPanel;
import com.galago.editor.ui.panels.ModelAddPanel;
import com.galago.editor.ui.panels.NodePropertiesPanel;
import com.galago.editor.ui.panels.SkyPanel;
import com.galago.editor.ui.panels.WaterPanel;
import com.galago.editor.utils.Action;
import com.bruynhuis.galago.util.MaterialUtils;
import com.galago.editor.utils.ModelReference;
import com.galago.editor.utils.ModelUtils;
import com.galago.editor.utils.TerrainFlattenTool;
import com.galago.editor.utils.TerrainGrassTool;
import com.galago.editor.utils.TerrainModelTool;
import com.galago.editor.utils.TerrainPaintTool;
import com.galago.editor.utils.TerrainRaiseTool;
import com.galago.editor.utils.TerrainSmoothTool;
import com.galago.editor.utils.TerrainUtils;
import com.jme3.asset.ModelKey;
import com.jme3.asset.TextureKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.terrain.geomipmap.TerrainPatch;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.water.WaterFilter;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author ndebruyn
 */
public class EditorScreen extends AbstractScreen implements MessageListener, PickListener, GizmoListener {

    public static final String NAME = "EditorScreen";

    private ToolbarPanel toolbarPanel;
    private HierarchyPanel hierarchyPanel;
//    private ObjectAddPanel objectAddPanel;
    private ModelAddPanel modelAddPanel;
    private TerrainPanel terrainPanel;
    private WaterPanel waterPanel;
    private SkyPanel skyPanel;
    private GeometryPropertiesPanel geometryPropertiesPanel;
    private NodePropertiesPanel nodePropertiesPanel;
    private Label statusLabel;
    private TouchButton messageBubble;

    private TerrainDialog terrainDialog;

    private TextArea logArea;

    protected AmbientLight ambientLight;
    protected DirectionalLight sunLight;
    protected Spatial sky;
    protected LightProbe lightProbe;
    protected DirectionalLightShadowRenderer shadowRenderer;
    protected DirectionalLightShadowFilter shadowFilter;
    protected FilterPostProcessor fpp;
    protected WaterFilter oceanFilter;
    protected FXAAFilter fXAAFilter;
    protected SSAOFilter basicSSAOFilter;

    protected ChaseCamera chaseCamera;
    protected EditorFlyCamAppState flyCamAppState;
    protected float cameraDistance = 15f;
    protected float distanceScale = 0f;

    protected TouchPickListener touchPickListener;

    private Node gridNode;
    private Spatial gridPlane;
    private Node editNode; //This is the main node if the scene/world. The terrain and scene node will be added to this node.
    private Node sceneNode; //This node is a child node of the main edit node. All standalone objects will be placed here.
    private Gizmo transformGizmo;
    private PaintGizmo paintGizmo;
    private Spatial selectedSpatial;
    private Spatial copySpatial;
    private Node chaseCameraTarget;
    private SelectObjectOutliner outliner;

    private TerrainPaintTool terrainPaintTool = new TerrainPaintTool();
    private TerrainRaiseTool terrainRaiseTool = new TerrainRaiseTool();
    private TerrainFlattenTool terrainFlattenTool = new TerrainFlattenTool();
    private TerrainSmoothTool terrainSmoothTool = new TerrainSmoothTool();
    private TerrainGrassTool terrainGrassTool = new TerrainGrassTool();
    private TerrainModelTool terrainModelTool = new TerrainModelTool();

    private Vector3f flattenPoint = new Vector3f(0, 10, 0);

    private File lastSavedFile;

    private boolean ctrlDown = false;
    private boolean shiftDown = false;
    private boolean leftMouseDown = false;
    private boolean overUI = false;
    private boolean statsVisible = false;
    private boolean gridVisible = true;
    private boolean placingObject = false;
    private String activeToolbarItem;

    @Override
    protected void init() {

        ModelUtils.loadAllModels();

        hierarchyPanel = new HierarchyPanel(hudPanel);
        hierarchyPanel.leftCenter(EditorUtils.TOOLBAR_WIDTH, 0);

//        objectAddPanel = new ObjectAddPanel(hudPanel);
//        objectAddPanel.leftCenter(EditorUtils.TOOLBAR_WIDTH, 0);
        modelAddPanel = new ModelAddPanel(hudPanel);
        modelAddPanel.leftCenter(EditorUtils.TOOLBAR_WIDTH, 0);

        terrainPanel = new TerrainPanel(hudPanel);
        terrainPanel.leftCenter(EditorUtils.TOOLBAR_WIDTH, 0);
        terrainPanel.addHeightMapButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                importTexture(uid);
            }

        });

        terrainPanel.addTerrainTexturesButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                importTexture(uid);
            }

        });

        terrainPanel.addTerrainModelButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                importModel();
            }

        });

        waterPanel = new WaterPanel(hudPanel);
        waterPanel.leftCenter(EditorUtils.TOOLBAR_WIDTH, 0);

        skyPanel = new SkyPanel(hudPanel);
        skyPanel.leftCenter(EditorUtils.TOOLBAR_WIDTH, 0);

        geometryPropertiesPanel = new GeometryPropertiesPanel(hudPanel);
        geometryPropertiesPanel.rightCenter(0, 0);
        geometryPropertiesPanel.addTextureButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                importTexture(uid);
            }

        });

        nodePropertiesPanel = new NodePropertiesPanel(hudPanel);
        nodePropertiesPanel.rightCenter(0, 0);

        statusLabel = new Label(hudPanel, "Status: None", 16, 600, 30);
        statusLabel.setAlignment(TextAlign.RIGHT);
        statusLabel.rightTop(EditorUtils.HIERARCHYBAR_WIDTH + 10, 0);

        terrainDialog = new TerrainDialog(window);

        toolbarPanel = new ToolbarPanel(hudPanel);
        toolbarPanel.leftCenter(0, 0);

        messageBubble = new TouchButton(hudPanel, "message-bubble", "Interface/hierarchy-header.png", 400, 32);
        messageBubble.setTextAlignment(TextAlign.LEFT);
        messageBubble.setText("Message Bubble");
        messageBubble.setTextColor(EditorUtils.theme.getTooltipTextColor());
        messageBubble.setBackgroundColor(EditorUtils.theme.getTooltipColor());
        messageBubble.centerAt(750, -510);

        logArea = new TextArea(hudPanel, "LOG_AREA", "Interface/log-area.png", 800, 300, 10);
        logArea.setFontSize(14);
        logArea.setTextColor(new ColorRGBA(1, 1, 1, 0.75f));
        logArea.setTextAlignment(TextAlign.LEFT);
        logArea.setTextVerticalAlignment(TextAlign.TOP);
//        logArea.setMaxLength(80);
        logArea.setMaxLines(12);
        logArea.leftBottom(EditorUtils.HIERARCHYBAR_WIDTH + EditorUtils.TOOLBAR_WIDTH + 5, 5);
        logArea.setText("Launghing scene editor...");

        touchPickListener = new TouchPickListener("scene-control", camera, rootNode);
        touchPickListener.setPickListener(this);

    }

    @Override
    protected void log(String text) {
        super.log(text);
        logArea.append(" > " + text);
    }

    @Override
    protected void load() {
        baseApplication.getViewPort().setBackgroundColor(EditorUtils.theme.getBackgroundColor());

//        logArea.clear();
        newScene();
        log("Setting up a new scene");

        initEnvironment();
        log("Initialize environment");

        initCameras();
        log("Placing the camera");

        initGizmos();
        log("Loading gizmos");
    }

    @Override
    protected void show() {
        baseApplication.getMessageManager().addMessageListener(this);
        toolbarPanel.reset();
        hidePanels();
        logArea.hide();
        touchPickListener.registerWithInput(inputManager);

        flyCamAppState.setEnabled(true);
        flyCamAppState.getCamera().setDragToRotate(true);
        flyCamAppState.getCamera().setMoveSpeed(15f);
        flyCamAppState.getCamera().setRotationSpeed(2f);

        chaseCamera.setEnabled(false);
        chaseCameraTarget.removeControl(chaseCamera);
    }

    protected void showMessage(String text) {
        messageBubble.setText(text);
//        messageBubble.centerAt(300, -800);
        messageBubble.setVisible(true);
        messageBubble.moveFromToCenter(0, 800, 0, 510, 0.4f, 0, new TweenCallback() {
            @Override
            public void onEvent(int i, BaseTween<?> bt) {
                messageBubble.fadeFromTo(1, 0, 1, 1);

            }
        });

    }

    protected void showPanel(Panel panel) {
        hidePanels();

        panel.show();

        if (panel.equals(hierarchyPanel)) {
            hierarchyPanel.reload(sceneNode);

            transformGizmo.setTarget(null);
            transformGizmo.removeFromParent();
            paintGizmo.removeFromParent();
            activateFlyCam();

        } else if (panel.equals(terrainPanel)) {
            terrainPanel.setTerrain(getTerrain());
            if (terrainPanel.getTerrain() != null) {
                touchPickListener.setTargetNode(getTerrain());

                //Show the paint gizmo
                transformGizmo.setTarget(null);
                transformGizmo.removeFromParent();
                rootNode.attachChild(paintGizmo);

            }

        } else if (panel.equals(waterPanel)) {
            waterPanel.setWaterFilter(oceanFilter);

            transformGizmo.setTarget(null);
            transformGizmo.removeFromParent();
            paintGizmo.removeFromParent();
            activateFlyCam();

        } else if (panel.equals(skyPanel)) {
            skyPanel.setShadowFilter(shadowFilter);
            skyPanel.setAmbientLight(ambientLight);
            skyPanel.setSunLight(sunLight);
            skyPanel.setLightProbe(lightProbe);
            skyPanel.setSky(getSky());

            transformGizmo.setTarget(null);
            transformGizmo.removeFromParent();
            paintGizmo.removeFromParent();
            activateFlyCam();

        } else if (panel.equals(geometryPropertiesPanel)) {
            geometryPropertiesPanel.setGeometry((Geometry) selectedSpatial);

        } else if (panel.equals(nodePropertiesPanel)) {
            nodePropertiesPanel.setNode((Node) selectedSpatial);

        } else {
            transformGizmo.setTarget(null);
            transformGizmo.removeFromParent();
            paintGizmo.removeFromParent();
            touchPickListener.setTargetNode(sceneNode);
            activateFlyCam();

        }

    }

    protected void hidePanels() {
        messageBubble.hide();
        hierarchyPanel.hide();
//        objectAddPanel.hide();
        modelAddPanel.hide();
        terrainPanel.hide();
        waterPanel.hide();
        skyPanel.hide();
        geometryPropertiesPanel.hide();
        nodePropertiesPanel.hide();
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
        if (editNode != null) {
            editNode.removeFromParent();

            if (gridNode.getParent() == null) {
                rootNode.attachChild(gridNode);

            }

            clearSelectedObject();

        } else {
            initGrid();
        }

        editNode = new Node("root");
        rootNode.attachChild(editNode);

        sceneNode = new Node("scene");
        editNode.attachChild(sceneNode);

        loadAmbientLight();
        loadDirectionalLight();
        loadProbeLight();

        lastSavedFile = null;
        showMessage("New scene created!");
    }

    private void initGrid() {
        int gridLines = 1000;
        float lineSpacing = 1;

        gridNode = new Node("grid-node");
        rootNode.attachChild(gridNode);

        gridPlane = SpatialUtils.addPlane(gridNode, gridLines * lineSpacing, gridLines * lineSpacing);
        SpatialUtils.addColor(gridPlane, ColorRGBA.Black, true);
        gridPlane.setCullHint(Spatial.CullHint.Always);

        Grid grid = new Grid(gridLines, gridLines, lineSpacing);
        Geometry gridGeom = new Geometry("gridsmall", grid);
        SpatialUtils.addColor(gridGeom, EditorUtils.theme.getGridColor(), true);
        gridGeom.getMaterial().getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        gridGeom.move(-gridLines * lineSpacing * 0.5f, 0, -gridLines * lineSpacing * 0.5f);
        gridGeom.setShadowMode(RenderQueue.ShadowMode.Off);
        gridNode.attachChild(gridGeom);

        Line line = new Line(new Vector3f(-gridLines * lineSpacing * 0.5f, 0, 0), new Vector3f(gridLines * lineSpacing * 0.5f, 0, 0));
        Geometry g = new Geometry("gridlinex", line);
        SpatialUtils.addColor(g, EditorUtils.theme.getXAxisColor(), true);
        g.getMaterial().getAdditionalRenderState().setLineWidth(3);
        g.setShadowMode(RenderQueue.ShadowMode.Off);
        gridNode.attachChild(g);

        line = new Line(new Vector3f(0, 0, -gridLines * lineSpacing * 0.5f), new Vector3f(0, 0, gridLines * lineSpacing * 0.5f));
        g = new Geometry("gridlinez", line);
        SpatialUtils.addColor(g, EditorUtils.theme.getZAxisColor(), true);
        g.getMaterial().getAdditionalRenderState().setLineWidth(3);
        g.setShadowMode(RenderQueue.ShadowMode.Off);
        gridNode.attachChild(g);

        gridLines = 100;
        lineSpacing = 10;
        grid = new Grid(gridLines, gridLines, lineSpacing);
        gridGeom = new Geometry("gridlarge", grid);
        SpatialUtils.addColor(gridGeom, ColorRGBA.Gray, true);
        gridGeom.getMaterial().getAdditionalRenderState().setLineWidth(1);
        gridGeom.move(-gridLines * lineSpacing * 0.5f, 0.01f, -gridLines * lineSpacing * 0.5f);
        gridGeom.setShadowMode(RenderQueue.ShadowMode.Off);
        gridNode.attachChild(gridGeom);

    }

    private void initCameras() {
        //Load the fly cam
        flyCamAppState = new EditorFlyCamAppState();
        baseApplication.getStateManager().attach(flyCamAppState);

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
        chaseCamera.setMaxDistance(cameraDistance * 100f);

        chaseCamera.setDragToRotate(true);
        chaseCamera.setRotationSensitivity(4);

    }

    private void loadAmbientLight() {
        ambientLight = new AmbientLight();
        editNode.addLight(ambientLight);
    }

    private void loadDirectionalLight() {
        sunLight = new DirectionalLight();
        sunLight.setColor(ColorRGBA.White);
        sunLight.setDirection(new Vector3f(0.3f, -0.6f, -0.2f).normalizeLocal());
        editNode.addLight(sunLight);
    }

    private void loadProbeLight() {
        lightProbe = SpatialUtils.loadLightProbe(editNode, "Models/Probes/Sky_Cloudy.j3o");
        lightProbe.setAreaType(LightProbe.AreaType.Spherical);
        lightProbe.getArea().setRadius(2000);
        lightProbe.getArea().setCenter(new Vector3f(0, 0, 0));
    }

    private void initEnvironment() {

//        sky = SpatialUtils.addSkySphere(rootNode, ColorUtils.rgb(52, 172, 224), ColorUtils.rgb(223, 249, 251), baseApplication.getCamera());
        shadowFilter = new DirectionalLightShadowFilter(baseApplication.getAssetManager(), 1024, 2);
        shadowFilter.setEdgeFilteringMode(EdgeFilteringMode.Dither);
        shadowFilter.setEdgesThickness(10);
        shadowFilter.setEnabledStabilization(true);
        shadowFilter.setRenderBackFacesShadows(false);
        shadowFilter.setShadowZExtend(50);
        shadowFilter.setShadowIntensity(0.4f);
        shadowFilter.setLight(sunLight);

        fpp = new FilterPostProcessor(baseApplication.getAssetManager());
        baseApplication.getViewPort().addProcessor(fpp);

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
        oceanFilter.setWaterHeight(5);
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
        oceanFilter.setEnabled(false);
        oceanFilter.setShoreHardness(1.0f);
        fpp.addFilter(oceanFilter);

        shadowFilter.setEnabled(false);
        fpp.addFilter(shadowFilter);

        //Smooth edging
        fXAAFilter = new FXAAFilter();
        fXAAFilter.setEnabled(true);
        fpp.addFilter(fXAAFilter);
    }

    protected void initGizmos() {
        transformGizmo = new Gizmo("GIZMO", camera, inputManager);
        transformGizmo.setGizmoListener(this);

        paintGizmo = new PaintGizmo("PAINT-GIZMO", camera, inputManager);

        outliner = new SelectObjectOutliner(assetManager);
        outliner.initOutliner(4, EditorUtils.theme.getOutlinerColor(), rootNode);

    }

    protected void save() {
        File selectedFile = null;

        if (lastSavedFile == null) {
            JFileChooser fileChooser = new JFileChooser();

            String destFolder = System.getProperty("user.home");
            if (baseApplication.getGameSaves().getGameData().getProperties().get(EditorUtils.LAST_LOCATION) != null) {
                destFolder = (String) baseApplication.getGameSaves().getGameData().getProperties().get(EditorUtils.LAST_LOCATION);
            }

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
                selectedFile = fileChooser.getSelectedFile();

            }

        } else {
            selectedFile = lastSavedFile;
        }

        //Here I save the scene to the specified file if the file actually exist
        if (selectedFile != null) {
            if (!selectedFile.getAbsolutePath().endsWith(EditorUtils.SPATIAL_EXTENSION)) {
                selectedFile = new File(selectedFile.getAbsolutePath() + EditorUtils.SPATIAL_EXTENSION);

            }
            log("Game to save: " + selectedFile);
            try {

                editNode.setUserData(EditorUtils.POST_PROCESS_FILTER, fpp);
                editNode.setUserData(EditorUtils.CAMERA_POSITION, camera.getLocation());
                editNode.setUserData(EditorUtils.CAMERA_ROTATION, camera.getRotation());

                EditorUtils.saveSpatial(editNode, selectedFile);
                showMessage("File successfully saved!");
                baseApplication.getGameSaves().getGameData().getProperties().setProperty(EditorUtils.LAST_LOCATION, selectedFile.getPath());
                baseApplication.getGameSaves().save();
                lastSavedFile = selectedFile;

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
                editNode.removeFromParent();
                editNode = (Node) spatial;
                rootNode.attachChild(editNode);

                sceneNode = (Node) editNode.getChild("scene");
                if (sceneNode == null) {
                    sceneNode = new Node("scene");
                    editNode.attachChild(sceneNode);
                }

                //Set the lights.
                //Please remember that this editor only handles 1 of each of these lights,
                //Ambient, Directions, LightProbe
                for (int i = 0; i < editNode.getLocalLightList().size(); i++) {
                    Light light = editNode.getLocalLightList().get(i);
                    if (light instanceof AmbientLight) {
                        this.ambientLight = (AmbientLight) light;

                    } else if (light instanceof DirectionalLight) {
                        this.sunLight = (DirectionalLight) light;

                    } else if (light instanceof LightProbe) {
                        this.lightProbe = (LightProbe) light;

                    } else {
                        //TODO: What to do with other lights
                    }

                }

                //Set the fpp
                if (editNode.getUserData(EditorUtils.POST_PROCESS_FILTER) != null) {
                    log("Loading filter post processor...");
                    FilterPostProcessor filterPostProcessor = editNode.getUserData(EditorUtils.POST_PROCESS_FILTER);

                    baseApplication.getViewPort().removeProcessor(fpp);
                    baseApplication.getViewPort().addProcessor(filterPostProcessor);

                    oceanFilter = filterPostProcessor.getFilter(WaterFilter.class);

                    if (oceanFilter != null) {
                        oceanFilter.setLightDirection(sunLight.getDirection());
                        oceanFilter.setLightColor(sunLight.getColor());
                    }

                    DirectionalLightShadowFilter savedShadowFilter = filterPostProcessor.getFilter(DirectionalLightShadowFilter.class);
                    if (savedShadowFilter != null) {
                        filterPostProcessor.removeFilter(savedShadowFilter);

                        shadowFilter.setEdgeFilteringMode(savedShadowFilter.getEdgeFilteringMode());
                        shadowFilter.setEdgesThickness(savedShadowFilter.getEdgesThickness());
                        shadowFilter.setEnabled(savedShadowFilter.isEnabled());
                        shadowFilter.setEnabledStabilization(savedShadowFilter.isEnabledStabilization());
                        shadowFilter.setLambda(savedShadowFilter.getLambda());
                        shadowFilter.setName(savedShadowFilter.getName());
                        shadowFilter.setRenderBackFacesShadows(savedShadowFilter.isRenderBackFacesShadows());
                        shadowFilter.setShadowCompareMode(savedShadowFilter.getShadowCompareMode());
                        shadowFilter.setShadowIntensity(savedShadowFilter.getShadowIntensity());
                        shadowFilter.setShadowZExtend(savedShadowFilter.getShadowZExtend());
                        shadowFilter.setShadowZFadeLength(savedShadowFilter.getShadowZFadeLength());
                        shadowFilter.setLight(sunLight);

                        filterPostProcessor.addFilter(shadowFilter);

                    }

                    fpp = filterPostProcessor;
                }

                //Set the camera
                if (editNode.getUserData(EditorUtils.CAMERA_POSITION) != null) {
                    camera.setLocation(editNode.getUserData(EditorUtils.CAMERA_POSITION));
                    camera.setRotation(editNode.getUserData(EditorUtils.CAMERA_ROTATION));

                }

                lastSavedFile = fileChooser.getSelectedFile();
                showMessage("File successfully opened!");

            }

            hidePanels();

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
                    importModelFromFile(selectedFiles[i], false);
                }
            }
        }
    }

    protected void exportObject(Spatial scene) {
        File selectedFile = null;

        JFileChooser fileChooser = new JFileChooser();

        String destFolder = System.getProperty("user.home");
        if (baseApplication.getGameSaves().getGameData().getProperties().get(EditorUtils.LAST_LOCATION) != null) {
            destFolder = (String) baseApplication.getGameSaves().getGameData().getProperties().get(EditorUtils.LAST_LOCATION);
        }

        fileChooser.setCurrentDirectory(new File(destFolder));
        fileChooser.setDialogTitle("Save Spatial As");
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
            selectedFile = fileChooser.getSelectedFile();

        }

        //Here I save the scene to the specified file if the file actually exist
        if (selectedFile != null) {
            if (!selectedFile.getAbsolutePath().endsWith(EditorUtils.SPATIAL_EXTENSION)) {
                selectedFile = new File(selectedFile.getAbsolutePath() + EditorUtils.SPATIAL_EXTENSION);

            }
            log("Game to save: " + selectedFile);
            try {

                EditorUtils.saveSpatial(scene, selectedFile);
                showMessage("File successfully saved!");
                baseApplication.getGameSaves().getGameData().getProperties().setProperty(EditorUtils.LAST_LOCATION, selectedFile.getPath());
                baseApplication.getGameSaves().save();
//
//                if (selectedSpatial instanceof Geometry) {
//                    Material material = ((Geometry) scene).getMaterial();
//                    String matFilePath = selectedFile.getParent() + File.separator + selectedFile.getName().replace(".j3o", "") + EditorUtils.MATERIAL_EXTENSION;
////                    log(matFilePath);
//                    File materialFile = new File(matFilePath);
//                    EditorUtils.saveMaterial(material, materialFile);
//                    log("Done saving material, " + matFilePath);
//
//                }

            } catch (Exception ex) {
                ex.printStackTrace();

            }

        }

    }

    protected void importModel() {
        JFileChooser fileChooser = new JFileChooser();
        String destFolder = System.getProperty("user.home");
        if (baseApplication.getGameSaves().getGameData().getProperties().get(EditorUtils.LAST_LOCATION) != null) {
            destFolder = (String) baseApplication.getGameSaves().getGameData().getProperties().get(EditorUtils.LAST_LOCATION);
        }

        fileChooser.setCurrentDirectory(new File(destFolder));
//        fileChooser.setMultiSelectionEnabled(true);
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
            File selectedFile = fileChooser.getSelectedFile();

            if (selectedFile != null) {
                baseApplication.getGameSaves().getGameData().getProperties().setProperty(EditorUtils.LAST_LOCATION, selectedFile.getPath());
                baseApplication.getGameSaves().save();

                importModelFromFile(selectedFile, true);
            }
        }
    }

    protected void importMaterial(Spatial spatial) {
        JFileChooser fileChooser = new JFileChooser();

        String destFolder = System.getProperty("user.home");
        if (baseApplication.getGameSaves().getGameData().getProperties().get(EditorUtils.LAST_LOCATION) != null) {
            destFolder = (String) baseApplication.getGameSaves().getGameData().getProperties().get(EditorUtils.LAST_LOCATION);
        }

        fileChooser.setCurrentDirectory(new File(destFolder));
        fileChooser.setDialogTitle("Open Material");
        fileChooser.setApproveButtonText("Open");
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file != null && (file.getName().endsWith(EditorUtils.MATERIAL_EXTENSION) || file.isDirectory());
            }

            @Override
            public String getDescription() {
                return EditorUtils.MATERIAL_EXTENSION;
            }
        };
        fileChooser.setFileFilter(fileFilter);
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            if (selectedFile != null) {
                baseApplication.getGameSaves().getGameData().getProperties().setProperty(EditorUtils.LAST_LOCATION, selectedFile.getPath());
                baseApplication.getGameSaves().save();

                log("Importing material: " + selectedFile);
                log("Parent: " + selectedFile.getParent());
                log("FileName: " + selectedFile.getName());

                Material m = null;
                try {
                    m = EditorUtils.loadMaterial(selectedFile, assetManager);
                    log("Material (" + m.getName() + ") successfully imported.");

                    if (spatial != null && m != null) {
                        spatial.setMaterial(m);
                        MaterialUtils.convertTexturesToEmbedded(m);
                        TangentBinormalGenerator.generate(spatial);
                        setSelectedObject(spatial);

                    }

                } catch (Exception ex) {
                    Logger.getLogger(EditorScreen.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

    }

    protected void exportMaterial(final Material material) {
        File selectedFile = null;

        JFileChooser fileChooser = new JFileChooser();

        String destFolder = System.getProperty("user.home");
        if (baseApplication.getGameSaves().getGameData().getProperties().get(EditorUtils.LAST_LOCATION) != null) {
            destFolder = (String) baseApplication.getGameSaves().getGameData().getProperties().get(EditorUtils.LAST_LOCATION);
        }

        fileChooser.setCurrentDirectory(new File(destFolder));
        fileChooser.setDialogTitle("Save Material");
        fileChooser.setApproveButtonText("Save");
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file != null && (file.getName().endsWith(EditorUtils.MATERIAL_EXTENSION) || file.isDirectory());
            }

            @Override
            public String getDescription() {
                return EditorUtils.MATERIAL_EXTENSION;
            }
        };
        fileChooser.setFileFilter(fileFilter);
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();

        }

        //Here I save the scene to the specified file if the file actually exist
        if (selectedFile != null) {
            if (!selectedFile.getAbsolutePath().endsWith(EditorUtils.MATERIAL_EXTENSION)) {
                selectedFile = new File(selectedFile.getAbsolutePath() + EditorUtils.MATERIAL_EXTENSION);

            }
            log("Material to save: " + selectedFile);
            try {

                EditorUtils.saveMaterial(material, selectedFile);
                showMessage("Material " + selectedFile.getName() + " successfully saved!");
                baseApplication.getGameSaves().getGameData().getProperties().setProperty(EditorUtils.LAST_LOCATION, selectedFile.getPath());
                baseApplication.getGameSaves().save();

            } catch (Exception ex) {
                ex.printStackTrace();

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

        } else if (Action.UI_OVER.equals(message)) {
            overUI = true;

        } else if (Action.UI_OFF.equals(message)) {
            overUI = false;

        } else if (Action.ADD_SKY.equals(message)) {
            if (getSky() == null) {
                addSky();
            } else {
                getSky().removeFromParent();
            }

            showPanel(skyPanel);

        } else if (Action.ADD.equals(message)) {

            if (object == null) {
                showPanel(modelAddPanel);
                setActiveToolbarItem(Action.ADD);

            } else {
                addObject((String) object);

                if (getTerrain() == null) {
                    touchPickListener.setTargetNode(gridNode);
                } else {
                    touchPickListener.setTargetNode(getTerrain());
                }

            }

        } else if (Action.ADD_DONE.equals(message)) {
            log("Done adding the new object");
            placeSelectedObject();

        } else if (Action.STATS.equals(message)) {
            statsVisible = !statsVisible;

            logArea.setVisible(statsVisible);

            if (statsVisible) {
                baseApplication.showStats();

            } else {
                baseApplication.hideStats();
            }

        } else if (Action.GRID.equals(message)) {
            gridVisible = !gridVisible;

            if (gridVisible && gridNode.getParent() == null) {
                rootNode.attachChild(gridNode);

            } else {
                gridNode.removeFromParent();

            }

        } else if (Action.TERRAIN.equals(message)) {
            setActiveToolbarItem(Action.TERRAIN);
            showPanel(terrainPanel);

        } else if (Action.TERRAIN_MODEL_EDIT.equals(message)) {
            if (object != null) {
                if (object instanceof Geometry) {

                } else if (object instanceof Node) {

                }

            }

        } else if (Action.TERRAIN_MODEL_CLEAR.equals(message)) {

        } else if (Action.CREATE_TERRAIN.equals(message)) {
            TerrainAction terrainAction = (TerrainAction) object;
            log("Creating terrain,..." + terrainAction.getTerrainSize());

            TerrainQuad existingTerrain = getTerrain();
            if (existingTerrain != null) {
                existingTerrain.removeFromParent();
            }

            TerrainQuad terrain = null;

            if (terrainAction.getType() == TerrainAction.TYPE_FLAT) {
                terrain = TerrainUtils.generateFlatTerrain(assetManager, camera, terrainAction.getTerrainSize());

            } else if (terrainAction.getType() == TerrainAction.TYPE_ISLAND) {
                terrain = TerrainUtils.generateIslandTerrain(assetManager, camera,
                        terrainAction.getTerrainSize(), terrainAction.getIterations(),
                        terrainAction.getMinRadius(), terrainAction.getMaxRadius(),
                        terrainAction.getSeed());

            } else if (terrainAction.getType() == TerrainAction.TYPE_MIDPOINT) {

                terrain = TerrainUtils.generateMidpointTerrain(assetManager, camera,
                        terrainAction.getTerrainSize(), terrainAction.getRange(),
                        terrainAction.getPersistence(),
                        terrainAction.getSeed());

            } else if (terrainAction.getType() == TerrainAction.TYPE_IMAGE) {

                if (terrainAction.getHeightMapTexture() != null) {
                    terrain = TerrainUtils.generateImageTerrain(assetManager, camera, terrainAction.getHeightMapTexture().getImage());
                }

            }

            if (terrain != null) {
                terrain.setShadowMode(RenderQueue.ShadowMode.Receive);
                Material material = null;

                if (terrainAction.getTerrainMaterial() == TerrainAction.MATERIAL_PAINTABLE) {
                    log("Generated paintable terrain material");
                    try {
                        material = TerrainUtils.generatePaintableTerrainMaterial(assetManager, terrainAction.getTerrainSize() * 4);
                    } catch (IOException ex) {
                        Logger.getLogger(EditorScreen.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else if (terrainAction.getTerrainMaterial() == TerrainAction.MATERIAL_HEIGHT_BASED) {
                    log("Generated lit height based terrain material");
                    material = TerrainUtils.generateLitHeightBasedMaterial(assetManager, terrainAction.getTerrainSize());

                } else if (terrainAction.getTerrainMaterial() == TerrainAction.MATERIAL_PBR) {
                    log("Generated paintable PBR terrain material");
                    try {
                        material = TerrainUtils.generatePaintablePBRTerrainMaterial(assetManager, terrainAction.getTerrainSize() * 4);
                    } catch (IOException ex) {
                        Logger.getLogger(EditorScreen.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

                terrain.setMaterial(material);

                //2022-12-10: Terrain batch nodes such as grass/trees/etc
                //We attach the vegetation batches for each on the terrain
                //GRASS1:
                BatchNode grassNode1 = new BatchNode(TerrainAction.BATCH_GRASS1);
                float heightScale = 1f / terrain.getLocalScale().y;
                grassNode1.setLocalScale(1, heightScale, 1);
                terrain.attachChild(grassNode1);
                Node grassModel1 = (Node) assetManager.loadModel("Models/vegetation/grass1.j3o");
                grassModel1.getChild(0).setMaterial(MaterialUtils.createGrassMaterial(assetManager, "Textures/vegetation/grass-blades.png", 0.8f, new Vector2f(0, 0)));
                grassNode1.setUserData(EditorUtils.MODEL, grassModel1.getChild(0));
                MaterialUtils.convertTextureToEmbeddedByName(((Geometry) grassModel1.getChild(0)).getMaterial(), "DiffuseMap");

                //GRASS2:
                BatchNode grassNode2 = new BatchNode(TerrainAction.BATCH_GRASS2);
                grassNode2.setLocalScale(1, heightScale, 1);
                terrain.attachChild(grassNode2);
                Node grassModel2 = (Node) assetManager.loadModel("Models/vegetation/grass2.j3o");
                grassModel2.getChild(0).setMaterial(MaterialUtils.createGrassMaterial(assetManager, "Textures/vegetation/grass-blades2.png", 0.8f, new Vector2f(0, 0)));
                grassNode2.setUserData(EditorUtils.MODEL, grassModel2.getChild(0));
                MaterialUtils.convertTextureToEmbeddedByName(((Geometry) grassModel2.getChild(0)).getMaterial(), "DiffuseMap");

                //GRASS3:
                BatchNode grassNode3 = new BatchNode(TerrainAction.BATCH_GRASS3);
                grassNode3.setLocalScale(1, heightScale, 1);
                terrain.attachChild(grassNode3);
                Node grassModel3 = (Node) assetManager.loadModel("Models/vegetation/grass3.j3o");
                grassModel3.getChild(0).setMaterial(MaterialUtils.createGrassMaterial(assetManager, "Textures/vegetation/grass-blades3.png", 0.8f, new Vector2f(0, 0)));
                grassNode3.setUserData(EditorUtils.MODEL, grassModel3.getChild(0));
                MaterialUtils.convertTextureToEmbeddedByName(((Geometry) grassModel3.getChild(0)).getMaterial(), "DiffuseMap");

                //TREE1:
                BatchNode treeNode1 = new BatchNode(TerrainAction.BATCH_TREES1);
//                treeNode1.setQueueBucket(RenderQueue.Bucket.Transparent);
                treeNode1.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                treeNode1.setLocalScale(1, heightScale, 1);
                terrain.attachChild(treeNode1);
                Node treeModel1 = (Node) assetManager.loadModel("Models/trees/pine_tree/scene.j3o");
                treeNode1.setUserData(EditorUtils.MODEL, treeModel1);
                treeModel1.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                MaterialUtils.convertTexturesToEmbedded(treeModel1);
//                
//                for (int i = 0; i < 1000; i++) {
//                    Spatial clone = treeModel1.clone(false);
//                    clone.setLocalTranslation(FastMath.nextRandomInt(-200, 200), 0, FastMath.nextRandomInt(-200, 200));
//                    treeNode1.attachChild(clone);
//                }

//                treeNode1.instance();
                //TODO:
//                MaterialUtils.convertTextureToEmbeddedByName(((Geometry) grassModel3.getChild(0)).getMaterial(), "DiffuseMap");
                editNode.attachChild(terrain);

//                TangentBinormalGenerator.generate(terrain);
            }

            terrainPanel.setTerrain(getTerrain());

            showPanel(terrainPanel);

        } else if (Action.WATER.equals(message)) {
            showPanel(waterPanel);

        } else if (Action.SKY.equals(message)) {
            showPanel(skyPanel);

        } else if (Action.IMPORT.equals(message)) {
            hidePanels();
            importObject();

        } else if (Action.IMPORT_MATERIAL.equals(message)) {
            if (object != null && object instanceof Geometry) {
                importMaterial((Geometry) object);
            }

        } else if (Action.EXPORT.equals(message)) {
            if (selectedSpatial != null) {
                exportObject(selectedSpatial);
            } else {
                exportObject(sceneNode);
            }

        } else if (Action.EXPORT_MATERIAL.equals(message)) {
            if (object != null && object instanceof Material) {
                exportMaterial((Material) object);

            }

        } else if (Action.HIERARCHY.equals(message)) {
            showPanel(hierarchyPanel);

        } else if (Action.UPDATE_OBJECT.equals(message)) {
            transformGizmo.setLocalTranslation(selectedSpatial.getWorldTranslation());

        } else if (Action.SELECT.equals(message)) {
            if (object == null) {

                this.clearSelectedObject();
                this.setActiveToolbarItem(Action.SELECT);
                touchPickListener.setTargetNode(sceneNode);

            } else {

                Spatial spatial = (Spatial) object;
                log("Spatial selection: " + spatial);
                this.setSelectedObject(spatial);

            }

            paintGizmo.removeFromParent();

        } else if (Action.PAINT.equals(message)) {

//            transformGizmo.setTarget(null);
//            transformGizmo.removeFromParent();
//
//            rootNode.attachChild(paintGizmo);
        } else if (Action.AUTO_PAINT.equals(message)) {

            TerrainAction terrainAction = (TerrainAction) object;

            TerrainQuad terrain = getTerrain();
            BoundingBox bb = (BoundingBox) terrain.getWorldBound();
            log("Auto paint: " + terrain.getWorldBound());

            int sizeX = (int) bb.getXExtent();
            int addAmount = (int) terrainPanel.getPaintRadius();
            CollisionResults results = new CollisionResults();
            Ray ray = new Ray();

//            float normalAngle = 0.8f;
            for (int x = -sizeX; x < sizeX; x += 1) {

                for (int z = -sizeX; z < sizeX; z += 1) {

                    if ((x % addAmount == 0) && (z % addAmount == 0)) {
                        float height = terrain.getHeight(new Vector2f(x, z));

                        if (height >= terrainAction.getAutoStartHeight()) {
                            results.clear();

                            ray.setOrigin(new Vector3f(x, 300, z));
                            ray.setDirection(new Vector3f(0, -1000, 0));

                            // 3. Collect intersections between Ray and Shootables in results list.
                            terrain.collideWith(ray, results);

                            // 5. Use the results (we mark the hit object)
                            if (results.size() > 0) {
                                CollisionResult cr = results.getCollision(0);
                                if (cr.getContactNormal().y <= terrainAction.getAutoPaintMax()
                                        && cr.getContactNormal().y >= terrainAction.getAutoPaintMin()) {
                                    terrainPaintTool.paintTexture(terrain, new Vector3f(x, height, z), terrainPanel.getPaintRadius(), terrainPanel.getPaintStrength(), terrainPanel.getSelectedLayer());

                                }

                            }
                        }

                    }
                }
            }

        } else {
            hidePanels();
        }

    }

    /**
     * Helper method that will return the current terrain.
     *
     * @return
     */
    protected TerrainQuad getTerrain() {
        TerrainQuad terrain = null;
        for (int i = 0; i < editNode.getQuantity(); i++) {
            Spatial s = editNode.getChild(i);

            if (s instanceof TerrainQuad) {
                terrain = (TerrainQuad) s;
                break;
            }

        }

        return terrain;
    }

    /**
     * Get the sky of this scene.
     *
     * @return
     */
    protected Geometry getSky() {
        Geometry skyGeom = null;
        for (int i = 0; i < editNode.getQuantity(); i++) {
            Spatial s = editNode.getChild(i);

            if (s instanceof Geometry && s.getName().equals("sky")) {
                skyGeom = (Geometry) s;
                break;
            }

        }

        return skyGeom;
    }

    /**
     * Add a sky to the scene
     */
    private void addSky() {
        if (getSky() == null) {
            Sphere sphere = new Sphere(50, 50, 500, false, true);
            Geometry sky = new Geometry("sky", sphere);
            sky.setQueueBucket(RenderQueue.Bucket.Sky);
            sky.setCullHint(Spatial.CullHint.Never);
            sky.setModelBound(new BoundingSphere(Float.POSITIVE_INFINITY, Vector3f.ZERO));
            //TODO: Need to find a solution to make the sky stick to the camera
            //sky.addControl(new CameraStickControl(camera));

            Material m = new Material(assetManager, "Resources/MatDefs/lineargradient.j3md");
            m.setColor("StartColor", ColorUtils.rgb(2, 107, 187));
            m.setColor("EndColor", ColorUtils.rgb(190, 221, 253));
            m.setFloat("MinStep", 0.27f);
            m.setFloat("MaxStep", 0.47f);
            m.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
            sky.setMaterial(m);

            SpatialUtils.rotate(sky, -90, 0, 0);

            editNode.attachChild(sky);

        }
    }

    /**
     * Called when the user start or clicks down to add an object to the scene
     *
     * @param modelName
     */
    private void addObject(String modelName) {

        log("Adding a new model, " + modelName);

        ModelReference modelReference = ModelUtils.getModelByName(modelName);
        if (modelReference != null) {
            Spatial s = modelReference.getModel().clone(false); //TODO: Can set it to not clone the material
            setupNewObject(s);

        }

    }

    private void setupNewObject(Spatial s) {
        s.setUserData(EditorUtils.GUID, UUID.randomUUID().toString());
        TangentBinormalGenerator.generate(s);
        rootNode.attachChild(s);

        //Remove the previous selected object from the outliner
        if (selectedSpatial != null) {
            outliner.deselect(selectedSpatial);
        }

        selectedSpatial = s;
        placingObject = true;

        transformGizmo.setTarget(null);
        transformGizmo.removeFromParent();
    }

    /**
     * This method will place the object that is being dragged into the scene at
     * its location. It will reparent the object.
     */
    private void placeSelectedObject() {
        if (placingObject && selectedSpatial != null && selectedSpatial.getParent().equals(rootNode)) {
            log("Placing selected object, " + selectedSpatial.getName());
            placingObject = false;
            selectedSpatial.removeFromParent();
            sceneNode.attachChild(selectedSpatial);

            setSelectedObject(selectedSpatial);
            modelAddPanel.show();

        }
    }

    private Spatial importModelFromFile(File selectedFile, boolean terrainModel) {
        if (selectedFile != null && EditorUtils.isCompatableModel(selectedFile)) {
            log("Importing model: " + selectedFile);
            log("Parent: " + selectedFile.getParent());
            log("FileName: " + selectedFile.getName());

            baseApplication.getAssetManager().registerLocator(selectedFile.getParent(), FileLocator.class);
            ModelKey key = new ModelKey(selectedFile.getName());
            Spatial m = baseApplication.getAssetManager().loadModel(key);

            baseApplication.getAssetManager().deleteFromCache(key);
            baseApplication.getAssetManager().unregisterLocator(selectedFile.getParent(), FileLocator.class);

            //Name the file the name of the folder
//            if (selectedFile.getParentFile() != null) {
//                selectedFile = selectedFile.getParentFile();
//            }
            if (selectedFile.getName().endsWith(".obj")) {
                m.setName(selectedFile.getName().replace(".obj", ""));

            } else if (selectedFile.getName().endsWith(".fbx")) {
                m.setName(selectedFile.getName().replace(".fbx", ""));

            } else if (selectedFile.getName().endsWith(".gltf")) {
                m.setName(selectedFile.getName().replace(".gltf", ""));

            } else {
                m.setName(selectedFile.getName());

            }

            log("Model (" + m.getName() + ") successfully imported.");

            //NB
            MaterialUtils.convertTexturesToEmbedded(m);
            TangentBinormalGenerator.generate(m);

            if (terrainModel) {
                terrainPanel.setInstanceModel(m);
            } else {
                m.setUserData(EditorUtils.GUID, UUID.randomUUID().toString());
                sceneNode.attachChild(m);
                setSelectedObject(m);

            }

            return m;
        }

        return null;
    }

    private void importTexture(String uid) {
        log("Importing a texture for: " + uid);

        JFileChooser fileChooser = new JFileChooser();
        String destFolder = System.getProperty("user.home");
        if (baseApplication.getGameSaves().getGameData().getProperties().get(EditorUtils.LAST_LOCATION) != null) {
            destFolder = (String) baseApplication.getGameSaves().getGameData().getProperties().get(EditorUtils.LAST_LOCATION);
        }

        fileChooser.setCurrentDirectory(new File(destFolder));
        fileChooser.setDialogTitle("Open Image");
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file != null && (EditorUtils.isCompatableTexture(file) || file.isDirectory());
            }

            @Override
            public String getDescription() {
                return EditorUtils.getCompatableTextureExtensions();
            }
        };
        fileChooser.setFileFilter(fileFilter);
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            log("Selected image: " + fileChooser.getSelectedFile());

            File file = fileChooser.getSelectedFile();

            baseApplication.getGameSaves().getGameData().getProperties().setProperty(EditorUtils.LAST_LOCATION, file.getPath());
            baseApplication.getGameSaves().save();

            assetManager.registerLocator(file.getParent(), FileLocator.class);
            Texture texture = assetManager.loadTexture(new TextureKey(file.getName(), false));
            texture.setKey(null); //Set the key to null so that it can be embedded

            if (EditorUtils.BASE_TEXTURE.equals(uid)) {
                geometryPropertiesPanel.setBaseTexture(texture);

            } else if (EditorUtils.NORMAL_TEXTURE.equals(uid)) {
                geometryPropertiesPanel.setNormalTexture(texture);

            } else if (EditorUtils.METALIC_TEXTURE.equals(uid)) {
                geometryPropertiesPanel.setMetalicTexture(texture);

            } else if (terrainPanel.getTerrainAction().getTool() == TerrainAction.TOOL_GRASS1) {
                //2022-12-10: If the tool that is selected is the grass tool then we should set the texture to grass file selected
                //The uid in this case will be the DiffuseMap or NormalMap
                terrainPanel.setGrassTexture(uid, texture, TerrainAction.TOOL_GRASS1, TerrainAction.BATCH_GRASS1);

            } else if (terrainPanel.getTerrainAction().getTool() == TerrainAction.TOOL_GRASS2) {
                //2022-12-10: If the tool that is selected is the grass tool then we should set the texture to grass file selected
                //The uid in this case will be the DiffuseMap or NormalMap
                terrainPanel.setGrassTexture(uid, texture, TerrainAction.TOOL_GRASS2, TerrainAction.BATCH_GRASS2);

            } else if (terrainPanel.getTerrainAction().getTool() == TerrainAction.TOOL_GRASS3) {
                //2022-12-13: If the tool that is selected is the grass tool then we should set the texture to grass file selected
                //The uid in this case will be the DiffuseMap or NormalMap
                terrainPanel.setGrassTexture(uid, texture, TerrainAction.TOOL_GRASS3, TerrainAction.BATCH_GRASS3);

            } else {
                //2022-11-01: Set the texture on the terrain
                if (uid.contains("heightmap")) {
                    terrainPanel.setHeightmapTexture(texture);
                } else {
                    //The uid in this case will be the DiffuseMap or NormalMap
                    terrainPanel.setTerrainMaterialTexture(uid, texture);
                }
            }

        }

    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (isActive()) {

            //Calculate distance scale
            distanceScale = transformGizmo.getWorldTranslation().distance(camera.getLocation()) / cameraDistance;
//            log("Distance scale: " + distanceScale);
            transformGizmo.setLocalScale(distanceScale);

            //Check for input
            if (Input.get("camera-action") > 0) {
//                log("Right mouse down");
                chaseCamera.setRotationSpeed(8);

            } else {
                chaseCamera.setRotationSpeed(0);

            }

            //Check for the delete action
            if (Input.get("delete") == 1) {
                if (selectedSpatial != null) {
                    selectedSpatial.removeFromParent();
                    clearSelectedObject();
                    hierarchyPanel.reload(editNode);
                }
            }

            if (Input.get("ctrl") == 1) {
                ctrlDown = true;
            } else if (Input.get("ctrl") == -1) {
                ctrlDown = false;

            }

            if (Input.get("shift") == 1) {
//                toolbarPanel.setSelectedButtonByName(Action.SELECT);
                shiftDown = true;
                touchPickListener.setTargetNode(sceneNode); //Set the scene as the target

            } else if (Input.get("shift") == -1) {
                shiftDown = false;

            }

            //Check for copy and past
            if (ctrlDown && selectedSpatial != null && !placingObject) {
                if (Input.get("copy") == 1) {
                    //Make a copy of the selected object
                    log("Copy an object: " + selectedSpatial.getName());
                    //copySpatial = selectedSpatial;
                    setupNewObject(selectedSpatial.clone(false));
                    if (getTerrain() == null) {
                        touchPickListener.setTargetNode(gridNode);
                    } else {
                        touchPickListener.setTargetNode(getTerrain());
                    }
                    Input.consume("copy");
                }
            }

            //Check for paste
//            if (ctrlDown && copySpatial != null && !placingObject) {
//
//                if (Input.get("paste") == -1) {
//                    //Paste the copied object
//                    log("Paste an object: " + copySpatial.getName());
//                    setupNewObject(copySpatial.clone(false));
//                    if (getTerrain() == null) {
//                        touchPickListener.setTargetNode(gridNode);
//                    } else {
//                        touchPickListener.setTargetNode(getTerrain());
//                    }
//
//                    Input.consume("paste");
//                }
//
//            }
        }
    }

    @Override
    public void picked(PickEvent pickEvent, float tpf) {

        if (pickEvent.isRightButton()) {
            if (pickEvent.isKeyDown()) {
                //log("Camera rotate on");

            } else {
                //log("Camera rotate off");

            }

        }

        if (pickEvent.isLeftButton()) {
            leftMouseDown = pickEvent.isKeyDown();

            if (leftMouseDown) {
                getWindow().removeFocusFromFields();

            }

            if (!pickEvent.isKeyDown()) {
                placeSelectedObject();
            }

            //If we are doing terrain editing
            if (pickEvent.getContactPoint() != null && paintGizmo.getParent() != null) {
                flattenPoint.setY(pickEvent.getContactPoint().y);
            }

            //This is when we want to select an object in the scene
            if (isObjectSelectionActive() || shiftDown) {
                if (leftMouseDown) {
                    log("Picking object: " + pickEvent.getContactObject());

                    //Avoid selection of the terrain
                    if (!(pickEvent.getContactObject() instanceof TerrainPatch)) {

                        if (ctrlDown) {
                            setSelectedObject(pickEvent.getContactObject());
                        } else {
                            setSelectedObject(findRootNodeForSelection(pickEvent.getContactObject()));
                        }

                    }

                }

            }
        }

    }

    @Override
    public void drag(PickEvent pickEvent, float tpf) {

        if (pickEvent.getContactPoint() != null && paintGizmo.getParent() != null && isCursorOnTerrain(pickEvent)) {
            statusLabel.setText("Terrain: (" + pickEvent.getContactPoint().x + ", "
                    + pickEvent.getContactPoint().y + ", "
                    + pickEvent.getContactPoint().z + ")");

            paintGizmo.setLocalTranslation(pickEvent.getContactPoint());
            paintGizmo.getLocalRotation().lookAt(pickEvent.getContactNormal(), Vector3f.UNIT_Y);
            paintGizmo.setRadius(terrainPanel.getPaintRadius());

            if (leftMouseDown && !overUI) {

                if (terrainPanel.isPaintable() && terrainPanel.getTerrainAction().getTool() == TerrainAction.TOOL_PAINT) {
                    terrainPaintTool.paintTexture(getTerrain(),
                            paintGizmo.getWorldTranslation(),
                            terrainPanel.getPaintRadius(),
                            ctrlDown ? (terrainPanel.getPaintStrength() * -1f) : (terrainPanel.getPaintStrength()),
                            terrainPanel.getSelectedLayer());

                } else if (terrainPanel.getTerrainAction().getTool() == TerrainAction.TOOL_RAISE) {
                    log("Modifying terrain height, " + terrainPanel.getPaintRadius());
                    terrainRaiseTool.modifyHeight(getTerrain(),
                            paintGizmo.getWorldTranslation(),
                            terrainPanel.getPaintRadius(),
                            ctrlDown ? (terrainPanel.getPaintStrength() * -1f) : (terrainPanel.getPaintStrength()),
                            TerrainRaiseTool.Meshes.Sphere);

                } else if (terrainPanel.getTerrainAction().getTool() == TerrainAction.TOOL_FLATTEN) {
                    terrainFlattenTool.modifyHeight(getTerrain(), flattenPoint,
                            paintGizmo.getWorldTranslation(),
                            terrainPanel.getPaintRadius(),
                            terrainPanel.getPaintStrength(),
                            false,
                            TerrainRaiseTool.Meshes.Sphere);

                } else if (terrainPanel.getTerrainAction().getTool() == TerrainAction.TOOL_SMOOTH) {
                    terrainSmoothTool.modifyHeight(getTerrain(),
                            paintGizmo.getWorldTranslation(),
                            terrainPanel.getPaintRadius(),
                            terrainPanel.getPaintStrength(),
                            TerrainRaiseTool.Meshes.Sphere);

                } else if (terrainPanel.getTerrainAction().getTool() == TerrainAction.TOOL_GRASS1) {
                    terrainGrassTool.paintGrass(getTerrain(),
                            terrainPanel.getSelectedBatchLayer(),
                            paintGizmo.getWorldTranslation(),
                            terrainPanel.getPaintRadius(),
                            ctrlDown ? (terrainPanel.getPaintStrength() * -1f) : (terrainPanel.getPaintStrength()),
                            terrainPanel.getTerrainAction().getScaleFactor(),
                            terrainPanel.getSelectedGrass());

                } else if (terrainPanel.getTerrainAction().getTool() == TerrainAction.TOOL_GRASS2) {
                    terrainGrassTool.paintGrass(getTerrain(),
                            terrainPanel.getSelectedBatchLayer(),
                            paintGizmo.getWorldTranslation(),
                            terrainPanel.getPaintRadius(),
                            ctrlDown ? (terrainPanel.getPaintStrength() * -1f) : (terrainPanel.getPaintStrength()),
                            terrainPanel.getTerrainAction().getScaleFactor(),
                            terrainPanel.getSelectedGrass());

                } else if (terrainPanel.getTerrainAction().getTool() == TerrainAction.TOOL_GRASS3) {
                    terrainGrassTool.paintGrass(getTerrain(),
                            terrainPanel.getSelectedBatchLayer(),
                            paintGizmo.getWorldTranslation(),
                            terrainPanel.getPaintRadius(),
                            ctrlDown ? (terrainPanel.getPaintStrength() * -1f) : (terrainPanel.getPaintStrength()),
                            terrainPanel.getTerrainAction().getScaleFactor(),
                            terrainPanel.getSelectedGrass());

                } else if (terrainPanel.getTerrainAction().getTool() == TerrainAction.TOOL_TREES1) {
                    log("Painting some trees1...");
                    terrainModelTool.paintModel(getTerrain(),
                            terrainPanel.getSelectedInstancedNode(),
                            paintGizmo.getWorldTranslation(),
                            terrainPanel.getPaintRadius(),
                            ctrlDown ? (terrainPanel.getPaintStrength() * -1f) : (terrainPanel.getPaintStrength()),
                            terrainPanel.getTerrainAction().getScaleFactor(),
                            terrainPanel.getSelectedModel());

                }

            }

        } else {
            statusLabel.setText("Screen: (" + pickEvent.getCursorPosition().x + ", "
                    + pickEvent.getCursorPosition().y + ")");

            //Check if we are busy placing a model
            if (placingObject) {
                log("Busy placing object, " + selectedSpatial.getName() + ", pos:" + pickEvent.getContactPoint());
                if (pickEvent.getContactPoint() != null) {

                    if (ctrlDown) {
                        selectedSpatial.setLocalTranslation((int) pickEvent.getContactPoint().x,
                                (int) pickEvent.getContactPoint().y,
                                (int) pickEvent.getContactPoint().z);
                    } else {
                        selectedSpatial.setLocalTranslation(pickEvent.getContactPoint().x,
                                pickEvent.getContactPoint().y,
                                pickEvent.getContactPoint().z);
                    }

                }

            }

        }

    }

    @Override
    public void gizmoUpdate(Vector3f position, Quaternion rotations, Vector3f scale) {
        if (selectedSpatial != null) {

            if (selectedSpatial instanceof Geometry) {
                geometryPropertiesPanel.setGeometry((Geometry) selectedSpatial);

            } else if (selectedSpatial instanceof Node) {
                nodePropertiesPanel.setNode((Node) selectedSpatial);

            }

            outliner.updatePosition(selectedSpatial);
            outliner.updateRotation(selectedSpatial);
            outliner.updateScale(selectedSpatial);

//            selectedSpatial.setLocalTranslation(position.x, position.y, position.z);
//            selectedSpatial.setLocalRotation(rotations.clone());
        }
    }

    //2022-12-13: This method will help to check if the cursor is over 
    //the terrain for painting and not over the panel area.
    private boolean isCursorOnTerrain(PickEvent pickEvent) {
        if (pickEvent.getCursorPosition().x > (terrainPanel.getWidth() + toolbarPanel.getWidth() + 10)) {
            return true;
        } else {
            return false;
        }
    }

    private void activateFlyCam() {
        flyCamAppState.setEnabled(true);
        chaseCamera.setEnabled(false);
    }

    private void activateOrbitCam() {
        flyCamAppState.setEnabled(false);
        chaseCamera.setEnabled(true);
    }

    private boolean isObjectSelectionActive() {
        return this.activeToolbarItem != null && this.activeToolbarItem.equals(Action.SELECT);
    }

    private void setActiveToolbarItem(String action) {
        this.activeToolbarItem = action;

    }

    private void clearSelectedObject() {
        transformGizmo.setTarget(null);
        transformGizmo.removeFromParent();

        if (selectedSpatial != null) {
            outliner.deselect(selectedSpatial);
        }

        activateFlyCam();
        hidePanels();
        selectedSpatial = null;
    }

    private void setSelectedObject(Spatial spatial) {
        log("Selected object, " + spatial.getName() + ", GUID = " + spatial.getUserData(EditorUtils.GUID));
        rootNode.attachChild(transformGizmo);
        transformGizmo.setLocalTranslation(spatial.getWorldTranslation());
//                transformGizmo.setLocalRotation(spatial.getWorldRotation());
//        activateOrbitCam();
//        chaseCameraTarget.setLocalTranslation(spatial.getWorldTranslation().clone());

        //Remove the previous selected object from the outliner
        if (selectedSpatial != null) {
            outliner.deselect(selectedSpatial);
        }

        selectedSpatial = spatial;
        transformGizmo.setTarget(spatial);
        setActiveToolbarItem(Action.TRANSFORM); //Taken out because I need to select any other object

        if (selectedSpatial instanceof Geometry) {
            geometryPropertiesPanel.setGeometry((Geometry) selectedSpatial);
            geometryPropertiesPanel.show();
            nodePropertiesPanel.hide();

        } else if (selectedSpatial instanceof Node) {
            nodePropertiesPanel.setNode((Node) selectedSpatial);
            nodePropertiesPanel.show();
            geometryPropertiesPanel.hide();

        }

        //Set the newly selected object on the outliner
        outliner.select(selectedSpatial);
        outliner.updatePosition(selectedSpatial);
        outliner.updateScale(selectedSpatial);
        outliner.updateRotation(selectedSpatial);
    }

    /**
     * This is a helper method which will return the root node for a child
     * spatial
     *
     * @param child
     * @return
     */
    private Spatial findRootNodeForSelection(Spatial child) {
        if (child.getParent() == null || child.getUserData(EditorUtils.GUID) != null) {
            return child;
        } else if (child.getParent().equals(sceneNode)) {
            return child;
        } else {
            return findRootNodeForSelection(child.getParent());
        }
    }

}
