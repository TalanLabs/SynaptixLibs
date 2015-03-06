package com.synaptix.swing.plaf.basic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.plaf.RoulementTimelineCenterUI;
import com.synaptix.swing.plaf.basic.DragRecognitionSupport.BeforeDrag;
import com.synaptix.swing.roulement.AbstractRoulementTimelineTaskRenderer;
import com.synaptix.swing.roulement.JRoulementTimeline;
import com.synaptix.swing.roulement.JRoulementTimelineCenter;
import com.synaptix.swing.roulement.JRoulementTimelineLeftResourcesHeader;
import com.synaptix.swing.roulement.RoulementTask;
import com.synaptix.swing.roulement.RoulementTimelineGroupFactory;
import com.synaptix.swing.roulement.RoulementTimelineGroupTask;
import com.synaptix.swing.roulement.RoulementTimelineModel;
import com.synaptix.swing.roulement.RoulementTimelineResource;
import com.synaptix.swing.roulement.RoulementTimelineResourcesModel;
import com.synaptix.swing.roulement.RoulementTimelineSelectionModel;
import com.synaptix.swing.roulement.RoulementTimelineTaskRenderer;

public class BasicRoulementTimelineCenterUI extends RoulementTimelineCenterUI {

	private static final Color daySelectedBackgroundColor = new Color(0, 255,
			0, 128);

	private static final Color dayNowBackgroundColor = new Color(255, 255, 255);

	private static final Color dayBackgroundColor = new Color(239, 244, 249); // new
	// Color(223,
	// 234,
	// 244);

	private static final Color errorTasksBackgroundColor = new Color(255, 0, 0,
			128);

	private static final Color dayBorderColor = new Color(221, 221, 221);

	private static final Color hourNowLineColor = new Color(231, 231, 231);

	private static final Color hourLineColor = new Color(215, 219, 224); // new
	// Color(247,
	// 247,
	// 247);

	private static final Color dayCycleBackgroundColor = new Color(129, 122,
			124);

	private static final Stroke defaultStroke = new BasicStroke(1.0f);

	private static final Stroke doubleStroke = new BasicStroke(2.0f);

	private static final RoulementTimelineTaskRenderer defaultTaskRenderer;

	private static final RoulementTimelineTaskRenderer outlineGhostTaskRenderer;

	private static final RoulementTimelineTaskRenderer fillGhostTaskRenderer;

	private static Comparator<RoulementTask> roulementTaskComparator = new RoulementTaskComparator();

	private JRoulementTimelineCenter roulementTimelineCenter;

	private MouseInputListener mouseInputListener;

	static {
		defaultTaskRenderer = new DefaultTaskRenderer();
		outlineGhostTaskRenderer = new OutlineGhostTaskRenderer();
		fillGhostTaskRenderer = new FillGhostTaskRenderer();
	}

	public static ComponentUI createUI(JComponent h) {
		return new BasicRoulementTimelineCenterUI();
	}

	public void installUI(JComponent c) {
		roulementTimelineCenter = (JRoulementTimelineCenter) c;

		installListeners();
	}

	private void installListeners() {
		mouseInputListener = new MouseInputHandler();

		roulementTimelineCenter.addMouseListener(mouseInputListener);
		roulementTimelineCenter.addMouseMotionListener(mouseInputListener);
	}

	public void uninstallUI(JComponent c) {
		roulementTimelineCenter = null;

		uninstallListeners();
	}

	private void uninstallListeners() {
		roulementTimelineCenter.removeMouseListener(mouseInputListener);
		roulementTimelineCenter.removeMouseMotionListener(mouseInputListener);

		mouseInputListener = null;
	}

