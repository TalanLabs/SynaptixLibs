package com.synaptix.widget.perimeter.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.IComponent;
import com.synaptix.prefs.SyPreferences;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.widget.perimeter.view.swing.descriptor.DefaultPerimeterDescriptor;
import com.synaptix.widget.perimeter.view.swing.descriptor.IPerimeterDescriptor;
import com.synaptix.widget.perimeter.view.swing.descriptor.IPerimeterDescriptor.IPerimeterSearchAxis;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceComboBoxRenderer;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.IToolBarActionsBuilder;

/**
 * A perimeter which can use a {@link PerimetersFilterModel} and some actions
 * 
 * @author Nicolas P
 */
public class PerimetersFilterPanel<E extends IComponent> extends AbstractPerimetersPanel {

	private static final String SEARCH_AXIS = "searchAxis"; //$NON-NLS-1$

	private static final long serialVersionUID = -7991442729884139732L;

	private final IPerimeterDescriptor perimeterDescriptor;

	private final AbstractPerimeterAction[] actions;

	private final PerimetersFilterModel<E> perimetersFilterModel;

	private JComboBox axisCombobox;

	public PerimetersFilterPanel(String id, AbstractPerimeterAction... actions) {
		this(new DefaultPerimeterDescriptor(id, actions), null);
	}

	public PerimetersFilterPanel(IPerimeterDescriptor perimeterDescriptor, PerimetersFilterModel<E> perimetersFilterModel) {
		this(perimeterDescriptor, SwingConstants.LEFT, 0, perimetersFilterModel);
	}

	public PerimetersFilterPanel(IPerimeterDescriptor perimeterDescriptor, int direction) {
		this(perimeterDescriptor, direction, 0);
	}

	public PerimetersFilterPanel(String id, int direction, int gap, AbstractPerimeterAction... actions) {
		this(new DefaultPerimeterDescriptor(id, actions), direction, gap);
	}

	public PerimetersFilterPanel(IPerimeterDescriptor perimeterDescriptor, int direction, int gap) {
		this(perimeterDescriptor, direction, gap, null);
	}

	private PerimetersFilterPanel(IPerimeterDescriptor perimeterDescriptor, int direction, int gap, PerimetersFilterModel<E> perimetersFilterModel) {
		super(perimeterDescriptor.getId(), direction, gap);

		this.perimeterDescriptor = perimeterDescriptor;
		this.actions = perimeterDescriptor.getActions();
		this.perimetersFilterModel = perimetersFilterModel;

		init();
	}

