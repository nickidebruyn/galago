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
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * Pursuit is similar to seek except that the quarry (target) is another moving
 * character. Effective pursuit requires a prediction of the target’s future
 * position.
 *
 * @author Jesús Martín Berlanga
 * @version 1.2
 */
public class PursuitBehavior extends SeekBehavior {

    /**
     * @see SeekBehavior#SeekBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.ai.agents.Agent)
     * @throws BehaviorExceptions.TargetNotFoundException If target is null
     */
    public PursuitBehavior(Agent agent, Agent target) {
        super(agent, target);
        this.validateTarget(target);
    }

    /**
     * @see SeekBehavior#SeekBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.ai.agents.Agent, com.jme3.scene.Spatial)
     * @throws BehaviorExceptions.TargetNotFoundException If target is null
     */
    public PursuitBehavior(Agent agent, Agent target, Spatial spatial) {
        super(agent, target, spatial);
        this.validateTarget(target);
    }

    private void validateTarget(Agent target) {
        if (target == null) {
            throw new BehaviorExceptions.TargetNotFoundException();
        }
    }

    /**
     * @see AbstractStrengthSteeringBehavior#calculateRawSteering()
     */
    @Override
    protected Vector3f calculateRawSteering() {
        //See how far ahead we need to leed
        Vector3f projectedLocation = this.getTarget().getPredictedPosition();

        //Seek behaviour
        Vector3f desierdVel = projectedLocation.subtract(this.agent.getLocalTranslation());

        Vector3f aVelocity = this.agent.getVelocity();

        if (aVelocity == null) {
            aVelocity = new Vector3f();
        }

        return desierdVel.subtract(aVelocity);
    }
}
