/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.test;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;
import java.util.ArrayList;
import jme3tools.optimize.TextureAtlas;

/**
 *
 * @author nidebruyn
 */
public class TestGUIBatch extends SimpleApplication {

    protected TextureAtlas textureAtlas;
    protected TouchButton printButton;
    protected BatchNode batchNode;
    protected ArrayList<Geometry> al = new ArrayList<Geometry>();

    public static void main(String[] args) {
        TestGUIBatch test = new TestGUIBatch();
        test.start();
    }

    @Override
    public void simpleInitApp() {
        batchNode = new BatchNode("piicturenode");
        guiNode.attachChild(batchNode);

        for (int i = -5; i < 5; i++) {
            for (int j = -5; j < 5; j++) {
                addImageGeom("Interface/smile.png", 128f * i, 128f * j);
            }
        }

        optimize();
        batchNode.batch();
    }

    protected void addImageIcon(String image, float x, float y) {
        Texture texture = assetManager.loadTexture(image);
        Picture picture = new Picture("Picture");
        picture.setMaterial(makeGuiMaterial(texture));
        picture.setWidth(128);
        picture.setHeight(128);
        batchNode.attachChild(picture);
        picture.setPosition(x, y);
        al.add(picture);

        textureAtlas.addTexture(texture, "ColorMap");

    }

    protected void addImageGeom(String image, float x, float y) {
        Texture texture = assetManager.loadTexture(image);
        Quad q = new Quad(128, 128);
        Geometry picture = new Geometry("Picture", q);
        picture.setMaterial(makeGuiMaterial(texture));
        batchNode.attachChild(picture);
        picture.setLocalTranslation(x, y, 0);
        al.add(picture);

        textureAtlas.addTexture(texture, "ColorMap");

    }

    protected void optimize() {
        Material m = makeGuiMaterial(textureAtlas.getAtlasTexture("ColorMap"));
        for (int i = 0; i < al.size(); i++) {
            Geometry geometry = al.get(i);
            textureAtlas.applyCoords(geometry);
            geometry.setMaterial(m);
        }


    }

    protected void addText(String text, float x, float y) {
        BitmapText bitmapText = new BitmapText(new BitmapFont());
        bitmapText.setText(text);
        bitmapText.setLocalTranslation(x, y, 1);
        batchNode.attachChild(bitmapText);
    }

    protected Material makeGuiMaterial(Texture texture) {
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.White);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        material.setTexture("ColorMap", texture);
        return material;
    }
}
