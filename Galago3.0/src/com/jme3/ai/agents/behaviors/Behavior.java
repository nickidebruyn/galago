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
package com.jme3.ai.agents.behaviors;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.BehaviorExceptions.AgentNotIncludedException;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * Base class for agent behaviors.
 *
 * @author Tihomir Radosavljević
 * @author Jesús Martín Berlanga
 * @version 1.2.1
 */
public abstract class Behavior extends AbstractControl {

    /**
     * Agent to whom behavior belongs.
     */
    protected Agent agent;

    /**
     * Constructor for behavior that doesn't have any special spatial during
     * execution.
     *
     * @param agent to whom behavior belongs
     * @throws AgentNotIncludedException if agent is null
     */
    public Behavior(Agent agent) {
        if (agent == null) {
            throw new BehaviorExceptions.AgentNotIncludedException();
        }
        this.agent = agent;
        this.spatial = agent.getSpatial();
    }

    /**
     * Constructor for behavior that has spatial during execution.
     *
     * @param agent to whom behavior belongs
     * @param spatial which is active during execution
     * @see Behavior#Behavior(com.jme3.ai.agents.Agent)
     * @throws AgentNotIncludedException if agent is null
     */
    public Behavior(Agent agent, Spatial spatial) {
        if (agent == null) {
            throw new BehaviorExceptions.AgentNotIncludedException();
        }
        this.agent = agent;
        this.spatial = spatial;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
