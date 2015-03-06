import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import com.synaptix.core.controller.AbstractCoreConfiguration;
import com.synaptix.core.controller.CoreController;
import com.synaptix.core.controller.ICoreController;
import com.synaptix.core.dock.AbstractViewDockable;
import com.synaptix.core.event.ViewDockableStateEvent;
import com.synaptix.core.menu.IViewMenuItemAction;
import com.synaptix.swing.utils.Manager;
import com.synaptix.swing.utils.PopupMenuMouseListener;

public class TestCoreController {

	private static final class MyCoreConfiguration extends
			AbstractCoreConfiguration {

		public String getName() {
			return "Test";
		}

		public String getVersion() {
			return "0.1";
		}

		public Image getLogo() {
			return null;
		}

		public String getOtherTitle() {
			return "";
		}

		public String getSave() {
			return "Test2";
		}
	}

	private static final class MyView extends AbstractViewDockable {

		private JButton view;

		private String text;

		private JPopupMenu popupMenu;

		public MyView(String text) {
			this.text = text;
			view = new JButton(text);
			popupMenu = new JPopupMenu();

			for (int i = 0; i < 10; i++) {
				popupMenu.add(new MyAction("Action_" + i));
			}

			view.addMouseListener(new PopupMenuMouseListener(popupMenu));
		}

		public String getId() {
			return text;
		}

		public String getName() {
			return text;
		}

		public JComponent getView() {
			return view;
		}

		public void viewDockableStateChanged(ViewDockableStateEvent e) {
			switch (e.getState()) {
			case OPENED:
				//System.out.println("coucou");
				break;
			case CLOSED:
				//System.out.println("Bye");
				break;
			}
		}
	}

	private static final class MyAction extends AbstractAction {

		public MyAction(String name) {
			super(name);
		}

		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub

		}
	}

	public static void main(String[] args) {
		Manager.installLookAndFeelWindows();

		final CoreController controller = new CoreController(
				new MyCoreConfiguration());

		controller.setDirectionComponent(
				ICoreController.DirectionComponent.North, new JButton("Gaby"));
		controller.addMenu("planning", "Planning", new String[] { "group1" });
		controller.addMenuItem("rien", "Rien", "planning/group1",
				new IViewMenuItemAction() {
					public void run(Action action, String id, String label) {
						controller.showDockable(label);
					}
				});
		controller.addMenuItem("toto", "Toto Rien", "planning/group1",
				new IViewMenuItemAction() {
					public void run(Action action, String id, String label) {
						controller.showDockable(label);
					}
				});

		controller.addMenu("planning", "Planning", new String[] { "group1","group2" });
		controller.addMenuItem("blalb2", "Blalbla 2", "planning/group1",
				new IViewMenuItemAction() {
					public void run(Action action, String id, String label) {
						controller.showDockable(label);
					}
				});
		
		controller.addMenuItem("gaby", "Gaby", "planning/group2",
				new IViewMenuItemAction() {
					public void run(Action action, String id, String label) {
						controller.showDockable(label);
					}
				});
		
		
		for (int i = 0; i < 5; i++) {
			controller.registerDockable(new MyView("View_" + i));
		}
		controller.registerDockable(new MyView("Rien"));
		controller.registerDockable(new MyView("Toto Rien"));

		controller.setReleaseNotesText(null);
		controller.setAboutText(null);
		controller.setHelpActions(new Action[] { new MyAction("Gaby"),
				new MyAction("Sandra"), null, new MyAction("toto") });

		controller.start();

		// controller.showCurrentPerspective();

		for (int i = 0; i < 10; i++) {
			controller.showDockable("View_" + i);
		}
	}
}
