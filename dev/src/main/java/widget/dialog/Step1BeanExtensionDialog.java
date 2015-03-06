package widget.dialog;

import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTextField;

import toto.IPerson;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.ValidationUtils;
import com.synaptix.swing.widget.JSyTextField;
import com.synaptix.widget.view.swing.ValidationListener;
import com.synaptix.widget.view.swing.dialog.AbstractBeanExtensionDialog;

public class Step1BeanExtensionDialog extends AbstractBeanExtensionDialog<IPerson> {

	private static final long serialVersionUID = -3446165388669489384L;

	private JTextField nameTextField;

	private JTextField surnameTextField;

	public Step1BeanExtensionDialog() {
		super("Votre identité");

		initialize();
	}

	@Override
	protected void initComponents() {
		nameTextField = new JSyTextField(50);
		surnameTextField = new JSyTextField(50);

		ValidationListener vl = new ValidationListener(this);
		nameTextField.getDocument().addDocumentListener(vl);
	}

	@Override
	protected JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("right:max(pref;40dlu),4DLU,FILL:150DLU,DEFAULT:GROW(1.0)");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.appendSeparator("Identity");
		builder.append("Name", nameTextField);
		builder.append("Surname", surnameTextField);
		return builder.getPanel();
	}

	@Override
	public void openDialog() {
		nameTextField.setText(bean.getName());
		surnameTextField.setText(bean.getSurname());

		updateValidation();
	}

	@Override
	public void commit(IPerson bean, Map<String, Object> valueMap) {
		bean.setName(nameTextField.getText());
		bean.setSurname(surnameTextField.getText());
	}

	@Override
	protected void updateValidation(ValidationResult result) {
		super.updateValidation(result);

		if (ValidationUtils.isBlank(nameTextField.getText())) {
			result.add(new SimpleValidationMessage("Obligatoire", Severity.ERROR, nameTextField));
		}
	}
}