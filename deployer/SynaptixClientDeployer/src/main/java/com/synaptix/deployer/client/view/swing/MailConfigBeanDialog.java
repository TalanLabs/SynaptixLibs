package com.synaptix.deployer.client.view.swing;

import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.ValidationResult;
import com.synaptix.deployer.client.util.StaticHelper;
import com.synaptix.deployer.model.IMailConfig;
import com.synaptix.swing.utils.SwingComponentFactory;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;
import com.synaptix.widget.view.swing.ValidationListener;
import com.synaptix.widget.view.swing.dialog.AbstractBeanExtensionDialog;

public class MailConfigBeanDialog extends AbstractBeanExtensionDialog<IMailConfig> implements IBeanExtensionDialogView<IMailConfig> {

	private static final long serialVersionUID = -2200987023568666169L;

	private JTextField smtpHostField;

	private JFormattedTextField smtpPortField;

	private JTextField loginField;

	private JPasswordField passwordField;

	private JTextField senderMailField;

	private JTextField receiversField;

	private JCheckBox sslCheckbox;

	public MailConfigBeanDialog() {
		super(StaticHelper.getDeployerManagementConstantsBundle().mail());

		initialize();
	}

	@Override
	protected void initComponents() {
		ValidationListener validationListener = new ValidationListener(this);

		smtpHostField = new JTextField();
		smtpPortField = SwingComponentFactory.createIntegerField();
		loginField = new JTextField();
		passwordField = new JPasswordField();
		senderMailField = new JTextField();
		receiversField = new JTextField();

		sslCheckbox = new JCheckBox();

		smtpHostField.getDocument().addDocumentListener(validationListener);
		smtpPortField.getDocument().addDocumentListener(validationListener);
		loginField.getDocument().addDocumentListener(validationListener);
		passwordField.getDocument().addDocumentListener(validationListener);
		senderMailField.getDocument().addDocumentListener(validationListener);
		receiversField.getDocument().addDocumentListener(validationListener);

		sslCheckbox.addActionListener(validationListener);
	}

	@Override
	protected JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:NONE,3DLU,FILL:DEFAULT:GROW(1.0)");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.append(StaticHelper.getDeployerManagementConstantsBundle().smtpHost(), smtpHostField);
		builder.append(StaticHelper.getDeployerManagementConstantsBundle().smtpPort(), smtpPortField);
		builder.append(StaticHelper.getDeployerManagementConstantsBundle().login(), loginField);
		builder.append(StaticHelper.getDeployerManagementConstantsBundle().password(), passwordField);
		builder.append(StaticHelper.getDeployerManagementConstantsBundle().sender(), senderMailField);
		builder.append(StaticHelper.getDeployerManagementConstantsBundle().receivers(), receiversField);
		builder.append(StaticHelper.getDeployerManagementConstantsBundle().ssl(), sslCheckbox);
		return builder.getPanel();
	}

	@Override
	public void openDialog() {
		smtpHostField.setText(bean.getMailSmtpHost());
		smtpPortField.setValue(bean.getMailSmtpPort());
		loginField.setText(bean.getLogin());
		if (bean.getPassword() != null) {
			passwordField.setText(new String(bean.getPassword()));
		} else {
			passwordField.setText(null);
		}
		StringBuilder receivers = new StringBuilder();
		if (bean.getDefaultReceivers() != null) {
			boolean first = true;
			for (String receiver : bean.getDefaultReceivers()) {
				if (!first) {
					receivers.append(", ");
				}
				receivers.append(receiver);
				first = false;
			}
		}
		senderMailField.setText(bean.getSenderMail());
		receiversField.setText(receivers.toString());
		sslCheckbox.setSelected(bean.isSSL());
	}

	@Override
	public void commit(IMailConfig bean, Map<String, Object> valueMap) {
		bean.setMailSmtpHost(smtpHostField.getText());
		bean.setMailSmtpPort((Integer) smtpPortField.getValue());
		bean.setLogin(loginField.getText());
		bean.setPassword(passwordField.getPassword());
		String text = receiversField.getText();
		if (StringUtils.isNotBlank(text)) {
			String[] split = text.split(",");
			bean.setDefaultReceivers(split);
			for (int i = 0; i < split.length; i++) {
				split[i] = split[i].trim();
			}
		} else {
			bean.setDefaultReceivers(null);
		}
		bean.setSenderMail(senderMailField.getText());
		bean.setSSL(sslCheckbox.isSelected());
	}

	@Override
	protected void updateValidation(ValidationResult result) {
		super.updateValidation(result);
	}
}
