package com.bruynhuis.galago.emitter;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.util.SafeArrayList;
import com.bruynhuis.galago.emitter.EmitterMesh.DirectionType;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bruynhuis.galago.emitter.influencers.ParticleInfluencer;
import com.bruynhuis.galago.emitter.particle.ParticleData;
import com.bruynhuis.galago.emitter.particle.ParticleDataMesh;
import com.bruynhuis.galago.emitter.particle.ParticleDataPointMesh;
import com.bruynhuis.galago.emitter.particle.ParticleDataTriMesh;
import com.bruynhuis.galago.emitter.shapes.TriangleEmitterShape;
import java.util.ArrayList;
import java.util.UUID;

/**
 *
 * @author t0neg0d
 */
public class Emitter implements Control, Cloneable {
	public static enum BillboardMode {
		/**
		 * Facing direction follows the velocity as it changes
		 */
		Velocity,
		/**
		 * Facing direction follows the velocity as it changes, Y of particle always faces Z of velocity
		 */
		Velocity_Z_Up,
		/**
		 * Facing direction follows the velocity as it changes, Y of particle always faces Z of velocity, Up of the particle always faces X
		 */
		Velocity_Z_Up_Y_Left,
		/**
		 * Facing direction remains constant to the face of the particle emitter shape that the particle was emitted from
		 */
		Normal,
		/**
		 * Facing direction remains constant for X, Z axis' to the face of the particle emitter shape that the particle was emitted from. Y axis maps to UNIT_Y
		 */
		Normal_Y_Up,
		/**
		 * ParticleData always faces camera
		 */
		Camera,
		/**
		 * ParticleData always faces X axis
		 */
		UNIT_X,
		/**
		 * ParticleData always faces Y axis
		 */
		UNIT_Y,
		/**
		 * ParticleData always faces Z axis
		 */
		UNIT_Z
	}
	public static enum ForcedStretchAxis {
		X, Y, Z
	}
	public static enum ParticleEmissionPoint {
		Particle_Center,
		Particle_Edge_Top,
		Particle_Edge_Bottom
	}
	
	private Spatial spatial;
	private String name;
	EmitterMesh emitterShape = new EmitterMesh();
	Class particleType = ParticleDataTriMesh.class;
	ParticleDataMesh mesh = null;
	Mesh template = null;
	ParticleData[] particles;
	SafeArrayList<ParticleInfluencer> influencers = new SafeArrayList(ParticleInfluencer.class);
	Node emitterNode, particleNode, emitterTestNode, particleTestNode;
	
	// ParticleData info
	private int maxParticles;
	private float forceMax = .5f;
	private float forceMin = .15f;
	private float lifeMin = 0.999f;
	private float lifeMax = 0.999f;
	protected int activeParticleCount = 0;
	protected Interpolation interpolation = Interpolation.linear;
	
	// Emitter info
	int nextIndex = 0;
	private float targetInterval = .00015f, currentInterval = 0;
	private int emissionsPerSecond, totalParticlesThisEmission, particlesPerEmission;
	private float tpfThreshold = 1f/400f;
	private Matrix3f inverseRotation = Matrix3f.IDENTITY.clone();
	private boolean useStaticParticles = false;
	private boolean useRandomEmissionPoint = false;
	private boolean useSequentialEmissionFace = false;
	private boolean useSequentialSkipPattern = false;
	private boolean useVelocityStretching = false;
	private float velocityStretchFactor = 0.35f;
	private ForcedStretchAxis stretchAxis = ForcedStretchAxis.Y;
	private ParticleEmissionPoint particleEmissionPoint = ParticleEmissionPoint.Particle_Center;
	private DirectionType directionType = DirectionType.Random;
	
	// Material information
	private AssetManager assetManager;
	private Material mat, testMat, userDefinedMat = null;
	private boolean applyLightingTransform = false;
	private String uniformName = "Texture";
	private Texture tex;
	private String texturePath;
	private float spriteWidth = -1, spriteHeight = -1;
	private int spriteCols = 1, spriteRows = 1;
	
	private BillboardMode billboardMode = BillboardMode.Camera;
	private boolean particlesFollowEmitter = false;
	
	private boolean enabled = false;
	private boolean requiresUpdate = false;
	private boolean postRequiresUpdate = false;
	
	private boolean TEST_EMITTER = false;
	private boolean TEST_PARTICLES = false;
	
	private boolean emitterInitialized = false;
	
	// Emitter animation
	Node esAnimNode = null;
	boolean esNodeExists = true;
	AnimControl esAnimControl = null;
	AnimChannel esAnimChannel = null;
	String esAnimName = "";
	float esAnimSpeed = 1;
	float esAnimBlendTime = 1;
	LoopMode esAnimLoopMode = LoopMode.Loop;
	
	// Particle animation
	Node ptAnimNode = null;
	AnimControl ptAnimControl = null;
	AnimChannel ptAnimChannel = null;
	String ptAnimName = "";
	float ptAnimSpeed = 1;
	float ptAnimBlendTime = 1;
	LoopMode ptAnimLoopMode = LoopMode.Loop;
	
	/**
	 * Creates a new instance of the Emitter
	 */
	public Emitter() {
		emitterNode = new Node();
		emitterTestNode = new Node();
		particleNode = new Node();
		particleTestNode = new Node();
	}
	
	/**
	 * Sets the mesh class used to create the particle mesh.
	 * For example:
	 * ParticleDataTriMesh.class - A quad-based particle mesh
	 * ParticleDataImpostorMesh.class - A star-shaped impostor mesh
	 * @param <T>
	 * @param t The Mesh class used to create the particle Mesh
	 */
	public <T extends ParticleDataMesh> void setParticleType(Class<T> t) {
		this.particleType = t;
	}
	
	/**
	 * Sets the mesh class used to create the particle mesh.
	 * For example:
	 * ParticleDataTemplateMesh.class - Uses a supplied mesh as a template for particles
	 * @param <T>
	 * @param t The Mesh class used to create the particle Mesh
	 * @param template The template mesh used to define a single particle
	 */
	public <T extends ParticleDataMesh> void setParticleType(Class<T> t, Mesh template) {
		this.particleType = t;
		this.template = template;
	}
	
	/**
	 * Sets the mesh class used to create the particle mesh.
	 * For example:
	 * ParticleDataTemplateMesh.class - Uses a supplied mesh as a template for particles
	 * NOTE: This method is supplied for use with animated particles.
	 * @param <T>
	 * @param t The Mesh class used to create the particle Mesh
	 * @param template The Node to extract the template mesh used to define a single particle
	 */
	public <T extends ParticleDataMesh> void setParticleType(Class<T> t, Node template) {
		if (ptAnimNode != null)
			ptAnimNode.removeFromParent();
		
		this.particleType = t;
		this.ptAnimNode = template;
		this.template = ((Geometry)ptAnimNode.getChild(0)).getMesh();
		ptAnimNode.setLocalScale(0);
		
		ptAnimControl = ptAnimNode.getControl(AnimControl.class);
		if (ptAnimControl != null) {
			ptAnimChannel = ptAnimControl.createChannel();
		}
		
		if (emitterInitialized) {
			if (getSpatial() != null)
				((Node)spatial).attachChild(ptAnimNode);
		}
	}
	
