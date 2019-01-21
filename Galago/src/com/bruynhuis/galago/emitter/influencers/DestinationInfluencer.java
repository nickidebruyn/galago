package com.bruynhuis.galago.emitter.influencers;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.util.SafeArrayList;
import java.io.IOException;
import java.util.ArrayList;
import com.bruynhuis.galago.emitter.Interpolation;
import com.bruynhuis.galago.emitter.particle.ParticleData;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author t0neg0d
 */
public class DestinationInfluencer implements ParticleInfluencer {
	private SafeArrayList<Vector3f> destinations = new SafeArrayList(Vector3f.class);
	private SafeArrayList<Float> weights = new SafeArrayList(Float.class);
	private SafeArrayList<Interpolation> interpolations = new SafeArrayList(Interpolation.class);
	private boolean enabled = true;
	private boolean initialized = false;
	private float blend;
	private boolean useRandomStartDestination = false;
	private float weight = 1f;
	private boolean cycle = false;;
	private float fixedDuration = 0f;
	private Vector3f destinationDir = new Vector3f();
	private float dist;
	
	@Override
	public void update(ParticleData p, float tpf) {
		if (enabled) {
			p.destinationInterval += tpf;
			if (p.destinationInterval >= p.destinationDuration)
				updateDestination(p);

			blend = p.destinationInterpolation.apply(p.destinationInterval/p.destinationDuration);

			destinationDir.set(destinations.getArray()[p.destinationIndex].subtract(p.position));
			dist = p.position.distance(destinations.getArray()[p.destinationIndex]);
			destinationDir.multLocal(dist);
			
			weight = weights.getArray()[p.destinationIndex];
			
			p.velocity.interpolateLocal(destinationDir, blend*tpf*(weight*10));
		}
	}
	
	private void updateDestination(ParticleData p) {
		p.destinationIndex++;
		if (p.destinationIndex == destinations.size())
			p.destinationIndex = 0;
		p.destinationInterpolation = interpolations.getArray()[p.destinationIndex];
		p.destinationInterval -= p.destinationDuration;
	}
	
	@Override
	public void initialize(ParticleData p) {
		if (!initialized) {
			if (destinations.isEmpty()) {
				addDestination(new Vector3f(0,0,0),0.5f);
			}
			initialized = true;
		}
		if (useRandomStartDestination) {
			p.destinationIndex = FastMath.nextRandomInt(0,destinations.size()-1);
		} else {
			p.destinationIndex = 0;
		}
		p.destinationInterval = 0f;
		p.destinationDuration = (cycle) ? fixedDuration : p.startlife/((float)destinations.size());
		
		p.destinationInterpolation = interpolations.getArray()[p.destinationIndex];
	}

	@Override
	public void reset(ParticleData p) {
		
	}
	
	/**
	 * When enabled, the initial step the particle will start at will be randomly selected from the defined list of directions
	 * @param useRandomStartDestination 
	 */
	public void setUseRandomStartDestination(boolean useRandomStartDestination) {
		this.useRandomStartDestination = useRandomStartDestination;
	}
	
	/**
	 * Returns if the influencer will start a newly emitted particle at a random step in the provided list of directions
	 * @return 
	 */
	public boolean getUseRandomStartDestination() { return this.useRandomStartDestination; }
	
	/**
	 * Adds a destination using linear interpolation to the list of destinations used during the life cycle of the particle
	 * @param destination The destination the particle will move towards
	 * @param weight How strong the pull towards the destination should be
	 */
	public void addDestination(Vector3f destination, float weight) {
		addDestination(destination, weight, Interpolation.linear);
	}
	
	/**
	 * Adds a destination using the defined interpolation to the list of destinations used during the life cycle of the particle
	 * @param destination The destination the particle will move towards
	 * @param weight How strong the pull towards the destination should be
	 * @interpolation The interpolation method used to blend from the this step value to the next
	 */
	public void addDestination(Vector3f destination, float weight, Interpolation interpolation) {
		this.destinations.add(destination.clone());
		this.weights.add(weight);
		this.interpolations.add(interpolation);
	}
	/**
	 * Removes the destination step value at the supplied index
	 * @param index 
	 */
	public void removeDestination(int index) {
		this.destinations.remove(index);
		this.weights.remove(index);
		this.interpolations.remove(index);
	}
	/**
	 * Removes all destination step values
	 */
	public void removeAll() {
		this.destinations.clear();
		this.weights.clear();
		this.interpolations.clear();
	}
	/**
	 * Returns an array containing all destination step values
	 * @return 
	 */
	public Vector3f[] getDestinations() { return this.destinations.getArray(); }
	/**
	 * Returns an array containing all step value weights
	 * @return 
	 */
	public Interpolation[] getInterpolations() { return this.interpolations.getArray(); }
	/**
	 * Returns an array containing all step value interpolations
	 * @return 
	 */
	public Float[] getWeights() { return this.weights.getArray(); }
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.writeSavableArrayList(new ArrayList(destinations), "destinations", null);
		oc.writeSavableArrayList(new ArrayList(weights), "weightss", null);
		Map<String,Vector2f> interps = new HashMap<String,Vector2f>();
		int index = 0;
		for (Interpolation in : interpolations.getArray()) {
			interps.put(Interpolation.getInterpolationName(in) + ":" + String.valueOf(index),null);
			index++;
		}
		oc.writeStringSavableMap(interps, "interpolations", null);
		oc.write(enabled, "enabled", true);
		oc.write(useRandomStartDestination, "useRandomStartDestination", false);
		oc.write(cycle, "cycle", false);
		oc.write(fixedDuration, "fixedDuration", 0.125f);
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		destinations = new SafeArrayList<Vector3f>(Vector3f.class, ic.readSavableArrayList("destinations", null));
		weights = new SafeArrayList<Float>(Float.class, ic.readSavableArrayList("weights", null));
		Map<String,Vector2f> interps = (Map<String,Vector2f>)ic.readStringSavableMap("interpolations", null);
		for (String in : interps.keySet()) {
			String name = in.substring(0,in.indexOf(":"));
			interpolations.add(Interpolation.getInterpolationByName(name));
		}
		enabled = ic.readBoolean("enabled", true);
		useRandomStartDestination = ic.readBoolean("useRandomStartDestination", false);
		cycle = ic.readBoolean("cycle", false);
		fixedDuration = ic.readFloat("fixedDuration", 0.125f);
	}

	@Override
	public ParticleInfluencer clone() {
		try {
			DestinationInfluencer clone = (DestinationInfluencer) super.clone();
			clone.destinations.addAll(destinations);
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
	
	/**
	 * Each step value should last the specified duration, cycling once reaching the end of the defined list (A value of 0 disables cycling)
	 * @param fixedDuration duration between step value updates
	 */
	public void setFixedDuration(float fixedDuration) {
		if (fixedDuration != 0) {
			this.cycle = true;
			this.fixedDuration = fixedDuration;
		} else {
			this.cycle = false;
			this.fixedDuration = 0;
		}
	}
	/**
	 * Returns the current duration used between steps for cycling
	 * @return 
	 */
	public float getFixedDuration() { return this.fixedDuration; }

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
		return DestinationInfluencer.class;
	}
}
