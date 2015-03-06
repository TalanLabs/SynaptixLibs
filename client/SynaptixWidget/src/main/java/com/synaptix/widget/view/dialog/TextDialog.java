package com.synaptix.widget.view.dialog;

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
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.other.SynaptixDialog;
import com.synaptix.swing.utils.GUIWindow;
import com.synaptix.widget.actions.view.swing.AbstractCloseAction;
import com.synaptix.widget.util.StaticWidgetHelper;

public class TextDialog extends JPanel {

	private static final long serialVersionUID = -2837631774124835301L;

	public final static int CLOSE_OPTION = 0;

	private JDialogModel dialog;

	private JEditorPane rapportPane;

	private Action closeAction;

	private int returnValue;

	private String subtitle;

	public TextDialog() {
		this(StaticWidgetHelper.getSynaptixWidgetConstantsBundle().information());
	}

	public TextDialog(String subtitle) {
		super(new BorderLayout());
		this.subtitle = subtitle;

		initComponents();

		dialog = null;
		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initComponents() {
		rapportPane = new JEditorPane("text/plain; charset=UTF-8", ""); //$NON-NLS-1$ //$NON-NLS-2$
		rapportPane.setEditable(false);

		rapportPane.addHyperlinkListener(new MyHyperlinkListener());
	}

	private JPanel buildContents() {
		FormLayout layout = new FormLayout("fill:300dlu:grow", //$NON-NLS-1$
				"p,3dlu,fill:150dlu:grow"); //$NON-NLS-1$

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		CellConstraints cc = new CellConstraints();

		builder.addSeparator(subtitle, cc.xy(1, 1));
		builder.add(new JScrollPane(rapportPane), cc.xy(1, 3));

		return builder.getPanel();
	}

	public int showDialog(Component parent, String title, String text) {
		returnValue = CLOSE_OPTION;

		rapportPane.setText(text);

		closeAction = new CloseAction();

		dialog = new JDialogModel(parent, title, this, new Action[] { closeAction }, closeAction);

		dialog.setResizable(true);

		rapportPane.setCaretPosition(0);

		dialog.showDialog();
		dialog.dispose();

		return returnValue;
	}

	private final class CloseAction extends AbstractCloseAction {

		private static final long serialVersionUID = -1165636071113981103L;

		@Override
		public void actionPerformed(ActionEvent e) {
			int m = e.getModifiers();
			if (((m & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) && ((m & ActionEvent.SHIFT_MASK) == ActionEvent.SHIFT_MASK) && ((m & ActionEvent.ALT_MASK) == ActionEvent.ALT_MASK)) {
				new SynaptixDialog().showDialog(TextDialog.this);
			}

			returnValue = CLOSE_OPTION;
			dialog.closeDialog();
		}
	}

	private static final class MyHyperlinkListener implements HyperlinkListener {

		@Override
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
					JOptionPane.showMessageDialog(GUIWindow.getActiveWindow(), StaticWidgetHelper.getSynaptixWidgetConstantsBundle().youHaveToOpenTheLinkInYourWebBrowser(), StaticWidgetHelper
							.getSynaptixWidgetConstantsBundle().error(), JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
