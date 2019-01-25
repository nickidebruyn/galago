/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.util;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Circ;
import com.bruynhuis.galago.app.Base3DApplication;
import com.bruynhuis.galago.control.camera.CameraStickControl;
import com.bruynhuis.galago.control.tween.RigidbodyAccessor;
import com.bruynhuis.galago.control.tween.SpatialAccessor;
import com.bruynhuis.galago.spatial.Disk;
import com.bruynhuis.galago.sprite.Sprite;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.Terrain;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.water.SimpleWaterProcessor;
import com.jme3.water.WaterFilter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * This is a spatial utility class which can be used to create or convert
 * certain spatial parameters.
 *
 * @author nidebruyn
 */
public class SpatialUtils {

    public static Spatial addSkySphere(Node parent, ColorRGBA bottomColor, ColorRGBA topColor, Camera camera) {

        Sphere sphere = new Sphere(20, 20, 100, false, true);
        Geometry sky = new Geometry("sky", sphere);
        sky.setQueueBucket(RenderQueue.Bucket.Sky);
        sky.setCullHint(Spatial.CullHint.Never);
        sky.setModelBound(new BoundingSphere(Float.POSITIVE_INFINITY, Vector3f.ZERO));
        sky.addControl(new CameraStickControl(camera));

        Material m = new Material(SharedSystem.getInstance().getBaseApplication().getAssetManager(), "Resources/MatDefs/lineargradient.j3md");
        m.setColor("StartColor", topColor);
        m.setColor("EndColor", bottomColor);
        m.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
        sky.setMaterial(m);

        rotate(sky, -90, 0, 0);

        parent.attachChild(sky);

        return sky;

    }

    /**
     *
     * @param parent
     * @param type
     * @return
     */
    public static Spatial addSkySphere(Node parent, int type, Camera camera) {
        String texture = "Resources/sky/day.jpg";

        if (type == 2) {
            texture = "Resources/sky/cloudy.jpg";

        } else if (type == 3) {
            texture = "Resources/sky/night.jpg";

        } else if (type == 4) {
            texture = "Resources/sky/dusk.jpg";

        } else if (type == 5) {
            texture = "Resources/sky/dawn.jpg";

        } else if (type == 6) {
            texture = "Resources/sky/flame.jpg";

        }

        return addSkySphere(parent, texture, camera);

    }

    /**
     * This will add a sky sphere with the given texture.
     *
     * @param parent
     * @param texture
     * @param camera
     * @return
     */
    public static Spatial addSkySphere(Node parent, String texture, Camera camera) {

        Sphere sphere = new Sphere(20, 20, 100, false, true);
        Geometry sky = new Geometry("sky", sphere);
        sky.setQueueBucket(RenderQueue.Bucket.Sky);
        sky.setCullHint(Spatial.CullHint.Never);
        sky.setModelBound(new BoundingSphere(Float.POSITIVE_INFINITY, Vector3f.ZERO));
        sky.addControl(new CameraStickControl(camera));

        Material m = addTexture(sky, texture, true);
        m.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);

        rotate(sky, -90, 0, 0);

        parent.attachChild(sky);

        return sky;

    }

    /**
     * This will add a sky dome with the given texture.
     *
     * @param parent
     * @param texture
     * @param camera
     * @return
     */
    public static Spatial addSkyDome(Node parent, String texture, Camera camera) {

        Dome dome = new Dome(Vector3f.ZERO, 11, 20, 100, true);
        Geometry sky = new Geometry("sky", dome);
        sky.setQueueBucket(RenderQueue.Bucket.Sky);
        sky.setCullHint(Spatial.CullHint.Never);
        sky.setModelBound(new BoundingSphere(Float.POSITIVE_INFINITY, Vector3f.ZERO));
        sky.addControl(new CameraStickControl(camera));

        Material m = addTexture(sky, texture, true);
        m.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);

