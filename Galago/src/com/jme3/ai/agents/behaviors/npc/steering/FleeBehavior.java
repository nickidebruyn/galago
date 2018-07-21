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
 * Flee is simply the inverse of seek and acts to steer the agent so that its
 * velocity is radially aligned away from the target. The desired velocity
 * points in the opposite direction.
 *
 * You can flee another agent or a specific space location.
 *
 * @author Tihomir Radosavljević
 * @author Jesús Martín Berlanga
 * @version 1.2
 */
public class FleeBehavior extends AbstractStrengthSteeringBehavior {

    /**
     * Agent from whom we flee.
     */
    private Agent target;
    private Vector3f fleePosition;

    /**
     * Constructor for flee behavior.
     *
     * @param agent to whom behavior belongs
     * @param target agent from whom we flee
     */
    public FleeBehavior(Agent agent, Agent target) {
        super(agent);
        this.target = target;
    }

    /**
     * Constructor for flee behavior.
     *
     * @param agent to whom behavior belongs
     * @param target agent from whom we flee
     * @param spatial active spatial during excecution of behavior
     */
    public FleeBehavior(Agent agent, Agent target, Spatial spatial) {
        super(agent, spatial);
        this.target = target;
    }

    /**
     * Constructor for flee behavior.
     *
     * @param agent to whom behavior belongs
     * @param fleePosition position from that we flee
     */
    public FleeBehavior(Agent agent, Vector3f fleePosition) {
        super(agent);
        this.fleePosition = fleePosition;
    }

    /**
     * Constructor for flee behavior.
     *
     * @param agent to whom behavior belongs
     * @param fleePosition position from that we flee
     * @param spatial active spatial during excecution of behavior
     */
    public FleeBehavior(Agent agent, Vector3f fleePosition, Spatial spatial) {
        super(agent, spatial);
        this.fleePosition = fleePosition;
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
        } else if (this.fleePosition != null) {
            desiredVelocity = this.fleePosition.subtract(agent.getLocalTranslation());
        } else {
            return new Vector3f(); //We do not have any target or flee position
        }
        Vector3f aVelocity = this.agent.getVelocity();

        if (aVelocity == null) {
            aVelocity = new Vector3f();
        }

        return desiredVelocity.subtract(aVelocity).negate();
    }

    /**
     * Get agent from whom we flee.
     *
     * @return agent
     */
    public Agent getTarget() {
        return target;
    }

    /**
     * Setting agent from whom we flee.
     *
     * @param target
     */
    public void setTarget(Agent target) {
        this.target = target;
        this.fleePosition = null;
    }

    public Vector3f getFleePosition() {
        return this.fleePosition;
    }

    public void setFleePosition(Vector3f fleePosition) {
        this.fleePosition = fleePosition;
        this.target = null;
    }
}
