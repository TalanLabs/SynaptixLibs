package com.synaptix.swingx.mapviewer.layers;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.mapviewer.SelectEvent;
import org.jdesktop.swingx.mapviewer.SelectListener;

public class SelectedSelectListener implements SelectListener {

	public static final String MODE_SINGLE_SELECTION = "org.jdesktop.swingx.mapviewer.layers.SelectedSelectListener.MODE_SINGLE_SELECTION";

	public static final String MODE_MULTIPLE_SELECTION = "org.jdesktop.swingx.mapviewer.layers.SelectedSelectListener.MODE_MULTIPLE_SELECTION";

	private JXMapViewer mapViewer;

	private String mode = MODE_SINGLE_SELECTION;

	private List<Selectable> selectables = new ArrayList<Selectable>();

	private EventListenerList listenerList = new EventListenerList();

	public SelectedSelectListener(JXMapViewer mapViewer) {
		super();

		this.mapViewer = mapViewer;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void selected(SelectEvent event) {
		if (event.isLeftPress() && event.getTopObject() instanceof Selectable) {
			Selectable s = (Selectable) event.getTopObject();
			if (s.isSelectable()) {
				MouseEvent mouseEvent = event.getMouseEvent();

				boolean change = false;

				boolean ctrl = mouseEvent.isControlDown();
				if (ctrl && MODE_MULTIPLE_SELECTION.equals(mode)) {
					if (s.isSelected()) {
						selectables.remove(s);
						s.setSelected(false);
					} else {
						selectables.add(s);
						s.setSelected(true);
					}

					change = true;
				} else {
					boolean c = selectables.contains(s);
					int size = selectables.size();
					for (Selectable s1 : selectables) {
						s1.setSelected(false);
					}
					selectables.clear();

					selectables.add(s);
					s.setSelected(true);

					change = size != selectables.size() || !c;
				}

				if (change) {
					mapViewer.repaint();

					fireChangeListener();
				}
			}
		}
	}

	public List<Selectable> getSelectables() {
		return Collections.unmodifiableList(selectables);
	}

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	protected void fireChangeListener() {
		ChangeEvent e = new ChangeEvent(this);
		for (ChangeListener l : listenerList.getListeners(ChangeListener.class)) {
			l.stateChanged(e);
		}
	}
}
