package com.galago.editor.utils;

import com.galago.editor.terrain.FlatHeightmap;
import com.galago.editor.terrain.IslandHeightMap;
import com.galago.editor.ui.actions.TerrainAction;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.system.JmeSystem;
import com.jme3.terrain.geomipmap.TerrainGrid;
import com.jme3.terrain.geomipmap.TerrainGridLodControl;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.grid.FractalTileLoader;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.terrain.heightmap.MidpointDisplacementHeightMap;
import com.jme3.terrain.noise.ShaderUtils;
import com.jme3.terrain.noise.basis.FilteredBasis;
import com.jme3.terrain.noise.filter.IterativeFilter;
import com.jme3.terrain.noise.filter.OptimizedErode;
import com.jme3.terrain.noise.filter.PerturbFilter;
import com.jme3.terrain.noise.filter.SmoothFilter;
import com.jme3.terrain.noise.fractal.FractalSum;
import com.jme3.terrain.noise.modulator.NoiseModulator;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author ndebruyn
 */
public class TerrainUtils {

    public static final String DEFAULT_TERRAIN_TEXTURE = "Textures/terrain/dirt_rock.jpg";
    public static final String DEFAULT_TERRAIN_TEXTURE_NORMAL = "Textures/terrain/dirt_rock_normal.png";

    public static final String L1_TERRAIN_TEXTURE = "Textures/terrain/grass.jpg";
    public static final String L1_TERRAIN_TEXTURE_NORMAL = "Textures/terrain/grass_normal.png";

    public static final String L2_TERRAIN_TEXTURE = "Textures/terrain/dirt.jpg";
    public static final String L2_TERRAIN_TEXTURE_NORMAL = "Textures/terrain/dirt_normal.png";

    public static final String L3_TERRAIN_TEXTURE = "Textures/terrain/dirt.jpg";
    public static final String L3_TERRAIN_TEXTURE_NORMAL = "Textures/terrain/dirt_normal.png";

    public static final float DEFAULT_TEXTURE_SCALE = 64f / 100f;
    public static final int NUM_ALPHA_TEXTURES = 3;
    protected final int MAX_DIFFUSE = 12;
    protected final int MAX_TEXTURES = 16 - NUM_ALPHA_TEXTURES; // 16 max (diffuse and normal), minus the ones we are reserving

    private static Vector2f tempVec = new Vector2f();

    public static TerrainGrid generateTerrainGrid(AssetManager assetManager, Camera camera) {

        FractalSum base = new FractalSum();
        base.setRoughness(0.75f);
        base.setFrequency(1.0f);
        base.setAmplitude(1.0f);
        base.setLacunarity(2.12f);
        base.setOctaves(8);
        base.setScale(0.02125f);
        base.addModulator(new NoiseModulator() {

            @Override
            public float value(float... in) {
                return ShaderUtils.clamp(in[0] * 0.5f + 0.5f, 0, 1);
            }
        });

        FilteredBasis ground = new FilteredBasis(base);

        PerturbFilter perturb = new PerturbFilter();
        perturb.setMagnitude(0.219f);

        OptimizedErode therm = new OptimizedErode();
        therm.setRadius(5);
        therm.setTalus(0.011f);

        SmoothFilter smooth = new SmoothFilter();
        smooth.setRadius(2);
        smooth.setEffect(0.7f);

        IterativeFilter iterate = new IterativeFilter();
        iterate.addPreFilter(perturb);
        iterate.addPostFilter(smooth);
        iterate.setFilter(therm);
        iterate.setIterations(1);

        ground.addPreFilter(iterate);

        TerrainGrid terrain
                = new TerrainGrid("terrain", 33, 129, new FractalTileLoader(ground, 256f));
        terrain.setMaterial(generateLitHeightBasedMaterial(assetManager, terrain.getTerrainSize()));
        terrain.setLocalTranslation(0, 0, 0);
        terrain.setLocalScale(3f, 1.5f, 3f);

        TerrainLodControl control = new TerrainGridLodControl(terrain, camera);
        control.setLodCalculator(new DistanceLodCalculator(33, 2.7f)); // patch size, and a multiplier
        terrain.addControl(control);

        return terrain;
    }

