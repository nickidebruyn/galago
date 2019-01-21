package com.bruynhuis.galago.emitter;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;

/**
 *
 * @author t0neg0d
 */
public class EmitterMesh {
	public static enum DirectionType {
		Normal,
		NormalNegate,
		Random,
		RandomTangent,
		RandomNormalAligned,
		RandomNormalNegate
	}
	
	private Mesh mesh;
	private int triangleIndex;
	private Triangle triStore = new Triangle();
	Vector3f p1 = new Vector3f();
	Vector3f p2 = new Vector3f();
	Vector3f p3 = new Vector3f();
	Vector3f a = new Vector3f();
	Vector3f b = new Vector3f();
	Vector3f result = new Vector3f();
	Node p = new Node(), n1 = new Node(), n2 = new Node(), n3 = new Node();
	private int triCount;
	private int currentTri = 0;
	Emitter emitter;
//	Geometry geom = new Geometry();
	Quaternion q = new Quaternion(), q2 = new Quaternion();
	Vector3f tempDir = new Vector3f();
	Vector3f up = new Vector3f();
	Vector3f left = new Vector3f();
	
//	DirectionType directionType = DirectionType.Normal;
	
	/**
	 * Sets the mesh to use as the emitter shape
	 * @param mesh The mesh to use as the emitter shape
	 */
	public void setShape(Emitter emitter, Mesh mesh) {
		this.emitter = emitter;
		this.mesh = mesh;
	//	geom.setMesh(mesh);
		triCount = mesh.getTriangleCount();
		
		p.attachChild(n1);
		p.attachChild(n2);
		p.attachChild(n3);
	}
	
	/**
	 * Returns the mesh used as the particle emitter shape
	 * @return The particle emitter shape mesh
	 */
	public Mesh getMesh() {
		return this.mesh;
	}
	/*
	public void setDirectionType(DirectionType directionType) {
		this.directionType = directionType;
	}
	
	public DirectionType getDirectionType() { return this.directionType; }
	*/
	/**
	 * Selects a random face as the next particle emission point
	 */
	public void setNext() {
		if (emitter.getUseSequentialEmissionFace()) {
			if (emitter.getUseSequentialSkipPattern())
				currentTri += 2;
			else
				currentTri++;
			if (currentTri >= triCount)
				currentTri = 0;
			triangleIndex = currentTri;
		} else {
			triangleIndex = FastMath.rand.nextInt(triCount);
		}
		mesh.getTriangle(triangleIndex, triStore);
		calcTransform();
		triStore.calculateCenter();
		triStore.calculateNormal();
	}
	
	/**
	 * Set the current particle emission face to the specified faces index
	 * @param triangleIndex The index of the face to set as the particle emission point
	 */
	public void setNext(int triangleIndex) {
		mesh.getTriangle(triangleIndex, triStore);
		calcTransform();
		triStore.calculateCenter();
		triStore.calculateNormal();
	}
	
	private void calcTransform() {
		n1.setLocalTranslation(triStore.get1());
		n2.setLocalTranslation(triStore.get2());
		n3.setLocalTranslation(triStore.get3());
		p.setLocalRotation(emitter.getEmitterNode().getLocalRotation());
		p.setLocalScale(emitter.getLocalScale());
		triStore.set1(n1.getWorldTranslation());
		triStore.set2(n2.getWorldTranslation());
		triStore.set3(n3.getWorldTranslation());
	}
	
	/**
	 * Returns the index of the current face being used as the particle emission point
	 * @return 
	 */
	public int getTriangleIndex() {
		return triangleIndex;
	}
	
	public Vector3f getNormal() {
		return triStore.getNormal();
	}
	/**
	 * Returns the local position of the center of the selected face
	 * @return A Vector3f representing the local translation of the selected emission point
	 */
	public Vector3f getNextTranslation(){
		return triStore.getCenter();
	}
	
	public Vector3f getRandomTranslation() {
		int start = FastMath.nextRandomInt(1, 3);
		
		switch(start) {
			case 1:
				p1.set(triStore.get1().subtract(triStore.getCenter()));
				p2.set(triStore.get2().subtract(triStore.getCenter()));
				p3.set(triStore.get3().subtract(triStore.getCenter()));
				break;
			case 2:
				p1.set(triStore.get2().subtract(triStore.getCenter()));
				p2.set(triStore.get1().subtract(triStore.getCenter()));
				p3.set(triStore.get3().subtract(triStore.getCenter()));
				break;
			case 3:
				p1.set(triStore.get3().subtract(triStore.getCenter()));
				p2.set(triStore.get2().subtract(triStore.getCenter()));
				p3.set(triStore.get1().subtract(triStore.getCenter()));
				break;
		}
		
		a.interpolateLocal(p1, p2, 1f-FastMath.rand.nextFloat());
		b.interpolateLocal(p1, p3, 1f-FastMath.rand.nextFloat());
		result.interpolateLocal(a,b,FastMath.rand.nextFloat());
		
		return result;
	}
	
	/**
	 * Returns the normal of the selected emission point
	 * @return A Vector3f containing the normal of the selected emission point
	 */
	public Vector3f getNextDirection(){
		switch (emitter.getDirectionType()) {
			case Normal:
				tempDir.set(getDirectionNormal());
				break;
			case NormalNegate:
				tempDir.set(getDirectionNormal().negate());
				break;
			case Random:
				tempDir.set(getDirectionRandom());
				break;
			case RandomTangent:
				tempDir.set(getDirectionRandomTangent());
				break;
			case RandomNormalAligned:
				tempDir.set(getDirectionRandom());
				if (tempDir.dot(getDirectionNormal()) < 0)
					tempDir.negateLocal();
				break;
			case RandomNormalNegate:
				tempDir.set(getDirectionRandom());
				if (tempDir.dot(getDirectionNormal()) > 0)
					tempDir.negateLocal();
				break;
		}
		return tempDir;
	}
	
	private Vector3f getDirectionNormal() {
		return triStore.getNormal();
	}
	
	private Vector3f getDirectionRandom() {
		q.fromAngles(
			FastMath.nextRandomFloat()*FastMath.TWO_PI,
			FastMath.nextRandomFloat()*FastMath.TWO_PI,
			FastMath.nextRandomFloat()*FastMath.TWO_PI
		).normalizeLocal();
		tempDir.set(q.mult(Vector3f.UNIT_Y));
		return tempDir;
	}
	
	private Vector3f getDirectionRandomTangent() {
		tempDir.set(Vector3f.UNIT_Y);
		q2.lookAt(getNormal(), Vector3f.UNIT_Y);
		tempDir.set(q2.mult(tempDir));
		q.fromAngleAxis(FastMath.nextRandomFloat()*360*FastMath.DEG_TO_RAD, getNormal());
		tempDir.set(q.mult(tempDir));
		return tempDir;
	}
}
