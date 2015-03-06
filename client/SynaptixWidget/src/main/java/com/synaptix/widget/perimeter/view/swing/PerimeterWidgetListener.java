package com.synaptix.widget.perimeter.view.swing;

import java.util.EventListener;

public interface PerimeterWidgetListener extends EventListener {

	public abstract void titleChanged(IPerimeterWidget source);

	public abstract void valuesChanged(IPerimeterWidget source);

}
