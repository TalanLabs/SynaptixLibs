package com.synaptix.swing.simpledaystimeline;

import java.util.List;

import com.synaptix.swing.SimpleDaysTask;

public interface SimpleDaysTimelineGroupFactory {

	/**
	 * 
	 * @param resIndex numéro de la resource courante par rapport au model
	 * @param taskList la liste des taches de la resources
	 * @return
	 */
	public abstract List<SimpleDaysTimelineGroupTask> buildGroupTaskList(
			int resIndex, List<SimpleDaysTask> taskList);

}
