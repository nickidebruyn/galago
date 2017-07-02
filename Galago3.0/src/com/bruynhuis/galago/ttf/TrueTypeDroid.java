/*
 * Feel free to use, modify, and/or distribute this source code for personal,
 * educational, commercial or any other reason you may conceive with or
 * without credit. There are absolutely no restrictions on the use,
 * modification or distribution of this code.
 */
package com.bruynhuis.galago.ttf;

import com.bruynhuis.galago.ttf.util.Style;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.table.Table;
import com.google.typography.font.sfntly.table.core.CMap;
import com.google.typography.font.sfntly.table.core.CMapTable;
import com.google.typography.font.sfntly.table.core.FontHeaderTable;
import com.google.typography.font.sfntly.table.core.HorizontalHeaderTable;
import com.google.typography.font.sfntly.table.core.HorizontalMetricsTable;
import com.google.typography.font.sfntly.table.truetype.CompositeGlyph;
import com.google.typography.font.sfntly.table.truetype.Glyph;
import com.google.typography.font.sfntly.table.truetype.Glyph.GlyphType;
import com.google.typography.font.sfntly.table.truetype.GlyphTable;
import com.google.typography.font.sfntly.table.truetype.LocaTable;
import com.google.typography.font.sfntly.table.truetype.SimpleGlyph;
import com.jme3.asset.AssetManager;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import com.jme3.util.NativeObjectManager;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.bruynhuis.galago.ttf.sfntly.AnchorTable;
import com.bruynhuis.galago.ttf.sfntly.NullAnchorTable;
import com.bruynhuis.galago.ttf.util.Glyf;

/**
 * <p>The <code>TrueTypeFont</code> class encapsulates variables and methods used to
 * create and display text created from a True Type Font(.ttf) file.
 * <code>TrueTypeDroid</code> is used for working with True Type Font files
 * while running on Android devices.</p>
 * 
 * <p>###It is imperative that you take care to provide {@link truetypefont.TTF_AtlasListener}s.
 * When the atlas is recreated to store a new character the previous atlas' underlying
 * ByteBuffers are removed from memory immediately. If you do not wish to work with
 * {@link truetypefont.TTF_AtlasListener}s then after loading the font pre-load all the
 * characters you desire to use by supplying a String with said characters to
 * {@link #getBitmapText(java.lang.String, int)} then supplying true to
 * {@link #lockAtlas(boolean)} which will prevent the atlas from loading any new characters.###</p>
 * 
 * @author Adam T. Ryder
 * <a href="http://1337atr.weebly.com">http://1337atr.weebly.com</a>
 */
public class TrueTypeDroid extends TrueTypeFont {
    private final Font font;
    
    private final CMap characterMap;
    private final LocaTable loca;
    private final GlyphTable glyphs;
    private final HorizontalMetricsTable hmtx;
    private final AnchorTable ankr;
    
    private final float pointScale;
    
    private float italic = 0;
    private float italicRef;
    
