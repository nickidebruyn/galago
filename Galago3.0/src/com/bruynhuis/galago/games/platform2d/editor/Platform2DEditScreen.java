/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.platform2d.editor;

import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.games.platform2d.Platform2DGame;
import com.bruynhuis.galago.games.platform2d.Tile;
import com.bruynhuis.galago.listener.KeyboardControlEvent;
import com.bruynhuis.galago.listener.KeyboardControlInputListener;
import com.bruynhuis.galago.listener.KeyboardControlListener;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Nidebruyn
 */
public abstract class Platform2DEditScreen extends AbstractScreen implements PickListener, KeyboardControlListener {

    public static final String FILE_EXT = ".blv";
    private Base2DApplication mainApplication;
    private TouchPickListener touchPickListener;
    private KeyboardControlInputListener keyboardControlInputListener;
    private FileNameDialog fileNameDialog;
    private ConfirmDialog trashConfirmDialog;
    private ConfirmDialog saveConfirmDialog;
    private Platform2DGame game;
    protected int columns = 26;
    protected int rows = 16;
    protected Toolbar toolbar;
    private Menubar menubar;
    protected String fileName = "default-level" + FILE_EXT;
    private boolean floodFill = false;
    private ArrayList<Sprite> worksheetTiles = new ArrayList<Sprite>();
    private boolean moveLeft;
    private boolean moveRight;
    private float moveSpeed = 5;
    private boolean moveUp;
    private boolean moveDown;
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

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

    @Override
    protected void init() {
        mainApplication = (Base2DApplication) baseApplication;

        menubar = new Menubar(hudPanel, new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
//                    log("Doing action: " + uid);

                    if (Menubar.ACTION_NEW.equals(uid)) {
                        fileNameDialog.show();

                    } else if (Menubar.ACTION_OPEN.equals(uid)) {
                        showScreen("fileselect");

                    } else if (Menubar.ACTION_DRAW.equals(uid)) {
                        floodFill = false;

                    } else if (Menubar.ACTION_PAINT.equals(uid)) {
                        floodFill = true;

                    } else if (Menubar.ACTION_SAVE.equals(uid)) {
                        game.save();
                        saveConfirmDialog.setText("Level " + fileName.replaceAll(FILE_EXT, "") + " saved successfully!");
                        saveConfirmDialog.show();
                        
                    } else if (Menubar.ACTION_TRASH.equals(uid)) {
                        trashConfirmDialog.show();

                    } else if (Menubar.ACTION_PLAY.equals(uid)) {
                        game.save();
                        doTestAction();
                    }

                }
            }
        });
        menubar.rightTop(0, 0);

        toolbar = new Toolbar(hudPanel);
        toolbar.leftTop(0, 0);

        fileNameDialog = new FileNameDialog(window);
        fileNameDialog.addOkButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {

                    String nameText = fileNameDialog.getFileName().trim();
                    if (!nameText.equals("")) {
                        setFileName(nameText + FILE_EXT);
                        showScreen("edit");
                    }

                }
            }
        });
        
        trashConfirmDialog = new ConfirmDialog(window, "Are you sure you want to start the level over?");
        trashConfirmDialog.addOkButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    game.clear();
                    trashConfirmDialog.hide();

                }
            }
        });
        
        saveConfirmDialog = new ConfirmDialog(window, "Level saved successfully!");
        saveConfirmDialog.addOkButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    saveConfirmDialog.hide();

                }
            }
        });
        
        initUI();

        touchPickListener = new TouchPickListener(camera, rootNode);
        touchPickListener.setPickListener(this);
        
        keyboardControlInputListener = new KeyboardControlInputListener();
        keyboardControlInputListener.addKeyboardControlListener(this);
    }
    
    protected abstract void initUI();

    @Override
    protected void load() {

        moveLeft = false;
        moveRight = false;
        moveUp = false;
        moveDown = false;
        
        game = initGame();        
        game.edit(fileName);
        game.load();

        loadWorksheet();
//
        camera.setLocation(new Vector3f(game.getStartPosition().x, game.getStartPosition().y, 10));
//
//        if (mainApplication.isMobileApp()) {
//            mainApplication.setCameraDistanceFrustrum(9.4f);
//        } else {
//            mainApplication.setCameraDistanceFrustrum(9.8f);
//        }


    }

    private void loadWorksheet() {
        worksheetTiles.clear();
        Node worksheet = new Node("worksheet");
        rootNode.attachChild(worksheet);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                Sprite tile = new Sprite("marker", 1f, 1f);
                Material material = baseApplication.getAssetManager().loadMaterial("Resources/worksheet.j3m");
                tile.setMaterial(material);
                tile.setLocalTranslation(c, r, 0);
                worksheet.attachChild(tile);
                worksheetTiles.add(tile);
            }
        }

