package com.synaptix.widget.perimeter.view.swing.descriptor;

import java.util.List;
import java.util.Map;

import com.synaptix.widget.perimeter.view.swing.AbstractPerimeterAction;
import com.synaptix.widget.perimeter.view.swing.AbstractPerimetersPanel;

public abstract class AbstractPerimeterDescriptor implements IPerimeterDescriptor {

	private AbstractPerimetersPanel perimeterPanel;

	@Override
	public AbstractPerimeterAction[] getActions() {
		return null;
	}

	@Override
	public List<IPerimeterSearchAxis> getPerimeterSearchAxisList() {
		return null;
	}

	@Override
	public Map<String, Object> getDefaultValueFilterMap() {
		return null;
	}

	@Override
	public final void installPerimeter(AbstractPerimetersPanel perimeterPanel) {
		this.perimeterPanel = perimeterPanel;
		perimeterInstalled(perimeterPanel);
	}

	protected void perimeterInstalled(AbstractPerimetersPanel perimeterPanel) {
	}

	protected final AbstractPerimetersPanel getPerimeterPanel() {
		return perimeterPanel;
	}
}