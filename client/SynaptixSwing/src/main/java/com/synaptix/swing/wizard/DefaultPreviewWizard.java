package com.synaptix.swing.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.utils.TextImageCacheFactory;
import com.synaptix.swing.utils.TextImageCacheFactory.ImageRect;
import com.synaptix.swing.wizard.event.WizardAdapter;

public class DefaultPreviewWizard<E> extends JPanel {

	private static final long serialVersionUID = 1285853622897877165L;

	private Font textFont = new Font("tahama", Font.PLAIN, 11);

	private Font textSelectedFont = new Font("tahama", Font.BOLD, 12);

	private Color textColor = Color.darkGray;

	private Color textSelectedColor = Color.black;

	private Color fillNodeColor = Color.white;

	private Color strokeNodeColor = Color.black;

	private Color fillSelectedNodeColor = new Color(130, 160, 255);

	private Color strokeSelectedNodeColor = new Color(25, 32, 128);

	private boolean center = false;

	private int nodeSize = 10;

	private int lineWitdh = 3;

	private int lineHeight = 10;

	private Wizard<E> wizard;

	private MyPath path;

	private List<WizardPage<E>> pageList;

	public DefaultPreviewWizard(Wizard<E> wizard) {
		super(new BorderLayout());

		this.wizard = wizard;
		pageList = new ArrayList<WizardPage<E>>();

		initActions();
		initComponents();

		this.setBorder(BorderFactory.createEtchedBorder());

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initActions() {
	}

	private void createComponents() {
		path = new MyPath();
	}

	private void initComponents() {
		createComponents();

		wizard.addWizardListener(new MyWizardListener());
	}

	private JComponent buildContents() {
		FormLayout layout = new FormLayout("FILL:75DLU:NONE", //$NON-NLS-1$
				"FILL:DEFAULT:GROW(1.0)"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(path, cc.xy(1, 1));
		return builder.getPanel();
	}

	public void nextWizardPage(WizardPage<E> wizardPage,
			WizardPage<E> newWizardPage) {
		pageList.add(newWizardPage);

	}

	public void previousWizardPage(WizardPage<E> wizardPage,
			WizardPage<E> newWizardPage) {
		pageList.remove(wizardPage);

		path.revalidate();
		path.repaint();
	}

	public Font getTextFont() {
		return textFont;
	}

	public void setTextFont(Font textFont) {
		this.textFont = textFont;
		path.repaint();
	}

	public Font getTextSelectedFont() {
		return textSelectedFont;
	}

	public void setTextSelectedFont(Font textSelectedFont) {
		this.textSelectedFont = textSelectedFont;
		path.repaint();
	}

	public Color getTextColor() {
		return textColor;
	}

	public void setTextColor(Color textColor) {
		this.textColor = textColor;
		path.repaint();
	}

	public Color getTextSelectedColor() {
		return textSelectedColor;
	}

	public void setTextSelectedColor(Color textSelectedColor) {
		this.textSelectedColor = textSelectedColor;
		path.repaint();
	}

	public Color getFillNodeColor() {
		return fillNodeColor;
	}

	public void setFillNodeColor(Color fillNodeColor) {
		this.fillNodeColor = fillNodeColor;
		path.repaint();
	}

	public Color getStrokeNodeColor() {
		return strokeNodeColor;
	}

	public void setStrokeNodeColor(Color strokeNodeColor) {
		this.strokeNodeColor = strokeNodeColor;
		path.repaint();
	}

	public Color getFillSelectedNodeColor() {
		return fillSelectedNodeColor;
	}

	public void setFillSelectedNodeColor(Color fillSelectedNodeColor) {
		this.fillSelectedNodeColor = fillSelectedNodeColor;
		path.repaint();
	}

	public Color getStrokeSelectedNodeColor() {
		return strokeSelectedNodeColor;
	}

	public void setStrokeSelectedNodeColor(Color strokeSelectedNodeColor) {
		this.strokeSelectedNodeColor = strokeSelectedNodeColor;
		path.repaint();
	}

	public boolean isCenter() {
		return center;
	}

	public void setCenter(boolean center) {
		this.center = center;
		path.repaint();
	}

	public int getNodeSize() {
		return nodeSize;
	}

	public void setNodeSize(int nodeSize) {
		this.nodeSize = nodeSize;
		path.repaint();
	}

	public int getLineWitdh() {
		return lineWitdh;
	}

	public void setLineWitdh(int lineWitdh) {
		this.lineWitdh = lineWitdh;
		path.repaint();
	}

	public int getLineHeight() {
		return lineHeight;
	}

	public void setLineHeight(int lineHeight) {
		this.lineHeight = lineHeight;
		path.repaint();
	}

	private final class MyWizardListener extends WizardAdapter<E> {

		public void selectedWizardPage(Wizard<E> wizard,
				WizardPage<E> wizardPage) {
			if (pageList.contains(wizardPage)) {
				int index = pageList.indexOf(wizardPage);
				for (int i = pageList.size() - 1; i > index; i--) {
					pageList.remove(i);
				}
			} else {
				pageList.add(wizardPage);
			}
			path.revalidate();
			path.repaint();
		}
	}

	private final class MyPath extends JComponent {

		private static final long serialVersionUID = -5270523558250786586L;

		private TextImageCacheFactory textImageCacheFactory;

		private int decalageX = 5;

		private int decalageY = 5;

		public MyPath() {
			super();

			textImageCacheFactory = new TextImageCacheFactory();

			this.setOpaque(true);
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2 = (Graphics2D) g.create(0, 0, this.getWidth(), this
					.getHeight());

			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);

			int msize = nodeSize / 2;

			if (pageList != null && !pageList.isEmpty()) {
				int maxh = decalageY + pageList.size() * nodeSize
						+ (pageList.size() - 1) * lineHeight;
				int mh = center ? this.getHeight() / 2 : this.getHeight();
				int y = mh < maxh ? -maxh + mh : decalageY;
				for (int i = 0; i < pageList.size(); i++) {
					WizardPage<E> wizardPage = pageList.get(i);

					if (i < pageList.size() - 1) {
						g2.setColor(Color.white);
						g2.fillRect(decalageX + (nodeSize - lineWitdh) / 2, y
								+ msize, lineWitdh, nodeSize + lineHeight);

						g2.setColor(Color.black);
						g2.drawRect(decalageX + (nodeSize - lineWitdh) / 2, y
								+ msize, lineWitdh - 1, nodeSize + lineHeight
								- 1);

						g2.setColor(fillNodeColor);
						g2.fillOval(decalageX, y, nodeSize, nodeSize);

						g2.setColor(strokeNodeColor);
						g2.drawOval(decalageX, y, nodeSize - 1, nodeSize - 1);

						ImageRect imageRect = textImageCacheFactory
								.getImageRect(wizardPage.getTitle(), textFont,
										textColor, null, 0);
						Rectangle rect = imageRect.getRectangle();
						textImageCacheFactory.paintRotateText(g2, decalageX
								+ nodeSize + 5, y
								+ (msize - (int) rect.getCenterY()), wizardPage
								.getTitle(), textFont, textColor, null, 0);
					} else {
						Shape shape = new Polygon(new int[] { decalageX,
								decalageX, decalageX + nodeSize }, new int[] {
								y, y + nodeSize, y + msize }, 3);

						g2.setColor(fillSelectedNodeColor);
						// g2.fillOval(decalageX, y, size, size);
						g2.fill(shape);

						g2.setColor(strokeSelectedNodeColor);
						// g2.drawOval(decalageX, y, size - 1, size - 1);
						g2.draw(shape);

						ImageRect imageRect = textImageCacheFactory
								.getImageRect(wizardPage.getTitle(),
										textSelectedFont, textSelectedColor,
										null, 0);
						Rectangle rect = imageRect.getRectangle();
						textImageCacheFactory.paintRotateText(g2, decalageX
								+ nodeSize + 5, y
								+ (msize - (int) rect.getCenterY()), wizardPage
								.getTitle(), textSelectedFont,
								textSelectedColor, null, 0);
					}

					y += lineHeight + nodeSize;
				}
			}

			g2.dispose();
		}
	}
}
