package com.synaptix.swing.docking;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import com.vlsolutions.swing.docking.DefaultDockableContainerFactory;
import com.vlsolutions.swing.docking.FloatingDockableContainer;

public class SyDockableContainerFactory extends DefaultDockableContainerFactory {

	@Override
	public FloatingDockableContainer createFloatingDockableContainer(
			Window owner) {
		if (owner instanceof Dialog) {
			return new SyFloatingFrame(((Dialog) owner).getTitle());
		} else {
			return new SyFloatingFrame(((Frame) owner).getTitle());
		}
		// if (owner instanceof Dialog) {
		// return new SyFloatingDialog((Dialog) owner);
		// } else {
		// return new SyFloatingDialog((Frame) owner);
		// }
	}
}
