import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class MainCarte {

	public static void main(String[] args) {
		// System.setProperty("http.proxyHost", "proxy-appl.inetgefco.net");
		// System.setProperty("http.proxyPort", "8080");
		//
		// Authenticator.setDefault(new Authenticator() {
		// protected PasswordAuthentication getPasswordAuthentication() {
		// return new PasswordAuthentication("gefco-acct\\E407780",
		// "Titoune01".toCharArray());
		// }
		// });

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// System.setProperty("http.proxyHost", "10.61.184.32");
				// System.setProperty("http.proxyPort", "8080");
				//
				// Authenticator.setDefault(new Authenticator() {
				// protected PasswordAuthentication getPasswordAuthentication()
				// {
				// return new PasswordAuthentication("gefco-acct\\mqsi",
				// "mqsi".toCharArray());
				// }
				// });

				// Authenticator.setDefault(new Authenticator() {
				// protected java.net.PasswordAuthentication
				// getPasswordAuthentication()
				// {
				// return new PasswordAuthentication("COMMUN\\PGAE02801",
				// "Titoune03".toCharArray());
				// }
				// });
				// System.setProperty("http.proxyHost", "internet1.sncf.fr");
				// System.setProperty("http.proxyPort", "8080");

				final JFrame frame = new JFrame("Map Tests");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().setLayout(new BorderLayout());

				frame.getContentPane().add(new CartePanel(), BorderLayout.CENTER);

				// frame.pack();
				frame.setSize(1280, 800);

				frame.setVisible(true);
			}
		});
	}
}