//        worksheet.center();

    }

    @Override
    protected void show() {
        setPreviousScreen(null);
        touchPickListener.registerWithInput(inputManager);
        keyboardControlInputListener.registerWithInput(inputManager);

    }

    @Override
    protected void exit() {
        touchPickListener.unregisterInput();
        keyboardControlInputListener.unregisterInput();
        game.close();
    }

    @Override
    protected void pause() {
    }

    private void doFloodFill(Tile selectedTile) {

        String selectedItem = toolbar.getSelectedItem();

        for (int i = 0; i < worksheetTiles.size(); i++) {
            Sprite sprite = worksheetTiles.get(i);

            Tile tile = game.getTileAtPosition(sprite.getWorldTranslation());

            if (tile != null && selectedItem != null && selectedItem.equals("erase")) {

                if (selectedTile.getUid().equals(tile.getUid())) {
                    log("Tile type to remove: " + tile.getUid());
                    game.removeTile(tile);
                }

            } else if (tile != null && selectedItem != null && !selectedItem.equals("erase")) {

                if (selectedTile != null && selectedTile.getUid().equals(tile.getUid())) {
                    log("Tile type to remove: " + tile.getUid());
                    game.removeTile(tile);
                    doPaintAction(sprite.getWorldTranslation().x, sprite.getWorldTranslation().y);

                }

            } else if (selectedTile == null && (tile == null || tile.getUid().startsWith("sky-"))) {
                doPaintAction(sprite.getWorldTranslation().x, sprite.getWorldTranslation().y);


            }
        }
    }

    private boolean isPickPositionValid(PickEvent pickEvent) {
        return pickEvent.getContactObject() != null && !pickEvent.getContactObject().getParent().getName().startsWith("sky-")
                && !fileNameDialog.isVisible() && !trashConfirmDialog.isVisible() && !saveConfirmDialog.isVisible();
    }

    public void picked(PickEvent pickEvent, float tpf) {

        if (pickEvent.isKeyDown() && pickEvent.getContactObject() != null && isPickPositionValid(pickEvent)) {

            if (pickEvent.getContactObject().getParent() instanceof Sprite) {

                Sprite sprite = (Sprite) pickEvent.getContactObject().getParent();                
                Tile selectedTile = game.getTileAtPosition(sprite.getWorldTranslation());
                log("Tile at pos: " + sprite.getWorldTranslation());

                if (floodFill) {
                    log("Flood fill: " + selectedTile);
                    doFloodFill(selectedTile);

                } else {
                    log("Picked: " + selectedTile);

                    if (selectedTile == null) {
                        doPaintAction(sprite.getWorldTranslation().x, sprite.getWorldTranslation().y);

                    } else if (toolbar.getSelectedItem() != null && toolbar.getSelectedItem().equals("erase")) {
                        log("Removing tile: " + selectedTile.getUid());

                        if (selectedTile != null && !selectedTile.getUid().startsWith("sky-")) {
                            game.removeTile(selectedTile);
                        }
                    } else if (toolbar.getSelectedItem() != null && selectedTile != null && !game.hasTileAtPosition(sprite.getWorldTranslation(), toolbar.getSelectedItem())) {
                        log("Selected Tile: " + selectedTile.getUid() + ";  Tile to add: " + toolbar.getSelectedItem());
                        doPaintAction(sprite.getWorldTranslation().x, sprite.getWorldTranslation().y);
                    }
                }

            }

        }

    }

    public void drag(PickEvent pickEvent, float tpf) {

        if (pickEvent.isKeyDown() && pickEvent.getContactObject() != null && isPickPositionValid(pickEvent)) {

            if (pickEvent.getContactObject().getParent() instanceof Sprite) {
                Sprite sprite = (Sprite) pickEvent.getContactObject().getParent();
                Tile selectedTile = game.getTileAtPosition(sprite.getWorldTranslation());

                if (!floodFill) {
//                    log("Picked: " + selectedTile);

                    if (selectedTile == null) {
                        doPaintAction(sprite.getWorldTranslation().x, sprite.getWorldTranslation().y);

                    } else if (toolbar.getSelectedItem() != null && toolbar.getSelectedItem().equals("erase")) {
                        log("Removing tile: " + selectedTile.getUid());

                        if (selectedTile != null && !selectedTile.getUid().startsWith("sky-")) {
                            game.removeTile(selectedTile);
                        }

                    } else if (toolbar.getSelectedItem() != null && selectedTile != null && !game.hasTileAtPosition(sprite.getWorldTranslation(), toolbar.getSelectedItem())) {
                        log("Selected Tile: " + selectedTile.getUid() + ";  Tile to add: " + toolbar.getSelectedItem());
                        doPaintAction(sprite.getWorldTranslation().x, sprite.getWorldTranslation().y);
                    }
                }


            }

        }

    }

    private void doPaintAction(float x, float y) {
//        log("doPaintAction: " + x + ", " + y);
        Vector3f pos = new Vector3f(x, y, 0);

        String selectedItem = toolbar.getSelectedItem();
        log("doPaintAction: " + selectedItem + ", " + x + ", " + y);
        if (selectedItem != null && !selectedItem.equals("erase")) {
            Sprite sprite = game.getItem(selectedItem);

            //First let's check for an existing sky
            if (selectedItem.startsWith("sky-")) {
                Tile existingTile = null;
                for (Iterator<Tile> tile = game.getTileMap().getTiles().iterator(); tile.hasNext();) {
                    Tile tile1 = tile.next();
                    if (tile1.getUid().startsWith("sky-")) {
                        existingTile = tile1;
                    }
                }

                if (existingTile != null) {
                    log("Removing sky");
                    game.removeTile(existingTile);
                }
            }

            //Now we add a new sky
            Tile tile = new Tile(pos.x, pos.y, sprite.getWorldTranslation().z, selectedItem);
            tile.setSpatial(sprite);
            game.addTile(tile);
        }


    }

    @Override
    public void onKey(KeyboardControlEvent keyboardControlEvent, float fps) {
        if (isActive()) {
            
            if (keyboardControlEvent.isLeft()) {
                moveLeft = keyboardControlEvent.isKeyDown();
                
            }
            
            if (keyboardControlEvent.isRight()) {
                moveRight = keyboardControlEvent.isKeyDown();
                
            }
            
            if (keyboardControlEvent.isUp()) {
                moveUp = keyboardControlEvent.isKeyDown();
                
            }
            
            if (keyboardControlEvent.isDown()) {
                moveDown = keyboardControlEvent.isKeyDown();
                
            }
            
        }
    }

    @Override
    public void update(float tpf) {
        if (isActive()) {
            if (moveLeft) {
                camera.setLocation(camera.getLocation().add(-tpf*moveSpeed, 0, 0));
                
            } else if (moveRight) {
                camera.setLocation(camera.getLocation().add(tpf*moveSpeed, 0, 0));
                
            } else if (moveUp) {
                camera.setLocation(camera.getLocation().add(0, tpf*moveSpeed, 0));
                
            } else if (moveDown) {
                camera.setLocation(camera.getLocation().add(0, -tpf*moveSpeed, 0));
            }
        }
    }
    
    
}
