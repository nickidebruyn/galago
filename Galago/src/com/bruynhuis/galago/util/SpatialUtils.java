/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.util;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Circ;
import com.bruynhuis.galago.app.Base3DApplication;
import com.bruynhuis.galago.control.tween.RigidbodyAccessor;
import com.bruynhuis.galago.control.tween.SpatialAccessor;
import com.bruynhuis.galago.sprite.Sprite;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
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
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.water.SimpleWaterProcessor;
import com.jme3.water.WaterFilter;

/**
 *
 * This is a spatial utility class which can be used to create or convert
 * certain spatial parameters.
 *
 * @author nidebruyn
 */
public class SpatialUtils {

    /**
     *
     * @param parent
     * @param type
     * @return
     */
    public static Spatial addSkySphere(Node parent, int type) {
        String texture = "Resources/sky/day.jpg";

        if (type == 2) {
            texture = "Resources/sky/cloudy.jpg";
        } else if (type == 3) {
            texture = "Resources/sky/night.jpg";
        }

        Texture t = SharedSystem.getInstance().getBaseApplication().getAssetManager().loadTexture(texture);

        Spatial sky = SkyFactory.createSky(SharedSystem.getInstance().getBaseApplication().getAssetManager(), t, true);
        parent.attachChild(sky);
        sky.setLocalScale(100);
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
    public static SimpleWaterProcessor addSimpleWater(Node parent, float size, float yPos, float waveSpeed, float waterDepth) {
        //create processor
        SimpleWaterProcessor simpleWaterProcessor = new SimpleWaterProcessor(SharedSystem.getInstance().getBaseApplication().getAssetManager());
        simpleWaterProcessor.setReflectionScene(parent);
//        simpleWaterProcessor.setDistortionMix(0.5f);
//        simpleWaterProcessor.setDistortionScale(0.15f);
        simpleWaterProcessor.setWaveSpeed(waveSpeed);
        simpleWaterProcessor.setWaterDepth(waterDepth);
        simpleWaterProcessor.setWaterTransparency(0.15f);
        simpleWaterProcessor.setTexScale(2);
        simpleWaterProcessor.setWaterColor(ColorRGBA.Blue);
        SharedSystem.getInstance().getBaseApplication().getViewPort().addProcessor(simpleWaterProcessor);
//        simpleWaterProcessor.setLightPosition(new Vector3f(30, 30, -30));

        //create water quad
        Geometry waterPlane = simpleWaterProcessor.createWaterGeometry(size, size);
        waterPlane.setMaterial(simpleWaterProcessor.getMaterial());
        waterPlane.setLocalTranslation(-size * 0.5f, 0.5f, size * 0.5f);

        parent.attachChild(waterPlane);

        return simpleWaterProcessor;

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
        water.setFoamHardness(0.85f);
        water.setFoamExistence(new Vector3f(0.2f, 1f, 0.6f));
        water.setShoreHardness(0.5f);
        water.setMaxAmplitude(0.5f);
        water.setWaveScale(0.01f);
        water.setSpeed(0.9f);
        water.setShininess(0.1f);
        water.setNormalScale(0.75f);
//        water.setRefractionConstant(0.2f);
//        water.setReflectionDisplace(20f);
        water.setCausticsIntensity(0.8f);
        water.setWaterTransparency(1.2f);
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

    /**
     * Adds sunlight to the scene.
     *
     * @param parent
     * @return
     */
    public static DirectionalLight addSunLight(Node parent, ColorRGBA colorRGBA) {
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, 0.5f)).normalizeLocal());
        sun.setColor(colorRGBA);
        parent.addLight(sun);
        
        DirectionalLightShadowRenderer shadowRenderer = new DirectionalLightShadowRenderer(SharedSystem.getInstance().getBaseApplication().getAssetManager(), 512, 1);
        shadowRenderer.setLight(sun);
        SharedSystem.getInstance().getBaseApplication().getViewPort().addProcessor(shadowRenderer);

        return sun;
    }
    
    /**
     * Adds a camera node to the scene
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
        cameraNode.rotate(angle*FastMath.DEG_TO_RAD, 0, 0);
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
    
    /**
     * Add a sprite to the scene
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

                } else {
                    collisionShape = new CompoundCollisionShape();

                }

                rigidBodyControl = new RigidBodyControl(collisionShape, mass);
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
    public static void interpolate(Spatial spatial, float x, float y, float z, float time, float delay, boolean loop) {
        int repeat = 0;
        if (loop) {
            repeat = Tween.INFINITY;
        }
        
        if (spatial.getControl(RigidBodyControl.class) == null) {
            Tween.to(spatial, SpatialAccessor.POS_XYZ, time)
                .target(x, y, z)
                .delay(delay)
                .repeatYoyo(repeat, delay)
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
            
        } else {
            Tween.to(spatial.getControl(RigidBodyControl.class), RigidbodyAccessor.POS_XYZ, time)
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
     * @param spatial
     * @param xAngle
     * @param yAngle
     * @param zAngle 
     */
    public static void rotateTo(Spatial spatial, float xAngle, float yAngle, float zAngle) {
        
        float angles[] = {xAngle* FastMath.DEG_TO_RAD, yAngle* FastMath.DEG_TO_RAD ,zAngle* FastMath.DEG_TO_RAD};

        if (spatial.getControl(RigidBodyControl.class) != null) {
            spatial.getControl(RigidBodyControl.class).setPhysicsRotation(new Quaternion(angles));
        } else {
            spatial.setLocalRotation(new Quaternion(angles));
        }

    }

    /**
     * This method will rotate a spatial a given amount.
     * @param spatial
     * @param xAngle
     * @param yAngle
     * @param zAngle 
     */
    public static void rotate(Spatial spatial, float xAngle, float yAngle, float zAngle) {
        
        if (spatial.getControl(RigidBodyControl.class) != null) {
            Quaternion q = spatial.getControl(RigidBodyControl.class).getPhysicsRotation();
            float angles[] = q.toAngles(null);
            angles[0] = angles[0] + xAngle* FastMath.DEG_TO_RAD;
            angles[1] = angles[1] + yAngle* FastMath.DEG_TO_RAD;
            angles[2] = angles[2] + zAngle* FastMath.DEG_TO_RAD;
            
            spatial.getControl(RigidBodyControl.class).setPhysicsRotation(new Quaternion(angles));
            
        } else {
            spatial.rotate(xAngle* FastMath.DEG_TO_RAD, yAngle* FastMath.DEG_TO_RAD, zAngle* FastMath.DEG_TO_RAD);
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
    
    
}