    public TrueTypeDroid(AssetManager assetManager, Font font, Style style,
            int pointSize, int outline, int screenDensity) {
        super(assetManager, pointSize, outline, screenDensity);
        
        this.font = font;
        
        CMapTable cmapTable = font.getTable(Tag.cmap);
        CMap cmap = cmapTable.cmap(Font.PlatformId.Windows.value(),
                Font.WindowsEncodingId.UnicodeUCS4.value());
        if (cmap == null) {
            characterMap = cmapTable.cmap(Font.PlatformId.Windows.value(),
                    Font.WindowsEncodingId.UnicodeUCS2.value());
        } else {
            characterMap = cmap;
        }
        
        loca = font.getTable(Tag.loca);
        glyphs = font.getTable(Tag.glyf);
        hmtx = font.getTable(Tag.hmtx);
        
        FontHeaderTable head = font.getTable(Tag.head);
        int maxX = head.xMax();
        int minX = head.xMin();
        int maxY = head.yMax();
        int minY = head.yMin();
        
        pointScale = (pointSize * dpi) / (72f * head.unitsPerEm());
        italicRef = maxY * pointScale;
        
        switch(style) {
            case Italic:
                italic = -(float)Math.sin(-0.25f) * italicRef;
                italicRef = (float)Math.cos(-0.25f) * italicRef;
                break;
            case Bold:
                bold = Math.round(((dpi / 72f) * 0.03f) * pointSize);
                break;
            case BoldItalic:
                italic = -(float)Math.sin(-0.25f) * italicRef;
                italicRef = (float)Math.cos(-0.25f) * italicRef;
                bold = Math.round(((dpi / 72f) * 0.03f) * pointSize);
        }
        
        resizeWidth = (int)Math.ceil((maxX - minX) * pointScale) + padding + bold;
        charHeight = (int)Math.ceil((maxY - minY) * pointScale) + padding + bold;
        
        HorizontalHeaderTable hhea = font.getTable(Tag.hhea);
        ascender = Math.round(hhea.ascender() * pointScale) + outline + Math.round(bold / 2f);
        descender = Math.round(-hhea.descender() * pointScale) + Math.round((outline / 2f) + (bold / 2f));
        lineGap = Math.round(hhea.lineGap() * pointScale);
        
        lineHeight = ascender + descender + lineGap;
        defaultCodePoint = '\u0000';
        
        Table t = font.getTable(Tag.intValue(new byte[]{'a', 'n', 'k', 'r'}));
        if (t != null) {
            ankr = new AnchorTable(t);
        } else
            ankr = new NullAnchorTable();
        
        getBitmapGlyphs(new StringBuilder().appendCodePoint(defaultCodePoint).append(" "));
    }
    
    public Font getFont() {
        return font;
    }
    
    /**
     * Converts an Android Bitmap to a jME Image.
     * 
     * @param bitmap The Android Bitmap to be converted, must be in
     * ARGB_8888 format.
     * @return The converted Image.
     */
    private Image droidBitmapToImage(Bitmap bitmap) {
        ByteBuffer buf = BufferUtils.createByteBuffer(bitmap.getWidth() * bitmap.getHeight() * 3);
        
        for (int y = 0; y < bitmap.getHeight(); y++){
            for (int x = 0; x < bitmap.getWidth(); x++){

                int rgb = bitmap.getPixel(x, y);
                byte r = (byte) ((rgb & 0x00FF0000) >> 16);
                byte g = (byte) ((rgb & 0x0000FF00) >> 8);
                byte b = (byte) ((rgb & 0x000000FF));
                buf.put(r).put(g).put(b);
            }
        }
        buf.flip();
        return new Image(Format.RGB8, bitmap.getWidth(), bitmap.getHeight(), buf);
    }
    
    @Override
    public boolean canDisplay(int codePoint) {
        return characterMap.glyphId(codePoint) != 0;
    }
    
    /**
     * Gets the GlyphID of a glyph associated with a particular character.
     * 
     * @param codePoint The Unicode code point of the requested character.
     * @return The ID which can be used to lookup a glyph in the truetype
     * font file.
     * 
     * @see #getGlyph(int)
     */
    public int getGlyphID(int codePoint) {
        return characterMap.glyphId(codePoint);
    }
    
    /**
     * Gets a <code>com.google.typography.font.sfntly.table.truetype.Glyph</code> from
     * the truetype font file.
     * 
     * @param glyphID The ID of the requested glyph.
     * @return The requested glyph.
     * 
     * @see #getGlyphID(int)
     */
    public Glyph getGlyph(int glyphID) {
        return glyphs.glyph(loca.glyphOffset(glyphID), loca.glyphLength(glyphID));
    }
    
