package com.synaptix.widget.perimeter.view.swing;

import javax.swing.JComponent;

import com.synaptix.client.view.IView;
import com.synaptix.swing.WaitComponentFeedbackPanel;

public abstract class AbstractPerimeterWidget extends WaitComponentFeedbackPanel implements IPerimeterWidget, IView {

	private static final long serialVersionUID = -1018312750879846662L;

	public JComponent getView() {
		return this;
	}

	public void addPerimetreWidgetListener(PerimeterWidgetListener l) {
		listenerList.add(PerimeterWidgetListener.class, l);
	}

	public void removePerimetreWidgetListener(PerimeterWidgetListener l) {
		listenerList.remove(PerimeterWidgetListener.class, l);
	}

	protected void fireTitleChanged() {
		PerimeterWidgetListener[] ls = listenerList.getListeners(PerimeterWidgetListener.class);
		for (PerimeterWidgetListener l : ls) {
			l.titleChanged(this);
		}
	}

	protected void fireValuesChanged() {
		PerimeterWidgetListener[] ls = listenerList.getListeners(PerimeterWidgetListener.class);
		for (PerimeterWidgetListener l : ls) {
			l.valuesChanged(this);
		}
	}
}
