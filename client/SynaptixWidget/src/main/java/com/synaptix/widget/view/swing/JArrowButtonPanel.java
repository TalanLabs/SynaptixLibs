package com.synaptix.widget.view.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.widget.view.swing.helper.StaticImage;

public class JArrowButtonPanel extends JPanel {

	private static final long serialVersionUID = -5713338830104801836L;

	private static final Icon LEFT_ICON;

	private static final Icon RIGHT_ICON;

	private static final Icon TOP_ICON;

	private static final Icon BOTTOM_ICON;

	private static final Color NORMAL_PANEL_COLOR = new Color(224, 224, 224);

	private static final Color ROLLOVER_PANEL_COLOR = new Color(240, 240, 240);

	private final static Color NORMAL_IMPORTANT_COLOR = new Color(224, 128, 128);

	private final static Color ROLLOVER_IMPORTANT_COLOR = new Color(240, 128, 128);

	private JLabel upLabel;

	private JLabel downLabel;

	private MyLabel textLabel;

	private JComponent barPanel;

	private boolean hover;

	private int direction;

	private boolean important;

	static {
		LEFT_ICON = StaticImage.getImageScale(new ImageIcon(JArrowButtonPanel.class.getResource("iconDoubleLeft.png")), 16); //$NON-NLS-1$
		RIGHT_ICON = StaticImage.getImageScale(new ImageIcon(JArrowButtonPanel.class.getResource("iconDoubleRight.png")), 16); //$NON-NLS-1$

		TOP_ICON = StaticImage.getImageScale(new ImageIcon(JArrowButtonPanel.class.getResource("iconDoubleTop.png")), 16); //$NON-NLS-1$
		BOTTOM_ICON = StaticImage.getImageScale(new ImageIcon(JArrowButtonPanel.class.getResource("iconDoubleBottom.png")), 16); //$NON-NLS-1$
	}

	public JArrowButtonPanel(String text) {
		this(text, SwingConstants.LEFT);
	}

	public JArrowButtonPanel(String text, int direction) {
		super(new BorderLayout());

		this.hover = false;
		this.direction = direction;
		this.important = false;

		switch (direction) {
			case SwingConstants.LEFT:
				upLabel = new JLabel(LEFT_ICON, JLabel.CENTER);
				downLabel = new JLabel(LEFT_ICON, JLabel.CENTER);
				break;
			case SwingConstants.RIGHT:
				upLabel = new JLabel(RIGHT_ICON, JLabel.CENTER);
				downLabel = new JLabel(RIGHT_ICON, JLabel.CENTER);
				break;
			case SwingConstants.TOP:
				upLabel = new JLabel(TOP_ICON, JLabel.CENTER);
				downLabel = new JLabel(TOP_ICON, JLabel.CENTER);
				break;
			case SwingConstants.BOTTOM:
				upLabel = new JLabel(BOTTOM_ICON, JLabel.CENTER);
				downLabel = new JLabel(BOTTOM_ICON, JLabel.CENTER);
				break;
		}

		textLabel = new MyLabel(text);

		barPanel = buildContents();
		barPanel.setBackground(NORMAL_PANEL_COLOR);
		barPanel.setOpaque(true);
		barPanel.addMouseListener(new MyMouseListener());

		this.setOpaque(false);
		this.setBorder(BorderFactory.createEtchedBorder());

		this.add(barPanel, BorderLayout.CENTER);
	}

	private JComponent buildContents() {
		JComponent res = null;
		switch (direction) {
			case SwingConstants.LEFT:
			case SwingConstants.RIGHT:
				res = buildContentsLeftRight();
				break;
			case SwingConstants.TOP:
			case SwingConstants.BOTTOM:
				res = buildContentsTopBottom();
				break;
		}
		return res;
	}

