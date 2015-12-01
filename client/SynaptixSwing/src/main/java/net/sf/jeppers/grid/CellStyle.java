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
import java.text.Format;

import javax.swing.border.Border;

/**
 * Provides rendering information about a cell.
 * 
 * @author <a href="grom@capsicumcorp.com">Cameron Zemek</a>
 */
public interface CellStyle {
	/**
	 * Returns the formatting to use for rendering cell text.
	 * 
	 * @return	the format to use to render text
	 */
	public Format getFormat();
	
	/**
	 * Set the format to use to render cell text.
	 * 
	 * @param format	the cell format
	 */
	public void setFormat(Format format);
	
	/**
	 * Return the font to use for rendering cell text.
	 * 
	 * @return	font to render text
	 */
	public Font getFont();
	
	/**
	 * Set the font to use to render cell text.
	 * 
	 * @param f	font to render cell text
	 */
	public void setFont(Font f);
	
	/**
	 * Returns the border to paint around the cell. Painting of the cell border
	 * is handled by <code>JGrid</code> since borders are shared with 
	 * adjancent cells.
	 * 
	 * @return	cell border
	 */
	public Border getBorder();
	
	/**
	 * Sets the border to paint around the edges of the cell.
	 * 
	 * @param cellBorder	the new cell border
	 */
	public void setBorder(Border cellBorder);
	
	/**
	 * Return the color to use for rendering cell text.
	 * 
	 * @return cell text color
	 */
	public Color getForegroundColor();
	
	/**
	 * Sets the color to use for rendering cell text.
	 * 
	 * @param c	the new cell text color
	 */
	public void setForegroundColor(Color c);
	
	/**
	 * Returns the background color of the cell.
	 * 
	 * @return cell background color
	 */
	public Color getBackgroundColor();
	
	/**
	 * Sets the background color of the cell.
	 * 
	 * @param c	the cell background color
	 */
	public void setBackgroundColor(Color c);
	
	/**
	 * Returns the horizontal alignment of the cell text.
	 * 
	 * @return <code>SwingContants.LEFT</code> ||
	 * 			 <code>SwingConstants.CENTER</code> ||
	 * 			<code>SwingConstants.RIGHT</code>
	 */
	public int getHorizontalAlignment();
	
	/**
	 * Set the horizontal alignment of the cell text.
	 * 
	 * @param hAlign 	one of  <code>SwingContants.LEFT</code>,
	 * 			 <code>SwingConstants.CENTER</code>, or
	 * 			<code>SwingConstants.RIGHT</code>
	 */
	public void setHorizontalAlignment(int hAlign);
	
	/**
	 * Returns the vertical alignment of the cell text.
	 * 
	 * @return <code>SwingContants.TOP</code> ||
	 * 			 <code>SwingConstants.CENTER</code> ||
	 * 			<code>SwingConstants.BOTTOM</code>
	 */
	public int getVerticalAlignment();
	
	/**
	 * Set the vertical alignment of the cell text.
	 * 
	 * @param hAlign 	one of  <code>SwingContants.TOP</code>,
	 * 			 <code>SwingConstants.CENTER</code>, or
	 * 			<code>SwingConstants.BOTTOM</code>
	 */
	public void setVerticalAlignment(int vAlign);
	
	/**
	 * Returns the padding to be used around the cell text.
	 * 
	 * @return	an <code>Insets</code> that represents the padding
	 */
	public Insets getPadding();
	
	/**
	 * Sets the padding to be used around the cell text.
	 * 
	 * @param padding		the new cell padding
	 */
	public void setPadding(Insets padding);
	
	/**
	 * Return a new object that is a duplicate of this <code>CellStyle</code>
	 * 
	 * @return duplicate <code>CellStyle</code>
	 */
	public CellStyle copy();
}
