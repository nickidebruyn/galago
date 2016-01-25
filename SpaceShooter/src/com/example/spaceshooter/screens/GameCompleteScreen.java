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
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;

/**
 *
 * @author NideBruyn
 */
public class GameCompleteScreen extends AbstractScreen {
    
    private Label heading;
    private Label messageLabel;
    private ButtonPanel buttonPanel;
    private Button backButton;
    private Button nextButton;

    @Override
    protected void init() {
        heading = new Label(hudPanel, "Game Complete", 50, window.getWidth(), 100);
        heading.centerTop(0, 10);
        
        messageLabel = new Label(hudPanel, "You win, You win!!! Well done, you completed this game.", 20, window.getWidth(), 200);
        messageLabel.setTextColor(ColorRGBA.LightGray);
        messageLabel.center();
        
        buttonPanel = new ButtonPanel(hudPanel, window.getWidth(), window.getHeight());
        hudPanel.add(buttonPanel);
        
        backButton = new Button(buttonPanel, "backButton", "Retry");
        backButton.leftBottom(100, 10f);
        backButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    showScreen("play");
                }
            }
            
        });
        
        nextButton = new Button(buttonPanel, "nextButton", "Next");
        nextButton.rightBottom(100, 10f);
        nextButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    showScreen("menu");
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
        setPreviousScreen("menu");
    }

    @Override
    protected void exit() {
        rootNode.detachAllChildren();
    }

    @Override
    protected void pause() {
    }
    
}
