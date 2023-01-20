package com.galago.editor.ui.panels;

import com.bruynhuis.galago.ui.listener.KeyboardListener;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.Panel;
import com.galago.editor.ui.FloatField;
import com.galago.editor.ui.SpinnerButton;
import com.galago.editor.ui.TextField;
import com.galago.editor.utils.Action;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;

/**
 *
 * @author ndebruyn
 */
public class GeometryPropertiesPanel extends AbstractPropertiesPanel {

    private Geometry geometry;
    private Material geometryMaterial;
    private TextField nameField;
    
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
    
    private SpinnerButton shadowSpinner;

    public GeometryPropertiesPanel(Panel parent) {
        super(parent, "properties-geometry");
    }

    @Override
    protected void init() {
        
        verticalSpacing = 26;
        
        createHeader("transformations", "Geometry");
        
        nameField = createLabeledTextInput("Name", "name-field");
        nameField.addKeyboardListener(new KeyboardListener() {

            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                //System.out.println("Pressed: " + evt.getKeyChar());

                geometry.setName(nameField.getValue());

            }

        });
        
        createHeader("position", "Position");
        positionXField = createLabeledFloatInput("X", "x-pos", 70, ColorRGBA.Red);
        positionXField.addKeyboardListener(new KeyboardListener() {

            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                geometry.setLocalTranslation(positionXField.getValue(), geometry.getLocalTranslation().y, geometry.getLocalTranslation().z);
                window.getApplication().getMessageManager().sendMessage(Action.UPDATE_OBJECT, null);

            }

        });
        
        positionYField = createLabeledFloatInput("Y", "y-pos", 70, ColorRGBA.Green);
        positionYField.addKeyboardListener(new KeyboardListener() {

            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                geometry.setLocalTranslation(geometry.getLocalTranslation().x, positionYField.getValue(), geometry.getLocalTranslation().z);
                window.getApplication().getMessageManager().sendMessage(Action.UPDATE_OBJECT, null);

            }

        });
        
        positionZField = createLabeledFloatInput("Z", "z-pos", 70, ColorRGBA.Blue);
        positionZField.addKeyboardListener(new KeyboardListener() {

            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                geometry.setLocalTranslation(geometry.getLocalTranslation().x, geometry.getLocalTranslation().y, positionZField.getValue());
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
                geometry.setLocalRotation(tempQuat);
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
                geometry.setLocalScale(scaleXField.getValue(), geometry.getLocalScale().y, geometry.getLocalScale().z);
                window.getApplication().getMessageManager().sendMessage(Action.UPDATE_OBJECT, null);

            }

        });
        
        scaleYField = createLabeledFloatInput("Y", "y-scale", 70, ColorRGBA.Green);
        scaleYField.addKeyboardListener(new KeyboardListener() {

            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                geometry.setLocalScale(geometry.getLocalScale().x, scaleYField.getValue(), geometry.getLocalScale().z);
                window.getApplication().getMessageManager().sendMessage(Action.UPDATE_OBJECT, null);

            }

        });
        
        scaleZField = createLabeledFloatInput("Z", "z-scale", 70, ColorRGBA.Blue);
        scaleZField.addKeyboardListener(new KeyboardListener() {

            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                geometry.setLocalScale(geometry.getLocalScale().x, geometry.getLocalScale().y, scaleZField.getValue());
                window.getApplication().getMessageManager().sendMessage(Action.UPDATE_OBJECT, null);

            }

        });
        
        createHeader("shadows", "Shadows");
        shadowSpinner = createLabeledSpinner("Shadows", "shadows", new String[]{"Off", "Receive", "Cast", "Both"});
        shadowSpinner.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (shadowSpinner.getIndex() == 0) {
                    geometry.setShadowMode(RenderQueue.ShadowMode.Off);
                    
                } else if (shadowSpinner.getIndex() == 1) {
                    geometry.setShadowMode(RenderQueue.ShadowMode.Receive);
                    
                } else if (shadowSpinner.getIndex() == 2) {
                    geometry.setShadowMode(RenderQueue.ShadowMode.Cast);
                    
                } else if (shadowSpinner.getIndex() == 3) {
                    geometry.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                    
                }
                
                //reload();
            }
        });
        
    }

    @Override
    protected void reload() {
        
        if (geometry == null) {
            nameField.setValue("no name");
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
                        
        } else {
            nameField.setValue(geometry.getName());
            positionXField.setValue(geometry.getLocalTranslation().x);
            positionYField.setValue(geometry.getLocalTranslation().y);
            positionZField.setValue(geometry.getLocalTranslation().z);
                        
            rotationXField.setValue(geometry.getLocalRotation().toAngles(null)[0] * FastMath.RAD_TO_DEG);
            rotationYField.setValue(geometry.getLocalRotation().toAngles(null)[1] * FastMath.RAD_TO_DEG);
            rotationZField.setValue(geometry.getLocalRotation().toAngles(null)[2] * FastMath.RAD_TO_DEG);
            
            scaleXField.setValue(geometry.getLocalScale().x);
            scaleYField.setValue(geometry.getLocalScale().y);
            scaleZField.setValue(geometry.getLocalScale().z);
            
            if (geometry.getShadowMode().equals(RenderQueue.ShadowMode.Off)) {
                shadowSpinner.setSelection(0);
                
            } else if (geometry.getShadowMode().equals(RenderQueue.ShadowMode.Receive)) {
                shadowSpinner.setSelection(1);
                
            } else if (geometry.getShadowMode().equals(RenderQueue.ShadowMode.Cast)) {
                shadowSpinner.setSelection(2);
                
            } else if (geometry.getShadowMode().equals(RenderQueue.ShadowMode.CastAndReceive)) {
                shadowSpinner.setSelection(3);
                
            }
        }

    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
        this.geometryMaterial = geometry.getMaterial();
        this.reload();
    }


}
