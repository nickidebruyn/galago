/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.spaceshooter.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.ButtonPanel;
import com.bruynhuis.galago.ui.panel.VPanel;
import com.example.spaceshooter.ui.Button;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;

/**
 *
 * @author NideBruyn
 */
public class AboutScreen extends AbstractScreen {
    
    private Label heading;
    private VPanel vPanel;
    private Button backButton;
    private ButtonPanel bp;

    @Override
    protected void init() {
        heading = new Label(hudPanel, "About", 50, window.getWidth(), 100);
        heading.centerTop(0, 10);
        
        vPanel = new VPanel(hudPanel, null, window.getWidth(), 300);
        vPanel.center();
        hudPanel.add(vPanel);
        
        addLine(vPanel, "This game is all about learning.");
        addLine(vPanel, "I wrote it for educational purposes.");
        addLine(vPanel, "The game is a 2d space shooter game.");
        addLine(vPanel, "It will show you how to make a 2D game in jMonkeyEngine.");
        addLine(vPanel, "It also uses the game development framework (Galago).");
        addLine(vPanel, "This tutorial is written by Nicki de Bruyn.");
        
        vPanel.layout();
        
        bp = new ButtonPanel(hudPanel, window.getWidth(), window.getHeight());
        hudPanel.add(bp);
        
        backButton = new Button(bp, "backButton", "Back");
        backButton.centerBottom(0, 10f);
        backButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    showPreviousScreen();
                }
            }
            
        });
    }
    
    private void addLine(VPanel vPanel, String message) {
        Label label = new Label(vPanel, message, 16, window.getWidth(), 50);
        label.setTextColor(ColorRGBA.LightGray);
                
    }

    @Override
    protected void load() {
        
        //Load the level
        Spatial spatial = baseApplication.getModelManager().getModel("Models/starfield.j3o");
        spatial.setLocalTranslation(0, 16, -1f);
        rootNode.attachChild(spatial);
        
    }

    @Override
    protected void show() {
    }

    @Override
    protected void exit() {
        rootNode.detachAllChildren();
    }

    @Override
    protected void pause() {
    }
    
}
