package com.synaptix.widget.joda.view.swing.filter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import org.joda.time.base.BaseLocal;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.SwingMessages;
import com.synaptix.swing.table.filter.AbstractFilterDialog;

/*proected package*/abstract class AbstractDialogLocalFilterColumn<E extends BaseLocal, F extends AbstractLocalSearch<E>> extends AbstractFilterDialog<F> {

	private static final long serialVersionUID = 8657039450727283600L;

	private JComboBox comparaisonTypeBox;

	private JFormattedTextField localField;

	protected void initComponents() {
		comparaisonTypeBox = new JComboBox(
				new String[] {
						SwingMessages.getString("DateFilterColumn.0"), //$NON-NLS-1$
						SwingMessages.getString("DateFilterColumn.1"), SwingMessages.getString("DateFilterColumn.2"), SwingMessages.getString("DateFilterColumn.3"), SwingMessages.getString("DateFilterColumn.4"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						SwingMessages.getString("DateFilterColumn.5"), SwingMessages.getString("DateFilterColumn.6"), SwingMessages.getString("DateFilterColumn.7") }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		comparaisonTypeBox.addActionListener(new ContainsActionListener());

		localField = new JFormattedTextField();
		localField.setEditable(true);
	}

	protected abstract JComponent decorate(JFormattedTextField localField);

	protected abstract E newDefaultLocal();

	protected abstract F createLocalSearch(int comparaisonType, E local);

	protected JPanel buildContents() {
		FormLayout formLayout = new FormLayout("p,4dlu,fill:100dlu:grow", //$NON-NLS-1$
				"p"//$NON-NLS-1$
		);
		PanelBuilder builder = new PanelBuilder(formLayout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.add(comparaisonTypeBox, cc.xy(1, 1));
		builder.add(decorate(localField), cc.xy(3, 1));
		return builder.getPanel();
	}

	protected void initiliazeSearch(F e) {
		localField.setValue(newDefaultLocal());
		if (e != null) {
			comparaisonTypeBox.setSelectedIndex(e.getComparaisonType());

			if (e.getValue() != null) {
				localField.setValue(e.getValue());
			}
		} else {
			comparaisonTypeBox.setSelectedIndex(0);
		}
		localField.requestFocus();

		localField.addPropertyChangeListener("value", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				updateSearchTable();
			}
		});
	}

	public int getComparaisonType() {
		return comparaisonTypeBox.getSelectedIndex();
	}

	@SuppressWarnings("unchecked")
	public E getLocal() {
		return (E) localField.getValue();
	}

	private void updateSearchTable() {
		int col = table.convertColumnIndexToView(columnIndex);
		table.setColumnFilter(col, createLocalSearch(getComparaisonType(), getLocal()));
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
				localField.setEnabled(true);
				break;
			case 6:
			case 7:
				localField.setEnabled(false);
				break;
			default:
				localField.setEnabled(true);
				break;
			}
			updateSearchTable();
		}
	}
}