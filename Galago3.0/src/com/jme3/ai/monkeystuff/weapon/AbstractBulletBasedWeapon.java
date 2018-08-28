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

import com.jme3.ai.monkeystuff.weapon.AbstractBullet;
import com.jme3.ai.agents.util.control.MonkeyBrainsAppState;
import com.jme3.math.Vector3f;

/**
 * Weapons with bullets.
 *
 * @author Tihomir Radosavljević
 * @version 1.0.0
 */
public abstract class AbstractBulletBasedWeapon extends AbstractWeapon {

    /**
     * One weapon have one type of bullet. Each fired bullet can be in it's own
     * update().
     */
    protected AbstractBullet bullet;

    /**
     * Method for creating bullets and setting them to move.
     *
     * @param direction direction of bullets to go
     * @param tpf time per frame
     */
    public void attack(Vector3f direction, float tpf) {
        //does weapon have bullets
        if (!isUsable()) {
            return;
        }
        //is weapon in cooldown
        if (isInCooldown()) {
            return;
        }
        //fire bullet
        AbstractBullet firedBullet = controlAttack(direction, tpf);
        if (firedBullet != null) {
            //if there is bullet than add it to be updated regulary in game
            MonkeyBrainsAppState.getInstance().addGameEntity(firedBullet);
        }
        //set weapon cooldown
        setFullCooldown();
        //decrease number of bullets if weapon have limited number of bullets
        if (!isUnlimitedUse()) {
            useWeapon();
        }
    }

    public AbstractBullet getBullet() {
        return bullet;
    }

    public void setBullet(AbstractBullet bullet) {
        this.bullet = bullet;
    }

    /**
     * Setting bullet that should be fired and giving it initial velocity.
     *
     * @param direction
     * @param tpf
     * @return
     */
    protected abstract AbstractBullet controlAttack(Vector3f direction, float tpf);
}
