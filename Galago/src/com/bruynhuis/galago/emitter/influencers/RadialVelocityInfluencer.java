package com.bruynhuis.galago.emitter.influencers;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.io.IOException;
import com.bruynhuis.galago.emitter.particle.ParticleData;

/**
 *
 * @author t0neg0d
 */
public class RadialVelocityInfluencer implements ParticleInfluencer {
	public static enum RadialPullAlignment {
		Emission_Point,
		Emitter_Center
	}
	public static enum RadialPullCenter {
		Absolute,
		Variable_X,
		Variable_Y,
		Variable_Z
	}
	public static enum RadialUpAlignment {
		Normal,
		UNIT_X,
		UNIT_Y,
		UNIT_Z
	}
	private boolean enabled = true;
	private float radialPull = 1, tangentForce = 1;
	private Vector3f tangent = new Vector3f();
	private Vector3f store = new Vector3f();
	private Vector3f up = Vector3f.UNIT_Y.clone(), left = new Vector3f();
	private Vector3f upStore = new Vector3f();
	private boolean useRandomDirection = false;
	private RadialPullAlignment alignment = RadialPullAlignment.Emission_Point;
	private RadialPullCenter center = RadialPullCenter.Absolute;
	private RadialUpAlignment upAlignment = RadialUpAlignment.UNIT_Y;
	private Quaternion q = new Quaternion();
	
	@Override
	public void update(ParticleData p, float tpf) {
		if (enabled) {
			switch (alignment) {
				case Emission_Point:
					p.emitter.getShape().setNext(p.triangleIndex);
					if (p.emitter.getUseRandomEmissionPoint())
						store.set(p.emitter.getShape().getNextTranslation().addLocal(p.randomOffset));
					else
						store.set(p.emitter.getShape().getNextTranslation());
					break;
				case Emitter_Center:
					store.set(p.emitter.getShape().getMesh().getBound().getCenter());
					break;
			}
			
			switch (center) {
				case Absolute:
					break;
				case Variable_X:
					store.setX(p.position.x);
					break;
				case Variable_Y:
					store.setY(p.position.y);
					break;
				case Variable_Z:
					store.setZ(p.position.z);
					break;
			}
			
			store.subtractLocal(p.position).normalizeLocal().multLocal(p.initialLength*radialPull).multLocal(tpf);
			
			switch (upAlignment) {
				case Normal:
					upStore.set(p.emitter.getLocalRotation().inverse().mult(upStore.set(p.emitter.getShape().getNormal())));
					break;
				case UNIT_X:
					upStore.set(Vector3f.UNIT_X);
					break;
				case UNIT_Y:
					upStore.set(Vector3f.UNIT_Y);
					break;
				case UNIT_Z:
					upStore.set(Vector3f.UNIT_Z);
					break;
			}
			
			up.set(store).crossLocal(upStore).normalizeLocal();
			up.set(p.emitter.getLocalRotation().mult(up));
			left.set(store).crossLocal(up).normalizeLocal();

			tangent.set(store).crossLocal(left).normalizeLocal().multLocal(p.tangentForce).multLocal(tpf);
			p.velocity.subtractLocal(tangent);
			p.velocity.addLocal(store.mult(radialPull));
		}
	}
	
	@Override
	public void initialize(ParticleData p) {
		if (useRandomDirection) {
			if (FastMath.rand.nextBoolean())
				p.tangentForce = tangentForce;
			else
				p.tangentForce = -tangentForce;
		} else
			p.tangentForce = tangentForce;
	}

	@Override
	public void reset(ParticleData p) {
		
	}
	
	/**
	 * The tangent force to apply when updating the particles trajectory
	 * @param force 
	 */
	public void setTangentForce(float force) {
		this.tangentForce = force;
	}
	
	/**
	 * Returns the defined tangent force used when calculating the particles trajectory
	 * @return 
	 */
	public float getTangentForce() {
		return this.tangentForce;
	}
	
	/**
	 * Defines the point of origin that the particle will use in calculating it's trajectory
	 * @param alignment \
	 */
	public void setRadialPullAlignment(RadialPullAlignment alignment) {
		this.alignment = alignment;
	}
	
	/**
	 * Returns the defined point of origin parameter
	 * @return 
	 */
	public RadialPullAlignment getRadialPullAlignment() {
		return this.alignment;
	}
	
	/**
	 * Alters how the particle will orbit it's radial pull alignment.  For example, Variable_Y, will use the X/Z components of the point of origin vector, 
	 * but use the individual particles Y component when calculating the updated trajectory.
	 * @param center 
	 */
	public void setRadialPullCenter(RadialPullCenter center) {
		this.center = center;
	}
	
	/**
	 * Returns the defined varient for the point of origin vector
	 * @return 
	 */
	public RadialPullCenter getRadialPullCenter() {
		return this.center;
	}
	
	/**
	 * Defines the gravitational force pulling against the tangent force - Or, how the orbit will tighten or decay over time
	 * @param radialPull 
	 */
	public void setRadialPull(float radialPull) {
		this.radialPull = radialPull;
	}
	
	/**
	 * Returns the defined radial pull used when calculating the particles trajectory
	 * @return 
	 */
	public float getRadialPull() {
		return this.radialPull;
	}
	
	/**
	 * Defines the up vector used to calculate rotation around a center point
	 * @return 
	 */
	public void setRadialUpAlignment(RadialUpAlignment upAlignment) {
		this.upAlignment = upAlignment;
	}
	
	/**
	 * Returns the defined up vector parameter
	 * @return 
	 */
	public RadialUpAlignment getRadialUpAlignment() {
		return this.upAlignment;
	}
	
	/**
	 * Allows the influencer to randomly select the negative of the defined tangentForce to reverse the direction of rotation
	 */
	public void setUseRandomDirection(boolean useRandomDirection) {
		this.useRandomDirection = useRandomDirection;
	}
	
	/**
	 * Returns if the influencer allows random reverse rotation
	 * @return 
	 */
	public boolean getUseRandomDirection() { return this.useRandomDirection; }
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(enabled, "enabled", true);
		oc.write(radialPull, "radialPull", 1.0f);
		oc.write(tangentForce, "tangentForce", 1.0f);
		oc.write(alignment.name(), "alignment", RadialPullAlignment.Emission_Point.name());
		oc.write(center.name(), "center", RadialPullCenter.Absolute.name());
		oc.write(upAlignment.name(), "upAlignment", RadialUpAlignment.UNIT_Y.name());
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		enabled = ic.readBoolean("enabled", true);
		radialPull = ic.readFloat("radialPull", 1.0f);
		tangentForce = ic.readFloat("tangentForce", 1.0f);
		alignment = RadialPullAlignment.valueOf(ic.readString("alignment",RadialPullAlignment.Emission_Point.name()));
		center = RadialPullCenter.valueOf(ic.readString("center",RadialPullCenter.Absolute.name()));
		upAlignment = RadialUpAlignment.valueOf(ic.readString("upAlignment",RadialUpAlignment.UNIT_Y.name()));
	}

	@Override
	public ParticleInfluencer clone() {
		try {
			RadialVelocityInfluencer clone = (RadialVelocityInfluencer) super.clone();
			clone.setEnabled(enabled);
			clone.setRadialPull(radialPull);
			clone.setTangentForce(tangentForce);
			clone.setRadialPullAlignment(alignment);
			clone.setRadialPullCenter(center);
			clone.setRadialUpAlignment(upAlignment);
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
		return RadialVelocityInfluencer.class;
	}
}
