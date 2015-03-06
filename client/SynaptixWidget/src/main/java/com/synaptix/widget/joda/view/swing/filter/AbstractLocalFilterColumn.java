package com.synaptix.widget.joda.view.swing.filter;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;

import org.joda.time.base.BaseLocal;

import com.synaptix.swing.JSyTable;
import com.synaptix.swing.table.FilterColumn;
import com.synaptix.swing.table.SyTableColumn;
import com.synaptix.swing.table.filter.SyRowFilter.SyComparaisonType;

/*protected package*/abstract class AbstractLocalFilterColumn<E extends BaseLocal, F extends AbstractLocalSearch<E>> implements FilterColumn {

	protected abstract JComponent decorate(JFormattedTextField localField);

	protected abstract E newDefaultLocal();

	protected abstract F createLocalSearch(int comparaisonType, E local);

	@Override
	@SuppressWarnings("unchecked")
	public Object getFilterColumn(JSyTable table, Object filter, SyTableColumn c) {
		DialogLocalDateFilterColumn dialog = new DialogLocalDateFilterColumn();
		int result = dialog.showDialog(table, c.getModelIndex(), c.getIdentifier().toString(), (F) filter);
		switch (result) {
		case DialogLocalDateFilterColumn.OK_OPTION:
			return createLocalSearch(dialog.getComparaisonType(), dialog.getLocal());
		case DialogLocalDateFilterColumn.RESET_OPTION:
			return null;
		}
		return filter;
	}

	@Override
	@SuppressWarnings("unchecked")
	public RowFilter<? super TableModel, ? super Integer> getRowFilter(JSyTable table, Object filter, SyTableColumn c) {
		F ds = (F) filter;

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

		return localFilter(type, ds.getValue(), c.getModelIndex());
	}

	@SuppressWarnings("unchecked")
	private <M, I> RowFilter<M, I> localFilter(SyComparaisonType type, BaseLocal date, int columnIndex) {
		return (RowFilter<M, I>) new LocalFilter(type, date, columnIndex);
	}

	private class DialogLocalDateFilterColumn extends AbstractDialogLocalFilterColumn<E, F> {

		private static final long serialVersionUID = 8657039450727283600L;

		@Override
		protected F createLocalSearch(int comparaisonType, E local) {
			return AbstractLocalFilterColumn.this.createLocalSearch(comparaisonType, local);
		}

		@Override
		protected JComponent decorate(JFormattedTextField localField) {
			return AbstractLocalFilterColumn.this.decorate(localField);
		}

		@Override
		protected E newDefaultLocal() {
			return AbstractLocalFilterColumn.this.newDefaultLocal();
		}
	}
}
