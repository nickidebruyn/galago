/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.screens.editor;

import com.bruynhuis.galago.games.platform2d.Tile;
import com.bruynhuis.galago.listener.PickEvent;
import com.bruynhuis.galago.listener.PickListener;
import com.bruynhuis.galago.listener.TouchPickListener;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.Iterator;
import com.galago.example.platformer2d.MainApplication;
import com.galago.example.platformer2d.game.Game;
import com.galago.example.platformer2d.screens.PlayScreen;

/**
 *
 * @author Nidebruyn
 */
public class EditScreen extends AbstractScreen implements PickListener {
    
//    private static int TILE_TOP_LEFT = 0;
//    private static int TILE_TOP = 1;
//    private static int TILE_TOP_RIGHT = 2;
//    private static int TILE_LEFT = 8;
//    private static int TILE_LEFT_SINGLE = 11;
//    private static int TILE_CENTER = 9;
//    private static int TILE_RIGHT = 10;
//    private static int TILE_RIGHT_SINGLE = 13;
//    private static int TILE_DOWN_LEFT = 16;
//    private static int TILE_DOWN = 17;
//    private static int TILE_DOWN_RIGHT = 18;
//    private static int TILE_TOP_SINGLE = 4;
//    private static int TILE_DOWN_SINGLE = 12;

    public static final String FILE_EXT = ".p2lv";
    private MainApplication mainApplication;
    private Game game;
    private TouchPickListener touchPickListener;
    private FileNameDialog fileNameDialog;
    private ConfirmDialog trashConfirmDialog;
    private ConfirmDialog saveConfirmDialog;
    private int columns = 32;
    private int rows = 18;
    private Toolbar toolbar;
    private Menubar menubar;
    private String fileName = "default-level" + FILE_EXT;
    private boolean floodFill = false;
    private ArrayList<Sprite> worksheetTiles = new ArrayList<Sprite>();
    private Label levelNameLabel;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;

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
                        PlayScreen playScreen = mainApplication.getPlayScreen();
                        playScreen.setTest(true);
                        playScreen.setEditFile(fileName);
                        showScreen("play");
                    }

                }
            }
        });
        menubar.rightTop(0, 0);

        toolbar = new Toolbar(hudPanel);
        toolbar.leftTop(0, 0);

        levelNameLabel = new Label(hudPanel, fileName, 24);
        levelNameLabel.setAlignment(TextAlign.RIGHT);
        levelNameLabel.rightBottom(10, 0);

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

        touchPickListener = new TouchPickListener(camera, rootNode);
        touchPickListener.setPickListener(this);
    }

    @Override
    protected void load() {

        game = new Game(mainApplication, rootNode);
        game.edit(fileName);
        game.load();

        levelNameLabel.setText(fileName.replace(FILE_EXT, ""));

        loadWorksheet();

        camera.setLocation(new Vector3f(-1.2f, 0.8f, 10));

        if (mainApplication.isMobileApp()) {
            mainApplication.setCameraDistanceFrustrum(9.4f);
        } else {
            mainApplication.setCameraDistanceFrustrum(10f);
        }

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

        worksheet.center();

    }

    @Override
    protected void show() {
        setPreviousScreen(null);
        touchPickListener.registerWithInput(inputManager);

    }

    @Override
    protected void exit() {
        touchPickListener.unregisterInput();
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
                && !fileNameDialog.isVisible();
    }

    public void picked(PickEvent pickEvent, float tpf) {

        if (pickEvent.isKeyDown() && pickEvent.getContactObject() != null && isPickPositionValid(pickEvent)) {

            if (pickEvent.getContactObject().getParent() instanceof Sprite) {

                Sprite sprite = (Sprite) pickEvent.getContactObject().getParent();
                Tile selectedTile = game.getTileAtPosition(sprite.getWorldTranslation());

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

                    }
                }

            }

        }

    }

    private void updateTileAtPosition(String newItem, Tile tile) {
        if (tile != null) {
            game.removeTile(tile);

            //Now we add the new item
            log("Update tile:" + tile.getUid() + " to newtile:" + newItem);
            Sprite sprite = game.getItem(newItem);
            if (sprite != null) {
                Tile newtile = new Tile(tile.getxPos(), tile.getyPos(), sprite.getWorldTranslation().z, newItem);
                newtile.setSpatial(sprite);
                game.addTile(newtile);
            }

        }
    }
    
    private String getTileByNumber(String selectedItem, int tileNum) {
        return selectedItem + "," + tileNum;
    }

