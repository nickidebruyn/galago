package com.galago.editor.spatial;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author ndebruyn
 */
public class SelectObjectOutliner {

    private AssetManager assetManager;

    private Material wireMaterial;
    private Node modelNode;
    private int width = 5;
    private ColorRGBA color = ColorRGBA.Yellow;

    /**
     *
     */
    public SelectObjectOutliner(AssetManager assetManager) {
        this.assetManager = assetManager;

    }

    /**
     * @param type of filter: OUTLINER_TYPE_FILTER or OUTLINER_TYPE_MATERIAL
     * @param width of the selection border
     * @param color of the selection border
     * @param modelNode direct node containing the spacial. Wil be used to add
     * geometry in OUTLINER_TYPE_MATERIAL node.
     * @param fpp - FilterPostProcessor to handle filtering
     * @param renderManager
     * @param assetManager
     * @param cam - main cam
     */
    public void initOutliner(int width, ColorRGBA color, Node modelNode) {
        this.modelNode = modelNode;
        this.width = width;
        this.color = color;

        wireMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        wireMaterial.setColor("Color", color);//color
        wireMaterial.getAdditionalRenderState().setWireframe(true); //we want wireframe
        wireMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);//that's just because we add an alpha pulse to the selection later, this is not mandatory
        wireMaterial.getAdditionalRenderState().setLineWidth(width); //you can play with this param to increase the line thickness
        wireMaterial.getAdditionalRenderState().setPolyOffset(-3f, -3f); //this is trick one, offsetting the polygons
        wireMaterial.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Front); // trick 2 we hide the front faces to not see the wireframe on top of the geom

    }

    public void updateColor(ColorRGBA color) {
        if (wireMaterial != null) {
            wireMaterial.setColor("Color", color);
        }

    }

    public void updatePosition(Spatial model) {
        if (model.getUserData("OutlineGeo") != null) {
            Spatial spatial = model.getUserData("OutlineGeo");
            spatial.setLocalTranslation(model.getWorldTranslation());
        }

    }

    public void updateScale(Spatial model) {
        if (model.getUserData("OutlineGeo") != null) {
            Spatial spatial = model.getUserData("OutlineGeo");
            spatial.setLocalScale(model.getWorldScale());
        }

    }

    public void updateRotation(Spatial model) {
        if (model.getUserData("OutlineGeo") != null) {
            Spatial spatial = model.getUserData("OutlineGeo");
            spatial.setLocalRotation(model.getWorldRotation());
        }

    }

    /**
     * @param model to be delected
     */
    public void deselect(Spatial model) {
        hideOutlineMaterialEffect(model);
        model.setUserData("OutlineSelected", false);
    }

    /**
     * @param model to delected
     */
    public void select(Spatial model) {
        showOutlineMaterialEffect(model, width, color);
        model.setUserData("OutlineSelected", true);
    }

    /**
     * @param model
     * @return
     */
    public boolean isSelected(Spatial model) {
        if (model.getUserData("OutlineSelected") != null && ((Boolean) model.getUserData("OutlineSelected")) == true) {
            return true;
        } else {
            return false;
        }
    }

    private void hideOutlineMaterialEffect(Spatial model) {

        Spatial geo = (Spatial) model.getUserData("OutlineGeo");
        if (geo != null) {
            modelNode.detachChild(geo);
        }
    }

    private void showOutlineMaterialEffect(Spatial model, int width, ColorRGBA color) {

        Spatial geo = model.clone(false);
        for (int i = 0; i < geo.getNumControls(); i++) {
            geo.removeControl(geo.getControl(i));
        }
        
        geo.setMaterial(wireMaterial);
        model.setUserData("OutlineGeo", geo);
        geo.setLocalTranslation(model.getWorldTranslation()); //Nicki Fix
        modelNode.attachChild(geo);
    }

}
