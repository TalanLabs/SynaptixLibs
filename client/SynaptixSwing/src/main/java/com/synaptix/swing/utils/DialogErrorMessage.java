package com.synaptix.swing.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.SwingMessages;
import com.synaptix.swing.widget.AbstractCloseAction;

public class DialogErrorMessage extends JPanel {

	private static final long serialVersionUID = 4594305283749073455L;

	public final static int SEND_OPTION = 0;

	public final static int CLOSE_OPTION = 1;

	private final static String TEXT_TITLE = SwingMessages.getString("DialogErrorMessage.0"); //$NON-NLS-1$

	private final static ImageIcon ICON_ERROR;

	private static ErrorMessageBuilder errorMessageBuilder;

	private JDialogModel dialog;

	private JLabel messageLabel;

	private JEditorPane rapportPane;

	private JToolBar toolBar;

	private Action saveAsAction;

	private Action printAction;

	private Action sendAction;

	private Action closeAction;

	private int returnValue;

	static {
		ICON_ERROR = new ImageIcon(DialogErrorMessage.class.getResource("/images/errorIcon.png")); //$NON-NLS-1$

		errorMessageBuilder = new DefaultErrorMessageBuilder();
	}

	public static void setErrorMessageBuilder(ErrorMessageBuilder errorMessageBuilder) {
		DialogErrorMessage.errorMessageBuilder = errorMessageBuilder;
	}

	public static ErrorMessageBuilder getErrorMessageBuilder() {
		return errorMessageBuilder;
	}

