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
import com.jme3.math.ColorRGBA;

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
        
        new Image(hudPanel, "Textures/Backgrounds/blue_land.png", 1300, 1300, true);
        
        heading = new Label(hudPanel, "Level Complete", 50, window.getWidth(), 100);
        heading.centerTop(0, 10);
        
        messageLabel = new Label(hudPanel, "Well done, you completed this leve.", 20, window.getWidth(), 200);
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
