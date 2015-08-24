package com.synaptix.taskmanager.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("Gestion des 'à faire'")
public interface TodosManagementConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Details todo")
	public String todoDetails();

	@DefaultStringValue("Details objet")
	public String objectDetails();

	@DefaultStringValue("{0}-{1} sur {2}")
	public String elementPage(int currentElement, int lastElement, int totalElements);

	@DefaultStringValue("")
	public String noElement();

	@DefaultStringValue("Effacer les tâches terminées")
	public String clearDoneTask();

	@DefaultStringValue("Tâches les plus récentes en premier")
	public String lastTodosFirst();

	@DefaultStringValue("Filtrer")
	String filter();
}
