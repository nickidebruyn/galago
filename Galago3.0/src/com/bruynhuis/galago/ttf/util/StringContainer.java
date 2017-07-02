/*
 * Feel free to use, modify, and/or distribute this source code for personal,
 * educational, commercial or any other reason you may conceive with or
 * without credit. There are absolutely no restrictions on the use,
 * modification or distribution of this code.
 */
package com.bruynhuis.galago.ttf.util;

import com.jme3.font.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import com.bruynhuis.galago.ttf.TrueTypeBitmapGlyph;
import com.bruynhuis.galago.ttf.TrueTypeFont;

/**
 * <p><code>StringContainer</code> is used to format one or more lines of
 * text with horizontal/vertical alignments, kerning and wrapping. The
 * supplied <code>textBox</code> defines an area of the screen in which
 * the text should conform. When using a <code>WrapMode</code> other than
 * <code>WrapMode.NoWrap</code> the text will be wrapped so as to fit
 * within the <code>textBox</code>. When using <code>WrapMode.WordClip</code>
 * or <code>WrapMode.CharClip</code> the text will conform to the height
 * value of the <code>textBox</code>, lines exceeding the specified height
 * will be cut off and an ellipsis appended to the last line.
 * See {@link StringContainer.WrapMode}
 * for more information about <code>WrapMode</code>s.</p>
 * 
 * <p>Text alignment is also performed using the parameters in the
 * <code>textBox</code>. The {@link #getTextHeight()} and
 * {@link #getTextWidth()} methods will return the actual height
 * and width of the formatted text, not the height and width of the
 * <code>textBox</code>. Those methods, along with others such as
 * {@link #getLineCount()}, will return 0 before the first call
 * to {@link #getLines()}. All formatting is performed in the
 * {@link #getLines()} method.</p>
 * 
 * @author Adam T. Ryder
 * <a href="http://1337atr.weebly.com">http://1337atr.weebly.com</a>
 * 
 * @see #getLines()
 * @see truetypefont.TrueTypeFont#getFormattedText(truetypefont.util.StringContainer, com.jme3.math.ColorRGBA, com.jme3.math.ColorRGBA) 
 */
public class StringContainer implements Cloneable {
    public enum Align {
        Left,
        Center,
        Right
    }
    
    public enum VAlign {
        Top,
        Center,
        Bottom
    }
    
    /**
     * <code>WrapMode.NoWrap</code> will
     * display the text as is. <code>WrapMode.Char</code> will wrap each
     * line to the next line when said line is wider than the <code>textBox</code>
     * width. <code>WrapMode.Word</code> performs the same action as
     * <code>WrapMode.Char</code> except that the algorithm attempts to
     * wrap the line between words. <code>WrapMode.Clip</code> will not
     * wrap the text at all, but instead cut off the first line where it
     * is wider than the <code>textBox</code> width and add the <code>ellipsis</code>.
     * <code>WrapMode.CharClip</code> performs the same action as
     * <code>WrapMode.Char</code> except that the text will be cut off
     * and the <code>ellipsis</code> added if and when the text lines
     * grow larger than the <code>textBox</code>'s height value.
     * <code>WrapMode.WordClip</code> performs the same action as
     * <code>WrapMode.CharClip</code> except that the algorithm attempts
     * to break the lines between words.
     */
    public enum WrapMode {
        NoWrap,
        Char,
        Word,
        CharClip,
        WordClip,
        Clip
    }
    
    private String text;
    private TrueTypeBitmapGlyph[][] lines;
    private float[] lineWidths;
    
    private Rectangle textBox;
    private Align align = Align.Left;
    private VAlign valign = VAlign.Top;
    private WrapMode wrap = WrapMode.WordClip;
    private TrueTypeBitmapGlyph[] ellipsis;
    private TrueTypeFont font;
    private int kerning = 0;
    
    private float width = 0;
    private float height = 0;
    private int numNonSpaceChars = 0;
    private int offset = 0;
    
    /**
     * Constructs a new <code>StringContainer</code> instance with no text
     * a default kerning of zero, horizontal alignment of <code>Align.Left</code>,
     * vertical alignment of <code>VAlign.Top</code>, wrap mode of
     * <code>WrapMode.NoWrap</code> and an ellipsis of "...".
     * 
     * @param font 
     */
    public StringContainer(TrueTypeFont font) {
        this(font, "");
    }
    
    /**
     * Constructs a new <code>StringContainer</code> instance with the default
     * kerning of zero, an empty <code>textBox</code>, horizontal alignment
     * of <code>Align.Left</code> vertical alignment of <code>VAlign.Top</code>
     * wrap mode of <code>WrapMode.NoWrap</code>, and an ellipsis of "...".
     * 
     * @param font
     * @param text 
     */
    public StringContainer(TrueTypeFont font, String text) {
        this(font, text, 0);
    }
    
