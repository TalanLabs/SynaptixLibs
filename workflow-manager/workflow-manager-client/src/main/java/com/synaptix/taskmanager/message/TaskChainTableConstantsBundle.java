package com.synaptix.taskmanager.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("Chaînes de tâches")
public interface TaskChainTableConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Code")
	public String code();

	@DefaultStringValue("Type d'objet")
	public String objectType();

	@DefaultStringValue("Description")
	public String description();

	@DefaultStringValue("Règle du graphe")
	public String graphRule();

	@DefaultStringValue("Règle du graphe")
	public String graphRuleReadable();

	@DefaultStringValue("Le code ne peut contenir que les caractères 0..9 a..z A..Z _ -")
	public String codeMustHaveCharacters();

	@DefaultStringValue("La règle n'est pas dans un langage correct")
	public String graphRuleIsNotValidLanguage();

	@DefaultStringValue("Le type de tâche doit avoir le même type d'objet")
	public String taskTypeMustHaveSameObjectType();

	@DefaultStringValue("Le code doit être unique")
	public String codeMustBeUnique();

	@DefaultStringValue("Le code doit être un type de tâche")
	public String codeMustBeTaskType();

	@DefaultStringValue("Types de tâches")
	public String taskTypes();

	@DefaultStringValue("Erreur")
	public String error();

	@DefaultStringValue("Chaîne de tâche déjà existante")
	public String alreadyExists();

	@DefaultStringValue("Echec de la suppression. Vérifier qu'il n'existe pas un critère de chaine de tâche utilisant cette chaine de tâche")
	public String isTaskChainCriteriaExisted();

}
