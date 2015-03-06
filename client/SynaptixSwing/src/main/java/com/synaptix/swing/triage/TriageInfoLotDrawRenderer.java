package com.synaptix.swing.triage;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

public interface TriageInfoLotDrawRenderer {

	public Dimension getMinimumSize(Object source, Side side, LotDraw lotDraw);

	public void paintInfoLotDraw(Graphics g, Rectangle rect, Object source,
			Side side, LotDraw lotDraw, boolean isSelected);

}