    /**
     * Constructs a new <code>StringContainer</code> instance with the default
     * horizontal alignment of <code>Align.Left</code>, vertical alignment
     * of <code>VAlign.Top</code>, wrap mode of <code>WrapMode.NoWrap</code>
     * and an ellipsis of "..." and an empty <code>textBox</code>.
     * 
     * @param font
     * @param text
     * @param kerning 
     */
    public StringContainer(TrueTypeFont font, String text, int kerning) {
        this(font, text, kerning, null);
    }
    
    /**
     * Constructs a new <code>StringContainer</code> instance with the default
     * horizontal alignment of <code>Align.Left</code>, vertical alignment
     * of <code>VAlign.Top</code>, wrap mode of <code>WrapMode.NoWrap</code>
     * and an ellipsis of "...".
     * 
     * @param font
     * @param text
     * @param kerning
     * @param textBox 
     */
    public StringContainer(TrueTypeFont font, String text, int kerning, Rectangle textBox) {
        this(font, text, kerning, textBox, Align.Left);
    }
    
    /**
     * Constructs a new <code>StringContainer</code> instance with the default
     * vertical alignment of <code>VAlign.Top</code>, wrap mode of
     * <code>WrapMode.NoWrap</code>, and an ellipsis of "...".
     * 
     * @param font
     * @param text
     * @param kerning
     * @param textBox
     * @param hAlign 
     */
    public StringContainer(TrueTypeFont font, String text, int kerning, Rectangle textBox,
            Align hAlign) {
        this(font, text, kerning, textBox, hAlign, VAlign.Top, WrapMode.NoWrap, null);
    }
    
    /**
     * Constructs a new <code>StringContainer</code> instance.
     * 
     * @param font
     * @param text
     * @param kerning
     * @param textBox
     * @param hAlign
     * @param vAlign
     * @param wrapMode
     * @param ellipsis Supplying null will yield the default "..." ellipsis.
     */
    public StringContainer(TrueTypeFont font, String text, int kerning, Rectangle textBox,
            Align hAlign, VAlign vAlign, WrapMode wrapMode, String ellipsis) {
        this.font = font;
        this.text = text == null ? "" : text;
        this.kerning = kerning;
        this.textBox = textBox == null ? new Rectangle(0, 0, 0, 0) : textBox;
        align = hAlign;
        valign = vAlign;
        wrap = wrapMode;
        this.ellipsis = (ellipsis == null) ? font.getBitmapGlyphs("...") : font.getBitmapGlyphs(ellipsis);
    }
    
    @Override
    public StringContainer clone() {
        StringContainer newContainer = new StringContainer(font, text, kerning, textBox.clone(),
                align, valign, wrap, null);
        newContainer.ellipsis = Arrays.copyOf(ellipsis, ellipsis.length);
        
        if (lines != null) {
            newContainer.lines = new TrueTypeBitmapGlyph[lines.length][];
            for (int i = 0; i < lines.length; i++) {
                newContainer.lines[i] = Arrays.copyOf(lines[i], lines[i].length);
            }
        }
        
        return newContainer;
    }
    
    /**
     * retrieves the text associated with this <code>StringContainer</code>.
     * 
     * @return The unformatted text.
     */
    public String getText() {
        return text;
    }
    
    /**
     * Sets the text to be formatted by this <code>StringContainer</code>.
     * You must call {@link #getLines()} for this to be applied.
     * 
     * @param text The text to format.
     */
    public void setText(String text) {
        this.text = text == null ? "" : text;
        lines = null;
    }
    
    /**
     * Text associated with this <code>StringContainer</code> will be
     * formatted according to the width and height values of this
     * <code>com.jme3.font.Rectangle</code>.
     * 
     * @return The <code>com.jme3.font.Rectangle</code> that defines
     * the visual constraints to be applied to this text.
     */
    public Rectangle getTextBox() {
        return textBox;
    }
    
    /**
     * Text associated with this <code>StringContainer</code> will be
     * formatted according to the width and height values of the
     * <code>com.jme3.font.Rectangle</code> supplied here or
     * in the constructor. You must call {@link #getLines()} for this
     * change to be applied.
     * 
     * @param textBox A <code>com.jme3.font.Rectangle</code> that defines
     * the visual constraints to be applied to this text.
     */
    public void setTextBox(Rectangle textBox) {
        this.textBox = textBox == null ? new Rectangle(0, 0, 0, 0) : textBox;
        lines = null;
    }
    
    /**
     * Sets the number of characters from the beginning of the text
     * that should not be displayed.
     * 
     * @param offset Number of prefix characters not to display.
     */
    public void setOffset(int offset) {
        this.offset = offset >= 0 ? offset : 0;
        lines = null;
    }
    
