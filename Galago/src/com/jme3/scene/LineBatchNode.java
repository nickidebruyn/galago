/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jme3.scene;

/**
 *
 * @author nicki
 */
public class LineBatchNode extends BatchNode {

    public LineBatchNode() {
    }

    public LineBatchNode(String name) {
        super(name);
    }

    @Override
    protected void doBatch() {
        super.doBatch();

        for (int i = 0; i < batches.size(); i++) {
            BatchNode.Batch batch = batches.get(i);
            batch.geometry.getMesh().setLineWidth(16);
//            log("Batches = " + batch.geometry.getMesh());

        }
    }
    
    /**
     * Log some text to the console
     *
     * @param text
     */
    protected void log(String text) {
        System.out.println(text);
    }
}
