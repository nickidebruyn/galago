/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.button;

import com.bruynhuis.galago.ui.panel.Panel;

/**
 * A spinner widget is a button the loop over an array of values that is set to it.
 * This can be used for on/off switches.
 *
 * @author nidebruyn
 */
public class Spinner extends TouchButton {

    private String[] options = null;
    private int index = 0;

    /**
     * 
     * @param panel
     * @param id
     * @param options 
     */
    public Spinner(Panel panel, String id, String[] options) {
        super(panel, id, " ");
        this.options = options;
        this.index = 0;

        setFontSize(18);
        refreshOptions();        
    }
    
    /**
     * 
     * @param panel
     * @param id
     * @param pictureFile
     * @param width
     * @param height
     * @param options 
     */
    public Spinner(Panel panel, String id, String pictureFile, float width, float height, String[] options) {
        super(panel, id, pictureFile, width, height);
        this.options = options;
        this.index = 0;

        setFontSize(18);
        refreshOptions();

    }

    protected void refreshOptions() {
        if (options != null && options.length > 0 && index < options.length) {
            String text = options[index];
            setText(text);
        } else {
            setText(" ");
        }
    }

    @Override
    public void fireTouchUp(float x, float y, float tpf) {        

        if (options != null && options.length > 0) {
            index++;
            if (index >= options.length) {
                index = 0;
            }

        }
        refreshOptions();
        
        super.fireTouchUp(x, y, tpf);

    }

    public String[] getOptions() {
        return options;
    }

    public int getIndex() {
        return index;
    }
    
    /**
     * 
     * @param index 
     */
    public void setSelection(int index) {
        this.index = index;
        refreshOptions();
    }

    /**
     * 
     * @param options 
     */
    public void setOptions(String[] options) {
        this.options = options;
        setSelection(0);
        refreshOptions();
    }    
    
}