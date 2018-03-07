package com.synaptix.widget.view.swing.wizard;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.synaptix.swing.IconFeedbackComponentValidationResultPanel;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.tooltip.ToolTipFeedbackComponentValidationResultFocusListener;
import com.synaptix.swing.wizard.WizardPage;
import com.synaptix.swing.wizard.action.WizardAction;
import com.synaptix.widget.view.swing.IValidationView;

public abstract class AbstractWizardPage<E> extends WaitComponentFeedbackPanel implements WizardPage<E>, IValidationView {

	private static final long serialVersionUID = 6791107352244877741L;

	private final String id;

	private final String title;

	private JComponent keyTypedResultView;

	private ValidationResultModel validationResultModel;

	protected ToolTipFeedbackComponentValidationResultFocusListener toolTipFeedbackComponentValidationResultFocusListener;

	public AbstractWizardPage(String id, String title) {
		super();

		this.id = id;
		this.title = title;
	}

	@Override
	public final String getId() {
		return id;
	}

	@Override
	public final String getTitle() {
		return title;
	}

	/**
	 * Call first
	 */
	protected final void initialize() {
		validationResultModel = new DefaultValidationResultModel();
		keyTypedResultView = ValidationResultViewFactory.createReportIconAndTextLabel(validationResultModel);
		if(hasValidationOnFocus()) {
			toolTipFeedbackComponentValidationResultFocusListener = new ToolTipFeedbackComponentValidationResultFocusListener(validationResultModel);
		}

		initComponents();

		this.addContent(buildContents());
	}

	protected abstract void initComponents();

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

	public Icon getIcon() {
		return null;
	}

	public Component getView() {
		return this;
	}

	@Override
	public String getDescription() {
		return null;
	}

	public WizardAction<E> getHelpAction() {
		return null;
	}

	protected void updateValidation(ValidationResult result) {
	}

	protected void fireUpdateValidator(ValidationResult result) {
		validationResultModel.setResult(result);
	}

	protected final ValidationResultModel getValidationResultModel() {
		return validationResultModel;
	}

	@Override
	public void updateValidation() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				ValidationResult result = new ValidationResult();
				updateValidation(result);
				fireUpdateValidator(result);
			}
		});
	}

	public boolean hasValidationOnFocus() {
		return true;
	}
}
