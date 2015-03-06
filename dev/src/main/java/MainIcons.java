import helper.MainHelper;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.widget.view.swing.helper.IconHelper;

public class MainIcons {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				JFrame frame = MainHelper.createFrame();

				FormLayout layout = new FormLayout("FILL:75DLU:NONE,4DLU,FILL:75DLU:NONE");
				DefaultFormBuilder builder = new DefaultFormBuilder(layout);

				for (IconHelper.Icons icon : IconHelper.Icons.values()) {
					builder.append(new JButton(icon.getFileIcon()));
					builder.append(new JButton(icon.getFaIcon()));
				}

				frame.getContentPane().add(builder.getPanel());

				frame.pack();
				frame.setVisible(true);
			}
		});
	}

}
