package com.synaptix.widget.view.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.utils.SwingComponentFactory;
import com.synaptix.widget.actions.view.swing.AbstractAcceptAction;
import com.synaptix.widget.actions.view.swing.AbstractCancelAction;
import com.synaptix.widget.util.StaticWidgetHelper;

public class AddEditCommentaireDialog extends JPanel {

	private static final long serialVersionUID = -5968015818775564189L;

	public static final int ACCEPT_OPTION = 0;

	private static final int CANCEL_OPTION = 1;

	private static final String TEXT_TITLE = StaticWidgetHelper.getSynaptixWidgetConstantsBundle().comment();

	private static final String SHOW_TEXT_SUBTITLE = StaticWidgetHelper.getSynaptixWidgetConstantsBundle().readComment();

	private static final String MODIFY_TEXT_SUBTITLE = StaticWidgetHelper.getSynaptixWidgetConstantsBundle().editComment();

	private JDialogModel dialog;

	private JLabel textLabel;

	private JTextArea commentaireArea;

	private Action acceptAction;

	private Action cancelAction;

	private int returnValue;

	private int commentaireLengthMax;

	private String textTitle;

	public AddEditCommentaireDialog() {
		this(TEXT_TITLE);
	}

	public AddEditCommentaireDialog(String title) {
		super(new BorderLayout());

		textTitle = title;
		commentaireLengthMax = 1000;

		initActions();
		initComponents();

		this.add(buildEditorPanel(), BorderLayout.CENTER);
	}

	private void initActions() {
		acceptAction = new AcceptAction();
		cancelAction = new CancelAction();
	}

	private void createComponents() {
		textLabel = new JLabel();
		commentaireArea = SwingComponentFactory.createTextArea(true, true);
	}

	private void initComponents() {
		createComponents();

		commentaireArea.getDocument().addDocumentListener(new CommentDocumentListener());
	}

	private JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("RIGHT:MAX(40DLU;DEFAULT):NONE,FILL:4DLU:NONE,FILL:200DLU:NONE,FILL:DEFAULT:GROW(1.0)", //$NON-NLS-1$
				"CENTER:DEFAULT:NONE,CENTER:3DLU:NONE,CENTER:DEFAULT:NONE,CENTER:30DLU:NONE,CENTER:DEFAULT:GROW(1.0)"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.addLabel(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().comment(), cc.xy(1, 3));
		builder.add(textLabel, cc.xyw(3, 1, 2));
		builder.add(new JScrollPane(commentaireArea), cc.xywh(3, 3, 2, 3));
		return builder.getPanel();
	}

	private void setEnabledAll(boolean enabled) {
		commentaireArea.setEditable(enabled);
	}

	public int showDialog(Component parent, String text, String commentaire, int commentaireLengthMax, boolean enabled) {
		returnValue = CANCEL_OPTION;

		this.commentaireLengthMax = commentaireLengthMax;
		this.textLabel.setText(text);
		this.commentaireArea.setText(commentaire);

		setEnabledAll(enabled);
		if (enabled) {
			dialog = new JDialogModel(parent, textTitle, MODIFY_TEXT_SUBTITLE, this, new Action[] { acceptAction, cancelAction }, cancelAction);
		} else {
			dialog = new JDialogModel(parent, textTitle, SHOW_TEXT_SUBTITLE, this, new Action[] { cancelAction }, cancelAction);
		}

		dialog.setResizable(true);

		dialog.showDialog();
		dialog.dispose();

		return returnValue;
	}

	public String getCommentaire() {
		return commentaireArea.getText();
	}

	private void updateComment() {
		acceptAction.setEnabled(commentaireArea.getText() == null || commentaireArea.getText().length() <= commentaireLengthMax);
	}

	private final class CommentDocumentListener implements DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent e) {
			updateComment();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			updateComment();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			updateComment();
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
}