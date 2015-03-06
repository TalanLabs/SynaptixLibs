package com.synaptix.swing.roulement;

import java.awt.datatransfer.Transferable;

import com.synaptix.swing.DayDate;

public interface RoulementTimelineDrop {

	public boolean canDrop(Transferable transferable);

	public RoulementTask[] getTask(Transferable transferable, int resource, DayDate dayDate);

	public void done(Transferable transferable, int resource, DayDate dayDate);

}
