package com.galago.tests.screens;

import com.bruynhuis.galago.control.RotationControl;
import com.bruynhuis.galago.screen.AbstractScreen;
import com.jme3.asset.AssetManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

/**
 *
 * @author ndebruyn
 */
public class GrassScreen extends AbstractScreen {
    
    public static final String NAME = "grassscreen";
    
    private DirectionalLight sun;
    private AmbientLight al;

    @Override
    protected void init() {
    }

    @Override
    protected void load() {
        Material m = createGrassMaterial(assetManager, "Models/grass/grass-blades.png", 0.2f, new Vector2f(1f, 1f));
        System.out.println("Loaded: " + m);
        
        Spatial grass = assetManager.loadModel("Models/grass/grass1.j3o");
        grass.setMaterial(m);
        rootNode.attachChild(grass);
        
        for (int i = 0; i < 50; i++) {
            Spatial g = grass.clone(false);
            g.setLocalTranslation(FastMath.nextRandomInt(-100, 100)*0.1f, 0, FastMath.nextRandomInt(-100, 100)*0.1f);
            rootNode.attachChild(g);            
        }
        
        rootNode.addControl(new RotationControl(10));
        
        sun = new DirectionalLight(new Vector3f(0.8f, 0.8f, -0.5f), ColorRGBA.White);
        rootNode.addLight(sun);
        
        al = new AmbientLight(ColorRGBA.LightGray);
        rootNode.addLight(al);
        
        camera.setLocation(new Vector3f(-10f, 5f, 10f));
        camera.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
        
    }

    @Override
    protected void show() {
    }

    @Override
    protected void exit() {
    }

    @Override
    protected void pause() {
    }
    
    public static Material createGrassMaterial(AssetManager assetManager, String texture, float windStrength, Vector2f windDirection) {
        Texture texture1 = assetManager.loadTexture(texture);
        Material mat = new Material(assetManager, "Resources/MatDefs/Grass.j3md");  // create a simple material
        texture1.setWrap(Texture.WrapAxis.S, Texture.WrapMode.Repeat);
        mat.setTexture("DiffuseMap", texture1);
        mat.setVector2("WindDirection", windDirection);
        mat.setFloat("WindStrength", windStrength);
//        mat.setTexture("Noise", assetManager.loadTexture("Models/grass/noise.jpg"));

        mat.setTransparent(true);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
//        mat.setColor("Color", new ColorRGBA(1f, 1f, 1f, 1f));
//        mat.setFloat("Time", 0);
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        mat.setFloat("AlphaDiscardThreshold", 0.55f);
        
        return mat;
    }    
    
}
