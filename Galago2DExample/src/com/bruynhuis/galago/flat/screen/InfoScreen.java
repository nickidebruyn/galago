/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.flat.screen;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.util.Timer;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author nidebruyn
 */
public class InfoScreen extends AbstractScreen {
    
    private Timer timer = new Timer(200);

    @Override
    protected void init() {
        new Image(hudPanel, "Interface/splash.png");
        
        Label l = new Label(hudPanel, "", 18, 600, 60);        
        l.setTextColor(ColorRGBA.Black);
        l.setText("Welcome to Galago, a SUPER SIMPLE game creation framework created by Nicolaas de Bruyn.");
        l.centerBottom(0, 100);

    }

    @Override
    protected void load() {
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.Gray);
        
    }

    @Override
    protected void show() {
//        hudPanel.updatePicture("Interface/splash.png");
        timer.start();
    }

    @Override
    protected void exit() {
        timer.stop();
    }

    @Override
    protected void pause() {
        
    }

    @Override
    public void update(float tpf) {
        if (isActive()) {
            timer.update(tpf);
            if (timer.finished()) {
                showScreen("menu");
            }
        }
    }
    
}
