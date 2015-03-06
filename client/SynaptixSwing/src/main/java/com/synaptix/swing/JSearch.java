package com.synaptix.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionListener;

import com.synaptix.swing.event.SearchModelEvent;
import com.synaptix.swing.event.SearchModelListener;
import com.synaptix.swing.search.AbstractSearchMoteurContext;
import com.synaptix.swing.search.ISearchTableRowHighlight;
import com.synaptix.swing.search.JSearchHeader;
import com.synaptix.swing.search.JSearchTable;
import com.synaptix.swing.search.Result;
import com.synaptix.swing.search.SearchModel;
import com.synaptix.swing.search.SearchMoteur;
import com.synaptix.swing.search.SearchRowHighlight;

public class JSearch extends JPanel implements SearchModelListener {

	private static final long serialVersionUID = 8375923140676310807L;

	private SearchModel dataModel;

	private JSearchHeader searchHeader;

	private JSearchTable searchTable;

	private SearchMoteur searchMoteur;

	private SearchRowHighlight searchRowHighlight;

	public JSearch(SearchModel searchModel) {
		super(new BorderLayout());

		setModel(searchModel);

		initComponent();

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initComponent() {
		searchHeader = new JSearchHeader(getModel(), getModel().getSaveName());
		searchHeader.addActionListener(new FiltersActionListener());

		searchTable = new JSearchTable(getModel(), getModel().getSaveName());
		searchTable.setSearchTableRowHighlight(new MySearchTableRowHighlight());

		searchMoteur = new SearchMoteur(new MySearchMoteurContext(),
				searchHeader, searchTable);
		searchMoteur.setUseCount(getModel().isUseCount());
	}

	private JComponent buildContents() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(searchHeader, BorderLayout.NORTH);
		panel.add(searchTable, BorderLayout.CENTER);
		return panel;
	}

	public JSearchHeader getSearchHeader() {
		return searchHeader;
	}

	public JSearchTable getSearchTable() {
		return searchTable;
	}

	public void load() {
		searchHeader.load();
	}

	public void save() {
		searchHeader.save();
	}

	public boolean isSave() {
		return searchHeader.isSave();
	}

	public void defaultFilters() {
		searchHeader.defaultFilters();
	}

	public SearchModel getModel() {
		return dataModel;
	}

	public void setModel(SearchModel dataModel) {
		if (dataModel == null) {
			throw new IllegalArgumentException("Cannot set a null TableModel"); //$NON-NLS-1$
		}
		if (this.dataModel != dataModel) {
			SearchModel old = this.dataModel;
			if (old != null) {
				old.removeSearchModelListener(this);
			}
			this.dataModel = dataModel;
			dataModel.addSearchModelListener(this);

			searchChanged(new SearchModelEvent(dataModel,
					SearchModelEvent.Type.FILTER));

			firePropertyChange("model", old, dataModel); //$NON-NLS-1$
		}
	}

	public void search(boolean useWaitScreen) {
		searchMoteur.search(useWaitScreen);
	}

	public void search(boolean useWaitScreen,
			SearchMoteur.FinishSearch finishSearch) {
		searchMoteur.search(useWaitScreen, finishSearch);
	}

	public Map<String, Object> getValueFilters() {
		return searchHeader.getValueFilters();
	}

	public void searchChanged(SearchModelEvent e) {
	}

	public void clearTable() {
		searchTable.clearTable();
	}

	public void setResult(Result result) {
		searchTable.setResult(result);
	}

	public JSyTable getTable() {
		return searchTable.getTable();
	}

	public boolean isViewDialogMaxCount() {
		return searchMoteur.isViewDialogMaxCount();
	}

	public void setViewDialogMaxCount(boolean viewDialogMaxCount) {
		searchMoteur.setViewDialogMaxCount(viewDialogMaxCount);
	}

	public boolean isViewDialogZeroCount() {
		return searchMoteur.isViewDialogZeroCount();
	}

	public void setViewDialogZeroCount(boolean viewDialogZeroCount) {
		searchMoteur.setViewDialogZeroCount(viewDialogZeroCount);
	}

