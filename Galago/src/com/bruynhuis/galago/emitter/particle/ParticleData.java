package com.bruynhuis.galago.emitter.particle;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.util.HashMap;
import java.util.Map;
import com.bruynhuis.galago.emitter.Emitter;
import com.bruynhuis.galago.emitter.Interpolation;
import com.bruynhuis.galago.emitter.influencers.ParticleInfluencer;

/**
 *
 * @author t0neg0d
 */
public class ParticleData {
    
	/**
     * ParticleData velocity.
     */
    public final Vector3f velocity = new Vector3f();
    public final Vector3f reverseVelocity = new Vector3f();
    /**
     * Current particle position
     */
    public final Vector3f position = new Vector3f();
    
    /**
     * ParticleData color
     */
    public final ColorRGBA color = new ColorRGBA(1,1,1,1);
    public int colorIndex = 0;
	public float colorInterval = 0f;
	public float colorDuration = 1f;
	public Interpolation colorInterpolation;
	/**
	 * Particle alpha
	 */
	public float alpha = 1;
	public int alphaIndex = 0;
	public float alphaInterval = 0;
	public float alphaDuration = 1;
	public Interpolation alphaInterpolation;
	
	/**
	 * The position of the emitter when the particle was released.
	 */
	public final Vector3f emitterPosition = new Vector3f();
	public final Vector3f initialPosition = new Vector3f();
	public final Vector3f randomOffset = new Vector3f();
    
	/**
	 * The parent particle emitter
	 */
	public Emitter emitter;
	
	/**
	 * The particles index
	 */
	public int index;
	
	/**
	 * The force at which the particle was emitted
	 */
	public float force;
	public float tangentForce;
	
    /**
     * ParticleData size or radius.
     */
    public Vector3f size = new Vector3f(1f,1f,1f);
	public Vector3f startSize = new Vector3f(1,1,1);
	public Vector3f endSize = new Vector3f(0,0,0);
	public int sizeIndex = 0;
	public float sizeInterval = 0;
	public float sizeDuration = 1;
	public Interpolation sizeInterpolation;
    
	/**
	 * 
	 */
	public int destinationIndex = 0;
	public Vector3f previousPosition = new Vector3f();
	public float destinationInterval = 0;
	public float destinationDuration = 1;
	public Interpolation destinationInterpolation;
    
    /**
	 * 
	 */
	public int directionIndex = 0;
	public float directionInterval = 0;
	public float directionDuration = 1;
	public Interpolation directionInterpolation;
    
    /**
     * ParticleData remaining life, in seconds.
     */
    public float life;
    
    /**
     * The total particle lifespan
     */
    public float startlife;
    
    /**
     * The current blend value
     */
    public float blend;
    public float interpBlend;
	
    /**
     * ParticleData rotation angle per axis (in radians).
     */
    public Vector3f angles = new Vector3f();
	
    /**
     * ParticleData rotation angle speed per axis (in radians).
     */
    public Vector3f rotationSpeed = new Vector3f();
	public Vector3f startRotationSpeed = new Vector3f();
	public Vector3f endRotationSpeed = new Vector3f();
	public int rotationIndex = 0;
	public float rotationInterval = 0;
	public float rotationDuration = 1;
	public Interpolation rotationInterpolation;
	
	/**
	 * The direction each axis' rotation will rotate in
	 */
	public boolean rotateDirectionX = true;
	public boolean rotateDirectionY = true;
	public boolean rotateDirectionZ = true;
	
	/**
	 * The index of the emitter shape's mesh triangle the particle was emitted from
	 */
	public int triangleIndex;
	
    /**
     * ParticleData image index. 
     */
    public int spriteCol = 0;
	public int spriteRow = 0;
	public int spriteIndex = 0;
	public float spriteInterval = 0;
	public float spriteDuration = 1;
	public Interpolation spriteInterpolation;
	
	/**
	 * The state of the particle
	 */
	public boolean active = false;
	
	public Vector3f upVec = new Vector3f(0,1,0);
	public Vector3f tempV3 = new Vector3f();
	