    @Override
    protected void createBitmapGlyphs(List<CharToCreate> characters) {
        if (atlas == null) {
            resizeAtlas();
        }
        
        boolean added = false;
        StringBuilder sb = new StringBuilder();
        Map<Integer, Glyf> backLog = new HashMap<Integer, Glyf>();
        do {
            int line = 0;
            for (AtlasLine al : atlasLines) {
                for (Iterator<CharToCreate> it = characters.iterator(); it.hasNext();) {
                    CharToCreate ctc = it.next();
                    if (cache.containsKey(ctc.codePoint)) {
                        it.remove();
                        continue;
                    }
                    
                    sb.delete(0, sb.length());
                    sb.appendCodePoint(ctc.codePoint);
                    int gid = getGlyphID(ctc.codePoint);
                    Glyf glyf = backLog.get(ctc.codePoint);
                    if (glyf == null)
                        glyf = getContours(getGlyph(gid));
                    int w = (int)Math.ceil(glyf.maxX - glyf.minX) + padding + bold;
                    if (al.canFit(w)) {
                        float xAdvance = (hmtx.advanceWidth(gid) * pointScale) + outline + bold + Math.round(outline / 2f);
                        TrueTypeBitmapGlyph ttbg = new TrueTypeBitmapGlyph(al.getX(),
                                line * charHeight, w, glyf, xAdvance, this, ctc.codePoint, sb);
                        cache.put(ctc.codePoint, ttbg);
                        
                        added = true;
                        al.addChar(w);
                        it.remove();
                    } else
                        backLog.put(ctc.codePoint, glyf);
                }
                line++;
            }
            
            if (!characters.isEmpty()) {
                if (atlasWidth + resizeWidth > maxTexRes
                        && atlasHeight + charHeight > maxTexRes) {
                    for (Iterator<CharToCreate> it = characters.iterator(); it.hasNext();) {
                        it.next().codePoint = defaultCodePoint;
                        it.remove();
                    }
                    break;
                } else
                    resizeAtlas();
            }
        } while (!characters.isEmpty());
        
        if (atlasResized || added) {
            int oldWidth = (atlas != null) ? atlas.getImage().getWidth() : 0;
            int oldHeight = (atlas != null) ? atlas.getImage().getHeight() : 0;
            
            if (outline > 0) {
                createAtlasOutlined();
            } else
                createAtlas();
            
            for (TTF_AtlasListener listener : onAtlas) {
                listener.mod(assetManager, oldWidth, oldHeight, atlasWidth,
                        atlasHeight, this);
            }
        }
    }
    
