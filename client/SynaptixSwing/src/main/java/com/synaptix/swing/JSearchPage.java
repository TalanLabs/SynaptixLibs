package com.synaptix.swing;

import java.awt.BorderLayout;
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
import com.synaptix.swing.search.JSearchPageControl;
import com.synaptix.swing.search.JSearchTable;
import com.synaptix.swing.search.Result;
import com.synaptix.swing.search.SearchMoteur;
import com.synaptix.swing.search.SearchPageModel;

public class JSearchPage extends JPanel implements SearchModelListener {

	private static final long serialVersionUID = 8375923140676310807L;

	private SearchPageModel dataModel;

	private JSearchHeader searchHeader;

	private JSearchTable searchTable;

	private JSearchPageControl searchPageControl;

	private SearchMoteur searchMoteur;

	public JSearchPage(SearchPageModel searchModel) {
		super(new BorderLayout());

		setModel(searchModel);

		initComponent();

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initComponent() {
		searchHeader = new JSearchHeader(getModel(), getModel().getSaveName());
		searchHeader.addActionListener(new FiltersActionListener());

		searchTable = new JSearchTable(getModel(), getModel().getSaveName());

		searchPageControl = new JSearchPageControl();

		searchMoteur = new SearchMoteur(new MySearchMoteurContext(),
				searchHeader, searchTable, searchPageControl);
	}

	private JComponent buildContents() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(searchHeader, BorderLayout.NORTH);
		panel.add(searchTable, BorderLayout.CENTER);
		panel.add(searchPageControl, BorderLayout.SOUTH);
		return panel;
	}

	public void defaultFilters() {
		searchHeader.defaultFilters();
	}

	public SearchPageModel getModel() {
		return dataModel;
	}

	public void setModel(SearchPageModel dataModel) {
		if (dataModel == null) {
			throw new IllegalArgumentException("Cannot set a null TableModel"); //$NON-NLS-1$
		}
		if (this.dataModel != dataModel) {
			SearchPageModel old = this.dataModel;
			this.dataModel = dataModel;
			firePropertyChange("model", old, dataModel); //$NON-NLS-1$
		}
	}

	public void search() {
		searchMoteur.search();
	}

	public void search(boolean useWaitScreen) {
		searchMoteur.search(useWaitScreen);
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

	public ISearchTableRowHighlight getSearchTableRowHighlight() {
		return searchTable.getSearchTableRowHighlight();
	}

	public void setSearchTableRowHighlight(
			ISearchTableRowHighlight searchTableRowHighlight) {
		searchTable.setSearchTableRowHighlight(searchTableRowHighlight);
	}

	private final class MySearchMoteurContext extends
			AbstractSearchMoteurContext {

		public boolean isUseCount() {
			return true;
		}
		
		public int getPageMaxCount() {
			return getModel().getPageMaxCount();
		}

		public Result search(Map<String, Object> filters, int skip, int max)
				throws Exception {
			return getModel().search(filters, skip, max);
		}

		public int searchCount(Map<String, Object> filters) throws Exception {
			return getModel().searchCount(filters);
		}
	}

	private final class FiltersActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			searchMoteur.search();
		}
	}
}
