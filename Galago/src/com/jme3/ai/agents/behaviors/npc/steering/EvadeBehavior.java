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
 * Evasion is analogous to pursuit, except that flee is used to steer away from
 * the predicted future position of the target character.
 *
 * @author Jesús Martín Berlanga
 * @version 1.2.1
 */
public class EvadeBehavior extends FleeBehavior {

    /**
     * @throws TargetNotFoundException If target is null
     * @see FleeBehavior#FleeBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.ai.agents.Agent)
     */
    public EvadeBehavior(Agent agent, Agent target) {
        super(agent, target);
        this.validateTarget(target);
    }

    /**
     * @throws TargetNotFoundException If target is null
     * @see FleeBehavior#FleeBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.ai.agents.Agent, com.jme3.scene.Spatial)
     */
    public EvadeBehavior(Agent agent, Agent target, Spatial spatial) {
        super(agent, target, spatial);
        this.validateTarget(target);
    }

    private void validateTarget(Agent target) {
        if (target == null) {
            throw new BehaviorExceptions.TargetNotFoundException();
        }
    }

    /**
     * @see FleeBehavior#calculateRawSteering()
     */
    @Override
    protected Vector3f calculateRawSteering() {
        Vector3f projectedLocation = this.getTarget().getPredictedPosition();

        //Return flee steering force
        Vector3f desiredVelocity = projectedLocation.subtract(agent.getLocalTranslation());
        return desiredVelocity.subtract(velocity).negate();
    }
}
