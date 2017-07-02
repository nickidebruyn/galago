/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui;

import com.bruynhuis.galago.ui.panel.Panel;

/**
 * An image widget will show a normal PNG/jpg image on the screen.
 *
 * @author NideBruyn
 */
public class Image extends ImageWidget {
    
    /**
     * 
     * @param panel Parent panel
     * @param pictureFile 
     */
    public Image(Panel panel, String pictureFile) {
        this(panel, pictureFile, panel.getWindow().getWidth(), panel.getWindow().getHeight(), false);
    }

    /**
     * 
     * @param panel Parent panel
     * @param pictureFile
     * @param width
     * @param height 
     */
    public Image(Panel panel, String pictureFile, float width, float height) {
        this(panel, pictureFile, width, height, false);
    }
    
    /**
     * 
     * @param panel Parent panel
     * @param pictureFile
     * @param width
     * @param height
     * @param lockscaling 
     */
    public Image(Panel panel, String pictureFile, float width, float height, boolean lockscaling) {
        super(panel.getWindow(), panel, pictureFile, width, height, lockscaling);
        panel.add(this);
    }
    
    @Override
    protected boolean isBatched() {
        return false;
    }   
}
