/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.test.blockbuilder;

import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.games.basic.BasicGame;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.water.SimpleWaterProcessor;

/**
 *
 * @author nidebruyn
 */
public class BlockBuilderGame extends BasicGame {

    public static final String TERRAIN = "terrain";
    private Node scene;
    private DirectionalLightShadowRenderer dlsr;
    private SimpleWaterProcessor simpleWaterProcessor;
    private FilterPostProcessor oceanFilterPostProcessor;
    private Vector3f lightPos =  new Vector3f(33,12,-29);
    private boolean unshaded = false;

    public BlockBuilderGame(BaseApplication baseApplication, Node rootNode, boolean unshaded) {
        super(baseApplication, rootNode);
        this.unshaded = unshaded;
    }

    @Override
    public void init() {
        
        SpatialUtils.addSkySphere(levelNode, 2);
        
        if (!unshaded) {
            initLight(ColorRGBA.White, new Vector3f(.35f, -1f, 1f).normalizeLocal());
            loadFilter();            
        }

        if (unshaded) {
            simpleWaterProcessor = SpatialUtils.addSimpleWater(levelNode, 128, 1f, 0.01f, 10f);
        } else {
            oceanFilterPostProcessor = SpatialUtils.addOceanWater(levelNode, sunLight.getDirection().clone(), 0.5f);
        }        

    }

    /**
     * load some effects
     */
    private void loadFilter() {
        dlsr = new DirectionalLightShadowRenderer(baseApplication.getAssetManager(), 512, 1);
        dlsr.setLight(sunLight);
//        dlsr.setShadowIntensity(0.4f);
        baseApplication.getViewPort().addProcessor(dlsr);
        log("Loaded the shadows");
    }

    @Override
    public void close() {
        if (simpleWaterProcessor != null) {
            getBaseApplication().getViewPort().removeProcessor(simpleWaterProcessor);
        }
        
        if (oceanFilterPostProcessor != null) {
            getBaseApplication().getViewPort().removeProcessor(oceanFilterPostProcessor);
        }
        
        super.close(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public void addObject(String item, Vector3f position) {
        Spatial spatial = baseApplication.getAssetManager().loadModel("Models/cubes/cuberockgrass.j3o");
        spatial.setName(TERRAIN);
        spatial.setLocalTranslation(position);
        levelNode.attachChild(spatial);
        
    }
    
}
