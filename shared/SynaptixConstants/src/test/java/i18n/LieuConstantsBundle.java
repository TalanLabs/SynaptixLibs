package i18n;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;

import i18n.test.TraceConstantsBundle;

public interface LieuConstantsBundle extends ConstantsWithLookingBundle {

	@DefaultStringValue("Ville")
	public String city();

	public TraceConstantsBundle trace();

}
