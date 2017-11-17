/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control.effects;

import com.bruynhuis.galago.util.SharedSystem;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.texture.Texture;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author nidebruyn
 */
public class FlowControl extends AbstractControl {

    private Material material;
    private String image;
    private float xSpeed;
    private float ySpeed;
    private Vector2f uvTranslate = new Vector2f(0, 0);
    private boolean applyOnSpatial = false;

    public FlowControl() {
    }

    public FlowControl(String image, float xSpeed, float ySpeed) {
        this.image = image;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        setImage(image);
    }
    
    private void spatialUpdate(Spatial sp) {
        if (sp instanceof Node) {
            nodeUpdate((Node) sp);
        } else if (sp instanceof Geometry) {
            geometryUpdate((Geometry) sp);
        }
    }

    private void nodeUpdate(Node node) {
        List<Spatial> children = node.getChildren();
        for (Spatial child : children) {
            spatialUpdate(child);
        }
    }

    private void geometryUpdate(Geometry geometry) {
        geometry.setMaterial(material);
    }

    public Vector2f getUvTranslate() {
        return uvTranslate;
    }

    public void setUvTranslate(Vector2f uvTranslate) {
        this.uvTranslate = uvTranslate;
    }
    
    @Override
    protected void controlUpdate(float tpf) {
        
        //Do the initialization
        if (material == null && image != null && spatial != null) {
            setImage(image);
        }

        //Now loop the uv translate amount
        if (material != null) {
            if (!applyOnSpatial) {
                //Set the material on all the children
                spatialUpdate(spatial);
                applyOnSpatial = true;
            }

            uvTranslate.addLocal(xSpeed * tpf, ySpeed * tpf);

            if (uvTranslate.x >= 1 || uvTranslate.x <= -1) {
                uvTranslate = uvTranslate.setX(0);

            }
            if (uvTranslate.y >= 1 || uvTranslate.y <= -1) {
                uvTranslate = uvTranslate.setY(0);

            }

            material.setVector2("TranslateAmount", uvTranslate);
        }

    }

    public void setImage(String image) {

        if (this.material == null) {
            if (SharedSystem.getInstance().getBaseApplication() == null) {
                throw new RuntimeException("Shared system not properly set.");

            }
            
            //Load the material and setup the paramters
            material = new Material(SharedSystem.getInstance().getBaseApplication().getAssetManager(), "Resources/water/FlowUnshaded.j3md");

        }

        if (material != null && image != null) {
            Texture texture = SharedSystem.getInstance().getBaseApplication().getAssetManager().loadTexture(image);
            texture.setWrap(Texture.WrapMode.Repeat);
            material.setTexture("ColorMap", texture);
            material.setVector2("TranslateAmount", uvTranslate);
            material.setBoolean("TranslateUV", true);
            material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
            material.setFloat("AlphaDiscardThreshold", 0.6f);

        }
    }

    public Material getMaterial() {
        return material;
    }

    public float getxSpeed() {
        return xSpeed;
    }

    public void setxSpeed(float xSpeed) {
        this.xSpeed = xSpeed;
    }

    public float getySpeed() {
        return ySpeed;
    }

    public void setySpeed(float ySpeed) {
        this.ySpeed = ySpeed;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public Control cloneForSpatial(Spatial spatial) {
        FlowControl control = new FlowControl();
        return control;
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule in = im.getCapsule(this);
        //TODO: load properties of this Control, e.g.
        //this.value = in.readFloat("name", defaultValue);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule out = ex.getCapsule(this);
        //TODO: save properties of this Control, e.g.
        //out.write(this.value, "name", defaultValue);
    }
}
