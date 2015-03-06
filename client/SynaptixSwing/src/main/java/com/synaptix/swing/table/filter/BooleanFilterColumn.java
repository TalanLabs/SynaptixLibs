package com.synaptix.swing.table.filter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.SwingMessages;
import com.synaptix.swing.table.FilterColumn;
import com.synaptix.swing.table.SyTableColumn;

public final class BooleanFilterColumn implements FilterColumn {

	@Override
	public Object getFilterColumn(JSyTable table, Object filter, SyTableColumn c) {
		DialogBooleanFilterColumn dialog = new DialogBooleanFilterColumn();
		int result = dialog.showDialog(table, c.getModelIndex(), c.getIdentifier().toString(), (BooleanSearch) filter);
		switch (result) {
		case DialogBooleanFilterColumn.OK_OPTION:
			return new BooleanSearch(dialog.getValue());
		case DialogBooleanFilterColumn.RESET_OPTION:
			return null;
		}
		return filter;
	}

	@Override
	public RowFilter<? super TableModel, ? super Integer> getRowFilter(JSyTable table, Object filter, SyTableColumn c) {
		BooleanSearch bs = (BooleanSearch) filter;
		Boolean res;
		switch (bs.getType()) {
		case ALL:
			res = null;
			break;
		case FALSE:
			res = false;
			break;
		case TRUE:
			res = true;
			break;
		default:
			res = null;
			break;
		}
		return SyRowFilter.booleanFilter(res, c.getModelIndex());
	}

	private final class DialogBooleanFilterColumn extends AbstractFilterDialog<BooleanSearch> {

		private static final long serialVersionUID = 8657039450727283600L;

		private JComboBox comboBox;

		protected void initComponents() {
			comboBox = new JComboBox(new String[] { SwingMessages.getString("BooleanSearch.2"), SwingMessages.getString("BooleanSearch.0"), SwingMessages.getString("BooleanSearch.1") });
			comboBox.addActionListener(new ValueActionListener());
		}

		protected JPanel buildContents() {
			FormLayout formLayout = new FormLayout("FILL:50DLU:GROW", // columns //$NON-NLS-1$
					"p" // rows //$NON-NLS-1$
			);

			PanelBuilder builder = new PanelBuilder(formLayout);
			builder.setDefaultDialogBorder();
			CellConstraints cc = new CellConstraints();
			builder.add(comboBox, cc.xy(1, 1));
			return builder.getPanel();
		}

		protected void initiliazeSearch(BooleanSearch e) {
			if (e != null && e.getType() != null) {
				comboBox.setSelectedIndex(e.getType().ordinal());
			} else {
				comboBox.setSelectedIndex(0);
			}
		}

		public BooleanSearch.Type getValue() {
			int i = comboBox.getSelectedIndex();
			return i != -1 ? BooleanSearch.Type.values()[i] : null;
		}

		private void updateSearchTable() {
			int col = table.convertColumnIndexToView(columnIndex);

			BooleanSearch bs = new BooleanSearch(getValue());
			table.setColumnFilter(col, bs);
		}

		private final class ValueActionListener implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				updateSearchTable();
			}
		}
	}
}
