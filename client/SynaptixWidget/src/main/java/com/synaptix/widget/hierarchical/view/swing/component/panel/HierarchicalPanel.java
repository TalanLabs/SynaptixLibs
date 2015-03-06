package com.synaptix.widget.hierarchical.view.swing.component.panel;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.synaptix.common.helper.CollectionHelper;
import com.synaptix.component.IComponent;
import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;
import com.synaptix.widget.hierarchical.context.ConfigurationContext;
import com.synaptix.widget.hierarchical.view.swing.component.ChangePropertyName;
import com.synaptix.widget.hierarchical.view.swing.component.HierarchicalPanelKind;
import com.synaptix.widget.hierarchical.view.swing.model.HierarchicalSelectionModel;
import com.synaptix.widget.hierarchical.view.swing.model.HierarchicalSelectionModelListener;

/**
 * 
 * @author psourisse
 * 
 * @param <U>
 *            the type of column header
 */
public abstract class HierarchicalPanel<U, E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> extends JPanel implements PropertyChangeListener,
		HierarchicalSelectionModelListener {

	private static final long serialVersionUID = -6027968471919595135L;

	private static final int HORIZONTAL_OFFSET = 10;

	protected final List<U> columnDefinitionList;

	protected final List<Integer> columnsSize;

	protected final ConfigurationContext<E, F, L> configurationContext;

	private HeaderPanel<U, E, F, L> header;

	private ContentPanel<U, E, F, L> content;

	private FooterPanel<U, E, F, L> footer;

	private List<L> model;

	protected HierarchicalSelectionModel<E, F, L> selectionModel;

	protected boolean isEnabledListeners;

	public HierarchicalPanel(final ConfigurationContext<E, F, L> configurationContext) {
		super(new BorderLayout());
		this.columnDefinitionList = new ArrayList<U>();
		this.columnsSize = new ArrayList<Integer>();
		this.configurationContext = configurationContext;
		this.isEnabledListeners = false;
	}

	public abstract HierarchicalPanelKind getPanelKind();

	public final void updateScrolls() {
		setEnableListeners(getModelSize() > 0 || columnDefinitionList.size() > 0);
		recalculatePosition();
	}

	/**
	 * Recalculate the position of the view to update scrolls
	 */
	protected abstract void recalculatePosition();

	protected abstract ChangePropertyName getKeyPropertyColumnTitleChanged();

	protected void setColumnsDefinitionAndCalculateColumnWidthIfRequired(final List<U> columnsDefinition) {
		this.columnDefinitionList.clear();
		if (CollectionHelper.isNotEmpty(columnsDefinition)) {
			this.columnDefinitionList.addAll(columnsDefinition);
		}
		updateColumnSize();
	}

	public final void updateColumnSize() {
		if (!columnDefinitionList.isEmpty()) {
			final int panelWidth = resizeColumnsAndCalculateTotalWidth();
			resizeWidth(panelWidth);
		}
		resizeAndRepaint();
	}

	private final int resizeColumnsAndCalculateTotalWidth() {
		this.columnsSize.clear();
		int totalWidth = 0;
		for (int columnIndex = 0; columnIndex < this.columnDefinitionList.size(); columnIndex++) {
			final int columnWidth = computeColumnWidth(columnIndex);
			this.columnsSize.add(columnWidth);
			totalWidth += columnWidth;
		}
		return totalWidth;
	}

	protected int computeColumnWidth(int columnIndex) {
		final String columnLabel = getColumnLabelAt(columnIndex);
		final int columnLabelSize = SwingUtilities.computeStringWidth(getFontMetrics(getColumnTitleFont()), columnLabel);
		final int columnWidth = calculateColumnWidthWithContentSize(columnLabelSize);
		return columnWidth;
	}

	public abstract Font getColumnTitleFont();

	protected void resizeWidth(final int panelWidth) {
		this.header.updateWidth(panelWidth);
		this.content.updateWidth(panelWidth);
		if (this.configurationContext.isShowSummaryLine()) {
			this.footer.updateWidth(panelWidth);
		}
	}

	protected void setModelAndComputePanelContentsIfRequired(final List<L> model) {
		this.model = model;
		setEnableListeners(getModelSize() > 0 || columnDefinitionList.size() > 0);
		computeContents();
		resizeAndRepaint();
	}

	protected void setEnableListeners(final boolean isEnabled) {
		this.isEnabledListeners = isEnabled;
		this.content.setEnableListeners(isEnabled);
	}

	protected void computeContents() {
		this.content.computeContents(this.model);
		if (this.configurationContext.isShowSummaryLine()) {
			this.footer.computeContents(this.model);
		}
	}

	public abstract String getColumnLabelAt(final int columnIndex);

	public U getColumnAtAbsciss(final int absciss) {
		return getColumnDefinitionAt(getColumnIndexAtAbsciss(absciss));
	}

	public int getColumnIndexAtAbsciss(final int absciss) {
		int currentColumnIndex = 0;
		int currentColumnRightAbsciss = getColumnSizeAt(currentColumnIndex);
		while (absciss > currentColumnRightAbsciss && currentColumnIndex + 1 < getColumnDefinitionList().size()) {
			currentColumnIndex++;
			currentColumnRightAbsciss += getColumnSizeAt(currentColumnIndex);
		}
		return currentColumnIndex;
	}

	public L getRowAtOrdinate(final int ordinate) {
		return getModel().get(getRowIndexAtOrdinate(ordinate));
	}

	public int getRowIndexAtOrdinate(final int ordinate) {
		return Math.min(ordinate / configurationContext.getCellHeight(), getModelSize() - 1);
	}

	public boolean isValidOrdinate(final double ordinate) {
		return ordinate > 0 && (ordinate / configurationContext.getCellHeight()) < (getModelSize() + 1);
	}

	public boolean isValidAbsciss(final double absciss) {
		int maxAbsciss = 0;
		for (int i = 0; i < getColumnDefinitionList().size(); i++) {
			maxAbsciss += getColumnSizeAt(i);
		}
		return absciss > 0 && maxAbsciss > absciss;
	}

	protected int calculateColumnWidthWithContentSize(final int contentSize) {
		return contentSize + HORIZONTAL_OFFSET * 2;
	}

	protected void initialize() {
		buildContents();
	}

	protected abstract void buildContents();

	public final HeaderPanel<U, E, F, L> getHeader() {
		return this.header;
	}

	protected void setHeader(final HeaderPanel<U, E, F, L> header) {
		this.header = header;
	}

	public final ContentPanel<U, E, F, L> getContent() {
		return this.content;
	}

	protected void setContent(final ContentPanel<U, E, F, L> content) {
		this.content = content;
	}

	public final FooterPanel<U, E, F, L> getFooter() {
		return this.footer;
	}

	protected void setFooter(final FooterPanel<U, E, F, L> footer) {
		this.footer = footer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		if (ChangePropertyName.MODEL.name().equals(evt.getPropertyName())) {
			setModelAndComputePanelContentsIfRequired((List<L>) evt.getNewValue());
		} else if (getKeyPropertyColumnTitleChanged().name().equals(evt.getPropertyName())) {
			setColumnsDefinitionAndCalculateColumnWidthIfRequired((List<U>) evt.getNewValue());
		} else if (ChangePropertyName.VERTICAL_SCROLL.name().equals(evt.getPropertyName()) && isValidScrollEvent(evt)) {
			updateVerticalView((BoundedRangeModel) evt.getNewValue());
		} else if (ChangePropertyName.HORIZONTAL_SCROLL.name().equals(evt.getPropertyName()) && isValidScrollEvent(evt)) {
			updateHorizontalView((BoundedRangeModel) evt.getNewValue());
		} else if (ChangePropertyName.SELECTION_MODEL.name().equals(evt.getPropertyName()) && isValidSelectionModelEvent(evt)) {
			this.selectionModel = (HierarchicalSelectionModel<E, F, L>) evt.getNewValue();
		}
	}

	private boolean isValidScrollEvent(final PropertyChangeEvent evt) {
		return BoundedRangeModel.class.isAssignableFrom(evt.getNewValue().getClass()) && this.isEnabledListeners;
	}

	private boolean isValidSelectionModelEvent(final PropertyChangeEvent evt) {
		return HierarchicalSelectionModel.class.isAssignableFrom(evt.getNewValue().getClass());
	}

	protected abstract void updateVerticalView(final BoundedRangeModel rangeModel);

	protected abstract void updateHorizontalView(final BoundedRangeModel rangeModel);

	public final List<U> getColumnDefinitionList() {
		return columnDefinitionList;
	}

	public final Integer getColumnSizeAt(final int columnIndex) {
		return this.columnsSize.get(columnIndex);
	}

	public Integer getColumnAbscissAt(final int columnIndex) {
		int absciss = 0;
		int maxIndex = Math.min(columnIndex + 1, this.columnsSize.size());
		for (int index = 0; index < maxIndex; index++) {
			absciss += this.columnsSize.get(index);
		}
		return absciss;
	}

	public Integer getLineOrdinateAt(final int rowIndex) {
		return rowIndex * configurationContext.getCellHeight();
	}

	public final U getColumnDefinitionAt(final int columnIndex) {
		return this.columnDefinitionList.get(columnIndex);
	}

	public List<L> getModel() {
		return this.model;
	}

	public int getModelSize() {
		if (CollectionHelper.isNotEmpty(this.model)) {
			return this.model.size();
		}
		return 0;
	}

	protected ConstantsWithLookingBundle getTranslationBundle() {
		return this.configurationContext.getTranslationBundle();
	}

	public <T> String getStringFromObject(final T object) {
		return this.configurationContext.getStringFromObject(object);
	}

	public JComponent getDisplayableContent() {
		return this.content;
	}

	public JComponent getDisplayableHeader() {
		return this.header;
	}

	public JComponent getDisplayableFooter() {
		return this.footer;
	}

	public abstract void resizeAndRepaint();

	public void setShowFooter(final boolean showTotalRow) {
		getDisplayableFooter().setVisible(showTotalRow);
	}

	@Override
	public void setVisible(boolean isVisible) {
		super.setVisible(isVisible);
		getDisplayableHeader().setVisible(isVisible);
		getDisplayableContent().setVisible(isVisible);
		getDisplayableFooter().setVisible(isVisible);
	}

	@Override
	public void selectionChanged() {
		repaintAll();
	}

	protected void repaintAll() {
		this.header.repaint();
		this.content.repaint();
		this.footer.repaint();
	}

	protected boolean isSelectionModelAvailable() {
		return this.selectionModel != null && CollectionHelper.isNotEmpty(this.model);
	}

	public void setSelectedValue(final String selectedValueAsString) {
		if (isSelectionModelAvailable()) {
			this.selectionModel.setSelectedValue(selectedValueAsString);
		}
	}

	public void unselectAll() {
		if (isSelectionModelAvailable()) {
			this.selectionModel.cleanSelectionModel();
		}
	}

	public void selectAll() {
		if (isSelectionModelAvailable()) {
			this.selectionModel.selectAllModel();
		}
	}

	public void changeSelectionStatusAt(final int lineIndex, final int columnIndex) {
		if (isSelectionModelAvailable()) {
			this.selectionModel.changeSelectionStatusAt(lineIndex, columnIndex);
		}
	}

	public void changeColumnSelectionStatusAt(final int columnIndex) {
		if (isSelectionModelAvailable()) {
			this.selectionModel.changeColumnSelectionStatusAt(columnIndex);
		}
	}

	public void changeRowSelectionStatusAt(final int rowIndex) {
		if (isSelectionModelAvailable()) {
			this.selectionModel.changeRowSelectionStatusAt(rowIndex);
		}
	}

	public void changeRowSelectionStatusByRange(final int lowerBoundIndex, final int upperBoundIndex) {
		if (isSelectionModelAvailable()) {
			this.selectionModel.changeRowSelectionStatusByRange(lowerBoundIndex, upperBoundIndex);
		}
	}

	public void changeColumnSelectionStatusByRange(final int lowerBoundIndex, final int upperBoundIndex) {
		if (isSelectionModelAvailable()) {
			this.selectionModel.changeColumnSelectionStatusByRange(lowerBoundIndex, upperBoundIndex);
		}
	}

	public boolean isRowSelected(final int rowIndex) {
		if (isSelectionModelAvailable()) {
			return this.selectionModel.isRowSelected(rowIndex);
		}
		return false;
	}

	public boolean isWholeLineSelected(final int rowIndex) {
		if (isSelectionModelAvailable()) {
			return this.selectionModel.isWholeRowSelected(rowIndex);
		}
		return false;
	}

	public boolean isWholeColumnSelected(final int columnIndex) {
		if (isSelectionModelAvailable()) {
			return this.selectionModel.isWholeColumnSelected(columnIndex);
		}
		return false;
	}

	public boolean isCellSelected(final int rowIndex, final int columnIndex) {
		if (isSelectionModelAvailable()) {
			return this.selectionModel.isSelectedAt(rowIndex, columnIndex);
		}
		return false;
	}

	public boolean isAllModelSelected() {
		if (isSelectionModelAvailable()) {
			return this.selectionModel.isAllModelSelected();
		}
		return false;
	}

	public boolean isLastColumn(final int columnIndex) {
		return getColumnDefinitionList().size() == columnIndex + 1;
	}

	public void copySelectedValueToClipboard() {
		if (isSelectionModelAvailable()) {
			Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection stringSelection = new StringSelection(this.selectionModel.getSelectedValue());
			c.setContents(stringSelection, stringSelection);
		}
	}

	public void addListenerToContentPanel(final MouseListener mouseListener) {
		this.content.addMouseListener(mouseListener);
	}

	public void addListenerToContentPanel(final MouseMotionListener mouseListener) {
		this.content.addMouseMotionListener(mouseListener);
	}

	public void addListenerToHeaderPanel(final MouseMotionListener mouseListener) {
		this.header.addMouseMotionListener(mouseListener);
	}

	public void addListenerToHeaderPanel(final MouseListener mouseListener) {
		this.header.addMouseListener(mouseListener);
	}

	public HierarchicalCellRenderer<E, F, L> getCellRenderer() {
		return this.configurationContext.getCellRendererForPanelKind(getPanelKind());
	}

	public final void setSelectionIsAdjusting(boolean selectionIsAdjusting) {
		selectionModel.setSelectionIsAdjusting(selectionIsAdjusting);
	}
}
