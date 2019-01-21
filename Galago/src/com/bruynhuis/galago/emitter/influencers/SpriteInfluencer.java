package com.bruynhuis.galago.emitter.influencers;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import java.io.IOException;
import com.bruynhuis.galago.emitter.particle.ParticleData;

/**
 *
 * @author t0neg0d
 */
public class SpriteInfluencer implements ParticleInfluencer {
	private boolean enabled = true;
	private boolean useRandomImage = false;
	private boolean animate = true;
	private int totalFrames = -1;
	private float fixedDuration = 0f;
	private boolean cycle = false;
	private transient float targetInterval;
	private int[] frameSequence = null;
	
	@Override
	public void update(ParticleData p, float tpf) {
		if (enabled) {
			if (animate) {
				p.spriteInterval += tpf;
				targetInterval = (cycle) ? fixedDuration : p.spriteDuration;
				if (p.spriteInterval >= targetInterval) {
					updateFrame(p);
				}
			}
		}
	}
	
	private void updateFrame(ParticleData p) {
		if (frameSequence == null) {
			p.spriteCol++;
			if (p.spriteCol == p.emitter.getSpriteColCount()) {
				p.spriteCol = 0;
				p.spriteRow++;
				if (p.spriteRow == p.emitter.getSpriteRowCount())
					p.spriteRow = 0;
			}
		} else {
			p.spriteIndex++;
			if (p.spriteIndex == frameSequence.length)
				p.spriteIndex = 0;
			p.spriteRow = (int)FastMath.floor(frameSequence[p.spriteIndex]/p.emitter.getSpriteRowCount())-2;
			p.spriteCol = (int)frameSequence[p.spriteIndex]%p.emitter.getSpriteColCount();
		}
		p.spriteInterval -= targetInterval;
	}
	
	@Override
	public void initialize(ParticleData p) {
		if (totalFrames == -1) {
			totalFrames = p.emitter.getSpriteColCount()*p.emitter.getSpriteRowCount();
			if (totalFrames == 1) setAnimate(false);
		}
		if (useRandomImage) {
			if (frameSequence == null) {
				p.spriteIndex = FastMath.nextRandomInt(0,totalFrames-1);
				p.spriteRow = (int)FastMath.floor(p.spriteIndex/p.emitter.getSpriteRowCount())-1;
				p.spriteCol = (int)p.spriteIndex%p.emitter.getSpriteColCount();
			//	p.spriteCol = FastMath.nextRandomInt(0,frameSequence.length-1);
			//	p.spriteRow = FastMath.nextRandomInt(0,frameSequence.length-1);
			} else {
				p.spriteIndex = FastMath.nextRandomInt(0,frameSequence.length-1);
				p.spriteRow = (int)FastMath.floor(frameSequence[p.spriteIndex]/p.emitter.getSpriteRowCount())-1;
				p.spriteCol = (int)frameSequence[p.spriteIndex]%p.emitter.getSpriteColCount();
			}
		} else {
			if (frameSequence != null) {
				p.spriteIndex = frameSequence[0];
				p.spriteRow = (int)FastMath.floor(frameSequence[p.spriteIndex]/p.emitter.getSpriteRowCount())-2;
				p.spriteCol = (int)frameSequence[p.spriteIndex]%p.emitter.getSpriteColCount();
			} else {
				p.spriteIndex = 0;
				p.spriteRow = 0;
				p.spriteCol = 0;
			}
		}
		if (animate) {
			p.spriteInterval = 0;
			if (!cycle) {
				if (frameSequence == null)
					p.spriteDuration = p.startlife/(float)totalFrames;
				else
					p.spriteDuration = p.startlife/(float)frameSequence.length;
			}
		}
	}

	@Override
	public void reset(ParticleData p) {
		p.spriteIndex = 0;
		p.spriteCol = 0;
		p.spriteRow = 0;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Particles will/will not use sprite animations
	 * @param animate boolean
	 */
	public void setAnimate(boolean animate) { this.animate = animate; }
	/**
	 * Current animation state of particle
	 * @return Returns if particles use sprite animation
	 */
	public boolean getAnimate() { return this.animate; }
	/**
	 * Sets if particles should select a random start image from the provided sprite texture
	 * @param useRandomImage boolean
	 */
	public void setUseRandomStartImage(boolean useRandomImage) { this.useRandomImage = useRandomImage; }
	/**
	 * Returns if particles currently select a random start image from the provided sprite texture
	 * @param useRandomImage boolean
	 * @return 
	 */
	public boolean getUseRandomStartImage() { return this.useRandomImage; }
	
	public void setFrameSequence(int... frame) {
		frameSequence = frame;
	}
	
	public int[] getFrameSequence() { return this.frameSequence; }
	
	public void clearFrameSequence() {
		frameSequence = null;
	}
	
	/**
	 * Animated texture should cycle and use the provided duration between frames (0 diables cycling)
	 * @param fixedDuration duration between frame updates
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
	 * Returns the current duration used between frames for cycled animation
	 * @return 
	 */
	public float getFixedDuration() { return this.fixedDuration; }
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		/*
		private boolean enabled = true;
		private boolean useRandomImage = false;
		private boolean animate = true;
		private float fixedDuration = 0f;
		private int[] frameSequence = null;
		 */
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(useRandomImage, "useRandomImage", false);
		oc.write(animate, "animate", true);
		oc.write(fixedDuration, "fixedDuration", 0f);
		oc.write(enabled, "enabled", true);
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		useRandomImage = ic.readBoolean("useRandomImage", false);
		animate = ic.readBoolean("animate", true);
		fixedDuration = ic.readFloat("fixedDuration", 0f);
		enabled = ic.readBoolean("enabled", true);
	}
	
	@Override
	public ParticleInfluencer clone() {
		try {
			SpriteInfluencer clone = (SpriteInfluencer) super.clone();
			clone.setAnimate(animate);
			clone.setFixedDuration(fixedDuration);
			clone.setUseRandomStartImage(useRandomImage);
			clone.setFrameSequence(frameSequence);
			clone.setEnabled(enabled);
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}

	@Override
	public Class getInfluencerClass() {
		return SpriteInfluencer.class;
	}
}
