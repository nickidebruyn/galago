package com.galago.editor.utils;

import com.galago.editor.themes.DefaultTheme;
import com.galago.editor.themes.EditorTheme;
import com.jme3.asset.AssetManager;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

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
    public static String MODEL = "MODEL";
    public static String POST_PROCESS_FILTER = "FPP";
    public static String CAMERA_POSITION = "CAMERA_POSITION";
    public static String CAMERA_ROTATION = "CAMERA_ROTATION";

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
            
            //TODO: (2022-12-14) We can put this code back when we need compression on the files
//            String gzipFile = file.getPath().replace(".j3o", ".j3g");            
//            compressGzipFile(file.getPath(), gzipFile);

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

            //Do some extra trix to the scene
            spatial.depthFirstTraversal(new SceneGraphVisitorAdapter() {
                @Override
                public void visit(Node node) {
                    if (node instanceof BatchNode) {
                        ((BatchNode) node).batch();

                    }
                }

            });

            return spatial;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Helper method to decompress a file to gzip
     * @param gzipFile
     * @param newFile 
     */
    private static void decompressGzipFile(String gzipFile, String newFile) {
        try {
            FileInputStream fis = new FileInputStream(gzipFile);
            GZIPInputStream gis = new GZIPInputStream(fis);
            FileOutputStream fos = new FileOutputStream(newFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            //close resources
            fos.close();
            gis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Helper method to compress a file to gzip
     * @param file
     * @param gzipFile 
     */
    private static void compressGzipFile(String file, String gzipFile) {
        try {
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(gzipFile);
            GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                gzipOS.write(buffer, 0, len);
            }
            //close resources
            gzipOS.close();
            fos.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
