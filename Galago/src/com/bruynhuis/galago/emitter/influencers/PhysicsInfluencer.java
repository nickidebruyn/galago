package com.bruynhuis.galago.emitter.influencers;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.GeometryList;
import com.jme3.renderer.queue.OpaqueComparator;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import java.io.IOException;
import com.bruynhuis.galago.emitter.particle.ParticleData;

/**
 *
 * @author t0neg0d
 */
public class PhysicsInfluencer implements ParticleInfluencer {
	public static enum CollisionReaction {
		Bounce,
		Stick,
		Destroy
	}
	
	private GeometryList geoms = new GeometryList(new OpaqueComparator());
	private GeometryList tempGeoms = new GeometryList(new OpaqueComparator());
	
	private boolean enabled = true;
	
	private Quad q = new Quad(1,1);
	private Geometry geom = new Geometry();
	private Quaternion quat = new Quaternion();
	private CollisionResults results = new CollisionResults();
	private CollisionResult result;
	private Triangle contactSurface;
	private Vector3f	reflect = new Vector3f(),
						two = new Vector3f(),
						normal = new Vector3f();
	private float twoDot, len;
	private float collisionThreshold = 0.1f;
	private float restitution = 0.5f;
	private CollisionReaction collisionReaction = CollisionReaction.Bounce;
	
	public PhysicsInfluencer() {
		geom.setMesh(q);
		q.updateBound();
		geom.updateModelBound();
	}
	
	@Override
	public void update(ParticleData p, float tpf) {
		if (enabled) {
			if (p.collision == false) {
				for (int i = 0; i < geoms.size(); i++) {
					Geometry g = geoms.get(i);
					try {
						if (results.size() != 0)
							results.clear();
						updateCollisionShape(p,tpf);
						g.collideWith(geom.getWorldBound(), results);
						if (results.size() > 0) {
							result = results.getClosestCollision();
							switch (collisionReaction) {
								case Bounce:
									contactSurface = result.getTriangle(null);
									contactSurface.calculateNormal();
									normal.set(contactSurface.getNormal());
									twoDot = 2.0f*p.velocity.dot(normal);
									two.set(twoDot,twoDot,twoDot);
									reflect.set(two.mult(normal).subtract(p.velocity)).negateLocal().normalizeLocal();
									len = p.velocity.length()*(restitution-0.1f)+(FastMath.nextRandomFloat()*0.2f);
									p.velocity.set(reflect).multLocal(len);
									p.collision = true;
									break;
								case Stick:
									p.velocity.set(0,0,0);
									break;
								case Destroy:
									p.emitter.killParticle(p);
									break;
							}
						}
					} catch (Exception ex) {  }
				}
			} else {
				p.collisionInterval += tpf;
				if (p.collisionInterval >= collisionThreshold) {
					p.collision = false;
					p.collisionInterval = 0;
				}
			}
		}
	}
	
	private void updateCollisionShape(ParticleData p, float tpf) {
		geom.setLocalTranslation(p.position.add(p.emitter.getLocalTranslation()));
		quat.fromAngles(p.angles.x,p.angles.y,p.angles.z);
		geom.setLocalRotation(quat);
		geom.setLocalScale(p.size);
		geom.updateLogicalState(tpf);
		geom.updateGeometricState();
		geom.updateModelBound();
	}
	
	public void addCollidable(Geometry g) {
		for (int i = 0; i < geoms.size(); i++) {
			if (geoms.get(i) == g)
				return;
		}
		geoms.add(g);
	}
	
	public void removeCollidable(Geometry g) {
		boolean wasEnabled = enabled;
		this.enabled = false;
		tempGeoms.clear();
		for (int i = 0; i < geoms.size(); i++) {
			if (geoms.get(i) != g)
				tempGeoms.add(geoms.get(i));
		}
		geoms.clear();
		for (int i = 0; i < tempGeoms.size(); i++) {
			geoms.add(tempGeoms.get(i));
		}
		this.enabled = wasEnabled;
	}
	
	public GeometryList getGeometries() {
		return this.geoms;
	}
	
	/**
	 * How "bouncy" the particle is (a value between 0.0f and 1.0f).  The default value is 0.5f.
	 * @param restitution The bounciness of the particle
	 */
	public void setRestitution(float restitution) {
		this.restitution = restitution;
	}
	
	public float getRestitution() {
		return this.restitution;
	}
	
	@Override
	public void initialize(ParticleData p) {
		
	}

	@Override
	public void reset(ParticleData p) {
		p.collision = false;
		p.collisionInterval = 0;
	}
	
	/**
	 * Defines the response when a particle collides with a geometry in the collidables list
	 * @param collisionReaction 
	 */
	public void setCollisionReaction(CollisionReaction collisionReaction) {
		this.collisionReaction = collisionReaction;
	}
	
	public CollisionReaction getCollisionReaction() {
		return this.collisionReaction;
	}
	
	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(enabled, "enabled", true);
		oc.write(collisionThreshold, "collisionThreshold", 0.1f);
		oc.write(restitution, "restitution", 0.5f);
		oc.write(collisionReaction.name(), "collisionReaction", CollisionReaction.Bounce.name());
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		InputCapsule ic = im.getCapsule(this);
		enabled = ic.readBoolean("enabled", true);
		collisionThreshold = ic.readFloat("collisionThreshold", 0.1f);
		restitution = ic.readFloat("restitution", 0.5f);
		collisionReaction = CollisionReaction.valueOf(ic.readString("collisionReaction", CollisionReaction.Bounce.name()));
	}

	/**
	 * This method clones the influencer instance.
	 * 
	 * ** Please note the geometry list is specific to each instance of the physics influencer and
	 * must be maintained by the user.  This list is NOT cloned from the original influencer.
	 * @return 
	 */
	@Override
	public ParticleInfluencer clone() {
		try {
			PhysicsInfluencer clone = (PhysicsInfluencer) super.clone();
			clone.setEnabled(enabled);
			clone.setCollisionReaction(collisionReaction);
			clone.setRestitution(restitution);
			clone.collisionThreshold = collisionThreshold;
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
		return PhysicsInfluencer.class;
	}
}
