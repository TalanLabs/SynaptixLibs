package com.synaptix.swing;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.EventListenerList;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.event.TriageModelEvent;
import com.synaptix.swing.event.TriageModelListener;
import com.synaptix.swing.event.TriageSelectionEvent;
import com.synaptix.swing.event.TriageSelectionListener;
import com.synaptix.swing.triage.LotDraw;
import com.synaptix.swing.triage.Side;
import com.synaptix.swing.triage.TriagePath;
import com.synaptix.swing.triage.VoyageDraw;
import com.synaptix.swing.utils.DateTimeUtils;
import com.synaptix.view.swing.error.ErrorViewManager;

public class JTriage extends JPanel implements TriageModelListener {

	private static final long serialVersionUID = -5194267400202070101L;

	private static final int CENTER_WIDTH = 80;

	private static final int MIN_LOT_WIDTH = 150;

	private static final int SPACE_LOTS_HEIGHT = 6;

	private static final int SPACE_LOT_VOYAGE_WIDTH = 4;

	private static final int SPACE_LOT_VOYAGE_HEIGHT = 4;

	private static final int voyageWidth = 22;

	private static final int voyageHeight = 17;

	private static final int SPACE_VOYAGES_WIDTH = 2;

	private static final int START_LOT_X = 10;

	private static final int SPACE_LOTS_WIDTH = 15;

	private static final Color selectedColor = Color.BLACK;

	private static final Color barDateMinMaxColor = new Color(50, 255, 64, 128);

	private static final Color barInverseDateMinMaxColor = new Color(255, 64, 50, 128);

	private static final Color selectedLotDrawColor = new Color(50, 64, 255, 128);

	private static final Color shadowLotDrawColor = new Color(200, 200, 200);

	private static final Color backgroundLotDrawColor = new Color(250, 250, 250);

	private static final Color borderLotDrawColor = new Color(128, 128, 128);

	private static final Color backgroundLeftRightColor = Color.WHITE;

	private static final Color highLineColor = new Color(0xC8B9E0);

