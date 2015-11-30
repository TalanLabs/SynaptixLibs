package i18n;

import java.util.Map;

import com.synaptix.constants.shared.ConstantsWithLookingBundle;
import com.synaptix.constants.shared.DefaultDescription;

import i18n.test.SousConstantsBundle;

@DefaultDescription("Coucou")
public interface MyConstantsBundle extends SousConstantsBundle, ConstantsWithLookingBundle {

	@DefaultStringValue("Je suis toto")
	public String toto();

	@Key("com.tata")
	@DefaultStringValue("Je suis tata")
	public String tata();

	@DefaultIntValue(5)
	public int nombreDeChiens();

	@DefaultStringValue("zero")
	public String zero();

	@DefaultStringArrayValue({ "zero", "one", ",two", "three", "four", "five", "six", "seven", "eight", "nine" })
	public String[] numberNames();

	@DefaultDoubleValue(3.14159)
	public double pi();

	@DefaultStringValue("key1")
	public String key1();

	@DefaultStringMapValue({ "key1", "value1", "key2", "value2" })
	public Map<String, String> map();

	@DefaultBooleanValue(true)
	public boolean cava();

	@DefaultStringValue("Coucou")
	public String cava(int nb);

	@DefaultStringValue("Bienvenue, {0}")
	@Description("Un message de bienvenue")
	@Meaning("Un text")
	public String message(String ok);

	public LieuConstantsBundle lieu();

	@Key("lieu.test")
	@DefaultStringValue("Super")
	public String lieu_test();
}
