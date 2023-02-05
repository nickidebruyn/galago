package com.galago.editor.utils;

import com.bruynhuis.galago.util.SharedSystem;
import com.bruynhuis.galago.util.SpatialUtils;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import java.util.ArrayList;
import java.util.List;

/**
 * This class controls all models that exist in the application.
 *
 * @author ndebruyn
 */
public class ModelUtils {

    private static List<ModelReference> modelReferences = new ArrayList<>();
    private static List<String> groups = new ArrayList<>();

    public static void loadAllModels() {
        Node tempNode = new Node("temp");

        //Load a box
        Spatial box = SpatialUtils.addBox(tempNode, 1, 1, 1);
        SpatialUtils.addColor(box, ColorRGBA.White, false);
        easyAddModel("Primitives", "Box", box);

        //Load a sphere
        Spatial sphere = SpatialUtils.addSphere(tempNode, 30, 30, 1);
        SpatialUtils.addColor(sphere, ColorRGBA.White, false);
        easyAddModel("Primitives", "Sphere", sphere);

        //Load a dome
        Spatial dome = SpatialUtils.addDome(tempNode, 30, 30, 1);
        SpatialUtils.addColor(dome, ColorRGBA.White, false);
        easyAddModel("Primitives", "Dome", dome);

        //Load a cylinder
        Spatial cyl = SpatialUtils.addCylinder(tempNode, 5, 30, 1, 2, true);
        SpatialUtils.addColor(cyl, ColorRGBA.White, false);
        cyl.rotate(90 * FastMath.DEG_TO_RAD, 0, 0);
        easyAddModel("Primitives", "Cylinder", cyl);

        //Load a pipe
        Spatial pipe = SpatialUtils.addCylinder(tempNode, 5, 30, 1, 2, false);
        SpatialUtils.addColor(pipe, ColorRGBA.White, false);
        pipe.rotate(90 * FastMath.DEG_TO_RAD, 0, 0);
        ((Geometry) pipe).getMaterial().getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        easyAddModel("Primitives", "Pipe", pipe);

        //Load a cone
        Spatial cone = SpatialUtils.addCone(tempNode, 30, 1, 2);
        SpatialUtils.addColor(cone, ColorRGBA.White, false);
        cone.rotate(-90 * FastMath.DEG_TO_RAD, 0, 0);
        easyAddModel("Primitives", "Cone", cone);

        //Load a pyramid
        Spatial pyramid = SpatialUtils.addCone(tempNode, 4, 1, 2);
        pyramid.setName("Pyramid");
        SpatialUtils.addColor(pyramid, ColorRGBA.White, false);
        pyramid.rotate(-90 * FastMath.DEG_TO_RAD, 0, 0);
        easyAddModel("Primitives", "Pyramid", pyramid);

        //Load a plane
        Spatial plane = SpatialUtils.addPlane(tempNode, 1, 1);
        plane.setName("Plane");
        SpatialUtils.addColor(plane, ColorRGBA.White, false);
        easyAddModel("Primitives", "Plane", plane);

        //Load a donut
        Spatial donut = SpatialUtils.addTorus(tempNode, 30, 30, 0.5f, 1f);
        donut.setName("Donut");
        SpatialUtils.addColor(donut, ColorRGBA.White, false);
        easyAddModel("Primitives", "Donut", donut);

        //Load a ring
        Spatial ring = SpatialUtils.addTorus(tempNode, 30, 30, 0.25f, 1f);
        ring.setName("Ring");
        SpatialUtils.addColor(ring, ColorRGBA.White, false);
        easyAddModel("Primitives", "Ring", ring);

        //Load the vegetation models
//        easyAddModel("Vegetation", "Pine Tree", "Models/trees/pine_tree/scene.j3o", null, 7, 4, 1);
//        easyAddModel("Vegetation", "Palm Trees", "Models/trees/palm_trees/scene.j3o", null, 7, 4, 1);
//
//        String s = "";
//        for (int i = 1; i < 14; i++) {
//            s = i +"";
//            if (i < 10) s = "0" + i;
//            easyAddModel("Vegetation", "Tree " + i, "Models/Editor/Fantacy/tree_"+s+"_combined.fbx", "Materials/Editor/fantacy.j3m", 12, 4, 0.015f);
//            
//        }      
//
//
//        //Load buildings
//        easyAddModel("Buildings", "House 1", "Models/Editor/Fantacy/house01_01.fbx", "Materials/Editor/fantacy.j3m", 12, 4, 0.015f);
//        easyAddModel("Buildings", "House 2", "Models/Editor/Fantacy/house01_02.fbx", "Materials/Editor/fantacy.j3m", 12, 4, 0.015f);
//        easyAddModel("Buildings", "House 3", "Models/Editor/Fantacy/house01_03.fbx", "Materials/Editor/fantacy.j3m", 12, 4, 0.015f);
//        easyAddModel("Buildings", "House 4", "Models/Editor/Fantacy/house01_04.fbx", "Materials/Editor/fantacy.j3m", 12, 4, 0.015f);
//        easyAddModel("Buildings", "House 5", "Models/Editor/Fantacy/house01_05.fbx", "Materials/Editor/fantacy.j3m", 12, 4, 0.015f);
//        
//        easyAddModel("Buildings", "House 6", "Models/Editor/Fantacy/house02_01.fbx", "Materials/Editor/fantacy.j3m", 12, 4, 0.015f);
//        easyAddModel("Buildings", "House 7", "Models/Editor/Fantacy/house02_02.fbx", "Materials/Editor/fantacy.j3m", 12, 4, 0.015f);
//        easyAddModel("Buildings", "House 8", "Models/Editor/Fantacy/house02_03.fbx", "Materials/Editor/fantacy.j3m", 12, 4, 0.015f);
//        easyAddModel("Buildings", "House 9", "Models/Editor/Fantacy/house02_04.fbx", "Materials/Editor/fantacy.j3m", 12, 4, 0.015f);
//        easyAddModel("Buildings", "House 10", "Models/Editor/Fantacy/house02_05.fbx", "Materials/Editor/fantacy.j3m", 12, 4, 0.015f);

//        easyAddModel("Vegetation", "Pine detail", "Models/temp/treePine_large.j3o", 7, 4);
    }

