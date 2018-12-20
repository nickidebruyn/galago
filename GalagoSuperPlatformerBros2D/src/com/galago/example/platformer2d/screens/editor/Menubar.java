/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.platformer2d.screens.editor;

import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.Panel;
import com.galago.example.platformer2d.ui.ButtonPlay;

/**
 *
 * @author NideBruyn
 */
public class Menubar extends Panel {
    
    public static final String ACTION_NEW = "action-new";
    public static final String ACTION_OPEN = "action-open";
    public static final String ACTION_SAVE = "action-save";
    public static final String ACTION_PAINT = "action-paint";
    public static final String ACTION_PLAY = "action-play";
    public static final String ACTION_DRAW = "action-draw";
    public static final String ACTION_TRASH = "action-trash";
    
    private float xVal = 10;

    public Menubar(Panel parent, TouchButtonAdapter buttonAdapter) {
        super(parent, "Interface/editor/side-panel-right.png", parent.getWindow().getWidth(), 74);
        
        
        addPlayButton(ACTION_PLAY, buttonAdapter);
        addSpace();
        addSpace();
        addToolButton(ACTION_SAVE, "Interface/editor/icon-save.png", buttonAdapter);
        addToolButton(ACTION_OPEN, "Interface/editor/icon-open.png", buttonAdapter);
        addToolButton(ACTION_NEW, "Interface/editor/icon-new.png", buttonAdapter);
        addToolButton(ACTION_TRASH, "Interface/editor/icon-trash.png", buttonAdapter);
        
        addSpace();
        
        addToolButton(ACTION_PAINT, "Interface/editor/icon-paint.png", buttonAdapter);
        addToolButton(ACTION_DRAW, "Interface/editor/icon-draw.png", buttonAdapter);
                
        parent.add(this);
    }    
    
    private void addPlayButton(String id, TouchButtonAdapter buttonAdapter) {
        ButtonPlay toolbarButton = new ButtonPlay(this, id, 0.45f);
        toolbarButton.rightTop(xVal, 7);
        toolbarButton.addTouchButtonListener(buttonAdapter); 
        xVal += 54;
    }
    
    private void addToolButton(String id, String image, TouchButtonAdapter buttonAdapter) {
        ToolbarButton toolbarButton = new ToolbarButton(this, id, "Interface/editor/toolbutton.png", "Interface/editor/toolbutton-on.png");
        toolbarButton.rightTop(xVal, 7);
        toolbarButton.addTouchButtonListener(buttonAdapter);
        
        Image img = new Image(this, image, 32, 32, true);
        img.rightTop(xVal + 11, 18);
        
        xVal += 54;
    }
    
    private void addSpace() {        
        xVal += 48;
    }
    
}
