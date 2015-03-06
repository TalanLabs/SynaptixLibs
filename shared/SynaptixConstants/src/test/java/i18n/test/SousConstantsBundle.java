package i18n.test;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;

public interface SousConstantsBundle extends SousSousConstantsBundle, ConstantsWithLookingBundle {

	@DefaultStringValue("Sous text")
	public String sousText();

}
