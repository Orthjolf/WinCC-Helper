import java.io.File;
import java.util.ArrayList;

public class FileHandler {

	// ������ ���� ������ �������
	static ArrayList<ProjectFile> listOfFiles = new ArrayList<ProjectFile>();

	// ����� ���� � �������
	static int absolutePathLen = 0;

	// ����������� �������, ������� ������� ����� � ���������� �� �
	// listOfFiles
	public void getFileList(String directory) {
		File dir = new File(directory);
		String relativePath;

		if (dir.list().length != 0) { // ���� ����� �� ������
			for (File item : dir.listFiles()) {
				if (item.isDirectory()) {
					getFileList(item.getPath());
				} else {
					relativePath = item.getPath().substring(absolutePathLen, item.getPath().length());
					for (String extension : FilterLib.extensionList) {
						if (relativePath.endsWith(extension)) {
							listOfFiles.add(new ProjectFile(relativePath, 1));
						}
					}
				}
			}
		}
	}
}
