package com.galago.editor.utils;

import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

/**
 *
 * @author ndebruyn
 */
public class ModelReference {
    
    private String group;
    private String name;
    private Texture previewTexture;
    private Spatial model;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Texture getPreviewTexture() {
        return previewTexture;
    }

    public void setPreviewTexture(Texture previewTexture) {
        this.previewTexture = previewTexture;
    }

    public Spatial getModel() {
        return model;
    }

    public void setModel(Spatial model) {
        this.model = model;
    }
    
    
    
}
