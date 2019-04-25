package com.jme.test.character;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Line;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.DirectionalLightShadowFilter;

/**
 * This is the CharacterMoveToLocationTest Class of your Game. You should only
 * do initialization here. Move your Logic into AppStates or Controls
 *
 * @author ndebruyn
 */
public class CharacterMoveToLocationTest extends SimpleApplication implements ActionListener, AnalogListener {

    //Setup some static variables to use for input.
    private static final String LEFT_MOUSE_CLICK = "LEFT_MOUSE_CLICK";
    public static final String DRAG_LEFT_ACTION = "drag_left_picker";
    public static final String DRAG_RIGHT_ACTION = "drag_right_picker";
    public static final String DRAG_UP_ACTION = "drag_up_picker";
    public static final String DRAG_DOWN_ACTION = "drag_down_picker";

    //Setup some private variables to use in this example.
    private DirectionalLight sun;
    private AmbientLight ambientLight;
    private Vector3f sunDirection = new Vector3f(0.5f, -0.8f, -0.2f);
    private Node characterNode;
    private Spatial leftFoot;
    private Spatial rightFoot;
    private Spatial floor;
    private Spatial marker;
    private Spatial targetMarker;
    private CollisionResults results;
    private Ray ray;
    private Vector3f contactPoint;
    private FilterPostProcessor fpp;
    private float minWalkDistance = 0.1f;
    private float distanceBetweenCharacterAndTarget = 0;
    private float moveSpeed = 2.5f;
    private Quaternion targetRotation = Quaternion.IDENTITY;
    private Vector3f lineStart = new Vector3f(0, 0, 0);
    private Vector3f lineEnd = new Vector3f(0, 0, 0);
    private Line line;
    protected BitmapText distanceText;
    private float feetPosition = 0f;
    private float walkAnimationSpeed = 15f;

    /**
     * This is the main method that will load the application.
     *
     * @param args
     */
    public static void main(String[] args) {
        CharacterMoveToLocationTest app = new CharacterMoveToLocationTest();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        //In this method we will load the scene and setup all required variable.

        loadFloor();
        loadMarker();
        loadCharacter();
        loadDistanceMarker();
        loadSun();
        loadFX();
        loadCamera();
        loadMouseInput();

    }

    /**
     * Load the floor
     */
    private void loadFloor() {
        floor = addBox(rootNode, 12, 0.1f, 12, ColorRGBA.LightGray);

    }

    /**
     * Load the character.
     */
    private void loadCharacter() {
        characterNode = new Node("Character");
        rootNode.attachChild(characterNode);

        //Load the character head.        
        Spatial head = addSphere(characterNode, 20, 20, 0.5f, ColorRGBA.White);
        head.move(0, 1.5f, 0);

        //Body
        Spatial body = addSphere(characterNode, 20, 20, 0.6f, ColorRGBA.White);
        body.move(0, 0.6f, 0);

        //Load the nose
        Spatial nose = addCone(characterNode, 20, 0.1f, 0.5f, ColorRGBA.Orange);
        nose.move(0, 1.55f, 0.6f);

        //Eyes
        Spatial eyeL = addSphere(characterNode, 10, 10, 0.07f, ColorRGBA.Black);
        eyeL.move(-0.2f, 1.75f, 0.45f);

        Spatial eyeR = addSphere(characterNode, 10, 10, 0.07f, ColorRGBA.Black);
        eyeR.move(0.2f, 1.75f, 0.45f);

        //Load the legs
        leftFoot = addSphere(characterNode, 20, 20, 0.3f, ColorRGBA.White);
        leftFoot.move(-0.3f, .1f, 0.25f);

        rightFoot = addSphere(characterNode, 20, 20, 0.3f, ColorRGBA.White);
        rightFoot.move(0.3f, .1f, 0.25f);

        //Load the arms
        Spatial armL = addCylinder(characterNode, 20, 20, 0.03f, 0.6f, false, ColorRGBA.Brown);
        armL.move(-0.8f, 0.5f, 0.f);
        armL.rotate(FastMath.DEG_TO_RAD * -45, FastMath.DEG_TO_RAD * 90, 0);

        Spatial armR = addCylinder(characterNode, 20, 20, 0.03f, 0.6f, false, ColorRGBA.Brown);
        armR.move(0.8f, 0.5f, 0.f);
        armR.rotate(FastMath.DEG_TO_RAD * 45, FastMath.DEG_TO_RAD * 90, 0);

    }