    @Override
    protected void createAtlas() {
        Bitmap bitmap = Bitmap.createBitmap(atlasWidth, atlasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRGB(0, 0, 0);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setARGB(255, 255, 255, 255);
        if (bold > 0) {
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(bold);
            paint.setStrokeCap(Paint.Cap.BUTT);
            paint.setStrokeJoin(Paint.Join.ROUND);
        } else
            paint.setStyle(Paint.Style.FILL);
        
        for (TrueTypeBitmapGlyph glyph : cache.values()) {
            int x = (glyph.x + (padding / 2)) - glyph.xMod;
            int y = (glyph.y + (padding /2 )) + glyph.hMod;
            canvas.translate(x, y);
            if (glyph.contour != null) {
                canvas.drawPath(glyph.contour, paint);
                glyph.contour = null;
            } else
                canvas.drawPath(getContours(getGlyph(getGlyphID(glyph.codePoint))).contours, paint);
            canvas.translate(-x, -y);
        }
        
        
        if (atlas != null) {
            atlas.getImage().dispose();
            if (!NativeObjectManager.UNSAFE) {
                for (ByteBuffer buf : atlas.getImage().getData()) {
                    BufferUtils.destroyDirectBuffer(buf);
                }
            }
        }
        
        atlas = new Texture2D(droidBitmapToImage(bitmap));
        bitmap.recycle();
        
        atlasResized = false;
    }
    
    @Override
    protected void createAtlasOutlined() {
        Bitmap bitmap = Bitmap.createBitmap(atlasWidth, atlasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas =new Canvas(bitmap);
        canvas.drawRGB(0, 0, 0);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStrokeJoin(Paint.Join.ROUND);
        
        for (TrueTypeBitmapGlyph glyph : cache.values()) {
            paint.setARGB(255, 255, 0, 0);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(outline + bold);
            
            int x = (glyph.x + (padding / 2)) - glyph.xMod;
            int y = (glyph.y + (padding /2 )) + glyph.hMod;
            canvas.translate(x, y);
            
            Path contours = glyph.contour;
            if (contours != null) {
                canvas.drawPath(contours, paint);
                glyph.contour = null;
            } else {
                contours = getContours(getGlyph(getGlyphID(glyph.codePoint))).contours;
                canvas.drawPath(contours, paint);
            }
            
            if (bold > 0) {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setStrokeWidth(bold);
            } else
                paint.setStyle(Paint.Style.FILL);
            
            paint.setARGB(255, 255, 0, 255);
            canvas.drawPath(contours, paint);
            canvas.translate(-x, -y);
        }
        
        
        if (atlas != null) {
            atlas.getImage().dispose();
            if (!NativeObjectManager.UNSAFE) {
                for (ByteBuffer buf : atlas.getImage().getData()) {
                    BufferUtils.destroyDirectBuffer(buf);
                }
            }
        }
        
        atlas = new Texture2D(droidBitmapToImage(bitmap));
        bitmap.recycle();
        
        atlasResized = false;
    }
    
    /**
     * Converts a Q2.14 format two byte byte array to floating point.
     * 
     * @param value 2 byte byte array.
     * @return The value as a float.
     */
    private float f2dot14(byte[] value) {
        return ByteBuffer.wrap(value).getShort() * (float)Math.pow(2, -14);
    }
    
    private Glyf getContours(Glyph glyf) {
        if (glyf.glyphType() == GlyphType.Composite) {
            CompositeGlyph g = (CompositeGlyph)glyf;
            Glyf[] glyfs = new Glyf[g.numGlyphs()];
            int flags = g.flags(0);
            
            float[] matrix = new float[]{1, 0, 0, 1, 0, 0};
            if ((flags & CompositeGlyph.FLAG_WE_HAVE_A_SCALE)
                    == CompositeGlyph.FLAG_WE_HAVE_A_SCALE) {
                matrix[0] = f2dot14(g.transformation(0));
                matrix[3] = matrix[0];
            } else if ((flags & CompositeGlyph.FLAG_WE_HAVE_AN_X_AND_Y_SCALE)
                    == CompositeGlyph.FLAG_WE_HAVE_AN_X_AND_Y_SCALE) {
                byte[] transform = g.transformation(0);
                matrix[0] = f2dot14(new byte[]{transform[0], transform[1]});
                matrix[3] = f2dot14(new byte[]{transform[2], transform[3]});
            } else if ((flags & CompositeGlyph.FLAG_WE_HAVE_A_TWO_BY_TWO)
                    == CompositeGlyph.FLAG_WE_HAVE_A_TWO_BY_TWO) {
                byte[] transform = g.transformation(0);
                matrix[0] = f2dot14(new byte[]{transform[0], transform[1]});
                matrix[1] = f2dot14(new byte[]{transform[2], transform[3]});
                matrix[2] = f2dot14(new byte[]{transform[4], transform[5]});
                matrix[3] = f2dot14(new byte[]{transform[6], transform[7]});
            }
            
            if ((flags & CompositeGlyph.FLAG_ARGS_ARE_XY_VALUES)
                == CompositeGlyph.FLAG_ARGS_ARE_XY_VALUES) {
                matrix[4] = (short)g.argument1(0);
                matrix[5] = (short)g.argument2(0);
            }
            
            Path path = new Path();
            glyfs[0] = getSimpleContours((SimpleGlyph)getGlyph(g.glyphIndex(0)), matrix, path);
            
            //path.set(glyfs[0].contours);
            float[] lastMatrix = new float[]{
                matrix[0],
                matrix[1],
                matrix[2],
                matrix[3],
                matrix[4],
                matrix[5]
            };
            
            for (int i = 1; i < glyfs.length; i++) {
                matrix[0] = 1;
                matrix[1] = 0;
                matrix[2] = 0;
                matrix[3] = 1;
                matrix[4] = 0;
                matrix[5] = 0;
                
                SimpleGlyph glyph = (SimpleGlyph)getGlyph(g.glyphIndex(i));
                flags = g.flags(i);
                
                if ((flags & CompositeGlyph.FLAG_WE_HAVE_A_SCALE)
                        == CompositeGlyph.FLAG_WE_HAVE_A_SCALE) {
                    matrix[0] = f2dot14(g.transformation(i));
                    matrix[3] = matrix[0];
                } else if ((flags & CompositeGlyph.FLAG_WE_HAVE_AN_X_AND_Y_SCALE)
                        == CompositeGlyph.FLAG_WE_HAVE_AN_X_AND_Y_SCALE) {
                    byte[] transform = g.transformation(i);
                    matrix[0] = f2dot14(new byte[]{transform[0], transform[1]});
                    matrix[3] = f2dot14(new byte[]{transform[2], transform[3]});
                } else if ((flags & CompositeGlyph.FLAG_WE_HAVE_A_TWO_BY_TWO)
                        == CompositeGlyph.FLAG_WE_HAVE_A_TWO_BY_TWO) {
                    byte[] transform = g.transformation(i);
                    matrix[0] = f2dot14(new byte[]{transform[0], transform[1]});
                    matrix[1] = f2dot14(new byte[]{transform[2], transform[3]});
                    matrix[2] = f2dot14(new byte[]{transform[4], transform[5]});
                    matrix[3] = f2dot14(new byte[]{transform[6], transform[7]});
                }
                
                if ((flags & CompositeGlyph.FLAG_ARGS_ARE_XY_VALUES)
                    == CompositeGlyph.FLAG_ARGS_ARE_XY_VALUES) {
                    matrix[4] = (short)g.argument1(i);
                    matrix[5] = (short)g.argument2(i);
                } else if ((flags & CompositeGlyph.FLAG_ARG_1_AND_2_ARE_WORDS)
                        == CompositeGlyph.FLAG_ARG_1_AND_2_ARE_WORDS) {
                    int[] p1 = ankr.getAnchor(g.glyphIndex(i - 1), g.argument1(i));
                    int[] p2 = ankr.getAnchor(g.glyphIndex(i), g.argument2(i));
                    
                    float x = (p1[0] * lastMatrix[0]) + (p1[1] * lastMatrix[2]) + lastMatrix[4];
                    float y = (p1[0] * lastMatrix[1]) + (p1[1] * lastMatrix[3]) + lastMatrix[5];
                    
                    float x2 = (p2[0] * matrix[0]) + (p2[1] * matrix[2]);
                    float y2 = (p2[0] * matrix[1]) + (p2[1] * matrix[3]);
                    
                    matrix[4] = x - x2;
                    matrix[5] = y - y2;
                }
                
                glyfs[i] = getSimpleContours(glyph, matrix, path);
                //path.addPath(glyfs[i].contours);
                lastMatrix[0] = matrix[0];
                lastMatrix[1] = matrix[1];
                lastMatrix[2] = matrix[2];
                lastMatrix[3] = matrix[3];
                lastMatrix[4] = matrix[4];
                lastMatrix[5] = matrix[5];
                
            }
            
            RectF bounds = new RectF();
            path.computeBounds(bounds, true);
            float maxX = bounds.right;
            float minX = bounds.left;
            float maxY = bounds.bottom;
            float minY = bounds.top;
            
            return new Glyf(path, maxX, minX, maxY, minY);
        }
        
        return getSimpleContours((SimpleGlyph)glyf);
    }
    
    private Glyf getSimpleContours(SimpleGlyph glyf) {
        float maxX = Float.MIN_VALUE;
        float minX = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        
        int numContours = glyf.numberOfContours();
        Path paths = new Path();
        for (int contour = 0; contour < numContours; contour++) {
            Path path = new Path();
            int numPoints = glyf.numberOfPoints(contour);
            
            float last1X = glyf.xCoordinate(contour, 0) * pointScale;
            float last1Y = glyf.yCoordinate(contour, 0) * pointScale;
            
            last1X += (last1Y / italicRef) * italic;
            
            float firstOnCurveX = last1X;
            float firstOnCurveY = last1Y;
            boolean last1OnCurve = glyf.onCurve(contour, 0);
            if (last1OnCurve)
                path.moveTo(last1X, last1Y);
            
            if (last1X > maxX)
                maxX = last1X;
            if (last1X < minX)
                minX = last1X;
            if (last1Y > maxY)
                maxY = last1Y;
            if (last1Y < minY)
                minY = last1Y;

            float firstX = last1X;
            float firstY = last1Y;
            boolean firstOnCurve = last1OnCurve;
            
            for (int point = 1; point < numPoints; point++) {
                boolean onCurve = glyf.onCurve(contour, point);
                float x = glyf.xCoordinate(contour, point) * pointScale;
                float y = glyf.yCoordinate(contour, point) * pointScale;
                
                x += (y / italicRef) * italic;
                
                if (x > maxX)
                    maxX = x;
                if (x < minX)
                    minX = x;
                if (y > maxY)
                    maxY = y;
                if (y < minY)
                    minY = y;
                
                if (!onCurve && !last1OnCurve) {
                    if (point == 1) {
                        last1X = x;
                        last1Y = y;
                        
                        x = last1X + ((x - last1X) / 2f);
                        y = last1Y + ((y - last1Y) / 2f);
                        
                        firstOnCurveX = x;
                        firstOnCurveY = y;
                        path.moveTo(x, y);
                        
                        last1OnCurve = false;
                        
                        continue;
                    }
                    x = last1X + ((x - last1X) / 2f);
                    y = last1Y + ((y - last1Y) / 2f);
                    
                    onCurve = true;
                    point--;
                } else if (!last1OnCurve && point == 1) {
                    firstOnCurveX = x;
                    firstOnCurveY = y;
                    path.moveTo(x, y);

                    last1X = x;
                    last1Y = y;
                    last1OnCurve = true;
                    
                    continue;
                }
                
                if (onCurve && !last1OnCurve) {
                    path.quadTo(last1X, last1Y, x, y);
                } else if (onCurve) {
                    path.lineTo(x, y);
                }

                last1X = x;
                last1Y = y;
                last1OnCurve = onCurve;
            }
            
            if (last1OnCurve) {
                if (firstOnCurve) {
                    //Both the first and last points are on-curve. We
                    //create an edge between the two.
                    path.close();
                } else {
                    //The last point is on-curve, but the first point is not.
                    //We create a bezier curve between the last and second
                    //points with the first point as the middle control.
                    path.quadTo(firstX, firstY, firstOnCurveX, firstOnCurveY);
                    path.close();
                }
            } else {
                if (firstOnCurve) {
                    //The first point is on-curve, but the last point
                    //was not. We create a bezier curve between the
                    //second to last point and the first point with
                    //the last point as the middle control.
                    path.quadTo(last1X, last1Y, firstX, firstY);
                    path.close();
                } else {
                    //The first and last contour points are both off-curve.
                    //We add an on-curve point between them and create two
                    //new quadratic bezier curves, one between the second to
                    //last point and the new mid-point with the last point as
                    //the middle control point and another between the new
                    //mid-point and the second point with the first point as
                    //the middle control point.
                    float x = last1X + ((firstX - last1X) / 2f);
                    float y = last1Y + ((firstY - last1Y) / 2f);
                    path.quadTo(last1X, last1Y, x, y);
                    
                    path.quadTo(firstX, firstY, firstOnCurveX, firstOnCurveY);
                    path.close();
                }
            }
            paths.addPath(path);
        }
        
        if (maxX == Float.MIN_VALUE) {
            maxX = 0;
            minX = 0;
        }
        if (maxY == Float.MIN_VALUE) {
            maxY = 0;
            minY = 0;
        }
        
        Glyf g = new Glyf(paths, maxX, minX, maxY, minY);
        
        return g;
    }
    
    private Glyf getSimpleContours(SimpleGlyph glyf, float[] matrix, Path path) {
        float maxX = Float.MIN_VALUE;
        float minX = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        
        int numContours = glyf.numberOfContours();
        for (int contour = 0; contour < numContours; contour++) {
            int numPoints = glyf.numberOfPoints(contour);
            
            float ox = glyf.xCoordinate(contour, 0);
            float last1Y = glyf.yCoordinate(contour, 0);
            //Transform our points by the supplied matrix
            float last1X = ((ox * matrix[0]) + (last1Y * matrix[2]) + matrix[4]) * pointScale;
            last1Y = ((ox * matrix[1]) + (last1Y * matrix[3]) + matrix[5]) * pointScale;
            
            last1X += (last1Y / italicRef) * italic;
            
            float firstOnCurveX = last1X;
            float firstOnCurveY = last1Y;
            boolean last1OnCurve = glyf.onCurve(contour, 0);
            if (last1OnCurve)
                path.moveTo(last1X, last1Y);
            
            if (last1X > maxX)
                maxX = last1X;
            if (last1X < minX)
                minX = last1X;
            if (last1Y > maxY)
                maxY = last1Y;
            if (last1Y < minY)
                minY = last1Y;

            float firstX = last1X;
            float firstY = last1Y;
            boolean firstOnCurve = last1OnCurve;
            
            for (int point = 1; point < numPoints; point++) {
                boolean onCurve = glyf.onCurve(contour, point);
                ox = glyf.xCoordinate(contour, point);
                float y = glyf.yCoordinate(contour, point);
                //transform by the supplied matrix
                float x = ((ox * matrix[0]) + (y * matrix[2]) + matrix[4]) * pointScale;
                y = ((ox * matrix[1]) + (y * matrix[3]) + matrix[5]) * pointScale;
                
                x += (y / italicRef) * italic;
                
                if (x > maxX)
                    maxX = x;
                if (x < minX)
                    minX = x;
                if (y > maxY)
                    maxY = y;
                if (y < minY)
                    minY = y;
                
                if (!onCurve && !last1OnCurve) {
                    if (point == 1) {
                        last1X = x;
                        last1Y = y;
                        
                        x = last1X + ((x - last1X) / 2f);
                        y = last1Y + ((y - last1Y) / 2f);
                        
                        firstOnCurveX = x;
                        firstOnCurveY = y;
                        path.moveTo(x, y);
                        
                        last1OnCurve = false;
                        
                        continue;
                    }
                    x = last1X + ((x - last1X) / 2f);
                    y = last1Y + ((y - last1Y) / 2f);
                    
                    onCurve = true;
                    point--;
                } else if (!last1OnCurve && point == 1) {
                    firstOnCurveX = x;
                    firstOnCurveY = y;
                    path.moveTo(x, y);

                    last1X = x;
                    last1Y = y;
                    last1OnCurve = true;
                    
                    continue;
                }
                
                if (onCurve && !last1OnCurve) {
                    path.quadTo(last1X, last1Y, x, y);
                } else if (onCurve) {
                    path.lineTo(x, y);
                }

                last1X = x;
                last1Y = y;
                last1OnCurve = onCurve;
            }
            
            if (last1OnCurve) {
                if (firstOnCurve) {
                    //Both the first and last points are on-curve. We
                    //create an edge between the two.
                    path.close();
                } else {
                    //The last point is on-curve, but the first point is not.
                    //We create a bezier curve between the last and second
                    //points with the first point as the middle control.
                    path.quadTo(firstX, firstY, firstOnCurveX, firstOnCurveY);
                    path.close();
                }
            } else {
                if (firstOnCurve) {
                    //The first point is on-curve, but the last point
                    //was not. We create a bezier curve between the
                    //second to last point and the first point with
                    //the last point as the middle control.
                    path.quadTo(last1X, last1Y, firstX, firstY);
                    path.close();
                } else {
                    //The first and last contour points are both off-curve.
                    //We add an on-curve point between them and create two
                    //new quadratic bezier curves, one between the second to
                    //last point and the new mid-point with the last point as
                    //the middle control point and another between the new
                    //mid-point and the second point with the first point as
                    //the middle control point.
                    float x = last1X + ((firstX - last1X) / 2f);
                    float y = last1Y + ((firstY - last1Y) / 2f);
                    path.quadTo(last1X, last1Y, x, y);
                    
                    path.quadTo(firstX, firstY, firstOnCurveX, firstOnCurveY);
                    path.close();
                }
            }
        }
        
        if (maxX == Float.MIN_VALUE) {
            maxX = 0;
            minX = 0;
        }
        if (maxY == Float.MIN_VALUE) {
            maxY = 0;
            minY = 0;
        }
        
        Glyf g = new Glyf(path, maxX, minX, maxY, minY);
        
        return g;
    }
}
