package com.synaptix.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.UIResource;

import com.synaptix.swing.event.TimelineDatesModelListener;
import com.synaptix.swing.event.TimelineModelEvent;
import com.synaptix.swing.event.TimelineModelListener;
import com.synaptix.swing.event.TimelineRessourceModelEvent;
import com.synaptix.swing.event.TimelineRessourceModelListener;
import com.synaptix.swing.plaf.TimelineUI;
import com.synaptix.swing.timeline.DefaultTimelineRessourceModel;
import com.synaptix.swing.timeline.DefaultTimelineTaskRenderer;
import com.synaptix.swing.timeline.JTimelineDatesHeader;
import com.synaptix.swing.timeline.JTimelineRessourceHeader;
import com.synaptix.swing.timeline.JTimelineScale;
import com.synaptix.swing.timeline.SelectionTimeline;
import com.synaptix.swing.timeline.TimelineDatesModel;
import com.synaptix.swing.timeline.TimelineRessource;
import com.synaptix.swing.timeline.TimelineRessourceModel;
import com.synaptix.swing.timeline.TimelineTaskRenderer;
import com.synaptix.swing.utils.GUIWindow;

public class JTimeline extends JComponent implements TimelineModelListener,
		Scrollable, TimelineRessourceModelListener, TimelineDatesModelListener,
		ListSelectionListener {

	public enum SelectionMode {
		Single, Multi
	}

	private static final long serialVersionUID = -5030096973479663744L;

	private static final String uiClassID = "TimelineUI"; //$NON-NLS-1$

	public static final int AUTO_RESIZE_OFF = 0;

	protected TimelineModel dataModel;

	protected TimelineRessourceModel ressourceModel;

	protected TimelineDatesModel datesModel;

	protected ListSelectionModel selectionModel;

	protected JTimelineRessourceHeader timelineRessourceHeader;

	protected JTimelineDatesHeader timelineDatesHeader;

	protected JTimelineScale timelineScale;

	protected JSlider slider;

	private ChangeListener sliderChangeListener;

	protected TimelineTaskRenderer taskRenderer;

	protected boolean autoCreateRessourcesFromModel;

	protected Dimension preferredViewportSize;

	private boolean fillsViewportWidth;

	protected SelectionTimeline selectedTimeline;

	protected Color selectionBackground;

	protected Color selectionForeground;

	protected Color gridColor;

	protected SelectionMode selectionMode;

	private Action saveAsImageAction;

	protected JPopupMenu mPopupMenu;

	protected JMenuItem mMenuItemHide;

	protected JMenuItem mMenuItemVisiblityProperties;

	protected JMenuItem mMenuExport;

	private TimelineRessource currentRessource;

	private static Set<KeyStroke> managingFocusForwardTraversalKeys;

	private static Set<KeyStroke> managingFocusBackwardTraversalKeys;

	static {
		UIManager.getDefaults().put(uiClassID,
				"com.synaptix.swing.plaf.basic.BasicTimelineUI"); //$NON-NLS-1$
	}

	public static final class DropLocation extends TransferHandler.DropLocation {

		private Date date;

		private int ressource;

		private DropLocation(Point p, int ressource, Date date) {
			super(p);

			this.ressource = ressource;
			this.date = date;
		}

		public Date getDate() {
			return date;
		}

		public int getRessource() {
			return ressource;
		}

		public String toString() {
			return getClass().getName() + "[dropPoint=" + getDropPoint() + "," //$NON-NLS-1$ //$NON-NLS-2$
					+ "ressource=" + ressource + "," + "date=" + date + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
	}

	public JTimeline() {
		this(null, null, null);
	}

	public JTimeline(TimelineModel dm) {
		this(dm, null, null);
	}

	public JTimeline(TimelineModel dm, TimelineRessourceModel rm) {
		this(dm, rm, null);
	}

	public JTimeline(TimelineModel tm, TimelineRessourceModel rm,
			ListSelectionModel sm) {
		super();
		this.setLayout(null);

		this.setFocusable(true);
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				JTimeline.getManagingFocusForwardTraversalKeys());
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS,
				JTimeline.getManagingFocusBackwardTraversalKeys());

		TimelineDatesModel dm = new TimelineDatesModel();
		setDatesModel(dm);

		if (rm == null) {
			rm = createDefaultRessourceModel();
			autoCreateRessourcesFromModel = true;
		}
		setRessourceModel(rm);

		if (sm == null) {
			sm = createDefaultSelectionModel();
		}
		setSelectionModel(sm);

		// Set the model last, that way if the autoCreatColumnsFromModel has
		// been set above, we will automatically populate an empty columnModel
		// with suitable columns for the new model.
		if (tm == null) {
			tm = createDefaultDataModel();
		}
		setModel(tm);

		initializeLocalVars();
		updateUI();

		getTimelineRessourceHeader().addMouseListener(
				new TimelineRessourceHeaderMouseListener());
	}

	static Set<KeyStroke> getManagingFocusForwardTraversalKeys() {
		synchronized (JComponent.class) {
			if (managingFocusForwardTraversalKeys == null) {
				managingFocusForwardTraversalKeys = new HashSet<KeyStroke>(1);
				managingFocusForwardTraversalKeys.add(KeyStroke.getKeyStroke(
						KeyEvent.VK_TAB, InputEvent.CTRL_MASK));
			}
		}
		return managingFocusForwardTraversalKeys;
	}

	static Set<KeyStroke> getManagingFocusBackwardTraversalKeys() {
		synchronized (JComponent.class) {
			if (managingFocusBackwardTraversalKeys == null) {
				managingFocusBackwardTraversalKeys = new HashSet<KeyStroke>(1);
				managingFocusBackwardTraversalKeys.add(KeyStroke.getKeyStroke(
						KeyEvent.VK_TAB, InputEvent.SHIFT_MASK
								| InputEvent.CTRL_MASK));
			}
		}
		return managingFocusBackwardTraversalKeys;
	}

	public boolean isEnabledSaveAsImage() {
		return saveAsImageAction.isEnabled();
	}

	public void setEnabledSaveAsImage(boolean enable) {
		saveAsImageAction.setEnabled(enable);
	}

	public void addNotify() {
		super.addNotify();
		configureEnclosingScrollPane();
	}

	private JLabel createLabelWithBorder(String text) {
		return createLabelWithBorder(text, JLabel.CENTER);
	}

	private JLabel createLabelWithBorder(String text, int horizontalAligment) {
		JLabel l = new JLabel(text, horizontalAligment);
		l.setOpaque(true);
		l.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		return l;
	}

	protected void configureEnclosingScrollPane() {
		Container p = getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				JViewport viewport = scrollPane.getViewport();
				if (viewport == null || viewport.getView() != this) {
					return;
				}
				scrollPane.setRowHeaderView(getTimelineRessourceHeader());
				scrollPane.setColumnHeaderView(getTimelineDatesHeader());

				JToolBar toolBar = new JToolBar();
				toolBar.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
				toolBar.setFloatable(false);
				toolBar.add(saveAsImageAction);

				scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, toolBar);

				scrollPane.setCorner(JScrollPane.LOWER_LEFT_CORNER, slider);
				scrollPane.setCorner(JScrollPane.LOWER_RIGHT_CORNER,
						createLabelWithBorder("")); //$NON-NLS-1$
				scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER,
						createLabelWithBorder("")); //$NON-NLS-1$

				scrollPane.setCorner(JSyScrollPane.NORTH_CENTER,
						getTimelineScale());
				scrollPane.setCorner(JSyScrollPane.NORTH_LEFT_CORNER,
						createLabelWithBorder("")); //$NON-NLS-1$
				scrollPane.setCorner(JSyScrollPane.NORTH_RIGHT_CORNER,
						createLabelWithBorder("")); //$NON-NLS-1$

				scrollPane
						.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

				Border border = scrollPane.getBorder();
				if (border == null || border instanceof UIResource) {
					Border scrollPaneBorder = UIManager
							.getBorder("Table.scrollPaneBorder"); //$NON-NLS-1$
					if (scrollPaneBorder != null) {
						scrollPane.setBorder(scrollPaneBorder);
					}
				}

				scrollPane.getHorizontalScrollBar().addAdjustmentListener(
						new HorizontalScrollBarAdjustmentListener());

			}
		}
	}

	public void setAutoCreateRessourcesFromModel(
			boolean autoCreateRessourcesFromModel) {
		if (this.autoCreateRessourcesFromModel != autoCreateRessourcesFromModel) {
			boolean old = this.autoCreateRessourcesFromModel;
			this.autoCreateRessourcesFromModel = autoCreateRessourcesFromModel;
			if (autoCreateRessourcesFromModel) {
				createDefaultRessourcesFromModel();
			}
			firePropertyChange("autoCreateRessourcesFromModel", old, //$NON-NLS-1$
					autoCreateRessourcesFromModel);
		}
	}

	public boolean getAutoCreateRessourcesFromModel() {
		return autoCreateRessourcesFromModel;
	}

	public void createDefaultDatesFromModel() {
		TimelineModel m = getModel();
		if (m != null) {
			TimelineDatesModel dm = getDatesModel();
			dm.setDateMin(m.getDateMin());
			dm.setDateMax(m.getDateMax());
		}
	}

	public void createDefaultRessourcesFromModel() {
		TimelineModel m = getModel();
		if (m != null) {
			// Remove any current ressources
			TimelineRessourceModel rm = getRessourceModel();
			while (rm.getRessourceCount() > 0) {
				rm.removeRessource(rm.getRessource(0));
			}

			// Create new ressources from the data model info
			for (int i = 0; i < m.getRessourceCount(); i++) {
				TimelineRessource newRessource = new TimelineRessource(i);
				addRessource(newRessource);
			}
		}
	}

	public void addRessource(TimelineRessource aRessource) {
		if (aRessource.getHeaderValue() == null) {
			int modelColumn = aRessource.getModelIndex();
			String columnName = getModel().getRessourceName(modelColumn);
			aRessource.setHeaderValue(columnName);
		}
		getRessourceModel().addRessource(aRessource);
	}

	public void removeColumn(TimelineRessource aRessource) {
		getRessourceModel().removeRessource(aRessource);
	}

	public void moveColumn(int ressource, int targetRessource) {
		getRessourceModel().moveRessource(ressource, targetRessource);
	}

	protected void initializeLocalVars() {
		setOpaque(true);

		saveAsImageAction = new SaveAsImageAction();

		setTimelineRessourceHeader(createDefaultTimelineRessourceHeader());
		setTimelineDatesHeader(createDefaultTimelineDatesHeader());
		setTimelineScale(createDefaultTimelineScale());

		setDefaultTasklRenderer(createDefaultTaskRenderer());

		sliderChangeListener = new SliderChangeListener();
		slider = new JSlider(JSlider.HORIZONTAL, TimelineDatesModel.MIN_WIDTH,
				TimelineDatesModel.MAX_WIDTH, TimelineDatesModel.DEFAULT_WIDTH);
		slider.addChangeListener(sliderChangeListener);

		// I'm registered to do tool tips so we can draw tips for the renderers
		ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
		toolTipManager.registerComponent(this);

		setAutoscrolls(true);

		setPreferredScrollableViewportSize(new Dimension(600, 400));

		selectedTimeline = null;
		selectionBackground = null;

		selectionMode = SelectionMode.Multi;

		createPopupMenu();
	}

	private final class SliderChangeListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			getDatesModel().setWidth(slider.getValue());
		}

	}

	public void setTimelineRessourceHeader(
			JTimelineRessourceHeader timelineRessourceHeader) {
		if (this.timelineRessourceHeader != timelineRessourceHeader) {
			JTimelineRessourceHeader old = this.timelineRessourceHeader;

			if (old != null) {
				old.setTimeline(null);
			}
			this.timelineRessourceHeader = timelineRessourceHeader;
			if (timelineRessourceHeader != null) {
				timelineRessourceHeader.setTimeline(this);
			}
			firePropertyChange("timelineRessourceHeader", old, //$NON-NLS-1$
					timelineRessourceHeader);
		}
	}

	public JTimelineRessourceHeader getTimelineRessourceHeader() {
		return timelineRessourceHeader;
	}

	protected JTimelineRessourceHeader createDefaultTimelineRessourceHeader() {
		return new JTimelineRessourceHeader(ressourceModel);
	}

	public void setTimelineScale(JTimelineScale timelineScale) {
		if (this.timelineScale != timelineScale) {
			JTimelineScale old = this.timelineScale;

			if (old != null) {
				old.setTimeline(null);
			}
			this.timelineScale = timelineScale;
			if (timelineScale != null) {
				timelineScale.setTimeline(this);
			}
			firePropertyChange("timelineScale", old, timelineScale); //$NON-NLS-1$
		}
	}

	public JTimelineScale getTimelineScale() {
		return timelineScale;
	}

	protected JTimelineScale createDefaultTimelineScale() {
		return new JTimelineScale();
	}

	public void setTimelineDatesHeader(JTimelineDatesHeader timelineDatesHeader) {
		if (this.timelineDatesHeader != timelineDatesHeader) {
			JTimelineDatesHeader old = this.timelineDatesHeader;

			if (old != null) {
				old.setTimeline(null);
			}
			this.timelineDatesHeader = timelineDatesHeader;
			if (timelineDatesHeader != null) {
				timelineDatesHeader.setTimeline(this);
			}
			firePropertyChange("timelineDatesHeader", old, timelineDatesHeader); //$NON-NLS-1$
		}
	}

	public JTimelineDatesHeader getTimelineDatesHeader() {
		return timelineDatesHeader;
	}

	protected JTimelineDatesHeader createDefaultTimelineDatesHeader() {
		return new JTimelineDatesHeader(datesModel);
	}

	public void setModel(TimelineModel dataModel) {
		if (dataModel == null) {
			throw new IllegalArgumentException(
					"Cannot set a null TimelineModel"); //$NON-NLS-1$
		}
		if (this.dataModel != dataModel) {
			TimelineModel old = this.dataModel;
			if (old != null) {
				old.removeTimelineModelListener(this);
			}
			this.dataModel = dataModel;
			dataModel.addTimelineModelListener(this);

			timelineChanged(new TimelineModelEvent(dataModel,
					TimelineModelEvent.Type.HEADER_RESSOURCES));

			firePropertyChange("model", old, dataModel); //$NON-NLS-1$
		}
	}

	public TimelineModel getModel() {
		return dataModel;
	}

	public void setDatesModel(TimelineDatesModel datesModel) {
		if (datesModel == null) {
			throw new IllegalArgumentException(
					"Cannot set a null RessourceModel"); //$NON-NLS-1$
		}
		TimelineDatesModel old = this.datesModel;
		if (datesModel != old) {
			if (old != null) {
				old.removeDatesModelListener(this);
			}
			this.datesModel = datesModel;
			datesModel.addDatesModelListener(this);

			// Set the column model of the header as well.
			if (timelineDatesHeader != null) {
				timelineDatesHeader.setDatesModel(datesModel);
			}

			firePropertyChange("datesModel", old, datesModel); //$NON-NLS-1$
			resizeAndRepaint();
		}
	}

	public TimelineDatesModel getDatesModel() {
		return datesModel;
	}

	public void setRessourceModel(TimelineRessourceModel ressourceModel) {
		if (ressourceModel == null) {
			throw new IllegalArgumentException(
					"Cannot set a null RessourceModel"); //$NON-NLS-1$
		}
		TimelineRessourceModel old = this.ressourceModel;
		if (ressourceModel != old) {
			if (old != null) {
				old.removeRessourceModelListener(this);
			}
			this.ressourceModel = ressourceModel;
			ressourceModel.addRessourceModelListener(this);

			// Set the column model of the header as well.
			if (timelineRessourceHeader != null) {
				timelineRessourceHeader.setRessourceModel(ressourceModel);
			}

			firePropertyChange("ressourceModel", old, ressourceModel); //$NON-NLS-1$
			resizeAndRepaint();
		}
	}

	public TimelineRessourceModel getRessourceModel() {
		return ressourceModel;
	}

	public void setSelectionModel(ListSelectionModel newModel) {
		if (newModel == null) {
			throw new IllegalArgumentException(
					"Cannot set a null SelectionModel"); //$NON-NLS-1$
		}

		ListSelectionModel oldModel = selectionModel;

		if (newModel != oldModel) {
			if (oldModel != null) {
				oldModel.removeListSelectionListener(this);
			}

			selectionModel = newModel;
			newModel.addListSelectionListener(this);

			firePropertyChange("selectionModel", oldModel, newModel); //$NON-NLS-1$
			repaint();
		}
	}

	protected void resizeAndRepaint() {
		if (timelineRessourceHeader != null) {
			timelineRessourceHeader.resizeAndRepaint();
		}

		revalidate();
		repaint();
	}

	protected TimelineRessourceModel createDefaultRessourceModel() {
		return new DefaultTimelineRessourceModel();
	}

	protected TimelineModel createDefaultDataModel() {
		return null;// new DefaultTableModel();
	}

	protected ListSelectionModel createDefaultSelectionModel() {
		return new DefaultListSelectionModel();
	}

	public TimelineUI getUI() {
		return (TimelineUI) ui;
	}

	public void setUI(TimelineUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	public void updateUI() {
		if (timelineRessourceHeader != null
				&& timelineRessourceHeader.getParent() == null) {
			timelineRessourceHeader.updateUI();
		}
		if (timelineDatesHeader != null
				&& timelineDatesHeader.getParent() == null) {
			timelineDatesHeader.updateUI();
		}

		setUI((TimelineUI) UIManager.getUI(this));
	}

	public String getUIClassID() {
		return uiClassID;
	}

	public int getRessourceCount() {
		return getRessourceModel().getRessourceCount();
	}

	public int convertRessourceIndexToModel(int viewRessourceIndex) {
		if (viewRessourceIndex < 0) {
			return viewRessourceIndex;
		}
		return getRessourceModel().getRessource(viewRessourceIndex)
				.getModelIndex();
	}

	public int convertRessourceIndexToView(int modelRessourceIndex) {
		if (modelRessourceIndex < 0) {
			return modelRessourceIndex;
		}
		TimelineRessourceModel rm = getRessourceModel();
		for (int ressource = 0; ressource < getRessourceCount(); ressource++) {
			if (rm.getRessource(ressource).getModelIndex() == modelRessourceIndex) {
				return ressource;
			}
		}
		return -1;
	}

	public Rectangle getCellRect(int i, int res, boolean b) {
		Rectangle rect = timelineRessourceHeader.getHeaderRect(res);
		rect.x = 0;
		rect.width = getDatesModel().getWidth();
		return rect;
	}

	public int getAutoResizeMode() {
		return AUTO_RESIZE_OFF;
	}

	public void setPreferredScrollableViewportSize(Dimension size) {
		preferredViewportSize = size;
	}

	public Dimension getPreferredScrollableViewportSize() {
		return preferredViewportSize;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		if (orientation == SwingUtilities.VERTICAL) {
			int ressource = ressourceAtPoint(new Point(visibleRect.x,
					visibleRect.y));
			if (ressource < 0) {
				return getRessourceModel().getDefaultHeight();
			} else {
				int y = 0;
				for (int i = 0; i < ressource; i++) {
					y += getRessourceModel().getHeight(i);
				}
				int h = getRessourceModel().getHeight(ressource);

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
			int w = getDatesModel().getWidth() * 7;
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

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		if (orientation == SwingUtilities.VERTICAL) {
			int ressource = ressourceAtPoint(new Point(visibleRect.x,
					visibleRect.y));
			if (ressource < 0) {
				return getRessourceModel().getDefaultHeight();
			} else {
				int y = 0;
				for (int i = 0; i < ressource; i++) {
					y += getRessourceModel().getHeight(i);
				}
				int h = getRessourceModel().getHeight(ressource);

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
			int w = getDatesModel().getWidth();
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

	public boolean getScrollableTracksViewportHeight() {
		return false;// !(autoResizeMode == AUTO_RESIZE_OFF);
	}

	public boolean getScrollableTracksViewportWidth() {
		return getFillsViewportWidth()
				&& getParent() instanceof JViewport
				&& (((JViewport) getParent()).getWidth() > getPreferredSize().width);
	}

	public void setFillsViewportWidth(boolean fillsViewportWidth) {
		boolean old = this.fillsViewportWidth;
		this.fillsViewportWidth = fillsViewportWidth;
		resizeAndRepaint();
		firePropertyChange("fillsViewportWidth", old, fillsViewportWidth); //$NON-NLS-1$
	}

	public boolean getFillsViewportWidth() {
		return fillsViewportWidth;
	}

	public void ressourceAdded(TimelineRessourceModelEvent e) {
		resizeAndRepaint();
	}

	private TimelineRessource getResizingRessource() {
		return (timelineRessourceHeader == null) ? null
				: timelineRessourceHeader.getResizingRessource();
	}

	public void ressourceMarginChanged(ChangeEvent e) {
		TimelineRessource resizingRessource = getResizingRessource();
		if (resizingRessource != null) {
			resizingRessource.setPreferredHeight(resizingRessource.getHeight());
		}
		resizeAndRepaint();
	}

	public void ressourceMoved(TimelineRessourceModelEvent e) {
		repaint();
	}

	public void ressourceRemoved(TimelineRessourceModelEvent e) {
		resizeAndRepaint();
	}

	public void ressourceSelectionChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub

	}

	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub

	}

	public void clearSelection() {
		this.setSelectedTimeline(null);
		selectionModel.clearSelection();
		ressourceModel.getSelectionModel().clearSelection();
	}

	private void clearSelectionAndLeadAnchor() {
		selectionModel.setValueIsAdjusting(true);
		ressourceModel.getSelectionModel().setValueIsAdjusting(true);

		clearSelection();

		selectionModel.setAnchorSelectionIndex(-1);
		selectionModel.setLeadSelectionIndex(-1);
		ressourceModel.getSelectionModel().setAnchorSelectionIndex(-1);
		ressourceModel.getSelectionModel().setLeadSelectionIndex(-1);

		selectionModel.setValueIsAdjusting(false);
		ressourceModel.getSelectionModel().setValueIsAdjusting(false);
	}

	public void timelineChanged(TimelineModelEvent e) {
		if (e == null
				|| e.getType() == TimelineModelEvent.Type.HEADER_RESSOURCES) {
			if (e.getRessource() == -1) {
				// The whole thing changed
				clearSelectionAndLeadAnchor();

				createDefaultDatesFromModel();

				if (getAutoCreateRessourcesFromModel()) {
					// This will effect invalidation of the JTable and
					// JTableHeader.
					createDefaultRessourcesFromModel();
					return;
				}
				resizeAndRepaint();
			} else if (timelineRessourceHeader != null) {
				int ressource = convertRessourceIndexToView(e.getRessource());
				TimelineRessourceModel cm = getRessourceModel();
				TimelineRessource tr = cm.getRessource(ressource);
				String name = getModel().getRessourceName(e.getRessource());
				tr.setHeaderValue(name);

				timelineRessourceHeader.resizeAndRepaint(ressource);
			}
		} else if (e.getType() == TimelineModelEvent.Type.MODIFY) {
			int ressource = convertRessourceIndexToView(e.getRessource());
			Rectangle rect = getRessourceRect(ressource);

			revalidate();
			repaint(0, rect.y, this.getWidth(), rect.height);

			if (timelineRessourceHeader != null) {
				TimelineRessourceModel cm = getRessourceModel();
				TimelineRessource tr = cm.getRessource(ressource);
				String name = getModel().getRessourceName(e.getRessource());
				tr.setHeaderValue(name);

				timelineRessourceHeader.resizeAndRepaint(ressource);
			}
		}
	}

	public void datesChanged(ChangeEvent e) {
		if (slider != null) {
			slider.removeChangeListener(sliderChangeListener);
			slider.setValue(getDatesModel().getWidth());
			slider.addChangeListener(sliderChangeListener);
		}

		resizeAndRepaint();
	}

	public Date dateAtPoint(Point point) {
		return getDatesModel().getDateInX(point.x);
	}

	public int pointAtDate(Date date) {
		return getDatesModel().getPixels(date);
	}

	public int ressourceAtPoint(Point point) {
		return getRessourceModel().getRessourceIndexAtY(point.y);
	}

	public int pointAtRessource(int ressource) {
		int h = 0;
		for (int i = 0; i < ressource; i++) {
			h += getRessourceModel().getHeight(i);
		}
		return h;
	}

	public TimelineTaskRenderer getDefaultTaskRenderer() {
		return taskRenderer;
	}

	public void setDefaultTasklRenderer(TimelineTaskRenderer taskRenderer) {
		TimelineTaskRenderer old = this.taskRenderer;
		this.taskRenderer = taskRenderer;
		resizeAndRepaint();
		firePropertyChange("taskRenderer", old, taskRenderer); //$NON-NLS-1$
	}

	protected TimelineTaskRenderer createDefaultTaskRenderer() {
		return new DefaultTimelineTaskRenderer();
	}

	public Rectangle getRessourceRect(int ressource) {
		Rectangle r = new Rectangle();
		TimelineRessourceModel cm = getRessourceModel();

		r.width = getWidth();

		if (ressource < 0) {
		} else if (ressource >= cm.getRessourceCount()) {
		} else {
			for (int i = 0; i < ressource; i++) {
				r.y += cm.getRessource(i).getHeight();
			}

			r.height = cm.getRessource(ressource).getHeight();
		}
		return r;
	}

	public void scrollDateAndRessourceToVisible(Date date, int r) {
		int x = pointAtDate(date);
		int y = pointAtRessource(r);
		int w = getDatesModel().getWidth();
		int h = getRessourceModel().getHeight(r);

		scrollRectToVisible(new Rectangle(x, y, w, h));
	}

	public SelectionTimeline getSelectedTimeline() {
		return selectedTimeline;
	}

	public void setSelectedTimeline(SelectionTimeline selectedTimeline) {
		SelectionTimeline old = this.selectedTimeline;
		this.selectedTimeline = selectedTimeline;
		resizeAndRepaint();
		firePropertyChange("selectedTimeline", old, selectedTimeline); //$NON-NLS-1$
	}

	private final class HorizontalScrollBarAdjustmentListener implements
			AdjustmentListener {

		double oldMax;

		Date date;

		public HorizontalScrollBarAdjustmentListener() {
			oldMax = -1;
			date = null;
		}

		public void adjustmentValueChanged(AdjustmentEvent e) {
			JScrollBar scrollBar = (JScrollBar) e.getSource();
			if (!e.getValueIsAdjusting()) {
				double max = scrollBar.getMaximum();
				if (oldMax == -1) {
					oldMax = max;
				} else if (oldMax != max) {
					// double delta = max / oldMax;
					oldMax = max;

					int x = pointAtDate(date);
					scrollBar.setValue(x);

					// JViewport viewport = (JViewport) getParent();
					// Rectangle rect = viewport.getViewRect();
					//
					// if (rect.x + rect.width < max) {
					// int v = (int) (scrollBar.getValue() * delta);
					// scrollBar.setValue(v);
					// }
				}
				date = dateAtPoint(new Point(scrollBar.getValue(), 0));
			}
		}
	}

	public Color getGridColor() {
		return gridColor;
	}

	public void setGridColor(Color gridColor) {
		Color oldValue = getGridColor();
		this.gridColor = gridColor;
		resizeAndRepaint();
		firePropertyChange("gridColor", oldValue, gridColor); //$NON-NLS-1$
	}

	public Color getSelectionBackground() {
		return selectionBackground;
	}

	public void setSelectionBackground(Color selectionBackground) {
		Color oldValue = getSelectionBackground();
		this.selectionBackground = selectionBackground;
		resizeAndRepaint();
		firePropertyChange("selectionBackground", oldValue, selectionBackground); //$NON-NLS-1$
	}

	public Color getSelectionForeground() {
		return selectionForeground;
	}

	public void setSelectionForeground(Color selectionForeground) {
		Color oldValue = getSelectionForeground();
		this.selectionForeground = selectionForeground;
		resizeAndRepaint();
		firePropertyChange("selectionForeground", oldValue, selectionForeground); //$NON-NLS-1$
	}

	public SelectionMode getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(SelectionMode selectionMode) {
		this.selectionMode = selectionMode;
	}

	public void scrollToDate(Date date) {
		int value = pointAtDate(date);
		Container p = getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				scrollPane.getHorizontalScrollBar().setValue(value);
			}
		}
	}

	public void scrollToRessource(int ressource) {
		int value = pointAtRessource(ressource);
		Container p = getParent();
		if (p instanceof JViewport) {
			Container gp = p.getParent();
			if (gp instanceof JScrollPane) {
				JScrollPane scrollPane = (JScrollPane) gp;
				scrollPane.getVerticalScrollBar().setValue(value);
			}
		}
	}

	private final class SaveAsImageAction extends AbstractAction {

		private static final long serialVersionUID = 5759533619932758066L;

		public SaveAsImageAction() {
			super(SwingMessages.getString("JTimeline.31")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			exportToImage();
		}
	}

	private final class SaveFileFilter extends FileFilter {

		public boolean accept(File pathname) {
			boolean res = false;
			if (pathname.isDirectory())
				res = true;
			else if (pathname.isFile()) {
				String extension = getExtension(pathname);
				if (extension != null) {
					if (extension.equals("jpg")) { //$NON-NLS-1$
						res = true;
					}
				}
			}
			return res;
		}

		public String getDescription() {
			return SwingMessages.getString("JTimeline.33"); //$NON-NLS-1$
		}
	}

	private String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	private void exportToImage() {
		JScrollPane scrollPane = (JScrollPane) getParent().getParent();

		Component cTop = scrollPane.getColumnHeader().getComponent(0);
		Component cLeft = scrollPane.getRowHeader().getComponent(0);
		Component cCenter = scrollPane.getViewport().getComponent(0);

		int width = cLeft.getWidth() + cCenter.getWidth();
		int height = cTop.getHeight() + cCenter.getHeight();

		System.out.println();

		if (width > 10000 || height > 10000) {
			JOptionPane
					.showMessageDialog(
							GUIWindow.getActiveWindow(),
							"<html>" //$NON-NLS-1$
									+ SwingMessages.getString("JTimeline.35") //$NON-NLS-1$
									+ "<br/>" //$NON-NLS-1$
									+ MessageFormat.format(SwingMessages
											.getString("JTimeline.37"), //$NON-NLS-1$
											String.valueOf(width), String
													.valueOf(height))
									+ "<br/>" + SwingMessages.getString("JTimeline.39") + " 10000px sur 10000px" + "</html>", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							SwingMessages.getString("JTimeline.42"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
			return;
		}

		BufferedImage buffer = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics g = buffer.createGraphics();
		g.setColor(cCenter.getBackground());
		g.fillRect(0, 0, width, height);

		g.translate(cLeft.getWidth(), 0);
		cTop.paint(g);
		g.translate(0, cTop.getHeight());
		cCenter.paint(g);
		g.translate(-cLeft.getWidth(), 0);
		cLeft.paint(g);

		g.dispose();

		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new SaveFileFilter());
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		if (chooser.showSaveDialog(GUIWindow.getActiveWindow()) == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			if (getExtension(file) == null) {
				file = new File(file.getAbsolutePath() + ".jpg"); //$NON-NLS-1$
			}
			if (!file.isDirectory()) {
				if (!file.exists()
						|| JOptionPane.showConfirmDialog(GUIWindow
								.getActiveWindow(), MessageFormat.format(
								SwingMessages.getString("JTimeline.44"), file //$NON-NLS-1$
										.getName()), SwingMessages
								.getString("JTimeline.45"), //$NON-NLS-1$
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					try {
						ImageIO.write(buffer, "jpg", file); //$NON-NLS-1$
					} catch (IOException e1) {
						JOptionPane
								.showMessageDialog(
										GUIWindow.getActiveWindow(),
										SwingMessages.getString("JTimeline.47"), SwingMessages.getString("JTimeline.42"), //$NON-NLS-1$ //$NON-NLS-2$
										JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}

	private void showDialogVisibility() {
		// DialogVisibilityColumns dialog = new DialogVisibilityColumns(this);
		// dialog.execute();
	}

	private void hideRessource(TimelineRessource c) {
		// SyTableColumnModel ytcm = (SyTableColumnModel) this.getColumnModel();
		// if (c.isVisible()) {
		// ytcm.invisibleColumn(c);
		// } else {
		// ytcm.visibleColumn(c);
		// }
	}

	protected void createPopupMenu() {
		mPopupMenu = new JPopupMenu();

		mMenuItemHide = new JMenuItem();
		mMenuItemHide.setText(SwingMessages.getString("JTimeline.0")); //$NON-NLS-1$
		mMenuItemHide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hideRessource(currentRessource);
			}
		});

		mMenuItemVisiblityProperties = new JMenuItem();
		mMenuItemVisiblityProperties.setText(SwingMessages
				.getString("JTimeline.50")); //$NON-NLS-1$
		mMenuItemVisiblityProperties.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showDialogVisibility();
			}
		});

		mMenuExport = new JMenuItem();
		mMenuExport.setText(SwingMessages.getString("JTimeline.51")); //$NON-NLS-1$
		mMenuExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent pEvent) {
				exportToImage();
			}
		});

		mPopupMenu.add(mMenuItemHide);
		mPopupMenu.add(mMenuItemVisiblityProperties);
		mPopupMenu.addSeparator();
		mPopupMenu.add(mMenuExport);

		currentRessource = null;
	}

	public void setMPopUpMenu(JPopupMenu popUpMenu) {
		mPopupMenu = popUpMenu;
	}

	public JPopupMenu getMPopUpMenu() {
		return mPopupMenu;
	}

	public String getRessourceName(int column) {
		return getModel()
				.getRessourceName(convertRessourceIndexToModel(column));
	}

	public TimelineRessource getRessource(Object identifier) {
		TimelineRessourceModel trm = getRessourceModel();
		int ressourceIndex = trm.getRessourceIndex(identifier);
		return trm.getRessource(ressourceIndex);
	}

	private void showPopupMenu(MouseEvent e) {
		int i = ressourceAtPoint(e.getPoint());
		if (i != -1) {
			currentRessource = this.getRessource(this.getRessourceName(i));
		} else {
			currentRessource = null;
		}

		if (currentRessource != null) {
			String name = this.getModel().getRessourceName(
					currentRessource.getModelIndex());

			mMenuItemHide
					.setText(SwingMessages.getString("JTimeline.0") + " " + name); //$NON-NLS-1$ //$NON-NLS-2$

			// TODO Temporaire
			mMenuItemHide.setEnabled(false);
			mMenuItemVisiblityProperties.setEnabled(false);

			mPopupMenu.show(this.getTimelineRessourceHeader(), e.getPoint().x,
					e.getPoint().y);
		}
	}

	private class TimelineRessourceHeaderMouseListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger()) {
				showPopupMenu(e);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger()) {
				showPopupMenu(e);
			}
		}
	}
}
