import java.io.File;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class Main {

	public static void main(String[] args) throws Exception {
		List<String> lines = Files.readLines(new File("D:\\flat.colorschemes"), Charsets.UTF_8);

		int t = Integer.parseUnsignedInt("FFFFFF", 16);

		for (String line : lines) {
			if (line.contains("#")) {
				int index = line.indexOf("#");
				String hex = line.substring(index + 1, index + 7);
				int i = Integer.parseUnsignedInt(hex, 16);
				int j = t - i;
				String hex2 = Integer.toHexString(j);
				line = line.substring(0, index + 1) + hex2 + line.substring(index + 7);
			}
			System.out.println(line);
		}
	}
}