//        rotate(sky, -90, 0, 0);
        parent.attachChild(sky);

        return sky;

    }

    /**
     * Add some real simple water to the scene.
     *
     * @param parent
     * @param size
     * @param yPos
     * @param waveSpeed
     * @param waterDepth
     * @return
     */
    public static SimpleWaterProcessor addSimpleWater(Node parent, float size, float yPos, float waveSpeed, boolean optimized) {

        // we create a water processor
        SimpleWaterProcessor waterProcessor = new SimpleWaterProcessor(SharedSystem.getInstance().getBaseApplication().getAssetManager());
        waterProcessor.setReflectionScene(parent);

        if (optimized) {
            waterProcessor.setRenderSize(128, 128);
        }

        SharedSystem.getInstance().getBaseApplication().getViewPort().addProcessor(waterProcessor);

        // we set the water plane
        Vector3f waterLocation = new Vector3f(0, yPos, 0);
        waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));

        // we set wave properties
        waterProcessor.setWaterDepth(40);         // transparency of water
        waterProcessor.setDistortionScale(0.08f); // strength of waves
        waterProcessor.setDistortionMix(0.1f); // strength of waves
        waterProcessor.setWaveSpeed(waveSpeed);       // speed of waves
//        waterProcessor.setWaterTransparency(0f);
//        waterProcessor.setWaterColor(ColorRGBA.Blue);
        waterProcessor.setReflectionClippingOffset(0);

        //creating a quad to render water to
//        Quad quad = new Quad(size, size);
        Disk disk = new Disk(7, size * 0.5f);
        disk.scaleTextureCoordinates(new Vector2f(size / 15f, size / 15f));

        //creating a geom to attach the water material
        Geometry water = new Geometry("water", disk);
        water.setLocalTranslation(0, yPos, 0);
        water.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        water.setMaterial(waterProcessor.getMaterial());
