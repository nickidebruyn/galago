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
package com.jme3.ai.monkeystuff.bluepillagent;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.behaviors.Behavior;
import com.jme3.ai.agents.behaviors.npc.SimpleMoveBehavior;
import com.jme3.input.controls.AnalogListener;
import com.jme3.scene.Spatial;
import java.util.HashMap;
import java.util.List;

/**
 * Simple move behavior for player controled agent. It has support for
 * AnalogListener.
 *
 * @author Tihomir Radosavljević
 * @version 1.0
 */
public class SimplePlayerMoveBehavior extends SimpleMoveBehavior implements AnalogListener {

    /**
     * Name of operation that should be done.
     */
    private String operation;
    /**
     * Supported operation for this agent base on inputs.
     */
    private HashMap<String, Behavior> supportedOperations;

    /**
     * Constructor for SimplePlayerMoveBehavior.
     *
     * @param agent Agent to whom is added this behavior
     * @param spatial active spatial durring moving
     */
    public SimplePlayerMoveBehavior(Agent agent, Spatial spatial) {
        super(agent, spatial);
        supportedOperations = new HashMap<String, Behavior>();
        enabled = true;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (operation == null) {
            return;
        }
        supportedOperations.get(operation).update(tpf);
    }

    public void onAnalog(String name, float value, float tpf) {
        operation = name;
        if (value != 0) {
            supportedOperations.get(operation).setEnabled(true);
            controlUpdate(tpf);
        }
    }

    /**
     * Add supported operations to this behavior.
     *
     * @param name of supported operation
     * @param operation behavior that should be done
     */
    public void addSuportedOperation(String name, Behavior operation) {
        supportedOperations.put(name, operation);
    }

    /**
     * Add supported operations to this behavior.
     *
     * @param names list of supported operations
     * @param operations list of behaviours that should be done
     * @return if sizes of names and operations don't match, true otherwise
     */
    public boolean addSuportedOperations(List<String> names, List<Behavior> operations) {
        if (names.size() != operations.size()) {
            return false;
        }
        for (int i = 0; i < operations.size(); i++) {
            supportedOperations.put(names.get(i), operations.get(i));
        }
        return true;
    }

    /**
     * Add supported operations to this behavior.
     *
     * @param supportedOperations map of supported operations
     */
    public void addSupportedOperations(HashMap<String, Behavior> supportedOperations) {
        this.supportedOperations.putAll(supportedOperations);
    }

    /**
     * Get supported operations.
     *
     * @return
     */
    public HashMap<String, Behavior> getSupportedOperations() {
        return supportedOperations;
    }

    /**
     * Setting supported operations.
     *
     * @param supportedActions
     */
    public void setSupportedOperations(HashMap<String, Behavior> supportedActions) {
        this.supportedOperations = supportedActions;
    }
}