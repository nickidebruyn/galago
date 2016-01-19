/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.test.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.field.TextField;
import com.bruynhuis.galago.ui.panel.VPanel;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author nidebruyn
 */
public class InputGuiScreen extends AbstractScreen {
    
    private Label label;
    private TextField textField;
    private TouchButton button;
    private VPanel vPanel;

    @Override
    protected void init() {
        vPanel = new VPanel(hudPanel, null, 220, 300);
        hudPanel.add(vPanel);
        
        label = new Label(vPanel, "Text Input", 20);
        
        textField = new TextField(vPanel, "field1");
        textField.setBackgroundColor(ColorRGBA.Pink);
        textField.setTextColor(ColorRGBA.Red);
        
        button = new TouchButton(vPanel, "submitbutton", "Submit");
        button.setBackgroundColor(ColorRGBA.Green);
        
        vPanel.layout();
    }

    @Override
    protected void load() {
        baseApplication.getViewPort().setBackgroundColor(ColorRGBA.LightGray);
        
    }

    @Override
    protected void show() {
        
    }

    @Override
    protected void exit() {
        
    }

    @Override
    protected void pause() {
        
    }
    
}
