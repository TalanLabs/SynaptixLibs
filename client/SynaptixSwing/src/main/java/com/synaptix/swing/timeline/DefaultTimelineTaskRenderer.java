package com.synaptix.swing.timeline;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import com.synaptix.swing.JTimeline;
import com.synaptix.swing.Task;

public class DefaultTimelineTaskRenderer extends JLabel implements
		TimelineTaskRenderer {

	private static final long serialVersionUID = -856984961470657433L;

	public DefaultTimelineTaskRenderer() {
		super();
		this.setOpaque(true);

		this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
	}

	public boolean isUsePreferredSize() {
		return false;
	}
	
	protected void setValue(Object value) {
		setText((value == null) ? "" : value.toString()); //$NON-NLS-1$
	}

	public Component getTimelineTaskRendererComponent(JTimeline timeline,
			Task task, boolean isSelected, boolean hasFocus, int ressource,
			int width, int height) {
		if (isSelected) {
			this.setBackground(new Color(220, 120, 120));
		} else {
			this.setBackground(new Color(200, 220, 220));
		}

		setValue(task.getTitle());
		return this;
	}
}
