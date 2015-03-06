package com.synaptix.deployer.client.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;

public interface CompareConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Comparateur")
	public String comparer();

	@DefaultStringValue("Rafraîchir")
	public String refresh();

	@DefaultStringValue("Tables manquantes")
	public String missingTable();

	@DefaultStringValue("{0} {0,choice,0#table|1#table|1<tables} manquantes")
	public String missingTables(int size);

	@DefaultStringValue("Différences de structure")
	public String structuralDifference();

	@DefaultStringValue("{0} {0,choice,0#différence|1#différence|1<différences} de structure")
	public String structuralDifferences(int size);

	@DefaultStringValue("Différences de contrainte")
	public String constraintDifferences();

	@DefaultStringValue("Nom de la colonne")
	public String columnName();

	@DefaultStringValue("Type")
	public String type();

	@DefaultStringValue("Id colonne")
	public String columnId();

	@DefaultStringValue("Précision")
	public String precision();

	@DefaultStringValue("Longueur")
	public String dataLength();

	@DefaultStringValue("Echelle")
	public String dataScale();

	@DefaultStringValue("Valeur par défaut")
	public String dataDefault();

	@DefaultStringValue("Annulable")
	public String nullable();

	@DefaultStringValue("Type de mouvement")
	public String movementType();

}
