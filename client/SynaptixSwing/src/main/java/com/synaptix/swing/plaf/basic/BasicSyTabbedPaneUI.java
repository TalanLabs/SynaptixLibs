package com.synaptix.swing.plaf.basic;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;

import com.synaptix.swing.JSyTabbedPane;
import com.synaptix.swing.plaf.SyTabbedPaneUI;

public class BasicSyTabbedPaneUI extends SyTabbedPaneUI {

	private static final int[] TOP_LEFT_CORNER = new int[] { 0, 6, 1, 5, 1, 4,
			4, 1, 5, 1, 6, 0 };

	private static final int[] TOP_RIGHT_CORNER = new int[] { -6, 0, -5, 1, -4,
			1, -1, 4, -1, 5, 0, 6 };

	private static final Insets INSETS = new Insets(1, 1, 3, 3);

	private static final int TAB_AREA_HEIGHT = 24;

	private static final int CHEVRON_WIDTH = 24;

	private static final int CHEVRON_HEIGHT = 10;

	private static final int MAXIMIZE_WIDTH = 18;

	private static final int MAXIMIZE_HEIGHT = 10;

	private static final int ALLCLOSE_WIDTH = 18;

	private static final int ALLCLOSE_HEIGHT = 10;

	private static final int TAB_BETWEEN = 6;

	private static final int TABBED_SPACE = MAXIMIZE_WIDTH + TAB_BETWEEN * 3
			+ ALLCLOSE_WIDTH;

	private static final int CLOSE_WIDTH = 10;

	private static final int NORMAL = 0;

	private static final int HOT = 1;

	private static final int SELECTED = 2;

	private static final int NONE = 3;

	private Color COLOR_BORDER = new Color(120, 120, 125);

	private JSyTabbedPane tabPane;

	private boolean showChevron = false;

	private int chevronImageState = NORMAL;

	private Rectangle chevronRect = null;

	private String chevronString = ""; //$NON-NLS-1$

	private int maximizeImageState = NORMAL;

	private Rectangle maxRect = null;

	private int allCloseImageState = NORMAL;

	private Rectangle allCloseRect = null;

	private List<TabRun> listTabRun = new ArrayList<TabRun>();

	private List<TabItem> listTabItem = new ArrayList<TabItem>();

	private Rectangle lastBound = new Rectangle(-1, -1, -1, -1);

	private int sens = -1;

	private Handler handler;

	private ChangeListener tabChangeListener;

	private MouseListener mouseListener;

	private FocusListener focusListener;

	private Component visibleComponent = null;

	private JPopupMenu chevronPopupMenu;

	private ChevronPopupMenuListener chevronPopupMenuListener;

	public static ComponentUI createUI(JComponent c) {
		return new BasicSyTabbedPaneUI();
	}

	@Override
	public void installUI(JComponent c) {
		tabPane = (JSyTabbedPane) c;

		chevronPopupMenu = new JPopupMenu();

		chevronPopupMenuListener = new ChevronPopupMenuListener();
		chevronPopupMenu.addPopupMenuListener(chevronPopupMenuListener);

		c.setLayout(createLayoutManager());

		createTabItems();

		installListeners();
	}

	@Override
	public void uninstallUI(JComponent c) {
		uninstallListeners();
		
		chevronPopupMenu.removePopupMenuListener(chevronPopupMenuListener);
		chevronPopupMenu = null;
		c.setLayout(null);
		tabPane = null;
	}

	private void createTabItems() {
		int selectedIndex = tabPane.getSelectedIndex();
		int count = tabPane.getTabCount();

		listTabItem.clear();
		for (int i = 0; i < count; i++) {
			TabItem tabItem = new TabItem();
			if (selectedIndex != i)
				tabItem.closeImageState = NONE;
			else
				tabItem.closeImageState = NORMAL;
			listTabItem.add(tabItem);
		}
	}

	private LayoutManager createLayoutManager() {
		return new TabbedPaneLayout();
	}

	private boolean resetTabItems() {
		boolean change = false;
		int selectedIndex = tabPane.getSelectedIndex();
		for (int i = 0; i < listTabItem.size(); i++) {
			TabItem tabItem = listTabItem.get(i);
			int a = tabItem.closeImageState;
			if (selectedIndex != i) {
				tabItem.closeImageState = NONE;
			} else {
				tabItem.closeImageState = NORMAL;
			}
			if (a != tabItem.closeImageState)
				change = true;
		}
		return change;
	}

	public Dimension getMinimumSize(JComponent c) {
		return null;
	}

	public Dimension getMaximumSize(JComponent c) {
		return null;
	}

	public void paint(Graphics g, JComponent c) {
		ensureCurrentLayout();

		paintTabbed(g);
		paintTabArea(g);

		update(g);
		paintTabs(g);
	}

	private Polygon buildTabbedPolygon(int x, int y, int w, int h) {
		int[] left = TOP_LEFT_CORNER;
		int[] right = TOP_RIGHT_CORNER;

		Polygon polygon = new Polygon();
		polygon.addPoint(x, y + h);
		for (int i = 0; i < left.length; i += 2) {
			polygon.addPoint(x + left[i], y + left[i + 1]);
		}
		for (int i = 0; i < right.length; i += 2) {
			polygon.addPoint(x + w + right[i], y + right[i + 1]);
		}
		polygon.addPoint(x + w, y + h);
		return polygon;
	}

	private Polygon buildTabAreaPolygon(int x, int y, int w, int h) {
		int[] left = TOP_LEFT_CORNER;
		int[] right = TOP_RIGHT_CORNER;

		Polygon polygon = new Polygon();
		polygon.addPoint(x, y + h);
		for (int i = 0; i < left.length; i += 2) {
			polygon.addPoint(x + left[i], y + left[i + 1]);
		}
		for (int i = 0; i < right.length; i += 2) {
			polygon.addPoint(x + w + right[i], y + right[i + 1]);
		}
		polygon.addPoint(x + w, y + h);
		return polygon;
	}

