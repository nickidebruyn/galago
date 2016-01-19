/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.platform2d;

import static com.bruynhuis.galago.games.tilemap.TileMapGame.BLANK;
import com.bruynhuis.galago.listener.JoystickEvent;
import com.bruynhuis.galago.listener.JoystickInputListener;
import com.bruynhuis.galago.listener.JoystickListener;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.HPanel;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.panel.VPanel;
import com.jme3.font.BitmapFont;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Quad;
import java.util.ArrayList;

/**
 *
 * @author nidebruyn
 */
public abstract class Platform2DEditor extends AbstractScreen implements PickListener, ActionListener, JoystickListener {

    protected static final String WORKSHEET = "worksheet";
    private static final String KEYBOARD_LEFT = "KEY-LEFT-EDIT";
    private static final String KEYBOARD_RIGHT = "KEY-RIGHT-EDIT";
    private static final String KEYBOARD_UP = "KEY-UP-EDIT";
    private static final String KEYBOARD_DOWN = "KEY-DOWN-EDIT";
    private static final String KEYBOARD_ADD = "KEY-ADD-EDIT";
    private static final String KEYBOARD_DEL = "KEY-DEL-EDIT";
    protected Platform2DGame game;
    protected TouchPickListener touchPickListener;
    protected JoystickInputListener joystickInputListener;
    protected float dragSpeed = 0.1f;
    protected VPanel toolsPanel;
    protected HPanel topPanel;
    protected VPanel controlsPanel;
    protected TouchButton saveButton;
    protected TouchButton clearButton;
    protected TouchButton testButton;
    protected TouchButton statsButton;
    protected Sprite selectedSprite;
    protected int index = 0;
    protected Spatial marker;
    protected Material markerMaterial;
    protected Geometry worksheet;
    protected TouchButtonAdapter tilesListener;
    protected ArrayList<String> selectedList = new ArrayList<>();
    protected Label infoLabel;
    protected DescriptionDialog descriptionDialog;
    protected float buttonSize = 64f;
    protected Vector3f targetLookAtPoint = Vector3f.ZERO;
    protected final Vector3f cameraDefaultLocation = new Vector3f(0, 0, 10);
    protected Material centerMarkerMaterial;
    protected boolean deleteActive = false;
    protected boolean lockMovement = false;

    /**
     * Returns the edit file for the level editor.
     *
     * @return
     */
    protected abstract String getEditFile();

    /**
     * When the test button is pressed this method will be called. Implementer
     * must move to the test screen.
     */
    protected abstract void doTestAction();

    /**
     * Create an instance of the game
     *
     * @return
     */
    protected abstract Platform2DGame initGame();

    /**
     * Called after the level was cleared.
     */
    protected abstract void doClearAction();

