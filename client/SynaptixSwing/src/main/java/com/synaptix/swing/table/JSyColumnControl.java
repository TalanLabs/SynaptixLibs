package com.synaptix.swing.table;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTable;
import javax.swing.SortOrder;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.TableColumn;

import com.synaptix.prefs.SyPreferences;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.SwingMessages;
import com.synaptix.swing.utils.GUIWindow;
import com.synaptix.swing.utils.Utils;

public class JSyColumnControl extends JButton {

	private static final long serialVersionUID = -6541355307417958411L;

	protected int maxItemPopupMenu;

	protected JPopupMenu popupMenu;

	protected JSyTable table;

	private JMenu openPerspectives;

	private JMenuItem emptyPersonalPerspectiveMenuItem;

	private JMenuItem emptyDefaultPerspectiveMenuItem;

	private Action savePerspectiveAction;

	private Action deletePerspectiveAction;

	protected JRadioButtonMenuItem autoResizeColumnAll;

	protected JRadioButtonMenuItem autoResizeColumnSub;

	protected JRadioButtonMenuItem autoResizeColumnNext;

	protected JRadioButtonMenuItem autoResizeColumnLast;

	protected JRadioButtonMenuItem autoResizeColumnNone;

	protected JMenuItem exportXLSCSV;

	private List<Perspective> defaultPerspectives;

	private List<Perspective> personalPerspectives;

	private JRadioButtonMenuItem allRowCountMenuItem;

	private JRadioButtonMenuItem filterRowCountMenuItem;
	
	private JMenu rowCountMenu;
	
	private JMenu resizeColumnMenu;

	public JSyColumnControl() {
		super();
		this.setAction(new ControlButtonAction(new ColumnControlIcon()));

		defaultPerspectives = new ArrayList<Perspective>();
		personalPerspectives = new ArrayList<Perspective>();

		initActions();
		initComponents();
	}

	private void initActions() {
		deletePerspectiveAction = new DeletePerspectiveAction();
		deletePerspectiveAction.setEnabled(false);

		savePerspectiveAction = new SavePerspectiveMenuAction();
		savePerspectiveAction.setEnabled(false);
	}

	private void createComponents() {
		openPerspectives = new JMenu(SwingMessages
				.getString("JSyColumnControl.0")); //$NON-NLS-1$

		resizeColumnMenu = new JMenu(SwingMessages
				.getString("JSyColumnControl.90")); //$NON-NLS-1$
		
		rowCountMenu = new JMenu(SwingMessages
				.getString("JSyColumnControl.91")); //$NON-NLS-1$
		
		emptyPersonalPerspectiveMenuItem = new JMenuItem(
				new EmptyPersonalPerspectiveAction());
		emptyDefaultPerspectiveMenuItem = new JMenuItem(
				new EmptyDefaultPerspectiveAction());

		autoResizeColumnSub = new JRadioButtonMenuItem(
				new AutoResizeColumnSubAction());
		autoResizeColumnAll = new JRadioButtonMenuItem(
				new AutoResizeColumnAllAction());
		autoResizeColumnNext = new JRadioButtonMenuItem(
				new AutoResizeColumnNextAction());
		autoResizeColumnLast = new JRadioButtonMenuItem(
				new AutoResizeColumnLastAction());
		autoResizeColumnNone = new JRadioButtonMenuItem(
				new AutoResizeColumnNoneAction());
		
		exportXLSCSV = new JMenuItem(new ExportXLSCSVAction());
		
		allRowCountMenuItem = new JRadioButtonMenuItem(new AllRowCountAction());
		filterRowCountMenuItem = new JRadioButtonMenuItem(new FilterRowCountAction());
	}

	private void initComponents() {
		createComponents();

		this.setFocusPainted(false);
		this.setFocusable(false);
		table = null;
		popupMenu = null;
		maxItemPopupMenu = 20;

		ButtonGroup columnGroup = new ButtonGroup();
		columnGroup.add(autoResizeColumnSub);
		columnGroup.add(autoResizeColumnAll);
		columnGroup.add(autoResizeColumnNext);
		columnGroup.add(autoResizeColumnLast);
		columnGroup.add(autoResizeColumnNone);
		
		resizeColumnMenu.add(autoResizeColumnSub);
		resizeColumnMenu.add(autoResizeColumnAll);
		resizeColumnMenu.add(autoResizeColumnNext);
		resizeColumnMenu.add(autoResizeColumnLast);
		resizeColumnMenu.add(autoResizeColumnNone);
		
		ButtonGroup rowCountGroup = new ButtonGroup();
		rowCountGroup.add(allRowCountMenuItem);
		rowCountGroup.add(filterRowCountMenuItem);
		
		rowCountMenu.add(allRowCountMenuItem);
		rowCountMenu.add(filterRowCountMenuItem);

		this.setToolTipText(SwingMessages.getString("JSyColumnControl.1")); //$NON-NLS-1$
	}

