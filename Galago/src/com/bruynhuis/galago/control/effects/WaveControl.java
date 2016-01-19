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
public class WaveControl extends AbstractControl {

    private Material material;
    private String image;
    private float speedY = 3f;
    private float sizeY = 15f;
    private float depthY = 0.06f;
    private boolean applyOnSpatial = false;


    public WaveControl() {
    }

    public WaveControl(String image, float speedY, float sizeY, float depthY) {
        this.image = image;
        this.speedY = speedY;
        this.sizeY = sizeY;
        this.depthY = depthY;
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

    @Override
    protected void controlUpdate(float tpf) {
        
        //Do the initialization
        if (material == null && image != null && spatial != null) {
            setImage(image);
        }
        
        if (material != null) {
            if (!applyOnSpatial) {
                //Set the material on all the children
                spatialUpdate(spatial);
                applyOnSpatial = true;
            }
            
        }

    }

    public void setImage(String image) {

        if (this.material == null) {
            if (SharedSystem.getInstance().getBaseApplication() == null) {
                throw new RuntimeException("Shared system not properly set.");

            }
            
            //Load the material and setup the paramters
            material = new Material(SharedSystem.getInstance().getBaseApplication().getAssetManager(), "Resources/water/WaveUnshaded.j3md");

        }

        if (material != null && image != null) {
            Texture texture = SharedSystem.getInstance().getBaseApplication().getAssetManager().loadTexture(image);
            texture.setWrap(Texture.WrapMode.Repeat);
            material.setTexture("ColorMap", texture);
            material.setBoolean("DeformY_Wave", true);
            material.setFloat("SpeedY", speedY);
            material.setFloat("SizeY", sizeY);
            material.setFloat("DepthY", depthY);

        }
    }

    public Material getMaterial() {
        return material;
    }

    public float getSpeedY() {
        return speedY;
    }

    public void setSpeedY(float speedY) {
        this.speedY = speedY;
        if (material != null) {
            material.setFloat("SpeedY", speedY);
        }
    }

    public float getSizeY() {
        return sizeY;
    }

    public void setSizeY(float sizeY) {
        this.sizeY = sizeY;
        if (material != null) {
            material.setFloat("SizeY", sizeY);
        }
    }

    public float getDepthY() {
        return depthY;
    }

    public void setDepthY(float depthY) {
        this.depthY = depthY;
        if (material != null) {
            material.setFloat("DepthY", depthY);
        }
    }


    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public Control cloneForSpatial(Spatial spatial) {
        WaveControl control = new WaveControl();
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
