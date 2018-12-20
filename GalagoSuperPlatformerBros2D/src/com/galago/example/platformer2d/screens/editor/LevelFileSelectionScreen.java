/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.screens.editor;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.VPagerPanel;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.system.JmeSystem;
import java.io.File;
import java.util.ArrayList;
import com.galago.example.platformer2d.MainApplication;
import com.galago.example.platformer2d.game.Game;
import com.galago.example.platformer2d.ui.ButtonEdit;
import com.galago.example.platformer2d.ui.ButtonFileSelection;

/**
 *
 * @author NideBruyn
 */
public class LevelFileSelectionScreen extends AbstractScreen {

    private Label title;
    private Image header;
    private VPagerPanel pagerPanel;
    private TouchButtonAdapter buttonAdapter;
    private TouchButtonAdapter deleteButtonAdapter;
    private MainApplication mainApplication;
    private Game game;
    private String selectedLevel;
    private ButtonEdit buttonEdit;
    private ConfirmDialog confirmDeleteDialog;

    @Override
    protected void init() {
        mainApplication = (MainApplication) baseApplication;

        header = new Image(hudPanel, "Interface/editor/side-panel-right.png", window.getWidth(), 74);
        header.centerTop(0, 0);

        title = new Label(hudPanel, "Select a Level", 30, 300, 70);
        title.setTextColor(ColorRGBA.White);
        title.centerTop(0, 0);

        buttonEdit = new ButtonEdit(hudPanel, "edit-level-button", 0.8f);
        buttonEdit.rightBottom(10, 10);
        buttonEdit.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && selectedLevel != null) {
                    mainApplication.getEditScreen().setFileName(selectedLevel);
                    showScreen("edit");
                }
            }
        });

        confirmDeleteDialog = new ConfirmDialog(window, "Are you sure you want to delete the level?");
        confirmDeleteDialog.addOkButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive() && selectedLevel != null) {
                    removeFile(selectedLevel);
                    showScreen("fileselect");
                }
            }
            
        });

        buttonAdapter = new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    selectedLevel = uid.trim() + EditScreen.FILE_EXT;


                    buttonEdit.show();

                    if (game != null) {
                        game.close();

                    }

                    game = new Game(mainApplication, rootNode);
                    game.edit(selectedLevel);
                    game.load();

                    rootNode.setLocalTranslation(5.4f, 0, 0);
                }
            }
        };

        deleteButtonAdapter = new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    selectedLevel = uid.trim() + EditScreen.FILE_EXT;
                    confirmDeleteDialog.setText("Are you sure you want to delete the level, " + uid.trim() + "?");
                    confirmDeleteDialog.show();
                }
            }
        };


    }

    private void removeFile(String filename) {
        File folder = JmeSystem.getStorageFolder();

        if (folder != null && folder.exists() && folder.isDirectory()) {
            //FIRST We get all the valid files
            File[] levelFiles = folder.listFiles();
            if (levelFiles != null && levelFiles.length > 0) {

                for (int i = 0; i < levelFiles.length; i++) {
                    File f = levelFiles[i];


                    if (f.exists() && f.isFile() && f.getName().equals(filename)) {
                        log("Removing File: " + f.getName());
                        f.delete();

                    }

                }
            }
        }
    }

    @Override
    protected void load() {
        
        selectedLevel = null;

        ArrayList<String> fileNames = new ArrayList<String>();

        File folder = JmeSystem.getStorageFolder();

        if (folder != null && folder.exists() && folder.isDirectory()) {
            //FIRST We get all the valid files
            File[] levelFiles = folder.listFiles();
            if (levelFiles != null && levelFiles.length > 0) {

                for (int i = 0; i < levelFiles.length; i++) {
                    File f = levelFiles[i];


                    if (f.exists() && f.isFile() && f.getName().endsWith(EditScreen.FILE_EXT)) {
                        log("File: " + f.getName());
                        fileNames.add(f.getName().replaceAll(EditScreen.FILE_EXT, ""));

                    }


                }
            }

            //Next we create the container panel
            pagerPanel = new VPagerPanel(hudPanel, window.getWidth(), 80 * fileNames.size());
            pagerPanel.leftCenter(50, 0);
            hudPanel.add(pagerPanel);

            //Now we add the file names per button
            for (int i = 0; i < fileNames.size(); i++) {
                String name = fileNames.get(i);
                ButtonFileSelection levelButton = new ButtonFileSelection(pagerPanel, name, "   " + name);
                levelButton.addOkButtonListener(buttonAdapter);
                levelButton.addDeleteButtonListener(deleteButtonAdapter);

            }
            pagerPanel.layout(-500 * window.getScaleFactorWidth(), 300 * window.getScaleFactorHeight());
        }

        mainApplication.setCameraDistanceFrustrum(14f);
        camera.setLocation(new Vector3f(0, 0, 10));
    }

    @Override
    protected void show() {
        setPreviousScreen("edit");
        buttonEdit.hide();
    }

    @Override
    protected void exit() {
        pagerPanel.clear();
        pagerPanel.remove();
    }

    @Override
    protected void pause() {
    }
}
