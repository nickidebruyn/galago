package com.bruynhuis.galago.emitter.influencers;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
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
public class ColorInfluencer implements ParticleInfluencer {
	private SafeArrayList<ColorRGBA> colors = new SafeArrayList<ColorRGBA>(ColorRGBA.class);
	private SafeArrayList<Interpolation> interpolations = new SafeArrayList<Interpolation>(Interpolation.class);
	private boolean initialized = false;
	private boolean enabled = true;
	private boolean useRandomStartColor = false;
	private boolean cycle = false;
	private transient ColorRGBA resetColor = new ColorRGBA(0,0,0,0);
	private ColorRGBA startColor = new ColorRGBA().set(ColorRGBA.Red);
	private ColorRGBA endColor = new ColorRGBA().set(ColorRGBA.Yellow);
	private float blend;
	private float fixedDuration = 0f;
	
	@Override
	public void update(ParticleData p, float tpf) {
		if (enabled) {
			p.colorInterval += tpf;
			if (p.colorInterval >= p.colorDuration)
				updateColor(p);
			
			blend = p.colorInterpolation.apply(p.colorInterval/p.colorDuration);
			
			startColor.set(colors.getArray()[p.colorIndex]);
			
			if (p.colorIndex == colors.size()-1)
				endColor.set(colors.getArray()[0]);
			else
				endColor.set(colors.getArray()[p.colorIndex+1]);
			
			p.color.interpolateLocal(startColor, endColor, blend);
		}
	}
	
	private void updateColor(ParticleData p) {
		p.colorIndex++;
		if (p.colorIndex >= colors.size())
			p.colorIndex = 0;
		p.colorInterpolation = interpolations.getArray()[p.colorIndex];
		p.colorInterval -= p.colorDuration;
	}
	
	@Override
	public void initialize(ParticleData p) {
		if (!initialized) {
			if (colors.isEmpty()) {
				addColor(ColorRGBA.Red);
				addColor(ColorRGBA.Yellow);
			} else if (colors.size() == 1) {
				setEnabled(false);
			}
			initialized = true;
		}
		if (useRandomStartColor) {
			p.colorIndex = FastMath.nextRandomInt(0,colors.size()-1);
		} else {
			p.colorIndex = 0;
		}
		p.colorInterval = 0f;
		p.colorDuration = (cycle) ? fixedDuration : p.startlife/((float)colors.size()-1);
		
		p.color.set(colors.getArray()[p.colorIndex]);
		p.colorInterpolation = interpolations.getArray()[p.colorIndex];
	}

	@Override
	public void reset(ParticleData p) {
		p.color.set(resetColor);
		p.colorIndex = 0;
		p.colorInterval = 0;
	}
	
	public void setUseRandomStartColor(boolean useRandomStartColor) {
		this.useRandomStartColor = useRandomStartColor;
	}
	
	public boolean getUseRandomStartColor() {
		return this.useRandomStartColor;
	}
	
	public void addColor(ColorRGBA color) {
		addColor(color, Interpolation.linear);
	}
	
	public void addColor(ColorRGBA color, Interpolation interpolation) {
		this.colors.add(color.clone());
		this.interpolations.add(interpolation);
	}
	
	public void removeColor(int index) {
		this.colors.remove(index);
		this.interpolations.remove(index);
	}
	
	public void removeAll() {
		this.colors.clear();
		this.interpolations.clear();
	}
	
	public ColorRGBA[] getColors() {
		return colors.getArray();
	}
	
	public Interpolation[] getInterpolations() {
		return interpolations.getArray();
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.writeSavableArrayList(new ArrayList(colors), "colors", null);
		Map<String,Vector2f> interps = new HashMap<String,Vector2f>();
		int index = 0;
		for (Interpolation in : interpolations.getArray()) {
			interps.put(Interpolation.getInterpolationName(in) + ":" + String.valueOf(index),null);
			index++;
		}
		oc.writeStringSavableMap(interps, "interpolations", null);
		oc.write(enabled, "enabled", true);
		oc.write(useRandomStartColor, "useRandomStartColor", false);
		oc.write(cycle, "cycle", false);
		oc.write(fixedDuration, "fixedDuration", 0.125f);
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		colors = new SafeArrayList<ColorRGBA>(ColorRGBA.class, ic.readSavableArrayList("colors", null));
		Map<String,Vector2f> interps = (Map<String,Vector2f>)ic.readStringSavableMap("interpolations", null);
		for (String in : interps.keySet()) {
			String name = in.substring(0,in.indexOf(":"));
			interpolations.add(Interpolation.getInterpolationByName(name));
		}
		enabled = ic.readBoolean("enabled", true);
		useRandomStartColor = ic.readBoolean("useRandomStartColor", false);
		cycle = ic.readBoolean("cycle", false);
		fixedDuration = ic.readFloat("fixedDuration", 0.125f);
	}

	@Override
	public ParticleInfluencer clone() {
		try {
			ColorInfluencer clone = (ColorInfluencer) super.clone();
			clone.colors.addAll(colors);
			clone.interpolations.addAll(interpolations);
			clone.enabled = enabled;
			clone.useRandomStartColor = useRandomStartColor;
			clone.cycle = cycle;
			clone.fixedDuration = fixedDuration;
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
	
	/**
	 * Animated texture should cycle and use the provided duration between frames (0 diables cycling)
	 * @param fixedDuration duration between step updates
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
	 * Returns the current duration used between steps for cycled animation
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
		return ColorInfluencer.class;
	}
}
