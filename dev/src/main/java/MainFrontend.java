import helper.MainHelper;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXHeader;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.client.core.controller.FrontendController;
import com.synaptix.client.view.IView;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.widget.core.controller.AbstractController;
import com.synaptix.widget.core.view.swing.IDockable;
import com.synaptix.widget.core.view.swing.IDockingContextView;
import com.synaptix.widget.core.view.swing.SyDockingContext;
import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockableState;

public class MainFrontend {

	FrontendController frontendController;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				MyFakeController c = new MyFakeController(new MyView());

				FrontendController frontendController = new FrontendController();

				frontendController.registerController(c);

				frontendController.start();

				c.show();
			}
		});
	}

	private static class MyFakeController extends AbstractController {

		MyView view;

		public MyFakeController(MyView view) {
			super();

			this.view = view;
		}

		@Override
		public IView getView() {
			return view;
		}

		public void show() {
			view.show();
		}
	}

	private static class MyView extends WaitComponentFeedbackPanel implements IView, IDockable, IDockingContextView {

		SyDockingContext dockingContext;

		DockKey dockKey = new DockKey("toto", "Name");

		public MyView() {
			super();

			dockKey.setFloatEnabled(false);
			dockKey.setMaximizeEnabled(false);
			dockKey.setCloseEnabled(false);
			dockKey.setAutoHideEnabled(false);
			dockKey.setLocation(DockableState.Location.DOCKED);
			// dockKey.setAutoHideBorder(DockingConstants.HIDE_LEFT);

			addContent(buildContents());
		}

		private JComponent buildContents() {
			FormLayout layout = new FormLayout("FILL:150DLU:GROW(1.0)", "FILL:DEFAULT:NONE,FILL:3DLU:NONE,FILL:DEFAULT:NONE,FILL:DEFAULT:GROW(1.0)");
			PanelBuilder builder = new PanelBuilder(layout);
			CellConstraints cc = new CellConstraints();
			builder.add(new JXHeader("Coucou", "Rien"), cc.xy(1, 1));
			builder.add(new JTextField(), cc.xy(1, 3));
			return builder.getPanel();
		}

		@Override
		public DockKey getDockKey() {
			return dockKey;
		}

		@Override
		public Component getComponent() {
			return this;
		}

		@Override
		public void initializeDockingContext(SyDockingContext dockingContext) {
			this.dockingContext = dockingContext;

			dockingContext.registerDockable(this);

			// dockingContext.setDockableState(dockingContext.getDockableByKey(dockKey.getKey()),new DockableState);
			// AnchorManager am = new AnchorManager(dockingContext, desk);
			// // and specify constraints (here : top + right)
			// AnchorConstraints constraints =
			// new AnchorConstraints(AnchorConstraints.ANCHOR_RIGHT|AnchorConstraints.ANCHOR_TOP);
			// am.putDockableContraints(dockable1, constraints);
			Dockable d = dockingContext.getDockableByKey(dockKey.getKey());
			dockingContext.setDockableState(d, new DockableState(null, d, DockableState.Location.HIDDEN));

			dockingContext.showDockable(this);
		}

		public void show() {
			dockingContext.showDockable(this);
		}

		@Override
		public String getCategory() {
			return null;
		}

	}
}
