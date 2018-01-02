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
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.ComponentUI;

import com.synaptix.swing.JSimpleTimeline;
import com.synaptix.swing.SimpleTask;
import com.synaptix.swing.SimpleTimelineModel;
import com.synaptix.swing.plaf.SimpleTimelineCenterUI;
import com.synaptix.swing.plaf.basic.DragRecognitionSupport.BeforeDrag;
import com.synaptix.swing.simpletimeline.JSimpleTimelineCenter;
import com.synaptix.swing.simpletimeline.JSimpleTimelineResourcesHeader;
import com.synaptix.swing.simpletimeline.SimpleTimelineResource;
import com.synaptix.swing.simpletimeline.SimpleTimelineResourcesModel;
import com.synaptix.swing.simpletimeline.SimpleTimelineSelectionModel;
import com.synaptix.swing.simpletimeline.SimpleTimelineTaskRenderer;
import com.synaptix.swing.utils.DateTimeUtils;

public class BasicSimpleTimelineCenterUI extends SimpleTimelineCenterUI {

	private static final Color dayNowBackgroundColor = new Color(0xFFFFFF);

	private static final Color dayBackgroundColor = new Color(0xdfeaf4);

	private static final Color dayBorderColor = new Color(0xDDDDDD);

	private static final Color hourNowLineColor = new Color(0xE7E7E7);
	
	private static final Color hourLineColor = new Color(0xF7F7F7);

	private static final Color errorTasksBackgroundColor = new Color(255, 0, 0,
			128);

	private static final Stroke defaultStroke = new BasicStroke(1.0f);

	private static final Stroke doubleStroke = new BasicStroke(2.0f);

	private static final SimpleTimelineTaskRenderer defaultTaskRenderer;

	private static final SimpleTimelineTaskRenderer outlineGhostTaskRenderer;

	private static final SimpleTimelineTaskRenderer fillGhostTaskRenderer;

	private JSimpleTimelineCenter simpleTimelineCenter;

	private MouseInputListener mouseInputListener;

	static {
		defaultTaskRenderer = new DefaultTaskRenderer();
		outlineGhostTaskRenderer = new OutlineGhostTaskRenderer();
		fillGhostTaskRenderer = new FillGhostTaskRenderer();
	}

	public static ComponentUI createUI(JComponent h) {
		return new BasicSimpleTimelineCenterUI();
	}

	public void installUI(JComponent c) {
		simpleTimelineCenter = (JSimpleTimelineCenter) c;

		installListeners();
	}

	private void installListeners() {
		mouseInputListener = new MouseInputHandler();

		simpleTimelineCenter.addMouseListener(mouseInputListener);
		simpleTimelineCenter.addMouseMotionListener(mouseInputListener);
	}

	public void uninstallUI(JComponent c) {
		simpleTimelineCenter = null;

		uninstallListeners();
	}

	private void uninstallListeners() {
		simpleTimelineCenter.removeMouseListener(mouseInputListener);
		simpleTimelineCenter.removeMouseMotionListener(mouseInputListener);

		mouseInputListener = null;
	}

