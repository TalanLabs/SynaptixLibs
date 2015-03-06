package com.synaptix.deployer.client.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;

public interface DDLConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Informations BDD")
	public String databaseInformation();

	@DefaultStringValue("Utilisateur")
	public String user();

	@DefaultStringValue("Mot de passe")
	public String password();

	@DefaultStringValue("Fichier .sql")
	public String sqlFile();

	@DefaultStringValue("DDL")
	public String ddl();

	@DefaultStringValue("Générer le DDL")
	public String generateDDL();

	@DefaultStringValue("Tablename")
	public String tablename();

}
