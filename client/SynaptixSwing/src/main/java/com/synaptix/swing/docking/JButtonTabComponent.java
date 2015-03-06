package com.synaptix.swing.docking;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicButtonUI;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class JButtonTabComponent extends JPanel {

	private static final long serialVersionUID = 6684343342823380648L;

	private String name;

	private Icon icon;

	private JLabel label;

	private Action closeAction;

	private Color upBackgroundColor;

	private Color downBackgroundColor;

	private boolean selected;

	public JButtonTabComponent(String name, Action closeAction,
			String toolTips, Icon icon) {
		super(new BorderLayout());

		this.name = name;
		this.icon = icon;
		this.closeAction = closeAction;
		this.setOpaque(false);

		selected = false;

		initComponents();

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initComponents() {
		label = new JLabel(name, icon, JLabel.LEFT);
	}

	private JComponent buildContents() {
		FormLayout layout = new FormLayout("p,3dlu,p", "p");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(label, cc.xy(1, 1));
		builder.add(new JTabButton(closeAction), cc.xy(3, 1));
		JPanel panel = builder.getPanel();
		panel.setOpaque(false);
		return panel;
	}

	public void setBackgroundColor(Color upBackgroundColor,
			Color downBackgroundColor) {
		this.upBackgroundColor = upBackgroundColor;
		this.downBackgroundColor = downBackgroundColor;
	}

	public Color getUpBackgroundColor() {
		return upBackgroundColor;
	}

	public Color getDownBackgroundColor() {
		return downBackgroundColor;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		if (selected) {
			label.setFont(new Font("arial", Font.BOLD, 12));
		} else {
			label.setFont(new Font("arial", Font.PLAIN, 12));
		}
	}

	public boolean isSelected() {
		return selected;
	}

	public void paint(Graphics g) {
		if (upBackgroundColor != null && downBackgroundColor != null) {
			Graphics2D g2 = (Graphics2D) g.create();

			if (selected) {
				g2.setPaint(new GradientPaint(0, 0, downBackgroundColor, 0,
						this.getHeight(), upBackgroundColor));
			} else {
				g2.setPaint(new GradientPaint(0, 0, upBackgroundColor, 0, this
						.getHeight(), downBackgroundColor));
			}
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());

			g2.dispose();
		}
		super.paint(g);
	}

	private class JTabButton extends JButton {

		private static final long serialVersionUID = 1058874152669939484L;

		private static final int NORMAL = 0;

		private static final int HOT = 1;

		private static final int SELECTED = 2;

		private final Color COLOR_BORDER = new Color(120, 120, 125);

		private int state;

		public JTabButton(Action action) {
			super(action);

			int size = 12;
			this.setPreferredSize(new Dimension(size, size));

			this.setUI(new BasicButtonUI());
			this.setContentAreaFilled(false);
			this.setFocusable(false);
			this.setBorderPainted(false);
			// Making nice rollover effect
			// we use the same listener for all buttons
			this.addMouseListener(new ButtonMouseListener());
			this.setRolloverEnabled(true);

			state = NORMAL;
		}

		public void updateUI() {
		}

		protected void paintComponent(Graphics g) {
			paintClose(g,
					new Rectangle(0, 0, this.getWidth(), this.getHeight()));
		}

		protected void paintClose(Graphics g, Rectangle closeRect) {
			int x = closeRect.x;
			int y = closeRect.y;
			switch (state) {
			case NORMAL:
				int[] shapeNormal = new int[] { x, y, x + 2, y, x + 4, y + 2,
						x + 5, y + 2, x + 7, y, x + 9, y, x + 9, y + 2, x + 7,
						y + 4, x + 7, y + 5, x + 9, y + 7, x + 9, y + 9, x + 7,
						y + 9, x + 5, y + 7, x + 4, y + 7, x + 2, y + 9, x,
						y + 9, x, y + 7, x + 2, y + 5, x + 2, y + 4, x, y + 2 };
				Polygon polygonClose = new Polygon();
				for (int i = 0; i < shapeNormal.length; i += 2)
					polygonClose.addPoint(shapeNormal[i], shapeNormal[i + 1]);
				g.setColor(Color.WHITE);
				g.fillPolygon(polygonClose);
				g.setColor(COLOR_BORDER);
				g.drawPolygon(polygonClose);
				break;
			case HOT:
				int[] shapeHot = new int[] { x, y, x + 2, y, x + 4, y + 2,
						x + 5, y + 2, x + 7, y, x + 9, y, x + 9, y + 2, x + 7,
						y + 4, x + 7, y + 5, x + 9, y + 7, x + 9, y + 9, x + 7,
						y + 9, x + 5, y + 7, x + 4, y + 7, x + 2, y + 9, x,
						y + 9, x, y + 7, x + 2, y + 5, x + 2, y + 4, x, y + 2 };
				Polygon polygonHot = new Polygon();
				for (int i = 0; i < shapeHot.length; i += 2)
					polygonHot.addPoint(shapeHot[i], shapeHot[i + 1]);
				g.setColor(new Color(252, 160, 160));
				g.fillPolygon(polygonHot);
				g.setColor(COLOR_BORDER);
				g.drawPolygon(polygonHot);
				break;
			case SELECTED:
				int[] shapeSelected = new int[] { x + 1, y + 1, x + 3, y + 1,
						x + 5, y + 3, x + 6, y + 3, x + 8, y + 1, x + 10,
						y + 1, x + 10, y + 3, x + 8, y + 5, x + 8, y + 6,
						x + 10, y + 8, x + 10, y + 10, x + 8, y + 10, x + 6,
						y + 8, x + 5, y + 8, x + 3, y + 10, x + 1, y + 10,
						x + 1, y + 8, x + 3, y + 6, x + 3, y + 5, x + 1, y + 3 };
				Polygon polygonSelected = new Polygon();
				for (int i = 0; i < shapeSelected.length; i += 2)
					polygonSelected.addPoint(shapeSelected[i],
							shapeSelected[i + 1]);
				g.setColor(new Color(252, 160, 160));
				g.fillPolygon(polygonSelected);
				g.setColor(COLOR_BORDER);
				g.drawPolygon(polygonSelected);
				break;
			}
		}

		private class ButtonMouseListener extends MouseAdapter {
			public void mouseEntered(MouseEvent e) {
				state = SELECTED;
				repaint();
			}

			public void mouseExited(MouseEvent e) {
				state = NORMAL;
				repaint();
			}
		}
	}
}
