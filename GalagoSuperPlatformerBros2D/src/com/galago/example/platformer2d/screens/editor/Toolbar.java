/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.screens.editor;

import com.bruynhuis.galago.control.FlickerControl;
import com.bruynhuis.galago.games.platform.PlatformGame;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Widget;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.util.Debug;
import com.jme3.math.FastMath;
import com.jme3.system.JmeSystem;
import com.jme3.system.Platform;
import java.io.File;
import java.net.URL;
import java.util.logging.Logger;

/**
 *
 * @author NideBruyn
 */
public class Toolbar extends Panel implements TouchButtonListener {

    private float yVal = 7;
    private float xVal = 130;
    private boolean shifted = false;
    private String selectedItem = "erase";
    private Image selectedImage;

    public Toolbar(Panel parent) {
        super(parent, "Interface/editor/side-panel-left.png", 124, 900);

        addToolTopButton("erase", "Interface/editor/icon-erase.png", 0);
//        addSpace();
        addToolTopButton("start", "Textures/other/start.png", 0);
        addToolTopButton("end", "Textures/other/end.png", 0);
        addToolTopButton("pickup-star", "Textures/pickup/star.png", 0);

        xVal += 20f;

        addToolTopButton("sky-default", "Interface/editor/icon-default.png", 0);
        addToolTopButton("sky-blue", "Interface/editor/icon-blue.png", 0);
        addToolTopButton("sky-orange", "Interface/editor/icon-orange.png", 0);
        addToolTopButton("sky-green", "Interface/editor/icon-green.png", 0);
        addToolTopButton("sky-red", "Interface/editor/icon-red.png", 0);
        addToolTopButton("sky-purple", "Interface/editor/icon-purple-back.png", 0);

        //READ THE TERRAINS
        addTerrainTools();

        addSpace();
//        
////        addToolButton("static-glass", "Textures/terrain/terrain-glass.png", 0);
//        addToolButton("static-ice", "Textures/terrain/terrain-ice.png", 0);
//        addToolButton("static-crate", "Textures/terrain/terrain-crate2.png", 0);        
        addToolButton("static-platform-horizontal", "Interface/editor/icon-platform-h.png", 0);
        addToolButton("static-platform-vertical", "Interface/editor/icon-platform-v.png", 0);
        addToolButton("static-portal-blue-in", "Textures/static/portal-blue.png", 0);
        addToolButton("static-portal-blue-out", "Textures/static/portal-blue.png", 0);
//        addToolButton("static-portal-yellow-in", "Textures/static/portal-yellow.png", 0);
//        addToolButton("static-portal-yellow-out", "Textures/static/portal-yellow.png", 0);
//        addToolButton("static-portal-purple-in", "Textures/static/portal-purple.png", 0);
//        addToolButton("static-portal-purple-out", "Textures/static/portal-purple.png", 0);

        addSpace();

        addToolButton("static-mushroom", "Textures/static/mushroom.png", 0);
//        
//        addToolButton("static-mover-right", "Interface/editor/icon-mover.png", 0);
//        addToolButton("static-mover-left", "Interface/editor/icon-mover.png", 180);
//        addToolButton("static-mover-up", "Interface/editor/icon-mover.png", 90);
//        addToolButton("static-mover-down", "Interface/editor/icon-mover.png", 270);
//        
        addSpace();
//        
        addToolButton("obstacle-spike-up", "Textures/obstacle/spikes.png", 0);
        addToolButton("obstacle-spike-down", "Textures/obstacle/spikes.png", 180);
        addToolButton("obstacle-spike-left", "Textures/obstacle/spikes.png", 90);
        addToolButton("obstacle-spike-right", "Textures/obstacle/spikes.png", 270);

        addToolButton("obstacle-blade-vertical", "Interface/editor/icon-blade.png", 0);
        addToolButton("obstacle-blade-horizontal", "Interface/editor/icon-blade.png", 90);
        addToolButton("obstacle-spike-ball", "Textures/obstacle/spike-ball.png", 0);

        selectedImage = new Image(this, "Interface/editor/toolbutton-highlight.png", 54, 54, true);
//        selectedImage.setBackgroundColor(ColorRGBA.Orange);
        selectedImage.leftTop(130, 7);
        selectedImage.getWidgetNode().addControl(new FlickerControl(3f));

        parent.add(this);
    }

