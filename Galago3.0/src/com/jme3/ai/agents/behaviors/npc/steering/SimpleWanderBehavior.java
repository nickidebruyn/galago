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

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * This behavior is based on a easy implementation that "generates random
 * steering force each frame, but this produces rather uninteresting motion. 
 * It is 'twitchy' and produces no sustained turns. <br><br>
 *
 * Steer forces changes durring time.
 * 
 * @see AbstractWander
 * 
 * @author Jesús Martín Berlanga
 * @version 1.0
 */
public class SimpleWanderBehavior extends AbstractWanderBehavior {

    /**
     * Current steer force.
     */
    protected Vector3f currentSteer = Vector3f.ZERO;
    /**
     * Current time.
     */
    protected float time = -1;
    /**
     * Random offset
     */
    protected class MaxRandSteer {
        public float x;
        public float y;
        public float z;
    }
    /** 
     * @see Offset
     */
    protected MaxRandSteer maxRandSteer = new MaxRandSteer();

    private float constantMod = -1; 
    private boolean hasConstantMod = false;
    
    /**
     * @param agent to whom behavior belongs
     * @param rX max x-random steer
     * @param rY max y-random steer
     * @param rZ max z-random steer
     */
    public SimpleWanderBehavior(Agent agent, float rX, float rY, float rZ) {
        super(agent);
        velocity = new Vector3f();
        timeInterval = 2f;
        maxRandSteer.x = rX;
        maxRandSteer.y = rY;
        maxRandSteer.z = rZ;
    }

    /**
     * @param spatial active spatial during excecution of behavior
     * @see SimpleWanderBehavior#SimpleWanderBehavior(com.jme3.ai.agents.Agent, float, float, float) 
     */
    public SimpleWanderBehavior(Agent agent, float rX, float rY, float rZ, Spatial spatial) {
        super(agent, spatial);
        velocity = new Vector3f();
        timeInterval = 2f;
        maxRandSteer.x = rX;
        maxRandSteer.y = rY;
        maxRandSteer.z = rZ;
    }

    /**
     * Calculate steering vector.
     *
     * @return steering vector
     */
    @Override
    protected Vector3f calculateRawSteering() {
        changeSteer(timePerFrame);
        
        if(getHasConstantMod())
            currentSteer.normalizeLocal().multLocal(constantMod);
        
        return currentSteer;
    }

    /**
     * Metod for changing the steer force.
     *
     * @param tpf time per frame
     */
    protected void changeSteer(float tpf) {
        time -= tpf;
        if (time <= 0) {
            currentSteer = newRandomSteer();
            time = timeInterval;
        }
    }
    
    /**
     * @return A new random steer force
     */
    protected Vector3f newRandomSteer() {
            float rX = (FastMath.nextRandomFloat() - 0.5f)*2 * maxRandSteer.x;
            float rY = (FastMath.nextRandomFloat() - 0.5f)*2 * maxRandSteer.y;
            float rZ = (FastMath.nextRandomFloat() - 0.5f)*2 * maxRandSteer.z; 
            return new Vector3f(rX, rY, rZ);
    }
      
    /**
     * Setting random steer
     *
     * @param rX max x-random steer
     * @param rY max y-random steer
     * @param rZ max z-random steer
     */
    public void setMaxRandSteer(float rX, float rY, float rZ) {
        this.maxRandSteer.x = rX;
        this.maxRandSteer.y = rY;
        this.maxRandSteer.z = rZ;
    }
    
    /**
     * The generated steer module will be a constant value.
     * If the distance is 0 or lower, there will be no constant steer module.
     * 
     * @param distance Target-Agent distance
     */
    public void setConstantMod(float value) {
        if(value > 0){
            this.constantMod = value;
            this.hasConstantMod = true;
        } else {
            this.constantMod = -1;
            this.hasConstantMod = false;            
        }
    }
    
    /**
     * @return If the generated steer module is a constant value.
     */
    public boolean getHasConstantMod() {
        return this.hasConstantMod;
    }
}