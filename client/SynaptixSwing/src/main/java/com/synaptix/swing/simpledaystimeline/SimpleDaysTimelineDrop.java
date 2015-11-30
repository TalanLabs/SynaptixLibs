package com.synaptix.swing.simpledaystimeline;

import java.awt.datatransfer.Transferable;

import com.synaptix.swing.DayDate;
import com.synaptix.swing.SimpleDaysTask;

public interface SimpleDaysTimelineDrop {

	public boolean canDrop(Transferable transferable);

	public SimpleDaysTask[] getTask(Transferable transferable, int resource, DayDate dayDate);

	public void done(Transferable transferable, int resource, DayDate dayDate);

}