    private void loadDistanceMarker() {
        this.line = addLine(rootNode, lineStart, lineEnd, ColorRGBA.Yellow, 4f);

        //Load the distance text
        distanceText = guiFont.createLabel("Distance: 0");
        distanceText.setText("Distance: 0");
        distanceText.setBox(new Rectangle(-200, 20, 200, 20));
        distanceText.setSize(20);      // font size
        distanceText.setColor(ColorRGBA.White);// font color
        distanceText.setAlignment(BitmapFont.Align.Left);
        distanceText.setVerticalAlignment(BitmapFont.VAlign.Center);
        distanceText.setLocalTranslation(210, settings.getHeight() - 15, 0);
        
        guiNode.attachChild(distanceText);
    }

    /**
     * Load the target marker.
     */
    private void loadMarker() {
        targetMarker = addSphere(rootNode, 20, 20, 0.2f, ColorRGBA.Green);
        marker = addSphere(rootNode, 20, 20, 0.15f, ColorRGBA.Blue);

    }

    /**
     * Load the sun.
     */
    private void loadSun() {
        sun = new DirectionalLight(sunDirection, ColorRGBA.White);
        rootNode.addLight(sun);

        ambientLight = new AmbientLight(ColorRGBA.DarkGray);
        rootNode.addLight(ambientLight);
    }

