package com.synaptix.taskmanager.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("Erreurs")
public interface ErrorsTableConstantsBundle extends ConstantsWithLookingBundle, TracableConstantsBundle {

	@DefaultStringValue("Code")
	public String code();

	@DefaultStringValue("Attribut")
	public String attribute();

	@DefaultStringValue("Libellé")
	public String label();

	@DefaultStringValue("Type")
	public String type();

	@DefaultStringValue("Valeur")
	public String value();

	@DefaultStringValue("ID objet")
	public String idObject();

	@DefaultStringValue("Type d'objet")
	public String objectType();

	@DefaultStringValue("ID tâche")
	public String idTask();

}
