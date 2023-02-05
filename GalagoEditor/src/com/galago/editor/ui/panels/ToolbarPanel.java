package com.galago.editor.ui.panels;

import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.Widget;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.Panel;
import com.galago.editor.ui.ToolbarButton;
import com.galago.editor.utils.Action;
import com.galago.editor.utils.EditorUtils;

/**
 *
 * @author ndebruyn
 */
public class ToolbarPanel extends Panel {

    private ToolbarButton selectButton;
    private ToolbarButton paintButton;
    private ToolbarButton transformButton;
    private ToolbarButton hierarchyButton;

    private ToolbarButton addButton;
    private ToolbarButton terrainButton;
    private ToolbarButton waterButton;
    private ToolbarButton skyButton;
    private ToolbarButton importButton;
    private ToolbarButton settingsButton;
    private ToolbarButton helpButton;
    private ToolbarButton saveButton;
    private ToolbarButton openButton;
    private ToolbarButton newButton;
    private ToolbarButton statsButton;
    private ToolbarButton gridButton;

    private ToolbarButton selectedButton;

    private Image selectedImage;
    private Panel toolTip;
    private TouchButton toolTipButton;

    private float spacing = EditorUtils.TOOLBAR_WIDTH;
    private float topSpacing = 0;
    private float bottomSpacing = spacing;

    private TouchButtonAdapter touchButtonAdapter = new TouchButtonAdapter() {
        @Override
        public void doHoverOff(float touchX, float touchY, float tpf, String uid) {
            toolTip.hide();
        }

        @Override
        public void doHoverOver(float touchX, float touchY, float tpf, String uid) {
            ToolbarButton overButton = getButtonByName(uid);
            setSelectedTooltip(overButton);

        }

        @Override
        public void doTouchUp(float touchX, float touchY, float tpf, String uid) {

            ToolbarButton overButton = getButtonByName(uid);
            setSelectedButton(overButton);
            window.getApplication().getMessageManager().sendMessage(uid, null);
            toolTip.hide();

        }

    };

    public void setSelectedButtonByName(String name) {
        ToolbarButton overButton = getButtonByName(name);
        setSelectedButton(overButton);
        window.getApplication().getMessageManager().sendMessage(name, null);
        toolTip.hide();

    }

    public ToolbarPanel(Panel parent) {
        super(parent, "Interface/toolbar-left.png", EditorUtils.TOOLBAR_WIDTH, parent.getWindow().getHeight());

        setBackgroundColor(EditorUtils.theme.getPanelColor());

        selectedImage = new Image(this, "Interface/blank.png", spacing - 4, spacing - 4);
        selectedImage.setBackgroundColor(EditorUtils.theme.getSelectionColor());

        toolTip = new Panel(this, "Interface/outline-box.png", spacing - 4, spacing - 4);
        toolTip.setBackgroundColor(EditorUtils.theme.getSelectionColor());
        this.add(toolTip);

        toolTipButton = new TouchButton(toolTip, "tooltip", "Interface/tooltip.png", 256, 64);
        toolTipButton.setText("Tooltip");
        toolTipButton.setTextColor(EditorUtils.theme.getTooltipTextColor());
        toolTipButton.setBackgroundColor(EditorUtils.theme.getTooltipColor());
        toolTipButton.rightCenter(-256, 0);

        selectButton = addButtonTop(Action.SELECT, "Interface/downLeft.png", "Select object");
        transformButton = addButtonTop(Action.TRANSFORM, "Interface/transform.png", "Move, scale, rotate");
        paintButton = addButtonTop(Action.PAINT, "Interface/brush.png", "Paint");
        hierarchyButton = addButtonTop(Action.HIERARCHY, "Interface/hierarchy.png", "Scene hierarchy");
        addButton = addButtonTop(Action.ADD, "Interface/plus.png", "Add object");
        terrainButton = addButtonTop(Action.TERRAIN, "Interface/terrain.png", "Terrain");
        waterButton = addButtonTop(Action.WATER, "Interface/water.png", "Water");
        skyButton = addButtonTop(Action.SKY, "Interface/sky.png", "Sky");
        importButton = addButtonTop(Action.IMPORT, "Interface/import.png", "Import object");
        statsButton = addButtonTop(Action.STATS, "Interface/stats.png", "Statistics");
        gridButton = addButtonTop(Action.GRID, "Interface/grid.png", "Show/Hide grid");

        helpButton = addButtonBottom(Action.HELP, "Interface/question.png", "Help");
        settingsButton = addButtonBottom(Action.SETTINGS, "Interface/settings.png", "Settings");
        saveButton = addButtonBottom(Action.SAVE, "Interface/save.png", "Save scene");
        openButton = addButtonBottom(Action.OPEN, "Interface/open.png", "Open scene");
        newButton = addButtonBottom(Action.NEW, "Interface/plus.png", "New scene");

        setSelectedButton(selectButton);

        parent.add(this);
    }

    private ToolbarButton addButtonTop(String id, String image, String tooltip) {

        ToolbarButton button = new ToolbarButton(this, id, image);
        button.centerTop(0, topSpacing);
        topSpacing += spacing;

        button.addTouchButtonListener(touchButtonAdapter);

        button.getWidgetNode().setUserData("tooltip", tooltip);
        return button;
    }

    private ToolbarButton addButtonBottom(String id, String image, String tooltip) {
        ToolbarButton button = new ToolbarButton(this, id, image);
        button.centerBottom(0, bottomSpacing);
        bottomSpacing += spacing;

        button.addTouchButtonListener(touchButtonAdapter);

        button.getWidgetNode().setUserData("tooltip", tooltip);

        return button;
    }

    private void setSelectedButton(ToolbarButton button) {

        if (selectedButton != null) {
            selectedButton.setBackgroundColor(EditorUtils.theme.getIconColor());
        }

        selectedImage.setPosition(button.getPosition().x, button.getPosition().y);
        button.setBackgroundColor(EditorUtils.theme.getSelectionInvertColor());

        selectedButton = button;

    }

    private void setSelectedTooltip(ToolbarButton button) {
        toolTipButton.setText(button.getWidgetNode().getUserData("tooltip"));
        toolTip.show();
        toolTip.setPosition(button.getPosition().x, button.getPosition().y);

    }

    private ToolbarButton getButtonByName(String name) {
        ToolbarButton button = null;
        for (int i = 0; i < widgets.size(); i++) {
            Widget widget = widgets.get(i);
            if (widget.getName().equals(name) && widget instanceof ToolbarButton) {
                button = (ToolbarButton) widget;
                break;

            }
        }

        return button;
    }

    public void reset() {
        toolTip.hide();
    }
}
