/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sprite.physics.shape;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector2f;
import java.io.IOException;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Link;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

/**
 * TODO:
 * @author nidebruyn
 */
public class LinkCollisionShape extends CollisionShape {

    private Vector2f[] points;

    public LinkCollisionShape() {
        points = new Vector2f[]{
                    new Vector2f(0f, 0),
                    new Vector2f(5, 1),
        };
    }

    /**
     * creates a collision box from the given size
     */
    public LinkCollisionShape(Vector2f[] p) {
        this.points = p;
        createShape();
    }

    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);
//        capsule.write(width, "width", 10);
//        capsule.write(height, "height", 10);
    }

    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);
//        this.width = capsule.readFloat("width", 10.0f);
//        this.height = capsule.readFloat("height", 10.0f);
        createShape();
    }

    protected void createShape() {
        Vector2[] vertices = new Vector2[points.length];
        
//        cShape = new Link(point1, point2);
    }

}
