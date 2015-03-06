package com.synaptix.deployer.client.view.swing.deployerManagement;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.synaptix.deployer.client.controller.DeployerManagementController;
import com.synaptix.deployer.client.deployerManagement.BuildNode;
import com.synaptix.deployer.client.util.StaticHelper;
import com.synaptix.deployer.job.ISynaptixJob;
import com.synaptix.deployer.model.IMailConfig;
import com.synaptix.widget.view.swing.IValidationView;
import com.synaptix.widget.view.swing.ValidationListener;

public class BuildPanel extends AbstractManagementPanel<BuildNode> implements IValidationView {

	private static final long serialVersionUID = -604848509763947228L;

	private ValidationResultModel validationResultModel;

	private JLabel consoleFullLabel;

	private JTextField buildTextField;

	private ValidationListener validationListener;

	private ButtonGroup radioGroup;

	private JButton goButton;

	private ISynaptixJob selectedJob;

	private JLabel sendMailLabel;

	private JCheckBox sendMailCheckbox;

	// private JComponent keyTypedResultView;

	// private ToolTipFeedbackComponentValidationResultFocusListener toolTipFeedbackComponentValidationResultFocusListener;

	public BuildPanel(BuildNode node, DeployerManagementController managementController) {
		super(node, managementController);
	}

	@Override
	protected void initComponents() {
		validationResultModel = new DefaultValidationResultModel();
		// keyTypedResultView = ValidationResultViewFactory.createReportIconAndTextLabel(validationResultModel);
		// toolTipFeedbackComponentValidationResultFocusListener = new ToolTipFeedbackComponentValidationResultFocusListener(validationResultModel);

		this.validationListener = new ValidationListener(this);

		goButton = new JButton(StaticHelper.getDeployerManagementConstantsBundle().run());
		goButton.setEnabled(false);
		goButton.addActionListener(new AbstractAction() {

			private static final long serialVersionUID = -6145415002787644937L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (selectedJob != null) {
					getManagementController().launch(selectedJob, buildTextField.getText(), sendMailCheckbox.isEnabled() && sendMailCheckbox.isSelected());
				}
			}
		});

