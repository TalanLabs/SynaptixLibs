package com.synaptix.swing.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.SwingMessages;
import com.synaptix.swing.utils.SpringUtilities;

public class DialogVisibilityColumns extends JPanel {

	private static final long serialVersionUID = -5213595834965784882L;

	private static final String TEXT_TITLE = SwingMessages
			.getString("DialogVisibilityColumns.0"); //$NON-NLS-1$

	private Action closeAction;

	private JDialogModel dialog;

	private JSyTable table;

	private List<JCheckBox> visibleBoxs;

	public DialogVisibilityColumns(JSyTable table) {
		super(new BorderLayout());

		this.table = table;

		initActions();
		initComponents();

		this.add(buildEditorPane(), BorderLayout.CENTER);

	}

	private void initActions() {
		closeAction = new CloseAction();
	}

	private void initComponents() {
		visibleBoxs = new ArrayList<JCheckBox>();

		SyTableColumnModel tcm = (SyTableColumnModel) table.getColumnModel();
		TableModel model = table.getModel();

		for (TableColumn c : tcm.getColumns(true, false)) {
			final SyTableColumn tc = (SyTableColumn) c;
			JCheckBox checkBoxColumn = new JCheckBox(model.getColumnName(tc
					.getModelIndex()));
			checkBoxColumn.setSelected(tc.isVisible());

			if (tc.isLock()) {
				checkBoxColumn.setEnabled(false);
			} else {
				checkBoxColumn.setEnabled(true);
			}

			checkBoxColumn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent pEvent) {
					SyTableColumnModel ytcm = (SyTableColumnModel) table
							.getColumnModel();
					if (tc.isVisible()) {
						ytcm.invisibleColumn(tc);
					} else {
						ytcm.visibleColumn(tc);
					}

					updateEnableCheckBox();
				}
			});

			visibleBoxs.add(checkBoxColumn);
		}

		updateEnableCheckBox();
	}

	private JComponent buildEditorPane() {
		JPanel panel = new JPanel();
		panel.setLayout(new SpringLayout());

		for (JCheckBox checkBoxColumn : visibleBoxs) {
			panel.add(checkBoxColumn);
		}

		int cols = visibleBoxs.size() / 10;
		int j = visibleBoxs.size() % 10;
		if (j > 0) {
			for (int i = 0; i < 10 - j; i++) {
				panel.add(new JLabel());
			}
			cols++;
		}
		SpringUtilities.makeCompactGrid(panel, 10, cols, 6, 6, 6, 6);

		return panel;
	}

	private void updateEnableCheckBox() {
		SyTableColumnModel tcm = (SyTableColumnModel) table.getColumnModel();

		int i = 0;
		for (TableColumn c : tcm.getColumns(true, false)) {
			SyTableColumn tc = (SyTableColumn) c;
			JCheckBox checkBoxColumn = visibleBoxs.get(i);

			if ((tc.isVisible() && tcm.getColumnCount() == 1) || tc.isLock()) {
				checkBoxColumn.setEnabled(false);
			} else {
				checkBoxColumn.setEnabled(true);
			}

			i++;
		}
	}

	public void showDialog(Component parent) {
		dialog = new JDialogModel(parent, TEXT_TITLE, this,
				new Action[] { closeAction }, closeAction);
		dialog.showDialog();
		dialog.dispose();
	}

	private class CloseAction extends AbstractAction {

		private static final long serialVersionUID = 7204804025668857817L;

		public CloseAction() {
			super(SwingMessages.getString("DialogVisibilityColumns.1")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			dialog.closeDialog();
		}
	}
}
