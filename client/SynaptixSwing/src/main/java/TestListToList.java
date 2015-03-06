import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.JDialogModel;
import com.synaptix.swing.JListToList;
import com.synaptix.swing.list.AbstractFilters;
import com.synaptix.swing.utils.Manager;

public class TestListToList {

	private static class MyDocumentListener extends AbstractFilters<String>
			implements DocumentListener {

		JTextField textField;

		public MyDocumentListener(JTextField textField) {
			this.textField = textField;
		}

		public void changedUpdate(DocumentEvent e) {
			fireChangeFilters();
		}

		public void insertUpdate(DocumentEvent e) {
			fireChangeFilters();
		}

		public void removeUpdate(DocumentEvent e) {
			fireChangeFilters();
		}

		public boolean isFilter(String value) {
			String text = textField.getText();
			return value.toUpperCase().contains(text.toUpperCase());
		}
	}

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final JListToList listToList = new JListToList();
		listToList.setComparator(new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});

		listToList.setAllValues(new String[] { "Yamm", "Gaby", "Toto", "Tata",
				"Sandra" });
		listToList.setSelectionValues(new String[] { "Gaby" });

		JButton button = new JButton("Button");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<String> prenomList = null;
				Object[] os = listToList.getSelectionValues();
				if (os != null && os.length > 0) {
					prenomList = new ArrayList<String>();
					for (Object o : os) {
						prenomList.add((String) o);
					}
				}
				System.out.println(prenomList);
			}
		});

		JTextField textField = new JTextField();

		MyDocumentListener d = new MyDocumentListener(textField);
		listToList.setFilters(d);
		textField.getDocument().addDocumentListener(d);

		FormLayout layout = new FormLayout(
				"RIGHT:MAX(40DLU;DEFAULT):NONE,FILL:4DLU:NONE,FILL:75DLU:NONE,FILL:25DLU:GROW(1.0)",
				"d,4dlu,d,4dlu,p");
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		CellConstraints cc = new CellConstraints();
		builder.addLabel("Filtre*", cc.xy(1, 1));
		builder.add(textField, cc.xy(3, 1));
		builder.add(listToList, cc.xyw(1, 3, 4));
		builder.add(button, cc.xyw(1, 5, 4));

		//JDialogModel dialog = new JDialogModel("", builder.getPanel(), actions)
		
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());

		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(builder.getPanel(), BorderLayout.CENTER);

		frame.pack();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame.setVisible(true);
			}
		});
	}
}
