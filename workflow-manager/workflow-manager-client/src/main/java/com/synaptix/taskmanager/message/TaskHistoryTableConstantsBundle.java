package com.synaptix.taskmanager.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("Historique des tâches")
public interface TaskHistoryTableConstantsBundle extends ConstantsWithLookingBundle, TracableConstantsBundle {

	@DefaultStringValue("Commentaire")
	public String comments();

	@DefaultStringValue("Statut")
	public String taskStatus();

}