	public boolean isViewDialogContinueCount() {
		return searchMoteur.isViewDialogContinueCount();
	}

	public void setViewDialogContinueCount(boolean viewDialogContinueCount) {
		searchMoteur.setViewDialogContinueCount(viewDialogContinueCount);
	}

	public boolean isUseWaitScreenForFilter() {
		return searchMoteur.isUseWaitScreenForFilter();
	}

	public void setUseWaitScreenForFilter(boolean useWaitScreenForFilter) {
		searchMoteur.setUseWaitScreenForFilter(useWaitScreenForFilter);
	}

	public void setSelectionMode(int selectionMode) {
		searchTable.setSelectionMode(selectionMode);
	}

	public int getSelectionMode() {
		return searchTable.getSelectionMode();
	}

	public int getSelectedRow() {
		return searchTable.getSelectedRow();
	}

	public int[] getSelectedRows() {
		return searchTable.getSelectedRows();
	}

	public int getSelectedRowCount() {
		return searchTable.getSelectedRowCount();
	}

	public List<Object> getRowAt(int row) {
		return searchTable.getRowAt(row);
	}

	public Result getResult() {
		return searchTable.getResult();
	}

	public void addListSelectionListener(ListSelectionListener x) {
		searchTable.addListSelectionListener(x);
	}

	public void removeListSelectionListener(ListSelectionListener x) {
		searchTable.removeListSelectionListener(x);
	}

	public Action getDoubleClickAction() {
		return searchTable.getDoubleClickAction();
	}

	public void setDoubleClickAction(Action doubleClickAction) {
		searchTable.setDoubleClickAction(doubleClickAction);
	}

	public JPopupMenu getPopupMenu() {
		return searchTable.getPopupMenu();
	}

	public void setPopupMenu(JPopupMenu popupMenu) {
		searchTable.setPopupMenu(popupMenu);
	}

	public void setFilterValue(String name, Object value) {
		searchHeader.setFilterValue(name, value);
	}

	public void setFilterEnabled(String name, boolean enabled) {
		searchHeader.setFilterEnabled(name, enabled);
	}

	public SearchRowHighlight getSearchRowHighlight() {
		return searchRowHighlight;
	}

	public void setSearchRowHighlight(SearchRowHighlight searchRowHighlight) {
		this.searchRowHighlight = searchRowHighlight;
	}

	private final class MySearchMoteurContext extends
			AbstractSearchMoteurContext {

		public Result search(Map<String, Object> filters) throws Exception {
			return getModel().search(filters);
		}

		public int searchCount(Map<String, Object> filters) throws Exception {
			return getModel().searchCount(filters);
		}

		public int getMaxCount() {
			return dataModel.getMaxCount();
		}
	}

	private final class FiltersActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			searchMoteur.search();
		}
	}

	private final class MySearchTableRowHighlight implements
			ISearchTableRowHighlight {

		public Color getBackgroundColor(JSearchTable searchTable,
				Result result, boolean isSelected, boolean hasFocus,
				int rowModel, int rowView, int columnModel, int columnView) {
			return searchRowHighlight != null ? searchRowHighlight
					.getBackgroundColor(JSearch.this, result, isSelected,
							hasFocus, rowModel, rowView, columnModel,
							columnView) : null;
		}

		public Font getFont(JSearchTable searchTable, Result result,
				boolean isSelected, boolean hasFocus, int rowModel,
				int rowView, int columnModel, int columnView) {
			return searchRowHighlight != null ? searchRowHighlight.getFont(
					JSearch.this, result, isSelected, hasFocus, rowModel,
					rowView, columnModel, columnView) : null;
		}

		public Color getForegroundColor(JSearchTable searchTable,
				Result result, boolean isSelected, boolean hasFocus,
				int rowModel, int rowView, int columnModel, int columnView) {
			return searchRowHighlight != null ? searchRowHighlight
					.getForegroundColor(JSearch.this, result, isSelected,
							hasFocus, rowModel, rowView, columnModel,
							columnView) : null;
		}

	}
}
