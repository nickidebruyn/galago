/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sprite.physics.shape;

import com.bruynhuis.galago.util.SharedSystem;
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
public class SpikeCollisionShape extends CollisionShape {

    private float width;
    private float height;
    private int splits = 5;

    public SpikeCollisionShape() {
        width = 50;
        height = 10;
        splits = 5;
    }

    /**
     * creates a collision box from the given size
     */
    public SpikeCollisionShape(float width, float height, int splits) {
        this.width = width;
        this.height = height;
        this.splits = splits;
        createShape();
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getSplits() {
        return splits;
    }

    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(width, "width", 50);
        capsule.write(height, "height", 10);
        capsule.write(splits, "splits", 5);
    }

    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);
        this.width = capsule.readFloat("width", 50.0f);
        this.height = capsule.readFloat("height", 10.0f);
        this.splits = capsule.readInt("splits", 5);
        createShape();
    }

    protected void createShape() {
        ArrayList<Vector2> vertices = new ArrayList<Vector2>();
        Vector2 point1 = new Vector2(-width * SharedSystem.getInstance().getBaseApplication().getApplicationHeightScaleFactor() * 0.5f, -height * SharedSystem.getInstance().getBaseApplication().getApplicationHeightScaleFactor() * 0.5f);
        vertices.add(point1);
        
        int max = (int)(splits*0.5f);
        int min = -(int)(splits*0.5f);
        boolean bottom = true;
        float heightPos = 0;
        Vector2 point = null;
                
        for (int i = max; i > min; i--) {
            float per = ((float)i/(float)splits);
            System.out.println("index = " + per);
            if (bottom) {
                heightPos = -height * SharedSystem.getInstance().getBaseApplication().getApplicationHeightScaleFactor() * 0.5f;
                
            } else {
                heightPos = height * SharedSystem.getInstance().getBaseApplication().getApplicationHeightScaleFactor() * 0.5f;
                
            }
            
            point = new Vector2(width * SharedSystem.getInstance().getBaseApplication().getApplicationHeightScaleFactor() * per);
            vertices.add(point);
            
            bottom = !bottom; //reverse the height
        }

        Vector2[] verts = new Vector2[vertices.size()];
        vertices.toArray(verts);

        cShape = new Polygon(verts);


    }
}