	public void paint(Graphics g, JComponent c) {
		Graphics2D g2 = (Graphics2D) g;
		Rectangle clip = g2.getClipBounds();

		Point left = clip.getLocation();
		Point right = new Point(clip.x + clip.width - 1, clip.y + clip.height
				- 1);

		JSimpleTimeline simpleTimeline = simpleTimelineCenter
				.getSimpleTimeline();
		if (simpleTimeline != null) {
			SimpleTimelineModel model = simpleTimeline.getModel();

			int rMin = simpleTimeline.resourceAtPoint(left);
			int rMax = simpleTimeline.resourceAtPoint(right);

			if (rMin == -1) {
				rMin = 0;
			}
			if (rMax == -1) {
				rMax = simpleTimeline.getResourceCount() - 1;
			}

			paintGrid(g2, clip);

			Date dateMin = simpleTimeline.getDateMin();
			Date dateMax = simpleTimeline.getDateMax();

			JSimpleTimelineResourcesHeader resourcesHeader = simpleTimeline
					.getResourcesHeader();
			SimpleTimelineResource draggedResource = resourcesHeader
					.getDraggedResource();

			for (int r = rMin; r <= rMax; r++) {
				SimpleTimelineResource resource = simpleTimeline
						.getResourcesModel().getResource(r);
				if (draggedResource != resource) {
					int modelIndex = resource.getModelIndex();
					List<SimpleTask> tasks = model.getTasks(modelIndex,
							dateMin, dateMax);

					Rectangle cellRect = simpleTimeline.getResourceRect(r);
					Rectangle rect = new Rectangle(left.x, cellRect.y,
							clip.width, cellRect.height);
					paintResource(g2, rect, r, false, tasks);
				}
			}

			if (draggedResource != null) {
				int modelIndex = draggedResource.getModelIndex();
				List<SimpleTask> tasks = model.getTasks(modelIndex, dateMin,
						dateMax);

				int r = viewIndexForResource(draggedResource);
				Rectangle draggedRect = simpleTimeline.getResourceRect(r);
				draggedRect.y += resourcesHeader.getDraggedDistance();

				Rectangle rect = new Rectangle(left.x, draggedRect.y,
						clip.width, draggedRect.height);
				paintResource(g2, rect, r, true, tasks);
			}

			SimpleTask[] tasksDrop = simpleTimeline.getTaskDrop();
			if (tasksDrop != null
					&& tasksDrop.length > 0
					&& simpleTimeline.getSimpleTimelineDrop() != null
					&& simpleTimeline.getDropMode() != JSimpleTimeline.DropMode.NONE) {
				int resourceDrop = simpleTimeline.getResourceDrop();

				Rectangle resourceRect = simpleTimeline
						.getResourceRect(resourceDrop);
				Rectangle rect = new Rectangle(left.x, resourceRect.y,
						clip.width, resourceRect.height);
				paintTasksDrop(g2, rect, resourceDrop, tasksDrop);
			}

			paintInfinite(g2, clip);
		}
	}

	private SimpleTimelineTaskRenderer getSimpleTimelineTaskRenderer(
			SimpleTask task) {
		SimpleTimelineTaskRenderer renderer = task.getTaskRenderer();
		if (renderer == null) {
			renderer = defaultTaskRenderer;
		}
		return renderer;
	}

	private void paintInfinite(Graphics g, Rectangle rect) {
		Graphics2D g2 = (Graphics2D) g;

		JSimpleTimeline simpleTimeline = simpleTimelineCenter
				.getSimpleTimeline();
		int dayWidth = simpleTimeline.getDayWidth();

		int nbDays = simpleTimeline.getNbBeforeDays()
				+ simpleTimeline.getNbAfterDays() + 1;

		int x1 = nbDays * dayWidth + 1;

		g2.setColor(dayBackgroundColor);
		g2.fillRect(x1, rect.y, (rect.x + rect.width) - x1, rect.height);
	}

	private void paintGrid(Graphics g, Rectangle rect) {
		Graphics2D g2 = (Graphics2D) g;

		JSimpleTimeline simpleTimeline = simpleTimelineCenter
				.getSimpleTimeline();
		int dayWidth = simpleTimeline.getDayWidth();

		int nDayMin = rect.x / dayWidth;
		int nDayMax = (rect.x + rect.width - 1) / dayWidth;

		double width3 = (double) dayWidth / 24.0;

		for (int d = nDayMin; d <= nDayMax; d++) {
			int x = d * dayWidth;

			if (d == simpleTimeline.getNbBeforeDays()) {
				g2.setColor(dayNowBackgroundColor);
			} else {
				g2.setColor(dayBackgroundColor);
			}

			g2.fillRect(x, rect.y, dayWidth, rect.height);

			if (simpleTimeline.getDayWidth() >= 400) {
				if (d == simpleTimeline.getNbBeforeDays()) {
					g2.setColor(hourNowLineColor);
				} else {
					g2.setColor(hourLineColor);
				}
				// g2.setStroke(hourStroke);

				for (int i = 1; i < 24; i++) {
					int x2 = (int) ((double) i * width3 + x);
					g2.drawLine(x2, rect.y, x2, rect.y + rect.height);
				}

				// g2.setStroke(defaultStroke);
			}
		}
	}

