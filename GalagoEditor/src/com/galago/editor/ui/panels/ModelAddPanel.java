package com.galago.editor.ui.panels;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.GridPanel;
import com.bruynhuis.galago.ui.panel.Panel;
import com.galago.editor.utils.Action;
import com.galago.editor.utils.EditorUtils;
import com.galago.editor.utils.ModelReference;
import com.galago.editor.utils.ModelUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ndebruyn
 */
public class ModelAddPanel extends AbstractPropertiesPanel {

    private Map<String, GridPanel> gridPanels;
    private Map<String, TouchButton> headerButtons;
    private boolean subLayerActive = false;

    public ModelAddPanel(Panel parent) {
        super(parent, "models-add-panel");
    }

    @Override
    protected void init() {

        verticalSpacing = 26;

        TouchButtonAdapter headerEvent = new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {

            }

            @Override
            public void doTouchCancel(float touchX, float touchY, float tpf, String uid) {

            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                System.out.println("Header clicked: " + uid);

                flowPanel.clear();

                if (subLayerActive) {
                    List<String> keys = new ArrayList<>(headerButtons.keySet());
                    Collections.sort(keys);

                    for (String key : keys) {
                        TouchButton value = headerButtons.get(key);
                        value.setText("> " + key);
                        flowPanel.add(value);

                    }

                    subLayerActive = false;

                } else {
                    TouchButton headerButton = headerButtons.get(uid);
                    headerButton.setText("< " + uid);
                    GridPanel gridPanel = gridPanels.get(uid);

                    flowPanel.add(headerButton);
                    flowPanel.add(gridPanel);
                    subLayerActive = true;
                }

                flowPanel.layout();

            }

        };

        TouchButtonAdapter buttonEvent = new TouchButtonAdapter() {
            @Override
            public void doTouchDown(float touchX, float touchY, float tpf, String uid) {
                window.getApplication().getMessageManager().sendMessage(Action.ADD, uid);

            }

            @Override
            public void doTouchCancel(float touchX, float touchY, float tpf, String uid) {
//                window.getApplication().getMessageManager().sendMessage(Action.ADD_DONE, uid);
            }

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                window.getApplication().getMessageManager().sendMessage(Action.ADD_DONE, uid);
            }

        };

        gridPanels = new HashMap<>();
        headerButtons = new HashMap<>();

        List<String> groups = ModelUtils.getAllGroups();
        for (String group : groups) {
            TouchButton header = createHeader(group, group);
            header.addTouchButtonListener(headerEvent);
            headerButtons.put(group, header);

            List<ModelReference> modelList = ModelUtils.getModelsByGroup(group);
            float rowSize = 100;
            int rowCount = (int) ((float) modelList.size() / 3f);
            if (modelList.size() % 3 != 0) {
                rowCount += 1;

            }

            GridPanel modelPanel = new GridPanel(flowPanel, EditorUtils.HIERARCHYBAR_WIDTH, rowCount * rowSize);
            gridPanels.put(group, modelPanel);

            if (modelList.size() > 0) {
                for (ModelReference modelReference : modelList) {
                    TouchButton button = createLabeledTextureButton(modelPanel, modelReference.getName(), modelReference.getName(), modelReference.getPreviewTexture());
                    button.addTouchButtonListener(buttonEvent);

                }

            }

            modelPanel.layout(rowCount, 3);

        }

        //Add the sorted list
        flowPanel.clear();
        List<String> keys = new ArrayList<>(headerButtons.keySet());
        Collections.sort(keys);

        for (String key : keys) {
            TouchButton value = headerButtons.get(key);
            value.setText("> " + key);
            flowPanel.add(value);

        }
        flowPanel.layout();

    }

    @Override
    protected void reload() {

    }

}
