import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import pathTree.AbstractPathTreeModel;
import pathTree.JPathTree;

public class MainPathTree {

	private static final class MyPathTreeModel extends AbstractPathTreeModel {

		Troncon[] troncons = { new Troncon("Paris", "Lille"), new Troncon("Paris", "Bruxelle"), new Troncon("Paris", "Mons"), new Troncon("Lille", "Bruxelle"), new Troncon("Mons", "Liège"),
				new Troncon("Liège", "Cologne"), new Troncon("Bruxelle", "Amsterdams"), new Troncon("Bruxelle", "Liège"), new Troncon("Paris", "Marseille") };

		public Object getNode(Object parent, int index) {
			Object res = null;
			int i = 0;
			int j = 0;
			while (i < troncons.length && res == null) {
				Troncon troncon = troncons[i];
				if (troncon.lieu1.equals(parent)) {
					if (j == index) {
						res = troncon.lieu2;
					}
					j++;
				}
				i++;
			}
			return res;
		}

		public int getNodeCount(Object parent) {
			int count = 0;
			for (Troncon troncon : troncons) {
				if (troncon.lieu1.equals(parent)) {
					count++;
				}
			}
			return count;
		}

		public Object getRoot() {
			return "Paris";
		}

		private class Troncon {

			String lieu1;

			String lieu2;

			public Troncon(String lieu1, String lieu2) {
				super();
				this.lieu1 = lieu1;
				this.lieu2 = lieu2;
			}
		}
	}

	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPathTree pathTree = new JPathTree(new MyPathTreeModel());
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(pathTree, BorderLayout.CENTER);

		frame.pack();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}