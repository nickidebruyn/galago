/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sprite.physics.shape;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector3f;
import java.io.IOException;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;

/**
 *
 * @author nidebruyn
 */
public abstract class CollisionShape implements Savable {

    protected Convex cShape;
    protected Vector3f scale = new Vector3f(1, 1, 1);
    protected Vector3f location = new Vector3f(0, 0, 0);
    protected float margin = 0.0f;
    protected BodyFixture bodyFixture;

    public CollisionShape() {
    }

    /**
     * used internally
     */
    public Convex getCShape() {
        return cShape;
    }

    /**
     * used internally
     */
    public void setCShape(Convex cShape) {
        this.cShape = cShape;
    }
    
    public void setLocation(float x, float y) {
        this.cShape.translate(x, y);
        this.location.setX(x);
        this.location.setY(y);
    }

    public Vector3f getLocation() {
        return location;
    }
    
    public void setRotation(float angle) {
        this.cShape.rotateAboutCenter(angle);
    }

    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
//        capsule.write(scale, "scale", new Vector3f(1, 1, 1));
//        capsule.write(getMargin(), "margin", 0.0f);
    }

    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
//        this.scale = (Vector3f) capsule.readSavable("scale", new Vector3f(1, 1, 1));
//        this.margin = capsule.readFloat("margin", 0.0f);
    }

    public BodyFixture getBodyFixture() {
        return bodyFixture;
    }

    public void setBodyFixture(BodyFixture bodyFixture) {
        this.bodyFixture = bodyFixture;
    }
    
    
}