	private Polygon buildTabPolygon(int x, int y, int width, int height) {
		int[] left = TOP_LEFT_CORNER;
		int d = height - 12;
		int[] right = { 0, 0, 0, 1, 2, 1, 3, 2, 5, 2, 6, 3, 7, 3, 9, 5, 10, 5,
				11, 6, 11 + d, 6 + d, 12 + d, 7 + d, 13 + d, 7 + d, 15 + d,
				9 + d, 16 + d, 9 + d, 17 + d, 10 + d, 19 + d, 10 + d, 20 + d,
				11 + d, 22 + d, 11 + d, 23 + d, 12 + d };
		Polygon polygon = new Polygon();
		polygon.addPoint(x, y + height);
		for (int i = 0; i < left.length; i += 2) {
			polygon.addPoint(x + left[i], y + left[i + 1]);
		}
		for (int i = 0; i < right.length; i += 2) {
			polygon.addPoint(x + (width - 23 - d) + right[i], y + right[i + 1]);
		}
		return polygon;
	}

	protected void paintTabbed(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int width = tabPane.getWidth();
		int height = tabPane.getHeight();
		Insets insets = INSETS;

		// border.paintBorder(tabPane, g, 0, 0, width, height);

		int x = insets.left;
		int y = insets.top;
		int w = width - insets.right - insets.left - 1;
		int h = height - insets.top - insets.bottom - 1;

		paintTabbedShadow(g);

		Polygon polygon = buildTabbedPolygon(x, y, w, h);

		Color control = UIManager.getColor("control"); //$NON-NLS-1$
		g2.setColor(control);
		g2.fillPolygon(polygon);

		g2.setColor(COLOR_BORDER);
		g2.drawPolygon(polygon);
	}

	private void paintTabbedShadow(Graphics g) {
		int width = tabPane.getWidth();
		int height = tabPane.getHeight();

		Polygon polygon2 = buildTabbedPolygon(0, 0, width - 3, height - 3);
		Color shadow = UIManager.getColor("controlShadow"); //$NON-NLS-1$
		if (shadow == null) {
			shadow = Color.GRAY;
		}
		Color lightShadow = new Color(shadow.getRed(), shadow.getGreen(),
				shadow.getBlue(), 170);
		Color lighterShadow = new Color(shadow.getRed(), shadow.getGreen(),
				shadow.getBlue(), 70);

		g.setColor(shadow);
		g.drawPolygon(polygon2);
		// Shadow line 1
		g.setColor(lightShadow);
		polygon2.translate(1, 1);
		g.drawPolygon(polygon2);
		// Shadow line2
		g.setColor(lighterShadow);
		polygon2.translate(1, 1);
		g.drawPolygon(polygon2);
	}

	private void paintTabArea(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int width = tabPane.getWidth();
		Insets insets = INSETS;

		int x = insets.left;
		int y = insets.top;
		int w = width - insets.right - insets.left - 1;
		int h = TAB_AREA_HEIGHT;

		Polygon polygon = buildTabAreaPolygon(x, y, w, h);
		Color control = UIManager.getColor("control"); //$NON-NLS-1$
		g2.setColor(control);
		g2.fillPolygon(polygon);
		g2.setColor(COLOR_BORDER);
		g2.drawPolygon(polygon);
	}

	protected void update(Graphics g) {
		int width = tabPane.getWidth();
		Insets insets = INSETS;
		int x = insets.left;
		int w = width - insets.right - insets.left - 1;

		int selectedIndex = tabPane.getSelectedIndex();
		int count = tabPane.getTabCount();
		listTabRun.clear();
		int sommeSize = 0;

		FontMetrics fm = g.getFontMetrics();
		for (int index = 0; index < count; index++) {
			int size = 0;
			Icon icon = tabPane.getIconAt(index);
			String title = tabPane.getTitleAt(index);

			// Icon
			if (icon != null) {
				if ((index == selectedIndex)
						|| (index != selectedIndex && tabPane
								.isShowUnselectedImage())) {
					size += TAB_BETWEEN + 16;
				}
			}

			// Text
			size += TAB_BETWEEN + fm.stringWidth(title);

			// Close
			if (tabPane.isShowClose()) {
				if ((index == selectedIndex)
						|| (index != selectedIndex && tabPane
								.isShowUnselectedClose())) {
					size += TAB_BETWEEN + 10;
				}
			}

			// Reste
			if (index == selectedIndex) {
				size += TAB_BETWEEN * TAB_BETWEEN;
			} else {
				size += TAB_BETWEEN;
			}

			listTabRun.add(new TabRun(index, size));
			x += size;
			sommeSize += size;
		}

		showChevron = false;

		Rectangle currentBound = tabPane.getBounds();
		if (lastBound.x != -1) {
			if (currentBound.width != lastBound.width)
				sens = -1;
			if (currentBound.x != lastBound.x)
				sens = 1;
		}
		lastBound = (Rectangle) currentBound.clone();

		int chevronNum = 0;
		int i = (sens == -1 ? count - 1 : 0);
		int taille = TABBED_SPACE;
		while ((sens == -1 ? i >= 0 : i <= listTabRun.size() - 1)
				&& sommeSize > w - taille) {
			if (i != selectedIndex) {
				TabRun indexSize = listTabRun.get(i);
				sommeSize -= indexSize.width;
				listTabRun.remove(i);
				chevronNum++;
				if (!showChevron)
					taille += TAB_BETWEEN + CHEVRON_WIDTH;
				showChevron = true;

				if (sens == -1)
					i--;
				if (i < selectedIndex)
					selectedIndex--;
			} else {
				i += sens;
			}
		}
		if (listTabRun.size() == 1 && sommeSize > w - taille) {
			TabRun tabRun = listTabRun.get(0);

			// On vire l'icone
			tabRun.showIcon = false;
			if (tabPane.getIconAt(tabRun.index) != null) {
				sommeSize -= 16;
				tabRun.width -= 16;
			}
			if (sommeSize > w - taille) {
				// On coupe le text
				tabRun.warpText = true;
				String title = tabPane.getTitleAt(tabRun.index);
				i = title.length();
				int ancien = fm.stringWidth(title);
				int courant = 0;
				while (i > 0 && sommeSize > w - taille) {
					String a = title.substring(0, i);
					courant = fm.stringWidth(a);
					sommeSize -= ancien - courant;
					ancien = courant;
					i--;
				}
				if (i == 0) {
					tabRun.showText = false;
					tabRun.width -= TAB_BETWEEN;
					tabRun.width -= fm.stringWidth(title);
				} else {
					tabRun.showText = true;
					tabRun.numText = i;
					tabRun.width -= fm.stringWidth(title)
							- fm.stringWidth(title.substring(0, i));
				}
			}
		}
		chevronString = String.valueOf(chevronNum);
	}