    /**
     * 
     * @return The number of characters from the beginning
     * of the text that will not be displayed.
     * 
     * @see #setOffset(int)
     */
    public int getOffset() {
        return offset;
    }
    
    /**
     * Set the maximum number of lines to display.
     * 
     * @param maxLines The maximum number of text lines.
     */
    public void setMaxLines(int maxLines) {
        if (maxLines <= 0) {
            textBox = new Rectangle(textBox.x, textBox.y, textBox.width, 0);
            lines = null;
            return;
        }
        
        textBox = new Rectangle(textBox.x, textBox.y, textBox.width,
                (font.getScaledLineHeight() * maxLines) - font.getScaledLineGap());
        lines = null;
    }
    
    /**
     * Retrieves the current horizontal alignment parameter.
     * 
     * @return The current horizontal alignment parameter.
     */
    public Align getAlignment() {
        return align;
    }
    
    /**
     * Retrieves the current vertical alignment parameter.
     * 
     * @return The current vertical alignment parameter.
     */
    public VAlign getVerticalAlignment() {
        return valign;
    }
    
    /**
     * Sets the horizontal alignment of the text within the <code>textBox</code>.
     * You must call {@link #getLines()} to apply this change.
     * 
     * @param hAlign The horizontal alignment to apply to the text.
     */
    public void setAlignment(Align hAlign) {
        align = hAlign;
        lines = null;
    }
    
    /**
     * Sets the vertical alignment of the text within the <code>textBox</code>.
     * You must call {@link #getLines()} to apply this change.
     * 
     * @param vAlign The vertical alignment to apply to the text.
     */
    public void setVerticalAlignment(VAlign vAlign) {
        valign = vAlign;
        lines = null;
    }
    
    /**
     * Retrieves the number of formatted text lines. This will return zero
     * if {@link #getLines()} has not yet been called.
     * 
     * @return The number of formatted text lines.
     */
    public int getLineCount() {
        return lines != null ? lines.length : 0;
    }
    
    /**
     * Gets the method by which the text should be wrapped according to the
     * <code>textBox</code> constraints. <code>WrapMode.NoWrap</code> will
     * display the text as is. <code>WrapMode.Char</code> will wrap each
     * line to the next line when said line is wider than the <code>textBox</code>
     * width. <code>WrapMode.Word</code> performs the same action as
     * <code>WrapMode.Char</code> except that the algorithm attempts to
     * wrap the line between words. <code>WrapMode.Clip</code> will not
     * wrap the text at all, but instead cut off the first line where it
     * is wider than the <code>textBox</code> width and add the <code>ellipsis</code>.
     * <code>WrapMode.CharClip</code> performs the same action as
     * <code>WrapMode.Char</code> except that the text will be cut off
     * and the <code>ellipsis</code> added if and when the text lines
     * grow larger than the <code>textBox</code>'s height value.
     * <code>WrapMode.WordClip</code> performs the same action as
     * <code>WrapMode.CharClip</code> except that the algorithm attempts
     * to break the lines between words.
     * 
     * @return The <code>WrapMode</code> in use.
     */
    public WrapMode getWrapMode() {
        return wrap;
    }
    
    /**
     * Sets the method by which the text should be wrapped according to the
     * <code>textBox</code> constraints. <code>WrapMode.NoWrap</code> will
     * display the text as is. <code>WrapMode.Char</code> will wrap each
     * line to the next line when said line is wider than the <code>textBox</code>
     * width. <code>WrapMode.Word</code> performs the same action as
     * <code>WrapMode.Char</code> except that the algorithm attempts to
     * wrap the line between words. <code>WrapMode.Clip</code> will not
     * wrap the text at all, but instead cut off the first line where it
     * is wider than the <code>textBox</code> width and add the <code>ellipsis</code>.
     * <code>WrapMode.CharClip</code> performs the same action as
     * <code>WrapMode.Char</code> except that the text will be cut off
     * and the <code>ellipsis</code> added if and when the text lines
     * grow larger than the <code>textBox</code>'s height value.
     * <code>WrapMode.WordClip</code> performs the same action as
     * <code>WrapMode.CharClip</code> except that the algorithm attempts
     * to break the lines between words. You must call {@link #getLines()}
     * to apply the new value.
     * 
     * @param wrapMode The <code>WrapMode</code> to use.
     */
    public void setWrapMode(WrapMode wrapMode) {
        wrap = wrapMode;
        lines = null;
    }
    
