import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXLoginPane;
import org.jdesktop.swingx.JXTextField;
import org.jdesktop.swingx.auth.LoginService;
import org.jdesktop.swingx.auth.PasswordStore;
import org.jdesktop.swingx.auth.UserNameStore;
import org.jdesktop.swingx.painter.AlphaPainter;
import org.jdesktop.swingx.painter.PinstripePainter;

import helper.MainHelper;

public class MainPainter {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				final JXLoginPane panel = new JXLoginPane(new LoginService() {
					public boolean authenticate(String name, char[] password, String server) throws Exception {
						System.out.println(Thread.currentThread());
						Thread.sleep(10000);
						return false;
					}
				}, new PasswordStore() {

					@Override
					public boolean set(String username, String server, char[] password) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void removeUserPassword(String username) {
						// TODO Auto-generated method stub

					}

					@Override
					public char[] get(String username, String server) {
						if (username.equals("Gabriel")) {
							return "Gaby".toCharArray();
						}
						return null;
					}
				}, new UserNameStore() {

					@Override
					public void setUserNames(String[] names) {
						// TODO Auto-generated method stub

					}

					@Override
					public void saveUserNames() {
						// TODO Auto-generated method stub

					}

					@Override
					public void removeUserName(String userName) {
						// TODO Auto-generated method stub

					}

					@Override
					public void loadUserNames() {
						// TODO Auto-generated method stub

					}

					@Override
					public String[] getUserNames() {
						return new String[] { "Gabriel", "Sandra" };
					}

					@Override
					public boolean containsUserName(String name) {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void addUserName(String userName) {
						// TODO Auto-generated method stub

					}
				});
				// panel.setBannerText("Coucou");
				// panel.setBanner(new ImageIcon(MainPainter.class
				// .getResource("/images/iconKey2.png")).getImage());
				PinstripePainter p = new PinstripePainter();
				p.setCacheable(true);
				p.setPaint(new Color(0, 0, 128));
				p.setStripeWidth(1);
				p.setSpacing(1);
				p.setAntialiasing(false);

				AlphaPainter<PinstripePainter> alpha = new AlphaPainter<PinstripePainter>();
				alpha.setAlpha(0.1f);
				alpha.setPainters(p);

				panel.setBackgroundPainter(alpha);
				// final JFrame frame = JXLoginPane.showLoginFrame(panel);

				JXTextField textField = new JXTextField();
				// textField.addBuddy(new JXButton("test"), BuddySupport.Position.RIGHT);
				textField.setPrompt("remplir");

				JXFrame frame = new JXFrame();
				frame.getContentPane().add(textField, BorderLayout.NORTH);
				frame.getContentPane().add(new JXButton("test"), BorderLayout.SOUTH);
				frame.setSize(800, 600);

				frame.setVisible(true);
			}
		});
	}

}
