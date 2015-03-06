package com.synaptix.widget.perimeter.view.swing.descriptor;

import java.util.List;
import java.util.Map;

import com.synaptix.widget.perimeter.view.swing.AbstractPerimeterAction;
import com.synaptix.widget.perimeter.view.swing.AbstractPerimeterFilterAction;
import com.synaptix.widget.perimeter.view.swing.AbstractPerimetersPanel;

/**
 * A default perimeter descriptor with a filter button
 * 
 * @author Nicolas P
 * 
 */
public abstract class AbstractFilterPerimeterDescriptor extends AbstractPerimeterDescriptor {

	private final AbstractPerimeterAction filterAction;

	private final List<IPerimeterSearchAxis> perimeterSearchAxisList;

	private Map<String, Object> defaultValueFilterMap;

	public AbstractFilterPerimeterDescriptor() {
		this(null);
	}

	public AbstractFilterPerimeterDescriptor(final List<IPerimeterSearchAxis> perimeterSearchAxisList) {
		this(perimeterSearchAxisList, null);
	}

	public AbstractFilterPerimeterDescriptor(final List<IPerimeterSearchAxis> perimeterSearchAxisList, final Map<String, Object> defaultValueFilterMap) {
		super();

		this.perimeterSearchAxisList = perimeterSearchAxisList;
		this.defaultValueFilterMap = defaultValueFilterMap;

		this.filterAction = new AbstractPerimeterFilterAction() {

			private static final long serialVersionUID = -8042617603518985488L;

			@Override
			protected void refresh() {
				AbstractFilterPerimeterDescriptor.this.refresh();
			}

			@Override
			protected boolean areValidatedMandatoryFilters() {
				return AbstractFilterPerimeterDescriptor.this.areValidatedMandatoryFilters();
			}
		};
		this.filterAction.setEnabled(true);
	}

	protected boolean areValidatedMandatoryFilters() {
		return true;
	}

	@Override
	protected final void perimeterInstalled(AbstractPerimetersPanel perimeterPanel) {
		super.perimeterInstalled(perimeterPanel);

		perimeterFilterInstalled(perimeterPanel);
	}

	protected void perimeterFilterInstalled(AbstractPerimetersPanel perimeterPanel) {
	}

	@Override
	public AbstractPerimeterAction[] getActions() {
		return new AbstractPerimeterAction[] { filterAction };
	}

	protected final AbstractPerimeterAction getFilterAction() {
		return filterAction;
	}

	public abstract void refresh();

	@Override
	public final List<IPerimeterSearchAxis> getPerimeterSearchAxisList() {
		return perimeterSearchAxisList;
	}

	@Override
	public Map<String, Object> getDefaultValueFilterMap() {
		return defaultValueFilterMap;
	}
}