	/**
	 * Returns the Class defined for the particle type.
	 * (ex. ParticleDataTriMesh.class - a quad-base particle)
	 * @return 
	 */
	public Class getParticleType() {
		return this.particleType;
	}
	
	/**
	 * Returns the Mesh defined as a template for a single particle
	 * @return The Mesh to use as a particle template
	 */
	public Mesh getParticleMeshTemplate() {
		return this.template;
	}
	
	public void setParticleAnimation(String ptAnimName, float ptAnimSpeed, float ptAnimBlendTime, LoopMode ptAnimLoopMode) {
		this.ptAnimName = ptAnimName;
		this.ptAnimSpeed = ptAnimSpeed;
		this.ptAnimBlendTime = ptAnimBlendTime;
		this.ptAnimLoopMode = ptAnimLoopMode;
		
		if (emitterInitialized && ptAnimControl != null) {
			ptAnimChannel.setAnim(ptAnimName, ptAnimBlendTime);
			ptAnimChannel.setSpeed(ptAnimSpeed);
			ptAnimChannel.setLoopMode(ptAnimLoopMode);
		}
	}
	
	/**
	 * Sets the maximum number of particles the emitter will manage
	 * @param maxParticles 
	 */
	public void setMaxParticles(int maxParticles) {
		this.maxParticles = maxParticles;
		if (emitterInitialized) {
			// rebuild emitter
		}
	}
	
	/**
	 * Set the user defined name of the influencer.  This is used in naming
	 * The Nodes generated for the emitter shape, particle node, and both test
	 * nodes.
	 * NOTE: If no name is set, a unique name is generated for the emitter.
	 * @param name The String name for the influencer
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the name of the emitter.
	 * @return The String name of the emitter
	 */
	public String getName() {
		return this.name;
	}
	
	private String generateName() {
		return UUID.randomUUID().toString();
	}
	
	private void initMaterials() {
		mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
		mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
		
		testMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		testMat.setColor("Color", ColorRGBA.Blue);
		testMat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
		testMat.getAdditionalRenderState().setWireframe(true);
	}
	
