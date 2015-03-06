package com.synaptix.deployer.client.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;

public interface DatabaseCheckerConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Vérificateur de BDD")
	public String databaseChecker();

	@DefaultStringValue("Description")
	public String description();

	@DefaultStringValue("Résultat")
	public String result();

	@DefaultStringValue("Valide")
	public String valid();

	@DefaultStringValue("Sélectionnez une vérification")
	public String chooseACheck();

	@DefaultStringValue("Tout tester")
	public String testAll();

	@DefaultStringValue("Tester")
	public String test();

}
