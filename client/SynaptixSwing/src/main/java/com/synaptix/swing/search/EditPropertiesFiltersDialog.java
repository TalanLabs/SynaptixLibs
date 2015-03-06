package com.synaptix.swing.search;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.DropMode;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.SearchMessages;
import com.synaptix.swing.event.SearchFilterModelEvent;
import com.synaptix.swing.event.SearchFilterModelListener;
import com.synaptix.swing.search.Filter.TypeVisible;
import com.synaptix.swing.search.transferable.SearchFilterTransferable;
import com.synaptix.swing.utils.GUIWindow;

public class EditPropertiesFiltersDialog extends JPanel {

	private static final long serialVersionUID = 4738563551810419139L;

	public final static int DEFAULT_OPTION = 0;

	public final static int CLOSE_OPTION = 1;

	private static final String TEXT_TITLE = SearchMessages.getString("EditPropertiesFiltersDialog.0"); //$NON-NLS-1$

	private JDialogModel dialog;

	private JSearchHeader searchHeader;

	private JCheckBox[] checkBoxs;

	private JList filterList;

	private Action applyAction;

	private Action closeAction;

	private Action defaultAction;

	private int result;

	public EditPropertiesFiltersDialog(JSearchHeader searchHeader) {
		super(new BorderLayout());

		this.searchHeader = searchHeader;

		initActions();
		initComponents();

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initActions() {
		applyAction = new ApplyAction();
		closeAction = new CloseAction();
		defaultAction = new DefaultAction();
	}

	private void initComponents() {
	}

	private JComponent buildContents() {
		final SearchFilterModel filterModel = searchHeader.getFilterModel();

		int[][] rowGroups = new int[1][filterModel.getFilterCount(true)];
		for (int i = 0; i < filterModel.getFilterCount(true); i++) {
			rowGroups[0][i] = (i + 1) * 2 + 3;
		}

		List<RowSpec> rowSpecs = new ArrayList<RowSpec>();
		rowSpecs.add(new RowSpec("3dlu")); //$NON-NLS-1$
		rowSpecs.add(new RowSpec("p")); //$NON-NLS-1$
		rowSpecs.add(new RowSpec("5dlu")); //$NON-NLS-1$
		for (int i = 0; i < filterModel.getFilterCount(true); i++) {
			rowSpecs.add(new RowSpec("3dlu")); //$NON-NLS-1$
			rowSpecs.add(new RowSpec("p")); //$NON-NLS-1$
		}
		rowSpecs.add(new RowSpec("3dlu")); //$NON-NLS-1$

		FormLayout layout = new FormLayout(ColumnSpec.decodeSpecs("3dlu,p,3dlu,d,3dlu,75dlu:grow,3dlu"), rowSpecs //$NON-NLS-1$
				.toArray(new RowSpec[rowSpecs.size()]));
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();

		layout.setRowGroups(rowGroups);

		builder.addLabel(SearchMessages.getString("EditPropertiesFiltersDialog.8"), cc.xy(2, 2)); //$NON-NLS-1$
		builder.addLabel(SearchMessages.getString("EditPropertiesFiltersDialog.9"), cc.xy(4, 2)); //$NON-NLS-1$
		builder.addLabel(SearchMessages.getString("EditPropertiesFiltersDialog.10"), cc.xy(6, 2)); //$NON-NLS-1$
		builder.addSeparator("", cc.xyw(2, 3, 5)); //$NON-NLS-1$

		checkBoxs = new JCheckBox[filterModel.getFilterCount(true)];

		int y;
		for (int i = 0; i < filterModel.getFilterCount(true); i++) {
			final SearchFilter searchFilter = filterModel.getFilter(i, true);

			y = (i + 1) * 2 + 3;

			JCheckBox checkBox = new JCheckBox(searchFilter.getName());
			if (searchFilter.getTypeVisible() != TypeVisible.Invisible) {
				checkBox.setSelected(searchFilter.isVisible());
			} else {
				checkBox.setSelected(false);
			}

			if (searchFilter.getTypeVisible() == TypeVisible.Visible) {
				checkBox.setEnabled(true);
			} else {
				checkBox.setEnabled(false);
			}
			checkBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent pEvent) {
					if (searchFilter.isVisible()) {
						filterModel.invisibleFilter(searchFilter);
					} else {
						filterModel.visibleFilter(searchFilter);
					}
				}
			});
			checkBoxs[i] = checkBox;
			builder.add(checkBox, cc.xy(2, y));

