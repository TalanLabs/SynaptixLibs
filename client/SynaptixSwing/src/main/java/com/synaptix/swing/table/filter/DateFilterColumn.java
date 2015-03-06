package com.synaptix.swing.table.filter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Date;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.RowFilter;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableModel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.SwingMessages;
import com.synaptix.swing.table.FilterColumn;
import com.synaptix.swing.table.SyTableColumn;
import com.synaptix.swing.table.filter.SyRowFilter.SyComparaisonType;
import com.synaptix.swing.utils.DateTimeUtils;
import com.synaptix.swing.utils.SwingComponentFactory;

public final class DateFilterColumn implements FilterColumn {

	@Override
	public Object getFilterColumn(JSyTable table, Object filter, SyTableColumn c) {
		DialogDateFilterColumn dialog = new DialogDateFilterColumn();
		int result = dialog.showDialog(table, c.getModelIndex(), c
				.getIdentifier().toString(), (DateSearch) filter);
		switch (result) {
		case DialogDateFilterColumn.OK_OPTION:
			return new DateSearch(dialog.getComparaisonType(),
					dialog.getDate(), dialog.isUseHour());
		case DialogDateFilterColumn.RESET_OPTION:
			return null;
		}
		return filter;
	}

	@Override
	public RowFilter<? super TableModel, ? super Integer> getRowFilter(
			JSyTable table, Object filter, SyTableColumn c) {
		DateSearch ds = (DateSearch) filter;

		SyComparaisonType type;
		switch (ds.getComparaisonType()) {
		case 0:
			type = SyComparaisonType.AFTER;
			break;
		case 1:
			type = SyComparaisonType.AFTER_OR_EQUAL;
			break;
		case 2:
			type = SyComparaisonType.BEFORE;
			break;
		case 3:
			type = SyComparaisonType.BEFORE_OR_EQUAL;
			break;
		case 4:
			type = SyComparaisonType.EQUAL;
			break;
		case 5:
			type = SyComparaisonType.NOT_EQUAL;
			break;
		case 6:
			type = SyComparaisonType.NULL;
			break;
		case 7:
			type = SyComparaisonType.NOT_NULL;
			break;
		default:
			type = SyComparaisonType.EQUAL;
			break;
		}

		return SyRowFilter.dateFilter(type, ds.getDate(), ds.isUseHour(), c
				.getModelIndex());
	}

	private final class DialogDateFilterColumn extends
			AbstractFilterDialog<DateSearch> {

		private static final long serialVersionUID = 8657039450727283600L;

		private JComboBox comparaisonTypeBox;

		private JFormattedTextField dateBox;

		private JCheckBox useHourBox;

		private JFormattedTextField hourField;

		protected void initComponents() {
			comparaisonTypeBox = new JComboBox(new String[] { SwingMessages.getString("DateFilterColumn.0"), //$NON-NLS-1$
					SwingMessages.getString("DateFilterColumn.1"), SwingMessages.getString("DateFilterColumn.2"), SwingMessages.getString("DateFilterColumn.3"), SwingMessages.getString("DateFilterColumn.4"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					SwingMessages.getString("DateFilterColumn.5"), SwingMessages.getString("DateFilterColumn.6"), SwingMessages.getString("DateFilterColumn.7") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			comparaisonTypeBox.addActionListener(new ContainsActionListener());

			dateBox = SwingComponentFactory.createDateField(false, true);
			dateBox.setEditable(true);

			useHourBox = new JCheckBox(SwingMessages.getString("DateFilterColumn.8")); //$NON-NLS-1$

			hourField = SwingComponentFactory.createHourField(false);
			hourField.setEnabled(false);
		}

		protected JPanel buildContents() {
			FormLayout formLayout = new FormLayout("p,4dlu,fill:100dlu:grow", // columns //$NON-NLS-1$
					"p,3dlu,p" // rows //$NON-NLS-1$
			);

			PanelBuilder builder = new PanelBuilder(formLayout);
			builder.setDefaultDialogBorder();

			CellConstraints cc = new CellConstraints();

			builder.add(comparaisonTypeBox, cc.xy(1, 1));
			builder.add(dateBox, cc.xy(3, 1));

			builder.add(useHourBox, cc.xy(1, 3));
			builder.add(hourField, cc.xy(3, 3));

			return builder.getPanel();
		}

		protected void initiliazeSearch(DateSearch e) {
			dateBox.setValue(new Date());
			if (e != null) {
				comparaisonTypeBox.setSelectedIndex(e.getComparaisonType());
				useHourBox.setSelected(e.isUseHour());

				if (e.getDate() != null) {
					hourField.setValue(DateTimeUtils.getMinutesFromDate(e
							.getDate()));

					dateBox.setValue(e.getDate());
				}
			} else {
				comparaisonTypeBox.setSelectedIndex(0);
			}
			dateBox.requestFocus();

			useHourBox.addChangeListener(new UseHourChangeListener());
			hourField.setEnabled(useHourBox.isSelected());

			dateBox.addActionListener(new DateActionListener());
			dateBox.addFocusListener(new DateFocusListener());

			hourField.addActionListener(new HourActionListener());
			hourField.addFocusListener(new HourFocusListener());
		}

		public int getComparaisonType() {
			return comparaisonTypeBox.getSelectedIndex();
		}

		public Date getDate() {
			Date date = (Date) dateBox.getValue();
			if (date != null) {
				if (hourField.getValue() == null) {
					date = DateTimeUtils.createDateAndHour(date, 0);
				} else {
					date = DateTimeUtils.createDateAndHour(date,
							(Integer) hourField.getValue());
				}
			}
			return date;
		}

		public boolean isUseHour() {
			return useHourBox.isSelected();
		}

		private void updateSearchTable() {
			int col = table.convertColumnIndexToView(columnIndex);

			DateSearch ds = new DateSearch(getComparaisonType(), getDate(),
					isUseHour());
			table.setColumnFilter(col, ds);
		}

		private final class HourFocusListener extends FocusAdapter {

			@Override
			public void focusGained(FocusEvent e) {
				updateSearchTable();
			}

			@Override
			public void focusLost(FocusEvent e) {
				updateSearchTable();
			}
		}

		private final class HourActionListener implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				updateSearchTable();
			}
		}

		private final class UseHourChangeListener implements ChangeListener {

			public void stateChanged(ChangeEvent e) {
				hourField.setEnabled(useHourBox.isSelected());
				updateSearchTable();
			}
		}

		private final class ContainsActionListener implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				switch (comparaisonTypeBox.getSelectedIndex()) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
					dateBox.setEnabled(true);
					useHourBox.setEnabled(true);
					hourField.setEnabled(useHourBox.isSelected());
					break;
				case 6:
				case 7:
					dateBox.setEnabled(false);
					useHourBox.setEnabled(false);
					hourField.setEnabled(false);
					break;
				default:
					dateBox.setEnabled(true);
					useHourBox.setEnabled(true);
					hourField.setEnabled(useHourBox.isSelected());
					break;
				}
				updateSearchTable();
			}
		}

		private final class DateFocusListener extends FocusAdapter {

			@Override
			public void focusGained(FocusEvent e) {
				updateSearchTable();
			}

			@Override
			public void focusLost(FocusEvent e) {
				updateSearchTable();
			}
		}

		private final class DateActionListener implements ActionListener {

			public void actionPerformed(ActionEvent e) {
				updateSearchTable();
			}
		}
	}
}
