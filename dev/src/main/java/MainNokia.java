import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class MainNokia {

	public static void main(String[] args) {
		try {
			BufferedReader br = new BufferedReader(new FileReader("D:/Nokia 5610 XpressMusic_2012-07-31.nbu"));

			int nb = 0;
			StringBuilder sb = new StringBuilder();
			String line = null;
			boolean begin = false;
			while ((line = br.readLine()) != null) {
				if (begin) {
					if (line.contains("END:VCARD")) {
						begin = false;
						sb.append(line).append("\n");
						System.out.println(sb.toString());
						nb++;
					} else {
						sb.append(line).append("\n");
					}
				}
				if (line.contains("BEGIN:VCARD")) {
					int index = line.indexOf("BEGIN:VCARD");
					sb.append(line.substring(index)).append("\n");
					begin = true;
				}
			}

			BufferedWriter bw = new BufferedWriter(new FileWriter("D:/temp/nokia/contacts_" + nb + ".vcf"));
			bw.write(sb.toString());
			bw.close();
			System.out.println(nb);

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
