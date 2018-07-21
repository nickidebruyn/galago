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
import com.jme3.ai.agents.behaviors.npc.steering.SteeringExceptions.IllegalIntervalException;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * Wander is a type of random steering. This behavior retain steering direction
 * state and make small random displacements to it each frame. Thus at one frame
 * the character may be turning up and to the right, and on the next frame will
 * still be turning in almost the same direction.
 *
 * @see SimpleWanderBehavior
 *
 * @author Jesús Martín Berlanga
 * @version 2.0
 */
public class RelativeWanderBehavior extends SimpleWanderBehavior {

    private float relativeFactor;
    private Vector3f lastSteer;
    
    /**
     * @param relativeFactor How much should differ each new wander force ? A
     * value near to 0 means that each new force must differ slightly from the
     * previous one. 1 means that the behaviour will work as a simple wander
     * behavior
     * @see SimpleWanderBehavior
     * @see SimpleWanderBehavior#SimpleWanderBehavior(com.jme3.ai.agents.Agent,
     * float, float, float) 
     * @throws IllegalIntervalException If relative factor is not contained in 
     * the [0, 1] interval.
     */
    public RelativeWanderBehavior(Agent agent, float rX, float rY, float rZ, float relativeFactor) {
        super(agent, rX, rY, rZ);
        this.validateRelativeFactor(relativeFactor);
        this.relativeFactor = relativeFactor;
        this.currentSteer = this.newRandomSteer();
    }

    /**
     * @see
     * RelativeWanderBehavior#RelativeWanderBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.math.Vector3f, com.jme3.math.Vector3f, float)
     * @see SimpleWanderBehavior#SimpleWanderBehavior(com.jme3.ai.agents.Agent, 
     * float, float, float, com.jme3.scene.Spatial) 
     */
    public RelativeWanderBehavior(Agent agent, float rX, float rY, float rZ, float relativeFactor, Spatial spatial) {
        super(agent, rX, rY, rZ);
        this.validateRelativeFactor(relativeFactor);
        this.relativeFactor = relativeFactor;
        this.currentSteer = this.newRandomSteer();
    }

    private void validateRelativeFactor(float factor) {
        if(factor < 0 || factor > 1)
            throw new IllegalIntervalException("relative", factor);
    }
    
    /**
     * @see SimpleWanderBehavior#changeSteer(float)
     */
    @Override
    protected void changeSteer(float tpf) {
        time -= tpf;
        if (time <= 0) {
            lastSteer = currentSteer;
            Vector3f randomSteer = this.newRandomSteer();
            randomSteer.setX(lastSteer.x*(1-relativeFactor) + randomSteer.getX()*relativeFactor);
            randomSteer.setY(lastSteer.y*(1-relativeFactor) + randomSteer.getY()*relativeFactor);
            randomSteer.setZ(lastSteer.z*(1-relativeFactor) + randomSteer.getZ()*relativeFactor);
            currentSteer = randomSteer;
            time = timeInterval;
        }
    }
        
    /**
     * @param relativeFactor How much should differ each new wander force ? A
     * value near to 0 means that each new force must differ slightly from the
     * previous one. 1 means that the behaviour will work as a simple wander
     * behavior
     * @throws IllegalIntervalException If relative factor is not contained in 
     * the [0, 1] interval.
     */
    public void setRelativeFactor(float factor) {
        this.validateRelativeFactor(factor);
        this.relativeFactor = factor;
    }
    /**
     * @return relativeFactor How much differ each new wander force. A
     * value near to 0 means that each new force differ slightly from the
     * previous one. 1 means that the behaviour works as a simple wander
     * behavior.
     */
    public float getRelativeFactor() {
        return this.relativeFactor;
    }
}