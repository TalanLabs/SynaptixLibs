package com.synaptix.swing.simpletimeline;

import java.awt.datatransfer.Transferable;

import com.synaptix.swing.JSimpleTimeline;

public interface SimpleTimelineDrag {

	public Transferable createTransferable(JSimpleTimeline simpleTimeline);

	public void exportDone(JSimpleTimeline simpleTimeline, Transferable transferable);
}
