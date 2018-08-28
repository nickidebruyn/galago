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
import com.jme3.ai.agents.AgentExceptions;
import com.jme3.ai.agents.behaviors.Behavior;
import com.jme3.ai.agents.events.GameEntitySeenEvent;
import com.jme3.ai.agents.events.GameEntitySeenListener;
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.ai.monkeystuff.weapon.AbstractWeapon;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * Simple attack behaviour for NPC. This behavior is for to agent to attack
 * targetedEntity or fixed point in space. It will attack with weapon, and it
 * doesn't check if it has ammo. It has implemented GameEntitySeenListener.
 * <br><br>
 * If targetedEntity nor fixed point in space are set, behavior will not be
 * activated.
 *
 * @see GameEntitySeenListener
 * @see AbstractWeapon#isInRange(com.jme3.math.Vector3f)
 * @see AbstractWeapon#isInRange(com.jme3.ai.agents.util.GameEntity)
 *
 * @author Tihomir Radosavljević
 * @version 1.0.3
 */
public class SimpleAttackBehavior extends BluePillBehavior implements GameEntitySeenListener {

    /**
     * Target for attack behavior.
     */
    protected GameEntity targetedEntity;
    /**
     * Target for attack behavior.
     */
    protected Vector3f targetPosition;

    /**
     * @param agent to whom behavior belongs
     */
    public SimpleAttackBehavior(BluePillAgent agent) {
        super(agent);
    }

    /**
     * @param agent to whom behavior belongs
     * @param spatial active spatial durring this behavior
     */
    public SimpleAttackBehavior(BluePillAgent agent, Spatial spatial) {
        super(agent, spatial);
    }

    /**
     * @param target at which agent will attack
     */
    public void setTarget(GameEntity target) {
        this.targetedEntity = target;
    }

    /**
     *
     * @param target at what point will agent attack
     */
    public void setTarget(Vector3f target) {
        this.targetPosition = target;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (agent.getInventory() != null) {
            try {
                if (targetPosition != null) {
                    agent.getInventory().getActiveWeapon().attack(targetPosition, tpf);
                    targetPosition = null;
                } else if (targetedEntity != null && targetedEntity.isEnabled()) {
                    agent.getInventory().getActiveWeapon().attack(targetedEntity, tpf);
                }
            } catch (NullPointerException npe) {
                throw new AgentExceptions.WeaponNotFoundException(agent);
            }
        } else {
            throw new AgentExceptions.InventoryNotFoundException(agent);
        }
    }

    /**
     * Behavior can automaticaly update its targetedEntity with
     * GameEntitySeenEvent, if it is agent, then it check if it is in range and
     * check if they are in same team.
     *
     * @param event
     */
    public void handleGameEntitySeenEvent(GameEntitySeenEvent event) {
        if (event.getGameEntitySeen() instanceof Agent) {
            Agent targetAgent = (Agent) event.getGameEntitySeen();
            if (agent.isSameTeam(targetAgent)) {
                return;
            }
            if (!agent.getInventory().getActiveWeapon().isInRange(targetAgent)) {
                return;
            }
        }
        targetedEntity = event.getGameEntitySeen();
        enabled = true;
    }

    /**
     * Method for checking is there any target that agent should attack.
     *
     * @return true if target is set
     */
    public boolean isTargetSet() {
        return targetPosition != null && targetedEntity != null;
    }
}
