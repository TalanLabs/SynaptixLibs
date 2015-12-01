import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXHeader;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.synaptix.client.view.IView;
import com.synaptix.swing.WaitComponentFeedbackPanel;
import com.synaptix.widget.core.view.swing.IDockable;
import com.synaptix.widget.core.view.swing.SyDockingContext;
import com.vlsolutions.swing.docking.AnchorConstraints;
import com.vlsolutions.swing.docking.AnchorManager;
import com.vlsolutions.swing.docking.CompoundDockable;
import com.vlsolutions.swing.docking.DockGroup;
import com.vlsolutions.swing.docking.DockKey;
import com.vlsolutions.swing.docking.DockableState;
import com.vlsolutions.swing.docking.DockingConstants;
import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.docking.RelativeDockablePosition;

import helper.MainHelper;

public class MainDocking {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainHelper.init();

				SyDockingContext dockingContext = new SyDockingContext();
				DockingDesktop principalDd = new DockingDesktop("principal", dockingContext);
				DockingDesktop dd2 = new DockingDesktop("gauche", dockingContext);

				DockGroup group1 = new DockGroup("Toto");

				DockGroup parent = new DockGroup("parent");
				DockGroup fils = new DockGroup("fils", parent);

				AnchorManager am = new AnchorManager(dockingContext, dd2);

				MyView view1 = new MyView("Icic");
				view1.getDockKey().setDockGroup(group1);
				view1.getDockKey().setCloseEnabled(false);
				view1.getDockKey().setMaximizeEnabled(false);
				view1.getDockKey().setAutoHideEnabled(true);
				dockingContext.registerDockable(view1);
				am.putDockableContraints(view1, new AnchorConstraints(AnchorConstraints.ANCHOR_LEFT | AnchorConstraints.ANCHOR_TOP | AnchorConstraints.ANCHOR_BOTTOM));

				dd2.addDockable(view1, RelativeDockablePosition.LEFT);
				dd2.setAutoHide(view1, false);

				CompoundDockable compound = new CompoundDockable(new DockKey("Nested"));
				compound.getDockKey().setDockGroup(parent);
				compound.getDockKey().setCloseEnabled(false);
				compound.getDockKey().setMaximizeEnabled(false);
				compound.getDockKey().setFloatEnabled(false);
				compound.getDockKey().setAutoHideEnabled(false);
				// principalDd.split(view1, compound, DockingConstants.SPLIT_RIGHT);
				principalDd.addDockable(compound);

				MyView view2 = new MyView("Lalal");
				view2.getDockKey().setAutoHideEnabled(false);
				view2.getDockKey().setDockGroup(fils);
				view2.getDockKey().setFloatEnabled(true);
				dockingContext.registerDockable(view2);
				principalDd.addDockable(compound, view2);

				JFrame frame = MainHelper.createFrame();
				frame.getContentPane().add(dd2, BorderLayout.NORTH);
				frame.getContentPane().add(principalDd, BorderLayout.CENTER);
				frame.setVisible(true);
			}
		});
	}

	private static class MyView extends WaitComponentFeedbackPanel implements IView, IDockable {

		static int id = 0;

		DockKey dockKey;

		public MyView(String name) {
			super();

			dockKey = new DockKey(name + String.valueOf(id++), name);
			// dockKey.setFloatEnabled(false);
			// dockKey.setMaximizeEnabled(false);
			// dockKey.setCloseEnabled(false);
			dockKey.setAutoHideEnabled(true);
			dockKey.setLocation(DockableState.Location.HIDDEN);
			dockKey.setAutoHideBorder(DockingConstants.HIDE_LEFT);

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
		public String getCategory() {
			return null;
		}

	}

}
