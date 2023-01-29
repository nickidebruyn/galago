package com.galago.editor.ui.panels;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.listener.KeyboardListener;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.listener.TouchButtonListener;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.util.SharedSystem;
import com.galago.editor.ui.ButtonGroup;
import com.galago.editor.ui.ColorButton;
import com.galago.editor.ui.FloatField;
import com.galago.editor.ui.SpinnerButton;
import com.galago.editor.ui.TextField;
import com.galago.editor.utils.Action;
import com.galago.editor.utils.EditorUtils;
import com.galago.editor.utils.MaterialUtils;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.texture.Texture;
import java.awt.Color;
import javax.swing.JColorChooser;

/**
 *
 * @author ndebruyn
 */
public class GeometryPropertiesPanel extends AbstractPropertiesPanel {

    private Geometry geometry;
    private Mesh mesh;
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

//    private FloatField textureScaleXField;
//    private FloatField textureScaleYField;
    private SpinnerButton shadowSpinner;
    private SpinnerButton renderQueueSpinner;

    private SpinnerButton materialType;
    private ColorButton baseColorButton;
    private ColorButton emissiveColorButton;
    private FloatField alphaThreshholdField;
    private FloatField shininessField;
    private FloatField metallicField;
    private FloatField roughnessField;
    private FloatField emissivePowerField;
    private FloatField emissiveIntensityField;
    private ButtonGroup baseTextureButtonGroup;
    private TouchButton metalicTextureButton;

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

//        textureScaleXField = createLabeledFloatInput("X", "t-x-scale", 70, ColorRGBA.Red);
//        textureScaleXField.addKeyboardListener(new KeyboardListener() {
//
//            @Override
//            public void doKeyPressed(KeyInputEvent evt) {
//                if (textureScaleXField.getValue() == 0 || textureScaleYField.getValue() == 0) {
//                    return;
//                }
//                Vector2f textureScale = new Vector2f(textureScaleXField.getValue(), textureScaleYField.getValue());                
//                System.out.println("Texture Scale = " + textureScale);
//                geometry.setUserData(EditorUtils.TEXTURE_SCALE, textureScale);
//                geometry.setMesh(mesh.clone());
//                geometry.getMesh().scaleTextureCoordinates(textureScale);
//
//            }
//
//        });
//
//        textureScaleYField = createLabeledFloatInput("Y", "t-y-scale", 70, ColorRGBA.Green);
//        textureScaleYField.addKeyboardListener(new KeyboardListener() {
//
//            @Override
//            public void doKeyPressed(KeyInputEvent evt) {
//                if (textureScaleXField.getValue() == 0 || textureScaleYField.getValue() == 0) {
//                    return;
//                }
//                Vector2f textureScale = new Vector2f(textureScaleXField.getValue(), textureScaleYField.getValue());                
//                System.out.println("Texture Scale = " + textureScale);
//                geometry.setUserData(EditorUtils.TEXTURE_SCALE, textureScale);
//                geometry.setMesh(mesh.clone());
//                geometry.getMesh().scaleTextureCoordinates(textureScale);
//
//            }
//
//        });
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