			if (searchFilter.getDefaultComponent() != null && searchFilter.getTypeVisible() != TypeVisible.Invisible) {
				builder.add(searchFilter.getDefaultComponent(), cc.xy(4, y));
			}
		}

		filterList = new JList(new MyListModel());
		filterList.setCellRenderer(new SearchFilterListCellRenderer());
		filterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		filterList.setTransferHandler(new MyTransferHandler());
		filterList.setDragEnabled(true);
		filterList.setDropMode(DropMode.INSERT);

		builder.add(new JScrollPane(filterList), cc.xywh(6, 5, 1, filterModel.getFilterCount(true) * 2));

		JScrollPane scrollPane = new JScrollPane(builder.getPanel());
		scrollPane.setBorder(null);

		return scrollPane;
	}

	public int showDialog(Component parent) {
		result = CLOSE_OPTION;

		dialog = new JDialogModel(parent, TEXT_TITLE, this, new Action[] { applyAction, defaultAction, closeAction }, closeAction);
		dialog.showDialog();
		dialog.dispose();

		return result;
	}

	private void defaultValues() {
		SearchFilterModel filterModel = searchHeader.getFilterModel();
		filterModel.defaultFilters();

		for (int i = 0; i < filterModel.getFilterCount(true); i++) {
			SearchFilter searchFilter = filterModel.getFilter(i, true);
			if (searchFilter.getTypeVisible() == TypeVisible.Visible) {
				checkBoxs[i].setSelected(searchFilter.isVisible());
			}
		}
	}

	private class ApplyAction extends AbstractAction {

		private static final long serialVersionUID = 7372031327454983068L;

		public ApplyAction() {
			super(SearchMessages.getString("EditPropertiesFiltersDialog.12")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			SearchFilterModel filterModel = searchHeader.getFilterModel();
			for (int i = 0; i < filterModel.getFilterCount(true); i++) {
				SearchFilter searchFilter = filterModel.getFilter(i, true);
				searchFilter.copyDefaultValue();
			}
		}
	}

	private class DefaultAction extends AbstractAction {

		private static final long serialVersionUID = 7372031327454983068L;

		public DefaultAction() {
			super(SearchMessages.getString("EditPropertiesFiltersDialog.13")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			// defaultValues();
			if (JOptionPane.showConfirmDialog(GUIWindow.getActiveWindow(), SearchMessages.getString("EditPropertiesFiltersDialog.14"), //$NON-NLS-1$
					SearchMessages.getString("EditPropertiesFiltersDialog.13"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) { //$NON-NLS-1$
				result = DEFAULT_OPTION;
				dialog.closeDialog();
			}
		}
	}

	private class CloseAction extends AbstractAction {

		private static final long serialVersionUID = 7204804025668857817L;

		public CloseAction() {
			super(SearchMessages.getString("EditPropertiesFiltersDialog.16")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			result = CLOSE_OPTION;
			dialog.closeDialog();
		}
	}

	private final class SearchFilterListCellRenderer extends JLabel implements ListCellRenderer {

		private static final long serialVersionUID = -5676024398548188106L;

		public SearchFilterListCellRenderer() {
			super("", JLabel.LEFT); //$NON-NLS-1$
			this.setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			if (value != null && value instanceof SearchFilter) {
				SearchFilter tosd = (SearchFilter) value;
				this.setText(tosd.getName());
			} else {
				this.setText(null);
			}

			return this;
		}
	}

	private final class MyTransferHandler extends TransferHandler {

		private static final long serialVersionUID = -8856940199334161039L;

		@Override
		protected Transferable createTransferable(JComponent c) {
			JList list = (JList) c;
			if (!list.getSelectionModel().isSelectionEmpty()) {
				return new SearchFilterTransferable(list.getSelectedIndex());
			}
			return null;
		}

		@Override
		public int getSourceActions(JComponent c) {
			return MOVE;
		}

		@Override
		public boolean canImport(TransferSupport support) {
			if (support.isDataFlavorSupported(SearchFilterTransferable.SEARCHFILTER_FLAVOR) && support.getTransferable() != null) {
				JList.DropLocation dropLocation = (JList.DropLocation) support.getDropLocation();
				return dropLocation.isInsert();
			}
			return false;
		}

		@Override
		public boolean importData(TransferSupport support) {
			if (canImport(support)) {
				try {
					JList.DropLocation dropLocation = (JList.DropLocation) support.getDropLocation();

					int index = (Integer) support.getTransferable().getTransferData(SearchFilterTransferable.SEARCHFILTER_FLAVOR);

					SearchFilterModel filterModel = searchHeader.getFilterModel();
					if (index >= dropLocation.getIndex()) {
						filterModel.moveFilter(index, dropLocation.getIndex());
					} else {
						filterModel.moveFilter(index, dropLocation.getIndex() - 1);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return false;
		}
	}

	private final class MyListModel extends AbstractListModel {

		private static final long serialVersionUID = -8025630494365222613L;

		public MyListModel() {
			super();

			SearchFilterModel filterModel = searchHeader.getFilterModel();
			filterModel.addFilterModelListener(new MySearchFilterModelListener());
		}

		@Override
		public Object getElementAt(int index) {
			SearchFilterModel filterModel = searchHeader.getFilterModel();
			return filterModel.getFilter(index);
		}

		@Override
		public int getSize() {
			SearchFilterModel filterModel = searchHeader.getFilterModel();
			return filterModel.getFilterCount();
		}

		private class MySearchFilterModelListener implements SearchFilterModelListener {
			public void filterAdded(SearchFilterModelEvent e) {
				fireIntervalAdded(e.getSource(), e.getFromIndex(), e.getFromIndex());
			}

			public void filterMoved(SearchFilterModelEvent e) {
				fireContentsChanged(e.getSource(), e.getFromIndex(), e.getToIndex());
			}

			public void filterRemoved(SearchFilterModelEvent e) {
				fireIntervalRemoved(e.getSource(), e.getFromIndex(), e.getFromIndex());
			}
		}
	}
}