    /**
     * Sets the <code>String</code> of characters to be shown at the end
     * of a clipped line of text. If you don't want to display an ellipsis
     * supply an empty <code>String</code>. You must call {@link #getLines()}
     * to apply the new ellipsis. Default: "..."
     * 
     * @param ellipsis The <code>String</code> of characters to be displayed
     * at the end of a clipped line of text.
     */
    public void setElipsis(String ellipsis) {
        this.ellipsis = (ellipsis == null) ? font.getBitmapGlyphs("...") : font.getBitmapGlyphs(ellipsis);
        lines = null;
    }
    
    /**
     * The {@link TrueTypeFont} used when formatting and displaying the text
     * associated with this <code>StringContainer</code>.
     * 
     * @return The {@link TrueTypeFont}.
     * 
     * @see TrueTypeFont
     */
    public TrueTypeFont getFont() {
        return font;
    }
    
    /**
     * Sets the {@link TrueTypeFont} to be used when formatting and displaying the
     * text associated with this <code>StringContainer</code>. You must call
     * {@link #getLines()} to apply this change.
     * 
     * @param font The {@link TrueTypeFont} to set.
     * 
     * @see TrueTypeFont
     */
    public void setFont(TrueTypeFont font) {
        this.font = font;
        lines = null;
    }
    
    /**
     * Kerning is the amount of extra space between characters.
     * 
     * @return The kerning value associated with this <code>StringContainer</code>.
     */
    public int getKerning() {
        return kerning;
    }
    
    /**
     * Sets additional space between characters in pixels.
     * {@link #getLines()} must be called for this to take effect.
     * 
     * @param kerning The amount of additional space between characters.
     */
    public void setKerning(int kerning) {
        this.kerning = kerning;
        lines = null;
    }
    
    /**
     * The actual width of the text from the origin in the upper left
     * corner to the right side of the last character in the longest line.
     * This will equal zero until the first call to {@link #getLines()}.
     * 
     * @return The width in pixels.
     */
    public float getTextWidth() {
        return width;
    }
    
    /**
     * The actual height of the text from the origin in the upper left
     * corner to the lowest point of the lowest character on the last line.
     * This will equal zero until the first call to {@link #getLines()}.
     * 
     * @return The height in pixels.
     */
    public float getTextHeight() {
        return height;
    }
    
    /**
     * The number of non space characters contained in the assigned text.
     * This will equal zero until the first call to {@link #getLines()}.
     * 
     * @return The number of non space characters.
     */
    public int getNumNonSpaceCharacters() {
        return numNonSpaceChars;
    }
    
    /**
     * The widths of each line of text. Will return null before the
     * first call to {@link #getLines()}.
     * 
     * @return An array enumerating the widths of each line of text.
     */
    public float[] getLineWidths() {
        return lineWidths;
    }
    
