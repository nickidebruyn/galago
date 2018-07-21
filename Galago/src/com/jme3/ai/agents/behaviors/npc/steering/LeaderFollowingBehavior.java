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
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * This is similar to pursuit behavior, but pursuiers must stay away from the
 * pursued path. In order to stay away from that path the pursuers resort to
 * "evade". Furthermore pursuers use "arrive" instead of "seek" to approach to
 * their objective.
 *
 * @see PursuitBehavior
 *
 * @author Jesús Martín Berlanga
 * @version 1.4
 */
public class LeaderFollowingBehavior extends SeekBehavior {

    private float distanceToChangeFocus;
    private float distanceToEvade;
    private float minimumAngle;
    private ArriveBehavior arriveBehavior;
    private EvadeBehavior evadeBehavior;

    /**
     * @see SeekBehavior#SeekBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.ai.agents.Agent)
     */
    public LeaderFollowingBehavior(Agent agent, Agent target) {
        super(agent, target);
        this.evadeBehavior = new EvadeBehavior(agent, target);
        this.arriveBehavior = new ArriveBehavior(agent, target);

        //Default values
        this.distanceToEvade = 2;
        this.distanceToChangeFocus = 5;
        this.minimumAngle = FastMath.PI / 2.35f;
    }

    /**
     * @see SeekBehavior#SeekBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.ai.agents.Agent, com.jme3.scene.Spatial)
     */
    public LeaderFollowingBehavior(Agent agent, Agent target, Spatial spatial) {
        super(agent, target, spatial);
        this.evadeBehavior = new EvadeBehavior(agent, target, spatial);
        this.arriveBehavior = new ArriveBehavior(agent, target);

        //Default values
        this.distanceToEvade = 2;
        this.distanceToChangeFocus = 5;
        this.minimumAngle = FastMath.PI / 2.35f;
    }

    /**
     * @param distanceToChangeFocus Distance to change the focus: After the
     * agent distance to the target is lower than this distance, the agent will
     * progressively stop seeking the future position and instead, directly seek
     * the target.
     * @param distanceToEvade If the agent is in front of the target and the
     * distance to him is lower than distanceToEvade, the agent will evade him
     * in order to stay out of his way.
     * @param minimunAngle Minimum angle betwen the target velocity and the
     * vehicle location.
     *
     * @throws BehaviorExceptions.TargetNotFoundException If target (leader) is
     * null
     * @throws SteeringExceptions.NegativeValueException If distanceToEvade is
     * lower than 0 or if distanceToEvade is lower than 0
     *
     * @see LeaderFollowingBehavior#LeaderFollowingBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.ai.agents.Agent)
     */
    public LeaderFollowingBehavior(Agent agent, Agent target, float distanceToEvade, float distanceToChangeFocus, float minimunAngle) {
        super(agent, target);
        this.validateTarget(target);
        this.validateDistanceToEvade(distanceToEvade);
        this.validateDistanceToChangeFocus(distanceToChangeFocus);
        this.distanceToEvade = distanceToEvade;
        this.evadeBehavior = new EvadeBehavior(agent, target);
        this.arriveBehavior = new ArriveBehavior(agent, target);
        this.distanceToChangeFocus = distanceToChangeFocus;
        this.minimumAngle = minimunAngle;
    }

    /**
     * @see LeaderFollowingBehavior#LeaderFollowingBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.ai.agents.Agent, com.jme3.scene.Spatial)
     * @see LeaderFollowingBehavior#LeaderFollowingBehavior(com.jme3.ai.agents.Agent,
     * com.jme3.ai.agents.Agent, float, float, float)
     */
    public LeaderFollowingBehavior(Agent agent, Agent target, float distanceToEvade, float distanceToChangeFocus, float minimunAngle, Spatial spatial) {
        super(agent, target, spatial);
        this.validateTarget(target);
        this.validateDistanceToEvade(distanceToEvade);
        this.validateDistanceToChangeFocus(distanceToChangeFocus);
        this.distanceToEvade = distanceToEvade;
        this.evadeBehavior = new EvadeBehavior(agent, target, spatial);
        this.arriveBehavior = new ArriveBehavior(agent, target);
        this.distanceToChangeFocus = distanceToChangeFocus;
        this.minimumAngle = minimunAngle;
    }

    private void validateDistanceToEvade(float distanceToEvade) {
        if (distanceToEvade < 0) {
            throw new SteeringExceptions.NegativeValueException("The distance to evade can not be negative.", distanceToEvade);
        }
    }

    private void validateTarget(Agent target) {
        if (target == null) {
            throw new BehaviorExceptions.TargetNotFoundException();
        }
    }

    private void validateDistanceToChangeFocus(float distanceToChangeFocus) {
        if (distanceToChangeFocus < 0) {
            throw new SteeringExceptions.NegativeValueException("The distance to change focus can not be negative.", distanceToChangeFocus);
        }
    }

    /**
     * @see AbstractStrengthSteeringBehavior#calculateRawSteering()
     */
    @Override
    protected Vector3f calculateRawSteering() {
        Vector3f steer;
        float distanceBetwen = this.agent.distanceRelativeToGameEntity(this.getTarget());

        //See how far ahead we need to leed
        Vector3f fullProjectedLocation = this.getTarget().getPredictedPosition();
        Vector3f predictedPositionDiff = fullProjectedLocation.subtract(this.getTarget().getLocalTranslation());
        Vector3f projectedLocation = this.getTarget().getLocalTranslation().add(predictedPositionDiff.mult(
                this.calculateFocusFactor(distanceBetwen)));

        this.arriveBehavior.setSeekingPosition(projectedLocation);

        steer = this.arriveBehavior.calculateRawSteering();

        if (!(distanceBetwen > this.distanceToEvade) && !(this.getTarget().forwardness(this.agent) < FastMath.cos(this.minimumAngle))) { //Incorrect angle and Is in the proper distance to evade -> Evade the leader

            Vector3f arriveSteer = steer.mult(distanceBetwen / this.distanceToEvade);
            Vector3f evadeSteer = this.evadeBehavior.calculateRawSteering();
            evadeSteer.mult(this.distanceToEvade / (1 + distanceBetwen));
            steer = (new Vector3f()).add(arriveSteer).add(evadeSteer);
        }

        return steer;
    }

    /**
     * Calculates the factor in order to change the focus
     */
    private float calculateFocusFactor(float distanceFromFocus) {
        float factor;

        if (distanceFromFocus > this.distanceToChangeFocus) {
            factor = 1;
        } else {
            factor = FastMath.pow((1 + distanceFromFocus / this.distanceToChangeFocus), 2);
        }

        return factor;
    }
}