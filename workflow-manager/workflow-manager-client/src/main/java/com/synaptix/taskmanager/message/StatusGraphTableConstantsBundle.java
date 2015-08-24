package com.synaptix.taskmanager.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("Graphe des statuts")
public interface StatusGraphTableConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Type d'objet")
	public String objectType();

	@DefaultStringValue("Type de tâche")
	@Key("taskType.code")
	public String taskType_code();

	@DefaultStringValue("Statut courant")
	public String currentStatus();

	@DefaultStringValue("Prochain statut")
	public String nextStatus();
}