    private static void addGroup(String group) {
        boolean containsGroup = groups.contains(group);
        if (!containsGroup) {
            groups.add(group);

        }

    }

    public static List<String> getAllGroups() {
        return groups;
    }

    private static void easyAddModel(String group, String name, Spatial s) {
        modelReferences.add(addModel(group, name, s));
        addGroup(group);
    }

    private static void easyAddModel(String group, String name, String modelPath, String materialPath, float camDis, float camHeight, float scale) {
        Spatial s = SharedSystem.getInstance().getBaseApplication().getAssetManager().loadModel(modelPath);
        s.setName(name);
                
        if (materialPath != null) {
            Material material = SharedSystem.getInstance().getBaseApplication().getAssetManager().loadMaterial(materialPath);
            s.setMaterial(material);
        }
        
        SceneGraphVisitor sgv = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial sptl) {
                sptl.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                if (sptl instanceof Geometry) {
                    sptl.setLocalScale(scale);                    
                }
            }
        };
        s.depthFirstTraversal(sgv);
        
        MaterialUtils.convertTexturesToEmbedded(s);
        modelReferences.add(addModel(group, s.getName(), s, camDis, camHeight));
        addGroup(group);
    }

    public static List<ModelReference> getAllModels() {
        return modelReferences;
    }

    public static List<ModelReference> getModelsByGroup(String groupName) {
        List<ModelReference> refList = new ArrayList<>();

        for (ModelReference modelReference : modelReferences) {
            if (modelReference.getGroup().equals(groupName)) {
                refList.add(modelReference);
            }
        }

        return refList;
    }

    public static ModelReference getModelByName(String name) {
        ModelReference ref = null;

        for (ModelReference modelReference : modelReferences) {
            if (modelReference.getName().equals(name)) {
                ref = modelReference;
            }
        }

        return ref;
    }

    public static ModelReference addModel(String group, String name, Spatial spatial) {
        return addModel(group, name, spatial, 3, 0);
    }

    public static ModelReference addModel(String group, String name, Spatial spatial, float cameraDistance, float lookatHeight) {
        ModelReference mr = new ModelReference();
        mr.setGroup(group);
        mr.setName(name);
        mr.setModel(spatial);
        mr.setPreviewTexture(createPreviewTexture(mr.getModel(), cameraDistance, lookatHeight));

        return mr;
    }

    public static ModelReference loadModel(String group, String name, String path) {
        ModelReference mr = new ModelReference();
        mr.setGroup(group);
        mr.setName(name);
        mr.setModel(SharedSystem.getInstance().getBaseApplication().getAssetManager().loadModel(path));
        mr.setPreviewTexture(createPreviewTexture(mr.getModel(), 10f, 0));

        return mr;
    }

    public static Texture createPreviewTexture(Spatial spatial, float cameraDistance, float lookatHeight) {
        Camera offCamera = new Camera(256, 256);
        RenderManager renderManager = SharedSystem.getInstance().getBaseApplication().getRenderManager();

        ViewPort offView = renderManager.createPreView("Offscreen View", offCamera);
        offView.setClearFlags(true, true, true);
        offView.setBackgroundColor(EditorUtils.theme.getBackgroundColor());

        // create offscreen framebuffer
        FrameBuffer offBuffer = new FrameBuffer(256, 256, 1);

        //setup framebuffer's cam
        offCamera.setFrustumPerspective(45f, 1f, 1f, 1000f);
        offCamera.setLocation(new Vector3f(-cameraDistance, (cameraDistance * 0.75f) + lookatHeight, cameraDistance));
        offCamera.lookAt(new Vector3f(0f, lookatHeight, 0f), Vector3f.UNIT_Y);

        //setup framebuffer's texture
        Texture2D offTex = new Texture2D(256, 256, Image.Format.RGBA8);
        offTex.setMinFilter(Texture.MinFilter.Trilinear);
        offTex.setMagFilter(Texture.MagFilter.Bilinear);

        //setup framebuffer to use texture
        offBuffer.setDepthTarget(FrameBuffer.FrameBufferTarget.newTarget(Image.Format.Depth));
        offBuffer.addColorTarget(FrameBuffer.FrameBufferTarget.newTarget(offTex));

        //set viewport to render to offscreen framebuffer
        offView.setOutputFrameBuffer(offBuffer);

        // setup framebuffer's scene
        // attach the scene to the viewport to be rendered
        Node rootNode = new Node("root");
        offView.attachScene(rootNode);

        rootNode.attachChild(spatial);

        DirectionalLight sun = new DirectionalLight(new Vector3f(0.25f, -0.5f, -0.7f), ColorRGBA.White);
        rootNode.addLight(sun);
        
        AmbientLight al = new AmbientLight(ColorRGBA.LightGray);
        rootNode.addLight(al);

        rootNode.updateLogicalState(1);
        rootNode.updateGeometricState();

        return offTex;
    }

}
