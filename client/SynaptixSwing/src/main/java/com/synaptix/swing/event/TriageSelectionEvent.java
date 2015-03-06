package com.synaptix.swing.event;

import java.util.EventObject;

import com.synaptix.swing.triage.TriagePath;

public class TriageSelectionEvent extends EventObject {

	private static final long serialVersionUID = 5119075681391198987L;

	private TriagePath selectedPath;

	private TriagePath otherSelectedPath;

	public TriageSelectionEvent(Object source, TriagePath selectedPath,
			TriagePath otherSelectedPath) {
		super(source);
		this.selectedPath = selectedPath;
		this.otherSelectedPath = otherSelectedPath;
	}

	public TriagePath getSelectedPath() {
		return selectedPath;
	}

	public TriagePath getOtherSelectedPath() {
		return otherSelectedPath;
	}
}
