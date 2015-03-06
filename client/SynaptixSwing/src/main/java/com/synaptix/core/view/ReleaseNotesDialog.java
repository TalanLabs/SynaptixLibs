package com.synaptix.core.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.core.CoreMessages;
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.utils.GUIWindow;
import com.synaptix.swing.widget.AbstractCloseAction;

public class ReleaseNotesDialog extends JPanel {

	private static final long serialVersionUID = -2837631774124835301L;

	public final static int CLOSE_OPTION = 0;

	private final static String TEXT_TITLE = CoreMessages.getString("ReleaseNotesDialog.0"); //$NON-NLS-1$

	private JDialogModel dialog;

	private JEditorPane rapportPane;

	private Action closeAction;

	private int returnValue;

	public ReleaseNotesDialog() {
		super(new BorderLayout());

		initActions();
		initComponents();

		dialog = null;
		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initActions() {
		closeAction = new CloseAction();
	}

	private void initComponents() {
		rapportPane = new JEditorPane("text/html; charset=UTF-8", ""); //$NON-NLS-1$ //$NON-NLS-2$
		rapportPane.setEditable(false);

		rapportPane.addHyperlinkListener(new MyHyperlinkListener());
	}

	private JPanel buildContents() {
		FormLayout layout = new FormLayout("fill:400dlu:grow", //$NON-NLS-1$
				"fill:300dlu:grow"); //$NON-NLS-1$

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		CellConstraints cc = new CellConstraints();

		builder.add(new JScrollPane(rapportPane), cc.xy(1, 1));

		return builder.getPanel();
	}

	public int showDialog(Component parent, String title, String text) {
		returnValue = CLOSE_OPTION;

		rapportPane.setText(text);

		dialog = new JDialogModel(parent, TEXT_TITLE + " " + title, this, new Action[] { closeAction }, closeAction);

		dialog.setResizable(true);

		rapportPane.setCaretPosition(0);

		dialog.showDialog();
		dialog.dispose();

		return returnValue;
	}

	private final class CloseAction extends AbstractCloseAction {

		private static final long serialVersionUID = -1165636071113981103L;

		public void actionPerformed(ActionEvent e) {
			returnValue = CLOSE_OPTION;
			dialog.closeDialog();
		}
	}

	private final class MyHyperlinkListener implements HyperlinkListener {

		public void hyperlinkUpdate(HyperlinkEvent e) {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(new URI(e.getURL().toExternalForm()));
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						e1.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(GUIWindow.getActiveWindow(), "Vous devez copier le lien dans votre navigateur internet", "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
