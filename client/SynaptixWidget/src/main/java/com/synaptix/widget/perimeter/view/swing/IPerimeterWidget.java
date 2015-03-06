package com.synaptix.widget.perimeter.view.swing;

import javax.swing.JComponent;

public interface IPerimeterWidget {

	public abstract String getTitle();

	public abstract JComponent getView();

	public abstract Object getValue();

	public abstract void setValue(Object value);

	public abstract void addPerimetreWidgetListener(PerimeterWidgetListener l);

	public abstract void removePerimetreWidgetListener(PerimeterWidgetListener l);

}