    public static Material generateHeightBasedMaterial(AssetManager assetManager) {

        final float grassScale = 32;
        final float dirtScale = 32;
        final float rockScale = 34;

        // TERRAIN TEXTURE material
        Material mat_terrain = new Material(assetManager, "Common/MatDefs/Terrain/HeightBasedTerrain.j3md");
        // Parameters to material:
        // regionXColorMap: X = 1..4 the texture that should be applied to state X
        // regionX: a Vector3f containing the following information:
        //      regionX.x: the start height of the region
        //      regionX.y: the end height of the region
        //      regionX.z: the texture scale for the region
        //  it might not be the most elegant way for storing these 3 values, but it packs the data nicely :)
        // slopeColorMap: the texture to be used for cliffs, and steep mountain sites
        // slopeTileFactor: the texture scale for slopes
        // terrainSize: the total size of the terrain (used for scaling the texture)
        // GRASS texture
        Texture grass = assetManager.loadTexture("Textures/terrain/grass.jpg");
        grass.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("region1ColorMap", grass);
        mat_terrain.setVector3("region1", new Vector3f(50, 100, grassScale));

        // DIRT texture
        Texture dirt = assetManager.loadTexture("Textures/terrain/dirt.jpg");
        dirt.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("region2ColorMap", dirt);
        mat_terrain.setVector3("region2", new Vector3f(0, 51, dirtScale));

        // ROCK texture
        Texture rock = assetManager.loadTexture("Textures/terrain/rock.jpg");
        rock.setWrap(Texture.WrapMode.Repeat);
        mat_terrain.setTexture("region3ColorMap", rock);
        mat_terrain.setVector3("region3", new Vector3f(99, 255, rockScale));

//    Texture lava = assetManager.loadTexture("Textures/terrain/Lava Cracks.jpg");
//    mat_terrain.setTexture("region4ColorMap", lava);
//    mat_terrain.setVector3("region4", new Vector3f(115, 255, rockScale));
        mat_terrain.setTexture("slopeColorMap", rock);
        mat_terrain.setFloat("slopeTileFactor", 6);

        mat_terrain.setFloat("terrainSize", 1025);

        return mat_terrain;
    }

    public static Material generateLitHeightBasedMaterial(AssetManager assetManager, int size) {

        final float scale = 124;

        int sizePlusOne = size + 1;

        Material terrainMaterial = new Material(assetManager, "MatDefs/HeightBasedTerrainLighting.j3md");

        Texture tex_region1 = assetManager.loadTexture("Textures/terrain/dirt.jpg");
        tex_region1.setWrap(Texture.WrapMode.Repeat);

        Texture tex_region2 = assetManager.loadTexture("Textures/terrain/rock.jpg");
        tex_region2.setWrap(Texture.WrapMode.Repeat);

        Texture tex_region3 = assetManager.loadTexture("Textures/terrain/grass.jpg");
        tex_region3.setWrap(Texture.WrapMode.Repeat);

        Texture tex_region4 = assetManager.loadTexture("Textures/terrain/rock.jpg");
        tex_region4.setWrap(Texture.WrapMode.Repeat);

        terrainMaterial.setTexture("DiffuseMap", tex_region1);
        terrainMaterial.setVector3("region1", new Vector3f(-32, 0, scale));

        terrainMaterial.setTexture("DiffuseMap_1", tex_region2);
        terrainMaterial.setVector3("region2", new Vector3f(0, 10, scale));

        terrainMaterial.setTexture("DiffuseMap_2", tex_region3);
        terrainMaterial.setVector3("region3", new Vector3f(8, 20, scale));

        terrainMaterial.setTexture("DiffuseMap_3", tex_region4);
        terrainMaterial.setVector3("region4", new Vector3f(16, 128, scale));

        // slope
        Texture rock = assetManager.loadTexture("Textures/terrain/rock.jpg");
        rock.setWrap(Texture.WrapMode.Repeat);

        terrainMaterial.setTexture("SlopeDiffuseMap", rock);
        terrainMaterial.setFloat("slopeTileFactor", 28f);

        terrainMaterial.setFloat("terrainSize", sizePlusOne);

        return terrainMaterial;
    }

