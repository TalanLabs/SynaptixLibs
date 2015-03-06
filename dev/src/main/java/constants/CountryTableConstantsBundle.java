package constants;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;

public interface CountryTableConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Pays")
	public String country();

	@DefaultStringValue("Libellé")
	public String meaning();

	@DefaultStringValue("Libellé")
	public String nlsMeaning();

	@DefaultStringValue("Code pays iso")
	public String isoCountryCode();

	@DefaultStringValue("N° pays iso")
	public String isoCountryNo();

	@Key("[isoCountryNo,isoCountryCode,country]{%s - (%s -> %s)}")
	@DefaultStringValue("Pays")
	public String isoCountryCode_country();

}