	private void paintResource(Graphics g, Rectangle rect, int resIndex,
			boolean isDrag, List<SimpleTask> tasks) {
		Graphics2D g2 = (Graphics2D) g;

		g2.setStroke(defaultStroke);

		JSimpleTimeline simpleTimeline = simpleTimelineCenter
				.getSimpleTimeline();
		int dayWidth = simpleTimeline.getDayWidth();

		int nDayMin = rect.x / dayWidth;
		int nDayMax = (rect.x + rect.width - 1) / dayWidth;

		double width3 = (double) dayWidth / 24.0;

		for (int d = nDayMin; d <= nDayMax; d++) {
			int x = d * dayWidth;

			if (isDrag) {
				if (d == simpleTimeline.getNbBeforeDays()) {
					g2.setColor(dayNowBackgroundColor);
				} else {
					g2.setColor(dayBackgroundColor);
				}
				g2.fillRect(x, rect.y, dayWidth, rect.height);

				if (simpleTimeline.getDayWidth() >= 400) {
					if (d == simpleTimeline.getNbBeforeDays()) {
						g2.setColor(hourNowLineColor);
					} else {
						g2.setColor(hourLineColor);
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

		if (tasks != null) {
			paintErrorTasks(g2, rect, tasks);
			paintTasks(g2, rect, resIndex, tasks);
		}
	}

	private void paintErrorTasks(Graphics g, Rectangle rect,
			List<SimpleTask> tasks) {
		Graphics2D g2 = (Graphics2D) g;

		JSimpleTimeline simpleTimeline = simpleTimelineCenter
				.getSimpleTimeline();

		g2.setColor(errorTasksBackgroundColor);
		for (int i = 0; i < tasks.size(); i++) {
			SimpleTask task1 = tasks.get(i);
			if (task1.isShowIntersection()) {

				int x1 = simpleTimeline.pointAtDate(task1.getDateMin());
				int x2 = simpleTimeline.pointAtDate(task1.getDateMax());

				Rectangle rect1 = new Rectangle(x1, rect.y, x2 - x1,
						rect.height);

				for (int j = i + 1; j < tasks.size(); j++) {
					SimpleTask task2 = tasks.get(j);
					if (task2.isShowIntersection()) {
						int x3 = simpleTimeline.pointAtDate(task2.getDateMin());
						int x4 = simpleTimeline.pointAtDate(task2.getDateMax());

						Rectangle rect2 = new Rectangle(x3, rect.y, x4 - x3,
								rect.height);
						if (rect1.intersects(rect2)) {
							Rectangle r = rect1.intersection(rect2);
							g2.fillRect(r.x, r.y, r.width, r.height);
						}
					}
				}
			}
		}
	}

	private void paintTasks(Graphics g, Rectangle rect, int resIndex,
			List<SimpleTask> tasks) {
		Graphics2D g2 = (Graphics2D) g;

		JSimpleTimeline simpleTimeline = simpleTimelineCenter
				.getSimpleTimeline();

		SimpleTimelineResource resource = simpleTimeline.getResourcesModel()
				.getResource(resIndex);

		Rectangle clip = g2.getClipBounds();
		for (SimpleTask task : tasks) {
			int x1 = simpleTimeline.pointAtDate(task.getDateMin());
			int x2 = simpleTimeline.pointAtDate(task.getDateMax());

			Rectangle tRect = new Rectangle(x1, rect.y, x2 - x1, rect.height);

			if (!task.isNoClipping()) {
				g2.clipRect(tRect.x, tRect.y, tRect.width, tRect.height);
			} else {
				g2.clipRect(rect.x, tRect.y, rect.width, tRect.height);
			}

			boolean isSelected = simpleTimeline.getSelectionModel()
					.isSelectedTasks(resource.getModelIndex(), task);

			getSimpleTimelineTaskRenderer(task).paintTask(g2, tRect,
					simpleTimeline, task, isSelected, false, resIndex);

			g2.setClip(clip.x, clip.y, clip.width, clip.height);
		}
	}

	private void paintTasksDrop(Graphics g, Rectangle rect, int resIndex,
			SimpleTask[] tasks) {
		Graphics2D g2 = (Graphics2D) g;

		JSimpleTimeline simpleTimeline = simpleTimelineCenter
				.getSimpleTimeline();

		JSimpleTimeline.DropMode dropMode = simpleTimeline.getDropMode();

		Rectangle clip = g2.getClipBounds();
		for (SimpleTask task : tasks) {
			int x1 = simpleTimeline.pointAtDate(task.getDateMin());
			int x2 = simpleTimeline.pointAtDate(task.getDateMax());

			Rectangle tRect = new Rectangle(x1, rect.y, x2 - x1, rect.height);

			switch (dropMode) {
			case OUTLINE_GHOST:
				outlineGhostTaskRenderer.paintTask(g2, tRect, simpleTimeline,
						task, false, false, resIndex);
				break;
			case FILL_GHOST:
				fillGhostTaskRenderer.paintTask(g2, tRect, simpleTimeline,
						task, false, false, resIndex);
				break;
			case RENDERER:
				if (!task.isNoClipping()) {
					g2.clipRect(tRect.x, tRect.y, tRect.width, tRect.height);
				} else {
					g2.clipRect(rect.x, tRect.y, rect.width, tRect.height);
				}
				getSimpleTimelineTaskRenderer(task).paintTask(g2, tRect,
						simpleTimeline, task, false, false, resIndex);

				g2.setClip(clip.x, clip.y, clip.width, clip.height);
				break;
			}
		}
	}

	private int viewIndexForResource(SimpleTimelineResource aRessource) {
		JSimpleTimeline simpleTimeline = simpleTimelineCenter
				.getSimpleTimeline();
		if (simpleTimeline != null) {
			SimpleTimelineResourcesModel strm = simpleTimeline
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

		JSimpleTimeline simpleTimeline = simpleTimelineCenter
				.getSimpleTimeline();
		if (simpleTimeline != null) {
			int dayWidth = simpleTimeline.getDayWidth();
			int nb = simpleTimeline.getNbBeforeDays()
					+ simpleTimeline.getNbAfterDays() + 1;
			width = dayWidth * nb;
		}
		return new Dimension(width, height);
	}

	public Dimension getMinimumSize(JComponent c) {
		int height = 0;
		JSimpleTimeline simpleTimeline = simpleTimelineCenter
				.getSimpleTimeline();
		if (simpleTimeline != null) {
			Enumeration<SimpleTimelineResource> enumeration = simpleTimeline
					.getResourcesModel().getResources();
			while (enumeration.hasMoreElements()) {
				SimpleTimelineResource aResource = enumeration.nextElement();
				height = height + aResource.getMinHeight();
			}
		}
		return createTimelineSize(height);
	}

	public Dimension getPreferredSize(JComponent c) {
		int height = 0;
		JSimpleTimeline simpleTimeline = simpleTimelineCenter
				.getSimpleTimeline();
		if (simpleTimeline != null) {
			Enumeration<SimpleTimelineResource> enumeration = simpleTimeline
					.getResourcesModel().getResources();
			while (enumeration.hasMoreElements()) {
				SimpleTimelineResource aResource = enumeration.nextElement();
				height = height + aResource.getPreferredHeight();
			}
		}
		return createTimelineSize(height);
	}

	public Dimension getMaximumSize(JComponent c) {
		int height = 0;
		JSimpleTimeline simpleTimeline = simpleTimelineCenter
				.getSimpleTimeline();
		if (simpleTimeline != null) {
			Enumeration<SimpleTimelineResource> enumeration = simpleTimeline
					.getResourcesModel().getResources();
			while (enumeration.hasMoreElements()) {
				SimpleTimelineResource aResource = enumeration.nextElement();
				height = height + aResource.getMaxHeight();
			}
		}
		return createTimelineSize(height);
	}

	private final class MouseInputHandler extends MouseInputAdapter implements
			BeforeDrag {

		private void selectedResourceAndTask(MouseEvent e) {
			JSimpleTimeline simpleTimeline = simpleTimelineCenter
					.getSimpleTimeline();
			if (simpleTimeline != null) {
				SimpleTimelineModel model = simpleTimeline.getModel();
				Date dateMin = simpleTimeline.getDateMin();
				Date dateMax = simpleTimeline.getDateMax();

				SimpleTimelineSelectionModel selectionModel = simpleTimeline
						.getSelectionModel();

				Point p = e.getPoint();
				int resourceIndex = simpleTimeline.resourceAtPoint(p);
				if (resourceIndex != -1) {
					SimpleTimelineResource resource = simpleTimeline
							.getResourcesModel().getResource(resourceIndex);

					Date date = simpleTimeline.dateAtPoint(p);

					List<SimpleTask> selectTasks = new ArrayList<SimpleTask>();
					List<SimpleTask> tasks = model.getTasks(resource
							.getModelIndex(), dateMin, dateMax);
					if (tasks != null) {
						for (SimpleTask task : tasks) {
							if (task.isSelected()
									&& DateTimeUtils.includeDates(task
											.getDateMin(), task.getDateMax(),
											date, date)) {
								selectTasks.add(task);
							}
						}
					}

					SimpleTask selectTask = selectTasks != null
							&& !selectTasks.isEmpty() ? selectTasks.get(0)
							: null;
					if (e.isControlDown()) {
						if (selectionModel.isSelectedTasks(resource
								.getModelIndex(), selectTask)) {
							selectionModel.removeSelectionIndexResource(
									resource.getModelIndex(), selectTask);
						} else {
							selectionModel.addSelectionIndexResource(resource
									.getModelIndex(), selectTask);
						}
					} else {
						selectionModel.setSelectionIndexResource(resource
								.getModelIndex(), selectTask);
					}
				} else {
					selectionModel.clearSelection();
				}
			}
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			JSimpleTimeline simpleTimeline = simpleTimelineCenter
					.getSimpleTimeline();
			if (simpleTimeline != null) {
				//selectedResourceAndTask(e);
				if (simpleTimeline.getSimpleTimelineDrag() != null) {
					DragRecognitionSupport.mousePressed(e);
				}
			}
		}

		public void dragStarting(MouseEvent me) {
		}

		public void mouseDragged(MouseEvent e) {
			JSimpleTimeline simpleTimeline = simpleTimelineCenter
					.getSimpleTimeline();
			if (simpleTimeline != null
					&& simpleTimeline.getSimpleTimelineDrag() != null) {
				DragRecognitionSupport.mouseDragged(e, this);
			}
		}

		public void mouseReleased(MouseEvent e) {
			JSimpleTimeline simpleTimeline = simpleTimelineCenter
					.getSimpleTimeline();
			if (simpleTimeline != null) {
				selectedResourceAndTask(e);
				if (simpleTimeline.getSimpleTimelineDrag() != null) {
					DragRecognitionSupport.mouseReleased(e);
				}
			}
		}
	}

	private static final class DefaultTaskRenderer implements
			SimpleTimelineTaskRenderer {

		public void paintTask(Graphics g, Rectangle rect,
				JSimpleTimeline simpleTimeline, SimpleTask task,
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

	private static final class OutlineGhostTaskRenderer implements
			SimpleTimelineTaskRenderer {

		private static final Color borderColor = new Color(0, 0, 0, 196);

		private static final Stroke stroke = new BasicStroke(2.0f);

		public void paintTask(Graphics g, Rectangle rect,
				JSimpleTimeline simpleTimeline, SimpleTask task,
				boolean isSelected, boolean hasFocus, int ressource) {
			Graphics2D g2 = (Graphics2D) g.create();

			g2.setColor(borderColor);
			g2.setStroke(stroke);
			g2.drawRect(rect.x, rect.y + 5, rect.width - 1, rect.height - 10);

			g2.dispose();
		}
	}

	private static final class FillGhostTaskRenderer implements
			SimpleTimelineTaskRenderer {

		private static final Color backgroundColor = new Color(0, 0, 0, 64);

		private static final Color borderColor = new Color(0, 0, 0, 128);

		public void paintTask(Graphics g, Rectangle rect,
				JSimpleTimeline simpleTimeline, SimpleTask task,
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
