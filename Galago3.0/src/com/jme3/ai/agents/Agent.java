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
package com.jme3.ai.agents;

import com.jme3.ai.agents.behaviors.Behavior;
import com.jme3.ai.agents.behaviors.BehaviorExceptions.NullBehaviorException;
import com.jme3.ai.agents.behaviors.npc.SimpleMainBehavior;
import com.jme3.ai.agents.behaviors.npc.steering.SteeringExceptions;
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.scene.Spatial;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 * Class that represents Agent.
 *
 * @author Jesús Martín Berlanga
 * @author Tihomir Radosavljević
 * @version 1.7.4
 */
public class Agent<T> extends GameEntity {

    /**
     * Class that enables you to add all variable you need for your agent.
     */
    private T model;
    /**
     * Unique name of Agent.
     */
    private String name;
    /**
     * Name of team. Primarily used for enabling friendly fire.
     */
    private Team team;
    /**
     * Main behaviour of Agent. Behavior that will be active while his alive.
     */
    private Behavior mainBehavior;

    public Agent() {
    }

    /**
     * @param name name of agent
     */
    public Agent(String name) {
        this.name = name;
    }

    /**
     * @param spatial spatial that will agent have durring game
     */
    public Agent(Spatial spatial) {
        this.spatial = spatial;
    }

    /**
     * @param name name of agent
     * @param spatial spatial that will agent have durring game
     */
    public Agent(String name, Spatial spatial) {
        this.name = name;
        this.spatial = spatial;
    }

    /**
     * @return main behavior of agent
     */
    public Behavior getMainBehavior() {
        return mainBehavior;
    }

    /**
     * Setting main behavior to agent. For more how should main behavior look
     * like:
     *
     * @see SimpleMainBehavior
     * @param mainBehavior
     */
    public void setMainBehavior(Behavior mainBehavior) {
        this.mainBehavior = mainBehavior;
        this.mainBehavior.setEnabled(false);
    }

    /**
     * @return unique name/id of agent
     */
    public String getName() {
        return name;
    }

    /**
     * Method for starting agent.
     *
     * @see Agent#enabled
     */
    public void start() {
        enabled = true;
        if (mainBehavior == null) {
            throw new NullBehaviorException("Agent " + name + " does not have set main behavior.");
        }
        mainBehavior.setEnabled(true);
    }

    /**
     * Method for stoping agent. Note: It will not remove spatial, it will just
     * stop agent from acting.
     *
     * @see Agent#enabled
     */
    public void stop() {
        enabled = false;
        mainBehavior.setEnabled(false);
    }

    /**
     * @return model of agent
     */
    public T getModel() {
        return model;
    }

    /**
     * @param model of agent
     */
    public void setModel(T model) {
        this.model = model;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (mainBehavior != null) {
            mainBehavior.update(tpf);
        }
    }

    /**
     * @return team in which agent belongs
     */
    public Team getTeam() {
        return team;
    }

    /**
     * @param team in which agent belongs
     */
    public void setTeam(Team team) {
        this.team = team;
    }

    /**
     * Check if this agent is in same team as another agent.
     *
     * @param agent
     * @return true if they are in same team, false otherwise
     */
    public boolean isSameTeam(Agent agent) {
        if (team == null || agent.getTeam() == null) {
            return false;
        }
        return team.equals(agent.getTeam());
    }


    /**
     * Check if this agent is considered in the same "neighborhood" in relation
     * with another agent. <br> <br>
     *
     * If the distance is lower than minDistance It is definitely considered in
     * the same neighborhood. <br> <br>
     *
     * If the distance is higher than maxDistance It is defenitely not
     * considered in the same neighborhood. <br> <br>
     *
     * If the distance is inside [minDistance. maxDistance] It is considered in
     * the same neighborhood if the forwardness is higher than "1 -
     * sinMaxAngle".
     *
     * @param GameEntity The other agent
     * @param minDistance Min. distance to be in the same "neighborhood"
     * @param maxDistance Max. distance to be in the same "neighborhood"
     * @param maxAngle Max angle in radians
     *
     * @throws SteeringExceptions.NegativeValueException If minDistance or
     * maxDistance is lower than 0
     *
     * @return If this agent is in the same "neighborhood" in relation with
     * another agent.
     */
    public boolean inBoidNeighborhood(GameEntity neighbour, float minDistance, float maxDistance, float maxAngle) {
        if (minDistance < 0) {
            throw new SteeringExceptions.NegativeValueException("The min distance can not be negative.", minDistance);
        } else if (maxDistance < 0) {
            throw new SteeringExceptions.NegativeValueException("The max distance can not be negative.", maxDistance);
        }
        boolean isInBoidNeighborhood;
        if (this == neighbour) {
            isInBoidNeighborhood = false;
        } else {
            float distanceSquared = distanceSquaredRelativeToGameEntity(neighbour);
            // definitely in neighborhood if inside minDistance sphere
            if (distanceSquared < (minDistance * minDistance)) {
                isInBoidNeighborhood = true;
            } // definitely not in neighborhood if outside maxDistance sphere
            else if (distanceSquared > maxDistance * maxDistance) {
                isInBoidNeighborhood = false;
            } // otherwise, test angular offset from forward axis.
            else {
                if (this.getAcceleration() != null) {
                    Vector3f unitOffset = this.offset(neighbour).divide(distanceSquared);
                    float forwardness = this.forwardness(unitOffset);
                    isInBoidNeighborhood = forwardness > FastMath.cos(maxAngle);
                } else {
                    isInBoidNeighborhood = false;
                }
            }
        }

        return isInBoidNeighborhood;
    }

