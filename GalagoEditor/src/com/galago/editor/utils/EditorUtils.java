package com.galago.editor.utils;

import com.galago.editor.themes.DefaultTheme;
import com.galago.editor.themes.EditorTheme;
import com.jme3.asset.AssetManager;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.scene.Spatial;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author ndebruyn
 */
public class EditorUtils {

    public static String GAME_EXTENSION = ".j3g";
    public static String SPATIAL_EXTENSION = ".j3o";
    public static float TOOLBAR_WIDTH = 46f;
    public static float TOOLBAR_BUTTON_SIZE = 42f;
    public static float HIERARCHYBAR_WIDTH = 256f;
    public static EditorTheme theme = new DefaultTheme();
    public static String LAST_LOCATION = "LAST_LOCATION";

    public static boolean isCompatableModel(File file) {
        return file.getPath().endsWith(".obj") || file.getPath().endsWith(".gltf") || file.getPath().endsWith(".fbx") || file.getPath().endsWith(".j3o");
    }

    public static boolean isCompatableTexture(File file) {
        return file.getPath().endsWith(".png") || file.getPath().endsWith(".jpg") || file.getPath().endsWith(".jpeg");
    }

    public static String getCompatableModelExtensions() {
        return ".obj, .gltf, .fbx, .j3o";
    }

    public static String getCompatableTextureExtensions() {
        return ".png, .jpg, .jpeg";
    }

    public static void saveSpatial(Spatial spatial, File file) throws Exception {
        //String userHome = System.getProperty("user.home");
        BinaryExporter exporter = BinaryExporter.getInstance();
        if (file != null && !file.getName().endsWith(SPATIAL_EXTENSION)) {
            throw new Exception("Invalid file extension, must be " + SPATIAL_EXTENSION);
        }

//        storage.getEditorData().setLastGameLocation(file.getPath());
//        System.out.println("Saving to location: " + storage.getEditorData().getLastGameLocation());
//        storage.save();
        try {
            exporter.save(spatial, file);
            System.out.println("Spatial " + file.getName() + " successfully saved!");

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static Spatial openSpatial(File file, AssetManager assetManager) {
        if (file == null || !file.exists()) {
            return null;
        }

        BinaryImporter importer = BinaryImporter.getInstance();
        importer.setAssetManager(assetManager);
        try {
            Spatial spatial = (Spatial) importer.load(file);
            System.out.println("Found spatial: " + spatial.getName());
            
            MaterialUtils.convertTexturesToDepthRendering(spatial);
            
            return spatial;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
