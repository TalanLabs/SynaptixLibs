package net.sf.jeppers.grid;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class JSpanGridHeader extends JPanel implements RulerModelListener,
		GridModelListener {

	private static final long serialVersionUID = 1453248854626841828L;

	private static final Icon DEFAULT_COLLAPSE_ICON = new ImageIcon(
			JSpanGridHeader.class.getResource("/images/moinsIcon.png"));

	private static final Icon DEFAULT_EXPAND_ICON = new ImageIcon(
			JSpanGridHeader.class.getResource("/images/plusIcon.png"));

	private static final int DEFAULT_SIZE = 20;

	private JComponent trait;

	private JGridHeader gridHeader;

	private RulerModel rulerModel;

	private List<Span> pointMap;

	private Icon collapseIcon;

	private Icon expandIcon;

	private int orientation;

	private List<Node> nodeList;

	public JSpanGridHeader(int orientation) {
		super(new BorderLayout());

		this.orientation = orientation;

		pointMap = new ArrayList<Span>();
		nodeList = new ArrayList<Node>();

		if (orientation == SwingConstants.VERTICAL) {
			trait = new VerticalTrait();
		} else {
			trait = new HorizontalTrait();
		}

		collapseIcon = DEFAULT_COLLAPSE_ICON;
		expandIcon = DEFAULT_EXPAND_ICON;

		this.add(trait, BorderLayout.CENTER);

	}

	public void setGridHeader(JGridHeader gridHeader) {
		if (this.gridHeader != null) {

			this.rulerModel.removeRulerModelListener(this);
			this.gridHeader.getViewport().getGridModel()
					.removeGridModelListener(this);
		}

		this.gridHeader = gridHeader;
		this.rulerModel = null;
		if (gridHeader != null) {
			if (orientation == SwingConstants.VERTICAL) {
				this.rulerModel = this.gridHeader.getRowModel();
				this.add(gridHeader, BorderLayout.EAST);
			} else {
				this.rulerModel = this.gridHeader.getColumnModel();
				this.add(gridHeader, BorderLayout.SOUTH);
			}

			this.rulerModel.addRulerModelListener(this);
			this.gridHeader.getViewport().getGridModel().addGridModelListener(
					this);
		}

		revalidate();
		repaint();
	}

	/**
	 * Permet de dire si le mode span est activer à l'affichage
	 * 
	 * @param showTrait
	 */
	public void setShowTrait(boolean showTrait) {
		trait.setVisible(showTrait);
	}

	/**
	 * Permet de savoir si le mode span est activer à l'affichage
	 * 
	 * @return
	 */
	public boolean isShowTrait() {
		return trait.isVisible();
	}

	/**
	 * Ajoute un span à partir de <code>start</code> jusqu'à <code>len</code>.
	 * 
	 * Le span est ajouté si il entrecroise pas un autre span
	 * 
	 * @param start
	 * @param len
	 */
	public void addSpan(int start, int len) {
		if (len <= 0 || start < 0) {
			throw new IllegalArgumentException("len <=0 or start < 0");
		}
		int end = start + len - 1;

		// Vérifier que des points ne font pas des intersections
		Iterator<Span> it = pointMap.iterator();
		while (it.hasNext()) {
			Span span = it.next();

			if (start == span.start
					|| (start >= span.start && start < span.end && end > span.end)
					|| (start < span.start && end >= span.start && end < span.end)) {
				throw new IllegalArgumentException(start + " -> " + len
						+ " intersection with " + span);
			}
		}

		pointMap.add(new Span(start, len));

		buildNodeList();

		repaint();
	}

	private void buildNodeList() {
		nodeList = buildNodeList(null, new ArrayList<Span>(pointMap));
	}

	private List<Node> buildNodeList(Node parent, List<Span> spanList) {
		List<Node> res = new ArrayList<Node>();

		if (spanList != null && !spanList.isEmpty()) {
			while (!spanList.isEmpty()) {
				// Recherche le span qui a le start plus petit
				Span petit = null;
				for (Span span : spanList) {
					if (petit == null || span.start < petit.start) {
						petit = span;
					}
				}

				spanList.remove(petit);

				List<Span> sList = new ArrayList<Span>();
				for (Span span : spanList) {
					if (span.start > petit.start && span.end <= petit.end) {
						sList.add(span);
					}
				}

				spanList.removeAll(sList);

				Node node = new Node();
				node.span = petit;
				node.parent = parent;
				node.childList = buildNodeList(node, sList);

				res.add(node);
			}
		}

		return res;
	}

	/**
	 * On efface le span
	 * 
	 * @param start
	 */
	public void removeSpan(int start) {
		pointMap.remove(start);

		buildNodeList();

		repaint();
	}

	/**
	 * Efface tous les spans
	 */
	public void clear() {
		pointMap.clear();

		buildNodeList();

		repaint();
	}

	/**
	 * Permet de donner l'état du span
	 * 
	 * @param start
	 * @param collapsed
	 */
	public void setCollapsed(int start, boolean collapsed) {
		Span span = pointMap.get(start);
		if (span != null) {
			setCollapsed(span, collapsed);
			repaint();
		}
	}

	private void setCollapsed(Span span, boolean collapsed) {
		if (collapsed && !span.collapsed) {
			collapse(span);
		} else if (!collapsed && span.collapsed) {
			expand(span);
		}
	}

	/**
	 * Permet de donner le même etat à tout les spans
	 * 
	 * @param collapsed
	 */
	public void setCollapsedAll(boolean collapsed) {
		setCollapsedAll(nodeList, collapsed);

		repaint();
	}

	private void setCollapsedAll(List<Node> nodeList, boolean collapsed) {
		if (nodeList != null && !nodeList.isEmpty()) {
			for (Node node : nodeList) {
				if (!collapsed) {
					setCollapsed(node.span, collapsed);
				}

				setCollapsedAll(node.childList, collapsed);
				if (collapsed) {
					setCollapsed(node.span, collapsed);
				}

			}
		}
	}

	/**
	 * Renvoie si le span est fermé
	 * 
	 * @param start
	 * @return
	 */
	public boolean isCollapsed(int start) {
		boolean res = false;
		Span span = pointMap.get(start);
		if (span != null) {
			res = span.collapsed;
		}
		return res;
	}

	public void setCollapseIcon(Icon collapseIcon) {
		if (collapseIcon == null) {
			throw new NullPointerException("collapseIcon is null");
		}
		this.collapseIcon = collapseIcon;
	}

	public void setExpandIcon(Icon expandIcon) {
		if (expandIcon == null) {
			throw new NullPointerException("expandIcon is null");
		}
		this.expandIcon = expandIcon;
	}

	private Node searchNode(Span span, List<Node> nodeList) {
		Node res = null;
		if (nodeList != null && !nodeList.isEmpty()) {
			Iterator<Node> it = nodeList.iterator();
			while (it.hasNext() && res == null) {
				Node node = it.next();
				if (node.span.equals(span)) {
					res = node;
				}
				if (res == null) {
					res = searchNode(span, node.childList);
				}
			}
		}
		return res;
	}

	public void rulerChanged(RulerModelEvent e) {
		this.rulerModel.removeRulerModelListener(this);

		for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
			Span span = searchSpanFromRow(i);
			if (span != null) {
				Node node = searchNode(span, nodeList);
				while (node != null && !node.span.collapsed) {
					node = node.parent;
				}
				if (node != null) {
					span = node.span;
					span.sizes[i - span.start - 1] = rulerModel.getSize(i);
					rulerModel.setSize(i, 0);
				}
			}
		}

		this.rulerModel.addRulerModelListener(this);

		repaint();
	}

	private void addReajusteSpan(int start, int end) {
		System.out.println(pointMap);

		System.out.println("add " + start + " " + end);
		if (nodeList != null && !nodeList.isEmpty()) {
			for (Node node : nodeList) {
				addReajusteSpan(node, start, end);
			}
		}

		setCollapsedAll(false);
		buildNodeList();

		System.out.println(pointMap);
	}

	private void addReajusteSpan(Node parent, int start, int end) {
		Span span = parent.span;

		List<Node> fils = parent.childList;
		if (fils != null && !fils.isEmpty()) {
			for (Node node : fils) {
				addReajusteSpan(node, start, end);
			}
		}

		if (span.end >= start) {
			int c = end - start + 1;

			if (span.start < start) {
				System.out.println("on reajuste " + span.start);

				span.length += c;
				span.end += c;

				int[] olds = span.sizes;
				span.sizes = new int[span.length - 1];

				if (span.collapsed) {
					int j = 0;
					for (int i = span.start + 1, k = 0; i <= span.end; i++, k++) {
						if (i >= start && i <= end) {
							span.sizes[k] = 20;// rulerModel.getSize(i);
						} else {
							span.sizes[k] = olds[j];
							j++;
						}

						System.out.println(i + " " + span.sizes[k]);
					}
				}
			} else {
				System.out.println("on decale " + span.start);

				span.start += c;
				span.end += c;
			}
		}
	}

	private void removeReajusteSpan(int start, int end) {
		System.out.println(pointMap);

		System.out.println("remove " + start + " " + end);
		if (nodeList != null && !nodeList.isEmpty()) {
			for (Node node : nodeList) {
				removeReajusteSpan(node, start, end);
			}
		}

		setCollapsedAll(false);
		buildNodeList();

		System.out.println(pointMap);
	}

	private void removeReajusteSpan(Node parent, int start, int end) {
		Span span = parent.span;

		List<Node> fils = parent.childList;
		if (fils != null && !fils.isEmpty()) {
			for (Node node : fils) {
				removeReajusteSpan(node, start, end);
			}
		}

		if (span.end >= start) {
			if (span.start < start) {
				int c = (span.end > end ? end : span.end) - start + 1;
				span.length -= c;
				span.end -= c;

				int[] olds = span.sizes;
				span.sizes = new int[span.length - 1];

				if (span.collapsed) {

				}

				System.out.println("on reajuste " + span.start);
			} else if (span.end <= end) {
				System.out.println("on efface " + span.start);

				pointMap.remove(span);
			} else {
				System.out.println("on decale " + span.start);

				int c = end - start + 1;

				span.start -= c;
				span.end -= c;
			}
		}
	}

	public void gridChanged(GridModelEvent event) {
		// int type = event.getType();
		// if (orientation == SwingConstants.VERTICAL) {
		// if (type == GridModelEvent.ROWS_INSERTED) {
		// addReajusteSpan(event.getFirstRow(), event.getLastRow());
		// } else if (type == GridModelEvent.ROWS_DELETED) {
		// removeReajusteSpan(event.getFirstRow(), event.getLastRow());
		// }
		// } else {
		// if (type == GridModelEvent.COLUMNS_INSERTED) {
		// addReajusteSpan(event.getFirstColumn(), event.getLastColumn());
		// } else if (type == GridModelEvent.COLUMNS_DELETED) {
		// removeReajusteSpan(event.getFirstColumn(), event
		// .getLastColumn());
		// }
		// }
		//
		// repaint();
	}

	private void expand(Span span) {
		this.rulerModel.removeRulerModelListener(this);

		for (int row = span.start + 1, i = 0; row <= span.end; row++, i++) {
			rulerModel.setSize(row, span.sizes[i]);
		}
		span.collapsed = false;

		this.rulerModel.addRulerModelListener(this);
	}

	private void collapse(Span span) {
		this.rulerModel.removeRulerModelListener(this);

		for (int row = span.start + 1, i = 0; row <= span.end; row++, i++) {
			span.sizes[i] = rulerModel.getSize(row);
			rulerModel.setSize(row, 0);
		}
		span.collapsed = true;

		this.rulerModel.addRulerModelListener(this);
	}

	private Span searchSpan(int i) {
		Span res = null;
		if (rulerModel != null) {
			res = searchSpanFromRow(rulerModel.getIndex(i));
		}
		return res;
	}

	private Span searchSpanFromRow(int row) {
		Span res = null;
		if (rulerModel != null) {
			if (row >= 0 && row < rulerModel.getCount()) {
				for (Span span : pointMap) {
					if (row >= span.start
							&& row <= span.end
							&& (res == null || (row - res.start > row
									- span.start))) {
						res = span;
					}
				}
			}
		}
		return res;
	}

	private final class Span {

		int start;

		int length;

		int end;

		int[] sizes;

		boolean collapsed;

		public Span(int start, int length) {
			super();
			this.start = start;
			this.length = length;

			this.end = start + length - 1;
			this.collapsed = false;
			this.sizes = new int[length - 1];
		}

		public String toString() {
			return "(start=" + start + ";length=" + length + ")";
		}
	}

	private final class Node {

		Span span;

		Node parent;

		List<Node> childList;

	}

	private final class VerticalTrait extends AbstractTrait {

		private static final long serialVersionUID = -7188701011707158922L;

		protected int getIndexToPoint(Point p) {
			return p.y;
		}

		public void paintLine(Graphics2D g2, Span span) {
			int endh = rulerModel.getSize(span.end);
			if (endh > 0) {
				int endy = rulerModel.getPosition(span.end) + endh / 2;

				g2.setColor(Color.GRAY);
				g2.drawLine(DEFAULT_SIZE / 2, rulerModel
						.getPosition(span.start)
						+ rulerModel.getSize(span.start) / 2, DEFAULT_SIZE / 2,
						endy);
				g2.drawLine(DEFAULT_SIZE / 2, endy, DEFAULT_SIZE * 3 / 4, endy);
			}
		}

		public int getIconX(Icon icon, Span span) {
			return (DEFAULT_SIZE - icon.getIconWidth()) / 2;
		}

		public int getIconY(Icon icon, Span span) {
			int y = rulerModel.getPosition(span.start);
			int h = rulerModel.getSize(span.start);
			return y + (h - icon.getIconHeight()) / 2;
		}
	}

	private final class HorizontalTrait extends AbstractTrait {

		private static final long serialVersionUID = -7188701011707158922L;

		protected int getIndexToPoint(Point p) {
			return p.x;
		}

		public void paintLine(Graphics2D g2, Span span) {
			int endw = rulerModel.getSize(span.end);
			if (endw > 0) {
				int endx = rulerModel.getPosition(span.end) + endw / 2;

				g2.setColor(Color.GRAY);
				g2.drawLine(rulerModel.getPosition(span.start)
						+ rulerModel.getSize(span.start) / 2, DEFAULT_SIZE / 2,
						endx, DEFAULT_SIZE / 2);
				g2.drawLine(endx, DEFAULT_SIZE / 2, endx, DEFAULT_SIZE * 3 / 4);
			}
		}

		public int getIconX(Icon icon, Span span) {
			int x = rulerModel.getPosition(span.start);
			int w = rulerModel.getSize(span.start);
			return x + (w - icon.getIconWidth()) / 2;
		}

		public int getIconY(Icon icon, Span span) {
			return (DEFAULT_SIZE - icon.getIconHeight()) / 2;
		}
	}

	private abstract class AbstractTrait extends JComponent implements
			MouseMotionListener, MouseListener {

		private static final long serialVersionUID = 5464557667898093790L;

		private Span rollover;

		public AbstractTrait() {
			super();

			rollover = null;

			this.addMouseMotionListener(this);
			this.addMouseListener(this);
			this.setPreferredSize(new Dimension(DEFAULT_SIZE, DEFAULT_SIZE));
		}

		protected abstract int getIndexToPoint(Point p);

		protected abstract void paintLine(Graphics2D g2, Span span);

		protected abstract int getIconX(Icon icon, Span span);

		protected abstract int getIconY(Icon icon, Span span);

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			if (rulerModel != null) {
				Graphics2D g2 = (Graphics2D) g.create();

				if (rollover != null) {
					paintLine(g2, rollover);
				}

				for (Span span : pointMap) {
					if (span.start >= 0 && span.start < rulerModel.getCount()
							&& (span.end) >= 0
							&& (span.end < rulerModel.getCount())) {
						if (rulerModel.getSize(span.start) > 0) {
							Icon icon = null;
							if (span.collapsed) {
								icon = expandIcon;
							} else {
								icon = collapseIcon;
							}
							icon.paintIcon(this, g2, getIconX(icon, span),
									getIconY(icon, span));
						}
					}
				}

				g2.dispose();
			}
		}

		public void mouseMoved(MouseEvent e) {
			rollover = searchSpan(getIndexToPoint(e.getPoint()));

			if (rollover != null
					&& rollover.start == rulerModel.getIndex(getIndexToPoint(e
							.getPoint()))) {
				this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			} else {
				this.setCursor(Cursor.getDefaultCursor());
			}

			repaint();
		}

		public void mouseDragged(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
			if (rulerModel != null && e.getButton() == MouseEvent.BUTTON1
					&& e.getClickCount() == 1) {
				Span span = searchSpan(getIndexToPoint(e.getPoint()));
				if (span.collapsed) {
					expand(span);
				} else {
					collapse(span);
				}

				rollover = searchSpan(getIndexToPoint(e.getPoint()));

				repaint();
			}
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
			rollover = null;

			repaint();
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}
	}
}