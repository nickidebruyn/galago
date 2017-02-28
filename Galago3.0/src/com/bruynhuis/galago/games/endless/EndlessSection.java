/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.endless;

import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.ArrayList;

/**
 *
 * @author nidebruyn
 */
public abstract class EndlessSection {

    protected EndlessGame endlessGame;
    protected Vector3f position;
    protected ArrayList<Spatial> spatials = new ArrayList<Spatial>();
    protected Node sectionNode;

    public EndlessSection(EndlessGame endlessGame, Vector3f position) {
        this.endlessGame = endlessGame;
        this.position = position;
    }

    public void load() {
        sectionNode = new Node("secion-node");
        sectionNode.setLocalTranslation(position);

        //Add the controller that will do checking when to remove or add a section
        sectionNode.addControl(new AbstractControl() {
            @Override
            protected void controlUpdate(float tpf) {
                if (endlessGame.isStarted() && !endlessGame.isPaused() && endlessGame.getPlayer() != null) {
                    if (getPosition().distance(endlessGame.getPlayer().getPosition()) > endlessGame.getSectionSpacing()) {

                        //We only move to next section if the player is actually higher or further away
                        if ((endlessGame.getDirection().y > 0 && endlessGame.getPlayer().getPosition().y > sectionNode.getWorldTranslation().y)
                                || (endlessGame.getDirection().x > 0 && endlessGame.getPlayer().getPosition().x > sectionNode.getWorldTranslation().x)
                                || (endlessGame.getDirection().z > 0 && endlessGame.getPlayer().getPosition().z < sectionNode.getWorldTranslation().z)) {

                            log("Closing section at: " + sectionNode.getWorldTranslation());
                            endlessGame.nextSection();
                            close();
                            
                        }

                    }
                }
            }

            @Override
            protected void controlRender(RenderManager rm, ViewPort vp) {
            }
        });

        init();        
        
        endlessGame.getLevelNode().attachChild(sectionNode);
    }

    protected abstract void init();

    protected void close() {
        sectionNode.removeFromParent();

        for (int i = 0; i < spatials.size(); i++) {
            Spatial spatial = spatials.get(i);
            RigidBodyControl rigidBodyControl = spatial.getControl(RigidBodyControl.class);
            if (rigidBodyControl != null) {
                endlessGame.getBaseApplication().getBulletAppState().getPhysicsSpace().remove(rigidBodyControl);
            }
            spatial.removeFromParent();
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    protected void log(String text) {
        System.out.println(text);
    }

    protected void addObstacle(Spatial spatial, Vector3f position, float mass) {
        spatial.setLocalTranslation(this.position.clone().add(position));
        spatial.setName(EndlessGame.TYPE_OBSTACLE);        
        
        //Physics movement        
        RigidBodyControl rigidBodyControl = new RigidBodyControl(mass);
        spatial.addControl(rigidBodyControl);
        endlessGame.getBaseApplication().getBulletAppState().getPhysicsSpace().add(rigidBodyControl);
        
        //NB: Add to the rootNode
        sectionNode.attachChild(spatial);        
        rigidBodyControl.setFriction(0.3f);
        spatials.add(spatial);
    }

    protected void addStatic(Spatial spatial, Vector3f position, float mass) {
        spatial.setLocalTranslation(this.position.clone().add(position));
        spatial.setName(EndlessGame.TYPE_STATIC);        
        
        //Physics movement        
        RigidBodyControl rigidBodyControl = new RigidBodyControl(mass);
        spatial.addControl(rigidBodyControl);
        endlessGame.getBaseApplication().getBulletAppState().getPhysicsSpace().add(rigidBodyControl);
        
        //NB: Add to the rootNode
        sectionNode.attachChild(spatial);        
        rigidBodyControl.setFriction(0.3f);
        spatials.add(spatial);
    }

    protected void addPickup(Spatial spatial, Vector3f position) {
        spatial.setLocalTranslation(this.position.clone().add(position));
        spatial.setName(EndlessGame.TYPE_PICKUP);        
         
        //Physics movement        
        GhostControl rigidBodyControl = new GhostControl();
        spatial.addControl(rigidBodyControl);
        endlessGame.getBaseApplication().getBulletAppState().getPhysicsSpace().add(rigidBodyControl);
        
        //NB: Add to the rootNode
        sectionNode.attachChild(spatial);        
        spatials.add(spatial);
    }

    protected void addVegetation(Spatial spatial, Vector3f position) {
        spatial.setLocalTranslation(this.position.clone().add(position));
        spatial.setName(EndlessGame.TYPE_VEGETATION);        
        sectionNode.attachChild(spatial);
        spatials.add(spatial);
    }
}
