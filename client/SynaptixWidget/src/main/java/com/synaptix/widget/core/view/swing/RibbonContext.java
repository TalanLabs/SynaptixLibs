package com.synaptix.widget.core.view.swing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NullArgumentException;
import org.pushingpixels.flamingo.api.common.AbstractCommandButton;
import org.pushingpixels.flamingo.api.ribbon.JRibbon;
import org.pushingpixels.flamingo.api.ribbon.JRibbonBand;
import org.pushingpixels.flamingo.api.ribbon.RibbonElementPriority;
import org.pushingpixels.flamingo.api.ribbon.RibbonTask;

public class RibbonContext {

	protected JRibbon ribbon;

	protected List<MyRibbonTaskPriority> ribbonTaskPriorites = new ArrayList<MyRibbonTaskPriority>();

	protected Map<String, MyRibbonTaskPriority> ribbonTaskPriorityMap = new HashMap<String, MyRibbonTaskPriority>();

	protected Map<String, MyRibbonBandPriority> ribbonBandPriorityMap = new HashMap<String, MyRibbonBandPriority>();

	public RibbonContext(JRibbon ribbon) {
		super();

		this.ribbon = ribbon;
	}

	public void addRibbonTask(RibbonTask ribbonTask) {
		addRibbonTask(ribbonTask, 50);
	}

	public void addRibbonTask(RibbonTask ribbonTask, int priority) {
		if (ribbonTask == null) {
			throw new NullArgumentException("ribbonTask is null");
		}
		MyRibbonTaskPriority ribbonTaskPriority = ribbonTaskPriorityMap.get(ribbonTask.getTitle());
		if (ribbonTaskPriority != null) {
			throw new IllegalArgumentException("ribbonTask exist");
		}

		ribbonTaskPriority = new MyRibbonTaskPriority();
		ribbonTaskPriority.title = ribbonTask.getTitle();
		ribbonTaskPriority.ribbonTask = ribbonTask;
		ribbonTaskPriority.priority = priority;

		ribbonTaskPriorites.add(ribbonTaskPriority);
		ribbonTaskPriorityMap.put(ribbonTaskPriority.title, ribbonTaskPriority);
	}

	public RibbonTaskBuilder addRibbonTask(String ribbonTaskTitle) {
		return addRibbonTask(ribbonTaskTitle, 50);
	}

	public RibbonTaskBuilder addRibbonTask(String ribbonTaskTitle, int priority) {
		if (ribbonTaskTitle == null || ribbonTaskTitle.trim().isEmpty()) {
			throw new NullArgumentException("ribbonTask is null or empty");
		}
		MyRibbonTaskPriority ribbonTaskPriority = ribbonTaskPriorityMap.get(ribbonTaskTitle);
		if (ribbonTaskPriority != null && ribbonTaskPriority.ribbonTask != null) {
			throw new IllegalArgumentException("ribbonTask use");
		}

		if (ribbonTaskPriority == null) {
			ribbonTaskPriority = new MyRibbonTaskPriority();
			ribbonTaskPriority.title = ribbonTaskTitle;
			ribbonTaskPriority.ribbonTask = null;
			ribbonTaskPriority.priority = priority;
			ribbonTaskPriority.ribbonBandPriorities = new ArrayList<DefaultRibbonContext.MyRibbonBandPriority>();

			ribbonTaskPriorites.add(ribbonTaskPriority);
			ribbonTaskPriorityMap.put(ribbonTaskPriority.title, ribbonTaskPriority);
		}
		return new RibbonTaskBuilder(ribbonTaskTitle);
	}

	@Deprecated
	/**
	 * use RibbonTaskBuilder
	 * @param ribbonTaskTitle
	 * @param ribbonBand
	 */
	public void addRibbonBand(String ribbonTaskTitle, JRibbonBand ribbonBand) {
		addRibbonBand(ribbonTaskTitle, ribbonBand, 50);
	}

