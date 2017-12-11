/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.platform2d.editor;

import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.Panel;

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
        super(parent, "Resources/editor/side-panel-top.png", parent.getWindow().getWidth(), 74);
        
        
        addPlayButton(ACTION_PLAY, buttonAdapter);
        addSpace();
        addSpace();
        addSpace();
        addToolButton(ACTION_SAVE, "Resources/editor/icon-save.png", buttonAdapter);
        addToolButton(ACTION_OPEN, "Resources/editor/icon-open.png", buttonAdapter);
        addToolButton(ACTION_NEW, "Resources/editor/icon-new.png", buttonAdapter);
        addToolButton(ACTION_TRASH, "Resources/editor/icon-trash.png", buttonAdapter);
        
        addSpace();
        
        addToolButton(ACTION_PAINT, "Resources/editor/icon-paint.png", buttonAdapter);
        addToolButton(ACTION_DRAW, "Resources/editor/icon-draw.png", buttonAdapter);
                
        parent.add(this);
    }    
    
    private void addPlayButton(String id, TouchButtonAdapter buttonAdapter) {
        ButtonPlay toolbarButton = new ButtonPlay(this, id);
        toolbarButton.rightTop(xVal, 7);
        toolbarButton.addTouchButtonListener(buttonAdapter); 
        xVal += 54;
    }
    
    private void addToolButton(String id, String image, TouchButtonAdapter buttonAdapter) {
        ToolbarButton toolbarButton = new ToolbarButton(this, id, "Resources/editor/toolbutton.png", "Resources/editor/toolbutton-on.png");
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
