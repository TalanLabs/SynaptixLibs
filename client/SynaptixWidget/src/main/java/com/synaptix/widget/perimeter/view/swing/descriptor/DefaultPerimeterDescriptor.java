package com.synaptix.widget.perimeter.view.swing.descriptor;

import java.util.List;
import java.util.Map;

import com.synaptix.widget.perimeter.view.swing.AbstractPerimeterAction;

public class DefaultPerimeterDescriptor extends AbstractPerimeterDescriptor {

	private final String id;

	private final AbstractPerimeterAction[] actions;

	public DefaultPerimeterDescriptor(String id, AbstractPerimeterAction... actions) {
		super();

		this.id = id;
		this.actions = actions;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public List<IPerimeterSearchAxis> getPerimeterSearchAxisList() {
		return null;
	}

	@Override
	public AbstractPerimeterAction[] getActions() {
		return actions;
	}

	@Override
	public Map<String, Object> getDefaultValueFilterMap() {
		return null;
	}
}