		buildTextField = new JTextField();
		consoleFullLabel = new JLabel();
		consoleFullLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		consoleFullLabel.setForeground(Color.blue);
		consoleFullLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);

				if (!validationResultModel.hasErrors()) {
					openUrl(selectedJob.getConsoleFullUrl().replaceAll("#BUILD#", buildTextField.getText()));
				}
			}
		});

		radioGroup = new ButtonGroup();

		sendMailLabel = new JLabel(new StringBuilder("<html><u>").append(StaticHelper.getDeployerManagementConstantsBundle().sendMail()).append("</u></html>").toString());
		sendMailLabel.setForeground(Color.blue);
		sendMailCheckbox = new JCheckBox("");
		sendMailCheckbox.setSelected(getManagementController().getMailConfig().isDefaultSend());
		updateMailCheckbox();
		sendMailLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				getManagementController().configMail(sendMailCheckbox.isSelected());
			}
		});

		buildTextField.getDocument().addDocumentListener(validationListener);
		buildTextField.addPropertyChangeListener("value", validationListener);
	}

	public void updateMailCheckbox() {
		IMailConfig mailConfig = getManagementController().getMailConfig();
		boolean valid = true;
		valid = valid && !StringUtils.isBlank(mailConfig.getMailSmtpHost());
		valid = valid && (mailConfig.getMailSmtpPort() != null);
		valid = valid && !StringUtils.isBlank(mailConfig.getLogin());
		valid = valid && (mailConfig.getPassword() != null);
		valid = valid && !StringUtils.isBlank(mailConfig.getSenderMail());
		valid = valid && (mailConfig.getDefaultReceivers() != null);
		sendMailCheckbox.setEnabled(valid);
	}

	// @Override
	// protected JPanel buildContent() {
	//		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", //$NON-NLS-1$
	//				"FILL:PREF:GROW(1.0),CENTER:3DLU:NONE,FILL:MAX(22DLU;PREF):NONE"); //$NON-NLS-1$
	// PanelBuilder builder = new PanelBuilder(layout);
	// builder.setDefaultDialogBorder();
	// CellConstraints cc = new CellConstraints();
	// builder.add(new IconFeedbackComponentValidationResultPanel(validationResultModel, buildEditorPanel()), cc.xy(1, 1));
	// builder.add(keyTypedResultView, cc.xy(1, 3));
	// return builder.getPanel();
	// }

	@Override
	protected JComponent buildContent() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(0.5),FILL:DEFAULT:NONE,3DLU,FILL:MAX(DEFAULT;40DLU):NONE,FILL:DEFAULT:GROW(0.5)");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);

		for (ISynaptixJob job : getManagementController().getSupportedJobs()) {
			MyRadio radio = new MyRadio(job);
			radioGroup.add(radio);
			radio.addActionListener(validationListener);

			builder.appendRow("FILL:DEFAULT:NONE");
			builder.add(radio, new CellConstraints(2, builder.getRow()));
			builder.nextRow();
		}
		builder.appendRow("FILL:DEFAULT:NONE");
		builder.add(new JLabel(StaticHelper.getDeployerManagementConstantsBundle().buildNumber()), new CellConstraints(2, builder.getRow()));

		builder.add(buildTextField, new CellConstraints(4, builder.getRow()));
		builder.nextRow();

		builder.appendRow("FILL:DEFAULT:NONE");
		builder.add(consoleFullLabel, new CellConstraints(1, builder.getRow(), 5, 1, CellConstraints.CENTER, CellConstraints.CENTER));
		builder.nextRow();

		builder.appendRow("4DLU");
		builder.nextRow();
		builder.appendRow("FILL:DEFAULT:NONE");
		builder.add(goButton, new CellConstraints(2, builder.getRow(), 3, 1));
		builder.nextRow();

		builder.appendRow("4DLU");
		builder.nextRow();
		builder.appendRow("FILL:DEFAULT:NONE");
		builder.add(sendMailCheckbox, new CellConstraints(2, builder.getRow(), 1, 1, CellConstraints.RIGHT, CellConstraints.CENTER));
		builder.add(sendMailLabel, new CellConstraints(4, builder.getRow(), 1, 1, CellConstraints.LEFT, CellConstraints.CENTER));
		return builder.getPanel();
	}

	@Override
	public void updateValidation() {
		ValidationResult result = new ValidationResult();
		if (StringUtils.isBlank(buildTextField.getText())) {
			result.add(new SimpleValidationMessage(StaticHelper.getDeployerManagementConstantsBundle().invalidNumber(), Severity.ERROR, buildTextField));
		}
		if (selectedJob == null) {
			result.add(new SimpleValidationMessage(StaticHelper.getDeployerManagementConstantsBundle().noSelectedJob(), Severity.ERROR, radioGroup));
		}
		if (result.hasErrors()) {
			consoleFullLabel.setText(null);
		} else if (selectedJob != null) {
			consoleFullLabel.setText("<html><u>" + selectedJob.getConsoleFullUrl().replaceAll("#BUILD#", buildTextField.getText()) + "</u></html>");
		}
		goButton.setEnabled(!result.hasErrors() && !getManagementController().isRunning());
		validationResultModel.setResult(result);
	}

	private void setSelectedJob(ISynaptixJob job) {
		this.selectedJob = job;
	}

	public void openUrl(String url) {
		URI uri = URI.create(url);
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				LogFactory.getLog(BuildPanel.class).error("ERROR OPENING WEB PAGE", e);
			}
		}
	}

	@Override
	public void setRunning(final boolean running) {
		super.setRunning(running);

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				goButton.setEnabled(((!validationResultModel.hasErrors()) && (!running)));
			}
		});
	}

	private final class MyRadio extends JRadioButton {

		private static final long serialVersionUID = -506079571404548720L;

		public MyRadio(final ISynaptixJob job) {
			super(job.getName());

			addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (isSelected()) {
						setSelectedJob(job);
					}
				}
			});
		}
	}
}
