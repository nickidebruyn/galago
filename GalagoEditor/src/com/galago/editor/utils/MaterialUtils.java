package com.galago.editor.utils;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.MatParam;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;

import java.awt.*;

public class MaterialUtils {

    public static Material loadLitPixelatedMaterial(AssetManager assetManager, String texture) {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture tex = assetManager.loadTexture(new TextureKey(texture, false));
        tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("DiffuseMap", tex);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.setFloat("AlphaDiscardThreshold", 0.55f);
        return mat;
    }

    public static Material loadUnlitPixelatedMaterial(AssetManager assetManager, String texture) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = assetManager.loadTexture(new TextureKey(texture, false));
        tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("ColorMap", tex);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.setFloat("AlphaDiscardThreshold", 0.55f);
        return mat;
    }

    public static void updateToPixelatedMaterial(Node node) {

        SceneGraphVisitor sceneGraphVisitor = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spatial) {
                if (spatial instanceof Geometry) {
                    Geometry geometry = ((Geometry) spatial);
                    if (geometry.getMaterial() != null) {
                        Material layerMaterial = geometry.getMaterial();

                        MatParam matParam = layerMaterial.getParam("DiffuseMap");
                        Texture texture = (Texture) matParam.getValue();
                        texture.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
                        texture.setMagFilter(Texture.MagFilter.Nearest);
                        layerMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
                        layerMaterial.setFloat("AlphaDiscardThreshold", 0.55f);
                    }
                    geometry.setQueueBucket(RenderQueue.Bucket.Transparent);
                }

            }
        };
        node.depthFirstTraversal(sceneGraphVisitor);

    }

    public static void updateMaterialColor(Spatial spatial, ColorRGBA colorRGBA) {

        SceneGraphVisitor sceneGraphVisitor = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spatial) {
                if (spatial instanceof Geometry) {
                    Geometry geometry = ((Geometry) spatial);
                    if (geometry.getMaterial() != null) {
                        Material material = geometry.getMaterial();
                        if (isUnshadedMaterial(material) || isUnshadedToonMaterial(material)) {
                            material.setColor("Color", colorRGBA);

                        } else if (isLightingMaterial(material) || isShadedToonMaterial(material)) {
                            material.setColor("Diffuse", colorRGBA);

                        } else if (isMatCapMaterial(material)) {
                            material.setColor("Multiply_Color", colorRGBA);

                        } else if (isPBRLightingMaterial(material)) {
                            material.setColor("BaseColor", colorRGBA);

                        }

                    }

                }

            }
        };
        spatial.depthFirstTraversal(sceneGraphVisitor);

    }

    public static Material createShadelessMaterial(AssetManager assetManager, ColorRGBA colorRGBA) {
//    Common/MatDefs/Light/Lighting.j3md
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");  // create a simple material
        mat.setColor("Color", colorRGBA);   // set color of material to blue
        return mat;
    }

    public static Material createMaterial(AssetManager assetManager, ColorRGBA colorRGBA) {
        System.out.println("Create material with color: " + colorRGBA);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");  // create a simple material
        mat.setBoolean("UseMaterialColors", true);  // Set some parameters, e.g. blue.
        mat.setColor("Ambient", colorRGBA);   // ... color of this object
        mat.setColor("Diffuse", colorRGBA);   // ... color of light being reflected
        mat.setColor("Specular", ColorRGBA.White);
        mat.setColor("GlowColor", ColorRGBA.Black);
        mat.setFloat("Shininess", 2f);
        return mat;
    }

    public static Material createMaterial(AssetManager assetManager, String texture) {
        Texture texture1 = assetManager.loadTexture(texture);
        texture1.setWrap(Texture.WrapMode.Repeat);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");  // create a simple material
        mat.setBoolean("UseMaterialColors", false);  // Set some parameters, e.g. blue.
        mat.setColor("Ambient", ColorRGBA.White);   // ... color of this object
        mat.setColor("Diffuse", ColorRGBA.White);   // ... color of light being reflected
        mat.setColor("Specular", ColorRGBA.White);
        mat.setColor("GlowColor", ColorRGBA.Black);
        mat.setFloat("Shininess", 2f);
        mat.setTexture("DiffuseMap", texture1);
        return mat;
    }
    
    public static Material createGrassMaterial(AssetManager assetManager, String texture, float windStrength, Vector2f windDirection) {
        Texture texture1 = assetManager.loadTexture(texture);
        
        Material mat = new Material(assetManager, "Resources/MatDefs/Grass.j3md");  // create a simple material
        mat.setVector2("WindDirection", windDirection);
        mat.setFloat("WindStrength", windStrength);
//        mat.setTransparent(true);

//        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");  // create a simple material
        texture1.setWrap(Texture.WrapAxis.S, Texture.WrapMode.Repeat);
        mat.setTexture("DiffuseMap", texture1);

        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
//        mat.setBoolean("BackfaceShadows", true);
//        mat.setBoolean("VertexLighting", true);
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Ambient", ColorRGBA.White);   // ... color of this object
        mat.setColor("Diffuse", ColorRGBA.White);   // ... color of light being reflected
        mat.setColor("Specular", ColorRGBA.White);
        mat.setColor("GlowColor", ColorRGBA.Black);
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        mat.setFloat("AlphaDiscardThreshold", 0.55f);
        
        return mat;
    }    

    public static Material createUnshadedCartoonMaterial(AssetManager assetManager, ColorRGBA colorRGBA, ColorRGBA edgeColorRGBA, float edgeSize) {
        System.out.println("Create toon material with color: " + colorRGBA);
        Material mat = new Material(assetManager, "MatDefs/UnshadedToon.j3md");  // create a simple material
        mat.setColor("Color", colorRGBA);   // ... color of this object
        mat.setColor("EdgesColor", edgeColorRGBA);
        mat.setFloat("EdgeSize", edgeSize);
        mat.setBoolean("Fog_Edges", true);
        mat.setBoolean("Toon", true);

        return mat;
    }

    public static Material createShadedCartoonMaterial(AssetManager assetManager, ColorRGBA colorRGBA, ColorRGBA edgeColorRGBA, float edgeSize) {
        System.out.println("Create toon material with color: " + colorRGBA);
        Material mat = new Material(assetManager, "MatDefs/LightBlow.j3md");  // create a simple material
        mat.setColor("Diffuse", colorRGBA);   // ... color of this object
        mat.setBoolean("UseMaterialColors", true);
        mat.setBoolean("Multiply_Color", true);

        mat.setColor("EdgesColor", edgeColorRGBA);
        mat.setFloat("EdgeSize", edgeSize);
        mat.setBoolean("Fog_Edges", true);
        mat.setBoolean("Toon", true);

        return mat;
    }

    public static Material createMatCapMaterial(AssetManager assetManager, ColorRGBA colorRGBA, ColorRGBA edgeColorRGBA, float edgeSize) {
        System.out.println("Create matcap material with color: " + colorRGBA);
        Material mat = new Material(assetManager, "MatDefs/MatCap.j3md");  // create a simple material

        mat.setColor("Multiply_Color", colorRGBA);
        Texture texture = assetManager.loadTexture("Textures/MatCap/matcapbronse.jpg");
        if (texture != null) {
            mat.setTexture("DiffuseMap", texture);
        }

        mat.setColor("EdgesColor", edgeColorRGBA);
        mat.setFloat("EdgeSize", edgeSize);
        mat.setBoolean("Fog_Edges", true);
        mat.setBoolean("Toon", true);

        return mat;
    }

    public static Material createPBRMaterial(AssetManager assetManager) {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");  // create a simple material
        mat.setColor("BaseColor", ColorRGBA.Black);
        mat.setFloat("Metallic", 0.5f);
        mat.setFloat("Roughness", 0.5f);
        return mat;
    }

    public static void updateSpatialMaterial(Spatial spatial, Material material) {

        SceneGraphVisitor sceneGraphVisitor = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spatial) {
                spatial.setMaterial(material);

            }
        };
        spatial.depthFirstTraversal(sceneGraphVisitor);

    }

    public static void convertTextureToEmbeddedByName(Material material, String textureName) {
        MatParamTexture matParam = material.getTextureParam(textureName);
        System.out.println("\t\tGET TEXTURE BY NAME: " + textureName);
        if (matParam != null && matParam.getTextureValue() != null) {
            matParam.getTextureValue().setKey(null);
            System.out.println("Converted texture of name: " + textureName);

        }
    }
    
    public static void convertTextureToDepthRendering(Material material, String textureName) {
        MatParamTexture matParam = material.getTextureParam(textureName);
        
        if (matParam != null && matParam.getTextureValue() != null) {
            System.out.println("\t\tGOT TEXTURE BY NAME: " + textureName);
            Texture texture = matParam.getTextureValue();
            texture.setMagFilter(Texture.MagFilter.Bilinear);
            texture.setMinFilter(Texture.MinFilter.BilinearNearestMipMap);

        }
    }

    public static void convertTexturesToEmbedded(Spatial s) {

        SceneGraphVisitor sceneGraphVisitor = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spatial) {
                if (spatial instanceof Geometry) {
                    System.out.println("convertTexturesToEmbedded.....:" + spatial.getName());
                    Material material = ((Geometry) spatial).getMaterial();
                    System.out.println("\tMaterial...: " + material.getMaterialDef().getAssetName());
                    if (isLightingMaterial(material)) {
                        convertTextureToEmbeddedByName(material, "DiffuseMap");

                    } else if (isUnshadedMaterial(material)) {
                        convertTextureToEmbeddedByName(material, "ColorMap");

                    } else if (isPBRLightingMaterial(material)) {
                        convertTextureToEmbeddedByName(material, "MetallicRoughnessMap");
                        convertTextureToEmbeddedByName(material, "MetallicMap");
                        convertTextureToEmbeddedByName(material, "RoughnessMap");                        
                        convertTextureToEmbeddedByName(material, "NormalMap");
                        convertTextureToEmbeddedByName(material, "SpecularMap");
                        convertTextureToEmbeddedByName(material, "BaseColorMap");
                        convertTextureToEmbeddedByName(material, "EmissiveMap");
                        convertTextureToEmbeddedByName(material, "LightMap");
                        convertTextureToEmbeddedByName(material, "GlossinessMap");
                        convertTextureToEmbeddedByName(material, "SpecularGlossinessMap");
                        convertTextureToEmbeddedByName(material, "ParallaxMap");
                        convertTextureToEmbeddedByName(material, "ShadowMap0");
                        convertTextureToEmbeddedByName(material, "ShadowMap1");
                        convertTextureToEmbeddedByName(material, "ShadowMap2");
                        convertTextureToEmbeddedByName(material, "ShadowMap3");
                        convertTextureToEmbeddedByName(material, "ShadowMap4");
                        convertTextureToEmbeddedByName(material, "ShadowMap5");
                        convertTextureToEmbeddedByName(material, "ShadowMap6");
                        convertTextureToEmbeddedByName(material, "ShadowMap7");
                                
                        
//                        System.out.println("Metalicness: " + material.getParamValue("BaseColor"));
//                        material.setTexture("MetallicRoughnessMap", null);
//                        material.setFloat("Roughness", 0f);

                    }

                }
            }
        };
        s.depthFirstTraversal(sceneGraphVisitor);

    }
    
    public static void convertTexturesToDepthRendering(Spatial s) {

        SceneGraphVisitor sceneGraphVisitor = new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spatial) {
                if (spatial instanceof Geometry) {
                    System.out.println("convertTexturesToEmbedded.....:" + spatial.getName());
                    Material material = ((Geometry) spatial).getMaterial();
                    System.out.println("\tMaterial...: " + material.getMaterialDef().getAssetName());
                    if (isTerrainLightingMaterial(material)) {
                        convertTextureToDepthRendering(material, "DiffuseMap");
                        convertTextureToDepthRendering(material, "NormalMap");
                        convertTextureToDepthRendering(material, "DiffuseMap_1");
                        convertTextureToDepthRendering(material, "NormalMap_1");
                        convertTextureToDepthRendering(material, "DiffuseMap_2");
                        convertTextureToDepthRendering(material, "NormalMap_2");                        
                        convertTextureToDepthRendering(material, "DiffuseMap_3");
                        convertTextureToDepthRendering(material, "NormalMap_3");                        
//                        convertTextureToDepthRendering(material, "DiffuseMap_4");
//                        convertTextureToDepthRendering(material, "NormalMap_4");
                        
                    } else if (isLightingMaterial(material)) {
                        convertTextureToEmbeddedByName(material, "DiffuseMap");

                    } else if (isUnshadedMaterial(material)) {
                        convertTextureToEmbeddedByName(material, "ColorMap");

                    } else if (isPBRLightingMaterial(material)) {
                        convertTextureToEmbeddedByName(material, "MetallicRoughnessMap");
                        convertTextureToEmbeddedByName(material, "NormalMap");
                        convertTextureToEmbeddedByName(material, "BaseColorMap");
                        convertTextureToEmbeddedByName(material, "EmissiveMap");
                        convertTextureToEmbeddedByName(material, "LightMap");

                    }

                }
            }
        };
        s.depthFirstTraversal(sceneGraphVisitor);

    }

    public static Color convertColor(ColorRGBA colorRGBA) {
        return new Color(colorRGBA.r, colorRGBA.g, colorRGBA.b, 1);
    }

    public static ColorRGBA convertColor(Color color) {
        float[] rgba = new float[4];
        rgba = color.getColorComponents(rgba);
        return new ColorRGBA(rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    public static boolean isLightingMaterial(Material material) {
        return material != null && material.getMaterialDef().getAssetName().endsWith("/Lighting.j3md");
    }

    public static boolean isUnshadedMaterial(Material material) {
        return material != null && material.getMaterialDef().getAssetName().endsWith("Unshaded.j3md");
    }

    public static boolean isUnshadedToonMaterial(Material material) {
        return material != null && material.getMaterialDef().getAssetName().endsWith("UnshadedToon.j3md");
    }

    public static boolean isShadedToonMaterial(Material material) {
        return material != null && material.getMaterialDef().getAssetName().endsWith("LightBlow.j3md");
    }

    public static boolean isMatCapMaterial(Material material) {
        return material != null && material.getMaterialDef().getAssetName().endsWith("MatCap.j3md");
    }

    public static boolean isPBRLightingMaterial(Material material) {
        return material != null && material.getMaterialDef().getAssetName().endsWith("/PBRLighting.j3md");
    }
    
    public static boolean isTerrainLightingMaterial(Material material) {
        return material != null && material.getMaterialDef().getAssetName().endsWith("/TerrainLighting.j3md");
    }
}
