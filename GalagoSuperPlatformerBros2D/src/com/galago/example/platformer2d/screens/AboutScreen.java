/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.ButtonPanel;
import com.bruynhuis.galago.ui.panel.VPanel;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.galago.example.platformer2d.MainApplication;
import com.galago.example.platformer2d.game.Game;
import com.galago.example.platformer2d.ui.HomeButton;

/**
 *
 * @author Nidebruyn
 */
public class AboutScreen extends AbstractScreen {
    
    private Label heading;
    private VPanel vPanel;
    private HomeButton backButton;
    private ButtonPanel bp;
    private Game game;

    @Override
    protected void init() {
        
        heading = new Label(hudPanel, "About", 50, window.getWidth(), 100);
        heading.centerTop(0, 10);
        
        vPanel = new VPanel(hudPanel, null, window.getWidth(), 300);
        vPanel.center();
        hudPanel.add(vPanel);
        
        addLine(vPanel, "Bounce is a game where the player");
        addLine(vPanel, "controls a bouncing ball by tilting the");
        addLine(vPanel, "device left or right.");
        addLine(vPanel, "The goal of the game is to get ");
        addLine(vPanel, "the ball to the finish point.");

        
        vPanel.layout();
        
        bp = new ButtonPanel(hudPanel, window.getWidth(), window.getHeight());
        hudPanel.add(bp);
        
        backButton = new HomeButton(bp);
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
    
    private void addLine(VPanel vPanel, String message) {
        Label label = new Label(vPanel, message, 28, window.getWidth(), 50);
        label.setTextColor(ColorRGBA.LightGray);
                
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
