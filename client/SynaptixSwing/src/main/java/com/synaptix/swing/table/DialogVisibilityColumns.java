package com.synaptix.swing.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.synaptix.swing.IconFeedbackComponentValidationResultPanel;
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.JListToList;
import com.synaptix.swing.JSyTable;
import com.synaptix.swing.SwingMessages;
import com.synaptix.swing.tooltip.ToolTipFeedbackComponentValidationResultFocusListener;

public class DialogVisibilityColumns extends JPanel {

	private static final long serialVersionUID = -5213595834965784882L;

	private static final String TEXT_TITLE = SwingMessages.getString("DialogVisibilityColumns.0"); //$NON-NLS-1$

	private Action closeAction;

	private JComponent keyTypedResultView;

	private ValidationResultModel validationResultModel;

	protected ToolTipFeedbackComponentValidationResultFocusListener toolTipFeedbackComponentValidationResultFocusListener;

	private JDialogModel dialog;

	private JListToList<SyTableColumn> listToList;

	private JSyTable table;

	private TableModel model;

	private List<SyTableColumn> allColumnList;

	public DialogVisibilityColumns(JSyTable table) {
		super(new BorderLayout());

		this.table = table;
		initComponents();

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initComponents() {
		validationResultModel = new DefaultValidationResultModel();
		keyTypedResultView = ValidationResultViewFactory.createReportIconAndTextLabel(validationResultModel);

		if (hasValidationOnFocus()) {
			toolTipFeedbackComponentValidationResultFocusListener = new ToolTipFeedbackComponentValidationResultFocusListener(validationResultModel);
		}

		closeAction = new CloseAction();
		listToList = new JListToList<SyTableColumn>();

		listToList.getAllList().setCellRenderer(new SyTableColumnCellRenderer());
		listToList.getSelectionList().setCellRenderer(new SyTableColumnCellRenderer());
		listToList.setComparator(new SyTableColumnModelComparator());

		SyTableColumnModel tcm = (SyTableColumnModel) table.getColumnModel();
		model = table.getModel();

		allColumnList = new ArrayList<SyTableColumn>();
		List<SyTableColumn> selectedColumnList = new ArrayList<SyTableColumn>();

		for (TableColumn c : tcm.getColumns(true, false)) {
			final SyTableColumn tc = (SyTableColumn) c;

			allColumnList.add(tc);
			if (tc.isVisible()) {
				selectedColumnList.add(tc);
			}
		}

		listToList.setAllValues(allColumnList.toArray());
		listToList.setSelectionValues(selectedColumnList.toArray());

		listToList.addChangeListener(new MySyTableColumnListSelectionModel());
	}

	protected JPanel buildContents() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", //$NON-NLS-1$
				"FILL:PREF:GROW(1.0),CENTER:3DLU:NONE,FILL:MAX(22DLU;PREF):NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.add(new IconFeedbackComponentValidationResultPanel(validationResultModel, listToList), cc.xy(1, 1));
		builder.add(keyTypedResultView, cc.xy(1, 3));
		return builder.getPanel();
	}

	public void showDialog(Component parent) {
		dialog = new JDialogModel(parent, TEXT_TITLE, this, new Action[] { closeAction }, closeAction);
		dialog.showDialog();
		dialog.dispose();
	}

	private final void updateValidation() {
		ValidationResult result = new ValidationResult();

		if (listToList.getSelectionValues().length == 0) {
			result.add(new SimpleValidationMessage(SwingMessages.getString("DialogVisibilityColumns.2"), Severity.ERROR, listToList)); //$NON-NLS-1$
		}

		validationResultModel.setResult(result);
		closeAction.setEnabled(!result.hasErrors());
	}

	private class CloseAction extends AbstractAction {

		private static final long serialVersionUID = 7204804025668857817L;

		public CloseAction() {
			super(SwingMessages.getString("DialogVisibilityColumns.1")); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			SyTableColumnModel ytcm = (SyTableColumnModel) table.getColumnModel();

			if (listToList.getSelectionValues() != null && listToList.getSelectionValues().length > 0) {
				for (SyTableColumn tc : allColumnList) {
					ytcm.invisibleColumn(tc);
				}

				for (Object o : listToList.getSelectionValues()) {
					SyTableColumn tc = (SyTableColumn) o;
					ytcm.visibleColumn(tc);
				}
			}

			dialog.closeDialog();
		}
	}

	private final class SyTableColumnCellRenderer extends DefaultListCellRenderer {

		private static final long serialVersionUID = -6305106130742823909L;

		public SyTableColumnCellRenderer() {
			super();
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component res = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			setValue(value);
			return res;
		}

		protected void setValue(Object value) {
			SyTableColumn t = (SyTableColumn) value;
			this.setText(model.getColumnName(t.getModelIndex()));
		}
	}

	private final class SyTableColumnModelComparator implements Comparator<SyTableColumn> {

		@Override
		public int compare(SyTableColumn o1, SyTableColumn o2) {
			return model.getColumnName(o1.getModelIndex()).compareTo(model.getColumnName(o2.getModelIndex()));
		}
	}

	private final class MySyTableColumnListSelectionModel implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			updateValidation();
		}
	}

	public boolean hasValidationOnFocus() {
		return true;
	}
}
