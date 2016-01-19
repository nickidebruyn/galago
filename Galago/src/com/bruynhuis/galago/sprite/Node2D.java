/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.sprite;

import com.bruynhuis.galago.app.BaseApplication;
import com.bruynhuis.galago.util.SharedSystem;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.BatchNode;

/**
 *
 * @author nidebruyn
 */
public class Node2D extends BatchNode {
    
    private BaseApplication baseApplication;

    public Node2D() {
        reinit();
    }

    public Node2D(String name) {
        super(name);
        reinit();
    }   
    
    protected void reinit() {
        baseApplication = SharedSystem.getInstance().getBaseApplication();
        setQueueBucket(RenderQueue.Bucket.Gui);
//        setLocalTranslation((baseApplication.getSCREEN_WIDTH() * 0.5f) * baseApplication.getApplicationWidthScaleFactor(), 
//                (baseApplication.getSCREEN_HEIGHT() * 0.5f) * baseApplication.getApplicationHeightScaleFactor(), -5f);
    }

    @Override
    public void batch() {
        super.batch(); //To change body of generated methods, choose Tools | Templates.        
        reinit();
        
    }
    
}