	public JSyTable getTable() {
		return table;
	}

	public void setTable(JSyTable table) {
		if (this.table != table) {
			JTable old = this.table;
			this.table = table;
			firePropertyChange("table", old, table); //$NON-NLS-1$
		}
	}

	protected void createPopupMenu() {
		popupMenu = new JPopupMenu();

		openPerspectives.removeAll();

		SyTableColumnModel tcm = (SyTableColumnModel) table.getColumnModel();

		if (defaultPerspectives.size() > 0) {
			for (Perspective p : defaultPerspectives) {
				openPerspectives.add(new JMenuItem(
						new OpenPerspectiveMenuAction(p)));
			}
		} else {
			openPerspectives.add(emptyDefaultPerspectiveMenuItem);
		}
		openPerspectives.addSeparator();

		if (personalPerspectives.size() > 0) {
			for (Perspective p : personalPerspectives) {
				openPerspectives.add(new JMenuItem(
						new OpenPerspectiveMenuAction(p)));
			}
		} else {
			openPerspectives.add(emptyPersonalPerspectiveMenuItem);
		}

		popupMenu.add(openPerspectives);
		popupMenu.addSeparator();
		popupMenu.add(savePerspectiveAction);
		popupMenu.add(deletePerspectiveAction);
		popupMenu.addSeparator();

		JPopupMenu current = popupMenu;

		int i = 0;
		for (TableColumn c : tcm.getColumns(true, false)) {
			SyTableColumn tc = (SyTableColumn) c;
			Action action = new ColumnAction(tc);

			if (i >= maxItemPopupMenu && (i % maxItemPopupMenu) == 0) {
				JMenu menu = new JMenu(SwingMessages
						.getString("JSyColumnControl.3")); //$NON-NLS-1$
				current.add(menu);
				current = menu.getPopupMenu();
			}

			JCheckBoxMenuItem item = new JCheckBoxMenuItem(action);
			item.setSelected(tc.isVisible());

			if ((tc.isVisible() && tcm.getColumnCount() == 1) || tc.isLock()) {
				item.setEnabled(false);
			} else {
				item.setEnabled(true);
			}
			current.add(item);
			i++;
		}

		popupMenu.addSeparator();
		popupMenu.add(new DefaultColumnAction());

		popupMenu.addSeparator();
		popupMenu.add(resizeColumnMenu);
		popupMenu.add(rowCountMenu);
		
		switch (table.getAutoResizeMode()) {
		case JTable.AUTO_RESIZE_ALL_COLUMNS:
			autoResizeColumnAll.setSelected(true);
			break;
		case JTable.AUTO_RESIZE_NEXT_COLUMN:
			autoResizeColumnNext.setSelected(true);
			break;
		case JTable.AUTO_RESIZE_OFF:
			autoResizeColumnNone.setSelected(true);
			break;
		case JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS:
			autoResizeColumnSub.setSelected(true);
			break;
		case JTable.AUTO_RESIZE_LAST_COLUMN:
			autoResizeColumnLast.setSelected(true);
			break;
		}
		
		if (table.isDisplayAllRowCount()) {
			allRowCountMenuItem.setSelected(true);
		} else {
			filterRowCountMenuItem.setSelected(true);
		}

		popupMenu.addSeparator();
		popupMenu.add(exportXLSCSV);

		popupMenu.revalidate();
		popupMenu.pack();
	}

	protected void inversePopupMenu() {
		createPopupMenu();
		int w = popupMenu.getSize().width;
		if (w == 0) {
			w = popupMenu.getPreferredSize().width;
		}

		popupMenu.show(this, -w + this.getWidth(), this.getHeight());
	}

	public int getMaxItemPopupMenu() {
		return maxItemPopupMenu;
	}

	public void setMaxItemPopupMenu(int maxItemPopupMenu) {
		this.maxItemPopupMenu = maxItemPopupMenu;
	}

