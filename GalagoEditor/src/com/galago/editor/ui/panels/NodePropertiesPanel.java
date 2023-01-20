package com.galago.editor.ui.panels;

import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.scene.Node;

/**
 *
 * @author ndebruyn
 */
public class NodePropertiesPanel extends AbstractPropertiesPanel {

    private Node node;

    public NodePropertiesPanel(Panel parent) {
        super(parent, "properties-node");
    }

    @Override
    protected void init() {
        createHeader("transformations", "Node");

    }

    @Override
    protected void reload() {

    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

}
