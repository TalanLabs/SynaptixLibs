package com.synaptix.swing.plaf.basic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.JSimpleDaysTimeline;
import com.synaptix.swing.SimpleDaysTask;
import com.synaptix.swing.SimpleDaysTimelineModel;
import com.synaptix.swing.plaf.SimpleDaysTimelineCenterUI;
import com.synaptix.swing.simpledaystimeline.AbstractSimpleDaysTimelineTaskRenderer;
import com.synaptix.swing.simpledaystimeline.JSimpleDaysTimelineCenter;
import com.synaptix.swing.simpledaystimeline.JSimpleDaysTimelineResourcesHeader;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineGroupFactory;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineGroupTask;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineResource;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineResourcesModel;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineSelectionModel;
import com.synaptix.swing.simpledaystimeline.SimpleDaysTimelineTaskRenderer;

public class BasicSimpleDaysTimelineCenterUI extends SimpleDaysTimelineCenterUI {

	private static final Color daySelectedBackgroundColor = new Color(0, 255, 0, 128);

	private static final Color dayNowBackgroundColor = new Color(255, 255, 255);

	private static final Color dayBackgroundColor = new Color(239, 244, 249); // new
	// Color(223,
	// 234,
	// 244);

	private static final Color errorTasksBackgroundColor = new Color(255, 0, 0, 128);

	private static final Color dayBorderColor = new Color(221, 221, 221);

	private static final Color hourNowLineColor = new Color(231, 231, 231);

	private static final Color hourLineColor = new Color(215, 219, 224); // new
	// Color(247,
	// 247,
	// 247);

	private static final Color dayCycleBackgroundColor = new Color(129, 122, 124);

	private static final Stroke defaultStroke = new BasicStroke(1.0f);

	private static final Stroke doubleStroke = new BasicStroke(2.0f);

	private static final SimpleDaysTimelineTaskRenderer defaultTaskRenderer;

	private static final SimpleDaysTimelineTaskRenderer outlineGhostTaskRenderer;

	private static final SimpleDaysTimelineTaskRenderer fillGhostTaskRenderer;

	private static Comparator<SimpleDaysTask> simpleDaysTaskComparator = new SimpleDaysTaskComparator();

	private static final Color selectionStrokeColor = new Color(50, 64, 255, 128);

	private static final Color selectionFillColor = new Color(50, 64, 255, 128);

	private JSimpleDaysTimelineCenter simpleDaysTimelineCenter;

	private MouseInputListener mouseInputListener;

	private Point firstMouseSelectionPoint;

	private Point secondMouseSelectionPoint;

	static {
		defaultTaskRenderer = new DefaultTaskRenderer();
		outlineGhostTaskRenderer = new OutlineGhostTaskRenderer();
		fillGhostTaskRenderer = new FillGhostTaskRenderer();
	}

	public static ComponentUI createUI(JComponent h) {
		return new BasicSimpleDaysTimelineCenterUI();
	}

	public void installUI(JComponent c) {
		simpleDaysTimelineCenter = (JSimpleDaysTimelineCenter) c;

		firstMouseSelectionPoint = null;
		secondMouseSelectionPoint = null;

		installListeners();
	}

	private void installListeners() {
		mouseInputListener = new MouseInputHandler();

		simpleDaysTimelineCenter.addMouseListener(mouseInputListener);
		simpleDaysTimelineCenter.addMouseMotionListener(mouseInputListener);
	}

	public void uninstallUI(JComponent c) {
		simpleDaysTimelineCenter = null;

		uninstallListeners();
	}

	private void uninstallListeners() {
		simpleDaysTimelineCenter.removeMouseListener(mouseInputListener);
		simpleDaysTimelineCenter.removeMouseMotionListener(mouseInputListener);

		mouseInputListener = null;
	}

	public void paint(Graphics g, JComponent c) {
		Graphics2D g2 = (Graphics2D) g;

		Rectangle clip = g2.getClipBounds();
		Point left = clip.getLocation();
		Point right = new Point(clip.x + clip.width - 1, clip.y + clip.height - 1);

		JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();
		if (simpleDaysTimeline != null) {
			SimpleDaysTimelineModel model = simpleDaysTimeline.getModel();

			int rMin = simpleDaysTimeline.resourceAtPoint(left);
			int rMax = simpleDaysTimeline.resourceAtPoint(right);

			if (rMin == -1) {
				rMin = 0;
			}
			if (rMax == -1) {
				rMax = simpleDaysTimeline.getResourceCount() - 1;
			}

			paintGrid(g2, clip);

			// DayDate dayDateMin = simpleDaysTimeline.dayDateAtPoint(left);
			// if (dayDateMin.getDay() < 0) {
			DayDate dayDateMin = new DayDate();
			// }
			// if (dayDateMin.getDay() > simpleDaysTimeline.getNbDays()) {
			// dayDateMin = new DayDate(simpleDaysTimeline.getNbDays());
			// }
			// DayDate dayDateMax = simpleDaysTimeline.dayDateAtPoint(right);
			// if (dayDateMax.getDay() < 0) {
			// dayDateMax = new DayDate();
			// }
			// if (dayDateMax.getDay() > simpleDaysTimeline.getNbDays()) {
			DayDate dayDateMax = new DayDate(simpleDaysTimeline.getNbDays());
			// }

			JSimpleDaysTimelineResourcesHeader resourcesHeader = simpleDaysTimeline.getResourcesHeader();
			SimpleDaysTimelineResource draggedResource = resourcesHeader.getDraggedResource();

			for (int r = rMin; r <= rMax; r++) {
				SimpleDaysTimelineResource resource = simpleDaysTimeline.getResourcesModel().getResource(r);
				if (draggedResource != resource) {
					int modelIndex = resource.getModelIndex();
					List<SimpleDaysTask> taskList = model.getTasks(modelIndex, dayDateMin, dayDateMax);

					Rectangle cellRect = simpleDaysTimeline.getResourceRect(r);
					Rectangle rect = new Rectangle(left.x, cellRect.y, clip.width, cellRect.height);
					paintResource(g2, rect, r, false, taskList);
				}
			}

			if (draggedResource != null) {
				int modelIndex = draggedResource.getModelIndex();
				List<SimpleDaysTask> taskList = model.getTasks(modelIndex, dayDateMin, dayDateMax);

				int r = viewIndexForResource(draggedResource);
				Rectangle draggedRect = simpleDaysTimeline.getResourceRect(r);
				draggedRect.y += resourcesHeader.getDraggedDistance();

				Rectangle rect = new Rectangle(left.x, draggedRect.y, clip.width, draggedRect.height);
				paintResource(g2, rect, r, true, taskList);
			}

			SimpleDaysTask[] tasksDrop = simpleDaysTimeline.getTaskDrop();
			if (tasksDrop != null && tasksDrop.length > 0 && simpleDaysTimeline.getSimpleDaysTimelineDrop() != null
					&& simpleDaysTimeline.getDropMode() != JSimpleDaysTimeline.DropMode.NONE) {
				int resourceDrop = simpleDaysTimeline.getResourceDrop();

				Rectangle resourceRect = simpleDaysTimeline.getResourceRect(resourceDrop);
				Rectangle rect = new Rectangle(left.x, resourceRect.y, clip.width, resourceRect.height);
				paintTasksDrop(g2, rect, resourceDrop, tasksDrop);
			}

			paintInfinite(g2, clip);

			paintMouseSelection(g);
		}
	}