	private void savePreferences() {
		String title = table.getTitle();
		if (title != null) {
			SyPreferences prefs = SyPreferences.getPreferences();

			int nbPerspectives = prefs.getInt(title
					+ "_personalPerspectives_lenght", -1); //$NON-NLS-1$
			if (nbPerspectives != -1) {
				prefs.remove(title + "_personalPerspectives_lenght"); //$NON-NLS-1$
				for (int i = 0; i < nbPerspectives; i++) {
					prefs.remove(title + "_personalPerspectives_" + i //$NON-NLS-1$
							+ "_name"); //$NON-NLS-1$
					prefs.remove(title + "_personalPerspectives_" + i //$NON-NLS-1$
							+ "_autoResizeMode"); //$NON-NLS-1$

					int l = prefs.getInt(title + "_personalPerspectives_" + i //$NON-NLS-1$
							+ "_lenght", -1); //$NON-NLS-1$
					if (l != -1) {
						for (int j = 0; j < l; j++) {
							prefs.remove(title + "_personalPerspectives_" + i //$NON-NLS-1$
									+ "_visible_" + j); //$NON-NLS-1$
							prefs.remove(title + "_personalPerspectives_" + i //$NON-NLS-1$
									+ "_position_" + j); //$NON-NLS-1$
							prefs.remove(title + "_personalPerspectives_" + i //$NON-NLS-1$
									+ "_size_" + j); //$NON-NLS-1$
							prefs.remove(title + "_personalPerspectives_" + i //$NON-NLS-1$
									+ "_search_" + j); //$NON-NLS-1$
							prefs.remove(title + "_personalPerspectives_" + i //$NON-NLS-1$
									+ "_sort_" + j); //$NON-NLS-1$
						}
						prefs.remove(title + "_personalPerspectives_" + i //$NON-NLS-1$
								+ "_lenght"); //$NON-NLS-1$
					}

					l = prefs.getInt(title + "_personalPerspectives_" + i //$NON-NLS-1$
							+ "_sort_lenght", -1); //$NON-NLS-1$
					if (l != -1) {
						for (int j = 0; j < l; j++) {
							prefs.remove(title + "_personalPerspectives_" + i //$NON-NLS-1$
									+ "_sort_column_" + j); //$NON-NLS-1$
							prefs.remove(title + "_personalPerspectives_" + i //$NON-NLS-1$
									+ "_sort_order_" + j); //$NON-NLS-1$
						}
						prefs.remove(title + "_personalPerspectives_" + i //$NON-NLS-1$
								+ "_sort_lenght"); //$NON-NLS-1$
					}
				}
			}

			if (personalPerspectives.size() > 0) {
				prefs.putInt(title + "_personalPerspectives_lenght", //$NON-NLS-1$
						personalPerspectives.size());
				for (int i = 0; i < personalPerspectives.size(); i++) {
					Perspective p = personalPerspectives.get(i);

					prefs.put(title + "_personalPerspectives_" + i + "_name", //$NON-NLS-1$ //$NON-NLS-2$
							p.name);
					prefs.putInt(title + "_personalPerspectives_" + i //$NON-NLS-1$
							+ "_autoResizeMode", p.autoResizeMode); //$NON-NLS-1$

					prefs.putInt(title + "_personalPerspectives_" + i //$NON-NLS-1$
							+ "_lenght", p.positions.length); //$NON-NLS-1$
					for (int j = 0; j < p.positions.length; j++) {
						prefs.putBoolean(title + "_personalPerspectives_" + i //$NON-NLS-1$
								+ "_visible_" + j, p.visibles[j]); //$NON-NLS-1$
						prefs.putInt(title + "_personalPerspectives_" + i //$NON-NLS-1$
								+ "_position_" + j, p.positions[j]); //$NON-NLS-1$
						prefs.putInt(title + "_personalPerspectives_" + i //$NON-NLS-1$
								+ "_size_" + j, p.sizes[j]); //$NON-NLS-1$

						Object value = p.searchs[j];
						byte[] bs = Utils
								.convertObjectToByteArray((Serializable) value);
						String s = Utils.convertByteArrayToString(bs);
						prefs.put(title + "_personalPerspectives_" + i //$NON-NLS-1$
								+ "_search_" + j, s); //$NON-NLS-1$
					}

					if (p.sortKeys != null) {
						prefs.putInt(title + "_personalPerspectives_" + i //$NON-NLS-1$
								+ "_sort_lenght", p.sortKeys.size()); //$NON-NLS-1$
						for (int j = 0; j < p.sortKeys.size(); j++) {
							SortKey sortKey = p.sortKeys.get(j);
							prefs.putInt(title + "_personalPerspectives_" + i //$NON-NLS-1$
									+ "_sort_column_" + j, sortKey.getColumn()); //$NON-NLS-1$
							prefs.put(title + "_personalPerspectives_" + i //$NON-NLS-1$
									+ "_sort_order_" + j, sortKey //$NON-NLS-1$
									.getSortOrder().name());
						}
					}
				}
			}
		}
	}