	private static final Stroke selectedStroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 4, 2 }, 0);

	private final DateFormat completFormatter = new SimpleDateFormat("dd MMM yyyy"); //$NON-NLS-1$

	private final DateFormat fullFormatter = new SimpleDateFormat("dd MMM yyyy HH:mm");

	private static final Font dateFont = new Font("Tahama", Font.BOLD, 12); //$NON-NLS-1$

	private static final Color dateColor = new Color(0x6A6A6B);

	private static final Font hourFont = new Font("Tahama", Font.PLAIN, 12); //$NON-NLS-1$

	private static final Color hourColor = new Color(0x6A6A6B);

	private static final Color hourLineColor = new Color(0xBBCCDD);

	private static final Color centerBackgroundColor = new Color(0xE8EEF7);

	private static final Color centerBorderColor = new Color(0xCCDDEE);

	private TriageModel model;

	private JScrollPane leftScrollPane;

	private JComponent leftComponent;

	private JScrollPane rightScrollPane;

	private JComponent rightComponent;

	private JScrollPane centerScrollPane;

	private JComponent centerComponent;

	private JScrollBar verticalScrollBar;

	private AdjustmentListener leftRightScrollBarAdjustementListener;

	private AdjustmentListener verticalScrollBarAdjustementListener;

	private int leftRightWidth;

	private int leftRightHeight;

	private List<LotDrawsPosition> lotDrawsPositions;

	private List<DateDrawPosition> dateDrawPositions;

	private TriagePath currentSelectedPath;

	private TriagePath otherSelectedPath;

	private EventListenerList eventListenerList;

	public JTriage(TriageModel model) {
		super(new BorderLayout());

		this.model = model;
		if (model != null) {
			model.addTriageModelListener(this);
		}

		initialize();
		initComponents();

		this.add(buildEditorPanel(), BorderLayout.CENTER);
		this.add(verticalScrollBar, BorderLayout.EAST);

		syncModelToView();
		computeLeftRightSize();
	}

	private void initialize() {
		leftRightWidth = 0;
		leftRightHeight = 0;

		currentSelectedPath = null;
		otherSelectedPath = null;

		eventListenerList = new EventListenerList();
	}

	private void initComponents() {
		verticalScrollBarAdjustementListener = new VerticalScrollBarAdjustementListener();

		verticalScrollBar = new MyScrollBar(JScrollBar.VERTICAL);
		verticalScrollBar.addAdjustmentListener(verticalScrollBarAdjustementListener);

		leftRightScrollBarAdjustementListener = new LeftRightScrollBarAdjustementListener();

		rightComponent = new LeftRightComponent(Side.RIGHT);
		rightScrollPane = new JScrollPane(rightComponent);
		rightScrollPane.setBorder(null);
		rightScrollPane.setHorizontalScrollBar(new MyScrollBar(JScrollBar.HORIZONTAL));
		rightScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		rightScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		rightScrollPane.getVerticalScrollBar().addAdjustmentListener(leftRightScrollBarAdjustementListener);
		rightScrollPane.setWheelScrollingEnabled(false);
		rightComponent.addMouseWheelListener(new MyMouseWheelListener());

		leftComponent = new LeftRightComponent(Side.LEFT);

		leftScrollPane = new JScrollPane(leftComponent);
		leftScrollPane.setBorder(null);
		leftScrollPane.setHorizontalScrollBar(new MyScrollBar(JScrollBar.HORIZONTAL));
		leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		leftScrollPane.getVerticalScrollBar().addAdjustmentListener(leftRightScrollBarAdjustementListener);
		leftScrollPane.setWheelScrollingEnabled(false);
		leftScrollPane.addMouseWheelListener(new MyMouseWheelListener());

		leftScrollPane.getHorizontalScrollBar().setBlockIncrement(-10);
		leftScrollPane.getHorizontalScrollBar().setUnitIncrement(-1);

		centerComponent = new CenterComponent();

		centerScrollPane = new JScrollPane(centerComponent);
		centerScrollPane.setBorder(null);
		centerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		centerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		centerScrollPane.getHorizontalScrollBar().setEnabled(false);
		centerScrollPane.addMouseWheelListener(new MyMouseWheelListener());
	}

	private JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0),FILL:" //$NON-NLS-1$
				+ CENTER_WIDTH + "PX:NONE,FILL:DEFAULT:GROW(1.0)", //$NON-NLS-1$
				"FILL:DEFAULT:GROW(1.0)"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		builder.add(leftScrollPane, cc.xy(1, 1));
		builder.add(centerScrollPane, cc.xy(2, 1));
		builder.add(rightScrollPane, cc.xy(3, 1));

		return builder.getPanel();
	}

	private void syncModelToView() {
		lotDrawsPositions = new ArrayList<LotDrawsPosition>();
		if (model != null) {
			List<LotDraw> allLotDraws = new ArrayList<LotDraw>();

			List<LotDraw> leftLotDraws = model.getLotDraws(Side.LEFT);
			if (leftLotDraws != null) {
				allLotDraws.addAll(leftLotDraws);
			}

			List<LotDraw> rightLotDraws = model.getLotDraws(Side.RIGHT);
			if (rightLotDraws != null) {
				allLotDraws.addAll(rightLotDraws);
			}

			if (allLotDraws.size() > 0) {
				Collections.sort(allLotDraws, new LotDrawComparator());

				LotDrawsPosition leftRightLotDrawsPosition = new LotDrawsPosition();
				leftRightLotDrawsPosition.leftLotDraws = new ArrayList<LotDraw>();
				leftRightLotDrawsPosition.rightLotDraws = new ArrayList<LotDraw>();

				Date prec = null;
				for (LotDraw lotDraw : allLotDraws) {
					if (prec != null) {
						if (prec.compareTo(lotDraw.getDate()) != 0) {
							lotDrawsPositions.add(leftRightLotDrawsPosition);

							leftRightLotDrawsPosition = new LotDrawsPosition();
							leftRightLotDrawsPosition.leftLotDraws = new ArrayList<LotDraw>();
							leftRightLotDrawsPosition.rightLotDraws = new ArrayList<LotDraw>();

							prec = lotDraw.getDate();
						}
					} else {
						prec = lotDraw.getDate();
					}

					if (leftLotDraws.contains(lotDraw)) {
						leftRightLotDrawsPosition.leftLotDraws.add(lotDraw);
					} else {
						leftRightLotDrawsPosition.rightLotDraws.add(lotDraw);
					}
				}

				lotDrawsPositions.add(leftRightLotDrawsPosition);
			}
		}
	}

	private int computeWidthByLotDraw(Side side, LotDraw lotDraw, int defaultWidth) {
		int count = 0;
		List<VoyageDraw> voyageDraws = model.getVoyageDraws(side, lotDraw);
		if (voyageDraws != null) {
			count = voyageDraws.size();
		}

		int width = 0;
		if (count > 0) {
			width = (count - 1) * (voyageWidth + SPACE_VOYAGES_WIDTH) + voyageWidth + SPACE_LOT_VOYAGE_WIDTH * 2;
		}
		width = Math.max(width, MIN_LOT_WIDTH);

		return Math.max(width, defaultWidth);
	}

	private void computeLeftRightSize() {
		dateDrawPositions = new ArrayList<DateDrawPosition>();
		leftRightWidth = START_LOT_X;
		leftRightHeight = SPACE_LOTS_HEIGHT;

		Date datePrec = null;

		for (LotDrawsPosition lotDrawsPosition : lotDrawsPositions) {
			int height = Integer.MIN_VALUE;
			Date dateCurrent = null;
			int leftWidth = START_LOT_X;

			for (LotDraw lotDraw : lotDrawsPosition.leftLotDraws) {
				Dimension d = lotDraw.getRenderer().getMinimumSize(this, Side.LEFT, lotDraw);
				height = Math.max(height, d.height);
				leftWidth += computeWidthByLotDraw(Side.LEFT, lotDraw, d.width) + SPACE_LOTS_WIDTH;

				dateCurrent = lotDraw.getDate();
			}

			int rightWidth = START_LOT_X;
			for (LotDraw lotDraw : lotDrawsPosition.rightLotDraws) {
				Dimension d = lotDraw.getRenderer().getMinimumSize(this, Side.RIGHT, lotDraw);
				height = Math.max(height, d.height);
				rightWidth += computeWidthByLotDraw(Side.RIGHT, lotDraw, d.width) + SPACE_LOTS_WIDTH;

				dateCurrent = lotDraw.getDate();
			}

			height += SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight;

			if (dateCurrent != null) {
				DateDrawPosition dateDrawPosition = new DateDrawPosition();
				dateDrawPosition.date = dateCurrent;

				dateDrawPosition.showDay = false;
				if (datePrec != null) {
					if (!DateTimeUtils.sameDay(datePrec, dateCurrent)) {
						dateDrawPosition.showDay = true;
					}
				} else {
					dateDrawPosition.showDay = true;
				}
				datePrec = dateCurrent;

				dateDrawPosition.y = leftRightHeight;
				dateDrawPosition.height = height;
				dateDrawPositions.add(dateDrawPosition);
			}

			leftRightHeight += height + SPACE_LOTS_HEIGHT;
			leftRightWidth = Math.max(leftRightWidth, Math.max(leftWidth, rightWidth));
		}

		leftComponent.setSize(leftRightWidth, leftRightHeight);
		rightComponent.setSize(leftRightWidth, leftRightHeight);
		centerComponent.setSize(CENTER_WIDTH, leftRightHeight);

		JScrollBar leftScrollBar = leftScrollPane.getHorizontalScrollBar();
		leftScrollBar.setMaximum(leftRightWidth);
		leftScrollBar.setValue(leftScrollBar.getMaximum());
	}

	private void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	public void triageChanged(TriageModelEvent e) {
		currentSelectedPath = null;
		otherSelectedPath = null;

		syncModelToView();
		computeLeftRightSize();

		resizeAndRepaint();
	}

	public Dimension getPreferredSize() {
		return new Dimension(800, 600);
	}

	public TriageModel getModel() {
		return model;
	}

	public TriagePath getSelectedPath() {
		return currentSelectedPath;
	}

	public TriagePath getOtherSelectedPath() {
		return otherSelectedPath;
	}

	public void addTriageSelectionListener(TriageSelectionListener l) {
		eventListenerList.add(TriageSelectionListener.class, l);
	}

	public void removeTriageSelectionListener(TriageSelectionListener l) {
		eventListenerList.remove(TriageSelectionListener.class, l);
	}

	protected void fireTriageSelectionChanged() {
		TriageSelectionEvent event = new TriageSelectionEvent(this, currentSelectedPath, otherSelectedPath);
		Object[] listeners = eventListenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TriageSelectionListener.class) {
				((TriageSelectionListener) listeners[i + 1]).selectionChange(event);
			}
		}
	}

	private TriagePath searchOtherSelectedPath(TriagePath selectedPath) {
		if (selectedPath != null && selectedPath.getVoyageDraw() != null) {
			Side side;
			if (selectedPath.getSide() == Side.LEFT) {
				side = Side.RIGHT;
			} else {
				side = Side.LEFT;
			}

			// rechercher le lot inverse qui contient le voyage
			List<LotDraw> lotDraws = model.getLotDraws(side);
			if (lotDraws != null && lotDraws.size() > 0) {
				for (LotDraw lotDraw : lotDraws) {
					List<VoyageDraw> voyageDraws = model.getVoyageDraws(side, lotDraw);
					if (voyageDraws != null && voyageDraws.size() > 0) {
						for (VoyageDraw voyageDraw : voyageDraws) {
							if (selectedPath.getVoyageDraw().equals(voyageDraw)) {
								return new TriagePath(side, lotDraw, voyageDraw);
							}
						}
					}
				}
			}
		}
		return null;
	}

	private int dateIndexAtPoint(Point p) {
		if (p.y < SPACE_LOTS_HEIGHT) {
			return 0;
		}
		for (int i = 0; i < dateDrawPositions.size(); i++) {
			DateDrawPosition dateDrawPosition = dateDrawPositions.get(i);
			if (p.y >= dateDrawPosition.y && p.y < dateDrawPosition.y + dateDrawPosition.height + SPACE_LOTS_HEIGHT) {
				return i;
			}
		}
		return -1;
	}

	private int pointAtDate(Date date) {
		for (int i = 0; i < dateDrawPositions.size() - 1; i++) {
			DateDrawPosition dateDrawPosition1 = dateDrawPositions.get(i);
			DateDrawPosition dateDrawPosition2 = dateDrawPositions.get(i + 1);

			if (DateTimeUtils.includeDates(dateDrawPosition1.date, dateDrawPosition2.date, date, date)) {
				long m1 = DateTimeUtils.getNumberOfMinutes(dateDrawPosition1.date, dateDrawPosition2.date);
				long m2 = DateTimeUtils.getNumberOfMinutes(dateDrawPosition1.date, date);
				long y1 = dateDrawPosition2.y - dateDrawPosition1.y;
				long y2 = m2 * y1 / m1;
				int y3 = (int) (y2) + dateDrawPosition1.y + (dateDrawPosition1.height + SPACE_LOTS_HEIGHT) / 2;
				return y3;
			}
		}

		DateDrawPosition dateDrawPosition1 = dateDrawPositions.get(0);
		if (date.before(dateDrawPosition1.date)) {
			return 0;
		}
		DateDrawPosition dateDrawPosition2 = dateDrawPositions.get(dateDrawPositions.size() - 1);
		if (date.after(dateDrawPosition2.date)) {
			return dateDrawPosition2.y + dateDrawPosition2.height;
		}

		return -1;
	}

	public int getScrollableBlockIncrement(int orientation, int direction) {
		return getScrollableUnitIncrement(orientation, direction);
	}

	public int getScrollableUnitIncrement(int orientation, int direction) {
		if (orientation == SwingUtilities.VERTICAL) {
			Rectangle visibleRect = centerScrollPane.getViewport().getViewRect();
			int dateIndex = dateIndexAtPoint(new Point(visibleRect.x, visibleRect.y));
			if (direction < 0) {
				dateIndex--;
			}

			if (dateIndex < 0 || dateDrawPositions.size() <= dateIndex) {
				return 50;
			} else {
				DateDrawPosition dateDrawPosition = dateDrawPositions.get(dateIndex);
				int y = dateDrawPosition.y;
				int h = dateDrawPosition.height + SPACE_LOTS_HEIGHT;

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
			return 100;
		}
	}

	public LotDraw lotDrawAtPoint(Side side, Point p) {
		int index = dateIndexAtPoint(p);
		if (index >= 0) {
			DateDrawPosition dateDrawPosition = dateDrawPositions.get(index);
			LotDrawsPosition lotDrawsPosition = lotDrawsPositions.get(index);

			int y = dateDrawPosition.y + SPACE_LOTS_HEIGHT / 2;
			int x = START_LOT_X;

			if (side == Side.LEFT) {
				for (LotDraw lotDraw : lotDrawsPosition.leftLotDraws) {
					Dimension d = lotDraw.getRenderer().getMinimumSize(this, Side.LEFT, lotDraw);

					int w = computeWidthByLotDraw(Side.LEFT, lotDraw, d.width);

					int x1 = leftComponent.getWidth() - (x + w + 1);
					Rectangle rect = new Rectangle(x1, y, w, d.height + (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight));
					if (rect.contains(p)) {
						return lotDraw;
					}

					x += w + SPACE_LOTS_WIDTH;
				}
			} else {
				for (LotDraw lotDraw : lotDrawsPosition.rightLotDraws) {
					Dimension d = lotDraw.getRenderer().getMinimumSize(JTriage.this, Side.RIGHT, lotDraw);
					int w = computeWidthByLotDraw(Side.RIGHT, lotDraw, d.width);

					Rectangle rect = new Rectangle(x, y, w, d.height + (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight));
					if (rect.contains(p)) {
						return lotDraw;
					}

					x += w + SPACE_LOTS_WIDTH;
				}
			}
		}
		return null;
	}

	public Rectangle getLotDrawRectangle(Side side, LotDraw l) {
		for (int index = 0; index < dateDrawPositions.size(); index++) {
			DateDrawPosition dateDrawPosition = dateDrawPositions.get(index);
			LotDrawsPosition lotDrawsPosition = lotDrawsPositions.get(index);

			int x = START_LOT_X;
			int y = dateDrawPosition.y + SPACE_LOTS_HEIGHT / 2;

			if (side == Side.LEFT) {
				for (LotDraw lotDraw : lotDrawsPosition.leftLotDraws) {
					Dimension d = lotDraw.getRenderer().getMinimumSize(JTriage.this, Side.LEFT, lotDraw);
					int w = computeWidthByLotDraw(Side.LEFT, lotDraw, d.width);
					if (l.equals(lotDraw)) {
						int x1 = leftComponent.getWidth() - (x + w + 1);
						Rectangle rect = new Rectangle(x1, y, w, d.height + (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight));
						return rect;
					}

					x += w + SPACE_LOTS_WIDTH;
				}
			}

			if (side == Side.RIGHT) {
				for (LotDraw lotDraw : lotDrawsPosition.rightLotDraws) {
					Dimension d = lotDraw.getRenderer().getMinimumSize(JTriage.this, Side.RIGHT, lotDraw);
					int w = computeWidthByLotDraw(Side.RIGHT, lotDraw, d.width);
					if (l.equals(lotDraw)) {
						Rectangle rect = new Rectangle(x, y, w, d.height + (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight));
						return rect;
					}

					x += w + SPACE_LOTS_WIDTH;
				}
			}
		}
		return null;
	}

	public VoyageDraw voyageDrawAtPoint(Side side, Point p) {
		LotDraw lotDraw = lotDrawAtPoint(side, p);
		if (lotDraw != null) {
			return voyageDrawAtPoint(side, lotDraw, p);
		}
		return null;
	}

	private VoyageDraw voyageDrawAtPoint(Side side, LotDraw lotDraw, Point p) {
		Rectangle rect = getLotDrawRectangle(side, lotDraw);
		Rectangle voyageDrawsRect = new Rectangle(rect.x, rect.y + rect.height - (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight), rect.width, (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight));

		List<VoyageDraw> voyageDraws = model.getVoyageDraws(side, lotDraw);
		if (voyageDraws != null && voyageDraws.size() > 0) {
			int y1 = p.y - (voyageDrawsRect.y + SPACE_LOT_VOYAGE_HEIGHT);
			if (y1 >= 0 && y1 < voyageHeight) {
				int x1 = p.x - (voyageDrawsRect.x + SPACE_LOT_VOYAGE_WIDTH);

				int index = x1 / (voyageWidth + SPACE_VOYAGES_WIDTH);
				int x2 = x1 - index * (voyageWidth + SPACE_VOYAGES_WIDTH);

				if (x2 < voyageWidth && index >= 0 && index < voyageDraws.size()) {
					return voyageDraws.get(index);
				}
			}
		}
		return null;
	}

	public void print() {
		PrinterJob printJob = PrinterJob.getPrinterJob();
		if (printJob.printDialog()) {
			try {
				PageFormat pf = printJob.defaultPage();
				pf.setOrientation(PageFormat.LANDSCAPE);
				printJob.setPrintable(new MyPrintable(), pf);
				printJob.print();
			} catch (PrinterException e) {
				ErrorViewManager.getInstance().showErrorDialog(this, e);
			}
		}
	}

	private void disableDoubleBuffering(Component c) {
		RepaintManager currentManager = RepaintManager.currentManager(c);
		currentManager.setDoubleBufferingEnabled(false);
	}

	private void enableDoubleBuffering(Component c) {
		RepaintManager currentManager = RepaintManager.currentManager(c);
		currentManager.setDoubleBufferingEnabled(true);
	}

	private final class MyPrintable implements Printable {

		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
			Graphics2D g2d = (Graphics2D) graphics;

			double maxW = 2 * leftRightWidth + CENTER_WIDTH;
			double maxH = leftRightHeight;

			double imX = pageFormat.getImageableX();
			double imY = pageFormat.getImageableY();
			double imW = pageFormat.getImageableWidth();
			double imH = pageFormat.getImageableHeight();

			double ratio1 = maxW / (double) leftRightHeight;
			double realImH = imW / ratio1;

			int pageMax = (int) (realImH / imH + 1);

			if (pageIndex >= pageMax) {
				return NO_SUCH_PAGE;
			}

			double scaleW = imW / maxW;
			double scaleH = realImH / maxH;
			double realLeftRightWidth2 = leftRightWidth * scaleW;
			double realCenterWidth2 = CENTER_WIDTH * scaleW;

			double rectHeight = imH / scaleH;

			double currentY = pageIndex * rectHeight;

			paint(g2d, leftComponent, imX, imY, scaleW, scaleH, new Rectangle(0, (int) currentY, leftRightWidth, (int) rectHeight));

			paint(g2d, centerComponent, imX + realLeftRightWidth2, imY, scaleW, scaleH, new Rectangle(0, (int) currentY, CENTER_WIDTH, (int) rectHeight));

			paint(g2d, rightComponent, imX + realLeftRightWidth2 + realCenterWidth2, imY, scaleW, scaleH, new Rectangle(0, (int) currentY, leftRightWidth, (int) rectHeight));

			return PAGE_EXISTS;
		}

		private void paint(Graphics2D g2d, Component c, double x, double y, double sW, double sH, Rectangle rect) {
			g2d = (Graphics2D) g2d.create();
			g2d.translate(x, y);
			g2d.scale(sW, sH);
			g2d.translate(0, -rect.y);
			g2d.setClip(rect.x, rect.y, rect.width, rect.height);
			c.setVisible(true);
			disableDoubleBuffering(c);
			c.paint(g2d);
			enableDoubleBuffering(c);
			g2d.dispose();
		}
	}

	private final class MyMouseWheelListener implements MouseWheelListener {
		public void mouseWheelMoved(MouseWheelEvent e) {
			int scroll = getScrollableUnitIncrement(SwingUtilities.VERTICAL, e.getWheelRotation()) * e.getWheelRotation();
			int i = verticalScrollBar.getValue() + scroll;
			verticalScrollBar.setValue(i);
		}
	}

	private final class LeftRightScrollBarAdjustementListener implements AdjustmentListener {
		public void adjustmentValueChanged(AdjustmentEvent e) {
			JScrollBar rSB = rightScrollPane.getVerticalScrollBar();

			verticalScrollBar.removeAdjustmentListener(verticalScrollBarAdjustementListener);

			verticalScrollBar.setMinimum(rSB.getMinimum());
			verticalScrollBar.setMaximum(rSB.getMaximum());
			verticalScrollBar.setBlockIncrement(rSB.getBlockIncrement());
			verticalScrollBar.setUnitIncrement(rSB.getUnitIncrement());
			verticalScrollBar.setVisibleAmount(rSB.getVisibleAmount());

			verticalScrollBar.addAdjustmentListener(verticalScrollBarAdjustementListener);
		}
	}

	private final class VerticalScrollBarAdjustementListener implements AdjustmentListener {
		public void adjustmentValueChanged(AdjustmentEvent e) {
			JScrollBar rSB = rightScrollPane.getVerticalScrollBar();
			JScrollBar lSB = leftScrollPane.getVerticalScrollBar();
			JScrollBar cSB = centerScrollPane.getVerticalScrollBar();

			rSB.removeAdjustmentListener(leftRightScrollBarAdjustementListener);
			lSB.removeAdjustmentListener(leftRightScrollBarAdjustementListener);

			rSB.setValue(verticalScrollBar.getValue());
			lSB.setValue(verticalScrollBar.getValue());
			cSB.setValue(verticalScrollBar.getValue());

			rSB.addAdjustmentListener(leftRightScrollBarAdjustementListener);
			lSB.addAdjustmentListener(leftRightScrollBarAdjustementListener);
		}
	}

	private final class CenterComponent extends JComponent {

		private static final long serialVersionUID = -5283602924621768341L;

		public CenterComponent() {
			super();

			ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
			toolTipManager.registerComponent(this);

			this.setOpaque(true);
		}

		public Dimension getPreferredSize() {
			return new Dimension(CENTER_WIDTH, leftRightHeight);
		}

		public String getToolTipText(MouseEvent event) {
			String res = null;
			int a = dateIndexAtPoint(event.getPoint());

			if (a >= 0 && a < dateDrawPositions.size()) {
				Date d = null;

				DateDrawPosition dateDrawPosition1 = dateDrawPositions.get(a);
				int y1 = dateDrawPosition1.y + dateDrawPosition1.height / 2;
				if (y1 > event.getY() && a > 0) {
					DateDrawPosition dateDrawPosition2 = dateDrawPositions.get(a - 1);
					int y2 = dateDrawPosition2.y + dateDrawPosition2.height / 2;

					long m1 = DateTimeUtils.getNumberOfMinutes(dateDrawPosition2.date, dateDrawPosition1.date);
					long m2 = (event.getY() - y2) * m1 / (y1 - y2);

					Calendar c = Calendar.getInstance();
					c.setTime(dateDrawPosition2.date);
					c.add(Calendar.MINUTE, (int) m2);

					d = c.getTime();
				} else if (y1 < event.getY() && a < dateDrawPositions.size() - 1) {
					DateDrawPosition dateDrawPosition2 = dateDrawPositions.get(a + 1);
					int y2 = dateDrawPosition2.y + dateDrawPosition2.height / 2;

					long m1 = DateTimeUtils.getNumberOfMinutes(dateDrawPosition1.date, dateDrawPosition2.date);
					long m2 = (event.getY() - y1) * m1 / (y2 - y1);

					Calendar c = Calendar.getInstance();
					c.setTime(dateDrawPosition1.date);
					c.add(Calendar.MINUTE, (int) m2);

					d = c.getTime();
				} else {
					d = dateDrawPosition1.date;
				}

				res = fullFormatter.format(d);
			}
			return res;
		}

		private void paintDate(Graphics2D g2, int y, int height, Date date) {
			int width = this.getWidth();

			TextLayout text = new TextLayout(completFormatter.format(date), dateFont, g2.getFontRenderContext());
			Rectangle2D rect = text.getBounds();

			float x1 = (float) (width - rect.getWidth()) / 2.0f;
			float y1 = (float) (y + height / 2 - rect.getHeight());

			g2.setColor(dateColor);
			text.draw(g2, x1, y1);
		}

		private void paintHour(Graphics g, int y, int height, Date date) {
			Graphics2D g2 = (Graphics2D) g;

			TextLayout text = new TextLayout(DateTimeUtils.formatShortTime(date), hourFont, g2.getFontRenderContext());
			Rectangle2D rect = text.getBounds();

			float x1 = (float) (CENTER_WIDTH - rect.getWidth()) / 2.0f;
			float y1 = (float) (y + height / 2 + rect.getHeight() / 2);

			g2.setColor(hourLineColor);
			g2.drawLine(0, y + height / 2, (int) (x1 - 4), y + height / 2);
			g2.drawLine((int) (x1 + rect.getWidth()) + 6, y + height / 2, CENTER_WIDTH - 2, y + height / 2);

			g2.setColor(hourColor);
			text.draw(g2, x1, y1);
		}

		private void paintBar(Graphics g, Side side, int y1, int y2, int yMin, int yMax, int decalage) {
			Graphics2D g2 = (Graphics2D) g;

			if (y1 != y2) {
				Color color = null;
				if (y1 > y2) {
					int t = y1;
					y1 = y2;
					y2 = t;

					color = barInverseDateMinMaxColor;
				} else {
					color = barDateMinMaxColor;
				}

				int qw = CENTER_WIDTH / 8;
				if (y1 <= yMax && y2 >= yMin) {
					y1 = Math.max(yMin, y1);
					y2 = Math.min(yMax, y2);

					if (side == Side.LEFT) {
						if (decalage == 0) {
							g2.setColor(color);
							g2.fillRect(0, y1, qw, y2 - y1 + 1);
						} else {
							g2.setColor(color);
							g2.fillRect(qw, y1, qw, y2 - y1 + 1);
						}
					} else {
						if (decalage == 0) {
							g2.setColor(color);
							g2.fillRect(qw * 6, y1, qw, y2 - y1 + 1);
						} else {
							g2.setColor(color);
							g2.fillRect(qw * 7, y1, qw, y2 - y1 + 1);
						}
					}
				}
			}
		}

		private void paintBarDateMinMax(Graphics g, Side side, Date dateMin, Date dateMax, int y, int yMin, int yMax) {
			Graphics2D g2 = (Graphics2D) g;

			if (dateMin != null) {
				int y1 = pointAtDate(dateMin);
				paintBar(g2, side, y1, y, yMin, yMax, 0);
			}

			if (dateMax != null) {
				int y2 = pointAtDate(dateMax);
				paintBar(g2, side, y, y2, yMin, yMax, 1);
			}
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			int width = this.getWidth();

			Graphics2D g2 = (Graphics2D) g;

			Rectangle clip = g2.getClipBounds();

			Point left = clip.getLocation();
			Point right = new Point(clip.x + clip.width - 1, clip.y + clip.height - 1);

			int a = dateIndexAtPoint(left);
			int b = dateIndexAtPoint(right);

			g2.setColor(centerBackgroundColor);
			g2.fillRect(clip.x, clip.y, clip.width, clip.height);

			g2.setColor(centerBorderColor);
			g2.drawLine(left.x, left.y, left.x, right.y);
			g2.drawLine(right.x, left.y, right.x, right.y);

			int count = dateDrawPositions.size();
			if (dateDrawPositions != null && count > 0 && a >= 0 && a < count) {
				b = b >= 0 && b < count ? b : count - 1;
				for (int index = a; index <= b; index++) {
					DateDrawPosition dateDrawPosition = dateDrawPositions.get(index);
					if (dateDrawPosition.showDay) {
						paintDate(g2, dateDrawPosition.y, dateDrawPosition.height, dateDrawPosition.date);
					}

					paintHour(g2, dateDrawPosition.y, dateDrawPosition.height, dateDrawPosition.date);
				}
			}

			if (currentSelectedPath != null) {
				Rectangle currentSelectedRect = getLotDrawRectangle(currentSelectedPath.getSide(), currentSelectedPath.getLotDraw());

				if (otherSelectedPath != null) {
					int hw = width / 2;

					g2.setStroke(selectedStroke);
					g2.setColor(selectedColor);

					int y1 = currentSelectedRect.y + (currentSelectedRect.height - (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight)) + SPACE_LOT_VOYAGE_HEIGHT + voyageHeight / 2;

					if (y1 >= left.y && y1 <= right.y) {
						if (currentSelectedPath.getSide() == Side.LEFT) {
							g2.drawLine(0, y1, hw, y1);
						} else {
							g2.drawLine(hw, y1, width - 1, y1);
						}
					}

					Rectangle otherSelectedRect = getLotDrawRectangle(otherSelectedPath.getSide(), otherSelectedPath.getLotDraw());
					int y2 = otherSelectedRect.y + (otherSelectedRect.height - (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight)) + SPACE_LOT_VOYAGE_HEIGHT + voyageHeight / 2;
					if (y2 >= left.y && y2 <= right.y) {
						if (otherSelectedPath.getSide() == Side.LEFT) {
							g2.drawLine(0, y2, hw, y2);
						} else {
							g2.drawLine(hw, y2, width - 1, y2);
						}
					}

					if (y1 > y2) {
						int t = y1;
						y1 = y2;
						y2 = t;
					}
					if (y1 <= right.y && y2 >= left.y) {
						y1 = Math.max(left.y, y1);
						y2 = Math.min(right.y, y2);
						g2.drawLine(hw, y1, hw, y2);
					}

					Date dateMin = otherSelectedPath.getLotDraw().getDateMin();
					Date dateMax = otherSelectedPath.getLotDraw().getDateMax();

					int c = dateIndexAtPoint(otherSelectedRect.getLocation());
					DateDrawPosition dateDrawPosition = dateDrawPositions.get(c);

					paintBarDateMinMax(g, otherSelectedPath.getSide(), dateMin, dateMax, dateDrawPosition.y + dateDrawPosition.height / 2, left.y, right.y);
				}

				Date dateMin = currentSelectedPath.getLotDraw().getDateMin();
				Date dateMax = currentSelectedPath.getLotDraw().getDateMax();

				int c = dateIndexAtPoint(currentSelectedRect.getLocation());
				DateDrawPosition dateDrawPosition = dateDrawPositions.get(c);

				paintBarDateMinMax(g, currentSelectedPath.getSide(), dateMin, dateMax, dateDrawPosition.y + dateDrawPosition.height / 2, left.y, right.y);
			}
		}
	}

	private final class LeftRightComponent extends JComponent implements MouseListener {

		private static final long serialVersionUID = -4606199008924793662L;

		private Side side;

		private boolean selectedVoyageDraw;

		private Rectangle selectedVoyageDrawRect;

		public LeftRightComponent(Side side) {
			super();

			this.side = side;

			this.setOpaque(true);
			this.addMouseListener(this);

			ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
			toolTipManager.registerComponent(this);
		}

		public Dimension getPreferredSize() {
			return new Dimension(leftRightWidth, leftRightHeight);
		}

		public String getToolTipText(MouseEvent e) {
			String res = null;

			LotDraw lotDraw = lotDrawAtPoint(side, e.getPoint());
			if (lotDraw != null) {
				VoyageDraw voyageDraw = voyageDrawAtPoint(side, lotDraw, e.getPoint());
				if (voyageDraw != null) {
					res = voyageDraw.getToolTipText();
				} else {
					res = lotDraw.getToolTipText();
				}
			}
			return res;
		}

		private void paintLiaison(Graphics g, Rectangle rect) {
			Graphics2D g2 = (Graphics2D) g;

			g2.setColor(highLineColor);
			g2.drawLine(rect.x, rect.y, rect.x + rect.width, rect.y);
		}

		private void paintVoyageDraw(Graphics g, LotDraw lotDraw, VoyageDraw voyageDraw, Rectangle rect) {
			Graphics2D g2 = (Graphics2D) g.create();

			boolean selected = false;
			if (currentSelectedPath != null && currentSelectedPath.getVoyageDraw() != null && currentSelectedPath.getVoyageDraw().equals(voyageDraw)) {
				selected = true;
				selectedVoyageDraw = true;
				selectedVoyageDrawRect = rect;
			}

			g2.clipRect(rect.x, rect.y, rect.width, rect.height);

			voyageDraw.getRenderer().paintVoyageDraw(g2, rect, JTriage.this, side, lotDraw, voyageDraw, selected);

			g2.dispose();
		}

		private Area getLotDrawArea(Rectangle rect) {
			Area a = new Area(new Rectangle2D.Double(rect.x, rect.y, rect.width - 1, rect.height - SPACE_LOT_VOYAGE_HEIGHT - 1));
			a.add(new Area(new Rectangle2D.Double(rect.x + SPACE_LOT_VOYAGE_WIDTH, rect.y + rect.height - SPACE_LOT_VOYAGE_HEIGHT - 1, rect.width - SPACE_LOT_VOYAGE_WIDTH * 2 - 1,
					SPACE_LOT_VOYAGE_HEIGHT)));
			a.add(new Area(new Ellipse2D.Double(rect.x, rect.y + rect.height - SPACE_LOT_VOYAGE_HEIGHT * 2 - 1, SPACE_LOT_VOYAGE_WIDTH * 2, SPACE_LOT_VOYAGE_HEIGHT * 2)));
			a.add(new Area(new Ellipse2D.Double(rect.x + rect.width - SPACE_LOT_VOYAGE_WIDTH * 2 - 1, rect.y + rect.height - SPACE_LOT_VOYAGE_HEIGHT * 2 - 1, SPACE_LOT_VOYAGE_WIDTH * 2,
					SPACE_LOT_VOYAGE_HEIGHT * 2)));
			return a;
		}

		private void paintLotDraw(Graphics g, LotDraw lotDraw, Rectangle rect) {
			Graphics2D g2 = (Graphics2D) g.create();

			boolean selected = false;
			if (currentSelectedPath != null && currentSelectedPath.getLotDraw().equals(lotDraw)) {
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

			Rectangle voyageDrawsRect = new Rectangle(rect.x, rect.y + rect.height - (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight), rect.width, (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight));

			Rectangle infoLotDrawRect = new Rectangle(rect.x, rect.y, rect.width, rect.height - (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight));
			Rectangle clip = g2.getClipBounds();
			g2.clipRect(infoLotDrawRect.x + 1, infoLotDrawRect.y + 1, infoLotDrawRect.width - 2, infoLotDrawRect.height - 2);

			lotDraw.getRenderer().paintInfoLotDraw(g2, infoLotDrawRect, JTriage.this, side, lotDraw, selected);

			g2.setClip(clip.x, clip.y, clip.width, clip.height);

			if (selected) {
				g2.setColor(selectedLotDrawColor);
				g2.fill(lotDrawArea);
			}

			List<VoyageDraw> voyageDraws = model.getVoyageDraws(side, lotDraw);
			if (voyageDraws != null && voyageDraws.size() > 0) {
				int x = voyageDrawsRect.x + SPACE_LOT_VOYAGE_WIDTH;

				for (VoyageDraw voyageDraw : voyageDraws) {
					Rectangle voyageDrawRect = new Rectangle(x, voyageDrawsRect.y + SPACE_LOT_VOYAGE_HEIGHT, voyageWidth, voyageHeight);

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
			Point right = new Point(clip.x + clip.width - 1, clip.y + clip.height - 1);

			int a = dateIndexAtPoint(left);
			int b = dateIndexAtPoint(right);

			g2.setColor(backgroundLeftRightColor);
			g2.fillRect(clip.x, clip.y, clip.width, clip.height);

			selectedVoyageDraw = false;

			int count = dateDrawPositions.size();
			if (dateDrawPositions != null && count > 0 && a >= 0 && a < count) {
				b = b >= 0 && b < count ? b : count - 1;
				for (int index = a; index <= b; index++) {
					DateDrawPosition dateDrawPosition = dateDrawPositions.get(index);
					LotDrawsPosition lotDrawsPosition = lotDrawsPositions.get(index);

					int x = START_LOT_X;
					int y = dateDrawPosition.y + SPACE_LOTS_HEIGHT / 2;

					if (side == Side.LEFT) {
						Rectangle liaisonRect = new Rectangle(0, dateDrawPosition.y, leftComponent.getWidth(), dateDrawPosition.height + SPACE_LOTS_HEIGHT);
						paintLiaison(g2, liaisonRect);

						for (LotDraw lotDraw : lotDrawsPosition.leftLotDraws) {
							Dimension d = lotDraw.getRenderer().getMinimumSize(JTriage.this, Side.LEFT, lotDraw);
							int w = computeWidthByLotDraw(Side.LEFT, lotDraw, d.width);

							int x1 = leftComponent.getWidth() - (x + w + 1);
							Rectangle rect = new Rectangle(x1, y, w, d.height + (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight));
							paintLotDraw(g2, lotDraw, rect);

							x += w + SPACE_LOTS_WIDTH;
						}
					}

					if (side == Side.RIGHT) {
						Rectangle liaisonRect = new Rectangle(0, dateDrawPosition.y, rightComponent.getWidth(), dateDrawPosition.height + SPACE_LOTS_HEIGHT);
						paintLiaison(g2, liaisonRect);

						for (LotDraw lotDraw : lotDrawsPosition.rightLotDraws) {
							Dimension d = lotDraw.getRenderer().getMinimumSize(JTriage.this, Side.RIGHT, lotDraw);
							int w = computeWidthByLotDraw(Side.RIGHT, lotDraw, d.width);

							Rectangle rect = new Rectangle(x, y, w, d.height + (SPACE_LOT_VOYAGE_HEIGHT * 2 + voyageHeight));
							paintLotDraw(g2, lotDraw, rect);

							x += w + SPACE_LOTS_WIDTH;
						}
					}
				}
			}

			if (selectedVoyageDraw) {
				if (otherSelectedPath != null) {
					g2.setColor(selectedColor);
					g2.setStroke(selectedStroke);

					int cy = selectedVoyageDrawRect.y + selectedVoyageDrawRect.height / 2;
					if (side == Side.LEFT) {
						g2.drawLine(selectedVoyageDrawRect.x + selectedVoyageDrawRect.width, cy, this.getWidth(), cy);
					} else {
						g2.drawLine(0, cy, selectedVoyageDrawRect.x, cy);
					}
				}
			}
		}

		private void dispatchMouse(MouseEvent e) {
			MouseEvent me = SwingUtilities.convertMouseEvent(this, e, JTriage.this);
			JTriage.this.processMouseEvent(me);
		}

		public void mouseClicked(MouseEvent e) {
			dispatchMouse(e);
		}

		public void mouseEntered(MouseEvent e) {
			dispatchMouse(e);
		}

		public void mouseExited(MouseEvent e) {
			dispatchMouse(e);
		}

		public void mouseReleased(MouseEvent e) {
			dispatchMouse(e);
		}

		public void mousePressed(MouseEvent e) {
			TriagePath selectedPath = null;
			TriagePath oSelectedPath = null;

			LotDraw lotDraw = lotDrawAtPoint(side, e.getPoint());
			if (lotDraw != null && lotDraw.isSelected()) {
				VoyageDraw voyageDraw = voyageDrawAtPoint(side, lotDraw, e.getPoint());
				if (voyageDraw != null && voyageDraw.isSelected()) {
					selectedPath = new TriagePath(side, lotDraw, voyageDraw);
					oSelectedPath = searchOtherSelectedPath(selectedPath);
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
				otherSelectedPath = oSelectedPath;

				resizeAndRepaint();

				fireTriageSelectionChanged();
			}

			dispatchMouse(e);
		}
	}

	private final class LotDrawsPosition {

		List<LotDraw> leftLotDraws;

		List<LotDraw> rightLotDraws;
	}

	private final class DateDrawPosition {

		int y;

		int height;

		Date date;

		boolean showDay;
	}

	private final class LotDrawComparator implements Comparator<LotDraw> {
		public int compare(LotDraw o1, LotDraw o2) {
			return o1.getDate().compareTo(o2.getDate());
		}
	}

	private final class MyScrollBar extends JScrollBar {

		private static final long serialVersionUID = -8022981469727015605L;

		public MyScrollBar(int orientation) {
			super(orientation);
		}

		@Override
		public int getUnitIncrement(int direction) {
			return getScrollableUnitIncrement(getOrientation(), direction);
		}

		@Override
		public int getBlockIncrement(int direction) {
			return getScrollableBlockIncrement(getOrientation(), direction);
		}
	}
}
