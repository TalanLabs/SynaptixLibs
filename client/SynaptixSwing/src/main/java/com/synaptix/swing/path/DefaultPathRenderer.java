package com.synaptix.swing.path;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class DefaultPathRenderer extends AbstractPathRenderer {

	private Font nodeNormalFont = new Font("tahama", Font.PLAIN, 10);

	private Font lineFont = new Font("tahama", Font.PLAIN, 11);

	private Dimension normalDimension = new Dimension(11, 11);

	private Dimension lineDimension = new Dimension(50, 5);

	private Color nodeNormalColor = Color.darkGray;

	private Color lineColor = Color.black;

	public DefaultPathRenderer() {
		super();
	}

	public Color getNodeNormalColor() {
		return nodeNormalColor;
	}

	public void setNodeNormalColor(Color nodeNormalColor) {
		this.nodeNormalColor = nodeNormalColor;
	}

	public Dimension getNormalDimension() {
		return normalDimension;
	}

	public void setNormalDimension(Dimension normalDimension) {
		this.normalDimension = normalDimension;
	}

	public Font getNodeNormalFont() {
		return nodeNormalFont;
	}

	public void setNodeNormalFont(Font nodeNormalFont) {
		this.nodeNormalFont = nodeNormalFont;
	}

	public Dimension getLineDimension() {
		return lineDimension;
	}

	public void setLineDimension(Dimension lineDimension) {
		this.lineDimension = lineDimension;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public Font getLineFont() {
		return lineFont;
	}

	public void setLineFont(Font lineFont) {
		this.lineFont = lineFont;
	}

	public Font getLineFont(JPath path, boolean selected, int index1, int index2) {
		return lineFont;
	}

	public Color getLineColorText(JPath path, boolean selected, int index1, int index2) {
		return lineColor;
	}

	public Color getNodeColorText(JPath path, boolean selected, int index) {
		return nodeNormalColor;
	}

	public Font getNodeFont(JPath path, boolean selected, int index) {
		return nodeNormalFont;
	}

	public Dimension getNodeDimension(JPath path, boolean selected, int index) {
		return normalDimension;
	}

	public void paintNode(Graphics g, JPath path, boolean selected, int index) {
		Graphics2D g2 = (Graphics2D) g;

		Dimension size = getNodeDimension(path, selected, index);

		g2.setColor(Color.white);
		g2.fillOval(0, 0, size.width, size.height);

		g2.setColor(Color.black);
		g2.drawOval(0, 0, size.width - 1, size.height - 1);

		if (selected) {
			g2.setColor(path.getSelectionNodeColor());
			g2.fillOval(0, 0, size.width, size.height);
		}
	}

	public Dimension getLineDimension(JPath path, boolean selected1, int index1, boolean selected2, int index2) {
		return lineDimension;
	}

	public void paintLine(Graphics g, JPath path, boolean selected1, int index1, boolean selected2, int index2) {
		Graphics2D g2 = (Graphics2D) g;

		Dimension nodeSize1 = getNodeDimension(path, selected1, index1);
		Dimension nodeSize2 = getNodeDimension(path, selected2, index2);
		Dimension size = getLineDimension(path, selected1, index1, selected2, index2);

		int width = nodeSize1.width / 2 + size.width + nodeSize2.width / 2;

		g2.setColor(Color.white);
		g2.fillRect(0, 0, width, size.height);

		g2.setColor(Color.black);
		g2.drawRect(0, 0, width - 1, size.height - 1);

		if (selected1 && selected2) {
			g2.setColor(path.getSelectionNodeColor());
			g2.fillRect(0, 0, width, size.height);
		}
	}
}