	protected void paintTabs(Graphics g) {
		int width = tabPane.getWidth();
		Insets insets = INSETS;

		int x = insets.left;
		int y = insets.top;
		int w = width - insets.right - insets.left - 1;
		int h = TAB_AREA_HEIGHT;

		int selectedIndex = tabPane.getSelectedIndex();

		FontMetrics fm = g.getFontMetrics();
		for (int i = 0; i < listTabRun.size(); i++) {
			TabRun is = listTabRun.get(i);
			int index = is.index;
			Icon icon = tabPane.getIconAt(index);
			String title = tabPane.getTitleAt(index);

			Rectangle tabRect = new Rectangle(x, y, 0, h);

			// Icon
			Rectangle iconRect = null;
			if (is.showIcon && icon != null) {
				if ((index == selectedIndex)
						|| (index != selectedIndex && tabPane
								.isShowUnselectedImage())) {
					x += TAB_BETWEEN;
					iconRect = new Rectangle(x, y + (h - 16) / 2, 16, 16);
					x += iconRect.width;
				}
			}

			// Text
			Rectangle textRect = null;
			if (is.showText) {
				x += TAB_BETWEEN;
				int len = fm.stringWidth(title);
				if (is.warpText)
					len = fm.stringWidth(title.substring(0, is.numText));
				textRect = new Rectangle(x, y + (h + fm.getHeight()) / 2
						- fm.getDescent(), len, fm.getHeight());
				x += textRect.width;
			}

			// Close
			Rectangle closeRect = null;
			if (tabPane.isShowClose()) {
				if ((index == selectedIndex)
						|| (index != selectedIndex && tabPane
								.isShowUnselectedClose())) {
					x += TAB_BETWEEN;
					closeRect = new Rectangle(x, y + (h - 10) / 2, 10, 10);
					x += closeRect.width;
				}
			}

			// Total
			if (index == selectedIndex) {
				x += TAB_BETWEEN * TAB_BETWEEN;
			} else {
				x += TAB_BETWEEN;
			}
			tabRect.width = is.width;

			boolean line = true;
			if (i + 1 < listTabRun.size()
					&& listTabRun.get(i + 1).index == selectedIndex) {
				line = false;
			}

			is.closeRect = closeRect;
			is.tabRect = tabRect;
			is.textRect = textRect;
			is.iconRect = iconRect;

			paintTab(g, fm, icon, title, is, index == selectedIndex, line);
		}

		if (showChevron) {
			chevronRect = new Rectangle(x - 2 + TAB_BETWEEN, y
					+ (h - CHEVRON_HEIGHT) / 2 - 4, CHEVRON_WIDTH,
					CHEVRON_HEIGHT + 8);
			paintChevron(g, x + TAB_BETWEEN, y + (h - CHEVRON_HEIGHT) / 2);
		}

		if (tabPane.isShowMaximize()) {
			maxRect = new Rectangle(w - MAXIMIZE_WIDTH - TAB_BETWEEN / 2 - 4
					- TAB_BETWEEN - ALLCLOSE_WIDTH, y + (h - MAXIMIZE_HEIGHT)
					/ 2 - 4, MAXIMIZE_WIDTH, MAXIMIZE_HEIGHT + 8);
			paintMaximize(g, w - MAXIMIZE_WIDTH - TAB_BETWEEN / 2 - TAB_BETWEEN
					- ALLCLOSE_WIDTH, y + (h - MAXIMIZE_HEIGHT) / 2);
		}

		if (tabPane.isShowAllClose()) {
			allCloseRect = new Rectangle(w - ALLCLOSE_WIDTH - TAB_BETWEEN / 2
					- 4, y + (h - ALLCLOSE_HEIGHT) / 2 - 4, ALLCLOSE_WIDTH,
					ALLCLOSE_HEIGHT + 8);
			paintAllClose(g, w - ALLCLOSE_WIDTH - TAB_BETWEEN / 2, y
					+ (h - ALLCLOSE_HEIGHT) / 2);
		}

		if (selectedIndex != -1) {
			TabRun tabRun = getTabRunAt(selectedIndex);
			int index = tabRun.index;
			Icon icon = tabPane.getIconAt(index);
			String title = tabPane.getTitleAt(index);
			paintTab(g, fm, icon, title, tabRun, index == selectedIndex, false);
		}
	}