    /**
     * Formats the text associated with this <code>StringContainer</code> according
     * to the various formatting parameters.
     * 
     * @return An array containing arrays of {@link TrueTypeBitmapGlyph}s representing
     * each line of text.
     * 
     * @see TrueTypeBitmapGlyph
     */
    public TrueTypeBitmapGlyph[][] getLines() {
        //I had to add 2 to the calculated width of each line
        //the widths appear to always be off about 2 pixels for
        //no apparent reason. Probably because the methods Java
        //supplies to measure glyphs are not exact.
        
        if (lines != null)
            return lines;
        
        String text = this.text;
        if (offset > 0) {
            if (offset < this.text.length()) {
                text = this.text.substring(offset);
            } else
                text = "";
        }
        if (text.isEmpty()) {
            width = 0;
            height = 0;
            numNonSpaceChars = 0;
            lines = new TrueTypeBitmapGlyph[0][];
            lineWidths = new float[0];
            
            return lines;
        }
        
        String[] stringLines = text.split("\n");
        int numTrailingBreaks = 0;
        for (int i = text.length() - 1; i >= 0; i--) {
            if (text.charAt(i) == '\n') {
                numTrailingBreaks++;
            } else
                break;
        }
        if (numTrailingBreaks > 0) {
            if (stringLines.length > 0) {
                stringLines = Arrays.copyOf(stringLines, stringLines.length + numTrailingBreaks);
                for (int i = stringLines.length - 1; i >= stringLines.length - numTrailingBreaks; i--)
                    stringLines[i] = "";
            } else {
                stringLines = new String[numTrailingBreaks + 1];
                Arrays.fill(stringLines, "");
            }
        }
            
        TrueTypeBitmapGlyph[][] tmpLines = new TrueTypeBitmapGlyph[stringLines.length][];
        for (int i = 0; i < tmpLines.length; i++) {
            tmpLines[i] = font.getBitmapGlyphs(stringLines[i]);
        }
        
        //getLines() may have been called recursively if the call to font.getBitmapGlyphs
        //causes the atlas to be updated and a TTF_AtlasListener was attached that calls
        //StringContainer.getLines() on this StringContainer. This prevents calculating
        //again.
        if (lines != null)
            return lines;
        
        width = 0;
        height = 0;
        numNonSpaceChars = 0;
        List<Float> lineWidthsArray = new ArrayList<Float>();
        
        if (wrap == WrapMode.NoWrap) {
            lines = tmpLines;
            int line = 0;
            for (TrueTypeBitmapGlyph[] glyphs : lines) {
                float lWidth = 0;
                int pos = 0;
                for (TrueTypeBitmapGlyph glyph : glyphs) {
                    if (!glyph.text.equals(" "))
                        numNonSpaceChars++;
                    
                    lWidth += glyph.getXAdvance()
                            + (pos < glyphs.length - 1 ? kerning : 0);
                    pos++;
                }
                
                lWidth *= font.getScale();
                lineWidthsArray.add(lWidth + 2);
                if (lWidth > width)
                    width = lWidth + 2;
                line ++;
            }
            
            height = (lines.length * font.getScaledLineHeight()) - font.getScaledLineGap();
            
            lineWidths = new float[lines.length];
            for (int i = 0; i < lineWidths.length; i++) {
                lineWidths[i] = lineWidthsArray.get(i);
            }
            
            return lines;
        }
        
        float eWidth = font.getLineWidth(ellipsis, kerning);
        if (wrap == WrapMode.Clip) {
            lines = new TrueTypeBitmapGlyph[1][];
            lines[0] = getCharClippedLine(0, tmpLines[0], eWidth, lineWidthsArray);
            height = font.getScaledLineHeight() - font.getScaledLineGap();
            
            lineWidths = new float[lines.length];
            if (!lineWidthsArray.isEmpty()) {
                for (int i = 0; i < lineWidths.length; i++) {
                    lineWidths[i] = lineWidthsArray.get(i);
                }
            } else {
                for (int i = 0; i < lineWidths.length; i++) {
                    lineWidths[i] = 0;
                }
            }
            
            return lines;
        }
        
        List<TrueTypeBitmapGlyph[]> newLines = new LinkedList();
        switch(wrap) {
            case Char:
                for (int i = 0; i < tmpLines.length; i++) {
                    int pos = 0;
                    TrueTypeBitmapGlyph[] glyphs = tmpLines[i];

                    if (glyphs.length == 0) {
                        newLines.add(glyphs);
                        lineWidthsArray.add(new Float(0));
                        continue;
                    }

                    while (pos < glyphs.length) {
                        TrueTypeBitmapGlyph[] newLine = getCharLine(pos, glyphs, lineWidthsArray);
                        newLines.add(newLine);

                        if (newLine.length == 0)
                            break;

                        pos += newLine.length;
                    }
                }
                break;
            case Word:
                for (int i = 0; i < tmpLines.length; i++) {
                    int pos = 0;
                    TrueTypeBitmapGlyph[] glyphs = tmpLines[i];

                    if (glyphs.length == 0) {
                        newLines.add(glyphs);
                        lineWidthsArray.add(new Float(0));
                        continue;
                    }

                    while (pos < glyphs.length) {
                        TrueTypeBitmapGlyph[] newLine = getWordLine(pos, glyphs, lineWidthsArray);
                        if (newLine.length == 0) {
                            lineWidthsArray.remove(lineWidthsArray.size() - 1);
                            newLine = getCharLine(pos, glyphs, lineWidthsArray);
                            pos += newLine.length;
                        } else if (pos + newLine.length < glyphs.length) {
                            pos += newLine.length + 1;
                            if (pos == glyphs.length)
                                pos--;
                        } else if (newLine.length == 1 && newLine[0].codePoint == ' '
                                && pos > 0 && glyphs[pos - 1].codePoint != ' ') {
                            newLine = new TrueTypeBitmapGlyph[0];
                            pos++;
                        } else
                            pos += newLine.length;
                        
                        newLines.add(newLine);
                        if (newLine.length == 0 || pos == glyphs.length)
                            break;
                    }
                }
                break;
            case CharClip:
                lineLoop: for (int i = 0; i < tmpLines.length; i++) {
                    if (((newLines.size() + 1) * font.getScaledLineHeight()) - font.getScaledLineGap() > textBox.height)
                        break;

                    int pos = 0;
                    TrueTypeBitmapGlyph[] glyphs = tmpLines[i];

                    if (glyphs.length == 0) {
                        newLines.add(glyphs);
                        height += font.getScaledLineHeight();
                        lineWidthsArray.add(new Float(0));
                        if (height + font.getScaledLineHeight() - font.getScaledLineGap() > textBox.height)
                            break;
                        continue;
                    }

                    while (pos < glyphs.length) {
                        if (height + (font.getScaledLineHeight() * 2)
                                - font.getScaledLineGap() <= textBox.height) {
                            TrueTypeBitmapGlyph[] newLine = getCharLine(pos, glyphs, lineWidthsArray);
                            newLines.add(newLine);
                            height += font.getScaledLineHeight();

                            if (newLine.length == 0)
                                break;

                            pos += newLine.length;
                        } else {
                            newLines.add(getCharClippedLine(pos, glyphs, eWidth, lineWidthsArray));
                            height += font.getScaledLineHeight();
                            break lineLoop;
                        }
                    }
                }
                break;
            default:
                lineLoop: for (int i = 0; i < tmpLines.length; i++) {
                    if (((newLines.size() + 1) * font.getScaledLineHeight()) - font.getScaledLineGap() > textBox.height)
                        break;

                    int pos = 0;
                    TrueTypeBitmapGlyph[] glyphs = tmpLines[i];

                    if (glyphs.length == 0) {
                        newLines.add(glyphs);
                        height += font.getScaledLineHeight();
                        lineWidthsArray.add(new Float(0));
                        if (height + font.getScaledLineHeight() - font.getScaledLineGap() > textBox.height)
                            break;
                        continue;
                    }
                    
                    while (pos < glyphs.length) {
                        if (height + (font.getScaledLineHeight() * 2)
                                - font.getScaledLineGap() <= textBox.height) {
                            TrueTypeBitmapGlyph[] newLine = getWordLine(pos, glyphs, lineWidthsArray);
                            if (newLine.length == 0) {
                                lineWidthsArray.remove(lineWidthsArray.size() - 1);
                                newLine = getCharLine(pos, glyphs, lineWidthsArray);
                                pos += newLine.length;
                            } else if (pos + newLine.length < glyphs.length) {
                                pos += newLine.length + 1;
                                if (pos == glyphs.length)
                                    pos--;
                            } else if (newLine.length == 1 && newLine[0].codePoint == ' '
                                    && pos > 0 && glyphs[pos - 1].codePoint != ' ') {
                                newLine = new TrueTypeBitmapGlyph[0];
                                pos++;
                            } else
                                pos += newLine.length;
                            
                            newLines.add(newLine);
                            height += font.getScaledLineHeight();

                            if (newLine.length == 0 || pos == glyphs.length)
                                break;
                        } else {
                            TrueTypeBitmapGlyph[] newLine = getWordClippedLine(pos, glyphs, eWidth,
                                    lineWidthsArray);
                            if (newLine.length == 0) {
                                lineWidthsArray.remove(lineWidthsArray.size() - 1);
                                newLine = getCharLine(pos, glyphs, lineWidthsArray);
                            }
                            newLines.add(newLine);
                            height += font.getScaledLineHeight();
                            break lineLoop;
                        }
                    }
                }
        }
        
        lines = new TrueTypeBitmapGlyph[newLines.size()][];
        lineWidths = new float[lines.length];
        int pos = 0;
        for (TrueTypeBitmapGlyph[] glyphs : newLines) {
            lines[pos] = glyphs;
            lineWidths[pos] = lineWidthsArray.get(pos);
            
            pos++;
        }
        
        height = (lines.length * font.getScaledLineHeight())
                - font.getScaledLineGap();
        if (height < 0)
            height = 0;
        
        return lines;
    }
    
