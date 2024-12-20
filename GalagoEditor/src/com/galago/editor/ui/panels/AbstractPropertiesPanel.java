package com.galago.editor.ui.panels;

import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.panel.VFlowPanel;
import com.galago.editor.ui.ButtonGroup;
import com.galago.editor.ui.ColorButton;
import com.galago.editor.ui.FloatField;
import com.galago.editor.ui.LongField;
import com.galago.editor.ui.SliderField;
import com.galago.editor.ui.SpinnerButton;
import com.galago.editor.ui.TextField;
import com.galago.editor.utils.EditorUtils;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;

/**
 *
 * @author ndebruyn
 */
public abstract class AbstractPropertiesPanel extends Panel {

    protected VFlowPanel flowPanel;
    protected TouchButton header;
    protected TouchButton hoverButton;
    protected float verticalSpacing = 32;

    public AbstractPropertiesPanel(Panel parent, String name) {
        super(parent, "Interface/panel-left.png", EditorUtils.HIERARCHYBAR_WIDTH, parent.getWindow().getHeight());

        setBackgroundColor(EditorUtils.theme.getPanelColor());

        flowPanel = new VFlowPanel(this, null, EditorUtils.HIERARCHYBAR_WIDTH, parent.getWindow().getHeight());
        this.add(flowPanel);
        flowPanel.centerTop(0, 0);

        init();

        flowPanel.layout();

//        hoverButton = new TouchButton(this, name + "-hover", "Interface/blank.png", EditorUtils.HIERARCHYBAR_WIDTH, parent.getWindow().getHeight());
//        hoverButton.centerTop(0, 0);
//        hoverButton.setTransparency(0.1f);
//        hoverButton.addTouchButtonListener(new TouchButtonAdapter() {
//            @Override
//            public void doHoverOff(float touchX, float touchY, float tpf, String uid) {
//                window.getApplication().getMessageManager().sendMessage(Action.UI_OFF, null);
//            }
//
//            @Override
//            public void doHoverOver(float touchX, float touchY, float tpf, String uid) {
//                window.getApplication().getMessageManager().sendMessage(Action.UI_OVER, null);
//            }
//
//        });
        parent.add(this);

    }

    protected abstract void init();

    protected abstract void reload();

    protected TouchButton createHeader(String uid, String text) {
        TouchButton header = new TouchButton(flowPanel, uid, "Interface/hierarchy-header.png", EditorUtils.HIERARCHYBAR_WIDTH - 4, verticalSpacing);
        header.setText(text);
        header.setTextColor(EditorUtils.theme.getHeaderTextColor());
        header.setBackgroundColor(EditorUtils.theme.getHeaderColor());
        header.setTextAlignment(TextAlign.LEFT);
        header.setFontSize(16);
        header.addEffect(new TouchEffect(header));
        return header;
    }

    protected ButtonGroup createLabledButtonGroup(String labelText, String uid1, String uid2, String text1, String text2) {
        Panel panel = new Panel(flowPanel, null, EditorUtils.HIERARCHYBAR_WIDTH - 6, verticalSpacing);
        flowPanel.add(panel);

        Label label = new Label(panel, labelText, 12, EditorUtils.HIERARCHYBAR_WIDTH * 0.5f, verticalSpacing/1.5f);
        label.setAlignment(TextAlign.LEFT);
        label.setVerticalAlignment(TextAlign.CENTER);
        label.leftTop(5, 5);

        float scale = 0.5f;
        TouchButton button1 = new TouchButton(panel, uid1, "Interface/texture-button.png", 150 * scale, 48 * scale);
        button1.setBackgroundColor(EditorUtils.theme.getButtonColor());
        button1.setText(text1);
        button1.setFontSize(12);
        button1.setTextColor(EditorUtils.theme.getButtonTextColor());
        button1.rightTop(86, 0);
        button1.addEffect(new TouchEffect(button1));

        TouchButton button2 = new TouchButton(panel, uid2, "Interface/texture-button.png", 150 * scale, 48 * scale);
        button2.setBackgroundColor(EditorUtils.theme.getButtonColor());
        button2.setText(text2);
        button2.setFontSize(12);
        button2.setTextColor(EditorUtils.theme.getButtonTextColor());        
        button2.rightTop(6, 0);
        button2.addEffect(new TouchEffect(button2));

        return new ButtonGroup(button1, button2, null, null, null, null);
    }