	public void paint(Graphics g, JComponent c) {
		Graphics2D g2 = (Graphics2D) g;

		Rectangle clip = g2.getClipBounds();
		Point left = clip.getLocation();
		Point right = new Point(clip.x + clip.width - 1, clip.y + clip.height
				- 1);

		JRoulementTimeline roulementTimeline = roulementTimelineCenter
				.getRoulementTimeline();
		if (roulementTimeline != null) {
			RoulementTimelineModel model = roulementTimeline.getModel();

			int rMin = roulementTimeline.resourceAtPoint(left);
			int rMax = roulementTimeline.resourceAtPoint(right);

			if (rMin == -1) {
				rMin = 0;
			}
			if (rMax == -1) {
				rMax = roulementTimeline.getResourceCount() - 1;
			}

			paintGrid(g2, clip);

			// DayDate dayDateMin = roulementTimeline.dayDateAtPoint(left);
			// if (dayDateMin.getDay() < 0) {
			DayDate dayDateMin = new DayDate();
			// }
			// if (dayDateMin.getDay() > roulementTimeline.getNbDays()) {
			// dayDateMin = new DayDate(roulementTimeline.getNbDays());
			// }
			// DayDate dayDateMax = roulementTimeline.dayDateAtPoint(right);
			// if (dayDateMax.getDay() < 0) {
			// dayDateMax = new DayDate();
			// }
			// if (dayDateMax.getDay() > roulementTimeline.getNbDays()) {
			DayDate dayDateMax = new DayDate(roulementTimeline.getNbDays());
			// }

			JRoulementTimelineLeftResourcesHeader resourcesHeader = roulementTimeline
					.getLeftResourcesHeader();
			RoulementTimelineResource draggedResource = resourcesHeader
					.getDraggedResource();

			for (int r = rMin; r <= rMax; r++) {
				RoulementTimelineResource resource = roulementTimeline
						.getResourcesModel().getResource(r);
				if (draggedResource != resource) {
					int modelIndex = resource.getModelIndex();
					List<RoulementTask> taskList = model.getTasks(modelIndex,
							dayDateMin, dayDateMax);

					Rectangle cellRect = roulementTimeline.getResourceRect(r);
					Rectangle rect = new Rectangle(left.x, cellRect.y,
							clip.width, cellRect.height);
					paintResource(g2, rect, r, false, taskList);
				}
			}

			if (draggedResource != null) {
				int modelIndex = draggedResource.getModelIndex();
				List<RoulementTask> taskList = model.getTasks(modelIndex,
						dayDateMin, dayDateMax);

				int r = viewIndexForResource(draggedResource);
				Rectangle draggedRect = roulementTimeline.getResourceRect(r);
				draggedRect.y += resourcesHeader.getDraggedDistance();

				Rectangle rect = new Rectangle(left.x, draggedRect.y,
						clip.width, draggedRect.height);
				paintResource(g2, rect, r, true, taskList);
			}

			RoulementTask[] tasksDrop = roulementTimeline.getTaskDrop();
			if (tasksDrop != null
					&& tasksDrop.length > 0
					&& roulementTimeline.getRoulementTimelineDrop() != null
					&& roulementTimeline.getDropMode() != JRoulementTimeline.DropMode.NONE) {
				int resourceDrop = roulementTimeline.getResourceDrop();

				Rectangle resourceRect = roulementTimeline
						.getResourceRect(resourceDrop);
				Rectangle rect = new Rectangle(left.x, resourceRect.y,
						clip.width, resourceRect.height);
				paintTasksDrop(g2, rect, resourceDrop, tasksDrop);
			}

			paintInfinite(g2, clip);
		}
	}

	private RoulementTimelineTaskRenderer getRoulementTimelineTaskRenderer(
			RoulementTask task) {
		RoulementTimelineTaskRenderer renderer = task.getTaskRenderer();
		if (renderer == null) {
			renderer = defaultTaskRenderer;
		}
		return renderer;
	}

	private void paintInfinite(Graphics g, Rectangle rect) {
		Graphics2D g2 = (Graphics2D) g;

		JRoulementTimeline roulementTimeline = roulementTimelineCenter
				.getRoulementTimeline();
		int dayWidth = roulementTimeline.getDayWidth();

		int nbDays = roulementTimeline.getNbDays();

		int x1 = nbDays * dayWidth + 1;

		g2
				.setColor(roulementTimeline
						.getDateCenterBackgroundImpairColor() != null ? roulementTimeline
						.getDateCenterBackgroundImpairColor()
						: dayBackgroundColor);
		g2.fillRect(x1, rect.y, (rect.x + rect.width) - x1, rect.height);
	}

