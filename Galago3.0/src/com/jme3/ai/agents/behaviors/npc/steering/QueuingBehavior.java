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
import com.jme3.ai.agents.Team;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.List;

/**
 * The queuing results from a steering behavior which produces braking
 * (deceleration) when the vehicle detects other vehicles which are: nearby, in
 * front of, and moving slower than itself. <br> <br>
 *
 * In most cases you will combine this behavior with seek, separation and/or
 * avoid. Queuing is designed to be combined with the behaviors mentioned before
 * in order to to leave a large 'room' through a narrow 'doorway': "drawn toward
 * the 'doorway' by seek behavior, avoid walls, and maintain separation from
 * each other."
 *
 * @author Jesús Martín Berlanga
 * @version 1.0.1
 */
public class QueuingBehavior extends AbstractStrengthSteeringBehavior {

    private List<Agent> neighbours;
    private float minDistance;

    /**
     * @param neighbours Queue of agents
     * @param minDistance Min. distance from center to center to consider a
     * neighbour as an obstacle
     *
     * @throws SteeringExceptions.NegativeValueException If minDistance is lower
     * than 0
     *
     * @see
     * AbstractStrengthSteeringBehavior#AbstractStrengthSteeringBehavior(com.jme3.ai.agents.Agent)
     */
    public QueuingBehavior(Agent agent, List<Agent> neighbours, float minDistance) {
        super(agent);
        this.validateMinDistance(minDistance);
        this.neighbours = neighbours;
        this.minDistance = minDistance;
    }

    /**
     * @see QueuingBehavior#QueuingBehavior(com.jme3.ai.agents.Agent,
     * java.util.List, float)
     * @see
     * AbstractStrengthSteeringBehavior#AbstractStrengthSteeringBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.scene.Spatial)
     */
    public QueuingBehavior(Agent agent, List<Agent> neighbours, float minDistance, Spatial spatial) {
        super(agent, spatial);
        this.validateMinDistance(minDistance);
        this.neighbours = neighbours;
        this.minDistance = minDistance;
    }

    private void validateMinDistance(float minDistance) {
        if (minDistance < 0) {
            throw new SteeringExceptions.NegativeValueException("The min distance from an obstacle can not be negative.", minDistance);
        }
    }

    /**
     * @see AbstractStrengthSteeringBehavior#calculateRawSteering()
     */
    @Override
    protected Vector3f calculateRawSteering() {
        Vector3f agentVelocity = this.agent.getVelocity();

        int numberObstaclesFactor = 1;
        float distanceFactor = 1;
        float velocityFactor = 1;

        if (agentVelocity != null && !agentVelocity.equals(Vector3f.ZERO)) {
            for (Agent neighbour : this.neighbours) {
                Vector3f neighVel = neighbour.getVelocity();
                float fordwardness = this.agent.forwardness(neighbour);
                float velDiff;
                float distance;

                if (neighbour != this.agent
                        && (distance = this.agent.distanceRelativeToGameEntity(neighbour)) < this.minDistance
                        && fordwardness > 0
                        && neighVel != null
                        && (velDiff = neighVel.length() - agentVelocity.length()) < 0) {
                    distanceFactor *= distance / this.minDistance;
                    velocityFactor *= -velDiff / this.agent.getMoveSpeed();
                    numberObstaclesFactor++;
                }
            }
        }

        this.setBrakingFactor((distanceFactor + velocityFactor + (1 / numberObstaclesFactor)) / 3);
        return new Vector3f();
    }

    public void setNeighbours(List<Agent> neighbours) {
        this.neighbours = neighbours;
    }

    public void setNeighboursFromTeam(Team team) {
        this.neighbours = team.getMembers();
    }
}