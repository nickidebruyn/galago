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
import com.jme3.ai.agents.behaviors.BehaviorExceptions.BehaviorException;

/**
 * Class container for exceptions related to steering behavior.
 *
 * @author Tihomir Radosavljević
 * @author Jesús Martín Berlanga
 * @version 1.0.0
 */
public class SteeringExceptions {

    /**
     * Main generic exception related for all illegal arguments for steering
     * behaviors.
     */
    public static class SteeringBehaviorException extends BehaviorException {

        public SteeringBehaviorException(Agent agent, String message, float value) {
            super(agent, message, value);
        }

        public SteeringBehaviorException(String message) {
            super(message);
        }
    }

    /**
     * This exception is thrown when value is not set in the [0, 1] interval.
     */
    public static class IllegalIntervalException extends SteeringBehaviorException {

        public IllegalIntervalException(String factorName, float value) {
            super("The " + factorName + " factor value must be contained in the [0,1] interval. The current value is " + value + '.');
        }
    }

    /**
     * This exception is thrown when negative value is given to steering
     * behaviour.
     */
    public static class NegativeValueException extends SteeringBehaviorException {

        public NegativeValueException(String message) {
            super(message);
        }

        public NegativeValueException(String message, float value) {
            super(message + " You inputed " + value + '.');
        }
    }

    public static class InvalidAreaException extends SteeringBehaviorException {

        public InvalidAreaException(String message) {
            super(message);
        }
    }

    public static class PathFollowInsufficientPointsException extends BehaviorException {

        public PathFollowInsufficientPointsException(String message, float value) {
            super(message + " You inputed " + value + '.');
        }
    }

    public static class WallApproachWithoutWallException extends BehaviorException {

        public WallApproachWithoutWallException(String message) {
            super(message);
        }
    }
}
