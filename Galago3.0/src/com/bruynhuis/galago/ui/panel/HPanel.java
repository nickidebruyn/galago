/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.panel;

import com.bruynhuis.galago.ui.Widget;
import com.bruynhuis.galago.ui.window.Window;

/**
 * This is a Horizontal Panel for widgets.
 * It can be used for example an inventory in the game.
 * The layout() method must be called after all widgets was added.
 *
 * @author nidebruyn
 */
public class HPanel extends Panel {

    /**
     * 
     * @param window
     * @param pictureFile 
     */
    public HPanel(Window window, String pictureFile) {
        super(window, pictureFile);
    }

    /**
     * 
     * @param window
     * @param pictureFile
     * @param width
     * @param height 
     */
    public HPanel(Window window, String pictureFile, float width, float height) {
        super(window, pictureFile, width, height);
    }
    
    public HPanel(Widget parent, String pictureFile, float width, float height) {
        super(parent.getWindow(), parent, pictureFile, width, height);
    }
    
    public HPanel(Widget parent, float width, float height) {
        super(parent.getWindow(), parent, null, width, height);
    }
    
    @Override
    protected boolean isBatched() {
        return true;
    }
    
    /**
     * This method will handle the way we layout the 
     */
    public void layout() {
        float sectionSize = getWidth()/widgets.size();
        float position = -getWidth()*0.5f + sectionSize*0.5f;
        
        for (int i = 0; i < widgets.size(); i++) {            
            Widget widget = widgets.get(i);
            widget.setPosition(position, 0);
            position += sectionSize;
        }
    }
    
}
