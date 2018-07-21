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
import java.util.ArrayList;
import java.util.List;

/**
 * "Explore goal is to exhaustively cover a region of space". <br><br>
 *
 * This is the simplest implementation of explore behavior.
 *
 * @author Jesús Martín Berlanga
 * @version 1.0.1
 */
public class BoxExploreBehavior extends AbstractStrengthSteeringBehavior {

    private Vector3f zeroCorner;
    private float boxWidthX;
    private float boxWidthZ;
    private float boxHeight;
    private float subdivisionDistance;
    private boolean isFinished = false;
    private List<Vector3f> targets = new ArrayList<Vector3f>();

    /**
     * @param boxCenter Center position of the box
     * @param boxWidthX Box x size
     * @param boxWidthZ Box z size
     * @param boxHeight Box y size
     * @param subdivisionDistance Distance between each subdivision. A lower
     * subdivision distance means that the agent will explore the region more
     * exhaustively.
     *
     * @throws SteeringExceptions.NegativeValueException If subdivisionDistance
     * is lower or equals to 0 or if boxWidthX, boxWidthZ or boxHeight is lower
     * than 0
     *
     * @see
     * AbstractStrengthSteeringBehavior#AbstractStrengthSteeringBehavior(com.jme3.ai.agents.Agent)
     */
    public BoxExploreBehavior(Agent agent, Vector3f boxCenter, float boxWidthX, float boxWidthZ, float boxHeight, float subdivisionDistance) {
        super(agent);
        this.construct(boxCenter, boxWidthX, boxWidthZ, boxHeight, subdivisionDistance);
    }

    /**
     * @see BoxExploreBehavior#BoxExploreBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.math.Vector3f, float, float, float, float)
     * @see
     * AbstractStrengthSteeringBehavior#AbstractStrengthSteeringBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.scene.Spatial)
     */
    public BoxExploreBehavior(Agent agent, Vector3f boxCenter, float boxWidthX, float boxWidthZ, float boxHeight, float subdivisionDistance, Spatial spatial) {
        super(agent, spatial);
        this.construct(boxCenter, boxWidthX, boxWidthZ, boxHeight, subdivisionDistance);
    }

    private void construct(Vector3f boxCenter, float boxWidthX, float boxWidthZ, float boxHeight, float subdivisionDistance) {
        if (boxWidthX < 0 || boxWidthZ < 0 || boxHeight < 0) {
            throw new SteeringExceptions.NegativeValueException("Box width, depth and height must be positive.");
        } else if (subdivisionDistance <= 0) {
            throw new SteeringExceptions.NegativeValueException("The subdivision distance must be higher than 0.");
        }

        this.boxWidthX = boxWidthX;
        this.boxWidthZ = boxWidthZ;
        this.boxHeight = boxHeight;
        this.subdivisionDistance = subdivisionDistance;
        this.zeroCorner = boxCenter.subtract(new Vector3f(this.boxHeight / 2, this.boxHeight / 2, this.boxWidthZ / 2));
        this.addNewTargets();
    }

    /**
     * Should be used after cleaning target list
     */
    protected void addNewTargets() {
        //Vertical subdivisions
        //1st Horizontal subdivisions in each vertical subdivision
        for (float i = 0; i < this.boxHeight; i += this.subdivisionDistance) {
            //2nd Horizontal subdivisions
            for (float j = 0; j < this.boxWidthX; j += this.subdivisionDistance) {
                for (float k = 0; k < this.boxWidthZ; k += this.subdivisionDistance) {
                    this.targets.add(this.zeroCorner.add(new Vector3f(j, i, k)));
                }
            }
        }
    }

    /**
     * @see AbstractSteeringBehavior#calculateSteering()
     */
    @Override
    protected Vector3f calculateRawSteering() {
        Vector3f steer = Vector3f.ZERO;

        if (!isFinished) {
            Vector3f closest = null;
            float closestDistance = Float.POSITIVE_INFINITY;

            for (int i = 0; i < this.targets.size(); i++) {
                Vector3f target = this.targets.get(i);
                float distanceFromTarget = this.agent.offset(target).length();

                if (distanceFromTarget < this.subdivisionDistance / 2) {
                    this.targets.remove(i);
                } else if (distanceFromTarget < closestDistance) {
                    closest = target;
                    closestDistance = distanceFromTarget;
                }
            }

            if (closest != null) {
                SeekBehavior seek = new SeekBehavior(this.agent, closest);
                steer = seek.calculateRawSteering();
            } else {
                isFinished = true;
            }
        }
        return steer;
    }

    public boolean getIsFinished() {
        return this.isFinished;
    }

    /**
     * The agent will have to explore the region again and all the progress will
     * be lost.
     */
    protected void resetExplore() {
        this.targets.clear();
        this.addNewTargets();
        this.isFinished = false;
    }
}