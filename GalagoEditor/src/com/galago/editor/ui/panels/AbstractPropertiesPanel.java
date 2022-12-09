package com.galago.editor.ui.panels;

import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.panel.VFlowPanel;
import com.galago.editor.ui.ButtonGroup;
import com.galago.editor.ui.LongField;
import com.galago.editor.ui.SliderField;
import com.galago.editor.ui.SpinnerButton;
import com.galago.editor.utils.Action;
import com.galago.editor.utils.EditorUtils;

/**
 *
 * @author ndebruyn
 */
public abstract class AbstractPropertiesPanel extends Panel {

    protected VFlowPanel flowPanel;
    protected TouchButton header;
    protected TouchButton hoverButton;

    public AbstractPropertiesPanel(Panel parent, String name) {
        super(parent, "Interface/panel-left.png", EditorUtils.HIERARCHYBAR_WIDTH, parent.getWindow().getHeight());

        setBackgroundColor(EditorUtils.theme.getPanelColor());

        flowPanel = new VFlowPanel(this, null, EditorUtils.HIERARCHYBAR_WIDTH, parent.getWindow().getHeight());
        this.add(flowPanel);
        flowPanel.centerTop(0, 0);

        init();

        flowPanel.layout();

        hoverButton = new TouchButton(this, name + "-hover", "Interface/blank.png", EditorUtils.HIERARCHYBAR_WIDTH, parent.getWindow().getHeight());
        hoverButton.centerTop(0, 0);
        hoverButton.setTransparency(0.1f);
        hoverButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doHoverOff(float touchX, float touchY, float tpf, String uid) {
                window.getApplication().getMessageManager().sendMessage(Action.UI_OFF, null);
            }

            @Override
            public void doHoverOver(float touchX, float touchY, float tpf, String uid) {
                window.getApplication().getMessageManager().sendMessage(Action.UI_OVER, null);
            }

        });

        parent.add(this);

    }

    protected abstract void init();

    protected abstract void reload();

    protected TouchButton createHeader(String uid, String text) {
        TouchButton header = new TouchButton(flowPanel, uid, "Interface/hierarchy-header.png", EditorUtils.HIERARCHYBAR_WIDTH - 4, 32);
        header.setText(text);
        header.setTextColor(EditorUtils.theme.getHeaderTextColor());
        header.setBackgroundColor(EditorUtils.theme.getHeaderColor());
        header.setTextAlignment(TextAlign.LEFT);
        return header;
    }

    protected SpinnerButton createLabeledSpinner(String text, String id, String[] ops) {
        System.out.println("Setting options: " + ops);
        Panel p = new Panel(flowPanel, null, EditorUtils.HIERARCHYBAR_WIDTH - 6, 32);
        flowPanel.add(p);

        Label label = new Label(p, text, 14, EditorUtils.HIERARCHYBAR_WIDTH * 0.5f, 32);
        label.setAlignment(TextAlign.LEFT);
        label.setVerticalAlignment(TextAlign.CENTER);
        label.leftCenter(5, 0);

        SpinnerButton spinner = new SpinnerButton(p, id, ops);
        spinner.rightCenter(5, 0);

        return spinner;
    }

    protected SliderField createLabeledSlider(String text, int min, int max, int increment) {
        Panel panel = new Panel(flowPanel, null, EditorUtils.HIERARCHYBAR_WIDTH - 6, 32);
        flowPanel.add(panel);

        Label label = new Label(panel, text, 14, EditorUtils.HIERARCHYBAR_WIDTH * 0.5f, 32);
        label.setAlignment(TextAlign.LEFT);
        label.setVerticalAlignment(TextAlign.CENTER);
        label.leftCenter(5, 0);

        SliderField field = new SliderField(panel, min, max, increment);
        field.rightCenter(5, 0);

        return field;
    }

    protected SliderField createLabeledSliderDecimal(String text, float min, float max, float increment) {
        Panel panel = new Panel(flowPanel, null, EditorUtils.HIERARCHYBAR_WIDTH - 6, 32);
        flowPanel.add(panel);

        Label label = new Label(panel, text, 14, EditorUtils.HIERARCHYBAR_WIDTH * 0.5f, 32);
        label.setAlignment(TextAlign.LEFT);
        label.setVerticalAlignment(TextAlign.CENTER);
        label.leftCenter(5, 0);

        SliderField field = new SliderField(panel, min, max, increment);
        field.setDecimals(true);
        field.rightCenter(5, 0);

        return field;
    }

    protected LongField createLabeledLongInput(String text, String id) {
        Panel panel = new Panel(flowPanel, null, EditorUtils.HIERARCHYBAR_WIDTH - 6, 32);
        flowPanel.add(panel);

        Label label = new Label(panel, text, 14, EditorUtils.HIERARCHYBAR_WIDTH * 0.5f, 32);
        label.setAlignment(TextAlign.LEFT);
        label.setVerticalAlignment(TextAlign.CENTER);
        label.leftCenter(5, 0);

        LongField field = new LongField(panel, id);
        field.rightCenter(5, 0);

        return field;
    }

    protected TouchButton createLabeledTextureButton(String text, String id) {
        Panel panel = new Panel(flowPanel, null, EditorUtils.HIERARCHYBAR_WIDTH - 6, 80);
        flowPanel.add(panel);

        Label label = new Label(panel, text, 14, EditorUtils.HIERARCHYBAR_WIDTH * 0.5f, 60);
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

    protected ButtonGroup createLabeledTextureButtonGroup(String text, String idB1, String idB2) {
        Panel panel = new Panel(flowPanel, null, EditorUtils.HIERARCHYBAR_WIDTH - 6, 80);
        flowPanel.add(panel);

        Label label = new Label(panel, text, 14, EditorUtils.HIERARCHYBAR_WIDTH * 0.5f, 60);
        label.setAlignment(TextAlign.LEFT);
        label.setVerticalAlignment(TextAlign.CENTER);
        label.leftTop(5, 5);

        Image img = new Image(panel, "Interface/texture-button.png", 70, 70);
        img.setBackgroundColor(EditorUtils.theme.getButtonColor());
        img.rightTop(0, 10);

        Image img2 = new Image(panel, "Interface/texture-button.png", 70, 70);
        img2.setBackgroundColor(EditorUtils.theme.getButtonColor());
        img2.rightTop(75, 10);

        TouchButton button1 = new TouchButton(panel, idB1, "Interface/texture-button.png", 60, 60);
        button1.rightTop(80, 15);
        button1.addEffect(new TouchEffect(button1));
        button1.getPicture().setMaterial(button1.getPicture().getMaterial().clone());

        TouchButton button2 = new TouchButton(panel, idB2, "Interface/texture-button.png", 60, 60);
        button2.rightTop(5, 15);
        button2.addEffect(new TouchEffect(button2));
        button2.getPicture().setMaterial(button2.getPicture().getMaterial().clone());

        return new ButtonGroup(button1, button2);
    }

    @Override
    public void show() {
        super.show();
        reload();
    }

}
