package com.galago.editor.ui.dialogs;

import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.PopupDialog;
import com.bruynhuis.galago.ui.window.Window;
import com.galago.editor.ui.Button;
import com.galago.editor.ui.actions.TerrainAction;
import com.galago.editor.utils.Action;
import com.galago.editor.utils.EditorUtils;

/**
 *
 * @author ndebruyn
 */
public class TerrainDialog extends PopupDialog {
    
    private TouchButton closeButton;
    private Button createButton;
    private TerrainAction terrainAction;

    public TerrainDialog(Window window) {
        super(window, "Interface/dialog-panel.png", 840, 640, true);

        setBackgroundColor(EditorUtils.theme.getPanelColor());
        setTitle("Terrain Settings");
        setTitleColor(EditorUtils.theme.getHeaderTextColor());
        setTitleSize(16);
        title.centerTop(0, 26);
        
        closeButton = new TouchButton(this, "terrain_close_button", "Interface/cross.png", 32, 32);
        closeButton.rightTop(26, 26);
        closeButton.addEffect(new TouchEffect(closeButton));
        closeButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                hide();
            }
            
        });

        createButton = new Button(this, "terrain_create", "Create");
        createButton.centerBottom(0, 40);
        createButton.addTouchButtonListener(new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                hide();
                window.getApplication().getMessageManager().sendMessage(Action.CREATE_TERRAIN, terrainAction);
                
            }
            
        });
    }

    @Override
    public void show() {
        this.terrainAction = new TerrainAction();
        this.terrainAction.setType(TerrainAction.TYPE_ISLAND);
        
        super.show();
    }

}
