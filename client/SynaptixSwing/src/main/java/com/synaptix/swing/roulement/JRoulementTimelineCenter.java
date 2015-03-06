package com.synaptix.swing.roulement;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.plaf.RoulementTimelineCenterUI;

public class JRoulementTimelineCenter extends JComponent {

	private static final long serialVersionUID = -7863759892137733142L;

	private static final String uiClassID = "RoulementTimelineCenterUI"; //$NON-NLS-1$

	private static final Comparator<RoulementTask> roulementTaskComparator = new RoulementTaskComparator();

	private JRoulementTimeline roulementTimeline;

	static {
		UIManager
				.getDefaults()
				.put(uiClassID,
						"com.synaptix.swing.plaf.basic.BasicRoulementTimelineCenterUI"); //$NON-NLS-1$
	}

	private List<Map<IDayDatesMinMax, Position>> positionMapList;

	private List<List<RoulementTimelineGroupTask>> roulementTimelineGroupTaskListList;

	private List<Integer> maxList;

	private RoulementTimelineGroupFactory groupFactory;

	private boolean showIntersection;

	private boolean showGroupIntersection;

	private boolean multiLine;

	public JRoulementTimelineCenter() {
		super();

		this.setOpaque(true);

		showIntersection = true;
		showGroupIntersection = false;
		multiLine = false;
		groupFactory = null;
		positionMapList = new ArrayList<Map<IDayDatesMinMax, Position>>();
		roulementTimelineGroupTaskListList = new ArrayList<List<RoulementTimelineGroupTask>>();
		maxList = new ArrayList<Integer>();

		roulementTimeline = null;

		ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
		toolTipManager.registerComponent(this);

		updateUI();
	}

	public RoulementTimelineCenterUI getUI() {
		return (RoulementTimelineCenterUI) ui;
	}

	public void setUI(RoulementTimelineCenterUI ui) {
		if (this.ui != ui) {
			super.setUI(ui);
			repaint();
		}
	}

	public void updateUI() {
		setUI((RoulementTimelineCenterUI) UIManager.getUI(this));
	}

	public String getUIClassID() {
		return uiClassID;
	}

	public void setRoulementTimeline(JRoulementTimeline roulementTimeline) {
		JRoulementTimeline old = this.roulementTimeline;
		this.roulementTimeline = roulementTimeline;
		firePropertyChange("timeline", old, roulementTimeline); //$NON-NLS-1$
	}

	public JRoulementTimeline getRoulementTimeline() {
		return roulementTimeline;
	}

	public void setShowIntersection(boolean showIntersection) {
		this.showIntersection = showIntersection;
		resizeAndRepaint();
	}

	public boolean isShowIntersection() {
		return showIntersection;
	}

	public void setShowGroupIntersection(boolean showGroupIntersection) {
		this.showGroupIntersection = showGroupIntersection;
		resizeAndRepaint();
	}

	public boolean isShowGroupIntersection() {
		return showGroupIntersection;
	}

	public void setMultiLine(boolean multiLine) {
		this.multiLine = multiLine;
		precalculeMultiLine();
		resizeAndRepaint();
	}

	public boolean isMultiLine() {
		return multiLine;
	}

	public void setGroupFactory(RoulementTimelineGroupFactory groupFactory) {
		this.groupFactory = groupFactory;
		precalculeMultiLine();
		resizeAndRepaint();
	}

	public RoulementTimelineGroupFactory getGroupFactory() {
		return groupFactory;
	}

	/**
	 * Calcule la position des tasks sur le planning
	 */
	public void precalculeMultiLine() {
		positionMapList.clear();
		maxList.clear();
		roulementTimelineGroupTaskListList.clear();

		DayDate dayDateMin = new DayDate();
		DayDate dayDateMax = new DayDate(roulementTimeline.getNbDays());

		RoulementTimelineModel model = roulementTimeline.getModel();
		if (model != null) {
			for (int resIndex = 0; resIndex < model.getResourceCount(); resIndex++) {
				precalculeMultiLine(model, resIndex, dayDateMin, dayDateMax);
			}
		}
	}

