package com.synaptix.user.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.JWaitGlassPane;
import com.synaptix.swing.other.SynaptixDialog;
import com.synaptix.swing.utils.GUIWindow;
import com.synaptix.user.IUserAuth;
import com.synaptix.user.PasswordStore;
import com.synaptix.user.ProfilMessages;
import com.synaptix.user.UserException;
import com.synaptix.user.UserListener;
import com.synaptix.user.UserNameStore;
import com.synaptix.view.swing.error.ErrorViewManager;

public class LoginDialog<T> extends JPanel {

	private static final long serialVersionUID = 6420692586840157890L;

	public final static int CONNECTION_OPTION = 0;

	public final static int QUIT_OPTION = 1;

	private final static String TEXT_TITLE = ProfilMessages.getString("LoginDialog.0"); //$NON-NLS-1$

	private final static String TEXT_SUBTITLE = ProfilMessages.getString("LoginDialog.1"); //$NON-NLS-1$

	private static final ImageIcon ICON_KEY;

	private static final ImageIcon ICON_DELETE;

	private JDialogModel dialog;

	private LoginComboBoxModel loginComboBoxModel;

	private JComboBox loginBox;

	private JTextField loginField;

	private JPasswordField passwordField;

	private JCheckBox saveLoginBox;

	private JCheckBox savePasswordBox;

	private JLabel iconKeyLabel;

	private Action deleteAction;

	private JButton deleteButton;

	private Action connectionAction;

	private Action quitAction;

	private int returnValue;

	private T user;

	private IUserAuth<T> userAuth;

	private Action[] otherActions;

	private JWaitGlassPane glassPane;

	private UserListener userListener;

	private UserNameStore userNameStore;

	private PasswordStore passwordStore;

	static {
		ImageIcon imageIcon = null;
		try {
			imageIcon = new ImageIcon(ImageIO.read(SynaptixDialog.class.getResource("/images/iconKey.png"))); //$NON-NLS-1$
		} catch (IOException e) {
		}
		ICON_KEY = new ImageIcon(imageIcon.getImage());
		try {
			imageIcon = new ImageIcon(ImageIO.read(SynaptixDialog.class.getResource("/images/iconDelete.png"))); //$NON-NLS-1$
		} catch (IOException e) {
		}
		ICON_DELETE = new ImageIcon(imageIcon.getImage());
	}

	public LoginDialog(IUserAuth<T> userAuth, UserNameStore userNameStore, PasswordStore passwordStore) {
		this(userAuth, userNameStore, passwordStore, null);
	}

	public LoginDialog(IUserAuth<T> userAuth, UserNameStore userNameStore, PasswordStore passwordStore, Action[] otherActions) {
		this(userAuth, userNameStore, passwordStore, null, null);
	}

	public LoginDialog(IUserAuth<T> userAuth, UserNameStore userNameStore, PasswordStore passwordStore, Action[] otherActions, UserListener userListener) {
		super(new BorderLayout());

		this.userNameStore = userNameStore;
		this.passwordStore = passwordStore;
		this.otherActions = otherActions;
		this.userListener = userListener;
		this.userAuth = userAuth;

		initActions();
		initComponents();

		dialog = null;
		this.add(buildContents(), BorderLayout.CENTER);
	}

	private void initActions() {
		quitAction = new QuitAction();
		connectionAction = new ConnectionAction();
		connectionAction.setEnabled(false);

		deleteAction = new DeleteAction();
		deleteAction.setEnabled(false);
	}

	private void initComponents() {
		glassPane = new JWaitGlassPane(JWaitGlassPane.TYPE_DIRECTION_PING_PONG, "", null, null); //$NON-NLS-1$

		loginComboBoxModel = new LoginComboBoxModel();
		loginBox = new JComboBox(loginComboBoxModel);
		loginBox.setEditable(true);

		loginField = (JTextField) loginBox.getEditor().getEditorComponent();
		loginField.addActionListener(connectionAction);
		loginField.getDocument().addDocumentListener(new LoginDocumentListener());

		passwordField = new JPasswordField();
		passwordField.addActionListener(connectionAction);

		loginField.setPreferredSize(passwordField.getPreferredSize());
		passwordField.setPreferredSize(loginBox.getPreferredSize());

		saveLoginBox = new JCheckBox(ProfilMessages.getString("LoginDialog.5")); //$NON-NLS-1$
		saveLoginBox.addChangeListener(new SaveLoginBoxChangeListener());

		savePasswordBox = new JCheckBox(ProfilMessages.getString("LoginDialog.6")); //$NON-NLS-1$
		savePasswordBox.setEnabled(false);

		iconKeyLabel = new JLabel(ICON_KEY);

		deleteButton = new JButton(deleteAction);
		deleteButton.setBorderPainted(false);
		deleteButton.setFocusable(false);
	}