	public void load() {
		loadPreferences();

		String title = table.getTitle();
		savePerspectiveAction.setEnabled(title != null);
		deletePerspectiveAction.setEnabled(personalPerspectives.size() > 0);
	}

	private void loadPreferences() {
		String title = table.getTitle();
		if (title != null) {
			SyPreferences prefs = SyPreferences.getPreferences();

			SyTableColumnModel ytcm = (SyTableColumnModel) table
					.getColumnModel();
			List<TableColumn> initialColumns = ytcm.getColumns(true, true);

			int nbPerspectives = prefs.getInt(title
					+ "_personalPerspectives_lenght", -1); //$NON-NLS-1$
			if (nbPerspectives != -1) {
				for (int i = 0; i < nbPerspectives; i++) {
					int l = prefs.getInt(title + "_personalPerspectives_" + i //$NON-NLS-1$
							+ "_lenght", -1); //$NON-NLS-1$
					if (l != -1) {
						if (l == initialColumns.size()) {
							Perspective p = new Perspective();

							p.name = prefs.get(title
									+ "_personalPerspectives_" + i //$NON-NLS-1$
									+ "_name", "No name" + i); //$NON-NLS-1$ //$NON-NLS-2$
							p.autoResizeMode = prefs
									.getInt(
											title
													+ "_personalPerspectives_" + i + "_autoResizeMode", //$NON-NLS-1$ //$NON-NLS-2$
											JTable.AUTO_RESIZE_ALL_COLUMNS);

							p.visibles = new boolean[l];
							p.positions = new int[l];
							p.sizes = new int[l];
							p.searchs = new Object[l];

							for (int j = 0; j < p.positions.length; j++) {
								p.visibles[j] = prefs.getBoolean(title
										+ "_personalPerspectives_" + i //$NON-NLS-1$
										+ "_visible_" + j, true); //$NON-NLS-1$
								p.positions[j] = prefs.getInt(title
										+ "_personalPerspectives_" + i //$NON-NLS-1$
										+ "_position_" + j, j); //$NON-NLS-1$
								p.sizes[j] = prefs.getInt(title
										+ "_personalPerspectives_" + i //$NON-NLS-1$
										+ "_size_" + j, 75); //$NON-NLS-1$

								String value = prefs.get(title
										+ "_personalPerspectives_" + i //$NON-NLS-1$
										+ "_search_" + j, null); //$NON-NLS-1$
								if (value != null) {
									byte[] bs = Utils
											.convertStringToByteArray(value);
									p.searchs[j] = Utils
											.convertByteArrayToObject(bs);
								} else {
									p.searchs[j] = null;
								}
							}

							l = prefs.getInt(title
									+ "_personalPerspectives_" + i //$NON-NLS-1$
									+ "_sort_lenght", -1); //$NON-NLS-1$
							if (l != -1) {
								List<SortKey> l1 = new ArrayList<SortKey>();
								for (int j = 0; j < l; j++) {
									int column = prefs.getInt(title
											+ "_personalPerspectives_" + i //$NON-NLS-1$
											+ "_sort_column_" + j, -1); //$NON-NLS-1$
									String name = prefs
											.get(
													title
															+ "_personalPerspectives_" + i //$NON-NLS-1$
															+ "_sort_order_" + j, SortOrder.UNSORTED //$NON-NLS-1$
															.name());

									SortKey sortKey = new SortKey(column,
											SortOrder.valueOf(name));
									l1.add(sortKey);
								}
								p.sortKeys = l1;
							} else {
								p.sortKeys = null;
							}

							personalPerspectives.add(p);
						}
					}
				}
			}
		}
	}

