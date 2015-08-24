package com.synaptix.taskmanager.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("Contexte taskmanager suivis d'incident")
public interface EventFollowupTaskManagerContextTableConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Tiers notifié")
	@Key("notifiedThirdParty.name")
	public String notifiedThirdParty_name();

	@DefaultStringValue("Mode de suivi")
	public String eventFollowupType();

	@DefaultStringValue("Criticité")
	public String incidentLevel();

	@DefaultStringValue("Type d'objet métier")
	public String businessObjectKind();

}
