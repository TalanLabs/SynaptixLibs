package com.synaptix.taskmanager.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("Gestion des tâches (ordonnanceur)")
public interface TaskManagerConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Tâches des Services")
	public String taskServiceDescriptorsManagement();

	@DefaultStringValue("Tâche d'un service")
	public String taskService();

	@DefaultStringValue("Type de tâches")
	public String taskTypesManagement();

	@DefaultStringValue("Ajouter un type de tâche")
	public String addTaskType();

	@DefaultStringValue("Editer un type de tâche")
	public String editTaskType();

	@DefaultStringValue("Type de tâche")
	public String taskType();

	@DefaultStringValue("Chaînes de tâches")
	public String taskChainsManagement();

	@DefaultStringValue("Chaîne de tâches")
	public String taskChain();

	@DefaultStringValue("Créer la chaîne de tâches")
	public String newTaskChain();

	@DefaultStringValue("Ajouter une chaîne de tâches")
	public String addTaskChain();

	@DefaultStringValue("Editer une chaîne de tâches")
	public String editTaskChain();

	@DefaultStringValue("Graphes des statuts")
	public String statusGraphsManagement();

	@DefaultStringValue("Graphe de statuts")
	public String statusGraph();

	@DefaultStringValue("Ajouter un graphe de statuts")
	public String addStatusGraph();

	@DefaultStringValue("Editer un graphe de statuts")
	public String editStatusGraph();

	@DefaultStringValue("Tâches")
	public String tasksManagement();

	@DefaultStringValue("Initiale")
	public String initial();

	@DefaultStringValue("Graphe des tâches")
	public String tasksGraphManagement();

	@DefaultStringValue("Voir les erreurs")
	public String errorsManagement();

	@DefaultStringValue("A faire")
	public String todosManagement();

	@DefaultStringValue("Outils")
	public String tools();

	@DefaultStringValue("Service")
	public String service();

	@DefaultStringValue("Dossiers ToDo")
	public String todosFolders();

	@DefaultStringValue("Dossier ToDo")
	public String todoFolder();

	@DefaultStringValue("Créer dossier")
	public String addTodoFolder();

	@DefaultStringValue("Editer")
	public String edit();

	@DefaultStringValue("Aficher/Masquer les tâches annulées")
	public String showOrHideCancelTasks();

	@DefaultStringValue("Editer le contexte")
	public String editContext();

	@DefaultStringValue("Contexte de suivi d'incidents")
	public String eventFollowupContext();

	@DefaultStringValue("Contexte d'une commande de transport")
	public String transportRequestContext();

	@DefaultStringValue("Contexte d'ordre d'exécution")
	public String executionOrderContext();

	@DefaultStringValue("Contexte d'ordre client")
	public String customerOrderContext();

	@DefaultStringValue("Choisir le contexte")
	public String addContext();

	@DefaultStringValue("Simulation du workflow")
	public String simulationManagement();

	@DefaultStringValue("Contexte")
	public String context();

}
