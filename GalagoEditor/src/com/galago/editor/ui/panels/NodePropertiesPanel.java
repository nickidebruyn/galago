package com.galago.editor.ui.panels;

import com.bruynhuis.galago.ui.listener.KeyboardListener;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.Panel;
import com.galago.editor.ui.FloatField;
import com.galago.editor.ui.SpinnerButton;
import com.galago.editor.ui.TextField;
import com.galago.editor.utils.Action;
import com.galago.editor.utils.EditorUtils;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;

/**
 *
 * @author ndebruyn
 */
public class NodePropertiesPanel extends AbstractPropertiesPanel {

    private Node node;

    private TextField nameField;
    private TextField groupField;

    private Quaternion tempQuat = new Quaternion();

    private FloatField positionXField;
    private FloatField positionYField;
    private FloatField positionZField;

    private FloatField rotationXField;
    private FloatField rotationYField;
    private FloatField rotationZField;

    private FloatField scaleXField;
    private FloatField scaleYField;
    private FloatField scaleZField;

//    private FloatField textureScaleXField;
//    private FloatField textureScaleYField;
    private SpinnerButton shadowSpinner;
    private SpinnerButton renderQueueSpinner;

    public NodePropertiesPanel(Panel parent) {
        super(parent, "properties-node");
    }

    @Override
    protected void init() {
        verticalSpacing = 26;

        createHeader("transformations", "Node");

        nameField = createLabeledTextInput("Name", "name-field");
        nameField.addKeyboardListener(new KeyboardListener() {

            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                //System.out.println("Pressed: " + evt.getKeyChar());

                node.setName(nameField.getValue());

            }

        });
        
