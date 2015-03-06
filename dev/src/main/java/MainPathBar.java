import helper.MainHelper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.synaptix.widget.pathbar.JPathBar;

public class MainPathBar {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				final JFrame frame = MainHelper.createFrame();

				final DefaultListModel model = new DefaultListModel();
				model.addElement("Premier");
				model.addElement("Deuxième");
				model.addElement("Troisième");
				model.addElement("Quatrième");
				model.addElement("Cinquième");
				final JPathBar pathBar = new JPathBar(model);
				pathBar.setFixedItemWidth(-1);
				pathBar.setFixedItemHeight(-1);
				pathBar.setShowGrid(true);
				// pathBar.setBorder(BorderFactory.createLineBorder(Color.red, 10));

				frame.getContentPane().add(pathBar);

				frame.pack();
				frame.setVisible(true);

				new Timer(1000, new ActionListener() {

					int i = 0;

					@Override
					public void actionPerformed(ActionEvent e) {
						// model.addElement("Test" + i);
						// frame.pack();
						// i++;
						pathBar.getSelectionModel().setSelectionInterval(i, i);
						i = (i + 1) % model.getSize();

					}
				}).start();
			}
		});
	}
}
