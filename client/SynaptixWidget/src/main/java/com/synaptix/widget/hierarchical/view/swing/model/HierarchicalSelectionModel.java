package com.synaptix.widget.hierarchical.view.swing.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.synaptix.component.IComponent;
import com.synaptix.component.factory.ComponentFactory;
import com.synaptix.service.hierarchical.model.IHierarchicalLine;

public class HierarchicalSelectionModel<E extends IComponent, F extends Serializable, L extends IHierarchicalLine<E, F>> {

	public enum SelectionMode {
		SINGLE_SELECTION, MULTIPLE_SELECTION
	}

	private int nbRows;

	private int nbColumns;

	private boolean[][] selectionModel;

	private List<HierarchicalSelectionModelListener> selectionModelListeners;

	private List<L> model;

	private List<F> columnList;

	private String selectedValue;

	private SelectionMode selectionMode;

	private Map<SelectionMode, SpecificHierarchicalSelectionModel> availableSelectionModel;

	private SpecificHierarchicalSelectionModel currentSelectionModel;

	private Class<L> modelClass;

	private HoverCell hoverCell;

	private IHoverChangeCallback hoverChangeCallback;

	private boolean selectionIsAdjusting = false;

	public HierarchicalSelectionModel(final Class<L> modelClass) {
		this.selectionModelListeners = new ArrayList<HierarchicalSelectionModelListener>();
		this.availableSelectionModel = new HashMap<HierarchicalSelectionModel.SelectionMode, SpecificHierarchicalSelectionModel>();
		this.modelClass = modelClass;
		initAvailableSelectionMode();
		setSelectionMode(SelectionMode.MULTIPLE_SELECTION);
	}

	private void initAvailableSelectionMode() {
		this.availableSelectionModel.put(SelectionMode.SINGLE_SELECTION, new SingleSelectionModel(this));
		this.availableSelectionModel.put(SelectionMode.MULTIPLE_SELECTION, new MultipleSelectionModel(this));
	}

	protected final int getNbColumn() {
		return this.nbColumns;
	}

	protected final int getNbRows() {
		return this.nbRows;
	}

	public final void setSelectionMode(final SelectionMode selectionMode) {
		this.selectionMode = selectionMode;
		this.currentSelectionModel = this.availableSelectionModel.get(selectionMode);
	}

	public final SelectionMode getSelectionMode() {
		return this.selectionMode;
	}

	public final void addSelectionModelListener(final HierarchicalSelectionModelListener listener) {
		this.selectionModelListeners.add(listener);
	}

	public final void removeSelectionModelListener(final HierarchicalSelectionModelListener listener) {
		this.selectionModelListeners.remove(listener);
	}

	public final void initialize(final List<L> model, final List<F> columnList) {
		this.model = model;
		this.columnList = columnList;
		this.nbRows = this.model != null ? this.model.size() : 0;
		this.nbColumns = columnList.size();
		this.selectionModel = new boolean[nbRows][nbColumns];
		cleanSelectionModel();
	}

	public final void reset() {
		this.model = null;
		this.nbRows = 0;
		this.nbColumns = 0;
		this.selectionModel = null;
	}

	public final void cleanSelectionModel() {
		for (int rowIndex = 0; rowIndex < this.nbRows; rowIndex++) {
			_cleanRow(rowIndex);
		}

		fireSelectionChanged();
	}

	public void selectAllModel() {
		this.currentSelectionModel.selectAllModel();
		fireSelectionChanged();
	}

	private boolean areValidCoordinates(final int rowIndex, final int columnIndex) {
		return isValidRowNumber(rowIndex) && isValidColumnNumber(columnIndex);
	}

	private boolean isValidRowNumber(final int rowIndex) {
		return rowIndex >= 0 && rowIndex < this.nbRows;
	}

	private boolean isValidColumnNumber(final int columnIndex) {
		return columnIndex >= 0 && columnIndex < this.nbColumns;
	}

	private void _cleanRow(final int rowIndex) {
		for (int columnIndex = 0; columnIndex < this.nbColumns; columnIndex++) {
			_cleanSelectionAtCoordinates(rowIndex, columnIndex);
		}
	}

