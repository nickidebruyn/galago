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
public class OptionsScreen extends AbstractScreen {
    
    private Label heading;

    private ButtonPanel buttonPanel;
    private Button backButton;

    @Override
    protected void init() {
        
        new Image(hudPanel, "Textures/Backgrounds/blue_land.png", 1300, 1300, true);
        
        heading = new Label(hudPanel, "Options", 50, window.getWidth(), 100);
        heading.centerTop(0, 10);
        
        buttonPanel = new ButtonPanel(hudPanel, window.getWidth(), window.getHeight());
        hudPanel.add(buttonPanel);
        
        backButton = new Button(buttonPanel, "backButton", "Back");
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

    @Override
    protected void load() {

        
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
