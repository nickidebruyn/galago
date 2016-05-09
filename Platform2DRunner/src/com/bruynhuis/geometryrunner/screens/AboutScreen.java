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
import com.bruynhuis.galago.ui.panel.VPanel;
import com.bruynhuis.geometryrunner.ui.Button;
import com.jme3.math.ColorRGBA;

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
        new Image(hudPanel, "Textures/Backgrounds/blue_land.png", 1300, 1300, true);
        
        heading = new Label(hudPanel, "About", 50, window.getWidth(), 100);
        heading.centerTop(0, 10);
        
        vPanel = new VPanel(hudPanel, null, window.getWidth(), 300);
        vPanel.center();
        hudPanel.add(vPanel);
        
        addLine(vPanel, "This game is written for jMe compo.");
        addLine(vPanel, "It will show you how to make a 2D game in jMonkeyEngine.");
        addLine(vPanel, "This game was written by Nicki de Bruyn.");
        
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
        Label label = new Label(vPanel, message, 28, window.getWidth(), 50);
        label.setTextColor(ColorRGBA.Orange);
                
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
