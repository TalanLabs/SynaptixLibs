package com.synaptix.taskmanager.view.swing.dialog;

import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.synaptix.client.common.util.StaticCommonHelper;
import com.synaptix.swing.widget.JSyTextField;
import com.synaptix.taskmanager.model.ITodoFolder;
import com.synaptix.taskmanager.util.StaticHelper;
import com.synaptix.widget.view.swing.JTextAreaMaxLength;
import com.synaptix.widget.view.swing.ValidationListener;
import com.synaptix.widget.view.swing.dialog.AbstractBeanExtensionDialog;

public class GeneralTodoFolderBeanExtensionDialog extends AbstractBeanExtensionDialog<ITodoFolder> {

	private static final long serialVersionUID = 3099602873158720281L;

	private JSyTextField labelTextField;
	private JCheckBox restrictedCheckBox;
	private JTextAreaMaxLength commentTextArea;

	private ValidationListener validationListener;

	public GeneralTodoFolderBeanExtensionDialog() {
		super(StaticCommonHelper.getCommonConstantsBundle().general());

		initialize();
	}

	@Override
	protected void initComponents() {
		labelTextField = new JSyTextField(64);
		restrictedCheckBox = new JCheckBox(StaticHelper.getTodoFoldersTableConstantsBundle().restricted());
		commentTextArea = new JTextAreaMaxLength(512);

		validationListener = new ValidationListener(this);

		labelTextField.getDocument().addDocumentListener(validationListener);
		commentTextArea.getDocument().addDocumentListener(validationListener);
	}

	@Override
	protected JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("RIGHT:MAX(40DLU;PREF):NONE,FILL:4DLU:NONE,FILL:150DLU:GROW(1.0)", ""); //$NON-NLS-1$
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.append(StaticHelper.getTodoFoldersTableConstantsBundle().meaning(), labelTextField);
		builder.append("", restrictedCheckBox);

		builder.nextLine();
		builder.append(StaticHelper.getTodoFoldersTableConstantsBundle().comments());
		builder.appendRow("FILL:30DLU:NONE");
		builder.add(new JScrollPane(commentTextArea), new CellConstraints(3, builder.getRow(), 1, 2));
		builder.nextLine(2);
		return builder.getPanel();
	}

	@Override
	public void openDialog() {
		validationListener.setEnabled(false);

		labelTextField.setText(bean.getMeaning());
		restrictedCheckBox.setSelected(bean.isRestricted());
		commentTextArea.setText(bean.getComments());

		validationListener.setEnabled(true);

		labelTextField.setEnabled(!readOnly);
		commentTextArea.setEnabled(!readOnly);

		updateValidation();
	}

	@Override
	protected void updateValidation(ValidationResult result) {
		if (labelTextField.getText() == null) {
			result.add(new SimpleValidationMessage(StaticCommonHelper.getCommonConstantsBundle().mandatoryField(), Severity.ERROR, labelTextField));
		}
	}

	@Override
	public void commit(ITodoFolder bean, Map<String, Object> valueMap) {
		bean.setMeaning(labelTextField.getText());
		bean.setRestricted(restrictedCheckBox.isSelected());
		bean.setComments(commentTextArea.getText());
	}
}
