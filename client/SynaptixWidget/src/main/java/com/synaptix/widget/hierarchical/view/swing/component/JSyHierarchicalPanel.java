package com.synaptix.widget.hierarchical.view.swing.component;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.pushingpixels.flamingo.api.common.CommandButtonDisplayState;
import org.pushingpixels.flamingo.api.common.JCommandButton;
import org.pushingpixels.flamingo.api.common.JCommandButton.CommandButtonKind;
import org.pushingpixels.flamingo.api.common.icon.ImageWrapperResizableIcon;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.IComponent;
import com.synaptix.component.field.IField;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.service.hierarchical.model.IHierarchicalTitle;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.context.IHierarchicalContext;
import com.synaptix.widget.hierarchical.view.swing.component.helper.HierarchicalDataHelper;
import com.synaptix.widget.hierarchical.view.swing.component.helper.HierarchicalLineComparator;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanel;
import com.synaptix.widget.hierarchical.view.swing.component.panel.HierarchicalPanelFactory;
import com.synaptix.widget.hierarchical.view.swing.component.rule.HierarchicalSummaryRule;
import com.synaptix.widget.hierarchical.view.swing.component.title.IHierarchicalTitleColumnElement;
import com.synaptix.widget.hierarchical.view.swing.model.HierarchicalSelectionModel;
import com.synaptix.widget.hierarchical.view.swing.model.HierarchicalSelectionModelListener;
import com.synaptix.widget.hierarchical.writer.IExportableTable;
import com.synaptix.widget.util.StaticWidgetHelper;
import com.synaptix.widget.view.swing.helper.IconHelper;

/**
 * 
 * @author psourisse
 * 
 * @param <E>
 *            class of the {@link IHierarchicalTitle} used as row identifier
 * @param <F>
 *            class used as column identifier -for the {@link HierarchicalPanelKind#VALUE}, "central" panel
 * @param <L>
 *            line
 * @see IHierarchicalLine
 */
public class JSyHierarchicalPanel<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends JPanel implements PropertyChangeListener {

	private static final long serialVersionUID = 53612385072836351L;

	private final ConfigurationContext<E, F, L> configurationContext;

	private final Map<HierarchicalPanelKind, HierarchicalPanel<?, E, F, L>> hierarchicalPanels;

	private final HierarchicalLineComparator<L> lineComparator;

	private final Map<IField, IHierarchicalTitleColumnElement> modelRegroupmentCriteria;

	private List<L> hierarchicalModel;

	private HierarchicalSelectionModel<E, F, L> selectionModel;

	private JScrollBar verticalScrollBar;

	private JScrollBar horizontalScrollBar;

	private JCommandButton exportCommandButton;

	private boolean titleColumnIsAdjusting;

	/**
	 * 
	 * @param hierarchicalContext
	 * @param title
	 * @param translationBundle
	 *            used internally to translate the titles column.
	 * @param modelClass
	 */
	public JSyHierarchicalPanel(final IHierarchicalContext<E, F, L> hierarchicalContext, final String title, final ConstantsWithLookingBundle translationBundle, final Class<L> modelClass) {
		this(hierarchicalContext.buildConfigurationContext(title, translationBundle, modelClass));
	}

	public JSyHierarchicalPanel(final ConfigurationContext<E, F, L> configurationContext) {
		super();
		this.hierarchicalPanels = new LinkedHashMap<HierarchicalPanelKind, HierarchicalPanel<?, E, F, L>>();
		this.configurationContext = configurationContext;
		this.lineComparator = new HierarchicalLineComparator<L>(configurationContext);
		this.modelRegroupmentCriteria = new LinkedHashMap<IField, IHierarchicalTitleColumnElement>();

		initComponents();
		buildContents();
	}

	private void initComponents() {
		setFocusable(true);
		createScrollBars();
		createExportCommandButton();
		for (final HierarchicalPanelKind panelKind : HierarchicalPanelKind.values()) {
			createAndStoreHierarchicalPanel(panelKind);
		}
		createAndInitSelectionModel();
		registerListeners();

		install();
	}

	private void createScrollBars() {
		this.horizontalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 0);
		this.horizontalScrollBar.setBlockIncrement(this.configurationContext.getCellWidth());
		this.horizontalScrollBar.setUnitIncrement(this.configurationContext.getCellWidth());

