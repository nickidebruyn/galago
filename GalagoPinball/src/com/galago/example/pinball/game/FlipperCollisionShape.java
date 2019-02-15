/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.galago.example.pinball.game;

import com.bruynhuis.galago.sprite.physics.shape.CollisionShape;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import java.io.IOException;
import java.util.ArrayList;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Vector2;

/**
 *
 * @author nidebruyn
 */
public class FlipperCollisionShape extends CollisionShape {

    private float width = 2f;
    private float height = 0.5f;
    private float gap = 0.2f;

    public FlipperCollisionShape() {

    }

    /**
     * creates a collision box from the given size
     */
    public FlipperCollisionShape(float width, float height, float gap) {
        this.width = width;
        this.height = height;
        this.gap = gap;

        createShape();
    }

    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);

    }

    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);

        createShape();
    }

    private void log(String text) {
        System.out.println(text);
    }

    protected void createShape() {
        //Create the points for the line
        ArrayList<Vector2> vertices = new ArrayList<Vector2>();
        vertices.add(new Vector2(0, height/2f));
        vertices.add(new Vector2(0, -height/2f));
        vertices.add(new Vector2(width, -gap/2f));
        vertices.add(new Vector2(width, gap/2f));

        //Convert to array
        Vector2[] verts = new Vector2[vertices.size()];
        vertices.toArray(verts);

        cShape = new Polygon(verts);

    }
}
