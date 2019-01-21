/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.tests.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.system.JmeSystem;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import jme3tools.optimize.TextureAtlas;

/**
 *
 * @author nidebruyn
 */
public class BatchGUIScreen extends AbstractScreen {

    protected TextureAtlas textureAtlas;
    protected TouchButton printButton;
    protected BatchNode batchNode;
    protected ArrayList<Geometry> al = new ArrayList<Geometry>();

    @Override
    protected void init() {

        textureAtlas = new TextureAtlas(1024, 1024);

        batchNode = new BatchNode("piicturenode");
        window.getWindowNode().attachChild(batchNode);

        for (int i = -5; i < 5; i++) {
            for (int j = -5; j < 5; j++) {
                addImageIcon("Interface/smile.png", 128f * i, 128f * j);
//                addText(i + ", " + j, 128f * i, 128f * j);
            }
        }

        printButton = new TouchButton(hudPanel, "print", "<< Back");
        printButton.rightBottom(5, 5);
        printButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (isActive()) {
                    showPreviousScreen();
//                    writeAtlas();
                }
            }
        });
        
//        optimize();
        batchNode.batch();
        batchNode.setLocalTranslation(0, 0, 0);
        batchNode.setCullHint(Spatial.CullHint.Always);
    }

    protected void addImageIcon(String image, float x, float y) {
        Node nd = new Node("image");
//        Texture texture = window.getAssetManager().loadTexture(image);
        Picture picture = new Picture("Picture");
//        picture.setMaterial(makeGuiMaterial(texture));
        picture.setMaterial(window.getApplication().getTextureManager().getGUIMaterial(image));
        picture.setWidth(128);
        picture.setHeight(128);
        nd.attachChild(picture);
        nd.setLocalTranslation(x, y, 0f);
        batchNode.attachChild(nd);
        al.add(picture);
        picture.addControl(new AbstractControl() {

            @Override
            protected void controlUpdate(float tpf) {
                spatial.rotate(0, 0, tpf);
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
                
            }
        });
        
//        textureAtlas.addTexture(texture, "ColorMap");

    }
    
    protected void optimize() {
        Material m = makeGuiMaterial(textureAtlas.getAtlasTexture("ColorMap"));
        for (int i = 0; i < al.size(); i++) {
            Geometry geometry = al.get(i);
            textureAtlas.applyCoords(geometry);
            geometry.setMaterial(m);
//            if (geometry.getParent() instanceof BitmapText) {
//                BitmapText bt = (BitmapText) geometry.getParent();
//                bt.setSize(24);
//                bt.setColor(ColorRGBA.Blue);
//            }
        }

    }

    protected void addText(String text, float x, float y) {
        BitmapText bitmapText = new BitmapText(baseApplication.getBitmapFont());
        bitmapText.setText(text);
        bitmapText.setSize(20);
        bitmapText.setColor(ColorRGBA.Red);        
        batchNode.attachChild(bitmapText);

        for (int i = 0; i < bitmapText.getChildren().size(); i++) {
            Spatial spatial = bitmapText.getChildren().get(i);
//            log("\t-> Text child" + spatial);
            if (spatial instanceof Geometry) {
//                al.add((Geometry)spatial);
            }
        }
        bitmapText.setLocalTranslation(x, y, 1f);
        
        Material material = bitmapText.getFont().getPage(0);
//        log("Material = " + material);
        Texture texture = material.getTextureParam("ColorMap").getTextureValue();
//        log("ColorMap = " + texture);
        
        textureAtlas.addTexture(texture, "ColorMap");
    }

    protected Material makeGuiMaterial(Texture texture) {
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.White);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        material.setTexture("ColorMap", texture);
        return material;
    }

    protected void writeAtlas() {

        Texture texture = textureAtlas.getAtlasTexture("ColorMap");
        Date date = new Date();
        File file = new File("screenshot-window-atlas-" + date.getSeconds() + ".png").getAbsoluteFile();

        OutputStream outStream = null;
        try {
            outStream = new FileOutputStream(file);
            log("size = " + texture.getImage().getData().size());
            JmeSystem.writeImageFile(outStream, "png", texture.getImage().getData(0), 1024, 1024);
        } catch (IOException ex) {
            log("Error while saving screenshot" + ex);
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException ex) {
                    log("Error while saving screenshot" + ex);
                }
            }
        }
    }

    @Override
    protected void load() {
    }

    @Override
    protected void show() {
        batchNode.setCullHint(Spatial.CullHint.Never);
    }

    @Override
    protected void exit() {
        batchNode.setCullHint(Spatial.CullHint.Always);
    }

    @Override
    protected void pause() {
        
    }

}
