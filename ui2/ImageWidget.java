/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui;

import com.bruynhuis.galago.ui.window.Window;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.ui.Picture;

/**
 * An image widget is a widget this will have some sort of image.
 * 
 * @author nidebruyn
 */
public abstract class ImageWidget extends Widget {

    protected Picture picture;
    protected String pictureFile;
    protected String currentPictureFile;
    protected ColorRGBA pictureColor = new ColorRGBA(1, 1, 1, 1);
    
    /**
     * 
     * @param window
     * @param parent
     * @param pictureFile
     * @param width
     * @param height
     * @param lockscaling 
     */
    public ImageWidget(Window window, Widget parent, String pictureFile, float width, float height, boolean lockscaling) {
        super(window, parent, width, height, lockscaling);

        if (pictureFile != null) {            
            //load picture
            this.pictureFile = pictureFile;
            this.currentPictureFile = pictureFile;
            picture = new Picture("IMAGE-WIDGET");
//            picture.setTexture(window.getAssetManager(), texture2D, true);
            picture.setMaterial(window.getApplication().getTextureManager().getGUIMaterial(pictureFile));
//            picture.setMaterial(window.makeGuiMaterial(texture2D));

            //adjust picture
            picture.setWidth(getWidth());
            picture.setHeight(getHeight());
            picture.move(-getWidth() * 0.5f, -getHeight() *0.5f, 0);         
            widgetNode.attachChild(picture);
            setTransparency(1);
            
//            window.addPictureForOptimization(picture);
        }

    }

    @Override
    public void add(Node parent) {
        super.add(parent);
        if (picture != null) {
            picture.setWidth(width);
            picture.setHeight(height);
        }

    }

    /**
     * 
     * @param pictureFile 
     */
    public void updatePicture(String pictureFile) {
        if (picture != null) {
            if (!pictureFile.equals(currentPictureFile)) {
                float alpha = pictureColor.a;
                picture.setMaterial(window.getApplication().getTextureManager().getGUIMaterial(pictureFile));
                currentPictureFile = pictureFile;
                setTransparency(alpha);
            }            
        }        
    }

    /**
     * Update back to old file
     */
    public void updateToOriginalPicture() {
        if (picture != null) {
            float alpha = pictureColor.a;
            picture.setMaterial(window.getApplication().getTextureManager().getGUIMaterial(pictureFile));
            setTransparency(alpha);
        }
        
    }

    public Picture getPicture() {
        return picture;
    }
//
//    public Texture2D getTexture2D() {
//        return texture2D;
//    }
    
    @Override
    public void setTransparency(float alpha) {
        if (picture != null && picture.getMaterial() != null) {            
            pictureColor.set(1f, 1f, 1f, alpha);
            picture.getMaterial().setColor("Color", pictureColor);
        }
    }

    @Override
    public float getTransparency() {
        if (picture != null && picture.getMaterial() != null) {            
            return pictureColor.getAlpha();

        }
        return 1f;
    }
    
    /**
     * This method will decouple the material and clone it.
     * @param colorRGBA 
     */
    public void setBackgroundColor(ColorRGBA colorRGBA) {
        if (picture != null && picture.getMaterial() != null) {
            pictureColor.set(colorRGBA.r, colorRGBA.g, colorRGBA.b, pictureColor.a);
            Material material = picture.getMaterial().clone();
            material.setColor("Color", pictureColor);
            picture.setMaterial(material);
        }
    }

    public ColorRGBA getPictureColor() {
        return pictureColor;
    }
    
    
}
