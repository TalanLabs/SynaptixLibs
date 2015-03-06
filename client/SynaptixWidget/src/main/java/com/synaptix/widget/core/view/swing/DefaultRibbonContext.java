package com.synaptix.widget.core.view.swing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;
import org.pushingpixels.flamingo.api.ribbon.resize.CoreRibbonResizePolicies;
import org.pushingpixels.flamingo.api.ribbon.resize.RibbonBandResizePolicy;

public class DefaultRibbonContext extends RibbonContext {

	private static final Comparator<Priority> PRIORITY_COMPARATOR = new MyPriorityComparator();

	private static final class MyPriorityComparator implements Comparator<Priority> {

		@Override
		public int compare(Priority o1, Priority o2) {
			return Integer.valueOf(o1.priority).compareTo(Integer.valueOf(o2.priority));
		}
	}

	public DefaultRibbonContext(JRibbon ribbon) {
		super(ribbon);
	}

	public void buildRibbon() {
		if (ribbonTaskPriorites != null && !ribbonTaskPriorites.isEmpty()) {
			Collections.sort(ribbonTaskPriorites, PRIORITY_COMPARATOR);

			for (MyRibbonTaskPriority ribbonTaskPriority : ribbonTaskPriorites) {
				if (ribbonTaskPriority.ribbonTask != null) {
					ribbon.addTask(ribbonTaskPriority.ribbonTask);
				} else if (ribbonTaskPriority.ribbonBandPriorities != null && !ribbonTaskPriority.ribbonBandPriorities.isEmpty()) {
					RibbonTask ribbonTask = createRibbonTask(ribbonTaskPriority.title, ribbonTaskPriority.ribbonBandPriorities);
					if (ribbonTask != null) {
						ribbon.addTask(ribbonTask);
					}
				}
			}
		}
	}

	private RibbonTask createRibbonTask(String title, List<MyRibbonBandPriority> ribbonBandPriorities) {
		List<JRibbonBand> ribbonBands = null;
		if (ribbonBandPriorities != null && !ribbonBandPriorities.isEmpty()) {
			Collections.sort(ribbonBandPriorities, PRIORITY_COMPARATOR);

			ribbonBands = new ArrayList<JRibbonBand>();
			for (MyRibbonBandPriority ribbonBandPriority : ribbonBandPriorities) {
				if (ribbonBandPriority.ribbonBand != null) {
					ribbonBands.add(ribbonBandPriority.ribbonBand);
				} else if (ribbonBandPriority.commandButtonPriorities != null && !ribbonBandPriority.commandButtonPriorities.isEmpty()) {
					ribbonBands.add(createCommandButtonsRibbonBand(ribbonBandPriority.title, ribbonBandPriority.commandButtonPriorities));
				}
			}
		}

		RibbonTask ribbonTask = null;
		if (ribbonBands != null && !ribbonBands.isEmpty()) {
			ribbonTask = new RibbonTask(title, ribbonBands.toArray(new JRibbonBand[ribbonBands.size()]));
		}
		return ribbonTask;
	}

	private JRibbonBand createCommandButtonsRibbonBand(String title, List<MyCommandButtonPriority> commandButtonPriorities) {
		JRibbonBand band = null;
		if (commandButtonPriorities != null && !commandButtonPriorities.isEmpty()) {
			band = new JRibbonBand(title, null);

			if (commandButtonPriorities != null) {
				for (MyCommandButtonPriority commandButtonPriority : commandButtonPriorities) {
					band.addCommandButton(commandButtonPriority.commandButton, commandButtonPriority.ribbonElementPriority);
				}
			}

			List<RibbonBandResizePolicy> resizePolicies = new ArrayList<RibbonBandResizePolicy>();
			resizePolicies.add(new CoreRibbonResizePolicies.Mirror(band.getControlPanel()));
			band.setResizePolicies(resizePolicies);
		}
		return band;
	}
}
