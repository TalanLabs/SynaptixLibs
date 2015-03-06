package com.synaptix.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.Calendar;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoundedRangeModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.JViewport;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.event.SimpleTimelineDatesListener;
import com.synaptix.swing.event.SimpleTimelineListener;
import com.synaptix.swing.event.SimpleTimelineModelEvent;
import com.synaptix.swing.event.SimpleTimelineModelListener;
import com.synaptix.swing.event.SimpleTimelineResourcesModelEvent;
import com.synaptix.swing.event.SimpleTimelineResourcesModelListener;
import com.synaptix.swing.event.SimpleTimelineSelectionListener;
import com.synaptix.swing.plaf.basic.DayWidthSliderUI;
import com.synaptix.swing.simpletimeline.DefaultSimpleTimelineResourcesModel;
import com.synaptix.swing.simpletimeline.JSimpleTimelineCenter;
import com.synaptix.swing.simpletimeline.JSimpleTimelineDatesHeader;
import com.synaptix.swing.simpletimeline.JSimpleTimelineResourcesHeader;
import com.synaptix.swing.simpletimeline.JWeekCalendarPanel;
import com.synaptix.swing.simpletimeline.SimpleTimelineDrag;
import com.synaptix.swing.simpletimeline.SimpleTimelineDrop;
import com.synaptix.swing.simpletimeline.SimpleTimelineResource;
import com.synaptix.swing.simpletimeline.SimpleTimelineResourcesModel;
import com.synaptix.swing.simpletimeline.SimpleTimelineSelectionModel;
import com.synaptix.swing.utils.DateTimeUtils;
import com.synaptix.swing.utils.DragDropComponent;
import com.synaptix.swing.utils.SynaptixTransferHandler;
import com.synaptix.view.swing.error.ErrorViewManager;