	protected void paintTab(Graphics g, FontMetrics metrics, Icon icon,
			String title, TabRun tabRun, boolean isSelected, boolean isLine) {
		Graphics2D g2 = (Graphics2D) g;
		Rectangle tabRect = tabRun.tabRect;
		Rectangle iconRect = tabRun.iconRect;
		Rectangle textRect = tabRun.textRect;
		Rectangle closeRect = tabRun.closeRect;
		if (!isSelected) {
			if (isLine) {
				g2.setColor(COLOR_BORDER);
				g2.drawLine(tabRect.x + tabRect.width - 1, tabRect.y, tabRect.x
						+ tabRect.width - 1, tabRect.y + tabRect.height - 1);
			}
		} else {
			Polygon polygon = buildTabPolygon(tabRect.x, tabRect.y,
					tabRect.width, tabRect.height);
			Color control = UIManager.getColor("control"); //$NON-NLS-1$

			g2.setPaint(new GradientPaint(tabRect.x, tabRect.y, Color.WHITE,
					tabRect.x, tabRect.y + tabRect.height, control));
			g2.fillPolygon(polygon);
			g2.setColor(COLOR_BORDER);
			g2.drawPolygon(polygon);
			g2.setColor(control);
			g2.drawLine(tabRect.x + 1, tabRect.y + tabRect.height, tabRect.x
					+ tabRect.width - 1, tabRect.y + tabRect.height);
		}
		if (iconRect != null) {
			g2.drawImage(((ImageIcon) icon).getImage(), iconRect.x, iconRect.y,
					iconRect.width, iconRect.height, null);
		}

		if (textRect != null) {
			String text = title;
			if (tabRun.warpText)
				text = title.substring(0, tabRun.numText);
			g2.setColor(Color.BLACK);
			g2.drawString(text, textRect.x, textRect.y);
		}

		if (closeRect != null)
			paintClose(g, tabRun.index, closeRect);
	}