		this.verticalScrollBar = new JScrollBar(JScrollBar.VERTICAL, 0, 0, 0, 0);
		this.verticalScrollBar.setBlockIncrement(this.configurationContext.getCellHeight());
		this.verticalScrollBar.setUnitIncrement(this.configurationContext.getCellHeight());
	}

	private void createExportCommandButton() {
		exportCommandButton = new JCommandButton(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().exportEllipsis(), ImageWrapperResizableIcon.getIcon(IconHelper.Icons.EXPORT_EXCEL.getFileURL(),
				new Dimension(16, 16)));
		exportCommandButton.setCommandButtonKind(CommandButtonKind.ACTION_ONLY);
		exportCommandButton.setDisplayState(CommandButtonDisplayState.MEDIUM);
		exportCommandButton.addActionListener(new ExportAction());
		exportCommandButton.setEnabled(false);
	}

	private void createAndStoreHierarchicalPanel(final HierarchicalPanelKind panelKind) {
		final HierarchicalPanelFactory<?, E, F, L> factory = this.configurationContext.getPanelFactory(panelKind);
		factory.install(configurationContext);
		final HierarchicalPanel<?, E, F, L> hierarchicalPanel = factory.buildHierarchicalPanel(this);
		this.hierarchicalPanels.put(panelKind, hierarchicalPanel);
		addPropertyChangeListener(hierarchicalPanel);
	}

	private void createAndInitSelectionModel() {
		this.selectionModel = new HierarchicalSelectionModel<E, F, L>(this.configurationContext.getModelClass());
		for (final HierarchicalPanel<?, E, F, L> hierarchicalPanel : this.hierarchicalPanels.values()) {
			this.selectionModel.addSelectionModelListener(hierarchicalPanel);
		}
		this.selectionModel.addSelectionModelListener(new MyHierarchicalSelectionModelListener());
		firePropertyChange(ChangePropertyName.SELECTION_MODEL.name(), null, this.selectionModel);
	}

	private void registerListeners() {
		this.verticalScrollBar.getModel().addChangeListener(new ScrollbarChangeListener(ChangePropertyName.VERTICAL_SCROLL));
		this.verticalScrollBar.addMouseWheelListener(new HierarchicalPanelMouseWheelListener(this.verticalScrollBar));
		this.horizontalScrollBar.getModel().addChangeListener(new ScrollbarChangeListener(ChangePropertyName.HORIZONTAL_SCROLL));
		this.horizontalScrollBar.addMouseWheelListener(new HierarchicalPanelMouseWheelListener(this.horizontalScrollBar));
		addKeyListener(new MyKeyListener());
	}

	private void install() {
		if (configurationContext.hasColumnControl()) {
			configurationContext.getColumnControl().install(this);
		}

		recalculateComponent();
	}

	private void buildContents() {
		final FormLayout layout = new FormLayout("FILL:P:NONE,	FILL:D:GROW,	FILL:P:NONE,	FILL:P:NONE", "FILL:P:NONE,	FILL:D:GROW,	FILL:P:NONE,	FILL:P:NONE");
		final PanelBuilder builder = new PanelBuilder(layout, this);
		final CellConstraints cc = new CellConstraints();

		builder.add(getHeaderForHierarchicalPanelKind(HierarchicalPanelKind.TITLE), cc.xy(1, 1));
		builder.add(getContentForHierarchicalPanelKind(HierarchicalPanelKind.TITLE), cc.xy(1, 2));
		if (this.configurationContext.isShowSummaryLine()) {
			builder.add(getFooterForHierarchicalPanelKind(HierarchicalPanelKind.TITLE), cc.xy(1, 3));
		}

		builder.add(getHeaderForHierarchicalPanelKind(HierarchicalPanelKind.VALUE), cc.xy(2, 1));
		builder.add(getContentForHierarchicalPanelKind(HierarchicalPanelKind.VALUE), cc.xy(2, 2));
		if (this.configurationContext.isShowSummaryLine()) {
			builder.add(getFooterForHierarchicalPanelKind(HierarchicalPanelKind.VALUE), cc.xy(2, 3));
		}

		if (this.configurationContext.isShowSummaryPanel()) {
			builder.add(getHeaderForHierarchicalPanelKind(HierarchicalPanelKind.SUMMARY), cc.xy(3, 1));
			builder.add(getContentForHierarchicalPanelKind(HierarchicalPanelKind.SUMMARY), cc.xy(3, 2));
			if (this.configurationContext.isShowSummaryLine()) {
				builder.add(getFooterForHierarchicalPanelKind(HierarchicalPanelKind.SUMMARY), cc.xy(3, 3));
			}
		}
		builder.add(this.verticalScrollBar, cc.xy(4, 2));
		builder.add(this.horizontalScrollBar, cc.xy(2, 4));

		if (configurationContext.hasExportWriter()) {
			builder.appendRelatedComponentsGapRow();
			builder.appendRow("FILL:P:NONE");
			builder.add(exportCommandButton, cc.xyw(1, 6, 4, CellConstraints.RIGHT, CellConstraints.FILL));
		}

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				for (HierarchicalPanel<?, E, F, L> hierarchicalPanel : hierarchicalPanels.values()) {
					hierarchicalPanel.updateScrolls();
				}
			}
		});
	}

	private JComponent getHeaderForHierarchicalPanelKind(final HierarchicalPanelKind panelKind) {
		return this.hierarchicalPanels.get(panelKind).getDisplayableHeader();
	}

	private JComponent getContentForHierarchicalPanelKind(final HierarchicalPanelKind panelKind) {
		return this.hierarchicalPanels.get(panelKind).getDisplayableContent();
	}

	private JComponent getFooterForHierarchicalPanelKind(final HierarchicalPanelKind panelKind) {
		return this.hierarchicalPanels.get(panelKind).getDisplayableFooter();
	}

	public void setModelAndRecalculateComponent(final List<L> newModel) {
		this.hierarchicalModel = newModel;
		recalculateComponent();
		firePropertyChange(ChangePropertyName.MODEL.name(), null, this.hierarchicalModel);
	}

	private void recalculateComponent() {
		if (CollectionHelper.isEmpty(this.hierarchicalModel)) {
			clear();
			exportCommandButton.setEnabled(false);
		} else {
			consolidateModel();
			sortModelIfRequested();
			exportCommandButton.setEnabled(true);
		}
		List<F> valueColumnDefinitionList = calculateColumnDefinitionListFromModelAndSortIfAvailable();
		this.selectionModel.initialize(this.hierarchicalModel, valueColumnDefinitionList);
	}

	private void consolidateModel() {
		HierarchicalDataHelper.consolidateModel(this.hierarchicalModel);
	}

	private void sortModelIfRequested() {
		if (canRegroupModel()) {
			this.lineComparator.setModelRegroupmentCriteria(new ArrayList<IHierarchicalTitleColumnElement>(this.modelRegroupmentCriteria.values()));
			Collections.sort(this.hierarchicalModel, this.lineComparator);
		}
	}

	private boolean canRegroupModel() {
		return CollectionHelper.isNotEmpty(this.hierarchicalModel) && CollectionHelper.isNotEmpty(this.modelRegroupmentCriteria) && this.configurationContext.isShouldRegroupModel();
	}

	private List<F> calculateColumnDefinitionListFromModelAndSortIfAvailable() {
		final List<F> columnDefinitionList = new ArrayList<F>();
		List<F> columnDefinitionFromConfigurationContext = configurationContext.getForcedColumnDefinitionList();
		if (CollectionHelper.isEmpty(columnDefinitionFromConfigurationContext)) {
			if (this.hierarchicalModel != null) {
				for (final L hierarchicalLine : this.hierarchicalModel) {
					addAllColumnDefinitionElementIfNotPresent(hierarchicalLine, columnDefinitionList);
				}
			}
		} else {
			columnDefinitionList.addAll(columnDefinitionFromConfigurationContext);
		}
		sortModelColumnDefinition(columnDefinitionList);
		setValueColumnDefinition(columnDefinitionList);
		return columnDefinitionList;
	}

	private void addAllColumnDefinitionElementIfNotPresent(final L hierarchicalLine, final List<F> columnDefinitionList) {
		for (final F collectionDefinitionElement : hierarchicalLine.getValuesMap().keySet()) {
			if (!columnDefinitionList.contains(collectionDefinitionElement)) {
				columnDefinitionList.add(collectionDefinitionElement);
			}
		}
	}

	private void sortModelColumnDefinition(final List<F> columnDefinitionList) {
		if (this.configurationContext.getValueColumnSorter() != null) {
			CollectionHelper.sort(columnDefinitionList, this.configurationContext.getValueColumnSorter());
		}
	}

	private void clear() {
		this.selectionModel.cleanSelectionModel();
	}

	/**
	 * Configure the fields to be used in the first column ({@link HierarchicalPanelKind#TITLE}). The order of the fileds in the list matters. <br />
	 * <br />
	 * For each IField in this list, a sub-column will be created. When a model will be given to the {@link JSyHierarchicalPanel}, fields from the {@link IHierarchicalLine#getTitleComponent()} will be
	 * used to fill those sub-columns. If the fields in columnDefinition do not match the fields in the titleComponent, a null value will be displayed.<br />
	 * Moreover, if {@link ConfigurationContext#isShouldRegroupModel()} is true, then in each subcolumn the similar lines will be regrouped.
	 * 
	 * @param columnDefinitions
	 */
	public void setTitleColumnDefinition(final List<IHierarchicalTitleColumnElement> columnDefinitions) {
		this.modelRegroupmentCriteria.clear();
		for (IHierarchicalTitleColumnElement element : columnDefinitions) {
			this.modelRegroupmentCriteria.put(element.getColumnElement(), element);
		}
		sortModelIfRequested();
		List<IField> titleColumnDefinitionList = HierarchicalDataHelper.convertTitleColumnElementListToFieldList(columnDefinitions);
		firePropertyChange(ChangePropertyName.TITLE_COLUMNS.name(), null, titleColumnDefinitionList);
	}

	public final List<IHierarchicalTitleColumnElement> getTitleColumnDefinitions() {
		return new ArrayList<IHierarchicalTitleColumnElement>(this.modelRegroupmentCriteria.values());
	}

	/**
	 * Configure the fields to be used in the first column ({@link HierarchicalPanelKind#TITLE}). The order of the fileds in the list matters. <br />
	 * <br />
	 * For each IField in this list, a sub-column will be created. When a model will be given to the {@link JSyHierarchicalPanel}, fields from the {@link IHierarchicalLine#getTitleComponent()} will be
	 * used to fill those sub-columns. If the fields in columnDefinition do not match the fields in the titleComponent, a null value will be displayed.<br />
	 * Moreover, if {@link ConfigurationContext#isShouldRegroupModel()} is true, then in each subcolumn the similar lines will be regrouped.
	 * 
	 * @param columnDefinitions
	 */
	public void setTitleColumnDefinition(final IHierarchicalTitleColumnElement... columnDefinitions) {
		setTitleColumnDefinition(Arrays.asList(columnDefinitions));
	}

	/**
	 * Get the title column definition corresponding to given field
	 * 
	 * @param field
	 * @return
	 */
	public IHierarchicalTitleColumnElement getTitleColumnDefinition(IField field) {
		return this.modelRegroupmentCriteria.get(field);
	}

	/**
	 * Add a listener to detect changes in title columns
	 * 
	 * @param titleColumnChangeListener
	 */
	public void addTitleColumnChangeListener(PropertyChangeListener titleColumnChangeListener) {
		addPropertyChangeListener(ChangePropertyName.TITLE_COLUMNS.name(), titleColumnChangeListener);
	}

	/**
	 * Configures the names and formulae to use on the third column ({@link HierarchicalPanelKind#SUMMARY}). Calling this method has no effect if {@link ConfigurationContext#isShowSummaryPanel()} is
	 * false.
	 * 
	 * @param columnDefinitions
	 * @see ConfigurationContext#setShowSummaryPanel(boolean)
	 */
	public void setSummaryColumnDefinition(final HierarchicalSummaryRule<E, F, L>... summaryRuleList) {
		setSummaryColumnDefinition(Arrays.asList(summaryRuleList));
	}

	/**
	 * Configures the names and formulae to use on the third column ({@link HierarchicalPanelKind#SUMMARY}). Calling this method has no effect if {@link ConfigurationContext#isShowSummaryPanel()} is
	 * false.
	 * 
	 * @param columnDefinitions
	 * @see ConfigurationContext#setShowSummaryPanel(boolean)
	 */
	public void setSummaryColumnDefinition(final List<? extends HierarchicalSummaryRule<E, F, L>> summaryRuleList) {
		if (this.configurationContext.isShowSummaryPanel()) {
			firePropertyChange(ChangePropertyName.SUMMARY_COLUM.name(), null, summaryRuleList);
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		if (isValidScrollbarEvent(evt) && ChangePropertyName.VERTICAL_POSITION.name().equals(evt.getPropertyName())) {
			updateVerticalScrollBarMOdel((BoundedRangeModel) evt.getNewValue());
		} else if (isValidScrollbarEvent(evt) && ChangePropertyName.HORIZONTAL_POSITION.name().equals(evt.getPropertyName())) {
			updateHorizontalScrollBarModel((BoundedRangeModel) evt.getNewValue());
		} else if (isValidWheelEvent(evt) && ChangePropertyName.VERTICAL_WHEEL.name().equals(evt.getPropertyName())) {
			setVerticalScrollbarValue((Number) evt.getNewValue());
		} else if (isValidWheelEvent(evt) && ChangePropertyName.HORIZONTAL_WHEEL.name().equals(evt.getPropertyName())) {
			setHorizontalScrollbarValue((Number) evt.getNewValue());
		} else if (isValidLineOrderChangeEvent(evt) && ChangePropertyName.LINE_ORDER_CHANGED.equals(evt.getPropertyName())) {
			changeLineOrderForField((IField) evt.getNewValue());
		}
	}

	private boolean isValidScrollbarEvent(final PropertyChangeEvent evt) {
		return evt != null && evt.getNewValue() != null && BoundedRangeModel.class.isAssignableFrom(evt.getNewValue().getClass());
	}

	private boolean isValidWheelEvent(final PropertyChangeEvent evt) {
		return evt != null && evt.getNewValue() != null && Number.class.isAssignableFrom(evt.getNewValue().getClass());
	}

	private boolean isValidLineOrderChangeEvent(final PropertyChangeEvent evt) {
		return evt != null && evt.getNewValue() != null && IField.class.isAssignableFrom(evt.getNewValue().getClass());
	}

	private void updateVerticalScrollBarMOdel(final BoundedRangeModel rangeModel) {
		if (isNewModelDifferent(this.verticalScrollBar.getModel(), rangeModel)) {
			this.verticalScrollBar.setValues(rangeModel.getValue(), rangeModel.getExtent(), rangeModel.getMinimum(), rangeModel.getMaximum());
		}
	}

	private boolean isNewModelDifferent(final BoundedRangeModel oldModel, final BoundedRangeModel newModel) {
		final boolean sameMinimum = oldModel.getMinimum() == newModel.getMinimum();
		final boolean sameMaximum = oldModel.getMaximum() == newModel.getMaximum();
		final boolean sameExtent = oldModel.getExtent() == newModel.getExtent();
		final boolean sameValue = oldModel.getValue() == newModel.getValue();
		return !(sameMinimum && sameMaximum && sameExtent && sameValue);
	}

	private void updateHorizontalScrollBarModel(final BoundedRangeModel rangeModel) {
		if (isNewModelDifferent(this.horizontalScrollBar.getModel(), rangeModel)) {
			this.horizontalScrollBar.setValues(rangeModel.getValue(), rangeModel.getExtent(), rangeModel.getMinimum(), rangeModel.getMaximum());
		}
	}

	private void setVerticalScrollbarValue(final Number valueToAdd) {
		this.verticalScrollBar.setValue(this.verticalScrollBar.getValue() + valueToAdd.intValue() * this.verticalScrollBar.getBlockIncrement());
	}

	private void setHorizontalScrollbarValue(final Number valueToAdd) {
		this.horizontalScrollBar.setValue(this.horizontalScrollBar.getValue() + valueToAdd.intValue() * this.horizontalScrollBar.getBlockIncrement());
	}

	private void changeLineOrderForField(final IField field) {
		this.lineComparator.reverseOrderForField(field);
		setModelAndRecalculateComponent(this.hierarchicalModel);
	}

	public HierarchicalSelectionModel<E, F, L> getSelectionModel() {
		return this.selectionModel;
	}

	public void addListenerToHeaderPanel(final HierarchicalPanelKind panelKind, final MouseListener mouseListener) {
		this.hierarchicalPanels.get(panelKind).addListenerToHeaderPanel(mouseListener);
	}

	public void addListenerToHeaderPanel(final HierarchicalPanelKind panelKind, final MouseMotionListener mouseListener) {
		this.hierarchicalPanels.get(panelKind).addListenerToHeaderPanel(mouseListener);
	}

	public void addListenerToContentPanel(final HierarchicalPanelKind panelKind, final MouseListener mouseListener) {
		this.hierarchicalPanels.get(panelKind).addListenerToContentPanel(mouseListener);
	}

	public void addListenerToContentPanel(final HierarchicalPanelKind panelKind, final MouseMotionListener mouseListener) {
		this.hierarchicalPanels.get(panelKind).addListenerToContentPanel(mouseListener);
	}

	public void changeTitleColumnVisibility(final IField field, final boolean visible) {
		IHierarchicalTitleColumnElement foundHierarchicalTitleColumnElement = this.modelRegroupmentCriteria.get(field);
		if (foundHierarchicalTitleColumnElement != null) {
			foundHierarchicalTitleColumnElement.setColumnVisible(visible);
			if (!titleColumnIsAdjusting) {
				fireTitleColumnDefinition();
			}
		}
	}

	private void fireTitleColumnDefinition() {
		final List<IField> columnDefinitionAsFieldList = HierarchicalDataHelper.convertTitleColumnElementListToFieldList(new ArrayList<IHierarchicalTitleColumnElement>(this.modelRegroupmentCriteria
				.values()));
		firePropertyChange(ChangePropertyName.TITLE_COLUMNS.name(), null, columnDefinitionAsFieldList);
	}

	/**
	 * Use this if and only if you need to dynamically change the value column definition. <br/>
	 * When the model is loaded, the forced column definition from the configuration context is loaded if available, or deduced from the model
	 * 
	 * @param columnDefinitionList
	 */
	public final void setValueColumnDefinition(List<F> columnDefinitionList) {
		firePropertyChange(ChangePropertyName.VALUE_COLUMN.name(), null, columnDefinitionList);
	}

	public void setTitleColumnIsAdjusting(boolean titleColumnIsAdjusting) {
		this.titleColumnIsAdjusting = titleColumnIsAdjusting;
		if (!titleColumnIsAdjusting) {
			fireTitleColumnDefinition();
		}
	}

	public final int getVerticalCellSpan(final int rowIndex, final int columnIndex) {
		return hierarchicalPanels.get(HierarchicalPanelKind.TITLE).getContent().getVerticalCellSpan(rowIndex, columnIndex);
	}

	private class ScrollbarChangeListener implements ChangeListener {

		private final ChangePropertyName eventNameToFire;

		private ScrollbarChangeListener(final ChangePropertyName eventNameToFire) {
			this.eventNameToFire = eventNameToFire;
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			firePropertyChange(this.eventNameToFire.name(), null, e.getSource());
		}
	}

	private final class HierarchicalPanelMouseWheelListener implements MouseWheelListener {

		final JScrollBar scrollbar;

		public HierarchicalPanelMouseWheelListener(final JScrollBar scrollbar) {
			this.scrollbar = scrollbar;
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			this.scrollbar.setValue(this.scrollbar.getValue() + e.getWheelRotation() * this.scrollbar.getBlockIncrement());
		}
	}

	private class MyKeyListener extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_C) {
				Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
				StringSelection stringSelection = new StringSelection(selectionModel.getSelectedValue());
				c.setContents(stringSelection, stringSelection);
			}
		}
	}

	private final class MyHierarchicalSelectionModelListener implements HierarchicalSelectionModelListener {

		@Override
		public void selectionChanged() {
			requestFocus();
		}
	}

	private final class ExportAction extends AbstractAction {

		private static final long serialVersionUID = 3568490886341781406L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (CollectionHelper.isNotEmpty(hierarchicalModel)) {
				int maxPanels = configurationContext.isShowSummaryPanel() ? 3 : 2;
				int maxLines = configurationContext.isShowSummaryLine() ? 3 : 2;
				IExportableTable[][] exportableTables = new IExportableTable[maxLines][maxPanels];
				Collection<HierarchicalPanel<?, E, F, L>> hierarchicalPanels = JSyHierarchicalPanel.this.hierarchicalPanels.values();
				int i = 0;
				for (HierarchicalPanel<?, E, F, L> hierarchicalPanel : hierarchicalPanels) {
					if (i < maxPanels) {
						exportableTables[0][i] = hierarchicalPanel.getHeader();
						exportableTables[1][i] = hierarchicalPanel.getContent();
						if (maxLines >= 2) {
							exportableTables[2][i] = hierarchicalPanel.getFooter();
						}
					}
					i++;
				}
				configurationContext.getHierarchicalContext().export(exportableTables);
			}
		}
	}
}
