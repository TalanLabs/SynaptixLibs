package com.synaptix.widget.perimeter.view.swing.descriptor;

import java.util.Map;

import com.synaptix.widget.perimeter.view.swing.descriptor.IPerimeterDescriptor.IPerimeterSearchAxis;

public abstract class AbstractPerimeterSearchAxis implements IPerimeterSearchAxis {

	private final String id;

	private final String name;

	public AbstractPerimeterSearchAxis(String id, String name) {
		super();

		this.id = id;
		this.name = name;
	}

	@Override
	public final String getId() {
		return id;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public Map<String, Object> getImplicitValueFilterMap() {
		return null;
	}
}
