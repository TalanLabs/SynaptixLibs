import helper.MainHelper;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.widget.actions.view.swing.AbstractAddAction;

public class MainTheme {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				JFrame frame = MainHelper.createFrame();

				JTabbedPane pane = new JTabbedPane();
				pane.addTab("Contenue", createTable());
				pane.addTab("fields", createFields());

				frame.getContentPane().add(pane, BorderLayout.CENTER);

				frame.setSize(800, 600);
				frame.setVisible(true);
			}
		});
	}

	private static JComponent createFields() {
		FormLayout layout = new FormLayout("RIGHT:MAX(40DLU;PREF):NONE,FILL:4DLU:NONE,FILL:150DLU:NONE");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		for (int i = 0; i < 5; i++) {
			JTextField textField = new JTextField();
			builder.append("Text " + i, textField);
		}
		return builder.getPanel();
	}

	private static JComponent createTable() {
		JTable table = new JTable(new DefaultTableModel(100, 10) {
			@Override
			public Object getValueAt(int row, int column) {
				return row + " " + column;
			}
		});
		table.setPreferredScrollableViewportSize(new Dimension(0, 200));

		FormLayout layout = new FormLayout("FILL:PREF:GROW(1.0)");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();
		builder.append(ButtonBarFactory.buildLeftAlignedBar(new JButton(new MyAction()), new JButton(new MyAction()), new JButton(new MyAction())));
		builder.appendRow(builder.getLineGapSpec());
		builder.appendRow("FILL:PREF:GROW(1.0)");
		builder.nextLine(2);
		builder.append(new JScrollPane(table));
		JButton test = new JButton("Test");
		test.setEnabled(false);
		builder.append(ButtonBarFactory.buildLeftAlignedBar(test));
		return builder.getPanel();
	}

	private static class MyAction extends AbstractAddAction {
		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}

}
