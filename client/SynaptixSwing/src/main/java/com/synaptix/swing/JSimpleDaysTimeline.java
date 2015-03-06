package com.synaptix.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.TooManyListenersException;

import javax.swing.AbstractAction;
import javax.swing.BoundedRangeModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JSlider;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.event.SimpleDaysTimelineListener;
import com.synaptix.swing.event.SimpleDaysTimelineModelEvent;
import com.synaptix.swing.event.SimpleDaysTimelineModelListener;
import com.synaptix.swing.event.SimpleDaysTimelineResourcesModelEvent;
import com.synaptix.swing.event.SimpleDaysTimelineResourcesModelListener;
import com.synaptix.swing.event.SimpleDaysTimelineSelectionListener;
import com.synaptix.swing.event.SimpleDaysTimelineWeekDatesListener;
import com.synaptix.swing.plaf.basic.DayWidthSliderUI;
import com.synaptix.swing.simpledaystimeline.DefaultSimpleDaysTimelineResourcesModel;
import com.synaptix.swing.simpledaystimeline.JSimpleDaysTimelineCenter;
import com.synaptix.swing.simpledaystimeline.JSimpleDaysTimelineDayDatesHeader;
import com.synaptix.swing.simpledaystimeline.JSimpleDaysTimelineResourcesHeader;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineDayDatesHeaderRenderer;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineDrag;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineDrop;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineGroupFactory;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineResource;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineResourcesModel;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineSelectionModel;

