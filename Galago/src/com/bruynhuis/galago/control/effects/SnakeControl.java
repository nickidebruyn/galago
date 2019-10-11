/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.control.effects;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.Stack;

/**
 *
 * The snake control allows you to add a tail like feature to a spatial object.
 * 
 * @author NideBruyn
 */
public class SnakeControl extends AbstractControl {

    private Node rootNode;
    private Spatial tail;
    private float tailSpawnDistance;
    private float tailCount;
    private Spatial closestTailSpatial;
    private Stack<Spatial> tailItems = new Stack<Spatial>();

    public SnakeControl(Node rootNode, Spatial tail, float tailSpawnDistance, float tailCount) {
        this.rootNode = rootNode;
        this.tail = tail;
        this.tailSpawnDistance = tailSpawnDistance;
        this.tailCount = tailCount;
    }

    @Override
    protected void controlUpdate(float tpf) {

        //First we start by setting the startlocation and adding the Head and First tail spatial
        if (closestTailSpatial == null) {
            addTailItem();
        }
        
        //Check if we can add another tail item
        if (closestTailSpatial != null) {
            if (spatial.getLocalTranslation().distance(closestTailSpatial.getLocalTranslation()) >= tailSpawnDistance) {
                addTailItem();                
            }            
        }

    }

    protected void addTailItem() {
        closestTailSpatial = tail.clone();
        closestTailSpatial.setLocalTranslation(spatial.getLocalTranslation().clone());
        closestTailSpatial.setLocalRotation(spatial.getLocalRotation().clone());
        rootNode.attachChild(closestTailSpatial);
        
        tailItems.push(closestTailSpatial);
        
        if (tailItems.size() > tailCount) {
            Spatial itemToRemove = tailItems.firstElement();
            itemToRemove.removeFromParent();
            tailItems.remove(itemToRemove);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
