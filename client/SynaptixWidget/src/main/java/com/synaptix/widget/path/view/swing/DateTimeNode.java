package com.synaptix.widget.path.view.swing;

import org.joda.time.Duration;
import org.joda.time.LocalDateTime;

public class DateTimeNode extends AbstractBaseLocalNode<LocalDateTime> {

	public DateTimeNode(String name, LocalDateTime dateTime, Duration pause) {
		super(name, dateTime, pause);
	}
}
