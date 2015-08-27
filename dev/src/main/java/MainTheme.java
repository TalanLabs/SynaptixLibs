import helper.MainHelper;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.pushingpixels.lafwidget.animation.AnimationConfigurationManager;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.utils.GenericObjectToString;
import com.synaptix.widget.renderer.view.swing.TypeGenericSubstanceComboBoxRenderer;
import com.synaptix.widget.view.swing.helper.EnumViewHelper;

public class MainTheme {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				AnimationConfigurationManager.getInstance().setTimelineDuration(0);

				JFrame frame = MainHelper.createFrame();

				JTabbedPane pane = new JTabbedPane();
				pane.addTab("buttons", createButtons());
				pane.addTab("fields", createFields());
				pane.addTab("table", MainHelper.createTable());

				frame.getContentPane().add(pane, BorderLayout.CENTER);

				frame.setSize(800, 600);
				frame.setVisible(true);
			}
		});
	}

	enum Type {

		V1, V2, V3

	}

	private static JComponent createButtons() {
		FormLayout layout = new FormLayout("RIGHT:MAX(40DLU;PREF):NONE,FILL:4DLU:NONE,FILL:150DLU:NONE");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();

		for (int i = 0; i < 15; i++) {
			final JButton button = new JButton("bouton " + i);
			button.getModel().addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					System.out.println(button.getText() + " " + button.getModel().isRollover());
				}
			});
			builder.append("Text " + i, button);
		}

		return builder.getPanel();
	}

	private static JComponent createFields() {
		FormLayout layout = new FormLayout("RIGHT:MAX(40DLU;PREF):NONE,FILL:4DLU:NONE,FILL:150DLU:NONE");
		DefaultFormBuilder builder = new DefaultFormBuilder(layout);
		builder.setDefaultDialogBorder();

		Map<String, String> map = new HashMap<String, String>();
		map.put(Type.V1.name(), "Valeu1");
		map.put(Type.V2.name(), "Valeu2");
		map.put(Type.V3.name(), "Valeu3");
		JComboBox comboBox1 = EnumViewHelper.createEnumComboBox(Type.values(), map, true);
		builder.append("ComboBox1 ", comboBox1);

		JComboBox comboBox2 = new JComboBox(new String[] { "Valeur1", "Valeur2", "Valeur3" });
		comboBox2.setRenderer(new TypeGenericSubstanceComboBoxRenderer<String>(comboBox2, new GenericObjectToString<String>() {
			@Override
			public String getString(String t) {
				return "Text " + t;
			}
		}));
		builder.append("ComboBox2 ", comboBox2);

		JComboBox disabledComboBox = new JComboBox(new String[] { "Valeur1", "Valeur2", "Valeur3" });
		disabledComboBox.setEnabled(false);
		builder.append("ComboBox ", disabledComboBox);

		for (int i = 0; i < 5; i++) {
			JTextField textField = new JTextField("blalblblalb");
			builder.append("Text " + i, textField);
		}

		JTextField disabledTextField = new JTextField("blalblblalb");
		disabledTextField.setEnabled(false);
		builder.append("Disabled ", disabledTextField);

		JTextField editableTextField = new JTextField("blalblblalb");
		editableTextField.setEditable(false);
		builder.append("NoEditable ", editableTextField);

		JTextField otherTextField = new JTextField("blalblblalb");
		otherTextField.setEnabled(false);
		otherTextField.setEditable(false);
		builder.append("Disabled/NoEditable ", otherTextField);

		return builder.getPanel();
	}
}
