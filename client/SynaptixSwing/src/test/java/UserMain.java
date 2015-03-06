import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.combo.WidestComboPopupPrototype;
import org.pushingpixels.substance.api.skin.SubstanceNebulaLookAndFeel;

import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.utils.Manager;
import com.synaptix.user.IUserAuth;
import com.synaptix.user.PasswordStore;
import com.synaptix.user.UserException;
import com.synaptix.user.UserNameStore;
import com.synaptix.user.view.LoginDialog;

public class UserMain {

	private final static class MyUserNameStore extends UserNameStore {

		private String save;

		private List<String> userNames;

		public MyUserNameStore(String save) {
			this.save = save;
		}

		public void removeUserName(String userName) {
			userNames.remove(userName);
		}

		public void addUserName(String userName) {
			userNames.add(0, userName);
		}

		public boolean containsUserName(String name) {
			return userNames.contains(name);
		}

		public void setUserNames(String[] names) {
		}

		public String[] getUserNames() {
			return userNames.toArray(new String[userNames.size()]);
		}

		public void loadUserNames() {
			userNames = new ArrayList<String>();

			Preferences prefs = Preferences.userRoot();
			int nb = prefs.getInt(save + "_user_login_nb", 0);
			for (int i = 0; i < nb; i++) {
				String name = prefs.get(save + "_user_login_" + String.valueOf(i), "");
				userNames.add(name);
			}
		}

		public void saveUserNames() {
			Preferences prefs = Preferences.userRoot();
			int nb = prefs.getInt(save + "_user_login_nb", 0);
			for (int i = 0; i < nb; i++) {
				prefs.remove(save + "_user_login_" + String.valueOf(i));
			}

			prefs.putInt(save + "_user_login_nb", userNames.size());
			for (int i = 0; i < userNames.size(); i++) {
				prefs.put(save + "_user_login_" + String.valueOf(i), userNames.get(i));
			}
		}
	}

	private final static class MyPasswordStore extends PasswordStore {

		private String save;

		public MyPasswordStore(String save) {
			this.save = save;
		}

		public char[] get(String username) {
			Preferences prefs = Preferences.userRoot();
			String p = prefs.get(save + "_user_password_" + username, null);
			return p != null ? p.toCharArray() : null;
		}

		public void set(String username, char[] password) {
			Preferences prefs = Preferences.userRoot();
			if (password != null) {
				prefs.put(save + "_user_password_" + username, String.valueOf(password));
			} else {
				prefs.remove(save + "_user_password_" + username);
			}
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(new SubstanceNebulaLookAndFeel());
					UIManager.put(SubstanceLookAndFeel.COMBO_POPUP_PROTOTYPE, new WidestComboPopupPrototype());
					JFrame.setDefaultLookAndFeelDecorated(true);
					JDialog.setDefaultLookAndFeelDecorated(true);
				} catch (UnsupportedLookAndFeelException e) {
					Manager.installLookAndFeelWindows();
				}
				JDialogModel.setActiveSave(true);
				JDialogModel.setMultiScreen(true);

				Locale.setDefault(Locale.FRENCH);

				// JFrame frame = new JFrame();
				// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				// frame.setSize(800, 600);
				// frame.setVisible(true);

				new LoginDialog<Boolean>(new IUserAuth<Boolean>() {
					public Boolean authenticate(String login, char[] password) throws UserException {
						System.out.println(login + " " + String.valueOf(password));

						// try {
						// Thread.sleep(5000);
						// } catch (InterruptedException e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// }
						// throw new UserException(Type.REJECTED, new Exception(
						// "rien"));
						return true;
					}
				}, new MyUserNameStore("toto"), new MyPasswordStore("toto")).showDialog(null);
			}
		});

	}
}
