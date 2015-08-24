package com.synaptix.taskmanager.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("Tâches")
public interface TaskTableConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Type d'objet")
	public String objectType();

	@DefaultStringValue("Service")
	public String serviceCode();

	@DefaultStringValue("Annulable")
	public String checkSkippable();

	@DefaultStringValue("Rôle éxécutant")
	public String executantRole();

	@DefaultStringValue("Rôle responsable")
	public String managerRole();

	@DefaultStringValue("Nature")
	public String nature();

	@DefaultStringValue("Groupe")
	public String checkGroup();

	@DefaultStringValue("Erreur")
	public String checkError();

	@DefaultStringValue("Message d'erreur")
	public String errorMessage();

	@DefaultStringValue("Prochain statut")
	public String nextStatus();

	@DefaultStringValue("Identifiant cluster")
	public String idCluster();

	@DefaultStringValue("Identifiant objet")
	public String idObject();

	@DefaultStringValue("Statut")
	public String taskStatus();

	@DefaultStringValue("Voir le graphe...")
	public String showGraphEllipsis();

	@DefaultStringValue("Ignorer la tâche...")
	public String skipTaskEllipsis();

	@DefaultStringValue("Ignorer une tâche")
	public String skipTask();

	@DefaultStringValue("Durée du Todo responsable")
	public String todoManagerDuration();

	@DefaultStringValue("Todo éxécutant")
	public String checkTodoExecutantCreated();

	@DefaultStringValue("Todo responsable")
	public String checkTodoManagerCreated();

	@DefaultStringValue("Libellé")
	public String meaning();

	@DefaultStringValue("Ouvrir")
	public String open();

	@DefaultStringValue("Date de début")
	public String startDate();

	@DefaultStringValue("Date de fin")
	public String endDate();

	@DefaultStringValue("Voir les tâches du cluster")
	public String showTaskByCluster();

	@DefaultStringValue("Statut du résultat")
	public String resultStatus();

	@DefaultStringValue("Description du résultat")
	public String resultDesc();

}
