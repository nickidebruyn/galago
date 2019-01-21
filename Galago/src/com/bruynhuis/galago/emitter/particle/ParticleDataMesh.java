package com.bruynhuis.galago.emitter.particle;

import com.jme3.math.Matrix3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Mesh;
import com.bruynhuis.galago.emitter.Emitter;

/**
 *
 * @author t0neg0d
 */
public abstract class ParticleDataMesh extends Mesh {
	
	/**
	 * The template mesh to use for defining a particle
	 * @param mesh The asset model to extract buffers from
	 */
	public abstract void extractTemplateFromMesh(Mesh mesh);
	
    /**
     * Initialize mesh data.
     * 
     * @param emitter The emitter which will use this <code>ParticleDataMesh</code>.
     * @param numParticles The maxmimum number of particles to simulate
     */
    public abstract void initParticleData(Emitter emitter, int numParticles);
    
    /**
     * Set the images on the X and Y coordinates
     * @param imagesX Images on the X coordinate
     * @param imagesY Images on the Y coordinate
     */
    public abstract void setImagesXY(int imagesX, int imagesY);
    
    /**
     * Update the particle visual data. Typically called every frame.
     */
    public abstract void updateParticleData(ParticleData[] particles, Camera cam, Matrix3f inverseRotation);

}
