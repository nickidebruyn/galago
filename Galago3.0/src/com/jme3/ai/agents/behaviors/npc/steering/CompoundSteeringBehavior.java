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

/**
 * A steer compound behavior contains one or more steer behaviors. This class
 * should be used to avoid bugs when adding several steer behaviors directly to
 * SimpleMainBehavior. <br> <br>
 *
 * First add several behaviors to make an CompoundBehavior and then add this
 * compoundBehavior to an SimpleMainBehavior. <br><br>
 *
 * All the steer behaviors added to this class must extend from
 * AbstractSteeringBehavior. <br><br>
 *
 * If you have issues related with components cancelling each other out, that
 * "can be addressed by assigning a priority to components (For example: first
 * priority is obstacle avoidance, second is evasion ...)." <br><br>
 *
 * The steering controller first checks the higher layer to see if all the
 * behaviors returns a value higher than 'minLengthToInvalidSteer', if so it
 * uses that layer. Otherwise, it moves on to the second layer, and so on.
 * <br><br>
 *
 * @author Jesús Martín Berlanga
 * @version 2.1
 */
public class CompoundSteeringBehavior extends AbstractStrengthSteeringBehavior {

    /**
     * Ordered list. <br><br>
     *
     * Nodes are ordered from highest to lowest layer number. Each node has a
     * behavior, a integer representing his layer and the min length needed to
     * consider the behavior invalid.
     */
    protected class steerBehaviorsLayerList {

        public class steerBehaviorsLayerNode {

            public class layerElementData {

                private AbstractSteeringBehavior behavior;
                private int layer;
                private float minLengthToInvalidSteer;

                public layerElementData(AbstractSteeringBehavior behaviour, int layer, float minLengthToInvalidSteer) {
                    this.behavior = behaviour;
                    this.layer = layer;
                    this.minLengthToInvalidSteer = minLengthToInvalidSteer;
                }
            }
            private layerElementData data;
            private steerBehaviorsLayerNode nextNode;

            public void setData(AbstractSteeringBehavior behaviour, int layer, float minLengthToInvalidSteer) {
                this.data = new layerElementData(behaviour, layer, minLengthToInvalidSteer);
            }

            public steerBehaviorsLayerNode(layerElementData data, steerBehaviorsLayerNode nextNode) {
                this.data = data;
                this.nextNode = nextNode;
            }

            public steerBehaviorsLayerNode(AbstractSteeringBehavior behaviour, int layer, float minLengthToInvalidSteer, steerBehaviorsLayerNode nextNode) {
                this.data = new layerElementData(behaviour, layer, minLengthToInvalidSteer);
                this.nextNode = nextNode;
            }
        }
        private steerBehaviorsLayerNode head = null;
        private steerBehaviorsLayerNode pointer = null;

        /**
         * To optimize the process speed You need to add the behaviors from
         * lowest to highest layer number.
         */
        public void add(AbstractSteeringBehavior behavior, int layer, float minLengthToInvalidSteer) {
            if (head == null) {
                head = new steerBehaviorsLayerNode(behavior, layer, minLengthToInvalidSteer, null);
                return;
            }

            steerBehaviorsLayerNode aux = head;

            while (aux.data.layer > layer && aux.nextNode != null) {
                aux = aux.nextNode;
            }

            if (aux.data.layer > layer) {
                aux.nextNode = new steerBehaviorsLayerNode(behavior, layer, minLengthToInvalidSteer, null);
            } else {
                steerBehaviorsLayerNode.layerElementData nextData = aux.data;
                steerBehaviorsLayerNode nextNode = aux.nextNode;

                aux.setData(behavior, layer, minLengthToInvalidSteer);
                aux.nextNode = new steerBehaviorsLayerNode(nextData, nextNode);
            }
        }

        public void remove(AbstractSteeringBehavior behavior) {
            if (head.data.behavior.equals(behavior)) {
                head = head.nextNode;
            } else {
                steerBehaviorsLayerNode current = head;

                while (current.nextNode != null) {
                    if (current.nextNode.data.behavior.equals(behavior)) {
                        current.nextNode = current.nextNode.nextNode;
                        break;
                    }

                    current = current.nextNode;
                }
            }
        }

        public void moveAtBeginning() {
            this.pointer = this.head;
        }

        public void moveNext() {
            steerBehaviorsLayerNode current = this.pointer;
            if (current != null) {
                this.pointer = this.pointer.nextNode;
            }
        }

        public boolean nullPointer() {
            return this.pointer == null;
        }

        public steerBehaviorsLayerNode getPointer() {
            return this.pointer;
        }