	@Deprecated
	/**
	 * use RibbonTaskBuilder
	 * @param ribbonTaskTitle
	 * @param ribbonBand
	 * @param priority
	 */
	public void addRibbonBand(String ribbonTaskTitle, JRibbonBand ribbonBand, int priority) {
		if (ribbonBand == null) {
			throw new NullArgumentException("ribbonBand is null");
		}
		addRibbonTask(ribbonTaskTitle);

		String key = ribbonTaskTitle + "_" + ribbonBand.getTitle();
		MyRibbonBandPriority ribbonBandPriority = ribbonBandPriorityMap.get(key);
		if (ribbonBandPriority != null) {
			throw new IllegalArgumentException("ribbonBand exist for RibbonTask " + ribbonTaskTitle);
		}

		ribbonBandPriority = new MyRibbonBandPriority();
		ribbonBandPriority.title = ribbonBand.getTitle();
		ribbonBandPriority.ribbonBand = ribbonBand;
		ribbonBandPriority.priority = priority;

		ribbonBandPriorityMap.put(key, ribbonBandPriority);
		ribbonTaskPriorityMap.get(ribbonTaskTitle).ribbonBandPriorities.add(ribbonBandPriority);
	}

	@Deprecated
	/**
	 * use RibbonTaskBuilder
	 * @param ribbonTaskTitle
	 * @param ribbonBandTitle
	 */
	public void addRibbonBand(String ribbonTaskTitle, String ribbonBandTitle) {
		addRibbonBand(ribbonTaskTitle, ribbonBandTitle, 50);
	}

	@Deprecated
	/**
	 * use RibbonTaskBuilder
	 * @param ribbonTaskTitle
	 * @param ribbonBandTitle
	 * @param priority
	 */
	public void addRibbonBand(String ribbonTaskTitle, String ribbonBandTitle, int priority) {
		if (ribbonBandTitle == null || ribbonBandTitle.trim().isEmpty()) {
			throw new NullArgumentException("ribbonBand is null or empty");
		}
		addRibbonTask(ribbonTaskTitle);

		String key = ribbonTaskTitle + "_" + ribbonBandTitle;
		MyRibbonBandPriority ribbonBandPriority = ribbonBandPriorityMap.get(key);
		if (ribbonBandPriority != null && ribbonBandPriority.ribbonBand != null) {
			throw new IllegalArgumentException("ribbonBand use for RibbonTask " + ribbonTaskTitle);
		}

		if (ribbonBandPriority == null) {
			ribbonBandPriority = new MyRibbonBandPriority();
			ribbonBandPriority.title = ribbonBandTitle;
			ribbonBandPriority.commandButtonPriorities = new ArrayList<DefaultRibbonContext.MyCommandButtonPriority>();
			ribbonBandPriority.priority = priority;

			ribbonBandPriorityMap.put(key, ribbonBandPriority);
			ribbonTaskPriorityMap.get(ribbonTaskTitle).ribbonBandPriorities.add(ribbonBandPriority);
		}
	}

	@Deprecated
	/**
	 * use RibbonBandBuilder
	 * @param ribbonTaskTitle
	 * @param ribbonBandTitle
	 * @param commandButton
	 * @param ribbonElementPriority
	 */
	public void addCommandeButton(String ribbonTaskTitle, String ribbonBandTitle, AbstractCommandButton commandButton, RibbonElementPriority ribbonElementPriority) {
		if (commandButton == null) {
			throw new NullArgumentException("commandButton is null");
		}
		if (ribbonElementPriority == null) {
			throw new NullArgumentException("ribbonElementPriority is null");
		}
		addRibbonBand(ribbonTaskTitle, ribbonBandTitle);

		String key = ribbonTaskTitle + "_" + ribbonBandTitle;

		MyCommandButtonPriority commandButtonPriority = new MyCommandButtonPriority();
		commandButtonPriority.commandButton = commandButton;
		commandButtonPriority.ribbonElementPriority = ribbonElementPriority;

		ribbonBandPriorityMap.get(key).commandButtonPriorities.add(commandButtonPriority);
	}

	/**
	 * Warning, not use for add RibbonTask
	 * 
	 * @return
	 */
	public JRibbon getRibbon() {
		return ribbon;
	}

	class MyRibbonTaskPriority extends Priority {

		RibbonTask ribbonTask;

		String title;

		List<MyRibbonBandPriority> ribbonBandPriorities;

	}

	class MyRibbonBandPriority extends Priority {

