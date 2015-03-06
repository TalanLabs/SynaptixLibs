package com.synaptix.swing.docking;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import com.vlsolutions.swing.docking.Dockable;
import com.vlsolutions.swing.docking.DockableContainerFactory;
import com.vlsolutions.swing.docking.DockableState;
import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.docking.FloatingDockableContainer;
import com.vlsolutions.swing.docking.SingleDockableContainer;
import com.vlsolutions.swing.docking.TabbedDockableContainer;

public class SyFloatingFrame extends JFrame implements
		FloatingDockableContainer {

	private static final long serialVersionUID = -4047959326676213021L;

	private FrameWindowListener frameWindowListener;

	private DockingDesktop desktop;

	public SyFloatingFrame(String title) {
		super(title);

		frameWindowListener = new FrameWindowListener();
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(frameWindowListener);

		this.getContentPane().setLayout(new BorderLayout());
		this.setResizable(true);
	}

	public void installDocking(DockingDesktop desktop) {
		this.desktop = desktop;
	}

	public void setInitialDockable(Dockable dockable) {
		SingleDockableContainer sdc = DockableContainerFactory.getFactory()
				.createDockableContainer(dockable,
						DockableContainerFactory.ParentType.PARENT_DETACHED_WINDOW);
		sdc.installDocking(desktop);

		Component comp = (Component) sdc;

		this.getContentPane().add(comp, BorderLayout.CENTER);
	}

	public void setInitialTabbedDockableContainer(TabbedDockableContainer tdc) {
		Component comp = (Component) tdc;

		this.getContentPane().add(comp, BorderLayout.CENTER);
	}
	
	@Override
	public void dispose() {
		this.removeWindowListener(frameWindowListener);

		super.dispose();
	}

	private final class FrameWindowListener extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			DockableState[] dockStates = desktop.getDockables();
			for (DockableState dockState : dockStates) {
				Component c = dockState.getDockable().getComponent();
				while (c != null && !c.equals(SyFloatingFrame.this)) {
					c = c.getParent();
				}
				if (c != null) {
					desktop.close(dockState.getDockable());
				}
			}
		}
	}
}
