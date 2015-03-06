package com.synaptix.widget.view.swing.extension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.synaptix.swing.IconFeedbackComponentValidationResultPanel;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.widget.view.swing.IValidationView;

public abstract class AbstractBeanExtensionDialog<E> extends WaitComponentFeedbackPanel implements IBeanExtensionDialog<E>, IValidationView {

	private static final long serialVersionUID = -886467348187872742L;

	private JComponent keyTypedResultView;

	private ValidationResultModel validationResultModel;

	public AbstractBeanExtensionDialog() {
		super();

		initComponents();
		initialize();

		this.addContent(buildContents());
	}

	protected abstract void initialize();

	private void createComponents() {
		validationResultModel = new DefaultValidationResultModel();
		keyTypedResultView = ValidationResultViewFactory.createReportIconAndTextLabel(validationResultModel);
	}

	private void initComponents() {
		createComponents();
	}

	private JPanel buildContents() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", //$NON-NLS-1$
				"FILL:PREF:GROW(1.0),CENTER:3DLU:NONE,FILL:MAX(22DLU;PREF):NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.add(new IconFeedbackComponentValidationResultPanel(validationResultModel, buildEditorPanel()), cc.xy(1, 1));
		builder.add(keyTypedResultView, cc.xy(1, 3));
		return builder.getPanel();
	}

	protected abstract JComponent buildEditorPanel();

	@Override
	public Icon getIcon() {
		return null;
	}

	@Override
	public JComponent getView() {
		return this;
	}

	protected void fireUpdateValidator(ValidationResult result) {
		validationResultModel.setResult(result);
		for (ValidatorListener l : listenerList.getListeners(ValidatorListener.class)) {
			l.updateValidator(this, getId(), result);
		}
	}

	@Override
	public void addValidatorListener(ValidatorListener l) {
		listenerList.add(ValidatorListener.class, l);
	}

	@Override
	public void removeValidatorListener(ValidatorListener l) {
		listenerList.remove(ValidatorListener.class, l);
	}

	@Override
	public void updateValidation() {
		ValidationResult result = new ValidationResult();
		updateValidation(result);
		fireUpdateValidator(result);
	}

	protected abstract void updateValidation(ValidationResult result);

	@Override
	public void closeDialog() {
	}
}
