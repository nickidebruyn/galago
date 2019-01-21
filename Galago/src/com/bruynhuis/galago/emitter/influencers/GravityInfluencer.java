package com.bruynhuis.galago.emitter.influencers;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.Vector3f;
import java.io.IOException;
import com.bruynhuis.galago.emitter.particle.ParticleData;

/**
 *
 * @author t0neg0d
 */
public class GravityInfluencer implements ParticleInfluencer {
	public static enum GravityAlignment {
		World,
		Reverse_Velocity,
		Emission_Point,
		Emitter_Center
	}
	private boolean enabled = true;
	private Vector3f gravity = new Vector3f(0,1f,0);
	private transient Vector3f store = new Vector3f();
	private boolean useNegativeVelocity = false;
	private float magnitude = 1;
	private GravityAlignment alignment = GravityAlignment.World;
	
	@Override
	public void update(ParticleData p, float tpf) {
		if (enabled) {
			if (!p.emitter.getUseStaticParticles()) {
				switch (alignment) {
					case World:
						store.set(gravity).multLocal(tpf);
						p.velocity.subtractLocal(store);
						break;
					case Reverse_Velocity:
						store.set(p.reverseVelocity).multLocal(tpf);
						p.velocity.addLocal(store);
						break;
					case Emission_Point:
						p.emitter.getShape().setNext(p.triangleIndex);
						if (p.emitter.getUseRandomEmissionPoint())
							store.set(p.emitter.getShape().getNextTranslation().addLocal(p.randomOffset));
						else
							store.set(p.emitter.getShape().getNextTranslation());
						store.subtractLocal(p.position).multLocal(p.initialLength*magnitude).multLocal(tpf);
						p.velocity.addLocal(store);
						break;
					case Emitter_Center:
						store.set(p.emitter.getShape().getMesh().getBound().getCenter());
						store.subtractLocal(p.position).multLocal(p.initialLength*magnitude).multLocal(tpf);
						p.velocity.addLocal(store);
						break;
				}
			}
		}
	}
	
	@Override
	public void initialize(ParticleData p) {
		p.reverseVelocity.set(p.velocity.negate().mult(magnitude));
	}

	@Override
	public void reset(ParticleData p) {
		
	}

	/**
	 * Aligns the gravity to the specified GravityAlignment
	 * @param alignment 
	 */
	public void setAlignment(GravityAlignment alignment) {
		this.alignment = alignment;
	}
	
	/**
	 * Returns the specified GravityAlignment
	 * @return 
	 */
	public GravityAlignment getAlignment() {
		return this.alignment;
	}
	
	/**
	 * Gravity multiplier
	 * @param magnitude 
	 */
	public void setMagnitude(float magnitude) {
		this.magnitude = magnitude;
	}
	
	/**
	 * Returns the current magnitude
	 * @return 
	 */
	public float getMagnitude() {
		return this.magnitude;
	}
	
	/**
	 * Sets gravity to the provided Vector3f
	 * @param gravity Vector3f representing gravity
	 */
	public void setGravity(Vector3f gravity) {
		this.gravity.set(gravity);
	}
	/**
	 * Sets gravity per axis to the specified values
	 * @param x Gravity along the x axis
	 * @param y Gravity along the y axis
	 * @param z Gravity along the z axis
	 */
	public void setGravity(float x, float y, float z) {
		this.gravity.set(x, y, z);
	}
	
	/**
	 * Returns the current gravity as a Vector3f
	 * @return 
	 */
	public Vector3f getGravity() {
		return this.gravity;
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
        oc.write(gravity, "gravity", new Vector3f(0,1,0));
		oc.write(enabled, "enabled" ,true);
		oc.write(useNegativeVelocity, "useNegativeVelocity", false);
		oc.write(magnitude, "magnitude", 1f);
		oc.write(alignment.name(), "alignment", GravityAlignment.World.name());
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		gravity = (Vector3f) ic.readSavable("gravity", new Vector3f(0,1,0));
		enabled = ic.readBoolean("enabled", true);
		useNegativeVelocity = ic.readBoolean("useNegativeVelocity", false);
		magnitude = ic.readFloat("magnitude", 1);
		alignment = GravityAlignment.valueOf(ic.readString("alignment", GravityAlignment.World.name()));
	}
	
	@Override
	public ParticleInfluencer clone() {
		try {
			GravityInfluencer clone = (GravityInfluencer) super.clone();
			clone.setGravity(gravity);
			clone.enabled = enabled;
			clone.useNegativeVelocity = false;
			clone.magnitude = 1;
			clone.alignment = alignment;
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
		return GravityInfluencer.class;
	}
}
