/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.util;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Circ;
import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.control.tween.Rigidbody2DAccessor;
import com.bruynhuis.galago.control.tween.SpatialAccessor;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CapsuleCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.TriCollisionShape;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

/**
 *
 * This is a spatial utility class which can be used to create or convert
 * certain spatial parameters.
 *
 * @author nidebruyn
 */
public class SpriteUtils {

    /**
     * This method will add a sprite to the parent object and use its native
     * resolution to load the image. * @param parent
     *
     * @param image
     * @param scale
     * @return
     */
    public static Sprite addSprite(Node parent, String image, boolean pixelated, float scale, float x, float y, float z) {
        float width = 100;
        float height = 100;

        //First we load the texture
        Texture texture = SharedSystem.getInstance().getBaseApplication().getAssetManager().loadTexture(image);
        width = ((float) texture.getImage().getWidth()) * scale;
        height = ((float) texture.getImage().getHeight()) * scale;

        if (pixelated) {
            texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
            texture.setMagFilter(Texture.MagFilter.Nearest);
        } else {
            texture.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
        }
//
//        Material material = new Material(SharedSystem.getInstance().getBaseApplication().getAssetManager(), "Resources/MatDefs/SpriteShader.j3md");
//        material.setColor("Color", ColorRGBA.White);
////        material.setFloat("AlphaDiscardThreshold", 0.1f);
//        material.setTexture("Texture", texture);
////        material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
//
//        //NB THIS IS VERY IMPORTANT FOR ALPHA BLENDING
//        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Custom);
//        material.getAdditionalRenderState().setCustomBlendFactors(RenderState.BlendFunc.Src_Alpha, 
//                RenderState.BlendFunc.One_Minus_Src_Alpha, 
//                RenderState.BlendFunc.One, 
//                RenderState.BlendFunc.One_Minus_Src_Alpha);
//        
//        material.getAdditionalRenderState().setBlendEquation(RenderState.BlendEquation.Add);
//        material.getAdditionalRenderState().setBlendEquationAlpha(RenderState.BlendEquationAlpha.Add);
        //create the material with the spritesheet material definition
        Material material = new Material(SharedSystem.getInstance().getBaseApplication().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
//        Material material = new Material(SharedSystem.getInstance().getBaseApplication().getAssetManager(), "Resources/MatDefs/SpriteShader.j3md");
        //set the spritesheet png built with TexturePacker
        material.setTexture("ColorMap", texture);
        material.setColor("Color", ColorRGBA.White);
//        material.setFloat("AlphaDiscardThreshold", 0.55f);

        //your sprites most likely contain transparency, so it's probably better to set Alpha otherwise you'll see artefacts
//        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        //Codota test
//        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Custom);
//        material.getAdditionalRenderState().setBlendEquation(RenderState.BlendEquation.Subtract);
//        material.getAdditionalRenderState().setBlendEquationAlpha(RenderState.BlendEquationAlpha.Subtract);
//        material.getAdditionalRenderState().setCustomBlendFactors(
//                RenderState.BlendFunc.Src_Alpha, RenderState.BlendFunc.Src_Alpha,
//                RenderState.BlendFunc.Zero, RenderState.BlendFunc.One);
//        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Custom);
//        material.getAdditionalRenderState().setBlendEquation(RenderState.BlendEquation.Add);
//        material.getAdditionalRenderState().setBlendEquationAlpha(RenderState.BlendEquationAlpha.Add);
//        material.getAdditionalRenderState().setCustomBlendFactors(RenderState.BlendFunc.Src_Alpha, 
//                RenderState.BlendFunc.One_Minus_Src_Alpha, 
//                RenderState.BlendFunc.One, 
//                RenderState.BlendFunc.One_Minus_Src_Alpha);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);

        Sprite sprite = new Sprite("sprite", width, height);
        sprite.setMaterial(material);
        sprite.flipCoords(true);
        sprite.flipHorizontal(true);
//        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);
        sprite.setOffset(x, y, z);
        parent.attachChild(sprite);

//        SpatialUtils.addDebugSphere(sprite, 0.1f, ColorRGBA.Blue, new Vector3f(0, 0, 0));
        return sprite;
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
     * Add a sprite image to the parent.
     *
     * @param parent
     * @param image
     * @param width
     * @param height
     * @return
     */
    public static Sprite addSprite(Node parent, String image, float width, float height) {
        Sprite sprite = addSprite(parent, width, height);
        addImage(sprite, image);

        return sprite;
    }

    public static Sprite addSpritePixelated(Node parent, String image, float width, float height) {
        Sprite sprite = addSprite(parent, width, height);
        addImage(sprite, image, true);

        return sprite;
    }

    /**
     * Add a sprite with an offset.
     *
     * @param parent
     * @param image
     * @param width
     * @param height
     * @param offsetX
     * @param offsetY
     * @param offsetZ
     * @return
     */
    public static Sprite addSprite(Node parent, String image, float width, float height, float offsetX, float offsetY, float offsetZ) {
        Sprite sprite = addSprite(parent, image, width, height);
        sprite.setLocalTranslation(offsetX, offsetY, offsetZ);
        return sprite;
    }

    /**
     *
     * @param parent
     * @param image
     * @param width
     * @param height
     * @param offsetX
     * @param offsetY
     * @param offsetZ
     * @return
     */
    public static Sprite addSpritePixelated(Node parent, String image, float width, float height, float offsetX, float offsetY, float offsetZ) {
        Sprite sprite = addSpritePixelated(parent, image, width, height);
        sprite.setLocalTranslation(offsetX, offsetY, offsetZ);
        return sprite;
    }

    /**
     * Add color to the spatial.
     *
     *
     * @param colorRGBA
     * @return
     */
    public static void addColor(Sprite sprite, ColorRGBA colorRGBA, boolean unshaded) {
        Material material = null;

        if (unshaded) {
            material = new Material(SharedSystem.getInstance().getBaseApplication().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            material.setColor("Color", colorRGBA);
            material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);

        } else {
            material = new Material(SharedSystem.getInstance().getBaseApplication().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
            material.setBoolean("UseMaterialColors", true);
            material.setColor("Ambient", colorRGBA);
            material.setColor("Diffuse", colorRGBA);
            material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);

        }

        sprite.setMaterial(material);

    }

    public static void addMaterial(Spatial spatial, Material material) {
        spatial.setMaterial(material);

    }

    public static Sprite addBackgroundSky(Node parent, float width, float height, float depth, ColorRGBA bottomColor, ColorRGBA topColor, Camera camera) {

        Sprite sky = new Sprite("sky", width, height);

        Material m = new Material(SharedSystem.getInstance().getBaseApplication().getAssetManager(), "Resources/MatDefs/lineargradient.j3md");
        m.setColor("StartColor", topColor);
        m.setColor("EndColor", bottomColor);
        m.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
        sky.setMaterial(m);

        SpriteUtils.move(sky, 0, 0, depth);

        parent.attachChild(sky);

        return sky;

    }

    public static Material addGradientColor(Spatial spatial, ColorRGBA bottomColor, ColorRGBA topColor) {
        Material material = new Material(SharedSystem.getInstance().getBaseApplication().getAssetManager(), "Resources/MatDefs/lineargradient.j3md");
        material.setColor("StartColor", bottomColor);
        material.setColor("EndColor", topColor);
        material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Back);
        spatial.setMaterial(material);
        return material;
    }

    public static void addImage(Sprite sprite, String image) {

        //First we load the texture
        Texture texture = SharedSystem.getInstance().getBaseApplication().getAssetManager().loadTexture(image);
        texture.setWrap(Texture.WrapMode.Repeat);
        if (SharedSystem.getInstance().getBaseApplication().getTextureManager().isPixelated()) {
            texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
            texture.setMagFilter(Texture.MagFilter.Nearest);
        } else {
            texture.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
        }

        Material material = new Material(SharedSystem.getInstance().getBaseApplication().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", texture);
        material.setColor("Color", ColorRGBA.White);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
//        material.getAdditionalRenderState().setCustomBlendFactors(RenderState.BlendFunc.Src_Alpha, RenderState.BlendFunc.One_Minus_Src_Alpha, RenderState.BlendFunc.One, RenderState.BlendFunc.One_Minus_Src_Alpha);
        material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        sprite.setMaterial(material);

        sprite.flipCoords(true);
        sprite.flipHorizontal(true);
        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);

    }

    public static void addImage(Sprite sprite, String image, boolean pixelated) {

        //First we load the texture
        Texture texture = SharedSystem.getInstance().getBaseApplication().getAssetManager().loadTexture(image);
        texture.setWrap(Texture.WrapMode.Repeat);
        if (pixelated) {
//            texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
            texture.setMagFilter(Texture.MagFilter.Nearest);
        } else {
            texture.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
        }

        Material material = new Material(SharedSystem.getInstance().getBaseApplication().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", texture);
        material.setColor("Color", ColorRGBA.White);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
//        material.getAdditionalRenderState().setCustomBlendFactors(RenderState.BlendFunc.Src_Alpha, RenderState.BlendFunc.One_Minus_Src_Alpha, RenderState.BlendFunc.One, RenderState.BlendFunc.One_Minus_Src_Alpha);
        material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        sprite.setMaterial(material);

        sprite.flipCoords(true);
        sprite.flipHorizontal(true);
//        sprite.setQueueBucket(RenderQueue.Bucket.Transparent);

    }

    /**
     * Add mass to the spatial.
     *
     * @param spatial
     * @param mass
     * @return
     */
    public static RigidBodyControl addBoxMass(Sprite sprite, float width, float height, float mass) {

        if (SharedSystem.getInstance().getBaseApplication() instanceof Base2DApplication) {
            Base2DApplication base2DApplication = (Base2DApplication) SharedSystem.getInstance().getBaseApplication();

            RigidBodyControl rigidBodyControl = sprite.getControl(RigidBodyControl.class);

            if (rigidBodyControl == null) {
                CollisionShape collisionShape = new BoxCollisionShape(width, height);
                rigidBodyControl = new RigidBodyControl(collisionShape, mass);
                sprite.addControl(rigidBodyControl);
                base2DApplication.getDyn4jAppState().getPhysicsSpace().add(sprite);
            }
            rigidBodyControl.setMass(mass);

            return rigidBodyControl;

        } else {
            throw new RuntimeException("Requires a Base2DApplication implementations with physics enabled.");

        }

    }

    /**
     * Add mass to the spatial.
     *
     * @param spatial
     * @param mass
     * @return
     */
    public static RigidBodyControl addCapsuleMass(Spatial sprite, float width, float height, float mass) {

        if (SharedSystem.getInstance().getBaseApplication() instanceof Base2DApplication) {
            Base2DApplication base2DApplication = (Base2DApplication) SharedSystem.getInstance().getBaseApplication();

            RigidBodyControl rigidBodyControl = sprite.getControl(RigidBodyControl.class);

            if (rigidBodyControl == null) {
                CollisionShape collisionShape = new CapsuleCollisionShape(width, height);
                rigidBodyControl = new RigidBodyControl(collisionShape, mass);
                sprite.addControl(rigidBodyControl);
                base2DApplication.getDyn4jAppState().getPhysicsSpace().add(sprite);
            }
            rigidBodyControl.setMass(mass);

            return rigidBodyControl;

        } else {
            throw new RuntimeException("Requires a Base2DApplication implementations with physics enabled.");

        }

    }

    /**
     * Add mass to the spatial.
     *
     * @param spatial
     * @param mass
     * @return
     */
    public static RigidBodyControl addCircleMass(Spatial sprite, float radius, float mass) {

        if (SharedSystem.getInstance().getBaseApplication() instanceof Base2DApplication) {
            Base2DApplication base2DApplication = (Base2DApplication) SharedSystem.getInstance().getBaseApplication();

            RigidBodyControl rigidBodyControl = sprite.getControl(RigidBodyControl.class);

            if (rigidBodyControl == null) {
                CollisionShape collisionShape = new CircleCollisionShape(radius);
                rigidBodyControl = new RigidBodyControl(collisionShape, mass);
                sprite.addControl(rigidBodyControl);
                base2DApplication.getDyn4jAppState().getPhysicsSpace().add(sprite);
            }
            rigidBodyControl.setMass(mass);

            return rigidBodyControl;

        } else {
            throw new RuntimeException("Requires a Base2DApplication implementations with physics enabled.");

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
    public static void translate(Spatial sprite, float x, float y, float z) {

        if (sprite.getControl(RigidBodyControl.class) != null) {
            sprite.setLocalTranslation(x, y, z);
            sprite.getControl(RigidBodyControl.class).setPhysicLocation(new Vector3f(x, y, z));
        } else {
            sprite.setLocalTranslation(x, y, z);
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
    public static void move(Spatial sprite, float xAmount, float yAmount, float zAmount) {

        if (sprite.getControl(RigidBodyControl.class) != null) {
            sprite.setLocalTranslation(new Vector3f(
                    sprite.getControl(RigidBodyControl.class).getPhysicLocation().x + xAmount,
                    sprite.getControl(RigidBodyControl.class).getPhysicLocation().y + yAmount,
                    sprite.getControl(RigidBodyControl.class).getPhysicLocation().z + zAmount));
            sprite.getControl(RigidBodyControl.class).setPhysicLocation(new Vector3f(
                    sprite.getControl(RigidBodyControl.class).getPhysicLocation().x + xAmount,
                    sprite.getControl(RigidBodyControl.class).getPhysicLocation().y + yAmount,
                    sprite.getControl(RigidBodyControl.class).getPhysicLocation().z + zAmount));
        } else {
            sprite.move(xAmount, yAmount, zAmount);
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
    public static void interpolate(Spatial sprite, float x, float y, float z, float time, float delay, boolean loop) {
        int repeat = 0;
        if (loop) {
            repeat = Tween.INFINITY;
        }

        if (sprite.getControl(RigidBodyControl.class) == null) {
            Tween.to(sprite, SpatialAccessor.POS_XYZ, time)
                    .target(x, y, z)
                    .delay(delay)
                    .repeatYoyo(repeat, delay)
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

        } else {
            Tween.to(sprite.getControl(RigidBodyControl.class), Rigidbody2DAccessor.POS, time)
                    .target(x, y, z)
                    .delay(delay)
                    .repeatYoyo(repeat, delay)
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
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
    public static void interpolate(Spatial sprite, float x, float y, float z, float time, float delay, boolean loop, TweenEquation tweenEquation) {
        int repeat = 0;
        if (loop) {
            repeat = Tween.INFINITY;
        }

        if (sprite.getControl(RigidBodyControl.class) == null) {
            Tween.to(sprite, SpatialAccessor.POS_XYZ, time)
                    .target(x, y, z)
                    .delay(delay)
                    .ease(tweenEquation)
                    .repeatYoyo(repeat, delay)                    
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

        } else {
            Tween.to(sprite.getControl(RigidBodyControl.class), Rigidbody2DAccessor.POS, time)
                    .target(x, y, z)
                    .delay(delay)
                    .ease(tweenEquation)
                    .repeatYoyo(repeat, delay)
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
        }

    }    

    public static void bounce(Sprite sprite, float x, float y, float z, float time, float delay, int count) {

        if (sprite.getControl(RigidBodyControl.class) == null) {
            Tween.to(sprite, SpatialAccessor.POS_XYZ, time)
                    .target(x, y, z)
                    .delay(delay)
                    .ease(Circ.OUT)
                    .repeatYoyo(count, delay)
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

        } else {
            Tween.to(sprite.getControl(RigidBodyControl.class), Rigidbody2DAccessor.POS, time)
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
    public static void rotateTo(Sprite sprite, float angle) {

        if (sprite.getControl(RigidBodyControl.class) != null) {
            sprite.getControl(RigidBodyControl.class).setPhysicRotation(angle * FastMath.DEG_TO_RAD);
        } else {
            Quaternion quaternion = new Quaternion();
            quaternion.fromAngleAxis(angle * FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
            sprite.setLocalRotation(quaternion);
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
    public static void rotate(Sprite sprite, float angle) {

        if (sprite.getControl(RigidBodyControl.class) != null) {
            float a = sprite.getControl(RigidBodyControl.class).getPhysicRotation() * FastMath.RAD_TO_DEG;
            a = a + angle;

            sprite.getControl(RigidBodyControl.class).setPhysicRotation(a * FastMath.DEG_TO_RAD);

        } else {
            sprite.rotate(0, 0, angle * FastMath.DEG_TO_RAD);
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
    public static void slerp(Sprite sprite, float angle, float time, float delay, boolean loop) {
        int repeat = 0;
        if (loop) {
            repeat = Tween.INFINITY;
        }

        if (sprite.getControl(RigidBodyControl.class) == null) {
            Tween.to(sprite, SpatialAccessor.ROTATION_XYZ, time)
                    .target(0, 0, angle)
                    .delay(delay)
                    .repeatYoyo(repeat, delay)
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

        } else {
            Tween.to(sprite.getControl(RigidBodyControl.class), Rigidbody2DAccessor.ROTATION, time)
                    .target(angle)
                    .delay(delay)
                    .repeatYoyo(repeat, delay)
                    .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());
        }

    }

    public static void scaleBounce(Spatial sprite, float scaleX, float scaleY, float time, float delay, boolean loop) {
        int repeat = 0;
        if (loop) {
            repeat = Tween.INFINITY;
        }

        Tween.to(sprite, SpatialAccessor.SCALE_XYZ, time)
                .target(scaleX, scaleY, 1)
                .delay(delay)
                .ease(Circ.OUT)
                .repeatYoyo(repeat, delay)
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

    }

    public static void scaleBounce(Spatial sprite, float scaleX, float scaleY, float time, float delay, TweenCallback callback) {
        Tween.to(sprite, SpatialAccessor.SCALE_XYZ, time)
                .target(scaleX, scaleY, 1)
                .delay(delay)
                .ease(Bounce.INOUT)
                .setCallback(callback)
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

    }

    public static void scaleDown(Spatial sprite, float scaleX, float scaleY, float time, float delay, boolean loop) {
        int repeat = 0;
        if (loop) {
            repeat = Tween.INFINITY;
        }

        Tween.to(sprite, SpatialAccessor.SCALE_XYZ, time)
                .target(scaleX, scaleY, 1)
                .delay(delay)
                .repeatYoyo(repeat, delay)
                .start(SharedSystem.getInstance().getBaseApplication().getTweenManager());

    }

    /**
     * Add text to the scene
     *
     * @param parent
     * @param font
     * @param text
     * @param size
     * @param color
     * @return
     */
    public static BitmapText addText(Node parent, BitmapFont font, String text, float size, ColorRGBA color) {
        //Add text
        BitmapText bText = new BitmapText(font);
        bText.setText(text);
        bText.setSize(size);
        bText.setColor(color);
        bText.setLocalTranslation(0, 0, 0);
        parent.attachChild(bText);
        return bText;
    }

    public static void addCollisionShapeBasedOfGeometry(RigidBodyControl rbc, Geometry geometry) {
        int triCount = geometry.getMesh().getTriangleCount();
        Debug.log("\t Add collision shapes: -TriCount: " + triCount);
        Debug.log("\t World Bounds: " + geometry.getWorldBound());

        Vector3f vec1 = new Vector3f(0, 0, 0);
        Vector3f vec2 = new Vector3f(0, 0, 0);
        Vector3f vec3 = new Vector3f(0, 0, 0);

        for (int t = 0; t < triCount; t++) {
            geometry.getMesh().getTriangle(t, vec1, vec2, vec3);
            TriCollisionShape triCollisionShape = new TriCollisionShape(vec1.clone(), vec2.clone(), vec3.clone());
            rbc.addCollisionShape(triCollisionShape);
        }

    }

    public static float angleBetweenPoints(float pointAx, float pointAy, float pointBx, float pointBy) {
        float deltaY = pointBy - pointAy;
        float deltaX = pointBx - pointAx;
        return (float) (Math.atan2(deltaY, deltaX));
    }

    public static Vector3f directionBetweenPointsNormalized(float pointAx, float pointAy, float pointBx, float pointBy) {
        Vector3f direction = new Vector3f(pointAx, pointAy, 0).subtractLocal(pointBx,
                pointBy, 0).normalizeLocal().negate();

        return direction;
    }

    public static Vector3f directionBetweenPoints(float pointAx, float pointAy, float pointBx, float pointBy) {
        Vector3f direction = new Vector3f(pointAx, pointAy, 0).subtractLocal(pointBx,
                pointBy, 0).negate();

        return direction;
    }

    public static void updateColor(Sprite sprite, ColorRGBA color) {
        if (sprite != null) {
            SceneGraphVisitor sgv = new SceneGraphVisitor() {
                @Override
                public void visit(Spatial sp) {
                    if (sp instanceof Geometry) {
                        Geometry geom = (Geometry) sp;
                        MatParam diffuseParam = geom.getMaterial().getParam("Diffuse");

                        if (diffuseParam == null) {
                            diffuseParam = geom.getMaterial().getParam("Color");
                        }

                        if (diffuseParam != null) {
                            diffuseParam.setValue(color);
                        }

                    }
                }
            };

            sprite.depthFirstTraversal(sgv);
        }

    }

    public static void setTransparency(Sprite sprite, float alpha) {
        SpatialUtils.updateSpatialTransparency(sprite, true, alpha);

    }

    void fadeFromTo(Sprite sprite, float from, float to, float duration, float delay) {
        SpriteUtils.setTransparency(sprite, from);
        Tween.to(this, SpatialAccessor.OPACITY, duration)
                .target(to)
                .delay(delay);

    }

    public static Tween fadeFromTo(Sprite sprite, float from, float to, float duration, float delay, TweenCallback callback) {
        SpriteUtils.setTransparency(sprite, from);
        return Tween.to(sprite, SpatialAccessor.OPACITY, duration)
                .target(to)
                .delay(delay)
                .setCallback(callback);

    }

    public static Tween fadeFromTo(Sprite sprite, float from, float to, float duration, float delay, TweenEquation tweenEquation, int count, boolean yoyo) {
        SpriteUtils.setTransparency(sprite, from);

        if (yoyo) {
            return Tween.to(sprite, SpatialAccessor.OPACITY, duration)
                    .target(to)
                    .delay(delay)
                    .ease(tweenEquation)
                    .repeatYoyo(count, 0);
        } else {
            return Tween.to(sprite, SpatialAccessor.OPACITY, duration)
                    .target(to)
                    .delay(delay)
                    .ease(tweenEquation)
                    .repeat(count, 0);
        }

    }

    public static Tween fadeFromTo(Sprite sprite, float from, float to, float duration, float delay, int count, boolean yoyo, float repeatDelay) {
        SpriteUtils.setTransparency(sprite, from);

        if (yoyo) {
            return Tween.to(sprite, SpatialAccessor.OPACITY, duration)
                    .target(to)
                    .delay(delay)
                    .repeatYoyo(count, repeatDelay);
        } else {
            return Tween.to(sprite, SpatialAccessor.OPACITY, duration)
                    .target(to)
                    .delay(delay)
                    .repeat(count, repeatDelay);
        }

    }

    public static Tween moveFromToCenter(Sprite sprite, float fromX, float fromY, float toX, float toY, float duration, float delay) {
        SpriteUtils.translate(sprite, fromX, fromY, sprite.getLocalTranslation().z);

        return Tween.to(sprite, SpatialAccessor.POS_XYZ, duration)
                .target(toX, toY, sprite.getLocalTranslation().z)
                .delay(delay);
    }

    public static Tween moveFromToCenter(Sprite sprite, float fromX, float fromY, float toX, float toY, float duration, float delay, int count, boolean yoyo, float repeatDelay) {
        SpriteUtils.translate(sprite, fromX, fromY, sprite.getLocalTranslation().z);

        if (yoyo) {
            return Tween.to(sprite, SpatialAccessor.POS_XYZ, duration)
                    .target(toX, toY, sprite.getLocalTranslation().z)
                    .delay(delay)
                    .repeatYoyo(count, repeatDelay);
        } else {
            return Tween.to(sprite, SpatialAccessor.POS_XYZ, duration)
                    .target(toX, toY, sprite.getLocalTranslation().z)
                    .delay(delay)
                    .repeat(count, repeatDelay);
        }

    }

    public static Tween moveFromToCenter(Sprite sprite, float fromX, float fromY, float toX, float toY, float duration, float delay, TweenCallback callback) {
        SpriteUtils.translate(sprite, fromX, fromY, sprite.getLocalTranslation().z);

        return Tween.to(sprite, SpatialAccessor.POS_XYZ, duration)
                .target(toX, toY, sprite.getLocalTranslation().z)
                .delay(delay);

    }

    public static Tween moveFromToCenter(Sprite sprite, float fromX, float fromY, float toX, float toY, float duration, float delay, TweenEquation tweenEquation) {
        SpriteUtils.translate(sprite, fromX, fromY, sprite.getLocalTranslation().z);

        return Tween.to(sprite, SpatialAccessor.POS_XYZ, duration)
                .target(toX, toY, sprite.getLocalTranslation().z)
                .delay(delay)
                .ease(tweenEquation);

    }

    public static Tween moveFromToCenter(Sprite sprite, float fromX, float fromY, float toX, float toY, float duration, float delay, TweenEquation tweenEquation, TweenCallback callback) {
        SpriteUtils.translate(sprite, fromX, fromY, sprite.getLocalTranslation().z);

        return Tween.to(sprite, SpatialAccessor.POS_XYZ, duration)
                .target(toX, toY, sprite.getLocalTranslation().z)
                .delay(delay)
                .ease(tweenEquation)
                .setCallback(callback);

    }

    public static Tween moveFromToCenter(Sprite sprite, float fromX, float fromY, float toX, float toY, float duration, float delay, TweenEquation tweenEquation, int count, boolean yoyo, float repeatDelay) {
        SpriteUtils.translate(sprite, fromX, fromY, sprite.getLocalTranslation().z);

        if (yoyo) {
            return Tween.to(sprite, SpatialAccessor.POS_XYZ, duration)
                    .target(toX, toY, sprite.getLocalTranslation().z)
                    .delay(delay)
                    .ease(tweenEquation)
                    .repeatYoyo(count, repeatDelay);
        } else {
            return Tween.to(sprite, SpatialAccessor.POS_XYZ, duration)
                    .target(toX, toY, sprite.getLocalTranslation().z)
                    .delay(delay)
                    .ease(tweenEquation)
                    .repeat(count, repeatDelay);
        }

    }

    public static Tween moveFromToCenter(Sprite sprite, float fromX, float fromY, float toX, float toY, float duration, float delay, TweenEquation tweenEquation, int count, boolean yoyo, TweenCallback callback) {

        SpriteUtils.translate(sprite, fromX, fromY, sprite.getLocalTranslation().z);

        if (yoyo) {
            return Tween.to(sprite, SpatialAccessor.POS_XYZ, duration)
                    .target(toX, toY, sprite.getLocalTranslation().z)
                    .delay(delay)
                    .ease(tweenEquation)
                    .repeatYoyo(count, 0)
                    .setCallback(callback);
        } else {
            return Tween.to(sprite, SpatialAccessor.POS_XYZ, duration)
                    .target(toX, toY, sprite.getLocalTranslation().z)
                    .delay(delay)
                    .ease(tweenEquation)
                    .repeat(count, 0)
                    .setCallback(callback);
        }

    }

    public static Tween scaleFromTo(Sprite sprite, float fromX, float fromY, float toX, float toY, float duration, float delay) {
        sprite.setLocalScale(fromX, fromY, 1);

        return Tween.to(sprite, SpatialAccessor.SCALE_XYZ, duration)
                .target(toX, toY, 1)
                .delay(delay);

    }

    public static Tween scaleFromTo(Sprite sprite, float fromX, float fromY, float toX, float toY, float duration, float delay, int count, boolean yoyo, float repeatDelay) {
        sprite.setLocalScale(fromX, fromY, 1);

        if (yoyo) {
            return Tween.to(sprite, SpatialAccessor.SCALE_XYZ, duration)
                    .target(toX, toY, 1)
                    .delay(delay)
                    .repeatYoyo(count, repeatDelay);

        } else {
            return Tween.to(sprite, SpatialAccessor.SCALE_XYZ, duration)
                    .target(toX, toY, 1)
                    .delay(delay)
                    .repeat(count, repeatDelay);

        }

    }

    public static Tween scaleFromTo(Sprite sprite, float fromX, float fromY, float toX, float toY, float duration, float delay, TweenCallback callback) {
        sprite.setLocalScale(fromX, fromY, 1);

        return Tween.to(sprite, SpatialAccessor.SCALE_XYZ, duration)
                .target(toX, toY, 1)
                .delay(delay)
                .setCallback(callback);

    }

    public static Tween scaleFromTo(Sprite sprite, float fromX, float fromY, float toX, float toY, float duration, float delay, TweenEquation tweenEquation) {
        sprite.setLocalScale(fromX, fromY, 1);

        return Tween.to(sprite, SpatialAccessor.SCALE_XYZ, duration)
                .target(toX, toY, 1)
                .delay(delay)
                .ease(tweenEquation);
    }

    public static Tween scaleFromTo(Sprite sprite, float fromX, float fromY, float toX, float toY, float duration, float delay, TweenEquation tweenEquation, TweenCallback callback) {
        sprite.setLocalScale(fromX, fromY, 1);

        return Tween.to(sprite, SpatialAccessor.SCALE_XYZ, duration)
                .target(toX, toY, 1)
                .delay(delay)
                .ease(tweenEquation)
                .setCallback(callback);
    }

    public static Tween rotateFromTo(Sprite sprite, float fromAngle, float toAngle, float duration, float delay) {
        SpriteUtils.rotateTo(sprite, fromAngle);

        return Tween.to(sprite, SpatialAccessor.ROTATION_Z, duration)
                .target(toAngle)
                .delay(delay);

    }

    public static Tween rotateFromTo(Sprite sprite, float fromAngle, float toAngle, float duration, float delay, int count, boolean yoyo, float repeatDelay) {
        SpriteUtils.rotateTo(sprite, fromAngle);

        if (yoyo) {
            return Tween.to(sprite, SpatialAccessor.ROTATION_Z, duration)
                    .target(toAngle)
                    .delay(delay)
                    .repeatYoyo(count, repeatDelay);
        } else {
            return Tween.to(sprite, SpatialAccessor.ROTATION_Z, duration)
                    .target(toAngle)
                    .delay(delay)
                    .repeat(count, repeatDelay);
        }

    }

    public static Tween rotateFromTo(Sprite sprite, float fromAngle, float toAngle, float duration, float delay, TweenEquation tweenEquation) {
        SpriteUtils.rotateTo(sprite, fromAngle);

        return Tween.to(sprite, SpatialAccessor.ROTATION_Z, duration)
                .target(toAngle)
                .delay(delay)
                .ease(tweenEquation);
    }

    public static Tween rotateFromTo(Sprite sprite, float fromAngle, float toAngle, float duration, float delay, TweenEquation tweenEquation, TweenCallback callback) {
        SpriteUtils.rotateTo(sprite, fromAngle);

        return Tween.to(sprite, SpatialAccessor.ROTATION_Z, duration)
                .target(toAngle)
                .delay(delay)
                .ease(tweenEquation)
                .setCallback(callback);
    }

    public static Vector3f moveTowards(Vector3f start, Vector3f target, float speed) {
        Vector3f dir = target.multLocal(1, 1, 0).subtract(start.multLocal(1, 1, 0));
        dir = dir.normalizeLocal().mult(speed);
        return start.add(dir.x, dir.y, dir.z);

    }    
}
