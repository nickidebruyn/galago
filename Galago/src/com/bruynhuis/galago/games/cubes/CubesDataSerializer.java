/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.games.cubes;

import com.bruynhuis.galago.games.platform.PlatformGame;
import com.bruynhuis.galago.games.platform2d.Platform2DGame;
import com.bruynhuis.galago.games.platform2d.TileMap;
import com.jme3.system.JmeSystem;
import com.jme3.system.Platform;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

/**
 *
 * @author NideBruyn
 */
public class CubesDataSerializer {

    /**
     * This method must be called when reading a room file from the assets
     * folder in your game
     *
     * @param fileName
     */
    public static CubesData readRoomFromAssets(String fileName) {

        InputStream levelInputStream = null;
        CubesData roomData = null;

        try {
            Platform platform = JmeSystem.getPlatform();

            if (isMobileApp()) {
                levelInputStream = JmeSystem.getResourceAsStream("/assets/Levels/" + fileName);

            } else {
                levelInputStream = JmeSystem.getResourceAsStream("/Levels/" + fileName);
            }
        } catch (UnsupportedOperationException e) {
            Logger.getLogger(PlatformGame.class.getName()).log(java.util.logging.Level.INFO, null, e);
            //Load the default
            levelInputStream = JmeSystem.getResourceAsStream("/assets/Levels/" + fileName);

        }

        try {
            if (levelInputStream != null) {
                //Load the room.
                System.out.println("Start loading room " + fileName + " from assests folder...");

                ObjectInputStream in = new ObjectInputStream(levelInputStream);
                try {
                    roomData = (CubesData) in.readObject();

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(TileMap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

                }

            }

        } catch (IOException ex) {
            Logger.getLogger(Platform2DGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        return roomData;
    }

    /**
     * Read the room file from the file system for editing.
     *
     */
    public static CubesData readRoomFromSystem(String fileName) {

        File file = null;
        File folder = JmeSystem.getStorageFolder();
        CubesData roomData = null;

        if (folder != null && folder.exists()) {
            try {
                file = new File(folder.getAbsolutePath() + File.separator + fileName);
                if (file.exists()) {
                    FileInputStream fileIn = new FileInputStream(file);

                    try {
                        ObjectInputStream in = new ObjectInputStream(fileIn);
                        roomData = (CubesData) in.readObject();

                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(TileMap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

                    } catch (EOFException eofe) {
                        Logger.getLogger(TileMap.class.getName()).log(java.util.logging.Level.WARNING, null, eofe);

                    }

                }

            } catch (IOException ex) {
                Logger.getLogger(Platform2DGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }

        return roomData;
    }

    /**
     * This method will save the file to the system folder.
     *
     * @param roomData
     * @param fileName
     */
    public static boolean save(CubesData roomData, String fileName) throws IOException {
        boolean success = false;
        File file = null;

        if (roomData != null) {
            File folder = JmeSystem.getStorageFolder();

            if (folder != null && folder.exists()) {
                file = new File(folder.getAbsolutePath() + File.separator + fileName);
                file.createNewFile();
                if (file != null) {
                    FileOutputStream fileOut = null;
                    ObjectOutputStream out = null;
                    try {
                        fileOut = new FileOutputStream(file);
                        out = new ObjectOutputStream(fileOut);
                        roomData.setSaved(true);
                        out.writeObject(roomData);
                        success = true;

                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(TileMap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

                    } catch (IOException ex) {
                        Logger.getLogger(TileMap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

                    } finally {
                        if (fileOut != null) {
                            try {
                                fileOut.close();
                            } catch (IOException ex) {
                                Logger.getLogger(TileMap.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                            }
                        }

                    }

                }
            }
        }

        return success;

    }

    /**
     * This method can be called from the game to determine if it is a mobile
     * app or not.
     *
     * @return boolean
     */
    public static boolean isMobileApp() {
        try {
            Platform platform = JmeSystem.getPlatform();
            return platform.compareTo(Platform.Android_ARM5) == 0
                    || platform.compareTo(Platform.Android_ARM6) == 0
                    || platform.compareTo(Platform.Android_ARM7) == 0
                    || platform.compareTo(Platform.Android_ARM8) == 0
                    || platform.compareTo(Platform.Android_Other) == 0
                    || platform.compareTo(Platform.Android_X86) == 0;

        } catch (UnsupportedOperationException e) {
            return true;
        }

    }

}
