import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.JDateTextField;
import com.synaptix.swing.utils.Manager;
import com.synaptix.swing.utils.SwingComponentFactory;

public class TestComponents {

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		JFormattedTextField dateHour = SwingComponentFactory
				.createDateHourField(false, true);
		dateHour.setValue(new Date());
		dateHour.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("ici1");
			}
		});

		JFormattedTextField date = SwingComponentFactory.createDateField(false,
				false);
		date.setValue(new Date());

		date.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("ici2");
			}
		});

		JFormattedTextField hourField = SwingComponentFactory
				.createHourField(true);
		hourField.setValue(90);

		FormLayout layout = new FormLayout("75dlu", "p,3dlu,p,3dlu,p");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();

		builder.add(date, cc.xy(1, 1));
		builder.add(dateHour, cc.xy(1, 3));
		builder.add(hourField, cc.xy(1, 5));

		frame.getContentPane().add(builder.getPanel(), BorderLayout.CENTER);

		frame.pack();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}
