package com.synaptix.swing.path;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

public interface PathRenderer {

	public abstract Font getNodeFont(JPath path, boolean selected, int index);

	public abstract Color getNodeColorText(JPath path, boolean selected, int index);

	public abstract Font getLineFont(JPath path, boolean selected, int index1, int index2);

	public abstract Color getLineColorText(JPath path, boolean selected, int index1, int index2);

	public abstract Dimension getNodeDimension(JPath path, boolean selected, int index);

	public abstract void paintNode(Graphics g, JPath path, boolean selected, int index);

	public abstract Dimension getLineDimension(JPath path, boolean selected1, int index1, boolean selected2, int index2);

	public abstract void paintLine(Graphics g, JPath path, boolean selected1, int index1, boolean selected2, int index2);

}