	public DialogErrorMessage() {
		super(new BorderLayout());

		initActions();
		initComponents();

		dialog = null;
		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initActions() {
		closeAction = new CloseAction();
		saveAsAction = new SaveAsAction();
		printAction = new PrintAction();
		if (!Desktop.isDesktopSupported()) {
			printAction.setEnabled(false);
			printAction.putValue(Action.SHORT_DESCRIPTION, SwingMessages.getString("DialogErrorMessage.2")); //$NON-NLS-1$
		}

		sendAction = new SendAction();
		if (!Desktop.isDesktopSupported()) {
			sendAction.setEnabled(false);
			sendAction.putValue(Action.SHORT_DESCRIPTION, SwingMessages.getString("DialogErrorMessage.3")); //$NON-NLS-1$
		}
	}

	private void createComponents() {
		messageLabel = new JLabel("", JLabel.LEFT); //$NON-NLS-1$
		rapportPane = new JEditorPane("text/html", ""); //$NON-NLS-1$ //$NON-NLS-2$
		toolBar = new JToolBar();
	}

	private void initComponents() {
		createComponents();

		rapportPane.setEditable(false);

		toolBar.setFloatable(false);
		toolBar.add(saveAsAction);
		// toolBar.add(sendAction);
		toolBar.add(printAction);
	}

	private JPanel buildContents() {
		FormLayout layout = new FormLayout("48px,4dlu,fill:300dlu:grow", //$NON-NLS-1$
				"48px, 3dlu,p,3dlu,fill:100dlu:grow,p"); //$NON-NLS-1$

		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();

		CellConstraints cc = new CellConstraints();

		builder.add(new JLabel(ICON_ERROR), cc.xy(1, 1));
		builder.add(messageLabel, cc.xy(3, 1, "left,top")); //$NON-NLS-1$
		builder.addSeparator(SwingMessages.getString("DialogErrorMessage.10"), cc.xyw(1, 3, 3)); //$NON-NLS-1$
		builder.add(new JScrollPane(rapportPane), cc.xyw(1, 5, 3));
		builder.add(toolBar, cc.xyw(1, 6, 3));

		return builder.getPanel();
	}

	public int showDialog(Component parent, Throwable throwable) {
		return showDialog(parent, throwable, TEXT_TITLE, null);
	}

	public int showDialog(Component parent, Throwable throwable, String text) {
		return showDialog(parent, throwable, TEXT_TITLE, text);
	}

	public int showDialog(Component parent, Throwable throwable, String title, String text) {
		returnValue = CLOSE_OPTION;

		if (errorMessageBuilder.isShowErrorMessage(throwable)) {
			dialog = new JDialogModel(parent, title, this, new Action[] { closeAction }, closeAction);
			if (dialog.getTitleComponent() != null) {
				dialog.getTitleComponent().setColorBackgroundLow(new Color(255, 0, 0, 255));
				dialog.getTitleComponent().setColorBackgroundHigh(new Color(255, 100, 75, 255));
				dialog.getTitleComponent().setPlayAnimation(false);
			}

			dialog.setResizable(true);

			// TODO Temporaire, affichage de l'exception levé
			throwable.printStackTrace();

			if (text != null) {
				messageLabel.setText(text);
			} else {
				if (errorMessageBuilder != null) {
					messageLabel.setText(errorMessageBuilder.buildShortMessage(throwable));
				} else {
					messageLabel.setText(""); //$NON-NLS-1$
				}
			}

			if (errorMessageBuilder != null) {
				String rapport = errorMessageBuilder.buildLongMessage(throwable);
				rapportPane.setText(rapport);
				rapportPane.setCaretPosition(0);
			} else {
				rapportPane.setText(""); //$NON-NLS-1$
			}

			dialog.showDialog();
			dialog.dispose();
		}

		return returnValue;
	}

	private final class SaveAsAction extends AbstractAction {

		private static final long serialVersionUID = -6917133229606702528L;

		public SaveAsAction() {
			super(SwingMessages.getString("DialogErrorMessage.13")); //$NON-NLS-1$

			this.putValue(Action.SHORT_DESCRIPTION, SwingMessages.getString("DialogErrorMessage.14")); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new SaveHTMLFileFilter());
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			if (chooser.showSaveDialog(GUIWindow.getActiveWindow()) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if (getExtension(file) == null) {
					file = new File(file.getAbsolutePath() + ".html"); //$NON-NLS-1$
				}
				if (!file.isDirectory()) {
					if (!file.exists() || JOptionPane.showConfirmDialog(GUIWindow.getActiveWindow(), MessageFormat.format(SwingMessages.getString("DialogErrorMessage.16"), //$NON-NLS-1$
							file.getName()), SwingMessages.getString("DialogErrorMessage.17"), //$NON-NLS-1$
							JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
						try {
							FileWriter writer = new FileWriter(file);
							writer.write(rapportPane.getText());
							writer.close();
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(GUIWindow.getActiveWindow(), SwingMessages.getString("DialogErrorMessage.18"), //$NON-NLS-1$
									SwingMessages.getString("DialogErrorMessage.19"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
						}
					}
				}
			}
		}

		private String getExtension(File f) {
			String ext = null;
			String s = f.getName();
			int i = s.lastIndexOf('.');

			if (i > 0 && i < s.length() - 1) {
				ext = s.substring(i + 1).toLowerCase();
			}
			return ext;
		}

		private final class SaveHTMLFileFilter extends FileFilter {

			@Override
			public boolean accept(File pathname) {
				boolean res = false;
				if (pathname.isDirectory()) {
					res = true;
				} else if (pathname.isFile()) {
					String extension = getExtension(pathname);
					if (extension != null) {
						if (extension.equals("html") || extension.equals("htm")) { //$NON-NLS-1$ //$NON-NLS-2$
							res = true;
						}
					}
				}
				return res;
			}

			@Override
			public String getDescription() {
				return SwingMessages.getString("DialogErrorMessage.22"); //$NON-NLS-1$
			}
		}
	}

	private final class PrintAction extends AbstractAction {

		private static final long serialVersionUID = 7257469619695044462L;

		public PrintAction() {
			super(SwingMessages.getString("DialogErrorMessage.23")); //$NON-NLS-1$

			this.putValue(Action.SHORT_DESCRIPTION, SwingMessages.getString("DialogErrorMessage.24")); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(GUIWindow.getActiveWindow(), SwingMessages.getString("DialogErrorMessage.25"), //$NON-NLS-1$
					SwingMessages.getString("DialogErrorMessage.26"), JOptionPane.INFORMATION_MESSAGE); //$NON-NLS-1$
			try {
				File file = File.createTempFile("print", ".html"); //$NON-NLS-1$ //$NON-NLS-2$
				FileWriter writer = new FileWriter(file);
				writer.write(rapportPane.getText());
				writer.close();

				SyDesktop.print(file);
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(GUIWindow.getActiveWindow(), SwingMessages.getString("DialogErrorMessage.29"), SwingMessages.getString("DialogErrorMessage.19"), //$NON-NLS-1$ //$NON-NLS-2$
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private final class SendAction extends AbstractAction {

		private static final long serialVersionUID = 9184100540105793420L;

		public SendAction() {
			super(SwingMessages.getString("DialogErrorMessage.31")); //$NON-NLS-1$

			this.putValue(Action.SHORT_DESCRIPTION, SwingMessages.getString("DialogErrorMessage.32")); //$NON-NLS-1$
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}

	private final class CloseAction extends AbstractCloseAction {

		private static final long serialVersionUID = -1165636071113981103L;

		@Override
		public void actionPerformed(ActionEvent e) {
			returnValue = CLOSE_OPTION;
			dialog.closeDialog();
		}
	}
}