    public static Material generatePaintableTerrainMaterial(AssetManager assetManager, int alphaTextureSize) throws IOException {
        Material mat = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");

        String assetFolder = JmeSystem.getStorageFolder().getAbsolutePath();
        System.out.println("Asset Folder = " + assetFolder);
        assetManager.registerLocator(assetFolder, FileLocator.class);

        // write out 3 alpha blend images
        for (int i = 0; i < NUM_ALPHA_TEXTURES; i++) {
            BufferedImage alphaBlend = new BufferedImage(alphaTextureSize, alphaTextureSize, BufferedImage.TYPE_INT_ARGB);
            if (i == 0) {
                // the first alpha level should be opaque so we see the first texture over the whole terrain
                for (int h = 0; h < alphaTextureSize; h++) {
                    for (int w = 0; w < alphaTextureSize; w++) {
                        alphaBlend.setRGB(w, h, 0x00FF0000);//argb
                    }
                }
            }

            String alphaBlendFileName = "/terrain-alpha.png";
            File alphaImageFile = new File(assetFolder + alphaBlendFileName);
            ImageIO.write(alphaBlend, "png", alphaImageFile);
            Texture tex = assetManager.loadAsset(new TextureKey(alphaBlendFileName, false));
            switch (i) {
                case 0:
                    mat.setTexture("AlphaMap", tex);
                    MaterialUtils.convertTextureToEmbeddedByName(mat, "AlphaMap");
                    break;
                case 1:
                    mat.setTexture("AlphaMap_1", tex);
                    MaterialUtils.convertTextureToEmbeddedByName(mat, "AlphaMap_1");
                    break;
                case 2:
                    mat.setTexture("AlphaMap_2", tex);
                    MaterialUtils.convertTextureToEmbeddedByName(mat, "AlphaMap_2");
                    break;
                default:
                    break;
            }

        }

        //Base Image
        Texture defaultTexture = assetManager.loadTexture(DEFAULT_TERRAIN_TEXTURE);
        defaultTexture.setWrap(Texture.WrapMode.Repeat);
        Texture normalTexture = assetManager.loadTexture(DEFAULT_TERRAIN_TEXTURE_NORMAL);
        normalTexture.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("DiffuseMap", defaultTexture);
        mat.setTexture("NormalMap", normalTexture);
        mat.setFloat("DiffuseMap_0_scale", DEFAULT_TEXTURE_SCALE);
        mat.setBoolean("WardIso", true);
        mat.setFloat("Shininess", 1f);
        MaterialUtils.convertTextureToEmbeddedByName(mat, "DiffuseMap");
        MaterialUtils.convertTextureToEmbeddedByName(mat, "NormalMap");

        //Layer1 Image
        defaultTexture = assetManager.loadTexture(L1_TERRAIN_TEXTURE);
        defaultTexture.setWrap(Texture.WrapMode.Repeat);
        normalTexture = assetManager.loadTexture(L1_TERRAIN_TEXTURE_NORMAL);
        normalTexture.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("DiffuseMap_1", defaultTexture);
        mat.setTexture("NormalMap_1", normalTexture);
        mat.setFloat("DiffuseMap_1_scale", DEFAULT_TEXTURE_SCALE);
        MaterialUtils.convertTextureToEmbeddedByName(mat, "DiffuseMap_1");
        MaterialUtils.convertTextureToEmbeddedByName(mat, "NormalMap_1");

        //Layer2 Image
        defaultTexture = assetManager.loadTexture(L2_TERRAIN_TEXTURE);
        defaultTexture.setWrap(Texture.WrapMode.Repeat);
        normalTexture = assetManager.loadTexture(L2_TERRAIN_TEXTURE_NORMAL);
        normalTexture.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("DiffuseMap_2", defaultTexture);
        mat.setTexture("NormalMap_2", normalTexture);
        mat.setFloat("DiffuseMap_2_scale", DEFAULT_TEXTURE_SCALE);
        MaterialUtils.convertTextureToEmbeddedByName(mat, "DiffuseMap_2");
        MaterialUtils.convertTextureToEmbeddedByName(mat, "NormalMap_2");

        //Layer3 Image
        defaultTexture = assetManager.loadTexture(L3_TERRAIN_TEXTURE);
        defaultTexture.setWrap(Texture.WrapMode.Repeat);
        normalTexture = assetManager.loadTexture(L3_TERRAIN_TEXTURE_NORMAL);
        normalTexture.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("DiffuseMap_3", defaultTexture);
        mat.setTexture("NormalMap_3", normalTexture);
        mat.setFloat("DiffuseMap_3_scale", DEFAULT_TEXTURE_SCALE);
        MaterialUtils.convertTextureToEmbeddedByName(mat, "DiffuseMap_3");
        MaterialUtils.convertTextureToEmbeddedByName(mat, "NormalMap_3");

        mat.setBoolean("useTriPlanarMapping", true);

        return mat;
    }

