package com.synaptix.swing.simpledaystimeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.SimpleDaysTask;
import com.synaptix.swing.event.SimpleDaysTimelineSelectionListener;

public class SimpleDaysTimelineSelectionModel {

	public enum Mode {
		NONE_SELECTION, // Aucune séléctionne
		SINGLE_SELECTION, // Qu'un élément à la fois, toujours celui avec ordre
							// plus grand
		MULTIPLE_SELECTION, // Plusieurs éléments mais sur un clique que
							// l'élément avec ordre plus grand
		MULTIPLE_SELECTION_SANS_ORDRE // Plusieurs éléments, sur un clique
										// plusieurs élements peuvent etre
										// séléctionné
	};

	private Mode selectionMode;

	private EventListenerList listenerList;

	private List<Integer> indexResources;

	private Map<Integer, List<SimpleDaysTask>> simpleDaysTaskListMap;

	private Map<Integer, List<DayDate>> dayDateListMap;

	public SimpleDaysTimelineSelectionModel() {
		listenerList = new EventListenerList();

		indexResources = new ArrayList<Integer>();
		simpleDaysTaskListMap = new HashMap<Integer, List<SimpleDaysTask>>();
		dayDateListMap = new HashMap<Integer, List<DayDate>>();

		selectionMode = Mode.MULTIPLE_SELECTION;
	}

	public void setSelectionIndexResource(int resource) {
		if (!selectionMode.equals(Mode.NONE_SELECTION)) {
			indexResources.clear();
			simpleDaysTaskListMap.clear();
			dayDateListMap.clear();
			indexResources.add(resource);
			fireValueChanged();
		}
	}

	public void addSelectionIndexResource(int resource) {
		if (!selectionMode.equals(Mode.NONE_SELECTION)) {
			if (!indexResources.contains(resource)) {
				if (selectionMode.equals(Mode.SINGLE_SELECTION)) {
					indexResources.clear();
					simpleDaysTaskListMap.clear();
					dayDateListMap.clear();
				}
				indexResources.add(resource);

				fireValueChanged();
			}
		}
	}

	public void removeSelectionIndexResource(int resource) {
		indexResources.remove(indexResources.indexOf(resource));
		if (simpleDaysTaskListMap.containsKey(resource)) {
			simpleDaysTaskListMap.remove(resource);
		}
		if (dayDateListMap.containsKey(resource)) {
			dayDateListMap.remove(resource);
		}
		fireValueChanged();
	}

	public void setSelectionIndexResource(int resource, DayDate dayDate, SimpleDaysTask task) {
		if (!selectionMode.equals(Mode.NONE_SELECTION)) {
			if (indexResources.size() == 1 && indexResources.contains(resource) && simpleDaysTaskListMap.get(resource) != null
					&& simpleDaysTaskListMap.get(resource).size() == 1 && simpleDaysTaskListMap.get(resource).contains(task)
					&& dayDateListMap.get(resource) != null && dayDateListMap.get(resource).size() == 1
					&& dayDateListMap.get(resource).contains(dayDate)) {
				return;
			}

			indexResources.clear();
			simpleDaysTaskListMap.clear();
			dayDateListMap.clear();
			indexResources.add(resource);

			if (task != null) {
				simpleDaysTaskListMap.put(resource, new ArrayList<SimpleDaysTask>());
				List<SimpleDaysTask> tasks = new ArrayList<SimpleDaysTask>();
				tasks.add(task);
				simpleDaysTaskListMap.put(resource, tasks);
			}

			if (dayDate != null) {
				dayDateListMap.put(resource, new ArrayList<DayDate>());
				List<DayDate> ddList = new ArrayList<DayDate>();
				ddList.add(dayDate);
				dayDateListMap.put(resource, ddList);
			}

			fireValueChanged();
		}
	}

