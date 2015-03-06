package com.synaptix.widget.path.view.swing;

import org.joda.time.Duration;
import org.joda.time.LocalTime;

public class TimeNode extends AbstractBaseLocalNode<LocalTime> {

	public TimeNode(String name, LocalTime time, Duration pause) {
		super(name, time, pause);
	}
}
