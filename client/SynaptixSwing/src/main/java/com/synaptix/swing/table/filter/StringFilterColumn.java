package com.synaptix.swing.table.filter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import org.jdesktop.swingx.autocomplete.AbstractAutoCompleteAdaptor;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.autocomplete.AutoCompleteDocument;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.SwingMessages;
import com.synaptix.swing.table.FilterColumn;
import com.synaptix.swing.table.SyTableColumn;
import com.synaptix.swing.table.filter.SyRowFilter.SyComparaisonType;

public final class StringFilterColumn implements FilterColumn {

	@Override
	public Object getFilterColumn(JSyTable table, Object filter, SyTableColumn c) {
		DialogStringFilterColumn dialog = new DialogStringFilterColumn();
		int result = dialog.showDialog(table, c.getModelIndex(), c
				.getIdentifier().toString(), (StringSearch) filter);
		switch (result) {
		case DialogStringFilterColumn.OK_OPTION:
			return new StringSearch(dialog.getComparaisonType(), dialog
					.getText());
		case DialogStringFilterColumn.RESET_OPTION:
			return null;
		}
		return filter;
	}

	@Override
	public RowFilter<? super TableModel, ? super Integer> getRowFilter(
			JSyTable table, Object filter, SyTableColumn c) {
		StringSearch ss = (StringSearch) filter;
		String text = ss.getText();

		SyComparaisonType type;
		switch (ss.getComparaisonType()) {
		case 0:
			type = SyComparaisonType.EQUAL;
			break;
		case 1:
			type = SyComparaisonType.NOT_EQUAL;
			break;
		case 2:
			type = SyComparaisonType.NULL;
			break;
		case 3:
			type = SyComparaisonType.NOT_NULL;
			break;
		default:
			type = SyComparaisonType.EQUAL;
			break;
		}

		text = text.replaceAll("\\\\", "\\\\\\\\"); //$NON-NLS-1$ //$NON-NLS-2$
		text = text.replaceAll("\\(", "\\\\("); //$NON-NLS-1$ //$NON-NLS-2$
		text = text.replaceAll("\\)", "\\\\)"); //$NON-NLS-1$ //$NON-NLS-2$
		text = text.replaceAll("\\[", "\\\\["); //$NON-NLS-1$ //$NON-NLS-2$
		text = text.replaceAll("\\]", "\\\\]"); //$NON-NLS-1$ //$NON-NLS-2$
		text = text.replaceAll("\\{", "\\\\{"); //$NON-NLS-1$ //$NON-NLS-2$
		text = text.replaceAll("\\}", "\\\\}"); //$NON-NLS-1$ //$NON-NLS-2$
		text = text.replaceAll("\\*", "\\\\*"); //$NON-NLS-1$ //$NON-NLS-2$
		text = text.replaceAll("\\+", "\\\\+"); //$NON-NLS-1$ //$NON-NLS-2$
		text = text.replaceAll("\\.", "\\\\."); //$NON-NLS-1$ //$NON-NLS-2$
		text = text.replaceAll("\\|", "\\\\|"); //$NON-NLS-1$ //$NON-NLS-2$
		text = text.replaceAll("\\?", "\\\\?"); //$NON-NLS-1$ //$NON-NLS-2$
		text = text.replaceAll("\\^", "\\\\^"); //$NON-NLS-1$ //$NON-NLS-2$
		text = text.replaceAll("\\#", "\\\\#"); //$NON-NLS-1$ //$NON-NLS-2$

		return SyRowFilter.regexFilter(type, text, c.getModelIndex());
	}

	private final class DialogStringFilterColumn extends
			AbstractFilterDialog<StringSearch> {

		private static final long serialVersionUID = 8657039450727283600L;

		private JComboBox comparaisonTypeBox;

		private JTextField textField;

		protected void initComponents() {
			comparaisonTypeBox = new JComboBox(new String[] { SwingMessages.getString("StringFilterColumn.28"), //$NON-NLS-1$
					SwingMessages.getString("StringFilterColumn.29"), SwingMessages.getString("StringFilterColumn.30"), SwingMessages.getString("StringFilterColumn.31") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			comparaisonTypeBox.addActionListener(new ContainsActionListener());

			textField = new JTextField();
			textField.addActionListener(new TextActionListener());
			textField.addFocusListener(new TextFocusListener());

			AbstractAutoCompleteAdaptor adaptor = new MyAutoCompleteAdaptor();
			AutoCompleteDocument document = new AutoCompleteDocument(adaptor,
					false);
			textField.setDocument(document);
			AutoCompleteDecorator.decorate(textField, document, adaptor);
		}

		protected JPanel buildContents() {
			FormLayout formLayout = new FormLayout("p,4dlu,fill:100dlu:grow", // columns //$NON-NLS-1$
					"p" // rows //$NON-NLS-1$
			);

			PanelBuilder builder = new PanelBuilder(formLayout);
			builder.setDefaultDialogBorder();

			CellConstraints cc = new CellConstraints();

			builder.add(comparaisonTypeBox, cc.xy(1, 1));
			builder.add(textField, cc.xy(3, 1));

			return builder.getPanel();
		}

		protected void initiliazeSearch(StringSearch e) {
			if (e != null) {
				comparaisonTypeBox.setSelectedIndex(e.getComparaisonType());
				if (e.getText() != null) {
					textField.setText(e.getText());
					textField.selectAll();
				}
			} else {
				comparaisonTypeBox.setSelectedIndex(0);
			}
			textField.requestFocus();
		}

		public int getComparaisonType() {
			return comparaisonTypeBox.getSelectedIndex();
		}

		public String getText() {
			return textField.getText();
		}

		private void updateSearchTable() {
			int col = table.convertColumnIndexToView(columnIndex);

			StringSearch ss = new StringSearch(getComparaisonType(), getText());
			table.setColumnFilter(col, ss);
		}

		private final class ContainsActionListener implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				switch (comparaisonTypeBox.getSelectedIndex()) {
				case 0:
				case 1:
					textField.setEnabled(true);
					break;
				case 2:
				case 3:
					textField.setEnabled(false);
					break;
				default:
					textField.setEnabled(true);
					break;
				}
				updateSearchTable();
			}
		}

		private final class TextFocusListener extends FocusAdapter {

			@Override
			public void focusGained(FocusEvent e) {
				updateSearchTable();
			}

			@Override
			public void focusLost(FocusEvent e) {
				updateSearchTable();
			}
		}

		private final class TextActionListener implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				resultOption = OK_OPTION;
				dialog.closeDialog();
			}
		}

		private final class MyAutoCompleteAdaptor extends
				AbstractAutoCompleteAdaptor {

			private Object selectedItem;

			public MyAutoCompleteAdaptor() {
				super();
				selectedItem = null;
			}

			@Override
			public Object getItem(int i) {
				return table.getValueAt(i, table
						.convertColumnIndexToView(columnIndex));
			}

			@Override
			public int getItemCount() {
				return table.getRowCount();
			}

			@Override
			public Object getSelectedItem() {
				return selectedItem;
			}

			@Override
			public JTextComponent getTextComponent() {
				return textField;
			}

			@Override
			public void setSelectedItem(Object item) {
				selectedItem = item;
				updateSearchTable();
			}
		}
	}
}