    protected SpinnerButton createLabeledSpinner(String text, String id, String[] ops) {
        System.out.println("Setting options: " + ops);
        Panel p = new Panel(flowPanel, null, EditorUtils.HIERARCHYBAR_WIDTH - 6, verticalSpacing);
        flowPanel.add(p);

        Label label = new Label(p, text, 12, EditorUtils.HIERARCHYBAR_WIDTH * 0.5f, verticalSpacing / 1.5f);
        label.setAlignment(TextAlign.LEFT);
        label.setVerticalAlignment(TextAlign.CENTER);
        label.leftCenter(5, 0);

        SpinnerButton spinner = new SpinnerButton(p, id, ops);
        spinner.rightCenter(5, 0);

        return spinner;
    }

    protected ColorButton createLabeledColorButton(String text, String id) {
        Panel p = new Panel(flowPanel, null, EditorUtils.HIERARCHYBAR_WIDTH - 6, verticalSpacing);
        flowPanel.add(p);

        Label label = new Label(p, text, 12, EditorUtils.HIERARCHYBAR_WIDTH * 0.5f, verticalSpacing / 1.5f);
        label.setAlignment(TextAlign.LEFT);
        label.setVerticalAlignment(TextAlign.CENTER);
        label.leftCenter(5, 0);

        ColorButton button = new ColorButton(p, id);
        button.rightCenter(5, 0);

        return button;
    }

    protected SliderField createLabeledSlider(String text, int min, int max, int increment) {
        Panel panel = new Panel(flowPanel, null, EditorUtils.HIERARCHYBAR_WIDTH - 6, verticalSpacing);
        flowPanel.add(panel);

        Label label = new Label(panel, text, 12, EditorUtils.HIERARCHYBAR_WIDTH * 0.5f, verticalSpacing / 1.5f);
        label.setAlignment(TextAlign.LEFT);
        label.setVerticalAlignment(TextAlign.CENTER);
        label.leftCenter(5, 0);

        SliderField field = new SliderField(panel, min, max, increment);
        field.rightCenter(5, 0);

        return field;
    }

    protected SliderField createLabeledSliderDecimal(String text, float min, float max, float increment) {
        Panel panel = new Panel(flowPanel, null, EditorUtils.HIERARCHYBAR_WIDTH - 6, verticalSpacing);
        flowPanel.add(panel);

        Label label = new Label(panel, text, 12, EditorUtils.HIERARCHYBAR_WIDTH * 0.5f, verticalSpacing / 1.5f);
        label.setAlignment(TextAlign.LEFT);
        label.setVerticalAlignment(TextAlign.CENTER);
        label.leftCenter(5, 0);

        SliderField field = new SliderField(panel, min, max, increment);
        field.setDecimals(true);
        field.rightCenter(5, 0);

        return field;
    }

    protected LongField createLabeledLongInput(String text, String id) {
        Panel panel = new Panel(flowPanel, null, EditorUtils.HIERARCHYBAR_WIDTH - 6, verticalSpacing);
        flowPanel.add(panel);

        Label label = new Label(panel, text, 12, EditorUtils.HIERARCHYBAR_WIDTH * 0.5f, verticalSpacing / 1.5f);
        label.setAlignment(TextAlign.LEFT);
        label.setVerticalAlignment(TextAlign.CENTER);
        label.leftCenter(5, 0);

        LongField field = new LongField(panel, id);
        field.rightCenter(5, 0);

        return field;
    }

    protected FloatField createLabeledFloatInput(String text, String id, float labelLeftPadding, ColorRGBA textColor) {
        return createLabeledFloatInput(text, id, labelLeftPadding, textColor, -1000000, 1000000);
    }

