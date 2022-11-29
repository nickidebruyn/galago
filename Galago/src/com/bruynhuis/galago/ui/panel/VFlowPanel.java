/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.panel;

import com.bruynhuis.galago.ui.Widget;
import com.bruynhuis.galago.ui.window.Window;

/**
 * This is a vertical Panel for widgets.
 * It can be used for example an tools panel in the game.
 * The layout() method must be called after all widgets was added.
 * 
 * @author nidebruyn
 */
public class VFlowPanel extends Panel {
    
    protected float padding = 5;

    /**
     * 
     * @param window
     * @param pictureFile 
     */
    public VFlowPanel(Window window, String pictureFile) {
        super(window, pictureFile);
    }

    /**
     * 
     * @param window
     * @param pictureFile
     * @param width
     * @param height 
     */
    public VFlowPanel(Window window, String pictureFile, float width, float height) {
        super(window, pictureFile, width, height);
    }
    
    /**
     * 
     * @param parent
     * @param pictureFile
     * @param width
     * @param height 
     */
    public VFlowPanel(Widget parent, String pictureFile, float width, float height) {
        super(parent.getWindow(), parent, pictureFile, width, height);
    }

    /**
     * 
     * @param window
     * @param parent
     * @param width
     * @param height 
     */
    public VFlowPanel(Window window, Widget parent, float width, float height) {
        super(window, parent, null, width, height);
    }
    
    /**
     * 
     * @param window
     * @param parent
     * @param pictureFile
     * @param width
     * @param height 
     */
    public VFlowPanel(Window window, Widget parent, String pictureFile, float width, float height) {
        super(window, parent, pictureFile, width, height); 
    }
    
    /**
     * This method will handle the way we layout the 
     */
    public void layout() {

        float position = 0;
        
        for (int i = 0; i < widgets.size(); i++) {            
            Widget widget = widgets.get(i);
            if (widget.isVisible()) {
                widget.centerTop(0, position);
                position += (padding + (widget.getHeight()/window.getScaleFactorHeight()));                
            }
        }
    }
    
    @Override
    protected boolean isBatched() {
        return true;
    }

    public float getPadding() {
        return padding;
    }

    public void setPadding(float padding) {
        this.padding = padding;
    }
    
}
