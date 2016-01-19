/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.flat.screen;

import com.bruynhuis.galago.flat.ui.PauseDialog;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.GridPanel;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author nidebruyn
 */
public class MenuScreen extends AbstractScreen {

    private TouchButton statsButton;
    private PauseDialog pauseDialog;
    
    protected void addSceneButton(GridPanel gridPanel, final String screenName, String label) {
        final TouchButton sceneButton = new TouchButton(gridPanel, screenName, label);
        sceneButton.setFontSize(18f);
        sceneButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    showScreen(screenName);
                }
            }
        });
        
    }

    @Override
    protected void init() {
        
        Label head = new Label(hudPanel, "Menu", 46, 600, 100);
        head.setTextColor(ColorRGBA.Yellow);
        head.centerTop(0, 0);
        
        GridPanel gridPanel = new GridPanel(hudPanel, 800, 450);
        hudPanel.add(gridPanel);        
        
        addSceneButton(gridPanel, "physics", "Physics Test");
        addSceneButton(gridPanel, "vehicle", "Vehicle Test");        
        addSceneButton(gridPanel, "line", "Line Test");        
        addSceneButton(gridPanel, "camrotation", "Cam Rotation Test");        
        
        gridPanel.layout(8, 3);                
        gridPanel.center();        
        
        pauseDialog = new PauseDialog(window);
        pauseDialog.center();
    }

    @Override
    protected void load() {
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.DarkGray);
    }

    @Override
    protected void show() {
        setPreviousScreen(null);
//        pauseDialog.show();
    }

    @Override
    protected void exit() {

    }

    @Override
    protected void pause() {
        pauseDialog.show();
    }
}
