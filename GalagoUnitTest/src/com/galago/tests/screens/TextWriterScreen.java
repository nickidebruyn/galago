/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.effect.TextWriteEffect;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author Nidebruyn
 */
public class TextWriterScreen extends AbstractScreen {
    
    private Label label;

    @Override
    protected void init() {
        
        label = new Label(hudPanel, "Hallo Johan hoe gaan dit?", 20, 600, 60);
        label.setTextColor(ColorRGBA.Yellow);
        label.setAlignment(TextAlign.LEFT);
        label.setAnimated(true);
        label.addEffect(new TextWriteEffect(label, 10));


    }

    @Override
    protected void load() {
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.Black);
        

    }

    @Override
    protected void show() {
        label.setVisible(false);
        label.show();

    }

    @Override
    protected void exit() {

    }

    @Override
    protected void pause() {

    }
    
}
