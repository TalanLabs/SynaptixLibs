import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.utils.Manager;
import com.synaptix.swing.utils.SwingComponentFactory;

public class MainDateHourField {

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		JFormattedTextField dateMinField = SwingComponentFactory
				.createDateHourField(false, true);

		JFormattedTextField dateMaxField = SwingComponentFactory
				.createDateHourField(false, true);

		FormLayout layout = new FormLayout(
				"right:min(40dlu;p),3dlu,75dlu,3dlu,right:min(40dlu;p),3dlu,75dlu",
				"d,4dlu,d");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.addLabel("Date1*", cc.xy(1, 1));
		builder.add(dateMinField, cc.xy(3, 1));
		builder.addLabel("Date2*", cc.xy(5, 1));
		builder.add(dateMaxField, cc.xy(7, 1));
		builder.add(new JButton("Toto"), cc.xyw(1, 3, 7));

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(builder.getPanel(), BorderLayout.CENTER);

		frame.setTitle("TestPlanning");
		frame.pack();
		// frame.setSize(300, 400);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}

}