public class JSimpleDaysTimeline extends JPanel implements
		SimpleDaysTimelineModelListener,
		SimpleDaysTimelineResourcesModelListener,
		SimpleDaysTimelineWeekDatesListener,
		SimpleDaysTimelineSelectionListener {

	private static final long serialVersionUID = -5807767341999317194L;

	private static final Log log = LogFactory.getLog(JSimpleDaysTimeline.class);

	public enum DropMode {
		NONE, OUTLINE_GHOST, FILL_GHOST, RENDERER
	};

	private static final Icon ICON_ZOOM_PLUS;

	private static final Icon ICON_ZOOM_MINUS;

	private SimpleDaysTimelineModel dataModel;

	private SimpleDaysTimelineResourcesModel resourcesModel;

	private SimpleDaysTimelineSelectionModel selectionModel;

	private JViewport resourcesViewport;

	private JSimpleDaysTimelineResourcesHeader resourcesPanel;

	private JViewport dayDatesViewport;

	private JSimpleDaysTimelineDayDatesHeader dayDatesPanel;

	private JViewport internalTimelineViewport;

	private JSimpleDaysTimelineCenter internalTimelinePanel;

	private JScrollBar verticalScrollBar;

	private JScrollBar horizontalScrollBar;

	private int nbDays;

	private JSlider dayWidthSlider;

	private JLabel minusLabel;

	private JLabel plusLabel;

	private DropMode dropMode;

	private int resourceDrop;

	private SimpleDaysTask[] tasksDrop;

	private SimpleDaysTimelineDrop timelineDrop;

	private SimpleDaysTimelineDrag timelineDrag;

	private ChangeListener sliderPanelChangeListener;

	private JPopupMenu centerPopupMenu;

	private JPopupMenu resourcesPopupMenu;

	private boolean autoHeight;

	private HorizontalScrollBarChangeListener horizontalChangeListener;

	private int dayCycle;

	private int dayStart;

	private int defaultResourceHeight;

	private JComboBox zoomComboBox;

	private JComponent upLeftComponent;

	private JPanel upLeftPanel;

	private Color dateCenterSelectionColor;

	private Color dateHeaderBackgroundPairColor;

	private Color dateHeaderBackgroundImpairColor;

	private Color dateHeaderForegroundPairColor;

	private Color dateHeaderForegroundImpairColor;

	private Color dateCenterBackgroundPairColor;

	private Color dateCenterBackgroundImpairColor;

	private Color dateCenterGridPairForegroundColor;

	private Color dateCenterGridImpairForegroundColor;

	private Color dateCenterCycleBackgoundColor;

	private Color errorIntersectionColor;

	static {
		ICON_ZOOM_PLUS = new ImageIcon(
				JSimpleDaysTimeline.class
						.getResource("/images/zoomPlusSlider.png")); //$NON-NLS-1$
		ICON_ZOOM_MINUS = new ImageIcon(
				JSimpleDaysTimeline.class
						.getResource("/images/zoomMinusSlider.png")); //$NON-NLS-1$
	}

	public JSimpleDaysTimeline(SimpleDaysTimelineModel dataModel) {
		super(new BorderLayout());

		this.setFocusable(true);

		dayStart = 0;
		dayCycle = 0;
		nbDays = 7;
		autoHeight = false;
		dropMode = DropMode.OUTLINE_GHOST;
		defaultResourceHeight = SimpleDaysTimelineResource.DEFAULT_HEIGHT;

		this.dataModel = dataModel;
		dataModel.addSimpleDaysTimelineModelListener(this);

		this.resourcesModel = new DefaultSimpleDaysTimelineResourcesModel();
		resourcesModel.addResourcesModelListener(this);

		this.selectionModel = new SimpleDaysTimelineSelectionModel();
		selectionModel.addSelectionListener(this);

		initActions();
		initComponents();

		internalTimelinePanel.precalculeMultiLine();
		updateAll();

		this.add(buildEditorPanel(), BorderLayout.CENTER);
	}

	private void initActions() {
	}

	private void createComponents() {
		resourcesViewport = new JViewport();
		resourcesPanel = new JSimpleDaysTimelineResourcesHeader(resourcesModel);

		dayDatesViewport = new JViewport();
		dayDatesPanel = new JSimpleDaysTimelineDayDatesHeader();

		internalTimelineViewport = new JViewport();
		internalTimelinePanel = new JSimpleDaysTimelineCenter();

		verticalScrollBar = new MyScrollBar(JScrollBar.VERTICAL);
		horizontalScrollBar = new MyScrollBar(JScrollBar.HORIZONTAL);

		dayWidthSlider = new JSlider(JSlider.HORIZONTAL,
				dayDatesPanel.getMinDayWidth(), dayDatesPanel.getMaxDayWidth(),
				dayDatesPanel.getDayWidth());

		minusLabel = new JLabel(ICON_ZOOM_MINUS);
		plusLabel = new JLabel(ICON_ZOOM_PLUS);

		horizontalChangeListener = new HorizontalScrollBarChangeListener();

		zoomComboBox = new JComboBox(new Integer[] { 400, 200, 100, 75, 50, 25,
				10 });

		upLeftPanel = new JPanel(new BorderLayout());
	}

	private void initComponents() {
		createComponents();

		zoomComboBox.setRenderer(new ZoomListCellRenderer());
		zoomComboBox.setSelectedItem(100);
		zoomComboBox.addActionListener(new ZoomComboBoxActionListener());

		resourcesPanel.setSimpleDaysTimeline(this);
		dayDatesPanel.setSimpleDaysTimeline(this);
		internalTimelinePanel.setSimpleDaysTimeline(this);

		resourcesPanel.addMouseListener(new ResourcesMouseListener());

		resourcesViewport.setView(resourcesPanel);
		resourcesViewport
				.addChangeListener(new ResourcesViewportChangeListener());

		dayDatesPanel.addDatesListener(this);

		dayDatesViewport.setView(dayDatesPanel);
		dayDatesViewport.addChangeListener(new DatesViewportChangeListener());

		internalTimelinePanel.setTransferHandler(new MyTransferHandler());
		internalTimelinePanel.addMouseListener(new CenterMouseListener());
		try {
			internalTimelinePanel.getDropTarget().addDropTargetListener(
					new MyDropTargetListener());
		} catch (TooManyListenersException e) {
			e.printStackTrace();
		}

		internalTimelineViewport.setView(internalTimelinePanel);

		verticalScrollBar.getModel().addChangeListener(
				new VerticalScrollBarChangeListener());
		horizontalScrollBar.getModel().addChangeListener(
				horizontalChangeListener);

		MyMouseWheelListener mwl = new MyMouseWheelListener();
		resourcesViewport.addMouseWheelListener(mwl);
		internalTimelineViewport.addMouseWheelListener(mwl);
		verticalScrollBar.addMouseWheelListener(mwl);

		sliderPanelChangeListener = new SliderPanelChangeListener();
		dayWidthSlider.addChangeListener(sliderPanelChangeListener);
		dayWidthSlider.setUI(new DayWidthSliderUI());

		minusLabel.setBorder(null);
		plusLabel.setBorder(null);

		upLeftPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

		installInputMap();
	}

	private final class MyDropTargetListener extends DropTargetAdapter {

		public void dragOver(DropTargetDragEvent dtde) {
			Point location = dtde.getLocation();
			Rectangle intRect = internalTimelineViewport.getViewRect();
			int dy1 = location.y - intRect.y;
			int dy2 = (intRect.y + intRect.height - 1) - location.y;

			int dx1 = location.x - intRect.x;
			int dx2 = (intRect.x + intRect.width - 1) - location.x;

			int x = intRect.x;
			int y = intRect.y;
			int w = intRect.width;
			int h = intRect.height;
			if (dx1 >= 0 && dx1 < 10) {
				x = intRect.x - 25;
				w = 25;
			} else if (dx2 >= 0 && dx2 < 10) {
				x = (intRect.x + intRect.width - 1);
				w = 25;
			}
			if (dy1 >= 0 && dy1 < 10) {
				y = intRect.y - 25;
				h = 25;
			} else if (dy2 >= 0 && dy2 < 10) {
				y = (intRect.y + intRect.height - 1);
				h = 25;
			}

			internalTimelinePanel
					.scrollRectToVisible(new Rectangle(x, y, w, h));

			Rectangle resRect = resourcesViewport.getViewRect();
			resourcesPanel.scrollRectToVisible(new Rectangle(resRect.x, y,
					resRect.width, h));

			Rectangle heaRect = dayDatesViewport.getViewRect();
			dayDatesPanel.scrollRectToVisible(new Rectangle(x, heaRect.y, w,
					heaRect.height));
		}

		public void dragExit(DropTargetEvent dte) {
			clearTaskDrop();
		}

		public void drop(DropTargetDropEvent arg0) {
			clearTaskDrop();
		}
	}

	private void installInputMap() {
		this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0),
				"doZoomPlus"); //$NON-NLS-1$
		this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0),
				"doZoomPlus"); //$NON-NLS-1$
		this.getActionMap().put("doZoomPlus", new ZoomPlusAction()); //$NON-NLS-1$

		this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0),
				"doZoomMinus"); //$NON-NLS-1$
		this.getInputMap()
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0),
						"doZoomMinus"); //$NON-NLS-1$
		this.getActionMap().put("doZoomMinus", new ZoomMinusAction()); //$NON-NLS-1$

		// Arrows
		this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
				"doUp"); //$NON-NLS-1$
		this.getActionMap().put("doUp", new UpAction()); //$NON-NLS-1$

		this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
				"doDown"); //$NON-NLS-1$
		this.getActionMap().put("doDown", new DownAction()); //$NON-NLS-1$

		this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
				"doLeft"); //$NON-NLS-1$
		this.getActionMap().put("doLeft", new LeftAction()); //$NON-NLS-1$

		this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
				"doRight"); //$NON-NLS-1$
		this.getActionMap().put("doRight", new RightAction()); //$NON-NLS-1$
	}

	private JLabel createLabelWithBorder() {
		JLabel l = new JLabel();
		l.setOpaque(true);
		l.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		return l;
	}

	private JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout(
				"FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:300DLU:GROW(1.0),FILL:DEFAULT:NONE", //$NON-NLS-1$
				"FILL:DEFAULT:NONE,FILL:50DLU:GROW(1.0),FILL:DEFAULT:NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(resourcesViewport, cc.xyw(1, 2, 4));
		builder.add(dayDatesViewport, cc.xy(5, 1));
		builder.add(internalTimelineViewport, cc.xy(5, 2));
		builder.add(verticalScrollBar, cc.xy(6, 2));
		builder.add(upLeftPanel, cc.xyw(1, 1, 4));
		builder.add(horizontalScrollBar, cc.xy(5, 3));
		builder.add(dayWidthSlider, cc.xy(2, 3));
		builder.add(minusLabel, cc.xy(1, 3));
		builder.add(plusLabel, cc.xy(3, 3));
		builder.add(zoomComboBox, cc.xy(4, 3));
		builder.add(createLabelWithBorder(), cc.xy(6, 1));
		return builder.getPanel();
	}

	public void addSimpleDaysTimelineListener(SimpleDaysTimelineListener l) {
		listenerList.add(SimpleDaysTimelineListener.class, l);
	}

	public void removeSimpleDaysTimelineListener(SimpleDaysTimelineListener l) {
		listenerList.remove(SimpleDaysTimelineListener.class, l);
	}

	private void fireZoomChanged(int dayWidth) {
		SimpleDaysTimelineListener[] listeners = listenerList
				.getListeners(SimpleDaysTimelineListener.class);
		for (SimpleDaysTimelineListener l : listeners) {
			l.zoomChanged(dayWidth);
		}
	}

	private void fireHorizontalScrollBarChanged(int value) {
		SimpleDaysTimelineListener[] listeners = listenerList
				.getListeners(SimpleDaysTimelineListener.class);
		for (SimpleDaysTimelineListener l : listeners) {
			l.horizontalScrollBarChanged(value);
		}
	}

	private void fireVerticalScrollBarChanged(int value) {
		SimpleDaysTimelineListener[] listeners = listenerList
				.getListeners(SimpleDaysTimelineListener.class);
		for (SimpleDaysTimelineListener l : listeners) {
			l.verticalScrollBarChanged(value);
		}
	}

	private void updateAll() {
		updateResourcesFromModel();
		updateVerticalScrollBar();
		updateHorizontalScrollBar();
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
		Dimension extentSize = dayDatesViewport.getExtentSize();
		Dimension viewSize = dayDatesViewport.getViewSize();
		Point viewPosition = dayDatesViewport.getViewPosition();

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
			int height = defaultResourceHeight;
			if (autoHeight) {
				int max = internalTimelinePanel.getNbMaxTotal(i);
				if (max > 1) {
					height = defaultResourceHeight * max;
				}
			}

			SimpleDaysTimelineResource newResource = new SimpleDaysTimelineResource(
					i, height);
			int modelColumn = newResource.getModelIndex();
			String columnName = dataModel.getResourceName(modelColumn);
			newResource.setHeaderValue(columnName);
			resourcesModel.addResource(newResource);
		}
	}

	/**
	 * Composant en haut à gauche
	 * 
	 * @param upLeftComponent
	 */
	public void setUpLeftComponent(JComponent upLeftComponent) {
		this.upLeftComponent = upLeftComponent;

		upLeftPanel.removeAll();
		upLeftPanel.add(upLeftComponent, BorderLayout.CENTER);

		upLeftPanel.revalidate();
		upLeftPanel.repaint();
	}

	public JComponent getUpLeftComponent() {
		return upLeftComponent;
	}

	/**
	 * Permet de faire que l'entete des dates suive le scroll A chaque fois
	 * qu'on scroll on redessinne completement l'entete
	 * 
	 * @param enabled
	 */
	public void setDynamicDayDatesHeader(boolean enabled) {
		dayDatesViewport.setScrollMode(enabled ? JViewport.SIMPLE_SCROLL_MODE
				: JViewport.BLIT_SCROLL_MODE);
	}

	/**
	 * Entete des dates dynamique
	 * 
	 * @return
	 */
	public boolean isDynamicDayDatesHeader() {
		return dayDatesViewport.getScrollMode() == JViewport.SIMPLE_SCROLL_MODE;
	}

	/**
	 * Couleur du haut du planning impair
	 * 
	 * @return
	 */
	public void setDateHeaderBackgroundImpairColor(
			Color dateHeaderBackgroundImpairColor) {
		this.dateHeaderBackgroundImpairColor = dateHeaderBackgroundImpairColor;
	}

	/**
	 * Couleur du haut du planning impair
	 * 
	 * @return
	 */
	public Color getDateHeaderBackgroundImpairColor() {
		return dateHeaderBackgroundImpairColor;
	}

	/**
	 * Couleur du haut du planning pair
	 * 
	 * @return
	 */
	public void setDateHeaderBackgroundPairColor(
			Color dateHeaderBackgroundPairColor) {
		this.dateHeaderBackgroundPairColor = dateHeaderBackgroundPairColor;
	}

	/**
	 * Couleur du haut du planning pair
	 * 
	 * @return
	 */
	public Color getDateHeaderBackgroundPairColor() {
		return dateHeaderBackgroundPairColor;
	}

	/**
	 * Couleur du texte pair de l'entete du planning
	 * 
	 * @param dateHeaderForegroundPairColor
	 */
	public void setDateHeaderForegroundPairColor(
			Color dateHeaderForegroundPairColor) {
		this.dateHeaderForegroundPairColor = dateHeaderForegroundPairColor;
	}

	/**
	 * Couleur du texte pair de l'entete du planning
	 * 
	 * @return
	 */
	public Color getDateHeaderForegroundPairColor() {
		return dateHeaderForegroundPairColor;
	}

	/**
	 * Couleur du texte impair de l'entete du planning
	 * 
	 * @param dateHeaderForegroundImpairColor
	 */
	public void setDateHeaderForegroundImpairColor(
			Color dateHeaderForegroundImpairColor) {
		this.dateHeaderForegroundImpairColor = dateHeaderForegroundImpairColor;
	}

	/**
	 * Couleur du texte impair de l'entete du planning
	 * 
	 * @return
	 */
	public Color getDateHeaderForegroundImpairColor() {
		return dateHeaderForegroundImpairColor;
	}

	/**
	 * Couleur de la sélection des jours sur le centre du planning
	 * 
	 * @return
	 */
	public void setDateCenterSelectionColor(Color dateCenterSelectionColor) {
		this.dateCenterSelectionColor = dateCenterSelectionColor;
	}

	/**
	 * Couleur de la sélection des jours sur le centre du planning
	 * 
	 * @return
	 */
	public Color getDateCenterSelectionColor() {
		return dateCenterSelectionColor;
	}

	/**
	 * Couleur du fond pair au centre du planning
	 * 
	 * @param dateCenterBackgroundPairColor
	 */
	public void setDateCenterBackgroundPairColor(
			Color dateCenterBackgroundPairColor) {
		this.dateCenterBackgroundPairColor = dateCenterBackgroundPairColor;
	}

	/**
	 * Couleur du fond pair au centre du planning
	 * 
	 * @param dateCenterBackgroundPairColor
	 */
	public Color getDateCenterBackgroundPairColor() {
		return dateCenterBackgroundPairColor;
	}

	/**
	 * Couleur du fond impair au centre du planning
	 * 
	 * @param dateCenterBackgroundImpairColor
	 */
	public void setDateCenterBackgroundImpairColor(
			Color dateCenterBackgroundImpairColor) {
		this.dateCenterBackgroundImpairColor = dateCenterBackgroundImpairColor;
	}

	/**
	 * Couleur du fond impair au centre du planning
	 * 
	 * @param dateCenterBackgroundImpairColor
	 */
	public Color getDateCenterBackgroundImpairColor() {
		return dateCenterBackgroundImpairColor;
	}

	/**
	 * Couleur de la grille pair au centre du planning
	 * 
	 * @param dateCenterGridPairForegroundColor
	 */
	public void setDateCenterGridPairForegroundColor(
			Color dateCenterGridPairForegroundColor) {
		this.dateCenterGridPairForegroundColor = dateCenterGridPairForegroundColor;
	}

	/**
	 * Couleur de la grille pair au centre du planning
	 * 
	 * @param dateCenterGridPairForegroundColor
	 */
	public Color getDateCenterGridPairForegroundColor() {
		return dateCenterGridPairForegroundColor;
	}

	/**
	 * Couleur de la grille impair au centre du planning
	 * 
	 * @param dateCenterGridImpairForegroundColor
	 */
	public Color getDateCenterGridImpairForegroundColor() {
		return dateCenterGridImpairForegroundColor;
	}

	/**
	 * Couleur de la grille impair au centre du planning
	 * 
	 * @param dateCenterGridImpairForegroundColor
	 */
	public void setDateCenterGridImpairForegroundColor(
			Color dateCenterGridImpairForegroundColor) {
		this.dateCenterGridImpairForegroundColor = dateCenterGridImpairForegroundColor;
	}

	/**
	 * Couleur de la barre de cycle au centre du planning
	 * 
	 * @param dateCenterCycleBackgoundColor
	 */
	public Color getDateCenterCycleBackgoundColor() {
		return dateCenterCycleBackgoundColor;
	}

	/**
	 * Couleur de la barre de cycle au centre du planning
	 * 
	 * @param dateCenterCycleBackgoundColor
	 */
	public void setDateCenterCycleBackgoundColor(
			Color dateCenterCycleBackgoundColor) {
		this.dateCenterCycleBackgoundColor = dateCenterCycleBackgoundColor;
	}

	/**
	 * Couleur de la barre d'erreur quand 2 taches s'entrecoupe
	 * 
	 * @param errorIntersectionColor
	 */
	public Color getErrorIntersectionColor() {
		return errorIntersectionColor;
	}

	/**
	 * Couleur de la barre d'erreur quand 2 taches s'entrecoupe
	 * 
	 * @param errorIntersectionColor
	 */
	public void setErrorIntersectionColor(Color errorIntersectionColor) {
		this.errorIntersectionColor = errorIntersectionColor;
	}

	public void setDefaultResourceHeight(int defaultResourceHeight) {
		this.defaultResourceHeight = defaultResourceHeight;
		updateAll();
	}

	public int getDefaultResourceHeight() {
		return defaultResourceHeight;
	}

	public void setShowIntersection(boolean showIntersection) {
		internalTimelinePanel.setShowIntersection(showIntersection);
	}

	public boolean isShowIntersection() {
		return internalTimelinePanel.isShowIntersection();
	}

	public void setShowGroupIntersection(boolean showGroupIntersection) {
		internalTimelinePanel.setShowGroupIntersection(showGroupIntersection);
	}

	public boolean isShowGroupIntersection() {
		return internalTimelinePanel.isShowGroupIntersection();
	}

	public void setMultiLine(boolean multiLine) {
		internalTimelinePanel.setMultiLine(multiLine);

		updateAll();
		resizeAndRepaint();
	}

	public boolean isMultiLine() {
		return internalTimelinePanel.isMultiLine();
	}

	public void setGroupFactory(SimpleDaysTimelineGroupFactory groupFactory) {
		internalTimelinePanel.setGroupFactory(groupFactory);

		updateAll();
		resizeAndRepaint();
	}

	public SimpleDaysTimelineGroupFactory getGroupFactory() {
		return internalTimelinePanel.getGroupFactory();
	}

	public void setAutoHeight(boolean autoHeight) {
		this.autoHeight = autoHeight;

		updateAll();
		resizeAndRepaint();
	}

	public boolean isAutoHeight() {
		return autoHeight;
	}

	public int getNbDays() {
		return nbDays;
	}

	public void setNbDays(int nbDays) {
		this.nbDays = nbDays;

		internalTimelinePanel.precalculeMultiLine();
		updateAll();
		resizeAndRepaint();
	}

	public int getDayStart() {
		return dayStart;
	}

	public void setDayStart(int dayStart) {
		this.dayStart = dayStart;
		updateAll();
		internalTimelinePanel.precalculeMultiLine();
		resizeAndRepaint();
	}

	public void setDayCycle(int dayCycle) {
		this.dayCycle = dayCycle;
		updateAll();
		internalTimelinePanel.precalculeMultiLine();
		resizeAndRepaint();
	}

	public int getDayCycle() {
		return dayCycle;
	}

	public void setDropMode(DropMode dropMode) {
		this.dropMode = dropMode;
	}

	public DropMode getDropMode() {
		return dropMode;
	}

	public void setTaskDrop(int resource, SimpleDaysTask... tasks) {
		this.resourceDrop = resource;
		this.tasksDrop = tasks;

		internalTimelinePanel.repaint();
	}

	public void clearTaskDrop() {
		this.resourceDrop = -1;
		this.tasksDrop = null;

		internalTimelinePanel.repaint();
	}

	public int getResourceDrop() {
		return resourceDrop;
	}

	public SimpleDaysTask[] getTaskDrop() {
		return tasksDrop;
	}

	public void setSimpleDaysTimelineDrop(SimpleDaysTimelineDrop timelineDrop) {
		this.timelineDrop = timelineDrop;
	}

	public SimpleDaysTimelineDrop getSimpleDaysTimelineDrop() {
		return timelineDrop;
	}

	public void setSimpleDaysTimelineDrag(SimpleDaysTimelineDrag timelineDrag) {
		this.timelineDrag = timelineDrag;
	}

	public SimpleDaysTimelineDrag getSimpleDaysTimelineDrag() {
		return timelineDrag;
	}

	public JSimpleDaysTimelineCenter getSimpleDaysTimelineCenter() {
		return internalTimelinePanel;
	}

	public SimpleDaysTimelineModel getModel() {
		return dataModel;
	}

	public SimpleDaysTimelineSelectionModel getSelectionModel() {
		return selectionModel;
	}

	public SimpleDaysTimelineResourcesModel getResourcesModel() {
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
		return getResourcesModel().getResource(viewResourceIndex)
				.getModelIndex();
	}

	public int convertResourceIndexToView(int modelResourceIndex) {
		if (modelResourceIndex < 0) {
			return modelResourceIndex;
		}
		SimpleDaysTimelineResourcesModel rm = getResourcesModel();
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

	public Rectangle getResourceRect(int resource) {
		Rectangle r = new Rectangle();
		SimpleDaysTimelineResourcesModel cm = getResourcesModel();

		r.width = resourcesPanel.getWidth();

		if (resource < 0) {
		} else if (resource >= cm.getResourceCount()) {
		} else {
			for (int i = 0; i < resource; i++) {
				r.y += cm.getResource(i).getHeight();
			}

			r.height = cm.getResource(resource).getHeight();
		}
		return r;
	}

	public Rectangle getInternalViewRect() {
		return internalTimelineViewport.getViewRect();
	}

	public Rectangle getSimpleDayTaskRect(int resource, SimpleDaysTask task) {
		Rectangle r = getResourceRect(resource);

		int x1 = pointAtDayDate(task.getDayDateMin());
		int x2 = pointAtDayDate(task.getDayDateMax());

		r.x = x1;
		r.width = x2 - x1;

		return r;
	}

	private SimpleDaysTimelineResource getResizingResource() {
		return (resourcesPanel == null) ? null : resourcesPanel
				.getResizingResource();
	}

	public JComboBox getDayWidthComboBox() {
		return zoomComboBox;
	}

	public JSlider getDayWidthSlider() {
		return dayWidthSlider;
	}

	public JSimpleDaysTimelineResourcesHeader getResourcesHeader() {
		return resourcesPanel;
	}

	public void setHeaderRenderer(
			SimpleDaysTimelineDayDatesHeaderRenderer headerRenderer) {
		dayDatesPanel.setHeaderRenderer(headerRenderer);
	}

	public SimpleDaysTimelineDayDatesHeaderRenderer getHeaderRenderer() {
		return dayDatesPanel.getHeaderRenderer();
	}

	public int pointAtDayDate(DayDate dayDate) {
		long m = dayDate.getTimeInMinutes() * getDayWidth();
		long a = m / (60L * 24L);
		return (int) a;
	}

	public DayDate dayDateAtPoint(Point point) {
		int x = point.x;

		long minutes = (x * 60L * 24L) / getDayWidth();

		DayDate c = new DayDate();
		c.setTimeInMinutes(minutes);
		return c;
	}

	public JSimpleDaysTimelineDayDatesHeader getDatesHeader() {
		return dayDatesPanel;
	}

	public void setMinDayWidth(int minDayWidth) {
		dayDatesPanel.setMinDayWidth(minDayWidth);
	}

	public void setMaxDayWidth(int maxDayWidth) {
		dayDatesPanel.setMaxDayWidth(maxDayWidth);
	}

	public int getMinDayWidth() {
		return dayDatesPanel.getMinDayWidth();
	}

	public int getMaxDayWidth() {
		return dayDatesPanel.getMaxDayWidth();
	}

	public int getDayWidth() {
		return dayDatesPanel.getDayWidth();
	}

	public void setDayWidth(int dayWidth) {
		dayDatesPanel.setDayWidth(dayWidth);
	}

	private void zoomTimeline(int dz) {
		double dw = JSimpleDaysTimelineDayDatesHeader.DEFAULT_DAY_WIDTH;
		double sc = (double) getDayWidth() / dw;

		int w = getDayWidth() + (int) (sc * dz);
		dayWidthSlider.setValue(w);
	}

	public int getHorizontalScrollBarValue() {
		return horizontalScrollBar.getValue();
	}

	public void setHorizontalScrollBarValue(int value) {
		horizontalScrollBar.setValue(value);
	}

	public int getVerticalScrollBarValue() {
		return verticalScrollBar.getValue();
	}

	public void setVerticalScrollBarValue(int value) {
		verticalScrollBar.setValue(value);
	}

	private int getDeltaMoveSelectionX(int dx) {
		double dw = JSimpleDaysTimelineDayDatesHeader.DEFAULT_DAY_WIDTH;
		double sc = dw / (double) getDayWidth();
		int x = (int) (sc * dx * getDayWidth());
		return x;
	}

	/**
	 * Calcule le nombre de pixel à déplacer selon le zoom
	 * 
	 * @param dx
	 * @return
	 */
	private int getMoveSelectionX(int dx) {
		int x = horizontalScrollBar.getValue() + getDeltaMoveSelectionX(dx);
		return x;
	}

	/**
	 * Calcule le nombre de pixel à deplacer en y
	 * 
	 * @param dy
	 * @return
	 */
	private int getMoveSelectionY(int dy) {
		int y = verticalScrollBar.getValue() + dy * defaultResourceHeight;
		return y;
	}

	/**
	 * Permet de deplacer le planning selon le zoom
	 * 
	 * @param dx
	 * @param dy
	 */
	private void moveSelection(int dx, int dy) {
		int y = getMoveSelectionY(dy);
		int x = getMoveSelectionX(dx);

		verticalScrollBar.setValue(y);
		horizontalScrollBar.setValue(x);
	}

	protected void resizeAndRepaint() {
		if (resourcesPanel != null) {
			resourcesPanel.resizeAndRepaint();
		}
		if (dayDatesPanel != null) {
			dayDatesPanel.resizeAndRepaint();
		}
		internalTimelinePanel.resizeAndRepaint();
	}

	public void simpleDaysTimelineChanged(SimpleDaysTimelineModelEvent e) {
		// if (e != null
		// && e.getType() == SimpleDaysTimelineModelEvent.Type.DATA_CHANGED) {
		//
		// }

		internalTimelinePanel.precalculeMultiLine();
		updateAll();
		resizeAndRepaint();
	}

	public void valueChanged() {
		if (resourcesPanel != null) {
			resourcesPanel.repaint();
		}
		internalTimelinePanel.repaint();
	}

	public void datesDayWidthChanged(int oldValue, int newValue) {
		// Pas besoin ?
		// internalTimelinePanel.precalculeMultiLine();

		dayWidthSlider.removeChangeListener(sliderPanelChangeListener);
		dayWidthSlider.setMaximum(dayDatesPanel.getMaxDayWidth());
		dayWidthSlider.setMinimum(dayDatesPanel.getMinDayWidth());
		dayWidthSlider.setValue(getDayWidth());
		dayWidthSlider.addChangeListener(sliderPanelChangeListener);

		double va = horizontalScrollBar.getVisibleAmount() / 2;
		double value = horizontalScrollBar.getValue() + va;
		double x = value / oldValue;

		dayDatesPanel.resizeAndRepaint();

		internalTimelinePanel.revalidate();
		internalTimelinePanel.repaint();

		int w = (int) (x * newValue - va);

		horizontalChangeListener.setStopListener(true);
		horizontalScrollBar.setValue(w);
		horizontalChangeListener.setStopListener(false);
	}

	public void resourceAdded(SimpleDaysTimelineResourcesModelEvent e) {
		resizeAndRepaint();
	}

	public void resourceMarginChanged(ChangeEvent e) {
		SimpleDaysTimelineResource resizingRessource = getResizingResource();
		if (resizingRessource != null) {
			resizingRessource.setPreferredHeight(resizingRessource.getHeight());
		}
		resizeAndRepaint();
	}

	public void resourceMoved(SimpleDaysTimelineResourcesModelEvent e) {
		internalTimelinePanel.repaint();
	}

	public void resourceRemoved(SimpleDaysTimelineResourcesModelEvent e) {
		resizeAndRepaint();
	}

	public void resourceSelectionChanged(ListSelectionEvent e) {
	}

	public void scrollRectToVisible(Rectangle contentRect) {
		internalTimelinePanel.scrollRectToVisible(contentRect);

		Rectangle resRect = resourcesViewport.getViewRect();
		resourcesPanel.scrollRectToVisible(new Rectangle(resRect.x,
				contentRect.y, resRect.width, contentRect.height));

		Rectangle heaRect = dayDatesViewport.getViewRect();
		dayDatesPanel.scrollRectToVisible(new Rectangle(contentRect.x,
				heaRect.y, contentRect.width, heaRect.height));
		// log.debug("Aller à : " + contentRect);
		// log.debug("Actuellement : " +
		// internalTimelineViewport.getViewRect());
		//
		// Point viewPosition = internalTimelineViewport.getViewPosition();
		// Dimension extent = internalTimelineViewport.getExtentSize();
		//
		// if (extent.getWidth() != 0 && extent.getHeight() != 0) {
		// int dx = positionAdjustment(extent.width, contentRect.width,
		// contentRect.x - viewPosition.x);
		// int dy = positionAdjustment(extent.height, contentRect.height,
		// contentRect.y - viewPosition.y);
		//
		// if (dx != 0 || dy != 0) {
		// int startX = viewPosition.x;
		// int startY = viewPosition.y;
		//
		// viewPosition.x -= dx;
		// viewPosition.y -= dy;
		//
		// log.debug("Deplacement à : " + viewPosition);
		//
		// if (viewPosition.x != startX || viewPosition.y != startY) {
		// resourcesViewport.setViewPosition(new Point(0,
		// viewPosition.y));
		// dayDatesViewport.setViewPosition(new Point(viewPosition.x,
		// 0));
		// }
		// }
		// }
	}

	private int positionAdjustment(int parentWidth, int childWidth, int childAt) {
		// +-----+
		// | --- | No Change
		// +-----+
		if (childAt >= 0 && childWidth + childAt <= parentWidth) {
			return 0;
		}

		// +-----+
		// --------- No Change
		// +-----+
		if (childAt <= 0 && childWidth + childAt >= parentWidth) {
			return 0;
		}

		// +-----+ +-----+
		// | ---- -> | ----|
		// +-----+ +-----+
		if (childAt > 0 && childWidth <= parentWidth) {
			return -childAt + parentWidth - childWidth;
		}

		// +-----+ +-----+
		// | -------- -> |--------
		// +-----+ +-----+
		if (childAt >= 0 && childWidth >= parentWidth) {
			return -childAt;
		}

		// +-----+ +-----+
		// ---- | -> |---- |
		// +-----+ +-----+
		if (childAt <= 0 && childWidth <= parentWidth) {
			return -childAt;
		}

		// +-----+ +-----+
		// -------- | -> --------|
		// +-----+ +-----+
		if (childAt < 0 && childWidth >= parentWidth) {
			return -childAt + parentWidth - childWidth;
		}

		return 0;
	}

	public int getScrollableBlockIncrement(int orientation, int direction) {
		return getScrollableUnitIncrement(orientation, direction);
	}

	public int getScrollableUnitIncrement(int orientation, int direction) {
		if (orientation == SwingUtilities.VERTICAL) {
			Rectangle visibleRect = resourcesViewport.getViewRect();
			int resourceIndex = resourceAtPoint(new Point(visibleRect.x,
					visibleRect.y));
			if (direction < 0) {
				resourceIndex--;
			}

			if (resourceIndex < 0) {
				return getDefaultResourceHeight();
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
			return Math.abs(getDeltaMoveSelectionX(direction));
		}
	}

	private final class VerticalScrollBarChangeListener implements
			ChangeListener {

		public void stateChanged(ChangeEvent e) {
			BoundedRangeModel model = (BoundedRangeModel) (e.getSource());

			Point p1 = resourcesViewport.getViewPosition();
			p1.y = model.getValue();
			resourcesViewport.setViewPosition(p1);

			Point p2 = internalTimelineViewport.getViewPosition();
			p2.y = model.getValue();
			internalTimelineViewport.setViewPosition(p2);

			fireVerticalScrollBarChanged(model.getValue());
		}
	}

	private final class HorizontalScrollBarChangeListener implements
			ChangeListener {

		private boolean stopListener;

		public HorizontalScrollBarChangeListener() {
			stopListener = false;
		}

		public void setStopListener(boolean stopListener) {
			this.stopListener = stopListener;
		}

		public void stateChanged(ChangeEvent e) {
			BoundedRangeModel model = (BoundedRangeModel) (e.getSource());

			Point p1 = dayDatesViewport.getViewPosition();
			p1.x = model.getValue();
			dayDatesViewport.setViewPosition(p1);

			Point p2 = internalTimelineViewport.getViewPosition();
			p2.x = model.getValue();
			internalTimelineViewport.setViewPosition(p2);

			if (!stopListener) {
				fireHorizontalScrollBarChanged(model.getValue());
			}
		}
	}

	private final class ResourcesViewportChangeListener implements
			ChangeListener {

		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == resourcesViewport) {
				updateVerticalScrollBar();
			}
		}
	}

	private final class DatesViewportChangeListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == dayDatesViewport) {
				updateHorizontalScrollBar();
			}
		}
	}

	private final class MyScrollBar extends JScrollBar {

		private static final long serialVersionUID = -8022981469727015605L;

		public MyScrollBar(int orientation) {
			super(orientation);
		}

		public int getUnitIncrement(int direction) {
			return getScrollableUnitIncrement(getOrientation(), direction);
		}

		public int getBlockIncrement(int direction) {
			return getScrollableBlockIncrement(getOrientation(), direction);
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

	private final class MyMouseWheelListener implements MouseWheelListener {

		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getWheelRotation() != 0) {
				if (e.isShiftDown()) {
					int direction = e.getWheelRotation() < 0 ? -1 : 1;
					if (e.getSource().equals(internalTimelineViewport)) {
						// int i = horizontalScrollBar.getValue();
						// Rectangle viewRect =
						// internalTimelineViewport.getViewRect();
						// int dx = e.getPoint().x - (viewRect.x +
						// viewRect.width / 2);
						//
						// DayDate dayDate = dayDateAtPoint(e.getPoint());

						zoomTimeline(direction * 20);
						//
						// int x = pointAtDayDate(dayDate);
						// int j = e.getPoint().x - x;
						//
						// horizontalScrollBar.setValue(i+j*2);
					} else {
						zoomTimeline(direction * 20);
					}
				} else {
					int direction = e.getWheelRotation() < 0 ? -1 : 1;
					moveSelection(0, direction);
				}
			}
		}
	}

	private final class SliderPanelChangeListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			int v = dayWidthSlider.getValue();
			dayDatesPanel.setDayWidth(v);
			fireZoomChanged(v);
		}
	}

	private final class ZoomComboBoxActionListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			int z = (Integer) zoomComboBox.getSelectedItem();
			int max = dayDatesPanel.getMaxDayWidth();
			int min = dayDatesPanel.getMinDayWidth();

			int v = min + (int) (((max - min) / 4.0) * (z / 100.0));
			dayDatesPanel.setDayWidth(v);
			fireZoomChanged(v);
		}
	}

	private final class MyTransferHandler extends TransferHandler {

		private static final long serialVersionUID = 6560212511433794019L;

		public int getSourceActions(JComponent c) {
			return COPY;
		}

		protected Transferable createTransferable(JComponent c) {
			if (timelineDrag != null) {
				return timelineDrag
						.createTransferable(JSimpleDaysTimeline.this);
			}
			return super.createTransferable(c);
		}

		protected void exportDone(JComponent source, Transferable data,
				int action) {
			if (timelineDrag != null) {
				timelineDrag.exportDone(JSimpleDaysTimeline.this, data);
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
						DayDate dayDate = dayDateAtPoint(point);

						SimpleDaysTask[] tasks = timelineDrop.getTask(
								transferable, resource, dayDate);
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
				DayDate dayDate = dayDateAtPoint(point);
				timelineDrop.done(transferable, resource, dayDate);
			}
			return false;
		}
	}

	private final class ZoomPlusAction extends AbstractAction {

		private static final long serialVersionUID = -3479117302708049061L;

		public void actionPerformed(ActionEvent e) {
			zoomTimeline(20);
		}
	}

	private final class ZoomMinusAction extends AbstractAction {

		private static final long serialVersionUID = 8910832951878077920L;

		public void actionPerformed(ActionEvent e) {
			zoomTimeline(-20);
		}
	}

	private final class UpAction extends AbstractAction {

		private static final long serialVersionUID = 2749748473590495760L;

		public void actionPerformed(ActionEvent e) {
			moveSelection(0, -1);
		}
	}

	private final class DownAction extends AbstractAction {

		private static final long serialVersionUID = 3715444693035726684L;

		public void actionPerformed(ActionEvent e) {
			moveSelection(0, 1);
		}
	}

	private final class LeftAction extends AbstractAction {

		private static final long serialVersionUID = 2198007363112869077L;

		public void actionPerformed(ActionEvent e) {
			moveSelection(-1, 0);
		}
	}

	private final class RightAction extends AbstractAction {

		private static final long serialVersionUID = 4831883271975396288L;

		public void actionPerformed(ActionEvent e) {
			moveSelection(1, 0);
		}
	}

	private final class ZoomListCellRenderer extends JLabel implements
			ListCellRenderer {

		private static final long serialVersionUID = 5803028326667728855L;

		public ZoomListCellRenderer() {
			super("", JLabel.LEFT);

			this.setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			if (value != null && value instanceof Integer) {
				int i = (Integer) value;
				this.setText(new StringBuilder().append(i).append(" %")
						.toString());
			}
			return this;
		}
	}

}
