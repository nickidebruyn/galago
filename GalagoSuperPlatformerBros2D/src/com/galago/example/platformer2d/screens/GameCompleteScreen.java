/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.ButtonPanel;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.galago.example.platformer2d.MainApplication;
import com.galago.example.platformer2d.game.Game;
import com.galago.example.platformer2d.ui.HomeButton;
import com.galago.example.platformer2d.ui.NextButton;

/**
 *
 * @author Nidebruyn
 */
public class GameCompleteScreen extends AbstractScreen {
    
    private Label heading;
    private Label messageLabel;
    private ButtonPanel buttonPanel;
    private HomeButton backButton;
    private NextButton nextButton;
    private Game game;

    @Override
    protected void init() {
        
        heading = new Label(hudPanel, "Level Complete", 50, window.getWidth(), 100);
        heading.centerTop(0, 10);
        
        messageLabel = new Label(hudPanel, "Well done, you completed this level.", 24, window.getWidth(), 200);
        messageLabel.setTextColor(ColorRGBA.LightGray);
        messageLabel.center();
        
        buttonPanel = new ButtonPanel(hudPanel, window.getWidth(), window.getHeight());
        hudPanel.add(buttonPanel);
        
        backButton = new HomeButton(buttonPanel);
        backButton.leftBottom(10, 10f);
        backButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    showScreen("play");
                }
            }
            
        });
        
        nextButton = new NextButton(buttonPanel);
        nextButton.rightBottom(10f, 10f);
        nextButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    baseApplication.getSoundManager().playSound("button");
                    showScreen("levels");
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
