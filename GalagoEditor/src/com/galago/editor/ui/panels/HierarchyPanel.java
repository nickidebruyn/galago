package com.galago.editor.ui.panels;

import com.bruynhuis.galago.ui.Image;
import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.Panel;
import com.bruynhuis.galago.ui.panel.VFlowPanel;
import com.galago.editor.ui.TreeButton;
import com.galago.editor.utils.Action;
import com.galago.editor.utils.EditorUtils;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.Terrain;

/**
 *
 * @author ndebruyn
 */
public class HierarchyPanel extends Panel {

    private TouchButton header;
    private VFlowPanel hierarchyTree;
    private Image selectedItem;
    private Image hoverItem;

    private TouchButtonAdapter touchButtonAdapter = new TouchButtonAdapter() {
        @Override
        public void doHoverOff(float touchX, float touchY, float tpf, String uid) {
            hoverItem.hide();
        }

        @Override
        public void doHoverOver(float touchX, float touchY, float tpf, String uid) {
            System.out.println("Over: " + uid);
            TreeButton button = getButtonById(uid);
            setHoverItem(button);

        }

        @Override
        public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
            System.out.println("Click: " + uid);
            TreeButton button = getButtonById(uid);
            setSelectedItem(button);
            hoverItem.hide();
            window.getApplication().getMessageManager().sendMessage(Action.SELECT, button.getItem());

        }

    };

    public HierarchyPanel(Panel parent) {
        super(parent, "Interface/panel-left.png", EditorUtils.HIERARCHYBAR_WIDTH, parent.getWindow().getHeight());

        setBackgroundColor(EditorUtils.theme.getPanelColor());

        header = new TouchButton(this, "hierarchy-header", "Interface/hierarchy-header.png", EditorUtils.HIERARCHYBAR_WIDTH, 32);
        header.centerTop(0, 0);
        header.setText("Hierarchy");
        header.setTextColor(EditorUtils.theme.getHeaderTextColor());
        header.setBackgroundColor(EditorUtils.theme.getHeaderColor());
        header.setTextAlignment(TextAlign.LEFT);

        hierarchyTree = new VFlowPanel(this, null, EditorUtils.HIERARCHYBAR_WIDTH, parent.getWindow().getHeight() - 32);
        this.add(hierarchyTree);
        hierarchyTree.centerTop(0, 32);
        
        selectedItem = new Image(this, "Interface/tree-item-outline.png", EditorUtils.HIERARCHYBAR_WIDTH, 18);
        selectedItem.setTransparency(0.2f);
        selectedItem.centerTop(0, 32);
        
        hoverItem = new Image(this, "Interface/tree-item-outline.png", EditorUtils.HIERARCHYBAR_WIDTH, 18);
        hoverItem.setBackgroundColor(EditorUtils.theme.getSelectionColor());
        hoverItem.centerTop(0, 32);

        parent.add(this);
    }

    public void reload(Node node) {
        hierarchyTree.clear();
        selectedItem.hide();

        int c = 0;
        if (node != null && node.getQuantity() > 0) {
            for (int i = 0; i < node.getQuantity(); i++) {
                Spatial s = node.getChild(i);
                
                if (!(s instanceof Terrain)) {
                    addHierarchyItem(s, 1, c);
                    c++;
                }                

            }

        }

        hierarchyTree.layout();
    }

    protected void addHierarchyItem(Spatial spatial, int depth, int index) {
//        System.out.println("Adding item: " + index);
        TreeButton button = new TreeButton(hierarchyTree, "" + index, spatial);
//        TouchButton button = new TouchButton(hierarchyTree, spatial.getName(), null, EditorUtils.HIERARCHYBAR_WIDTH, 16, new FontStyle(16));
        String text = spatial.getName();
        if (depth == 2) {
            text = "  - " + text;
        } else if (depth == 3) {
            text = "    - " + text;
        } else {
            text = "+ " + text;
        }
        button.setText(text);
        button.setTextAlignment(TextAlign.LEFT);
        button.addTouchButtonListener(touchButtonAdapter);

//        if (spatial instanceof Node) {
//            Node node = (Node) spatial;
//            if (node != null && node.getQuantity() > 0) {
//                for (int i = 0; i < node.getQuantity(); i++) {
//                    Spatial s = node.getChild(i);
//                    addHierarchyItem(s, depth+1);
//
//                }
//
//            }
//
//        }
    }
    
    private TreeButton getButtonById(String id) {
        int index = Integer.parseInt(id);
        TreeButton button = (TreeButton)hierarchyTree.getWidgets().get(index);
        return button;
    }
    
    private void setSelectedItem(TreeButton button) {        
        selectedItem.show();
        selectedItem.setPosition(button.getPosition().x, button.getPosition().y-18);

    }
    
    private void setHoverItem(TreeButton button) {        
        hoverItem.show();
        hoverItem.setPosition(button.getPosition().x, button.getPosition().y-18);

    }
}