    private TrueTypeBitmapGlyph[] getCharLine(int start, TrueTypeBitmapGlyph[] glyphs,
            List<Float>lineWidths) {
        float newWidth = 0;
        int pos = start - 1;
        
        do {
            int newPos = pos + 1;
            float gWidth = glyphs[newPos].getXAdvance() + (newPos < glyphs.length - 1 ? kerning : 0);
            gWidth *= font.getScale();
            if (newWidth + gWidth > textBox.width)
                break;
            
            if (!glyphs[newPos].text.equals(" "))
                numNonSpaceChars++;
            
            pos = newPos;
            newWidth += gWidth;
        } while (pos < glyphs.length - 1);
        
        if (pos >= start) {
            if (newWidth > width)
                width = newWidth + 2;
            
            lineWidths.add(newWidth + 2);
            return Arrays.copyOfRange(glyphs, start, pos + 1);
        }
        
        lineWidths.add(0f);
        return new TrueTypeBitmapGlyph[0];
    }
    
    private TrueTypeBitmapGlyph[] getCharClippedLine(int start, TrueTypeBitmapGlyph[] glyphs,
            float ellipsisWidth, List<Float>lineWidths) {
        if (ellipsisWidth > textBox.width) {
            lineWidths.add(new Float(0));
            return new TrueTypeBitmapGlyph[0];
        }
        
        TrueTypeBitmapGlyph[] lineGlyphs;
        int pos = start - 1;
        float w = 0;
        
        do {
            int newPos = pos + 1;
            float gWidth = glyphs[newPos].getXAdvance() + (newPos < glyphs.length - 1 ? kerning : 0);
            gWidth *= font.getScale();
            if (w + gWidth > textBox.width)
                break;
            
            if (!glyphs[newPos].text.equals(" "))
                numNonSpaceChars++;

            w += gWidth;
            pos = newPos;
        } while (pos < glyphs.length - 1);
        
        if (pos == glyphs.length - 1) {
            if (w > width)
                width = w + 2;
            
            lineWidths.add(w + 2);
            return Arrays.copyOfRange(glyphs, start, glyphs.length);
        }
            
        if (pos < start) {
            lineWidths.add(0f);
            return new TrueTypeBitmapGlyph[0];
        }
        
        do {
            if (w + ellipsisWidth <= textBox.width && !glyphs[pos].text.equals(" "))
                break;
            
            w -= (glyphs[pos].getXAdvance() + kerning) * font.getScale();
            if (!glyphs[pos].text.equals(" "))
                numNonSpaceChars--;
            pos--;
        } while (pos >= start);
        
        if (pos >= start) {
            lineGlyphs = new TrueTypeBitmapGlyph[(pos + 1 + ellipsis.length) - start];
            int newPos = start;
            do {
                lineGlyphs[newPos - start] = glyphs[newPos];
                newPos++;
            } while (newPos <= pos);
            
            pos = 0;
            newPos -= start;
            while (newPos < lineGlyphs.length) {
                lineGlyphs[newPos] = ellipsis[pos];
                if (!ellipsis[pos].text.equals(" "))
                    numNonSpaceChars++;
                newPos++;
                pos++;
            }
            
            if (w + ellipsisWidth > width)
                width = w + ellipsisWidth + 2;
            lineWidths.add(w + ellipsisWidth + 2);
        } else {
            lineGlyphs = Arrays.copyOf(ellipsis, ellipsis.length);
            for (TrueTypeBitmapGlyph glyph : lineGlyphs) {
                if (!glyph.text.equals(" "))
                    numNonSpaceChars++;
            }
            if (ellipsisWidth > width)
                width = ellipsisWidth + 2;
            lineWidths.add(ellipsisWidth + 2);
        }
        
        return lineGlyphs;
    }
    
