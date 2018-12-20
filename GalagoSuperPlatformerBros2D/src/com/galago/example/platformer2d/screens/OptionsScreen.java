/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.ButtonPanel;
import com.jme3.math.Vector3f;
import com.galago.example.platformer2d.MainApplication;
import com.galago.example.platformer2d.game.Game;
import com.galago.example.platformer2d.ui.HomeButton;

/**
 *
 * @author Nidebruyn
 */
public class OptionsScreen extends AbstractScreen {
    
    private Label heading;

    private ButtonPanel buttonPanel;
    private HomeButton backButton;
    private Game game;

    @Override
    protected void init() {
        
        heading = new Label(hudPanel, "Options", 50, window.getWidth(), 100);
        heading.centerTop(0, 10);
        
        buttonPanel = new ButtonPanel(hudPanel, window.getWidth(), window.getHeight());
        hudPanel.add(buttonPanel);
        
        backButton = new HomeButton(buttonPanel);
        backButton.leftBottom(10, 10f);
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
        game = new Game((MainApplication)baseApplication, rootNode);
        rootNode.attachChild(game.getSky("sky-autumn1"));
        camera.setLocation(new Vector3f(0, 0, -10));
        
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
