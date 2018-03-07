package com.synaptix.widget.view.swing.dialog;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JComponent;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.ValidationResultModel;
import com.jgoodies.validation.util.DefaultValidationResultModel;
import com.jgoodies.validation.view.ValidationResultViewFactory;
import com.synaptix.client.view.IView;
import com.synaptix.swing.IconFeedbackComponentValidationResultPanel;
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.tooltip.ToolTipFeedbackComponentValidationResultFocusListener;
import com.synaptix.swing.utils.GUIWindow;
import com.synaptix.widget.actions.view.swing.AbstractAcceptAction;
import com.synaptix.widget.actions.view.swing.AbstractCancelAction;
import com.synaptix.widget.actions.view.swing.AbstractCloseAction;
import com.synaptix.widget.view.dialog.ISimpleDialogView;
import com.synaptix.widget.view.swing.IValidationView;

/**
 * A simple dialog.
 * <p>
 * To build the dialog, <code>AbstractSimpleDialog2()</code> and <code>initialize()</code> must be called. <code>initialize()</code> should be called after initialization of the components in the
 * dialog. Then, call the <code>showDialog()</code> to display the dialog.
 *
 * @author E407780
 *
 */
public abstract class AbstractSimpleDialog2 extends WaitComponentFeedbackPanel implements IValidationView, ISimpleDialogView {

	private static final long serialVersionUID = 8542499211229848574L;

	private JDialogModel dialog;

	private JComponent keyTypedResultView;

	private ValidationResultModel validationResultModel;

	protected Action acceptAction;

	protected Action cancelAction;

	protected Action closeAction;

	private int returnValue = CANCEL_OPTION;

	protected boolean readOnly;

	protected ToolTipFeedbackComponentValidationResultFocusListener toolTipFeedbackComponentValidationResultFocusListener;

	public AbstractSimpleDialog2() {
		super();
	}

	private void initActions() {
		acceptAction = new AcceptAction();
		acceptAction.setEnabled(false);
		cancelAction = new CancelAction();
		closeAction = new CloseAction();
	}

	/**
	 * Initializes the dialog and builds the contents.
	 * <p>
	 * As <code>buildEditorPanel()</code> is called after this method, all components of the dialog should be initialized before calling <code>initialize()</code>.
	 */
	protected void initialize() {
		initActions();

		validationResultModel = new DefaultValidationResultModel();
		keyTypedResultView = ValidationResultViewFactory.createReportIconAndTextLabel(validationResultModel);

		if(hasValidationOnFocus()) {
			toolTipFeedbackComponentValidationResultFocusListener = new ToolTipFeedbackComponentValidationResultFocusListener(validationResultModel);
		}

		dialog = null;

		readOnly = false;

		this.addContent(buildContents());
	}

	private JComponent buildContents() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:GROW(1.0)", //$NON-NLS-1$
				"FILL:PREF:GROW(1.0),CENTER:3DLU:NONE,FILL:MAX(22DLU;PREF):NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.add(buildEditorPanel(), cc.xy(1, 1));
		builder.add(keyTypedResultView, cc.xy(1, 3));
		return new IconFeedbackComponentValidationResultPanel(validationResultModel, builder.getPanel());
	}

	/**
	 * Override this function to build the components in the dialog.
	 * <p>
	 * This function is called after the <code>initialize</code> method.
	 *
	 * @return
	 */
	protected abstract JComponent buildEditorPanel();

	/**
	 * Unique Id for Dialog, save size
	 *
	 * @return
	 */
	protected String getDialogId() {
		return null;
	}

	/**
	 * Opens an editable and resizable dialog
	 *
	 * @param view
	 * @param title
	 * @param subTitle
	 * @return
	 */
	public int showDialog(IView parent, String title, String subTitle) {
		return showDialog(parent, title, subTitle, false, true);
	}

	/**
	 * Opens a resizable dialog
	 *
	 * @param parent
	 * @param title
	 * @param subTitle
	 * @param readOnly
	 * @return
	 */
	protected int showDialog(IView parent, String title, String subTitle, boolean readOnly) {
		return showDialog(parent, title, subTitle, readOnly, true);
	}

	/**
	 * Displays the dialog
	 *
	 * @param parent
	 * @param title
	 * @param subTitle
	 * @param readOnly
	 * @return <code>ACCEPT_OPTION</code> if user clicks on "OK", <code>CANCEL_OPTION</code> if user clicks on "cancel", and <code>CLOSE_OPTION</code> if user clicks on "Close" button.
	 */
	protected int showDialog(IView parent, String title, String subTitle, boolean readOnly, boolean resizable) {
		return showDialog(getComponent(parent), title, subTitle, readOnly, resizable);
	}

	private int showDialog(Component component, String title, String subTitle, boolean readOnly, boolean resizable) {
		if (readOnly) {
			dialog = new JDialogModel(component, title, subTitle, this, new Action[] { closeAction }, new OpenActionListener(), closeAction);
		} else {
			dialog = new JDialogModel(component, title, subTitle, this, new Action[] { acceptAction, cancelAction }, new OpenActionListener(), cancelAction);
		}
		dialog.setId(getDialogId());
		dialog.setResizable(resizable);

		dialog.showDialog();
		dialog.dispose();

		return returnValue;
	}

	protected JDialogModel getDialogModel() {
		return dialog;
	}

	@Override
	public final void updateValidation() {
		ValidationResult result = new ValidationResult();

		updateValidation(result);

		validationResultModel.setResult(result);
		acceptAction.setEnabled(!result.hasErrors());
	}

	private Component getComponent(IView parent) {
		if (parent instanceof Component) {
			return (Component) parent;
		}
		return GUIWindow.getActiveWindow();
	}

	protected abstract void updateValidation(ValidationResult result);

	protected abstract void openDialog();

	protected void accept() {
	}

	protected void cancel() {
	}

	protected void close() {
	}

	private final class OpenActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			openDialog();
		}
	}

	private final class AcceptAction extends AbstractAcceptAction {

		private static final long serialVersionUID = 9184100540105793420L;

		@Override
		public void actionPerformed(ActionEvent e) {
			returnValue = ACCEPT_OPTION;

			accept();

			dialog.closeDialog();
		}
	}

	private final class CancelAction extends AbstractCancelAction {

		private static final long serialVersionUID = -1165636071113981103L;

		@Override
		public void actionPerformed(ActionEvent e) {
			returnValue = CANCEL_OPTION;

			cancel();

			dialog.closeDialog();
		}
	}

	private final class CloseAction extends AbstractCloseAction {

		private static final long serialVersionUID = -1165636071113981103L;

		@Override
		public void actionPerformed(ActionEvent e) {
			returnValue = CLOSE_OPTION;

			close();

			dialog.closeDialog();
		}
	}

	public boolean hasValidationOnFocus() {
		return true;
	}
}
