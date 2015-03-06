package com.synaptix.swing.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.RowSorter.SortKey;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import com.synaptix.prefs.SyPreferences;
import com.synaptix.swing.JSyTable;

public class SyDefaultTableColumnModel extends DefaultTableColumnModel
		implements SyTableColumnModel {

	private static final long serialVersionUID = -3257422428835949041L;

	private List<TableColumn> initialColumns;

	private List<TableColumn> currentColumns;

	private Set<SyTableColumn> invisibleColumns;

	private JSyTable table;

	private boolean activeSave;

	public SyDefaultTableColumnModel(JSyTable table) {
		super();

		this.table = table;
		activeSave = true;

		initializeLocalVars();
	}

	protected void initializeLocalVars() {
		initialColumns = new ArrayList<TableColumn>();
		currentColumns = new ArrayList<TableColumn>();
		invisibleColumns = new HashSet<SyTableColumn>();
	}

	@Override
	public void addColumn(TableColumn aColumn) {
		initialColumns.add(aColumn);
		currentColumns.add(aColumn);

		super.addColumn(aColumn);
	}

	@Override
	public void moveColumn(int columnIndex, int newIndex) {
		if (columnIndex != newIndex) {
			updateCurrentColumns(columnIndex, newIndex);
		}
		super.moveColumn(columnIndex, newIndex);

		save();
	}

	private void updateCurrentColumns(int oldIndex, int newIndex) {
		TableColumn movedColumn = tableColumns.elementAt(oldIndex);
		int oldPosition = currentColumns.indexOf(movedColumn);
		TableColumn targetColumn = tableColumns.elementAt(newIndex);
		int newPosition = currentColumns.indexOf(targetColumn);
		currentColumns.remove(oldPosition);
		currentColumns.add(newPosition, movedColumn);
	}

	@Override
	public void removeColumn(TableColumn column) {
		initialColumns.remove(column);
		currentColumns.remove(column);
		invisibleColumns.remove(column);

		super.removeColumn(column);
	}

	public List<TableColumn> getColumns(boolean includeHidden, boolean initial) {
		if (includeHidden) {
			if (initial) {
				return new ArrayList<TableColumn>(initialColumns);
			} else {
				return new ArrayList<TableColumn>(currentColumns);
			}
		}
		return Collections.list(getColumns());
	}

	public int getColumnCount(boolean includeHidden) {
		if (includeHidden) {
			return initialColumns.size();
		}
		return getColumnCount();
	}

	public TableColumn getColumn(int columnIndex, boolean includeHidden) {
		if (includeHidden) {
			return initialColumns.get(columnIndex);
		}
		return super.getColumn(columnIndex);
	}

	public void visibleColumn(TableColumn column) {
		SyTableColumn c = (SyTableColumn) column;
		c.setVisible(true);

		invisibleColumns.remove(c);

		super.addColumn(column);

		int addIndex = currentColumns.indexOf(c);
		for (int i = 0; i < (getColumnCount() - 1); i++) {
			TableColumn tableCol = getColumn(i);
			int actualPosition = currentColumns.indexOf(tableCol);
			if (actualPosition > addIndex) {
				super.moveColumn(getColumnCount() - 1, i);
				break;
			}
		}

		table.reconstructRowFilter();

		save();
	}

	public void invisibleColumn(TableColumn column) {
		SyTableColumn c = (SyTableColumn) column;
		c.setVisible(false);

		c.setSearch(null);

		invisibleColumns.add(c);

		super.removeColumn(column);

		table.reconstructRowFilter();

		save();
	}

	public void defaultColumns() {
		TableColumn[] tcs = initialColumns.toArray(new TableColumn[0]);

		for (int i = this.getColumnCount() - 1; i >= 0; i--) {
			TableColumn tableCol = this.getColumn(i);
			super.removeColumn(tableCol);
		}
		currentColumns.clear();
		invisibleColumns.clear();
		initialColumns.clear();
		for (TableColumn column : tcs) {
			SyTableColumn c = (SyTableColumn) column;
			c.setVisible(true);
			c.setPreferredWidth(c.getDefaultWidth());
			c.setWidth(c.getDefaultWidth());
			c.setSearch(null);

			addColumn(c);
		}

		for (TableColumn column : initialColumns) {
			SyTableColumn c = (SyTableColumn) column;
			if (!c.isDefaultVisible()) {
				c.setVisible(false);

				invisibleColumns.add(c);

				super.removeColumn(column);
			}
		}

		table.setAutoResizeMode(JSyTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

		save();

		table.getRowSorter().setSortKeys(null);

		table.reconstructRowFilter();
	}

	protected void fireColumnMarginChanged() {
		super.fireColumnMarginChanged();
		if (activeSave) {
			save();
		}
	}

	public void save() {
		if (table != null) {
			String title = table.getTitle();
			if (title != null) {
				SyPreferences prefs = SyPreferences.getPreferences();
				int l = Integer.parseInt(prefs.get(title + "_lenght", "-1")); //$NON-NLS-1$ //$NON-NLS-2$
				if (l != -1 && l != initialColumns.size()) {
					prefs.remove(title + "_lenght"); //$NON-NLS-1$
					for (int j = 0; j < l; j++) {
						prefs.remove(title + "_visible_" + j); //$NON-NLS-1$
						prefs.remove(title + "_position_" + j); //$NON-NLS-1$
						prefs.remove(title + "_size_" + j); //$NON-NLS-1$
					}
				}
				prefs.remove(title + "_autoResizeMode"); //$NON-NLS-1$

				prefs.put(title + "_lenght", String.valueOf(initialColumns //$NON-NLS-1$
						.size()));
				int i = 0;
				for (TableColumn column : initialColumns) {
					SyTableColumn c = (SyTableColumn) column;
					prefs.put(title + "_visible_" + i, String.valueOf(c //$NON-NLS-1$
							.isVisible()));
					prefs.put(title + "_position_" + i, String //$NON-NLS-1$
							.valueOf(currentColumns.indexOf(c)));
					prefs.put(title + "_size_" + i, String.valueOf(column //$NON-NLS-1$
							.getWidth()));

					i++;
				}

				prefs.put(title + "_autoResizeMode", String.valueOf(table //$NON-NLS-1$
						.getAutoResizeMode()));
			}
		}
	}

	public boolean isSave() {
		boolean res = false;
		if (table != null) {
			String title = table.getTitle();
			if (title != null) {
				SyPreferences prefs = SyPreferences.getPreferences();
				int l = Integer.parseInt(prefs.get(title + "_lenght", "-1")); //$NON-NLS-1$ //$NON-NLS-2$
				if (l != -1 && l == initialColumns.size()) {
					res = true;
				}
			}
		}
		return res;
	}

	public void load() {
		boolean load = false;

		activeSave = false;
		try {
			if (table != null) {
				String title = table.getTitle();
				if (title != null) {
					SyPreferences prefs = SyPreferences.getPreferences();
					int l = Integer
							.parseInt(prefs.get(title + "_lenght", "-1")); //$NON-NLS-1$ //$NON-NLS-2$
					if (l != -1) {
						if (l != initialColumns.size()) {
							prefs.remove(title + "_lenght"); //$NON-NLS-1$
							prefs.remove(title + "_autoResizeMode"); //$NON-NLS-1$

							for (int j = 0; j < l; j++) {
								prefs.remove(title + "_visible_" + j); //$NON-NLS-1$
								prefs.remove(title + "_position_" + j); //$NON-NLS-1$
								prefs.remove(title + "_size_" + j); //$NON-NLS-1$
							}
						} else {
							load = true;

							String s = prefs.get(title + "_autoResizeMode", //$NON-NLS-1$
									null);
							if (s != null) {
								table.setAutoResizeMode(Integer.parseInt(s));
							}

							int i = 0;
							for (TableColumn column : initialColumns) {
								String p = prefs
										.get(title + "_size_" + i, null); //$NON-NLS-1$
								if (p != null) {
									int size = Integer.parseInt(p);
									column.setWidth(size);
									column.setPreferredWidth(size);
								}
								i++;
							}

							i = 0;
							TableColumn[] ps = new TableColumn[initialColumns
									.size()];
							for (TableColumn column : initialColumns) {
								String p = prefs.get(title + "_position_" + i, //$NON-NLS-1$
										null);
								if (p != null) {
									int newIndex = Integer.parseInt(p);
									ps[newIndex] = column;
								} else {
									ps[i] = column;
								}

								super.removeColumn(column);

								i++;
							}

							currentColumns.clear();
							for (TableColumn column : ps) {
								currentColumns.add(column);
								super.addColumn(column);
							}

							i = 0;
							for (TableColumn column : initialColumns) {
								SyTableColumn c = (SyTableColumn) column;
								String v = prefs.get(title + "_visible_" + i, //$NON-NLS-1$
										null);
								if (v != null) {
									boolean visible = Boolean.parseBoolean(v);
									if (!visible) {
										c.setVisible(false);

										invisibleColumns.add(c);

										super.removeColumn(column);
									}
								}
								i++;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		activeSave = true;

		if (!load) {
			for (TableColumn column : initialColumns) {
				SyTableColumn c = (SyTableColumn) column;
				if (!c.isDefaultVisible()) {
					c.setVisible(false);

					invisibleColumns.add(c);

					super.removeColumn(column);
				}
			}
		}
	}

	public void load(int autoResizeMode, int[] positions, boolean[] visibles,
			int[] sizes, Object[] searchs, List<? extends SortKey> sortKeys) {
		activeSave = false;

		defaultColumns();

		table.setAutoResizeMode(autoResizeMode);

		int l = positions.length;
		if (l != -1 && l == initialColumns.size()) {
			int i = 0;
			for (TableColumn column : initialColumns) {
				int size = sizes[i];

				column.setWidth(size);
				column.setPreferredWidth(size);

				i++;
			}

			i = 0;
			TableColumn[] ps = new TableColumn[initialColumns.size()];
			for (TableColumn column : initialColumns) {
				int newIndex = positions[i];
				ps[newIndex] = column;

				super.removeColumn(column);

				i++;
			}

			currentColumns.clear();
			for (TableColumn column : ps) {
				SyTableColumn c = (SyTableColumn) column;
				c.setVisible(true);
				currentColumns.add(c);
				super.addColumn(c);
			}

			i = 0;
			for (TableColumn column : initialColumns) {
				SyTableColumn c = (SyTableColumn) column;
				boolean visible = visibles[i];
				if (!visible && c.isVisible()) {
					c.setVisible(false);

					invisibleColumns.add(c);

					super.removeColumn(column);
				}
				i++;
			}

			i = 0;
			for (TableColumn column : initialColumns) {
				SyTableColumn c = (SyTableColumn) column;
				Object search = searchs[i];
				c.setSearch(search);
				i++;
			}

			table.reconstructRowFilter();

			table.getRowSorter().setSortKeys(sortKeys);
		}
		save();
		activeSave = true;
	}
}
