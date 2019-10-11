/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.ui;

import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

/**
 * An image widget is a widget this will have some sort of image.
 *
 * @author nidebruyn
 */
public class SpriteWidget extends Widget {

    protected Sprite sprite;
    protected String imageFile;
    protected ColorRGBA imageColor = new ColorRGBA(1, 1, 1, 1);

    /**
     *
     * @param parent
     * @param pictureFile
     * @param width
     * @param height
     * @param lockscaling
     */
    public SpriteWidget(Panel parent, String pictureFile, float width, float height, int columns, int rows, int colIndex, int rowIndex, boolean lockscaling) {
        super(parent.getWindow(), parent, width, height, lockscaling);

        if (pictureFile != null) {
            //load picture
            this.imageFile = pictureFile;
            sprite = new Sprite("SPRITE-WIDGET", getWidth(), getHeight(), columns, rows, colIndex, rowIndex);
            sprite.setImage(imageFile);

            //adjust picture
//            sprite.move(-getWidth() * 0.5f, -getHeight() *0.5f, 0);
            widgetNode.attachChild(sprite);
            setTransparency(1);

        }

        parent.add(this);

    }

    public void updateImage(int colIndex, int rowIndex) {
        if (sprite != null) {
            sprite.showIndex(colIndex, rowIndex);
        }
    }

    @Override
    public void add(Node parent) {
        super.add(parent);
//        if (picture != null) {
//            picture.setWidth(width);
//            picture.setHeight(height);
//        }

    }

    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void setTransparency(float alpha) {
        if (sprite != null && sprite.getMaterial() != null) {
            imageColor.set(1f, 1f, 1f, alpha);
            sprite.getMaterial().setColor("Color", imageColor);
        }
    }

    @Override
    public float getTransparency() {
        if (sprite != null && sprite.getMaterial() != null) {
            return imageColor.getAlpha();

        }
        return 1f;
    }

    /**
     * This method will decouple the material and clone it.
     *
     * @param colorRGBA
     */
    public void setBackgroundColor(ColorRGBA colorRGBA) {
        if (sprite != null && sprite.getMaterial() != null) {
            imageColor.set(colorRGBA.r, colorRGBA.g, colorRGBA.b, imageColor.a);
            Material material = sprite.getMaterial().clone();
            material.setColor("Color", imageColor);
            sprite.setMaterial(material);
        }
    }

    public ColorRGBA getImageColor() {
        return imageColor;
    }

    @Override
    protected boolean isBatched() {
        return false;
    }

}
