package com.synaptix.taskmanager.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("A faire")
public interface TodoTableConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("No")
	public String code();

	@DefaultStringValue("Type du propriétaire")
	public String owner();

	@DefaultStringValue("Code du propriétaire")
	public String ownerCode();

	@DefaultStringValue("Centre ou tier de contact")
	public String contactCode();

	@DefaultStringValue("Tiers")
	@Key("contactThirdParty.thirdPartyCode")
	public String contactThirdParty_code();

	@DefaultStringValue("Centre")
	@Key("contactCenter.centerCode")
	public String contactCenter_code();

	@DefaultStringValue("Type d'objet")
	public String objectType();

	@DefaultStringValue("Identifiant objet")
	public String idObject();

	@DefaultStringValue("URI")
	public String uri();

	@DefaultStringValue("Description")
	public String description();

	@DefaultStringValue("Tiers")
	@Key("ownerThirdParty.shortDescription")
	public String ownerThirdParty_shortDescription();

	@DefaultStringValue("Centre")
	@Key("ownerCenter.meaning")
	public String ownerCenter_meaning();

	@DefaultStringValue("Statut")
	public String status();

	@Key("task.taskStatus")
	@DefaultStringValue("Statut")
	public String task_status();

	@Key("task.nature")
	@DefaultStringValue("Nature de la tâche")
	public String task_nature();

	@Key("task.executantRole")
	@DefaultStringValue("Executant")
	public String task_executantRole();

	@Key("task.managerRole")
	@DefaultStringValue("Responsable")
	public String task_managerRole();

	@Key("createdDate")
	@DefaultStringValue("Date de création")
	public String createdDate();

	@DefaultStringValue("Relancer les traitements")
	public String startTaskManager();

	@DefaultStringValue("Service")
	public String task_taskType_meaning();

	@DefaultStringValue("Recherche")
	public String search();

}
