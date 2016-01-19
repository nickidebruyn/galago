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
import java.util.ArrayList;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

/**
 *
 * @author nidebruyn
 */
public class LineCollisionShape extends CollisionShape {
    
    private Vector2f start;
    private Vector2f end;
    private float lineWidth = 0.05f;

    public LineCollisionShape() {

    }

    /**
     * creates a collision box from the given size
     */
    public LineCollisionShape(Vector2f start, Vector2f end, float lineWidth) {
        this.start = start;
        this.end = end;
        this.lineWidth = lineWidth;

        createShape();
    }

    public Vector2f getStart() {
        return start;
    }

    public Vector2f getEnd() {
        return end;
    }
    
    public void updateShape(Vector2f start, Vector2f end) {
        this.start = start;
        this.end = end;
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
        log("CREATE SHAPE: start:" + start + "; end:" + end);
        //Create the points for the line
        ArrayList<Vector2> vertices = new ArrayList<Vector2>();
        //Kwadrant 1
        if (start.x < end.x && start.y > end.y) {
            log("Kwadrant 1");
            vertices.add(new Vector2(start.x, start.y));
            vertices.add(new Vector2(start.x-lineWidth, start.y-lineWidth));
            vertices.add(new Vector2(end.x-lineWidth, end.y-lineWidth));
            vertices.add(new Vector2(end.x, end.y));
            
        } else if (start.x > end.x && start.y > end.y) {
            log("Kwadrant 2");
            vertices.add(new Vector2(end.x, end.y));
            vertices.add(new Vector2(end.x+lineWidth, end.y-lineWidth));
            vertices.add(new Vector2(start.x+lineWidth, start.y-lineWidth));
            vertices.add(new Vector2(start.x, start.y));
            
        } else if (start.x > end.x && start.y < end.y) {
            log("Kwadrant 3");
            vertices.add(new Vector2(end.x, end.y));
            vertices.add(new Vector2(end.x-lineWidth, end.y-lineWidth));            
            vertices.add(new Vector2(start.x-lineWidth, start.y-lineWidth));
            vertices.add(new Vector2(start.x, start.y));
            
        } else if (start.x < end.x && start.y < end.y) {
            log("Kwadrant 4");
            vertices.add(new Vector2(start.x, start.y));
            vertices.add(new Vector2(start.x+lineWidth, start.y-lineWidth));
            vertices.add(new Vector2(end.x+lineWidth, end.y-lineWidth));
            vertices.add(new Vector2(end.x, end.y));
        }
        
        //Convert to array
        Vector2[] verts = new Vector2[vertices.size()];
        vertices.toArray(verts);
        
        if (vertices.size() <= 0) {
            log("RECTANGLE SHAPE");
            cShape = new Rectangle(lineWidth, lineWidth);
        } else {
            cShape = new Polygon(verts);
        }

    }
}
