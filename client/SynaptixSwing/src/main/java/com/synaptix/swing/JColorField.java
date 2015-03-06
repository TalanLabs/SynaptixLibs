package com.synaptix.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JColorField extends JLabel {

	private static final long serialVersionUID = -5812059755646105739L;

	private Color color;

	public JColorField() {
		this(Color.RED);
	}

	public JColorField(Color color) {
		super();

		this.color = color;

		initActions();
		initComponents();
	}

	public void initActions() {
	}

	public void initComponents() {
		this.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		this.addMouseListener(new MyMouseListener());
		this.setPreferredSize(new Dimension(32, 20));
	}

	public void setColor(Color color) {
		Color old = getColor();
		this.color = color;
		repaint();

		firePropertyChange("color", old, color); //$NON-NLS-1$
	}

	public Color getColor() {
		return color;
	}

	protected void paintComponent(Graphics g) {
		g.setColor(color);
		Rectangle rect = g.getClipBounds();
		g.fillRect(rect.x, rect.y, rect.width, rect.height);
	}

	public void addChangeListener(ChangeListener changeListener) {
		listenerList.add(ChangeListener.class, changeListener);
	}

	public void removeChangeListener(ChangeListener changeListener) {
		listenerList.remove(ChangeListener.class, changeListener);
	}

	protected void fireChangeListener(ChangeEvent e) {
		ChangeListener[] list = listenerList.getListeners(ChangeListener.class);
		for (ChangeListener cl : list)
			cl.stateChanged(e);
	}

	private final class MyMouseListener extends MouseAdapter {

		private static final long serialVersionUID = 1959497701900655795L;

		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				Color c = JColorChooser.showDialog(JColorField.this,
						SwingMessages.getString("JColorField.1"), color); //$NON-NLS-1$
				if (c != null) {
					setColor(c);

					fireChangeListener(new ChangeEvent(JColorField.this));
				}
			}
		}
	}
}
