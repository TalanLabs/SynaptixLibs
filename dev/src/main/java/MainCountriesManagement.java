import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.synaptix.client.ref.controller.CountriesManagementController;

import helper.MainHelper;

public class MainCountriesManagement {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();
				MainHelper.initMyBatis();

				CountriesManagementController controller = new CountriesManagementController();

				JFrame frame = MainHelper.createFrame();
				frame.getContentPane().add((Component) controller.getView());
				frame.setVisible(true);
			}
		});
	}
}