	private void paintMouseSelection(Graphics g) {
		if (firstMouseSelectionPoint != null && secondMouseSelectionPoint != null) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int x;
			int y;
			int w;
			int h;
			if (firstMouseSelectionPoint.x < secondMouseSelectionPoint.x) {
				x = firstMouseSelectionPoint.x;
				w = secondMouseSelectionPoint.x - firstMouseSelectionPoint.x + 1;
			} else {
				x = secondMouseSelectionPoint.x;
				w = firstMouseSelectionPoint.x - secondMouseSelectionPoint.x + 1;
			}

			if (firstMouseSelectionPoint.y < secondMouseSelectionPoint.y) {
				y = firstMouseSelectionPoint.y;
				h = secondMouseSelectionPoint.y - firstMouseSelectionPoint.y + 1;
			} else {
				y = secondMouseSelectionPoint.y;
				h = firstMouseSelectionPoint.y - secondMouseSelectionPoint.y + 1;
			}

			g2.setColor(selectionFillColor);
			g2.fillRect(x, y, w, h);

			g2.setColor(selectionStrokeColor);
			g2.drawRect(x, y, w - 1, h - 1);

			g2.dispose();
		}
	}

	private SimpleDaysTimelineTaskRenderer getSimpleDaysTimelineTaskRenderer(SimpleDaysTask task) {
		SimpleDaysTimelineTaskRenderer renderer = task.getTaskRenderer();
		if (renderer == null) {
			renderer = defaultTaskRenderer;
		}
		return renderer;
	}

	private void paintInfinite(Graphics g, Rectangle rect) {
		Graphics2D g2 = (Graphics2D) g;

		JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();
		int dayWidth = simpleDaysTimeline.getDayWidth();

		int nbDays = simpleDaysTimeline.getNbDays();

		int x1 = nbDays * dayWidth + 1;

		g2.setColor(simpleDaysTimeline.getDateCenterBackgroundImpairColor() != null ? simpleDaysTimeline.getDateCenterBackgroundImpairColor()
				: dayBackgroundColor);
		g2.fillRect(x1, rect.y, (rect.x + rect.width) - x1, rect.height);
	}

	private void paintGrid(Graphics g, Rectangle rect) {
		Graphics2D g2 = (Graphics2D) g;

		JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();
		int dayWidth = simpleDaysTimeline.getDayWidth();

		int nDayMin = rect.x / dayWidth;
		int nDayMax = (rect.x + rect.width - 1) / dayWidth;

		double width3 = (double) dayWidth / 24.0;

		for (int d = nDayMin; d <= nDayMax; d++) {
			int x = d * dayWidth;

			if (d % 2 == 0) {
				g2.setColor(simpleDaysTimeline.getDateCenterBackgroundPairColor() != null ? simpleDaysTimeline.getDateCenterBackgroundPairColor()
						: dayNowBackgroundColor);
			} else {
				g2.setColor(simpleDaysTimeline.getDateCenterBackgroundImpairColor() != null ? simpleDaysTimeline.getDateCenterBackgroundImpairColor()
						: dayBackgroundColor);
			}

			g2.fillRect(x, rect.y, dayWidth, rect.height);

			if (simpleDaysTimeline.getDayWidth() >= 400) {
				if (d % 2 == 0) {
					g2.setColor(simpleDaysTimeline.getDateCenterGridPairForegroundColor() != null ? simpleDaysTimeline
							.getDateCenterGridPairForegroundColor() : hourNowLineColor);
				} else {
					g2.setColor(simpleDaysTimeline.getDateCenterGridImpairForegroundColor() != null ? simpleDaysTimeline
							.getDateCenterGridImpairForegroundColor() : hourLineColor);
				}

				for (int i = 1; i < 24; i++) {
					int x2 = (int) ((double) i * width3 + x);
					g2.drawLine(x2, rect.y, x2, rect.y + rect.height);
				}
			}

		}

		int dayCycle = simpleDaysTimeline.getDayCycle();
		int dayStart = simpleDaysTimeline.getDayStart();
		g2.setColor(simpleDaysTimeline.getDateCenterCycleBackgoundColor() != null ? simpleDaysTimeline.getDateCenterCycleBackgoundColor()
				: dayCycleBackgroundColor);
		if (dayCycle > 0) {
			for (int d = nDayMin; d <= nDayMax; d++) {
				int x = d * dayWidth;
				if ((d + dayStart) % dayCycle == 0) {
					g2.fillRect(x + 1, rect.y, 2, rect.height);
				}
			}
		}
	}

	private void paintResource(Graphics g, Rectangle rect, int resIndexView, boolean isDrag, List<SimpleDaysTask> taskList) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setStroke(defaultStroke);

		JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();
		int dayWidth = simpleDaysTimeline.getDayWidth();

		int nDayMin = rect.x / dayWidth;
		int nDayMax = (rect.x + rect.width - 1) / dayWidth;

		double width3 = (double) dayWidth / 24.0;

		SimpleDaysTimelineResource resource = simpleDaysTimeline.getResourcesModel().getResource(resIndexView);
		SimpleDaysTimelineSelectionModel selectionModel = simpleDaysTimeline.getSelectionModel();

		for (int d = nDayMin; d <= nDayMax; d++) {
			int x = d * dayWidth;

			boolean selected = selectionModel.isSelectedDayDate(resource.getModelIndex(), new DayDate(d));
			if (selected) {
				g2.setColor(simpleDaysTimeline.getDateCenterSelectionColor() != null ? simpleDaysTimeline.getDateCenterSelectionColor()
						: daySelectedBackgroundColor);
				g2.fillRect(x, rect.y, dayWidth, rect.height);
			} else if (isDrag) {
				if (d % 2 == 0) {
					g2.setColor(simpleDaysTimeline.getDateCenterBackgroundPairColor() != null ? simpleDaysTimeline.getDateCenterBackgroundPairColor()
							: dayNowBackgroundColor);
				} else {
					g2.setColor(simpleDaysTimeline.getDateCenterBackgroundImpairColor() != null ? simpleDaysTimeline
							.getDateCenterBackgroundImpairColor() : dayBackgroundColor);
				}
				g2.fillRect(x, rect.y, dayWidth, rect.height);

				if (simpleDaysTimeline.getDayWidth() >= 400) {
					if (d % 2 == 0) {
						g2.setColor(simpleDaysTimeline.getDateCenterGridPairForegroundColor() != null ? simpleDaysTimeline
								.getDateCenterGridPairForegroundColor() : hourNowLineColor);
					} else {
						g2.setColor(simpleDaysTimeline.getDateCenterGridImpairForegroundColor() != null ? simpleDaysTimeline
								.getDateCenterGridImpairForegroundColor() : hourLineColor);
					}

					for (int i = 1; i < 24; i++) {
						int x2 = (int) ((double) i * width3 + x);
						g2.drawLine(x2, rect.y, x2, rect.y + rect.height);
					}
				}
			}

			g2.setColor(dayBorderColor);
			if (isDrag) {
				g2.drawRect(x, rect.y, dayWidth, rect.height - 1);
			} else {
				g2.drawRect(x, rect.y, dayWidth, rect.height);
			}
		}

		if (taskList != null && !taskList.isEmpty()) {
			SimpleDaysTimelineGroupFactory factory = simpleDaysTimeline.getGroupFactory();
			if (factory != null) {
				List<SimpleDaysTimelineGroupTask> groupTaskList = simpleDaysTimelineCenter.getSimpleDaysTimelineGroupTaskListList(resource
						.getModelIndex());
				if (groupTaskList != null && !groupTaskList.isEmpty()) {
					if (simpleDaysTimeline.isShowIntersection() || simpleDaysTimeline.isShowGroupIntersection()) {
						paintErrorGroupTasks(g2, rect, resIndexView, groupTaskList);
					}
					paintGroupTasks(g2, rect, resIndexView, groupTaskList);
				}
			} else {
				List<List<SimpleDaysTask>> ordreTaskList = simpleDaysTimelineCenter.computeTaskListByOrder(taskList);

				if (simpleDaysTimeline.isShowIntersection()) {
					paintErrorTasks(g2, rect, ordreTaskList, rect.y, rect.height);
				}

				if (simpleDaysTimeline.isMultiLine()) {
					paintMultiLineTasks(g2, rect, resIndexView, ordreTaskList);
				} else {
					paintOneLineTasks(g2, rect, resIndexView, ordreTaskList);
				}
			}
		}
	}

	private void paintErrorTasks(Graphics g, Rectangle rect, List<List<SimpleDaysTask>> ordreTaskList, int y, int h) {
		Graphics2D g2 = (Graphics2D) g;

		JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();

		g2.setColor(simpleDaysTimeline.getErrorIntersectionColor() != null ? simpleDaysTimeline.getErrorIntersectionColor()
				: errorTasksBackgroundColor);
		for (List<SimpleDaysTask> tasks : ordreTaskList) {
			for (int i = 0; i < tasks.size(); i++) {
				SimpleDaysTask task1 = tasks.get(i);

				int x1 = simpleDaysTimeline.pointAtDayDate(task1.getDayDateMin());
				int x2 = simpleDaysTimeline.pointAtDayDate(task1.getDayDateMax());

				for (int j = i + 1; j < tasks.size(); j++) {
					SimpleDaysTask task2 = tasks.get(j);

					int x3 = simpleDaysTimeline.pointAtDayDate(task2.getDayDateMin());
					int x4 = simpleDaysTimeline.pointAtDayDate(task2.getDayDateMax());

					if (x4 > x1 && x3 < x2) {
						Rectangle rect1 = new Rectangle(x1, y, x2 - x1, h);
						Rectangle rect2 = new Rectangle(x3, y, x4 - x3, h);
						Rectangle r = rect1.intersection(rect2);
						g2.fillRect(r.x, r.y, r.width, r.height);
					}
				}
			}
		}
	}

	private void paintErrorGroupTasks(Graphics g, Rectangle rect, int resIndexView, List<SimpleDaysTimelineGroupTask> groupTaskList) {
		Graphics2D g2 = (Graphics2D) g;

		JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();

		int resIndex = simpleDaysTimeline.convertResourceIndexToModel(resIndexView);
		int nbMaxTotal = simpleDaysTimelineCenter.getNbMaxTotal(resIndex);

		if (simpleDaysTimeline.isShowIntersection()) {
			for (SimpleDaysTimelineGroupTask groupTask : groupTaskList) {
				JSimpleDaysTimelineCenter.Position position = simpleDaysTimelineCenter.getPositionSimpleDaysTask(resIndex, groupTask);
				if (position != null) {
					int h = rect.height * position.getSize() / nbMaxTotal;
					int y = rect.y + (rect.height / nbMaxTotal) * position.getPosition();

					List<SimpleDaysTask> taskList = groupTask.getSimpleDaysTaskList();
					List<List<SimpleDaysTask>> ordreTaskList = simpleDaysTimelineCenter.computeTaskListByOrder(taskList);

					paintErrorTasks(g2, rect, ordreTaskList, y, h);
				}
			}
		}

		if (simpleDaysTimeline.isShowGroupIntersection()) {
			g2.setColor(simpleDaysTimeline.getErrorIntersectionColor() != null ? simpleDaysTimeline.getErrorIntersectionColor()
					: errorTasksBackgroundColor);
			for (int i = 0; i < groupTaskList.size(); i++) {
				SimpleDaysTimelineGroupTask task1 = groupTaskList.get(i);

				int x1 = simpleDaysTimeline.pointAtDayDate(task1.getDayDateMin());
				int x2 = simpleDaysTimeline.pointAtDayDate(task1.getDayDateMax());

				for (int j = i + 1; j < groupTaskList.size(); j++) {
					SimpleDaysTimelineGroupTask task2 = groupTaskList.get(j);

					int x3 = simpleDaysTimeline.pointAtDayDate(task2.getDayDateMin());
					int x4 = simpleDaysTimeline.pointAtDayDate(task2.getDayDateMax());

					if (x4 > x1 && x3 < x2) {
						Rectangle rect1 = new Rectangle(x1, rect.y, x2 - x1, rect.height);
						Rectangle rect2 = new Rectangle(x3, rect.y, x4 - x3, rect.height);
						Rectangle r = rect1.intersection(rect2);
						g2.fillRect(r.x, r.y, r.width, r.height);
					}
				}
			}
		}
	}

	private void paintOneLineTasks(Graphics g, Rectangle rect, int resIndex, List<List<SimpleDaysTask>> ordreTaskList) {
		Graphics2D g2 = (Graphics2D) g;

		JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();

		Rectangle clip = g2.getClipBounds();
		for (List<SimpleDaysTask> tasks : ordreTaskList) {
			for (SimpleDaysTask task : tasks) {
				int x1 = simpleDaysTimeline.pointAtDayDate(task.getDayDateMin());
				int x2 = simpleDaysTimeline.pointAtDayDate(task.getDayDateMax());

				Rectangle tRect = new Rectangle(x1, rect.y, x2 - x1, rect.height);

				if (!task.isNoClipping()) {
					g2.clipRect(tRect.x, tRect.y, tRect.width, tRect.height);
				} else {
					g2.clipRect(rect.x, tRect.y, rect.width, tRect.height);
				}

				boolean isSelected = simpleDaysTimeline.getSelectionModel().isSelectedTask(task);

				getSimpleDaysTimelineTaskRenderer(task).paintTask(g2, tRect, simpleDaysTimeline, task, isSelected, false, resIndex);

				g2.setClip(clip.x, clip.y, clip.width, clip.height);
			}
		}
	}

	private void paintMultiLineTasks(Graphics g, Rectangle rect, int resIndexView, List<List<SimpleDaysTask>> ordreTaskList) {
		Graphics2D g2 = (Graphics2D) g;

		JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();

		Rectangle clip = g2.getClipBounds();

		int resIndex = simpleDaysTimeline.convertResourceIndexToModel(resIndexView);

		int nbMaxTotal = simpleDaysTimelineCenter.getNbMaxTotal(resIndex);

		for (List<SimpleDaysTask> tasks : ordreTaskList) {
			for (int i = 0; i < tasks.size(); i++) {
				SimpleDaysTask task1 = tasks.get(i);

				int x1 = simpleDaysTimeline.pointAtDayDate(task1.getDayDateMin());
				int x2 = simpleDaysTimeline.pointAtDayDate(task1.getDayDateMax());

				JSimpleDaysTimelineCenter.Position position = simpleDaysTimelineCenter.getPositionSimpleDaysTask(resIndex, task1);
				if (position != null) {
					int h = rect.height * position.getSize() / nbMaxTotal;
					int y = rect.y + (rect.height / nbMaxTotal) * position.getPosition();

					Rectangle tRect = new Rectangle(x1, y, x2 - x1, h);

					if (!task1.isNoClipping()) {
						g2.clipRect(tRect.x, tRect.y, tRect.width, tRect.height);
					} else {
						g2.clipRect(rect.x, tRect.y, rect.width, tRect.height);
					}

					boolean isSelected = simpleDaysTimeline.getSelectionModel().isSelectedTask(task1);

					getSimpleDaysTimelineTaskRenderer(task1).paintTask(g2, tRect, simpleDaysTimeline, task1, isSelected, false, resIndexView);

					g2.setClip(clip.x, clip.y, clip.width, clip.height);
				}
			}
		}
	}

	private void paintGroupTasks(Graphics g, Rectangle rect, int resIndexView, List<SimpleDaysTimelineGroupTask> groupTaskList) {
		Graphics2D g2 = (Graphics2D) g;

		JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();

		Rectangle clip = g2.getClipBounds();

		int resIndex = simpleDaysTimeline.convertResourceIndexToModel(resIndexView);
		int nbMaxTotal = simpleDaysTimelineCenter.getNbMaxTotal(resIndex);

		for (int i = 0; i < groupTaskList.size(); i++) {
			SimpleDaysTimelineGroupTask groupTask = groupTaskList.get(i);
			if (groupTask.getSimpleDaysTaskList() != null) {
				List<SimpleDaysTask> res = new ArrayList<SimpleDaysTask>(groupTask.getSimpleDaysTaskList());
				Collections.sort(res, simpleDaysTaskComparator);

				for (SimpleDaysTask task1 : res) {
					int x1 = simpleDaysTimeline.pointAtDayDate(task1.getDayDateMin());
					int x2 = simpleDaysTimeline.pointAtDayDate(task1.getDayDateMax());

					JSimpleDaysTimelineCenter.Position position = simpleDaysTimelineCenter.getPositionSimpleDaysTask(resIndex, task1);
					if (position != null) {
						int h = rect.height * position.getSize() / nbMaxTotal;
						int y = rect.y + (rect.height / nbMaxTotal) * position.getPosition();

						Rectangle tRect = new Rectangle(x1, y, x2 - x1, h);

						if (!task1.isNoClipping()) {
							g2.clipRect(tRect.x, tRect.y, tRect.width, tRect.height);
						} else {
							g2.clipRect(rect.x, tRect.y, rect.width, tRect.height);
						}

						boolean isSelected = simpleDaysTimeline.getSelectionModel().isSelectedTask(task1);

						getSimpleDaysTimelineTaskRenderer(task1).paintTask(g2, tRect, simpleDaysTimeline, task1, isSelected, false, resIndexView);

						g2.setClip(clip.x, clip.y, clip.width, clip.height);
					}
				}
			}
		}
	}

	private void paintTasksDrop(Graphics g, Rectangle rect, int resIndex, SimpleDaysTask[] tasks) {
		Graphics2D g2 = (Graphics2D) g;

		JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();

		JSimpleDaysTimeline.DropMode dropMode = simpleDaysTimeline.getDropMode();

		Rectangle clip = g2.getClipBounds();
		for (SimpleDaysTask task : tasks) {
			int x1 = simpleDaysTimeline.pointAtDayDate(task.getDayDateMin());
			int x2 = simpleDaysTimeline.pointAtDayDate(task.getDayDateMax());

			Rectangle tRect = new Rectangle(x1, rect.y, x2 - x1, rect.height);

			switch (dropMode) {
			case OUTLINE_GHOST:
				outlineGhostTaskRenderer.paintTask(g2, tRect, simpleDaysTimeline, task, false, false, resIndex);
				break;
			case FILL_GHOST:
				fillGhostTaskRenderer.paintTask(g2, tRect, simpleDaysTimeline, task, false, false, resIndex);
				break;
			case RENDERER:
				if (!task.isNoClipping()) {
					g2.clipRect(tRect.x, tRect.y, tRect.width, tRect.height);
				} else {
					g2.clipRect(rect.x, tRect.y, rect.width, tRect.height);
				}
				getSimpleDaysTimelineTaskRenderer(task).paintTask(g2, tRect, simpleDaysTimeline, task, false, false, resIndex);

				g2.setClip(clip.x, clip.y, clip.width, clip.height);
				break;
			}
		}
	}

	private int viewIndexForResource(SimpleDaysTimelineResource aRessource) {
		JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();
		if (simpleDaysTimeline != null) {
			SimpleDaysTimelineResourcesModel strm = simpleDaysTimeline.getResourcesModel();
			for (int resIndex = 0; resIndex < strm.getResourceCount(); resIndex++) {
				if (strm.getResource(resIndex) == aRessource) {
					return resIndex;
				}
			}
		}
		return -1;
	}

	private Dimension createTimelineSize(int height) {
		int width = 100;

		JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();
		if (simpleDaysTimeline != null) {
			int dayWidth = simpleDaysTimeline.getDayWidth();
			int nb = simpleDaysTimeline.getNbDays();
			width = dayWidth * nb;
		}
		return new Dimension(width, height);
	}

	public Dimension getMinimumSize(JComponent c) {
		int height = 0;
		JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();
		if (simpleDaysTimeline != null) {
			Enumeration<SimpleDaysTimelineResource> enumeration = simpleDaysTimeline.getResourcesModel().getResources();
			while (enumeration.hasMoreElements()) {
				SimpleDaysTimelineResource aResource = enumeration.nextElement();
				height = height + aResource.getMinHeight();
			}
		}
		return createTimelineSize(height);
	}

	public Dimension getPreferredSize(JComponent c) {
		int height = 0;
		JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();
		if (simpleDaysTimeline != null) {
			Enumeration<SimpleDaysTimelineResource> enumeration = simpleDaysTimeline.getResourcesModel().getResources();
			while (enumeration.hasMoreElements()) {
				SimpleDaysTimelineResource aResource = enumeration.nextElement();
				height = height + aResource.getPreferredHeight();
			}
		}
		return createTimelineSize(height);
	}

	public Dimension getMaximumSize(JComponent c) {
		int height = 0;
		JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();
		if (simpleDaysTimeline != null) {
			Enumeration<SimpleDaysTimelineResource> enumeration = simpleDaysTimeline.getResourcesModel().getResources();
			while (enumeration.hasMoreElements()) {
				SimpleDaysTimelineResource aResource = enumeration.nextElement();
				height = height + aResource.getMaxHeight();
			}
		}
		return createTimelineSize(height);
	}

	private static final class SimpleDaysTaskComparator implements Comparator<SimpleDaysTask> {

		public int compare(SimpleDaysTask o1, SimpleDaysTask o2) {
			if (o1.getOrdre() > o2.getOrdre()) {
				return 1;
			} else if (o1.getOrdre() == o2.getOrdre()) {
				return 0;
			}
			return -1;
		}
	}

	private final class MouseInputHandler extends MouseInputAdapter implements DragRecognitionSupport.BeforeDrag {

		private boolean dragPressDidSelection;

		private List<SimpleDaysTask> findAllSelectedTaskListBy(Point point, boolean withOrdre) {
			List<SimpleDaysTask> res = null;
			JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();
			if (simpleDaysTimeline != null) {
				int viewResourceIndex = simpleDaysTimeline.resourceAtPoint(point);
				DayDate dayDate = simpleDaysTimeline.dayDateAtPoint(point);
				if (withOrdre) {
					SimpleDaysTask t = findMaxOrdreSelectedTaskBy(viewResourceIndex, dayDate, point.y);
					if (t != null) {
						res = Arrays.asList(t);
					}
				} else {
					res = findAllSelectedTaskListBy(viewResourceIndex, dayDate, false, dayDate, false, point.y, point.y);
				}
			}

			return res;
		}

		// On ne séléctionne que la tache qui a l'ordre le plus haut
		private SimpleDaysTask findMaxOrdreSelectedTaskBy(int viewResourceIndex, DayDate dayDate, int pointY) {
			SimpleDaysTask res = null;
			JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();
			if (simpleDaysTimeline != null) {
				int resourceIndex = simpleDaysTimeline.convertResourceIndexToModel(viewResourceIndex);
				if (resourceIndex != -1) {
					Rectangle rect = simpleDaysTimeline.getResourcesHeader().getHeaderRect(viewResourceIndex);
					int nbMaxTotal = simpleDaysTimelineCenter.getNbMaxTotal(resourceIndex);
					List<SimpleDaysTask> list = simpleDaysTimeline.getModel().getTasks(resourceIndex, dayDate, dayDate);
					if (list != null && !list.isEmpty()) {
						int ordreMax = Integer.MIN_VALUE;
						for (SimpleDaysTask task : list) {
							if (task.isSelectable()) {
								JSimpleDaysTimelineCenter.Position position = simpleDaysTimelineCenter.getPositionSimpleDaysTask(resourceIndex, task);
								if (position != null) {
									double ph = task.getTaskRenderer().getSelectionHeightPourcent();

									int h = rect.height * position.getSize() / nbMaxTotal;
									int y = rect.y + (rect.height / nbMaxTotal) * position.getPosition();

									int dh = (int) (h - h * ph);

									if (pointY < 0 || (pointY >= y + dh / 2 && pointY <= y + h - dh / 2)) {
										if (task.getOrdre() > ordreMax) {
											ordreMax = task.getOrdre();
											res = task;
										}
									}
								}
							}
						}
					}
				}
			}
			return res;
		}

		// On séléctionne les taches qui sont dans la plage de daydate
		private List<SimpleDaysTask> findAllSelectedTaskListBy(int viewResourceIndex, DayDate ddMin, boolean onlyIncludeMin, DayDate ddMax,
				boolean onlyIncludeMax, int pointY1, int pointY2) {
			List<SimpleDaysTask> res = new ArrayList<SimpleDaysTask>();
			JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();
			if (simpleDaysTimeline != null) {
				int resourceIndex = simpleDaysTimeline.convertResourceIndexToModel(viewResourceIndex);
				if (resourceIndex != -1) {
					Rectangle rect = simpleDaysTimeline.getResourcesHeader().getHeaderRect(viewResourceIndex);
					int nbMaxTotal = simpleDaysTimelineCenter.getNbMaxTotal(resourceIndex);
					List<SimpleDaysTask> list = simpleDaysTimeline.getModel().getTasks(resourceIndex, ddMin, ddMax);
					if (list != null && !list.isEmpty()) {
						for (SimpleDaysTask task : list) {
							if (task.isSelectable()) {
								if ((!onlyIncludeMin || !task.getDayDateMin().before(ddMin))
										&& (!onlyIncludeMax || !task.getDayDateMax().after(ddMax))) {
									JSimpleDaysTimelineCenter.Position position = simpleDaysTimelineCenter.getPositionSimpleDaysTask(resourceIndex,
											task);
									if (position != null) {
										double ph = task.getTaskRenderer().getSelectionHeightPourcent();

										int h = rect.height * position.getSize() / nbMaxTotal;
										int y = rect.y + (rect.height / nbMaxTotal) * position.getPosition();

										int dh = (int) (h - h * ph);

										if (pointY2 >= (y + dh / 2) && pointY1 <= (y + h - dh / 2)) {
											res.add(task);
										}
									}
								}
							}
						}
					}
				}
			}
			return res;
		}

		public void mousePressed(MouseEvent e) {
			JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();
			if (simpleDaysTimeline != null) {
				SimpleDaysTimelineSelectionModel selectionModel = simpleDaysTimeline.getSelectionModel();

				int viewResourceIndex = simpleDaysTimeline.resourceAtPoint(e.getPoint());
				int modelResourceIndex = simpleDaysTimeline.convertResourceIndexToModel(viewResourceIndex);
				DayDate dayDate = simpleDaysTimeline.dayDateAtPoint(e.getPoint());
				DayDate dayDate2 = new DayDate(dayDate.getDay(), 0, 0);

				// On regarde ce qu'on peut séléctionner
				List<SimpleDaysTask> selectionTaskList = findAllSelectedTaskListBy(e.getPoint(), selectionModel.getSelectionMode() != null
						&& !SimpleDaysTimelineSelectionModel.Mode.MULTIPLE_SELECTION_SANS_ORDRE.equals(selectionModel.getSelectionMode()));

				boolean grabFocus = true;

				boolean selection = selectionTaskList != null && !selectionTaskList.isEmpty();
				if (selection) {
					boolean surSelection = true;
					int c = selectionModel.getSelectionTaskCount();
					if (c != selectionTaskList.size()) {
						surSelection = false;
					} else {
						Iterator<SimpleDaysTask> it = selectionTaskList.iterator();
						while (it.hasNext() && surSelection) {
							surSelection = selectionModel.isSelectedTask(it.next());
						}
					}

					boolean adjuste = true;
					if (simpleDaysTimeline.getSimpleDaysTimelineDrag() != null) {
						if (DragRecognitionSupport.mousePressed(e)) {
							dragPressDidSelection = false;

							if (e.isControlDown()) {
								adjuste = false;
							} else if (surSelection) {
								adjuste = false;
							} else {
								grabFocus = false;

								dragPressDidSelection = true;
							}
						}
					}

					if (adjuste && (e.getButton() == MouseEvent.BUTTON1 || !surSelection)) {
						adjusteSelection(e);
					}
				} else if (e.isShiftDown() && e.getButton() == MouseEvent.BUTTON1) {
					adjusteSelection(e);
				} else if (!e.isControlDown()) {
					selectionModel.clearSelection();
					// On séléctionne au moins la date et la resource
					selectionModel.addSelectionIndexResource(modelResourceIndex, dayDate2);
				} else {
					// On séléctionne au moins la date et la resource
					selectionModel.addSelectionIndexResource(modelResourceIndex, dayDate2);
				}

				firstMouseSelectionPoint = e.getPoint();

				if (grabFocus) {
					simpleDaysTimeline.requestFocus();
				}

				simpleDaysTimelineCenter.repaint();
			}
		}

		public void mouseReleased(MouseEvent e) {
			JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();
			if (simpleDaysTimeline != null) {
				SimpleDaysTimelineSelectionModel selectionModel = simpleDaysTimeline.getSelectionModel();

				if (simpleDaysTimeline.getSimpleDaysTimelineDrag() != null) {
					MouseEvent e2 = DragRecognitionSupport.mouseReleased(e);
					if (e2 != null) {
						simpleDaysTimeline.requestFocus();
						if (!dragPressDidSelection) {
							adjusteSelection(e2);
						}
					}
				}

				if (firstMouseSelectionPoint != null && secondMouseSelectionPoint != null) {
					int x1;
					int y1;
					int x2;
					int y2;
					if (firstMouseSelectionPoint.x < secondMouseSelectionPoint.x) {
						x1 = firstMouseSelectionPoint.x;
						x2 = secondMouseSelectionPoint.x;
					} else {
						x1 = secondMouseSelectionPoint.x;
						x2 = firstMouseSelectionPoint.x;
					}

					if (firstMouseSelectionPoint.y < secondMouseSelectionPoint.y) {
						y1 = firstMouseSelectionPoint.y;
						y2 = secondMouseSelectionPoint.y;
					} else {
						y1 = secondMouseSelectionPoint.y;
						y2 = firstMouseSelectionPoint.y;
					}

					Point p1 = new Point(x1, y1);
					Point p2 = new Point(x2, y2);

					int viewResourceIndex1 = simpleDaysTimeline.resourceAtPoint(p1);
					DayDate dayDate1 = simpleDaysTimeline.dayDateAtPoint(p1);
					int viewResourceIndex2 = simpleDaysTimeline.resourceAtPoint(p2);
					DayDate dayDate2 = simpleDaysTimeline.dayDateAtPoint(p2);

					if (viewResourceIndex1 != -1 && viewResourceIndex2 != -1) {
						for (int viewResourceIndex = viewResourceIndex1; viewResourceIndex <= viewResourceIndex2; viewResourceIndex++) {
							int modelResourceIndex = simpleDaysTimeline.convertResourceIndexToModel(viewResourceIndex);

							List<SimpleDaysTask> selectionList = findAllSelectedTaskListBy(viewResourceIndex, dayDate1, false, dayDate2, false, p1.y,
									p2.y);

							if (selectionList != null && !selectionList.isEmpty()) {
								for (SimpleDaysTask t : selectionList) {
									if (!selectionModel.isSelectedTask(t)) {
										selectionModel
												.addSelectionIndexResource(modelResourceIndex, new DayDate(t.getDayDateMin().getDay(), 0, 0), t);
									}
								}
							}

							// On séléctionne les jours entre min et max
							int d1 = dayDate1.getDay();
							while (d1 <= dayDate2.getDay()) {
								selectionModel.addSelectionIndexResource(modelResourceIndex, new DayDate(d1, 0, 0));
								d1++;
							}
						}
					}

					firstMouseSelectionPoint = null;
					secondMouseSelectionPoint = null;

					simpleDaysTimelineCenter.repaint();
				}
			}
		}

		public void mouseDragged(MouseEvent e) {
			if (!DragRecognitionSupport.mouseDragged(e, this)) {
				JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();
				if (simpleDaysTimeline != null) {
					secondMouseSelectionPoint = e.getPoint();

					Rectangle intRect = simpleDaysTimeline.getInternalViewRect();
					int dy1 = secondMouseSelectionPoint.y - intRect.y;
					int dy2 = (intRect.y + intRect.height - 1) - secondMouseSelectionPoint.y;

					int dx1 = secondMouseSelectionPoint.x - intRect.x;
					int dx2 = (intRect.x + intRect.width - 1) - secondMouseSelectionPoint.x;

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

					simpleDaysTimeline.scrollRectToVisible(new Rectangle(x, y, w, h));

					simpleDaysTimelineCenter.repaint();
				}
			}
		}

		public void dragStarting(MouseEvent e) {
			JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();
			if (simpleDaysTimeline != null) {
				if (e.isControlDown()) {
					SimpleDaysTimelineSelectionModel selectionModel = simpleDaysTimeline.getSelectionModel();

					// On regarde ce qu'on peut séléctionner
					List<SimpleDaysTask> selectionList = findAllSelectedTaskListBy(e.getPoint(), selectionModel.getSelectionMode() != null
							&& !SimpleDaysTimelineSelectionModel.Mode.MULTIPLE_SELECTION_SANS_ORDRE.equals(selectionModel.getSelectionMode()));

					if (selectionList != null && !selectionList.isEmpty()) {
						int viewResourceIndex = simpleDaysTimeline.resourceAtPoint(e.getPoint());
						int modelResourceIndex = simpleDaysTimeline.convertResourceIndexToModel(viewResourceIndex);
						for (SimpleDaysTask t : selectionList) {
							if (!selectionModel.isSelectedTask(t)) {
								selectionModel.addSelectionIndexResource(modelResourceIndex, new DayDate(t.getDayDateMin().getDay(), 0, 0), t);
							}
						}
					}
				}
			}
		}

		private void adjusteSelection(MouseEvent e) {
			JSimpleDaysTimeline simpleDaysTimeline = simpleDaysTimelineCenter.getSimpleDaysTimeline();
			if (simpleDaysTimeline != null) {
				SimpleDaysTimelineSelectionModel selectionModel = simpleDaysTimeline.getSelectionModel();

				// On regarde ce qu'on peut séléctionner
				List<SimpleDaysTask> selectionList = findAllSelectedTaskListBy(e.getPoint(), selectionModel.getSelectionMode() != null
						&& !SimpleDaysTimelineSelectionModel.Mode.MULTIPLE_SELECTION_SANS_ORDRE.equals(selectionModel.getSelectionMode()));

				int viewResourceIndex = simpleDaysTimeline.resourceAtPoint(e.getPoint());
				int modelResourceIndex = simpleDaysTimeline.convertResourceIndexToModel(viewResourceIndex);
				DayDate dayDate = simpleDaysTimeline.dayDateAtPoint(e.getPoint());
				DayDate dayDate2 = new DayDate(dayDate.getDay(), 0, 0);

				if (e.isControlDown()) {
					if (selectionList != null && !selectionList.isEmpty()) {
						for (SimpleDaysTask t : selectionList) {
							if (!selectionModel.isSelectedTask(t)) {
								selectionModel.addSelectionIndexResource(modelResourceIndex, dayDate2, t);
							} else {
								selectionModel.removeSelectionIndexResource(modelResourceIndex, dayDate2, t);
							}
						}
					}
				} else if (e.isShiftDown() && selectionModel.getSelectionDayDateCount(modelResourceIndex) > 0) {
					DayDate min = dayDate;
					boolean onlyIncludeMin = false;
					DayDate max = dayDate;
					boolean onlyIncludeMax = false;

					// Si il y a une selection, on prend le premier comme repère
					if (selectionList != null && !selectionList.isEmpty()) {
						SimpleDaysTask t = selectionList.get(0);
						min = t.getDayDateMin();
						onlyIncludeMin = true;
						max = t.getDayDateMax();
						onlyIncludeMax = true;
					}

					// On cherche la derniere selection pour un point de depart
					DayDate[] dds = selectionModel.getSelectionDayDates(modelResourceIndex);
					SimpleDaysTask[] sdts = selectionModel.getSelectionTasks(modelResourceIndex);
					if (sdts != null && sdts.length > 0) {
						SimpleDaysTask last = sdts[sdts.length - 1];
						if (last.getDayDateMin().before(dayDate)) {
							min = last.getDayDateMin();
							onlyIncludeMin = true;
						} else if (last.getDayDateMax().after(dayDate)) {
							max = last.getDayDateMax();
							onlyIncludeMax = true;
						}
					} else if (dds != null && dds.length > 0) {
						DayDate c = dds[dds.length - 1];
						if (c.before(dayDate)) {
							min = c;
							onlyIncludeMin = true;
						} else if (c.after(dayDate)) {
							max = new DayDate(c.getDay(), 23, 59);
							onlyIncludeMax = true;
						}
					}

					Rectangle rect = simpleDaysTimeline.getResourceRect(viewResourceIndex);

					// On séléctionne les tasks entre min et max
					selectionList = findAllSelectedTaskListBy(viewResourceIndex, min, onlyIncludeMin, max, onlyIncludeMax, rect.y, rect.y
							+ rect.height);
					if (selectionList != null && !selectionList.isEmpty()) {
						for (SimpleDaysTask t : selectionList) {
							if (!selectionModel.isSelectedTask(t)) {
								selectionModel.addSelectionIndexResource(modelResourceIndex, dayDate2, t);
							}
						}
					}
					// On séléctionne les jours entre min et max
					int d1 = min.getDay();
					while (d1 <= max.getDay()) {
						selectionModel.addSelectionIndexResource(modelResourceIndex, new DayDate(d1, 0, 0));
						d1++;
					}
				} else {
					selectionModel.clearSelection();
					if (selectionList != null && !selectionList.isEmpty()) {
						for (SimpleDaysTask t : selectionList) {
							if (!selectionModel.isSelectedTask(t)) {
								selectionModel.addSelectionIndexResource(modelResourceIndex, dayDate2, t);
							}
						}
					}
				}
			}
		}
	}

	private static final class DefaultTaskRenderer extends AbstractSimpleDaysTimelineTaskRenderer {

		public void paintTask(Graphics g, Rectangle rect, JSimpleDaysTimeline simpleDaysTimeline, SimpleDaysTask task, boolean isSelected,
				boolean hasFocus, int ressource) {
			Graphics2D g2 = (Graphics2D) g.create();

			FontMetrics fm = g2.getFontMetrics();

			if (isSelected) {
				g2.setColor(Color.BLUE);
			} else {
				g2.setColor(Color.GREEN);
			}
			g2.fillRect(rect.x, rect.y + 5, rect.width, rect.height - 10);

			String text = task.toString();
			g2.setColor(Color.WHITE);
			g2.drawString(text == null ? "None" : text, rect.x + 2, rect.y //$NON-NLS-1$
					+ rect.height / 2 + fm.getDescent());

			g2.setColor(Color.BLACK);
			g2.drawRect(rect.x, rect.y + 5, rect.width - 1, rect.height - 10);

			g2.dispose();
		}
	}

	private static final class OutlineGhostTaskRenderer extends AbstractSimpleDaysTimelineTaskRenderer {

		private static final Color borderColor = new Color(0, 0, 0, 196);

		private static final Stroke stroke = new BasicStroke(2.0f);

		public void paintTask(Graphics g, Rectangle rect, JSimpleDaysTimeline simpleDaysTimeline, SimpleDaysTask task, boolean isSelected,
				boolean hasFocus, int ressource) {
			Graphics2D g2 = (Graphics2D) g.create();

			g2.setColor(borderColor);
			g2.setStroke(stroke);
			g2.drawRect(rect.x, rect.y + 5, rect.width - 1, rect.height - 10);

			g2.dispose();
		}
	}

	private static final class FillGhostTaskRenderer extends AbstractSimpleDaysTimelineTaskRenderer {

		private static final Color backgroundColor = new Color(0, 0, 0, 64);

		private static final Color borderColor = new Color(0, 0, 0, 128);

		public void paintTask(Graphics g, Rectangle rect, JSimpleDaysTimeline simpleDaysTimeline, SimpleDaysTask task, boolean isSelected,
				boolean hasFocus, int ressource) {
			Graphics2D g2 = (Graphics2D) g.create();

			g2.setColor(backgroundColor);
			g2.fillRect(rect.x, rect.y + 5, rect.width - 1, rect.height - 10);

			g2.setColor(borderColor);
			g2.setStroke(doubleStroke);
			g2.drawRect(rect.x, rect.y + 5, rect.width - 1, rect.height - 10);

			g2.dispose();
		}
	}
}
