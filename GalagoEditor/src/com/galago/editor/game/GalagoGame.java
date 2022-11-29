package com.galago.editor.game;

import com.jme3.asset.AssetKey;
import com.jme3.asset.CloneableSmartAsset;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.material.Material;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SafeArrayList;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ndebruyn
 */
public class GalagoGame implements Savable, Cloneable, CloneableSmartAsset {

    private File file; //This property should not be saved

    //All the following properties must be saved.
    private int buildNumber;
    private String version;
    private String name;
    private boolean saved;
    private boolean changed;
    protected SafeArrayList<Node> scenes = new SafeArrayList<>(Node.class);
    protected SafeArrayList<Spatial> models = new SafeArrayList<>(Spatial.class);
    protected SafeArrayList<Material> materials = new SafeArrayList<>(Material.class);
    protected SafeArrayList<Texture> textures = new SafeArrayList<>(Texture.class);

    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(buildNumber, "buildNumber", 1);
        capsule.write(version, "version", "v0.0.0");
        capsule.write(name, "name", "");
        capsule.write(saved, "saved", false);

        ex.getCapsule(this).writeSavableArrayList(new ArrayList(scenes), "scenes", null);
        ex.getCapsule(this).writeSavableArrayList(new ArrayList(models), "models", null);
        ex.getCapsule(this).writeSavableArrayList(new ArrayList(materials), "materials", null);
        ex.getCapsule(this).writeSavableArrayList(new ArrayList(textures), "textures", null);

    }

    public void read(JmeImporter im) throws IOException {
        scenes = new SafeArrayList(Spatial.class, im.getCapsule(this).readSavableArrayList("scenes", new ArrayList()));
        models = new SafeArrayList(Spatial.class, im.getCapsule(this).readSavableArrayList("models", new ArrayList()));
        materials = new SafeArrayList(Material.class, im.getCapsule(this).readSavableArrayList("materials", new ArrayList()));
        textures = new SafeArrayList(Texture.class, im.getCapsule(this).readSavableArrayList("textures", new ArrayList()));

        InputCapsule capsule = im.getCapsule(this);
        buildNumber = capsule.readInt("buildNumber", 1);
        version = capsule.readString("version", "v0.0.0");
        name = capsule.readString("name", "");
        saved = capsule.readBoolean("saved", false);
    }

    @Override
    public GalagoGame clone() {

        GalagoGame game = new GalagoGame();
        game.setName(name);
        game.setFile(file);
        game.setSaved(saved);
        game.setBuildNumber(buildNumber);
        game.setVersion(version);

        for (int i = 0; i < getSceneCount(); i++) {
            game.addScene(getScene(i).clone(true));
        }

        for (int i = 0; i < getModelCount(); i++) {
            game.addModel(getModel(i).clone(true));
        }

        for (int i = 0; i < getMaterialCount(); i++) {
            game.addMaterial(getMaterial(i).clone());
        }

        for (int i = 0; i < getTextureCount(); i++) {
            game.addTexture(getTexture(i).clone());
        }

        return game;

    }

    @Override
    public void setKey(AssetKey key) {

    }

    @Override
    public AssetKey getKey() {
        return null;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Node getScene(int index) {
        return scenes.get(index);
    }

    public void addScene(Node node) {
        scenes.add(node);
    }

    public int getSceneCount() {
        return scenes.size();
    }

    public void removeScene(int index) {
        scenes.remove(index);
    }

    public void removeScene(Node scene) {
        scenes.remove(scene);
    }

    public Spatial getModel(int index) {
        return models.get(index);
    }

    public void addModel(Spatial model) {
        models.add(model);
    }

    public int getModelCount() {
        return models.size();
    }

    public void removeModel(int index) {
        models.remove(index);
    }

    public void removeModel(Spatial spatial) {
        models.remove(spatial);
    }

    public Material getMaterial(int index) {
        return materials.get(index);
    }

    public void addMaterial(Material material) {
        materials.add(material);
    }

    public int getMaterialCount() {
        return materials.size();
    }

    public void removeMaterial(int index) {
        materials.remove(index);
    }

    public void removeMaterial(Material material) {
        materials.remove(material);
    }

    public Texture getTexture(int index) {
        return textures.get(index);
    }

    public void addTexture(Texture texture) {
        textures.add(texture);
    }

    public int getTextureCount() {
        return textures.size();
    }

    public void removeTexture(int index) {
        textures.remove(index);
    }

    public void removeTexture(Texture texture) {
        textures.remove(texture);
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public List<Texture> getTextures() {
        return textures;
    }
}
