/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.test2d.spritebatching;

import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.GridPanel;
import com.bruynhuis.galago.util.SpatialUtils;
import com.galago.test2d.MainApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.jme3.texture.Texture;

/**
 *
 * @author NideBruyn
 */
public class BatchingScreen extends AbstractScreen implements PickListener {

    public static final String NAME = "BatchingScreen";
    private MainApplication mainApplication;
    private Label title;
//    private TouchButton batchButton;
    private TouchPickListener touchPickListener;
    private GridPanel toolPanel;
    private BatchNode backgroundNode;
    private BatchNode terrainNode;
    private BatchNode frontNode;
    private BatchNode selectedLayerNode;
    private int cols = 30;
    private int rows = 18;
    private float tileSize = 1;
    private float toolSize = 42;
    private float cameraFrustrum = 10;
    private float zoomSpeed = 10f;
    private int spriteSheetWidth = 6;
    private int spriteSheetHeight = 6;
    private TouchButtonAdapter toolbarButtonListener;
    private ToolbarButton selectedToolbarButton;
    private LayerButton backgroundLayerButton;
    private LayerButton terrainLayerButton;
    private LayerButton deleteButton;
    private LayerButton fillButton;
    private boolean painting = false;
    private boolean fill = false;
    private Grid grid;
    private Node gridNode;
    private FilterPostProcessor fpp;

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;

        title = new Label(hudPanel, "Sprite Batching");
        title.centerTop(0, 0);

        toolbarButtonListener = new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                log("picked: " + uid);
                for (int i = 2; i < toolPanel.getWidgets().size(); i++) {
                    ToolbarButton tb = (ToolbarButton) toolPanel.getWidgets().get(i);
                    if (tb.getUid().equals(uid)) {
                        tb.select();
                        selectedToolbarButton = tb;
                    } else {
                        tb.unselect();
                    }

                }
                deleteButton.unselect();
                fillButton.unselect();
                fill = false;

            }

        };

        toolPanel = new GridPanel(hudPanel, toolSize * 2, toolSize * 20);

        deleteButton = new LayerButton(toolPanel, "deleteButton", "X", toolSize, 4);
        deleteButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                deleteButton.select();
                fill = false;
                if (selectedToolbarButton != null) {
                    selectedToolbarButton.unselect();
                    selectedToolbarButton = null;
                }

            }

        });

        fillButton = new LayerButton(toolPanel, "fillButton", "F", toolSize, 4);
        fillButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                fill = true;
                deleteButton.unselect();
                fillButton.select();
//                if (selectedToolbarButton != null) {
//                    selectedToolbarButton.unselect();
//                    selectedToolbarButton = null;
//                }

            }

        });

        for (int r = 0; r < spriteSheetHeight; r++) {
            for (int c = 0; c < spriteSheetWidth; c++) {
                addToolButton(c, r);

            }
        }

        hudPanel.add(toolPanel);
        toolPanel.layout(20, 2);

        backgroundLayerButton = new LayerButton(hudPanel, "background", "1", toolSize, 4);
        backgroundLayerButton.rightTop(10, 10);
        backgroundLayerButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                selectedLayerNode = backgroundNode;
                backgroundLayerButton.select();
                terrainLayerButton.unselect();
                deleteButton.unselect();

            }

        });

        terrainLayerButton = new LayerButton(hudPanel, "background", "2", toolSize, 4);
        terrainLayerButton.rightTop(10, 20 + toolSize);
        terrainLayerButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                selectedLayerNode = terrainNode;
                backgroundLayerButton.unselect();
                terrainLayerButton.select();
                deleteButton.unselect();

            }

        });