	public void addSelectionIndexResource(int resource, DayDate dayDate, SimpleDaysTask task) {
		if (!selectionMode.equals(Mode.NONE_SELECTION)) {
			if (selectionMode.equals(Mode.SINGLE_SELECTION)) {
				indexResources.clear();
				simpleDaysTaskListMap.clear();
				dayDateListMap.clear();
			}
			if (!indexResources.contains(resource)) {
				indexResources.add(resource);
			}

			if (task != null) {
				if (!simpleDaysTaskListMap.containsKey(resource)) {
					simpleDaysTaskListMap.put(resource, new ArrayList<SimpleDaysTask>());
				}

				List<SimpleDaysTask> tasks = simpleDaysTaskListMap.get(resource);
				if (!tasks.contains(task)) {
					tasks.add(task);
				}
			}

			if (dayDate != null) {
				if (!dayDateListMap.containsKey(resource)) {
					dayDateListMap.put(resource, new ArrayList<DayDate>());
				}

				List<DayDate> ddList = dayDateListMap.get(resource);
				if (!ddList.contains(dayDate)) {
					ddList.add(dayDate);
				}
			}

			fireValueChanged();
		}
	}

	public void removeSelectionIndexResource(int resource, DayDate dayDate, SimpleDaysTask task) {
		List<SimpleDaysTask> tasks = simpleDaysTaskListMap.get(resource);
		tasks.remove(task);

		if (tasks.isEmpty()) {
			simpleDaysTaskListMap.remove(resource);
		}

		List<DayDate> ddList = dayDateListMap.get(resource);
		if (ddList != null) {
			ddList.remove(dayDate);

			if (ddList.isEmpty()) {
				dayDateListMap.remove(resource);
			}
		}

		fireValueChanged();
	}

	public void setSelectionIndexResource(int resource, DayDate dayDate) {
		if (!selectionMode.equals(Mode.NONE_SELECTION)) {
			if (indexResources.size() == 1 && indexResources.contains(resource) && dayDateListMap.get(resource) != null
					&& dayDateListMap.get(resource).size() == 1 && dayDateListMap.get(resource).contains(dayDate)) {
				return;
			}

			indexResources.clear();
			simpleDaysTaskListMap.clear();
			dayDateListMap.clear();
			indexResources.add(resource);

			if (dayDate != null) {
				dayDateListMap.put(resource, new ArrayList<DayDate>());
				List<DayDate> ddList = new ArrayList<DayDate>();
				ddList.add(dayDate);
				dayDateListMap.put(resource, ddList);
			}

			fireValueChanged();
		}
	}

	public void addSelectionIndexResource(int resource, DayDate dayDate) {
		if (!selectionMode.equals(Mode.NONE_SELECTION)) {
			if (selectionMode.equals(Mode.SINGLE_SELECTION)) {
				indexResources.clear();
				simpleDaysTaskListMap.clear();
				dayDateListMap.clear();
			}
			if (!indexResources.contains(resource)) {
				indexResources.add(resource);
			}

			if (dayDate != null) {
				if (!dayDateListMap.containsKey(resource)) {
					dayDateListMap.put(resource, new ArrayList<DayDate>());
				}

				List<DayDate> ddList = dayDateListMap.get(resource);
				if (!ddList.contains(dayDate)) {
					ddList.add(dayDate);
				}
			}

			fireValueChanged();
		}
	}

	public void removeSelectionIndexResource(int resource, DayDate dayDate) {
		List<DayDate> ddList = dayDateListMap.get(resource);
		ddList.remove(dayDate);

		if (ddList.isEmpty()) {
			dayDateListMap.remove(resource);
		}

		fireValueChanged();
	}

	public void clearSelection() {
		indexResources.clear();
		simpleDaysTaskListMap.clear();
		dayDateListMap.clear();
		fireValueChanged();
	}

	public boolean isSelectedIndexResource(int resource) {
		return !indexResources.isEmpty() && indexResources.contains(resource);
	}

	public int getMaxSelectionIndexResource() {
		if (!indexResources.isEmpty()) {
			return indexResources.get(indexResources.size() - 1);
		}
		return -1;
	}

	public int getMinSelectionIndexResource() {
		if (!indexResources.isEmpty()) {
			return indexResources.get(0);
		}
		return -1;
	}

	public Integer[] getSelectionIndexResources() {
		return indexResources.toArray(new Integer[0]);
	}

	public int getSelectionResourceCount() {
		return indexResources.size();
	}

