package com.synaptix.swing.wizard;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.view.ValidationComponentUtils;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.synaptix.swing.IconFeedbackPanel;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.wizard.action.WizardAction;

public abstract class AbstractValidationWizardPage<E> extends
		WaitComponentFeedbackPanel implements WizardPage<E> {

	private static final long serialVersionUID = 6791107352244877741L;

	private JComponent keyTypedResultView;

	private ValidationResultModel validationResultModel;

	private JTextArea infoArea;

	private JPanel infoAreaPane;

	public AbstractValidationWizardPage() {
		super();

		initComponents();
		initEventHandling();
		initialize();

		this.addContent(buildContents());
	}

	protected abstract void initialize();

	private void createComponents() {
		validationResultModel = new DefaultValidationResultModel();
		keyTypedResultView = ValidationResultViewFactory
				.createReportIconAndTextLabel(validationResultModel);
	}

	private void initComponents() {
		createComponents();
	}

	protected void addValidationComponentUtils(JComponent component, String text) {
		ValidationComponentUtils.setMandatory(component, true);
		ValidationComponentUtils.setInputHint(component, text);
		ValidationComponentUtils.setMessageKey(component, component);
	}

	private void initEventHandling() {
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addPropertyChangeListener(new FocusChangeHandler());
	}

	private JPanel buildContents() {
		FormLayout layout = new FormLayout(
				"FILL:DEFAULT:GROW(1.0)", //$NON-NLS-1$
				"MAX(14DLU;PREF):NONE, CENTER:6DLU:NONE, CENTER:DEFAULT:NONE, CENTER:3DLU:NONE, FILL:PREF:GROW(1.0),CENTER:3DLU:NONE,FILL:MAX(22DLU;PREF):NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.add(buildInfoAreaPane(), cc.xy(1, 1));
		builder.addSeparator("Informations", cc.xy(1, 3));
		builder.add(new IconFeedbackPanel(validationResultModel,
				buildEditorPanel()), cc.xy(1, 5));
		builder.add(keyTypedResultView, cc.xy(1, 7));
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

	protected abstract JComponent buildEditorPanel();

	public Icon getIcon() {
		return null;
	}

	public Component getView() {
		return this;
	}

	public WizardAction<E> getHelpAction() {
		return null;
	}

	protected void fireUpdateValidator(ValidationResult result) {
		validationResultModel.setResult(result);
	}

	private final class FocusChangeHandler implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			String propertyName = evt.getPropertyName();
			if (!"permanentFocusOwner".equals(propertyName)) { //$NON-NLS-1$
				return;
			}

			Component focusOwner = KeyboardFocusManager
					.getCurrentKeyboardFocusManager().getFocusOwner();

			String focusHint = (focusOwner instanceof JComponent) ? (String) ValidationComponentUtils
					.getInputHint((JComponent) focusOwner)
					: null;

			infoArea.setText(focusHint);
			infoAreaPane.setVisible(focusHint != null);
		}
	}
}
