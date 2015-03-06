package com.synaptix.widget.perimeter.view.swing.descriptor;

import java.util.List;
import java.util.Map;

import com.synaptix.widget.perimeter.view.swing.AbstractPerimeterAction;
import com.synaptix.widget.perimeter.view.swing.AbstractPerimetersPanel;

public interface IPerimeterDescriptor {

	public String getId();

	public AbstractPerimeterAction[] getActions();

	public void installPerimeter(AbstractPerimetersPanel perimeterPanel);

	public List<IPerimeterSearchAxis> getPerimeterSearchAxisList();

	public Map<String, Object> getDefaultValueFilterMap();

	public interface IPerimeterSearchAxis {

		public String getId();

		public String getName();

		public String[] getPerimeterFilters();

		public Map<String, Object> getImplicitValueFilterMap();

	}
}