package com.synaptix.swing;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.VerticalLayout;

public class JChoixPanel<E extends Component> extends JPanel implements
		Scrollable {

	private static final long serialVersionUID = -1025191027862179081L;

	private Color borderColor = Color.LIGHT_GRAY;

	private Color selectionStrokeColor = new Color(50, 64, 255, 128);

	private Color selectionFillColor = new Color(50, 64, 255, 128);

	private Color rolloverStrokeColor = new Color(255, 180, 7, 128);

	private Map<E, JSelectedPanel> map1;

	private Map<JSelectedPanel, E> map2;

	private E selectedComponent;

	public JChoixPanel() {
		super(new VerticalLayout(5));

		map1 = new HashMap<E, JSelectedPanel>();
		map2 = new HashMap<JSelectedPanel, E>();

		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		MyMouseAdapter mouseAdapter = new MyMouseAdapter();
		this.addMouseListener(mouseAdapter);
		this.addMouseMotionListener(mouseAdapter);
		Toolkit.getDefaultToolkit().addAWTEventListener(
				new MyAWTEventListener(), AWTEvent.MOUSE_EVENT_MASK);
	}

	public void addSelectionListener(SelectionListener<E> l) {
		listenerList.add(SelectionListener.class, l);
	}

	public void removeSelectionListener(SelectionListener<E> l) {
		listenerList.remove(SelectionListener.class, l);
	}

	@SuppressWarnings("unchecked")
	protected void fireSelectionChanged(E c) {
		SelectionListener<E>[] listeners = listenerList
				.getListeners(SelectionListener.class);

		for (SelectionListener<E> listener : listeners) {
			listener.selectionChanged(c);
		}
	}

	public E getSelectedComponent() {
		return selectedComponent;
	}

	public void setSelectedComponent(E selectedComponent) {
		E oldValue = this.selectedComponent;

		this.selectedComponent = selectedComponent;

		if (map1.containsKey(selectedComponent)) {
			map1.get(selectedComponent).setSelected(true);
		}
		if (oldValue != null) {
			map1.get(oldValue).setSelected(false);
		}
	}

	@SuppressWarnings("unchecked")
	protected void addImpl(Component comp, Object constraints, int index) {
		JSelectedPanel s = new JSelectedPanel(comp);
		map1.put((E) comp, s);
		map2.put(s, (E) comp);

		// Utils.addAllMouseListener(s, mouseListener);

		super.addImpl(s, constraints, index);
	}

	public void remove(int index) {
		super.remove(index);

		for (JSelectedPanel s : map2.keySet()) {
			boolean ok = false;
			int i = 0;
			while (i < super.getComponentCount() && !ok) {
				ok = super.getComponent(i).equals(s);
				i++;
			}
			if (!ok) {
				Component c = map2.get(s);

				map1.remove(c);
				map2.remove(s);

				// Utils.removeAllMouseListener(s, mouseListener);

				if (c == selectedComponent) {
					selectedComponent = null;
				}
			}
		}
	}

	public void remove(Component comp) {
		super.remove(map1.get(comp));
	}

	public void removeAll() {
		// for (int i = 0; i < super.getComponentCount(); i++) {
		// Utils.removeAllMouseListener(super.getComponent(i), mouseListener);
		// }

		super.removeAll();

		map1.clear();
		map2.clear();
		selectedComponent = null;
	}

	private void rebuildSelection() {
		for (JSelectedPanel s : map2.keySet()) {
			s.setSelected(s.isSelected());
		}
	}

	public void setSelectionStrokeColor(Color selectionBorderColor) {
		this.selectionStrokeColor = selectionBorderColor;

		rebuildSelection();
	}

	public Color getSelectionStrokeColor() {
		return selectionStrokeColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;

		rebuildSelection();
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setSelectionFillColor(Color selectionFillColor) {
		this.selectionFillColor = selectionFillColor;

		rebuildSelection();
	}

	public Color getSelectionFillColor() {
		return selectionFillColor;
	}

	@SuppressWarnings("unchecked")
	private void selecteComponent(Point p) {
		Component old = selectedComponent;

		Component c = JChoixPanel.this.getComponentAt(p);
		if (c instanceof JChoixPanel.JSelectedPanel) {
			JSelectedPanel pa = (JSelectedPanel) c;

			if ((pa == null && old != null)
					|| (pa != null && map2.get(pa) != old)) {
				if (c == null) {
					selectedComponent = null;
				} else {
					pa.setSelected(true);

					selectedComponent = map2.get(pa);
				}
			}
		} else if (c instanceof JChoixPanel) {
			selectedComponent = null;
		}

		if (old != selectedComponent) {
			if (old != null) {
				map1.get(old).setSelected(false);
			}

			fireSelectionChanged(selectedComponent);
		}
	}

	public Dimension getPreferredScrollableViewportSize() {
		return this.getPreferredSize();
	}

	public boolean getScrollableTracksViewportWidth() {
		return getParent() instanceof JViewport
				&& (((JViewport) getParent()).getWidth() > getPreferredSize().width);
	}

	public boolean getScrollableTracksViewportHeight() {
		return getParent() instanceof JViewport
				&& (((JViewport) getParent()).getHeight() > getPreferredSize().height);
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 50;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 25;
	}

	private void fireMouseEvent(MouseEvent e) {
		MouseListener[] ls = getMouseListeners();
		for (MouseListener l : ls) {
			switch (e.getID()) {
			case MouseEvent.MOUSE_PRESSED:
				l.mousePressed(e);
				break;
			case MouseEvent.MOUSE_CLICKED:
				l.mouseClicked(e);
				break;
			case MouseEvent.MOUSE_RELEASED:
				l.mouseReleased(e);
				break;
			case MouseEvent.MOUSE_ENTERED:
				l.mouseEntered(e);
				break;
			case MouseEvent.MOUSE_EXITED:
				l.mouseExited(e);
				break;
			}
		}
	}

	private final class MyAWTEventListener implements AWTEventListener {

		public void eventDispatched(AWTEvent event) {
			MouseEvent m = (MouseEvent) event;

			if (SwingUtilities.isDescendingFrom(m.getComponent(),
					JChoixPanel.this)
					&& m.getComponent() != JChoixPanel.this) {
				Point p = SwingUtilities.convertPoint(m.getComponent(), m
						.getPoint(), JChoixPanel.this);
				fireMouseEvent(new MouseEvent(JChoixPanel.this, m.getID(), m
						.getWhen(), m.getModifiers(), p.x, p.y, m
						.getClickCount(), m.isPopupTrigger(), m.getButton()));
			}
		}
	}

	private final class MyMouseAdapter extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			selecteComponent(e.getPoint());
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}

	private final class JSelectedPanel extends JPanel {

		private static final long serialVersionUID = -2024919156994859815L;

		private int thickness = 3;

		private boolean selected;

		private boolean rollover;

		private Color oldColor;

		public JSelectedPanel(Component c) {
			super(new BorderLayout());

			this.add(c, BorderLayout.CENTER);

			this.setOpaque(true);

			this.rollover = false;
			this.selected = false;
			updatePaint();
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			if (selected != this.selected) {
				this.selected = selected;

				updatePaint();
			}
		}

		public boolean isRollover() {
			return rollover;
		}

		public void setRollover(boolean rollover) {
			if (rollover != this.rollover) {
				this.rollover = rollover;

				updatePaint();
			}
		}

		private void updatePaint() {
			if (selected) {
				if (selectionStrokeColor != null) {
					this.setBorder(new LineRoundBorder(selectionStrokeColor,
							thickness));
				} else {
					this.setBorder(BorderFactory.createEmptyBorder(thickness,
							thickness, thickness, thickness));
				}

				if (oldColor == null) {
					oldColor = this.getBackground();
				}
				if (selectionFillColor != null) {
					// this.setBackground(selectionFillColor);
				}
			} else {
				if (borderColor != null) {
					this.setBorder(new LineRoundBorder(borderColor, thickness));
				} else {
					this.setBorder(BorderFactory.createEmptyBorder(thickness,
							thickness, thickness, thickness));
				}

				if (oldColor != null) {
					this.setBackground(oldColor);
					oldColor = null;
				}
			}

			if (rollover && rolloverStrokeColor != null) {
				this.setBorder(BorderFactory.createLineBorder(
						rolloverStrokeColor, thickness));
			}

			repaint();
		}

		public void paint(Graphics g) {
			g.clearRect(0, 0, this.getWidth(), this.getHeight());

			super.paint(g);
		}

		protected void paintComponent(Graphics g) {
			if (selected && selectionFillColor != null) {
				int x = 0;
				int y = 0;
				int width = this.getWidth();
				int height = this.getHeight();

				Graphics2D g2 = (Graphics2D) g.create();

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);

				g2.setColor(selectionFillColor);
				int round = thickness * 4;

				g2.fillRect(x + round, y, width - round, round);
				g2.fillRect(x, y + round, width, height - round * 2);
				g2.fillRect(x, y + height - round, width - round, round
						+ thickness);

				g2.fillArc(x, y, round * 2, round * 2, 90, 90);
				g2.fillArc(x + width - 1 - round * 2 + thickness / 2, y
						+ height - 1 - round * 2 + thickness / 2, round * 2,
						round * 2, 270, 90);

				g2.dispose();
			} else {
				super.paintComponent(g);
			}
		}
	}

	public interface SelectionListener<E extends Component> extends
			EventListener {

		public void selectionChanged(E c);

	}
}
