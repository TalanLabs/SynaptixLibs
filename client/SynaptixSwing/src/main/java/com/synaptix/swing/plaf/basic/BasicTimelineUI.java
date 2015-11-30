package com.synaptix.swing.plaf.basic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.CellRendererPane;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

import com.synaptix.swing.JTimeline;
import com.synaptix.swing.Task;
import com.synaptix.swing.TimelineModel;
import com.synaptix.swing.plaf.TimelineUI;
import com.synaptix.swing.timeline.JTimelineRessourceHeader;
import com.synaptix.swing.timeline.SelectionTimeline;
import com.synaptix.swing.timeline.TimelineDatesModel;
import com.synaptix.swing.timeline.TimelineRessource;
import com.synaptix.swing.timeline.TimelineRessourceModel;
import com.synaptix.swing.timeline.TimelineTaskRenderer;
import com.synaptix.swing.utils.DateTimeUtils;

import sun.swing.SwingUtilities2;

public class BasicTimelineUI extends TimelineUI {

	private final static Color selectedColorDefault = new Color(255, 100, 100,
			64);

	protected JTimeline timeline;

	protected CellRendererPane rendererPane;

	protected MouseInputListener mouseInputListener;

	protected MouseWheelListener mouseWheelListener;

	protected KeyListener keyListener;

	protected FocusListener focusListener;

	private final static Stroke strokeFirstDayOfWeekGrid;

	private final static Stroke strokeDayGrid;

	private final static Stroke strokeHourGrid;

	private final static Color colorFirstDayOfWeekGrid;

	private final static Color colorDayGrid;

	private final static Color colorHourGrid;

	private TaskPriorityComparator taskPriorityComparator;

	private TaskComparator taskComparator;

	static {
		strokeDayGrid = new BasicStroke(1.0f);
		strokeFirstDayOfWeekGrid = new BasicStroke(2.0f);

		strokeHourGrid = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL, 0, new float[] { 4, 4 }, 0);

