package com.synaptix.client.common.swing.view.dialog;

import java.util.List;

import javax.swing.JComponent;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.ValidationResult;
import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.component.helper.ComponentHelper;
import com.synaptix.swing.JSyScrollPane;
import com.synaptix.swing.JSyTable;
import com.synaptix.taskmanager.controller.helper.INlsMessageData;
import com.synaptix.taskmanager.controller.helper.NlsMessageDataFields;
import com.synaptix.taskmanager.view.swing.renderer.SerializableTableCellRenderer;
import com.synaptix.widget.view.swing.dialog.AbstractSimpleDialog2;
import com.synaptix.widget.view.swing.tablemodel.DefaultComponentTableModel;


public class ShowImportNlsMessageDatasDialog extends AbstractSimpleDialog2 {

	private static final long serialVersionUID = -2300764781388001970L;

	private static final String[] TABLE_COLUMNS = ComponentHelper.PropertyArrayBuilder.build(NlsMessageDataFields.objectType(), NlsMessageDataFields.columnName(), NlsMessageDataFields.idObject(),
			NlsMessageDataFields.myMeaning(), NlsMessageDataFields.meaning());

	private final List<INlsMessageData> nlsClientMessages;

	private DefaultComponentTableModel<INlsMessageData> tableModel;

	private JSyTable table;

	public ShowImportNlsMessageDatasDialog(List<INlsMessageData> nlsClientMessages) {
		super();

		this.nlsClientMessages = nlsClientMessages;

		initComponents();

		initialize();
	}

	private void initComponents() {
		tableModel = new DefaultComponentTableModel<INlsMessageData>(INlsMessageData.class, StaticCommonHelper.getNlsMessageDataTableConstantsBundle(), TABLE_COLUMNS);
		table = new JSyTable(tableModel, ShowImportNlsMessageDatasDialog.class.getName());
		table.getYColumnModel().getColumn(tableModel.findColumnIndexById(NlsMessageDataFields.idObject().name()), true).setCellRenderer(new SerializableTableCellRenderer());

		table.setShowTableLines(true);
	}

	@Override
	protected JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("fill:default:grow", //$NON-NLS-1$
				"max(14dlu;pref), 6dlu, fill:pref:grow"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.addLabel(StaticCommonHelper.getCommonConstantsBundle().youAreAboutToImportTheseTranslationsWouldYouContinue(), cc.xy(1, 1));
		builder.add(new JSyScrollPane(table), cc.xy(1, 3));
		return builder.getPanel();
	}

	@Override
	protected void openDialog() {
		tableModel.setComponents(nlsClientMessages);
		updateValidation();
	}

	@Override
	protected void updateValidation(ValidationResult result) {
	}
}
