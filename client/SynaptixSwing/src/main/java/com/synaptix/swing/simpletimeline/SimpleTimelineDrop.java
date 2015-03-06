package com.synaptix.swing.simpletimeline;

import java.awt.datatransfer.Transferable;
import java.util.Date;

import com.synaptix.swing.SimpleTask;

public interface SimpleTimelineDrop {

	public boolean canDrop(Transferable transferable);

	public SimpleTask[] getTask(Transferable transferable, int resource, Date date);

	public void done(Transferable transferable, int resource, Date date);

}
