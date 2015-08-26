package com.synaptix.taskmanager.message;

import java.util.Map;

import com.synaptix.constants.shared.ConstantsBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("Module : Moteur de tâches")
public interface TaskManagerModuleConstantsBundle extends ConstantsBundle {

	@DefaultStringValue("Saisir un libellé")
	public String enterALabel();

	@DefaultStringMapValue({ "UPDATE_STATUS", "Changement de statut", "DATA_CHECK", "Contrôle des données", "ENRICHMENT", "Enrichissement", "MANUAL_ENRICHMENT", "Enrichissement manuel",
			"EXTERNAL_PROCESS", "Processus externe" })
	public Map<String, String> serviceNatures();

	@DefaultStringMapValue({ "TRANSPORT_REQUEST", "Commande de transport", "CUSTOMER_ORDER", "Ordre client", "EXECUTION_ORDER", "Ordre d'exécution", "OPERATION", "Opération", "SUPPLIER_INVOICE",
			"Facture" })
	public Map<String, String> objectTypes();

	@DefaultStringValue("Moteur de tâches")
	public String taskManager();

	@DefaultStringValue("Configuration")
	public String configuration();

	@DefaultStringValue("Tâches")
	public String tasks();

	@DefaultStringValue("À faire")
	public String todo();

	@DefaultStringMapValue({ "TODO", "À faire", "CURRENT", "En cours", "DONE", "Faite", "SKIPPED", "Ignorée", "CANCELED", "Annulée" })
	public Map<String, String> taskStatuses();

	@DefaultStringMapValue({ "TODO", "À faire", "PENDING", "En attente" })
	public Map<String, String> todoStatuses();

	@DefaultStringMapValue({ "MANAGER", "Responsable", "EXECUTANT", "Exécutant" })
	public Map<String, String> todoOwners();

	@DefaultStringValue("Code")
	public String serviceCode();

	@DefaultStringValue("Date de début")
	public String startDate();

	@DefaultStringValue("Date de fin")
	public String endDate();

	@DefaultStringValue("Description")
	public String description();

	@DefaultStringValue("Erreur")
	public String error();

	@DefaultStringValue("Entrez une chaîne de tâches")
	public String enterTaskChain();

	@DefaultStringMapValue({ "EXECUTED", "Traitement exécuté", "UPDATED", "Modification effectuée", "CORRECTED", "Correction effectuée", "NO_ACTION_NEEDED", "Modification inutile", "UPDATE_ERROR",
			"Modification en échec", "CHECK_ERROR", "Contrôle en échec", "WAIT", "En attente", "ALTERNATIVE", "Alternatif" })
	public Map<String, String> serviceResultStatuses();

}
