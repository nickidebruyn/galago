/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui.field;

import com.bruynhuis.galago.ui.ImageWidget;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.ui.Picture;

/**
 *
 * The progressBar class is exactly that.
 * 2 Images layered over each other to represent a Horizontal ProgressBar.
 * 
 * @author nidebruyn
 */
public class ProgressBar extends ImageWidget {

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
    public ProgressBar(Panel panel, String pictureFile, String progressPictureFile) {
        this(panel, pictureFile, progressPictureFile, 250, 40);
    }

    /**
     * 
     * @param panel
     * @param pictureFile
     * @param progressPictureFile
     * @param width
     * @param height 
     */
    public ProgressBar(Panel panel, String pictureFile, String progressPictureFile, float width, float height) {
        super(panel.getWindow(), panel, pictureFile, width, height, false);
        this.progressPictureFile = progressPictureFile;

        if (progressPictureFile != null) {
//            Texture2D texture2D = (Texture2D) window.getAssetManager().loadTexture(progressPictureFile);
            progressPicture = new Picture("PROGRESS-IMAGE-WIDGET");
//            progressPicture.setImage(window.getAssetManager(), progressPictureFile, true);
//            progressPicture.setMaterial(window.makeGuiMaterial(texture2D));
            progressPicture.setMaterial(window.getApplication().getTextureManager().getGUIMaterial(progressPictureFile));
            
            progressPicture.setWidth(getWidth());
            progressPicture.setHeight(getHeight());
            progressPicture.move(-getWidth() * 0.5f, -getHeight() * 0.5f, 0f);
            widgetNode.attachChild(progressPicture);
            
//            window.addPictureForOptimization(picture);

        }

        setProgress(progress);
    }
    
    /**
     * 
     * @param progress 
     */
    public void setProgress(float progress) {
        this.progress = progress;
        progressPicture.setWidth(getWidth()*progress);
    }
    
    public float getProgress() {
        return progress;
    }   
    
    @Override
    protected boolean isBatched() {
        return false;
    }
}
