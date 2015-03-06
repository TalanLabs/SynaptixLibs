import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.synaptix.swing.utils.SyDesktop;

public class MainTest {

	private static int prefixI18nClassLength = "<i18n class=\"${".length();

	private static int suffixI18nClassLength = "}\"/>".length();

	private static Pattern i18nClassPattern = Pattern.compile("<i18n class=\"\\$\\{([\\w_\\-]+)(\\.([\\w_\\-]+))*#[\\w_\\-\\.]+\\}\"/>");

	public static void main(String[] args) throws Exception {
		// String res = "Je suis Gaby est <i18n class=\"gaby.toto#Rien\"/> voilà quoi ? <i18n class=\"gaby.toto#toto\"/> ici c'est la fin <i18n class=\"gaby.toto#end\"/>";
		//
		// File file = new File("D:/Projects/workspace_synaptix/TestGWT/target/TestGWT-0.0.1-SNAPSHOT/rien/AA6C6DA057D7F4266C1691FC0C05C67F.cache.html");
		//
		// String r = IOUtils.toString(new FileInputStream(file), "UTF-8");
		// toto(r);

		System.out.println("I'm test".replaceAll("'", "\\\\'"));

		// Scanner sc = new Scanner();
		// StringBuilder sb = new StringBuilder();
		// int end = 0;
		// String v = null;
		// while ((v = sc.findWithinHorizon(i18nClassPattern, 0)) != null) {
		// MatchResult mr = sc.match();
		// sc
		// sb.append(res.substring(end, mr.start()));
		// System.out.println(v);
		// end = mr.end();
		// sb.append("COUCOU");
		// }
		// if (end < res.length()) {
		// sb.append(res.substring(end));
		// }
		//
		// System.out.println(sb.toString());
	}

	private static void toto(String res) throws Exception {
		Matcher m = i18nClassPattern.matcher(res);
		StringBuilder sb = new StringBuilder();
		int end = 0;
		while (m.find()) {
			sb.append(res.substring(end, m.start()));
			String c = res.substring(m.start(), m.end());
			String key = c.substring(prefixI18nClassLength, c.length() - suffixI18nClassLength);
			int diese = key.indexOf("#");
			String bundleName = key.substring(0, diese);
			String property = key.substring(diese + 1);
			System.out.println(bundleName + " " + property);
			end = m.end();
			sb.append("COUCOU");
		}
		if (end < res.length()) {
			sb.append(res.substring(end));
		}

		File file = File.createTempFile("test", ".txt");
		IOUtils.write(sb.toString(), new FileOutputStream(file), "UTF-8");

		SyDesktop.open(file);
	}
}