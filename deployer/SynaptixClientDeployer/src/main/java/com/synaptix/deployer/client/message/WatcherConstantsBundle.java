package com.synaptix.deployer.client.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;

public interface WatcherConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Surveillance")
	public String watcher();

	@DefaultStringValue("Lancer")
	public String start();

	@DefaultStringValue("Arrêter")
	public String stop();

	@DefaultStringValue("Sélectionnez un environnement et une instance à observer")
	public String selectAnEnvironmentAndInstanceToWatch();

	@DefaultStringValue("Changement d''environnement et instance : {0}, {1}")
	public String switchedToEnvironmentAndInstance(String title, String subtitle);

	@DefaultStringValue("Télécharger")
	public String download();

	@DefaultStringValue("Effacer la console")
	public String clearConsole();

}
