package com.galago.editor.ui.panels;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.GridPanel;
import com.bruynhuis.galago.ui.panel.Panel;
import com.galago.editor.utils.Action;
import com.galago.editor.utils.EditorUtils;
import com.galago.editor.utils.ModelReference;
import com.galago.editor.utils.ModelUtils;
import java.util.List;

/**
 *
 * @author ndebruyn
 */
public class ModelAddPanel extends AbstractPropertiesPanel {

    private GridPanel primitivesPanel;
    private GridPanel vegetationPanel;

    public ModelAddPanel(Panel parent) {
        super(parent, "models-add-panel");
    }

    @Override
    protected void init() {

        verticalSpacing = 26;

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

        //Define the primitive section
        createHeader("primitives", "Primitives");
        List<ModelReference> primitivesList = ModelUtils.getModelsByGroup("Primitives");
        float rowSize = 100;
        int rowCount = (int) ((float) primitivesList.size() / 3f);
        if (primitivesList.size() % 3 != 0) {
            rowCount += 1;

        }

        primitivesPanel = new GridPanel(flowPanel, EditorUtils.HIERARCHYBAR_WIDTH, rowCount * rowSize);
        flowPanel.add(primitivesPanel);

        if (primitivesList.size() > 0) {
            for (ModelReference modelReference : primitivesList) {
                TouchButton button = createLabeledTextureButton(primitivesPanel, modelReference.getName(), modelReference.getName(), modelReference.getPreviewTexture());
                button.addTouchButtonListener(buttonEvent);

            }

        }

        primitivesPanel.layout(rowCount, 3);
        

        //Define the vegetation section
        createHeader("vegetation", "Vegetation");
        List<ModelReference> vegList = ModelUtils.getModelsByGroup("Vegetation");        
        rowCount = (int) ((float) vegList.size() / 3f);
        if (vegList.size() % 3 != 0) {
            rowCount += 1;

        }

        vegetationPanel = new GridPanel(flowPanel, EditorUtils.HIERARCHYBAR_WIDTH, rowCount * rowSize);
        flowPanel.add(vegetationPanel);

        if (vegList.size() > 0) {
            for (ModelReference modelReference : vegList) {
                TouchButton button = createLabeledTextureButton(vegetationPanel, modelReference.getName(), modelReference.getName(), modelReference.getPreviewTexture());
                button.addTouchButtonListener(buttonEvent);

            }

        }

        vegetationPanel.layout(rowCount, 3);        
        
        
    }

    @Override
    protected void reload() {

    }

}
