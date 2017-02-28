/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sprite.physics.shape;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;
import java.io.IOException;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;

/**
 *
 * @author nidebruyn
 */
public class TriCollisionShape extends CollisionShape {

    private Vector3f vec1;
    private Vector3f vec2;
    private Vector3f vec3;

    public TriCollisionShape() {
    }

    /**
     * creates a collision shape with 3 points. It will ignore the z position
     */
    public TriCollisionShape(Vector3f vec1, Vector3f vec2, Vector3f vec3) {
        this.vec1 = vec1;
        this.vec2 = vec2;
        this.vec3 = vec3;
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
        Vector2 point1 = new Vector2(vec1.x, vec1.y);
        Vector2 point2 = new Vector2(vec2.x, vec2.y);
        Vector2 point3 = new Vector2(vec3.x, vec3.y);

        cShape = new Triangle(point1, point2, point3);
    }
}
