package com.synaptix.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import com.synaptix.swing.event.TriageModelEvent;
import com.synaptix.swing.event.TriageModelListener;
import com.synaptix.swing.event.TriageSelectionEvent;
import com.synaptix.swing.event.TriageSelectionListener;
import com.synaptix.swing.triage.LotDraw;
import com.synaptix.swing.triage.Side;
import com.synaptix.swing.triage.TriagePath;
import com.synaptix.swing.triage.VoyageDraw;

public class JTableTriage extends JComponent implements TriageModelListener,
		Scrollable {

	private static final long serialVersionUID = -5194267400202070101L;

	private static final int MIN_LOT_WIDTH = 150;

	private static final int SPACE_LOTS_HEIGHT = 6;

	private static final int SPACE_LOT_VOYAGE_WIDTH = 4;

	private static final int SPACE_LOT_VOYAGE_HEIGHT = 4;

	private static final int voyageWidth = 22;

	private static final int voyageHeight = 17;

	private static final int SPACE_VOYAGES_WIDTH = 2;

	private static final int START_LOT_X = 10;

	private static final int SPACE_LOTS_WIDTH = 15;

	private static final Color selectedLotDrawColor = new Color(50, 64, 255,
			128);

	private static final Color shadowLotDrawColor = new Color(200, 200, 200);

	private static final Color backgroundLotDrawColor = new Color(250, 250, 250);

	private static final Color borderLotDrawColor = new Color(128, 128, 128);

	private static final Color backgroundLeftRightColor = Color.WHITE;

	private static final Color highLineColor = new Color(0xC8B9E0);

	private TriageModel model;

	private int leftRightWidth;

	private int leftRightHeight;

	private TriagePath currentSelectedPath;

	private EventListenerList eventListenerList;

	private Side side;

	public JTableTriage(Side side, TriageModel model) {
		super();

		this.side = side;
		this.model = model;
		if (model != null) {
			model.addTriageModelListener(this);
		}

		initialize();
		initComponents();

		computeLeftRightSize();
	}

	private void initialize() {
		leftRightWidth = 0;
		leftRightHeight = 0;

		currentSelectedPath = null;

		eventListenerList = new EventListenerList();
	}

	private void initComponents() {
		this.setOpaque(true);
		this.addMouseListener(new MyMouseListener());
	}

	private int computeWidthByLotDraw(Side side, LotDraw lotDraw,
			int defaultWidth) {
		int count = 0;
		List<VoyageDraw> voyageDraws = model.getVoyageDraws(side, lotDraw);
		if (voyageDraws != null) {
			count = voyageDraws.size();
		}

		int width = 0;
		if (count > 0) {
			width = (count - 1) * (voyageWidth + SPACE_VOYAGES_WIDTH)
					+ voyageWidth + SPACE_LOT_VOYAGE_WIDTH * 2;
		}
		width = Math.max(width, MIN_LOT_WIDTH);

		return Math.max(width, defaultWidth);
	}

	private void computeLeftRightSize() {
		leftRightWidth = START_LOT_X;
		leftRightHeight = SPACE_LOTS_HEIGHT;

		List<LotDraw> lotDraws = model.getLotDraws(side);
		if (lotDraws != null && lotDraws.size() > 0) {
			for (LotDraw lotDraw : lotDraws) {
				int leftWidth = START_LOT_X;
				Dimension d = lotDraw.getRenderer().getMinimumSize(this, side,
						lotDraw);
				leftWidth = computeWidthByLotDraw(side, lotDraw, d.width)
						+ SPACE_LOTS_WIDTH;

				leftRightHeight += d.height + SPACE_LOTS_HEIGHT
						+ (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight);
				leftRightWidth = Math.max(leftRightWidth, leftWidth);
			}
		}

		this.setSize(leftRightWidth, leftRightHeight);
	}

	private void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	public void triageChanged(TriageModelEvent e) {
		currentSelectedPath = null;

		computeLeftRightSize();

		resizeAndRepaint();
	}

	public Dimension getPreferredSize() {
		return new Dimension(leftRightWidth, leftRightHeight);
	}

	public TriageModel getModel() {
		return model;
	}

	public TriagePath getSelectedPath() {
		return currentSelectedPath;
	}

	public void addTriageSelectionListener(TriageSelectionListener l) {
		eventListenerList.add(TriageSelectionListener.class, l);
	}

	public void removeTriageSelectionListener(TriageSelectionListener l) {
		eventListenerList.remove(TriageSelectionListener.class, l);
	}

	protected void fireTriageSelectionChanged() {
		TriageSelectionEvent event = new TriageSelectionEvent(this,
				currentSelectedPath, null);
		Object[] listeners = eventListenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TriageSelectionListener.class) {
				((TriageSelectionListener) listeners[i + 1])
						.selectionChange(event);
			}
		}
	}

	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension(800, 600);
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return getScrollableUnitIncrement(visibleRect, orientation, direction);
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		if (orientation == SwingUtilities.VERTICAL) {
			List<LotDraw> lotDraws = model.getLotDraws(side);
			if (lotDraws != null && lotDraws.size() > 0) {
				int lotDrawIndex = lotDrawIndexAtPoint(new Point(visibleRect.x,
						visibleRect.y));
				if (direction < 0) {
					lotDrawIndex--;
				}

				if (lotDrawIndex < 0) {
					return 50;
				} else {
					LotDraw lotDraw = lotDraws.get(lotDrawIndex);
					Rectangle rect = getLotDrawRectangle(lotDraw);
					int y = rect.y;
					int h = rect.height + SPACE_LOTS_HEIGHT;

					if (y < visibleRect.y) {
						int d = visibleRect.y - y;
						if (direction > 0) {
							return h - d;
						} else {
							return d;
						}
					} else {
						if (visibleRect.y < SPACE_LOTS_HEIGHT) {
							return h + SPACE_LOTS_HEIGHT;
						} else {
							return h;
						}
					}
				}
			} else {
				return 50;
			}
		} else {
			return 100;
		}
	}

	public boolean getScrollableTracksViewportWidth() {
		return getParent() instanceof JViewport
				&& (((JViewport) getParent()).getWidth() > getPreferredSize().width);
	}

	public boolean getScrollableTracksViewportHeight() {
		return getParent() instanceof JViewport
		&& (((JViewport) getParent()).getHeight() > getPreferredSize().height);
	}

	public LotDraw lotDrawAtPoint(Point p) {
		int index = lotDrawIndexAtPoint(p);
		if (index >= 0) {
			List<LotDraw> lotDraws = model.getLotDraws(side);
			LotDraw lotDraw = lotDraws.get(index);
			Rectangle rect = getLotDrawRectangle(lotDraw);
			if (rect.contains(p)) {
				return lotDraw;
			}
		}
		return null;
	}

	private int lotDrawIndexAtPoint(Point p) {
		List<LotDraw> lotDraws = model.getLotDraws(side);
		if (lotDraws != null && lotDraws.size() > 0) {
			if (p.y < SPACE_LOTS_HEIGHT) {
				return 0;
			}

			int y = SPACE_LOTS_HEIGHT;
			int i = 0;
			for (LotDraw lotDraw : lotDraws) {
				Dimension d = lotDraw.getRenderer().getMinimumSize(this, side,
						lotDraw);
				if (p.y >= y
						&& p.y < y + d.height + SPACE_LOTS_HEIGHT
								+ (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight)) {
					return i;
				}
				i++;
				y += d.height + SPACE_LOTS_HEIGHT
						+ (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight);
			}
		}
		return -1;
	}

	public Rectangle getLotDrawRectangle(LotDraw l) {
		List<LotDraw> lotDraws = model.getLotDraws(side);
		if (lotDraws != null && lotDraws.size() > 0) {
			int y = SPACE_LOTS_HEIGHT;

			for (LotDraw lotDraw : lotDraws) {
				Dimension d = lotDraw.getRenderer().getMinimumSize(this, side,
						lotDraw);
				if (lotDraw.equals(l)) {
					int w = computeWidthByLotDraw(side, lotDraw, d.width);
					Rectangle rect = new Rectangle(START_LOT_X, y, w, d.height
							+ (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight));
					return rect;
				}
				y += d.height + SPACE_LOTS_HEIGHT
						+ (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight);
			}
		}
		return null;
	}

	public VoyageDraw voyageDrawAtPoint(Point p) {
		LotDraw lotDraw = lotDrawAtPoint(p);
		if (lotDraw != null) {
			return voyageDrawAtPoint(lotDraw, p);
		}
		return null;
	}

	private VoyageDraw voyageDrawAtPoint(LotDraw lotDraw, Point p) {
		Rectangle rect = getLotDrawRectangle(lotDraw);
		Rectangle voyageDrawsRect = new Rectangle(rect.x, rect.y + rect.height
				- (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight), rect.width,
				(SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight));

		List<VoyageDraw> voyageDraws = model.getVoyageDraws(side, lotDraw);
		if (voyageDraws != null && voyageDraws.size() > 0) {
			int y1 = p.y - (voyageDrawsRect.y + SPACE_LOT_VOYAGE_HEIGHT);
			if (y1 >= 0 && y1 < voyageHeight) {
				int x1 = p.x - (voyageDrawsRect.x + SPACE_LOT_VOYAGE_WIDTH);

				int index = x1 / (voyageWidth + SPACE_VOYAGES_WIDTH);
				int x2 = x1 - index * (voyageWidth + SPACE_VOYAGES_WIDTH);

				if (x2 < voyageWidth && index >= 0
						&& index < voyageDraws.size()) {
					return voyageDraws.get(index);
				}
			}
		}
		return null;
	}

	private void paintLiaison(Graphics g, Rectangle rect) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setColor(highLineColor);
		g2.drawLine(rect.x, rect.y, rect.x + rect.width, rect.y);
	}

	private void paintVoyageDraw(Graphics g, LotDraw lotDraw,
			VoyageDraw voyageDraw, Rectangle rect) {
		Graphics2D g2 = (Graphics2D) g.create();

		boolean selected = false;
		if (currentSelectedPath != null
				&& currentSelectedPath.getVoyageDraw() != null
				&& currentSelectedPath.getVoyageDraw().equals(voyageDraw)) {
			selected = true;
		}

		g2.clipRect(rect.x, rect.y, rect.width, rect.height);

		voyageDraw.getRenderer().paintVoyageDraw(g2, rect, JTableTriage.this,
				side, lotDraw, voyageDraw, selected);

		g2.dispose();
	}

	private Area getLotDrawArea(Rectangle rect) {
		Area a = new Area(new Rectangle2D.Double(rect.x, rect.y,
				rect.width - 1, rect.height - SPACE_LOT_VOYAGE_HEIGHT - 1));
		a.add(new Area(new Rectangle2D.Double(rect.x + SPACE_LOT_VOYAGE_WIDTH,
				rect.y + rect.height - SPACE_LOT_VOYAGE_HEIGHT - 1, rect.width
						- SPACE_LOT_VOYAGE_WIDTH * 2 - 1,
				SPACE_LOT_VOYAGE_HEIGHT)));
		a.add(new Area(new Ellipse2D.Double(rect.x, rect.y + rect.height
				- SPACE_LOT_VOYAGE_HEIGHT * 2 - 1, SPACE_LOT_VOYAGE_WIDTH * 2,
				SPACE_LOT_VOYAGE_HEIGHT * 2)));
		a.add(new Area(new Ellipse2D.Double(rect.x + rect.width
				- SPACE_LOT_VOYAGE_WIDTH * 2 - 1, rect.y + rect.height
				- SPACE_LOT_VOYAGE_HEIGHT * 2 - 1, SPACE_LOT_VOYAGE_WIDTH * 2,
				SPACE_LOT_VOYAGE_HEIGHT * 2)));
		return a;
	}

	private void paintLotDraw(Graphics g, LotDraw lotDraw, Rectangle rect) {
		Graphics2D g2 = (Graphics2D) g.create();

		boolean selected = false;
		if (currentSelectedPath != null
				&& currentSelectedPath.getLotDraw().equals(lotDraw)) {
			selected = true;
		}

		Area lotDrawArea = getLotDrawArea(rect);
		Area shadowLotDrawArea = getLotDrawArea(rect);

		AffineTransform af = new AffineTransform();
		af.translate(3, 3);
		shadowLotDrawArea.transform(af);

		g2.setColor(shadowLotDrawColor);
		g2.fill(shadowLotDrawArea);

		g2.setColor(backgroundLotDrawColor);
		g2.fill(lotDrawArea);

		g2.setColor(borderLotDrawColor);
		g2.draw(lotDrawArea);

		Rectangle voyageDrawsRect = new Rectangle(rect.x, rect.y + rect.height
				- (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight), rect.width,
				(SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight));

		Rectangle infoLotDrawRect = new Rectangle(rect.x, rect.y, rect.width,
				rect.height - (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight));
		Rectangle clip = g2.getClipBounds();
		g2.clipRect(infoLotDrawRect.x + 1, infoLotDrawRect.y + 1,
				infoLotDrawRect.width - 2, infoLotDrawRect.height - 2);

		lotDraw.getRenderer().paintInfoLotDraw(g2, infoLotDrawRect,
				JTableTriage.this, side, lotDraw, selected);

		g2.setClip(clip.x, clip.y, clip.width, clip.height);

		if (selected) {
			g2.setColor(selectedLotDrawColor);
			g2.fill(lotDrawArea);
		}

		List<VoyageDraw> voyageDraws = model.getVoyageDraws(side, lotDraw);
		if (voyageDraws != null && voyageDraws.size() > 0) {
			int x = voyageDrawsRect.x + SPACE_LOT_VOYAGE_WIDTH;

			for (VoyageDraw voyageDraw : voyageDraws) {
				Rectangle voyageDrawRect = new Rectangle(x, voyageDrawsRect.y
						+ SPACE_LOT_VOYAGE_HEIGHT, voyageWidth, voyageHeight);

				paintVoyageDraw(g2, lotDraw, voyageDraw, voyageDrawRect);

				x += voyageWidth + SPACE_VOYAGES_WIDTH;
			}
		}

		g2.dispose();
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;

		Rectangle clip = g2.getClipBounds();

		Point left = clip.getLocation();
		Point right = new Point(clip.x + clip.width - 1, clip.y + clip.height
				- 1);

		g2.setColor(backgroundLeftRightColor);
		g2.fillRect(clip.x, clip.y, clip.width, clip.height);

		List<LotDraw> lotDraws = model.getLotDraws(side);
		if (lotDraws != null && lotDraws.size() > 0) {
			int a = lotDrawIndexAtPoint(left);
			int b = lotDrawIndexAtPoint(right);

			int count = lotDraws.size();
			if (count > 0 && a >= 0 && a < count) {
				b = b >= 0 && b < count ? b : count - 1;
				for (int index = a; index <= b; index++) {
					LotDraw lotDraw = lotDraws.get(index);

					Rectangle rect = getLotDrawRectangle(lotDraw);

					Rectangle liaisonRect = new Rectangle(0, rect.y, this
							.getWidth(), rect.height + SPACE_LOTS_HEIGHT);
					paintLiaison(g2, liaisonRect);

					paintLotDraw(g2, lotDraw, rect);
					//
					// for (LotDraw lotDraw : lotDrawsPosition.leftLotDraws) {
					// Dimension d = lotDraw.getRenderer().getMinimumSize(
					// JTableTriage.this, Side.LEFT, lotDraw);
					// int w = computeWidthByLotDraw(Side.LEFT, lotDraw,
					// d.width);
					//
					// int x1 = leftComponent.getWidth() - (x + w + 1);
					// Rectangle rect = new Rectangle(x1, y, w, d.height
					// + (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight));
					// paintLotDraw(g2, lotDraw, rect);
					//
					// x += w + SPACE_LOTS_WIDTH;
					// }
					// }
					//
					// if (side == Side.RIGHT) {
					// Rectangle liaisonRect = new Rectangle(0,
					// dateDrawPosition.y, rightComponent.getWidth(),
					// dateDrawPosition.height + SPACE_LOTS_HEIGHT);
					// paintLiaison(g2, liaisonRect);
					//
					// for (LotDraw lotDraw : lotDrawsPosition.rightLotDraws) {
					// Dimension d = lotDraw.getRenderer().getMinimumSize(
					// JTableTriage.this, Side.RIGHT, lotDraw);
					// int w = computeWidthByLotDraw(Side.RIGHT, lotDraw,
					// d.width);
					//
					// Rectangle rect = new Rectangle(x, y, w, d.height
					// + (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight));
					// paintLotDraw(g2, lotDraw, rect);
					//
					// x += w + SPACE_LOTS_WIDTH;
					// }
					// }
				}
			}
		}
	}

	private final class MyMouseListener extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			TriagePath selectedPath = null;

			LotDraw lotDraw = lotDrawAtPoint(e.getPoint());
			if (lotDraw != null && lotDraw.isSelected()) {
				VoyageDraw voyageDraw = voyageDrawAtPoint(lotDraw, e.getPoint());
				if (voyageDraw != null && voyageDraw.isSelected()) {
					selectedPath = new TriagePath(side, lotDraw, voyageDraw);
				} else {
					selectedPath = new TriagePath(side, lotDraw);
				}
			}

			boolean same = false;
			if (currentSelectedPath != null) {
				if (currentSelectedPath.equals(selectedPath)) {
					same = true;
				}
			} else if (selectedPath == null) {
				same = true;
			}

			if (!same) {
				currentSelectedPath = selectedPath;

				resizeAndRepaint();

				fireTriageSelectionChanged();
			}
		}
	}
}
