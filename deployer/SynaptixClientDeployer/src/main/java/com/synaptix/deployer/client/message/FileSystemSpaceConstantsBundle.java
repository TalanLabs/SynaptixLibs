package com.synaptix.deployer.client.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;

public interface FileSystemSpaceConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Rafraîchir")
	public String refresh();

	@DefaultStringValue("Espace mémoire du FS")
	public String fileSystemSpace();

	@DefaultStringValue("Environnement")
	public String environment();

	@DefaultStringValue("Système de fichiers")
	public String name();

	@DefaultStringValue("Espace total")
	public String totalSpace();

	@DefaultStringValue("Espace utilisé")
	public String usedSpace();

	@DefaultStringValue("Espace disponible")
	public String availableSpace();

	@DefaultStringValue("Pourcentage")
	public String percentage();

	@DefaultStringValue("Monté sur")
	public String mountedOn();

}
