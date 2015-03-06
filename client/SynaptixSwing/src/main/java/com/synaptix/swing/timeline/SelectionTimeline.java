package com.synaptix.swing.timeline;

import java.util.Date;

import com.synaptix.swing.Task;

public class SelectionTimeline {
	
	private int ressource;

	private Date[] dates;

	private Task[] tasks;

	public SelectionTimeline(int ressource, Date[] dates, Task[] tasks) {
		this.ressource = ressource;
		this.dates = dates;
		this.tasks = tasks;
	}

	public Date[] getDates() {
		return dates;
	}

	public int getRessource() {
		return ressource;
	}

	public Task[] getTasks() {
		return tasks;
	}
}
