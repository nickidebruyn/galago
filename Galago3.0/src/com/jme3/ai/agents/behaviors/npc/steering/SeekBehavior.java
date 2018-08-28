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
package com.jme3.ai.agents.behaviors.npc.steering;

import com.jme3.ai.agents.Agent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * Purpose of seek behavior is to steer agent towards a specified position or
 * object.<br>
 *
 * You can seek another agent or a specific space location.
 *
 * @author Tihomir Radosavljević
 * @author Jesús Martín Berlanga
 * @version 1.5.1
 */
public class SeekBehavior extends AbstractStrengthSteeringBehavior {

    /**
     * Agent whom we seek.
     */
    private Agent target;
    private Vector3f seekingPosition;

    public SeekBehavior(Agent agent) {
        super(agent);
    }

    /**
     * Constructor for seek behaviour.
     *
     * @param agent to whom behavior belongs
     * @param target agent whom we seek
     */
    public SeekBehavior(Agent agent, Agent target) {
        super(agent);
        this.target = target;
    }

    /**
     * Constructor for seek behavior.
     *
     * @param agent to whom behavior belongs
     * @param seekingPos position that we seek
     * @param spatial active spatial during excecution of behavior
     */
    public SeekBehavior(Agent agent, Agent target, Spatial spatial) {
        super(agent, spatial);
        this.target = target;
    }

    /**
     * Constructor for seek behavior.
     *
     * @param agent to whom behavior belongs
     * @param seekingPosition position that we seek
     */
    public SeekBehavior(Agent agent, Vector3f seekingPosition) {
        super(agent);
        this.seekingPosition = seekingPosition;
    }

    /**
     * Constructor for seek behavior.
     *
     * @param agent to whom behavior belongs
     * @param target agent from we seek
     * @param spatial active spatial during excecution of behavior
     */
    public SeekBehavior(Agent agent, Vector3f seekingPosition, Spatial spatial) {
        super(agent, spatial);
        this.seekingPosition = seekingPosition;
    }

    /**
     * Calculate steering vector.
     *
     * @return steering vector
     *
     * @see AbstractStrengthSteeringBehavior#calculateRawSteering()
     */
    @Override
    protected Vector3f calculateRawSteering() {
        Vector3f desiredVelocity;

        if (this.target != null) {
            desiredVelocity = target.getLocalTranslation().subtract(agent.getLocalTranslation());
        } else if (this.seekingPosition != null) {
            desiredVelocity = this.seekingPosition.subtract(agent.getLocalTranslation()).normalize();
        } else {
            return new Vector3f(); //We do not have a target or position to seek
        }
        Vector3f aVelocity = this.agent.getVelocity();

        if (aVelocity == null) {
            aVelocity = new Vector3f();
        }

        return desiredVelocity.subtract(aVelocity);
    }

    /**
     * Get agent from we seek.
     *
     * @return agent
     */
    public Agent getTarget() {
        return target;
    }

    /**
     * Setting agent from we seek.
     *
     * @param target
     */
    public void setTarget(Agent target) {
        this.target = target;
        this.seekingPosition = null;
    }

    public Vector3f getSeekingPosition() {
        return this.seekingPosition;
    }

    public void setSeekingPosition(Vector3f seekingPosition) {
        this.seekingPosition = seekingPosition;
        this.target = null;
    }
}
