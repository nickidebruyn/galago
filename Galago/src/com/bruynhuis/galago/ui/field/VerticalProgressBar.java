/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.field;

import com.bruynhuis.galago.ui.ImageWidget;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.ui.Picture;

/**
 * A Vertical progress bar.
 * 2 Images is required.
 * 
 * @author nidebruyn
 */
public class VerticalProgressBar extends ImageWidget {

    protected Panel panel;    
    protected float progress = 1;
    protected Picture progressPicture;
    protected String progressPictureFile;
    
    /**
     * 
     * @param panel
     * @param pictureFile
     * @param progressPictureFile 
     */
    public VerticalProgressBar(Panel panel, String pictureFile, String progressPictureFile) {
        this(panel, pictureFile, progressPictureFile, 40, 250);
    }

    /**
     * 
     * @param panel
     * @param pictureFile
     * @param progressPictureFile
     * @param width
     * @param height 
     */
    public VerticalProgressBar(Panel panel, String pictureFile, String progressPictureFile, float width, float height) {
        super(panel.getWindow(), panel, pictureFile, width, height, false);
        this.progressPictureFile = progressPictureFile;

        if (progressPictureFile != null) {
//            Texture2D texture2D = (Texture2D) window.getAssetManager().loadTexture(progressPictureFile);
            
            progressPicture = new Picture("PROGRESS-IMAGE-WIDGET");
            progressPicture.setMaterial(window.getApplication().getTextureManager().getGUIMaterial(progressPictureFile));
//            progressPicture.setImage(window.getAssetManager(), progressPictureFile, true);
//            progressPicture.setMaterial(window.makeGuiMaterial(texture2D));
            progressPicture.setWidth(getWidth());
            progressPicture.setHeight(getHeight());
            progressPicture.move(-getWidth() * 0.5f, -getHeight() * 0.5f, -0.1f);
            widgetNode.attachChild(progressPicture);

        }

        setProgress(progress);
        
        panel.add(this);
    }
    
    @Override
    protected boolean isBatched() {
        return false;
    }
    
    /**
     * 
     * @param progress 
     */
    public void setProgress(float progress) {
        this.progress = progress;
        progressPicture.setHeight(getHeight()*progress);
    }
    
    public float getProgress() {
        return progress;
    }   
}