	protected final void _selectRow(final int rowIndex) {
		for (int columnIndex = 0; columnIndex < this.nbColumns; columnIndex++) {
			_setSelectedAt(rowIndex, columnIndex);
		}
	}

	private void _cleanColumn(final int columnIndex) {
		for (int rowIndex = 0; rowIndex < this.nbRows; rowIndex++) {
			_cleanSelectionAtCoordinates(rowIndex, columnIndex);
		}
	}

	private void _selectColumn(final int columnIndex) {
		for (int rowIndex = 0; rowIndex < this.nbRows; rowIndex++) {
			_setSelectedAt(rowIndex, columnIndex);
		}
	}

	private void _cleanSelectionAtCoordinates(final int rowIndex, final int columnIndex) {
		this.selectionModel[rowIndex][columnIndex] = false;
	}

	public void setSelectedAt(final int rowIndex, final int columnIndex) {
		if (areValidCoordinates(rowIndex, columnIndex)) {
			this.currentSelectionModel.setSelectedAt(rowIndex, columnIndex);
			fireSelectionChanged();
		}
	}

	protected final void _setSelectedAt(final int rowIndex, final int columnIndex) {
		this.selectionModel[rowIndex][columnIndex] = true;
	}

	public final Boolean isSelectedAt(final int rowIndex, final int columnIndex) {
		if (areValidCoordinates(rowIndex, columnIndex)) {
			return _isSelectedAt(rowIndex, columnIndex);
		}
		return false;
	}

	private Boolean _isSelectedAt(final int rowIndex, final int columnIndex) {
		return this.selectionModel[rowIndex][columnIndex];
	}

	public Boolean changeSelectionStatusAt(final int rowIndex, final int columnIndex) {
		if (areValidCoordinates(rowIndex, columnIndex)) {
			this.currentSelectionModel.changeSelectionStatusAt(rowIndex, columnIndex);
			fireSelectionChanged();
		}
		return isSelectedAt(rowIndex, columnIndex);
	}

	protected final void _changeSelectionStatusAt(final int rowIndex, final int columnIndex) {
		final Boolean selectionStatus = isSelectedAt(rowIndex, columnIndex);
		this.selectionModel[rowIndex][columnIndex] = !selectionStatus;
	}

	public void changeColumnSelectionStatusAt(final int columnIndex) {
		if (isValidColumnNumber(columnIndex)) {
			this.currentSelectionModel.changeColumnSelectionStatusAt(columnIndex);
			fireSelectionChanged();
		}
	}

	protected final void _changeColumnSelectionStatusAt(final int columnIndex) {
		if (_isWholeColumnSelected(columnIndex)) {
			_cleanColumn(columnIndex);
		} else {
			_selectColumn(columnIndex);
		}
	}

	public void changeRowSelectionStatusAt(final int rowIndex) {
		if (isValidRowNumber(rowIndex)) {
			this.currentSelectionModel.changeRowSelectionStatusAt(rowIndex);
			fireSelectionChanged();
		}
	}

	protected final void _changeRowSelectionStatusAt(final int rowIndex) {
		if (isWholeRowSelected(rowIndex)) {
			_cleanRow(rowIndex);
		} else {
			_selectRow(rowIndex);
		}
	}

	public void changeRowSelectionStatusByRange(final int lowerBoundIndex, final int upperBoundIndex) {
		if (isValidRowNumber(lowerBoundIndex) && isValidRowNumber(upperBoundIndex)) {
			this.currentSelectionModel.changeRowSelectionStatusByRange(lowerBoundIndex, upperBoundIndex);
			fireSelectionChanged();
		}
	}

	protected final void _changeRowSelectionStatusByRange(final int lowerBoundIndex, final int upperBoundIndex) {
		boolean areRowsAlreadySelected = true;
		for (int rowIndex = lowerBoundIndex; rowIndex <= upperBoundIndex && areRowsAlreadySelected; rowIndex++) {
			areRowsAlreadySelected = areRowsAlreadySelected && _isWholeRowSelected(rowIndex);
		}
		if (areRowsAlreadySelected) {
			_unselectRowsByRange(lowerBoundIndex, upperBoundIndex);
		} else {
			_selectRowsByRange(lowerBoundIndex, upperBoundIndex);
		}

	}