    @Override
    protected void init() {

        Label loadingLabel = new Label(loadingPanel, "Loading...", 24);
        loadingLabel.center();

        infoLabel = new Label(hudPanel, "Item: ", 18, 1280, 40);
        infoLabel.leftBottom(2, 2);
        infoLabel.setTextColor(ColorRGBA.Green);
        infoLabel.setAlignment(BitmapFont.Align.Left);

        toolsPanel = new VPanel(hudPanel, null, buttonSize, 680);
        toolsPanel.rightTop(0, 0);
        hudPanel.add(toolsPanel);

        tilesListener = new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    selectedList = game.getTypeList(uid);
                    index = 0;
                    updateSelection();

                }
            }
        };

        createTileButton(Platform2DGame.TYPE_TERRAIN, "Resources/button_terrain.png", toolsPanel, tilesListener);
        createTileButton(Platform2DGame.TYPE_STATIC, "Resources/button_static.png", toolsPanel, tilesListener);
        createTileButton(Platform2DGame.TYPE_ENEMY, "Resources/button_enemy.png", toolsPanel, tilesListener);
        createTileButton(Platform2DGame.TYPE_OBSTACLE, "Resources/button_obstacle.png", toolsPanel, tilesListener);
        createTileButton(Platform2DGame.TYPE_VEGETATION, "Resources/button_veg.png", toolsPanel, tilesListener);
        createTileButton(Platform2DGame.TYPE_PICKUP, "Resources/button_pickup.png", toolsPanel, tilesListener);
        createTileButton(Platform2DGame.TYPE_START, "Resources/button_start.png", toolsPanel, tilesListener);
        createTileButton(Platform2DGame.TYPE_END, "Resources/button_end.png", toolsPanel, tilesListener);
        createTileButton(Platform2DGame.TYPE_SKY, "Resources/button_sky.png", toolsPanel, tilesListener);
        createTileButton(Platform2DGame.TYPE_BACK_LAYER1, "Resources/button_sky.png", toolsPanel, tilesListener);
        createTileButton(Platform2DGame.TYPE_BACK_LAYER2, "Resources/button_sky.png", toolsPanel, tilesListener);

        toolsPanel.layout();

        //Add the top panel
        topPanel = new HPanel(hudPanel, null, 750, 55);
        topPanel.centerTop(0, 0);
        hudPanel.add(topPanel);

        saveButton = new TouchButton(topPanel, "edit_save_button", "Resources/button_save.png", 184f, 52f);
        saveButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game != null) {
                    descriptionDialog.show(game.getTileMap().getDescription());
                }
            }
        });

        clearButton = new TouchButton(topPanel, "edit_clear_button", "Resources/button_clear.png", 184f, 52f);
        clearButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game != null) {
                    game.clear();
                    targetLookAtPoint = Vector3f.ZERO;
                    camera.setLocation(cameraDefaultLocation);
                    doClearAction();


                }
            }
        });

        testButton = new TouchButton(topPanel, "edit_test_button", "Resources/button_test.png", 184f, 52f);
        testButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game != null) {
                    game.save();
                    doTestAction();
                }
            }
        });

        statsButton = new TouchButton(topPanel, "edit_stats_button", "Resources/button_stats.png", 184f, 52f);
        statsButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game != null) {
                    baseApplication.showStats();
                }
            }
        });

        topPanel.layout();

        //lets create some controls
        controlsPanel = new VPanel(hudPanel, null, buttonSize, 200);
        controlsPanel.leftBottom(0, 0);
        hudPanel.add(controlsPanel);

        createTileButton("delete", "Resources/button_del.png", controlsPanel, new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game != null) {
                    setDeleteActive(!deleteActive);

                }
            }
        });

        createTileButton("swapup", "Resources/button_up.png", controlsPanel, new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game != null) {
                    swapTileUp();
                }
            }
        });

        createTileButton("swapdown", "Resources/button_down.png", controlsPanel, new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && game != null) {
                    swapTileDown();
                }
            }
        });

        controlsPanel.layout();


        descriptionDialog = new DescriptionDialog(window);
        descriptionDialog.addSaveButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    game.getTileMap().setDescription(descriptionDialog.getDescription());
                    game.save();
                    descriptionDialog.hide();
                }
            }
        });

    }

    protected void createTileButton(String id, String image, Panel parent, TouchButtonListener buttonListener) {
        TouchButton button = new TouchButton(parent, id, image, buttonSize, buttonSize);
        button.addEffect(new TouchEffect(button));
        button.addTouchButtonListener(buttonListener);

    }

    protected void setDeleteActive(boolean active) {
        deleteActive = active;
        //Fix the selection
        if (selectedSprite != null) {
            if (deleteActive) {
                markerMaterial.setColor("Color", ColorRGBA.Red);
                selectedSprite.setCullHint(Spatial.CullHint.Always);
            } else {
                markerMaterial.setColor("Color", ColorRGBA.Green);
                selectedSprite.setCullHint(Spatial.CullHint.Never);
            }
        }
    }

    /**
     * Load some input controls
     */
    protected void initInput() {
        log("Init input");
        if (!inputManager.hasMapping(KEYBOARD_LEFT)) {
            inputManager.addMapping(KEYBOARD_LEFT, new KeyTrigger(KeyInput.KEY_LEFT));
            inputManager.addMapping(KEYBOARD_RIGHT, new KeyTrigger(KeyInput.KEY_RIGHT));
            inputManager.addMapping(KEYBOARD_UP, new KeyTrigger(KeyInput.KEY_UP));
            inputManager.addMapping(KEYBOARD_DOWN, new KeyTrigger(KeyInput.KEY_DOWN));
            inputManager.addMapping(KEYBOARD_ADD, new KeyTrigger(KeyInput.KEY_SPACE));
            inputManager.addMapping(KEYBOARD_DEL, new KeyTrigger(KeyInput.KEY_DELETE));

            inputManager.addListener(this, new String[]{KEYBOARD_LEFT, KEYBOARD_RIGHT, KEYBOARD_UP, KEYBOARD_DOWN, KEYBOARD_ADD, KEYBOARD_DEL});
        }

    }

    protected void swapTileUp() {
        index++;
        if (index > selectedList.size() - 1) {
            index = 0;
        }
        log("index = " + index);
        updateSelection();
    }

    protected void swapTileDown() {
        index--;
        if (index < 0) {
            index = selectedList.size() - 1;
        }
        log("index = " + index);
        updateSelection();
    }

    private void updateSelectedSpritePosition(float xPos, float yPos) {
        if (selectedSprite != null) {
            selectedSprite.setLocalTranslation(xPos, yPos, selectedSprite.getWorldTranslation().z);

        }
    }

    protected void updateSelection() {
        if (selectedSprite != null) {
            selectedSprite.removeFromParent();
        }

        if (selectedList.size() > 0) {
            selectedSprite = game.getItem(selectedList.get(index));
            infoLabel.setText("Item: " + selectedList.get(index) + ";   Tiles: " + game.getTileMap().getTiles().size());
        } else {
            infoLabel.setText("Item: Nothing");
        }

        if (selectedSprite != null) {
            rootNode.attachChild(selectedSprite);
            updateSelectedSpritePosition(marker.getLocalTranslation().x, marker.getLocalTranslation().y);
        }

        setDeleteActive(deleteActive);

//        marker.setLocalTranslation(0, 0, 0);
    }

    protected void initMarker() {
        markerMaterial = baseApplication.getAssetManager().loadMaterial("Common/Materials/RedColor.j3m");
        markerMaterial.setColor("Color", ColorRGBA.Green);
        WireBox box = new WireBox(0.5f, 0.5f, 0.1f);
        box.setLineWidth(2);
        Geometry g = new Geometry(BLANK, box);
        g.setMaterial(markerMaterial);
        marker = g;
        rootNode.attachChild(marker);
    }

    protected void initWorksheet(int width, int height) {
        Quad quad = new Quad((float) width, (float) height);
        worksheet = new Geometry(WORKSHEET, quad);
        quad.scaleTextureCoordinates(new Vector2f(width, height));
        Material material = baseApplication.getAssetManager().loadMaterial("Resources/worksheet.j3m");
        worksheet.setMaterial(material);
        worksheet.move(-(width * 0.5f), -(height * 0.5f), -10f);
        rootNode.attachChild(worksheet);

    }

    @Override
    protected void load() {

        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.Black);
        targetLookAtPoint = Vector3f.ZERO;
        camera.setLocation(cameraDefaultLocation);
        deleteActive = false;

        if (centerMarkerMaterial == null) {
            centerMarkerMaterial = assetManager.loadMaterial("Common/Materials/RedColor.j3m");
            centerMarkerMaterial.setColor("Color", ColorRGBA.Blue);
        }

        /*
         * The loadStart method is called very first when ever the user calls to go to this screen.
         * A black panel will by default be shown over the screen
         * One must normally load the level and player and inputs and camera stuff here.
         */
        game = initGame();
        game.edit(getEditFile());
        game.load();

        //Get the defautls
        selectedSprite = null;

        index = 0;
        selectedList = game.getTypeList(Platform2DGame.TYPE_TERRAIN);
        initWorksheet(101, 31);
        initMarker();
        initCenter();
        updateSelection();

        if (!baseApplication.isMobileApp()) {
            initInput();
        }

        //Load the inputs
        //Init the picker listener
        touchPickListener = new TouchPickListener(baseApplication.getCamera(), rootNode);
        touchPickListener.setPickListener(this);
        touchPickListener.registerWithInput(inputManager);

        joystickInputListener = new JoystickInputListener();
        joystickInputListener.setJoystickListener(this);
        joystickInputListener.registerWithInput(inputManager);

    }

    @Override
    protected void exit() {
        /*
         * This exitDone method is called when the user leaves the current screen.
         * I a screen such as the play screen one will normally close the level or remove all
         * spatials and controls and lights, etc from the rootNode
         */
        touchPickListener.unregisterInput();
        joystickInputListener.unregisterInput();
        marker.removeFromParent();
        worksheet.removeFromParent();
        game.close();

    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (isActive()) {
//            log("Action = " + name);
            if (name != null) {
                if (game != null) {
                    
                    targetLookAtPoint = marker.getWorldTranslation().clone();

                    if (KEYBOARD_LEFT.equals(name)) {
                        if (isPressed) {
                            marker.move(-1, 0, 0);
                            updateSelectedSpritePosition(marker.getLocalTranslation().x, marker.getLocalTranslation().y);
//                            camera.setLocation(new Vector3f(marker.getLocalTranslation().x, marker.getLocalTranslation().y, camera.getLocation().z));
                        }
                    }

                    if (KEYBOARD_RIGHT.equals(name)) {
                        if (isPressed) {
                            marker.move(1, 0, 0);
                            updateSelectedSpritePosition(marker.getLocalTranslation().x, marker.getLocalTranslation().y);
//                            camera.setLocation(new Vector3f(marker.getLocalTranslation().x, marker.getLocalTranslation().y, camera.getLocation().z));
                        }
                    }

                    if (KEYBOARD_UP.equals(name)) {
                        if (isPressed) {
                            marker.move(0, 1, 0);
                            updateSelectedSpritePosition(marker.getLocalTranslation().x, marker.getLocalTranslation().y);
//                            camera.setLocation(new Vector3f(marker.getLocalTranslation().x, marker.getLocalTranslation().y, camera.getLocation().z));
                        }

                    }

                    if (KEYBOARD_DOWN.equals(name)) {
                        if (isPressed) {
                            marker.move(0, -1, 0);
                            updateSelectedSpritePosition(marker.getLocalTranslation().x, marker.getLocalTranslation().y);
//                            camera.setLocation(new Vector3f(marker.getLocalTranslation().x, marker.getLocalTranslation().y, camera.getLocation().z));
                        }

                    }

                    if (KEYBOARD_ADD.equals(name)) {
                        if (isPressed) {
                            if (selectedSprite != null && selectedList.size() > 0) {
                                Vector3f pos = selectedSprite.getWorldTranslation();
                                Sprite sprite = game.getItem(selectedList.get(index));
                                Tile tile = new Tile(pos.x, pos.y, sprite.getWorldTranslation().z, selectedList.get(index));
                                tile.setSpatial(sprite);
                                game.addTile(tile);
                                updateSelection();

                            }
                        }

                    }
//
//                    if (KEYBOARD_DEL.equals(name)) {
//                        if (isPressed) {
//                            level.removeObject(getMarkerPosition());
//                            updateObjectCount();
//                        }
//
//                    }
                }

            }


        }
    }

    public void stick(JoystickEvent joystickEvent, float fps) {
        if (isActive()) {
            if (game != null && joystickEvent.isKeyDown()) {
                
                targetLookAtPoint = marker.getWorldTranslation().clone();

                if (joystickEvent.isLeft()) {
                    marker.move(-1, 0, 0);
                    updateSelectedSpritePosition(marker.getLocalTranslation().x, marker.getLocalTranslation().y);
//                    camera.setLocation(new Vector3f(marker.getLocalTranslation().x, marker.getLocalTranslation().y, camera.getLocation().z));
                }

                if (joystickEvent.isRight()) {

                    marker.move(1, 0, 0);
                    updateSelectedSpritePosition(marker.getLocalTranslation().x, marker.getLocalTranslation().y);
//                    camera.setLocation(new Vector3f(marker.getLocalTranslation().x, marker.getLocalTranslation().y, camera.getLocation().z));

                }

                if (joystickEvent.isUp()) {

                    marker.move(0, 1, 0);
                    updateSelectedSpritePosition(marker.getLocalTranslation().x, marker.getLocalTranslation().y);
//                    camera.setLocation(new Vector3f(marker.getLocalTranslation().x, marker.getLocalTranslation().y, camera.getLocation().z));


                }

                if (joystickEvent.isDown()) {

                    marker.move(0, -1, 0);
                    updateSelectedSpritePosition(marker.getLocalTranslation().x, marker.getLocalTranslation().y);
//                    camera.setLocation(new Vector3f(marker.getLocalTranslation().x, marker.getLocalTranslation().y, camera.getLocation().z));


                }

                if (joystickEvent.isButton3()) {

                    if (selectedSprite != null && selectedList.size() > 0) {
                        Vector3f pos = selectedSprite.getWorldTranslation();
                        Sprite sprite = game.getItem(selectedList.get(index));
                        Tile tile = new Tile(pos.x, pos.y, sprite.getWorldTranslation().z, selectedList.get(index));
                        tile.setSpatial(sprite);
                        game.addTile(tile);
                        updateSelection();

                    }

                }

                if (joystickEvent.isButton4()) {
                    game.removeTileAtPosition(marker.getLocalTranslation());
                    updateSelection();
                }

                if (joystickEvent.isButton5()) {
                    swapTileUp();
                }

                if (joystickEvent.isButton6()) {
                    swapTileDown();
                }
            }
        }
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

            //Update the marker
            marker.setLocalTranslation((int) pickEvent.getContactPoint().x, (int) pickEvent.getContactPoint().y, 1);

            if (pickEvent.isKeyDown() && !descriptionDialog.isVisible() && pickEvent.getContactObject() != null
                    && pickEvent.getCursorPosition().x > (buttonSize * window.getScaleFactorWidth())
                    && pickEvent.getCursorPosition().x < window.getWidth() - (buttonSize * window.getScaleFactorWidth())
                    && pickEvent.getCursorPosition().y < window.getHeight() - (buttonSize * window.getScaleFactorHeight())) {
                targetLookAtPoint = marker.getWorldTranslation().clone();
            }

            /**
             * Here we add a new tile
             */
            if (!pickEvent.isKeyDown() && pickEvent.isLeftButton() && !descriptionDialog.isVisible() && pickEvent.getContactPoint() != null
                    && pickEvent.getContactObject() != null
                    && pickEvent.getCursorPosition().x > (buttonSize * window.getScaleFactorWidth())
                    && pickEvent.getCursorPosition().x < window.getWidth() - (buttonSize * window.getScaleFactorWidth())
                    && pickEvent.getCursorPosition().y < window.getHeight() - (buttonSize * window.getScaleFactorHeight())) {

                //Check if a remove can happen on mobile devices
                if (deleteActive && pickEvent.getContactObject().getParent() instanceof Sprite) {
                    //Remove a sprite
                    log(pickEvent.getContactObject().getName());
                    game.removeTile((Sprite) pickEvent.getContactObject().getParent());
                    updateSelection();

                }

                //Check to see if we may add
                if (!deleteActive && pickEvent.getContactObject().getName().contains(Platform2DGame.TYPE_SKY)
                        || pickEvent.getContactObject().getName().contains(WORKSHEET)
                        || pickEvent.getContactObject().getParent().equals(selectedSprite)) {

                    if (selectedSprite != null && selectedList.size() > 0) {
//                        Vector3f pos = selectedSprite.getWorldTranslation();                        
                        Vector3f pos = marker.getLocalTranslation();

                        Sprite sprite = game.getItem(selectedList.get(index));
                        Tile tile = new Tile(pos.x, pos.y, sprite.getWorldTranslation().z, selectedList.get(index));
                        tile.setSpatial(sprite);
                        game.addTile(tile);
                        updateSelection();

                    }

                }

            }

            //Remove tile using the right mouse button
            if (pickEvent.isKeyDown() && pickEvent.isRightButton() && !descriptionDialog.isVisible()
                    && pickEvent.getContactObject() != null && !pickEvent.getContactObject().getName().equals(WORKSHEET)
                    && !pickEvent.getContactObject().getName().contains(Platform2DGame.TYPE_SKY)
                    && !pickEvent.getContactObject().getParent().equals(selectedSprite)
                    && pickEvent.getCursorPosition().x > (buttonSize * window.getScaleFactorWidth())
                    && pickEvent.getCursorPosition().x < window.getWidth() - (buttonSize * window.getScaleFactorWidth())
                    && pickEvent.getCursorPosition().y < window.getHeight() - (buttonSize * window.getScaleFactorHeight())) {
                //Remove a sprite
                log(pickEvent.getContactObject().getName());
                game.removeTile((Sprite) pickEvent.getContactObject().getParent());
                updateSelection();

            }
        }
    }

    @Override
    public void update(float tpf) {
        /**
         * We override the update loop so that we can move the camera position.
         */
        if (isActive() && !lockMovement) {
            camera.setLocation(camera.getLocation().interpolate(camera.getLocation().clone().setX(targetLookAtPoint.x).setY(targetLookAtPoint.y), 0.02f));

        }
    }

    /**
     * When draging happens
     *
     * @param pickEvent
     * @param tpf
     */
    public void drag(PickEvent pickEvent, float tpf) {
        if (isActive() && game != null && !descriptionDialog.isVisible()) {

            if (pickEvent.isZoomDown()) {
                swapTileDown();

            }
            if (pickEvent.isZoomUp()) {
                swapTileUp();

            }

            //Update the marker pos
            if (pickEvent.getContactPoint() != null
                    && pickEvent.getCursorPosition().x > (buttonSize * window.getScaleFactorWidth())
                    && pickEvent.getCursorPosition().x < window.getWidth() - (buttonSize * window.getScaleFactorWidth())
                    && pickEvent.getCursorPosition().y < window.getHeight() - (buttonSize * window.getScaleFactorHeight())) {

                marker.setLocalTranslation((int) pickEvent.getContactPoint().x, (int) pickEvent.getContactPoint().y, 1);
                updateSelectedSpritePosition((int) pickEvent.getContactPoint().x, (int) pickEvent.getContactPoint().y);
            }

            if (pickEvent.isKeyDown() && !descriptionDialog.isVisible() && pickEvent.getContactObject() != null
                    && pickEvent.getCursorPosition().x > (buttonSize * window.getScaleFactorWidth())
                    && pickEvent.getCursorPosition().x < window.getWidth() - (buttonSize * window.getScaleFactorWidth())
                    && pickEvent.getCursorPosition().y < window.getHeight() - (buttonSize * window.getScaleFactorHeight())) {
                targetLookAtPoint = marker.getWorldTranslation().clone();
            }

        }
    }

    protected void initCenter() {
        addLine(rootNode, new Vector3f(-1f, 0, 1f), new Vector3f(1f, 0, 1f));
        addLine(rootNode, new Vector3f(0f, 1f, 1f), new Vector3f(0f, -1f, 1f));
    }

    protected void addLine(Node parent, Vector3f start, Vector3f end) {

        Line line = new Line(start, end);
        line.setLineWidth(2);

        Geometry geometry = new Geometry("line_geom", line);
        geometry.setMaterial(centerMarkerMaterial);
        parent.attachChild(geometry);
    }
}
