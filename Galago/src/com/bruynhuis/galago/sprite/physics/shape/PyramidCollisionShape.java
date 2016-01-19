/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sprite.physics.shape;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import java.io.IOException;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;

/**
 *
 * @author nidebruyn
 */
public class PyramidCollisionShape extends CollisionShape {

    private float width;
    private float height;

    public PyramidCollisionShape() {
        width = 10;
        height = 10;
    }

    /**
     * creates a collision box from the given size
     */
    public PyramidCollisionShape(float width, float height) {
        this.width = width;
        this.height = height;
        createShape();
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(width, "width", 10);
        capsule.write(height, "height", 10);
    }

    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);
        this.width = capsule.readFloat("width", 10.0f);
        this.height = capsule.readFloat("height", 10.0f);
        createShape();
    }

    protected void createShape() {
        Vector2 point1 = new Vector2(-width*0.5f, -height*0.5f);
        Vector2 point2 = new Vector2(0f, height*0.5f);
        Vector2 point3 = new Vector2(width*0.5f, -height*0.5f);
        
        cShape = new Triangle(point1, point3, point2);        
    }

}