	public float initialLength;
	
	public boolean collision = false;
	public float collisionInterval = 0;
	
	/**
	 * A strage facility for per-particle data used by influencers
	 */
	Map<String,Object> data = new HashMap();
	
	/**
	 * Sets data to store with the particle
	 * @param key The data's map key
	 * @param data The data
	 */
	public void setData(String key, Object data) {
		this.data.put(key, data);
	}
	
	/**
	 * Returns the stored per-particle data
	 * @param key The data's map key
	 * @return The data
	 */
	public Object getData(String key) {
		return this.data.get(key);
	}
	
	public void update(float tpf) {
		if (!emitter.getUseStaticParticles()) {
			life -= tpf;
			if (life <= 0) {
				reset();
				return;
			}
			blend = 1.0f * (startlife-life) / startlife;
			interpBlend = emitter.getInterpolation().apply(blend);
		}
		for (ParticleInfluencer influencer : emitter.getInfluencers()) {
			influencer.update(this, tpf);
		}
		
		tempV3.set(velocity).multLocal(tpf);
		position.addLocal(tempV3);
		
		// TODO: Test this!
		if (emitter.getUseStaticParticles()) {
			emitter.getShape().setNext(triangleIndex);
			if (emitter.getUseRandomEmissionPoint()) {
				position.set(emitter.getShape().getNextTranslation().addLocal(randomOffset));
			} else {
				position.set(emitter.getShape().getNextTranslation());
			}
		}
	}
	
	/**
	 * Called once per particle use when the particle is emitted
	 */
	public void initialize() {
		emitter.incActiveParticleCount();
		active = true;
		blend = 0;
		size.set(0,0,0);
		if (emitter.getLifeMin() != emitter.getLifeMax())
			startlife = (emitter.getLifeMax() - emitter.getLifeMin()) * FastMath.nextRandomFloat() + emitter.getLifeMin();
		else
			startlife = emitter.getLifeMax();
		life = startlife;
		if (emitter.getForceMin() != emitter.getForceMax())
			force = (emitter.getForceMax() - emitter.getForceMin()) * FastMath.nextRandomFloat() + emitter.getForceMin();
		else
			force = emitter.getForceMax();
		emitter.getShape().setNext();
		triangleIndex = emitter.getShape().getTriangleIndex();
		if (!emitter.getUseRandomEmissionPoint()) {
			position.set(
				emitter.getShape().getNextTranslation()
			);
		} else {
			randomOffset.set(emitter.getShape().getRandomTranslation());
			position.set(
				emitter.getShape().getNextTranslation().add(randomOffset)
			);
		}
		velocity.set(
			emitter.getShape().getNextDirection()
		).normalizeLocal().multLocal(force);
		
		initialLength = velocity.length();
		initialPosition.set(
			emitter.getEmitterNode().getWorldTranslation()
		);
	//	spriteIndex = 0;
	//	spriteCol = 0;
	//	spriteRow = 0;
		
		for (ParticleInfluencer influencer : emitter.getInfluencers()) {
			influencer.initialize(this);
		}
		
		switch (emitter.getParticleEmissionPoint()) {
			case Particle_Edge_Bottom:
				tempV3.set(emitter.getShape().getNextDirection()).normalizeLocal();
				tempV3.multLocal(startSize.getY());
				position.addLocal(tempV3);
				break;
			case Particle_Edge_Top:
				tempV3.set(emitter.getShape().getNextDirection()).normalizeLocal();
				tempV3.multLocal(startSize.getY());
				position.subtractLocal(tempV3);
				break;
		}
	}
	
	/**
	 * Called once per particle use when the particle finishes it's life cycle
	 */
	public void reset() {
		active = false;
		if (emitter.getActiveParticleCount() > 0)
			emitter.decActiveParticleCount();
		for (ParticleInfluencer influencer : emitter.getInfluencers()) {
			influencer.reset(this);
		}
		emitter.setNextIndex(index);
	}
}