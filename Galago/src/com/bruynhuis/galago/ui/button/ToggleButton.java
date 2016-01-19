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
public class ToggleButton extends Spinner {
    
    protected static final String[] values = {"On", "Off"};

    /**
     * 
     * @param panel
     * @param id
     * @param pictureFile
     * @param width
     * @param height 
     */
    public ToggleButton(Panel panel, String id, String pictureFile, float width, float height) {
        super(panel, id, pictureFile, width, height, values);

    }
       
    /**
     * 
     * @param selected 
     */
    public void setSelected(boolean selected) {
        if (selected) {
            setSelection(0);
        } else {
            setSelection(1);
        }        
    }
    
    public boolean isSelected() {
        return getIndex() == 0;
    }
    
}
