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
import com.jme3.ai.agents.behaviors.npc.steering.MoveBehavior;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * Simple move behavior for NPC. Agent should move to targeted position or to
 * moveDirection. If both are added then agent will move to targeted position.
 * <br>Warrning:<br>
 * Agent sometimes will never move exactly to targeted position if moveSpeed is
 * too high so add appropriate distance error.
 *
 * @see Agent#setMoveSpeed(float)
 * @see MoveBehavior
 *
 * @author Tihomir Radosavljević
 * @version 1.0.1
 */
public class SimpleMoveBehavior extends Behavior {

    /**
     * Targeted position.
     */
    protected Vector3f targetPosition;
    /**
     * Move direction of agent.
     */
    protected Vector3f moveDirection;
    /**
     * Distance of targetPosition that is acceptable.
     */
    protected float distanceError;

    public SimpleMoveBehavior(Agent agent) {
        super(agent);
        distanceError = 0;
    }

    public SimpleMoveBehavior(Agent agent, Spatial spatial) {
        super(agent, spatial);
        distanceError = 0;
    }

    @Override
    protected void controlUpdate(float tpf) {
        //if there is target position where agent should move
        if (targetPosition != null) {
            if (agent.getLocalTranslation().distance(targetPosition) <= distanceError) {
                targetPosition = null;
                moveDirection = null;
                enabled = false;
                return;
            }
            moveDirection = targetPosition.subtract(agent.getLocalTranslation()).normalize();
        }
        //if there is movement direction in which agent should move
        if (moveDirection != null) {
            agent.getSpatial().move(moveDirection.mult(agent.getMoveSpeed() * tpf));
            rotateAgent(tpf);
        }
    }

    /**
     * @return position of target
     */
    public Vector3f getTargetPosition() {
        return targetPosition;
    }

    /**
     * @param targetPosition position of target
     */
    public void setTargetPosition(Vector3f targetPosition) {
        this.targetPosition = targetPosition;
    }

    /**
     *
     * @return movement vector
     */
    public Vector3f getMoveDirection() {
        return moveDirection;
    }

    /**
     *
     * @param moveDirection movement vector
     */
    public void setMoveDirection(Vector3f moveDirection) {
        this.moveDirection = moveDirection.normalize();
    }

    /**
     *
     * @return allowed distance error
     */
    public float getDistanceError() {
        return distanceError;
    }

    /**
     *
     * @param distanceError allowed distance error
     */
    public void setDistanceError(float distanceError) {
        this.distanceError = distanceError;
    }

    /**
     * Method for rotating agent in direction of velocity of agent.
     *
     * @param tpf time per frame
     */
    public void rotateAgent(float tpf) {
        Quaternion q = new Quaternion();
        q.lookAt(moveDirection, new Vector3f(0, 1, 0));
        agent.getLocalRotation().slerp(q, agent.getRotationSpeed() * tpf);
    }
}
