/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.flat.test;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Line;

/**
 *
 * @author nidebruyn
 */
public class TestBatchLine extends SimpleApplication {
    
    private BatchNode batchNode;
        
    public static void main(String[] args) {
        TestBatchLine app = new TestBatchLine();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        batchNode = new BatchNode("drawing");
        
        addLine(Vector3f.ZERO, new Vector3f(0, 2, 0));
        addLine(new Vector3f(0, 2, 0), new Vector3f(2, 2, 0));
        
        rootNode.attachChild(batchNode);
        
        batchNode.batch();
    }
    
    protected void addLine(Vector3f start, Vector3f end) {
        Material mat = assetManager.loadMaterial("Common/Materials/RedColor.j3m");
        
        Line line = new Line(start, end);
        line.setLineWidth(10);
        
        Geometry geometry = new Geometry("line_geom", line);
        geometry.setMaterial(mat);
        batchNode.attachChild(geometry);
    }
}
