/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.galago.tests.screens;

import com.bruynhuis.galago.screen.AbstractScreen;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

/**
 *
 * @author NideBruyn
 */
public class TextureMaskingScreen extends AbstractScreen {
	
    public static final String NAME = "TextureMaskingScreen";
    private Label title;
    private Spatial floor;
    private Spatial canvas;
    private PointLight pointLight;

    @Override
    protected void init() {
        title = new Label(hudPanel, "Masking a texture");
        title.centerTop(0, 0);
        
        
    }

    @Override
    protected void load() {
        
        floor = SpatialUtils.addBox(rootNode, 10, 0.1f, 10);
        SpatialUtils.addCartoonColor(floor, null, ColorRGBA.White, ColorRGBA.Black, 0.1f, false, false);
        
        canvas = SpatialUtils.addPlane(rootNode, 3, 3);
        canvas.move(0, 1, 0);
        canvas.rotate(FastMath.DEG_TO_RAD*90, 0, 0);
        Material canvasMat = new Material(baseApplication.getAssetManager(), "MatDefs/unshadedmask.j3md");
        canvasMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        Texture texture = baseApplication.getAssetManager().loadTexture("Textures/floor.jpg");
        Texture textureMask = baseApplication.getAssetManager().loadTexture("Textures/skull.png");
        
        canvasMat.setTexture("Texture", texture);
        canvasMat.setTexture("MaskTexture", textureMask);
        canvasMat.setColor("MaskColor", ColorRGBA.Blue);
        
        canvas.setMaterial(canvasMat);
        
        pointLight = new PointLight(new Vector3f(0, 4, 6), ColorRGBA.White.mult(1.2f), 0);
        rootNode.addLight(pointLight);
        
        camera.setLocation(new Vector3f(0, 5, 20));        
        camera.lookAt(new Vector3f(0, 2, 0), Vector3f.UNIT_Y);
        
    }

    @Override
    protected void show() {
    }

    @Override
    protected void exit() {
        rootNode.removeLight(pointLight);
    }

    @Override
    protected void pause() {
    }
    
}