	public boolean isSelectedTask(SimpleDaysTask task) {
		boolean ok = false;
		Iterator<List<SimpleDaysTask>> it = simpleDaysTaskListMap.values().iterator();
		while (it.hasNext() && !ok) {
			ok = it.next().contains(task);
		}
		return ok;
	}

	public boolean isSelectedTasks(int resource, SimpleDaysTask task) {
		return isSelectedIndexResource(resource) && !simpleDaysTaskListMap.isEmpty() && simpleDaysTaskListMap.containsKey(resource)
				&& simpleDaysTaskListMap.get(resource).contains(task);
	}

	public SimpleDaysTask[] getSelectionTasks(int resource) {
		List<SimpleDaysTask> sdList = simpleDaysTaskListMap.get(resource);
		return sdList != null ? sdList.toArray(new SimpleDaysTask[sdList.size()]) : new SimpleDaysTask[0];
	}

	public SimpleDaysTask[] getSelectionTasks(int resource, int ordre) {
		List<SimpleDaysTask> res = new ArrayList<SimpleDaysTask>();
		if (simpleDaysTaskListMap.containsKey(resource)) {
			for (SimpleDaysTask task : simpleDaysTaskListMap.get(resource)) {
				if (task.getOrdre() == ordre) {
					res.add(task);
				}
			}
		}
		return res != null ? res.toArray(new SimpleDaysTask[res.size()]) : new SimpleDaysTask[0];
	}

	public int getSelectionTaskCount(int resource) {
		if (simpleDaysTaskListMap.containsKey(resource)) {
			return simpleDaysTaskListMap.get(resource).size();
		}
		return 0;
	}

	public int getSelectionTaskCountByResourceOrdre(int resource, int ordre) {
		int t = 0;
		if (simpleDaysTaskListMap.containsKey(resource)) {
			for (SimpleDaysTask task : simpleDaysTaskListMap.get(resource)) {
				if (task.getOrdre() == ordre) {
					t++;
				}
			}
			return simpleDaysTaskListMap.get(resource).size();
		}
		return t;
	}

	public int getSelectionTaskCount() {
		int t = 0;
		for (int i : indexResources) {
			t += getSelectionTaskCount(i);
		}
		return t;
	}

	public int getSelectionTaskCountByOrdre(int ordre) {
		int t = 0;
		for (int i : indexResources) {
			t += getSelectionTaskCountByResourceOrdre(i, ordre);
		}
		return t;
	}

	public boolean isSelectedDayDate(int resource, DayDate dayDate) {
		return isSelectedIndexResource(resource) && !dayDateListMap.isEmpty() && dayDateListMap.containsKey(resource)
				&& dayDateListMap.get(resource).contains(dayDate);
	}

	public DayDate[] getSelectionDayDates(int resource) {
		List<DayDate> ddList = dayDateListMap.get(resource);
		return ddList != null ? ddList.toArray(new DayDate[ddList.size()]) : new DayDate[0];
	}

	public int getSelectionDayDateCount(int resource) {
		if (dayDateListMap.containsKey(resource)) {
			return dayDateListMap.get(resource).size();
		}
		return 0;
	}

	public int getSelectionDayDateCount() {
		int t = 0;
		for (int i : indexResources) {
			t += getSelectionDayDateCount(i);
		}
		return t;
	}

	public boolean isSelectionEmpty() {
		return indexResources.isEmpty();
	}

	public Mode getSelectionMode() {
		return selectionMode;
	}

	public void setSelectionMode(Mode selectionMode) {
		this.selectionMode = selectionMode;
	}

	protected void fireValueChanged() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == SimpleDaysTimelineSelectionListener.class) {
				((SimpleDaysTimelineSelectionListener) listeners[i + 1]).valueChanged();
			}
		}
	}

	public void addSelectionListener(SimpleDaysTimelineSelectionListener x) {
		listenerList.add(SimpleDaysTimelineSelectionListener.class, x);
	}

	public void removeSelectionListener(SimpleDaysTimelineSelectionListener x) {
		listenerList.remove(SimpleDaysTimelineSelectionListener.class, x);
	}
}