		JRibbonBand ribbonBand;

		String title;

		List<MyCommandButtonPriority> commandButtonPriorities;

	}

	class Priority {

		int priority;
	}

	class MyCommandButtonPriority {

		AbstractCommandButton commandButton;

		RibbonElementPriority ribbonElementPriority;

	}

	public class RibbonTaskBuilder {

		private final String ribbonTaskTitle;

		public RibbonTaskBuilder(String ribbonTaskTitle) {
			super();
			this.ribbonTaskTitle = ribbonTaskTitle;
		}

		public void addRibbonBand(JRibbonBand ribbonBand) {
			addRibbonBand(ribbonBand, 50);
		}

		public void addRibbonBand(JRibbonBand ribbonBand, int priority) {
			if (ribbonBand == null) {
				throw new NullArgumentException("ribbonBand is null");
			}

			String key = ribbonTaskTitle + "_" + ribbonBand.getTitle();
			MyRibbonBandPriority ribbonBandPriority = ribbonBandPriorityMap.get(key);
			if (ribbonBandPriority != null) {
				throw new IllegalArgumentException("ribbonBand exist for RibbonTask " + ribbonTaskTitle);
			}

			ribbonBandPriority = new MyRibbonBandPriority();
			ribbonBandPriority.title = ribbonBand.getTitle();
			ribbonBandPriority.ribbonBand = ribbonBand;
			ribbonBandPriority.priority = priority;

			ribbonBandPriorityMap.put(key, ribbonBandPriority);
			ribbonTaskPriorityMap.get(ribbonTaskTitle).ribbonBandPriorities.add(ribbonBandPriority);
		}

		public RibbonBandBuilder addRibbonBand(String ribbonBandTitle) {
			return addRibbonBand(ribbonBandTitle, 50);
		}

		public RibbonBandBuilder addRibbonBand(String ribbonBandTitle, int priority) {
			if (ribbonBandTitle == null || ribbonBandTitle.trim().isEmpty()) {
				throw new NullArgumentException("ribbonBand is null or empty");
			}

			String key = ribbonTaskTitle + "_" + ribbonBandTitle;
			MyRibbonBandPriority ribbonBandPriority = ribbonBandPriorityMap.get(key);
			if (ribbonBandPriority != null && ribbonBandPriority.ribbonBand != null) {
				throw new IllegalArgumentException("ribbonBand use for RibbonTask " + ribbonTaskTitle);
			}

			if (ribbonBandPriority == null) {
				ribbonBandPriority = new MyRibbonBandPriority();
				ribbonBandPriority.title = ribbonBandTitle;
				ribbonBandPriority.commandButtonPriorities = new ArrayList<DefaultRibbonContext.MyCommandButtonPriority>();
				ribbonBandPriority.priority = priority;

				ribbonBandPriorityMap.put(key, ribbonBandPriority);
				ribbonTaskPriorityMap.get(ribbonTaskTitle).ribbonBandPriorities.add(ribbonBandPriority);
			}
			return new RibbonBandBuilder(ribbonTaskTitle, ribbonBandTitle);
		}
	}

	public class RibbonBandBuilder {

		private final String ribbonTaskTitle;

		private final String ribbonBandTitle;

		public RibbonBandBuilder(String ribbonTaskTitle, String ribbonBandTitle) {
			super();
			this.ribbonTaskTitle = ribbonTaskTitle;
			this.ribbonBandTitle = ribbonBandTitle;
		}

		public void addCommandeButton(AbstractCommandButton commandButton, RibbonElementPriority ribbonElementPriority) {
			if (commandButton == null) {
				throw new NullArgumentException("commandButton is null");
			}
			if (ribbonElementPriority == null) {
				throw new NullArgumentException("ribbonElementPriority is null");
			}

			String key = ribbonTaskTitle + "_" + ribbonBandTitle;

			MyCommandButtonPriority commandButtonPriority = new MyCommandButtonPriority();
			commandButtonPriority.commandButton = commandButton;
			commandButtonPriority.ribbonElementPriority = ribbonElementPriority;

			ribbonBandPriorityMap.get(key).commandButtonPriorities.add(commandButtonPriority);
		}
	}
}