//    private String calculateRealTerrainTileItem(String selectedItem, Vector3f pos) {
//        String item = getTileByNumber(selectedItem, TILE_CENTER);
//        
//        Tile tileUp = game.getTileAtPosition(pos.add(0, 1, 0));
//        Tile tileUp2 = game.getTileAtPosition(pos.add(0, 2, 0));
//        Tile tileUpLeft = game.getTileAtPosition(pos.add(-1, 1, 0));
//        Tile tileUpRight = game.getTileAtPosition(pos.add(1, 1, 0));
//
//        Tile tileDown = game.getTileAtPosition(pos.add(0, -1, 0));
//        Tile tileDown2 = game.getTileAtPosition(pos.add(0, -2, 0));
//        Tile tileDownLeft = game.getTileAtPosition(pos.add(-1, -1, 0));
//        Tile tileDownRight = game.getTileAtPosition(pos.add(1, -1, 0));
//
//        Tile tileLeft = game.getTileAtPosition(pos.add(-1, 0, 0));
//        Tile tileLeft2 = game.getTileAtPosition(pos.add(-2, 0, 0));
//        Tile tileRight = game.getTileAtPosition(pos.add(1, 0, 0));
//        Tile tileRight2 = game.getTileAtPosition(pos.add(2, 0, 0));
//
//        log("==============================");
//        log("|Tile-Up\t:" + tileUp);
//        log("|Tile-Up2\t:" + tileUp2);
//        log("|Tile-Up-L\t:" + tileUpLeft);
//        log("|Tile-Up-R\t:" + tileUpRight);
//        log("|Tile-Down\t:" + tileDown);
//        log("|Tile-Down2\t:" + tileDown2);
//        log("|Tile-Down-L\t:" + tileDownLeft);
//        log("|Tile-Down-R\t:" + tileDownRight);
//        log("|Tile-Left\t:" + tileLeft);
//        log("|Tile-Left2\t:" + tileLeft2);
//        log("|Tile-Right\t:" + tileRight);
//        log("==============================");
//        
//                
//        //SINGLE TILE
//        if (tileDown == null && tileUp == null && tileLeft == null && tileRight == null) {
//            item = getTileByNumber(selectedItem, TILE_TOP_SINGLE);
//            
//        }
//        //TILES HORIZONTAL ALONE
//        else if (tileDown == null && tileUp == null && tileLeft != null && tileRight == null && tileLeft2 == null) {
//            item = getTileByNumber(selectedItem, TILE_RIGHT_SINGLE);
//            
//            if (tileUpLeft == null) {
//                updateTileAtPosition(getTileByNumber(selectedItem, TILE_LEFT_SINGLE), tileLeft);
//            }
//            
//        }        
//        else if (tileDown == null && tileUp == null && tileLeft != null && tileRight == null && tileLeft2 != null) {
//            item = getTileByNumber(selectedItem, TILE_RIGHT_SINGLE);
//            
//            if (tileUpLeft == null) {
//                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP), tileLeft);
//            }
//            
//            
//        }
//        else if (tileDown == null && tileUp == null && tileLeft == null && tileRight != null && tileRight2 == null) {
//            item = getTileByNumber(selectedItem, TILE_LEFT_SINGLE);
//            
//            if (tileUpRight == null) {
//                updateTileAtPosition(getTileByNumber(selectedItem, TILE_RIGHT_SINGLE), tileRight);
//            }
//            
//        }        
//        else if (tileDown == null && tileUp == null && tileLeft == null && tileRight != null && tileRight2 != null) {
//            item = getTileByNumber(selectedItem, TILE_LEFT_SINGLE);
//            
//            if (tileUpRight == null) {
//                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP), tileRight);
//            }
//            
//        }   
//        else if (tileDown == null && tileUp == null && tileLeft != null && tileRight != null) {
//            item = getTileByNumber(selectedItem, TILE_TOP);
//            updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP), tileRight);
//            updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP), tileLeft);
//        }   
//        //TILES BELOW
//        else if (tileDown != null && tileUp == null && tileLeft == null && tileRight == null) {
//            item = getTileByNumber(selectedItem, TILE_TOP_SINGLE);
//            updateTileAtPosition(getTileByNumber(selectedItem, TILE_CENTER), tileDown);
//        }  
//        else if (tileDown != null && tileUp == null && tileLeft != null && tileRight == null) {
//            item = getTileByNumber(selectedItem, TILE_TOP);
//            updateTileAtPosition(getTileByNumber(selectedItem, TILE_CENTER), tileDown);
//            
//            if (tileUpLeft == null) {
//                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP), tileLeft);
//            }
//        }  
//        else if (tileDown != null && tileUp == null && tileLeft == null && tileRight != null) {
//            item = getTileByNumber(selectedItem, TILE_TOP);
//            updateTileAtPosition(getTileByNumber(selectedItem, TILE_CENTER), tileDown);
//            
//            if (tileUpRight == null) {
//                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP), tileRight);
//            } 
//        }  
//        else if (tileDown != null && tileUp == null && tileLeft != null && tileRight != null) {
//            item = getTileByNumber(selectedItem, TILE_TOP);
//            updateTileAtPosition(getTileByNumber(selectedItem, TILE_CENTER), tileDown);
//        }  
//        //TILES ABOVE
//        else if (tileDown == null && tileUp != null && tileLeft == null && tileRight == null && tileUpLeft == null && tileUpRight != null) {
//            item = getTileByNumber(selectedItem, TILE_CENTER);
//            updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP), tileUp);
//            
//        }  
////        else if (tileDown == null && tileUp != null && tileLeft == null && tileRight == null && tileUpLeft != null && tileUpRight == null) {
////            item = getTileByNumber(selectedItem, TILE_CENTER);
////            updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP), tileUp);
////            
////        }  
////        else if (tileDown == null && tileUp != null && tileLeft != null && tileRight == null) {
////            item = getTileByNumber(selectedItem, TILE_RIGHT);
////            
////            if (tileUp2 == null) {
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP_RIGHT), tileUp);
////            }
////            
////        }  
////        else if (tileDown == null && tileUp != null && tileLeft == null && tileRight != null) {
////            item = getTileByNumber(selectedItem, TILE_LEFT);
////            
////            if (tileUp2 == null) {
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP_LEFT), tileUp);
////            }
////            
////        }  
//
////        // Vertical no left or right
////        if (tileLeft == null && tileRight == null) {
////
////            if (tileUp == null && tileDown == null) {
////                item = getTileByNumber(selectedItem, TILE_TOP_SINGLE);
////                
////            } else if (tileUp != null && tileDown != null) {
////                item = getTileByNumber(selectedItem, TILE_CENTER);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_CENTER), tileDown);
////
////            } else if (tileUp != null && tileDown == null) {
////                item = getTileByNumber(selectedItem, TILE_CENTER);
////                
////                if (tileUp2 == null) {
////                    updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP), tileUp);
////                }                
////
////            } else if (tileUp == null && tileDown != null) {
////                item = getTileByNumber(selectedItem, TILE_TOP_SINGLE);                
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_CENTER), tileDown);
////            }
////
////        }         
////        //Vertical left single
////        else if (tileLeft != null && tileRight == null) {
////            
////            if (tileUp == null && tileDown == null) {
////                item = getTileByNumber(selectedItem, TILE_RIGHT_SINGLE);
////                
////                if (tileLeft2 == null) {
////                    updateTileAtPosition(getTileByNumber(selectedItem, TILE_LEFT_SINGLE), tileLeft);
////                } else {
////                    updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP), tileLeft);
////                }
////                
////                
////            } else if (tileUp != null && tileDown == null) {
////                item = getTileByNumber(selectedItem, TILE_CENTER);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_LEFT_SINGLE), tileLeft);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP_SINGLE), tileUp);
////                
////            } else if (tileUp == null && tileDown != null) {
////                item = getTileByNumber(selectedItem, TILE_TOP_RIGHT);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_LEFT_SINGLE), tileLeft);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_RIGHT), tileDown);
////                
////            } else if (tileUp != null && tileDown != null) {
////                item = getTileByNumber(selectedItem, TILE_CENTER);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_LEFT_SINGLE), tileLeft);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_CENTER), tileDown);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP_SINGLE), tileUp);
////                
////            }
////            
////        }
////        //Vertical right single
////        else if (tileLeft == null && tileRight != null) {
////            
////            if (tileUp == null && tileDown == null) {
////                item = getTileByNumber(selectedItem, TILE_LEFT_SINGLE);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_RIGHT_SINGLE), tileRight);
////                
////            } else if (tileUp != null && tileDown == null) {
////                item = getTileByNumber(selectedItem, TILE_CENTER);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_RIGHT_SINGLE), tileRight);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP_SINGLE), tileUp);
////                
////            } else if (tileUp == null && tileDown != null) {
////                item = getTileByNumber(selectedItem, TILE_TOP_LEFT);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_RIGHT_SINGLE), tileRight);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_LEFT), tileDown);
////                
////            } else if (tileUp != null && tileDown != null) {
////                item = getTileByNumber(selectedItem, TILE_CENTER);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_RIGHT_SINGLE), tileRight);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_CENTER), tileDown);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP_SINGLE), tileUp);
////                
////            }
////            
////        }
////        //Vertical left and right single
////        else if (tileLeft != null && tileRight != null) {
////            
////            if (tileUp == null && tileDown == null) {
////                item = getTileByNumber(selectedItem, TILE_TOP);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_LEFT_SINGLE), tileLeft);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_RIGHT_SINGLE), tileRight);
////                
////            } else if (tileUp != null && tileDown == null) {
////                item = getTileByNumber(selectedItem, TILE_TOP_SINGLE);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_RIGHT_SINGLE), tileRight);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_LEFT_SINGLE), tileLeft);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP_SINGLE), tileUp);
////                
////            } else if (tileUp == null && tileDown != null) {
////                item = getTileByNumber(selectedItem, TILE_TOP);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_LEFT_SINGLE), tileLeft);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_RIGHT_SINGLE), tileRight);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_CENTER), tileDown);
////                
////            } else if (tileUp != null && tileDown != null) {
////                item = getTileByNumber(selectedItem, TILE_CENTER);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_LEFT_SINGLE), tileLeft);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_RIGHT_SINGLE), tileRight);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_CENTER), tileDown);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP_SINGLE), tileUp);
////                
////            }
////            
////        }
//        
//        
//        
//        
//        
//        
//        
//        
//        
//        
//        
//        
//        
//        
//        
//        
//        
//        
//        
////        else if (tileLeft == null && tileRight != null && tileRight2 == null) {
////
////            if (tileUp == null && tileDown == null) {
////                item = getTileByNumber(selectedItem, TILE_TOP_LEFT);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP_RIGHT), tileRight);
////
////            } else if (tileUp != null && tileDown == null) {
////                item = getTileByNumber(selectedItem, TILE_CENTER);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP_RIGHT), tileRight);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP_SINGLE), tileUp);
////
////            } else if (tileUp == null && tileDown != null) {
////                item = getTileByNumber(selectedItem, TILE_TOP_LEFT);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP), tileRight);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_LEFT), tileDown);
////
////            }
////
////        } else if (tileLeft == null && tileRight != null && tileRight2 != null) {
////
////            if (tileUp == null && tileDown == null) {
////                item = getTileByNumber(selectedItem, TILE_TOP_LEFT);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP), tileRight);
////
////                //If tile is a middle tile skip update
////                if (!tileRight2.getUid().equalsIgnoreCase(getTileByNumber(selectedItem, TILE_TOP))) {
////                    updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP_RIGHT), tileRight2);
////                }
////
////            } else if (tileUp != null && tileDown == null) {
////                //TODO
////
////            } else if (tileUp == null && tileDown != null) {
////                //TODO
////
////            }
////
////        } else if (tileLeft != null && tileRight == null && tileLeft2 == null) {
////
////            if (tileUp == null && tileDown == null) {
////                item = getTileByNumber(selectedItem, TILE_TOP_RIGHT);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP_LEFT), tileLeft);
////
////            } else if (tileUp != null && tileDown == null) {
////                item = getTileByNumber(selectedItem, TILE_CENTER);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP_LEFT), tileLeft);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP_SINGLE), tileUp);
////
////            } else if (tileUp == null && tileDown != null) {
////                item = getTileByNumber(selectedItem, TILE_TOP_RIGHT);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP_LEFT), tileLeft);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_RIGHT), tileDown);
////            }
////
////        } else if (tileLeft != null && tileRight == null && tileLeft2 != null) {
////
////            if (tileUp == null && tileDown == null) {
////                item = getTileByNumber(selectedItem, TILE_TOP_RIGHT);
////                updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP), tileLeft);
////
////                //If tile is a middle tile skip update
////                if (!tileLeft2.getUid().equalsIgnoreCase(getTileByNumber(selectedItem, TILE_TOP))) {
////                    updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP_LEFT), tileLeft2);
////                }
////
////            } else if (tileUp != null && tileDown == null) {
////                //TODO
////
////            } else if (tileUp == null && tileDown != null) {
////                //TODO
////
////            }
////        } else if (tileLeft != null && tileRight != null) {
////            if (tileUp == null && tileDown == null) {
////                if (tileLeft.getUid().equalsIgnoreCase(getTileByNumber(selectedItem, TILE_TOP_LEFT))) {
////                    updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP), tileLeft);
////                }
////                if (tileRight.getUid().equalsIgnoreCase(getTileByNumber(selectedItem, TILE_TOP_RIGHT))) {
////                    updateTileAtPosition(getTileByNumber(selectedItem, TILE_TOP), tileRight);
////                }
////                item = getTileByNumber(selectedItem, TILE_TOP);
////                
////            }
////            
////        }
//
//        return item;
//    }

    private void doPaintAction(float x, float y) {
//        log("doPaintAction: " + x + ", " + y);
        Vector3f pos = new Vector3f(x, y, 0);

        String selectedItem = toolbar.getSelectedItem();
        log("doPaintAction: " + selectedItem + ", " + x + ", " + y);

        if (selectedItem != null && !selectedItem.equals("erase")) {

//            if (selectedItem.startsWith("terrain-")) {
//                selectedItem = calculateRealTerrainTileItem(selectedItem, pos);
//            }

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
}