        public void setPointer(steerBehaviorsLayerNode pointer) {
            this.pointer = (steerBehaviorsLayerNode) pointer;
        }

        public AbstractSteeringBehavior getBehavior() {
            return this.pointer.data.behavior;
        }

        public int getLayer() {
            return this.pointer.data.layer;
        }

        public float getMinLengthToInvalidSteer() {
            return this.pointer.data.minLengthToInvalidSteer;
        }
    }
    /**
     * Partial behaviors
     */
    protected steerBehaviorsLayerList behaviors;

    /**
     * @see
     * AbstractSteeringBehavior#AbstractSteeringBehavior(com.jme3.ai.agents.Agent)
     */
    public CompoundSteeringBehavior(Agent agent) {
        super(agent);
        this.behaviors = new steerBehaviorsLayerList();
    }

    /**
     * @see
     * AbstractSteeringBehavior#AbstractSteeringBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.scene.Spatial)
     */
    public CompoundSteeringBehavior(Agent agent, Spatial spatial) {
        super(agent, spatial);
        this.behaviors = new steerBehaviorsLayerList();
    }

    /**
     * Adds a behavior to the compound behavior. <br><br>
     *
     * Layer and the min length to consider the behavior invalid are 0 by
     * default.
     *
     * @param behavior Behavior that you want to add
     */
    public void addSteerBehavior(AbstractSteeringBehavior behavior) {
        this.behaviors.add(behavior, 0, 0);
    }

    /**
     * Removes a behavior from the compound steer behavior.
     *
     * @param behavior Behavior that you want to remove
     */
    public void removeSteerBehavior(AbstractSteeringBehavior behavior) {
        this.behaviors.remove(behavior);
    }

    /**
     * To optimize the process speed add the behaviors with the lowest priority
     * first.
     *
     * @see
     * CompoundSteeringBehavior#addSteerBehavior(com.jme3.ai.agents.behaviors.npc.steering.AbstractSteeringBehavior)
     *
     * @param priority This behavior will be processed If all higher priority
     * behaviors can be considered inactives
     * @param minLengthToInvalidSteer If the behavior steer force length is
     * less than this value It will be considered inactive
     */
    public void addSteerBehavior(AbstractSteeringBehavior behavior, int priority, float minLengthToInvalidSteer) {
        this.behaviors.add(behavior, priority, minLengthToInvalidSteer);
    }

    /**
     * Calculates the composed steering force. The composed force is the
     * summatory of the behaviors steering forces.
     *
     * @return The composed steering force.
     */
    @Override
    protected Vector3f calculateRawSteering() {

        Vector3f totalForce = new Vector3f();
        float totalBraking = 1;

        this.behaviors.moveAtBeginning();

        if (!this.behaviors.nullPointer()) {
            int currentLayer = this.behaviors.getLayer();
            int inLayerCounter = 0;
            int validCounter = 0;

            while (!this.behaviors.nullPointer()) {
                if (this.behaviors.getLayer() != currentLayer) {
                    //We have finished the last layer, check If it was a valid layer
                    if (inLayerCounter == validCounter) {
                        //If we have a valid layer, return the force
                        break;
                    } else {
                        //If not, reset the total force
                        totalForce = new Vector3f();
                        //and reset braking
                        totalBraking = 1;
                    }

                    currentLayer = this.behaviors.getLayer();
                    inLayerCounter = 0;
                    validCounter = 0;
                }

                Vector3f force = this.calculatePartialForce(this.behaviors.getBehavior());
                if (force.length() > this.behaviors.getMinLengthToInvalidSteer()) {
                    validCounter++;
                }
                totalForce = totalForce.add(force);
                totalBraking *= this.behaviors.getBehavior().getBrakingFactor();

                inLayerCounter++;
                this.behaviors.moveNext();
            }
        }

        this.setBrakingFactor(totalBraking);
        return totalForce;
    }

    /**
     * Calculates the steering force of a single behavior
     *
     * @param behavior The behavior.
     * @return The steering force of that behavior
     */
    protected Vector3f calculatePartialForce(AbstractSteeringBehavior behavior) {
        return behavior.calculateSteering();
    }

    /**
     * Usual update pattern for steering behaviors.
     *
     * @param tpf
     */
    @Override
    protected void controlUpdate(float tpf) {
        this.behaviors.moveAtBeginning();
        while (!this.behaviors.nullPointer()) {
            this.behaviors.getBehavior().setTimePerFrame(tpf);
            this.behaviors.moveNext();
        }
        super.controlUpdate(tpf);
    }
}