	private JComponent buildContentsTopBottom() {
		FormLayout layout = new FormLayout("CENTER:DEFAULT:GROW(0.35),FILL:20PX,CENTER:DEFAULT:GROW(0.15),FILL:DEFAULT:NONE,CENTER:DEFAULT:GROW(0.15),FILL:20PX,CENTER:DEFAULT:GROW(0.35)", //$NON-NLS-1$
				"FILL:20PX"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(upLabel, cc.xy(2, 1));
		builder.add(textLabel, cc.xy(4, 1));
		builder.add(downLabel, cc.xy(6, 1));
		return builder.getPanel();
	}

	private JComponent buildContentsLeftRight() {
		FormLayout layout = new FormLayout("FILL:20PX", //$NON-NLS-1$
				"CENTER:DEFAULT:GROW(0.35),FILL:20PX,CENTER:DEFAULT:GROW(0.15),FILL:DEFAULT:NONE,CENTER:DEFAULT:GROW(0.15),FILL:20PX,CENTER:DEFAULT:GROW(0.35)"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(upLabel, cc.xy(1, 2));
		builder.add(textLabel, cc.xy(1, 4));
		builder.add(downLabel, cc.xy(1, 6));
		return builder.getPanel();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		upLabel.setEnabled(enabled);
		textLabel.setEnabled(enabled);
		downLabel.setEnabled(enabled);
	}

	/**
	 * Direction des fleches SwingConstants.LEFT ou SwingConstants.RIGHT ou SwingConstants.TOP ou SwingConstants.BOTTOM
	 * 
	 * @return
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * Change la direction des fleches SwingConstants.LEFT ou SwingConstants.RIGHT ou SwingConstants.TOP ou SwingConstants.BOTTOM
	 * 
	 * @param direction
	 */
	public void setDirection(int direction) {
		this.direction = direction;
		updateIcons();
	}

	/**
	 * Inverse la direction des fleches
	 */
	public void inverseDirection() {
		switch (direction) {
			case SwingConstants.LEFT:
				direction = SwingConstants.RIGHT;
				break;
			case SwingConstants.RIGHT:
				direction = SwingConstants.LEFT;
				break;
			case SwingConstants.TOP:
				direction = SwingConstants.BOTTOM;
				break;
			case SwingConstants.BOTTOM:
				direction = SwingConstants.TOP;
				break;
		}
		updateIcons();
	}

	private void updateIcons() {
		switch (direction) {
			case SwingConstants.LEFT:
				upLabel.setIcon(LEFT_ICON);
				downLabel.setIcon(LEFT_ICON);
				break;
			case SwingConstants.RIGHT:
				upLabel.setIcon(RIGHT_ICON);
				downLabel.setIcon(RIGHT_ICON);
				break;
			case SwingConstants.TOP:
				upLabel.setIcon(TOP_ICON);
				downLabel.setIcon(TOP_ICON);
				break;
			case SwingConstants.BOTTOM:
				upLabel.setIcon(BOTTOM_ICON);
				downLabel.setIcon(BOTTOM_ICON);
				break;
		}
	}

	public boolean isImportant() {
		return important;
	}

	public void setImportant(boolean important) {
		this.important = important;
		updateColor();
	}

	public void addActionListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}

	public void removeActionListener(ActionListener l) {
		listenerList.remove(ActionListener.class, l);
	}

	protected void fireActionPerformed() {
		ActionListener[] ls = listenerList.getListeners(ActionListener.class);
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "clicked");
		for (ActionListener l : ls) {
			l.actionPerformed(e);
		}
	}

	private void updateColor() {
		if (hover) {
			barPanel.setBackground(important ? ROLLOVER_IMPORTANT_COLOR : ROLLOVER_PANEL_COLOR);
		} else {
			barPanel.setBackground(important ? NORMAL_IMPORTANT_COLOR : NORMAL_PANEL_COLOR);
		}
	}

	private final class MyMouseListener extends MouseAdapter {

		@Override
		public void mouseClicked(MouseEvent e) {
			if ((isEnabled()) && (e.getButton() == MouseEvent.BUTTON1)) {
				inverseDirection();
				fireActionPerformed();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			if (isEnabled()) {
				hover = true;
				updateColor();
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (isEnabled()) {
				hover = false;
				updateColor();
			}
		}
	}

	private final class MyLabel extends JLabel {

		private static final long serialVersionUID = -2770943796133438228L;

		private boolean needsRotate;

		public MyLabel(String text) {
			super(text, JLabel.CENTER);
		}

		@Override
		public Dimension getPreferredSize() {
			if (direction == SwingConstants.LEFT || direction == SwingConstants.RIGHT) {
				return new Dimension(super.getPreferredSize().height, super.getPreferredSize().width);
			}
			return super.getPreferredSize();
		}

		@Override
		public Dimension getSize() {
			if (!needsRotate) {
				return super.getSize();
			}
			Dimension size = super.getSize();

			return new Dimension(size.height, size.width);
		}

		@Override
		public int getHeight() {
			return getSize().height;
		}

		@Override
		public int getWidth() {
			return getSize().width;
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D gr = (Graphics2D) g.create();

			if (direction == SwingConstants.LEFT || direction == SwingConstants.RIGHT) {
				gr.translate(0, getSize().getHeight());
				gr.transform(AffineTransform.getQuadrantRotateInstance(-1));
				needsRotate = true;
			}

			super.paintComponent(gr);
			needsRotate = false;

			gr.dispose();
		}
	}

	public final void showIcons(boolean visible) {
		upLabel.setVisible(visible);
		downLabel.setVisible(visible);
	}
}