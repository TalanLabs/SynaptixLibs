package com.synaptix.widget.view.swing;

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
import com.synaptix.swing.IconFeedbackComponentValidationResultPanel;
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.swing.tooltip.ToolTipFeedbackComponentValidationResultFocusListener;
import com.synaptix.widget.actions.view.swing.AbstractAcceptAction;
import com.synaptix.widget.actions.view.swing.AbstractCancelAction;
import com.synaptix.widget.actions.view.swing.AbstractCloseAction;

/**
 * A simple dialog designed to work with a controller.
 * 
 * @author Nicolas P
 * 
 */
public abstract class AbstractSimpleDialogWithController extends WaitComponentFeedbackPanel implements IValidationView {

	private static final long serialVersionUID = 8542499211229848574L;

	public static final int ACCEPT_OPTION = 0;

	public static final int CANCEL_OPTION = 1;

	public static final int CLOSE_OPTION = 2;

	private JDialogModel dialog;

	private JComponent keyTypedResultView;

	private ValidationResultModel validationResultModel;

	protected Action acceptAction;

	protected Action cancelAction;

	protected Action closeAction;

	private int returnValue;

	protected ToolTipFeedbackComponentValidationResultFocusListener toolTipFeedbackComponentValidationResultFocusListener;

	public AbstractSimpleDialogWithController() {
		super();
		initActions();

		validationResultModel = new DefaultValidationResultModel();
		keyTypedResultView = ValidationResultViewFactory.createReportIconAndTextLabel(validationResultModel);
		if(hasValidationOnFocus()) {
			toolTipFeedbackComponentValidationResultFocusListener = new ToolTipFeedbackComponentValidationResultFocusListener(validationResultModel);
		}

		initialize();

		dialog = null;
		this.addContent(buildContents());
	}

	private void initActions() {
		acceptAction = new AcceptAction();
		acceptAction.setEnabled(false);
		cancelAction = new CancelAction();
		closeAction = new CloseAction();
	}

	protected abstract void initialize();

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

	protected abstract JComponent buildEditorPanel();

	protected int showDialog(Component parent, String title, String subTitle) {
		return showDialog(parent, title, subTitle, false);
	}

	protected int showDialog(Component parent, String title, String subTitle, boolean readOnly) {
		if (readOnly) {
			dialog = new JDialogModel(parent, title, subTitle, this, new Action[] { acceptAction, cancelAction }, new OpenActionListener(), cancelAction);
		} else {
			dialog = new JDialogModel(parent, title, subTitle, this, new Action[] { acceptAction, cancelAction }, new OpenActionListener(), cancelAction);
		}

		dialog.setResizable(true);

		dialog.showDialog();
		dialog.dispose();

		return returnValue;
	}

	@Override
	public final void updateValidation() {
		ValidationResult result = new ValidationResult();

		updateValidation(result);

		validationResultModel.setResult(result);
		acceptAction.setEnabled(result.isEmpty());
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
