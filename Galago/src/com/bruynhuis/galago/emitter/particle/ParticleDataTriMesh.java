package com.bruynhuis.galago.emitter.particle;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Format;
import com.jme3.scene.VertexBuffer.Usage;
import com.jme3.util.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import com.bruynhuis.galago.emitter.Emitter;
import static com.bruynhuis.galago.emitter.Emitter.BillboardMode.Velocity_Z_Up;

/**
 *
 * @author t0neg0d
 */
public class ParticleDataTriMesh extends ParticleDataMesh {

    private int imagesX = 1;
    private int imagesY = 1;
    private boolean uniqueTexCoords = false;
    private Emitter emitter;
	private Vector3f left = new Vector3f(), tempLeft = new Vector3f();
	private Vector3f up = new Vector3f(), tempUp = new Vector3f();
	private Vector3f dir = new Vector3f();
	private Vector3f tempV3 = new Vector3f();
	private Vector3f tempV3a = new Vector3f();
	private Vector3f tempV3b = new Vector3f();
	private Quaternion rotStore = new Quaternion();
	private Quaternion tempQ = new Quaternion();
	private int imgX, imgY;
	private float startX, startY, endX, endY;
	private ColorRGBA tempC = new ColorRGBA();
	private Vector3f lock = new Vector3f(0,0.99f,0.01f);
	private Vector3f tangUp = new Vector3f();
	
    @Override
    public void initParticleData(Emitter emitter, int numParticles) {
        setMode(Mode.Triangles);

        this.emitter = emitter;

//        particlesCopy = new ParticleData[numParticles];

        // set positions
        FloatBuffer pb = BufferUtils.createVector3Buffer(numParticles * 4);
        // if the buffer is already set only update the data
        VertexBuffer buf = getBuffer(VertexBuffer.Type.Position);
        if (buf != null) {
            buf.updateData(pb);
        } else {
            VertexBuffer pvb = new VertexBuffer(VertexBuffer.Type.Position);
            pvb.setupData(Usage.Stream, 3, Format.Float, pb);
            setBuffer(pvb);
        }
        
        // set colors
        ByteBuffer cb = BufferUtils.createByteBuffer(numParticles * 4 * 4);
        buf = getBuffer(VertexBuffer.Type.Color);
        if (buf != null) {
            buf.updateData(cb);
        } else {
            VertexBuffer cvb = new VertexBuffer(VertexBuffer.Type.Color);
            cvb.setupData(Usage.Stream, 4, Format.UnsignedByte, cb);
            cvb.setNormalized(true);
            setBuffer(cvb);
        }

        // set texcoords
        FloatBuffer tb = BufferUtils.createVector2Buffer(numParticles * 4);
        uniqueTexCoords = false;
        for (int i = 0; i < numParticles; i++){
            tb.put(0f).put(1f);
            tb.put(1f).put(1f);
            tb.put(0f).put(0f);
            tb.put(1f).put(0f);
        }
        tb.flip();
        
        buf = getBuffer(VertexBuffer.Type.TexCoord);
        if (buf != null) {
            buf.updateData(tb);
        } else {
            VertexBuffer tvb = new VertexBuffer(VertexBuffer.Type.TexCoord);
            tvb.setupData(Usage.Static, 2, Format.Float, tb);
            setBuffer(tvb);
        }

        // set indices
        ShortBuffer ib = BufferUtils.createShortBuffer(numParticles * 6);
        for (int i = 0; i < numParticles; i++){
            int startIdx = (i * 4);

            // triangle 1
            ib.put((short)(startIdx + 1))
              .put((short)(startIdx + 0))
              .put((short)(startIdx + 2));

            // triangle 2
            ib.put((short)(startIdx + 1))
              .put((short)(startIdx + 2))
              .put((short)(startIdx + 3));
        }
        ib.flip();

        buf = getBuffer(VertexBuffer.Type.Index);
        if (buf != null) {
            buf.updateData(ib);
        } else {
            VertexBuffer ivb = new VertexBuffer(VertexBuffer.Type.Index);
            ivb.setupData(Usage.Static, 3, Format.UnsignedShort, ib);
            setBuffer(ivb);
        }
        
        updateCounts();
    }
    
