package com.synaptix.swing.roulement;

import java.util.List;

public interface RoulementTimelineGroupFactory {

	/**
	 * 
	 * @param resIndex numéro de la resource courante par rapport au model
	 * @param taskList la liste des taches de la resources
	 * @return
	 */
	public abstract List<RoulementTimelineGroupTask> buildGroupTaskList(
			int resIndex, List<RoulementTask> taskList);

}
