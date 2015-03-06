package org.jdesktop.swingx.mapviewer.layers;

import java.awt.Graphics2D;
import java.awt.Point;

import org.jdesktop.swingx.mapviewer.DrawContext;

public interface Layer {

	/**
	 * Permet de savoir si le calque peut etre pickable
	 * 
	 * @return
	 */
	public boolean isPickEnabled();

	/**
	 * Permet de rendre la calque pickable
	 * 
	 * @param pickEnabled
	 */
	public void setPickEnabled(boolean pickEnabled);

	/**
	 * Permet de recupérer une liste objet pick dans le calque
	 * 
	 * @param dc
	 * @param point
	 *            position par rapport à l'ecran
	 * @return
	 */
	public void pick(DrawContext dc, Point point);

	/**
	 * Permet de savoir si le calque est visible
	 * 
	 * @return
	 */
	public boolean isVisible();

	/**
	 * Permet de rendre le calque visible ou pas
	 * 
	 * @param visible
	 */
	public void setVisible(boolean visible);

	/**
	 * Permet de dessiner le calque
	 * 
	 * @param dc
	 */
	public void paint(Graphics2D g, DrawContext dc);

}
