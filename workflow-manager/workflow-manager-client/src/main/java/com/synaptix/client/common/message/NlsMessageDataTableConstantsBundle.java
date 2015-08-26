package com.synaptix.client.common.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("Traductions")
public interface NlsMessageDataTableConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Type d'objet")
	public String objectType();

	@DefaultStringValue("Identifiant")
	public String idObject();

	@DefaultStringValue("nom du bundle")
	public String columnName();

	@DefaultStringValue("Libellé à traduire")
	public String meaning();

	@DefaultStringValue("Libellé par défaut")
	public String defaultMeaning();

	@DefaultStringValue("Libellé dans ma langue")
	public String myMeaning();

}
