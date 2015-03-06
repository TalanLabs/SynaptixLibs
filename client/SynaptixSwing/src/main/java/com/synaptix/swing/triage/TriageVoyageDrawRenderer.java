package com.synaptix.swing.triage;

import java.awt.Graphics;
import java.awt.Rectangle;

public interface TriageVoyageDrawRenderer {

	public void paintVoyageDraw(Graphics g, Rectangle rect, Object source,
			Side side, LotDraw lotDraw, VoyageDraw voyageDraw,
			boolean isSelected);

}