    private void addToolButton(String id, String image, float angle) {
        float xxVal = shifted ? 54 : 0;

        ToolbarButton toolbarButton = new ToolbarButton(this, id, "Interface/editor/toolbutton.png", "Interface/editor/toolbutton-on.png");
        toolbarButton.leftTop(xxVal, yVal);
        toolbarButton.addTouchButtonListener(this);

        Image img = new Image(this, image, 32, 32, true);
        img.leftTop(xxVal + 11, yVal + 11);
        img.rotate(angle * FastMath.DEG_TO_RAD);

        if (shifted) {
            yVal += 54;
            shifted = false;
        } else {
            shifted = true;

        }

    }

    private void addSpace() {
        if (shifted) {
            yVal += 66;
            shifted = false;
        } else {
            yVal += 12;
        }

        Image img = new Image(this, "Interface/line.png", 100, 10, true);
        img.leftTop(0, yVal - 10);
    }

    private void addToolTopButton(String id, String image, float angle) {

        ToolbarButton toolbarButton = new ToolbarButton(this, id, "Interface/editor/toolbutton.png", "Interface/editor/toolbutton-on.png");
        toolbarButton.leftTop(xVal, 7);
        toolbarButton.addTouchButtonListener(this);

        Image img = new Image(this, image, 32, 32, true);
        img.leftTop(xVal + 11, 7 + 11);
        img.rotate(angle * FastMath.DEG_TO_RAD);

        xVal += 54;

    }

    public String getSelectedItem() {
        return selectedItem;
    }

    public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
    }

    public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
        this.selectedItem = uid;
//        window.log("Selected: " + this.selectedItem);
        for (int i = 0; i < this.getWidgets().size(); i++) {
            Widget widget = this.getWidgets().get(i);
            if (widget instanceof ToolbarButton && ((ToolbarButton) widget).getId().equals(uid)) {
                selectedImage.setPosition(widget.getPosition().x, widget.getPosition().y);
            }
        }
    }

    public void doTouchMove(float touchX, float touchY, float tpf, String uid) {
    }

    public void doTouchCancel(float touchX, float touchY, float tpf, String uid) {
    }

    @Override
    public void doHoverOver(float touchX, float touchY, float tpf, String uid) {
        
    }

    @Override
    public void doHoverOff(float touchX, float touchY, float tpf, String uid) {
        
    }

    private void addTerrainTools() {

        URL url = null;

        try {
            Platform platform = JmeSystem.getPlatform();

            if (window.getApplication().isMobileApp()) {
                url = JmeSystem.getResource("/assets/Textures/terrain/");

            } else {
                url = JmeSystem.getResource("/Textures/terrain/");
            }
        } catch (UnsupportedOperationException e) {
            Logger.getLogger(PlatformGame.class.getName()).log(java.util.logging.Level.INFO, null, e);
            //Load the default
            url = JmeSystem.getResource("/assets/Textures/terrain/");

        }

        if (url != null) {
            Debug.log("Found terrain assets: " + url.getFile());
            File file = new File(url.getFile());

            if (file.isDirectory()) {
                File[] terrainTypes = file.listFiles();
                for (int i = 0; i < terrainTypes.length; i++) {
                    File terrainTypeFile = terrainTypes[i];
                    if (terrainTypeFile.isDirectory()) {
                        String terrainType = terrainTypeFile.getName();
                        Debug.log("Terrain type: " + terrainType);
                        File[] tileTypes = terrainTypeFile.listFiles();

                        for (int j = 0; j < tileTypes.length; j++) {
                            File tileTypeFile = tileTypes[j];
                            if (tileTypeFile.isFile() && tileTypeFile.getName().endsWith(".png")) {
                                String tileType = tileTypeFile.getName().replaceAll(".png", "");
                                Debug.log("Tile type: " + tileType);

                                addToolButton("terrain/" + terrainType + "/" + tileType, "Textures/terrain/" + terrainType + "/" + tileType + ".png", 0);
                            }

                        }

                    }
                }

            }

        }
    }

}
