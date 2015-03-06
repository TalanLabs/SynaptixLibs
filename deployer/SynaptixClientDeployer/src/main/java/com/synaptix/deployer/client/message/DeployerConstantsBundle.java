package com.synaptix.deployer.client.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;

public interface DeployerConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Déployeur")
	public String deployer();

	@DefaultStringValue("Fermer l'application")
	public String exit();

	@DefaultStringValue("Réduire le ruban")
	public String minimizeTheRibbon();

	@DefaultStringValue("Maximiser le ruban")
	public String maximizeTheRibbon();

	@DefaultStringValue("N'affiche que le nom des onglets sur le ruban")
	public String displayOnlyTheNameOfTheTabsOnTheRibbon();

	@DefaultStringValue("Garde le ruban ouvert")
	public String displaysTheRibbonSoItIsAlwaysDevelopedEvenAfterClickingACommand();

	@DefaultStringValue("Exploitation")
	public String exploitation();

	@DefaultStringValue("Base de données")
	public String database();

}
