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
package com.jme3.ai.agents;

import com.jme3.ai.agents.util.GameEntityExceptions;

/**
 *
 * Class container for exceptions related to agent.
 *
 * @author Tihomir Radosavljević
 * @author Jesús Martín Berlanga
 * @version 1.0.1
 */
public class AgentExceptions {

    /**
     * This exception is thrown If it has been tried to instantiate an agent
     * with illegal arguments.
     */
    public static class IllegalAgentException extends RuntimeException {

        public IllegalAgentException(String message) {
            super(message);
        }
    }

    /**
     * Agent does not have initiliazed attribute.
     */
    public static class AgentAttributeNotFound extends GameEntityExceptions.GameEntityAttributeNotFound {

        public AgentAttributeNotFound(Agent agent, String message) {
            super(agent, message);
        }
    }

    public static class TeamNotFoundException extends AgentAttributeNotFound {

        public TeamNotFoundException(Agent agent) {
            super(agent, "a designated team");
        }
    }

    public static class InventoryNotFoundException extends AgentAttributeNotFound {

        public InventoryNotFoundException(Agent agent) {
            super(agent, "an inventory");
        }
    }

    public static class WeaponNotFoundException extends AgentAttributeNotFound {

        public WeaponNotFoundException(Agent agent) {
            super(agent, "a weapon");
        }
    }

    public static class InvalidNeighborhoodDistanceException extends IllegalAgentException{

        public InvalidNeighborhoodDistanceException(String message) {
            super(message);
        }
    }
    
    public static class MaxAgentsException extends RuntimeException {

        public MaxAgentsException() {
            super("There are more agents than the framework supports.");
        } 
    }
}
