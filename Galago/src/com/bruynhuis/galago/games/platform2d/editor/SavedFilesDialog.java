/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.platform2d.editor;

import com.bruynhuis.galago.ui.FontStyle;
import com.bruynhuis.galago.ui.Label;
import com.bruynhuis.galago.ui.listener.TouchButtonAdapter;
import com.bruynhuis.galago.ui.panel.PopupDialog;
import com.bruynhuis.galago.ui.panel.VPagerPanel;
import com.bruynhuis.galago.ui.window.Window;
import com.jme3.math.ColorRGBA;
import com.jme3.system.JmeSystem;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 *
 * @author NideBruyn
 */
public class SavedFilesDialog extends PopupDialog {

    private ButtonClose closeButton;
    private VPagerPanel pagerPanel;
    private TouchButtonAdapter buttonAdapter;
    private TouchButtonAdapter deleteButtonAdapter;
    private TouchButtonAdapter shareButtonAdapter;
    private TouchButtonAdapter fileSelectedButtonAdapter;
    private String selectedLevel;
    private String filePrefix;

    public SavedFilesDialog(Window window, String filePrefix) {
        super(window, "Resources/panel.png", window.getWidth() * 0.9f, window.getHeight(), true);
        this.filePrefix = filePrefix;

        setBackgroundColor(ColorRGBA.Gray);

        title.remove();

        title = new Label(this, "Saved Levels", 400, 30, new FontStyle(24));
        title.setTextColor(ColorRGBA.LightGray);
        title.centerTop(0, 15);

        closeButton = new ButtonClose(this, "close files dialog");
        closeButton.rightTop(25, 25);
        closeButton.addTouchButtonListener(new TouchButtonAdapter() {

            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                SavedFilesDialog.this.hide();
            }

        });

        buttonAdapter = new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                selectedLevel = uid.trim() + Platform2DEditScreen.FILE_EXT;
                fileSelectedButtonAdapter.doTouchUp(touchX, touchY, tpf, selectedLevel);

            }
        };

        deleteButtonAdapter = new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                selectedLevel = uid.trim() + Platform2DEditScreen.FILE_EXT;
                hide();
                removeFile(selectedLevel);
                show();
            }
        };

        shareButtonAdapter = new TouchButtonAdapter() {
            @Override
            public void doTouchUp(float touchX, float touchY, float tpf, String uid) {
                selectedLevel = uid.trim() + Platform2DEditScreen.FILE_EXT;
                shareFile(selectedLevel);

            }
        };

    }

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    private void removeFile(String filename) {
        File folder = JmeSystem.getStorageFolder();

        if (folder != null && folder.exists() && folder.isDirectory()) {
            //FIRST We get all the valid files
            File[] levelFiles = folder.listFiles();
            if (levelFiles != null && levelFiles.length > 0) {

                for (int i = 0; i < levelFiles.length; i++) {
                    File f = levelFiles[i];

                    if (f.exists() && f.isFile() && f.getName().equals(filename)) {
                        System.out.println("Removing File: " + f.getName());
                        f.delete();

                    }

                }
            }
        }
    }

    private void shareFile(String filename) {
        File folder = JmeSystem.getStorageFolder();

        if (folder != null && folder.exists() && folder.isDirectory()) {
            //FIRST We get all the valid files
            File[] levelFiles = folder.listFiles();
            if (levelFiles != null && levelFiles.length > 0) {

                for (int i = 0; i < levelFiles.length; i++) {
                    File f = levelFiles[i];

                    if (f.exists() && f.isFile() && f.getName().equals(filename)) {
                        
                        String content = "";
                        StringBuffer mailStr = new StringBuffer("");
                        try {
                            // Open the file that is the first 
                            // command line parameter
                            FileInputStream fstream = new FileInputStream(f);
                            // Get the object of DataInputStream
                            DataInputStream in = new DataInputStream(fstream);
                            BufferedReader br = new BufferedReader(new InputStreamReader(in));
                            
                            String strLine = null;
                            //Read File Line By Line
                            while ((strLine = br.readLine()) != null) {
                                // Print the content on the console
                                mailStr.append(strLine);
                            }
                            //Close the input stream
                            in.close();
                            
                        } catch (Exception e) {//Catch exception if any
                            System.err.println("Error: " + e.getMessage());
                        }

                        content = mailStr.toString();
                        System.out.println("Share File: " + f.getName());
                        System.out.println("Share Content: " + content);
                        window.getApplication().doEmail("nickidebruyn@gmail.com", filename, content);

                    }

                }
            }
        }
    }

    public void setFileSelectedButtonAdapter(TouchButtonAdapter fileSelectedButtonAdapter) {
        this.fileSelectedButtonAdapter = fileSelectedButtonAdapter;
    }

    @Override
    public void show() {
        if (pagerPanel != null) {
            pagerPanel.clear();
            pagerPanel.remove();
        }

        super.show(); //To change body of generated methods, choose Tools | Templates.

        selectedLevel = null;

        ArrayList<String> fileNames = new ArrayList<String>();

        File folder = JmeSystem.getStorageFolder();

        if (folder != null && folder.exists() && folder.isDirectory()) {
            //FIRST We get all the valid files
            File[] levelFiles = folder.listFiles();
            if (levelFiles != null && levelFiles.length > 0) {

                for (int i = 0; i < levelFiles.length; i++) {
                    File f = levelFiles[i];

                    if (f.exists() && f.isFile() && f.getName().startsWith(filePrefix) && f.getName().endsWith(Platform2DEditScreen.FILE_EXT)) {
                        System.out.println("File: " + f.getName());
                        fileNames.add(f.getName().replaceAll(Platform2DEditScreen.FILE_EXT, ""));

                    }

                }
            }

            //Next we create the container panel
            pagerPanel = new VPagerPanel(this, window.getWidth(), 60 * fileNames.size());
            pagerPanel.centerAt(0, 0);
            this.add(pagerPanel);

            //Now we add the file names per button
            for (int i = 0; i < fileNames.size(); i++) {
                String name = fileNames.get(i);
                FileButton fileButton = new FileButton(pagerPanel, name, "   " + name);
                fileButton.addOkButtonListener(buttonAdapter);
                fileButton.addDeleteButtonListener(deleteButtonAdapter);
                fileButton.addShareButtonListener(shareButtonAdapter);

            }
            pagerPanel.layout(0, 300 * window.getScaleFactorHeight());
            pagerPanel.setDepthPosition(1);
        }

    }

}
