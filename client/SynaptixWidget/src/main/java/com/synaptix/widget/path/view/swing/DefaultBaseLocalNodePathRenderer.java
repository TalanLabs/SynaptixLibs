package com.synaptix.widget.path.view.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.List;

import com.synaptix.swing.path.AbstractPathRenderer;
import com.synaptix.swing.path.JPath;
import com.synaptix.swing.utils.Toolkit;

public class DefaultBaseLocalNodePathRenderer extends AbstractPathRenderer {

	protected Image normalBackImage;

	protected Image passageBackImage;

	protected Image redImage;

	protected Image greenImage;

	protected Image yellowImage;

	protected Image grayImage;

	protected Image passageGrayImage;

	protected Color lineColor = new Color(0x999999);

	protected int lineWidth = 120;

	protected int lineHeight = 15;

	protected int nodeSize = 25;

	protected BasicStroke arrowStroke = new BasicStroke(5.0f);

	protected Dimension passageNodeDimension = new Dimension(15, 15);

	protected Dimension nodeDimension = new Dimension(nodeSize, nodeSize);

	protected Dimension lineDimension = new Dimension(lineWidth, lineHeight);

	protected Font nodeImportantFont = new Font("Dialog", Font.BOLD, 12);

	protected Font lineFont = new Font("Dialog", Font.PLAIN, 11);

	protected Font nodeNormalFont = new Font("Dialog", Font.PLAIN, 9);

	private Color nodeImportantColor = Color.black;

	private Color nodeNormalColor = Color.darkGray;

	public DefaultBaseLocalNodePathRenderer() {
		super();

		buildImages();
	}

	protected void buildImages() {
		passageBackImage = Toolkit.resizeTrick(StaticNodeImage.nodeBackgroundImage, passageNodeDimension.width, passageNodeDimension.height);
		normalBackImage = Toolkit.resizeTrick(StaticNodeImage.nodeBackgroundImage, nodeSize, nodeSize);

		redImage = Toolkit.resizeTrick(StaticNodeImage.nodeRedForegroundImage, nodeSize, nodeSize);
		yellowImage = Toolkit.resizeTrick(StaticNodeImage.nodeYellowForegroundImage, nodeSize, nodeSize);
		greenImage = Toolkit.resizeTrick(StaticNodeImage.nodeGreenForegroundImage, nodeSize, nodeSize);
		grayImage = Toolkit.resizeTrick(StaticNodeImage.nodeGrayForegroundImage, nodeSize, nodeSize);
		passageGrayImage = Toolkit.resizeTrick(StaticNodeImage.nodeGrayForegroundImage, passageNodeDimension.width, passageNodeDimension.height);
	}

	private AbstractNodePathModel<? extends AbstractBaseLocalNode<?>> getPathModel(JPath path) {
		return (AbstractNodePathModel<?>) path.getPathModel();
	}

	private List<? extends AbstractBaseLocalNode<?>> getNodes(JPath path) {
		return getPathModel(path).getNodes();
	}

	public void setLineDimension(Dimension lineDimension) {
		this.lineDimension = lineDimension;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public void setLineFont(Font lineFont) {
		this.lineFont = lineFont;
	}

	@Override
	public Color getLineColorText(JPath path, boolean selected, int index1, int index2) {
		return lineColor;
	}

	@Override
	public Font getLineFont(JPath path, boolean selected, int index1, int index2) {
		return lineFont;
	}

	@Override
	public Color getNodeColorText(JPath path, boolean selected, int index) {
		List<? extends AbstractBaseLocalNode<?>> nodes = getNodes(path);

		boolean arrive = index > 0;
		boolean depart = index < nodes.size() - 1;
		boolean passage = nodes.get(index).getPause() == null || nodes.get(index).getPause().getMillis() == 0;
		if (!depart || !arrive || !passage) {
			return nodeImportantColor;
		}
		return nodeNormalColor;
	}

	@Override
	public Font getNodeFont(JPath path, boolean selected, int index) {
		List<? extends AbstractBaseLocalNode<?>> nodes = getNodes(path);

		boolean arrive = index > 0;
		boolean depart = index < nodes.size() - 1;
		boolean passage = nodes.get(index).getPause() == null || nodes.get(index).getPause().getMillis() == 0;
		if (!depart || !arrive || !passage) {
			return nodeImportantFont;
		}
		return nodeNormalFont;
	}

	public Dimension getNodeDimension(JPath path, boolean selected, int index) {
		List<? extends AbstractBaseLocalNode<?>> nodes = getNodes(path);

		boolean arrive = index > 0;
		boolean depart = index < nodes.size() - 1;
		boolean passage = nodes.get(index).getPause() == null || nodes.get(index).getPause().getMillis() == 0;
		if (!depart || !arrive || !passage) {
			return nodeDimension;
		}
		return passageNodeDimension;
	}

	public Dimension getLineDimension(JPath path, boolean selected1, int index1, boolean selected2, int index2) {
		return lineDimension;
	}

	public void paintLine(Graphics g, JPath path, boolean selected1, int index1, boolean selected2, int index2) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Dimension nodeSize1 = getNodeDimension(path, selected1, index1);
		Dimension nodeSize2 = getNodeDimension(path, selected2, index2);
		Dimension size = getLineDimension(path, selected1, index1, selected2, index2);

		int width = nodeSize1.width / 2 + size.width + nodeSize2.width / 2;

		g2.setColor(lineColor);
		g2.fillRect(0, 5, width, 5);

		g2.setStroke(arrowStroke);

		int n = lineHeight / 3;
		int l = n / 2;

		int m = width / 2;
		g2.drawLine(m + l, n * 2, m - l, lineHeight);
		g2.drawLine(m + l, n, m - l, 0);
	}

	public void paintNode(Graphics g, JPath path, boolean selected, int index) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Dimension size;
		boolean important;

		List<? extends AbstractBaseLocalNode<?>> nodes = getNodes(path);

		boolean arrive = index > 0;
		boolean depart = index < nodes.size() - 1;
		boolean passage = nodes.get(index).getPause() == null || nodes.get(index).getPause().getMillis() == 0;
		if (!depart || !arrive || !passage) {
			size = nodeDimension;
			important = true;
		} else {
			size = passageNodeDimension;
			important = false;
		}

		if (index == 0) {
			g2.drawImage(normalBackImage, 0, 0, null);
			g2.drawImage(greenImage, 0, 0, null);
		} else if (index == path.getPathModel().getNodeCount() - 1) {
			g2.drawImage(normalBackImage, 0, 0, null);
			g2.drawImage(redImage, 0, 0, null);
		} else if (important) {
			g2.drawImage(normalBackImage, 0, 0, null);
			g2.drawImage(yellowImage, 0, 0, null);
		} else {
			g2.drawImage(passageBackImage, 0, 0, null);
			g2.drawImage(passageGrayImage, 0, 0, null);
		}

		if (selected) {
			g2.setColor(path.getSelectionNodeColor());
			g2.fillRoundRect(0, 0, size.width, size.height, size.width / 5, size.height / 5);
		}
	}
}
