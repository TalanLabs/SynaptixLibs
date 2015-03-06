package com.synaptix.swing.event;

import java.util.EventObject;

import com.synaptix.swing.TriageModel;

public class TriageModelEvent extends EventObject {

	private static final long serialVersionUID = 4613573788804777254L;

	public TriageModelEvent(TriageModel source) {
		super(source);
	}
}
