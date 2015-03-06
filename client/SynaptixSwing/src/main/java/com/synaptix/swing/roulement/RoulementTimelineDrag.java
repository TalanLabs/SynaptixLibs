package com.synaptix.swing.roulement;

import java.awt.datatransfer.Transferable;

public interface RoulementTimelineDrag {

	public Transferable createTransferable(JRoulementTimeline roulementTimeline);

	public void exportDone(JRoulementTimeline roulementTimeline,
			Transferable transferable);
}
