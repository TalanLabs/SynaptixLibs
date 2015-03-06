package com.synaptix.swingx.mapviewer.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

import org.jdesktop.swingx.mapviewer.AVKey;

public class MultiLineTextRenderer {

	private TextRenderer textRenderer;

	private int lineSpacing = 0; // Inter line spacing in pixels

	private int lineHeight = 14; // Will be set by getBounds() or by application

	private String continuationString = "...";

	private String textAlign = AVKey.ALIGN_LEFT;

	private Color textColor = Color.DARK_GRAY;

	private Color backColor = Color.LIGHT_GRAY;

	public MultiLineTextRenderer(Font font) {
		this(new TextRenderer(font));
	}

	public MultiLineTextRenderer(TextRenderer textRenderer) {
		super();

		this.textRenderer = textRenderer;
	}

	public TextRenderer getTextRenderer() {
		return textRenderer;
	}

	public String getTextAlign() {
		return textAlign;
	}

	public void setTextAlign(String textAlign) {
		this.textAlign = textAlign;
	}

	public String getContinuationString() {
		return continuationString;
	}

	public void setContinuationString(String continuationString) {
		this.continuationString = continuationString;
	}

	public int getLineHeight() {
		return lineHeight;
	}

	public void setLineHeight(int lineHeight) {
		this.lineHeight = lineHeight;
	}

	public int getLineSpacing() {
		return lineSpacing;
	}

	public void setLineSpacing(int lineSpacing) {
		this.lineSpacing = lineSpacing;
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
		this.textRenderer.setColor(textColor);
	}

	public Color getBackColor() {
		return backColor;
	}

	public void setBackColor(Color backColor) {
		this.backColor = backColor;
	}

	public double getMaxLineHeight(TextRenderer tr) {
		// Check underscore + capital E with acute accent
		return tr.getBounds("_\u00c9").getHeight();
	}

	/**
	 * Returns the bounding rectangle for a multi-line string.
	 * <p>
	 * Note that the X component of the rectangle is the number of lines found
	 * in the text and the Y component of the rectangle is the max line height
	 * encountered.
	 * <p>
	 * Note too that this method will automatically set the current line height
	 * to the max height found.
	 * 
	 * @param text
	 *            the multi-line text to evaluate.
	 * @return the bounding rectangle for the string.
	 */
	public Rectangle getBounds(String text) {
		int width = 0;
		double maxLineHeight = 0;
		String[] lines = text.split("\n");
		for (String line : lines) {
			Rectangle2D lineBounds = this.textRenderer.getBounds(line);
			width = (int) Math.max(lineBounds.getWidth(), width);
			maxLineHeight = (int) Math.max(lineBounds.getHeight(), lineHeight);
		}
		// Make sure we have the highest line height
		maxLineHeight = Math.max(getMaxLineHeight(this.textRenderer), maxLineHeight);
		// Set current line height for future draw
		this.lineHeight = (int)maxLineHeight;
		
		int v = (int)Math.ceil(lines.length
				* maxLineHeight + (lines.length - 1) * this.lineSpacing);
		
		// Compute final height using maxLineHeight and number of lines
		return new Rectangle(lines.length, lineHeight, width, v);
	}

	/**
	 * Add 'new line' characters inside a string so that it's bounding rectangle
	 * tries not to exceed the given dimension width.
	 * <p>
	 * If the dimension height is more than zero, the text will be truncated
	 * accordingly and the continuation string will be appended to the last
	 * line.
	 * <p>
	 * Note that words will not be split and at least one word will be used per
	 * line so the longest word defines the final width of the bounding
	 * rectangle. Each line is trimmed of leading and trailing spaces.
	 * 
	 * @param text
	 *            the text string to wrap
	 * @param width
	 *            the maximum width in pixels the text can occupy.
	 * @param height
	 *            if not zero, the maximum height in pixels the text can occupy.
	 * @return the wrapped string.
	 */
	public String wrap(String text, int width, int height) {
		String[] lines = text.split("\n");
		StringBuffer wrappedText = new StringBuffer();
		// Wrap each line
		for (int i = 0; i < lines.length; i++) {
			lines[i] = this.wrapLine(lines[i], width);
		}
		// Concatenate all lines in one string with new line separators
		// between lines - not at the end
		// Checks for height limit.
		int currentHeight = 0;
		boolean heightExceeded = false;
		double maxLineHeight = getMaxLineHeight(this.textRenderer);
		for (int i = 0; i < lines.length && !heightExceeded; i++) {
			String[] subLines = lines[i].split("\n");
			for (int j = 0; j < subLines.length && !heightExceeded; j++) {
				if (height <= 0 || currentHeight + maxLineHeight <= height) {
					wrappedText.append(subLines[j]);
					currentHeight += maxLineHeight + this.lineSpacing;
					if (j < subLines.length - 1)
						wrappedText.append('\n');
				} else {
					heightExceeded = true;
				}
			}
			if (i < lines.length - 1 && !heightExceeded) {
				wrappedText.append('\n');
			}
		}
		// Add continuation string if text truncated
		if (heightExceeded) {
			if (wrappedText.length() > 0)
				wrappedText.deleteCharAt(wrappedText.length() - 1); // Remove
																	// excess
																	// new line
			wrappedText.append(this.continuationString);
		}


		return wrappedText.toString();
	}

