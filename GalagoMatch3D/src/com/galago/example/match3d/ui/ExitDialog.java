/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.match3d.ui;

import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.PopupDialog;
import com.bruynhuis.galago.ui.window.Window;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author NideBruyn
 */
public class ExitDialog extends PopupDialog {
    
    private Button exitButton;
    private Button cancelButton;
    private Button restartButton;
    
    public ExitDialog(Window window) {
        super(window, "Interface/popup.png", 480, 800);
        
        setTitle("What would you like to do?");
        setTitleColor(ColorRGBA.White);
        title.setFontSize(34);
        title.centerAt(0, 100);                
        
        cancelButton = new Button(this, "cancel", "Resume");
        cancelButton.centerAt(0, 20);
        
        restartButton = new Button(this, "restartButton", "Restart");
        restartButton.centerAt(0, -40);
        
        exitButton = new Button(this, "exit", "Exit");
        exitButton.centerAt(0, -100);        
    }
    
    public void addExitButtonListener(TouchButtonListener listener) {
        exitButton.addTouchButtonListener(listener);
    }
    
    public void addRestartButtonListener(TouchButtonListener listener) {
        restartButton.addTouchButtonListener(listener);
    }
    
    public void addCancelButtonListener(TouchButtonListener listener) {
        cancelButton.addTouchButtonListener(listener);
    }
}