    private TrueTypeBitmapGlyph[] getWordLine(int start, TrueTypeBitmapGlyph[] glyphs,
            List<Float>lineWidths) {
        int pos = start;
        float w = 0;
        wordSearch: do {
            WordBound wBound = getWord(pos, glyphs);
            
            float newWidth = 0;
            if (wBound == null) {
                int count = 0;
                while(pos < glyphs.length) {
                    float nw = (glyphs[pos].getXAdvance() + (pos < glyphs.length - 1 ? kerning : 0)) * font.getScale();
                    if (newWidth + nw + w > textBox.width) {
                        if (count > 0) {
                            pos--;
                            newWidth -= (glyphs[pos - 1].getXAdvance() + kerning) * font.getScale();
                        }
                        break;
                    }
                    
                    count++;
                    newWidth += nw;
                    pos++;
                }
                w += newWidth;
                break;
            }
            
            int numSpaces = 0;
            float spaceWidth = 0;
            for (int i = pos; i < wBound.end; i++) {
                newWidth += (glyphs[i].getXAdvance() + (i < glyphs.length - 1 ? kerning : 0)) * font.getScale();
                
                if (w + newWidth > textBox.width) {
                    pos += (numSpaces > 0) ? numSpaces - 1 : 0;
                    w += spaceWidth;
                    break wordSearch;
                } else if (glyphs[i].codePoint == ' ') {
                    spaceWidth += (numSpaces >= 1)
                            ? (glyphs[i].getXAdvance() + (i < glyphs.length - 1 ? kerning : 0)) * font.getScale()
                            : 0;
                    numSpaces++;
                }
            }
            
            numNonSpaceChars += wBound.end - wBound.start;
            w += newWidth;
            pos = wBound.end;
        } while (pos < glyphs.length);
        
        if (pos == glyphs.length) {
            if (w > width)
                width = w + 2;
            lineWidths.add(w + 2);
            
            return Arrays.copyOfRange(glyphs, start, glyphs.length);
        }
        if (pos == 0) {
            lineWidths.add(0f);
            return new TrueTypeBitmapGlyph[0];
        }
        
        w -= (glyphs[pos - 1].getXAdvance() + kerning) * font.getScale();
        w += glyphs[pos - 1].getXAdvance() * font.getScale();
        
        if (w > width)
            width = w + 2;
        lineWidths.add(w + 2);
        
        return Arrays.copyOfRange(glyphs, start, pos);
    }
    
