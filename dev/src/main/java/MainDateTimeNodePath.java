import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.joda.time.LocalDateTime;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.swing.path.JPath;
import com.synaptix.widget.path.view.swing.DateTimeNode;
import com.synaptix.widget.path.view.swing.DateTimeNodePathModel;
import com.synaptix.widget.path.view.swing.DefaultBaseLocalNodePathRenderer;
import com.synaptix.widget.path.view.swing.plaf.basic.HorizontalPathUI;

import helper.MainHelper;

public class MainDateTimeNodePath {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				DateTimeNodePathModel model = new DateTimeNodePathModel();

				model.setNodes(Arrays.asList(new DateTimeNode("A", LocalDateTime.now(), null), new DateTimeNode("B", LocalDateTime.now(), null), new DateTimeNode("D", LocalDateTime.now(), null)),
						null);

				JPath path = new JPath(model);
				path.setUI(new HorizontalPathUI());
				path.setPathRenderer(new DefaultBaseLocalNodePathRenderer());

				FormLayout layout = new FormLayout("CENTER:DEFAULT", "CENTER:DEFAULT");
				PanelBuilder builder = new PanelBuilder(layout);
				builder.setDefaultDialogBorder();
				CellConstraints cc = new CellConstraints();
				builder.add(path, cc.xy(1, 1));

				JFrame frame = MainHelper.createFrame();
				frame.getContentPane().add(builder.getPanel());
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
}