	private <T extends ParticleDataMesh> void initParticles(Class<T> t, Mesh template) {
		try {
			this.mesh = t.newInstance();
			if (template != null) {
				this.mesh.extractTemplateFromMesh(template);
				this.template = template;
			}
			initParticles();
		} catch (InstantiationException | IllegalAccessException ex) {
			Logger.getLogger(Emitter.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private void initParticles() {
		particles = new ParticleData[maxParticles];
		
		for (int i = 0; i < maxParticles; i++) {
			particles[i] = new ParticleData();
			particles[i].emitter = this;
			particles[i].index = i;
			particles[i].reset();
		}
		
		mesh.initParticleData(this, maxParticles);
	}
	
	/**
	 * Creates a single triangle emitter shape
	 */
	public void setShapeSimpleEmitter() {
		setShape(new TriangleEmitterShape(1));
		requiresUpdate = true;
	}
	
	/**
	 * Sets the particle emitter shape to the specified mesh
	 * @param mesh The Mesh to use as the particle emitter shape
	 */
	public final void setShape(Mesh mesh) {
		emitterShape.setShape(this, mesh);
		if (!emitterTestNode.getChildren().isEmpty()) {
			emitterTestNode.getChild(0).removeFromParent();
			Geometry testGeom = new Geometry();
			testGeom.setMesh(emitterShape.getMesh());
			emitterTestNode.attachChild(testGeom);
			emitterTestNode.setMaterial(testMat);
		}
		requiresUpdate = true;
	}
	
	/**
	 * Sets the particle emitter shape to the specified mesh
	 * NOTE: This method is supplied for use with animated emitter shapes.
	 * @param n The node containing the Mesh used as the particle emitter shape
	 * @param sceneAlreadyContainsEmitterShape Tells the emitter if shape is an asset that is already contained within the scene.  This allows you to manage animations via the asset in place of calling setEmitterAnimation
	 */
	public final void setShape(Node n, boolean sceneAlreadyContainsEmitterShape) {
		if (esAnimNode != null)
			esAnimNode.removeFromParent();
		
		esAnimNode = n;
		esNodeExists = sceneAlreadyContainsEmitterShape;
		if (!esNodeExists)
			esAnimNode.setLocalScale(0);
		
		Mesh shape = ((Geometry)n.getChild(0)).getMesh();
		setShape(shape);
		
		esAnimControl = esAnimNode.getControl(AnimControl.class);
		if (esAnimControl != null) {
			esAnimChannel = esAnimControl.createChannel();
		}
		
		if (emitterInitialized) {
			if (getSpatial() != null)
				((Node)spatial).attachChild(esAnimNode);
		}
	}
	
	/**
	 * Returns the current ParticleData Emitter's EmitterMesh
	 * @return The EmitterMesh containing the specified shape Mesh
	 */
	public EmitterMesh getShape() {
		return emitterShape;
	}
	
	/**
	 * Returns if the current emitter shape has an associated animation Control
	 * @return 
	 */
	public boolean getIsShapeAnimated() {
		return !(esAnimControl == null);
	}
	
	/**
	 * Called to set the emitter shape's animation IF the emitter shape is not pointing to a scene asset
	 * @param esAnimName The String name of the animation
	 * @param esAnimSpeed The speed at which the animation should run
	 * @param esAnimBlendTime The blend time to use when switching animations
	 */
	public void setEmitterAnimation(String esAnimName, float esAnimSpeed, float esAnimBlendTime, LoopMode esAnimLoopMode) {
		this.esAnimName = esAnimName;
		this.esAnimSpeed = esAnimSpeed;
		this.esAnimBlendTime = esAnimBlendTime;
		this.esAnimLoopMode = esAnimLoopMode;
		
		if (emitterInitialized && esAnimControl != null) {
			esAnimChannel.setAnim(esAnimName, esAnimBlendTime);
			esAnimChannel.setSpeed(esAnimSpeed);
			esAnimChannel.setLoopMode(esAnimLoopMode);
		}
	}
	
	/**
	 * Specifies the number of times the particle emitter will emit particles over the course of one second
	 * @param emissionsPerSecond The number of particle emissions per second
	 */
	public void setEmissionsPerSecond(int emissionsPerSecond) {
		this.emissionsPerSecond = emissionsPerSecond;
		targetInterval = 1f/emissionsPerSecond;
		requiresUpdate = true;
	}
	
	/**
	 * Return the number of times the particle emitter will emit particles over the course of one second
	 * @return 
	 */
	public int getEmissionsPerSecond() { return this.emissionsPerSecond; }
	
	/**
	 * Specifies the number of particles to be emitted per emission.
	 * @param particlesPerEmission The number of particle to emit per emission
	 */
	public void setParticlesPerEmission(int particlesPerEmission) {
		this.particlesPerEmission = particlesPerEmission;
		requiresUpdate = true;
	}
	
	/**
	 * Returns the number of particles to be emitted per emission.
	 * @return 
	 */
	public int getParticlesPerEmission() { return this.particlesPerEmission; }
	
	/**
	 * Defines how particles are emitted from the face of the emitter shape.
	 * For example:
	 * Normal will emit in the direction of the face's normal
	 * NormalNegate will emit the the opposite direction of the face's normal
	 * RandomTangent will select a random tagent to the face's normal.
	 * @param directionType 
	 */
	public void setDirectionType(DirectionType directionType) {
		this.directionType = directionType;
	}
	
	/**
	 * Returns the direction in which the particles will be emitted relative
	 * to the emitter shape's selected face.
	 * @return 
	 */
	public DirectionType getDirectionType() { return this.directionType; }
	
	public void setTargetFPS(float fps) {
		tpfThreshold = 1f/fps;
	}
	
	/**
	 * Particles are created as staticly placed, with no velocity.  Particles set to static with remain in place and follow the emitter shape's animations.
	 * @param useStaticParticles 
	 */
	public void setUseStaticParticles(boolean useStaticParticles) {
		this.useStaticParticles = useStaticParticles;
		requiresUpdate = true;
	}
	
	/**
	 * Returns if particles are flagged as static
	 * @return Current state of static particle flag
	 */
	public boolean getUseStaticParticles() {
		return this.useStaticParticles;
	}
	
	/**
	 * Enable or disable to use of particle stretching
	 * @param useVelocityStretching 
	 */
	public void setUseVelocityStretching(boolean useVelocityStretching) {
		this.useVelocityStretching = useVelocityStretching;
		requiresUpdate = true;
	}
	
	/**
	 * Returns if the emitter will use particle stretching
	 * @return 
	 */
	public boolean getUseVelocityStretching() { return this.useVelocityStretching; }
	
	/**
	 * Sets the magnitude of the particle stretch
	 * @param velocityStretchFactor 
	 */
	public void setVelocityStretchFactor(float velocityStretchFactor) {
		this.velocityStretchFactor = velocityStretchFactor;
		requiresUpdate = true;
	}
	
	/**
	 * Gets the magnitude of the particle stretching
	 * @return 
	 */
	public float getVelocityStretchFactor() { return this.velocityStretchFactor; }
	
	/**
	 * Forces the stretch to occure along the specified axis relative to the particle's velocity
	 * @param axis The axis to stretch against.  Default is Y
	 */
	public void setForcedStretchAxis(ForcedStretchAxis axis) {
		this.stretchAxis = axis;
		requiresUpdate = true;
	}
	
	/**
	 * Returns the axis to stretch particles against.  Axis is relative to the particles velocity.
	 * @return 
	 */
	public ForcedStretchAxis getForcedStretchAxis() { return this.stretchAxis; }
	
	/**
	 * Determine how the particle is placed when first emitted.  The default is the particles 0,0,0 point
	 * @param particleEmissionPoint 
	 */
	public void setParticleEmissionPoint(ParticleEmissionPoint particleEmissionPoint) {
		this.particleEmissionPoint = particleEmissionPoint;
		requiresUpdate = true;
	}
	
	/**
	 * Returns how the particle is placed when first emitted.
	 * @return 
	 */
	public ParticleEmissionPoint getParticleEmissionPoint() {
		return this.particleEmissionPoint;
	}
	
	/**
	 * Particles are effected by updates to the translation of the emitter node.  This option is set to false by default
	 * @param particlesFollowEmitter Particles should/should not update according to the emitter node's translation updates
	 */
	public void setParticlesFollowEmitter(boolean particlesFollowEmitter) {
		this.particlesFollowEmitter = particlesFollowEmitter;
		requiresUpdate = true;
	}
	
	/**
	 * Returns if the particles are set to update according to the emitter node's translation updates
	 * @return Current state of the follows emitter flag
	 */
	public boolean getParticlesFollowEmitter() { return this.particlesFollowEmitter; }
	
	/**
	 * By default, emission happens from the direct center of the selected emitter shape face.  This
	 * flag enables selecting a random point of emission within the selected face.
	 * @param useRandomEmissionPoint 
	 */
	public void setUseRandomEmissionPoint(boolean useRandomEmissionPoint) {
		this.useRandomEmissionPoint = useRandomEmissionPoint;
		requiresUpdate = true;
	}
	
	/**
	 * Returns if particle emission uses a randomly selected point on the emitter shape's selected
	 * face or it's absolute center.  Center emission is default.
	 * @return 
	 */
	public boolean getUseRandomEmissionPoint() { return this.useRandomEmissionPoint; }
	
	/**
	 * For use with emitter shapes that contain more than one face.
	 * By default, the face selected for emission is random.  Use this to enforce
	 * emission in the sequential order the faces are created in the emitter shape mesh.
	 * @param useSequentialEmissionFace 
	 */
	public void setUseSequentialEmissionFace(boolean useSequentialEmissionFace) {
		this.useSequentialEmissionFace = useSequentialEmissionFace;
		requiresUpdate = true;
	}
	
	/**
	 * Returns if emission happens in the sequential order the faces of the emitter
	 * shape mesh are defined.
	 * @return 
	 */
	public boolean getUseSequentialEmissionFace() { return this.useSequentialEmissionFace; }
	
	/**
	 * Enabling skip pattern will use every other face in the emitter shape.  This
	 * stops the clustering of two particles per quad that makes up the the emitter
	 * shape.
	 * @param useSequentialSkipPattern 
	 */
	public void setUseSequentialSkipPattern(boolean useSequentialSkipPattern) {
		this.useSequentialSkipPattern = useSequentialSkipPattern;
		requiresUpdate = true;
	}
	
	/**
	 * Returns if the emitter will skip every other face in the sequential order the emitter
	 * shape faces are defined.
	 * @return 
	 */
	public boolean getUseSequentialSkipPattern() {
		return this.useSequentialSkipPattern;
	}
	
	/**
	 * Sets the default interpolation for the emitter will use
	 * @param interpolation 
	 */
	public void setInterpolation(Interpolation interpolation) {
		this.interpolation = interpolation;
		requiresUpdate = true;
	}
	
	/**
	 * Returns the default interpolation used by the emitter
	 * @return 
	 */
	public Interpolation getInterpolation() {
		return this.interpolation;
	}
	
	//<editor-fold desc="Emission Force & Particle Lifespan">
	/**
	 * Sets the inner and outter bounds of the time a particle will remain alive (active)
	 * @param lifeMin The minimum time a particle must remian alive once emitted
	 * @param lifeMax The maximum time a particle can remain alive once emitted
	 */
	public void setLifeMinMax(float lifeMin, float lifeMax) {
		this.lifeMin = lifeMin;
		this.lifeMax = lifeMax;
		requiresUpdate = true;
	}
	
	/**
	 * Sets the inner and outter bounds of the time a particle will remain alive (active) to a fixed duration of time
	 * @param life The fixed duration an emitted particle will remain alive
	 */
	public void setLife(float life) {
		this.lifeMin = life;
		this.lifeMax = life;
		requiresUpdate = true;
	}
	
	/**
	 * Sets the outter bounds of the time a particle will remain alive (active)
	 * @param lifeMax The maximum time a particle can remain alive once emitted
	 */
	public void setLifeMax(float lifeMax) {
		this.lifeMax = lifeMax;
		requiresUpdate = true;
	}
	
	/**
	 * Returns the maximum time a particle can remain alive once emitted.
	 * @return The maximum time a particle can remain alive once emitted
	 */
	public float getLifeMax() { return this.lifeMax; }
	
	/**
	 * Sets the inner bounds of the time a particle will remain alive (active)
	 * @param lifeMin The minimum time a particle must remian alive once emitted
	 */
	public void setLifeMin(float lifeMin) {
		this.lifeMin = lifeMin;
		requiresUpdate = true;
	}
	
	/**
	 * Returns the minimum time a particle must remian alive once emitted
	 * @return The minimum time a particle must remian alive once emitted
	 */
	public float getLifeMin() { return this.lifeMin; }
	
	/**
	 * Sets the inner and outter bounds of the initial force with which the particle is emitted.  This directly effects the initial velocity vector of the particle.
	 * @param forceMin The minimum force with which the particle will be emitted
	 * @param forceMax The maximum force with which the particle can be emitted
	 */
	public void setForceMinMax(float forceMin, float forceMax) {
		this.forceMin = forceMin;
		this.forceMax = forceMax;
		requiresUpdate = true;
	}
	
	/**
	 * Sets the inner and outter bounds of the initial force with which the particle is emitted to a fixed ammount.  This directly effects the initial velocity vector of the particle.
	 * @param force The force with which the particle will be emitted
	 */
	public void setForce(float force) {
		this.forceMin = force;
		this.forceMax = force;
		requiresUpdate = true;
	}
	
	/**
	 * Sets the inner bounds of the initial force with which the particle is emitted.  This directly effects the initial velocity vector of the particle.
	 * @param forceMin The minimum force with which the particle will be emitted
	 */
	public void setForceMin(float forceMin) {
		this.forceMin = forceMin;
		requiresUpdate = true;
	}
	
	/**
	 * Sets the outter bounds of the initial force with which the particle is emitted.  This directly effects the initial velocity vector of the particle.
	 * @param forceMax The maximum force with which the particle can be emitted
	 */
	public void setForceMax(float forceMax) {
		this.forceMax = forceMax;
		requiresUpdate = true;
	}
	
	/**
	 * Returns the minimum force with which the particle will be emitted
	 * @return The minimum force with which the particle will be emitted
	 */
	public float getForceMin() { return this.forceMin; }
	
	/**
	 * Returns the maximum force with which the particle can be emitted
	 * @return The maximum force with which the particle can be emitted
	 */
	public float getForceMax() { return this.forceMax; }
	//</editor-fold>
	
	//<editor-fold desc="Add/Remove Influencers">
	/**
	 * Returns the maximum number of particles managed by the emitter
	 * @return 
	 */
	public int getMaxParticles() { return this.maxParticles; }
	
	/**
	 * Adds a series of influencers
	 * @param influencers The list of influencers
	 */
	public void addInfluencers(ParticleInfluencer... influencers) {
		for (ParticleInfluencer pi : influencers) {
			addInfluencer(pi);
		}
	}
	
	/**
	 * Adds a new ParticleData Influencer to the chain of influencers that will effect particles
	 * @param influencer The particle influencer to add to the chain
	 */
	public final void addInfluencer(ParticleInfluencer influencer) {
	//	influencers.put(influencer.getInfluencerClass().getName(), influencer);
		influencers.add(influencer);
		requiresUpdate = true;
	}
	
	/**
	 * Returns the current chain of particle influencers
	 * @return The Collection of particle influencers
	 */
	public ParticleInfluencer[] getInfluencers() {
		return (ParticleInfluencer[])this.influencers.getArray();
	}
	
	/**
	 * Returns the first instance of a specified ParticleData Influencer type
	 * @param <T>
	 * @param c
	 * @return 
	 */
	public <T extends ParticleInfluencer> T getInfluencer(Class<T> c) {
		T ret = null;
		for (ParticleInfluencer pi : (ParticleInfluencer[])influencers.getArray()) {
			if (pi.getInfluencerClass() == c) {
				ret = (T)pi;
				break;
			}
		}
		return ret;
	//	return (T) influencers.get(c.getName());
	}
	
	/**
	 * Removes the specified influencer by class
	 * @param c The class of the influencer to remove
	 */
	public void removeInfluencer(Class c) {
		for (ParticleInfluencer pi : (ParticleInfluencer[])influencers.getArray()) {
			if (pi.getInfluencerClass() == c) {
				influencers.remove(pi);
				break;
			}
		}
		requiresUpdate = true;
	}
	
	/**
	 * Removes all influencers
	 */
	public void removeAllInfluencers() {
		influencers.clear();
		requiresUpdate = true;
	}
	//</editor-fold>
	
	//<editor-fold desc="Material & Particle Texture">
	/**
	 * Sets the texture to be used by particles, when calling this method, it is assumed that the image
	 * does not contain multiple sprite images
	 * @param texturePath The path of the texture to use
	 */
	public void setSprite(String texturePath) {
		setSpriteByCount(texturePath, uniformName, 1, 1);
	}
	
	/**
	 * Sets the texture to be used by particles, this can contain multiple images for random image selection or sprite animation of particles.
	 * @param texturePath The path of the texture to use
	 * @param numCols The number of sprite images per row
	 * @param numRows The number of rows containing sprite images
	 */
	public void setSprite(String texturePath, int numCols, int numRows) {
		setSpriteByCount(texturePath, uniformName, numCols, numRows);
	}
	
	/**
	 * Sets the texture to be used by particles, when calling this method, it is assumed that the image
	 * @param texturePath The path of the texture to use
	 * @param uniformName The uniform name used when setting the particle texture
	 */
	public void setSprite(String texturePath, String uniformName) {
		setSpriteByCount(texturePath, uniformName, 1, 1);
	}
	
	/**
	 * Sets the texture to be used by particles, this can contain multiple images for random image selection or sprite animation of particles.
	 * @param texturePath The path of the texture to use
	 * @param uniformName The uniform name used when setting the particle texture
	 * @param numCols The number of sprite images per row
	 * @param numRows The number of rows containing sprite images
	 */
	public void setSprite(String texturePath, String uniformName, int numCols, int numRows) {
		setSpriteByCount(texturePath, uniformName, numCols, numRows);
	}
	
	/**
	 * Sets the texture to be used by particles, this can contain multiple images for random image selection or sprite animation of particles.
	 * @param texturePath The path of the texture to use
	 * @param numCols The number of sprite images per row
	 * @param numRows The number of rows containing sprite images
	 */
	public void setSpriteByCount(String texturePath, int numCols, int numRows) {
		setSpriteByCount(texturePath, uniformName, numCols, numRows);
	}
	
	/**
	 * Sets the texture to be used by particles, this can contain multiple images for random image selection or sprite animation of particles.
	 * @param texturePath The path of the texture to use
	 * @param uniformName The uniform name used when setting the particle texture
	 * @param numCols The number of sprite images per row
	 * @param numRows The number of rows containing sprite images
	 */
	public void setSpriteByCount(String texturePath, String uniformName, int numCols, int numRows) {
		this.texturePath = texturePath;
		this.spriteCols = numCols;
		this.spriteRows = numRows;
		this.uniformName = uniformName;
		
		if (emitterInitialized) {
			tex = assetManager.loadTexture(texturePath);
			tex.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
			tex.setMagFilter(Texture.MagFilter.Bilinear);
			mat.setTexture(uniformName, tex);

			Image img = tex.getImage();
			int width = img.getWidth();
			int height = img.getHeight();

			spriteWidth = (int)(width/spriteCols);
			spriteHeight = (int)(height/spriteRows);
			
			mesh.setImagesXY(spriteCols,spriteRows);
			requiresUpdate = true;
		}
	}
	
	/**
	 * Returns the current material used by the emitter.
	 * @return 
	 */
	public Material getMaterial() { return this.mat; }
	
	/**
	 * Can be used to override the default Particle material.
	 * NOTE: If the color/diffuse uniform name differs from "Texture", the new uniform
	 * name must be set when calling one of the setSprite methods.
	 * @param mat The material
	 */
	public void setMaterial(Material mat) {
		setMaterial(mat, false);
	}
	
	/**
	 * Can be used to override the default Particle material.
	 * NOTE: If the color/diffuse uniform name differs from "Texture", the new uniform
	 * name must be set when calling one of the setSprite methods.
	 * @param mat The material
	 * @param applyLightingTransform Forces update of normals and should only be used if the emitter material uses a lighting shader
	 */
	public void setMaterial(Material mat, boolean applyLightingTransform) {
		setMaterial(mat, uniformName, applyLightingTransform);
	}
	
	/**
	 * Can be used to override the default Particle material.
	 * @param mat The material
	 * @param uniformName The material uniform name used for applying a color map (ex: Texture, ColorMap, DiffuseMap)
	 */
	public void setMaterial(Material mat, String uniformName) {
		setMaterial(mat, uniformName, false);
	}
	
	/**
	 * Can be used to override the default Particle material.
	 * @param mat The material
	 * @param uniformName The material uniform name used for applying a color map (ex: Texture, ColorMap, DiffuseMap)
	 * @param applyLightingTransform Forces update of normals and should only be used if the emitter material uses a lighting shader
	 */
	public void setMaterial(Material mat, String uniformName, boolean applyLightingTransform) {
		this.userDefinedMat = mat;
		this.applyLightingTransform = applyLightingTransform;
		this.uniformName = uniformName;
		
		if (emitterInitialized) {
			tex = assetManager.loadTexture(texturePath);
			tex.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
			tex.setMagFilter(Texture.MagFilter.Bilinear);
			mat.setTexture(uniformName, tex);
		}
		
		if (particleNode != null) {
			particleNode.setMaterial(mat);
			requiresUpdate = true;
		}
	}
	
	/**
	 * Returns the number of columns of sprite images in the specified texture
	 * @return The number of available sprite columns
	 */
	public int getSpriteColCount() { return this.spriteCols; }
	
	/**
	 * Returns the number of rows of sprite images in the specified texture
	 * @return The number of available sprite rows
	 */
	public int getSpriteRowCount() { return this.spriteRows; }
	
	/**
	 * Returns if the emitter will update normals for lighting materials
	 * @return 
	 */
	public boolean getApplyLightingTransform() {
		return this.applyLightingTransform;
	}
	//</editor-fold>
	
	/**
	 * Sets the billboard mode to be used by emitted particles.  The default mode is Camera
	 * @param billboardMode The billboard mode to use
	 */
	public void setBillboardMode(BillboardMode billboardMode) {
		this.billboardMode = billboardMode;
		requiresUpdate = true;
	}
	
	/**
	 * Returns the current selected BillboardMode used by emitted particles
	 * @return The current selected BillboardMode
	 */
	public BillboardMode getBillboardMode() {
		return billboardMode;
	}
	
	/**
	 * Enables the particle emitter.  The emitter is disabled by default.
	 * Enabling the emitter will actively call the update loop each frame.
	 * The emitter should remain disabled if you are using the emitter to produce static meshes.
	 * @param enabled Activate/deactivate the emitter
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * Returns if the emitter is actively calling update.
	 * @return 
	 */
	public boolean isEnabled() { return this.enabled; }
	
	/**
	 * Initializes the emitter, materials & particle mesh
	 * Must be called prior to adding the control to your scene.
	 * @param assetManager 
	 */
	public void initialize(AssetManager assetManager) {
		if (!emitterInitialized) {
			this.assetManager = assetManager;
			initMaterials();
			if (userDefinedMat != null) {
				mat = userDefinedMat;
			}
			if (this.name == null)
				name = this.generateName();
			emitterNode.setName(this.name + ":Emitter");
			emitterTestNode.setName(this.name + ":EmitterTest");
			particleNode.setName(this.name);
			particleTestNode.setName(this.name + ":Test");
			
			initParticles(particleType,template);
			mesh.setImagesXY(spriteCols,spriteRows);
			
			tex = assetManager.loadTexture(texturePath);
			tex.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);
			tex.setMagFilter(Texture.MagFilter.Bilinear);
			mat.setTexture(uniformName, tex);

			Image img = tex.getImage();
			int width = img.getWidth();
			int height = img.getHeight();
			
			spriteWidth = width/spriteCols;
			spriteHeight = height/spriteRows;
			
			if (esAnimControl != null) {
				if (!esAnimName.equals("")) {
					esAnimChannel.setAnim(esAnimName, esAnimBlendTime);
					esAnimChannel.setSpeed(esAnimSpeed);
				}
			}

			if (ptAnimControl != null) {
				if (!ptAnimName.equals("")) {
					ptAnimChannel.setAnim(ptAnimName, ptAnimBlendTime);
					ptAnimChannel.setSpeed(ptAnimSpeed);
				}
			}
			
			if (particleNode.getChildren().isEmpty()) {
				Geometry geom = new Geometry();
				geom.setMesh(mesh);
				particleNode.attachChild(geom);
				particleNode.setMaterial(mat);
				particleNode.setQueueBucket(RenderQueue.Bucket.Transparent);
			}
			
			if (emitterTestNode.getChildren().isEmpty()) {
				Geometry testGeom = new Geometry();
				testGeom.setMesh(emitterShape.getMesh());
				emitterTestNode.attachChild(testGeom);
				emitterTestNode.setMaterial(testMat);
			}
			if (particleTestNode.getChildren().isEmpty()) {
				Geometry testPGeom = new Geometry();
				testPGeom.setMesh(mesh);
				particleTestNode.attachChild(testPGeom);
				particleTestNode.setMaterial(testMat);
			}
			
			emitterInitialized = true;
		}
	}
	
	@Override
	public void setSpatial(Spatial spatial) {
		if (spatial != null) {
			((Node)spatial).attachChild(particleNode);
			if (esAnimControl != null && !esNodeExists)
				((Node)spatial).attachChild(esAnimNode);
			if (ptAnimControl != null)
				((Node)spatial).attachChild(ptAnimNode);
			if (TEST_EMITTER)
				((Node)spatial).attachChild(emitterTestNode);
			if (TEST_PARTICLES)
				((Node)spatial).attachChild(particleTestNode);
		} else {
			particleNode.removeFromParent();
			if (esAnimNode != null)
				esAnimNode.removeFromParent();
			if (ptAnimNode != null)
				ptAnimNode.removeFromParent();
			emitterTestNode.removeFromParent();
			particleTestNode.removeFromParent();
		}
		requiresUpdate = true;
		this.spatial = spatial;
	}
	
	public Spatial getSpatial() { return this.spatial; }
	
	public void setEmitterTestMode(boolean showEmitterShape, boolean showParticleMesh) {
		this.TEST_EMITTER = showEmitterShape;
		this.TEST_PARTICLES = showParticleMesh;
		
		if (spatial != null) {
			if (TEST_EMITTER)
				((Node)spatial).attachChild(emitterTestNode);
			else
				emitterTestNode.removeFromParent();
			
			if (TEST_PARTICLES)
				((Node)spatial).attachChild(particleTestNode);
			else
				particleTestNode.removeFromParent();
		}
		requiresUpdate = true;
	}
	
	/**
	 * Returns if the emitter is set to show the emitter shape as a wireframe.
	 * @return 
	 */
	public boolean getEmitterTestModeShape() {
		return TEST_EMITTER;
	}
	
	/**
	 * Returns if the emitter is set to show the particle mesh as a wireframe.
	 * @return 
	 */
	public boolean getEmitterTestModeParticles() {
		return TEST_PARTICLES;
	}
	
	/**
	 * Returns the node containing the particle mesh
	 * @return The node containing the particle mesh
	 */
	public Node getParticleNode() {
		return this.particleNode;
	}
	
	public Node getParticleTestNode() {
		return this.particleTestNode;
	}
	
	/**
	 * Returns the node containing the emitter transform information
	 * @return The node containing the emitter transform information
	 */
	public Node getEmitterNode() {
		return this.emitterNode;
	}
	
	public Node getEmitterTestNode() {
		return this.emitterTestNode;
	}
	
	@Override
	public void update(float tpf) {
		if (enabled && emitterInitialized) {
			for (ParticleData p : particles) {
				if (p.active) p.update(tpf);
			}

			currentInterval += (tpf <= targetInterval) ? tpf : targetInterval;

			if (currentInterval >= targetInterval) {
				totalParticlesThisEmission = this.particlesPerEmission;
				for (int i = 0; i < totalParticlesThisEmission; i++) {
					emitNextParticle();
				}
				currentInterval -= targetInterval;
			}
		//	((Geometry)particleNode.getChild(0)).updateModelBound();
		} else {
			currentInterval = 0;
		}
		if (emitterInitialized && (enabled || postRequiresUpdate)) {
			((Geometry)particleNode.getChild(0)).updateModelBound();
			if (TEST_PARTICLES)
				((Geometry)particleTestNode.getChild(0)).updateModelBound();
			postRequiresUpdate = false;
		}
	}
	
	private int calcParticlesPerEmission() {
		return (int)(currentInterval/targetInterval*particlesPerEmission);
	}
	
	/**
	 * Emits the next available (non-active) particle
	 */
	public void emitNextParticle() {
		if (nextIndex != -1 && nextIndex < maxParticles) {
			particles[nextIndex].initialize();
			int searchIndex = nextIndex;
			int initIndex = nextIndex;
			int loop = 0;
			while (particles[searchIndex].active) {
				searchIndex++;
				if (searchIndex > particles.length-1) {
					searchIndex = 0;
					loop++;
				}
				if (searchIndex == initIndex && loop == 1) {
					searchIndex = -1;
					break;
				}
			}
			nextIndex = searchIndex;
		}
		/*
		if (nextIndex != -1 && nextIndex < maxParticles) {
			particles[nextIndex].initialize();
			int searchIndex = nextIndex;
			while (particles[searchIndex].active) {
				searchIndex++;
				if (searchIndex > particles.length-1) {
					searchIndex = -1;
					break;
				}
			}
			nextIndex = searchIndex;
		}
		*/
	}
	
	/**
	 * Emits all non-active particles
	 */
	public void emitAllParticles() {
		for (ParticleData p : particles) {
			if (!p.active)
				p.initialize();
		}
		requiresUpdate = true;
	}
	
	/**
	 * Emits the specified number of particles
	 * @param count The number of particles to emit.
	 */
	public void emitNumParticles(int count) {
		int counter = 0;
		for (ParticleData p : particles) {
			if (!p.active && counter < count) {
				p.initialize();
				counter++;
			}
			if (counter > count)
				break;
		}
		requiresUpdate = true;
	}
	
	/**
	 * Clears all current particles, setting them to inactive
	 */
	public void killAllParticles() {
		for (ParticleData p : particles) {
			p.reset();
		}
		requiresUpdate = true;
	}
	
	/**
	 * Deactivates and resets the specified particle
	 * @param p The particle to reset
	 */
	public void killParticle(ParticleData p) {
		for (ParticleData particle : particles) {
			if (particle == p)
				p.reset();
		}
		requiresUpdate = true;
	}
	
	/**
	 * Returns the number of active particles
	 * @return 
	 */
	public int getActiveParticleCount() {
		return activeParticleCount;
	}
	
	/**
	 * DO NOT CALL - For internal use.
	 */
	public void incActiveParticleCount() {
		activeParticleCount++;
	}
	
	/**
	 * DO NOT CALL - For internal use.
	 */
	public void decActiveParticleCount() {
		activeParticleCount--;
	}
	
	/**
	 * Deactivates and resets the specified particle
	 * @param index The index of the particle to reset
	 */
	public void killParticle(int index) {
		particles[index].reset();
		requiresUpdate = true;
	}
	
	/**
	 * Resets all particle data and the current emission interval
	 */
	public void reset() {
		killAllParticles();
		currentInterval = 0;
		requiresUpdate = true;
	}
	
	/**
	 * Resets the current emission interval
	 */
	public void resetInterval() {
		currentInterval = 0;
	}
	
	/**
	 * This method should not be called.  Particles call this method to help track the next available particle index
	 * @param index The index of the particle that was just reset
	 */
	public void setNextIndex(int index) {
		if (index < nextIndex || nextIndex == -1)
			nextIndex = index;
	}
	
	@Override
	public void render(RenderManager rm, ViewPort vp) {
		if (emitterInitialized && (enabled || (!enabled && requiresUpdate))) {
			Camera cam = vp.getCamera();

			if (mesh.getClass() == ParticleDataPointMesh.class) {
				float C = cam.getProjectionMatrix().m00;
				C *= cam.getWidth() * 0.5f;

				// send attenuation params
				mat.setFloat("Quadratic", C);
			}
			mesh.updateParticleData(particles, cam, inverseRotation);
			if (requiresUpdate) {
				requiresUpdate = false;
				postRequiresUpdate = true;
			}
		}
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.writeSavableArrayList(new ArrayList(influencers), "influencers", null);
		
		oc.write(name, "name", null);
		
		// Emitter shape
		oc.write(emitterShape.getMesh(), "emitterShape", new TriangleEmitterShape(1));
		
		// Particle mesh
		oc.write(particleType.getName(), "particleType", ParticleDataTriMesh.class.getName());
		oc.write(template, "template", null);
		
		// Emitter animation
		oc.write(esAnimNode, "esAnimNode", null);
		oc.write(esNodeExists, "esNodeExists", true);
		oc.write(esAnimName, "esAnimName", "");
		oc.write(esAnimSpeed, "esAnimSpeed", 1);
		oc.write(esAnimBlendTime, "esAnimBlendTime", 1);
		
		// Particle animation
		oc.write(ptAnimNode, "ptAnimNode", null);
		oc.write(ptAnimName, "ptAnimName", "");
		oc.write(ptAnimSpeed, "ptAnimSpeed", 1);
		oc.write(ptAnimBlendTime, "ptAnimBlendTime", 1);
		
		oc.write(maxParticles, "maxParticles", 30);
		oc.write(emissionsPerSecond, "emissionsPerSecond", 20);
		oc.write(particlesPerEmission, "particlesPerEmission", 1);
		oc.write(useStaticParticles, "useStaticParticles", false);
		oc.write(forceMin, "forceMin", .15f);
		oc.write(forceMax, "forceMax", .5f);
		oc.write(lifeMin, "lifeMin", 0.999f);
		oc.write(lifeMax, "lifeMax", 0.999f);
		oc.write(velocityStretchFactor, "velocityStretchFactor", 0.35f);
		
		// Material
		oc.write(userDefinedMat, "userDefinedMat", null);
		oc.write(applyLightingTransform, "applyLightingTransform", false);
		oc.write(uniformName, "uniformName", null);
		oc.write(texturePath, "texturePath", null);
		oc.write(spriteWidth, "spriteWidth", 50);
		oc.write(spriteHeight, "spriteHeight", 50);
		oc.write(spriteCols, "spriteCols", 1);
		oc.write(spriteRows, "spriteRows", 1);
		
		oc.write(directionType.name(), "directionType", EmitterMesh.DirectionType.Random.name());
		oc.write(billboardMode.name(), "billboardMode", BillboardMode.Camera.name());
		oc.write(stretchAxis.name(), "stretchAxis", ForcedStretchAxis.Y.name());
		oc.write(particleEmissionPoint.name(), "particleEmissionPoint", ParticleEmissionPoint.Particle_Center.name());
		
		oc.write(particlesFollowEmitter, "particlesFollowEmitter", false);
		oc.write(useStaticParticles, "useStaticParticles", false);
		oc.write(useRandomEmissionPoint, "useRandomEmissionPoint", false);
		oc.write(useSequentialEmissionFace, "useSequentialEmissionFace", false);
		oc.write(useSequentialSkipPattern, "useSequentialSkipPattern", false);
		oc.write(TEST_EMITTER, "TEST_EMITTER", false);
		oc.write(TEST_PARTICLES, "TEST_PARTICLES", false);
		
		oc.write(enabled, "enabled", false);
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		
		influencers = new SafeArrayList<ParticleInfluencer>(ParticleInfluencer.class, ic.readSavableArrayList("influencers", null));
		
		name = ic.readString("name", generateName());
		
		// Reconstruct particle mesh
		try {
			particleType = Class.forName(ic.readString("particleType", ParticleDataTriMesh.class.getName()));
		} catch (IOException | ClassNotFoundException ex) {
			particleType = ParticleDataTriMesh.class;
		}
		template = (Mesh)ic.readSavable("template", null);
		Node ptAnimN = (Node)ic.readSavable("ptAnimNode", null);
		
		if (ptAnimN == null)
			setParticleType(particleType, template);
		else
			setParticleType(particleType, ptAnimN);
		initParticles(particleType, template);
		
		// Reconstruct emitter shape
		Mesh eShape = (Mesh)ic.readSavable("emitterShape", new TriangleEmitterShape(1));
		Node esAnimN = (Node)ic.readSavable("esAnimNode", null);
		boolean esNExists = ic.readBoolean("esNodeExists", true);
		
		if (esAnimN == null)
			setShape(eShape);
		else
			setShape(esAnimN, esNExists);
		
		// Emitter animation
		String esAName = ic.readString("esAnimName", "");
		float esASpeed = ic.readFloat("esAnimSpeed", 1);
		float esABlendTime = ic.readFloat("esAnimBlendTime", 1);
		LoopMode esALoop = LoopMode.valueOf(ic.readString("esAnimLoopMode", LoopMode.Loop.name()));
		
		if (!esAName.equals("") && esAnimControl != null)
			setEmitterAnimation(esAName, esASpeed, esABlendTime, esALoop);
		
		// Particle animation
		String ptAName = ic.readString("ptAnimName", "");
		float ptASpeed = ic.readFloat("ptAnimSpeed", 1);
		float ptABlendTime = ic.readFloat("ptAnimBlendTime", 1);
		LoopMode ptALoop = LoopMode.valueOf(ic.readString("ptAnimLoopMode", LoopMode.Loop.name()));
		
		if (!ptAName.equals("") && ptAnimControl != null)
			setParticleAnimation(ptAName, ptASpeed, ptABlendTime, ptALoop);
		
		maxParticles = ic.readInt("maxParticles", 30);
		int emsPerSec = ic.readInt("emissionsPerSecond", 20);
		setEmissionsPerSecond(emsPerSec);
		int parsPerEm = ic.readInt("particlesPerEmission", 1);
		setParticlesPerEmission(parsPerEm);
		
		float fMin = ic.readFloat("forceMin", .15f);
		float fMax = ic.readFloat("forceMax", .5f);
		setForceMinMax(fMin,fMax);
		
		float lMin = ic.readFloat("lifeMin", 0.999f);
		float lMax = ic.readFloat("lifeMax", 0.999f);
		setLifeMinMax(lMin, lMax);
		
		float stretchFactor = ic.readFloat("velocityStretchFactor", 0.35f);
		setVelocityStretchFactor(stretchFactor);
		
		// Material
		userDefinedMat = (Material)ic.readSavable("userDefinedMat", null);
		applyLightingTransform = ic.readBoolean("applyLightingTransform", false);
		uniformName = ic.readString("uniformName", null);
		texturePath = ic.readString("texturePath", null);
		spriteWidth = ic.readFloat("spriteWidth", 50);
		spriteHeight = ic.readFloat("spriteHeight", 50);
		spriteCols = ic.readInt("spriteCols", 1);
		spriteRows = ic.readInt("spriteRows", 1);
		
		directionType = DirectionType.valueOf(ic.readString("directionType", EmitterMesh.DirectionType.Random.name()));
		billboardMode = BillboardMode.valueOf(ic.readString("billboardMode", BillboardMode.Camera.name()));
		stretchAxis = ForcedStretchAxis.valueOf(ic.readString("stretchAxis", ForcedStretchAxis.Y.name()));
		particleEmissionPoint = ParticleEmissionPoint.valueOf(ic.readString("particleEmissionPoint", ParticleEmissionPoint.Particle_Center.name()));
		
		useStaticParticles = ic.readBoolean("useStaticParticles", false);
		particlesFollowEmitter = ic.readBoolean("particlesFollowEmitter", false);
		useStaticParticles = ic.readBoolean("useStaticParticles", false);
		useRandomEmissionPoint = ic.readBoolean("useRandomEmissionPoint", false);
		useSequentialEmissionFace = ic.readBoolean("useSequentialEmissionFace", false);
		useSequentialSkipPattern = ic.readBoolean("useSequentialSkipPattern", false);
		TEST_EMITTER = ic.readBoolean("TEST_EMITTER", false);
		TEST_PARTICLES = ic.readBoolean("TEST_PARTICLES", false);
		
		enabled = ic.readBoolean("enabled", false);
	}

	@Override
	public Control cloneForSpatial(Spatial spatial) {
		Emitter clone = clone();
		clone.setSpatial(spatial);
		return clone;
	}
	
	@Override
	public Emitter clone() {
		Emitter clone = new Emitter();
		clone.setMaxParticles(maxParticles);
		if (esAnimNode != null) {
			clone.setShape(esAnimNode, this.esNodeExists);
			clone.setEmitterAnimation(esAnimName, esAnimSpeed, esAnimBlendTime, esAnimLoopMode);
		} else
			clone.setShape(emitterShape.getMesh());
		if (ptAnimNode != null) {
			clone.setParticleType(particleType, ptAnimNode);
			clone.setParticleAnimation(ptAnimName, ptAnimSpeed, ptAnimBlendTime, ptAnimLoopMode);
		} else
			clone.setParticleType(particleType, template);
		clone.setInterpolation(getInterpolation());
		clone.setForceMinMax(forceMin, forceMax);
		clone.setLifeMinMax(lifeMin, lifeMax);
		clone.setEmissionsPerSecond(emissionsPerSecond);
		clone.setParticlesPerEmission(particlesPerEmission);
		clone.setUseRandomEmissionPoint(useRandomEmissionPoint);
		clone.setUseSequentialEmissionFace(useSequentialEmissionFace);
		clone.setUseSequentialSkipPattern(useSequentialSkipPattern);
		clone.setParticlesFollowEmitter(particlesFollowEmitter);
		clone.setUseStaticParticles(useStaticParticles);
		clone.setUseVelocityStretching(useVelocityStretching);
		clone.setVelocityStretchFactor(velocityStretchFactor);
		clone.setForcedStretchAxis(stretchAxis);
		clone.setParticleEmissionPoint(particleEmissionPoint);
		clone.setBillboardMode(billboardMode);
		clone.setEmitterTestMode(TEST_EMITTER, TEST_PARTICLES);
		
		for (ParticleInfluencer inf : influencers) {
			clone.addInfluencer(inf.clone());
		}
		
	//	clone.initParticles(mesh.getClass(), template);
		clone.setSprite(texturePath, spriteCols, spriteRows);
		clone.initialize(assetManager);
		clone.setEnabled(enabled);
		return clone;
	}
	
	//<editor-fold desc="Emitter Transforms">
	public void setLocalTranslation(Vector3f translation) {
		emitterNode.setLocalTranslation(translation);
		emitterTestNode.setLocalTranslation(translation);
		particleNode.setLocalTranslation(translation);
		particleTestNode.setLocalTranslation(translation);
		requiresUpdate = true;
	}
	
	public void setLocalTranslation(float x, float y, float z) {
		emitterNode.setLocalTranslation(x, y, z);
		emitterTestNode.setLocalTranslation(x, y, z);
		particleNode.setLocalTranslation(x, y, z);
		particleTestNode.setLocalTranslation(x, y, z);
		requiresUpdate = true;
	}
	
	public void setLocalRotation(Quaternion q) {
		emitterNode.setLocalRotation(q);
		emitterTestNode.setLocalRotation(q);
		requiresUpdate = true;
	}
	
	public void setLocalRotation(Matrix3f m) {
		emitterNode.setLocalRotation(m);
		emitterTestNode.setLocalRotation(m);
		requiresUpdate = true;
	}
	
	public void setLocalScale(Vector3f scale) {
		emitterNode.setLocalScale(scale);
		emitterTestNode.setLocalScale(scale);
		requiresUpdate = true;
	}
	
	public void setLocalScale(float scale) {
		emitterNode.setLocalScale(scale);
		emitterTestNode.setLocalScale(scale);
		requiresUpdate = true;
	}
	
	public void setLocalScale(float x, float y, float z) {
		emitterNode.setLocalScale(x, y, z);
		emitterTestNode.setLocalScale(x, y, z);
		requiresUpdate = true;
	}
	
	public Quaternion getLocalRotation() {
		return emitterNode.getLocalRotation();
	}
	
	public Vector3f getLocalTranslation() {
		return emitterNode.getLocalTranslation();
	}
	
	public Vector3f getLocalScale() {
		return emitterNode.getLocalScale();
	}
	//</editor-fold>
}
