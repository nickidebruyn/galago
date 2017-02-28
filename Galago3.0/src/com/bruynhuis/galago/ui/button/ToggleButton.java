/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.button;

import com.bruynhuis.galago.ui.panel.Panel;

/**
 * This is a on Off spinner button.
 * When touched or clicked the text swap between "On" and "Off".
 *
 * @author nidebruyn
 */
public class ToggleButton extends TouchButton {
    
    protected String offImage = "Resources/icon-switch-off.png";
    protected String onImage = "Resources/icon-switch-on.png";
    protected boolean selected = false;
    
    public ToggleButton(Panel panel, String id) {
        this(panel, id, "Resources/icon-switch-on.png", "Resources/icon-switch-off.png", 60, 35);
        
    }

    /**
     * 
     * @param panel
     * @param id
     * @param pictureFile
     * @param width
     * @param height 
     */
    public ToggleButton(Panel panel, String id, String onImage, String offImage, float width, float height) {
        super(panel, id, offImage, width, height, true);
        this.offImage = offImage;
        this.onImage = onImage;

    }
    
    @Override
    public void fireTouchUp(float x, float y, float tpf) {        

        setSelected(!selected);
        
        super.fireTouchUp(x, y, tpf);

    }
       
    /**
     * 
     * @param selected 
     */
    public void setSelected(boolean selected) {
        if ((this.selected && !selected) || (!this.selected && selected)) {            
            this.selected = selected;
            if (selected) {
                updatePicture(onImage);
            } else {
                updatePicture(offImage);
            }
        }
        
    }
    
    public boolean isSelected() {
        return selected;
    }
    
}
