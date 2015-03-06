package com.synaptix.swing.simpledaystimeline;

import java.awt.datatransfer.Transferable;

import com.synaptix.swing.JSimpleDaysTimeline;

public interface SimpleDaysTimelineDrag {

	public Transferable createTransferable(
			JSimpleDaysTimeline simpleDaysTimeline);

	public void exportDone(JSimpleDaysTimeline simpleDaysTimeline,
			Transferable transferable);
}