	protected void paintClose(Graphics g, int index, Rectangle closeRect) {
		int x = closeRect.x;
		int y = closeRect.y;
		switch (listTabItem.get(index).closeImageState) {
		case NORMAL:
			int[] shapeNormal = new int[] { x, y, x + 2, y, x + 4, y + 2,
					x + 5, y + 2, x + 7, y, x + 9, y, x + 9, y + 2, x + 7,
					y + 4, x + 7, y + 5, x + 9, y + 7, x + 9, y + 9, x + 7,
					y + 9, x + 5, y + 7, x + 4, y + 7, x + 2, y + 9, x, y + 9,
					x, y + 7, x + 2, y + 5, x + 2, y + 4, x, y + 2 };
			Polygon polygonClose = new Polygon();
			for (int i = 0; i < shapeNormal.length; i += 2)
				polygonClose.addPoint(shapeNormal[i], shapeNormal[i + 1]);
			g.setColor(Color.WHITE);
			g.fillPolygon(polygonClose);
			g.setColor(COLOR_BORDER);
			g.drawPolygon(polygonClose);
			break;
		case HOT:
			int[] shapeHot = new int[] { x, y, x + 2, y, x + 4, y + 2, x + 5,
					y + 2, x + 7, y, x + 9, y, x + 9, y + 2, x + 7, y + 4,
					x + 7, y + 5, x + 9, y + 7, x + 9, y + 9, x + 7, y + 9,
					x + 5, y + 7, x + 4, y + 7, x + 2, y + 9, x, y + 9, x,
					y + 7, x + 2, y + 5, x + 2, y + 4, x, y + 2 };
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
					x + 5, y + 3, x + 6, y + 3, x + 8, y + 1, x + 10, y + 1,
					x + 10, y + 3, x + 8, y + 5, x + 8, y + 6, x + 10, y + 8,
					x + 10, y + 10, x + 8, y + 10, x + 6, y + 8, x + 5, y + 8,
					x + 3, y + 10, x + 1, y + 10, x + 1, y + 8, x + 3, y + 6,
					x + 3, y + 5, x + 1, y + 3 };
			Polygon polygonSelected = new Polygon();
			for (int i = 0; i < shapeSelected.length; i += 2)
				polygonSelected
						.addPoint(shapeSelected[i], shapeSelected[i + 1]);
			g.setColor(new Color(252, 160, 160));
			g.fillPolygon(polygonSelected);
			g.setColor(COLOR_BORDER);
			g.drawPolygon(polygonSelected);
			break;
		}
	}

	protected void paintAllClose(Graphics g, int x, int y) {
		switch (allCloseImageState) {
		case NORMAL:
			int[] shapeNormal = new int[] { x, y, x + 2, y, x + 4, y + 2,
					x + 5, y + 2, x + 7, y, x + 9, y, x + 9, y + 2, x + 7,
					y + 4, x + 7, y + 5, x + 9, y + 7, x + 9, y + 9, x + 7,
					y + 9, x + 5, y + 7, x + 4, y + 7, x + 2, y + 9, x, y + 9,
					x, y + 7, x + 2, y + 5, x + 2, y + 4, x, y + 2 };
			Polygon polygonNormal = new Polygon();
			for (int i = 0; i < shapeNormal.length; i += 2)
				polygonNormal.addPoint(shapeNormal[i], shapeNormal[i + 1]);
			g.setColor(Color.WHITE);
			g.fillPolygon(polygonNormal);
			g.setColor(COLOR_BORDER);
			g.drawPolygon(polygonNormal);
			break;
		case HOT:
			int[] shapeHot = new int[] { x, y, x + 2, y, x + 4, y + 2, x + 5,
					y + 2, x + 7, y, x + 9, y, x + 9, y + 2, x + 7, y + 4,
					x + 7, y + 5, x + 9, y + 7, x + 9, y + 9, x + 7, y + 9,
					x + 5, y + 7, x + 4, y + 7, x + 2, y + 9, x, y + 9, x,
					y + 7, x + 2, y + 5, x + 2, y + 4, x, y + 2 };
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
					x + 5, y + 3, x + 6, y + 3, x + 8, y + 1, x + 10, y + 1,
					x + 10, y + 3, x + 8, y + 5, x + 8, y + 6, x + 10, y + 8,
					x + 10, y + 10, x + 8, y + 10, x + 6, y + 8, x + 5, y + 8,
					x + 3, y + 10, x + 1, y + 10, x + 1, y + 8, x + 3, y + 6,
					x + 3, y + 5, x + 1, y + 3 };
			Polygon polygonSelected = new Polygon();
			for (int i = 0; i < shapeSelected.length; i += 2)
				polygonSelected
						.addPoint(shapeSelected[i], shapeSelected[i + 1]);
			g.setColor(new Color(252, 160, 160));
			g.fillPolygon(polygonSelected);
			g.setColor(COLOR_BORDER);
			g.drawPolygon(polygonSelected);
			break;
		}
	}

	protected void paintChevron(Graphics g, int x, int y) {
		Font storedFont = g.getFont();
		g.setFont(storedFont.deriveFont(10.0f));
		FontMetrics fm = g.getFontMetrics();
		switch (chevronImageState) {
		case NORMAL:
			g.setColor(Color.BLACK);
			g.drawLine(x, y, x + 2, y + 2);
			g.drawLine(x + 2, y + 2, x, y + 4);
			g.drawLine(x + 1, y, x + 3, y + 2);
			g.drawLine(x + 3, y + 2, x + 1, y + 4);
			g.drawLine(x + 4, y, x + 6, y + 2);
			g.drawLine(x + 6, y + 2, x + 5, y + 4);
			g.drawLine(x + 5, y, x + 7, y + 2);
			g.drawLine(x + 7, y + 2, x + 4, y + 4);
			g.drawString(chevronString, x + 7, y + 3 + fm.getHeight()
					- fm.getDescent());
			break;
		case HOT:
			g.setColor(Color.WHITE);
			g.fillRoundRect(chevronRect.x, chevronRect.y, chevronRect.width,
					chevronRect.height, 6, 6);
			g.setColor(COLOR_BORDER);
			g.drawRoundRect(chevronRect.x, chevronRect.y, chevronRect.width,
					chevronRect.height, 6, 6);
			g.drawLine(x, y, x + 2, y + 2);
			g.drawLine(x + 2, y + 2, x, y + 4);
			g.drawLine(x + 1, y, x + 3, y + 2);
			g.drawLine(x + 3, y + 2, x + 1, y + 4);
			g.drawLine(x + 4, y, x + 6, y + 2);
			g.drawLine(x + 6, y + 2, x + 5, y + 4);
			g.drawLine(x + 5, y, x + 7, y + 2);
			g.drawLine(x + 7, y + 2, x + 4, y + 4);
			g.drawString(chevronString, x + 7, y + 3 + fm.getHeight()
					- fm.getDescent());
			break;
		case SELECTED:
			g.setColor(Color.WHITE);
			g.fillRoundRect(chevronRect.x, chevronRect.y, chevronRect.width,
					chevronRect.height, 6, 6);
			g.setColor(COLOR_BORDER);
			g.drawRoundRect(chevronRect.x, chevronRect.y, chevronRect.width,
					chevronRect.height, 6, 6);
			x += 1;
			y += 1;
			g.drawLine(x, y, x + 2, y + 2);
			g.drawLine(x + 2, y + 2, x, y + 4);
			g.drawLine(x + 1, y, x + 3, y + 2);
			g.drawLine(x + 3, y + 2, x + 1, y + 4);
			g.drawLine(x + 4, y, x + 6, y + 2);
			g.drawLine(x + 6, y + 2, x + 5, y + 4);
			g.drawLine(x + 5, y, x + 7, y + 2);
			g.drawLine(x + 7, y + 2, x + 4, y + 4);
			g.drawString(chevronString, x + 8, y + 4 + fm.getHeight()
					- fm.getDescent());
			break;
		}
		g.setFont(storedFont);
	}

	protected void paintMaximize(Graphics g, int x, int y) {
		boolean maximized = tabPane.isMaximized();
		switch (maximizeImageState) {
		case NORMAL:
			if (!maximized) {
				g.setColor(Color.WHITE);
				g.fillRect(x, y, 9, 9);
				g.setColor(COLOR_BORDER);
				g.drawRect(x, y, 9, 9);
				g.drawLine(x + 1, y + 2, x + 8, y + 2);
			} else {
				g.setColor(Color.WHITE);
				g.fillRect(x, y + 3, 5, 4);
				g.fillRect(x + 2, y, 5, 4);
				g.setColor(COLOR_BORDER);
				g.drawRect(x, y + 3, 5, 4);
				g.drawRect(x + 2, y, 5, 4);
				g.drawLine(x + 3, y + 1, x + 6, y + 1);
				g.drawLine(x + 1, y + 4, x + 4, y + 4);
			}
			break;
		case HOT:
			g.setColor(Color.WHITE);
			g.fillRoundRect(maxRect.x, maxRect.y, maxRect.width,
					maxRect.height, 6, 6);
			g.setColor(COLOR_BORDER);
			g.drawRoundRect(maxRect.x, maxRect.y, maxRect.width - 1,
					maxRect.height - 1, 6, 6);
			if (!maximized) {
				g.setColor(Color.WHITE);
				g.fillRect(x, y, 9, 9);
				g.setColor(COLOR_BORDER);
				g.drawRect(x, y, 9, 9);
				g.drawLine(x + 1, y + 2, x + 8, y + 2);
			} else {
				g.setColor(Color.WHITE);
				g.fillRect(x, y + 3, 5, 4);
				g.fillRect(x + 2, y, 5, 4);
				g.setColor(COLOR_BORDER);
				g.drawRect(x, y + 3, 5, 4);
				g.drawRect(x + 2, y, 5, 4);
				g.drawLine(x + 3, y + 1, x + 6, y + 1);
				g.drawLine(x + 1, y + 4, x + 4, y + 4);
			}
			break;
		case SELECTED:
			g.setColor(Color.WHITE);
			g.fillRoundRect(maxRect.x, maxRect.y, maxRect.width,
					maxRect.height, 6, 6);
			g.setColor(COLOR_BORDER);
			g.drawRoundRect(maxRect.x, maxRect.y, maxRect.width - 1,
					maxRect.height - 1, 6, 6);
			if (!maximized) {
				g.setColor(Color.WHITE);
				g.fillRect(x + 1, y + 1, 9, 9);
				g.setColor(COLOR_BORDER);
				g.drawRect(x + 1, y + 1, 9, 9);
				g.drawLine(x + 2, y + 3, x + 9, y + 3);
			} else {
				g.setColor(Color.WHITE);
				g.fillRect(x + 1, y + 4, 5, 4);
				g.fillRect(x + 3, y + 1, 5, 4);
				g.setColor(COLOR_BORDER);
				g.drawRect(x + 1, y + 4, 5, 4);
				g.drawRect(x + 3, y + 1, 5, 4);
				g.drawLine(x + 4, y + 2, x + 7, y + 2);
				g.drawLine(x + 2, y + 5, x + 5, y + 5);
			}
			break;
		}
	}

	protected Color getHeaderBackground() {
		Color c = UIManager
				.getColor("SimpleInternalFrame.activeTitleBackground"); //$NON-NLS-1$
		if (c != null)
			return c;
//		if (LookUtils.IS_LAF_WINDOWS_XP_ENABLED)
//			c = UIManager.getColor("InternalFrame.activeTitleGradient");
		return c != null ? c : UIManager
				.getColor("InternalFrame.activeTitleBackground"); //$NON-NLS-1$
	}

	public int tabForCoordinate(JTabbedPane pane, int x, int y) {
		TabRun tabRun = tabRunForCoordinate(x, y);
		return tabRun == null ? -1 : tabRun.index;
	}

	private TabRun tabRunForCoordinate(int x, int y) {
		TabRun tabRun = null;
		int i = 0;
		while (i < listTabRun.size() && tabRun == null) {
			TabRun tr = listTabRun.get(i);
			Rectangle tabRect = tr.tabRect;
			if (tabRect != null && tabRect.contains(x, y)) {
				tabRun = tr;
			}
			i++;
		}
		return tabRun;
	}

	public Rectangle getTabBounds(JTabbedPane pane, int index) {
		return new Rectangle(0,0,100,10);
	}

	@Override
	public int getTabRunCount(JTabbedPane pane) {
		return listTabRun.size();
	}

	private void installListeners() {
		// if ((propertyChangeListener = createPropertyChangeListener()) !=
		// null) {
		// tabPane.addPropertyChangeListener(propertyChangeListener);
		// }
		if ((tabChangeListener = createChangeListener()) != null) {
			tabPane.addChangeListener(tabChangeListener);
		}
		if ((mouseListener = createMouseListener()) != null) {
			tabPane.addMouseListener(mouseListener);
		}
		tabPane.addMouseMotionListener(getHandler());
		if ((focusListener = createFocusListener()) != null) {
			tabPane.addFocusListener(focusListener);
		}
		tabPane.addContainerListener(getHandler());
		// if (tabPane.getTabCount() > 0) {
		// htmlViews = createHTMLVector();
		// }
	}

	private void uninstallListeners() {
		if (mouseListener != null) {
			tabPane.removeMouseListener(mouseListener);
			mouseListener = null;
		}
		tabPane.removeMouseMotionListener(getHandler());
		if (focusListener != null) {
			tabPane.removeFocusListener(focusListener);
			focusListener = null;
		}
		tabPane.removeContainerListener(getHandler());
		// if (htmlViews!=null) {
		// htmlViews.removeAllElements();
		// htmlViews = null;
		// }

		if (tabChangeListener != null) {
			tabPane.removeChangeListener(tabChangeListener);
			tabChangeListener = null;
		}

		handler = null;
	}

	protected MouseListener createMouseListener() {
		return getHandler();
	}

	protected FocusListener createFocusListener() {
		return getHandler();
	}

	protected ChangeListener createChangeListener() {
		return getHandler();
	}

	private Handler getHandler() {
		if (handler == null) {
			handler = new Handler();
		}
		return handler;
	}

	protected Component getVisibleComponent() {
		return visibleComponent;
	}

	protected void setVisibleComponent(Component component) {
		if (visibleComponent != null && visibleComponent != component
				&& visibleComponent.getParent() == tabPane) {
			visibleComponent.setVisible(false);
		}
		if (component != null && !component.isVisible()) {
			component.setVisible(true);
		}
		visibleComponent = component;
	}

	private static Component compositeRequestFocus(Component component) {
		if (component instanceof Container) {
			Container container = (Container) component;
			if (container.isFocusCycleRoot()) {
				FocusTraversalPolicy policy = container
						.getFocusTraversalPolicy();
				Component comp = policy.getDefaultComponent(container);
				if (comp != null) {
					comp.requestFocus();
					return comp;
				}
			}
			Container rootAncestor = container.getFocusCycleRootAncestor();
			if (rootAncestor != null) {
				FocusTraversalPolicy policy = rootAncestor
						.getFocusTraversalPolicy();
				Component comp = policy.getComponentAfter(rootAncestor,
						container);

				if (comp != null
						&& SwingUtilities.isDescendingFrom(comp, container)) {
					comp.requestFocus();
					return comp;
				}
			}
		}
		if (component.isFocusable()) {
			component.requestFocus();
			return component;
		}
		return null;
	}

	private boolean requestFocusForVisibleComponent() {
		Component visibleComponent = getVisibleComponent();
		if (visibleComponent != null && visibleComponent.isFocusable()) {
			compositeRequestFocus(visibleComponent);
			return true;
		} else if (visibleComponent instanceof JComponent) {
			if (((JComponent) visibleComponent).requestDefaultFocus()) {
				return true;
			}
		}
		return false;
	}

	private void ensureCurrentLayout() {
		if (!tabPane.isValid()) {
			tabPane.validate();
		}
		/*
		 * If tabPane doesn't have a peer yet, the validate() call will silently
		 * fail. We handle that by forcing a layout if tabPane is still invalid.
		 * See bug 4237677.
		 */
		if (!tabPane.isValid()) {
			// TabbedPaneLayout layout = (TabbedPaneLayout) tabPane.getLayout();
			// layout.calculateLayoutInfo();
		}
	}

	private boolean isExistTabRunAt(int index) {
		int i = 0;
		while (i < listTabRun.size() && listTabRun.get(i).index != index)
			i++;
		return i != listTabRun.size();
	}

	private TabRun getTabRunAt(int index) {
		TabRun res = null;
		int i = 0;
		while (i < listTabRun.size() && listTabRun.get(i).index != index)
			i++;
		if (i < listTabRun.size())
			res = listTabRun.get(i);
		return res;
	}



	private class TabRun {

		int index;

		int width;

		Rectangle tabRect;

		Rectangle iconRect;

		boolean showIcon;

		Rectangle closeRect;

		Rectangle textRect;

		boolean showText;

		boolean warpText;

		int numText;

		public TabRun(int index, int w) {
			this.index = index;
			this.width = w;
			showIcon = true;
			showText = true;
			warpText = false;
		}
	}

	private class TabItem {

		int closeImageState = NONE;

	}

	private class Handler implements MouseListener, MouseMotionListener,
			FocusListener, ContainerListener, ChangeListener {

		private boolean isContains(Rectangle rect, int x, int y) {
			return rect != null && rect.contains(x, y);
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				TabRun tabRun = tabRunForCoordinate(e.getX(), e.getY());
				if (tabRun != null) {
					if (!isContains(tabRun.closeRect, e.getX(), e.getY())) {
						tabPane.setMaximized(!tabPane.isMaximized());
					}
				} else if (!showChevron
						|| !isContains(chevronRect, e.getX(), e.getY())) {
					if (!tabPane.isShowAllClose()
							|| !isContains(allCloseRect, e.getX(), e.getY())) {
						maximizeImageState = NORMAL;
						tabPane.setMaximized(!tabPane.isMaximized());
					}
				}
			}
		}

		public void mousePressed(MouseEvent e) {
			boolean change = resetTabItems();
			TabRun tabRun = tabRunForCoordinate(e.getX(), e.getY());
			if (tabRun != null) {
				TabItem tabItem = listTabItem.get(tabRun.index);
				if (isContains(tabRun.closeRect, e.getX(), e.getY())) {
					tabItem.closeImageState = SELECTED;
					change = true;
				}
			} else if (showChevron
					&& isContains(chevronRect, e.getX(), e.getY())) {
				chevronImageState = SELECTED;
				change = true;
			} else if (tabPane.isShowMaximize()
					&& isContains(maxRect, e.getX(), e.getY())) {
				maximizeImageState = SELECTED;
				change = true;
			} else if (tabPane.isShowAllClose()
					&& isContains(allCloseRect, e.getX(), e.getY())) {
				allCloseImageState = SELECTED;
				change = true;
			}
			if (change)
				tabPane.repaint(0, 0, tabPane.getWidth(), TAB_AREA_HEIGHT);
		}

		public void mouseReleased(MouseEvent e) {
			TabRun tabRun = tabRunForCoordinate(e.getX(), e.getY());
			if (tabRun != null) {
				TabItem tabItem = listTabItem.get(tabRun.index);
				if (isContains(tabRun.closeRect, e.getX(), e.getY())
						&& tabItem.closeImageState == SELECTED) {
					int index = tabRun.index;
					tabPane.closeAt(tabRun.index);
					int selectedIndex = tabPane.getSelectedIndex();
					if (index < selectedIndex) {
						selectedIndex--;
						tabPane.setSelectedIndex(selectedIndex);
					}
				}
			}

			if (showChevron && isContains(chevronRect, e.getX(), e.getY())) {
				chevronImageState = NORMAL;
				chevronPopupMenu.show(tabPane, e.getX(), e.getY());
			}

			if (tabPane.isShowMaximize()
					&& isContains(maxRect, e.getX(), e.getY())) {
				maximizeImageState = NORMAL;
				tabPane.setMaximized(!tabPane.isMaximized());
			}

			if (tabPane.isShowAllClose()
					&& isContains(allCloseRect, e.getX(), e.getY())) {
				allCloseImageState = NORMAL;
				tabPane.closeAll();
			}

			resetTabItems();
			if (tabRun != null) {
				if (!isContains(tabRun.closeRect, e.getX(), e.getY())) {
					TabItem tabItem = listTabItem.get(tabRun.index);
					tabItem.closeImageState = NORMAL;
					tabPane.setSelectedIndex(tabRun.index);
				}
			}
			tabPane.repaint(0, 0, tabPane.getWidth(), TAB_AREA_HEIGHT);
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
			boolean change = resetTabItems();

			if (chevronImageState != NORMAL)
				change = true;
			chevronImageState = NORMAL;

			if (maximizeImageState != NORMAL)
				change = true;
			maximizeImageState = NORMAL;

			if (allCloseImageState != NORMAL)
				change = true;
			allCloseImageState = NORMAL;

			if (change)
				tabPane.repaint(0, 0, tabPane.getWidth(), TAB_AREA_HEIGHT);
		}

		public void mouseDragged(MouseEvent e) {
		}

		public void mouseMoved(MouseEvent e) {
			boolean change = resetTabItems();
			TabRun tabRun = tabRunForCoordinate(e.getX(), e.getY());
			if (tabRun != null) {
				TabItem tabItem = listTabItem.get(tabRun.index);
				if (isContains(tabRun.closeRect, e.getX(), e.getY())) {
					tabItem.closeImageState = HOT;
					change = true;
				} else {
					if (tabItem.closeImageState != NORMAL)
						change = true;
					tabItem.closeImageState = NORMAL;
				}
			}
			// Chevron
			if (showChevron && isContains(chevronRect, e.getX(), e.getY())) {
				chevronImageState = HOT;
				change = true;
			} else {
				if (chevronImageState != NORMAL)
					change = true;
				chevronImageState = NORMAL;
			}

			// Maximize
			if (tabPane.isShowMaximize()
					&& isContains(maxRect, e.getX(), e.getY())) {
				maximizeImageState = HOT;
				change = true;
			} else {
				if (maximizeImageState != NORMAL)
					change = true;
				maximizeImageState = NORMAL;
			}

			// All Close
			if (tabPane.isShowAllClose()
					&& isContains(allCloseRect, e.getX(), e.getY())) {
				allCloseImageState = HOT;
				change = true;
			} else {
				if (allCloseImageState != NORMAL)
					change = true;
				allCloseImageState = NORMAL;
			}
			if (change)
				tabPane.repaint(0, 0, tabPane.getWidth(), TAB_AREA_HEIGHT);
		}

		public void focusGained(FocusEvent e) {
			System.err.println("J'ai le focus"); //$NON-NLS-1$
		}

		public void focusLost(FocusEvent e) {
			System.err.println("J'ai plus le focus"); //$NON-NLS-1$
		}

		public void componentAdded(ContainerEvent e) {
			createTabItems();
		}

		public void componentRemoved(ContainerEvent e) {
			createTabItems();
		}

		public void stateChanged(ChangeEvent e) {
			resetTabItems();

			JTabbedPane tabPane = (JTabbedPane) e.getSource();
			tabPane.revalidate();
			tabPane.repaint(0, 0, tabPane.getWidth(), TAB_AREA_HEIGHT);
		}
	}

	private class TabbedPaneLayout implements LayoutManager {

		public void addLayoutComponent(String name, Component comp) {
		}

		public void removeLayoutComponent(Component comp) {
		}

		public Dimension preferredLayoutSize(Container parent) {
			return calculateSize(false);
		}

		public Dimension minimumLayoutSize(Container parent) {
			return calculateSize(true);
		}

		protected Dimension calculateSize(boolean minimum) {
			Insets insets = INSETS;
			Dimension res = new Dimension(0, 0);
			int selectedIndex = tabPane.getSelectedIndex();
			int width = 0;
			if (minimum || selectedIndex == -1) {
				if (tabPane.isShowClose())
					width += TAB_BETWEEN + CLOSE_WIDTH;
				width += TAB_BETWEEN * TAB_BETWEEN;
			} else {
				TabRun tabRun = getTabRunAt(selectedIndex);
				if (tabRun != null) {
					width += tabRun.tabRect.width;
				}
			}
			// if (showChevron)
			width += TAB_BETWEEN + CHEVRON_WIDTH;
			res.width = width + insets.right + insets.left + TABBED_SPACE;
			res.height = TAB_AREA_HEIGHT + insets.bottom + insets.top + 1;
			return res;
		}

		public void layoutContainer(Container parent) {
			int selectedIndex = tabPane.getSelectedIndex();
			Component visibleComponent = getVisibleComponent();
			if (selectedIndex < 0) {
				if (visibleComponent != null) {
					// The last tab was removed, so remove the component
					setVisibleComponent(null);
				}
			} else {
				Component selectedComponent = tabPane
						.getComponentAt(selectedIndex);
				boolean shouldChangeFocus = false;

				if (selectedComponent != null) {
					if (selectedComponent != visibleComponent
							&& visibleComponent != null) {
						if (SwingUtilities.findFocusOwner(visibleComponent) != null) {
							shouldChangeFocus = true;
						}
					}
					setVisibleComponent(selectedComponent);
				}

				int width = tabPane.getWidth();
				int height = tabPane.getHeight();
				Insets insets = INSETS;

				int x = insets.left + 1;
				int y = insets.top + TAB_AREA_HEIGHT + 1;
				int w = width - insets.right - insets.left - 2;
				int h = height - TAB_AREA_HEIGHT - insets.top - insets.bottom
						- 2;

				int numChildren = tabPane.getComponentCount();
				for (int i = 0; i < numChildren; i++) {
					Component child = tabPane.getComponent(i);
					child.setBounds(x, y, w, h);
				}

				if (shouldChangeFocus) {
					if (!requestFocusForVisibleComponent()) {
						tabPane.requestFocus();
					}
				}
			}
		}

	}

	private class ChevronPopupMenuListener implements PopupMenuListener {

		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			int count = tabPane.getComponentCount();
			for (int i = 0; i < count; i++) {
				Icon icon = tabPane.getIconAt(i);
				String text = tabPane.getTitleAt(i);

				JChevronMenuItem menuItem = new JChevronMenuItem(i, text, icon);
				menuItem.addActionListener(new MenuItemActionListener());
				if (!isExistTabRunAt(i)) {
					menuItem.setBold(true);
					chevronPopupMenu.insert(menuItem, 0);
				} else {
					chevronPopupMenu.add(menuItem);
				}
			}
		}

		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			chevronPopupMenu.removeAll();
		}

		public void popupMenuCanceled(PopupMenuEvent e) {
		}
	}

	private class MenuItemActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() instanceof JChevronMenuItem) {
				JChevronMenuItem c = (JChevronMenuItem) e.getSource();
				int index = c.getIndex();
				tabPane.setSelectedIndex(index);
			}
		}
	}

	private class JChevronMenuItem extends JMenuItem {

		private static final long serialVersionUID = 1L;

		private int index;

		public JChevronMenuItem(int index, String text, Icon icon) {
			super(text, icon);
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public void setBold(boolean b) {
			Font font = this.getFont();
			Font f;
			if (b)
				f = font.deriveFont(Font.BOLD);
			else
				f = font.deriveFont(Font.PLAIN);
			this.setFont(f);
		}
	}

}