//        water.setShadowMode(RenderQueue.ShadowMode.Receive);
        parent.attachChild(water);

        return waterProcessor;

    }

    /**
     * Add more complex water.
     *
     * @param parent
     * @param lightDir
     * @param yPos
     * @return
     */
    public static FilterPostProcessor addOceanWater(Node parent, Vector3f lightDir, float yPos) {
        FilterPostProcessor fpp = new FilterPostProcessor(SharedSystem.getInstance().getBaseApplication().getAssetManager());
        final WaterFilter water = new WaterFilter(parent, lightDir);
        water.setWaterHeight(yPos);
        water.setWindDirection(new Vector2f(-0.15f, 0.15f));
        water.setFoamHardness(0.6f);
        water.setFoamExistence(new Vector3f(0.4f, 1f, 0.6f));
        water.setShoreHardness(0.2f);
        water.setMaxAmplitude(1f);
        water.setWaveScale(0.01f);
        water.setSpeed(0.9f);
        water.setShininess(0.7f);
        water.setNormalScale(0.75f);
//        water.setRefractionConstant(0.2f);
//        water.setReflectionDisplace(20f);
        water.setCausticsIntensity(0.5f);
        water.setWaterTransparency(0.8f);
        water.setColorExtinction(new Vector3f(10f, 20f, 30f));
        fpp.addFilter(water);
        SharedSystem.getInstance().getBaseApplication().getViewPort().addProcessor(fpp);
        return fpp;
    }

    /**
     * Convert the terrain to an unshaded terrain. This is for use on android
     * and slow devices.
     *
     * @param terrainQuad
     */
    public static void makeTerrainUnshaded(TerrainQuad terrainQuad) {
        SceneGraphVisitor sgv = new SceneGraphVisitor() {
            public void visit(Spatial spatial) {
                if (spatial instanceof Geometry) {
                    Geometry geom = (Geometry) spatial;

                    Material mat = new Material(SharedSystem.getInstance().getBaseApplication().getAssetManager(), "Common/MatDefs/Terrain/Terrain.j3md");
                    mat.setBoolean("useTriPlanarMapping", false);
                    mat.setTexture("Alpha", geom.getMaterial().getTextureParam("AlphaMap").getTextureValue());

                    if (geom.getMaterial().getTextureParam("DiffuseMap") != null) {
                        mat.setTexture("Tex1", geom.getMaterial().getTextureParam("DiffuseMap").getTextureValue());
                        mat.getTextureParam("Tex1").getTextureValue().setWrap(Texture.WrapMode.Repeat);
                        mat.setFloat("Tex1Scale", Float.valueOf(geom.getMaterial().getParam("DiffuseMap_0_scale").getValueAsString()));
                    }

                    if (geom.getMaterial().getTextureParam("DiffuseMap_1") != null) {
                        mat.setTexture("Tex2", geom.getMaterial().getTextureParam("DiffuseMap_1").getTextureValue());
                        mat.getTextureParam("Tex2").getTextureValue().setWrap(Texture.WrapMode.Repeat);
                        mat.setFloat("Tex2Scale", Float.valueOf(geom.getMaterial().getParam("DiffuseMap_1_scale").getValueAsString()));
                    }

                    if (geom.getMaterial().getTextureParam("DiffuseMap_2") != null) {
                        mat.setTexture("Tex3", geom.getMaterial().getTextureParam("DiffuseMap_2").getTextureValue());
                        mat.getTextureParam("Tex3").getTextureValue().setWrap(Texture.WrapMode.Repeat);
                        mat.setFloat("Tex3Scale", Float.valueOf(geom.getMaterial().getParam("DiffuseMap_2_scale").getValueAsString()));
                    }

                    geom.setMaterial(mat);

                }
            }
        };
        terrainQuad.depthFirstTraversal(sgv);
    }

    /**
     * Helper method which converts all ligting materials of a node to an
     * unshaded material.
     *
     * @param node
     */
    public static void makeUnshaded(Node node) {

        SceneGraphVisitor sgv = new SceneGraphVisitor() {
            public void visit(Spatial spatial) {

                if (spatial instanceof Geometry) {

                    Geometry geom = (Geometry) spatial;
                    Material mat = new Material(SharedSystem.getInstance().getBaseApplication().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                    Material tat = new Material(SharedSystem.getInstance().getBaseApplication().getAssetManager(), "Common/MatDefs/Terrain/Terrain.j3md");

                    if (geom.getMaterial().getTextureParam("DiffuseMap_1") != null) {

                        tat.setTexture("Alpha", geom.getMaterial().getTextureParam("AlphaMap").getTextureValue());

                        if (geom.getMaterial().getTextureParam("DiffuseMap") != null) {

                            tat.setTexture("Tex1", geom.getMaterial().getTextureParam("DiffuseMap").getTextureValue());
                            tat.getTextureParam("Tex1").getTextureValue().setWrap(Texture.WrapMode.Repeat);
                            tat.setFloat("Tex1Scale", Float.valueOf(geom.getMaterial().getParam("DiffuseMap_0_scale").getValueAsString()));

                        }

                        if (geom.getMaterial().getTextureParam("DiffuseMap_1") != null) {

                            tat.setTexture("Tex2", geom.getMaterial().getTextureParam("DiffuseMap_1").getTextureValue());
                            tat.getTextureParam("Tex2").getTextureValue().setWrap(Texture.WrapMode.Repeat);
                            tat.setFloat("Tex2Scale", Float.valueOf(geom.getMaterial().getParam("DiffuseMap_1_scale").getValueAsString()));

                        }

                        if (geom.getMaterial().getTextureParam("DiffuseMap_2") != null) {

                            tat.setTexture("Tex3", geom.getMaterial().getTextureParam("DiffuseMap_2").getTextureValue());
                            tat.getTextureParam("Tex3").getTextureValue().setWrap(Texture.WrapMode.Repeat);
                            tat.setFloat("Tex3Scale", Float.valueOf(geom.getMaterial().getParam("DiffuseMap_2_scale").getValueAsString()));

                        }

                        tat.setBoolean("useTriPlanarMapping", true);
                        geom.setMaterial(tat);

                    } else if (geom.getMaterial().getTextureParam("DiffuseMap") != null) {

                        mat.setTexture("ColorMap", geom.getMaterial().getTextureParam("DiffuseMap").getTextureValue());
                        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
                        mat.setFloat("AlphaDiscardThreshold", .5f);
                        mat.setFloat("ShadowIntensity", 5);
                        mat.setVector3("LightPos", new Vector3f(5, 20, 5));
                        geom.setMaterial(mat);

                    }

                }

            }
        };

        node.depthFirstTraversal(sgv);

    }
    
    public static void enableWireframe(Node node, final boolean enabled) {

        SceneGraphVisitor sgv = new SceneGraphVisitor() {
            public void visit(Spatial spatial) {

                if (spatial instanceof Geometry) {

                    Geometry geom = (Geometry) spatial;
                    Material mat = geom.getMaterial();
                    
                    if (mat != null) {
                        mat.getAdditionalRenderState().setWireframe(enabled);
                        
                    }

                }

            }
        };

        node.depthFirstTraversal(sgv);

    }

    /**
     * Helper method which converts all materials to pixelated
     *
     * @param node
     */
    public static void makePixelated(Node node) {

        SceneGraphVisitor sgv = new SceneGraphVisitor() {
            public void visit(Spatial spatial) {

                if (spatial instanceof Geometry) {

                    Geometry geom = (Geometry) spatial;
                    if (geom.getMaterial().getTextureParam("ColorMap") != null) {
//                        System.out.println("Found colormap");
                        MatParamTexture mpt = geom.getMaterial().getTextureParam("ColorMap");
                        mpt.getTextureValue().setMinFilter(Texture.MinFilter.NearestNoMipMaps);

                    }

                }

            }
        };

        node.depthFirstTraversal(sgv);

    }

    /**
     * Adds sunlight to the scene.
     *
     * @param parent
     * @return
     */
    public static DirectionalLight addSunLight(Node parent, ColorRGBA colorRGBA) {
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.85f, -0.5f)).normalizeLocal());
        sun.setColor(colorRGBA);
        sun.setFrustumCheckNeeded(true);
        parent.addLight(sun);
        /**
         * A white ambient light source.
         */
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.LightGray);
        ambient.setFrustumCheckNeeded(true);
        parent.addLight(ambient);

        return sun;
    }

    /**
     * Adds a camera node to the scene
     *
     * @param parent
     * @param camera
     * @param distance
     * @param height
     * @param angle
     * @return
     */
    public static Node addCameraNode(Node parent, Camera camera, float distance, float height, float angle) {
        final Node targetNode = new Node("camera-link");

        CameraNode cameraNode = new CameraNode("camera-node", camera);
        cameraNode.setLocalTranslation(0, height, -distance);
        cameraNode.rotate(angle * FastMath.DEG_TO_RAD, 0, 0);
        targetNode.attachChild(cameraNode);

        parent.attachChild(targetNode);

        return targetNode;
    }

    /**
     * Add a simple box to the node.
     *
     * @param parent
     * @param xExtend
     * @param yExtend
     * @param zExtend
     * @return
     */
    public static Spatial addBox(Node parent, float xExtend, float yExtend, float zExtend) {

        Box box = new Box(xExtend, yExtend, zExtend);
        Geometry geometry = new Geometry("box", box);
        parent.attachChild(geometry);
        geometry.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        return geometry;
    }

    /**
     * Add a sphere to the scene.
     *
     * @param parent
     * @param zSamples
     * @param radialSamples
     * @param radius
     * @return
     */
    public static Spatial addSphere(Node parent, int zSamples, int radialSamples, float radius) {

        Sphere sphere = new Sphere(zSamples, radialSamples, radius);
        Geometry geometry = new Geometry("sphere", sphere);
        parent.attachChild(geometry);
        geometry.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        return geometry;
    }
    
    public static Spatial addCone(Node parent, int radialSamples, float radius, float height) {

        Cylinder c = new Cylinder(2, radialSamples, 0.0001f, radius, height, true, false);                    
        Geometry geometry = new Geometry("cone", c);
        parent.attachChild(geometry);
        geometry.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        return geometry;
    }

    public static Spatial addDebugPoint(Node parent, float size, ColorRGBA color, Vector3f position) {
        Spatial marker = addBox(parent, size * 0.05f, size, size * 0.05f);
//        Spatial marker = addSphere(parent, 10, 10, size);
        addColor(marker, color, true);
        marker.setLocalTranslation(position.x, position.y, position.z);
        marker.move(0, size, 0);
        return marker;
    }

    /**
     * Add a cyclinder to the scene.
     *
     * @param parent
     * @param axisSamples
     * @param radialSamples
     * @param radius
     * @param height
     * @param closed
     * @return
     */
    public static Spatial addCylinder(Node parent, int axisSamples, int radialSamples, float radius, float height, boolean closed) {

        Cylinder cylinder = new Cylinder(axisSamples, radialSamples, radius, height, closed);
        Geometry geometry = new Geometry("cylinder", cylinder);
        parent.attachChild(geometry);
        geometry.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        return geometry;
    }

    /**
     * Add a simple plane to the node.
     *
     * @param parent
     * @param xExtend
     * @param zExtend
     * @return
     */
    public static Spatial addPlane(Node parent, float xExtend, float zExtend) {

        Quad quad = new Quad(xExtend * 2, zExtend * 2);
        Geometry geometry = new Geometry("quad", quad);
        geometry.rotate(-FastMath.DEG_TO_RAD * 90, 0, 0);
        geometry.move(-xExtend, 0, zExtend);
        parent.attachChild(geometry);
        geometry.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        return geometry;
    }

    /**
     * Add a sprite to the scene
     *
     * @param parent
     * @param width
     * @param height
     * @return
     */
    public static Sprite addSprite(Node parent, float width, float height) {
        Sprite sprite = new Sprite("sprite", width, height);
        parent.attachChild(sprite);

        return sprite;
    }

    /**
     * Add color to the spatial.
     *
     *
     * @param colorRGBA
     * @return
     */
    public static void addColor(Spatial spatial, ColorRGBA colorRGBA, boolean unshaded) {
        Material material = null;

        if (unshaded) {
            material = new Material(SharedSystem.getInstance().getBaseApplication().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            material.setColor("Color", colorRGBA);

        } else {
            material = new Material(SharedSystem.getInstance().getBaseApplication().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
            material.setBoolean("UseMaterialColors", true);
            material.setColor("Ambient", colorRGBA);
            material.setColor("Diffuse", colorRGBA);

        }

        spatial.setMaterial(material);

    }

    /**
     * Add color to the spatial.
     *
     *
     * @param colorRGBA
     * @return
     */
    public static Material addTexture(Spatial spatial, String texturePath, boolean unshaded) {
        Material material = null;

        Texture texture = SharedSystem.getInstance().getBaseApplication().getAssetManager().loadTexture(texturePath);
//        texture.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
        texture.setWrap(Texture.WrapMode.Repeat);

        if (unshaded) {
            material = new Material(SharedSystem.getInstance().getBaseApplication().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            material.setTexture("ColorMap", texture);

        } else {
            material = new Material(SharedSystem.getInstance().getBaseApplication().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
            material.setTexture("DiffuseMap", texture);

        }

        spatial.setMaterial(material);

        return material;
    }

    public static void addMaterial(Spatial spatial, Material material) {
        spatial.setMaterial(material);

    }

    /**
     * Add mass to the spatial.
     *
     * @param spatial
     * @param mass
     * @return
     */
    public static RigidBodyControl addMass(Spatial spatial, float mass) {

        if (SharedSystem.getInstance().getBaseApplication() instanceof Base3DApplication) {
            Base3DApplication base3DApplication = (Base3DApplication) SharedSystem.getInstance().getBaseApplication();

            RigidBodyControl rigidBodyControl = spatial.getControl(RigidBodyControl.class);

            if (rigidBodyControl == null) {
                CollisionShape collisionShape = null;
                if (spatial instanceof Geometry) {
                    //Check for box mesh
                    if (((Geometry) spatial).getMesh() instanceof Box) {
                        Box box = (Box) ((Geometry) spatial).getMesh();
                        collisionShape = new BoxCollisionShape(new Vector3f(box.getXExtent(), box.getYExtent(), box.getZExtent()));

                    } else if (((Geometry) spatial).getMesh() instanceof Sphere) {
                        Sphere sphere = (Sphere) ((Geometry) spatial).getMesh();
                        collisionShape = new SphereCollisionShape(sphere.getRadius());
                    }

                    //TODO: Need to check for other mesh types
                }

                if (collisionShape != null) {
                    rigidBodyControl = new RigidBodyControl(collisionShape, mass);
                } else {
                    rigidBodyControl = new RigidBodyControl(mass);
                }

                spatial.addControl(rigidBodyControl);
                base3DApplication.getBulletAppState().getPhysicsSpace().add(spatial);
            }
            rigidBodyControl.setMass(mass);

            return rigidBodyControl;

        } else {
            throw new RuntimeException("Requires a Base3DApplication implementations with physics enabled.");

        }

    }

    /**
     * Translate any object to a given position.
     *
     * @param spatial
     * @param x
     * @param y
     * @param z
     */
    public static void translate(Spatial spatial, float x, float y, float z) {

        if (spatial.getControl(RigidBodyControl.class) != null) {
            spatial.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(x, y, z));
        } else {
            spatial.setLocalTranslation(x, y, z);
        }

    }

    /**
     * Move any object with the given amount.
     *
     * @param spatial
     * @param x
     * @param y
     * @param z
     */
    public static void move(Spatial spatial, float xAmount, float yAmount, float zAmount) {

        if (spatial.getControl(RigidBodyControl.class) != null) {
            spatial.getControl(RigidBodyControl.class).setPhysicsLocation(new Vector3f(
                    spatial.getControl(RigidBodyControl.class).getPhysicsLocation().x + xAmount,
                    spatial.getControl(RigidBodyControl.class).getPhysicsLocation().y + yAmount,
                    spatial.getControl(RigidBodyControl.class).getPhysicsLocation().z + zAmount));
        } else {
            spatial.move(xAmount, yAmount, zAmount);
        }

    }

    /**
     * This helper method will interpolate a spatial to a position.
     *
     * @param spatial
     * @param x
     * @param y
     * @param z
     * @param time
     * @param delay
     */
    public static Tween interpolate(Spatial spatial, float x, float y, float z, float time, float delay, boolean loop) {
        int repeat = 0;
        if (loop) {
            repeat = Tween.INFINITY;
        }

        if (spatial.getControl(RigidBodyControl.class) == null) {
            return Tween.to(spatial, SpatialAccessor.POS_XYZ, time)
                    .target(x, y, z)
                    .delay(delay)
                    .repeatYoyo(repeat, delay)
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

        } else {
            return Tween.to(spatial.getControl(RigidBodyControl.class), RigidbodyAccessor.POS_XYZ, time)
                    .target(x, y, z)
                    .delay(delay)
                    .repeatYoyo(repeat, delay)
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
        }

    }

    public static void bounce(Spatial spatial, float x, float y, float z, float time, float delay, int count) {

        if (spatial.getControl(RigidBodyControl.class) == null) {
            Tween.to(spatial, SpatialAccessor.POS_XYZ, time)
                    .target(x, y, z)
                    .delay(delay)
                    .ease(Circ.OUT)
                    .repeatYoyo(count, delay)
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

        } else {
            Tween.to(spatial.getControl(RigidBodyControl.class), RigidbodyAccessor.POS_XYZ, time)
                    .target(x, y, z)
                    .delay(delay)
                    .ease(Circ.OUT)
                    .repeatYoyo(count, delay)
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
        }

    }

    /**
     * This method will rotate a spatial to the given angle.
     *
     * @param spatial
     * @param xAngle
     * @param yAngle
     * @param zAngle
     */
    public static void rotateTo(Spatial spatial, float xAngle, float yAngle, float zAngle) {

        float angles[] = {xAngle * FastMath.DEG_TO_RAD, yAngle * FastMath.DEG_TO_RAD, zAngle * FastMath.DEG_TO_RAD};

        if (spatial.getControl(RigidBodyControl.class) != null) {
            spatial.getControl(RigidBodyControl.class).setPhysicsRotation(new Quaternion(angles));
        } else {
            spatial.setLocalRotation(new Quaternion(angles));
        }

    }

    /**
     * This method will rotate a spatial a given amount.
     *
     * @param spatial
     * @param xAngle
     * @param yAngle
     * @param zAngle
     */
    public static void rotate(Spatial spatial, float xAngle, float yAngle, float zAngle) {

        if (spatial.getControl(RigidBodyControl.class) != null) {
            Quaternion q = spatial.getControl(RigidBodyControl.class).getPhysicsRotation();
            float angles[] = q.toAngles(null);
            angles[0] = angles[0] + xAngle * FastMath.DEG_TO_RAD;
            angles[1] = angles[1] + yAngle * FastMath.DEG_TO_RAD;
            angles[2] = angles[2] + zAngle * FastMath.DEG_TO_RAD;

            spatial.getControl(RigidBodyControl.class).setPhysicsRotation(new Quaternion(angles));

        } else {
            spatial.rotate(xAngle * FastMath.DEG_TO_RAD, yAngle * FastMath.DEG_TO_RAD, zAngle * FastMath.DEG_TO_RAD);
        }

    }

    /**
     * This helper method will slerp the spatial to a rotation.
     *
     * @param spatial
     * @param x
     * @param y
     * @param z
     * @param time
     * @param delay
     */
    public static void slerp(Spatial spatial, float xAngle, float yAngle, float zAngle, float time, float delay, boolean loop) {
        int repeat = 0;
        if (loop) {
            repeat = Tween.INFINITY;
        }

        if (spatial.getControl(RigidBodyControl.class) == null) {
            Tween.to(spatial, SpatialAccessor.ROTATION_XYZ, time)
                    .target(xAngle, yAngle, zAngle)
                    .delay(delay)
                    .repeatYoyo(repeat, delay)
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

        } else {
            Tween.to(spatial.getControl(RigidBodyControl.class), RigidbodyAccessor.ROTATION_XYZ, time)
                    .target(xAngle, yAngle, zAngle)
                    .delay(delay)
                    .repeatYoyo(repeat, delay)
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
        }

    }

    public static void scaleBounce(Spatial spatial, float scaleX, float scaleY, float time, float delay, boolean loop) {
        int repeat = 0;
        if (loop) {
            repeat = Tween.INFINITY;
        }

        Tween.to(spatial, SpatialAccessor.SCALE_XYZ, time)
                .target(scaleX, scaleY, 1)
                .delay(delay)
                .ease(Circ.OUT)
                .repeatYoyo(repeat, delay)
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
    }

    /**
     * This is a helper method that will move a spatial object in the -z direction which is forward.
     * 
     * @param spatial
     * @param amount 
     */
    public static void forward(Spatial spatial, float amount) {
        
        if (spatial != null) {
            
            Vector3f dir = spatial.getLocalRotation().getRotationColumn(1);
            dir = dir.normalize();
//            Debug.log("forward = " + dir);
            spatial.move(amount*dir.x, amount*dir.y, amount*dir.z);
            
        }
        
    }
    
    
    /**
     * Perform the actual height modification on the terrain.
     * @param worldLoc the location in the world where the tool was activated
     * @param radius of the tool, terrain in this radius will be affected
     * @param heightFactor the amount to adjust the height by
     */
    public static void doModifyTerrainHeight(Terrain terrain, Vector3f worldLoc, float radius, float heightFactor) {

        if (terrain == null)
            return;

        int radiusStepsX = (int) (radius / ((Node)terrain).getLocalScale().x);
        int radiusStepsZ = (int) (radius / ((Node)terrain).getLocalScale().z);

        float xStepAmount = ((Node)terrain).getLocalScale().x;
        float zStepAmount = ((Node)terrain).getLocalScale().z;

        List<Vector2f> locs = new ArrayList<Vector2f>();
        List<Float> heights = new ArrayList<Float>();

        for (int z=-radiusStepsZ; z<radiusStepsZ; z++) {
            for (int x=-radiusStepsZ; x<radiusStepsX; x++) {

                float locX = worldLoc.x + (x*xStepAmount);
                float locZ = worldLoc.z + (z*zStepAmount);

                // see if it is in the radius of the tool
                if (isInRadius(locX-worldLoc.x,locZ-worldLoc.z,radius)) {
                    // adjust height based on radius of the tool
                    float h = calculateHeight(radius, heightFactor, locX-worldLoc.x, locZ-worldLoc.z);
                    // increase the height
                    locs.add(new Vector2f(locX, locZ));
                    heights.add(h);
                }
            }
        }

        // do the actual height adjustment
        terrain.adjustHeight(locs, heights);

        ((Node)terrain).updateModelBound(); // or else we won't collide with it where we just edited
        
    }
    
    /**
     * See if the X,Y coordinate is in the radius of the circle. It is assumed
     * that the "grid" being tested is located at 0,0 and its dimensions are 2*radius.
     * @param x
     * @param z
     * @param radius
     * @return
     */
    public static boolean isInRadius(float x, float y, float radius) {
        Vector2f point = new Vector2f(x,y);
        // return true if the distance is less than equal to the radius
        return Math.abs(point.length()) <= radius;
    }
    
    /**
     * Interpolate the height value based on its distance from the center (how far along
     * the radius it is).
     * The farther from the center, the less the height will be.
     * This produces a linear height falloff.
     * @param radius of the tool
     * @param heightFactor potential height value to be adjusted
     * @param x location
     * @param z location
     * @return the adjusted height value
     */
    public static float calculateHeight(float radius, float heightFactor, float x, float z) {
        float val = calculateRadiusPercent(radius, x, z);
        return heightFactor * val;
    }
    
    public static float calculateRadiusPercent(float radius, float x, float z) {
         // find percentage for each 'unit' in radius
        Vector2f point = new Vector2f(x,z);
        float val = Math.abs(point.length()) / radius;
        val = 1f - val;
        return val;
    }
}
