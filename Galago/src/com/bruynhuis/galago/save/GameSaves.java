/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.save;

import com.bruynhuis.galago.util.Debug;
import com.jme3.system.JmeSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

/**
 * This class will handle all player data. Save user settings with this class.
 *
 * @author NideBruyn
 */
public class GameSaves {

    private File file;
    private File levelfile;
    private String fileName = "defaultgame.save";
    private GameData gameData;

    /**
     *
     * @param fileName
     */
    public GameSaves(String fileName) {
        this.fileName = fileName;
    }

    public GameData getGameData() {
        if (gameData == null) {
            gameData = new GameData();
        }
        return gameData;
    }

    public void setGameData(GameData gameData) {
        this.gameData = gameData;
    }

    /**
     * Read any saved data from the file system.
     */
    public void read() {
        File folder = JmeSystem.getStorageFolder();

        if (folder != null && folder.exists()) {
            try {
                file = new File(folder.getAbsolutePath() + File.separator + fileName);
                if (file.exists()) {
                    FileInputStream fileIn = new FileInputStream(file);
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    try {
                        gameData = (GameData) in.readObject();

                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(GameSaves.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                    }

                } else {
                    file.createNewFile();
                    gameData = new GameData();
                    save();
                }

            } catch (IOException ex) {
                Logger.getLogger(GameSaves.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        } else {
        }
    }

    /**
     * Write game data to the file system.
     */
    public void save() {
        File folder = JmeSystem.getStorageFolder();

        if (folder != null && folder.exists()) {
            if (file != null) {
                FileOutputStream fileOut = null;
                ObjectOutputStream out = null;
                try {
                    fileOut = new FileOutputStream(file);
                    out = new ObjectOutputStream(fileOut);
                    out.writeObject(gameData);

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(GameSaves.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

                } catch (IOException ex) {
                    Logger.getLogger(GameSaves.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);

                } finally {
                    if (fileOut != null) {
                        try {
                            fileOut.close();
                        } catch (IOException ex) {
                            Logger.getLogger(GameSaves.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                        }
                    }


                }

            }
        }
    }

    /**
     * Read any saved data from the file system.
     */
    public LevelData read(String levelFileName) {
        File folder = JmeSystem.getStorageFolder();
        LevelData levelData = null;

        if (folder != null && folder.exists()) {
            try {
                levelfile = new File(folder.getAbsolutePath() + File.separator + levelFileName);
                if (levelfile.exists()) {
                    FileInputStream fileIn = new FileInputStream(levelfile);
                    ObjectInputStream in = new ObjectInputStream(fileIn);
                    try {
                        levelData = (LevelData) in.readObject();

                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(GameSaves.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                        Debug.log("LevelData on read: " + ex.getMessage());
                    }

                } else {
                    levelfile.createNewFile();
                    levelData = new LevelData();
                    levelData.setSaved(true);
                    save();
                }

            } catch (IOException ex) {
                Logger.getLogger(GameSaves.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        } else {
        }
        return levelData;
    }

    /**
     * Write level data to the file system.
     */
    public void save(LevelData levelData) {
        File folder = JmeSystem.getStorageFolder();

        if (folder != null && folder.exists()) {
            File fileLevel = new File(levelData.getFileName());
            FileOutputStream fileOut = null;
            ObjectOutputStream out = null;
            try {
                if (!fileLevel.exists()) {
                    fileLevel.createNewFile();
                }
                
                fileOut = new FileOutputStream(fileLevel);
                out = new ObjectOutputStream(fileOut);
                levelData.setSaved(true);
                out.writeObject(levelData);

            } catch (FileNotFoundException ex) {
                Logger.getLogger(GameSaves.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                Debug.log("LevelData on save: " + ex.getMessage());

            } catch (IOException ex) {
                Logger.getLogger(GameSaves.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                Debug.log("LevelData on save: " + ex.getMessage());

            } finally {
                if (fileOut != null) {
                    try {
                        fileOut.close();
                    } catch (IOException ex) {
                        Logger.getLogger(GameSaves.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                        Debug.log("LevelData on save: " + ex.getMessage());
                    }
                }


            }


        }
    }
    
//    /**
//     * This helper method will serialize an object to a byte array
//     * @param gd
//     * @return 
//     */
//    public byte[] serializeGameData(GameData gd) {
//        ObjectOutput o = null;
//        ByteArrayOutputStream b = null;
//        
//        try {
//            b = new ByteArrayOutputStream();
//            o = new ObjectOutputStream(b);
//            o.writeObject(gd);
//            o.flush();
//            
//            return b.toByteArray();
//            
//        } catch (IOException ex) {
//            Logger.getLogger(GameSaves.class.getName()).log(Level.SEVERE, null, ex);
//            
//        } finally {
//            try {
//                if (o != null) {
//                    o.close();
//                }
//                if (b != null) {
//                    b.close();
//                }
//                
//            } catch (IOException ex) {
//                Logger.getLogger(GameSaves.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        
//        return null;
//    }
//
//    public GameData deserialize(byte[] bytes, BaseApplication baseApplication) {
//        ObjectInput o = null;
//        ByteArrayInputStream b = null;
//        
//        if (bytes == null || bytes.length <= 0) {
//            return null;
//        }
//        
//        try {            
//            baseApplication.doAlert("Byte: " + new String(bytes));
//            b = new ByteArrayInputStream(bytes);
//            o = new ObjectInputStream(b);
//            baseApplication.doAlert("Byte was found: " + o);
//            
//            return (GameData)o.readObject();
//            
//        } catch (IOException ex) {
//            baseApplication.doAlert("Cause: " + ex.getCause());
//            baseApplication.doAlert("Exception: " + ex.toString());
//            return null;
//            
//        } catch (ClassNotFoundException ex) {
//            baseApplication.doAlert("Bytes exception2: " + ex.getMessage());
//            return null;
//            
//        } catch (Exception ex) {
//            baseApplication.doAlert("Bytes exception3: " + ex.getMessage());
//            return null;
//            
//        } finally {
//            try {
//                if (o != null) {
//                    o.close();
//                }                
//            } catch (Exception ex) {
//            }
//        }
//
//    }
}