	private JPanel buildContents() {
		FormLayout layout = new FormLayout("FILL:100PX:NONE,FILL:4DLU:NONE,RIGHT:MAX(60DLU;PREF):NONE,FILL:4DLU:NONE,FILL:150DLU:NONE,FILL:4DLU:NONE,FILL:32PX:NONE", //$NON-NLS-1$
				"FILL:PREF:NONE,CENTER:3DLU:NONE,FILL:PREF:NONE,CENTER:3DLU:NONE,CENTER:DEFAULT:NONE,CENTER:3DLU:NONE,CENTER:DEFAULT:NONE"); //$NON-NLS-1$
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.add(iconKeyLabel, cc.xywh(1, 1, 1, 7));
		builder.addLabel(ProfilMessages.getString("LoginDialog.9") + "*", cc.xy(3, 1)); //$NON-NLS-1$
		builder.add(loginBox, cc.xy(5, 1));
		builder.add(deleteButton, cc.xy(7, 1));
		builder.addLabel(ProfilMessages.getString("LoginDialog.10") + "*", cc.xy(3, 3)); //$NON-NLS-1$
		builder.add(passwordField, cc.xy(5, 3));
		builder.add(saveLoginBox, cc.xy(5, 5));
		builder.add(savePasswordBox, cc.xy(5, 7));
		return builder.getPanel();
	}

	public T getUser() {
		return user;
	}

	public int showDialog(Component parent) {
		return showDialog(parent, TEXT_TITLE, TEXT_SUBTITLE);
	}

	public int showDialog(Component parent, String title, String subtitle) {
		returnValue = CONNECTION_OPTION;

		List<Action> actions = new ArrayList<Action>();
		actions.add(connectionAction);
		if (otherActions != null) {
			actions.addAll(Arrays.asList(otherActions));
		}
		actions.add(quitAction);

		dialog = new JDialogModel(parent, title, subtitle, this, actions.toArray(new Action[actions.size()]), quitAction);

		dialog.setResizable(false);

		loadPreference();

		if (saveLoginBox.isSelected()) {
			passwordField.requestFocus();
		}

		dialog.showDialog();
		dialog.dispose();

		return returnValue;
	}

	private void verifyLogin() {
		saveLoginBox.setSelected(false);
		savePasswordBox.setSelected(false);
		connectionAction.setEnabled(false);
		deleteAction.setEnabled(false);
		passwordField.setText(null);

		String login = loginField.getText();
		if (login != null && !login.trim().isEmpty()) {
			connectionAction.setEnabled(true);

			if (userNameStore.containsUserName(login)) {
				deleteAction.setEnabled(true);
				saveLoginBox.setSelected(true);

				char[] pass = passwordStore.get(loginField.getText());

				if (pass != null) {
					passwordField.setText(String.valueOf(pass));
					savePasswordBox.setSelected(true);
				}
			}
		}
	}

	private void loadPreference() {
		userNameStore.loadUserNames();

		loginComboBoxModel.setUserNames(userNameStore.getUserNames());

		if (loginComboBoxModel.getSize() > 0) {
			loginBox.setSelectedIndex(0);
		} else {
			loginBox.setSelectedIndex(-1);
		}
	}

	private void savePreference() {
		String name = loginField.getText();
		if (saveLoginBox.isSelected()) {
			if (userNameStore.containsUserName(name)) {
				userNameStore.removeUserName(name);
			}
			userNameStore.addUserName(name);
			if (savePasswordBox.isSelected()) {
				passwordStore.set(name, passwordField.getPassword());
			} else {
				passwordStore.set(name, null);
			}
		} else {
			if (userNameStore.containsUserName(name)) {
				userNameStore.removeUserName(name);
				passwordStore.set(name, null);
			}
		}

		userNameStore.saveUserNames();
	}

	private final class SaveLoginBoxChangeListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			if (saveLoginBox.isSelected()) {
				savePasswordBox.setEnabled(true);
			} else {
				savePasswordBox.setEnabled(false);
				savePasswordBox.setSelected(false);
			}
		}
	}

	private final class LoginComboBoxModel extends AbstractListModel implements ComboBoxModel {

		private static final long serialVersionUID = -1177111377561988739L;

		private Object selectedItem;

		private String[] userNames;

		public void setUserNames(String[] userNames) {
			this.userNames = userNames;
			fireContentsChanged(this, -1, -1);
		}

		public Object getSelectedItem() {
			return selectedItem;
		}

		public void setSelectedItem(Object anItem) {
			selectedItem = anItem;
			fireContentsChanged(this, -1, -1);
		}

		public Object getElementAt(int index) {
			return userNames[index];
		}

		public int getSize() {
			if (userNames != null) {
				return userNames.length;
			}
			return 0;
		}
	}

	private final class LoginDocumentListener implements DocumentListener {

		public void changedUpdate(DocumentEvent e) {
			verifyLogin();
		}

		public void insertUpdate(DocumentEvent e) {
			verifyLogin();
		}

		public void removeUpdate(DocumentEvent e) {
			verifyLogin();
		}
	}

	private final class QuitAction extends AbstractAction {

		private static final long serialVersionUID = -832561687909401074L;

		public QuitAction() {
			super(ProfilMessages.getString("LoginDialog.11")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			returnValue = QUIT_OPTION;
			dialog.closeDialog();
		}
	}

	private final class ConnectionAction extends AbstractAction {

		private static final long serialVersionUID = -1165636071113981103L;

		public ConnectionAction() {
			super(ProfilMessages.getString("LoginDialog.12")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			new AuthSwingWorker().execute();
		}
	}

	private final class DeleteAction extends AbstractAction {

		private static final long serialVersionUID = -1165636071113981103L;

		public DeleteAction() {
			super("", ICON_DELETE); //$NON-NLS-1$
			this.putValue(Action.SHORT_DESCRIPTION, ProfilMessages.getString("LoginDialog.14")); //$NON-NLS-1$
		}

		public void actionPerformed(ActionEvent e) {
			String name = loginField.getText();
			if (userNameStore.containsUserName(name)) {
				userNameStore.removeUserName(name);
				passwordStore.set(name, null);
			}
			userNameStore.saveUserNames();

			loadPreference();
		}
	}

	private final class AuthSwingWorker extends SwingWorker<T, String> {

		public AuthSwingWorker() {
			super();

			GUIWindow.setGlassPaneForActiveWindow(glassPane);
			glassPane.start();
		}

		@Override
		protected T doInBackground() throws Exception {
			T user = userAuth.authenticate(loginField.getText(), passwordField.getPassword());

			return user;
		}

		@Override
		protected void process(List<String> chunks) {
			for (String string : chunks) {
				glassPane.setText(string);
			}
		}

		@Override
		protected void done() {
			glassPane.stop();

			try {
				user = get();
				if (user != null) {
					savePreference();

					returnValue = CONNECTION_OPTION;
					dialog.closeDialog();
				} else {
					JOptionPane.showMessageDialog(GUIWindow.getActiveWindow(), ProfilMessages.getString("LoginDialog.15"), //$NON-NLS-1$
							ProfilMessages.getString("LoginDialog.16"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
				}
			} catch (Exception e) {
				Throwable t = e.getCause();
				if (t != null && t instanceof UserException) {
					UserException ue = (UserException) t;
					if (userListener != null) {
						UserListener.Result result = userListener.errorAuthentification(ue);
						switch (result) {
						case RESET_ALL:
							loginBox.setSelectedIndex(-1);
							passwordField.setText(null);
							break;
						case RESET_PASSWORD:
							passwordField.setText(null);
							break;
						}
					} else {
						StringBuilder sb = new StringBuilder();
						switch (ue.getType()) {
						case REJECTED:
							sb.append(ProfilMessages.getString("LoginDialog.17")); //$NON-NLS-1$
							sb.append("\n"); //$NON-NLS-1$
							sb.append(ProfilMessages.getString("LoginDialog.3")); //$NON-NLS-1$
							sb.append(" : "); //$NON-NLS-1$
							sb.append(ue.getCause().getMessage());
							break;
						case WRONG_USERNAME:
							sb.append(ProfilMessages.getString("LoginDialog.19")); //$NON-NLS-1$
							break;
						case WRONG_PASSWORD:
							sb.append(ProfilMessages.getString("LoginDialog.20")); //$NON-NLS-1$
							break;
						case ERROR_CONNECTION:
							sb.append(ProfilMessages.getString("LoginDialog.21")); //$NON-NLS-1$
							sb.append("\n"); //$NON-NLS-1$
							sb.append(ProfilMessages.getString("LoginDialog.3")); //$NON-NLS-1$
							sb.append(" : "); //$NON-NLS-1$
							sb.append(ue.getCause().getMessage());
							break;
						default:
							break;
						}
						JOptionPane.showMessageDialog(GUIWindow.getActiveWindow(), sb.toString(), ProfilMessages.getString("LoginDialog.16"), //$NON-NLS-1$
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					ErrorViewManager.getInstance().showErrorDialog(LoginDialog.this, e);
				}
			}
		}
	}
}
