package titleheader.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.synaptix.swing.utils.GraphicsHelper;

import titleheader.CardHeaderActionRenderer;
import titleheader.CardHeaderTitleRenderer;
import titleheader.JCardHeader;

public class BasicCardHeaderUI extends CardHeaderUI {

	public static ComponentUI createUI(JComponent h) {
		return new BasicCardHeaderUI();
	}

	private JCardHeader cardHeader;

	private CellRendererPane rendererPane;

	private PropertyChangeListener propertyChangeListener;

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		cardHeader = (JCardHeader) c;

		rendererPane = new CellRendererPane();
		c.add(rendererPane);

		propertyChangeListener = new MyPropertyChangeListener();
		cardHeader.addPropertyChangeListener(propertyChangeListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);

		c.remove(rendererPane);

		cardHeader.removePropertyChangeListener(propertyChangeListener);
	}

	private int getRightActionsWidth() {
		int bw = 0;
		Action[] rightAction = cardHeader.getRightActions();
		if (rightAction != null && rightAction.length > 0) {
			CardHeaderActionRenderer actionRenderer = cardHeader.getActionRenderer();

			for (Action action : rightAction) {
				Component ac = actionRenderer.getCardHeaderActionRendererComponent(cardHeader, cardHeader.isSelected(), action, false, false);
				rendererPane.add(ac);
				Dimension ps = ac.getPreferredSize();
				bw += ps.width;
			}
			bw = bw + (rightAction.length - 1) * 4;
		}
		return bw;
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		Insets insets = cardHeader.getInsets();

		int cellHeight = cardHeader.getCellHeight();
		CardHeaderTitleRenderer titleRenderer = cardHeader.getTitleRenderer();
		Component tc = titleRenderer.getCardHeaderTitleRendererComponent(cardHeader, cardHeader.getTitle(), cardHeader.isSelected());
		rendererPane.add(tc);
		Dimension titleSize = tc.getPreferredSize();

		int rbw = getRightActionsWidth();

		rendererPane.removeAll();
		return new Dimension(insets.left + titleSize.width + rbw + insets.right, insets.top + cellHeight + insets.bottom);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);

		Insets insets = cardHeader.getInsets();

		int rbw = getRightActionsWidth();
		int w = cardHeader.getWidth() - (insets.left + insets.right);

		paintBackground(g);

		paintTitle(g, insets.left, w - rbw);
		paintRightActions(g, insets.left + (w - rbw));

		rendererPane.removeAll();
	}

	protected void paintBackground(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		GraphicsHelper.activeAntiAliasing(g2);

		Insets insets = cardHeader.getInsets();

		int w = cardHeader.getWidth() - (insets.left + insets.right);
		int h = cardHeader.getHeight() - (insets.top + insets.bottom);

		g2.setColor(cardHeader.getBackground());
		g2.fillRect(insets.left, insets.top, w, h);

		g2.dispose();
	}

	protected void paintTitle(Graphics g, int x, int width) {
		Graphics2D g2 = (Graphics2D) g.create();

		GraphicsHelper.activeAntiAliasing(g2);

		Insets insets = cardHeader.getInsets();

		int h = cardHeader.getHeight() - (insets.top + insets.bottom);

		CardHeaderTitleRenderer titleRenderer = cardHeader.getTitleRenderer();
		Component titleComponent = titleRenderer.getCardHeaderTitleRendererComponent(cardHeader, cardHeader.getTitle(), cardHeader.isSelected());
		rendererPane.paintComponent(g2, titleComponent, cardHeader, x, insets.top, width, h, true);

		g2.dispose();
	}

	protected void paintRightActions(Graphics g, int x) {
		Action[] rightAction = cardHeader.getRightActions();
		if (rightAction != null && rightAction.length > 0) {
			Graphics2D g2 = (Graphics2D) g.create();

			GraphicsHelper.activeAntiAliasing(g2);

			Insets insets = cardHeader.getInsets();

			CardHeaderActionRenderer actionRenderer = cardHeader.getActionRenderer();

			int h = cardHeader.getHeight() - (insets.top + insets.bottom);

			int p = x;
			for (Action action : rightAction) {
				Component ac = actionRenderer.getCardHeaderActionRendererComponent(cardHeader, cardHeader.isSelected(), action, false, false);
				Dimension ps = ac.getPreferredSize();
				rendererPane.paintComponent(g2, ac, cardHeader, p, insets.top, ps.width, h, true);

				p += ps.width + 4;
			}

			g2.dispose();
		}
	}

	private void redrawList() {
		cardHeader.revalidate();
		cardHeader.repaint();
	}

	private class MyPropertyChangeListener implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			redrawList();
		}
	}
}