	private void _selectRowsByRange(final int lowerBoundIndex, final int upperBoundIndex) {
		for (int rowIndex = lowerBoundIndex; rowIndex <= upperBoundIndex; rowIndex++) {
			_selectRow(rowIndex);
		}
	}

	private void _unselectRowsByRange(final int lowerBoundIndex, final int upperBoundIndex) {
		for (int rowIndex = lowerBoundIndex; rowIndex <= upperBoundIndex; rowIndex++) {
			_cleanRow(rowIndex);
		}
	}

	public void changeColumnSelectionStatusByRange(final int lowerBoundIndex, final int upperBoundIndex) {
		if (isValidColumnNumber(lowerBoundIndex) && isValidColumnNumber(upperBoundIndex)) {
			this.currentSelectionModel.changeColumnSelectionStatusByRange(lowerBoundIndex, upperBoundIndex);
			fireSelectionChanged();
		}
	}

	protected final void _changeColumnSelectionStatusByRange(final int lowerBoundIndex, final int upperBoundIndex) {
		boolean areColumnsAlreadySelected = true;
		for (int columnIndex = lowerBoundIndex; columnIndex <= upperBoundIndex && areColumnsAlreadySelected; columnIndex++) {
			areColumnsAlreadySelected = areColumnsAlreadySelected && _isWholeColumnSelected(columnIndex);
		}
		if (areColumnsAlreadySelected) {
			_unselectColumnsByRange(lowerBoundIndex, upperBoundIndex);
		} else {
			_selectColumnsByRange(lowerBoundIndex, upperBoundIndex);
		}

	}

	private void _selectColumnsByRange(final int lowerBoundIndex, final int upperBoundIndex) {
		for (int columnIndex = lowerBoundIndex; columnIndex <= upperBoundIndex; columnIndex++) {
			_selectColumn(columnIndex);
		}
	}

	private void _unselectColumnsByRange(final int lowerBoundIndex, final int upperBoundIndex) {
		for (int columnIndex = lowerBoundIndex; columnIndex <= upperBoundIndex; columnIndex++) {
			_cleanColumn(columnIndex);
		}
	}

	private void fireSelectionChanged() {
		if (!selectionIsAdjusting) {
			for (final HierarchicalSelectionModelListener listener : this.selectionModelListeners) {
				listener.selectionChanged();
			}
		}
	}

	public final void setSelectionIsAdjusting(boolean selectionIsAdjusting) {
		if (selectionIsAdjusting != this.selectionIsAdjusting) {
			this.selectionIsAdjusting = selectionIsAdjusting;
			if (!selectionIsAdjusting) {
				fireSelectionChanged();
			}
		}
	}

	public final boolean isRowSelected(final int rowIndex) {
		boolean isRowSelected = false;
		if (isValidRowNumber(rowIndex)) {
			isRowSelected = _isRowSelected(rowIndex);
		}
		return isRowSelected;
	}

	private boolean _isRowSelected(final int rowIndex) {
		boolean isRowSelected = false;
		for (int columnIndex = 0; columnIndex < this.nbColumns && !isRowSelected; columnIndex++) {
			isRowSelected = isRowSelected || isSelectedAt(rowIndex, columnIndex);
		}
		return isRowSelected;
	}

	public final boolean isWholeRowSelected(final int rowIndex) {
		boolean isRowSelected = false;
		if (isValidRowNumber(rowIndex)) {
			isRowSelected = _isWholeRowSelected(rowIndex);
		}
		return isRowSelected;
	}

	private boolean _isWholeRowSelected(final int rowIndex) {
		boolean isRowSelected = true;
		for (int columnIndex = 0; columnIndex < this.nbColumns && isRowSelected; columnIndex++) {
			isRowSelected = isRowSelected && isSelectedAt(rowIndex, columnIndex);
		}
		return isRowSelected;
	}