	private void precalculeMultiLine(RoulementTimelineModel model,
			int resIndex, DayDate dayDateMin, DayDate dayDateMax) {
		Map<IDayDatesMinMax, Position> positionMap = new HashMap<IDayDatesMinMax, Position>();

		int nbMaxTotal = 0;

		List<RoulementTimelineGroupTask> groupTaskList = null;

		List<RoulementTask> taskList = model.getTasks(resIndex, dayDateMin,
				dayDateMax);
		if (taskList != null && !taskList.isEmpty()) {
			if (multiLine) {
				if (groupFactory != null) {
					groupTaskList = groupFactory.buildGroupTaskList(resIndex,
							taskList);
					int[] nbAvantGroupes = new int[groupTaskList.size()];
					int nbMaxGroupeTotal = 0;
					for (int i = 0; i < groupTaskList.size(); i++) {
						RoulementTimelineGroupTask groupTask1 = groupTaskList
								.get(i);
						int nbAvant = calcule(groupTask1, i, nbMaxGroupeTotal,
								nbAvantGroupes, groupTaskList);
						nbAvantGroupes[i] = nbAvant;

						nbMaxGroupeTotal = Math.max(nbMaxGroupeTotal,
								nbAvant + 1);
					}

					int nbPrec = 0;
					for (int k = 0; k < nbMaxGroupeTotal; k++) {
						int nbMaxTotalLigne = 0;
						for (int l = 0; l < groupTaskList.size(); l++) {
							if (nbAvantGroupes[l] == k) {
								RoulementTimelineGroupTask groupTask1 = groupTaskList
										.get(l);
								List<RoulementTask> taskList2 = groupTask1
										.getRoulementTaskList();
								int[] nbAvants = new int[taskList2.size()];
								for (int i = 0; i < taskList2.size(); i++) {
									RoulementTask task1 = taskList2.get(i);
									if (!task1.isGroup()) {
										int nbAvant = calculeSansGroupe(task1,
												i, nbMaxTotalLigne, nbAvants,
												taskList2);
										nbAvants[i] = nbAvant;

										nbMaxTotalLigne = Math.max(
												nbMaxTotalLigne, nbAvant + 1);

										positionMap.put(task1, new Position(
												nbPrec + nbAvant, 1));
									}
								}
							}
						}

						nbMaxTotalLigne = nbMaxTotalLigne == 0 ? 1
								: nbMaxTotalLigne;
						for (int l = 0; l < groupTaskList.size(); l++) {
							if (nbAvantGroupes[l] == k) {
								RoulementTimelineGroupTask groupTask1 = groupTaskList
										.get(l);
								List<RoulementTask> taskList2 = groupTask1
										.getRoulementTaskList();
								for (int i = 0; i < taskList2.size(); i++) {
									RoulementTask task1 = taskList2.get(i);

									if (task1.isGroup()) {
										positionMap.put(task1, new Position(
												nbPrec, nbMaxTotalLigne));
									}
								}

								positionMap.put(groupTask1, new Position(
										nbPrec, nbMaxTotalLigne));
							}
						}

						nbPrec += nbMaxTotalLigne;
					}

					nbMaxTotal = nbPrec;
				} else {
					int[] nbAvants = new int[taskList.size()];
					for (int i = 0; i < taskList.size(); i++) {
						RoulementTask task1 = taskList.get(i);
						if (!task1.isUseMaxHeight()) {
							int nbAvant = calcule(task1, i, nbMaxTotal,
									nbAvants, taskList);

							nbAvants[i] = nbAvant;

							positionMap.put(task1, new Position(nbAvant, 1));

							nbMaxTotal = Math.max(nbMaxTotal, nbAvant + 1);
						}
					}

					nbMaxTotal = nbMaxTotal == 0 ? 1 : nbMaxTotal;

					for (int i = 0; i < taskList.size(); i++) {
						RoulementTask task1 = taskList.get(i);
						if (task1.isUseMaxHeight()) {
							positionMap.put(task1, new Position(0, nbMaxTotal));
						}
					}
				}
			} else {
				for (RoulementTask task : taskList) {
					positionMap.put(task, new Position(0, 1));
				}

				nbMaxTotal = 1;
			}
		}

		nbMaxTotal = nbMaxTotal == 0 ? 1 : nbMaxTotal;

		if (maxList.size() <= resIndex) {
			maxList.add(nbMaxTotal);
			positionMapList.add(positionMap);
			roulementTimelineGroupTaskListList.add(groupTaskList);
		} else {
			maxList.set(resIndex, nbMaxTotal);
			positionMapList.set(resIndex, positionMap);
			roulementTimelineGroupTaskListList.set(resIndex, groupTaskList);
		}
	}

	private int calcule(IDayDatesMinMax task1, int i, int nbMaxTotal,
			int[] nbAvants, List<? extends IDayDatesMinMax> taskList) {
		int x1 = roulementTimeline.pointAtDayDate(task1.getDayDateMin());
		int x2 = roulementTimeline.pointAtDayDate(task1.getDayDateMax());

		boolean trouve = false;
		int k = 0;
		while (k < nbMaxTotal && !trouve) {
			trouve = true;
			for (int j = 0; j < i; j++) {
				if (nbAvants[j] == k) {
					IDayDatesMinMax task2 = taskList.get(j);

					int x3 = roulementTimeline.pointAtDayDate(task2
							.getDayDateMin());
					int x4 = roulementTimeline.pointAtDayDate(task2
							.getDayDateMax());
					if (x4 > x1 && x3 < x2) {
						trouve = false;
					}
				}
			}
			k++;
		}
		return trouve ? k - 1 : k;
	}

