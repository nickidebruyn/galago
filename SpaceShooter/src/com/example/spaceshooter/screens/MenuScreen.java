/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.spaceshooter.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.ButtonPanel;
import com.example.spaceshooter.ui.Button;
import com.jme3.scene.Spatial;

/**
 *
 * @author NideBruyn
 */
public class MenuScreen extends AbstractScreen {
    
    private Label heading;
    private Button playButton;
    private Button optionsButton;
    private Button aboutButton;
    private Button exitButton;
    private ButtonPanel buttonPanel;

    @Override
    protected void init() {
        heading = new Label(hudPanel, "Space Shooter", 50, window.getWidth(), 100);
        heading.centerTop(0, 10);
        
        buttonPanel = new ButtonPanel(hudPanel, window.getWidth(), window.getHeight());
        hudPanel.add(buttonPanel);
        
        playButton = new Button(buttonPanel, "playbutton", "New Game");
        playButton.centerAt(0, 80);
        playButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    showScreen("play");
                }
            }
            
        });        
        
        optionsButton = new Button(buttonPanel, "optionsButton", "Options");
        optionsButton.centerAt(0, 0);
        optionsButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    showScreen("options");
                }
            }
            
        });
        
        aboutButton = new Button(buttonPanel, "aboutButton", "About");
        aboutButton.centerAt(0, -80);
        aboutButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    showScreen("about");
                }
            }
                        
        });
        
        exitButton = new Button(buttonPanel, "exitButton", "Exit");
        exitButton.centerAt(0, -160);
        exitButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    exitScreen();
                }
            }
                        
        });
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
        setPreviousScreen(null);
        buttonPanel.show();
    }

    @Override
    protected void exit() {
        rootNode.detachAllChildren();
    }

    @Override
    protected void pause() {
    }
    
}
