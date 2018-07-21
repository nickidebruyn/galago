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
package com.jme3.ai.monkeystuff.weapon;

import com.jme3.ai.agents.Agent;
import com.jme3.ai.agents.util.GameEntity;
import com.jme3.math.Vector3f;

/**
 * Abstract class for defining weapons used by agents.
 *
 * @author Tihomir Radosavljević
 * @version 2.0.0
 */
public abstract class AbstractWeapon extends GameEntity {

    /**
     * Name of weapon.
     */
    protected String name;
    /**
     * Name of agent to whom weapon is added.
     */
    protected Agent agent;
    /**
     * Maximum range of weapon.
     */
    protected float maxAttackRange;
    /**
     * Minimum range of weapon.
     */
    protected float minAttackRange = 0;
    /**
     * Attack damage of weapon.
     */
    protected float attackDamage;
    /**
     * How much time is needed for next attack.
     */
    protected float cooldown;
    /**
     * Time used for calculating cooldown of weapons.
     */
    private float timeSinceFired = 0;

    /**
     * Check if target position is in range of weapon.
     *
     * @param targetPosition
     * @return
     */
    public boolean isInRange(Vector3f targetPosition) {
        if (agent.getLocalTranslation().distance(targetPosition) > maxAttackRange) {
            return false;
        }
        if (agent.getLocalTranslation().distance(targetPosition) < minAttackRange) {
            return false;
        }
        return true;
    }

    /**
     * Check if enemy agent or game object of interest to AI is in range of
     * weapon.
     *
     * @param target
     * @return
     */
    public boolean isInRange(GameEntity target) {
        return isInRange(target.getLocalTranslation());
    }

    @Override
    protected void controlUpdate(float tpf) {
        timeSinceFired -= tpf;
    }

    /**
     * Method for inquiry is weapon in cooldown.
     *
     * @return false - if weapon can attack, true otherwise
     */
    public boolean isInCooldown() {
        return timeSinceFired > 0;
    }

    /**
     * Reset cooldown so the weapon can be used again.
     */
    public void resetCooldown() {
        timeSinceFired = 0;
    }

    /**
     * Method for setting weapon's cooldown to maximum.
     */
    protected void setFullCooldown() {
        timeSinceFired = cooldown;
    }

    /**
     * Method for calculating if the requirements have been met for the weapon
     * being used. (Is there ammo, etc)
     *
     * Note: Do not check cooldown, it is already checked by this framework.
     *
     * @return true if usable, false otherwise
     */
    public abstract boolean isUsable();

    public abstract void attack(Vector3f targetPosition, float tpf);

    /**
     * Method for creating bullets and setting them to move.
     *
     * @param target
     * @param tpf time per frame
     */
    public void attack(GameEntity target, float tpf) {
        attack(target.getLocalTranslation(), tpf);
    }

    /**
     * Method for checking if there is unlimited use of the weapon.
     *
     * @return true - unlimited use, false otherwise
     */
    protected abstract boolean isUnlimitedUse();

    /**
     * Method for decreasing stats of weapon after usage. (ammo, mana, hp etc.)
     */
    protected abstract void useWeapon();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public float getMaxAttackRange() {
        return maxAttackRange;
    }

    public void setMaxAttackRange(float maxAttackRange) {
        this.maxAttackRange = maxAttackRange;
    }

    public float getMinAttackRange() {
        return minAttackRange;
    }

    public void setMinAttackRange(float minAttackRange) {
        this.minAttackRange = minAttackRange;
    }

    public float getAttackDamage() {
        return attackDamage;
    }

    public void setAttackDamage(float attackDamage) {
        this.attackDamage = attackDamage;
    }

    public float getCooldown() {
        return cooldown;
    }

    public void setCooldown(float cooldown) {
        this.cooldown = cooldown;
    }
}