    public static Material generatePaintablePBRTerrainMaterial(AssetManager assetManager, int alphaTextureSize) throws IOException {
        Material mat = new Material(assetManager, "Common/MatDefs/Terrain/PBRTerrain.j3md");

        String assetFolder = JmeSystem.getStorageFolder().getAbsolutePath();
        System.out.println("Asset Folder = " + assetFolder);
        assetManager.registerLocator(assetFolder, FileLocator.class);

        // write out 3 alpha blend images
        for (int i = 0; i < NUM_ALPHA_TEXTURES; i++) {
            BufferedImage alphaBlend = new BufferedImage(alphaTextureSize, alphaTextureSize, BufferedImage.TYPE_INT_ARGB);
            if (i == 0) {
                // the first alpha level should be opaque so we see the first texture over the whole terrain
                for (int h = 0; h < alphaTextureSize; h++) {
                    for (int w = 0; w < alphaTextureSize; w++) {
                        alphaBlend.setRGB(w, h, 0x00FF0000);//argb
                    }
                }
            }

            String alphaBlendFileName = "/terrain-alpha.png";
            File alphaImageFile = new File(assetFolder + alphaBlendFileName);
            ImageIO.write(alphaBlend, "png", alphaImageFile);
            Texture tex = assetManager.loadAsset(new TextureKey(alphaBlendFileName, false));
            switch (i) {
                case 0:
                    mat.setTexture("AlphaMap", tex);
                    MaterialUtils.convertTextureToEmbeddedByName(mat, "AlphaMap");
                    break;
                case 1:
                    mat.setTexture("AlphaMap_1", tex);
                    MaterialUtils.convertTextureToEmbeddedByName(mat, "AlphaMap_1");
                    break;
                case 2:
                    mat.setTexture("AlphaMap_2", tex);
                    MaterialUtils.convertTextureToEmbeddedByName(mat, "AlphaMap_2");
                    break;
                default:
                    break;
            }

        }

        //Base Image
        Texture defaultTexture = assetManager.loadTexture(DEFAULT_TERRAIN_TEXTURE);
        defaultTexture.setWrap(Texture.WrapMode.Repeat);
        Texture normalTexture = assetManager.loadTexture(DEFAULT_TERRAIN_TEXTURE_NORMAL);
        normalTexture.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("AlbedoMap_0", defaultTexture);
        mat.setTexture("NormalMap_0", normalTexture);
        mat.setFloat("AlbedoMap_0_scale", DEFAULT_TEXTURE_SCALE);
        mat.setFloat("Roughness_0", 0.6f);
        mat.setFloat("Metallic_0", 0.2f);
        MaterialUtils.convertTextureToEmbeddedByName(mat, "AlbedoMap_0");
        MaterialUtils.convertTextureToEmbeddedByName(mat, "NormalMap_0");

        //Layer1 Image
        defaultTexture = assetManager.loadTexture(L1_TERRAIN_TEXTURE);
        defaultTexture.setWrap(Texture.WrapMode.Repeat);
        normalTexture = assetManager.loadTexture(L1_TERRAIN_TEXTURE_NORMAL);
        normalTexture.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("AlbedoMap_1", defaultTexture);
        mat.setTexture("NormalMap_1", normalTexture);
        mat.setFloat("AlbedoMap_1_scale", DEFAULT_TEXTURE_SCALE);
        mat.setFloat("Roughness_1", 1);
        mat.setFloat("Metallic_1", 0);
        MaterialUtils.convertTextureToEmbeddedByName(mat, "AlbedoMap_1");
        MaterialUtils.convertTextureToEmbeddedByName(mat, "NormalMap_1");

        //Layer2 Image
        defaultTexture = assetManager.loadTexture(L2_TERRAIN_TEXTURE);
        defaultTexture.setWrap(Texture.WrapMode.Repeat);
        normalTexture = assetManager.loadTexture(L2_TERRAIN_TEXTURE_NORMAL);
        normalTexture.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("AlbedoMap_2", defaultTexture);
        mat.setTexture("NormalMap_2", normalTexture);
        mat.setFloat("AlbedoMap_2_scale", DEFAULT_TEXTURE_SCALE);
        mat.setFloat("Roughness_2", 1);
        mat.setFloat("Metallic_2", 0);
        MaterialUtils.convertTextureToEmbeddedByName(mat, "AlbedoMap_2");
        MaterialUtils.convertTextureToEmbeddedByName(mat, "NormalMap_2");

        //Layer3 Image
        defaultTexture = assetManager.loadTexture(L3_TERRAIN_TEXTURE);
        defaultTexture.setWrap(Texture.WrapMode.Repeat);
        normalTexture = assetManager.loadTexture(L3_TERRAIN_TEXTURE_NORMAL);
        normalTexture.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("AlbedoMap_3", defaultTexture);
        mat.setTexture("NormalMap_3", normalTexture);
        mat.setFloat("AlbedoMap_3_scale", DEFAULT_TEXTURE_SCALE);
        mat.setFloat("Roughness_3", 1);
        mat.setFloat("Metallic_3", 0);
        MaterialUtils.convertTextureToEmbeddedByName(mat, "AlbedoMap_3");
        MaterialUtils.convertTextureToEmbeddedByName(mat, "NormalMap_3");

        mat.setBoolean("useTriPlanarMapping", true);

        return mat;
    }

