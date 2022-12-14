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
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.util.ColorUtils;
import com.bruynhuis.galago.util.SpatialUtils;
import com.galago.editor.camera.EditorFlyCamAppState;
import com.galago.editor.spatial.Gizmo;
import com.galago.editor.spatial.GizmoListener;
import com.galago.editor.spatial.PaintGizmo;
import com.galago.editor.ui.panels.HierarchyPanel;
import com.galago.editor.ui.panels.ObjectAddPanel;
import com.galago.editor.ui.panels.TerrainPanel;
import com.galago.editor.ui.panels.ToolbarPanel;
import com.galago.editor.ui.actions.TerrainAction;
import com.galago.editor.ui.dialogs.TerrainDialog;
import com.galago.editor.ui.panels.WaterPanel;
import com.galago.editor.utils.Action;
import com.galago.editor.utils.MaterialUtils;
import com.galago.editor.utils.TerrainFlattenTool;
import com.galago.editor.utils.TerrainGrassTool;
import com.galago.editor.utils.TerrainPaintTool;
import com.galago.editor.utils.TerrainRaiseTool;
import com.galago.editor.utils.TerrainSmoothTool;
import com.galago.editor.utils.TerrainUtils;
import com.jme3.asset.ModelKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
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
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.water.WaterFilter;
import java.io.File;
import java.io.IOException;
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
    private ObjectAddPanel objectAddPanel;
    private TerrainPanel terrainPanel;
    private WaterPanel waterPanel;
    private Label statusLabel;
    private TouchButton messageBubble;

    private TerrainDialog terrainDialog;

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
    protected float cameraDistance = 24f;
    protected float distanceScale = 0f;

    protected TouchPickListener touchPickListener;

    private Node gridNode;
    private Node editNode;
    private Gizmo transformGizmo;
    private PaintGizmo paintGizmo;
    private Spatial selectedSpatial;
    private Node chaseCameraTarget;

    private TerrainPaintTool terrainPaintTool = new TerrainPaintTool();
    private TerrainRaiseTool terrainRaiseTool = new TerrainRaiseTool();
    private TerrainFlattenTool terrainFlattenTool = new TerrainFlattenTool();
    private TerrainSmoothTool terrainSmoothTool = new TerrainSmoothTool();
    private TerrainGrassTool terrainGrassTool = new TerrainGrassTool();

    private Vector3f flattenPoint = new Vector3f(0, 10, 0);

    private File lastSavedFile;

    private boolean leftMouseDown = false;
    private boolean overUI = false;
    private boolean statsVisible = false;

    @Override
    protected void init() {

        toolbarPanel = new ToolbarPanel(hudPanel);
        toolbarPanel.leftCenter(0, 0);

        hierarchyPanel = new HierarchyPanel(hudPanel);
        hierarchyPanel.leftCenter(EditorUtils.TOOLBAR_WIDTH, 0);

        objectAddPanel = new ObjectAddPanel(hudPanel);
        objectAddPanel.leftCenter(EditorUtils.TOOLBAR_WIDTH, 0);

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
                System.out.println("Selected Texture: " + uid);
                importTexture(uid);
            }

        });

        waterPanel = new WaterPanel(hudPanel);
        waterPanel.leftCenter(EditorUtils.TOOLBAR_WIDTH, 0);

        statusLabel = new Label(hudPanel, "Status: None", 16, 600, 30);
        statusLabel.setAlignment(TextAlign.RIGHT);
        statusLabel.rightTop(5, 0);

        terrainDialog = new TerrainDialog(window);

        messageBubble = new TouchButton(hudPanel, "message-bubble", "Interface/hierarchy-header.png", 400, 32);
        messageBubble.setTextAlignment(TextAlign.LEFT);
        messageBubble.setText("Message Bubble");
        messageBubble.setTextColor(EditorUtils.theme.getTooltipTextColor());
        messageBubble.setBackgroundColor(EditorUtils.theme.getTooltipColor());
        messageBubble.centerAt(750, -510);

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
        hidePanels();
        touchPickListener.registerWithInput(inputManager);

        flyCamAppState.setEnabled(true);
        flyCamAppState.getCamera().setDragToRotate(true);
        flyCamAppState.getCamera().setMoveSpeed(25f);
        flyCamAppState.getCamera().setRotationSpeed(3f);

        chaseCamera.setEnabled(false);
    }
    
    protected void showMessage(String text) {
        messageBubble.setText(text);
//        messageBubble.centerAt(300, -800);
        messageBubble.setVisible(true);
        messageBubble.moveFromToCenter(750, -800, 750, -510, 2, 1, new TweenCallback() {
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
            hierarchyPanel.reload(editNode);

        }

        if (panel.equals(terrainPanel)) {
            terrainPanel.setTerrain(getTerrain());
            if (terrainPanel.getTerrain() != null) {
                touchPickListener.setTargetNode(getTerrain());

            }

        }

        if (panel.equals(waterPanel)) {
            waterPanel.setWaterFilter(oceanFilter);

        }

    }

    protected void hidePanels() {
        messageBubble.hide();
        hierarchyPanel.hide();
        objectAddPanel.hide();
        terrainPanel.hide();
        waterPanel.hide();
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

        } else {
            initGrid();
        }

        editNode = new Node("root");
        rootNode.attachChild(editNode);

        loadAmbientLight();
        loadDirectionalLight();
        loadProbeLight();

        lastSavedFile = null;
        showMessage("New scene created!");
    }

    private void initGrid() {
        gridNode = new Node("grid-node");
        rootNode.attachChild(gridNode);

        int gridLines = 1000;
        float lineSpacing = 1;
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
        ambientLight.setColor(ColorRGBA.Gray);
        editNode.addLight(ambientLight);
    }

    private void loadDirectionalLight() {
        sunLight = new DirectionalLight();
        sunLight.setColor(ColorRGBA.White);
        sunLight.setDirection(new Vector3f(0.6f, -0.8f, -0.6f).normalizeLocal());
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

//        fpp.addFilter(shadowFilter);
        //Smooth edging
        fXAAFilter = new FXAAFilter();
        fXAAFilter.setEnabled(true);
        fpp.addFilter(fXAAFilter);
    }

    protected void initGizmos() {
        transformGizmo = new Gizmo("GIZMO", camera, inputManager);
        transformGizmo.setGizmoListener(this);

        paintGizmo = new PaintGizmo("PAINT-GIZMO", camera, inputManager);

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
            System.out.println("Game to save: " + selectedFile);
            try {
                EditorUtils.saveSpatial(editNode, selectedFile);
                showMessage("File successfully saved!");
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

        } else if (Action.UI_OVER.equals(message)) {
            overUI = true;

        } else if (Action.UI_OFF.equals(message)) {
            overUI = false;

        } else if (Action.ADD.equals(message)) {

            if (object == null) {
                showPanel(objectAddPanel);

            } else {
                addObject((Spatial) object);

            }

        } else if (Action.STATS.equals(message)) {
            statsVisible = !statsVisible;

            if (statsVisible) {
                baseApplication.showStats();
            } else {
                baseApplication.hideStats();
            }

        } else if (Action.TERRAIN.equals(message)) {
            System.out.println("Show terrain dialog");
            showPanel(terrainPanel);

        } else if (Action.CREATE_TERRAIN.equals(message)) {
            System.out.println("Create terrain: " + object);
            TerrainAction terrainAction = (TerrainAction) object;

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
                Material material = null;

                if (terrainAction.getTerrainMaterial() == TerrainAction.MATERIAL_PAINTABLE) {
                    System.out.println("Generated generatePaintableTerrainMaterial");
                    try {
                        material = TerrainUtils.generatePaintableTerrainMaterial(assetManager, terrainAction.getTerrainSize());
                    } catch (IOException ex) {
                        Logger.getLogger(EditorScreen.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else if (terrainAction.getTerrainMaterial() == TerrainAction.MATERIAL_HEIGHT_BASED) {
                    System.out.println("Generated generateLitHeightBasedMaterial");
                    material = TerrainUtils.generateLitHeightBasedMaterial(assetManager, terrainAction.getTerrainSize());

                } else if (terrainAction.getTerrainMaterial() == TerrainAction.MATERIAL_PBR) {
                    System.out.println("Generated generatePaintablePBRTerrainMaterial");
                    try {
                        material = TerrainUtils.generatePaintablePBRTerrainMaterial(assetManager, terrainAction.getTerrainSize());
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
                grassNode1.setUserData(EditorUtils.MODEL, grassModel1.getChild(0));
                MaterialUtils.convertTextureToEmbeddedByName(((Geometry) grassModel1.getChild(0)).getMaterial(), "DiffuseMap");

                //GRASS2:
                BatchNode grassNode2 = new BatchNode(TerrainAction.BATCH_GRASS2);
                grassNode2.setLocalScale(1, heightScale, 1);
                terrain.attachChild(grassNode2);
                Node grassModel2 = (Node) assetManager.loadModel("Models/vegetation/grass2.j3o");
                grassNode2.setUserData(EditorUtils.MODEL, grassModel2.getChild(0));
                MaterialUtils.convertTextureToEmbeddedByName(((Geometry) grassModel2.getChild(0)).getMaterial(), "DiffuseMap");

                //GRASS3:
                BatchNode grassNode3 = new BatchNode(TerrainAction.BATCH_GRASS3);
                grassNode3.setLocalScale(1, heightScale, 1);
                terrain.attachChild(grassNode3);
                Node grassModel3 = (Node) assetManager.loadModel("Models/vegetation/grass3.j3o");
                grassNode3.setUserData(EditorUtils.MODEL, grassModel3.getChild(0));
                MaterialUtils.convertTextureToEmbeddedByName(((Geometry) grassModel3.getChild(0)).getMaterial(), "DiffuseMap");

                editNode.attachChild(terrain);

//                TangentBinormalGenerator.generate(terrain);
            }

            terrainPanel.setTerrain(getTerrain());

        } else if (Action.WATER.equals(message)) {
            System.out.println("Show water");

            transformGizmo.setTarget(null);
            transformGizmo.removeFromParent();

//            oceanFilter.setEnabled(true);
            showPanel(waterPanel);

        } else if (Action.IMPORT.equals(message)) {
//            baseApplication.enqueue(new Runnable() {
//                @Override
//                public void run() {
            importObject();
//                }
//
//            });

        } else if (Action.HIERARCHY.equals(message)) {
            showPanel(hierarchyPanel);

        } else if (Action.SELECT.equals(message)) {
            if (object == null) {
                transformGizmo.setTarget(null);
                transformGizmo.removeFromParent();
                flyCamAppState.setEnabled(true);
                chaseCamera.setEnabled(false);

            } else {
                rootNode.attachChild(transformGizmo);
                Spatial spatial = (Spatial) object;
                transformGizmo.setLocalTranslation(spatial.getWorldTranslation());
//                transformGizmo.setLocalRotation(spatial.getWorldRotation());

                flyCamAppState.setEnabled(false);
                chaseCamera.setEnabled(true);

                chaseCameraTarget.setLocalTranslation(spatial.getWorldTranslation().clone());
                selectedSpatial = spatial;
                transformGizmo.setTarget(spatial);

            }

            paintGizmo.removeFromParent();

        } else if (Action.PAINT.equals(message)) {
            System.out.println("Prepaire to paint");

            transformGizmo.setTarget(null);
            transformGizmo.removeFromParent();

            rootNode.attachChild(paintGizmo);

        } else if (Action.AUTO_PAINT.equals(message)) {

            TerrainAction terrainAction = (TerrainAction) object;

            TerrainQuad terrain = getTerrain();
            BoundingBox bb = (BoundingBox) terrain.getWorldBound();
            System.out.println("Auto paint: " + terrain.getWorldBound());

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
//                                System.out.println("Collison res = " + cr.getContactNormal());
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

        if (terrain != null) {
            gridNode.removeFromParent();

        }

        return terrain;
    }

    private void addObject(Spatial spatial) {

        System.out.println("Trying to add: " + spatial.getName());

        if (spatial.getName().equals("cube")) {
            Spatial box = SpatialUtils.addBox(editNode, 5, 10, 5);
            box.setName("Box");
            SpatialUtils.addColor(box, ColorRGBA.randomColor(), false);

        } else if (spatial.getName().equals("sky")) {
            SpatialUtils.addSkySphere(editNode, ColorUtils.rgb(19, 15, 64), ColorUtils.rgb(199, 236, 238), camera);

        }

    }

    private Spatial importModelFromFile(File selectedFile) {
        if (selectedFile != null && EditorUtils.isCompatableModel(selectedFile)) {
            System.out.println("Importing model: " + selectedFile);
            System.out.println("Parent: " + selectedFile.getParent());
            System.out.println("FileName: " + selectedFile.getName());

            baseApplication.getAssetManager().registerLocator(selectedFile.getParent(), FileLocator.class);
            ModelKey key = new ModelKey(selectedFile.getName());
            Spatial m = baseApplication.getAssetManager().loadModel(key);

            System.out.println("Model (" + m.getName() + ") successfully imported.");

            baseApplication.getAssetManager().deleteFromCache(key);
            baseApplication.getAssetManager().unregisterLocator(selectedFile.getParent(), FileLocator.class);

            //Name the file the name of the folder
            if (selectedFile.getParentFile() != null) {
                selectedFile = selectedFile.getParentFile();
            }

            if (selectedFile.getName().endsWith(".obj")) {
                m.setName(selectedFile.getName().replace(".obj", ""));

            } else if (selectedFile.getName().endsWith(".fbx")) {
                m.setName(selectedFile.getName().replace(".fbx", ""));

            } else if (selectedFile.getName().endsWith(".gltf")) {
                m.setName(selectedFile.getName().replace(".gltf", ""));

            } else {
                m.setName(selectedFile.getName());

            }

            //NB
            MaterialUtils.convertTexturesToEmbedded(m);
            editNode.attachChild(m);

            return m;
        }

        return null;
    }

    private void importTexture(String uid) {
        System.out.println("TERRAIN BUTTON: " + uid);

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
            System.out.println("Selected image: " + fileChooser.getSelectedFile());

            File file = fileChooser.getSelectedFile();

            baseApplication.getGameSaves().getGameData().getProperties().setProperty(EditorUtils.LAST_LOCATION, file.getPath());
            baseApplication.getGameSaves().save();

            assetManager.registerLocator(file.getParent(), FileLocator.class);
            Texture texture = assetManager.loadTexture(file.getName());
            texture.setKey(null); //Set the key to null so that it can be embedded

            System.out.println("Texture: " + texture);

            if (terrainPanel.getTerrainAction().getTool() == TerrainAction.TOOL_GRASS1) {
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

        if (pickEvent.isLeftButton()) {
            leftMouseDown = pickEvent.isKeyDown();

            if (pickEvent.getContactPoint() != null && paintGizmo.getParent() != null) {

                flattenPoint.setY(pickEvent.getContactPoint().y);

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
                            terrainPanel.getPaintStrength(),
                            terrainPanel.getSelectedLayer());

                } else if (terrainPanel.getTerrainAction().getTool() == TerrainAction.TOOL_RAISE) {
                    terrainRaiseTool.modifyHeight(getTerrain(),
                            paintGizmo.getWorldTranslation(),
                            terrainPanel.getPaintRadius(),
                            terrainPanel.getPaintStrength(),
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
                            terrainPanel.getPaintStrength(),
                            terrainPanel.getSelectedGrass());

                } else if (terrainPanel.getTerrainAction().getTool() == TerrainAction.TOOL_GRASS2) {
                    terrainGrassTool.paintGrass(getTerrain(),
                            terrainPanel.getSelectedBatchLayer(),
                            paintGizmo.getWorldTranslation(),
                            terrainPanel.getPaintRadius(),
                            terrainPanel.getPaintStrength(),
                            terrainPanel.getSelectedGrass());

                } else if (terrainPanel.getTerrainAction().getTool() == TerrainAction.TOOL_GRASS3) {
                    terrainGrassTool.paintGrass(getTerrain(),
                            terrainPanel.getSelectedBatchLayer(),
                            paintGizmo.getWorldTranslation(),
                            terrainPanel.getPaintRadius(),
                            terrainPanel.getPaintStrength(),
                            terrainPanel.getSelectedGrass());

                }

            }

        } else {
            statusLabel.setText("Screen: (" + pickEvent.getCursorPosition().x + ", "
                    + pickEvent.getCursorPosition().y + ")");

        }

    }

    @Override
    public void gizmoUpdate(Vector3f position, Quaternion rotations, Vector3f scale) {
        if (selectedSpatial != null) {
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
}
