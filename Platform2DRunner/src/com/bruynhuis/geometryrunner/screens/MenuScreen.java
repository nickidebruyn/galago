/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.geometryrunner.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.ButtonPanel;
import com.bruynhuis.geometryrunner.ui.Button;

/**
 *
 * @author NideBruyn
 */
public class MenuScreen extends AbstractScreen {
    
    private Label heading;
    private Button playButton;
    private Button optionsButton;
    private Button aboutButton;
    private Button editButton;
    private Button exitButton;
    private ButtonPanel buttonPanel;

    @Override
    protected void init() {
        
        Image backgroundImage = new Image(hudPanel, "Textures/Backgrounds/blue_land.png", 1300, 1300, true);
        
        heading = new Label(hudPanel, "Emoti Dash", 60, window.getWidth(), 100);
        heading.centerTop(0, 10);
        
        buttonPanel = new ButtonPanel(hudPanel, window.getWidth(), window.getHeight());
        hudPanel.add(buttonPanel);
        
        playButton = new Button(buttonPanel, "playbutton", "Play Game");
        playButton.centerAt(0, 80);
        playButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    showScreen("levels");
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
        
        editButton = new Button(buttonPanel, "editButton", "Editor");
        editButton.centerAt(0, -160);
        editButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    showScreen("edit");
                }
            }
                        
        });
        
        exitButton = new Button(buttonPanel, "exitButton", "Exit");
        exitButton.centerAt(0, -240);
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
