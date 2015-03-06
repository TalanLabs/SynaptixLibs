package com.synaptix.widget.view.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.message.SimpleValidationMessage;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.util.ValidationUtils;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.synaptix.swing.IconFeedbackPanel;
import com.synaptix.swing.JDialogModel;
import com.synaptix.widget.actions.view.swing.AbstractAcceptAction;
import com.synaptix.widget.actions.view.swing.AbstractCancelAction;
import com.synaptix.widget.util.StaticWidgetHelper;

public class AddEditLibelleDialog extends JPanel {

	private static final long serialVersionUID = -5968015818775564189L;

	public static final int ACCEPT_OPTION = 0;

	private static final int CANCEL_OPTION = 1;

	private JDialogModel dialog;

	private JLabel textLabel;

	private JTextField libelleField;

	private Action acceptAction;

	private Action cancelAction;

	private int returnValue;

	private int libelleLengthMax;

	private JComponent keyTypedResultView;

	private ValidationResultModel validationResultModel;

	private JTextArea infoArea;

	private JPanel infoAreaPane;

	public AddEditLibelleDialog() {
		super(new BorderLayout());

		libelleLengthMax = 140;

		initActions();
		initComponents();
		initComponentAnnotations();
		initEventHandling();

		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initActions() {
		acceptAction = new AcceptAction();
		cancelAction = new CancelAction();
	}

	private void createComponents() {
		textLabel = new JLabel();
		libelleField = new JTextField();

		validationResultModel = new DefaultValidationResultModel();
		keyTypedResultView = ValidationResultViewFactory.createReportIconAndTextLabel(validationResultModel);
	}

	private void initComponents() {
		createComponents();

		libelleField.getDocument().addDocumentListener(new LibelleDocumentListener());
		libelleField.addActionListener(new LibelleActionListener());
	}

	private void initComponentAnnotations() {
		addValidationComponentUtils(libelleField, "LIBELLE", //$NON-NLS-1$
				StaticWidgetHelper.getSynaptixWidgetConstantsBundle().enterAMeaning());
	}

	private void addValidationComponentUtils(JComponent component, Object value, String text) {
		ValidationComponentUtils.setMandatory(component, true);
		ValidationComponentUtils.setInputHint(component, text);
		ValidationComponentUtils.setMessageKey(component, value);
	}

	private void initEventHandling() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(new FocusChangeHandler());

		validationResultModel.addPropertyChangeListener(ValidationResultModel.PROPERTYNAME_RESULT, new ValidationChangeHandler());
	}

	private JPanel buildContents() {
		FormLayout layout = new FormLayout("fill:default:grow", //$NON-NLS-1$
				"max(14dlu;pref), 6dlu, pref, 3dlu, fill:pref:grow"); //$NON-NLS-1$

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		CellConstraints cc = new CellConstraints();
		builder.add(buildInfoAreaPane(), cc.xy(1, 1));
		builder.addSeparator(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().information(), cc.xy(1, 3));
		builder.add(buildEditorPanel(), cc.xy(1, 5));
		return builder.getPanel();
	}

	private JPanel buildInfoAreaPane() {
		JLabel infoLabel = new JLabel(ValidationResultViewFactory.getInfoIcon());
		infoArea = new JTextArea(1, 38);
		infoArea.setEditable(false);
		infoArea.setOpaque(false);

		FormLayout layout = new FormLayout("pref, 2dlu, default", "pref"); //$NON-NLS-1$ //$NON-NLS-2$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.add(infoLabel, cc.xy(1, 1));
		builder.add(infoArea, cc.xy(3, 1));

		infoAreaPane = builder.getPanel();
		infoAreaPane.setVisible(false);
		return infoAreaPane;
	}

	private JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("RIGHT:MAX(40DLU;DEFAULT):NONE,FILL:4DLU:NONE,FILL:200DLU:NONE,FILL:DEFAULT:GROW(1.0)", //$NON-NLS-1$
				"CENTER:DEFAULT:NONE,CENTER:3DLU:NONE,CENTER:DEFAULT:NONE,CENTER:3DLU:NONE,FILL:MAX(22DLU;PREF):NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.addLabel(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().meaning(), cc.xy(1, 3));
		builder.add(textLabel, cc.xyw(3, 1, 2));
		builder.add(libelleField, cc.xyw(3, 3, 2));
		builder.add(keyTypedResultView, cc.xyw(1, 5, 4));
		return new IconFeedbackPanel(validationResultModel, builder.getPanel());
	}

	private void setEnabledAll(boolean enabled) {
		libelleField.setEditable(enabled);
	}

	public int showDialog(String title, String subtitle, String libelle, int commentaireLengthMax, boolean enabled) {
		returnValue = CANCEL_OPTION;

		this.libelleLengthMax = commentaireLengthMax;
		this.textLabel.setText(subtitle);
		this.libelleField.setText(libelle);

		setEnabledAll(enabled);
		if (enabled) {
			dialog = new JDialogModel(null, title, subtitle, this, new Action[] { acceptAction, cancelAction }, cancelAction);
		} else {
			dialog = new JDialogModel(null, title, subtitle, this, new Action[] { cancelAction }, cancelAction);
		}

		updateValidator();

		dialog.setResizable(true);

		dialog.showDialog();
		dialog.dispose();

		return returnValue;
	}

	public String getLibelle() {
		return libelleField.getText();
	}

	private void updateComponentTreeMandatoryAndSeverity(ValidationResult result) {
		acceptAction.setEnabled(result.isEmpty());
	}

	private void updateValidator() {
		ValidationResult result = new ValidationResult();

		if (ValidationUtils.isEmpty(libelleField.getText())) {
			result.add(new SimpleValidationMessage(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().mandatoryField(), Severity.ERROR, "LIBELLE")); //$NON-NLS-1$
		} else if (!ValidationUtils.hasMaximumLength(libelleField.getText(), libelleLengthMax)) {
			result.add(new SimpleValidationMessage(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().maximumLengthCharacters(libelleLengthMax), Severity.ERROR, "LIBELLE")); //$NON-NLS-1$
		}

		validationResultModel.setResult(result);
	}

	private final class LibelleActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (acceptAction.isEnabled()) {
				acceptAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "")); //$NON-NLS-1$
			}
		}
	}

	private final class LibelleDocumentListener implements DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent e) {
			updateValidator();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			updateValidator();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			updateValidator();
		}
	}

	private final class AcceptAction extends AbstractAcceptAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			returnValue = ACCEPT_OPTION;
			dialog.closeDialog();
		}
	}

	private final class CancelAction extends AbstractCancelAction {

		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			returnValue = CANCEL_OPTION;
			dialog.closeDialog();
		}
	}

	private final class FocusChangeHandler implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			String propertyName = evt.getPropertyName();
			if (!"permanentFocusOwner".equals(propertyName)) { //$NON-NLS-1$
				return;
			}

			Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

			String focusHint = (focusOwner instanceof JComponent) ? (String) ValidationComponentUtils.getInputHint((JComponent) focusOwner) : null;

			infoArea.setText(focusHint);
			infoAreaPane.setVisible(focusHint != null);
		}
	}

	private final class ValidationChangeHandler implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			updateComponentTreeMandatoryAndSeverity((ValidationResult) evt.getNewValue());
		}
	}
}