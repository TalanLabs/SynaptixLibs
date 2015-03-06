import java.util.ArrayList;

import javax.swing.SwingUtilities;

import com.synaptix.client.view.Perspective;
import com.synaptix.core.view.EditBindingPerspectivesDialog;


public class MainEditBindingPerspectivesDialog {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				EditBindingPerspectivesDialog dialog = new EditBindingPerspectivesDialog();
				dialog.showDialog(null, new ArrayList<Perspective>());
			}
		});
	}

}
