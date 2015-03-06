package com.synaptix.core.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.core.CoreMessages;
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.widget.AbstractCloseAction;

public class JOutputConsole extends JPanel {

	private static final long serialVersionUID = 1L;

	private final static String TEXT_TITLE = CoreMessages.getString("JOutputConsole.0"); //$NON-NLS-1$

	private static final PrintStream outDefault = System.out;

	private static final PrintStream errDefault = System.err;

	private JDialogModel dialog;

	private Action acceptAction;

	private Action cleanAction;

	private Action actifAction;

	private JTextArea ta;

	private PrintStream ps;

	private JToggleButton enableButton;

	public JOutputConsole() {
		super(new BorderLayout());

		initActions();
		initComponents();

		this.add(buildEditorPanel(), BorderLayout.CENTER);
	}

	private void initActions() {
		acceptAction = new QuitAction();
		cleanAction = new CleanAction();
		actifAction = new ActifAction();
	}

	private void createComponents() {
		ta = new JTextArea();
		enableButton = new JToggleButton(actifAction);
	}

	private void initComponents() {
		createComponents();

		ta.setEditable(false);
		enableButton.setSelected(false);
	}

	private JComponent buildEditorPanel() {
		FormLayout layout = new FormLayout("FILL:DEFAULT:NONE,FILL:250DLU:GROW(1.0)", //$NON-NLS-1$
				"CENTER:DEFAULT:NONE,FILL:250DLU:GROW(1.0)"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.add(enableButton, cc.xy(1, 1));
		builder.add(new JScrollPane(ta), cc.xyw(1, 2, 2));
		return builder.getPanel();
	}

	public void showDialog(Component parent) {
		if (dialog == null) {
			dialog = new JDialogModel(parent, TEXT_TITLE, null, this, new Action[] { cleanAction, acceptAction }, acceptAction);

			dialog.setResizable(true);

			dialog.setModal(false);

			dialog.showDialog();
		} else {
			dialog.requestFocus();
		}
	}

	public boolean isActif() {
		return enableButton.isSelected();
	}

	public void setActif(boolean actif) {
		enableButton.setSelected(actif);
		updateActif();
	}

	private void updateActif() {
		if (!enableButton.isSelected()) {
			ps.close();
			System.setOut(outDefault);
			System.setErr(errDefault);

			actifAction.putValue(Action.NAME, CoreMessages.getString("JOutputConsole.3")); //$NON-NLS-1$
		} else {
			ps = new PrintStream(new TextAreaOutputStream(ta), true);
			System.setOut(ps);
			System.setOut(ps);

			actifAction.putValue(Action.NAME, CoreMessages.getString("JOutputConsole.4")); //$NON-NLS-1$
		}
	}

	private final class ActifAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public ActifAction() {
			super(CoreMessages.getString("JOutputConsole.3")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			updateActif();
		}
	}

	private final class QuitAction extends AbstractCloseAction {

		private static final long serialVersionUID = -6102171821481254472L;

		public void actionPerformed(ActionEvent e) {
			dialog.closeDialog();
			dialog.dispose();

			dialog = null;
		}
	}

	private final class CleanAction extends AbstractAction {

		private static final long serialVersionUID = -6102171821481254472L;

		public CleanAction() {
			super(CoreMessages.getString("JOutputConsole.7")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			ta.setText(""); //$NON-NLS-1$
		}
	}

	private final class TextAreaOutputStream extends OutputStream {

		private JTextArea ta;

		public TextAreaOutputStream(JTextArea ta) {
			this.ta = ta;
		}

		public void flush() throws IOException {
			// outDefault.println("flush");
			super.flush();
		}

		public synchronized void write(int b) throws IOException {
			ta.append(String.valueOf((char) b));

			Document doc = ta.getDocument();

			int len = doc.getLength();

			if (len >= 80000) {
				try {
					doc.remove(0, 1);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			// ta.setDocument(doc);

			ta.setCaretPosition(doc.getLength() - 1);
		}
	}
}