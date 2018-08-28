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
package com.jme3.ai.monkeystuff.systems;

import com.jme3.ai.agents.Agent;

/**
 * Basic implementation of HitPoints for agents.
 *
 * @author Tihomir Radosavljević
 * @version 1.0.0
 */
public class SimpleAgentHitPoints implements HitPoints {
    /**
     * Current hit point value.
     */
    private float currentHP;
    private float maxHP;
    private Agent agent;

    public SimpleAgentHitPoints(Agent agent) {
        this.agent = agent;
        this.currentHP = 100f;
        this.maxHP = 100f;
    }

    public SimpleAgentHitPoints(Agent agent, float maxHP) {
        this.maxHP = maxHP;
        this.currentHP = maxHP;
        this.agent = agent;
    }

    /**
     * Method for increasing agents hitPoint for fixed amount. If adding
     * hitPoint will cross the maximum hitPoint of agent, then agent's hitPoint
     * status will be set to maximum allowed hitPoint for that agent.
     *
     * @see Agent#maxHitPoint
     * @param potionHitPoint amount of hitPoint that should be added
     */
    public void increaseHP(float potionHP) {
        currentHP = Math.min(maxHP, currentHP + potionHP);
    }

    /**
     * Method for decreasing agents hitPoint for fixed amount. If hitPoint drops
     * to zero or bellow, agent's hitPoint status will be set to zero and he
     * will be dead. It is up to programmer to remove its spatial.
     *
     * @see Agent#stop()
     * @param damage amount of hitPoint that should be removed
     */
    public void decreaseHitPoints(double damage) {
        currentHP -= damage;
        if (currentHP <= 0) {
            currentHP = 0;
            agent.stop();
        }
    }

    /**
     * Method for refilling currentHP to max.
     */
    public void refillCompletely() {
        currentHP = maxHP;
    }

    public float getCurrentHitPoints() {
        return currentHP;
    }

    public void setCurrentHP(float hp) {
        this.currentHP = Math.min(maxHP, hp);
    }

    public float getMaxHitPoints() {
        return maxHP;
    }

    public void setMaxHP(float maxHP) {
        this.maxHP = maxHP;
    }
}
