/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jme3.ai.monkeystuff.bluepillagent;

import com.jme3.ai.agents.Agent;
import com.jme3.renderer.Camera;
import com.jme3.ai.monkeystuff.Inventory;

/**
 *
 */
public class BluePillAgent extends Agent{
    /**
     * Camera that is attached to agent.
     */
    private Camera camera;
    /**
     * Inventory that agent will use.
     */
    private Inventory inventory;
    @Override
    protected void controlUpdate(float tpf) {
        super.controlUpdate(tpf);
        //for updating cooldown on inventory items
        if (inventory != null) {
            inventory.update(tpf);
        }
    }
    /**
     * @return camera that is attached to agent
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * Setting camera for agent. It is recommended for use mouse input.
     *
     * @param camera
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    /**
     *
     * @return inventory that agent is using
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Setting inventory system for agent to use.
     *
     * @param inventory
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

}
