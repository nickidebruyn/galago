/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.util;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Circ;
import com.bruynhuis.galago.app.Base2DApplication;
import com.bruynhuis.galago.control.tween.Rigidbody2DAccessor;
import com.bruynhuis.galago.control.tween.SpatialAccessor;
import com.bruynhuis.galago.sprite.Sprite;
import com.bruynhuis.galago.sprite.physics.RigidBodyControl;
import com.bruynhuis.galago.sprite.physics.shape.BoxCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CircleCollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.CollisionShape;
import com.bruynhuis.galago.sprite.physics.shape.TriCollisionShape;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * This is a spatial utility class which can be used to create or convert
 * certain spatial parameters.
 *
 * @author nidebruyn
 */
public class SpriteUtils {

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
     * @param parent
     * @param image
     * @param width
     * @param height
     * @return 
     */
    public static Sprite addSprite(Node parent, String image, float width, float height) {
        Sprite sprite = addSprite(parent, width, height);        
        sprite.setImage(image);
        sprite.getMaterial().setFloat("AlphaDiscardThreshold", 0.5f);

        return sprite;
    }
    
    /**
     * Add a sprite with an offset.
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
    public static RigidBodyControl addCircleMass(Sprite sprite, float radius, float mass) {

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
    public static void translate(Sprite sprite, float x, float y, float z) {

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
    public static void move(Sprite sprite, float xAmount, float yAmount, float zAmount) {

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
    
}
