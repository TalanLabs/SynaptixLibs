import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.synaptix.widget.actions.view.swing.AbstractAddAction;
import com.synaptix.widget.error.view.swing.ErrorInfoErrorViewManager;

import helper.MainHelper;

public class MainErrorView {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				final JFrame frame = new JFrame();
				frame.getContentPane().add(new JButton(new AbstractAddAction() {
					@Override
					public void actionPerformed(ActionEvent ec) {
						ErrorInfoErrorViewManager e = new ErrorInfoErrorViewManager();
						e.showErrorDialog(frame, new Exception(new Exception(new Exception(new Exception(new NullPointerException("Je suis null"))))));
					}
				}));
				frame.pack();
				frame.setVisible(true);
			}
		});
	}

}
