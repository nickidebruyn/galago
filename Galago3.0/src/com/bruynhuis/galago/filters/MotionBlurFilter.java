/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.filters;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Matrix4f;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;

/**
 *
 * @author NideBruyn
 */
public class MotionBlurFilter extends Filter {
     
    private FrameBuffer currentBuffer;
    private Texture2D depthTexture;
    
    private Matrix4f previousMatrix;
    private Matrix4f matrixReference;
    
    /*
    private Renderer renderer;
    private Texture2D previous;    
    private FrameBuffer prevFb;
    private FrameBuffer prevSceneFb;
    private float strength = 0.5f;
    private float frameOffset = 0.04f;
    private float cpt = 0;
    */
 
    public MotionBlurFilter() {
        super("Motion blur");
    }
    
    @Override
    protected void initFilter(AssetManager manager,
            RenderManager renderManager, ViewPort vp, int w, int h) {
        
        material = new Material(manager, "Resources/filters/motionblur.j3md");
        
        currentBuffer = new FrameBuffer(w, h, 1);
        currentBuffer.setColorBuffer(Image.Format.RGBA8);
        currentBuffer.setDepthBuffer(Image.Format.Depth);
        depthTexture = new Texture2D(w, h, Image.Format.Depth);
        currentBuffer.setDepthTexture(depthTexture);
        setDepthTexture(depthTexture);
        
        matrixReference = new Matrix4f(vp.getCamera().getViewProjectionMatrix());
        previousMatrix = new Matrix4f(matrixReference);
        
        /*
        postRenderPasses = new ArrayList<Pass>();
        blurPass = new Pass() {
            @Override
            public boolean requiresSceneAsTexture() {
                return true;
            }

            @Override
            public void beforeRender() {
                material.setTexture("DepthTexture", depthTexture);
                material.setInt("Strength", 2);
                material.setMatrix4("PrevMatrix", previousMatrix);
            }
        };
        blurPass.init(renderManager.getRenderer(), w, h, Format.RGBA8, Format.Depth, 1, material);
        postRenderPasses.add(blurPass);
        */
        /*        
        renderer = renderManager.getRenderer();
 
        currentBuffer = new FrameBuffer(w, h, 1);
        currentBuffer.setColorBuffer(Format.RGBA8);
        currentBuffer.setDepthBuffer(Format.Depth);
        //currentBuffer.setColorTexture(previous);
        depthTexture = new Texture2D(w, h, Format.Depth);
        currentBuffer.setDepthTexture(depthTexture);
 
        material = new Material(manager, "Common/MatDefs/Post/MotionBlur.j3md");
 
        //material.setTexture("Depth", depthTexture);
        setDepthTexture(depthTexture);
        this.previousMatrix = vp.getCamera().getViewProjectionMatrix().invert();
        material.setMatrix4("PrevMatrix", this.previousMatrix);
        */
        
        //material.setTexture("Previous", previous);
        //material.setFloat("strength", strength);
    }
 
    @Override
    protected void postFrame(RenderManager renderManager, ViewPort viewPort, FrameBuffer prevFilterBuffer, FrameBuffer sceneBuffer) {
        if (enabled) {
            renderManager.getRenderer().copyFrameBuffer(sceneBuffer, currentBuffer, true);
            matrixReference.set(viewPort.getCamera().getViewProjectionMatrix());
        }        
    }
 
    /*
    @Override
    protected void postQueue(RenderManager renderManager, ViewPort viewPort) {
        renderManager.getRenderer().setBackgroundColor(ColorRGBA.BlackNoAlpha);            
        renderManager.getRenderer().setFrameBuffer(this.currentBuffer); // preGlowPass.getRenderFrameBuffer()
        renderManager.getRenderer().clearBuffers(true, true, true);
        renderManager.setForcedMaterial(this.material);
        renderManager.setForcedTechnique("MotionBlur");
        renderManager.renderViewPortQueues(viewPort, false);         
        renderManager.setForcedTechnique(null);
        renderManager.setForcedMaterial(null);
        renderManager.getRenderer().setFrameBuffer(viewPort.getOutputFrameBuffer());
    }
    */
    
    @Override
    public void preFrame(float tpf) {
        previousMatrix.set(matrixReference);
        /*
        if (enabled) {
            //computing time since last saved frame
            cpt += tpf;
            if (cpt >= frameOffset) {
                //we reached the frame offset, let's save the previous frame
                if (prevSceneFb != null) {
                    //copying the frmae buffers
                    renderer.copyFrameBuffer(prevSceneFb, prevFb);
                }
                //reseting time
                cpt = 0;
            }
        }
        */
    }
 
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        /*
        if (enabled) {
            renderer.copyFrameBuffer(prevSceneFb, prevFb);
        }
        */
    }
 
    @Override
    protected Material getMaterial() {
        material.setInt("Strength", 2);
        material.setMatrix4("PrevMatrix", this.previousMatrix);
        return material;
    }
 
    /*
    public float getStrength() {
        return strength;
    }
 
    public void setStrength(float strength) {
        this.strength = strength;
        if (material != null) {
            material.setFloat("strength", strength);
        }
    }
 
    public float getFrameOffset() {
        return frameOffset;
    }
 
    public void setFrameOffset(float frameOffset) {
        this.frameOffset = frameOffset;
    }
    */
}