    protected FloatField createLabeledFloatInput(String text, String id, float labelLeftPadding, ColorRGBA textColor, float minAmount, float maxAmount) {
        Panel panel = new Panel(flowPanel, null, EditorUtils.HIERARCHYBAR_WIDTH - 6, verticalSpacing);
        flowPanel.add(panel);

        Label label = new Label(panel, text, 12, EditorUtils.HIERARCHYBAR_WIDTH * 0.5f, verticalSpacing / 1.5f);
        label.setAlignment(TextAlign.LEFT);
        label.setVerticalAlignment(TextAlign.CENTER);
        label.leftCenter(labelLeftPadding, 0);
        label.setTextColor(textColor);

        FloatField field = new FloatField(panel, id);
        field.setMinAmount(minAmount);
        field.setMaxAmount(maxAmount);
        field.rightCenter(5, 0);

        return field;
    }

    protected FloatField createLabeledFloatInput(String text, String id, float minAmount, float maxAmount) {
        return createLabeledFloatInput(text, id, 5, ColorRGBA.White, minAmount, maxAmount);
    }

    protected FloatField createLabeledFloatInput(String text, String id) {
        return createLabeledFloatInput(text, id, 5, ColorRGBA.White);
    }

    protected TextField createLabeledTextInput(String text, String id) {
        Panel panel = new Panel(flowPanel, null, EditorUtils.HIERARCHYBAR_WIDTH - 6, verticalSpacing);
        flowPanel.add(panel);

        Label label = new Label(panel, text, 12, EditorUtils.HIERARCHYBAR_WIDTH * 0.5f, verticalSpacing / 1.5f);
        label.setAlignment(TextAlign.LEFT);
        label.setVerticalAlignment(TextAlign.CENTER);
        label.leftCenter(5, 0);

        TextField field = new TextField(panel, id);
        field.rightCenter(5, 0);

        return field;
    }

    protected TouchButton createLabeledTextureButton(String text, String id) {
        Panel panel = new Panel(flowPanel, null, EditorUtils.HIERARCHYBAR_WIDTH - 6, 80);
        flowPanel.add(panel);

        Label label = new Label(panel, text, 12, EditorUtils.HIERARCHYBAR_WIDTH * 0.5f, 60);
        label.setAlignment(TextAlign.LEFT);
        label.setVerticalAlignment(TextAlign.CENTER);
        label.leftTop(5, 5);

        Image img = new Image(panel, "Interface/texture-button.png", 70, 70);
        img.setBackgroundColor(EditorUtils.theme.getButtonColor());
        img.rightTop(0, 10);

        TouchButton button = new TouchButton(panel, id, "Interface/texture-button.png", 60, 60);
        button.rightTop(5, 15);
        button.addEffect(new TouchEffect(button));
        button.getPicture().setMaterial(button.getPicture().getMaterial().clone());

        return button;
    }

    protected TouchButton createLabeledTextureButton(Panel parentPanel, String text, String id, Texture texture) {
        Panel panel = new Panel(parentPanel, null, 80, 80);
        parentPanel.add(panel);

        Label label = new Label(panel, text, 12, 80, 60);
        label.setAlignment(TextAlign.CENTER);
        label.setVerticalAlignment(TextAlign.CENTER);
        label.centerTop(0, -20);

        Image img = new Image(panel, "Interface/texture-button.png", 70, 70);
        img.setBackgroundColor(EditorUtils.theme.getButtonColor());
        img.center();

        TouchButton button = new TouchButton(panel, id, "Interface/texture-button.png", 60, 60);
        button.center();
        button.addEffect(new TouchEffect(button));
        button.getPicture().setMaterial(button.getPicture().getMaterial().clone());

        if (texture != null) {
            button.getPicture().getMaterial().setTexture("Texture", texture);
        }

        return button;
    }

