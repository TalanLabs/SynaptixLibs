package com.synaptix.swing.roulement;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.AbstractAction;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.DayDate;
import com.synaptix.swing.roulement.event.RoulementTimelineListener;
import com.synaptix.swing.roulement.event.RoulementTimelineModelEvent;
import com.synaptix.swing.roulement.event.RoulementTimelineModelListener;
import com.synaptix.swing.roulement.event.RoulementTimelineResourcesModelEvent;
import com.synaptix.swing.roulement.event.RoulementTimelineResourcesModelListener;
import com.synaptix.swing.roulement.event.RoulementTimelineSelectionListener;
import com.synaptix.swing.roulement.event.RoulementTimelineWeekDatesListener;
import com.synaptix.swing.utils.DragDropComponent;
import com.synaptix.swing.utils.SynaptixTransferHandler;

public class JRoulementTimeline extends JPanel implements
		RoulementTimelineModelListener,
		RoulementTimelineResourcesModelListener,
		RoulementTimelineWeekDatesListener, RoulementTimelineSelectionListener,
		DragDropComponent {

	private static final long serialVersionUID = -5807767341999317194L;

	private static final Log log = LogFactory.getLog(JRoulementTimeline.class);

	public enum DropMode {
		NONE, OUTLINE_GHOST, FILL_GHOST, RENDERER
	};

	private RoulementTimelineModel dataModel;

	private RoulementTimelineResourcesModel resourcesModel;

	private RoulementTimelineSelectionModel selectionModel;

	private JViewport leftResourcesViewport;

	private JRoulementTimelineLeftResourcesHeader leftResourcesPanel;

	private JViewport rightResourcesViewport;

	private JRoulementTimelineRightResourcesHeader rightResourcesPanel;

	private JViewport dayDatesViewport;

	private JRoulementTimelineDayDatesHeader dayDatesPanel;

	private JViewport internalTimelineViewport;

	private JRoulementTimelineCenter internalTimelinePanel;

	private JScrollBar verticalScrollBar;

	private JScrollBar horizontalScrollBar;

	private int nbDays;

	private DropMode dropMode;

	private int resourceDrop;

	private RoulementTask[] tasksDrop;

	private RoulementTimelineDrop timelineDrop;

	private RoulementTimelineDrag timelineDrag;

	private JPopupMenu centerPopupMenu;

	private JPopupMenu leftResourcesPopupMenu;

	private JPopupMenu rightResourcesPopupMenu;

	private boolean autoHeight;

	private HorizontalScrollBarChangeListener horizontalChangeListener;

	private int dayCycle;

	private int dayStart;

	private int defaultResourceHeight;

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

	public JRoulementTimeline(RoulementTimelineModel dataModel) {
		super(new BorderLayout());

		this.setFocusable(true);

		dayStart = 0;
		dayCycle = 0;
		nbDays = 7;
		autoHeight = false;
		dropMode = DropMode.OUTLINE_GHOST;
		defaultResourceHeight = RoulementTimelineResource.DEFAULT_HEIGHT;

		this.dataModel = dataModel;
		dataModel.addRoulementTimelineModelListener(this);

		this.resourcesModel = new DefaultRoulementTimelineResourcesModel();
		resourcesModel.addResourcesModelListener(this);

		this.selectionModel = new RoulementTimelineSelectionModel();
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
		leftResourcesViewport = new JViewport();
		leftResourcesPanel = new JRoulementTimelineLeftResourcesHeader(
				resourcesModel);
		rightResourcesViewport = new JViewport();
		rightResourcesPanel = new JRoulementTimelineRightResourcesHeader(
				resourcesModel);

		dayDatesViewport = new JViewport();
		dayDatesPanel = new JRoulementTimelineDayDatesHeader();

		internalTimelineViewport = new JViewport();
		internalTimelinePanel = new JRoulementTimelineCenter();

		verticalScrollBar = new MyScrollBar(JScrollBar.VERTICAL);
		horizontalScrollBar = new MyScrollBar(JScrollBar.HORIZONTAL);

		horizontalChangeListener = new HorizontalScrollBarChangeListener();
	}

	private void initComponents() {
		createComponents();

		leftResourcesPanel.setRoulementTimeline(this);
		rightResourcesPanel.setRoulementTimeline(this);
		dayDatesPanel.setRoulementTimeline(this);
		internalTimelinePanel.setRoulementTimeline(this);

		leftResourcesPanel.addMouseListener(new LeftResourcesMouseListener());
		rightResourcesPanel.addMouseListener(new RightResourcesMouseListener());

		leftResourcesViewport.setView(leftResourcesPanel);
		leftResourcesViewport
				.addChangeListener(new ResourcesViewportChangeListener());

		rightResourcesViewport.setView(rightResourcesPanel);
		rightResourcesViewport
				.addChangeListener(new ResourcesViewportChangeListener());

		dayDatesPanel.addDatesListener(this);

		dayDatesViewport.setView(dayDatesPanel);
		dayDatesViewport.addChangeListener(new DatesViewportChangeListener());

		internalTimelinePanel.setTransferHandler(new MyTransferHandler());
		internalTimelinePanel.addMouseListener(new CenterMouseListener());

		internalTimelineViewport.setView(internalTimelinePanel);

		verticalScrollBar.getModel().addChangeListener(
				new VerticalScrollBarChangeListener());
		horizontalScrollBar.getModel().addChangeListener(
				horizontalChangeListener);

		MyMouseWheelListener mwl = new MyMouseWheelListener();
		leftResourcesViewport.addMouseWheelListener(mwl);
		rightResourcesViewport.addMouseWheelListener(mwl);
		internalTimelineViewport.addMouseWheelListener(mwl);
		verticalScrollBar.addMouseWheelListener(mwl);

		installInputMap();
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
		// FormLayout layout = new FormLayout(
		//				"FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:NONE,FILL:300DLU:GROW(1.0),FILL:DEFAULT:NONE,FILL:DEFAULT:NONE", //$NON-NLS-1$
		//				"FILL:DEFAULT:NONE,FILL:50DLU:GROW(1.0),FILL:DEFAULT:NONE"); //$NON-NLS-1$
		// PanelBuilder builder = new PanelBuilder(layout);
		// CellConstraints cc = new CellConstraints();
		// builder.add(leftResourcesViewport, cc.xyw(1, 2, 4));
		// builder.add(dayDatesViewport, cc.xy(5, 1));
		// builder.add(internalTimelineViewport, cc.xy(5, 2));
		// builder.add(rightResourcesViewport, cc.xy(6, 2));
		// builder.add(verticalScrollBar, cc.xy(7, 2));
		// builder.add(upLeftPanel, cc.xyw(1, 1, 4));
		// builder.add(horizontalScrollBar, cc.xy(5, 3));
		// builder.add(dayWidthSlider, cc.xy(2, 3));
		// builder.add(minusLabel, cc.xy(1, 3));
		// builder.add(plusLabel, cc.xy(3, 3));
		// builder.add(zoomComboBox, cc.xy(4, 3));
		// builder.add(createLabelWithBorder(), cc.xy(6, 1));

		FormLayout layout = new FormLayout(
				"FILL:DEFAULT:NONE,FILL:300DLU:GROW(1.0),FILL:DEFAULT:NONE,FILL:DEFAULT:NONE", //$NON-NLS-1$
				"FILL:DEFAULT:NONE,FILL:50DLU:GROW(1.0),FILL:DEFAULT:NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(leftResourcesViewport, cc.xy(1, 2));
		builder.add(dayDatesViewport, cc.xy(2, 1));
		builder.add(internalTimelineViewport, cc.xy(2, 2));
		builder.add(rightResourcesViewport, cc.xy(3, 2));
		builder.add(verticalScrollBar, cc.xy(4, 2));
		builder.add(horizontalScrollBar, cc.xy(2, 3));

		builder.add(createLabelWithBorder(), cc.xy(1, 1));
		builder.add(createLabelWithBorder(), cc.xy(1, 3));
		builder.add(createLabelWithBorder(), cc.xyw(3, 1, 2));
		builder.add(createLabelWithBorder(), cc.xyw(3, 3, 2));
		return builder.getPanel();
	}

	public void addRoulementTimelineListener(RoulementTimelineListener l) {
		listenerList.add(RoulementTimelineListener.class, l);
	}

	public void removeRoulementTimelineListener(RoulementTimelineListener l) {
		listenerList.remove(RoulementTimelineListener.class, l);
	}

	private void fireZoomChanged(int dayWidth) {
		RoulementTimelineListener[] listeners = listenerList
				.getListeners(RoulementTimelineListener.class);
		for (RoulementTimelineListener l : listeners) {
			l.zoomChanged(dayWidth);
		}
	}

	private void fireHorizontalScrollBarChanged(int value) {
		RoulementTimelineListener[] listeners = listenerList
				.getListeners(RoulementTimelineListener.class);
		for (RoulementTimelineListener l : listeners) {
			l.horizontalScrollBarChanged(value);
		}
	}

	private void fireVerticalScrollBarChanged(int value) {
		RoulementTimelineListener[] listeners = listenerList
				.getListeners(RoulementTimelineListener.class);
		for (RoulementTimelineListener l : listeners) {
			l.verticalScrollBarChanged(value);
		}
	}

	private void updateAll() {
		updateResourcesFromModel();
		updateVerticalScrollBar();
		updateHorizontalScrollBar();
	}

	private void updateVerticalScrollBar() {
		Dimension extentSize = leftResourcesViewport.getExtentSize();
		Dimension viewSize = leftResourcesViewport.getViewSize();
		Point viewPosition = leftResourcesViewport.getViewPosition();

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

			RoulementTimelineResource newResource = new RoulementTimelineResource(
					i, height);
			int modelColumn = newResource.getModelIndex();
			String columnName = dataModel.getResourceName(modelColumn);
			newResource.setHeaderValue(columnName);
			resourcesModel.addResource(newResource);
		}
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

	public void setGroupFactory(RoulementTimelineGroupFactory groupFactory) {
		internalTimelinePanel.setGroupFactory(groupFactory);

		updateAll();
		resizeAndRepaint();
	}

	public RoulementTimelineGroupFactory getGroupFactory() {
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

	public void setTaskDrop(int resource, RoulementTask... tasks) {
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

	public RoulementTask[] getTaskDrop() {
		return tasksDrop;
	}

	public void setRoulementTimelineDrop(RoulementTimelineDrop timelineDrop) {
		this.timelineDrop = timelineDrop;

		if (timelineDrop != null) {
			SynaptixTransferHandler
					.addDragDropComponent(JRoulementTimeline.this);
		} else {
			SynaptixTransferHandler
					.removeDragDropComponent(JRoulementTimeline.this);
		}
	}

	public RoulementTimelineDrop getRoulementTimelineDrop() {
		return timelineDrop;
	}

	public void setRoulementTimelineDrag(RoulementTimelineDrag timelineDrag) {
		this.timelineDrag = timelineDrag;
	}

	public RoulementTimelineDrag getRoulementTimelineDrag() {
		return timelineDrag;
	}

	public JRoulementTimelineCenter getRoulementTimelineCenter() {
		return internalTimelinePanel;
	}

	public JRoulementTimelineLeftResourcesHeader getLeftResourcesHeader() {
		return leftResourcesPanel;
	}

	public JRoulementTimelineRightResourcesHeader getRightResourcesHeader() {
		return rightResourcesPanel;
	}

	public void setHeaderRenderer(
			RoulementTimelineDayDatesHeaderRenderer headerRenderer) {
		dayDatesPanel.setHeaderRenderer(headerRenderer);
	}

	public RoulementTimelineDayDatesHeaderRenderer getHeaderRenderer() {
		return dayDatesPanel.getHeaderRenderer();
	}

	public RoulementTimelineModel getModel() {
		return dataModel;
	}

	public RoulementTimelineSelectionModel getSelectionModel() {
		return selectionModel;
	}

	public RoulementTimelineResourcesModel getResourcesModel() {
		return resourcesModel;
	}

	public JPopupMenu getCenterPopupMenu() {
		return centerPopupMenu;
	}

	public void setCenterPopupMenu(JPopupMenu centerPopupMenu) {
		this.centerPopupMenu = centerPopupMenu;
	}

	public JPopupMenu getLeftResourcesPopupMenu() {
		return leftResourcesPopupMenu;
	}

	public void setLeftResourcesPopupMenu(JPopupMenu resourcesPopupMenu) {
		this.leftResourcesPopupMenu = resourcesPopupMenu;
	}

	public JPopupMenu getRightResourcesPopupMenu() {
		return rightResourcesPopupMenu;
	}

	public void setRightResourcesPopupMenu(JPopupMenu rightResourcesPopupMenu) {
		this.rightResourcesPopupMenu = rightResourcesPopupMenu;
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
		RoulementTimelineResourcesModel rm = getResourcesModel();
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
		RoulementTimelineResourcesModel cm = getResourcesModel();

		r.width = leftResourcesPanel.getWidth();

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

	public Rectangle getSimpleDayTaskRect(int resource, RoulementTask task) {
		Rectangle r = getResourceRect(resource);

		int x1 = pointAtDayDate(task.getDayDateMin());
		int x2 = pointAtDayDate(task.getDayDateMax());

		r.x = x1;
		r.width = x2 - x1;

		return r;
	}

	private RoulementTimelineResource getResizingResource() {
		return (leftResourcesPanel == null) ? null : leftResourcesPanel
				.getResizingResource();
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

	public JRoulementTimelineDayDatesHeader getDatesHeader() {
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
		double dw = JRoulementTimelineDayDatesHeader.DEFAULT_DAY_WIDTH;
		double sc = (double) getDayWidth() / dw;

		int w = getDayWidth() + (int) (sc * dz);

		dayDatesPanel.setDayWidth(w);
		fireZoomChanged(w);
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
		double dw = JRoulementTimelineDayDatesHeader.DEFAULT_DAY_WIDTH;
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
		if (leftResourcesPanel != null) {
			leftResourcesPanel.resizeAndRepaint();
		}
		if (rightResourcesPanel != null) {
			rightResourcesPanel.resizeAndRepaint();
		}
		if (dayDatesPanel != null) {
			dayDatesPanel.resizeAndRepaint();
		}
		internalTimelinePanel.resizeAndRepaint();
	}

	public void roulementTimelineChanged(RoulementTimelineModelEvent e) {
		// if (e != null
		// && e.getType() == RoulementTimelineModelEvent.Type.DATA_CHANGED) {
		//
		// }

		internalTimelinePanel.precalculeMultiLine();
		updateAll();
		resizeAndRepaint();
	}

	public void valueChanged() {
		if (leftResourcesPanel != null) {
			leftResourcesPanel.repaint();
		}
		if (rightResourcesPanel != null) {
			rightResourcesPanel.repaint();
		}
		internalTimelinePanel.repaint();
	}

	public void datesDayWidthChanged(int oldValue, int newValue) {
		// Pas besoin ?
		// internalTimelinePanel.precalculeMultiLine();

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

	public void resourceAdded(RoulementTimelineResourcesModelEvent e) {
		resizeAndRepaint();
	}

	public void resourceMarginChanged(ChangeEvent e) {
		RoulementTimelineResource resizingRessource = getResizingResource();
		if (resizingRessource != null) {
			resizingRessource.setPreferredHeight(resizingRessource.getHeight());
		}
		resizeAndRepaint();
	}

	public void resourceMoved(RoulementTimelineResourcesModelEvent e) {
		internalTimelinePanel.repaint();
	}

	public void resourceRemoved(RoulementTimelineResourcesModelEvent e) {
		resizeAndRepaint();
	}

	public void resourceSelectionChanged(ListSelectionEvent e) {
	}

	public void scrollRectToVisible(Rectangle contentRect) {
		log.debug("Aller à : " + contentRect);
		log.debug("Actuellement : " + internalTimelineViewport.getViewRect());

		Point viewPosition = internalTimelineViewport.getViewPosition();
		Dimension extent = internalTimelineViewport.getExtentSize();

		if (extent.getWidth() != 0 && extent.getHeight() != 0) {
			int dx = positionAdjustment(extent.width, contentRect.width,
					contentRect.x - viewPosition.x);
			int dy = positionAdjustment(extent.height, contentRect.height,
					contentRect.y - viewPosition.y);

			if (dx != 0 || dy != 0) {
				int startX = viewPosition.x;
				int startY = viewPosition.y;

				viewPosition.x -= dx;
				viewPosition.y -= dy;

				log.debug("Deplacement à : " + viewPosition);

				if (viewPosition.x != startX || viewPosition.y != startY) {
					leftResourcesViewport.setViewPosition(new Point(0,
							viewPosition.y));
					rightResourcesViewport.setViewPosition(new Point(0,
							viewPosition.y));
					dayDatesViewport.setViewPosition(new Point(viewPosition.x,
							0));
				}
			}
		}
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
			Rectangle visibleRect = leftResourcesViewport.getViewRect();
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

			Point p1 = leftResourcesViewport.getViewPosition();
			p1.y = model.getValue();
			leftResourcesViewport.setViewPosition(p1);
			rightResourcesViewport.setViewPosition(p1);

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
			if (e.getSource() == leftResourcesViewport) {
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

	private final class LeftResourcesMouseListener extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			if (leftResourcesPopupMenu != null && e.isPopupTrigger()) {
				leftResourcesPopupMenu.show(e.getComponent(), e.getX(), e
						.getY());
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (leftResourcesPopupMenu != null && e.isPopupTrigger()) {
				leftResourcesPopupMenu.show(e.getComponent(), e.getX(), e
						.getY());
			}
		}
	}

	private final class RightResourcesMouseListener extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			if (rightResourcesPopupMenu != null && e.isPopupTrigger()) {
				rightResourcesPopupMenu.show(e.getComponent(), e.getX(), e
						.getY());
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (rightResourcesPopupMenu != null && e.isPopupTrigger()) {
				rightResourcesPopupMenu.show(e.getComponent(), e.getX(), e
						.getY());
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

	private final class MyTransferHandler extends SynaptixTransferHandler {

		private static final long serialVersionUID = 6560212511433794019L;

		public int getSourceActions(JComponent c) {
			return COPY;
		}

		protected Transferable createTransferable(JComponent c) {
			if (timelineDrag != null) {
				return timelineDrag.createTransferable(JRoulementTimeline.this);
			}
			return super.createTransferable(c);
		}

		protected void exportDone(JComponent source, Transferable data,
				int action) {
			if (timelineDrag != null) {
				timelineDrag.exportDone(JRoulementTimeline.this, data);
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

						RoulementTask[] tasks = timelineDrop.getTask(
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
}
