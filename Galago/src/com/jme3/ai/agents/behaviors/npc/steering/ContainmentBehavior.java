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
import com.jme3.ai.agents.behaviors.npc.steering.SteeringExceptions.InvalidAreaException;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * "Containment refers to motion which is restricted to remain within a certain
 * region." <br><br>
 *
 * "To implement: first predict our character's future position, if it is inside
 * the allowed region no corrective steering is necessary. Otherwise we steer
 * towards the allowed region." <br><br>
 *
 * "Examples of containment include: fish swimming in an aquarium and hockey
 * players skating within an ice rink."
 *
 * @author Jesús Martín Berlanga
 * @version 1.0.1
 */
public class ContainmentBehavior extends AbstractStrengthSteeringBehavior {

    private Node containmentArea;
    /**
     * Remember the last normal vector from the containment surface
     */
    private Vector3f lastExitSurfaceNormal;
    /*
     * Saves the Normal vector from the triangle where the agent will exit at "surfaceNormal".
     * 
     * Also saves the exit point at "exitPoint"
     */
    private Vector3f exitPoint;
    private Vector3f surfaceNormal;

    /**
     * @param containmentArea Area where the agent will be restricted
     *
     * @throws InvalidAreaException If containmentArea is null
     *
     * @see
     * AbstractStrengthSteeringBehavior#AbstractStrengthSteeringBehavior(com.jme3.ai.agents.Agent)
     */
    public ContainmentBehavior(Agent agent, Node containmentArea) {
        super(agent);
        this.validateContainmentArea(containmentArea);
        this.containmentArea = containmentArea;
    }

    /**
     * @param containmentArea Area where the agent will be restricted
     * @see
     * AbstractStrengthSteeringBehavior#AbstractStrengthSteeringBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.scene.Spatial)
     */
    public ContainmentBehavior(Agent agent, Node containmentArea, Spatial spatial) {
        super(agent, spatial);
        this.validateContainmentArea(containmentArea);
        this.containmentArea = containmentArea;
    }

    private void validateContainmentArea(Node containmentArea) {
        if (containmentArea == null) {
            throw new InvalidAreaException("The containment area can not be null.");
        } else if (containmentArea.getWorldBound() == null) {
            throw new InvalidAreaException("The containment area must be bounded.");
        }
    }

    /**
     * @see AbstractStrengthSteeringBehavior#calculateRawSteering()
     */
    @Override
    protected Vector3f calculateRawSteering() {
        Vector3f steer = new Vector3f();
        Vector3f predictedPos = this.agent.getPredictedPosition();

        //Check if the agent is outside the area
        if (!this.containmentArea.getWorldBound().contains(this.agent.getLocalTranslation())) {
            //If we know where is the point he exited, return to the area
            if (lastExitSurfaceNormal != null) {
                steer = this.surfaceNormal.mult(this.exitPoint.distance(predictedPos));
            } else {
                steer = this.containmentArea.getWorldBound().getCenter().subtract(this.agent.getLocalTranslation());
            }
        } else {
            //Check if correction is necessary
            if (!this.containmentArea.getWorldBound().contains(predictedPos)) {
                this.processExitSurface();

                if (exitPoint != null && surfaceNormal != null) {
                    //Check If the normal vector will mantain the agent inside the area, 
                    //if not flip it
                    if (this.surfaceNormal.angleBetween(this.agent.getVelocity()) < FastMath.PI / 2) {
                        this.surfaceNormal = this.surfaceNormal.negate();
                    }

                    steer = this.surfaceNormal.mult(this.exitPoint.distance(predictedPos));
                }
            }
        }
        return steer;
    }

    protected void processExitSurface() {
        this.surfaceNormal = null;
        this.exitPoint = null;

        CollisionResults results = new CollisionResults();

        Vector3f vel = this.agent.getVelocity();
        if (vel == null) {
            vel = new Vector3f();
        }

        Ray ray = new Ray(this.agent.getLocalTranslation(), vel);
        this.containmentArea.collideWith(ray, results);

        CollisionResult closestCollision = results.getClosestCollision();

        if (closestCollision != null) {
            this.surfaceNormal = closestCollision.getContactNormal();
            //closestCollision.getTriangle(new Triangle()).getNormal();
            this.exitPoint = closestCollision.getContactPoint();
        }
    }
}