    /**
     * Load some special environmental fx
     */
    private void loadFX() {
        viewPort.setBackgroundColor(new ColorRGBA(10f/255f, 61f/255f, 98f/255f, 1f));
        
        fpp = new FilterPostProcessor(assetManager);
        viewPort.addProcessor(fpp);
        
        FXAAFilter fXAAFilter = new FXAAFilter();
        fpp.addFilter(fXAAFilter);
                
        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 1024, 1);
        dlsf.setShadowIntensity(0.5f);
        dlsf.setLight(sun);
        fpp.addFilter(dlsf);
    }

    /**
     * Load the camera position and direction.
     */
    private void loadCamera() {
        flyCam.setEnabled(false);
        cam.setLocation(new Vector3f(-15, 8f, 15));
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);


    }

    /**
     * Load the mouse input and map it.
     */
    public void loadMouseInput() {
        //Add a mapping for mouse left click actions
        this.inputManager.addMapping(LEFT_MOUSE_CLICK, new MouseButtonTrigger(0));
        this.inputManager.addMapping(DRAG_RIGHT_ACTION, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        this.inputManager.addMapping(DRAG_UP_ACTION, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        this.inputManager.addMapping(DRAG_LEFT_ACTION, new MouseAxisTrigger(MouseInput.AXIS_X, true));
        this.inputManager.addMapping(DRAG_DOWN_ACTION, new MouseAxisTrigger(MouseInput.AXIS_Y, true));

        //Add this class as the listener for the mapped mouse click action.
        //The onAction() method will be called.
        this.inputManager.addListener(this, LEFT_MOUSE_CLICK, DRAG_RIGHT_ACTION,
                DRAG_DOWN_ACTION, DRAG_LEFT_ACTION,
                DRAG_UP_ACTION);

        //Load the collision system and ray checking classes.
        results = new CollisionResults();
        ray = new Ray(cam.getLocation(), cam.getDirection());
    }

    @Override
    public void simpleUpdate(float tpf) {

        //Calculate the character movement
        distanceBetweenCharacterAndTarget = characterNode.getWorldTranslation().distance(targetMarker.getWorldTranslation());
        if (distanceBetweenCharacterAndTarget > minWalkDistance) {

            //Move the character towards the target
            //1. start rotating character towards target each frame
//            characterNode.lookAt(targetMarker.getLocalTranslation(), Vector3f.UNIT_Y);
            targetRotation.lookAt(targetMarker.getLocalTranslation().subtract(characterNode.getWorldTranslation()).normalize(), Vector3f.UNIT_Y);
            characterNode.getLocalRotation().slerp(targetRotation, 0.01f);

            //2. move in the direction the character is facing
            characterNode.move(characterNode.getLocalRotation().getRotationColumn(2).normalize().mult(moveSpeed * tpf));

            //3. Update the line
            line.updatePoints(characterNode.getLocalTranslation().add(0, 0.14f, 0),
                    targetMarker.getLocalTranslation().add(0, 0.14f, 0));
            
            //4. Update the distance text
            distanceText.setText("Distance: " + String.format("%.2f", distanceBetweenCharacterAndTarget));
            
            //5. Update feed position
            feetPosition = FastMath.sin(timer.getTimeInSeconds()*walkAnimationSpeed);
            leftFoot.setLocalTranslation(leftFoot.getLocalTranslation().x, leftFoot.getLocalTranslation().y, feetPosition*0.3f);
            rightFoot.setLocalTranslation(rightFoot.getLocalTranslation().x, rightFoot.getLocalTranslation().y, -feetPosition*0.3f);
            
        }

    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {

        //Check if the mapped mouse click action was pressed.
        if (name != null && LEFT_MOUSE_CLICK.equals(name)) {
            //We now know the left click was fired we now only 
            //need to check if the button was pressed or release.
            if (isPressed) {
                checkCursorCollision();
                updateMarkerPosition();
                targetMarker.setLocalTranslation(marker.getLocalTranslation().clone());

            }

        }

    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        checkCursorCollision();
        updateMarkerPosition();

    }

    /**
     * This is a private helper method which will help determine where the mouse
     * pointer collision happened.
     *
     */
    private void checkCursorCollision() {

        results.clear();

        // 1. calc direction
        Vector3f origin = cam.getWorldCoordinates(inputManager.getCursorPosition(), 0f);
        Vector3f direction = cam.getWorldCoordinates(inputManager.getCursorPosition(), 1f);
        direction.subtractLocal(origin).normalizeLocal();

        // 2. Aim the ray from cam loc to cam direction.        
        ray.setOrigin(origin);
        ray.setDirection(direction);

        // 3. Collect intersections between Ray and Shootables in results list.
        floor.collideWith(ray, results);

        // 5. Use the results (we mark the hit object)
        if (results.size() > 0) {

            CollisionResult closest = results.getClosestCollision();
            contactPoint = closest.getContactPoint();

        } else {
            contactPoint = null;

        }
    }

    private void updateMarkerPosition() {
        if (contactPoint != null) {
            marker.setLocalTranslation(contactPoint);

        }

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
    public Spatial addBox(Node parent, float xExtend, float yExtend, float zExtend, ColorRGBA colorRGBA) {

        Box box = new Box(xExtend, yExtend, zExtend);
        Geometry geometry = new Geometry("box", box);
        parent.attachChild(geometry);
        geometry.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        addColor(geometry, colorRGBA, false);

        return geometry;
    }

    /**
     * Add a sphere to the parent node.
     *
     * @param parent
     * @param zSamples
     * @param radialSamples
     * @param radius
     * @return
     */
    public Spatial addSphere(Node parent, int zSamples, int radialSamples, float radius, ColorRGBA colorRGBA) {

        Sphere sphere = new Sphere(zSamples, radialSamples, radius);
        Geometry geometry = new Geometry("sphere", sphere);
        parent.attachChild(geometry);
        geometry.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        addColor(geometry, colorRGBA, false);

        return geometry;
    }

    /**
     * Add a cone to the parent node
     *
     * @param parent
     * @param radialSamples
     * @param radius
     * @param height
     * @return
     */
    public Spatial addCone(Node parent, int radialSamples, float radius, float height, ColorRGBA colorRGBA) {

        Cylinder c = new Cylinder(2, radialSamples, 0.0001f, radius, height, true, false);
        Geometry geometry = new Geometry("cone", c);
        parent.attachChild(geometry);
        geometry.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        addColor(geometry, colorRGBA, false);

        return geometry;
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
    public Spatial addCylinder(Node parent, int axisSamples, int radialSamples, float radius, float height, boolean closed, ColorRGBA colorRGBA) {

        Cylinder cylinder = new Cylinder(axisSamples, radialSamples, radius, height, closed);
        Geometry geometry = new Geometry("cylinder", cylinder);
        parent.attachChild(geometry);
        geometry.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        addColor(geometry, colorRGBA, false);

        return geometry;
    }

    /**
     * Add a line to the scene
     *
     * @param parent
     * @param start
     * @param end
     * @param linewidth
     * @return
     */
    public Line addLine(Node parent, Vector3f start, Vector3f end, ColorRGBA color, float linewidth) {

        Line line = new Line(start, end);
        line.setLineWidth(linewidth);
        Geometry geometry = new Geometry("line", line);
        parent.attachChild(geometry);
        geometry.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        Material m = addColor(geometry, color, true);
        m.getAdditionalRenderState().setLineWidth(linewidth);

        return line;
    }

    /**
     * Add color to the spatial.
     *
     *
     * @param colorRGBA
     * @return
     */
    public Material addColor(Spatial spatial, ColorRGBA colorRGBA, boolean unshaded) {
        Material material = null;

        if (unshaded) {
            material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            material.setColor("Color", colorRGBA);

        } else {
            material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            material.setBoolean("UseMaterialColors", true);
            material.setColor("Ambient", colorRGBA);
            material.setColor("Diffuse", colorRGBA);

        }

        spatial.setMaterial(material);

        return material;
    }
}
