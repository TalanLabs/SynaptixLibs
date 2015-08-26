package com.synaptix.taskmanager.message;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

@DefaultDescription("Contexte taskmanager commandes de transport")
public interface TransportRequestTaskManagerContextTableConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Offre")
	@Key("offer.meaning")
	public String offer_meaning();

	@DefaultStringValue("Offre client")
	@Key("customerOffer.meaning")
	public String customerOffer_meaning();

	@DefaultStringValue("Donneur d'ordre")
	@Key("principal.thirdPartyCode")
	public String principal_thirdPartyCode();

	@DefaultStringValue("Communauté")
	@Key("community.label")
	public String community_label();

	@DefaultStringValue("Donneur d'ordre")
	@Key("principal.name")
	public String principal_name();

}