	private Perspective getPercpectiveByName(String name) {
		for (Perspective p : personalPerspectives) {
			if (p.name.equals(name)) {
				return p;
			}
		}
		return null;
	}

	private void savePersonalPerspective() {
		String name = JOptionPane
				.showInputDialog(
						GUIWindow.getActiveWindow(),
						SwingMessages.getString("JSyColumnControl.75"), SwingMessages.getString("JSyColumnControl.76"), //$NON-NLS-1$ //$NON-NLS-2$
						JOptionPane.QUESTION_MESSAGE);
		if (name != null && !name.trim().isEmpty()) {
			SyTableColumnModel ytcm = (SyTableColumnModel) table
					.getColumnModel();

			List<TableColumn> initialColumns = ytcm.getColumns(true, true);
			List<TableColumn> currentColumns = ytcm.getColumns(true, false);

			Perspective p = getPercpectiveByName(name);
			if (p == null) {
				p = new Perspective();
			}

			p.name = name;
			p.autoResizeMode = table.getAutoResizeMode();
			p.sortKeys = table.getRowSorter().getSortKeys();

			p.positions = new int[initialColumns.size()];
			p.visibles = new boolean[initialColumns.size()];
			p.sizes = new int[initialColumns.size()];
			p.searchs = new Object[initialColumns.size()];

			int i = 0;
			for (TableColumn column : initialColumns) {
				SyTableColumn c = (SyTableColumn) column;
				p.visibles[i] = c.isVisible();
				p.positions[i] = currentColumns.indexOf(c);
				p.sizes[i] = column.getWidth();
				p.searchs[i] = c.getSearch();
				i++;
			}

			personalPerspectives.add(p);

			deletePerspectiveAction.setEnabled(true);

			savePreferences();
		}
	}

	public void deletePersonalPerspective() {
		Perspective p = (Perspective) JOptionPane
				.showInputDialog(
						GUIWindow.getActiveWindow(),
						SwingMessages.getString("JSyColumnControl.77"), //$NON-NLS-1$
						SwingMessages.getString("JSyColumnControl.78"), JOptionPane.QUESTION_MESSAGE, null, //$NON-NLS-1$
						personalPerspectives.toArray(), personalPerspectives
								.get(0));
		if (p != null) {
			personalPerspectives.remove(p);

			if (personalPerspectives.size() == 0) {
				deletePerspectiveAction.setEnabled(false);
			}

			savePreferences();
		}
	}

	public void addDefaultPerspective(String name, int autoResizeMode,
			int[] positions, boolean[] visibles, int[] sizes, Object[] searchs,
			List<? extends SortKey> sortKeys) {
		Perspective p = new Perspective();
		p.name = name;
		p.autoResizeMode = autoResizeMode;
		p.positions = positions;
		p.visibles = visibles;
		p.sizes = sizes;
		p.searchs = searchs;
		p.sortKeys = sortKeys;

		defaultPerspectives.add(p);
	}

	private final class Perspective {

		String name;

		int autoResizeMode;

		int[] positions;

		boolean[] visibles;

		int[] sizes;

		Object[] searchs;

		List<? extends SortKey> sortKeys;

		public String toString() {
			return name;
		}
	}

	private final class OpenPerspectiveMenuAction extends AbstractAction {

		private static final long serialVersionUID = -1848517650204131096L;

		private Perspective perspective;

		public OpenPerspectiveMenuAction(Perspective perspective) {
			super();
			this.perspective = perspective;
			this.putValue(Action.NAME, perspective.name);
		}

		public void actionPerformed(ActionEvent e) {
			SyTableColumnModel ytcm = (SyTableColumnModel) table
					.getColumnModel();
			ytcm.load(perspective.autoResizeMode, perspective.positions,
					perspective.visibles, perspective.sizes,
					perspective.searchs, perspective.sortKeys);
		}
	}

	private final class AutoResizeColumnAllAction extends AbstractAction {

		private static final long serialVersionUID = -4719113289304295816L;

		public AutoResizeColumnAllAction() {
			super(SwingMessages.getString("JSyColumnControl.79")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			SyTableColumnModel ytcm = (SyTableColumnModel) table
					.getColumnModel();
			ytcm.save();
		}
	}

	private final class AutoResizeColumnSubAction extends AbstractAction {

		private static final long serialVersionUID = 6016560077308583092L;