	private int calculeSansGroupe(RoulementTask task1, int i, int nbMaxTotal,
			int[] nbAvants, List<RoulementTask> taskList) {
		int x1 = roulementTimeline.pointAtDayDate(task1.getDayDateMin());
		int x2 = roulementTimeline.pointAtDayDate(task1.getDayDateMax());

		boolean trouve = false;
		int k = 0;
		while (k < nbMaxTotal && !trouve) {
			trouve = true;
			for (int j = 0; j < i; j++) {
				if (nbAvants[j] == k) {
					RoulementTask task2 = taskList.get(j);
					if (!task2.isGroup()) {
						int x3 = roulementTimeline.pointAtDayDate(task2
								.getDayDateMin());
						int x4 = roulementTimeline.pointAtDayDate(task2
								.getDayDateMax());
						if (x4 > x1 && x3 < x2) {
							trouve = false;
						}
					}
				}
			}
			k++;
		}
		return trouve ? k - 1 : k;
	}

	public int getNbMaxTotal(int resIndex) {
		return maxList.get(resIndex);
	}

	public Position getPositionRoulementTask(int resIndex, IDayDatesMinMax task) {
		return positionMapList.get(resIndex).get(task);
	}

	public List<RoulementTimelineGroupTask> getRoulementTimelineGroupTaskListList(
			int resIndex) {
		return roulementTimelineGroupTaskListList.get(resIndex);
	}

	public void resizeAndRepaint() {
		revalidate();
		repaint();
	}

	public String getToolTipText(MouseEvent event) {
		String tip = null;
		Point p = event.getPoint();

		DayDate dayDate = roulementTimeline.dayDateAtPoint(p);
		int resource = roulementTimeline.resourceAtPoint(p);

		if (dayDate != null && resource != -1) {
			int resIndex = roulementTimeline
					.convertResourceIndexToModel(resource);

			int nbMaxTotal = getNbMaxTotal(resIndex);

			Rectangle rect = roulementTimeline.getResourceRect(resource);

			RoulementTimelineModel model = roulementTimeline.getModel();
			if (model != null) {
				List<RoulementTask> taskList = model.getTasks(resIndex,
						dayDate, dayDate);
				if (taskList != null && !taskList.isEmpty()) {
					Collections.sort(taskList,
							new Comparator<RoulementTask>() {
								public int compare(RoulementTask o1,
										RoulementTask o2) {
									return o1.getOrdre() == o2.getOrdre() ? 0
											: (o1.getOrdre() > o2.getOrdre() ? -1
													: 1);
								}
							});
					boolean ok = false;

					Iterator<RoulementTask> it = taskList.iterator();
					while (it.hasNext() && !ok) {
						RoulementTask task1 = it.next();

						int x1 = roulementTimeline.pointAtDayDate(task1
								.getDayDateMin());
						int x2 = roulementTimeline.pointAtDayDate(task1
								.getDayDateMax());

						JRoulementTimelineCenter.Position position = getPositionRoulementTask(
								resIndex, task1);
						if (position != null) {
							double ph = task1.getTaskRenderer()
									.getSelectionHeightPourcent();

							int h = rect.height * position.getSize()
									/ nbMaxTotal;
							int y = rect.y + (rect.height / nbMaxTotal)
									* position.getPosition();

							int dh = (int) (h - h * ph);

							Rectangle tRect = new Rectangle(x1, y + dh / 2, x2
									- x1, (int) h - dh);
							if (tRect.contains(p)) {
								Rectangle r = new Rectangle(x1, y, x2 - x1, h);
								Point p2 = new Point(p);
								p2.translate(-r.x, -r.y);

								tip = task1.getTaskRenderer().getToolTipText(r,
										roulementTimeline, task1, resource,
										dayDate, p2);
								ok = tip != null;
							}
						}
					}
				}
			}
		}

		return tip;
	}

	public List<List<RoulementTask>> computeTaskListByOrder(
			List<RoulementTask> taskList) {
		List<List<RoulementTask>> res = new ArrayList<List<RoulementTask>>();
		if (taskList != null && !taskList.isEmpty()) {
			Collections.sort(taskList, roulementTaskComparator);

			int ordre = taskList.get(0).getOrdre();
			List<RoulementTask> courant = new ArrayList<RoulementTask>();
			res.add(courant);
			for (RoulementTask task : taskList) {
				if (task.getOrdre() == ordre) {
					courant.add(task);
				} else {
					ordre = task.getOrdre();
					courant = new ArrayList<RoulementTask>();
					courant.add(task);
					res.add(courant);
				}
			}
		}
		return res;
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

	public static final class Position {

		private int position;

		private int size;

		public Position(int position, int size) {
			super();
			this.position = position;
			this.size = size;
		}

		public int getPosition() {
			return position;
		}

		public int getSize() {
			return size;
		}
	}
}
