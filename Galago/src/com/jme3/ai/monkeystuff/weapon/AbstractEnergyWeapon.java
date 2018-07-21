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

/**
 * Abstract bullet based weapon that uses power source for as ammo.
 *
 * @author Pesegato
 * @author Tihomir Radosavljević
 * @version 1.0.0
 */
public abstract class AbstractEnergyWeapon extends AbstractBulletBasedWeapon {

    /**
     * Amount of energy needed to charge the weapon. Set to -1 if infinite.
     */
    protected int energyRequired;
    /**
     * The power source that fuels the weapon.
     */
    protected AbstractPowerSource powerSource;

    @Override
    public boolean isUsable() {
        return powerSource.contains(energyRequired);
    }

    @Override
    protected boolean isUnlimitedUse() {
        return energyRequired == -1;
    }

    @Override
    protected void useWeapon() {
        powerSource.consume(energyRequired);
    }

    public int getEnergyRequired() {
        return energyRequired;
    }

    public void setEnergyRequired(int energyRequired) {
        this.energyRequired = energyRequired;
    }

    public AbstractPowerSource getPowerSource() {
        return powerSource;
    }

    public void setPowerSource(AbstractPowerSource powerSource) {
        this.powerSource = powerSource;
    }
}
