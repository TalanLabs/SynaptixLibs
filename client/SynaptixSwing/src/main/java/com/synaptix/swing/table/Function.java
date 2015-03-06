package com.synaptix.swing.table;

import java.math.BigDecimal;

import com.synaptix.swing.JSyTable;

public abstract class Function {

	public static Function sumFunction(boolean visibleColumn) {
		if (visibleColumn) {
			return new SumFunction(SumFunction.VISIBLE_COLUMNS);
		}
		return new SumFunction();
	}

	public abstract Object calculate(JSyTable table,int columnIndex);

	private static class SumFunction extends Function {

		public static final int ALL_COLUMNS = 0;

		public static final int VISIBLE_COLUMNS = 2;

		private int type;

		public SumFunction() {
			this(ALL_COLUMNS);
		}

		public SumFunction(int type) {
			this.type = type;
		}

		@Override
		public Object calculate(JSyTable table,int columnIndex) {
			BigDecimal res = BigDecimal.ZERO;
			
			SpecialTableModel model = (SpecialTableModel) table.getModel();
			switch (type) {
			case ALL_COLUMNS:
				for (int i = 0; i < model.getRowCount(); i++) {
					Object o = model.getValueAt(i, columnIndex);
					if (o instanceof Number) {
						res = res.add(new BigDecimal(o.toString()));
					}
				}
				break;
			case VISIBLE_COLUMNS:
				for (int i = 0; i < table.getRowCount(); i++) {
					int row = table.convertRowIndexToModel(i);
					Object o = model.getValueAt(row, columnIndex);
					if (o instanceof Number) {
						res = res.add(new BigDecimal(o.toString()));
					}
				}
				break;
			}

			return res;
		}
	}

	public static Function avgFunction(boolean visibleColumn) {
		if (visibleColumn) {
			return new AvgFunction(AvgFunction.VISIBLE_COLUMNS);
		}
		return new AvgFunction();
	}

	private static class AvgFunction extends Function {

		public static final int ALL_COLUMNS = 0;

		public static final int VISIBLE_COLUMNS = 2;

		private int type;

		public AvgFunction() {
			this(ALL_COLUMNS);
		}

		public AvgFunction(int type) {
			this.type = type;
		}

		@Override
		public Object calculate(JSyTable table,int columnIndex) {
			BigDecimal res = BigDecimal.ZERO;
			
			SpecialTableModel model = (SpecialTableModel) table.getModel();
			switch (type) {
			case ALL_COLUMNS:
				for (int i = 0; i < model.getRowCount(); i++) {
					Object o = model.getValueAt(i, columnIndex);
					if (o instanceof Number) {
						res = res.add(new BigDecimal(o.toString()));
					}
				}
				if(model.getRowCount() > 0)
					res = res.divide(new BigDecimal(model.getRowCount()), 2);
				break;
			case VISIBLE_COLUMNS:
				for (int i = 0; i < table.getRowCount(); i++) {
					int row = table.convertRowIndexToModel(i);
					Object o = model.getValueAt(row, columnIndex);
					if (o instanceof Number) {
						res = res.add(new BigDecimal(o.toString()));
					}
				}
				if(table.getRowCount() > 0)
					res = res.divide(new BigDecimal(table.getRowCount()), 2);
				break;
			}

			return res;
		}
	}

}