public class JSimpleTimeline extends JPanel implements SimpleTimelineModelListener, SimpleTimelineResourcesModelListener, SimpleTimelineDatesListener, SimpleTimelineSelectionListener,
		DragDropComponent {

	private static final long serialVersionUID = -5807767341999317194L;

	public enum DropMode {
		NONE, OUTLINE_GHOST, FILL_GHOST, RENDERER
	};

	private static final Icon ICON_ZOOM_PLUS;

	private static final Icon ICON_ZOOM_MINUS;

	private static final Icon ICON_PRINT;

	private SimpleTimelineModel dataModel;

	private SimpleTimelineResourcesModel resourcesModel;

	private SimpleTimelineSelectionModel selectionModel;

	private JWeekCalendarPanel weekCalendarPanel;

	private JViewport resourcesViewport;

	private JSimpleTimelineResourcesHeader resourcesPanel;

	private JViewport datesViewport;

	private JSimpleTimelineDatesHeader datesPanel;

	private JViewport internalTimelineViewport;

	private JSimpleTimelineCenter internalTimelinePanel;

	private JScrollBar verticalScrollBar;

	private JScrollBar horizontalScrollBar;

	private Calendar currentDate;

	private int nbBeforeDays;

	private Date dateMin;

	private int nbAfterDays;

	private Date dateMax;

	private JSlider dayWidthSlider;

	private JLabel minusLabel;

	private JLabel plusLabel;

	private DropMode dropMode;

	private int resourceDrop;

	private SimpleTask[] tasksDrop;

	private SimpleTimelineDrop timelineDrop;

	private SimpleTimelineDrag timelineDrag;

	private EventListenerList listenerList;

	private ChangeListener sliderPanelChangeListener;

	private Action printAction;

	private JPopupMenu centerPopupMenu;

	private JPopupMenu resourcesPopupMenu;

	static {
		ICON_ZOOM_PLUS = new ImageIcon(JSimpleTimeline.class.getResource("/images/zoomPlusSlider.png")); //$NON-NLS-1$
		ICON_ZOOM_MINUS = new ImageIcon(JSimpleTimeline.class.getResource("/images/zoomMinusSlider.png")); //$NON-NLS-1$
		ICON_PRINT = new ImageIcon(JSimpleTimeline.class.getResource("/images/iconPrint.png")); //$NON-NLS-1$
	}

	public JSimpleTimeline(SimpleTimelineModel dataModel) {
		super(new BorderLayout());

		listenerList = new EventListenerList();

		currentDate = Calendar.getInstance();
		currentDate.setTime(DateTimeUtils.clearHourForDate(new Date()));

		nbBeforeDays = 2;
		nbAfterDays = 2;

		dropMode = DropMode.OUTLINE_GHOST;

		this.dataModel = dataModel;
		dataModel.addSimpleTimelineModelListener(this);

		this.resourcesModel = new DefaultSimpleTimelineResourcesModel();
		resourcesModel.addResourcesModelListener(this);

		this.selectionModel = new SimpleTimelineSelectionModel();
		selectionModel.addSelectionListener(this);

		initActions();
		initComponents();

		updateAll();
		centerAtCurrentDay();

		this.add(buildEditorPanel(), BorderLayout.CENTER);
	}

	private void initActions() {
		printAction = new PrintAction();
	}

	private void createComponents() {
		weekCalendarPanel = new JWeekCalendarPanel();

		resourcesViewport = new JViewport();
		resourcesPanel = new JSimpleTimelineResourcesHeader(resourcesModel);

		datesViewport = new JViewport();
		datesPanel = new JSimpleTimelineDatesHeader();

		internalTimelineViewport = new JViewport();
		internalTimelinePanel = new JSimpleTimelineCenter();

		verticalScrollBar = new MyScrollBar(JScrollBar.VERTICAL);
		horizontalScrollBar = new MyScrollBar(JScrollBar.HORIZONTAL);

		dayWidthSlider = new JSlider(JSlider.HORIZONTAL, JSimpleTimelineDatesHeader.MIN_DAY_WIDTH, JSimpleTimelineDatesHeader.MAX_DAY_WIDTH, datesPanel.getDayWidth());

		minusLabel = new JLabel(ICON_ZOOM_MINUS);
		plusLabel = new JLabel(ICON_ZOOM_PLUS);
	}

	private void initComponents() {
		createComponents();

		resourcesPanel.setSimpleTimeline(this);
		datesPanel.setSimpleTimeline(this);
		internalTimelinePanel.setSimpleTimeline(this);

		resourcesPanel.addMouseListener(new ResourcesMouseListener());

		resourcesViewport.setView(resourcesPanel);
		resourcesViewport.addChangeListener(new ResourcesViewportChangeListener());

		datesPanel.addDatesListener(this);

		datesViewport.setView(datesPanel);
		datesViewport.addChangeListener(new DatesViewportChangeListener());

		internalTimelinePanel.setTransferHandler(new MyTransferHandler());
		internalTimelinePanel.addMouseListener(new CenterMouseListener());

		internalTimelineViewport.setView(internalTimelinePanel);

		verticalScrollBar.getModel().addChangeListener(new VerticalScrollBarChangeListener());
		horizontalScrollBar.getModel().addChangeListener(new HorizontalScrollBarChangeListener());

		weekCalendarPanel.addChangeListener(new CalendarPanelChangeListener());

		MyMouseWheelListener mwl = new MyMouseWheelListener();
		resourcesViewport.addMouseWheelListener(mwl);
		internalTimelineViewport.addMouseWheelListener(mwl);
		verticalScrollBar.addMouseWheelListener(mwl);

		internalTimelineViewport.addComponentListener(new MyComponentListener());

		sliderPanelChangeListener = new SliderPanelChangeListener();
		dayWidthSlider.addChangeListener(sliderPanelChangeListener);
		dayWidthSlider.setUI(new DayWidthSliderUI());

		minusLabel.setBorder(null);
		plusLabel.setBorder(null);
	}

	private JLabel createLabelWithBorder() {
		JLabel l = new JLabel();
		l.setOpaque(true);
		l.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		return l;
	}

	private JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:100DLU:GROW(1.0),FILL:PREF:NONE", //$NON-NLS-1$
				"FILL:DEFAULT:NONE,FILL:100DLU:GROW(1.0),FILL:PREF:NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(resourcesViewport, cc.xyw(1, 2, 3));
		builder.add(datesViewport, cc.xy(4, 1));
		builder.add(internalTimelineViewport, cc.xy(4, 2));
		builder.add(verticalScrollBar, cc.xy(5, 2));
		builder.add(weekCalendarPanel, cc.xyw(1, 1, 3));
		builder.add(horizontalScrollBar, cc.xy(4, 3));

		JButton b = new JButton(printAction);
		b.setFocusPainted(false);
		b.setFocusable(false);
		b.setPreferredSize(new Dimension(verticalScrollBar.getPreferredSize().width, horizontalScrollBar.getPreferredSize().height));

		builder.add(b, cc.xy(5, 3));
		builder.add(dayWidthSlider, cc.xy(2, 3));
		builder.add(minusLabel, cc.xy(1, 3));
		builder.add(plusLabel, cc.xy(3, 3));
		builder.add(createLabelWithBorder(), cc.xy(5, 1));
		return builder.getPanel();
	}

	public void setDefaultResourceHeight(int height) {
		for (int i = 0; i < resourcesModel.getResourceCount(); i++) {
			SimpleTimelineResource r = resourcesModel.getResource(i);
			r.setPreferredHeight(height);
			r.setHeight(height);
			r.setMinHeight(height);
			r.setMaxHeight(height);
		}

		resizeAndRepaint();
	}

	public void addSimpleTimelineListener(SimpleTimelineListener l) {
		listenerList.add(SimpleTimelineListener.class, l);
	}

	public void removeSimpleTimelineListener(SimpleTimelineListener l) {
		listenerList.remove(SimpleTimelineListener.class, l);
	}

	private void fireDatesSimpleTimelineListener() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SimpleTimelineListener.class) {
				((SimpleTimelineListener) listeners[i + 1]).datesChanged();
			}
		}
	}

	private void fireZoomSimpleTimelineListener() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SimpleTimelineListener.class) {
				((SimpleTimelineListener) listeners[i + 1]).zoomChanged();
			}
		}
	}

	private void updateAll() {
		updateResourcesFromModel();
		updateCalendarPanel();
		updateVerticalScrollBar();
		updateHorizontalScrollBar();
		updateDateMin();
		updateDateMax();
	}

	private void updateCalendarPanel() {
		weekCalendarPanel.setNbBeforeDays(nbBeforeDays);
		weekCalendarPanel.setNbAfterDays(nbAfterDays);
		weekCalendarPanel.setDate(currentDate.getTime());
	}

	private void updateVerticalScrollBar() {
		Dimension extentSize = resourcesViewport.getExtentSize();
		Dimension viewSize = resourcesViewport.getViewSize();
		Point viewPosition = resourcesViewport.getViewPosition();

		int extent = extentSize.height;
		int max = viewSize.height;
		int value = Math.max(0, Math.min(viewPosition.y, max - extent));
		verticalScrollBar.setValues(value, extent, 0, max);
	}

	private void updateHorizontalScrollBar() {
		Dimension extentSize = datesViewport.getExtentSize();
		Dimension viewSize = datesViewport.getViewSize();
		Point viewPosition = datesViewport.getViewPosition();

		int extent = extentSize.width;
		int max = viewSize.width;
		int value = Math.max(0, Math.min(viewPosition.x, max - extent));
		horizontalScrollBar.setValues(value, extent, 0, max);
	}

	private void updateResourcesFromModel() {
		while (resourcesModel.getResourceCount() > 0) {
			resourcesModel.removeResource(resourcesModel.getResource(0));
		}

		// Create new ressources from the data model info
		for (int i = 0; i < dataModel.getResourceCount(); i++) {
			SimpleTimelineResource newResource = new SimpleTimelineResource(i);
			int modelColumn = newResource.getModelIndex();
			String columnName = dataModel.getResourceName(modelColumn);
			newResource.setHeaderValue(columnName);
			resourcesModel.addResource(newResource);
		}
	}

	private void updateDateMin() {
		Calendar cMin = Calendar.getInstance();
		cMin.setTime(currentDate.getTime());
		cMin.add(Calendar.DAY_OF_YEAR, -nbBeforeDays);
		dateMin = cMin.getTime();
	}

	private void updateDateMax() {
		Calendar cMax = Calendar.getInstance();
		cMax.setTime(currentDate.getTime());
		cMax.add(Calendar.DAY_OF_YEAR, nbAfterDays + 1);
		dateMax = cMax.getTime();
	}

	private void centerAtCurrentDay() {
		int w = nbBeforeDays * datesPanel.getDayWidth();
		int b = (horizontalScrollBar.getVisibleAmount() - datesPanel.getDayWidth()) / 2;
		horizontalScrollBar.setValue(w - b);
	}

	public Date getCurrentDate() {
		return currentDate.getTime();
	}

	public void setCurrentDate(Date date) {
		weekCalendarPanel.setDate(date);
		currentDate.setTime(date);
		updateAll();
		resizeAndRepaint();
		centerAtCurrentDay();
	}

	public int getNbBeforeDays() {
		return nbBeforeDays;
	}

	public void setNbBeforeDays(int nbBeforeDays) {
		this.nbBeforeDays = nbBeforeDays;
		weekCalendarPanel.setNbBeforeDays(nbBeforeDays);
		updateAll();
		resizeAndRepaint();
		centerAtCurrentDay();
	}

	public int getNbAfterDays() {
		return nbAfterDays;
	}

	public void setNbAfterDays(int nbAfterDays) {
		this.nbAfterDays = nbAfterDays;
		weekCalendarPanel.setNbAfterDays(nbAfterDays);
		updateAll();
		resizeAndRepaint();
		centerAtCurrentDay();
	}

	public Date getDateMin() {
		return dateMin;
	}

	public Date getDateMax() {
		return dateMax;
	}

	public void setDropMode(DropMode dropMode) {
		this.dropMode = dropMode;
	}

	public DropMode getDropMode() {
		return dropMode;
	}

	public void setTaskDrop(int resource, SimpleTask... tasks) {
		this.resourceDrop = resource;
		this.tasksDrop = tasks;

		internalTimelinePanel.repaint();
	}

	public void clearTaskDrop() {
		this.resourceDrop = -1;
		this.tasksDrop = null;

		internalTimelinePanel.repaint();
	}

	public void clearDrop() {
		clearTaskDrop();
	}

	public int getResourceDrop() {
		return resourceDrop;
	}

	public SimpleTask[] getTaskDrop() {
		return tasksDrop;
	}

	public void setSimpleTimelineDrop(SimpleTimelineDrop timelineDrop) {
		this.timelineDrop = timelineDrop;

		if (timelineDrop != null) {
			SynaptixTransferHandler.addDragDropComponent(JSimpleTimeline.this);
		} else {
			SynaptixTransferHandler.removeDragDropComponent(JSimpleTimeline.this);
		}
	}

	public SimpleTimelineDrop getSimpleTimelineDrop() {
		return timelineDrop;
	}

	public void setSimpleTimelineDrag(SimpleTimelineDrag timelineDrag) {
		this.timelineDrag = timelineDrag;
	}

	public SimpleTimelineDrag getSimpleTimelineDrag() {
		return timelineDrag;
	}

	public JSimpleTimelineCenter getSimpleTimelineCenter() {
		return internalTimelinePanel;
	}

	public SimpleTimelineModel getModel() {
		return dataModel;
	}

	public SimpleTimelineSelectionModel getSelectionModel() {
		return selectionModel;
	}

	public SimpleTimelineResourcesModel getResourcesModel() {
		return resourcesModel;
	}

	public JPopupMenu getCenterPopupMenu() {
		return centerPopupMenu;
	}

	public void setCenterPopupMenu(JPopupMenu centerPopupMenu) {
		this.centerPopupMenu = centerPopupMenu;
	}

	public JPopupMenu getResourcesPopupMenu() {
		return resourcesPopupMenu;
	}

	public void setResourcesPopupMenu(JPopupMenu resourcesPopupMenu) {
		this.resourcesPopupMenu = resourcesPopupMenu;
	}

	public int convertResourceIndexToModel(int viewResourceIndex) {
		if (viewResourceIndex < 0) {
			return viewResourceIndex;
		}
		return getResourcesModel().getResource(viewResourceIndex).getModelIndex();
	}

	public int convertResourceIndexToView(int modelResourceIndex) {
		if (modelResourceIndex < 0) {
			return modelResourceIndex;
		}
		SimpleTimelineResourcesModel rm = getResourcesModel();
		for (int resource = 0; resource < getResourceCount(); resource++) {
			if (rm.getResource(resource).getModelIndex() == modelResourceIndex) {
				return resource;
			}
		}
		return -1;
	}

	public int resourceAtPoint(Point point) {
		return getResourcesModel().getResourceIndexAtY(point.y);
	}

	public int pointAtRessource(int ressource) {
		int h = 0;
		for (int i = 0; i < ressource; i++) {
			h += getResourcesModel().getHeight(i);
		}
		return h;
	}

	public int getResourceCount() {
		return getResourcesModel().getResourceCount();
	}

	public Rectangle getResourceRect(int ressource) {
		Rectangle r = new Rectangle();
		SimpleTimelineResourcesModel cm = getResourcesModel();

		r.width = resourcesPanel.getWidth();

		if (ressource < 0) {
		} else if (ressource >= cm.getResourceCount()) {
		} else {
			for (int i = 0; i < ressource; i++) {
				r.y += cm.getResource(i).getHeight();
			}

			r.height = cm.getResource(ressource).getHeight();
		}
		return r;
	}

	private SimpleTimelineResource getResizingResource() {
		return (resourcesPanel == null) ? null : resourcesPanel.getResizingResource();
	}

	public JSimpleTimelineResourcesHeader getResourcesHeader() {
		return resourcesPanel;
	}

	public int pointAtDate(Date date) {
		long m = DateTimeUtils.getNumberOfMinutes(dateMin, date) * getDayWidth();
		long a = m / (60L * 24L);
		return (int) a;
	}

	public Date dateAtPoint(Point point) {
		int x = point.x;

		Calendar cMin = Calendar.getInstance();
		cMin.setTime(dateMin);

		int nbDays = x / getDayWidth();
		int minutes = ((x - nbDays * getDayWidth()) * 60 * 24) / getDayWidth();

		Calendar c = Calendar.getInstance();
		c.setTime(dateMin);
		c.add(Calendar.DAY_OF_YEAR, nbDays);
		c.add(Calendar.MINUTE, minutes);

		long deltaOffset = c.get(Calendar.DST_OFFSET) - cMin.get(Calendar.DST_OFFSET);
		long milliseconds = (long) ((double) x * (60.0 * 24.0 * 1000.0 * 60.0) / (double) getDayWidth());

		c.setTimeInMillis(cMin.getTimeInMillis() + milliseconds - deltaOffset);

		return c.getTime();
	}

	public JSimpleTimelineDatesHeader getDatesHeader() {
		return datesPanel;
	}

	public int getDayWidth() {
		return datesPanel.getDayWidth();
	}

	public void setDayWidth(int dayWidth) {
		datesPanel.setDayWidth(dayWidth);
	}

	protected void resizeAndRepaint() {
		if (resourcesPanel != null) {
			resourcesPanel.resizeAndRepaint();
		}
		if (datesPanel != null) {
			datesPanel.resizeAndRepaint();
		}
		internalTimelinePanel.resizeAndRepaint();
	}

	public void simpleTimelineChanged(SimpleTimelineModelEvent e) {
		updateAll();

		resizeAndRepaint();
	}

	public void valueChanged() {
		if (resourcesPanel != null) {
			resourcesPanel.repaint();
		}
		internalTimelinePanel.repaint();
	}

	public void datesDayWidthChanged() {
		dayWidthSlider.removeChangeListener(sliderPanelChangeListener);
		dayWidthSlider.setValue(getDayWidth());
		dayWidthSlider.addChangeListener(sliderPanelChangeListener);

		// internalTimelinePanel.revalidate();
		datesPanel.resizeAndRepaint();
		internalTimelinePanel.repaint();

		centerAtCurrentDay();
	}

	public void resourceAdded(SimpleTimelineResourcesModelEvent e) {
		resizeAndRepaint();
	}

	public void resourceMarginChanged(ChangeEvent e) {
		SimpleTimelineResource resizingRessource = getResizingResource();
		if (resizingRessource != null) {
			resizingRessource.setPreferredHeight(resizingRessource.getHeight());
		}
		resizeAndRepaint();
	}

	public void resourceMoved(SimpleTimelineResourcesModelEvent e) {
		internalTimelinePanel.repaint();
	}

	public void resourceRemoved(SimpleTimelineResourcesModelEvent e) {
		resizeAndRepaint();
	}

	public void resourceSelectionChanged(ListSelectionEvent e) {
	}

	public void scrollRectToVisible(Rectangle contentRect) {
		// internalTimelineViewport.scrollRectToVisible(contentRect);
		//
		// TODO faire le suivi de scroll

		// if (dx != 0 || dy != 0) {
		// System.out.println("A faire scroller..." + dx + " " + dy + " "
		// + contentRect);
		// Point viewPosition = internalTimelineViewport.getViewPosition();
		// Dimension viewSize = internalTimelinePanel.getSize();
		// int startX = viewPosition.x;
		// int startY = viewPosition.y;
		// Dimension extent = internalTimelineViewport.getExtentSize();
		//
		// viewPosition.x -= dx;
		// viewPosition.y -= dy;
		//
		// if (extent.width > viewSize.width) {
		// viewPosition.x = viewSize.width - extent.width;
		// } else {
		// viewPosition.x = Math.max(0, Math.min(viewSize.width
		// - extent.width, viewPosition.x));
		// }
		// if (viewPosition.y + extent.height > viewSize.height) {
		// viewPosition.y = Math.max(0, viewSize.height - extent.height);
		// } else if (viewPosition.y < 0) {
		// viewPosition.y = 0;
		// }
		// if (viewPosition.x != startX || viewPosition.y != startY) {
		// Point p1 = resourcesViewport.getViewPosition();
		// p1.y = viewPosition.y;
		// resourcesViewport.setViewPosition(p1);
		// }
		// }
	}

	public int getScrollableBlockIncrement(int orientation, int direction) {
		return getScrollableUnitIncrement(orientation, direction);
	}

	public int getScrollableUnitIncrement(int orientation, int direction) {
		if (orientation == SwingUtilities.VERTICAL) {
			Rectangle visibleRect = resourcesViewport.getViewRect();
			int resourceIndex = resourceAtPoint(new Point(visibleRect.x, visibleRect.y));
			if (direction < 0) {
				resourceIndex--;
			}

			if (resourceIndex < 0) {
				return getResourcesModel().getDefaultHeight();
			} else {
				int y = 0;
				for (int i = 0; i < resourceIndex; i++) {
					y += getResourcesModel().getHeight(i);
				}
				int h = getResourcesModel().getHeight(resourceIndex);

				if (y < visibleRect.y) {
					int d = visibleRect.y - y;
					if (direction > 0) {
						return h - d;
					} else {
						return d;
					}
				} else {
					return h;
				}
			}
		} else {
			Rectangle visibleRect = datesViewport.getViewRect();
			int w = datesPanel.getDayWidth();
			int x = (int) (visibleRect.x / w) * w;
			if (x < visibleRect.x) {
				int d = visibleRect.x - x;
				if (direction > 0) {
					return w - d;
				} else {
					return d;
				}
			} else {
				return w;
			}
		}
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

			double maxW = resourcesPanel.getWidth() + internalTimelinePanel.getWidth();
			double maxH = datesPanel.getHeight() + internalTimelinePanel.getHeight();

			double imX = pageFormat.getImageableX();
			double imY = pageFormat.getImageableY();
			double imW = pageFormat.getImageableWidth();
			double imH = pageFormat.getImageableHeight();

			double ratio1 = maxW / (double) internalTimelinePanel.getWidth();
			double realImH = imW / ratio1;

			int pageMax = (int) (realImH / imH + 1);

			if (pageIndex >= pageMax) {
				return NO_SUCH_PAGE;
			}

			double scaleW = imW / maxW;
			double scaleH = realImH / maxH;
			double realDatesHeight2 = datesPanel.getHeight() * scaleH;
			double realCenterWidth2 = resourcesPanel.getWidth() * scaleW;

			double rectHeight = imH / scaleH - datesPanel.getHeight();

			double currentY = pageIndex * rectHeight;

			paint(g2d, resourcesPanel, imX, imY + realDatesHeight2, scaleW, scaleH, new Rectangle(0, (int) currentY, resourcesPanel.getWidth(), (int) rectHeight));

			paint(g2d, internalTimelinePanel, imX + realCenterWidth2, imY + realDatesHeight2, scaleW, scaleH, new Rectangle(0, (int) currentY, internalTimelinePanel.getWidth(), (int) rectHeight));

			paint(g2d, datesPanel, imX + realCenterWidth2, imY, scaleW, scaleH, new Rectangle(0, 0, datesPanel.getWidth(), datesPanel.getHeight()));

			// paint(g2d, rightComponent, imX + realLeftRightWidth2
			// + realCenterWidth2, imY, scaleW, scaleH, new Rectangle(0,
			// (int) currentY, leftRightWidth, (int) rectHeight));

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

	private final class VerticalScrollBarChangeListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			BoundedRangeModel model = (BoundedRangeModel) (e.getSource());

			Point p1 = resourcesViewport.getViewPosition();
			p1.y = model.getValue();
			resourcesViewport.setViewPosition(p1);

			Point p2 = internalTimelineViewport.getViewPosition();
			p2.y = model.getValue();
			internalTimelineViewport.setViewPosition(p2);
		}
	}

	private final class HorizontalScrollBarChangeListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			BoundedRangeModel model = (BoundedRangeModel) (e.getSource());

			Point p1 = datesViewport.getViewPosition();
			p1.x = model.getValue();
			datesViewport.setViewPosition(p1);

			Point p2 = internalTimelineViewport.getViewPosition();
			p2.x = model.getValue();
			internalTimelineViewport.setViewPosition(p2);
		}
	}

	private final class ResourcesViewportChangeListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == resourcesViewport) {
				updateVerticalScrollBar();
			}
		}
	}

	private final class DatesViewportChangeListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == datesViewport) {
				updateHorizontalScrollBar();
			}
		}
	}

	private final class CalendarPanelChangeListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			currentDate.setTime(weekCalendarPanel.getDate());
			updateAll();
			resizeAndRepaint();
			centerAtCurrentDay();

			fireDatesSimpleTimelineListener();
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

	private final class MyWheelBar extends JWheelBar {

		private static final long serialVersionUID = -8022981469727015605L;

		public MyWheelBar(int orientation) {
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

	private final class MyMouseWheelListener implements MouseWheelListener {

		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getWheelRotation() != 0) {
				int direction = e.getWheelRotation() < 0 ? -1 : 1;
				int v = verticalScrollBar.getValue();
				v += direction * verticalScrollBar.getUnitIncrement(direction);
				verticalScrollBar.setValue(v);
			}
		}
	}

	private final class CenterMouseListener extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			if (centerPopupMenu != null && e.isPopupTrigger()) {
				centerPopupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (centerPopupMenu != null && e.isPopupTrigger()) {
				centerPopupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private final class ResourcesMouseListener extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			if (resourcesPopupMenu != null && e.isPopupTrigger()) {
				resourcesPopupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (resourcesPopupMenu != null && e.isPopupTrigger()) {
				resourcesPopupMenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private final class SliderPanelChangeListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			int v = dayWidthSlider.getValue();
			datesPanel.setDayWidth(v);
			fireZoomSimpleTimelineListener();
		}
	}

	private final class MyComponentListener extends ComponentAdapter {

		public void componentShown(ComponentEvent e) {
			centerAtCurrentDay();
		}

		public void componentResized(ComponentEvent e) {
			centerAtCurrentDay();
		}
	}

	private final class PrintAction extends AbstractAction {

		private static final long serialVersionUID = -6242577318043371041L;

		public PrintAction() {
			super(null, ICON_PRINT);

			this.putValue(Action.SHORT_DESCRIPTION, SwingMessages.getString("JSimpleTimeline.5")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			print();
		}
	}

	private final class MyTransferHandler extends SynaptixTransferHandler {

		private static final long serialVersionUID = 6560212511433794019L;

		public int getSourceActions(JComponent c) {
			return COPY;
		}

		protected Transferable createTransferable(JComponent c) {
			if (timelineDrag != null) {
				return timelineDrag.createTransferable(JSimpleTimeline.this);
			}
			return super.createTransferable(c);
		}

		protected void exportDone(JComponent source, Transferable data, int action) {
			if (timelineDrag != null) {
				timelineDrag.exportDone(JSimpleTimeline.this, data);
			}
			super.exportDone(source, data, action);
		}

		public boolean canImport(TransferSupport support) {
			boolean res = false;
			if (timelineDrop != null) {
				Transferable transferable = support.getTransferable();
				if (transferable != null && timelineDrop.canDrop(transferable)) {
					if (support.isDrop()) {
						// System.out.println(support.getTransferable().);

						Point point = support.getDropLocation().getDropPoint();
						int resource = resourceAtPoint(point);
						Date date = dateAtPoint(point);

						SimpleTask[] tasks = timelineDrop.getTask(transferable, resource, date);
						if (tasks != null && tasks.length > 0) {
							setTaskDrop(resource, tasks);
							res = true;
						} else {
							clearTaskDrop();
							res = false;
						}
					}
				}
			}
			return res;
		}

		public boolean importData(TransferSupport support) {
			if (canImport(support)) {
				Transferable transferable = support.getTransferable();
				Point point = support.getDropLocation().getDropPoint();
				int resource = resourceAtPoint(point);
				Date date = dateAtPoint(point);
				timelineDrop.done(transferable, resource, date);
			}
			return false;
		}
	}
}
