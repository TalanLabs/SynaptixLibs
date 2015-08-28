package com.synaptix.widget.view.swing.dialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.synaptix.widget.view.dialog.BeanValidatorListener;
import com.synaptix.widget.view.dialog.IBeanDialogView0;
import com.synaptix.widget.view.dialog.IBeanExtensionDialogView;
import com.synaptix.widget.view.swing.IValidationView;

public abstract class AbstractBeanExtensionDialog<E> extends WaitComponentFeedbackPanel implements IBeanExtensionDialogView<E>, IValidationView {

	private static final long serialVersionUID = -886467348187872742L;

	private String title;

	private String subtitle;

	private JComponent keyTypedResultView;

	private ValidationResultModel validationResultModel;

	protected E bean;

	protected Map<String, Object> valueMap;

	protected boolean readOnly;

	private List<BeanValidatorListener<E>> validationListeners = new ArrayList<BeanValidatorListener<E>>();

	protected ToolTipFeedbackComponentValidationResultFocusListener toolTipFeedbackComponentValidationResultFocusListener;

	private boolean creation;

	private IBeanDialogView0<E> beanDialog;

	public AbstractBeanExtensionDialog(String title) {
		this(title, null);
	}

	public AbstractBeanExtensionDialog(String title, String subtitle) {
		super();

		this.title = title;
		this.subtitle = subtitle;
	}

	/**
	 * Call first
	 */
	protected final void initialize() {
		validationResultModel = new DefaultValidationResultModel();
		keyTypedResultView = ValidationResultViewFactory.createReportIconAndTextLabel(validationResultModel);
		toolTipFeedbackComponentValidationResultFocusListener = new ToolTipFeedbackComponentValidationResultFocusListener(validationResultModel);

		initComponents();

		this.addContent(buildContents());
	}

	protected abstract void initComponents();

	protected final ValidationResultModel getValidationResultModel() {
		return validationResultModel;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getSubtitle() {
		return subtitle;
	}

	protected JPanel buildContents() {
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

	protected void fireUpdateValidator(ValidationResult result) {
		validationResultModel.setResult(result);
		for (BeanValidatorListener<E> l : validationListeners) {
			l.updateValidator(this, result);
		}
	}

	@Override
	public void addValidatorListener(BeanValidatorListener<E> l) {
		validationListeners.add(l);
	}

	@Override
	public void removeValidatorListener(BeanValidatorListener<E> l) {
		validationListeners.remove(l);
	}

	@Override
	public final void updateValidation() {
		// always use a new thread, don't use the EDT if we're already in it
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				ValidationResult result = new ValidationResult();
				updateValidation(result);
				fireUpdateValidator(result);
			}
		});
	}

	protected void updateValidation(ValidationResult result) {
	}

	/**
	 * Set bean for dialog
	 *
	 * Call first before openDialog
	 *
	 * @param bean
	 * @param extensionMap
	 */
	@Override
	public void setBean(E bean, Map<String, Object> valueMap, boolean readOnly, boolean creation) {
		this.bean = bean;
		this.valueMap = valueMap;
		this.readOnly = readOnly;
		this.creation = creation;
	}

	@Override
	public abstract void commit(E bean, Map<String, Object> valueMap);

	@Override
	public void openDialog() {
	}

	@Override
	public boolean closeDialog() {
		return true;
	}

	protected final boolean isCreation() {
		return creation;
	}

	@Override
	public final void setBeanDialog(IBeanDialogView0<E> beanDialog) {
		this.beanDialog = beanDialog;
	}

	protected final IBeanDialogView0<E> getBeanDialog() {
		return beanDialog;
	}
}
