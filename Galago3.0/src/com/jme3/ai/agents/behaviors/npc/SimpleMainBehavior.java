/**
 * Copyright (c) 2014, jMonkeyEngine All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * Neither the name of 'jMonkeyEngine' nor the names of its contributors may be
 * used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.ai.agents.behaviors.npc;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.Behavior;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Simple main behavior for NPC. Main behavior contains other Behaviors and if
 * active it will update all behaviors that are enabled. <br> <br>
 * You can only add one steer behavior to this container. But you can use
 * CompoundSteeringBehaviour to merge more steer behaviors into one.
 *
 * @author Tihomir Radosavljević
 * @version 1.1.1
 */
public class SimpleMainBehavior extends Behavior {

    /**
     * Behaviors are implemented as LinkedList for flexibility. If there isn't
     * changing behaviors while agent is active you can change it to ArrayList
     * for speed.
     *
     * @see ArrayList
     * @see LinkedList
     */
    protected List<Behavior> behaviors;
    /**
     * Instance of aiAppState. Main behavior will not work if aiAppState is not
     * in progress.
     *
     * @see MonkeyBrainsAppState#inProgress
     */
    protected MonkeyBrainsAppState aiAppState;

    /**
     * This behavior never have spatial.
     *
     * @param agent
     */
    public SimpleMainBehavior(Agent agent) {
        //Main behavior doesn't have need for spatials.
        super(agent);
        aiAppState = MonkeyBrainsAppState.getInstance();
        behaviors = new LinkedList<Behavior>();
        enabled = true;
    }

    @Override
    protected void controlUpdate(float tpf) {
        for (Behavior behaviour : behaviors) {
            behaviour.update(tpf);
        }
    }

    /**
     * Remove all behaviors from this behavior.
     */
    public void clearBehaviors() {
        behaviors.clear();
    }

    /**
     * Set list of behaviors for this behavior to do.
     *
     * @param behaviors
     */
    public void setBehaviors(List<Behavior> behaviors) {
        this.behaviors = behaviors;
    }

    /**
     * Add behavior to this main behavior.
     *
     * @param behavior that will be added
     */
    public void addBehavior(Behavior behavior) {
        behaviors.add(behavior);
    }

    /**
     * Remove behavior from this main behavior.
     *
     * @param behavior that will be removed
     */
    public void removeBehavior(Behavior behavior) {
        behaviors.remove(behavior);
    }
}
