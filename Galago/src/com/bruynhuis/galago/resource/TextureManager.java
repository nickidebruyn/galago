/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.resource;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import java.util.HashMap;
import java.util.Map;
import com.bruynhuis.galago.app.BaseApplication;
import com.jme3.texture.Texture;

/**
 * 
 * Manage the textures in the game. This is to increase performance.
 *
 * @author nidebruyn
 */
public class TextureManager {

    private boolean pixelated = false;
    private BaseApplication application;
    private Map<String, Material> textures = new HashMap<String, Material>();

    public TextureManager(BaseApplication simpleApplication) {
        this.application = simpleApplication;
    }

    public void destroy() {
        textures.clear();
    }

    /**
     * Must be called to cash textures that wants to be loaded in the system.
     *
     * @param texturePath
     */
    public void loadTexture(String texturePath) {
        //GUI material
        Texture texture = application.getAssetManager().loadTexture(texturePath);
        if (isPixelated()) {
            texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
            texture.setMagFilter(Texture.MagFilter.Nearest);
        } else {
            texture.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
        }
        
        Material material = new Material(application.getAssetManager(), "Common/MatDefs/Gui/Gui.j3md");
        material.setColor("Color", ColorRGBA.White);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        material.setTexture("Texture", texture);
        textures.put(texturePath, material);
        
//        //Or Unshaded material
//        Texture texture = application.getAssetManager().loadTexture(texturePath);
//        Material material = new Material(application.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//        material.setColor("Color", ColorRGBA.White);
//        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
//        material.setTexture("ColorMap", texture);
//        textures.put(texturePath, material);

    }

    /**
     * Called when a material needs to be retrieved.
     *
     * @param texturePath
     * @return
     */
    public Material getGUIMaterial(String texturePath) {
        Material material = textures.get(texturePath);
        if (material == null) {
            loadTexture(texturePath);
            material = textures.get(texturePath);
        }
        return material;
    }

    public boolean isPixelated() {
        return pixelated;
    }

    public void setPixelated(boolean pixelated) {
        this.pixelated = pixelated;
    }
    
    
}
