package com.synaptix.taskmanager.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("Type de tâches")
public interface TaskTypeTableConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Code")
	public String code();

	@DefaultStringValue("Type d'objet")
	public String objectType();

	@DefaultStringValue("Service")
	public String serviceCode();

	@DefaultStringValue("Categorie")
	public String category();

	@DefaultStringValue("Description")
	public String description();

	@DefaultStringValue("Annulable")
	public String checkSkippable();

	@DefaultStringValue("Rôle éxécutant")
	public String executantRole();

	@DefaultStringValue("Rôle responsable")
	public String managerRole();

	@DefaultStringValue("Le code ne peut contenir que les caractères 0..9 a..z A..Z _ -")
	public String codeMustHaveCharacters();

	@DefaultStringValue("Nature")
	public String nature();

	@DefaultStringValue("Durée d'avertissement du responsable (h)")
	public String todoManagerDuration();

	@DefaultStringValue("Libellé")
	public String meaning();

	@DefaultStringValue("Echec de la suppression. Vérifier qu'il n'existe pas une chaine de tâche utilisant cette tâche.")
	public String isTaskChainExisted();

	@DefaultStringValue("Niveau de profondeur pour la restitution")
	public String resultDepth();

	@DefaultStringValue("Le niveau de profondeur doit être un nombre compris entre -1 et 9")
	public String resultDepthNumber();

}
