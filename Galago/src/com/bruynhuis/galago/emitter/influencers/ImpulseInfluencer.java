package com.bruynhuis.galago.emitter.influencers;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.io.IOException;
import com.bruynhuis.galago.emitter.particle.ParticleData;

/**
 *
 * @author t0neg0d
 */
public class ImpulseInfluencer implements ParticleInfluencer {
	private boolean enabled = true;
	private transient Vector3f temp = new Vector3f();
    private transient Vector3f velocityStore = new Vector3f();
	private float chance = .02f;
	private float magnitude = .2f;
	private float strength = 3;
	
	@Override
	public void update(ParticleData p, float tpf) {
		if (enabled) {
			if (FastMath.rand.nextFloat() > 1-(chance+tpf)) {
				velocityStore.set(p.velocity);
				temp.set(FastMath.nextRandomFloat()*strength,
						FastMath.nextRandomFloat()*strength,
						FastMath.nextRandomFloat()*strength
				);
				if (FastMath.rand.nextBoolean()) temp.x = -temp.x;
				if (FastMath.rand.nextBoolean()) temp.y = -temp.y;
				if (FastMath.rand.nextBoolean()) temp.z = -temp.z;
				temp.multLocal(velocityStore.length());
				velocityStore.interpolateLocal(temp, magnitude);
				p.velocity.interpolateLocal(velocityStore, magnitude);
			}
		}
	}
	
	@Override
	public void initialize(ParticleData p) {
		
	}
	
	@Override
	public void reset(ParticleData p) {
		
	}
	
	/**
	 * Sets the chance the influencer has of successfully affecting the particle's velocity vector
	 * @param chance float
	 */
	public void setChance(float chance) { this.chance = chance; }
	
	/**
	 * Returns the chance the influencer has of successfully affecting the particle's velocity vector
	 * @return float
	 */
	public float getChance() { return chance; }
	/**
	 * Sets the magnitude at which the impulse will effect the particle's velocity vector
	 * @param magnitude float
	 */
	public void setMagnitude(float magnitude) { this.magnitude = magnitude; }
	/**
	 * Returns  the magnitude at which the impulse will effect the particle's velocity vector
	 * @return float
	 */
	public float getMagnitude() { return magnitude; }
	/**
	 * Sets the strength of the full impulse
	 * @param strength float
	 */
	public void setStrength(float strength) { this.strength = strength; }
	/**
	 * Returns the strength of the full impulse
	 * @return float
	 */
	public float getStrength() { return strength; }
	
	@Override
	public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(chance, "chance", 0.02f);
        oc.write(magnitude, "magnitude", 0.2f);
        oc.write(strength, "strength", 3f);
        oc.write(enabled, "enabled", true);
    }
	
	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		chance = ic.readFloat("chance", 0.02f);
    	magnitude = ic.readFloat("magnitude", 0.2f);
    	strength = ic.readFloat("strength", 3f);
		enabled = ic.readBoolean("enabled", true);
    }
	
    @Override
    public ParticleInfluencer clone() {
        try {
            ImpulseInfluencer clone = (ImpulseInfluencer) super.clone();
            clone.setChance(chance);
			clone.setMagnitude(magnitude);
			clone.setStrength(strength);
			clone.setEnabled(enabled);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
	
	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@Override
	public boolean isEnabled() {
		return this.enabled;
	}
	
	@Override
	public Class getInfluencerClass() {
		return ImpulseInfluencer.class;
	}
}