        renderQueueSpinner = createLabeledSpinner("Rendering", "render-queue", new String[]{"Normal", "Transparent", "Translucent", "Inherit", "Sky", "Gui"});
        renderQueueSpinner.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                if (shadowSpinner.getIndex() == 0) {
                    geometry.setQueueBucket(RenderQueue.Bucket.Opaque);

                } else if (shadowSpinner.getIndex() == 1) {
                    geometry.setQueueBucket(RenderQueue.Bucket.Transparent);

                } else if (shadowSpinner.getIndex() == 2) {
                    geometry.setQueueBucket(RenderQueue.Bucket.Translucent);

                } else if (shadowSpinner.getIndex() == 3) {
                    geometry.setQueueBucket(RenderQueue.Bucket.Inherit);

                } else if (shadowSpinner.getIndex() == 4) {
                    geometry.setQueueBucket(RenderQueue.Bucket.Sky);

                } else if (shadowSpinner.getIndex() == 5) {
                    geometry.setQueueBucket(RenderQueue.Bucket.Gui);

                }

            }
        });

        createHeader("material", "Material");
        materialType = createLabeledSpinner("Type", "material-type", new String[]{"Default", "PBR", "Shadeless"});
        materialType.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                ColorRGBA color = MaterialUtils.getBaseColor(geometryMaterial);
                Texture baseTexture = MaterialUtils.getBaseTexture(geometryMaterial);
                Texture normalTexture = MaterialUtils.getNormalTexture(geometryMaterial);
                Texture metalicTexture = MaterialUtils.getMetalicTexture(geometryMaterial);
                Float shininess = geometryMaterial.getParamValue("Shininess");
                Float alphaThreshold = geometryMaterial.getParamValue("AlphaDiscardThreshold");
                Float metallic = geometryMaterial.getParamValue("Metallic");
                Float roughness = geometryMaterial.getParamValue("Roughness");
                Float emissivePower = geometryMaterial.getParamValue("EmissivePower");
                Float emissiveIntensity = geometryMaterial.getParamValue("EmissiveIntensity");
                ColorRGBA emissiveColor = MaterialUtils.getEmissiveColor(geometryMaterial);

                Material newMaterial = null;

                if (materialType.getIndex() == 0) {
                    newMaterial = MaterialUtils.createMaterial(SharedSystem.getInstance().getBaseApplication().getAssetManager(), color);
                    if (shininess != null) {
                        newMaterial.setFloat("Shininess", shininess);
                    }
                    if (alphaThreshold != null) {
                        newMaterial.setFloat("AlphaDiscardThreshold", alphaThreshold);
                    }

                } else if (materialType.getIndex() == 1) {
                    newMaterial = MaterialUtils.createPBRMaterial(SharedSystem.getInstance().getBaseApplication().getAssetManager());
                    newMaterial.setColor("BaseColor", color);
                    if (alphaThreshold != null) {
                        newMaterial.setFloat("AlphaDiscardThreshold", alphaThreshold);
                    }
                    if (metallic != null) {
                        newMaterial.setFloat("Metallic", metallic);
                    }
                    if (roughness != null) {
                        newMaterial.setFloat("Roughness", roughness);
                    }

                    if (emissiveColor != null) {
                        newMaterial.setColor("Emissive", emissiveColor);
                    }
                    
                    if (emissivePower != null) {
                        newMaterial.setFloat("EmissivePower", emissivePower);
                    }

                    if (emissiveIntensity != null) {
                        newMaterial.setFloat("EmissiveIntensity", emissiveIntensity);
                    }                    

                } else if (materialType.getIndex() == 2) {
                    newMaterial = MaterialUtils.createShadelessMaterial(SharedSystem.getInstance().getBaseApplication().getAssetManager(), color);
                    if (alphaThreshold != null) {
                        newMaterial.setFloat("AlphaDiscardThreshold", alphaThreshold);
                    }

                }

                //This section will try to set previous values on the new material
                if (newMaterial != null) {
                    MaterialUtils.setBaseTexture(newMaterial, baseTexture);
                    MaterialUtils.setNormalTexture(newMaterial, normalTexture);
                    MaterialUtils.setMetalicTexture(newMaterial, metalicTexture);

                    newMaterial.getAdditionalRenderState().setBlendMode(geometryMaterial.getAdditionalRenderState().getBlendMode());
                    newMaterial.getAdditionalRenderState().setFaceCullMode(geometryMaterial.getAdditionalRenderState().getFaceCullMode());

                    geometry.setMaterial(newMaterial);
                    setGeometry(geometry);

                }

            }
        });

        baseTextureButtonGroup = createLabeledTextureButtonGroup("Textures", EditorUtils.BASE_TEXTURE, EditorUtils.NORMAL_TEXTURE);
        metalicTextureButton = createLabeledTextureButton("Metalic", EditorUtils.METALIC_TEXTURE);

        baseColorButton = createLabeledColorButton("Color", "base-color-button");
        baseColorButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                Color currentColor = MaterialUtils.convertColor(MaterialUtils.getBaseColor(geometryMaterial));
                Color newColor = JColorChooser.showDialog(null, "Choose a color", currentColor);

                if (newColor != null) {
                    ColorRGBA colorRGBA = MaterialUtils.convertColor(newColor);

                    System.out.println("New Color: " + colorRGBA);
                    MaterialUtils.setBaseColor(geometryMaterial, colorRGBA);
                    baseColorButton.setColor(colorRGBA);
                }

            }

        });

        alphaThreshholdField = createLabeledFloatInput("Alpha", "alpha threshold", 0, 1);
        alphaThreshholdField.addKeyboardListener(new KeyboardListener() {
            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                geometryMaterial.setFloat("AlphaDiscardThreshold", alphaThreshholdField.getValue());

            }

        });

        shininessField = createLabeledFloatInput("Shininess", "shininess", 0, 5);
        shininessField.addKeyboardListener(new KeyboardListener() {
            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                geometryMaterial.setFloat("Shininess", shininessField.getValue());

            }

        });

        metallicField = createLabeledFloatInput("Metallic", "metallic", 0, 2);
        metallicField.addKeyboardListener(new KeyboardListener() {
            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                geometryMaterial.setFloat("Metallic", metallicField.getValue());

            }

        });

        roughnessField = createLabeledFloatInput("Roughness", "Roughness", 0, 1);
        roughnessField.addKeyboardListener(new KeyboardListener() {
            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                geometryMaterial.setFloat("Roughness", roughnessField.getValue());

            }

        });

        emissiveColorButton = createLabeledColorButton("Emissive", "emissive-color-button");
        emissiveColorButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                Color currentColor = MaterialUtils.convertColor(MaterialUtils.getEmissiveColor(geometryMaterial));
                Color newColor = JColorChooser.showDialog(null, "Choose a color", currentColor);

                if (newColor != null) {
                    ColorRGBA colorRGBA = MaterialUtils.convertColor(newColor);

                    System.out.println("New Color: " + colorRGBA);
                    MaterialUtils.setEmissiveColor(geometryMaterial, colorRGBA);
                    emissiveColorButton.setColor(colorRGBA);
                }

            }

        });

        emissivePowerField = createLabeledFloatInput("Power", "EmissivePower", 0, 1);
        emissivePowerField.addKeyboardListener(new KeyboardListener() {
            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                geometryMaterial.setFloat("EmissivePower", emissivePowerField.getValue());

            }

        });
        
        emissiveIntensityField = createLabeledFloatInput("Intensity", "EmissiveIntensity", 0, 1);
        emissiveIntensityField.addKeyboardListener(new KeyboardListener() {
            @Override
            public void doKeyPressed(KeyInputEvent evt) {
                geometryMaterial.setFloat("EmissiveIntensity", emissiveIntensityField.getValue());

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

            renderQueueSpinner.setSelection(0);

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

            //Shadows settings
            if (geometry.getShadowMode().equals(RenderQueue.ShadowMode.Off)) {
                shadowSpinner.setSelection(0);

            } else if (geometry.getShadowMode().equals(RenderQueue.ShadowMode.Receive)) {
                shadowSpinner.setSelection(1);

            } else if (geometry.getShadowMode().equals(RenderQueue.ShadowMode.Cast)) {
                shadowSpinner.setSelection(2);

            } else if (geometry.getShadowMode().equals(RenderQueue.ShadowMode.CastAndReceive)) {
                shadowSpinner.setSelection(3);

            }

            //Render queue settings
            if (geometry.getQueueBucket().equals(RenderQueue.Bucket.Opaque)) {
                renderQueueSpinner.setSelection(0);

            } else if (geometry.getQueueBucket().equals(RenderQueue.Bucket.Transparent)) {
                renderQueueSpinner.setSelection(1);

            } else if (geometry.getQueueBucket().equals(RenderQueue.Bucket.Translucent)) {
                renderQueueSpinner.setSelection(2);

            } else if (geometry.getQueueBucket().equals(RenderQueue.Bucket.Inherit)) {
                renderQueueSpinner.setSelection(3);

            } else if (geometry.getQueueBucket().equals(RenderQueue.Bucket.Sky)) {
                renderQueueSpinner.setSelection(4);

            } else if (geometry.getQueueBucket().equals(RenderQueue.Bucket.Gui)) {
                renderQueueSpinner.setSelection(5);

            }

            //Setup the material type
            if (MaterialUtils.isLightingMaterial(geometryMaterial)) {
                materialType.setSelection(0);

            } else if (MaterialUtils.isPBRLightingMaterial(geometryMaterial)) {
                materialType.setSelection(1);

            } else if (MaterialUtils.isUnshadedMaterial(geometryMaterial)) {
                materialType.setSelection(2);

            }

//            //Texture scale
//            Vector2f textureScale = geometry.getUserData(EditorUtils.TEXTURE_SCALE);
//            if (textureScale == null) {
//                textureScale = new Vector2f(1, 1);
//            }
//            geometry.setUserData(EditorUtils.TEXTURE_SCALE, textureScale);
//            geometry.getMesh().scaleTextureCoordinates(textureScale);
//            textureScaleXField.setValue(textureScale.x);
//            textureScaleYField.setValue(textureScale.y);
            baseColorButton.setColor(MaterialUtils.getBaseColor(geometryMaterial));

            flowPanel.remove(metalicTextureButton.getParent());
            flowPanel.remove(alphaThreshholdField.getParent());
            flowPanel.remove(shininessField.getParent());
            flowPanel.remove(metallicField.getParent());
            flowPanel.remove(roughnessField.getParent());
            flowPanel.remove(emissiveColorButton.getParent());
            flowPanel.remove(emissivePowerField.getParent());
            flowPanel.remove(emissiveIntensityField.getParent());

            if (MaterialUtils.isLightingMaterial(geometryMaterial)) {
                flowPanel.add(alphaThreshholdField.getParent());
                flowPanel.add(shininessField.getParent());

                baseTextureButtonGroup.getButton2().setVisible(true);
                baseTextureButtonGroup.getBackImage2().setVisible(true);

                setButtonTextureFromMaterial(geometryMaterial, "DiffuseMap", baseTextureButtonGroup.getButton1());
                setButtonTextureFromMaterial(geometryMaterial, "NormalMap", baseTextureButtonGroup.getButton2());

                alphaThreshholdField.setValue(geometryMaterial.getParamValue("AlphaDiscardThreshold") != null ? geometryMaterial.getParamValue("AlphaDiscardThreshold") : 0);
                shininessField.setValue(geometryMaterial.getParamValue("Shininess"));

            } else if (MaterialUtils.isPBRLightingMaterial(geometryMaterial)) {
                flowPanel.add(alphaThreshholdField.getParent());
                flowPanel.add(metalicTextureButton.getParent());
                flowPanel.add(metallicField.getParent());
                flowPanel.add(roughnessField.getParent());
                flowPanel.add(emissiveColorButton.getParent());
                flowPanel.add(emissivePowerField.getParent());
                flowPanel.add(emissiveIntensityField.getParent());

                baseTextureButtonGroup.getButton2().setVisible(true);
                baseTextureButtonGroup.getBackImage2().setVisible(true);

                setButtonTextureFromMaterial(geometryMaterial, "BaseColorMap", baseTextureButtonGroup.getButton1());
                setButtonTextureFromMaterial(geometryMaterial, "NormalMap", baseTextureButtonGroup.getButton2());
                setButtonTextureFromMaterial(geometryMaterial, "MetallicMap", metalicTextureButton);

                alphaThreshholdField.setValue(geometryMaterial.getParamValue("AlphaDiscardThreshold") != null ? geometryMaterial.getParamValue("AlphaDiscardThreshold") : 0);
                metallicField.setValue(geometryMaterial.getParamValue("Metallic") != null ? geometryMaterial.getParamValue("Metallic") : 0);
                roughnessField.setValue(geometryMaterial.getParamValue("Roughness") != null ? geometryMaterial.getParamValue("Roughness") : 0);
                
                emissivePowerField.setValue(geometryMaterial.getParamValue("EmissivePower") != null ? geometryMaterial.getParamValue("EmissivePower") : 0);
                emissiveIntensityField.setValue(geometryMaterial.getParamValue("EmissiveIntensity") != null ? geometryMaterial.getParamValue("EmissiveIntensity") : 0);

                emissiveColorButton.setColor(MaterialUtils.getEmissiveColor(geometryMaterial));

            } else {
                flowPanel.add(alphaThreshholdField.getParent());

                baseTextureButtonGroup.getButton2().setVisible(false);
                baseTextureButtonGroup.getBackImage2().setVisible(false);

                setButtonTextureFromMaterial(geometryMaterial, "ColorMap", baseTextureButtonGroup.getButton1());

                alphaThreshholdField.setValue(geometryMaterial.getParamValue("AlphaDiscardThreshold") != null ? geometryMaterial.getParamValue("AlphaDiscardThreshold") : 0);

            }
        }

        flowPanel.layout();

    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
        this.mesh = geometry.getMesh().clone();
        this.geometryMaterial = geometry.getMaterial();
        this.reload();
    }

    public void addTextureButtonListener(TouchButtonListener buttonListener) {
        this.baseTextureButtonGroup.getButton1().addTouchButtonListener(buttonListener);
        this.baseTextureButtonGroup.getButton2().addTouchButtonListener(buttonListener);
        this.metalicTextureButton.addTouchButtonListener(buttonListener);

    }

    public void setBaseTexture(Texture texture) {
        baseTextureButtonGroup.getButton1().getPicture().getMaterial().setTexture("Texture", texture);
        texture.setWrap(Texture.WrapMode.Repeat);
        MaterialUtils.setBaseTexture(geometryMaterial, texture);

    }

    public void setNormalTexture(Texture texture) {
        baseTextureButtonGroup.getButton2().getPicture().getMaterial().setTexture("Texture", texture);
        //texture.setWrap(Texture.WrapMode.Repeat);
        MaterialUtils.setNormalTexture(geometryMaterial, texture);

    }

    public void setMetalicTexture(Texture texture) {
        metalicTextureButton.getPicture().getMaterial().setTexture("Texture", texture);
        texture.setWrap(Texture.WrapMode.Repeat);
        MaterialUtils.setMetalicTexture(geometryMaterial, texture);

    }
}
