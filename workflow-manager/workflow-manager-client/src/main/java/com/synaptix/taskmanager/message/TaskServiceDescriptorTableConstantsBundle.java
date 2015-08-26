package com.synaptix.taskmanager.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("Tâches des Services")
public interface TaskServiceDescriptorTableConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Code")
	public String code();

	@DefaultStringValue("Type d'objet")
	public String objectType();

	@DefaultStringValue("Nature")
	public String nature();

	@DefaultStringValue("Categorie")
	public String category();

	@DefaultStringValue("Description")
	public String description();

}