        groupField = createLabeledTextInput("Group", "group-field");
        groupField.addKeyboardListener(new KeyboardListener() {

            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                node.setUserData(EditorUtils.GROUP, groupField.getValue());

            }

        });        

        createHeader("position", "Position");
        positionXField = createLabeledFloatInput("X", "x-pos", 70, ColorRGBA.Red);
        positionXField.addKeyboardListener(new KeyboardListener() {

            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                node.setLocalTranslation(positionXField.getValue(), node.getLocalTranslation().y, node.getLocalTranslation().z);
                window.getApplication().getMessageManager().sendMessage(Action.UPDATE_OBJECT, null);

            }

        });

        positionYField = createLabeledFloatInput("Y", "y-pos", 70, ColorRGBA.Green);
        positionYField.addKeyboardListener(new KeyboardListener() {

            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                node.setLocalTranslation(node.getLocalTranslation().x, positionYField.getValue(), node.getLocalTranslation().z);
                window.getApplication().getMessageManager().sendMessage(Action.UPDATE_OBJECT, null);

            }

        });

        positionZField = createLabeledFloatInput("Z", "z-pos", 70, ColorRGBA.Blue);
        positionZField.addKeyboardListener(new KeyboardListener() {

            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                node.setLocalTranslation(node.getLocalTranslation().x, node.getLocalTranslation().y, positionZField.getValue());
                window.getApplication().getMessageManager().sendMessage(Action.UPDATE_OBJECT, null);

            }

        });

        createHeader("rotation", "Rotation");
        rotationXField = createLabeledFloatInput("X", "x-rot", 70, ColorRGBA.Red);
        rotationYField = createLabeledFloatInput("Y", "y-rot", 70, ColorRGBA.Green);
        rotationZField = createLabeledFloatInput("Z", "z-rot", 70, ColorRGBA.Blue);

        KeyboardListener rotationChange = new KeyboardListener() {

            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                tempQuat = new Quaternion(new float[]{rotationXField.getValue() * FastMath.DEG_TO_RAD, rotationYField.getValue() * FastMath.DEG_TO_RAD, rotationZField.getValue() * FastMath.DEG_TO_RAD});
                node.setLocalRotation(tempQuat);
                window.getApplication().getMessageManager().sendMessage(Action.UPDATE_OBJECT, null);

            }

        };

        rotationXField.addKeyboardListener(rotationChange);
        rotationYField.addKeyboardListener(rotationChange);
        rotationZField.addKeyboardListener(rotationChange);

        createHeader("scale", "Scale");
        scaleXField = createLabeledFloatInput("X", "x-scale", 70, ColorRGBA.Red);
        scaleXField.addKeyboardListener(new KeyboardListener() {

            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                node.setLocalScale(scaleXField.getValue(), node.getLocalScale().y, node.getLocalScale().z);
                window.getApplication().getMessageManager().sendMessage(Action.UPDATE_OBJECT, null);

            }

        });

        scaleYField = createLabeledFloatInput("Y", "y-scale", 70, ColorRGBA.Green);
        scaleYField.addKeyboardListener(new KeyboardListener() {

            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                node.setLocalScale(node.getLocalScale().x, scaleYField.getValue(), node.getLocalScale().z);
                window.getApplication().getMessageManager().sendMessage(Action.UPDATE_OBJECT, null);

            }

        });

        scaleZField = createLabeledFloatInput("Z", "z-scale", 70, ColorRGBA.Blue);
        scaleZField.addKeyboardListener(new KeyboardListener() {

            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                node.setLocalScale(node.getLocalScale().x, node.getLocalScale().y, scaleZField.getValue());
                window.getApplication().getMessageManager().sendMessage(Action.UPDATE_OBJECT, null);

            }

        });

        createHeader("shadows", "Shadows");
        shadowSpinner = createLabeledSpinner("Shadows", "shadows", new String[]{"Off", "Receive", "Cast", "Both"});
        shadowSpinner.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (shadowSpinner.getIndex() == 0) {
                    node.setShadowMode(RenderQueue.ShadowMode.Off);

                } else if (shadowSpinner.getIndex() == 1) {
                    node.setShadowMode(RenderQueue.ShadowMode.Receive);

                } else if (shadowSpinner.getIndex() == 2) {
                    node.setShadowMode(RenderQueue.ShadowMode.Cast);

                } else if (shadowSpinner.getIndex() == 3) {
                    node.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

                }

                //reload();
            }
        });

        renderQueueSpinner = createLabeledSpinner("Rendering", "render-queue", new String[]{"Normal", "Transparent", "Translucent", "Inherit", "Sky", "Gui"});
        renderQueueSpinner.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (shadowSpinner.getIndex() == 0) {
                    node.setQueueBucket(RenderQueue.Bucket.Opaque);

                } else if (shadowSpinner.getIndex() == 1) {
                    node.setQueueBucket(RenderQueue.Bucket.Transparent);

                } else if (shadowSpinner.getIndex() == 2) {
                    node.setQueueBucket(RenderQueue.Bucket.Translucent);

                } else if (shadowSpinner.getIndex() == 3) {
                    node.setQueueBucket(RenderQueue.Bucket.Inherit);

                } else if (shadowSpinner.getIndex() == 4) {
                    node.setQueueBucket(RenderQueue.Bucket.Sky);

                } else if (shadowSpinner.getIndex() == 5) {
                    node.setQueueBucket(RenderQueue.Bucket.Gui);

                }

                //reload();
            }
        });

    }

    @Override
    protected void reload() {

        if (node == null) {
            nameField.setValue("");
            groupField.setValue("");
            
            positionXField.setValue(0);
            positionYField.setValue(0);
            positionZField.setValue(0);

            rotationXField.setValue(0);
            rotationYField.setValue(0);
            rotationZField.setValue(0);

            scaleXField.setValue(0);
            scaleYField.setValue(0);
            scaleZField.setValue(0);

            shadowSpinner.setSelection(0);

            renderQueueSpinner.setSelection(0);

        } else {
            nameField.setValue(node.getName());
            
            String group = node.getUserData(EditorUtils.GROUP);
            if (group == null) group = "";            
            groupField.setValue(group);
            
            positionXField.setValue(node.getLocalTranslation().x);
            positionYField.setValue(node.getLocalTranslation().y);
            positionZField.setValue(node.getLocalTranslation().z);

            rotationXField.setValue(node.getLocalRotation().toAngles(null)[0] * FastMath.RAD_TO_DEG);
            rotationYField.setValue(node.getLocalRotation().toAngles(null)[1] * FastMath.RAD_TO_DEG);
            rotationZField.setValue(node.getLocalRotation().toAngles(null)[2] * FastMath.RAD_TO_DEG);

            scaleXField.setValue(node.getLocalScale().x);
            scaleYField.setValue(node.getLocalScale().y);
            scaleZField.setValue(node.getLocalScale().z);

            //Shadows settings
            if (node.getShadowMode().equals(RenderQueue.ShadowMode.Off)) {
                shadowSpinner.setSelection(0);

            } else if (node.getShadowMode().equals(RenderQueue.ShadowMode.Receive)) {
                shadowSpinner.setSelection(1);

            } else if (node.getShadowMode().equals(RenderQueue.ShadowMode.Cast)) {
                shadowSpinner.setSelection(2);

            } else if (node.getShadowMode().equals(RenderQueue.ShadowMode.CastAndReceive)) {
                shadowSpinner.setSelection(3);

            }

            //Render queue settings
            if (node.getQueueBucket().equals(RenderQueue.Bucket.Opaque)) {
                renderQueueSpinner.setSelection(0);

            } else if (node.getQueueBucket().equals(RenderQueue.Bucket.Transparent)) {
                renderQueueSpinner.setSelection(1);

            } else if (node.getQueueBucket().equals(RenderQueue.Bucket.Translucent)) {
                renderQueueSpinner.setSelection(2);

            } else if (node.getQueueBucket().equals(RenderQueue.Bucket.Inherit)) {
                renderQueueSpinner.setSelection(3);

            } else if (node.getQueueBucket().equals(RenderQueue.Bucket.Sky)) {
                renderQueueSpinner.setSelection(4);

            } else if (node.getQueueBucket().equals(RenderQueue.Bucket.Gui)) {
                renderQueueSpinner.setSelection(5);

            }

        }
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

}
