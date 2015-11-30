package widget.dialog;

import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTextField;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.ValidationUtils;
import com.synaptix.swing.widget.JSyTextField;
import com.synaptix.widget.view.swing.ValidationListener;
import com.synaptix.widget.view.swing.dialog.AbstractBeanExtensionDialog;

import toto.IPerson;

public class Step3BeanExtensionDialog extends AbstractBeanExtensionDialog<IPerson> {

	private static final long serialVersionUID = -3446165388669489384L;

	private JTextField zipTextField;

	private JTextField cityTextField;

	private JTextField countryTextField;

	public Step3BeanExtensionDialog() {
		super("Votre lieu de naissance");

		initialize();
	}

	@Override
	protected void initComponents() {
		zipTextField = new JSyTextField(50);
		cityTextField = new JSyTextField(50);
		countryTextField = new JSyTextField(50);

		ValidationListener vl = new ValidationListener(this);
		countryTextField.getDocument().addDocumentListener(vl);
		zipTextField.getDocument().addDocumentListener(vl);
	}

	@Override
	protected JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("right:max(pref;40dlu),4DLU,FILL:150DLU,DEFAULT:GROW(1.0)");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.appendSeparator("Lieu");
		builder.append("Zip", zipTextField);
		builder.append("City", cityTextField);
		builder.append("Country", countryTextField);
		return builder.getPanel();
	}

	@Override
	public void openDialog() {
		zipTextField.setText(bean.getZip());
		cityTextField.setText(bean.getCity());
		countryTextField.setText(bean.getCountry());

		updateValidation();
	}

	@Override
	public void commit(IPerson bean, Map<String, Object> valueMap) {
		bean.setCity(cityTextField.getText());
		bean.setZip(zipTextField.getText());
		bean.setCountry(countryTextField.getText());
	}

	@Override
	protected void updateValidation(ValidationResult result) {
		super.updateValidation(result);

		if (ValidationUtils.isBlank(zipTextField.getText())) {
			result.add(new SimpleValidationMessage("Obligatoire", Severity.ERROR, zipTextField));
		}
		if (ValidationUtils.isBlank(countryTextField.getText())) {
			result.add(new SimpleValidationMessage("Obligatoire", Severity.ERROR, countryTextField));
		}
	}
}