	public final boolean isAllModelSelected() {
		boolean isAllModelSelected = true;
		for (int rowIndex = 0; rowIndex < this.nbRows && isAllModelSelected; rowIndex++) {
			isAllModelSelected = isAllModelSelected && _isWholeRowSelected(rowIndex);
		}
		return isAllModelSelected;
	}

	public final boolean isWholeColumnSelected(final int columnIndex) {
		boolean isColumnSelected = false;
		if (isValidColumnNumber(columnIndex)) {
			isColumnSelected = _isWholeColumnSelected(columnIndex);
		}
		return isColumnSelected;
	}

	private boolean _isWholeColumnSelected(final int columnIndex) {
		boolean isColumnSelected = true;
		for (int rowIndex = 0; rowIndex < this.nbRows && isColumnSelected; rowIndex++) {
			isColumnSelected = isColumnSelected && isSelectedAt(rowIndex, columnIndex);
		}
		return isColumnSelected;
	}

	public final List<L> buildSelectedModel() {
		final List<L> selectionModelList = new ArrayList<L>();
		for (int rowIndex = 0; rowIndex < this.nbRows; rowIndex++) {
			addSelectedRowToSelectionListIfRequired(selectionModelList, rowIndex);
		}
		return selectionModelList;
	}

	private void addSelectedRowToSelectionListIfRequired(final List<L> selectionModelList, final int rowIndex) {
		if (_isRowSelected(rowIndex)) {
			selectionModelList.add(buildSelectionRowAt(rowIndex));
		}
	}

	private L buildSelectionRowAt(final int rowIndex) {
		final L selectedLine = ComponentFactory.getInstance().createInstance(this.modelClass);
		final L modelLine = this.model.get(rowIndex);
		selectedLine.setTitleComponent(modelLine.getTitleComponent());
		selectedLine.setValuesMap(new HashMap<F, Serializable>());
		fillHierarchicalLineWithSelectedValues(selectedLine, rowIndex);
		return selectedLine;
	}

	private void fillHierarchicalLineWithSelectedValues(final L selectedLine, final int rowIndex) {
		for (int columnIndex = 0; columnIndex < this.nbColumns; columnIndex++) {
			if (_isSelectedAt(rowIndex, columnIndex)) {
				addSelectedValueToLine(rowIndex, columnIndex, selectedLine);
			}
		}
	}

	private void addSelectedValueToLine(final int rowIndex, final int columnIndex, final L selectedLine) {
		final L modelLine = this.model.get(rowIndex);
		final F column = this.columnList.get(columnIndex);
		// if (modelLine.getValuesMap().containsKey(column)) {
		selectedLine.getValuesMap().put(column, modelLine.getValuesMap().get(column));
		// }
	}

	public final void setSelectedValue(final String valueAsString) {
		this.selectedValue = valueAsString;
	}

	public final String getSelectedValue() {
		return this.selectedValue;
	}

	public final HoverCell getHoverCell() {
		return hoverCell;
	}

	public void setHoverCell(HoverCell hoverCell, IHoverChangeCallback hoverChangeCallback) {
		if (this.hoverChangeCallback != null) {
			if (!sameHover(hoverCell, this.hoverCell)) {
				this.hoverChangeCallback.hoverChanged(hoverCell);
			}
		}
		this.hoverCell = hoverCell;
		this.hoverChangeCallback = hoverChangeCallback;
	}

	private boolean sameHover(HoverCell hc1, HoverCell hc2) {
		if (hc1 == hc2) {
			return true;
		}
		if ((hc1 == null) || (hc2 == null)) {
			return false;
		}
		if ((hc1.getColumnIndex() == hc2.getColumnIndex()) && (hc1.getRowIndex() == hc2.getRowIndex()) && (hc1.getPanelKind() == hc2.getPanelKind())) {
			return true;
		}
		return false;
	}

	public interface IHoverChangeCallback {

		public void hoverChanged(HoverCell hoverCell);

	}
}