    @Override
    public void setImagesXY(int imagesX, int imagesY) {
        this.imagesX = imagesX;
        this.imagesY = imagesY;
        if (imagesX != 1 || imagesY != 1){
            uniqueTexCoords = true;
            getBuffer(VertexBuffer.Type.TexCoord).setUsage(Usage.Stream);
        }
    }
	
	public int getSpriteCols() { return this.imagesX; }
	public int getSpriteRows() { return this.imagesY; }
	
    @Override
    public void updateParticleData(ParticleData[] particles, Camera cam, Matrix3f inverseRotation) {
        VertexBuffer pvb = getBuffer(VertexBuffer.Type.Position);
        FloatBuffer positions = (FloatBuffer) pvb.getData();

        VertexBuffer cvb = getBuffer(VertexBuffer.Type.Color);
        ByteBuffer colors = (ByteBuffer) cvb.getData();

        VertexBuffer tvb = getBuffer(VertexBuffer.Type.TexCoord);
        FloatBuffer texcoords = (FloatBuffer) tvb.getData();

        // update data in vertex buffers
        positions.clear();
        colors.clear();
        texcoords.clear();
        
        for (int i = 0; i < particles.length; i++){
            ParticleData p = particles[i];
            if (p.life == 0 || !p.active) {
                positions.put(0).put(0).put(0);
                positions.put(0).put(0).put(0);
                positions.put(0).put(0).put(0);
                positions.put(0).put(0).put(0);
				/*
				if (uniqueTexCoords){
					startX = 1f/p.emitter.getSpriteColCount()*p.spriteCol;
					startY = 1f/p.emitter.getSpriteRowCount()*p.spriteRow;
					endX   = startX + 1f/p.emitter.getSpriteColCount();
					endY   = startY + 1f/p.emitter.getSpriteRowCount();

					texcoords.put(startX).put(endY);
					texcoords.put(endX).put(endY);
					texcoords.put(startX).put(startY);
					texcoords.put(endX).put(startY);
				}
				
				tempC.set(p.color);
				tempC.a *= p.alpha;
				int abgr = tempC.asIntABGR();
				colors.putInt(abgr);
				colors.putInt(abgr);
				colors.putInt(abgr);
				colors.putInt(abgr);
                continue;
				*/
            } else {
			
				switch (emitter.getBillboardMode()) {
					case Velocity:
						if (p.velocity.x != Vector3f.UNIT_Y.x &&
							p.velocity.y != Vector3f.UNIT_Y.y &&
							p.velocity.z != Vector3f.UNIT_Y.z)
							up.set(p.velocity).crossLocal(Vector3f.UNIT_Y).normalizeLocal();
						else
							up.set(p.velocity).crossLocal(lock).normalizeLocal();
						left.set(p.velocity).crossLocal(up).normalizeLocal();
						dir.set(p.velocity);
						break;
					case Velocity_Z_Up:
						if (p.velocity.x != Vector3f.UNIT_Y.x &&
							p.velocity.y != Vector3f.UNIT_Y.y &&
							p.velocity.z != Vector3f.UNIT_Y.z)
							up.set(p.velocity).crossLocal(Vector3f.UNIT_Y).normalizeLocal();
						else
							up.set(p.velocity).crossLocal(lock).normalizeLocal();
						left.set(p.velocity).crossLocal(up).normalizeLocal();
						dir.set(p.velocity);
						rotStore = tempQ.fromAngleAxis(-90*FastMath.DEG_TO_RAD, left);
						left = rotStore.mult(left);
						up = rotStore.mult(up);
						break;
					case Velocity_Z_Up_Y_Left:
						up.set(p.velocity).crossLocal(Vector3f.UNIT_Y).normalizeLocal();
						left.set(p.velocity).crossLocal(up).normalizeLocal();
						dir.set(p.velocity);
						tempV3.set(left).crossLocal(up).normalizeLocal();
						rotStore = tempQ.fromAngleAxis(90*FastMath.DEG_TO_RAD, p.velocity);
						left = rotStore.mult(left);
						up = rotStore.mult(up);
						rotStore = tempQ.fromAngleAxis(-90*FastMath.DEG_TO_RAD, left);
						up = rotStore.mult(up);
						break;
					case Normal:
						emitter.getShape().setNext(p.triangleIndex);
						tempV3.set(emitter.getShape().getNormal());
						if (tempV3 == Vector3f.UNIT_Y)
							tempV3.set(p.velocity);

						up.set(tempV3).crossLocal(Vector3f.UNIT_Y).normalizeLocal();
						left.set(tempV3).crossLocal(up).normalizeLocal();
						dir.set(tempV3);
						break;
					case Normal_Y_Up:
						emitter.getShape().setNext(p.triangleIndex);
						tempV3.set(p.velocity);
						if (tempV3 == Vector3f.UNIT_Y)
							tempV3.set(Vector3f.UNIT_X);

						up.set(Vector3f.UNIT_Y);
						left.set(tempV3).crossLocal(up).normalizeLocal();
						dir.set(tempV3);
						break;
					case Camera:
						up.set(cam.getUp());
						left.set(cam.getLeft());
						dir.set(cam.getDirection());
						break;
					case UNIT_X:
						up.set(Vector3f.UNIT_Y);
						left.set(Vector3f.UNIT_Z);
						dir.set(Vector3f.UNIT_X);
						break;
					case UNIT_Y:
						up.set(Vector3f.UNIT_Z);
						left.set(Vector3f.UNIT_X);
						dir.set(Vector3f.UNIT_Y);
						break;
					case UNIT_Z:
						up.set(Vector3f.UNIT_X);
						left.set(Vector3f.UNIT_Y);
						dir.set(Vector3f.UNIT_Z);
						break;
				}

				p.upVec.set(up);

				if (p.emitter.getUseVelocityStretching()) {
					up.multLocal(p.velocity.length()*p.emitter.getVelocityStretchFactor());
				}

				up.multLocal(p.size.y);
				left.multLocal(p.size.x);

				rotStore = tempQ.fromAngleAxis(p.angles.y, left);
				left = rotStore.mult(left);
				up = rotStore.mult(up);

				rotStore = tempQ.fromAngleAxis(p.angles.x, up);
				left = rotStore.mult(left);
				up = rotStore.mult(up);

				rotStore = tempQ.fromAngleAxis(p.angles.z, dir);
				left = rotStore.mult(left);
				up = rotStore.mult(up);

				if (emitter.getParticlesFollowEmitter()) {
					tempV3.set(p.position);
				} else {
					tempV3.set(p.position).subtractLocal(
						emitter.getEmitterNode().getWorldTranslation().subtract(p.initialPosition)
					);
				}

				positions.put(tempV3.x + left.x + up.x)
						 .put(tempV3.y + left.y + up.y)
						 .put(tempV3.z + left.z + up.z);

				positions.put(tempV3.x - left.x + up.x)
						 .put(tempV3.y - left.y + up.y)
						 .put(tempV3.z - left.z + up.z);

				positions.put(tempV3.x + left.x - up.x)
						 .put(tempV3.y + left.y - up.y)
						 .put(tempV3.z + left.z - up.z);

				positions.put(tempV3.x - left.x - up.x)
						 .put(tempV3.y - left.y - up.y)
						 .put(tempV3.z - left.z - up.z);
			}
			
			if (uniqueTexCoords){
				startX = 1f/p.emitter.getSpriteColCount()*p.spriteCol;
				startY = 1f/p.emitter.getSpriteRowCount()*p.spriteRow;
				endX   = startX + 1f/p.emitter.getSpriteColCount();
				endY   = startY + 1f/p.emitter.getSpriteRowCount();
				
				texcoords.put(startX).put(endY);
				texcoords.put(endX).put(endY);
				texcoords.put(startX).put(startY);
				texcoords.put(endX).put(startY);
			}

			tempC.set(p.color);
			tempC.a *= p.alpha;
			int abgr = tempC.asIntABGR();
            colors.putInt(abgr);
            colors.putInt(abgr);
            colors.putInt(abgr);
            colors.putInt(abgr);
        }
		
	//	this.setBuffer(VertexBuffer.Type.Position, 3, positions);
        positions.clear();
        colors.clear();
        if (!uniqueTexCoords)
            texcoords.clear();
        else{
            texcoords.clear();
            tvb.updateData(texcoords);
        }

        // force renderer to re-send data to GPU
        pvb.updateData(positions);
        cvb.updateData(colors);
		
		updateBound();
    }

	@Override
	public void extractTemplateFromMesh(Mesh mesh) {  }
}