    /**
     * Given two vehicles, based on their current positions and velocities,
     * determine the time until nearest approach.
     *
     * @param gameEntity Other gameEntity
     * @return The time until nearest approach
     */
    public float predictNearestApproachTime(GameEntity gameEntity) {
        Vector3f agentVelocity = velocity;
        Vector3f otherVelocity = gameEntity.getVelocity();

        if (agentVelocity == null) {
            agentVelocity = new Vector3f();
        }

        if (otherVelocity == null) {
            otherVelocity = new Vector3f();
        }

        /* "imagine we are at the origin with no velocity,
         compute the relative velocity of the other vehicle" */
        Vector3f relVel = otherVelocity.subtract(agentVelocity);
        float relSpeed = relVel.length();

        /* "Now consider the path of the other vehicle in this relative
         space, a line defined by the relative position and velocity.
         The distance from the origin (our vehicle) to that line is
         the nearest approach." */

        // "Take the unit tangent along the other vehicle's path"
        Vector3f relTangent = relVel.divide(relSpeed);

        /* "find distance from its path to origin (compute offset from
         other to us, find length of projection onto path)" */
        Vector3f offset = gameEntity.offset(this);
        float projection = relTangent.dot(offset);

        return projection / relSpeed;
    }

    /**
     * Given the time until nearest approach (predictNearestApproachTime)
     * determine position of each vehicle at that time, and the distance between
     * them.
     *
     * @param agent Other agent
     * @param time The time until nearest approach
     * @return The time until nearest approach
     *
     * @see Agent#predictNearestApproachTime(com.jme3.ai.agents.Agent)
     */
    public float computeNearestApproachPositions(Agent agent, float time) {
        Vector3f agentVelocity = velocity;
        Vector3f otherVelocity = agent.getVelocity();

        if (agentVelocity == null) {
            agentVelocity = new Vector3f();
        }

        if (otherVelocity == null) {
            otherVelocity = new Vector3f();
        }

        Vector3f myTravel = agentVelocity.mult(time);
        Vector3f otherTravel = otherVelocity.mult(time);

        return myTravel.distance(otherTravel);
    }

    /**
     * Given the time until nearest approach (predictNearestApproachTime)
     * determine position of each vehicle at that time, and the distance between
     * them. <br> <br>
     *
     * Anotates the positions at nearest approach in the given vectors.
     *
     * @param gameEntity Other gameEntity
     * @param time The time until nearest approach
     * @param ourPositionAtNearestApproach Pointer to a vector, This bector will
     * be changed to our position at nearest approach
     * @param hisPositionAtNearestApproach Pointer to a vector, This bector will
     * be changed to other position at nearest approach
     *
     * @return The time until nearest approach
     *
     * @see Agent#predictNearestApproachTime(com.jme3.ai.agents.Agent)
     */
    public float computeNearestApproachPositions(GameEntity gameEntity, float time, Vector3f ourPositionAtNearestApproach, Vector3f hisPositionAtNearestApproach) {
        Vector3f agentVelocity = this.getVelocity();
        Vector3f otherVelocity = gameEntity.getVelocity();

        if (agentVelocity == null) {
            agentVelocity = new Vector3f();
        }

        if (otherVelocity == null) {
            otherVelocity = new Vector3f();
        }

        Vector3f myTravel = agentVelocity.mult(time);
        Vector3f otherTravel = otherVelocity.mult(time);

        //annotation
        ourPositionAtNearestApproach.set(myTravel);
        hisPositionAtNearestApproach.set(otherTravel);

        return myTravel.distance(otherTravel);
    }

    @Override
    public String toString() {
        return "Agent{" + "name=" + name + ", id=" + id + '}';
    }
}