	private void paintGrid(Graphics g, Rectangle rect) {
		Graphics2D g2 = (Graphics2D) g;

		JRoulementTimeline roulementTimeline = roulementTimelineCenter
				.getRoulementTimeline();
		int dayWidth = roulementTimeline.getDayWidth();

		int nDayMin = rect.x / dayWidth;
		int nDayMax = (rect.x + rect.width - 1) / dayWidth;

		double width3 = (double) dayWidth / 24.0;

		for (int d = nDayMin; d <= nDayMax; d++) {
			int x = d * dayWidth;

			if (d % 2 == 0) {
				g2
						.setColor(roulementTimeline
								.getDateCenterBackgroundPairColor() != null ? roulementTimeline
								.getDateCenterBackgroundPairColor()
								: dayNowBackgroundColor);
			} else {
				g2
						.setColor(roulementTimeline
								.getDateCenterBackgroundImpairColor() != null ? roulementTimeline
								.getDateCenterBackgroundImpairColor()
								: dayBackgroundColor);
			}

			g2.fillRect(x, rect.y, dayWidth, rect.height);

			if (roulementTimeline.getDayWidth() >= 400) {
				if (d % 2 == 0) {
					g2
							.setColor(roulementTimeline
									.getDateCenterGridPairForegroundColor() != null ? roulementTimeline
									.getDateCenterGridPairForegroundColor()
									: hourNowLineColor);
				} else {
					g2
							.setColor(roulementTimeline
									.getDateCenterGridImpairForegroundColor() != null ? roulementTimeline
									.getDateCenterGridImpairForegroundColor()
									: hourLineColor);
				}

				for (int i = 1; i < 24; i++) {
					int x2 = (int) ((double) i * width3 + x);
					g2.drawLine(x2, rect.y, x2, rect.y + rect.height);
				}
			}

		}

		int dayCycle = roulementTimeline.getDayCycle();
		int dayStart = roulementTimeline.getDayStart();
		g2
				.setColor(roulementTimeline.getDateCenterCycleBackgoundColor() != null ? roulementTimeline
						.getDateCenterCycleBackgoundColor()
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

	private void paintResource(Graphics g, Rectangle rect, int resIndexView,
			boolean isDrag, List<RoulementTask> taskList) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setStroke(defaultStroke);

		JRoulementTimeline roulementTimeline = roulementTimelineCenter
				.getRoulementTimeline();
		int dayWidth = roulementTimeline.getDayWidth();

		int nDayMin = rect.x / dayWidth;
		int nDayMax = (rect.x + rect.width - 1) / dayWidth;

		double width3 = (double) dayWidth / 24.0;

		RoulementTimelineResource resource = roulementTimeline
				.getResourcesModel().getResource(resIndexView);
		RoulementTimelineSelectionModel selectionModel = roulementTimeline
				.getSelectionModel();

		for (int d = nDayMin; d <= nDayMax; d++) {
			int x = d * dayWidth;

			boolean selected = selectionModel.isSelectedDayDate(resource
					.getModelIndex(), new DayDate(d));
			if (selected) {
				g2
						.setColor(roulementTimeline
								.getDateCenterSelectionColor() != null ? roulementTimeline
								.getDateCenterSelectionColor()
								: daySelectedBackgroundColor);
				g2.fillRect(x, rect.y, dayWidth, rect.height);
			} else if (isDrag) {
				if (d % 2 == 0) {
					g2
							.setColor(roulementTimeline
									.getDateCenterBackgroundPairColor() != null ? roulementTimeline
									.getDateCenterBackgroundPairColor()
									: dayNowBackgroundColor);
				} else {
					g2
							.setColor(roulementTimeline
									.getDateCenterBackgroundImpairColor() != null ? roulementTimeline
									.getDateCenterBackgroundImpairColor()
									: dayBackgroundColor);
				}
				g2.fillRect(x, rect.y, dayWidth, rect.height);

				if (roulementTimeline.getDayWidth() >= 400) {
					if (d % 2 == 0) {
						g2
								.setColor(roulementTimeline
										.getDateCenterGridPairForegroundColor() != null ? roulementTimeline
										.getDateCenterGridPairForegroundColor()
										: hourNowLineColor);
					} else {
						g2
								.setColor(roulementTimeline
										.getDateCenterGridImpairForegroundColor() != null ? roulementTimeline
										.getDateCenterGridImpairForegroundColor()
										: hourLineColor);
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
			RoulementTimelineGroupFactory factory = roulementTimeline
					.getGroupFactory();
			if (factory != null) {
				List<RoulementTimelineGroupTask> groupTaskList = roulementTimelineCenter
						.getRoulementTimelineGroupTaskListList(resource
								.getModelIndex());
				if (groupTaskList != null && !groupTaskList.isEmpty()) {
					if (roulementTimeline.isShowIntersection()
							|| roulementTimeline.isShowGroupIntersection()) {
						paintErrorGroupTasks(g2, rect, resIndexView,
								groupTaskList);
					}
					paintGroupTasks(g2, rect, resIndexView, groupTaskList);
				}
			} else {
				List<List<RoulementTask>> ordreTaskList = roulementTimelineCenter
						.computeTaskListByOrder(taskList);

				if (roulementTimeline.isShowIntersection()) {
					paintErrorTasks(g2, rect, ordreTaskList, rect.y,
							rect.height);
				}

				if (roulementTimeline.isMultiLine()) {
					paintMultiLineTasks(g2, rect, resIndexView, ordreTaskList);
				} else {
					paintOneLineTasks(g2, rect, resIndexView, ordreTaskList);
				}
			}
		}
	}

	private void paintErrorTasks(Graphics g, Rectangle rect,
			List<List<RoulementTask>> ordreTaskList, int y, int h) {
		Graphics2D g2 = (Graphics2D) g;

		JRoulementTimeline roulementTimeline = roulementTimelineCenter
				.getRoulementTimeline();

		g2
				.setColor(roulementTimeline.getErrorIntersectionColor() != null ? roulementTimeline
						.getErrorIntersectionColor()
						: errorTasksBackgroundColor);
		for (List<RoulementTask> tasks : ordreTaskList) {
			for (int i = 0; i < tasks.size(); i++) {
				RoulementTask task1 = tasks.get(i);

				int x1 = roulementTimeline.pointAtDayDate(task1
						.getDayDateMin());
				int x2 = roulementTimeline.pointAtDayDate(task1
						.getDayDateMax());

				for (int j = i + 1; j < tasks.size(); j++) {
					RoulementTask task2 = tasks.get(j);

					int x3 = roulementTimeline.pointAtDayDate(task2
							.getDayDateMin());
					int x4 = roulementTimeline.pointAtDayDate(task2
							.getDayDateMax());

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

	private void paintErrorGroupTasks(Graphics g, Rectangle rect,
			int resIndexView, List<RoulementTimelineGroupTask> groupTaskList) {
		Graphics2D g2 = (Graphics2D) g;

		JRoulementTimeline roulementTimeline = roulementTimelineCenter
				.getRoulementTimeline();

		int resIndex = roulementTimeline
				.convertResourceIndexToModel(resIndexView);
		int nbMaxTotal = roulementTimelineCenter.getNbMaxTotal(resIndex);

		if (roulementTimeline.isShowIntersection()) {
			for (RoulementTimelineGroupTask groupTask : groupTaskList) {
				JRoulementTimelineCenter.Position position = roulementTimelineCenter
						.getPositionRoulementTask(resIndex, groupTask);
				if (position != null) {
					int h = rect.height * position.getSize() / nbMaxTotal;
					int y = rect.y + (rect.height / nbMaxTotal)
							* position.getPosition();

					List<RoulementTask> taskList = groupTask
							.getRoulementTaskList();
					List<List<RoulementTask>> ordreTaskList = roulementTimelineCenter
							.computeTaskListByOrder(taskList);

					paintErrorTasks(g2, rect, ordreTaskList, y, h);
				}
			}
		}

		if (roulementTimeline.isShowGroupIntersection()) {
			g2
					.setColor(roulementTimeline.getErrorIntersectionColor() != null ? roulementTimeline
							.getErrorIntersectionColor()
							: errorTasksBackgroundColor);
			for (int i = 0; i < groupTaskList.size(); i++) {
				RoulementTimelineGroupTask task1 = groupTaskList.get(i);

				int x1 = roulementTimeline.pointAtDayDate(task1
						.getDayDateMin());
				int x2 = roulementTimeline.pointAtDayDate(task1
						.getDayDateMax());

				for (int j = i + 1; j < groupTaskList.size(); j++) {
					RoulementTimelineGroupTask task2 = groupTaskList.get(j);

					int x3 = roulementTimeline.pointAtDayDate(task2
							.getDayDateMin());
					int x4 = roulementTimeline.pointAtDayDate(task2
							.getDayDateMax());

					if (x4 > x1 && x3 < x2) {
						Rectangle rect1 = new Rectangle(x1, rect.y, x2 - x1,
								rect.height);
						Rectangle rect2 = new Rectangle(x3, rect.y, x4 - x3,
								rect.height);
						Rectangle r = rect1.intersection(rect2);
						g2.fillRect(r.x, r.y, r.width, r.height);
					}
				}
			}
		}
	}

	private void paintOneLineTasks(Graphics g, Rectangle rect, int resIndex,
			List<List<RoulementTask>> ordreTaskList) {
		Graphics2D g2 = (Graphics2D) g;

		JRoulementTimeline roulementTimeline = roulementTimelineCenter
				.getRoulementTimeline();

		Rectangle clip = g2.getClipBounds();
		for (List<RoulementTask> tasks : ordreTaskList) {
			for (RoulementTask task : tasks) {
				int x1 = roulementTimeline
						.pointAtDayDate(task.getDayDateMin());
				int x2 = roulementTimeline
						.pointAtDayDate(task.getDayDateMax());

				Rectangle tRect = new Rectangle(x1, rect.y, x2 - x1,
						rect.height);

				if (!task.isNoClipping()) {
					g2.clipRect(tRect.x, tRect.y, tRect.width, tRect.height);
				} else {
					g2.clipRect(rect.x, tRect.y, rect.width, tRect.height);
				}

				boolean isSelected = roulementTimeline.getSelectionModel()
						.isSelectedTask(task);

				getRoulementTimelineTaskRenderer(task).paintTask(g2, tRect,
						roulementTimeline, task, isSelected, false, resIndex);

				g2.setClip(clip.x, clip.y, clip.width, clip.height);
			}
		}
	}

	private void paintMultiLineTasks(Graphics g, Rectangle rect,
			int resIndexView, List<List<RoulementTask>> ordreTaskList) {
		Graphics2D g2 = (Graphics2D) g;

		JRoulementTimeline roulementTimeline = roulementTimelineCenter
				.getRoulementTimeline();

		Rectangle clip = g2.getClipBounds();

		int resIndex = roulementTimeline
				.convertResourceIndexToModel(resIndexView);

		int nbMaxTotal = roulementTimelineCenter.getNbMaxTotal(resIndex);

		for (List<RoulementTask> tasks : ordreTaskList) {
			for (int i = 0; i < tasks.size(); i++) {
				RoulementTask task1 = tasks.get(i);

				int x1 = roulementTimeline.pointAtDayDate(task1
						.getDayDateMin());
				int x2 = roulementTimeline.pointAtDayDate(task1
						.getDayDateMax());

				JRoulementTimelineCenter.Position position = roulementTimelineCenter
						.getPositionRoulementTask(resIndex, task1);
				if (position != null) {
					int h = rect.height * position.getSize() / nbMaxTotal;
					int y = rect.y + (rect.height / nbMaxTotal)
							* position.getPosition();

					Rectangle tRect = new Rectangle(x1, y, x2 - x1, h);

					if (!task1.isNoClipping()) {
						g2
								.clipRect(tRect.x, tRect.y, tRect.width,
										tRect.height);
					} else {
						g2.clipRect(rect.x, tRect.y, rect.width, tRect.height);
					}

					boolean isSelected = roulementTimeline.getSelectionModel()
							.isSelectedTask(task1);

					getRoulementTimelineTaskRenderer(task1).paintTask(g2,
							tRect, roulementTimeline, task1, isSelected,
							false, resIndexView);

					g2.setClip(clip.x, clip.y, clip.width, clip.height);
				}
			}
		}
	}

	private void paintGroupTasks(Graphics g, Rectangle rect, int resIndexView,
			List<RoulementTimelineGroupTask> groupTaskList) {
		Graphics2D g2 = (Graphics2D) g;

		JRoulementTimeline roulementTimeline = roulementTimelineCenter
				.getRoulementTimeline();

		Rectangle clip = g2.getClipBounds();

		int resIndex = roulementTimeline
				.convertResourceIndexToModel(resIndexView);
		int nbMaxTotal = roulementTimelineCenter.getNbMaxTotal(resIndex);

		for (int i = 0; i < groupTaskList.size(); i++) {
			RoulementTimelineGroupTask groupTask = groupTaskList.get(i);
			if (groupTask.getRoulementTaskList() != null) {
				List<RoulementTask> res = new ArrayList<RoulementTask>(
						groupTask.getRoulementTaskList());
				Collections.sort(res, roulementTaskComparator);

				for (RoulementTask task1 : res) {
					int x1 = roulementTimeline.pointAtDayDate(task1
							.getDayDateMin());
					int x2 = roulementTimeline.pointAtDayDate(task1
							.getDayDateMax());

					JRoulementTimelineCenter.Position position = roulementTimelineCenter
							.getPositionRoulementTask(resIndex, task1);
					if (position != null) {
						int h = rect.height * position.getSize() / nbMaxTotal;
						int y = rect.y + (rect.height / nbMaxTotal)
								* position.getPosition();

						Rectangle tRect = new Rectangle(x1, y, x2 - x1, h);

						if (!task1.isNoClipping()) {
							g2.clipRect(tRect.x, tRect.y, tRect.width,
									tRect.height);
						} else {
							g2.clipRect(rect.x, tRect.y, rect.width,
									tRect.height);
						}

						boolean isSelected = roulementTimeline
								.getSelectionModel().isSelectedTask(task1);

						getRoulementTimelineTaskRenderer(task1).paintTask(g2,
								tRect, roulementTimeline, task1, isSelected,
								false, resIndexView);

						g2.setClip(clip.x, clip.y, clip.width, clip.height);
					}
				}
			}
		}
	}

	private void paintTasksDrop(Graphics g, Rectangle rect, int resIndex,
			RoulementTask[] tasks) {
		Graphics2D g2 = (Graphics2D) g;

		JRoulementTimeline roulementTimeline = roulementTimelineCenter
				.getRoulementTimeline();

		JRoulementTimeline.DropMode dropMode = roulementTimeline
				.getDropMode();

		Rectangle clip = g2.getClipBounds();
		for (RoulementTask task : tasks) {
			int x1 = roulementTimeline.pointAtDayDate(task.getDayDateMin());
			int x2 = roulementTimeline.pointAtDayDate(task.getDayDateMax());

			Rectangle tRect = new Rectangle(x1, rect.y, x2 - x1, rect.height);

			switch (dropMode) {
			case OUTLINE_GHOST:
				outlineGhostTaskRenderer.paintTask(g2, tRect,
						roulementTimeline, task, false, false, resIndex);
				break;
			case FILL_GHOST:
				fillGhostTaskRenderer.paintTask(g2, tRect, roulementTimeline,
						task, false, false, resIndex);
				break;
			case RENDERER:
				if (!task.isNoClipping()) {
					g2.clipRect(tRect.x, tRect.y, tRect.width, tRect.height);
				} else {
					g2.clipRect(rect.x, tRect.y, rect.width, tRect.height);
				}
				getRoulementTimelineTaskRenderer(task).paintTask(g2, tRect,
						roulementTimeline, task, false, false, resIndex);

				g2.setClip(clip.x, clip.y, clip.width, clip.height);
				break;
			}
		}
	}

	private int viewIndexForResource(RoulementTimelineResource aRessource) {
		JRoulementTimeline roulementTimeline = roulementTimelineCenter
				.getRoulementTimeline();
		if (roulementTimeline != null) {
			RoulementTimelineResourcesModel strm = roulementTimeline
					.getResourcesModel();
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

		JRoulementTimeline roulementTimeline = roulementTimelineCenter
				.getRoulementTimeline();
		if (roulementTimeline != null) {
			int dayWidth = roulementTimeline.getDayWidth();
			int nb = roulementTimeline.getNbDays();
			width = dayWidth * nb;
		}
		return new Dimension(width, height);
	}

	public Dimension getMinimumSize(JComponent c) {
		int height = 0;
		JRoulementTimeline roulementTimeline = roulementTimelineCenter
				.getRoulementTimeline();
		if (roulementTimeline != null) {
			Enumeration<RoulementTimelineResource> enumeration = roulementTimeline
					.getResourcesModel().getResources();
			while (enumeration.hasMoreElements()) {
				RoulementTimelineResource aResource = enumeration
						.nextElement();
				height = height + aResource.getMinHeight();
			}
		}
		return createTimelineSize(height);
	}

	public Dimension getPreferredSize(JComponent c) {
		int height = 0;
		JRoulementTimeline roulementTimeline = roulementTimelineCenter
				.getRoulementTimeline();
		if (roulementTimeline != null) {
			Enumeration<RoulementTimelineResource> enumeration = roulementTimeline
					.getResourcesModel().getResources();
			while (enumeration.hasMoreElements()) {
				RoulementTimelineResource aResource = enumeration
						.nextElement();
				height = height + aResource.getPreferredHeight();
			}
		}
		return createTimelineSize(height);
	}

	public Dimension getMaximumSize(JComponent c) {
		int height = 0;
		JRoulementTimeline roulementTimeline = roulementTimelineCenter
				.getRoulementTimeline();
		if (roulementTimeline != null) {
			Enumeration<RoulementTimelineResource> enumeration = roulementTimeline
					.getResourcesModel().getResources();
			while (enumeration.hasMoreElements()) {
				RoulementTimelineResource aResource = enumeration
						.nextElement();
				height = height + aResource.getMaxHeight();
			}
		}
		return createTimelineSize(height);
	}

	private static final class RoulementTaskComparator implements
			Comparator<RoulementTask> {

		public int compare(RoulementTask o1, RoulementTask o2) {
			if (o1.getOrdre() > o2.getOrdre()) {
				return 1;
			} else if (o1.getOrdre() == o2.getOrdre()) {
				return 0;
			}
			return -1;
		}
	}

	private final class MouseInputHandler extends MouseInputAdapter implements
			BeforeDrag {

		private RoulementTask findAllSelectedTaskListBy(
				JRoulementTimeline roulementTimeline, Point p,
				List<List<RoulementTask>> ordreTaskList, Rectangle rect,
				int resIndex) {
			RoulementTask simpleTask = null;
			List<RoulementTask> selectTasks = new ArrayList<RoulementTask>();

			if (ordreTaskList != null && !ordreTaskList.isEmpty()) {
				for (List<RoulementTask> taskList : ordreTaskList) {
					int nbMaxTotal = roulementTimelineCenter
							.getNbMaxTotal(resIndex);

					for (int i = 0; i < taskList.size(); i++) {
						RoulementTask task1 = taskList.get(i);
						if (task1.isSelectable()) {
							int x1 = roulementTimeline.pointAtDayDate(task1
									.getDayDateMin());
							int x2 = roulementTimeline.pointAtDayDate(task1
									.getDayDateMax());

							JRoulementTimelineCenter.Position position = roulementTimelineCenter
									.getPositionRoulementTask(resIndex, task1);
							if (position != null) {
								double ph = task1.getTaskRenderer()
										.getSelectionHeightPourcent();

								double h = rect.height * position.getSize()
										/ nbMaxTotal;
								int y = rect.y + (rect.height / nbMaxTotal)
										* position.getPosition();

								int dh = (int) (h - h * ph);

								Rectangle tRect = new Rectangle(x1, y + dh / 2,
										x2 - x1, (int) h - dh);

								if (tRect.contains(p)) {
									selectTasks.add(task1);
								}
							}
						}
					}
				}
			}

			if (!selectTasks.isEmpty()) {
				Collections.sort(selectTasks, roulementTaskComparator);
				simpleTask = selectTasks.get(selectTasks.size() - 1);
			}
			return simpleTask;
		}

		private RoulementTask findAllSelectedGroupTaskListBy(
				JRoulementTimeline roulementTimeline, Point p,
				List<RoulementTimelineGroupTask> groupTaskList,
				Rectangle rect, int resIndex) {
			RoulementTask simpleTask = null;
			List<RoulementTask> selectTaskList = new ArrayList<RoulementTask>();

			int nbMaxTotal = roulementTimelineCenter.getNbMaxTotal(resIndex);

			for (int i = 0; i < groupTaskList.size(); i++) {
				RoulementTimelineGroupTask groupTask = groupTaskList.get(i);

				if (groupTask.getRoulementTaskList() != null) {
					List<RoulementTask> res = new ArrayList<RoulementTask>(
							groupTask.getRoulementTaskList());
					Collections.sort(res, roulementTaskComparator);

					for (RoulementTask task1 : res) {
						if (task1.isSelectable()) {
							int x1 = roulementTimeline.pointAtDayDate(task1
									.getDayDateMin());
							int x2 = roulementTimeline.pointAtDayDate(task1
									.getDayDateMax());

							JRoulementTimelineCenter.Position position = roulementTimelineCenter
									.getPositionRoulementTask(resIndex, task1);
							if (position != null) {
								double ph = task1.getTaskRenderer()
										.getSelectionHeightPourcent();

								double h = rect.height * position.getSize()
										/ nbMaxTotal;
								int y = rect.y + (rect.height / nbMaxTotal)
										* position.getPosition();

								int dh = (int) (h - h * ph);

								Rectangle tRect = new Rectangle(x1, y + dh / 2,
										x2 - x1, (int) h - dh);

								if (tRect.contains(p)) {
									selectTaskList.add(task1);
								}
							}
						}
					}
				}
			}

			if (!selectTaskList.isEmpty()) {
				Collections.sort(selectTaskList, roulementTaskComparator);
				simpleTask = selectTaskList.get(selectTaskList.size() - 1);
			}
			return simpleTask;
		}

		private List<RoulementTask> findAllSelectedTaskListBy(
				JRoulementTimeline roulementTimeline, int resource,
				RoulementTask task, DayDate dayDate) {
			List<RoulementTask> res = new ArrayList<RoulementTask>();
			RoulementTimelineModel model = roulementTimeline.getModel();

			List<RoulementTask> taskList = null;
			if (task.getDayDateMax().before(dayDate)) {
				DayDate d = new DayDate(dayDate.getDay() + 1, 0, 0);
				taskList = model.getTasks(resource, task.getDayDateMax(), d);
			} else if (task.getDayDateMin().after(dayDate)) {
				DayDate d = new DayDate(dayDate.getDay(), 0, 0);
				taskList = model.getTasks(resource, d, task.getDayDateMin());
			}

			if (taskList != null) {
				for (RoulementTask t : taskList) {
					if (t.getOrdre() == task.getOrdre()) {
						res.add(t);
					}
				}
			}

			return res;
		}

		private List<RoulementTask> findAllSelectedTaskListBy(
				JRoulementTimeline roulementTimeline, int resource,
				DayDate dayDateA, DayDate dayDate) {
			List<RoulementTask> res = new ArrayList<RoulementTask>();
			RoulementTimelineModel model = roulementTimeline.getModel();

			List<RoulementTask> taskList = null;
			if (dayDateA.before(dayDate)) {
				DayDate d = new DayDate(dayDate.getDay(), 23, 59);
				DayDate d2 = new DayDate(dayDateA.getDay(), 0, 0);
				taskList = model.getTasks(resource, d2, d);
			} else if (dayDateA.after(dayDate)) {
				DayDate d = new DayDate(dayDate.getDay(), 0, 0);
				DayDate d2 = new DayDate(dayDateA.getDay(), 23, 59);
				taskList = model.getTasks(resource, d, d2);
			}

			if (taskList != null) {
				for (RoulementTask t : taskList) {
					res.add(t);
				}
			}

			return res;
		}

		private void selectedResourceAndTask(MouseEvent e) {
			JRoulementTimeline roulementTimeline = roulementTimelineCenter
					.getRoulementTimeline();
			if (roulementTimeline != null) {
				RoulementTimelineModel model = roulementTimeline.getModel();
				DayDate dayDateMin = new DayDate();
				DayDate dayDateMax = new DayDate(roulementTimeline.getNbDays());

				RoulementTimelineSelectionModel selectionModel = roulementTimeline
						.getSelectionModel();

				Point p = e.getPoint();
				int resourceIndex = roulementTimeline.resourceAtPoint(p);
				DayDate dayDate = roulementTimeline.dayDateAtPoint(p);
				if (resourceIndex != -1) {
					RoulementTimelineResource resource = roulementTimeline
							.getResourcesModel().getResource(resourceIndex);
					if (e.isShiftDown()
							&& selectionModel.getSelectionTaskCount(resource
									.getModelIndex()) == 1) {
						RoulementTask[] tasks = selectionModel
								.getSelectionTasks(resource.getModelIndex());

						List<RoulementTask> taskList = findAllSelectedTaskListBy(
								roulementTimeline, resource.getModelIndex(),
								tasks[0], dayDate);
						for (RoulementTask t : taskList) {
							if (!selectionModel.isSelectedTask(t)) {
								selectionModel.addSelectionIndexResource(
										resource.getModelIndex(), t
												.getDayDateMin(), t);
							}
						}
					} else if (e.isShiftDown()
							&& selectionModel.getSelectionDayDateCount(resource
									.getModelIndex()) == 1) {
						DayDate[] dds = selectionModel
								.getSelectionDayDates(resource.getModelIndex());

						List<RoulementTask> taskList = findAllSelectedTaskListBy(
								roulementTimeline, resource.getModelIndex(),
								dds[0], dayDate);
						for (RoulementTask t : taskList) {
							if (!selectionModel.isSelectedTask(t)) {
								selectionModel.addSelectionIndexResource(
										resource.getModelIndex(), t
												.getDayDateMin(), t);
							}
						}
					} else {
						List<RoulementTask> taskList = model.getTasks(resource
								.getModelIndex(), dayDateMin, dayDateMax);

						RoulementTask selectTask = null;

						RoulementTimelineGroupFactory factory = roulementTimelineCenter
								.getGroupFactory();
						if (factory != null) {
							List<RoulementTimelineGroupTask> groupTaskList = roulementTimelineCenter
									.getRoulementTimelineGroupTaskListList(resource
											.getModelIndex());
							if (groupTaskList != null
									&& !groupTaskList.isEmpty()) {
								selectTask = findAllSelectedGroupTaskListBy(
										roulementTimeline,
										p,
										groupTaskList,
										roulementTimeline
												.getResourceRect(resourceIndex),
										resource.getModelIndex());
							}
						} else {
							List<List<RoulementTask>> ordreTaskList = roulementTimelineCenter
									.computeTaskListByOrder(taskList);

							selectTask = findAllSelectedTaskListBy(
									roulementTimeline, p, ordreTaskList,
									roulementTimeline
											.getResourceRect(resourceIndex),
									resource.getModelIndex());
						}

						if (!((e.getButton() == MouseEvent.BUTTON3 || e
								.isPopupTrigger())
								&& ((selectTask != null && selectionModel
										.isSelectedTask(selectTask))) || (selectTask == null && selectionModel
								.isSelectedDayDate(resource.getModelIndex(),
										new DayDate(dayDate.getDay()))))) {
							if (e.isControlDown()) {
								DayDate dayDate2 = new DayDate(dayDate.getDay());
								if (selectTask == null) {
									if (selectionModel.isSelectedDayDate(
											resource.getModelIndex(), dayDate2)) {
										selectionModel
												.removeSelectionIndexResource(
														resource
																.getModelIndex(),
														dayDate2);
									} else {
										selectionModel
												.addSelectionIndexResource(
														resource
																.getModelIndex(),
														dayDate2);
									}
								} else {
									if (selectionModel.isSelectedTasks(resource
											.getModelIndex(), selectTask)) {
										selectionModel
												.removeSelectionIndexResource(
														resource
																.getModelIndex(),
														dayDate2, selectTask);
									} else {
										selectionModel
												.addSelectionIndexResource(
														resource
																.getModelIndex(),
														dayDate2, selectTask);
									}
								}
							} else {
								selectionModel.setSelectionIndexResource(
										resource.getModelIndex(), new DayDate(
												dayDate.getDay()), selectTask);
							}
						}
					}
				} else {
					selectionModel.clearSelection();
				}
			}
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			JRoulementTimeline roulementTimeline = roulementTimelineCenter
					.getRoulementTimeline();
			if (roulementTimeline != null) {
				roulementTimeline.requestFocus();
				if (roulementTimeline.getRoulementTimelineDrag() != null) {
					DragRecognitionSupport.mousePressed(e);
				}
			}
		}

		public void dragStarting(MouseEvent me) {
		}

		public void mouseDragged(MouseEvent e) {
			JRoulementTimeline roulementTimeline = roulementTimelineCenter
					.getRoulementTimeline();
			if (roulementTimeline != null
					&& roulementTimeline.getRoulementTimelineDrag() != null) {
				DragRecognitionSupport.mouseDragged(e, this);
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			JRoulementTimeline roulementTimeline = roulementTimelineCenter
					.getRoulementTimeline();
			if (roulementTimeline != null) {
				selectedResourceAndTask(e);
				if (roulementTimeline.getRoulementTimelineDrag() != null) {
					DragRecognitionSupport.mousePressed(e);
				}
			}
		}
	}

	private static final class DefaultTaskRenderer extends
			AbstractRoulementTimelineTaskRenderer {

		public void paintTask(Graphics g, Rectangle rect,
				JRoulementTimeline roulementTimeline, RoulementTask task,
				boolean isSelected, boolean hasFocus, int ressource) {
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

	private static final class OutlineGhostTaskRenderer extends
			AbstractRoulementTimelineTaskRenderer {

		private static final Color borderColor = new Color(0, 0, 0, 196);

		private static final Stroke stroke = new BasicStroke(2.0f);

		public void paintTask(Graphics g, Rectangle rect,
				JRoulementTimeline roulementTimeline, RoulementTask task,
				boolean isSelected, boolean hasFocus, int ressource) {
			Graphics2D g2 = (Graphics2D) g.create();

			g2.setColor(borderColor);
			g2.setStroke(stroke);
			g2.drawRect(rect.x, rect.y + 5, rect.width - 1, rect.height - 10);

			g2.dispose();
		}
	}

	private static final class FillGhostTaskRenderer extends
			AbstractRoulementTimelineTaskRenderer {

		private static final Color backgroundColor = new Color(0, 0, 0, 64);

		private static final Color borderColor = new Color(0, 0, 0, 128);

		public void paintTask(Graphics g, Rectangle rect,
				JRoulementTimeline roulementTimeline, RoulementTask task,
				boolean isSelected, boolean hasFocus, int ressource) {
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
