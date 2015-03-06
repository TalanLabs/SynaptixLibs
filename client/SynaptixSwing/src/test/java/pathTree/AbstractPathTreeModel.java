package pathTree;

import javax.swing.event.EventListenerList;

public abstract class AbstractPathTreeModel implements PathTreeModel {

	protected EventListenerList listenerList;

	public AbstractPathTreeModel() {
		listenerList = new EventListenerList();
	}

	public void addPathTreeModelListener(PathTreeModelListener l) {
		listenerList.add(PathTreeModelListener.class, l);
	}

	public void removePathTreeModelListener(PathTreeModelListener l) {
		listenerList.remove(PathTreeModelListener.class, l);
	}

	public PathTreeModelListener[] getPathTreeModelListeners() {
		return (PathTreeModelListener[]) listenerList
				.getListeners(PathTreeModelListener.class);
	}

	protected void firePathTreeChanged() {
		PathTreeModelListener[] listeners = listenerList
				.getListeners(PathTreeModelListener.class);
		PathTreeModelEvent e = new PathTreeModelEvent(this);

		for (PathTreeModelListener listener : listeners) {
			listener.pathTreeModelChanged(e);
		}
	}
}