		colorFirstDayOfWeekGrid = new Color(128, 128, 128);
		colorDayGrid = new Color(196, 196, 196);
		colorHourGrid = new Color(210, 210, 210);
	}

	public static ComponentUI createUI(JComponent h) {
		return new BasicTimelineUI();
	}

	public void installUI(JComponent c) {
		timeline = (JTimeline) c;
		rendererPane = new CellRendererPane();
		timeline.add(rendererPane);

		installDefaults();
		installListeners();
		installActions();
	}

	private void moveSelection(int dx, int dy) {
		SelectionTimeline st = timeline.getSelectedTimeline();
		if (st != null) {
			int r = st.getRessource() + dy;
			if (r < 0 || r >= timeline.getRessourceCount()) {
				r = st.getRessource();
			}

			Calendar c = Calendar.getInstance();
			c.setTime(st.getDates()[0]);
			c.add(Calendar.DAY_OF_YEAR, dx);

			Calendar cMin = Calendar.getInstance();
			cMin.setTime(timeline.getDatesModel().getDateMin());

			Calendar cMax = Calendar.getInstance();
			cMax.setTime(timeline.getDatesModel().getDateMax());

			if (c.before(cMin) || c.after(cMax)) {
				c.setTime(st.getDates()[0]);
			}

			timeline.setSelectedTimeline(new SelectionTimeline(r,
					new Date[] { c.getTime() }, null));
		} else {
			timeline
					.setSelectedTimeline(new SelectionTimeline(
							0,
							new Date[] { timeline.getDatesModel().getDateMin() },
							null));
		}
		st = timeline.getSelectedTimeline();
		Date date = st.getDates()[0];
		int r = st.getRessource();
		timeline.scrollDateAndRessourceToVisible(date, r);
	}

	private void moveTaskSelection(int dx, int dy) {
		SelectionTimeline st = timeline.getSelectedTimeline();
		TaskAndResource taskAndResource = null;
		if (st != null) {
			if (st.getTasks() != null && st.getTasks().length > 0) {
				if (dy != 0) {
					if (dy > 0) {
						taskAndResource = searchTaskNextResource(st
								.getRessource(), st.getTasks()[0].getDateMin());
					} else {
						taskAndResource = searchTaskPrevResource(st
								.getRessource(), st.getTasks()[0].getDateMin());
					}
				} else {
					if (dx > 0) {
						taskAndResource = searchNextTask(st.getRessource(), st
								.getTasks()[0]);
					} else {
						taskAndResource = searchPrevTask(st.getRessource(), st
								.getTasks()[0]);
					}
				}
			} else {
				if (dy != 0) {
					if (dy > 0) {
						taskAndResource = searchTaskNextResource(st
								.getRessource(), st.getDates()[0]);
					} else {
						taskAndResource = searchTaskPrevResource(st
								.getRessource(), st.getDates()[0]);
					}
				} else {
					if (dx > 0) {
						taskAndResource = searchNextTask(st.getRessource(), st
								.getDates()[0]);
					} else {
						taskAndResource = searchPrevTask(st.getRessource(), st
								.getDates()[0]);
					}
				}
			}
		} else {
			taskAndResource = searchFirstTask();
		}
		if (taskAndResource != null) {
			timeline
					.setSelectedTimeline(new SelectionTimeline(
							taskAndResource.resource,
							new Date[] { DateTimeUtils
									.clearHourForDate(taskAndResource.task
											.getDateMin()) },
							new Task[] { taskAndResource.task }));

			st = timeline.getSelectedTimeline();
			Date date = st.getDates()[0];
			int r = st.getRessource();
			timeline.scrollDateAndRessourceToVisible(date, r);
		}
	}

	private final class TaskAndResource {

		public Task task;

		public int resource;

	}

	private TaskAndResource searchTaskPrevResource(int resource, Date date) {
		for (int r = resource - 1; r >= 0; r--) {
			List<Task> list = timeline.getModel().getTasks(
					timeline.convertRessourceIndexToModel(r),
					timeline.getDatesModel().getDateMin(),
					timeline.getDatesModel().getDateMax());
			if (list != null && list.size() > 0) {
				Collections.sort(list, taskComparator);

				TaskAndResource tr = new TaskAndResource();
				tr.resource = r;

				for (int i = list.size() - 1; i >= 0; i--) {
					Task task = list.get(i);
					if (task.isSelected()
							&& DateTimeUtils.compareToDate(date, task
									.getDateMin()) >= 0) {
						tr.task = task;
						return tr;
					}
				}

				// si pas trouver, on prend le premier
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).isSelected()) {
						tr.task = list.get(i);
						return tr;
					}
				}
			}
		}
		return null;
	}

	private TaskAndResource searchTaskNextResource(int resource, Date date) {
		for (int r = resource + 1; r < timeline.getRessourceCount(); r++) {
			List<Task> list = timeline.getModel().getTasks(
					timeline.convertRessourceIndexToModel(r),
					timeline.getDatesModel().getDateMin(),
					timeline.getDatesModel().getDateMax());
			if (list != null && list.size() > 0) {
				Collections.sort(list, taskComparator);

				TaskAndResource tr = new TaskAndResource();
				tr.resource = r;

				for (int i = list.size() - 1; i >= 0; i--) {
					Task task = list.get(i);
					if (task.isSelected()
							&& DateTimeUtils.compareToDate(date, task
									.getDateMin()) >= 0) {
						tr.task = task;
						return tr;
					}
				}

				// si pas trouver, on prend le premier
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).isSelected()) {
						tr.task = list.get(i);
						tr.resource = r;
						return tr;
					}
				}
			}
		}
		return null;
	}

	private TaskAndResource searchPrevTask(int r, Date date) {
		List<Task> list = timeline.getModel().getTasks(
				timeline.convertRessourceIndexToModel(r),
				timeline.getDatesModel().getDateMin(), date);
		if (list != null && list.size() > 0) {
			Collections.sort(list, taskComparator);

			TaskAndResource tr = new TaskAndResource();
			for (int i = list.size() - 1; i >= 0; i--) {
				if (list.get(i).isSelected()) {
					tr.task = list.get(i);
					tr.resource = r;
					return tr;
				}
			}
		}
		return null;
	}

	private TaskAndResource searchNextTask(int r, Date date) {
		List<Task> list = timeline.getModel().getTasks(
				timeline.convertRessourceIndexToModel(r), date,
				timeline.getDatesModel().getDateMax());
		if (list != null && list.size() > 0) {
			Collections.sort(list, taskComparator);

			TaskAndResource tr = new TaskAndResource();
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).isSelected()) {
					tr.task = list.get(i);
					tr.resource = r;
					return tr;
				}
			}
		}
		return null;
	}

	private TaskAndResource searchPrevTask(int r, Task task) {
		List<Task> list = timeline.getModel().getTasks(
				timeline.convertRessourceIndexToModel(r),
				timeline.getDatesModel().getDateMin(), task.getDateMin());
		if (list != null && list.size() > 1) {
			Collections.sort(list, taskComparator);

			TaskAndResource tr = new TaskAndResource();
			for (int i = list.size() - 2; i >= 0; i--) {
				if (list.get(i).isSelected()) {
					tr.task = list.get(i);
					tr.resource = r;
					return tr;
				}
			}
		}
		return null;
	}

	private TaskAndResource searchNextTask(int r, Task task) {
		List<Task> list = timeline.getModel().getTasks(
				timeline.convertRessourceIndexToModel(r), task.getDateMax(),
				timeline.getDatesModel().getDateMax());
		if (list != null && list.size() > 1) {
			Collections.sort(list, taskComparator);

			TaskAndResource tr = new TaskAndResource();
			for (int i = 1; i < list.size(); i++) {
				if (list.get(i).isSelected()) {
					tr.task = list.get(i);
					tr.resource = r;
					return tr;
				}
			}
		}
		return null;
	}

	private TaskAndResource searchFirstTask() {
		for (int r = 0; r < timeline.getRessourceCount(); r++) {
			List<Task> list = timeline.getModel().getTasks(
					timeline.convertRessourceIndexToModel(r),
					timeline.getDatesModel().getDateMin(),
					timeline.getDatesModel().getDateMax());
			if (list != null && list.size() > 0) {
				Collections.sort(list, taskComparator);

				TaskAndResource tr = new TaskAndResource();
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).isSelected()) {
						tr.task = list.get(i);
						tr.resource = r;
						return tr;
					}
				}
			}
		}
		return null;
	}

	private final class TaskComparator implements Comparator<Task> {
		@Override
		public int compare(Task t1, Task t2) {
			if (t1 != null && t2 == null) {
				return 1;
			} else if (t1 == null && t2 != null) {
				return -1;
			} else if (t1 != null && t2 != null) {
				return DateTimeUtils.compareToDate(t1.getDateMin(), t2
						.getDateMin());
			}
			return 0;
		}
	}

	private void zoomTimeline(int dz) {
		TimelineDatesModel tdm = timeline.getDatesModel();
		int w = tdm.getWidth() + dz;
		if (w >= TimelineDatesModel.MIN_WIDTH
				&& w <= TimelineDatesModel.MAX_WIDTH) {
			tdm.setWidth(w);
		} else if (w < TimelineDatesModel.MIN_WIDTH) {
			tdm.setWidth(TimelineDatesModel.MIN_WIDTH);
		} else {
			tdm.setWidth(TimelineDatesModel.MAX_WIDTH);
		}
	}

	protected void installActions() {
		timeline.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, 0),
				"doZoomPlus"); //$NON-NLS-1$
		timeline.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), "doZoomPlus"); //$NON-NLS-1$
		timeline.getActionMap().put("doZoomPlus", new ZoomPlusAction()); //$NON-NLS-1$

		timeline.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, 0), "doZoomMinus"); //$NON-NLS-1$
		timeline.getInputMap()
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0),
						"doZoomMinus"); //$NON-NLS-1$
		timeline.getActionMap().put("doZoomMinus", new ZoomMinusAction()); //$NON-NLS-1$

		// Arrows
		timeline.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
				"doUp"); //$NON-NLS-1$
		timeline.getActionMap().put("doUp", new UpAction()); //$NON-NLS-1$

		timeline.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
				"doDown"); //$NON-NLS-1$
		timeline.getActionMap().put("doDown", new DownAction()); //$NON-NLS-1$

		timeline.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
				"doLeft"); //$NON-NLS-1$
		timeline.getActionMap().put("doLeft", new LeftAction()); //$NON-NLS-1$

		timeline.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "doRight"); //$NON-NLS-1$
		timeline.getActionMap().put("doRight", new RightAction()); //$NON-NLS-1$

		// Shift+Arrows
		timeline.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_UP,
						InputEvent.SHIFT_DOWN_MASK), "doShiftUp"); //$NON-NLS-1$
		timeline.getActionMap().put("doShiftUp", new ShiftUpAction()); //$NON-NLS-1$

		timeline.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
						InputEvent.SHIFT_DOWN_MASK), "doShiftDown"); //$NON-NLS-1$
		timeline.getActionMap().put("doShiftDown", new ShiftDownAction()); //$NON-NLS-1$

		timeline.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_LEFT,
						InputEvent.SHIFT_DOWN_MASK), "doShiftLeft"); //$NON-NLS-1$
		timeline.getActionMap().put("doShiftLeft", new ShiftLeftAction()); //$NON-NLS-1$

		timeline.getInputMap().put(
				KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,
						InputEvent.SHIFT_DOWN_MASK), "doShiftRight"); //$NON-NLS-1$
		timeline.getActionMap().put("doShiftRight", new ShiftRightAction()); //$NON-NLS-1$
	}

	protected void installListeners() {
		mouseWheelListener = createMouseWheelListener();
		mouseInputListener = createMouseInputListener();
		focusListener = createFocusListener();
		keyListener = createKeyListener();

		timeline.addFocusListener(focusListener);
		timeline.addKeyListener(keyListener);
		timeline.addMouseListener(mouseInputListener);
		timeline.addMouseMotionListener(mouseInputListener);
		timeline.addMouseWheelListener(mouseWheelListener);
	}

	protected void installDefaults() {
		taskPriorityComparator = new TaskPriorityComparator();
		taskComparator = new TaskComparator();

		LookAndFeel.installColorsAndFont(timeline, "Table.background", //$NON-NLS-1$
				"Table.foreground", "Table.font"); //$NON-NLS-1$ //$NON-NLS-2$

		LookAndFeel.installProperty(timeline, "opaque", Boolean.TRUE); //$NON-NLS-1$

		Color sbg = timeline.getSelectionBackground();
		if (sbg == null || sbg instanceof UIResource) {
			timeline.setSelectionBackground(UIManager
					.getColor("Table.selectionBackground")); //$NON-NLS-1$
		}

		Color sfg = timeline.getSelectionForeground();
		if (sfg == null || sfg instanceof UIResource) {
			timeline.setSelectionForeground(UIManager
					.getColor("Table.selectionForeground")); //$NON-NLS-1$
		}

		Color gridColor = timeline.getGridColor();
		if (gridColor == null || gridColor instanceof UIResource) {
			timeline.setGridColor(UIManager.getColor("Table.gridColor")); //$NON-NLS-1$
		}

		// install the scrollpane border
		Container parent = timeline.getParent(); // should be viewport
		if (parent != null) {
			parent = parent.getParent(); // should be the scrollpane
			if (parent != null && parent instanceof JScrollPane) {
				LookAndFeel.installBorder((JScrollPane) parent,
						"Table.scrollPaneBorder"); //$NON-NLS-1$
			}
		}
	}

	public void uninstallUI(JComponent c) {
		uninstallListeners();

		timeline.remove(rendererPane);
		rendererPane = null;
		timeline = null;
	}

	protected void uninstallListeners() {
		timeline.removeFocusListener(focusListener);
		timeline.removeKeyListener(keyListener);
		timeline.removeMouseListener(mouseInputListener);
		timeline.removeMouseMotionListener(mouseInputListener);

		focusListener = null;
		keyListener = null;
		mouseInputListener = null;
	}

	protected KeyListener createKeyListener() {
		return null;
	}

	protected FocusListener createFocusListener() {
		return new FocusHandler();
	}

	protected MouseInputListener createMouseInputListener() {
		return new MouseInputHandler();
	}

	protected MouseWheelHandler createMouseWheelListener() {
		return new MouseWheelHandler();
	}

	public void paint(Graphics g, JComponent c) {
		Graphics2D g2 = (Graphics2D) g;
		Rectangle clip = g2.getClipBounds();

		g.setColor(Color.WHITE);
		g.fillRect(clip.x, clip.y, clip.width, clip.height);

		Point left = clip.getLocation();
		Point right = new Point(clip.x + clip.width - 1, clip.y + clip.height
				- 1);

		int rMin = timeline.ressourceAtPoint(left);
		int rMax = timeline.ressourceAtPoint(right);

		if (rMin == -1) {
			rMin = 0;
		}
		if (rMax == -1) {
			rMax = timeline.getRessourceCount() - 1;
		}

		paintSelectedDate(g, rMin, rMax);
		paintGrid(g, rMin, rMax);
		paintTasks(g, rMin, rMax);

		rendererPane.removeAll();
	}

	private void paintTasks(Graphics g, int rMin, int rMax) {
		JTimelineRessourceHeader header = timeline.getTimelineRessourceHeader();
		TimelineRessource draggedRessource = header.getDraggedRessource();

		Graphics2D g2 = (Graphics2D) g;
		Rectangle clip = g2.getClipBounds();

		TimelineModel model = timeline.getModel();
		TimelineRessourceModel rm = timeline.getRessourceModel();

		Point left = clip.getLocation();
		Point right = new Point(clip.x + clip.width - 1, clip.y + clip.height
				- 1);

		Date dMin = timeline.dateAtPoint(left);
		Date dMax = timeline.dateAtPoint(right);

		Rectangle cellRect = timeline.getRessourceRect(rMin);
		int y = cellRect.y;
		for (int r = rMin; r <= rMax; r++) {
			TimelineRessource aRessource = rm.getRessource(r);
			int ressourceHeight = aRessource.getHeight();
			if (aRessource != draggedRessource) {
				int index = timeline.convertRessourceIndexToModel(r);

				List<Task> tasks = model.getTasks(index, dMin, dMax);
				if (tasks != null) {
					Collections.sort(tasks, taskPriorityComparator);
					paintTasksForRessource(g, index, tasks, y,
							ressourceHeight - 1);
				}
			}
			y += ressourceHeight;
		}

		if (draggedRessource != null) {
			int r = viewIndexForRessource(draggedRessource);
			Rectangle draggedRect = timeline.getRessourceRect(r);
			draggedRect.y += header.getDraggedDistance();
			int ressourceHeight = draggedRessource.getHeight();

			List<Task> tasks = model.getTasks(draggedRessource.getModelIndex(),
					dMin, dMax);
			if (tasks != null) {
				Collections.sort(tasks, taskPriorityComparator);
				paintTasksForRessource(g, draggedRessource.getModelIndex(),
						tasks, draggedRect.y, ressourceHeight);
			}
		}
	}

	private final class TaskPriorityComparator implements Comparator<Task> {
		@Override
		public int compare(Task t1, Task t2) {
			if (t1 != null && t2 == null) {
				return 1;
			} else if (t1 == null && t2 != null) {
				return -1;
			} else if (t1 != null && t2 != null) {
				if (t1.getPriority() > t2.getPriority()) {
					return 1;
				} else if (t1.getPriority() > t2.getPriority()) {
					return -1;
				}
			}
			return 0;
		}
	}

	private void paintTasksForRessource(Graphics g, int index,
			List<Task> tasks, int y, int h) {
		for (Task task : tasks) {
			int x1 = timeline.pointAtDate(task.getDateMin());
			int x2 = timeline.pointAtDate(task.getDateMax());

			Task next = task.getLiaison();
			if (next != null) {
				paintTaskLiaison(g, task, next, y + h * 3 / 4);
			}

			paintTask(g, index, task, new Rectangle(x1, y, x2 - x1, h), task
					.getTitle());
		}
	}

	private void paintTaskLiaison(Graphics g, Task task1, Task task2, int y) {
		Task temp1, temp2;

		Calendar c1Min = Calendar.getInstance();
		c1Min.setTime(task1.getDateMin());

		Calendar c2Max = Calendar.getInstance();
		c2Max.setTime(task2.getDateMax());

		if (c1Min.after(c2Max)) {
			temp1 = task2;
			temp2 = task1;
		} else {
			temp1 = task1;
			temp2 = task2;
		}

		int x1 = timeline.pointAtDate(temp1.getDateMax());
		int x2 = timeline.pointAtDate(temp2.getDateMin());

		g.setColor(new Color(100, 100, 100));
		g.fillRect(x1 - 1, y - 4, x2 - x1 + 1, 4);
	}

	private void paintTask(Graphics g, int ressource, Task task,
			Rectangle cellRect, String text) {
		TimelineTaskRenderer taskRenderer = task.getTaskRenderer();
		if (taskRenderer == null) {
			taskRenderer = timeline.getDefaultTaskRenderer();
		}

		SelectionTimeline selectedTimeline = timeline.getSelectedTimeline();
		boolean selected = false;
		if (selectedTimeline != null) {
			Task[] tasks = selectedTimeline.getTasks();
			if (tasks != null) {
				for (Task t : tasks) {
					if (t == task) {
						selected = true;
					}
				}
			}
		}

		Component c = taskRenderer.getTimelineTaskRendererComponent(timeline,
				task, selected, false, ressource, cellRect.width,
				cellRect.height);

		int width = cellRect.width;
		int height = cellRect.height;
		if (taskRenderer.isUsePreferredSize()) {
			Dimension d = c.getPreferredSize();
			width = d.width;
			height = d.height;
		}
		if (height > cellRect.height) {
			height = cellRect.height;
		}
		int y = cellRect.y + (cellRect.height - height) / 2;
		rendererPane.paintComponent(g, c, timeline, cellRect.x, y, width,
				height, true);
	}

	private void paintSelectedDate(Graphics g, int rMin, int rMax) {
		Graphics2D g2 = (Graphics2D) g;
		TimelineDatesModel dm = timeline.getDatesModel();

		SelectionTimeline selectedTimeline = timeline.getSelectedTimeline();
		if (selectedTimeline != null) {
			int r = selectedTimeline.getRessource();
			Date[] dates = selectedTimeline.getDates();
			if (dates != null && r >= rMin && r <= rMax) {
				Rectangle selectedRect = timeline.getRessourceRect(r);

				for (Date date : dates) {
					int p = dm.getPixels(date);

					int pd = (int) (p / dm.getWidth()) * dm.getWidth();

					if (timeline.getSelectionBackground() == null) {
						g2.setColor(selectedColorDefault);
					} else {
						g2.setColor(timeline.getSelectionBackground());
					}

					g2.fillRect(pd, selectedRect.y, dm.getWidth(),
							selectedRect.height);
				}
			}
		}
	}

	private void paintGrid(Graphics g, int rMin, int rMax) {
		Graphics2D g2 = (Graphics2D) g;
		Rectangle clip = g2.getClipBounds();

		Point left = clip.getLocation();
		Point right = new Point(clip.x + clip.width - 1, clip.y + clip.height
				- 1);

		TimelineDatesModel dm = timeline.getDatesModel();

		Calendar cMin = Calendar.getInstance();
		cMin.setTime(dm.getDateInX(clip.x));

		Calendar cMax = Calendar.getInstance();
		cMax.setTime(dm.getDateInX(clip.x + clip.width));

		Calendar cCur = (Calendar) cMin.clone();
		cCur.clear();
		cCur.set(cMin.get(Calendar.YEAR), cMin.get(Calendar.MONTH), cMin
				.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

		int x = (int) (clip.x / dm.getWidth()) * dm.getWidth();
		int days = DateTimeUtils
				.getNumberOfDays(cMin.getTime(), cMax.getTime());
		double wh = dm.getWidth() / 24.0;
		for (int d = 0; d < days; d++) {
			if (cCur.get(Calendar.DAY_OF_WEEK) == cCur.getFirstDayOfWeek()) {
				g2.setPaint(colorFirstDayOfWeekGrid);
				g2.setStroke(strokeFirstDayOfWeekGrid);
			} else {
				g2.setPaint(colorDayGrid);
				g2.setStroke(strokeDayGrid);
			}
			g2.drawLine(x, clip.y, x, clip.y + clip.height);
			cCur.add(Calendar.DAY_OF_MONTH, 1);

			if (dm.getWidth() >= 400) {
				g2.setPaint(colorHourGrid);
				g2.setStroke(strokeHourGrid);
				for (int hour = 1; hour < 24; hour++) {
					int xh = x + (int) (wh * hour);
					g2.drawLine(xh, clip.y, xh, clip.y + clip.height);
				}
			}

			x += dm.getWidth();
		}

		TimelineRessourceModel rm = timeline.getRessourceModel();
		g2.setPaint(new Color(0, 0, 0, 196));
		g2.setStroke(new BasicStroke(1));

		Rectangle cellRect = timeline.getRessourceRect(rMin);
		int y = cellRect.y;
		for (int r = rMin; r <= rMax; r++) {
			int ressourceHeight = rm.getRessource(r).getHeight();

			y += ressourceHeight;
			g2.drawLine(left.x, y - 1, right.x, y - 1);
		}
	}

	private int viewIndexForRessource(TimelineRessource aRessource) {
		TimelineRessourceModel rm = timeline.getRessourceModel();
		for (int ressource = 0; ressource < rm.getRessourceCount(); ressource++) {
			if (rm.getRessource(ressource) == aRessource) {
				return ressource;
			}
		}
		return -1;
	}

	private Dimension createTimelineSize(long height) {
		TimelineDatesModel dm = timeline.getDatesModel();
		int pixels = DateTimeUtils.getNumberOfDays(dm.getDateMin(), dm
				.getDateMax())
				* dm.getWidth();

		return new Dimension(pixels, (int) height);
	}

	public Dimension getMinimumSize(JComponent c) {
		long height = 0;
		Enumeration<TimelineRessource> enumeration = timeline
				.getRessourceModel().getRessources();
		while (enumeration.hasMoreElements()) {
			TimelineRessource aRessource = enumeration.nextElement();
			height = height + aRessource.getMinHeight();
		}
		return createTimelineSize(height);
	}

	public Dimension getPreferredSize(JComponent c) {
		long height = 0;
		Enumeration<TimelineRessource> enumeration = timeline
				.getRessourceModel().getRessources();
		while (enumeration.hasMoreElements()) {
			TimelineRessource aRessource = enumeration.nextElement();
			height = height + aRessource.getPreferredHeight();
		}
		return createTimelineSize(height);
	}

	public Dimension getMaximumSize(JComponent c) {
		long height = 0;
		Enumeration<TimelineRessource> enumeration = timeline
				.getRessourceModel().getRessources();
		while (enumeration.hasMoreElements()) {
			TimelineRessource aRessource = enumeration.nextElement();
			height = height + aRessource.getMaxHeight();
		}
		return createTimelineSize(height);
	}

	private void selectedDateAndTaskForPoint(Point p, boolean add, boolean all) {
		int r = timeline.ressourceAtPoint(p);
		int index = timeline.convertRessourceIndexToModel(r);

		TimelineModel model = timeline.getModel();
		TimelineRessourceModel rm = timeline.getRessourceModel();

		// System.out.println("Avant "+p.x);
		Date date = timeline.dateAtPoint(p);
		// System.out.println("Apres "+date);

		List<Task> tasks = model.getTasks(index, date, date);
		Collections.sort(tasks, taskPriorityComparator);
		List<Task> tasks2 = new ArrayList<Task>();
		if (tasks != null) {
			for (Task t : tasks) {
				if (t.isSelected()) {
					tasks2.add(t);
				}
			}
		}

		Task task = null;
		if (!tasks2.isEmpty()) {
			task = tasks2.get(0);
		} else {
			Calendar cMin = Calendar.getInstance();
			cMin.setTime(date);
			cMin.add(Calendar.DAY_OF_YEAR, -7);

			tasks = model.getTasks(index, cMin.getTime(), date);
			Collections.sort(tasks, taskPriorityComparator);
			tasks2.clear();
			if (tasks != null) {
				for (Task t : tasks) {
					if (t.isSelected()) {
						tasks2.add(t);
					}
				}

				for (Task t : tasks2) {
					TimelineTaskRenderer taskRenderer = t.getTaskRenderer();

					if (taskRenderer != null
							&& taskRenderer.isUsePreferredSize()) {
						int x1 = timeline.pointAtDate(t.getDateMin());
						int x2 = timeline.pointAtDate(t.getDateMax());

						TimelineRessource aRessource = rm.getRessource(r);
						int ressourceHeight = aRessource.getHeight();

						Component c = taskRenderer
								.getTimelineTaskRendererComponent(timeline, t,
										false, false, index, x2 - x1,
										ressourceHeight);
						if (p.x >= x1 && p.x < x1 + c.getPreferredSize().width) {
							task = t;
							continue;
						}
					}
				}
			}
		}

		List<Date> ds = new ArrayList<Date>();
		List<Task> ts = new ArrayList<Task>();
		if (date != null) {
			if(!ds.contains(DateTimeUtils.clearHourForDate(date)))
				ds.add(DateTimeUtils.clearHourForDate(date));
		}

		if (task != null) {
			if(!ts.contains(task))
				ts.add(task);
			if(!ds.contains(DateTimeUtils.clearHourForDate(task.getDateMin())))
				ds.add(DateTimeUtils.clearHourForDate(task.getDateMin()));
			if(!ds.contains(DateTimeUtils.clearHourForDate(task.getDateMax())))
				ds.add(DateTimeUtils.clearHourForDate(task.getDateMax()));
		}

		if(all) {
			SelectionTimeline st = timeline.getSelectedTimeline();
			Date datemin = null;
			Date datemax = null;
			Date dateminS = null;
			Date datemaxS = null;
			if (task != null) {
				dateminS = task.getDateMin();
				datemaxS = task.getDateMax();
			} 
			if (date != null) {
				dateminS = (dateminS!=null && date.after(dateminS))?dateminS:date;
				datemaxS = (datemaxS!=null && date.before(datemaxS))?datemaxS:date;
			}
			if(st !=null && st.getRessource() == r) {
				if (st.getTasks() != null) {
					for (Task t : st.getTasks()) {
						datemin = (datemin!=null && (t.getDateMin()).after(datemin))?datemin:t.getDateMin();
						datemax = (datemax!=null && (t.getDateMax()).before(datemax))?datemax:t.getDateMax();
					}
				}
				if (st.getDates() != null) {
					for (Date d : st.getDates()) {
						datemin = (datemin!=null && (d.after(datemin) || DateTimeUtils.clearHourForDate(datemin).equals(DateTimeUtils.clearHourForDate(d))))?datemin:d;
						datemax = (datemax!=null && d.before(datemax))?datemax:d;
					}
				}
			}else{
				datemin = dateminS;
				datemax = model.getDateMax();
			}
			if(datemin!=null && dateminS != null && (dateminS.before(datemin) || dateminS.equals(datemin))){
				datemin = dateminS;
			}else{
				datemax = datemaxS;
			}
			if(datemin!=null && datemax!=null){
				if(!ds.contains(DateTimeUtils.clearHourForDate(datemin)))
					ds.add(DateTimeUtils.clearHourForDate(datemin));
				if(!ds.contains(DateTimeUtils.clearHourForDate(datemax)))
					ds.add(DateTimeUtils.clearHourForDate(datemax));
				List<Task> tasksToAdd = model.getTasks(index, datemin, datemax);
				for (Task t : tasksToAdd) {
					if(!ts.contains(t))
						ts.add(t);
					if(!ds.contains(DateTimeUtils.clearHourForDate(t.getDateMin())))
						ds.add(DateTimeUtils.clearHourForDate(t.getDateMin()));
					if(!ds.contains(DateTimeUtils.clearHourForDate(t.getDateMax())))
						ds.add(DateTimeUtils.clearHourForDate(t.getDateMax()));
				}
			}
		}else if (add) {
			SelectionTimeline st = timeline.getSelectedTimeline();
			if (st != null) {
				if (st.getRessource() == r) {
					if (st.getDates() != null) {
						for (Date d : st.getDates()) {
							if(!ds.contains(d))
								ds.add(d);
						}
					}
					if (st.getTasks() != null) {
						for (Task t : st.getTasks()) {
							if(!ts.contains(t))
								ts.add(t);
							if(!ds.contains(DateTimeUtils.clearHourForDate(t.getDateMin())))
								ds.add(DateTimeUtils.clearHourForDate(t.getDateMin()));
							if(!ds.contains(DateTimeUtils.clearHourForDate(t.getDateMax())))
								ds.add(DateTimeUtils.clearHourForDate(t.getDateMax()));
						}
					}
				}
			}
		}

		if (ds.size() > 0) {
			timeline.setSelectedTimeline(new SelectionTimeline(r, ds
					.toArray(new Date[ds.size()]), ts.toArray(new Task[ts
					.size()])));
		} else {
			timeline.setSelectedTimeline(null);
		}
	}

	private void selectedDateAndTaskForPoint(Point p, boolean add) {
		int r = timeline.ressourceAtPoint(p);
		int index = timeline.convertRessourceIndexToModel(r);

		TimelineModel model = timeline.getModel();
		TimelineRessourceModel rm = timeline.getRessourceModel();

		// System.out.println("Avant "+p.x);
		Date date = timeline.dateAtPoint(p);
		// System.out.println("Apres "+date);

		List<Task> tasks = model.getTasks(index, date, date);
		Collections.sort(tasks, taskPriorityComparator);
		List<Task> tasks2 = new ArrayList<Task>();
		if (tasks != null) {
			for (Task t : tasks) {
				if (t.isSelected()) {
					tasks2.add(t);
				}
			}
		}

		Task task = null;
		if (!tasks2.isEmpty()) {
			task = tasks2.get(0);
		} else {
			Calendar cMin = Calendar.getInstance();
			cMin.setTime(date);
			cMin.add(Calendar.DAY_OF_YEAR, -7);

			tasks = model.getTasks(index, cMin.getTime(), date);
			Collections.sort(tasks, taskPriorityComparator);
			tasks2.clear();
			if (tasks != null) {
				for (Task t : tasks) {
					if (t.isSelected()) {
						tasks2.add(t);
					}
				}

				for (Task t : tasks2) {
					TimelineTaskRenderer taskRenderer = t.getTaskRenderer();

					if (taskRenderer != null
							&& taskRenderer.isUsePreferredSize()) {
						int x1 = timeline.pointAtDate(t.getDateMin());
						int x2 = timeline.pointAtDate(t.getDateMax());

						TimelineRessource aRessource = rm.getRessource(r);
						int ressourceHeight = aRessource.getHeight();

						Component c = taskRenderer
								.getTimelineTaskRendererComponent(timeline, t,
										false, false, index, x2 - x1,
										ressourceHeight);
						if (p.x >= x1 && p.x < x1 + c.getPreferredSize().width) {
							task = t;
							continue;
						}
					}
				}
			}
		}

		List<Date> ds = new ArrayList<Date>();
		List<Task> ts = new ArrayList<Task>();

		if (date != null) {
			ds.add(DateTimeUtils.clearHourForDate(date));
		}

		if (task != null) {
			ts.add(task);
		}

		if (add) {
			SelectionTimeline st = timeline.getSelectedTimeline();
			if (st != null) {
				if (st.getRessource() == r) {
					if (st.getDates() != null) {
						for (Date d : st.getDates()) {
							ds.add(d);
						}
					}
					if (st.getTasks() != null) {
						for (Task t : st.getTasks()) {
							ts.add(t);
						}
					}
				}
			}
		}
		if (ds.size() > 0) {
			timeline.setSelectedTimeline(new SelectionTimeline(r, ds
					.toArray(new Date[ds.size()]), ts.toArray(new Task[ts
					.size()])));
		} else {
			timeline.setSelectedTimeline(null);
		}
	}

	private final class MouseWheelHandler implements MouseWheelListener {

		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.isShiftDown()) {
				zoomTimeline(-e.getScrollAmount() * e.getWheelRotation());
			} else {
				if (timeline.getParent() != null) {
					timeline.getParent().dispatchEvent(
							SwingUtilities.convertMouseEvent(timeline, e,
									timeline.getParent()));
				}
			}
		}
	}

	private final class MouseInputHandler extends MouseInputAdapter {

		public void mouseReleased(MouseEvent e) {
			SwingUtilities2.adjustFocus(timeline);
			if (e.getButton() == MouseEvent.BUTTON1) {
				selectedDateAndTaskForPoint(
						e.getPoint(),
						e.isControlDown()
								&& timeline.getSelectionMode() == JTimeline.SelectionMode.Multi,
						e.isShiftDown()
								&& timeline.getSelectionMode() == JTimeline.SelectionMode.Multi
					);
			}
		}
	}

	private final class FocusHandler implements FocusListener {

		public void focusGained(FocusEvent e) {
		}

		public void focusLost(FocusEvent e) {
		}
	}

	private final class UpAction extends AbstractAction {

		private static final long serialVersionUID = 2749748473590495760L;

		@Override
		public void actionPerformed(ActionEvent e) {
			moveSelection(0, -1);
		}
	}

	private final class DownAction extends AbstractAction {

		private static final long serialVersionUID = 3715444693035726684L;

		@Override
		public void actionPerformed(ActionEvent e) {
			moveSelection(0, 1);
		}
	}

	private final class LeftAction extends AbstractAction {

		private static final long serialVersionUID = 2198007363112869077L;

		@Override
		public void actionPerformed(ActionEvent e) {
			moveSelection(-1, 0);
		}
	}

	private final class RightAction extends AbstractAction {

		private static final long serialVersionUID = 4831883271975396288L;

		@Override
		public void actionPerformed(ActionEvent e) {
			moveSelection(1, 0);
		}
	}

	private final class ShiftUpAction extends AbstractAction {

		private static final long serialVersionUID = -189080950369118880L;

		@Override
		public void actionPerformed(ActionEvent e) {
			moveTaskSelection(0, -1);
		}
	}

	private final class ShiftDownAction extends AbstractAction {

		private static final long serialVersionUID = -6535720225978905056L;

		@Override
		public void actionPerformed(ActionEvent e) {
			moveTaskSelection(0, 1);
		}
	}

	private final class ShiftLeftAction extends AbstractAction {

		private static final long serialVersionUID = -609493885489382787L;

		@Override
		public void actionPerformed(ActionEvent e) {
			moveTaskSelection(-1, 0);
		}
	}

	private final class ShiftRightAction extends AbstractAction {

		private static final long serialVersionUID = -3306767936960268387L;

		@Override
		public void actionPerformed(ActionEvent e) {
			moveTaskSelection(1, 0);
		}
	}

	private final class ZoomPlusAction extends AbstractAction {

		private static final long serialVersionUID = -3479117302708049061L;

		@Override
		public void actionPerformed(ActionEvent e) {
			zoomTimeline(5);
		}
	}

	private final class ZoomMinusAction extends AbstractAction {

		private static final long serialVersionUID = 8910832951878077920L;

		@Override
		public void actionPerformed(ActionEvent e) {
			zoomTimeline(-5);
		}
	}

}
