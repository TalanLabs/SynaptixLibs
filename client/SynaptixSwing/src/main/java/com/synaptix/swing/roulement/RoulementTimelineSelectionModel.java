package com.synaptix.swing.roulement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.roulement.event.RoulementTimelineSelectionListener;

public class RoulementTimelineSelectionModel {

	public enum Mode {
		SINGLE_SELECTION, MULTIPLE_SELECTION
	};

	private Mode selectionMode;

	private EventListenerList listenerList;

	private List<Integer> indexResources;

	private Map<Integer, List<RoulementTask>> roulementTaskListMap;

	private Map<Integer, List<DayDate>> dayDateListMap;

	public RoulementTimelineSelectionModel() {
		listenerList = new EventListenerList();

		indexResources = new ArrayList<Integer>();
		roulementTaskListMap = new HashMap<Integer, List<RoulementTask>>();
		dayDateListMap = new HashMap<Integer, List<DayDate>>();

		selectionMode = Mode.MULTIPLE_SELECTION;
	}

	public void setSelectionIndexResource(int resource) {
		indexResources.clear();
		roulementTaskListMap.clear();
		dayDateListMap.clear();
		indexResources.add(resource);
		fireValueChanged();
	}

	public void addSelectionIndexResource(int resource) {
		if (!indexResources.contains(resource)) {
			if (selectionMode.equals(Mode.SINGLE_SELECTION)) {
				indexResources.clear();
				roulementTaskListMap.clear();
				dayDateListMap.clear();
			}
			indexResources.add(resource);

			fireValueChanged();
		}
	}

	public void removeSelectionIndexResource(int resource) {
		indexResources.remove(indexResources.indexOf(resource));
		if (roulementTaskListMap.containsKey(resource)) {
			roulementTaskListMap.remove(resource);
		}
		if (dayDateListMap.containsKey(resource)) {
			dayDateListMap.remove(resource);
		}
		fireValueChanged();
	}

	public void setSelectionIndexResource(int resource, DayDate dayDate,
			RoulementTask task) {
		if (indexResources.size() == 1 && indexResources.contains(resource)
				&& roulementTaskListMap.get(resource) != null
				&& roulementTaskListMap.get(resource).size() == 1
				&& roulementTaskListMap.get(resource).contains(task)
				&& dayDateListMap.get(resource) != null
				&& dayDateListMap.get(resource).size() == 1
				&& dayDateListMap.get(resource).contains(dayDate)) {
			return;
		}

		indexResources.clear();
		roulementTaskListMap.clear();
		dayDateListMap.clear();
		indexResources.add(resource);

		if (task != null) {
			roulementTaskListMap
					.put(resource, new ArrayList<RoulementTask>());
			List<RoulementTask> tasks = new ArrayList<RoulementTask>();
			tasks.add(task);
			roulementTaskListMap.put(resource, tasks);
		}

		if (dayDate != null) {
			dayDateListMap.put(resource, new ArrayList<DayDate>());
			List<DayDate> ddList = new ArrayList<DayDate>();
			ddList.add(dayDate);
			dayDateListMap.put(resource, ddList);
		}

		fireValueChanged();
	}

	public void addSelectionIndexResource(int resource, DayDate dayDate,
			RoulementTask task) {
		if (selectionMode.equals(Mode.SINGLE_SELECTION)) {
			indexResources.clear();
			roulementTaskListMap.clear();
			dayDateListMap.clear();
		}
		if (!indexResources.contains(resource)) {
			indexResources.add(resource);
		}

		if (task != null) {
			if (!roulementTaskListMap.containsKey(resource)) {
				roulementTaskListMap.put(resource,
						new ArrayList<RoulementTask>());
			}

			List<RoulementTask> tasks = roulementTaskListMap.get(resource);
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

	public void removeSelectionIndexResource(int resource, DayDate dayDate,
			RoulementTask task) {
		List<RoulementTask> tasks = roulementTaskListMap.get(resource);
		tasks.remove(task);

		if (tasks.isEmpty()) {
			roulementTaskListMap.remove(resource);
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
		if (indexResources.size() == 1 && indexResources.contains(resource)
				&& dayDateListMap.get(resource) != null
				&& dayDateListMap.get(resource).size() == 1
				&& dayDateListMap.get(resource).contains(dayDate)) {
			return;
		}

		indexResources.clear();
		roulementTaskListMap.clear();
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

	public void addSelectionIndexResource(int resource, DayDate dayDate) {
		if (selectionMode.equals(Mode.SINGLE_SELECTION)) {
			indexResources.clear();
			roulementTaskListMap.clear();
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
		roulementTaskListMap.clear();
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

	public boolean isSelectedTask(RoulementTask task) {
		boolean ok = false;
		Iterator<List<RoulementTask>> it = roulementTaskListMap.values()
				.iterator();
		while (it.hasNext() && !ok) {
			ok = it.next().contains(task);
		}
		return ok;
	}

	public boolean isSelectedTasks(int resource, RoulementTask task) {
		return isSelectedIndexResource(resource)
				&& !roulementTaskListMap.isEmpty()
				&& roulementTaskListMap.containsKey(resource)
				&& roulementTaskListMap.get(resource).contains(task);
	}

	public RoulementTask[] getSelectionTasks(int resource) {
		List<RoulementTask> sdList = roulementTaskListMap.get(resource);
		return sdList != null ? sdList
				.toArray(new RoulementTask[sdList.size()])
				: new RoulementTask[0];
	}

	public RoulementTask[] getSelectionTasks(int resource, int ordre) {
		List<RoulementTask> res = new ArrayList<RoulementTask>();
		if (roulementTaskListMap.containsKey(resource)) {
			for (RoulementTask task : roulementTaskListMap.get(resource)) {
				if (task.getOrdre() == ordre) {
					res.add(task);
				}
			}
		}
		return res != null ? res.toArray(new RoulementTask[res.size()])
				: new RoulementTask[0];
	}

	public int getSelectionTaskCount(int resource) {
		if (roulementTaskListMap.containsKey(resource)) {
			return roulementTaskListMap.get(resource).size();
		}
		return 0;
	}

	public int getSelectionTaskCountByResourceOrdre(int resource, int ordre) {
		int t = 0;
		if (roulementTaskListMap.containsKey(resource)) {
			for (RoulementTask task : roulementTaskListMap.get(resource)) {
				if (task.getOrdre() == ordre) {
					t++;
				}
			}
			return roulementTaskListMap.get(resource).size();
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
		return isSelectedIndexResource(resource) && !dayDateListMap.isEmpty()
				&& dayDateListMap.containsKey(resource)
				&& dayDateListMap.get(resource).contains(dayDate);
	}

	public DayDate[] getSelectionDayDates(int resource) {
		List<DayDate> ddList = dayDateListMap.get(resource);
		return ddList != null ? ddList.toArray(new DayDate[ddList.size()])
				: new DayDate[0];
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
			if (listeners[i] == RoulementTimelineSelectionListener.class) {
				((RoulementTimelineSelectionListener) listeners[i + 1])
						.valueChanged();
			}
		}
	}

	public void addSelectionListener(RoulementTimelineSelectionListener x) {
		listenerList.add(RoulementTimelineSelectionListener.class, x);
	}

	public void removeSelectionListener(RoulementTimelineSelectionListener x) {
		listenerList.remove(RoulementTimelineSelectionListener.class, x);
	}
}