	private void init() {
		addRefreshListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if ((REFRESH.equals(e.getActionCommand())) && (actions != null)) {
					for (AbstractPerimeterAction a : actions) {
						a.fireRefreshAction();
					}
				}
			}
		});

		addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if (actions != null) {
					for (AbstractPerimeterAction a : actions) {
						a.fireValuesChangedAction();
					}
				}
			}
		});

		if (hasPerimeterSearchAxis()) {
			axisCombobox = new JComboBox(perimeterDescriptor.getPerimeterSearchAxisList().toArray(new IPerimeterSearchAxis[perimeterDescriptor.getPerimeterSearchAxisList().size()]));
			axisCombobox.setRenderer(new TypeGenericSubstanceComboBoxRenderer<IPerimeterSearchAxis>(axisCombobox, new GenericObjectToString<IPerimeterSearchAxis>() {
				@Override
				public String getString(IPerimeterSearchAxis t) {
					return t.getName();
				}
			}));
			axisCombobox.addActionListener(new AbstractAction() {

				private static final long serialVersionUID = 7134916011776931121L;

				@Override
				public void actionPerformed(ActionEvent e) {
					removeAllWidgets();

					loadModel();

					savePrefs();

					fireSearchAxisChanged();
				}
			});
		}

		initialize();

		loadPrefs();
	}

	public void setSearchAxis(String searchAxis) {
		if (hasPerimeterSearchAxis()) {
			for (IPerimeterSearchAxis perimeterSearchAxis : perimeterDescriptor.getPerimeterSearchAxisList()) {
				if (perimeterSearchAxis.getId().equals(searchAxis)) {
					axisCombobox.setSelectedItem(perimeterSearchAxis);
				}
			}
		}
	}

	public final void addSearchAxisListener(SearchAxisListener searchAxisListener) {
		listenerList.add(SearchAxisListener.class, searchAxisListener);
	}

	private final void fireSearchAxisChanged() {
		SearchAxisListener[] sal = listenerList.getListeners(SearchAxisListener.class);
		for (SearchAxisListener s : sal) {
			s.searchAxisChanged();
		}
	}

	private final void savePrefs() {
		IPerimeterSearchAxis currentPerimeterSearchAxis = getCurrentPerimeterSearchAxis();
		if (currentPerimeterSearchAxis != null) {

			SyPreferences prefs = SyPreferences.getPreferences();
			String name = AbstractPerimetersPanel.class.getName() + "_" + perimeterDescriptor.getId();

			prefs.put(name + "_" + SEARCH_AXIS, currentPerimeterSearchAxis.getId());
		}
	}

	private final void loadPrefs() {
		if (axisCombobox != null) {
			SyPreferences prefs = SyPreferences.getPreferences();
			String name = AbstractPerimetersPanel.class.getName() + "_" + perimeterDescriptor.getId();

			String pref = prefs.get(name + "_" + SEARCH_AXIS, null);

			boolean found = false;
			if (pref != null) {
				for (int i = 0; i < perimeterDescriptor.getPerimeterSearchAxisList().size() && !found; i++) {
					IPerimeterSearchAxis perimeterSearchAxis = perimeterDescriptor.getPerimeterSearchAxisList().get(i);
					if (pref.equals(perimeterSearchAxis.getId())) {
						axisCombobox.setSelectedIndex(i);
						found = true;
					}
				}
			}
			if (!found) {
				axisCombobox.setSelectedIndex(0);
			}
			fireSearchAxisChanged();
		} else {
			loadModel();
		}
	}

	@Override
	protected JComponent buildCenter() {
		JComponent center = super.buildCenter();
		if (axisCombobox != null) {
			FormLayout layout = new FormLayout("FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)", "FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)");
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();

			builder.addLabel(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().searchAxis(), cc.xy(1, 1));
			builder.add(axisCombobox, cc.xy(3, 1));
			builder.add(center, cc.xyw(1, 3, 3));
			return builder.getPanel();
		}
		return center;
	}

	@Override
	protected void createSubComponents() {
	}

	private void loadModel() {
		if ((axisCombobox != null) && (axisCombobox.getSelectedIndex() >= 0)) {
			IPerimeterSearchAxis perimeterSearchAxis = (IPerimeterSearchAxis) axisCombobox.getSelectedItem();
			if (perimeterSearchAxis.getPerimeterFilters() != null) {
				for (String perimeterFilter : perimeterSearchAxis.getPerimeterFilters()) {
					addPerimetreWidget(perimeterFilter, perimetersFilterModel.findById(perimeterFilter));
				}
			}
		} else if ((perimetersFilterModel != null) && (perimetersFilterModel.getPerimeterCount() > 0)) {
			for (int i = 0; i < perimetersFilterModel.getPerimeterCount(); i++) {
				addPerimetreWidget(perimetersFilterModel.getIdPerimeter(i), perimetersFilterModel.getPerimeter(i));
			}
		}
		updateButtons();
	}

	@Override
	protected void addActions(IToolBarActionsBuilder toolbarActionsBuilder) {
		super.addActions(toolbarActionsBuilder);

		if (actions != null) {
			for (AbstractPerimeterAction action : actions) {
				toolbarActionsBuilder.addAction(action);
			}
		}
	}

	@Override
	public Map<String, Object> getFiltersMap() {
		Map<String, Object> filtersMap = super.getFiltersMap();
		if (axisCombobox != null) {
			IPerimeterSearchAxis perimeterSearchAxis = (IPerimeterSearchAxis) axisCombobox.getSelectedItem();
			if (perimeterSearchAxis.getImplicitValueFilterMap() != null) {
				filtersMap.putAll(perimeterSearchAxis.getImplicitValueFilterMap());
			}
		}
		return filtersMap;
	}

	@Override
	public Map<String, Object> getFinalFiltersMap() {
		Map<String, Object> finalFiltersMap = super.getFinalFiltersMap();
		if (axisCombobox != null) {
			IPerimeterSearchAxis perimeterSearchAxis = (IPerimeterSearchAxis) axisCombobox.getSelectedItem();
			if (perimeterSearchAxis.getImplicitValueFilterMap() != null) {
				finalFiltersMap.putAll(perimeterSearchAxis.getImplicitValueFilterMap());
			}
		}
		return finalFiltersMap;
	}

	public final boolean hasPerimeterSearchAxis() {
		return CollectionHelper.isNotEmpty(perimeterDescriptor.getPerimeterSearchAxisList());
	}

	public IPerimeterSearchAxis getCurrentPerimeterSearchAxis() {
		if (axisCombobox != null) {
			return (IPerimeterSearchAxis) axisCombobox.getSelectedItem();
		}
		return null;
	}

	public interface SearchAxisListener extends EventListener {

		public void searchAxisChanged();

	}
}