    private TrueTypeBitmapGlyph[] getWordClippedLine(int start, TrueTypeBitmapGlyph[] glyphs,
            float ellipsisWidth, List<Float>lineWidths) {
        if (ellipsisWidth > textBox.width) {
            lineWidths.add(new Float(0));
            return new TrueTypeBitmapGlyph[0];
        }
        int pos = start;
        float w = 0;
        wordSearch: do {
            WordBound wBound = getWord(pos, glyphs);
            
            float newWidth = 0;
            if (wBound == null) {
                int count = 0;
                while(pos < glyphs.length) {
                    float nw = (glyphs[pos].getXAdvance() + (pos < glyphs.length - 1 ? kerning : 0)) * font.getScale();
                    if (newWidth + nw + w > textBox.width) {
                        if (count > 0) {
                            pos--;
                            newWidth -= (glyphs[pos - 1].getXAdvance() + kerning) * font.getScale();
                        }
                        break;
                    }
                    
                    count++;
                    newWidth += nw;
                    pos++;
                }
                w += newWidth;
                break;
            }
            
            int numSpaces = 0;
            float spaceWidth = 0;
            for (int i = pos; i < wBound.end; i++) {
                newWidth += (glyphs[i].getXAdvance() + (i < glyphs.length - 1 ? kerning : 0)) * font.getScale();
                
                /*if (w + newWidth + ellipsisWidth > textBox.width)
                    break wordSearch;*/
                if (w + newWidth > textBox.width) {
                    pos += (numSpaces > 0) ? numSpaces - 1 : 0;
                    w += spaceWidth;
                    break wordSearch;
                } else if (glyphs[i].codePoint == ' ') {
                    spaceWidth += (numSpaces >= 1)
                            ? (glyphs[i].getXAdvance() + (i < glyphs.length - 1 ? kerning : 0)) * font.getScale()
                            : 0;
                    numSpaces++;
                }
            }
            
            numNonSpaceChars += wBound.end - wBound.start;
            w += newWidth;
            pos = wBound.end;
        } while (pos < glyphs.length);
        
        if (pos == glyphs.length) {
            if (w > width)
                width = w + 2;
            lineWidths.add(w + 2);
            
            return Arrays.copyOfRange(glyphs, start, glyphs.length);
        }
        
        if (pos == start) {
            for (int i = 0; i < ellipsis.length; i++) {
                if (!ellipsis[i].text.equals(" "))
                    numNonSpaceChars++;
            }
            lineWidths.add(ellipsisWidth + 2);
            if(ellipsisWidth > width)
                width = ellipsisWidth + 2;
            
            return Arrays.copyOf(ellipsis, ellipsis.length);
        }
        
        if (w + ellipsisWidth > textBox.width) {
            int backPos = pos - 1;
            do {
                w -= (glyphs[backPos].getXAdvance() + (backPos < glyphs.length - 1 ? kerning : 0)) * font.getScale();
                backPos--;
            } while (backPos >= start && w + ellipsisWidth > textBox.width);
            pos = backPos + 1;
        }
        
        if (pos == start) {
            for (int i = 0; i < ellipsis.length; i++) {
                if (!ellipsis[i].text.equals(" "))
                    numNonSpaceChars++;
            }
            lineWidths.add(ellipsisWidth + 2);
            if(ellipsisWidth > width)
                width = ellipsisWidth + 2;
            
            return Arrays.copyOf(ellipsis, ellipsis.length);
        }
        
        if (w + ellipsisWidth > width)
            width = w + ellipsisWidth + 2;
        lineWidths.add(w + ellipsisWidth + 2);
        
        TrueTypeBitmapGlyph[] lineGlyphs = new TrueTypeBitmapGlyph[(pos + ellipsis.length) - start];
        int newPos = start;
        do {
            lineGlyphs[newPos - start] = glyphs[newPos];
            newPos++;
        } while (newPos < pos);

        pos = 0;
        newPos -= start;
        while (newPos < lineGlyphs.length) {
            lineGlyphs[newPos] = ellipsis[pos];
            if (!ellipsis[pos].text.equals(" "))
                numNonSpaceChars++;
            newPos++;
            pos++;
        }
        
        return lineGlyphs;
    }
    
    private WordBound getWord(int start, TrueTypeBitmapGlyph[] glyphs) {
        while (start != glyphs.length && glyphs[start].text.equals(" ")) {
            start++;
        }
        
        if (start == glyphs.length)
            return null;
        
        int end = start + 1;
        while (end != glyphs.length && !glyphs[end].text.equals(" ")) {
            end++;
        }
        
        return new WordBound(start, end);
    }
    
    private class WordBound {
        public final int start;
        public final int end;
        
        private WordBound(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }
}
