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
 * steering force inside an area each frame, but this produces rather 
 * uninteresting motion. It is 'twitchy' and produces no sustained turns. 
 * <br><br>
 *
 * This is done by same calculation as seek behaviour, but difference is that
 * target for this behavior are random positions that changes durring time.
 * 
 * @see AbstractWander
 * 
 * @author Tihomir Radosavljević
 * - Original wander idea by Thiomir
 * @author Jesús Martín Berlanga
 * - Wander redesign by Jesús: Clearer and simpler version
 *
 * @version 2.0
 */
public class WanderAreaBehavior extends AbstractWanderBehavior {

    /**
     * Position of target.
     */
    protected Vector3f targetPosition;
    /**
     * Current time.
     */
    protected float time = -1;
    /**
     * Area in which agent will wander.
     */
    protected Vector3f center = Vector3f.ZERO;
    /**
     * Offset from the center of the area
     */
    protected class Offset {
        public float x = Integer.MAX_VALUE;
        public float y = Integer.MAX_VALUE;
        public float z = Integer.MAX_VALUE;
    }
    /** 
     * @see Offset
     */
    protected Offset offset = new Offset();

    /**
     * Constructor for wander behavior.
     *
     * @param agent to whom behavior belongs
     */
    public WanderAreaBehavior(Agent agent) {
        super(agent);
        velocity = new Vector3f();
        timeInterval = 2f;
    }

    /**
     * Constructor for wander behavior.
     *
     * @param agent to whom behavior belongs
     * @param spatial active spatial during excecution of behavior
     */
    public WanderAreaBehavior(Agent agent, Spatial spatial) {
        super(agent, spatial);
        velocity = new Vector3f();
        timeInterval = 2f;
    }

    /**
     * Calculate steering vector.
     *
     * @return steering vector
     */
    @Override
    protected Vector3f calculateRawSteering() {
        changeTargetPosition(timePerFrame);
        Vector3f desiredVelocity = targetPosition.subtract(agent.getLocalTranslation()).normalize().mult(agent.getMoveSpeed());
        desiredVelocity.subtract(velocity);
        return desiredVelocity;
    }

    /**
     * Metod for changing target position.
     *
     * @param tpf time per frame
     */
    protected void changeTargetPosition(float tpf) {
        time -= tpf;
        if (time <= 0) {
            float rOffsetX = (FastMath.nextRandomFloat() - 0.5f)*2 * this.offset.x;
            float rOffsetY = (FastMath.nextRandomFloat() - 0.5f)*2 * this.offset.y;
            float rOffsetZ = (FastMath.nextRandomFloat() - 0.5f)*2 * this.offset.z; 
            targetPosition = center.add(rOffsetX, rOffsetY, rOffsetZ);
            time = timeInterval;
        }
    }
   
    /**
     * Setting area for wander.
     *
     * @param center center of the area
     * @param offsetX max random offset from center
     * @param offsetY max random offset from center
     * @param offsetZ max random offset from center
     */
    public void setArea(Vector3f center, float offsetX, float offsetY, float offsetZ) {
        this.center = center;
        this.offset.x = offsetX;
        this.offset.y = offsetY;
        this.offset.z = offsetZ;
    }
    
    /**
     * Setting area for wander.
     *
     * @param center center of the area
     * @param offset max random offset from center
     */
    public void setArea(Vector3f center, Vector3f offset) {
        this.center = center;
        this.offset.x = offset.x;
        this.offset.y = offset.y;
        this.offset.z = offset.z;
    }
}