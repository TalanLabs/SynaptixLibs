/* 
 * Copyright (c) 2002, Cameron Zemek
 * 
 * This file is part of JGrid.
 * 
 * JGrid is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * JGrid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sf.jeppers.grid;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.io.Serializable;
import java.text.Format;

import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * Default implementation of <code>CellStyle</code>. 
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public class DefaultCellStyle implements CellStyle, Serializable {
    static final private Font DEFAULT_FONT = new Font("Dialog", Font.PLAIN, 12);
    static final private Border DEFAULT_BORDER = new EmptyBorder(0, 0, 0, 0);
    
	private Format formatter;
	private Font font;
	private Border border;
	private Color fgColor;
	private Color bgColor;
	private int horizontalAlignment;
	private int verticalAlignment;
	private Insets padding;

	/**
	 * Constructs the default CellStyle.
	 */
	public DefaultCellStyle() {
		formatter = null;
		font = DEFAULT_FONT;
		border = DEFAULT_BORDER;
		fgColor = Color.BLACK;
		bgColor = Color.WHITE;
		horizontalAlignment = SwingConstants.LEFT;
		verticalAlignment = SwingConstants.BOTTOM;
		padding = new Insets(2, 4, 2, 4);
	}

	public DefaultCellStyle(CellStyle parent) {
		formatter = parent.getFormat();
		horizontalAlignment = parent.getHorizontalAlignment();
		verticalAlignment = parent.getVerticalAlignment();
		font = parent.getFont();
		fgColor = parent.getForegroundColor();
		bgColor = parent.getBackgroundColor();
		padding = parent.getPadding();
		border = parent.getBorder();
	}
	
	public CellStyle copy(){
		return new DefaultCellStyle(this);
	}
	
	public Format getFormat(){
		return formatter;
	}
	
	public void setFormat(Format format){
		formatter = format;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font f) {
		font = f;
	}

	public Border getBorder() {
		return border;
	}

	public void setBorder(Border cellBorder) {
		border = cellBorder;
	}

	public Color getForegroundColor() {
		return fgColor;
	}

	public void setForegroundColor(Color c) {
		fgColor = c;
	}

	public Color getBackgroundColor() {
		return bgColor;
	}

	public void setBackgroundColor(Color c) {
		bgColor = c;
	}

	public int getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public void setHorizontalAlignment(int hAlign) {
		horizontalAlignment = hAlign;
	}

	public int getVerticalAlignment() {
		return verticalAlignment;
	}

	public void setVerticalAlignment(int vAlign) {
		verticalAlignment = vAlign;
	}

	public Insets getPadding() {
		return padding;
	}

	public void setPadding(Insets padding) {
		this.padding = padding;
	}
    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (! (obj instanceof DefaultCellStyle)) {
            return false;
        }
        DefaultCellStyle s = (DefaultCellStyle) obj;
        return horizontalAlignment == s.horizontalAlignment
                && verticalAlignment == s.verticalAlignment
                && (fgColor == s.fgColor ? true : fgColor.equals(s.fgColor))
                && (bgColor == s.bgColor ? true : bgColor.equals(s.bgColor))
                && (font == s.font ? true : font.equals(s.font))
                && (border == s.border ? true : border.equals(s.border))
                && (padding == s.padding ? true : padding.equals(s.padding))
                && (formatter == s.formatter ? true : formatter
                        .equals(s.formatter));
    }
    
    public int hashCode() {
        // Computed as per "Effective Java" book
        int result = 17;
        result = 37 * result + horizontalAlignment;
        result = 37 * result + verticalAlignment;
        if (fgColor != null)   result = 37 * result + fgColor.hashCode();
        if (bgColor != null)   result = 37 * result + bgColor.hashCode();
        if (font != null)      result = 37 * result + font.hashCode();
        if (border != null)    result = 37 * result + border.hashCode();
        if (padding != null)   result = 37 * result + padding.hashCode();
        if (formatter != null) result = 37 * result + formatter.hashCode();
        return result;
    }    
}
