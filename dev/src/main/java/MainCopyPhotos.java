import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainCopyPhotos {

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		File dir = new File("D:\\Temp\\photo");
		File[] files = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isFile() && (pathname.getName().toLowerCase().endsWith(".jpg") || pathname.getName().toLowerCase().endsWith(".mov"));
			}
		});
		for (File file : files) {
			File newDir = new File("D:/temp/photo2/" + sdf.format(new Date(file.lastModified())) + "/");
			if (!newDir.exists()) {
				newDir.mkdirs();
			}
			System.out.println(new File(newDir, file.getName()));
			file.renameTo(new File(newDir, file.getName()));
		}
	}

}
