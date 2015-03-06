package i18n;

import i18n.test.TraceConstantsBundle;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;

public interface LieuConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Ville")
	public String city();

	public TraceConstantsBundle trace();

}
