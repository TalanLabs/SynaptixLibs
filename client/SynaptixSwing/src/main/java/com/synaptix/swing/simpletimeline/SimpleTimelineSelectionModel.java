package com.synaptix.swing.simpletimeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

import com.synaptix.swing.SimpleTask;
import com.synaptix.swing.event.SimpleTimelineSelectionListener;

public class SimpleTimelineSelectionModel {

	public enum Mode {
		SINGLE_SELECTION, MULTIPLE_SELECTION
	};

	private Mode selectionMode;

	private EventListenerList listenerList;

	private List<Integer> indexResources;

	private Map<Integer, List<SimpleTask>> map;

	public SimpleTimelineSelectionModel() {
		listenerList = new EventListenerList();

		indexResources = new ArrayList<Integer>();
		map = new HashMap<Integer, List<SimpleTask>>();

		selectionMode = Mode.MULTIPLE_SELECTION;
	}

	public void setSelectionIndexResource(int resource) {
		indexResources.clear();
		map.clear();
		indexResources.add(resource);
		fireValueChanged();
	}

	public void addSelectionIndexResource(int resource) {
		if (!indexResources.contains(resource)) {
			if (selectionMode.equals(Mode.SINGLE_SELECTION)) {
				indexResources.clear();
				map.clear();
			}
			indexResources.add(resource);

			fireValueChanged();
		}
	}

	public void removeSelectionIndexResource(int resource) {
		indexResources.remove(indexResources.indexOf(resource));
		if (map.containsKey(resource)) {
			map.remove(resource);
		}
		fireValueChanged();
	}

	public void setSelectionIndexResource(int resource, SimpleTask task) {
		if (indexResources.size() == 1 && indexResources.contains(resource)
				&& map.get(resource) != null && map.get(resource).size() == 1
				&& map.get(resource).contains(task)) {
			return;
		}

		indexResources.clear();
		map.clear();
		indexResources.add(resource);

		if (task != null) {
			map.put(resource, new ArrayList<SimpleTask>());
			List<SimpleTask> tasks = new ArrayList<SimpleTask>();
			tasks.add(task);
			map.put(resource, tasks);
		}

		fireValueChanged();
	}

	public void addSelectionIndexResource(int resource, SimpleTask task) {
		if (selectionMode.equals(Mode.SINGLE_SELECTION)) {
			indexResources.clear();
			map.clear();
		}
		if (!indexResources.contains(resource)) {
			indexResources.add(resource);
		}

		if (task != null) {
			if (!map.containsKey(resource)) {
				map.put(resource, new ArrayList<SimpleTask>());
			}

			List<SimpleTask> tasks = map.get(resource);
			if (!tasks.contains(task)) {
				tasks.add(task);
			}
		}

		fireValueChanged();
	}

	public void removeSelectionIndexResource(int resource, SimpleTask task) {
		List<SimpleTask> tasks = map.get(resource);
		tasks.remove(task);

		if (tasks.isEmpty()) {
			map.remove(resource);
		}

		fireValueChanged();
	}

	public void clearSelection() {
		indexResources.clear();
		map.clear();
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

	public boolean isSelectedTasks(int resource, SimpleTask task) {
		return isSelectedIndexResource(resource) && !map.isEmpty()
				&& map.containsKey(resource)
				&& map.get(resource).contains(task);
	}

	public SimpleTask[] getSelectionTasks(int resource) {
		return map.get(resource).toArray(new SimpleTask[0]);
	}

	public int getSelectionTaskCount(int resource) {
		if (map.containsKey(resource)) {
			return map.get(resource).size();
		}
		return 0;
	}

	public int getSelectionTaskCount() {
		int t = 0;
		for (int i : indexResources) {
			t += getSelectionTaskCount(i);
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
			if (listeners[i] == SimpleTimelineSelectionListener.class) {
				((SimpleTimelineSelectionListener) listeners[i + 1])
						.valueChanged();
			}
		}
	}

	public void addSelectionListener(SimpleTimelineSelectionListener x) {
		listenerList.add(SimpleTimelineSelectionListener.class, x);
	}

	public void removeSelectionListener(SimpleTimelineSelectionListener x) {
		listenerList.remove(SimpleTimelineSelectionListener.class, x);
	}
}
