/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.tilemap;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import java.util.ArrayList;
import static com.bruynhuis.galago.games.tilemap.TileMapGame.BLANK;
import com.bruynhuis.galago.listener.KeyboardControlEvent;
import com.bruynhuis.galago.listener.KeyboardControlInputListener;
import com.bruynhuis.galago.listener.KeyboardControlListener;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.VPanel;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.debug.Grid;

/**
 *
 * @author nidebruyn
 */
public abstract class TileMapEditor extends AbstractScreen implements PickListener, KeyboardControlListener {

    protected TileMapGame game;
    protected float cameraHeight = 20f;
    protected float cameraVerticalAngle = FastMath.DEG_TO_RAD * 45f;
    protected float cameraDepth = 10f;
    private TouchPickListener touchPickListener;
    private KeyboardControlInputListener keyboardControlInputListener;
    protected float dragSpeed = 50f;
    private VPanel toolsPanel;
    private VPanel controlPanel;
    private TouchButtonAdapter tilesListener;
    private String selectedTileType = TileMapGame.BLANK;
    private TouchButton saveButton;
    private TouchButton clearButton;
    private TouchButton testButton;
    private TouchButton statsButton;
    private Label positionLabel;
    private Vector2f downPosition;
    private Node cameraJointNode;
    private CameraNode cameraNode;
    private Spatial selectedSpatial;
    private ArrayList<Spatial> selectedList = new ArrayList<Spatial>();
    private int index = 0;
    private int angle = 0;
    private Tile selectedTile;
    private Spatial marker;
    private Spatial centerSpatial;
    private Spatial gridSpatial;

    @Override
    protected void init() {

        toolsPanel = new VPanel(hudPanel, null, 60, 500);
        toolsPanel.leftTop(2, 100);
        hudPanel.add(toolsPanel);

        tilesListener = new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    selectedTileType = uid;
                    selectedList = game.getTileListForType(selectedTileType);
                    index = 0;

                    //update the selection
                    updateSelection();

                }
            }
        };

        createTileButton(TileMapGame.BLANK, "Del", toolsPanel, tilesListener);
        createTileButton(TileMapGame.TYPE_TERRAIN, "Terrain", toolsPanel, tilesListener);
        createTileButton(TileMapGame.TYPE_STATIC, "Static", toolsPanel, tilesListener);
        createTileButton(TileMapGame.TYPE_ENEMY, "Enemy", toolsPanel, tilesListener);
        createTileButton(TileMapGame.TYPE_OBSTACLE, "Obstacle", toolsPanel, tilesListener);
        createTileButton(TileMapGame.TYPE_VEGETATION, "Veggie", toolsPanel, tilesListener);
        createTileButton(TileMapGame.TYPE_PICKUP, "Pickup", toolsPanel, tilesListener);
        createTileButton(TileMapGame.TYPE_START, "Start", toolsPanel, tilesListener);

        toolsPanel.layout();

        controlPanel = new VPanel(hudPanel, null, 60, 240);
        controlPanel.rightBottom(2, 0);
        hudPanel.add(controlPanel);

        createTileButton("left_edit_button", "<", controlPanel, new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    rotateTileLeft();

                }
            }
        });

        createTileButton("right_edit_button", ">", controlPanel, new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    rotateTileRight();

                }
            }
        });

        createTileButton("up_edit_button", "+", controlPanel, new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
//                    height++;
                }
            }
        });

        createTileButton("down_edit_button", "-", controlPanel, new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
//                    height--;
                }
            }
        });

        controlPanel.layout();

        saveButton = new TouchButton(hudPanel, "edit_save_button", "Save");
        saveButton.rightTop(5, 5);
        saveButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game != null) {
                    game.save();
                }
            }
        });

        clearButton = new TouchButton(hudPanel, "edit_clear_button", "Clear");
        clearButton.rightTop(265, 5);
        clearButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game != null) {
                    game.clear();

                    Vector3f centerPoint = new Vector3f((game.getMapSize() * game.getTileSize()) * 0.5f, 0, (game.getMapSize() * game.getTileSize()) * 0.5f);
                    cameraJointNode.setLocalTranslation(centerPoint);
                    cameraJointNode.rotate(0, cameraVerticalAngle, 0);

                    cameraNode.setLocalTranslation(0, cameraHeight, cameraDepth);
                    cameraNode.lookAt(centerPoint, Vector3f.UNIT_Y);

                }
            }
        });

        testButton = new TouchButton(hudPanel, "edit_test_button", "Test");
        testButton.rightTop(524, 5);
        testButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game != null) {
                    game.save();
                    doTestAction(getEditFileName());
                }
            }
        });

        statsButton = new TouchButton(hudPanel, "edit_stats_button", "Stats");
        statsButton.rightTop(784, 5);
        statsButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game != null) {
                    baseApplication.showStats();
                }
            }
        });

        positionLabel = new Label(hudPanel, "Point: (0, 0)", 16, 200, 30);
        positionLabel.setTextColor(ColorRGBA.LightGray);
        positionLabel.rightBottom(100, 5);
        positionLabel.setAlignment(TextAlign.RIGHT);

