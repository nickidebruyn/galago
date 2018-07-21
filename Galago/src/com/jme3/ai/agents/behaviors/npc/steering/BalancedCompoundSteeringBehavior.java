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
 * Each force generated inside this container is reduced in relation with a
 * proportion factor. <br> <br>
 *
 * "Proportion factor" = "Partial Force" / "Total container force" <br><br>
 *
 * The balace is activated by default. This balance can be "desactivated" with
 * setStrengthIsBalanced(false).
 *
 * @see CompoundSteeringBehavior
 *
 * @author Jesús Martín Berlanga
 * @version 2.0.1
 */
public class BalancedCompoundSteeringBehavior extends CompoundSteeringBehavior {

    private boolean strengthIsBalanced;
    private Vector3f totalForce;
    private List<Vector3f> partialForces;
    private int numberOfPartialForcesAlreadyCalculated = 0;
    private int numberOfBehaviors;

    /**
     * @see
     * AbstractSteeringBehavior#AbstractSteeringBehavior(com.jme3.ai.agents.Agent)
     */
    public BalancedCompoundSteeringBehavior(Agent agent) {
        super(agent);
        this.strengthIsBalanced = true;
        partialForces = new ArrayList<Vector3f>();
    }

    /**
     * @see
     * AbstractSteeringBehavior#AbstractSteeringBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.scene.Spatial)
     */
    public BalancedCompoundSteeringBehavior(Agent agent, Spatial spatial) {
        super(agent, spatial);
        this.strengthIsBalanced = true;
        partialForces = new ArrayList<Vector3f>();
    }

    /**
     * @see
     * BalancedCompoundSteeringBehavior#addSteerBehavior(com.jme3.ai.agents.behaviors.npc.steering.AbstractSteeringBehavior)
     */
    @Override
    public void addSteerBehavior(AbstractSteeringBehavior behavior) {
        super.addSteerBehavior(behavior);
        this.numberOfBehaviors++;
    }

    /**
     * Turn on or off the balance. The balance is activated by default.
     */
    public void setStrengthIsBalanced(boolean strengthIsBalanced) {
        this.strengthIsBalanced = strengthIsBalanced;
    }

    /**
     * @see
     * CompoundSteeringBehavior#calculatePartialForce(com.jme3.ai.agents.behaviors.npc.steering.AbstractSteeringBehavior)
     */
    @Override
    protected Vector3f calculatePartialForce(AbstractSteeringBehavior behavior) {
        this.calculateTotalForce();
        Vector3f partialForce = this.partialForces.get(this.numberOfPartialForcesAlreadyCalculated);

        if (this.strengthIsBalanced && this.totalForce.length() > 0) {
            partialForce.mult(partialForce.length()
                    / this.totalForce.length());
        }

        this.partialForceCalculated();
        return partialForce;
    }

    /**
     * Calculates the total force if it is not calculated.
     */
    protected void calculateTotalForce() {
        if (numberOfPartialForcesAlreadyCalculated == 0) {
            Vector3f totalForceAux = new Vector3f();

            //We do not want to modify the current pointer
            steerBehaviorsLayerList.steerBehaviorsLayerNode currentPointer = this.behaviors.getPointer();

            behaviors.moveAtBeginning();

            while (!behaviors.nullPointer()) {
                Vector3f partial = behaviors.getBehavior().calculateSteering();
                partialForces.add(partial);
                totalForceAux = totalForceAux.add(partial);
                behaviors.moveNext();
            }

            //Restore the previous pointer
            this.behaviors.setPointer(currentPointer);
            this.totalForce = totalForceAux;
        }

    }

    /**
     * Reset the forces if we have finished with all the forces.
     */
    protected void partialForceCalculated() {
        numberOfPartialForcesAlreadyCalculated++;

        if (numberOfPartialForcesAlreadyCalculated >= this.numberOfBehaviors) {
            numberOfPartialForcesAlreadyCalculated = 0;
            partialForces.clear();
        }
    }
}
