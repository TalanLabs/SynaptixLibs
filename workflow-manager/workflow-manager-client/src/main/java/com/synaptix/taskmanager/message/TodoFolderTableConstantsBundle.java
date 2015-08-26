package com.synaptix.taskmanager.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("Dossiers ToDo")
public interface TodoFolderTableConstantsBundle extends ConstantsWithLookingBundle {

	@Key("meaning")
	@DefaultStringValue("Libellé")
	public String meaning();

	@Key("comments")
	@DefaultStringValue("Commentaires")
	public String comments();

	@Key("withoutFolder")
	@DefaultStringValue("Sans dossier")
	public String withoutFolder();

	@Key("allTodos")
	@DefaultStringValue("Voir tout")
	public String allTodos();

	@DefaultStringValue("Accès restreint")
	public String restricted();

	@DefaultStringValue("Restrictions")
	public String roles();

	@DefaultStringValue("Roles autorisés")
	public String authorizedRoles();

	@DefaultStringValue("Modifier les rôles")
	public String modifyRoles();

}