		public AutoResizeColumnSubAction() {
			super(SwingMessages.getString("JSyColumnControl.80")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			SyTableColumnModel ytcm = (SyTableColumnModel) table
					.getColumnModel();
			ytcm.save();
		}
	}

	private final class AutoResizeColumnNextAction extends AbstractAction {

		private static final long serialVersionUID = -1645220352305427405L;

		public AutoResizeColumnNextAction() {
			super(SwingMessages.getString("JSyColumnControl.81")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
			SyTableColumnModel ytcm = (SyTableColumnModel) table
					.getColumnModel();
			ytcm.save();
		}
	}

	private final class AutoResizeColumnLastAction extends AbstractAction {

		private static final long serialVersionUID = -1645220352305427405L;

		public AutoResizeColumnLastAction() {
			super(SwingMessages.getString("JSyColumnControl.82")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
			SyTableColumnModel ytcm = (SyTableColumnModel) table
					.getColumnModel();
			ytcm.save();
		}
	}

	private final class AutoResizeColumnNoneAction extends AbstractAction {

		private static final long serialVersionUID = 3286920082200997313L;

		public AutoResizeColumnNoneAction() {
			super(SwingMessages.getString("JSyColumnControl.83")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			SyTableColumnModel ytcm = (SyTableColumnModel) table
					.getColumnModel();
			ytcm.save();
		}
	}

	private final class DefaultColumnAction extends AbstractAction {

		private static final long serialVersionUID = 7753141188327561915L;

		public DefaultColumnAction() {
			super(SwingMessages.getString("JSyColumnControl.84")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			SyTableColumnModel ytcm = (SyTableColumnModel) table
					.getColumnModel();
			ytcm.defaultColumns();
		}
	}

	private final class ColumnAction extends AbstractAction {

		private static final long serialVersionUID = 2091527413339688970L;

		private TableColumn column;

		public ColumnAction(TableColumn column) {
			super(column.getHeaderValue().toString());
			this.column = column;
		}

		public void actionPerformed(ActionEvent e) {
			SyTableColumn c = (SyTableColumn) column;
			SyTableColumnModel ytcm = (SyTableColumnModel) table
					.getColumnModel();
			if (c.isVisible()) {
				ytcm.invisibleColumn(c);
			} else {
				ytcm.visibleColumn(c);
			}
		}
	}

	private final class ControlButtonAction extends AbstractAction {

		private static final long serialVersionUID = 3192787412586147481L;

		public ControlButtonAction(Icon icon) {
			super(null, icon);
		}

		public void actionPerformed(ActionEvent e) {
			inversePopupMenu();
		}
	}

	private final class ExportXLSCSVAction extends AbstractAction {

		private static final long serialVersionUID = 5634702497842182426L;

		public ExportXLSCSVAction() {
			super(SwingMessages.getString("JSyColumnControl.85")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			table.exportTable();
		}
	}

	private final class SavePerspectiveMenuAction extends AbstractAction {

		private static final long serialVersionUID = -4372546543499693455L;

		public SavePerspectiveMenuAction() {
			super(SwingMessages.getString("JSyColumnControl.86")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			savePersonalPerspective();
		}
	}

	private final class DeletePerspectiveAction extends AbstractAction {

		private static final long serialVersionUID = -4372546543499693455L;

		public DeletePerspectiveAction() {
			super(SwingMessages.getString("JSyColumnControl.87")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			deletePersonalPerspective();
		}
	}

	private final class EmptyPersonalPerspectiveAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public EmptyPersonalPerspectiveAction() {
			super(SwingMessages.getString("JSyColumnControl.88")); //$NON-NLS-1$

			this.setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
		}
	}

	private final class EmptyDefaultPerspectiveAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public EmptyDefaultPerspectiveAction() {
			super(SwingMessages.getString("JSyColumnControl.89")); //$NON-NLS-1$

			this.setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
		}
	}
	
	private final class AllRowCountAction extends AbstractAction {

		private static final long serialVersionUID = 3286920082200997313L;

		public AllRowCountAction() {
			super(SwingMessages.getString("JSyColumnControl.92")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			table.setDisplayAllRowCount(true);
		}
	}
	
	private final class FilterRowCountAction extends AbstractAction {

		private static final long serialVersionUID = 3286920082200997313L;

		public FilterRowCountAction() {
			super(SwingMessages.getString("JSyColumnControl.93")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			table.setDisplayAllRowCount(false);
		}
	}
}
