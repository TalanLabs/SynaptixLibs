package com.synaptix.taskmanager.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("Générique : traces")
public interface TracableConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Créé par")
	public String createdBy();

	@DefaultStringValue("Créé le")
	public String createdDate();

	@DefaultStringValue("Mis à jour par")
	public String updatedBy();

	@DefaultStringValue("Mis à jour le")
	public String updatedDate();

}