//        defaultButton = new TouchButton(hudPanel, "edit_default_button", "Default");
//        defaultButton.rightBottom(5, 5);
//        defaultButton.addTouchButtonListener(new TouchButtonAdapter() {
//            @Override
//            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
//                if (isActive() && game != null) {
//                    game.clear();
//                    loadCameraSettings();
//                }
//            }
//        });
        //Load the inputs
        //Init the picker listener
        touchPickListener = new TouchPickListener(baseApplication.getCamera(), rootNode);
        touchPickListener.setPickListener(this);

        keyboardControlInputListener = new KeyboardControlInputListener();
        keyboardControlInputListener.addKeyboardControlListener(this);
    }

    protected abstract void doTestAction(String fileName);

    protected void createTileButton(String id, String text, Panel parent, TouchButtonListener buttonListener) {
        TouchButton button = new TouchButton(parent, id, "Resources/smallbutton.png", 60, 60);
        button.setText(text);
        button.setTextColor(ColorRGBA.DarkGray);
        button.setFontSize(12);
        button.addEffect(new TouchEffect(button));
        button.addTouchButtonListener(buttonListener);

    }

    protected void rotateTileRight() {
        angle += 90;
        if (angle == 0) {
            angle = 360;
        }

//        if (selectedTile != null) {
//            game.updateTile(selectedTile.getxPos(), selectedTile.getzPos(), angle, selectedSpatial.getName());
//        }
        updateSelection();
    }

    protected void rotateTileLeft() {
        angle -= 90;
        if (angle == 0) {
            angle = 360;
        }

        updateSelection();

//        if (selectedTile != null) {
//            game.updateTile(selectedTile.getxPos(), selectedTile.getzPos(), angle, selectedSpatial.getName());
//        }
    }

    protected void swapTileUp() {
        index++;
        if (index >= selectedList.size()) {
            index = 0;
        }
        updateSelection();
    }

    protected void swapTileDown() {
        index--;
        if (index < 0) {
            index = selectedList.size() - 1;
        }
        updateSelection();
    }

    protected abstract TileMapGame initGame();

    /**
     * Method for refreshing the selection index.
     */
    protected void updateSelection() {
        if (selectedSpatial != null) {
            selectedSpatial.removeFromParent();
        }

        if (selectedList.size() > 0) {
            selectedSpatial = selectedList.get(index);
        } else {
            selectedSpatial = null;
        }

        if (selectedTile != null) {
            if (selectedSpatial != null) {
                selectedSpatial.setLocalTranslation(selectedTile.getxPos() * game.getTileSize(), 0.001f, selectedTile.getzPos() * game.getTileSize());
                selectedSpatial.setLocalRotation(selectedSpatial.getLocalRotation().fromAngleAxis(angle * FastMath.DEG_TO_RAD, new Vector3f(0, 1, 0)));

                rootNode.attachChild(selectedSpatial);
            }

            marker.setLocalTranslation(selectedTile.getxPos() * game.getTileSize(), 0, selectedTile.getzPos() * game.getTileSize());
            positionLabel.setText("Point: (" + marker.getLocalTranslation().x + ", " + marker.getLocalTranslation().z + ")");
        }

    }

    protected abstract String getEditFileName();

    protected void initMarker() {
        Material mat = baseApplication.getAssetManager().loadMaterial("Common/Materials/RedColor.j3m");
        WireBox box = new WireBox(game.getTileSize() * 0.5f, 0.01f, game.getTileSize() * 0.5f);
        box.setLineWidth(3);
        Geometry g = new Geometry(BLANK, box);
        g.setMaterial(mat);
        marker = g;
        rootNode.attachChild(marker);
    }

    protected void initCenter() {
        Material mat = baseApplication.getAssetManager().loadMaterial("Common/Materials/RedColor.j3m");
        mat.setColor("Color", ColorRGBA.Blue);
        Arrow a = new Arrow(Vector3f.UNIT_Y.mult(2f));
        Geometry g = new Geometry(BLANK, a);
        g.setMaterial(mat);
        centerSpatial = g;
        rootNode.attachChild(centerSpatial);

        g.setLocalTranslation(game.getMapSize() * game.getTileSize() * 0.5f, 0, game.getMapSize() * game.getTileSize() * 0.5f);
    }

    protected void initGrid() {
        Material mat = baseApplication.getAssetManager().loadMaterial("Common/Materials/RedColor.j3m");
        mat.setColor("Color", ColorRGBA.Gray);
        Grid grid = new Grid(game.getMapSize(), game.getMapSize(), game.getTileSize());
        Geometry g = new Geometry(BLANK, grid);
        g.setMaterial(mat);
        gridSpatial = g;
        rootNode.attachChild(gridSpatial);

        g.setLocalTranslation(-game.getTileSize() * 0.5f, 0.01f, -game.getTileSize() * 0.5f);
//        g.setLocalTranslation(game.getMapSize()*game.getTileSize()*0.5f, 0, game.getMapSize()*game.getTileSize()*0.5f);
    }

    @Override
    protected void load() {

        baseApplication.getViewPort().setBackgroundColor(new ColorRGBA(52f / 255f, 152f / 255f, 219f / 255f, 1));
        /*
         * The loadStart method is called very first when ever the user calls to go to this screen.
         * A black panel will by default be shown over the screen
         * One must normally load the level and player and inputs and camera stuff here.
         */
        game = initGame();
        game.edit(getEditFileName());
        game.load();

        //Load the camera
        loadCameraSettings();

        //Get the defautls
        selectedTile = null;
        selectedTileType = TileMapGame.BLANK;
        selectedList = game.getTileListForType(selectedTileType);
        index = 0;
//        initGrid();
        initCenter();
        initMarker();
        updateSelection();

        touchPickListener.registerWithInput(inputManager);
        keyboardControlInputListener.registerWithInput(inputManager);
    }

    protected void loadCameraSettings() {
        Vector3f centerPoint = new Vector3f((game.getMapSize() * game.getTileSize()) * 0.5f, 0, (game.getMapSize() * game.getTileSize()) * 0.5f);
        cameraJointNode = new Node("camerajoint");
        cameraJointNode.setLocalTranslation(centerPoint);
        cameraJointNode.rotate(0, cameraVerticalAngle, 0);
        rootNode.attachChild(cameraJointNode);

        cameraNode = new CameraNode("camnode", camera);
        cameraNode.setLocalTranslation(0, cameraHeight, cameraDepth);
        cameraNode.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        cameraJointNode.attachChild(cameraNode);
    }

    @Override
    protected void exit() {
        /*
         * This exitDone method is called when the user leaves the current screen.
         * I a screen such as the play screen one will normally close the level or remove all
         * spatials and controls and lights, etc from the rootNode
         */
        touchPickListener.unregisterInput();
        keyboardControlInputListener.unregisterInput();

        cameraJointNode.removeFromParent();
        gridSpatial.removeFromParent();
        marker.removeFromParent();
        centerSpatial.removeFromParent();
        game.close();

    }

    /**
     * This method will be called when the player has touched the 3d spatials.
     *
     * @param contactPoint
     * @param contactObject
     * @param keyDown
     * @param cursorPosition
     */
    public void picked(PickEvent pickEvent, float tpf) {
        if (isActive() && game != null) {

            if (!pickEvent.isKeyDown() && pickEvent.isLeftButton() && selectedSpatial != null
                    && pickEvent.getCursorPosition().x > (80 * window.getScaleFactorWidth())
                    && pickEvent.getCursorPosition().x < window.getWidth() - (80 * window.getScaleFactorWidth())
                    && pickEvent.getCursorPosition().y < window.getHeight() - (80 * window.getScaleFactorHeight())
                    && downPosition != null
                    && downPosition.distance(pickEvent.getCursorPosition()) < 5f) {

                Tile clickedTile = game.getTileFromContactPoint(pickEvent.getContactPoint().x, pickEvent.getContactPoint().z);

                if (clickedTile != null) {
                    log("----------------------------------------------------");
                    log("Clicked: " + pickEvent.getContactObject().getName());
                    log("Position: " + pickEvent.getContactObject().getWorldTranslation());
                    log("Tile: " + clickedTile.toString());
                    if (selectedSpatial.getName().startsWith(TileMapGame.TYPE_TERRAIN)) {
                        clickedTile.setTerrainName(selectedSpatial.getName());
                        clickedTile.setTerrainAngle(angle);
                    } else {
                        clickedTile.setObjectName(selectedSpatial.getName());
                        clickedTile.setObjectAngle(angle);
                    }

                    selectedTile = game.updateTile(clickedTile);
                }

                downPosition = null;

            }

            //Rotate
            if (pickEvent.isKeyDown() && pickEvent.isRightButton()) {

                if (selectedSpatial == null) {
                    removeSelectedTile(selectedTile);
                } else {
                    rotateTileRight();
                }

            }

            //Store the old pick value
            if (pickEvent.isKeyDown()) {
                downPosition = pickEvent.getCursorPosition().clone();
            }
        }
    }

    @Override
    public void update(float tpf) {
        /**
         * We override the update loop so that we can move the camera position.
         */
        if (isActive() && game.isStarted() && !game.isPaused()) {
        }
    }

    /**
     * When draging happens
     *
     * @param pickEvent
     * @param tpf
     */
    public void drag(PickEvent pickEvent, float tpf) {
        if (isActive()) {

            if (pickEvent.isKeyDown()) {
                if (pickEvent.isRight()) {
                    cameraJointNode.move(cameraJointNode.getWorldRotation().getRotationColumn(0).mult(-pickEvent.getAnalogValue() * dragSpeed));
//                    cameraJointNode.rotate(0, -pickEvent.getAnalogValue() * dragSpeed * 0.25f, 0);

                } else if (pickEvent.isUp()) {
                    cameraJointNode.move(cameraJointNode.getWorldRotation().getRotationColumn(2).mult(pickEvent.getAnalogValue() * dragSpeed));

                } else if (pickEvent.isLeft()) {
                    cameraJointNode.move(cameraJointNode.getWorldRotation().getRotationColumn(0).mult(pickEvent.getAnalogValue() * dragSpeed));
//                    cameraJointNode.rotate(0, pickEvent.getAnalogValue() * dragSpeed * 0.25f, 0);

                } else if (pickEvent.isDown()) {
                    cameraJointNode.move(cameraJointNode.getWorldRotation().getRotationColumn(2).mult(-pickEvent.getAnalogValue() * dragSpeed));

                }

                cameraNode.lookAt(cameraJointNode.getWorldTranslation(), Vector3f.UNIT_Y);

            } else //                if (selectedSpatial != null) {
             if (pickEvent.getContactPoint() != null) {
                    selectedTile = game.getTileFromContactPoint(pickEvent.getContactPoint().x, pickEvent.getContactPoint().z);
                    updateSelection();

                } //                }
            if (pickEvent.isZoomUp()) {
                log("zoom up = " + pickEvent.getAnalogValue());
                swapTileUp();

            } else if (pickEvent.isZoomDown()) {
                log("zoom down = " + pickEvent.getAnalogValue());
                swapTileDown();
            }

        }
    }

    @Override
    public void onKey(KeyboardControlEvent keyboardControlEvent, float fps) {
        if (isActive() && game != null) {

            if (keyboardControlEvent.isDelete() && !keyboardControlEvent.isKeyDown()) {
                log("Delete: " + selectedTile);
                removeSelectedTile(selectedTile);

            }

        }
    }

    /**
     * This will remove or clear the selected tile.
     *
     * @param selectedTile
     */
    private void removeSelectedTile(Tile selectedTile) {
        if (selectedTile != null) {
            game.clearTile(selectedTile);
        }
    }
}
