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
 * Arrival behavior is identical to seek while the character is far from its
 * target. But instead of moving through the target at full speed, this behavior
 * causes the character to slow down as it approaches the target, eventually
 * slowing to a stop coincident with the target.
 *
 * @see SeekBehavior
 *
 * @author Jesús Martín Berlanga
 * @version 1.2.1
 */
public class ArriveBehavior extends SeekBehavior {

    /**
     * Distance of targetPosition that is acceptable.
     */
    private static final float ERROR_FACTOR = 0.001f;
    /**
     * Rate of negative acceleration.
     */
    private float slowingDistance;

    /**
     * The slowingDistance is (0.1 * distance betwen agents) by default.
     *
     * @see SeekBehavior#SeekBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.ai.agents.Agent)
     */
    public ArriveBehavior(Agent agent, Agent target) {
        super(agent, target);
        this.slowingDistance = agent.distanceRelativeToGameEntity(target) * 0.25f;
    }

    /**
     * The slowingDistance is (0.1 * distance betwen agents) by default.
     *
     * @see SeekBehavior#SeekBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.ai.agents.Agent, com.jme3.scene.Spatial)
     */
    public ArriveBehavior(Agent agent, Agent target, Spatial spatial) {
        super(agent, target, spatial);
        this.slowingDistance = agent.distanceRelativeToGameEntity(target) * 0.25f;
    }

    /**
     * The slowingDistance is (0.1 * distance betwen agents) by default.
     *
     * @see SeekBehavior#SeekBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.ai.agents.Agent)
     */
    public ArriveBehavior(Agent agent, Vector3f seekingPosition) {
        super(agent, seekingPosition);
        this.slowingDistance = agent.getLocalTranslation().subtract(seekingPosition).length() * 0.1f;
    }

    /**
     * The slowingDistance is (0.1 * distance betwen agents) by default.
     *
     * @see SeekBehavior#SeekBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.ai.agents.Agent, com.jme3.scene.Spatial)
     */
    public ArriveBehavior(Agent agent, Vector3f seekingPosition, Spatial spatial) {
        super(agent, seekingPosition, spatial);
        this.slowingDistance = agent.getLocalTranslation().subtract(seekingPosition).length() * 0.1f;
    }

    /**
     * @param slowingDistance Distance where the agent will start slowing
     * @throws NegativeSlowingDistanceException If slowingDistance is lower than
     * 0
     * @see ArriveBehavior#ArriveBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.ai.agents.Agent)
     */
    public ArriveBehavior(Agent agent, Agent target, float slowingDistance) {
        super(agent, target);
        this.validateSlowingDistance(slowingDistance);
        this.slowingDistance = slowingDistance;
    }

    /**
     * @see ArriveBehavior#ArriveBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.ai.agents.Agent, float)
     * @see
     * AbstractSteeringBehaviour#AbstractSteeringBehaviour(com.jme3.ai.agents.Agent,
     * com.jme3.scene.Spatial)
     */
    public ArriveBehavior(Agent agent, Agent target, float slowingDistance, Spatial spatial) {
        super(agent, target, spatial);
        this.validateSlowingDistance(slowingDistance);
        this.slowingDistance = slowingDistance;
    }

    /**
     * @see ArriveBehavior#ArriveBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.ai.agents.Agent, float)
     * @see ArriveBehavior#ArriveBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.math.Vector3f)
     */
    public ArriveBehavior(Agent agent, Vector3f seekingPosition, float slowingDistance) {
        super(agent, seekingPosition);
        this.validateSlowingDistance(slowingDistance);
        this.slowingDistance = slowingDistance;
    }

    /**
     * @see ArriveBehavior#ArriveBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.ai.agents.Agent, float)
     * @see ArriveBehavior#ArriveBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.math.Vector3f, com.jme3.scene.Spatial)
     */
    public ArriveBehavior(Agent agent, Vector3f seekingPosition, float slowingDistance, Spatial spatial) {
        super(agent, seekingPosition, spatial);
        this.validateSlowingDistance(slowingDistance);
        this.slowingDistance = slowingDistance;
    }

    private void validateSlowingDistance(float slowingDistance) {
        if (slowingDistance < 0) {
            throw new SteeringExceptions.NegativeValueException("The slowing distance value can not be negative.", slowingDistance);
        }
    }

    /**
     * Calculate steering vector.
     *
     * @return steering vector
     *
     * @see AbstractStrengthSteeringBehaviour#calculateRawSteering()
     * @see SeekBehavior#calculateRawSteering()
     */
    @Override
    protected Vector3f calculateRawSteering() {
        float distanceToTarget;
        float radious = 0;

        if (this.getTarget() != null) {
            distanceToTarget = this.agent.distanceRelativeToGameEntity(this.getTarget());
            radious = this.getTarget().getRadius();
        } else if (this.getSeekingPosition() != null) {
            distanceToTarget = this.agent.getLocalTranslation().subtract(this.getSeekingPosition()).length();
        } else {
            return new Vector3f(); //We dont have any target or location to arrive 
        }
        if (distanceToTarget < radious + ArriveBehavior.ERROR_FACTOR) {
            this.setBrakingFactor(0);
        } else if (distanceToTarget < this.slowingDistance) {
            this.setBrakingFactor(distanceToTarget / this.slowingDistance);
        } else {
            this.setBrakingFactor(1);
        }

        return super.calculateRawSteering();
    }
}
