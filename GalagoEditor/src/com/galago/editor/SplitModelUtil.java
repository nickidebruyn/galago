package com.galago.editor;

import com.galago.editor.utils.EditorUtils;
import com.galago.editor.utils.MaterialUtils;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.ModelKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.system.JmeContext;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nicki
 */
public class SplitModelUtil extends SimpleApplication {

    public static float SCALE = 1f;
    public static Material material;

    public static void main(String[] args) {

        SplitModelUtil app = new SplitModelUtil();
        app.start(JmeContext.Type.Headless);

    }

    @Override
    public void simpleInitApp() {

        System.out.println("Starting the app...");

        //Do the conversion job
        File file = new File("E:\\Backups\\3DModels\\CreativeTrio\\Flowers_Pack_01-20230203T223728Z-001\\Flowers_Pack_01\\Flowers_01.fbx");

        if (file.exists()) {
            String folder = file.getParent();
            System.out.println("Loading the model: " + file.getParent());
            getAssetManager().registerLocator(file.getParent(), FileLocator.class);
            ModelKey key = new ModelKey(file.getName());
            Spatial m = getAssetManager().loadModel(key);
            System.out.println("Model (" + m.getName() + ") successfully imported.");

            Node root = (Node) m;
            for (int i = 0; i < root.getQuantity(); i++) {
                Spatial child = root.getChild(i);
                System.out.println("\t- Child found, " + child.getName());
                System.out.println("\t- Child pos, " + child.getLocalTranslation());
                System.out.println("\t- Child scale, " + child.getLocalScale());
                child.setLocalTranslation(0, 0, 0);
                child.setLocalScale(SCALE);

                try {
                    //Save the material
                    if (i == 0) {
                        material = getMaterialFromSpatial(child);
                        if (material != null) {
                            MaterialUtils.convertTexturesToEmbedded(material);
                            System.out.println("Material texture: " + MaterialUtils.getBaseTexture(material));
                            EditorUtils.saveMaterial(material, new File(folder + "\\split\\" + file.getName() + ".j3m"));
                        }

                    }

                    child.setMaterial(material);

                    //Save the spatial
                    EditorUtils.saveSpatial(child, new File(folder + "\\split\\" + child.getName() + ".j3o"));

                } catch (Exception ex) {
                    Logger.getLogger(SplitModelUtil.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        } else {
            System.out.println("File does not exist...");
        }

        //Exit the tool
        System.out.println("Exiting the app...");
        stop();

    }

    private Material getMaterialFromSpatial(Spatial spatial) {
        Material mat = null;
        material = null;

        final SceneGraphVisitor sgv = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial child) {
                if (child instanceof Geometry) {
                    material = ((Geometry) child).getMaterial();

                }

            }
        };

        spatial.depthFirstTraversal(sgv);
        mat = material;

        return mat;
    }
}
