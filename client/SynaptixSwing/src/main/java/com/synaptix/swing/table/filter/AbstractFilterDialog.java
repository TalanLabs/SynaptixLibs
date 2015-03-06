package com.synaptix.swing.table.filter;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.SwingMessages;
import com.synaptix.swing.widget.AbstractAcceptAction;
import com.synaptix.swing.widget.AbstractCancelAction;
import com.synaptix.swing.widget.AbstractClearAction;

public abstract class AbstractFilterDialog<E> extends JPanel {

	private static final long serialVersionUID = 8657039450727283600L;

	public static final int OK_OPTION = 1;

	public static final int CANCEL_OPTION = 2;

	public static final int RESET_OPTION = 3;

	protected JDialogModel dialog;

	protected int resultOption;

	protected JSyTable table;

	protected int columnIndex;

	protected Action acceptAction;

	protected Action cancelAction;

	protected Action clearAction;

	public AbstractFilterDialog() {
		super(new BorderLayout());

		initActions();
		initComponents();

		this.add(buildContents(), BorderLayout.CENTER);
	}

	protected void initActions() {
		acceptAction = new AcceptAction();
		cancelAction = new CancelAction();
		clearAction = new ClearAction();
	}

	protected abstract void initComponents();

	protected abstract JComponent buildContents();

	protected abstract void initiliazeSearch(E e);

	public int showDialog(JSyTable table, int columnIndex, String name, E e) {
		resultOption = CANCEL_OPTION;

		this.table = table;
		this.columnIndex = columnIndex;

		dialog = new JDialogModel(table, MessageFormat.format(SwingMessages.getString("AbstractFilterDialog.0"), name), false, this, new Action[] { //$NON-NLS-1$
				acceptAction, clearAction, cancelAction }, cancelAction);

		initiliazeSearch(e);

		dialog.showDialog();
		dialog.dispose();

		return resultOption;
	}

	private final class AcceptAction extends AbstractAcceptAction {

		private static final long serialVersionUID = 7204804025668857817L;

		public void actionPerformed(ActionEvent e) {
			resultOption = OK_OPTION;
			dialog.closeDialog();
		}
	}

	private final class ClearAction extends AbstractClearAction {

		private static final long serialVersionUID = 7204804025668857817L;

		public void actionPerformed(ActionEvent e) {
			resultOption = RESET_OPTION;
			dialog.closeDialog();
		}
	}

	private final class CancelAction extends AbstractCancelAction {

		private static final long serialVersionUID = 8189207877250546387L;

		public void actionPerformed(ActionEvent e) {
			resultOption = CANCEL_OPTION;
			dialog.closeDialog();
		}
	}
}