    /**
     * Size must only be 256/512/1024
     *
     * @param assetManager
     * @param camera
     * @param size (256/512/1024)
     * @param iterations (2000)
     * @param minRadius (10)
     * @param maxRadius (60)
     * @param seed (random)
     * @return
     */
    public static TerrainQuad generateIslandTerrain(AssetManager assetManager, Camera camera,
            int size, int iterations, float minRadius,
            float maxRadius, long seed) {
        // CREATE HEIGHTMAP
        AbstractHeightMap heightmap = null;
        TerrainQuad terrain = null;

        int sizePlusOne = size + 1;

        //GENERATE THE HEIGHTMAP
        try {
            heightmap = new IslandHeightMap(sizePlusOne, iterations, minRadius, maxRadius, seed);
//            heightmap = new IslandHeightMap(1025, 2000, 20, 90, seed);
//      heightmap.erodeTerrain();
//      heightmap = new ParticleDepositionHeightMap(1025, 1000, 10, 5, 10, 0.1f );
//      heightmap = new MidpointDisplacementHeightMap(1025, 200f, 0.4f, seed);

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
     * Here we create the actual terrain. The tiles will be 65x65, and the total size of the
     * terrain will be 513x513. It uses the heightmap we created to generate the height values.
         */
 /*
     * Optimal terrain patch size is 65 (64x64).
     * The total size is up to you. At 1025, it ran fine for me (200+FPS), however at
     * size=2049, it got really slow. But that is a jump from 2 million to 8 million triangles...
         */
        terrain = new TerrainQuad("terrain", 65, sizePlusOne, heightmap.getHeightMap());
        TerrainLodControl control = new TerrainLodControl(terrain, camera);
        control.setLodCalculator(new DistanceLodCalculator(65, 2.7f)); // patch size, and a multiplier
        terrain.addControl(control);
//    terrain.setMaterial(generateHeightBasedMaterial(assetManager));
//    terrain.setMaterial(generateLitHeightBasedMaterial(assetManager));
//    terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(1f, 0.25f, 1f);

        return terrain;
    }

    public static TerrainQuad generateFlatTerrain(AssetManager assetManager, Camera camera, int size) {
        // CREATE HEIGHTMAP
        AbstractHeightMap heightmap = null;
        TerrainQuad terrain = null;

        int sizePlusOne = size + 1;

        //GENERATE THE HEIGHTMAP
        try {
            heightmap = new FlatHeightmap(sizePlusOne);
//            heightmap = new IslandHeightMap(1025, 2000, 20, 90, seed);
//      heightmap.erodeTerrain();
//      heightmap = new ParticleDepositionHeightMap(1025, 1000, 10, 5, 10, 0.1f );
//      heightmap = new MidpointDisplacementHeightMap(1025, 200f, 0.4f, seed);

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
     * Here we create the actual terrain. The tiles will be 65x65, and the total size of the
     * terrain will be 513x513. It uses the heightmap we created to generate the height values.
         */
 /*
     * Optimal terrain patch size is 65 (64x64).
     * The total size is up to you. At 1025, it ran fine for me (200+FPS), however at
     * size=2049, it got really slow. But that is a jump from 2 million to 8 million triangles...
         */
        terrain = new TerrainQuad("terrain", 65, sizePlusOne, heightmap.getHeightMap());
        TerrainLodControl control = new TerrainLodControl(terrain, camera);
        control.setLodCalculator(new DistanceLodCalculator(65, 2.7f)); // patch size, and a multiplier
        terrain.addControl(control);
//    terrain.setMaterial(generateHeightBasedMaterial(assetManager));
//    terrain.setMaterial(generateLitHeightBasedMaterial(assetManager));
//    terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(1f, 0.25f, 1f);

        return terrain;
    }

    public static TerrainQuad generateMidpointTerrain(AssetManager assetManager, Camera camera,
            int size, float range, float persistence, long seed) {
        // CREATE HEIGHTMAP
        AbstractHeightMap heightmap = null;
        TerrainQuad terrain = null;

        int sizePlusOne = size + 1;

        //GENERATE THE HEIGHTMAP
        try {
            heightmap = new MidpointDisplacementHeightMap(sizePlusOne, range, persistence, seed);
//            heightmap = new IslandHeightMap(1025, 2000, 20, 90, seed);
//      heightmap.erodeTerrain();
//      heightmap = new ParticleDepositionHeightMap(1025, 1000, 10, 5, 10, 0.1f );
//      heightmap = new MidpointDisplacementHeightMap(1025, 200f, 0.4f, seed);

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
     * Here we create the actual terrain. The tiles will be 65x65, and the total size of the
     * terrain will be 513x513. It uses the heightmap we created to generate the height values.
         */
 /*
     * Optimal terrain patch size is 65 (64x64).
     * The total size is up to you. At 1025, it ran fine for me (200+FPS), however at
     * size=2049, it got really slow. But that is a jump from 2 million to 8 million triangles...
         */
        terrain = new TerrainQuad("terrain", 65, sizePlusOne, heightmap.getHeightMap());
        TerrainLodControl control = new TerrainLodControl(terrain, camera);
        control.setLodCalculator(new DistanceLodCalculator(65, 2.7f)); // patch size, and a multiplier
        terrain.addControl(control);
//    terrain.setMaterial(generateHeightBasedMaterial(assetManager));
//    terrain.setMaterial(generateLitHeightBasedMaterial(assetManager));
//    terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(1f, 0.25f, 1f);

        return terrain;
    }

    public static TerrainQuad generateImageTerrain(AssetManager assetManager, Camera camera, Image image) {
        // CREATE HEIGHTMAP
        AbstractHeightMap heightmap = null;
        TerrainQuad terrain = null;
        int size = 256;

        if (image.getWidth() < image.getHeight()) {
            size = image.getWidth();
        } else {
            size = image.getHeight();
        }

        int sizePlusOne = size + 1;

        System.out.println("GENERATE IMAGE HEIGHTMAP: " + image.getHeight());

        //GENERATE THE HEIGHTMAP
        try {
            heightmap = new ImageBasedHeightMap(image, 1);
            heightmap.load();

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
     * Here we create the actual terrain. The tiles will be 65x65, and the total size of the
     * terrain will be 513x513. It uses the heightmap we created to generate the height values.
         */
 /*
     * Optimal terrain patch size is 65 (64x64).
     * The total size is up to you. At 1025, it ran fine for me (200+FPS), however at
     * size=2049, it got really slow. But that is a jump from 2 million to 8 million triangles...
         */
        terrain = new TerrainQuad("terrain", 65, sizePlusOne, heightmap.getHeightMap());
        TerrainLodControl control = new TerrainLodControl(terrain, camera);
        control.setLodCalculator(new DistanceLodCalculator(65, 2.7f)); // patch size, and a multiplier
        terrain.addControl(control);
//    terrain.setMaterial(generateHeightBasedMaterial(assetManager));
//    terrain.setMaterial(generateLitHeightBasedMaterial(assetManager));
//    terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(1f, 0.25f, 1f);

        return terrain;
    }

    /**
     * See if the X,Y coordinate is in the radius of the circle. It is assumed
     * that the "grid" being tested is located at 0,0 and its dimensions are
     * 2*radius.
     *
     * @param x
     * @param z
     * @param radius
     * @return
     */
    public static boolean isInRadius(float x, float y, float radius) {
        Vector2f point = new Vector2f(x, y);
        // return true if the distance is less than equal to the radius
        return Math.abs(point.length()) <= radius;
    }

    /**
     * See if the X,Y coordinate is inside the box. It is assumed that the
     * "grid" being tested is located at 0,0 and its dimensions are 2*radius.
     *
     * @param x
     * @param z
     * @param radius
     * @return
     */
    public static boolean isInBox(final float x, final float y, final float radius) {
        return Math.abs(x) <= Math.abs(radius) && Math.abs(y) <= Math.abs(radius);
    }

    /**
     * Based on the mesh type, chooses the proper way to see if the point is
     * inside the marker mesh.It is assumed that the "grid" being tested is
     * located at 0,0 and its dimensions are 2*radius.
     *
     * @param x
     * @param y
     * @param radius
     * @param mesh
     * @return
     */
    public static boolean isInMesh(float x, float y, float radius, TerrainRaiseTool.Meshes mesh) {
        switch (mesh) {
            case Box:
                return isInBox(x, y, radius);
            case Sphere:
                return isInRadius(x, y, radius);
            default:
                throw new IllegalArgumentException("Unkown mesh type " + mesh);
        }
    }

    /**
     * Interpolate the height value based on its distance from the center (how
     * far along the radius it is). The farther from the center, the less the
     * height will be. This produces a linear height falloff.
     *
     * @param radius of the tool
     * @param heightFactor potential height value to be adjusted
     * @param x location
     * @param z location
     * @return the adjusted height value
     */
    public static float calculateHeight(float radius, float heightFactor, float x, float z) {
        float val = calculateRadiusPercent(radius, x, z);
        return heightFactor * val;
    }

    public static float calculateRadiusPercent(float radius, float x, float z) {
        // find percentage for each 'unit' in radius
        Vector2f point = new Vector2f(x, z);
        float val = Math.abs(point.length()) / radius;
        val = 1f - val;
        return val;
    }

    public static int compareFloat(float a, float b, float epsilon) {
        if (floatEquals(a, b, epsilon)) {
            return 0;
        } else if (floatLessThan(a, b, epsilon)) {
            return -1;
        } else {
            return 1;
        }
    }

    public static boolean floatEquals(float a, float b, float epsilon) {
        return a == b ? true : Math.abs(a - b) < epsilon;
    }

    public static boolean floatLessThan(float a, float b, float epsilon) {
        return b - a > epsilon;
    }

    public static boolean floatGreaterThan(float a, float b, float epsilon) {
        return a - b > epsilon;
    }

    public static void updateVegetationBatches(TerrainQuad terrain, Vector3f worldLoc, float radius) {

        BatchNode grass1Node = (BatchNode) terrain.getChild(TerrainAction.BATCH_GRASS1);
        grass1Node.depthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Geometry geom) {

                if (worldLoc.distance(geom.getWorldTranslation()) < radius) {
                    tempVec.set(geom.getLocalTranslation().x, geom.getLocalTranslation().z);
                    geom.setLocalTranslation(geom.getLocalTranslation().x, terrain.getHeight(tempVec), geom.getLocalTranslation().z);
                }

            }

        });

    }

}