//        batchButton = new TouchButton(hudPanel, "batching", "Batch");
//        batchButton.rightBottom(0, 0);
//        batchButton.addTouchButtonListener(new TouchButtonAdapter() {
//            @Override
//            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
//                terrainNode.batch();
//
//            }
//
//        });
        touchPickListener = new TouchPickListener(camera, rootNode);
        touchPickListener.setPickListener(this);
    }

    protected void addToolButton(int colIndex, int rowIndex) {
        ToolbarButton button = new ToolbarButton(toolPanel, (colIndex + "," + rowIndex),
                "Textures/tileset-lab.png", toolSize, 4, spriteSheetWidth, spriteSheetHeight, colIndex, rowIndex);
        button.addTouchButtonListener(toolbarButtonListener);
    }

    private void loadPaintGrid() {
        gridNode = new Node("grid-node");
        rootNode.attachChild(gridNode);
        gridNode.move(0, 0, -2);

        Spatial canvas = SpatialUtils.addQuad(gridNode, tileSize * cols * 0.5f, tileSize * rows * 0.5f);
        SpatialUtils.addColor(canvas, ColorRGBA.Black, true);
        gridNode.attachChild(canvas);

        grid = new Grid(cols + 1, rows + 1, tileSize);
        Geometry g = new Geometry("grid", grid);
        Material material = new Material(baseApplication.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.DarkGray);
        g.setMaterial(material);
        g.rotate(-90 * FastMath.DEG_TO_RAD, 180 * FastMath.DEG_TO_RAD, 90 * FastMath.DEG_TO_RAD);
        gridNode.attachChild(g);

    }

    @Override
    protected void load() {
        mainApplication.setCameraDistanceFrustrum(cameraFrustrum);
        mainApplication.getViewPort().setBackgroundColor(ColorRGBA.Black);

        painting = false;

        loadPaintGrid();

        backgroundNode = new BatchNode("background");
        backgroundNode.setLocalTranslation(0, 0, -1);
        rootNode.attachChild(backgroundNode);

        terrainNode = new BatchNode("terrain");
        rootNode.attachChild(terrainNode);

        selectedLayerNode = backgroundNode;
        backgroundLayerButton.select();
        terrainLayerButton.unselect();
        
        fpp = new FilterPostProcessor(assetManager);
        baseApplication.getViewPort().addProcessor(fpp);

        BloomFilter bloomFilter = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloomFilter);
        
        camera.setLocation(new Vector3f(tileSize * cols * 0.5f, tileSize * rows * 0.5f, 10));
    }

    @Override
    protected void show() {
        touchPickListener.registerWithInput(inputManager);
        toolPanel.leftTop(0, 0);

    }

    @Override
    protected void exit() {
        baseApplication.getViewPort().removeProcessor(fpp);
        touchPickListener.unregisterInput();
        rootNode.detachAllChildren();
    }

    @Override
    protected void pause() {
    }

    protected void paintTile(BatchNode node, int colIndex, int rowIndex, float x, float y) {
        Sprite picture = new Sprite("sprite: " + colIndex + "," + rowIndex, tileSize, tileSize, spriteSheetWidth, spriteSheetHeight, colIndex, rowIndex);
        picture.setImage("Textures/tileset-lab.png");
//        picture.setQueueBucket(RenderQueue.Bucket.Transparent);
        picture.getMaterial().setFloat("AlphaDiscardThreshold", 0.55f);
        Texture texture = baseApplication.getAssetManager().loadTexture("Textures/tileset-lab-glow.png");
        texture.setMagFilter(Texture.MagFilter.Nearest);
        picture.getMaterial().setTexture("GlowMap", texture);
        node.attachChild(picture);
        //Here we need to offset the tile
        picture.setLocalTranslation(x + (tileSize * 0.5f), y + (tileSize * 0.5f), 0);
    }

    private void floodFillLayer() {
        if (selectedToolbarButton != null) {
            selectedLayerNode.detachAllChildren();

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    paintTile(selectedLayerNode, selectedToolbarButton.getColumnIndex(), selectedToolbarButton.getRowIndex(), c * tileSize, r * tileSize);
                }
            }

            selectedLayerNode.batch();
            fillButton.unselect();
        }

    }

    @Override
    public void picked(PickEvent pickEvent, float tpf) {

        if (pickEvent.isKeyDown()) {
            if (!fill) {
                painting = true;
            } else {
                painting = false;
                floodFillLayer();

            }

        } else {
            painting = false;
        }

    }

    @Override
    public void drag(PickEvent pickEvent, float tpf) {

//        log("Point = " + pickEvent.getContactPoint());
        if (painting && pickEvent.getContactPoint() == null) {
            painting = false;
        }

        if (painting) {
            float xPos = FastMath.floor(pickEvent.getContactPoint().x);
            float yPos = FastMath.floor(pickEvent.getContactPoint().y);
            log("Paint at = " + xPos + ", " + yPos);

            //First we check if there is an existing tile to delete
            if (pickEvent.getContactObject().getParent().getName().startsWith("sprite:")) {
                Sprite s = (Sprite) pickEvent.getContactObject().getParent();
//                log("Parent = " + s.getParent().getName());
                if (s.getParent().equals(selectedLayerNode)) {
                    s.removeFromParent();
                }

                if (selectedToolbarButton == null) {
                    selectedLayerNode.batch();
                }

            }

            //Second we check if we should paint something
            if (selectedToolbarButton != null) {
                paintTile(selectedLayerNode, selectedToolbarButton.getColumnIndex(), selectedToolbarButton.getRowIndex(), xPos, yPos);
                selectedLayerNode.batch();
                log("selected = " + selectedLayerNode.getLocalTranslation());
            }

        }

//        if (painting && pickEvent.getContactObject() != null && selectedToolbarButton != null) {            
//            log("Picked = " + pickEvent.getContactObject().getParent().getName());
//
//            if (pickEvent.getContactObject().getParent().getName().startsWith("sprite:")) {
//                Sprite s = (Sprite) pickEvent.getContactObject().getParent();
//                log("Update index of sprite = " + selectedToolbarButton.getColumnIndex() + ", " + selectedToolbarButton.getRowIndex());
//
//                float xPos = s.getLocalTranslation().x;
//                float yPos = s.getLocalTranslation().y;
//
//                if (selectedLayerNode == null && s.getParent().equals(terrainNode)) {
//                    s.removeFromParent();
//                    
//                } else if (selectedLayerNode != null) {
//                    s.removeFromParent();
//                }
//
//                if (selectedLayerNode != null) {
//                    paintTile(selectedLayerNode, selectedToolbarButton.getColumnIndex(), selectedToolbarButton.getRowIndex(), xPos, yPos);
////                selectedLayerNode.batch();                    
//                }
//
//            }
//
//        }
        if (pickEvent.isZoomDown()) {
            cameraFrustrum += tpf * zoomSpeed;
            mainApplication.setCameraDistanceFrustrum(cameraFrustrum);

        } else if (pickEvent.isZoomUp()) {
            cameraFrustrum -= tpf * zoomSpeed;
            mainApplication.setCameraDistanceFrustrum(cameraFrustrum);
        }

    }

}
