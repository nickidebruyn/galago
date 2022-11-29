package com.galago.editor.utils;

import com.jme3.material.MatParam;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.terrain.Terrain;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import java.nio.ByteBuffer;

/**
 *
 * @author ndebruyn
 */
public class TerrainPaintTool {

    public TerrainPaintTool() {
        
    }

    public void paintTexture(Terrain terrain, Vector3f markerLocation, float toolRadius, float toolWeight, int selectedTextureIndex) {
        if (selectedTextureIndex < 0 || markerLocation == null) {
            return;
        }

        int alphaIdx = selectedTextureIndex / 4; // 4 = rgba = 4 textures
        Texture tex = getAlphaTexture(terrain, alphaIdx);
        Image image = tex.getImage();

        Vector2f UV = getPointPercentagePosition(terrain, markerLocation);

        // get the radius of the brush in pixel-percent
        float brushSize = toolRadius / (terrain.getTerrainSize() * ((Node) terrain).getWorldScale().x);
        int texIndex = selectedTextureIndex - ((selectedTextureIndex / 4) * 4); // selectedTextureIndex/4 is an int floor, do not simplify the equation
        boolean erase = toolWeight < 0;
        if (erase) {
            toolWeight *= -1;
        }

        doPaintAction(texIndex, image, UV, true, brushSize, erase, toolWeight);

        tex.getImage().setUpdateNeeded();
    }

    public Vector2f getPointPercentagePosition(Terrain terrain, Vector3f worldLoc) {
        Vector2f uv = new Vector2f(worldLoc.x, -worldLoc.z);
        float scale = ((Node) terrain).getWorldScale().x;

        // already centered on Terrain's node origin (0,0)
        //uv.subtractLocal(((Node)terrain).getWorldTranslation().x*scale, ((Node)terrain).getWorldTranslation().z*scale); // center it on 0,0
        float scaledSize = terrain.getTerrainSize() * scale;
        uv.addLocal(scaledSize / 2, scaledSize / 2); // shift the bottom left corner up to 0,0
        uv.divideLocal(scaledSize); // get the location as a percentage

        return uv;
    }

    private Texture getAlphaTexture(Terrain terrain, int alphaLayer) {
        if (terrain == null) {
            return null;
        }
        MatParam matParam = null;
        if (alphaLayer == 0) {
            matParam = terrain.getMaterial(null).getParam("AlphaMap");
        } else if (alphaLayer == 1) {
            matParam = terrain.getMaterial(null).getParam("AlphaMap_1");
        } else if (alphaLayer == 2) {
            matParam = terrain.getMaterial(null).getParam("AlphaMap_2");
        }

        if (matParam == null || matParam.getValue() == null) {
            return null;
        }
        Texture tex = (Texture) matParam.getValue();
        return tex;
    }

