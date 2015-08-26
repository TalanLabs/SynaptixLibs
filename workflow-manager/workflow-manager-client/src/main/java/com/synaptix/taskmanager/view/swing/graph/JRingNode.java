package com.synaptix.taskmanager.view.swing.graph;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import com.synaptix.swing.utils.Toolkit;
import com.synaptix.widget.path.view.swing.StaticNodeImage;

public class JRingNode extends JComponent {

	private static final long serialVersionUID = -2604608700655231331L;

	public enum ColorNode {
		green, yellow, red, gray, white, blue, black
	};

	public enum TypeNode {
		updateStatus, dataCheck, enrichment, manualEnrichment, externalProcess, startGroup, endGroup, interrogation
	};

	private static final Image nodeBackgroundImage = new ImageIcon(StaticNodeImage.class.getResource("/images/common/path/node_background.png")).getImage();

	private static final Image nodeBlueForegroundImage = new ImageIcon(JRingNode.class.getResource("/images/common/path/node_blue_foreground.png")).getImage();

	private static final Image nodeRedForegroundImage = new ImageIcon(JRingNode.class.getResource("/images/common/path/node_red_foreground.png")).getImage();

	private static final Image nodeGreenForegroundImage = new ImageIcon(JRingNode.class.getResource("/images/common/path/node_green_foreground.png")).getImage();

	private static final Image nodeGrayForegroundImage = new ImageIcon(JRingNode.class.getResource("/images/common/path/node_gray_foreground.png")).getImage();

	private static final Image nodeYellowForegroundImage = new ImageIcon(JRingNode.class.getResource("/images/common/path/node_yellow_foreground.png")).getImage();

	private static final Image nodeWhiteForegroundImage = new ImageIcon(JRingNode.class.getResource("/images/common/path/node_white_foreground.png")).getImage();

	private static final Image nodeBlackForegroundImage = new ImageIcon(JRingNode.class.getResource("/images/common/path/node_black_foreground.png")).getImage();

	private static final Image nodeDataCheckImage = new ImageIcon(JRingNode.class.getResource("/images/common/path/node_data_check.png")).getImage();

	private static final Image nodeUpdateStatusImage = new ImageIcon(JRingNode.class.getResource("/images/common/path/node_update_status.png")).getImage();

	private static final Image nodeManualEnrichmentImage = new ImageIcon(JRingNode.class.getResource("/images/common/path/node_manual_enrichment.png")).getImage();

	private static final Image nodeEnrichmentImage = new ImageIcon(JRingNode.class.getResource("/images/common/path/node_enrichment.png")).getImage();

	private static final Image nodeExternalProcessImage = new ImageIcon(JRingNode.class.getResource("/images/common/path/node_external_process.png")).getImage();

	private static final Image nodeStartGroupImage = new ImageIcon(JRingNode.class.getResource("/images/common/path/node_start_group.png")).getImage();

	private static final Image nodeEndGroupImage = new ImageIcon(JRingNode.class.getResource("/images/common/path/node_end_group.png")).getImage();

	private static final Image nodeInterrogationImage = new ImageIcon(JRingNode.class.getResource("/images/common/path/node_interrogation.png")).getImage();

	private final Image normalBackImage;

	private final Image redImage;

	private final Image blueImage;

	private final Image greenImage;

	private final Image yellowImage;

	private final Image whiteImage;

	private final Image grayImage;

	private final Image blackImage;

	private final Image dataCheckImage;

	private final Image updateStatusImage;

	private final Image manualEnrichmentImage;

	private final Image enrichmentImage;

	private final Image externalProcessImage;

	private final Image startGroupImage;

	private final Image endGroupImage;

	private final Image interrogationImage;

	private ColorNode colorNode;

	private TypeNode typeNode;

	public JRingNode(int nodeSize) {
		super();

		normalBackImage = Toolkit.resizeTrick(nodeBackgroundImage, nodeSize, nodeSize);

		redImage = Toolkit.resizeTrick(nodeRedForegroundImage, nodeSize, nodeSize);
		yellowImage = Toolkit.resizeTrick(nodeYellowForegroundImage, nodeSize, nodeSize);
		greenImage = Toolkit.resizeTrick(nodeGreenForegroundImage, nodeSize, nodeSize);
		grayImage = Toolkit.resizeTrick(nodeGrayForegroundImage, nodeSize, nodeSize);
		blueImage = Toolkit.resizeTrick(nodeBlueForegroundImage, nodeSize, nodeSize);
		whiteImage = Toolkit.resizeTrick(nodeWhiteForegroundImage, nodeSize, nodeSize);
		blackImage = Toolkit.resizeTrick(nodeBlackForegroundImage, nodeSize, nodeSize);

		dataCheckImage = Toolkit.resizeTrick(nodeDataCheckImage, nodeSize, nodeSize);
		updateStatusImage = Toolkit.resizeTrick(nodeUpdateStatusImage, nodeSize, nodeSize);
		manualEnrichmentImage = Toolkit.resizeTrick(nodeManualEnrichmentImage, nodeSize, nodeSize);
		enrichmentImage = Toolkit.resizeTrick(nodeEnrichmentImage, nodeSize, nodeSize);
		externalProcessImage = Toolkit.resizeTrick(nodeExternalProcessImage, nodeSize, nodeSize);
		startGroupImage = Toolkit.resizeTrick(nodeStartGroupImage, nodeSize, nodeSize);
		endGroupImage = Toolkit.resizeTrick(nodeEndGroupImage, nodeSize, nodeSize);
		interrogationImage = Toolkit.resizeTrick(nodeInterrogationImage, nodeSize, nodeSize);
	}

	public void setColorNode(ColorNode colorNode) {
		this.colorNode = colorNode;
		repaint();
	}

	public void setTypeNode(TypeNode typeNode) {
		this.typeNode = typeNode;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.drawImage(normalBackImage, 0, 0, null);

		if (colorNode != null) {
			switch (colorNode) {
			case green:
				g2.drawImage(greenImage, 0, 0, null);
				break;
			case red:
				g2.drawImage(redImage, 0, 0, null);
				break;
			case yellow:
				g2.drawImage(yellowImage, 0, 0, null);
				break;
			case gray:
				g2.drawImage(grayImage, 0, 0, null);
				break;
			case white:
				g2.drawImage(whiteImage, 0, 0, null);
				break;
			case blue:
				g2.drawImage(blueImage, 0, 0, null);
				break;
			case black:
				g2.drawImage(blackImage, 0, 0, null);
				break;
			}
		}

		if (typeNode != null) {
			switch (typeNode) {
			case dataCheck:
				g2.drawImage(dataCheckImage, 0, 0, null);
				break;
			case updateStatus:
				g2.drawImage(updateStatusImage, 0, 0, null);
				break;
			case enrichment:
				g2.drawImage(enrichmentImage, 0, 0, null);
				break;
			case manualEnrichment:
				g2.drawImage(manualEnrichmentImage, 0, 0, null);
				break;
			case externalProcess:
				g2.drawImage(externalProcessImage, 0, 0, null);
				break;
			case startGroup:
				g2.drawImage(startGroupImage, 0, 0, null);
				break;
			case endGroup:
				g2.drawImage(endGroupImage, 0, 0, null);
				break;
			case interrogation:
				g2.drawImage(interrogationImage, 0, 0, null);
				break;
			}
		}
	}
}