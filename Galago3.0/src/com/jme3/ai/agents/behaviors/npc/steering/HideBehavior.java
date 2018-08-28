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
import com.jme3.ai.agents.behaviors.BehaviorExceptions;
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.List;

/**
 * Hide behavior involves identifying a target location which is on the opposite
 * side of an obstacle from the opponent, and steering toward it using seek.
 * <br><br>
 *
 * First of all we check which is the nearest obstacle with a radius equal or
 * lower than the agent, then we hide behind it.
 *
 * @author Jesús Martín Berlanga
 * @version 1.0.1
 */
public class HideBehavior extends AbstractStrengthSteeringBehavior {

    private float separationFromObstacle;
    private Agent target;
    private List<GameEntity> obstacles;

    /**
     * @param obstacles Obstacles that this agent will use to hide from the
     * target
     * @param separationFromObstacle Distance from the obstacle surface that
     * this agent will maintain
     *
     * @throws SteeringExceptions.NegativeValueException If
     * separationFromObstacle is lower than 0
     * @throws BehaviorExceptions.TargetNotFoundException If target is null
     *
     * @see
     * AbstractSteeringBehaviour#AbstractSteeringBehaviour(com.jme3.ai.agents.Agent)
     */
    public HideBehavior(Agent agent, Agent target, List<GameEntity> obstacles, float separationFromObstacle) {
        super(agent);
        this.validateTarget(target);
        this.validateSeparationFromObstacle(separationFromObstacle);
        this.target = target;
        this.obstacles = obstacles;
        this.separationFromObstacle = separationFromObstacle;
    }

    /**
     * @see HideBehavior#HideBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.ai.agents.Agent, java.util.List, float)
     * @see
     * AbstractSteeringBehavior#AbstractSteeringBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.scene.Spatial)
     */
    public HideBehavior(Agent agent, Agent target, List<GameEntity> obstacles, float separationFromObstacle, Spatial spatial) {
        super(agent, spatial);
        this.validateTarget(target);
        this.validateSeparationFromObstacle(separationFromObstacle);
        this.target = target;
        this.obstacles = obstacles;
        this.separationFromObstacle = separationFromObstacle;
    }

    private void validateTarget(Agent target) {
        if (target == null) {
            throw new BehaviorExceptions.TargetNotFoundException();
        }
    }

    private void validateSeparationFromObstacle(float separationFromObstacle) {
        if (separationFromObstacle < 0) {
            throw new SteeringExceptions.NegativeValueException("The separation distance from the obstacle can not be negative.", separationFromObstacle);
        }
    }

    /**
     * @see AbstractStrengthSteeringBehavior#calculateRawSteering()
     */
    @Override
    protected Vector3f calculateRawSteering() {
        Vector3f steer = Vector3f.ZERO;

        GameEntity closestObstacle = null;
        float closestDistanceFromAgent = Float.POSITIVE_INFINITY;

        for (GameEntity obstacle : this.obstacles) {
            if (obstacle != this.agent) {
                float distanceFromAgent = this.agent.distanceRelativeToGameEntity(obstacle);

                if (distanceFromAgent < closestDistanceFromAgent && obstacle.getRadius() >= this.agent.getRadius()) {
                    closestObstacle = obstacle;
                    closestDistanceFromAgent = distanceFromAgent;
                }
            }
        }

        if (closestObstacle != null && this.agent.distanceRelativeToGameEntity(closestObstacle) > closestObstacle.getRadius()) {
            Vector3f targetToObstacleOffset = this.target.offset(closestObstacle);
            Vector3f seekPos = this.target.getLocalTranslation().add(targetToObstacleOffset).add(
                    targetToObstacleOffset.normalize().mult(this.separationFromObstacle));

            SeekBehavior seek = new SeekBehavior(this.agent, seekPos);
            return seek.calculateRawSteering();
        }

        return steer;
    }

    public void setObstacles(List<GameEntity> obstacles) {
        this.obstacles = obstacles;
    }
}