	// Wrap one line to fit the given width
	private String wrapLine(String text, int width) {
		StringBuffer wrappedText = new StringBuffer();
		// Single line - trim leading and trailing spaces
		String source = text.trim();
		Rectangle2D lineBounds = this.textRenderer.getBounds(source);
		if (lineBounds.getWidth() > width) {
			// Split single line to fit preferred width
			StringBuffer line = new StringBuffer();
			int start = 0;
			int end = source.indexOf(' ', start + 1);
			while (start < source.length()) {
				if (end == -1)
					end = source.length(); // last word
				// Extract a 'word' which is in fact a space and a word
				String word = source.substring(start, end);
				String linePlusWord = line + word;
				if (this.textRenderer.getBounds(linePlusWord).getWidth() <= width) {
					// Keep adding to the current line
					line.append(word);
				} else {
					// Width exceeded
					if (line.length() != 0) {
						// Finish current line and start new one
						wrappedText.append(line);
						wrappedText.append('\n');
						line.delete(0, line.length());
						line.append(word.trim()); // get read of leading
													// space(s)
					} else {
						// Line is empty, force at least one word
						line.append(word.trim());
					}
				}
				// Move forward in source string
				start = end;
				if (start < source.length() - 1) {
					end = source.indexOf(' ', start + 1);
				}
			}
			// Gather last line
			wrappedText.append(line);
		} else {
			// Line doesn't need to be wrapped
			wrappedText.append(source);
		}
		return wrappedText.toString();
	}

	/**
	 * Draw a multi-line text string with bounding rectangle top starting at the
	 * y position. Depending on the current textAlign, the x position is either
	 * the rectangle left side, middle or right side.
	 * <p>
	 * Uses the given line height and effect.
	 * 
	 * @param text
	 *            the multi-line text to draw.
	 * @param x
	 *            the x position for top left corner of text rectangle.
	 * @param y
	 *            the y position for top left corner of the text rectangle.
	 * @param textLineHeight
	 *            the line height in pixels.
	 * @param effect
	 *            the effect to use for the text rendering. Can be one of
	 *            <code>EFFECT_NONE</code>, <code>EFFECT_SHADOW</code> or
	 *            <code>EFFECT_OUTLINE</code>.
	 */
	public void draw(Graphics2D g, String text, int x, int y,
			int textLineHeight, String effect) {
		if (effect.equals(AVKey.TEXT_EFFECT_SHADOW)) {
			this.textRenderer.setColor(backColor);
			this.draw(g, text, x + 1, y + 1, textLineHeight);
			this.textRenderer.setColor(textColor);
		} else if (effect.equals(AVKey.TEXT_EFFECT_OUTLINE)) {
			this.textRenderer.setColor(backColor);
			this.draw(g, text, x, y + 1, textLineHeight);
			this.draw(g, text, x + 1, y, textLineHeight);
			this.draw(g, text, x, y - 1, textLineHeight);
			this.draw(g, text, x - 1, y, textLineHeight);
			this.textRenderer.setColor(textColor);
		}
		// Draw normal text
		this.draw(g, text, x, y, textLineHeight);
	}

	/**
	 * Draw a multi-line text string with bounding rectangle top starting at the
	 * y position. Depending on the current textAlign, the x position is either
	 * the rectangle left side, middle or right side.
	 * <p>
	 * Uses the given line height.
	 * 
	 * @param text
	 *            the multi-line text to draw.
	 * @param x
	 *            the x position for top left corner of text rectangle.
	 * @param y
	 *            the y position for top left corner of the text rectangle.
	 * @param textLineHeight
	 *            the line height in pixels.
	 */
	public void draw(Graphics2D g, String text, int x, int y, int textLineHeight) {
		String[] lines = text.split("\n");
		for (String line : lines) {
			int xAligned = x;
			if (this.textAlign.equals(AVKey.ALIGN_CENTER))
				xAligned = x
						- (int) (this.textRenderer.getBounds(line).getWidth() / 2);
			else if (this.textAlign.equals(AVKey.ALIGN_RIGHT))
				xAligned = x
						- (int) (this.textRenderer.getBounds(line).getWidth());
			y += textLineHeight;
			this.textRenderer.draw(g, line, xAligned, y);
			y += this.lineSpacing;
		}
	}

	public LineMetrics getLineMetrics(String text) {
		return textRenderer.getLineMetrics(text);
	}
}