    /**
     * Goes through each pixel in the image. At each pixel it looks to see if
     * the UV mouse coordinate is within the of the brush. If it is in the brush
     * radius, it gets the existing color from that pixel so it can add/subtract
     * to/from it. Essentially it does a radius check and adds in a fade value.
     * It does this to the color value returned by the first pixel color query.
     * Next it sets the color of that pixel. If it was within the radius, the
     * color will change. If it was outside the radius, then nothing will
     * change, the color will be the same; but it will set it nonetheless. Not
     * efficient.
     *
     * If the mouse is being dragged with the button down, then the dragged
     * value should be set to true. This will reduce the intensity of the brush
     * to 10% of what it should be per spray. Otherwise it goes to 100% opacity
     * within a few pixels. This makes it work a little more realistically.
     *
     * @param image to manipulate
     * @param uv the world x,z coordinate
     * @param dragged true if the mouse button is down and it is being dragged,
     * use to reduce brush intensity
     * @param radius in percentage so it can be translated to the image
     * dimensions
     * @param erase true if the tool should remove the paint instead of add it
     * @param fadeFalloff the percentage of the radius when the paint begins to
     * start fading
     */
    protected void doPaintAction(int texIndex, Image image, Vector2f uv, boolean dragged, float radius, boolean erase, float fadeFalloff) {
        Vector2f texuv = new Vector2f();
        ColorRGBA color = ColorRGBA.Black;

        float width = image.getWidth();
        float height = image.getHeight();

        int minx = (int) Math.max(0, (uv.x * width - radius * width)); // convert percents to pixels to limit how much we iterate
        int maxx = (int) Math.min(width, (uv.x * width + radius * width));
        int miny = (int) Math.max(0, (uv.y * height - radius * height));
        int maxy = (int) Math.min(height, (uv.y * height + radius * height));

        float radiusSquared = radius * radius;
        float radiusFalloff = radiusSquared * fadeFalloff;
        // go through each pixel, in the radius of the tool, in the image
        for (int y = miny; y < maxy; y++) {
            for (int x = minx; x < maxx; x++) {

                texuv.set((float) x / width, (float) y / height);// gets the position in percentage so it can compare with the mouse UV coordinate

                float dist = texuv.distanceSquared(uv);
                if (dist < radiusSquared) { // if the pixel is within the distance of the radius, set a color (distance times intensity)
                    manipulatePixel(image, x, y, color, false); // gets the color at that location (false means don't write to the buffer)

                    // calculate the fade falloff intensity
                    float intensity = (1.0f - (dist / radiusSquared)) * fadeFalloff;
                    /*if (dist > radiusFalloff) {
                        float dr = radius - radiusFalloff; // falloff to radius length
                        float d2 = dist - radiusFalloff; // dist minus falloff
                        d2 = d2/dr; // dist percentage of falloff length
                        intensity = 1-d2; // fade out more the farther away it is
                    }*/

                    //if (dragged)
                    //	intensity = intensity*0.1f; // magical divide it by 10 to reduce its intensity when mouse is dragged
                    if (erase) {
                        switch (texIndex) {
                            case 0:
                                color.r -= intensity;
                                break;
                            case 1:
                                color.g -= intensity;
                                break;
                            case 2:
                                color.b -= intensity;
                                break;
                            case 3:
                                color.a -= intensity;
                                break;
                        }
                    } else {
                        switch (texIndex) {
                            case 0:
                                color.r += intensity;
                                break;
                            case 1:
                                color.g += intensity;
                                break;
                            case 2:
                                color.b += intensity;
                                break;
                            case 3:
                                color.a += intensity;
                                break;
                        }
                    }
                    color.clamp();

                    manipulatePixel(image, x, y, color, true); // set the new color
                }

            }
        }

        image.getData(0).rewind();
    }

    /**
     * We are only using RGBA8 images for alpha textures right now.
     *
     * @param image to get/set the color on
     * @param x location
     * @param y location
     * @param color color to get/set
     * @param write to write the color or not
     */
    public void manipulatePixel(Image image, int x, int y, ColorRGBA color, boolean write) {
        ByteBuffer buf = image.getData(0);
        int width = image.getWidth();

        int position = (y * width + x) * 4;

        if (position > buf.capacity() - 1 || position < 0) {
            return;
        }

        if (write) {
            switch (image.getFormat()) {
                case RGBA8:
                    buf.position(position);
                    buf.put(float2byte(color.r))
                            .put(float2byte(color.g))
                            .put(float2byte(color.b))
                            .put(float2byte(color.a));
                    return;
                case ABGR8:
                    buf.position(position);
                    buf.put(float2byte(color.a))
                            .put(float2byte(color.b))
                            .put(float2byte(color.g))
                            .put(float2byte(color.r));
                    return;
                default:
                    throw new UnsupportedOperationException("Image format: " + image.getFormat());
            }
        } else {
            switch (image.getFormat()) {
                case RGBA8:
                    buf.position(position);
                    color.set(byte2float(buf.get()), byte2float(buf.get()), byte2float(buf.get()), byte2float(buf.get()));
                    return;
                case ABGR8:
                    buf.position(position);
                    float a = byte2float(buf.get());
                    float b = byte2float(buf.get());
                    float g = byte2float(buf.get());
                    float r = byte2float(buf.get());
                    color.set(r, g, b, a);
                    return;
                default:
                    throw new UnsupportedOperationException("Image format: " + image.getFormat());
            }
        }

    }

    private float byte2float(byte b) {
        return ((float) (b & 0xFF)) / 255f;
    }

    private byte float2byte(float f) {
        return (byte) (f * 255f);
    }
}