    protected ButtonGroup createLabeledTextureButtonGroup(String text, String idB1, String idB2) {
        Panel panel = new Panel(flowPanel, null, EditorUtils.HIERARCHYBAR_WIDTH - 6, 80);
        flowPanel.add(panel);

        Label label = new Label(panel, text, 12, EditorUtils.HIERARCHYBAR_WIDTH * 0.5f, 60);
        label.setAlignment(TextAlign.LEFT);
        label.setVerticalAlignment(TextAlign.CENTER);
        label.leftTop(5, 5);

        Image img = new Image(panel, "Interface/texture-button.png", 64, 64);
        img.setBackgroundColor(EditorUtils.theme.getButtonColor());
        img.rightTop(14, 13);

        Image img2 = new Image(panel, "Interface/texture-button.png", 64, 64);
        img2.setBackgroundColor(EditorUtils.theme.getButtonColor());
        img2.rightTop(94, 13);

        TouchButton button1 = new TouchButton(panel, idB1, "Interface/texture-button.png", 56, 56);
        button1.rightTop(98, 17);
        button1.addEffect(new TouchEffect(button1));
        button1.getPicture().setMaterial(button1.getPicture().getMaterial().clone());

        TouchButton button2 = new TouchButton(panel, idB2, "Interface/texture-button.png", 56, 56);
        button2.rightTop(18, 17);
        button2.addEffect(new TouchEffect(button2));
        button2.getPicture().setMaterial(button2.getPicture().getMaterial().clone());

        TouchButton leftButton = new TouchButton(panel, "left-remove-button-" + idB1, "Interface/cross.png", 18, 18);
        leftButton.rightTop(79, 14);
        leftButton.addEffect(new TouchEffect(leftButton));
        
        TouchButton leftHFlipButton = new TouchButton(panel, "left-h-flip-button-" + idB1, "Interface/scrollHorizontal.png", 20, 20);
        leftHFlipButton.rightTop(79, 34);
        leftHFlipButton.addEffect(new TouchEffect(leftHFlipButton));
        
        TouchButton leftVFlipButton = new TouchButton(panel, "left-v-flip-button-" + idB1, "Interface/scrollVertical.png", 20, 20);
        leftVFlipButton.rightTop(79, 54);
        leftVFlipButton.addEffect(new TouchEffect(leftVFlipButton));

        TouchButton rightButton = new TouchButton(panel, "right-remove-button-" + idB1, "Interface/cross.png", 18, 18);
        rightButton.rightTop(0, 14);
        rightButton.addEffect(new TouchEffect(rightButton));
        
        
        TouchButton rightHFlipButton = new TouchButton(panel, "right-h-flip-button-" + idB1, "Interface/scrollHorizontal.png", 20, 20);
        rightHFlipButton.rightTop(0, 34);
        rightHFlipButton.addEffect(new TouchEffect(rightHFlipButton));
        
        TouchButton rightVFlipButton = new TouchButton(panel, "right-v-flip-button-" + idB1, "Interface/scrollVertical.png", 20, 20);
        rightVFlipButton.rightTop(0, 54);
        rightVFlipButton.addEffect(new TouchEffect(leftVFlipButton));                
        
        ButtonGroup buttonGroup = new ButtonGroup(button1, button2, img2, img, leftButton, rightButton);
        buttonGroup.setHorizontalFlipButton1(leftHFlipButton);
        buttonGroup.setVerticalFlipButton1(leftVFlipButton);
        buttonGroup.setHorizontalFlipButton2(rightHFlipButton);
        buttonGroup.setVerticalFlipButton2(rightVFlipButton);

        return buttonGroup;
    }

    @Override
    public void show() {
        super.show();
        reload();
    }

    protected void setButtonTextureFromMaterial(Material material, String materialTextureName, TouchButton button) {
        MatParamTexture mpt = material.getTextureParam(materialTextureName);
        System.out.println("Setting, " + materialTextureName + " on preview button with, " + mpt);
        if (mpt != null) {
            Texture texture = mpt.getTextureValue();
            button.getPicture().getMaterial().setTexture("Texture", texture);
        } else {
//            button.updatePicture("Interface/texture-button.png");
            Texture texture = window.getApplication().getAssetManager().loadTexture("Interface/texture-button.png");
            button.getPicture().getMaterial().setTexture("Texture", texture);
        }

    }
}
